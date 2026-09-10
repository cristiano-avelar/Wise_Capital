package dao;
import model.ForumTopico;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ForumDAO extends DAO {
    public ForumDAO() { super(); conectar(); }

    public boolean insert(ForumTopico topico) {
        boolean status = false;
        try {
            String sql = "INSERT INTO forum_topicos (usuario_id, titulo, conteudo, imagem_url, data_criacao) VALUES (?, ?, ?, ?, ?)";
            PreparedStatement st = conexao.prepareStatement(sql);
            st.setInt(1, topico.getUsuarioId());
            st.setString(2, topico.getTitulo());
            st.setString(3, topico.getConteudo());
            st.setString(4, topico.getImagemUrl());
            st.setTimestamp(5, new Timestamp(System.currentTimeMillis()));
            st.executeUpdate();
            st.close();
            status = true;
        } catch (SQLException u) { throw new RuntimeException(u); }
        return status;
    }

    public List<ForumTopico> getAll() {
        List<ForumTopico> topicos = new ArrayList<>();
        try {
            String sql = "SELECT t.*, (SELECT COUNT(*) FROM forum_comentarios c WHERE c.topico_id = t.id) AS qtd_comentarios FROM forum_topicos t ORDER BY t.data_criacao DESC";
            Statement st = conexao.createStatement();
            ResultSet rs = st.executeQuery(sql);
            while (rs.next()) {
                topicos.add(new ForumTopico(rs.getInt("id"), rs.getInt("usuario_id"), rs.getString("titulo"), rs.getString("conteudo"), rs.getString("imagem_url"), rs.getInt("likes"), rs.getInt("dislikes"), rs.getInt("qtd_comentarios"), rs.getTimestamp("data_criacao")));
            }
            st.close();
        } catch (SQLException e) { System.err.println(e.getMessage()); }
        return topicos;
    }

    public ForumTopico getById(int id) {
        ForumTopico topico = null;
        try {
            String sql = "SELECT t.*, (SELECT COUNT(*) FROM forum_comentarios c WHERE c.topico_id = t.id) AS qtd_comentarios FROM forum_topicos t WHERE id = ?";
            PreparedStatement st = conexao.prepareStatement(sql);
            st.setInt(1, id);
            ResultSet rs = st.executeQuery();
            if (rs.next()) {
                topico = new ForumTopico(rs.getInt("id"), rs.getInt("usuario_id"), rs.getString("titulo"), rs.getString("conteudo"), rs.getString("imagem_url"), rs.getInt("likes"), rs.getInt("dislikes"), rs.getInt("qtd_comentarios"), rs.getTimestamp("data_criacao"));
            }
            st.close();
        } catch (SQLException e) { System.err.println(e.getMessage()); }
        return topico;
    }

    public boolean interagir(int topicoId, int usuarioId, String tipo) {
        boolean status = false;
        try {
            String sqlVerifica = "SELECT tipo FROM forum_topico_likes WHERE usuario_id = ? AND topico_id = ?";
            PreparedStatement stVerifica = conexao.prepareStatement(sqlVerifica);
            stVerifica.setInt(1, usuarioId);
            stVerifica.setInt(2, topicoId);
            ResultSet rs = stVerifica.executeQuery();

            if (rs.next()) {
                String tipoAtual = rs.getString("tipo");
                if (!tipoAtual.equals(tipo)) {
                    String sqlUpdate = "UPDATE forum_topico_likes SET tipo = ? WHERE usuario_id = ? AND topico_id = ?";
                    PreparedStatement stUpdate = conexao.prepareStatement(sqlUpdate);
                    stUpdate.setString(1, tipo);
                    stUpdate.setInt(2, usuarioId);
                    stUpdate.setInt(3, topicoId);
                    stUpdate.executeUpdate();
                    stUpdate.close();
                    
                    String colunaMais = tipo.equals("like") ? "likes" : "dislikes";
                    String colunaMenos = tipo.equals("like") ? "dislikes" : "likes";
                    String sqlCount = "UPDATE forum_topicos SET " + colunaMais + " = " + colunaMais + " + 1, " + colunaMenos + " = " + colunaMenos + " - 1 WHERE id = ?";
                    PreparedStatement stCount = conexao.prepareStatement(sqlCount);
                    stCount.setInt(1, topicoId);
                    stCount.executeUpdate();
                    stCount.close();
                    status = true;
                }
            } else {
                String sqlInsert = "INSERT INTO forum_topico_likes (usuario_id, topico_id, tipo) VALUES (?, ?, ?)";
                PreparedStatement stInsert = conexao.prepareStatement(sqlInsert);
                stInsert.setInt(1, usuarioId);
                stInsert.setInt(2, topicoId);
                stInsert.setString(3, tipo);
                stInsert.executeUpdate();
                stInsert.close();

                String coluna = tipo.equals("like") ? "likes" : "dislikes";
                String sqlCount = "UPDATE forum_topicos SET " + coluna + " = " + coluna + " + 1 WHERE id = ?";
                PreparedStatement stCount = conexao.prepareStatement(sqlCount);
                stCount.setInt(1, topicoId);
                stCount.executeUpdate();
                stCount.close();
                status = true;
            }
            stVerifica.close();
        } catch (SQLException e) { System.err.println(e.getMessage()); }
        return status;
    }

    // --- NOVO: MÉTODO PARA ATUALIZAR UM TÓPICO ---
    public boolean update(int id, String titulo, String conteudo, String imagemUrl) {
        boolean status = false;
        try {
            String sql = "UPDATE forum_topicos SET titulo = ?, conteudo = ?, imagem_url = ? WHERE id = ?";
            PreparedStatement st = conexao.prepareStatement(sql);
            st.setString(1, titulo);
            st.setString(2, conteudo);
            st.setString(3, imagemUrl);
            st.setInt(4, id);
            st.executeUpdate();
            st.close();
            status = true;
        } catch (SQLException u) { throw new RuntimeException(u); }
        return status;
    }
 // --- NOVO: MÉTODO PARA DELETAR UM TÓPICO ---
    public boolean delete(int id) {
        boolean status = false;
        try {
            // Primeiro deleta as curtidas e comentários associados (para evitar conflito de chave estrangeira)
            String sqlDelLikes = "DELETE FROM forum_topico_likes WHERE topico_id = ?";
            PreparedStatement stDelLikes = conexao.prepareStatement(sqlDelLikes);
            stDelLikes.setInt(1, id);
            stDelLikes.executeUpdate();
            stDelLikes.close();

            String sqlDelComLikes = "DELETE FROM forum_comentario_likes WHERE comentario_id IN (SELECT id FROM forum_comentarios WHERE topico_id = ?)";
            PreparedStatement stDelComLikes = conexao.prepareStatement(sqlDelComLikes);
            stDelComLikes.setInt(1, id);
            stDelComLikes.executeUpdate();
            stDelComLikes.close();

            String sqlDelComentarios = "DELETE FROM forum_comentarios WHERE topico_id = ?";
            PreparedStatement stDelComentarios = conexao.prepareStatement(sqlDelComentarios);
            stDelComentarios.setInt(1, id);
            stDelComentarios.executeUpdate();
            stDelComentarios.close();

            // Finalmente deleta o tópico
            String sql = "DELETE FROM forum_topicos WHERE id = ?";
            PreparedStatement st = conexao.prepareStatement(sql);
            st.setInt(1, id);
            st.executeUpdate();
            st.close();
            
            status = true;
        } catch (SQLException u) { 
            throw new RuntimeException(u); 
        }
        return status;
    }
}