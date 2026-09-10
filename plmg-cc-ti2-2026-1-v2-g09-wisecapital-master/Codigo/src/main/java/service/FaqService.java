package service;
import com.google.gson.Gson;

import dao.FaqDAO;
import model.Faq;
import spark.Request;
import spark.Response;

import java.util.List;

public class FaqService {

    private FaqDAO faqDAO = new FaqDAO();

    /**
     * CREATE
     */
    public Object insert(Request request, Response response) {

        String pergunta = request.queryParams("pergunta");
        String respostaTexto = request.queryParams("resposta");

        // Cria o FAQ novo já com 0 acessos iniciais
        Faq faq = new Faq(-1, pergunta, respostaTexto, 0);

        if (faqDAO.insert(faq)) {
            response.status(201);
            return "FAQ cadastrada com sucesso!";
        } else {
            response.status(500);
            return "Erro ao cadastrar FAQ.";
        }
    }

    /**
     * READ POR ID
     */
    public Object get(Request request, Response response) {

        int id = Integer.parseInt(request.params(":id"));

        Faq faq = faqDAO.get(id);

        if (faq != null) {
             // REGISTRA ACESSO
            faqDAO.registrarAcesso(id);
            
            response.status(200);

            return "Pergunta: " + faq.getPergunta()
                    + "\nResposta: " + faq.getResposta();

        } else {
            response.status(404);
            return "FAQ não encontrada.";
        }
    }

    /**
     * REGISTRAR VISUALIZAÇÃO (NOVO MÉTODO)
     * Chamado pelo JavaScript quando a sanfona é aberta
     */
    public Object registrarView(Request request, Response response) {
        
        int id = Integer.parseInt(request.params(":id"));
        
        // Regista o acesso na base de dados
        faqDAO.registrarAcesso(id);
        
        response.status(200);
        response.type("application/json");
        return "{\"success\": true}";
    }

    /**
     * LISTAR TODAS AS FAQS
     */
    public Object listar(Request request, Response response) {

        List<Faq> faqs = faqDAO.get();

        response.type("application/json");

        Gson gson = new Gson();

        return gson.toJson(faqs);
    }

    /**
     * UPDATE
     */
    public Object update(Request request, Response response) {

        int id = Integer.parseInt(request.params(":id"));

        String pergunta = request.queryParams("pergunta");
        String respostaTexto = request.queryParams("resposta");

        Faq faqExistente = faqDAO.get(id);

        if (faqExistente == null) {
            response.status(404);
            return "FAQ não encontrada.";
        }

        // Atualiza a pergunta e a resposta, mas mantém os acessos que já existiam
        Faq faqAtualizada =
                new Faq(id, pergunta, respostaTexto, faqExistente.getAcessos());

        if (faqDAO.update(faqAtualizada)) {

            response.status(200);

            return "FAQ atualizada com sucesso!";
        } else {

            response.status(500);

            return "Erro ao atualizar FAQ.";
        }
    }

    /**
     * DELETE
     */
    public Object delete(Request request, Response response) {

        int id = Integer.parseInt(request.params(":id"));

        Faq faq = faqDAO.get(id);

        if (faq == null) {
            response.status(404);
            return "FAQ não encontrada.";
        }

        if (faqDAO.delete(id)) {

            response.status(200);

            return "FAQ removida com sucesso!";
        } else {

            response.status(500);

            return "Erro ao remover FAQ.";
        }
    }
}