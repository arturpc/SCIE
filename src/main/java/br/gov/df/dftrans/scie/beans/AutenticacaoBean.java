package br.gov.df.dftrans.scie.beans;

import java.util.ArrayList;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.context.FacesContext;

import javax.servlet.http.HttpSession;

import br.gov.df.dftrans.scie.dao.ExtensaoAcessoDAO;
import br.gov.df.dftrans.scie.dao.InstituicaoEnsinoDAO;
import br.gov.df.dftrans.scie.dao.LogDAO;
import br.gov.df.dftrans.scie.dao.SolicitacaoDAO;
import br.gov.df.dftrans.scie.dao.UsuarioDAO;
import br.gov.df.dftrans.scie.domain.Usuario;
import br.gov.df.dftrans.scie.exceptions.EntityNotFoundException;
import br.gov.df.dftrans.scie.utils.FacesUtil;

@ManagedBean(name = "loginBean", eager = true)
@SessionScoped
/**
 * Classe responsável pela autenticação do login do usuário
 * 
 * @author 9317295
 *
 */
public class AutenticacaoBean {
	private Usuario user;
	private String novaSenha = "", senhaAntiga = "", novaSenha2 = "";
	private UsuarioDAO userdao = UsuarioDAO.UsuarioDAO();
	private InstituicaoEnsinoDAO instdao = InstituicaoEnsinoDAO.InstituicaoEnsinoDAO();
	private LogDAO logdao = LogDAO.LogDAO();
	private SolicitacaoDAO soldao = SolicitacaoDAO.SolicitacaoDAO();
	private ExtensaoAcessoDAO extdao = ExtensaoAcessoDAO.ExtensaoAcessoDAO();
	private boolean captcha = false;
	//private boolean captcha = true;

	public AutenticacaoBean() {
		setUser(new Usuario());
	}

