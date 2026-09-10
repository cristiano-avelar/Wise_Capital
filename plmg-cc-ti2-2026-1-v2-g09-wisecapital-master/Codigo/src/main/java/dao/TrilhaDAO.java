package dao;

import model.NivelTrilha;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class TrilhaDAO extends DAO {
    public TrilhaDAO() {
        super();
        conectar();
    }
    public List<NivelTrilha> getAllNiveis() {
        List<NivelTrilha> niveis = new ArrayList<>();
        try {
            String sql = "SELECT * FROM niveis_trilha ORDER BY id";
            Statement st = conexao.createStatement();
            ResultSet rs = st.executeQuery(sql);
            while (rs.next()) {
                niveis.add(new NivelTrilha(rs.getInt("id"), rs.getString("titulo"), rs.getString("descricao")));
            }
            st.close();
        } catch (Exception e) {
            System.err.println(e.getMessage());
        }
        return niveis;
    }
    public boolean atualizarNivelUsuario(int usuarioId, int novoNivel) {
        boolean status = false;
        try {
            String sql = "UPDATE usuarios SET nivel_atual_id = ? WHERE id = ?";
            PreparedStatement st = conexao.prepareStatement(sql);
            st.setInt(1, novoNivel);
            st.setInt(2, usuarioId);
            st.executeUpdate();
            st.close();
            status = true;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return status;
    }
}