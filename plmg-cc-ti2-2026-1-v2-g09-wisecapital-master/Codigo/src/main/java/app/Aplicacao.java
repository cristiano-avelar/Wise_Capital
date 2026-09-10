package app;

import static spark.Spark.*;

import service.FaqService;
import service.TrilhaService;
import service.UsuarioService;
import service.QuizService;
import service.ForumService;
import service.MaterialService;

public class Aplicacao {
    
    private static UsuarioService usuarioService = new UsuarioService();
    private static TrilhaService trilhaService = new TrilhaService();
    private static QuizService quizService = new QuizService();
    private static FaqService faqService = new FaqService();
    private static ForumService forumService = new ForumService();
    private static MaterialService materialService = new MaterialService();

    public static void main(String[] args) {
        // Define a porta do servidor
        port(8080); 

        // Configura o Spark para servir arquivos estáticos (frontend)
        // Certifique-se de que sua pasta 'frontend' está em src/main/resources/public
        staticFiles.location("/public"); 
        
        get("/biblioteca/listar", (request, response) -> materialService.listar(request, response));
        post("/biblioteca/insert", (request, response) -> materialService.insert(request, response));
        get("/biblioteca/delete/:id", (request, response) -> materialService.delete(request, response));

        // --- Rotas de Usuário ---
        post("/usuario/insert", (request, response) -> usuarioService.insert(request, response));
        post("/usuario/login", (request, response) -> usuarioService.login(request, response));
        post("/usuario/logout", (request, response) -> usuarioService.logout(request, response));
        
        // A rota /atual DEVE vir antes da rota /:id
        get("/usuario/atual", (request, response) -> usuarioService.getUsuarioAtual(request, response));
        get("/usuario/:id", (request, response) -> usuarioService.get(request, response));

        // --- Rotas de Trilha ---
        get("/trilha/niveis", (request, response) -> trilhaService.listarNiveis(request, response));
        post("/trilha/avancar", (request, response) -> trilhaService.avancarNivel(request, response));
        get("/trilha/quizzes", (request, response) -> trilhaService.listarQuizzesDoUsuario(request, response));

        // --- Rotas de Quiz e Questionário ---
        get("/quiz/:id/perguntas", (request, response) -> quizService.getPerguntasQuiz(request, response));
        post("/quiz/:id/concluir", (request, response) -> quizService.concluirQuiz(request, response)); 
        post("/quiz/cadastrar", (request, response) -> quizService.cadastrar(request, response));
        post("/questionario/avaliar-aberta", (request, response) -> quizService.avaliarRespostaAberta(request, response));
        post("/questionario/salvar", (request, response) -> usuarioService.avaliarDiagnostico(request, response));
        post("/quiz/:id/atualizar", (request, response) -> quizService.atualizar(request, response));
        get("/quiz/delete/:id", (request, response) -> quizService.deletarQuiz(request, response));

        // --- Rotas FAQ ---
        post("/faq/view/:id", (request, response) -> faqService.registrarView(request, response));
        post("/faq/insert", (request, response) -> faqService.insert(request, response));
        get("/faq/listar", (request, response) -> faqService.listar(request, response));
        get("/faq/:id", (request, response) -> faqService.get(request, response));
        put("/faq/update/:id", (request, response) -> faqService.update(request, response));
        get("/faq/delete/:id", (request, response) -> faqService.delete(request, response));
        
        // --- Rotas de Fórum ---
        post("/forum/topico/:id/atualizar", (req, res) -> forumService.updateTopico(req, res));
        post("/forum/insert", (req, res) -> forumService.insert(req, res));
        get("/forum", (req, res) -> forumService.listarTodos(req, res));
        get("/forum/:id", (req, res) -> forumService.getTopico(req, res));
        post("/forum/topico/:id/:tipo", (req, res) -> forumService.interagirTopico(req, res));

        // --- Rotas de Comentários ---
        post("/forum/comentario/:id/atualizar", (req, res) -> forumService.updateComentario(req, res));
        post("/forum/:id/comentarios/insert", (req, res) -> forumService.addComentario(req, res));
        get("/forum/:id/comentarios", (req, res) -> forumService.listarComentarios(req, res));
        post("/forum/comentario/:id/:tipo", (req, res) -> forumService.interagirComentario(req, res));
        get("/forum/comentario/delete/:id", (req, res) -> forumService.deleteComentario(req, res)); // <-- Rota nova adicionada
        
        System.out.println("Servidor Wise Capital rodando em http://localhost:8080");
    }
}