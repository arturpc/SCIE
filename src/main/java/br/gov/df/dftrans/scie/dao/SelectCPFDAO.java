package br.gov.df.dftrans.scie.dao;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.faces.bean.ApplicationScoped;
import javax.faces.bean.ManagedBean;

import br.gov.df.dftrans.scie.domain.ExtensaoAcesso;
import br.gov.df.dftrans.scie.domain.Solicitacao;
import br.gov.df.dftrans.scie.exceptions.EntityNotFoundException;
import br.gov.df.dftrans.scie.utils.Parametros;

//Classe transformada em managed bean para inclusão no escopo da aplicação
@ManagedBean(name = "selectcpfMB")
@ApplicationScoped
public class SelectCPFDAO {
	public static final String URL = Parametros.getParameter("bd_url");
	public static final String USER = Parametros.getParameter("bd_user_name");
	public static final String PASS = Parametros.getParameter("bd_password");
	public static final String SELECT = "SELECT NUMEROCPF FROM DB_PLE.VW_CPF_ALUNOS_APROVADOS";
	public static final String SELECT_SENHA = "SELECT U.SENHA "+
			"FROM DB_PLE.VW_CA_USUARIO_PORTAL U "+
			"INNER JOIN DB_PLE.VW_CA_PARCEIRO_PROTOCOLO PAR ON PAR.ID = U.PARCEIROPROTOCOLO_ID "+
			"INNER JOIN DB_PLE.VW_CA_PESSOA_PROTOCOLO PES ON PES.ID = PAR.PESSOAPROTOCOLO_ID "+
			"INNER JOIN DB_PLE.VW_CA_PESSOA_FISICA_PROTOCOLO PF ON PF.ID = PES.PESSOAFISICAPROTOCOLO_ID "+
			"WHERE PF.NUMEROCPF = ";
	public static final int SEGUNDAVIA = 1;
	public static final int ACESSOS = 2;
	private static Date date;
	private static List<String[]> CPFList = new ArrayList<String[]>();
	public static SelectCPFDAO dao;
	private static SolicitacaoDAO soldao = SolicitacaoDAO.SolicitacaoDAO();
	private static ExtensaoAcessoDAO acedao = ExtensaoAcessoDAO.ExtensaoAcessoDAO();

	public static SelectCPFDAO SelectCPFDAO() throws ClassNotFoundException, SQLException, EntityNotFoundException {
		if (dao == null) {
			dao = new SelectCPFDAO();
			getAll();
		}
		return dao;
	}

	public static Connection getConnection() throws ClassNotFoundException, SQLException {
		Connection con = null;
		Class.forName("oracle.jdbc.OracleDriver");
		con = DriverManager.getConnection(URL, USER, PASS);
		return con;

	}

	/**
	 * Método que carrega em memória uma lista dos cpfs dos estudantes e a
	 * situação de solicitações de segunda via e extensão de acessos pendentes e
	 * atualiza esta lista a cada 15 minutos (sob demanda)
	 * 
	 * @throws SQLException
	 * @throws ClassNotFoundException
	 * @throws EntityNotFoundException
	 */
	public static void getAll() throws SQLException, ClassNotFoundException, EntityNotFoundException {
		if (date == null || (new Date()).getTime() - date.getTime() > (long) 900000) {
			date = new Date();
			Connection con = getConnection();
			List<String[]> retorno = new ArrayList<String[]>();
			List<String> solicitados = new ArrayList<String>(), acessos = new ArrayList<String>();
			PreparedStatement instrucao = con.prepareStatement(SELECT);
			ResultSet linhas = instrucao.executeQuery();
			String[] s;
			List<Solicitacao> list = soldao.get();
			for (Solicitacao temp : list) {
				if (temp.getStatus() < 2) {
					solicitados.add(temp.getCpf());
				}
			}
			List<ExtensaoAcesso> listAcesso = acedao.get();
			for (ExtensaoAcesso temp : listAcesso) {
				if (temp.getStatus() < 2) {
					acessos.add(temp.getCpf());
				}
			}
			while (linhas.next()) {
				String obj = linhas.getString(1);
				s = new String[3];
				s[0] = obj;
				s[1] = solicitados.contains(obj) ? "1" : "0";
				s[2] = acessos.contains(obj) ? "1" : "0";
				retorno.add(s);
			}
			con.close();
			setCPFList(retorno);
		} else {
			List<String[]> retorno = new ArrayList<String[]>();
			List<String> solicitados = new ArrayList<String>(), acessos = new ArrayList<String>();
			List<Solicitacao> list = soldao.get();
			for (Solicitacao temp : list) {
				if (temp.getStatus() == 0 || temp.getStatus() == 1 || temp.getStatus() == 2 || temp.getStatus() == 4) {
					solicitados.add(temp.getCpf());
				}
			}
			List<ExtensaoAcesso> listAcesso = acedao.get();
			for (ExtensaoAcesso temp : listAcesso) {
				if (temp.getStatus() == 0 || temp.getStatus() == 1 || temp.getStatus() == 2 || temp.getStatus() == 4) {
					acessos.add(temp.getCpf());
				}
			}
			String[] s;
			for (String[] temp : CPFList) {
				s = new String[3];
				s[0] = temp[0];
				s[1] = solicitados.contains(s[0]) ? "1" : "0";
				s[2] = acessos.contains(s[0]) ? "1" : "0";
				retorno.add(s);
			}
			setCPFList(retorno);
		}
	}

