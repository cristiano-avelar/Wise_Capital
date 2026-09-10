package dao;

import model.Material;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class MaterialDAO extends DAO {

    public MaterialDAO() {
        super();
        conectar();
    }

    public boolean insert(Material mat) {
        boolean status = false;
        try {
            String sql = "INSERT INTO biblioteca (titulo, descricao, tipo, categoria, url_conteudo) VALUES (?, ?, ?, ?, ?)";
            PreparedStatement st = conexao.prepareStatement(sql);
            st.setString(1, mat.getTitulo());
            st.setString(2, mat.getDescricao());
            st.setString(3, mat.getTipo());
            st.setString(4, mat.getCategoria());
            st.setString(5, mat.getUrlConteudo());
            st.executeUpdate();
            st.close();
            status = true;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return status;
    }

    public List<Material> get() {
        List<Material> lista = new ArrayList<>();
        try {
            String sql = "SELECT * FROM biblioteca ORDER BY id DESC";
            Statement st = conexao.createStatement();
            ResultSet rs = st.executeQuery(sql);
            while (rs.next()) {
                lista.add(new Material(
                    rs.getInt("id"),
                    rs.getString("titulo"),
                    rs.getString("descricao"),
                    rs.getString("tipo"),
                    rs.getString("categoria"),
                    rs.getString("url_conteudo")
                ));
            }
            st.close();
        } catch (Exception e) {
            System.err.println(e.getMessage());
        }
        return lista;
    }

    public boolean delete(int id) {
        boolean status = false;
        try {
            String sql = "DELETE FROM biblioteca WHERE id = ?";
            PreparedStatement st = conexao.prepareStatement(sql);
            st.setInt(1, id);
            st.executeUpdate();
            st.close();
            status = true;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return status;
    }
}