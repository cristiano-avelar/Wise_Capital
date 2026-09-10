package service;

import dao.ForumDAO;
import dao.ComentarioDAO;
import model.ForumTopico;
import model.ForumComentario;
import spark.Request;
import spark.Response;
import com.google.gson.Gson;

public class ForumService {
    private ForumDAO forumDAO = new ForumDAO();
    private ComentarioDAO comentarioDAO = new ComentarioDAO();
    private Gson gson = new Gson();

    public Object insert(Request request, Response response) {
        int usuarioId = Integer.parseInt(request.queryParams("usuarioId"));
        String titulo = request.queryParams("titulo");
        String conteudo = request.queryParams("conteudo");
        String imagemUrl = request.queryParams("imagemUrl");
        ForumTopico topico = new ForumTopico(-1, usuarioId, titulo, conteudo, imagemUrl, 0, 0, 0, null);
        response.type("application/json");
        return forumDAO.insert(topico) ? "{\"success\": true}" : "{\"success\": false}";
    }

    public Object listarTodos(Request request, Response response) {
        response.type("application/json");
        return gson.toJson(forumDAO.getAll());
    }

    public Object getTopico(Request request, Response response) {
        response.type("application/json");
        return gson.toJson(forumDAO.getById(Integer.parseInt(request.params(":id"))));
    }

    public Object interagirTopico(Request request, Response response) {
        Integer usuarioId = request.session().attribute("usuario_logado");
        response.type("application/json");

        if (usuarioId == null) {
            response.status(401);
            return "{\"success\": false, \"message\": \"Você precisa fazer login para curtir.\"}";
        }

        boolean success = forumDAO.interagir(Integer.parseInt(request.params(":id")), usuarioId, request.params(":tipo"));
        return success ? "{\"success\": true}" : "{\"success\": false}";
    }

    public Object updateTopico(Request request, Response response) {
        int id = Integer.parseInt(request.params(":id"));
        String titulo = request.queryParams("titulo");
        String conteudo = request.queryParams("conteudo");
        String imagemUrl = request.queryParams("imagemUrl");
        response.type("application/json");
        return forumDAO.update(id, titulo, conteudo, imagemUrl) ? "{\"success\": true}" : "{\"success\": false}";
    }

    // --- MÉTODOS DE COMENTÁRIOS ---
    public Object addComentario(Request request, Response response) {
        int topicoId = Integer.parseInt(request.params(":id"));
        int usuarioId = Integer.parseInt(request.queryParams("usuarioId"));
        String conteudo = request.queryParams("conteudo");
        ForumComentario c = new ForumComentario(-1, topicoId, usuarioId, conteudo, 0, 0, null);
        response.type("application/json");
        return comentarioDAO.insert(c) ? "{\"success\": true}" : "{\"success\": false}";
    }

    public Object listarComentarios(Request request, Response response) {
        response.type("application/json");
        return gson.toJson(comentarioDAO.getByTopicoId(Integer.parseInt(request.params(":id"))));
    }

    public Object interagirComentario(Request request, Response response) {
        Integer usuarioId = request.session().attribute("usuario_logado");
        response.type("application/json");

        if (usuarioId == null) {
            response.status(401);
            return "{\"success\": false, \"message\": \"Você precisa fazer login para curtir.\"}";
        }

        boolean success = comentarioDAO.interagir(Integer.parseInt(request.params(":id")), usuarioId, request.params(":tipo"));
        return success ? "{\"success\": true}" : "{\"success\": false}";
    }

    public Object updateComentario(Request request, Response response) {
        int id = Integer.parseInt(request.params(":id"));
        String conteudo = request.queryParams("conteudo");
        response.type("application/json");
        return comentarioDAO.update(id, conteudo) ? "{\"success\": true}" : "{\"success\": false}";
    }

    // --- NOVO: MÉTODO PARA DELETAR COMENTÁRIO ---
    public Object deleteComentario(Request request, Response response) {
        int id = Integer.parseInt(request.params(":id"));
        response.type("application/json");
        
        if (comentarioDAO.delete(id)) {
            return "{\"success\": true}";
        } else {
            response.status(500);
            return "{\"success\": false, \"message\": \"Erro ao deletar comentário.\"}";
        }
    }
}