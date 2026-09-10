package dao;
import model.ForumComentario;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ComentarioDAO extends DAO {
    public ComentarioDAO() { super(); conectar(); }

    public boolean insert(ForumComentario c) {
        boolean status = false;
        try {
            String sql = "INSERT INTO forum_comentarios (topico_id, usuario_id, conteudo, data_criacao) VALUES (?, ?, ?, ?)";
            PreparedStatement st = conexao.prepareStatement(sql);
            st.setInt(1, c.getTopicoId());
            st.setInt(2, c.getUsuarioId());
            st.setString(3, c.getConteudo());
            st.setTimestamp(4, new Timestamp(System.currentTimeMillis()));
            st.executeUpdate();
            st.close();
            status = true;
        } catch (SQLException u) { throw new RuntimeException(u); }
        return status;
    }

    public List<ForumComentario> getByTopicoId(int topicoId) {
        List<ForumComentario> comentarios = new ArrayList<>();
        try {
            String sql = "SELECT * FROM forum_comentarios WHERE topico_id = ? ORDER BY data_criacao ASC";
            PreparedStatement st = conexao.prepareStatement(sql);
            st.setInt(1, topicoId);
            ResultSet rs = st.executeQuery();
            while (rs.next()) {
                comentarios.add(new ForumComentario(rs.getInt("id"), rs.getInt("topico_id"), rs.getInt("usuario_id"), rs.getString("conteudo"), rs.getInt("likes"), rs.getInt("dislikes"), rs.getTimestamp("data_criacao")));
            }
            st.close();
        } catch (SQLException e) { System.err.println(e.getMessage()); }
        return comentarios;
    }

    public boolean interagir(int comentarioId, int usuarioId, String tipo) {
        boolean status = false;
        try {
            String sqlVerifica = "SELECT tipo FROM forum_comentario_likes WHERE usuario_id = ? AND comentario_id = ?";
            PreparedStatement stVerifica = conexao.prepareStatement(sqlVerifica);
            stVerifica.setInt(1, usuarioId);
            stVerifica.setInt(2, comentarioId);
            ResultSet rs = stVerifica.executeQuery();

            if (rs.next()) {
                String tipoAtual = rs.getString("tipo");
                if (!tipoAtual.equals(tipo)) {
                    String sqlUpdate = "UPDATE forum_comentario_likes SET tipo = ? WHERE usuario_id = ? AND comentario_id = ?";
                    PreparedStatement stUpdate = conexao.prepareStatement(sqlUpdate);
                    stUpdate.setString(1, tipo);
                    stUpdate.setInt(2, usuarioId);
                    stUpdate.setInt(3, comentarioId);
                    stUpdate.executeUpdate();
                    stUpdate.close();
                    
                    String colunaMais = tipo.equals("like") ? "likes" : "dislikes";
                    String colunaMenos = tipo.equals("like") ? "dislikes" : "likes";
                    String sqlCount = "UPDATE forum_comentarios SET " + colunaMais + " = " + colunaMais + " + 1, " + colunaMenos + " = " + colunaMenos + " - 1 WHERE id = ?";
                    PreparedStatement stCount = conexao.prepareStatement(sqlCount);
                    stCount.setInt(1, comentarioId);
                    stCount.executeUpdate();
                    stCount.close();
                    status = true;
                }
            } else {
                String sqlInsert = "INSERT INTO forum_comentario_likes (usuario_id, comentario_id, tipo) VALUES (?, ?, ?)";
                PreparedStatement stInsert = conexao.prepareStatement(sqlInsert);
                stInsert.setInt(1, usuarioId);
                stInsert.setInt(2, comentarioId);
                stInsert.setString(3, tipo);
                stInsert.executeUpdate();
                stInsert.close();

                String coluna = tipo.equals("like") ? "likes" : "dislikes";
                String sqlCount = "UPDATE forum_comentarios SET " + coluna + " = " + coluna + " + 1 WHERE id = ?";
                PreparedStatement stCount = conexao.prepareStatement(sqlCount);
                stCount.setInt(1, comentarioId);
                stCount.executeUpdate();
                stCount.close();
                status = true;
            }
            stVerifica.close();
        } catch (SQLException e) { System.err.println(e.getMessage()); }
        return status;
    }

    // --- NOVO: MÉTODO PARA ATUALIZAR UM COMENTÁRIO ---
    public boolean update(int id, String conteudo) {
        boolean status = false;
        try {
            String sql = "UPDATE forum_comentarios SET conteudo = ? WHERE id = ?";
            PreparedStatement st = conexao.prepareStatement(sql);
            st.setString(1, conteudo);
            st.setInt(2, id);
            st.executeUpdate();
            st.close();
            status = true;
        } catch (SQLException u) { throw new RuntimeException(u); }
        return status;
    }
 // --- NOVO: MÉTODO PARA DELETAR UM COMENTÁRIO ---
    public boolean delete(int id) {
        boolean status = false;
        try {
            // Apaga as curtidas do comentário primeiro
            String sqlDelLikes = "DELETE FROM forum_comentario_likes WHERE comentario_id = ?";
            PreparedStatement stDelLikes = conexao.prepareStatement(sqlDelLikes);
            stDelLikes.setInt(1, id);
            stDelLikes.executeUpdate();
            stDelLikes.close();

            // Apaga o comentário
            String sql = "DELETE FROM forum_comentarios WHERE id = ?";
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