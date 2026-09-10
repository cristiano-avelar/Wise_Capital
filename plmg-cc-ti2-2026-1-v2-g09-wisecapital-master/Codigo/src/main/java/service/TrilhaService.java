package service;
import dao.QuizDAO;
import dao.UsuarioDAO;
import model.Quiz;
import model.Usuario;
import dao.TrilhaDAO;
import model.NivelTrilha;
import spark.Request;
import spark.Response;
import java.util.List;

public class TrilhaService {
	private QuizDAO quizDAO = new QuizDAO();
	private UsuarioDAO usuarioDAO = new UsuarioDAO();

	public Object listarQuizzesDoUsuario(Request request, Response response) {
	    response.type("application/json");
	    Integer idLogado = request.session().attribute("usuario_logado");
	    
	    if (idLogado == null) {
	        response.status(401);
	        return "{\"erro\": \"Usuário não logado\"}";
	    }

	    Usuario usuario = usuarioDAO.get(idLogado);
	    int maxLiberado = usuario.getQuizLiberadoMaximo(); // Pega do BD
	    
	    List<Quiz> quizzes = quizDAO.getAllQuizzes();
	    List<Integer> feitos = quizDAO.getQuizzesFeitos(idLogado); // IDs concluídos
	    
	    StringBuilder json = new StringBuilder("[");
	    for (int i = 0; i < quizzes.size(); i++) {
	        Quiz q = quizzes.get(i);
	        
	        String status = "bloqueado";
	        
	        if (feitos.contains(q.getId())) {
	            status = "feito";
	        } else if (q.getId() <= maxLiberado) {
	            status = "liberado"; // Pode clicar e fazer, mas não tá marcado verde ainda
	        }
	        
	        json.append("{")
	            .append("\"id\": ").append(q.getId()).append(",")
	            .append("\"titulo\": \"").append(q.getTitulo()).append("\",")
	            .append("\"status\": \"").append(status).append("\"")
	            .append("}");
	            
	        if (i < quizzes.size() - 1) json.append(",");
	    }
	    json.append("]");
	    
	    response.status(200);
	    return json.toString();
	}
    private TrilhaDAO trilhaDAO = new TrilhaDAO();

    
    public Object listarNiveis(Request request, Response response) {
        List<NivelTrilha> niveis = trilhaDAO.getAllNiveis();
        response.type("application/json");
        return niveis.toString(); 
    }

    
    public Object avancarNivel(Request request, Response response) {
        int usuarioId = Integer.parseInt(request.queryParams("usuarioId"));
        int nivelCompletado = Integer.parseInt(request.queryParams("nivelId"));
        
        int proximoNivel = nivelCompletado + 1;
        
        if (trilhaDAO.atualizarNivelUsuario(usuarioId, proximoNivel)) {
            response.status(200);
            return "Parabéns! Você avançou para o nível " + proximoNivel;
        } else {
            response.status(500);
            return "Erro ao atualizar progresso.";
        }
    }
}