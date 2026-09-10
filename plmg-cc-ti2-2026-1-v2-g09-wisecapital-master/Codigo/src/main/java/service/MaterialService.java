package service;

import com.google.gson.Gson;
import dao.MaterialDAO;
import model.Material;
import spark.Request;
import spark.Response;
import java.util.List;

public class MaterialService {
    private MaterialDAO materialDAO = new MaterialDAO();

    public Object insert(Request request, Response response) {
        String titulo = request.queryParams("titulo");
        String descricao = request.queryParams("descricao");
        String tipo = request.queryParams("tipo");
        String categoria = request.queryParams("categoria");
        String urlConteudo = request.queryParams("urlConteudo");

        Material mat = new Material(-1, titulo, descricao, tipo, categoria, urlConteudo);

        if (materialDAO.insert(mat)) {
            response.status(201);
            return "Material adicionado com sucesso!";
        } else {
            response.status(500);
            return "Erro ao cadastrar material.";
        }
    }

    public Object listar(Request request, Response response) {
        List<Material> materiais = materialDAO.get();
        response.type("application/json");
        return new Gson().toJson(materiais);
    }

    public Object delete(Request request, Response response) {
        int id = Integer.parseInt(request.params(":id"));
        if (materialDAO.delete(id)) {
            response.status(200);
            return "Material removido.";
        } else {
            response.status(500);
            return "Erro ao remover.";
        }
    }
}