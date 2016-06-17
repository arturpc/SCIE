package br.gov.df.dftrans.scie.utils;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;


import br.gov.df.dftrans.scie.domain.CEP;

public class ValidadorCEP {
	
	public static final String URL = "jdbc:mysql://localhost:3306/scie";
	public static final String USER = "root";
	public static final String PASS = "root";
	public static final String SELECT = "SELECT uf.sg_uf, cid.nm_cidade, end.nm_bairro, end.ds_logradouro FROM TB_ENDERECO end LEFT JOIN TB_CIDADE cid ON end.id_cidade = cid. id_cidade LEFT JOIN TB_UF uf ON cid.id_uf = uf.id_uf WHERE end.NR_CEP = ?";
	
	public static Connection getConnection() throws ClassNotFoundException, SQLException {
		Connection con = null;
		Class.forName("oracle.jdbc.OracleDriver");
		con = DriverManager.getConnection(URL, USER, PASS);
		return con;

	}
	
	public static CEP getEndereco(String cep){
		CEP retorno = null;
		try{
			Connection con = getConnection();
			PreparedStatement instrucao = con.prepareStatement(SELECT);
			instrucao.setString(1, cep);
			ResultSet linhas = instrucao.executeQuery();
			retorno = new CEP();
			while (linhas.next()) {
				retorno.setUf(linhas.getString(1));
				retorno.setCidade(linhas.getString(2));
				retorno.setBairro(linhas.getString(3));
				retorno.setLogradouro(linhas.getString(4));
			}
		}catch (ClassNotFoundException e) {
			e.printStackTrace();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return retorno;
	}
    

}
