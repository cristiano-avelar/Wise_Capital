package dao;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import model.Quiz; 
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class QuizDAO extends DAO {
    public QuizDAO() {
        super();
        conectar();
    }

    // --- MÉTODO ATUALIZADO (Removido o nivel_id) ---
    public List<Quiz> getAllQuizzes() {
        List<Quiz> quizzes = new ArrayList<>();
        try {
            // Ordenamos simplesmente pelo id (que vai de 1 a 100)
            String sql = "SELECT * FROM quizzes ORDER BY id ASC";
            Statement st = conexao.createStatement();
            ResultSet rs = st.executeQuery(sql);
            while (rs.next()) {
                // Instancia apenas com ID e Título
                quizzes.add(new Quiz(rs.getInt("id"), rs.getString("titulo")));
            }
            st.close();
        } catch (Exception e) {
            System.err.println("Erro ao buscar quizzes: " + e.getMessage());
        }
        return quizzes;
    }

    // --- MÉTODO ATUALIZADO (Removido o nivel_id do INSERT) ---
    public boolean cadastrarQuizCompleto(JsonObject dados) {
        try {
            conexao.setAutoCommit(false); 
            
            // O INSERT agora só precisa do titulo
            String sqlQuiz = "INSERT INTO quizzes (titulo) VALUES (?) RETURNING id";
            PreparedStatement stQuiz = conexao.prepareStatement(sqlQuiz);
            stQuiz.setString(1, dados.get("titulo").getAsString());
            ResultSet rsQuiz = stQuiz.executeQuery();
            rsQuiz.next();
            int quizId = rsQuiz.getInt(1);

            JsonArray perguntas = dados.getAsJsonArray("perguntas");
            for (JsonElement pEl : perguntas) {
                JsonObject p = pEl.getAsJsonObject();
                String sqlP = "INSERT INTO perguntas (quiz_id, pergunta, tipo, explicacao) VALUES (?, ?, ?, ?) RETURNING id";
                PreparedStatement stP = conexao.prepareStatement(sqlP);
                stP.setInt(1, quizId);
                stP.setString(2, p.get("texto").getAsString());
                stP.setString(3, p.get("tipo").getAsString());
                stP.setString(4, p.get("explicacao").getAsString());
                ResultSet rsP = stP.executeQuery();
                rsP.next();
                int perguntaId = rsP.getInt(1);

                if (p.get("tipo").getAsString().equals("FECHADA")) {
                    JsonArray opcoes = p.getAsJsonArray("opcoes");
                    int idOpcaoCorreta = -1;
                    for (JsonElement oEl : opcoes) {
                        JsonObject o = oEl.getAsJsonObject();
                        String sqlO = "INSERT INTO opcoes_pergunta (pergunta_id, texto) VALUES (?, ?) RETURNING id";
                        PreparedStatement stO = conexao.prepareStatement(sqlO);
                        stO.setInt(1, perguntaId);
                        stO.setString(2, o.get("texto").getAsString());
                        ResultSet rsO = stO.executeQuery();
                        rsO.next();
                        int opcaoId = rsO.getInt(1);
                        
                        if (o.get("correta").getAsBoolean()) idOpcaoCorreta = opcaoId;
                    }
                    
                    String sqlUpdateP = "UPDATE perguntas SET correta = ? WHERE id = ?";
                    PreparedStatement stUp = conexao.prepareStatement(sqlUpdateP);
                    stUp.setInt(1, idOpcaoCorreta);
                    stUp.setInt(2, perguntaId);
                    stUp.executeUpdate();
                }
            }

            conexao.commit();
            return true;
        } catch (Exception e) {
            try { conexao.rollback(); } catch (SQLException ex) {}
            System.err.println("Erro ao cadastrar quiz: " + e.getMessage());
            return false;
        }
    }

    public JsonArray getPerguntasDoQuiz(int quizId) {
        JsonArray perguntasArray = new JsonArray();
        try {
            String sql = "SELECT * FROM perguntas WHERE quiz_id = ? ORDER BY id";
            PreparedStatement st = conexao.prepareStatement(sql);
            st.setInt(1, quizId);
            ResultSet rs = st.executeQuery();
            
            while (rs.next()) {
                JsonObject perguntaJson = new JsonObject();
                int perguntaId = rs.getInt("id");
                String tipo = rs.getString("tipo");
                
                perguntaJson.addProperty("id", perguntaId);
                perguntaJson.addProperty("pergunta", rs.getString("pergunta"));
                perguntaJson.addProperty("tipo", tipo);
                perguntaJson.addProperty("explicacao", rs.getString("explicacao"));
                
                if ("FECHADA".equals(tipo)) {
                    perguntaJson.addProperty("correta", rs.getInt("correta"));
                    JsonArray opcoesArray = new JsonArray();
                    String sqlOpcoes = "SELECT * FROM opcoes_pergunta WHERE pergunta_id = ?";
                    PreparedStatement stOp = conexao.prepareStatement(sqlOpcoes);
                    stOp.setInt(1, perguntaId);
                    ResultSet rsOp = stOp.executeQuery();
                    
                    while (rsOp.next()) {
                        JsonObject opcao = new JsonObject();
                        opcao.addProperty("id", rsOp.getInt("id"));
                        opcao.addProperty("texto", rsOp.getString("texto"));
                        opcoesArray.add(opcao);
                    }
                    stOp.close();
                    perguntaJson.add("opcoes", opcoesArray);
                }
                perguntasArray.add(perguntaJson);
            }
            st.close();
        } catch (Exception e) {
            System.err.println("Erro ao buscar perguntas: " + e.getMessage());
        }
        return perguntasArray;
    }

    // --- NOVOS MÉTODOS PARA O SISTEMA DE PROGRESSÃO ---

    // Retorna uma lista com os IDs dos quizzes que o utilizador já completou
    public List<Integer> getQuizzesFeitos(int usuarioId) {
        List<Integer> feitos = new ArrayList<>();
        try {
            String sql = "SELECT quiz_id FROM usuario_quizzes_feitos WHERE usuario_id = ?";
            PreparedStatement st = conexao.prepareStatement(sql);
            st.setInt(1, usuarioId);
            ResultSet rs = st.executeQuery();
            while (rs.next()) {
                feitos.add(rs.getInt("quiz_id"));
            }
            st.close();
        } catch (Exception e) {
            System.err.println("Erro ao buscar quizzes feitos: " + e.getMessage());
        }
        return feitos;
    }

    // Quando o utilizador acerta o quiz, marca como concluído no banco de dados e liberta o próximo (caso seja o limite)
    public void registrarQuizFeito(int usuarioId, int quizId) {
        try {
            // Tenta inserir na tabela de progresso. O ON CONFLICT DO NOTHING evita duplicados caso o utilizador refaça o quiz
            String sql = "INSERT INTO usuario_quizzes_feitos (usuario_id, quiz_id) VALUES (?, ?) ON CONFLICT DO NOTHING";
            PreparedStatement st = conexao.prepareStatement(sql);
            st.setInt(1, usuarioId);
            st.setInt(2, quizId);
            st.executeUpdate();
            st.close();
            
            // Se o quiz atual era o último que ele tinha liberado, liberta o próximo (quizId + 1)
            // Utiliza o GREATEST para garantir que não reduzimos o nível caso o utilizador refaça um quiz muito antigo
            String sqlUpdate = "UPDATE usuarios SET quiz_liberado_maximo = GREATEST(quiz_liberado_maximo, ?) WHERE id = ?";
            PreparedStatement stUp = conexao.prepareStatement(sqlUpdate);
            stUp.setInt(1, quizId + 1);
            stUp.setInt(2, usuarioId);
            stUp.executeUpdate();
            stUp.close();
            
        } catch (Exception e) {
            System.err.println("Erro ao registar quiz feito: " + e.getMessage());
        }
    }
 // --- NOVO: MÉTODO PARA ATUALIZAR UM QUIZ ---
    public boolean atualizarQuizCompleto(int quizId, JsonObject dados) {
        try {
            conexao.setAutoCommit(false); 
            
            // 1. Atualiza o título do Quiz
            String sqlQuiz = "UPDATE quizzes SET titulo = ? WHERE id = ?";
            PreparedStatement stQuiz = conexao.prepareStatement(sqlQuiz);
            stQuiz.setString(1, dados.get("titulo").getAsString());
            stQuiz.setInt(2, quizId);
            stQuiz.executeUpdate();

            // 2. Apaga as opções e as perguntas antigas deste quiz para evitar duplicação ou falha nas referências
            String sqlDelOpcoes = "DELETE FROM opcoes_pergunta WHERE pergunta_id IN (SELECT id FROM perguntas WHERE quiz_id = ?)";
            PreparedStatement stDelOpcoes = conexao.prepareStatement(sqlDelOpcoes);
            stDelOpcoes.setInt(1, quizId);
            stDelOpcoes.executeUpdate();

            String sqlDelPerg = "DELETE FROM perguntas WHERE quiz_id = ?";
            PreparedStatement stDelPerg = conexao.prepareStatement(sqlDelPerg);
            stDelPerg.setInt(1, quizId);
            stDelPerg.executeUpdate();

            // 3. Re-insere as perguntas e opções vindas do formulário atualizado
            JsonArray perguntas = dados.getAsJsonArray("perguntas");
            for (JsonElement pEl : perguntas) {
                JsonObject p = pEl.getAsJsonObject();
                String sqlP = "INSERT INTO perguntas (quiz_id, pergunta, tipo, explicacao) VALUES (?, ?, ?, ?) RETURNING id";
                PreparedStatement stP = conexao.prepareStatement(sqlP);
                stP.setInt(1, quizId);
                stP.setString(2, p.get("texto").getAsString());
                stP.setString(3, p.get("tipo").getAsString());
                stP.setString(4, p.get("explicacao").getAsString());
                ResultSet rsP = stP.executeQuery();
                rsP.next();
                int perguntaId = rsP.getInt(1);

                if (p.get("tipo").getAsString().equals("FECHADA")) {
                    JsonArray opcoes = p.getAsJsonArray("opcoes");
                    int idOpcaoCorreta = -1;
                    for (JsonElement oEl : opcoes) {
                        JsonObject o = oEl.getAsJsonObject();
                        String sqlO = "INSERT INTO opcoes_pergunta (pergunta_id, texto) VALUES (?, ?) RETURNING id";
                        PreparedStatement stO = conexao.prepareStatement(sqlO);
                        stO.setInt(1, perguntaId);
                        stO.setString(2, o.get("texto").getAsString());
                        ResultSet rsO = stO.executeQuery();
                        rsO.next();
                        int opcaoId = rsO.getInt(1);
                        
                        if (o.get("correta").getAsBoolean()) idOpcaoCorreta = opcaoId;
                    }
                    
                    String sqlUpdateP = "UPDATE perguntas SET correta = ? WHERE id = ?";
                    PreparedStatement stUp = conexao.prepareStatement(sqlUpdateP);
                    stUp.setInt(1, idOpcaoCorreta);
                    stUp.setInt(2, perguntaId);
                    stUp.executeUpdate();
                }
            }

            conexao.commit();
            return true;
        } catch (Exception e) {
            try { conexao.rollback(); } catch (SQLException ex) {}
            System.err.println("Erro ao atualizar quiz: " + e.getMessage());
            return false;
        }
    }
 // --- NOVO: MÉTODO PARA DELETAR UM QUIZ ---
    public boolean deleteQuiz(int quizId) {
        try {
            conexao.setAutoCommit(false); // Inicia uma transação

            // 1. Apaga as opções das perguntas ligadas a este quiz
            String sqlDelOpcoes = "DELETE FROM opcoes_pergunta WHERE pergunta_id IN (SELECT id FROM perguntas WHERE quiz_id = ?)";
            PreparedStatement st1 = conexao.prepareStatement(sqlDelOpcoes);
            st1.setInt(1, quizId);
            st1.executeUpdate();

            // 2. Apaga as perguntas deste quiz
            String sqlDelPerguntas = "DELETE FROM perguntas WHERE quiz_id = ?";
            PreparedStatement st2 = conexao.prepareStatement(sqlDelPerguntas);
            st2.setInt(1, quizId);
            st2.executeUpdate();

            // 3. Apaga os registos de utilizadores que já fizeram este quiz
            String sqlDelProgresso = "DELETE FROM usuario_quizzes_feitos WHERE quiz_id = ?";
            PreparedStatement st3 = conexao.prepareStatement(sqlDelProgresso);
            st3.setInt(1, quizId);
            st3.executeUpdate();

            // 4. Finalmente, apaga o próprio quiz
            String sqlDelQuiz = "DELETE FROM quizzes WHERE id = ?";
            PreparedStatement st4 = conexao.prepareStatement(sqlDelQuiz);
            st4.setInt(1, quizId);
            st4.executeUpdate();

            conexao.commit(); // Confirma as alterações
            return true;

        } catch (Exception e) {
            try { conexao.rollback(); } catch (SQLException ex) {}
            System.err.println("Erro ao deletar quiz: " + e.getMessage());
            return false;
        }
    }
}