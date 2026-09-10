package dao;

import java.sql.*;

public class DAO {
	protected Connection conexao;
	
	public DAO() {
		conexao = null;
	}
	
	public boolean conectar() {
	    String driverName = "org.postgresql.Driver";                    
	    String serverName = "wisecapital.postgres.database.azure.com"; // O host da Azure
	    String mydatabase = "postgres"; // Ou "wise_capital" se tiveres criado uma base com esse nome
	    int porta = 5432;
	    
	    // O ?sslmode=require é OBRIGATÓRIO para a Azure
	    String url = "jdbc:postgresql://" + serverName + ":" + porta +"/" + mydatabase + "?sslmode=require";
	    
	    String username = "wise"; 
	    String password = "seilacapitaL1"; // Coloca a tua senha aqui
	    
	    boolean status = false;

	    try {
	        Class.forName(driverName);
	        conexao = DriverManager.getConnection(url, username, password);
	        status = (conexao == null);
	        System.out.println("Conexão efetuada com o postgres na nuvem!");
	    } catch (ClassNotFoundException e) { 
	        System.err.println("Conexão NÃO efetuada com o postgres -- Driver não encontrado -- " + e.getMessage());
	    } catch (SQLException e) {
	        System.err.println("Conexão NÃO efetuada com o postgres -- " + e.getMessage());
	    }

	    return status;
	}
	public boolean close() {
		boolean status = false;
		
		try {
			conexao.close();
			status = true;
		} catch (SQLException e) {
			System.err.println(e.getMessage());
		}
		return status;
	}
}