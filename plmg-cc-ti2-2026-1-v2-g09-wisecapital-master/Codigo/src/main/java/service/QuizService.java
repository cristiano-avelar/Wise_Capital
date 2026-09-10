package service;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import spark.Request;
import spark.Response;

// --- NOVOS IMPORTS DO SDK ---
import com.openai.client.OpenAIClient;
import com.openai.client.okhttp.OpenAIOkHttpClient;
import com.openai.models.ChatModel;
import com.openai.models.chat.completions.ChatCompletion;
import com.openai.models.chat.completions.ChatCompletionCreateParams;
import com.openai.credential.BearerTokenCredential;

public class QuizService {
	private dao.QuizDAO quizDAO = new dao.QuizDAO();

    // Rota para entregar as perguntas ao frontend
    public Object getPerguntasQuiz(Request request, Response response) {
        response.type("application/json");
        int quizId;
        try {
            quizId = Integer.parseInt(request.params(":id"));
        } catch (NumberFormatException e) {
            response.status(400);
            return "{\"erro\": \"ID do quiz inválido\"}";
        }

        com.google.gson.JsonArray perguntas = quizDAO.getPerguntasDoQuiz(quizId);
        
        response.status(200);
        return perguntas.toString();
    }
    
    public Object cadastrar(Request request, Response response) {
        JsonObject dados = JsonParser.parseString(request.body()).getAsJsonObject();
        
        // Opcional: Verificar aqui se o usuário da sessão é ADM por segurança
        
        if (quizDAO.cadastrarQuizCompleto(dados)) {
            response.status(201);
            return "{\"success\": true}";
        } else {
            response.status(500);
            return "{\"success\": false}";
        }
    }
    
    public Object avaliarRespostaAberta(Request request, Response response) {
        String perguntaDada = request.queryParams("pergunta");
        String respostaAluno = request.queryParams("resposta_aluno");
        
        String promptLimpo = ("Pergunta: " + perguntaDada + " | Resposta do aluno: " + respostaAluno).replace("\"", "\\\"");
        
        String endpoint = "https://formularioia.services.ai.azure.com/openai/v1";
        String deploymentName = "gpt-5.4-nano";

        try {
            // 1. Inicializa o cliente do SDK
        	String azureKey = System.getenv("AZURE_OPENAI_KEY");

        	OpenAIClient client = OpenAIOkHttpClient.builder()
        	    .baseUrl(endpoint)
        	    .credential(BearerTokenCredential.create(azureKey))
        	    .build();

            // 2. Constrói as mensagens para o modelo
            ChatCompletionCreateParams createParams = ChatCompletionCreateParams.builder()
                .model(ChatModel.of(deploymentName))
                .addSystemMessage("Você é um professor de finanças. Avalie a resposta do aluno. Retorne APENAS um JSON estruturado com 'porcentagem' (int de 0 a 100) e 'feedback' (string com a correção ou dicas para 100%). Não use formatação markdown.")
                .addUserMessage(promptLimpo)
                .build();

            // 3. Executa a requisição nativa da OpenAI
            ChatCompletion chatCompletion = client.chat().completions().create(createParams);
            
            // 4. Lê a string retornada
            String contentString = chatCompletion.choices().get(0).message().content().orElse("{}");
            
            System.out.println("Resposta da API: " + contentString);
                                               
            // O resultado já vem limpo, passamos direto para o GSON
            JsonObject avaliacaoIA = JsonParser.parseString(contentString).getAsJsonObject();
            
            int porcentagem = avaliacaoIA.get("porcentagem").getAsInt();
            String feedback = avaliacaoIA.get("feedback").getAsString();
            boolean aprovado = porcentagem >= 70;
            
            JsonObject resultadoFinal = new JsonObject();
            resultadoFinal.addProperty("success", true);
            resultadoFinal.addProperty("porcentagem", porcentagem);
            resultadoFinal.addProperty("aprovado", aprovado);
            resultadoFinal.addProperty("feedback", feedback);
            
            response.type("application/json");
            response.status(200);
            
            return resultadoFinal.toString();

        } catch (Exception e) {
            System.err.println("Erro ao processar a avaliação: " + e.getMessage());
            response.status(500);
            return "{\"success\": false, \"message\": \"Erro interno ao processar a avaliação com a IA.\"}";
        }
    }
    
    public Object concluirQuiz(Request request, Response response) {
        Integer usuarioId = request.session().attribute("usuario_logado");
        int quizId = Integer.parseInt(request.params(":id"));
        
        if(usuarioId != null) {
            quizDAO.registrarQuizFeito(usuarioId, quizId);
        }
        
        response.status(200);
        return "{\"success\": true}";
    }
    
    public Object atualizar(Request request, Response response) {
        int quizId;
        try {
            quizId = Integer.parseInt(request.params(":id"));
        } catch (NumberFormatException e) {
            response.status(400);
            return "{\"success\": false, \"erro\": \"ID inválido\"}";
        }

        JsonObject dados = JsonParser.parseString(request.body()).getAsJsonObject();
        
        if (quizDAO.atualizarQuizCompleto(quizId, dados)) {
            response.status(200);
            return "{\"success\": true}";
        } else {
            response.status(500);
            return "{\"success\": false}";
        }
    }
    
    public Object deletarQuiz(Request request, Response response) {
        int quizId;
        try {
            quizId = Integer.parseInt(request.params(":id"));
        } catch (NumberFormatException e) {
            response.status(400);
            return "{\"success\": false, \"erro\": \"ID inválido\"}";
        }

        if (quizDAO.deleteQuiz(quizId)) {
            response.status(200);
            return "{\"success\": true}";
        } else {
            response.status(500);
            return "{\"success\": false, \"erro\": \"Erro ao deletar o quiz\"}";
        }
    }
}