	/**
	 * valida a senha e o login do usuário
	 * 
	 * @return o redirecionamento da página posterior de acordo com o resultado
	 *         da autenticação
	 */
	public String validate() {
		try {
			Usuario usuario = userdao.getByAutenticacao(user.getLogin(), user.getSenha());
			if (usuario == null) {
				FacesUtil.addMsggError("Usuario ou Senha inválidos!");
				getUser().setSenha(null);
				return "/pages/login.xhtml?faces-redirect=false";
			} else {
				HttpSession session = (HttpSession) FacesContext.getCurrentInstance().getExternalContext()
						.getSession(false);
				session.setAttribute("usuario", usuario);
				if (usuario.getReset() == 0) {
					return "/pages/resetSenha.xhtml?faces-redirect=true";
				} else {
					getUser().setSenha(null);
					switch (usuario.getPerfil()) {
					case (0):
						session.setAttribute("instituicao", instdao.getByRepNome(usuario.getNome()));
						return "/pages/autenticado/instituicao/instituicaoIndex.xhtml?faces-redirect=true";
					case (2):
						return "/pages/autenticado/validador/validadorIndex.xhtml?faces-redirect=true";
					case (1):
						return "/pages/autenticado/admin/adminIndex.xhtml?faces-redirect=true";
					default:
						return "/pages/index.xhtml?faces-redirect=true";
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			FacesUtil.addMsggError("Usuario ou Senha inválidos!");
			getUser().setSenha(null);
			return "/pages/login.xhtml?faces-redirect=false";
		}

	}

	/**
	 * gera um teclado com a posição das teclas aleatórias
	 * 
	 * @return String com as teclas do teclado embaralhadas
	 */
	public String gerarTeclado() {
		ArrayList<String> teclas = new ArrayList<String>();
		ArrayList<String> numeros = new ArrayList<String>();
		ArrayList<String> botoes = new ArrayList<String>();
		teclas.add("a");
		teclas.add("b");
		teclas.add("c");
		teclas.add("w");
		teclas.add("d");
		teclas.add("e");
		teclas.add("f");
		teclas.add("g");
		teclas.add("h");
		teclas.add("i");
		teclas.add("j");
		teclas.add("k");
		teclas.add("l");
		teclas.add("m");
		teclas.add("n");
		teclas.add("o");
		teclas.add("p");
		teclas.add("q");
		teclas.add("r");
		teclas.add("s");
		teclas.add("t");
		teclas.add("u");
		teclas.add("v");
		teclas.add("x");
		teclas.add("y");
		teclas.add("z");
		numeros.add("1");
		numeros.add("2");
		numeros.add("3");
		numeros.add("4");
		numeros.add("0");
		numeros.add("5");
		numeros.add("6");
		numeros.add("7");
		numeros.add("8");
		numeros.add("9");
		botoes.add("close");
		botoes.add("clear");
		botoes.add("back");
		botoes.add("shift");
		botoes.add("spacebar");
		String resultado = "";
		int seed;
		for (int i = 0; i < 10; i++) {
			seed = (int) (Math.random() * 1000 % numeros.size());
			resultado += numeros.remove(seed);
		}
		resultado += "-" + botoes.remove(0) + ",";
		for (int i = 0; i < 36; i++) {
			seed = (int) (Math.random() * 1000 % teclas.size());
			if (teclas.size() == 0) {
				resultado += "-" + "space";
			} else {
				resultado += teclas.remove(seed);
			}
			if (botoes.size() == 0) {
				resultado += "-" + "space" + ",";
			} else {
				if (i % 10 == 9) {
					resultado += "-" + botoes.remove(0) + ",";
				}
			}
		}
		resultado += "-" + botoes.remove(0) + ",";
		return resultado;
	}

	/**
	 * Alterar senha do usuário
	 * 
	 * @return redirecionamento para página de resetar senha
	 */
	public String resetPassword() {
		Usuario usuario = userdao.getByAutenticacao(user.getLogin(), getSenhaAntiga());
		// casso não tem registros no banco da senha e do login informado
		if (usuario == null) {
			FacesUtil.addMsggError("Senha Antiga inválida!");
			getUser().setSenha(null);
			return "/pages/resetSenha.xhtml?faces-redirect=false";
		}
		// validação caso a senha seja igual a antiga
		if (getNovaSenha().equals(getSenhaAntiga())) {
			FacesUtil.addMsggError("A nova senha deve ser diferente da antiga!");
			setNovaSenha(null);
			setNovaSenha2(null);
			setSenhaAntiga(null);
			return "/pages/resetSenha.xhtml?faces-redirect=false";
		}
		// caso a confirmação da nova senha de errado
		if (!getNovaSenha().equals(getNovaSenha2())) {
			FacesUtil.addMsggError("A senha e confirmação não coincidem!");
			setNovaSenha(null);
			setNovaSenha2(null);
			setSenhaAntiga(null);
			return "/pages/resetSenha.xhtml?faces-redirect=false";
		}
		// salva os novos dados no usuário da sessão caso de tudo certo
		HttpSession session = (HttpSession) FacesContext.getCurrentInstance().getExternalContext().getSession(false);
		setUser((Usuario) session.getAttribute("usuario"));
		getUser().setSenha(getNovaSenha());
		getUser().setReset(1);
		userdao.update(getUser());
		getUser().setSenha(null);
		return "/pages/login.xhtml?faces-redirect=true";
	}

	/**
	 * Descobrir se tem usuário logado
	 * 
	 * @return true caso não tenha e false caso tenha usuário logado
	 */
	public boolean logado() {
		HttpSession session = (HttpSession) FacesContext.getCurrentInstance().getExternalContext().getSession(false);
		Usuario teste = (Usuario) session.getAttribute("usuario");
		if (teste == null) {
			return true;
		} else {
			return false;
		}
	}

	/**
	 * Evento de deslogar usuário
	 * 
	 * @return true ou false
	 */
	public boolean deslogado() {
		return !logado();
	}

	/**
	 * seta o usuário da sessão como null, para garantir que foi realmente
	 * deslogado
	 * 
	 * @return direcionamento para página de login
	 */
	public String logOff() {
		HttpSession session = (HttpSession) FacesContext.getCurrentInstance().getExternalContext().getSession(false);
		session.setAttribute("usuario", null);
		getUser().setSenha(null);
		return "/pages/login.xhtml?faces-redirect=true";
	}

	/**
	 * Classe responsavel por validar o cadastro
	 * 
	 * @return o redirecionamento da próxima página
	 */
	public String validadorCadastro() {
		// pega o usuário da sessão
		HttpSession session = (HttpSession) FacesContext.getCurrentInstance().getExternalContext().getSession(false);
		setUser((Usuario) session.getAttribute("usuario"));
		try {
			// caso não tenha logs abertos
			if (logdao.getOpens().isEmpty() && logdao.getAnalisysUser(getUser()) == null) {
				FacesUtil.addMsggError("Não há documentos a serem validados");
				return "/pages/autenticado/validador/validadorIndex.xhtml?faces-redirect=false";
			}
		} catch (EntityNotFoundException e) {
			e.printStackTrace();
		}
		return "/pages/autenticado/validador/validadorCadastro.xhtml?faces-redirect=true";
	}

	/**
	 * Verifica se tem solicitação para se validar
	 * 
	 * @return redirecionamento de acordo com a resposta
	 */
	public String validadorSolicitacao() {
		HttpSession session = (HttpSession) FacesContext.getCurrentInstance().getExternalContext().getSession(false);
		setUser((Usuario) session.getAttribute("usuario"));
		try {
			if (soldao.getSolicitado().size() == 0 && soldao.getAnalise(getUser()).size() == 0) {
				FacesUtil.addMsggError("Não há solicitações a serem validadas");
				return "/pages/autenticado/validador/validadorIndex.xhtml?faces-redirect=false";
			}
		} catch (EntityNotFoundException e) {
			e.printStackTrace();
		}
		return "/pages/autenticado/validador/validadorSolicitacao.xhtml?faces-redirect=true";
	}

	/**
	 * valida acesso
	 * 
	 * @return redirecionamento de acordo com a resposta
	 */

	public String validadorAcessos() {
		HttpSession session = (HttpSession) FacesContext.getCurrentInstance().getExternalContext().getSession(false);
		setUser((Usuario) session.getAttribute("usuario"));
		try {
			if (extdao.getSolicitado().size() == 0 && extdao.getAnalise(getUser()).size() == 0) {
				FacesUtil.addMsggError("Não há solicitações a serem validadas");
				return "/pages/autenticado/validador/validadorIndex.xhtml?faces-redirect=false";
			}
		} catch (EntityNotFoundException e) {
			e.printStackTrace();
		}
		return "/pages/autenticado/validador/validadorAcessos.xhtml?faces-redirect=true";
	}

	// redirecionamentos de página
	public String index() {
		HttpSession session = (HttpSession) FacesContext.getCurrentInstance().getExternalContext().getSession(false);
		Usuario user = (Usuario) session.getAttribute("usuario");
		if (user == null) {
			return "/pages/index.xhtml?faces-redirect=true";
		} else {
			switch (user.getPerfil()) {
			case (0):
				session.setAttribute("instituicao", instdao.getByRepNome(user.getNome()));
				return "/pages/notyet.xhtml?faces-redirect=true";
			case (2):
				session.setAttribute("usuario", getUser());
				return "/pages/autenticado/validador/validadorIndex.xhtml?faces-redirect=true";
			case (1):
				return "/pages/autenticado/admin/adminIndex.xhtml?faces-redirect=true";
			default:
				return "/pages/index.xhtml?faces-redirect=true";
			}
		}
	}

	public String atualizarCadastro() {
		return "/pages/instituicaoHome.xhtml?faces-redirect=true";
	}

	public String instituicaoHome() {
		return "/pages/autenticado/instituicao/instituicaoIndex.xhtml?faces-redirect=true";
	}

	public String estudanteHome() {
		return "/pages/estudanteHome.xhtml?faces-redirect=true";
	}

	public String estudanteAgendamento() {
		return "/pages/estudanteAgendamento.xhtml?faces-redirect=true";
	}

	public String estudanteAcessosHome() {
		return "/pages/estudanteAcessosHome.xhtml?faces-redirect=true";
	}

	public String resetSenha() {
		return "/pages/resetSenha.xhtml?faces-redirect=true";
	}

	public String adminUsers() {
		return "/pages/autenticado/admin/adminUsers.xhtml?faces-redirect=true";
	}

	public String validadorIndex() {
		return "/pages/autenticado/validador/validadorIndex.xhtml?faces-redirect=true";
	}

	public String subirDeclaracao() {
		return "/pages/autenticado/instituicao/subirDeclaracao.xhtml?faces-redirect=true";
	}
	
	public String subirFrequencia(){
		return "/pages/autenticado/instituicao/subirFrequencia.xhtml?faces-redirect=true";
	}
	
	public String listSolicitacoes2Via() {
		return "/pages/autenticado/validador/listSolicitacoes.xhtml?faces-redirect=true";
	}

	public String listSolicitacoesAcessos() {
		return "/pages/autenticado/validador/listSolicitacoesAcessos.xhtml?faces-redirect=true";
	}

	public String login() {
		return "/pages/login.xhtml?faces-redirect=true";
	}
	
	public String estudanteNovo() {
		return "/pages/estudanteNovo.xhtml?faces-redirect=true";
	}
	
	public String concatenaArquivos() {
		return "/pages/estudante/concatenaArquivos.xhtml?faces-redirect=true";
	}

	///////////////////////////////////////////////////////////////////////////

	// getteres and setteres

	public Usuario getUser() {
		return user;
	}

	public void setUser(Usuario user) {
		this.user = user;
	}

	public String getNovaSenha() {
		return novaSenha;
	}

	public void setNovaSenha(String novaSenha) {
		this.novaSenha = novaSenha;
	}

	public String getSenhaAntiga() {
		return senhaAntiga;
	}

	public void setSenhaAntiga(String senhaAntiga) {
		this.senhaAntiga = senhaAntiga;
	}

	public String getNovaSenha2() {
		return novaSenha2;
	}

	public void setNovaSenha2(String novaSenha2) {
		this.novaSenha2 = novaSenha2;
	}

	public boolean isCaptcha() {
		return captcha;
	}

	public void setCaptcha(boolean captcha) {
		this.captcha = captcha;
	}
}
