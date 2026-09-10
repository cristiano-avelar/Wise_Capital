package dao;

import model.Faq;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class FaqDAO extends DAO {

    public FaqDAO() {
        super();
        conectar();
    }

    public void finalize() {
        close();
    }

    // CREATE
    public boolean insert(Faq faq) {
        boolean status = false;
        try {
            // Agora insere também os acessos
            String sql = "INSERT INTO faq (pergunta, resposta, acessos) VALUES (?, ?, ?)";
            PreparedStatement st = conexao.prepareStatement(sql);

            st.setString(1, faq.getPergunta());
            st.setString(2, faq.getResposta());
            st.setInt(3, faq.getAcessos());

            st.executeUpdate();
            st.close();

            status = true;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return status;
    }

    // READ POR ID
    public Faq get(int id) {
        Faq faq = null;
        try {
            String sql = "SELECT * FROM faq WHERE id = ?";
            PreparedStatement st = conexao.prepareStatement(sql);
            st.setInt(1, id);

            ResultSet rs = st.executeQuery();

            if (rs.next()) {
                // Recupera a nova coluna de acessos da base de dados
                faq = new Faq(
                    rs.getInt("id"),
                    rs.getString("pergunta"),
                    rs.getString("resposta"),
                    rs.getInt("acessos")
                );
            }
            st.close();

        } catch (Exception e) {
            System.err.println(e.getMessage());
        }
        return faq;
    }

    // LISTAR TODOS
    public List<Faq> get() {
        return get("");
    }

    public List<Faq> getOrderByID() {
        return get("id");
    }

    private List<Faq> get(String orderBy) {
        List<Faq> faqs = new ArrayList<Faq>();
        try {
            String sql = "SELECT * FROM faq" +
                ((orderBy.trim().length() == 0) ? "" : (" ORDER BY " + orderBy));

            Statement st = conexao.createStatement(
                ResultSet.TYPE_SCROLL_INSENSITIVE,
                ResultSet.CONCUR_READ_ONLY
            );

            ResultSet rs = st.executeQuery(sql);

            while (rs.next()) {
                // Recupera também os acessos em cada pergunta listada
                Faq faq = new Faq(
                    rs.getInt("id"),
                    rs.getString("pergunta"),
                    rs.getString("resposta"),
                    rs.getInt("acessos")
                );
                faqs.add(faq);
            }
            st.close();

        } catch (Exception e) {
            System.err.println(e.getMessage());
        }
        return faqs;
    }

    // UPDATE
    public boolean update(Faq faq) {
        boolean status = false;
        try {
            // Permite atualizar também os acessos, se necessário
            String sql = "UPDATE faq SET pergunta = ?, resposta = ?, acessos = ? WHERE id = ?";
            PreparedStatement st = conexao.prepareStatement(sql);

            st.setString(1, faq.getPergunta());
            st.setString(2, faq.getResposta());
            st.setInt(3, faq.getAcessos());
            st.setInt(4, faq.getId());

            st.executeUpdate();
            st.close();

            status = true;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return status;
    }

    // DELETE
    public boolean delete(int id) {
        boolean status = false;
        try {
            String sql = "DELETE FROM faq WHERE id = ?";
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

    // REGISTAR ACESSO À FAQ
    public void registrarAcesso(int id) {
        try {
            String sql = "UPDATE faq SET acessos = acessos + 1 WHERE id = ?";
            PreparedStatement st = conexao.prepareStatement(sql);
            st.setInt(1, id);

            st.executeUpdate();
            st.close();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}