	/**
	 * Pesquisa uma solicitação de segunda via pendente para dado cpf
	 * 
	 * @param CPF
	 * @return boolean
	 */
	public static boolean getSegundaVia(String CPF) {
		try {
			getAll();
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (EntityNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		boolean retorno = false;
		for (String[] temp : CPFList) {
			if (temp[0] != null && temp[0].equals(CPF)) {
				if (temp[1].equals("0")) {
					retorno = true;
				}
			}
		}
		return retorno;
	}

	/**
	 * Pesquisa uma solicitação de extensão de acesso pendente para dado cpf
	 * 
	 * @param CPF
	 * @return boolean
	 */
	public static boolean getAcessos(String CPF) {
		try {
			getAll();
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (EntityNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		boolean retorno = false;
		for (String[] temp : CPFList) {
			if (temp[0] != null && temp[0].equals(CPF)) {
				if (temp[2].equals("0")) {
					retorno = true;
				}
			}
		}
		return retorno;
	}

	/**
	 * Seleciona uma lista de nomes de estudantes com até 80% de distância de
	 * edição com dado nome
	 * 
	 * @param nome
	 * @return List<String>
	 */
	public static List<String> getNomes(String nome) {
		List<String> results = new ArrayList<String>();
		try {
			Connection con = getConnection();
			PreparedStatement instrucao = con.prepareStatement(
					"SELECT DISTINCT DS_NOME FROM AGE_TB_ESTUDANTE WHERE UTL_MATCH.EDIT_DISTANCE_SIMILARITY(SUBSTR(DS_NOME,0,"
							+ nome.length() + "), UPPER('" + nome + "')) > 80");
			ResultSet linhas = instrucao.executeQuery();
			while (linhas.next()) {
				results.add(linhas.getString(1));
			}
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return results;
	}
	
	/**
	 * Seleciona uma lista de nomes de estudantes com até 80% de distância de
	 * edição com dado nome
	 * 
	 * @param nome
	 * @return List<String>
	 */
	public static boolean getAutenticacaoSITPASS(String CPF, String senhaParametro) {
		try {
			Connection con = getConnection();
			PreparedStatement instrucao = con.prepareStatement(SELECT_SENHA + CPF);
			ResultSet linhas = instrucao.executeQuery();
			String senha = null;
			while (linhas.next()) {
				senha = linhas.getString(1);
			}
			return senhaParametro.equals(senha);
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return false;
	}

	/**
	 * Seleciona todos os agendamentos já realizados para determinado estudante
	 * com dado nome e dado cpf
	 * 
	 * @param cpf
	 * @param nome
	 * @return String[][]
	 */
	public static String[][] getAgendamento(String cpf, String nome) {
		String[][] results = null;
		try {
			Connection con = getConnection();
			PreparedStatement instrucao = con.prepareStatement(
					"SELECT DISTINCT EST.DS_NOME, EST.NR_CPF, TUR.ANO_MES_DIA, TUR.DS_HORARIO, TUR.DS_TURNO, TUR.DS_LOCAL FROM AGE_TB_ESTUDANTE EST "
							+ "LEFT JOIN AGE_TB_AGENDAMENTO AGE ON EST.ID_ESTUDANTE = AGE.ID_ESTUDANTE LEFT JOIN AGE_TB_TURNO TUR ON "
							+ "TUR.ID_TURNO = AGE.ID_TURNO WHERE EST.ST_AGENDADO = 1 AND EST.ST_CARTAO_NOVO = 1 AND NR_CPF = ? AND EST.DS_NOME = ? ORDER BY ANO_MES_DIA",
					ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_UPDATABLE);
			instrucao.setString(1, cpf);
			instrucao.setString(2, nome);
			ResultSet linhas = instrucao.executeQuery();
			results = new String[getRowCount(linhas)][6];
			int i = 0;
			while (linhas.next()) {
				results[i][0] = linhas.getString(1);
				results[i][1] = linhas.getString(2);
				results[i][2] = linhas.getString(3);
				results[i][3] = linhas.getString(4);
				results[i][4] = linhas.getString(5);
				results[i][5] = linhas.getString(6);
				i++;
			}
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return results;
	}

	/**
	 * Seleciona todos os emails enviados de carga embarcada para determinado
	 * estudante com dado nome e dado cpf
	 * 
	 * @param cpf
	 * @param nome
	 * @return String[][]
	 */
	public static String[][] getCargaEmbarcada(String cpf, String nome) {
		String[][] results = null;
		try {
			Connection con = getConnection();
			PreparedStatement instrucao = con.prepareStatement(
					"SELECT DISTINCT DS_NOME, NR_CARTAO, NR_CPF FROM AGE_TB_ESTUDANTE WHERE ST_CARTAO_NOVO = 0 AND "
							+ "DS_NOME = ? AND NR_CPF = ?",
					ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_UPDATABLE);
			instrucao.setString(2, cpf);
			instrucao.setString(1, nome);
			ResultSet linhas = instrucao.executeQuery();
			results = new String[getRowCount(linhas)][3];
			int i = 0;
			while (linhas.next()) {
				results[i][0] = linhas.getString(1);
				results[i][1] = linhas.getString(2);
				results[i][2] = linhas.getString(3);
				i++;
			}
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return results;
	}

	/**
	 * Retorna a lista de CPFs de estudante regularmente cadastrados e a
	 * situação de pedidos de segunda via e extensão
	 * 
	 * @return List<String[]>
	 */
	public static List<String[]> getCPFList() {
		try {
			getAll();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		} catch (SQLException e) {
			e.printStackTrace();
		} catch (EntityNotFoundException e) {
			e.printStackTrace();
		}
		return CPFList;
	}

	/**
	 * Retorna o booleano representando a presença ou ausência de dado cpf (sem
	 * máscara) na lista de cpfs de estudantes devidamente cadastrados
	 * 
	 * @param cpf
	 * @return boolean
	 */
	public static boolean contains(String cpf) {
		try {
			getAll();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		} catch (SQLException e) {
			e.printStackTrace();
		} catch (EntityNotFoundException e) {
			e.printStackTrace();
		}
		for (String[] s : CPFList) {
			if (s[0] != null && s[0].equals(cpf)) {
				return true;
			}
		}
		return false;
	}

	public static void setCPFList(List<String[]> cPFList) {
		CPFList = cPFList;
	}

	/**
	 * Retorna o número de linhas em dado ResultSet
	 * 
	 * @param resultSet
	 * @return int
	 */
	private static int getRowCount(ResultSet resultSet) {
		if (resultSet == null) {
			return 0;
		}
		int rows = 0;
		try {

			while (resultSet.next()) {
				if (resultSet.last()) {
					rows = resultSet.getRow();
				}
			}
			return rows;
		} catch (SQLException exp) {
			exp.printStackTrace();
		} finally {
			try {
				resultSet.beforeFirst();
			} catch (SQLException exp) {
				exp.printStackTrace();
			}
		}
		return 0;
	}
}
