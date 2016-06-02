package br.gov.df.dftrans.scie.beans;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import javax.el.ELResolver;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.component.UIOutput;
import javax.faces.context.FacesContext;
import javax.faces.event.AjaxBehaviorEvent;
import javax.faces.model.ListDataModel;
import javax.servlet.http.HttpSession;
import br.gov.df.dftrans.scie.dao.ExtensaoAcessoDAO;
import br.gov.df.dftrans.scie.domain.Comentario;
import br.gov.df.dftrans.scie.domain.ExtensaoAcesso;
import br.gov.df.dftrans.scie.domain.Solicitacao;
import br.gov.df.dftrans.scie.domain.Usuario;
import br.gov.df.dftrans.scie.exceptions.EntityNotFoundException;
import br.gov.df.dftrans.scie.utils.AutenticacaoDocumentos;
import br.gov.df.dftrans.scie.utils.FacesUtil;
import br.gov.df.dftrans.scie.utils.Mail;
import br.gov.df.dftrans.scie.utils.ManipuladorArquivos;
import br.gov.df.dftrans.scie.utils.Parametros;
import br.gov.df.dftrans.scie.view.ArquivoMB;

@ManagedBean(name = "acessoMB")
@SessionScoped
/**
 * Classe responsável pelo controle do acesso do usuário
 * 
 * @author 9317295
 *
 */
public class AcessosBean {

	private Usuario user;
	private boolean[] arquivoValido = new boolean[3], existeArquivo = new boolean[2];
	private String[] comentario = new String[3], nomes = new String[3], nomesArquivos = new String[2];
	private ExtensaoAcesso extAcesso;
	private String motivo;
	private String[] path;
	private String current = Parametros.getParameter("root_upload"), pathAtual;
	private int documento, origem;
	private ExtensaoAcessoDAO extdao = ExtensaoAcessoDAO.extensaoAcessoDAO();
	private ListDataModel<ExtensaoAcesso> extAcessos;
	private boolean isEscolhida = false;
	private String delimitadorDiretorio = Parametros.getParameter("delimitador_diretorios");
	private String delimitadorDiretorioREGEX;
	private String cpf = "00000000000";

	/**
	 * Inicia a variável extAcesso com um ListDataModel de todas as extensões
	 */
	public void start() {
		setExtAcessos(new ListDataModel<ExtensaoAcesso>(extdao.get()));
	}

	/**
	 * Método resposável pela validação da solicitação seta a variável extAcesso
	 * com o objeto que vem da tela setEscolhida(true) = solicitação foi
	 * escolhida
	 * 
	 * @return a nova URL no qual a aplicação será direcionada
	 */
	public String validarSolicitacao() {
		setExtAcesso((ExtensaoAcesso) FacesContext.getCurrentInstance().getExternalContext().getRequestMap()
				.get("solicitacao"));
		setEscolhida(true);
		return "/pages/autenticado/validador/validadorAcessos.xhtml?faces-redirect=true";

	}
	
	/**
	 * Método resposável por desalocar uma solicitação
	 * 
	 * @return a nova URL no qual a aplicação será direcionada
	 */

	public String desalocarSolicitacao() {
		setExtAcesso((ExtensaoAcesso) FacesContext.getCurrentInstance().getExternalContext().getRequestMap()
				.get("solicitacao"));
		extdao.update(getExtAcesso(), 0);
		return "/pages/autenticado/validador/validadorIndex.xhtml?faces-redirect=true";

	}

	/**
	 * Método responsável por tratar caracteres reservados em expresões
	 * regulares
	 */
	public void setDelimitadorDiretorioREGEX() {
		if (".\\dDwW*+?sS^$|".contains(delimitadorDiretorio)) {
			delimitadorDiretorioREGEX = "\\" + delimitadorDiretorio;
		} else {
			delimitadorDiretorioREGEX = delimitadorDiretorio;
		}
	}

	/**
	 * 
	 */
	public void init() {
		setDelimitadorDiretorioREGEX();
		path = null;
		documento = -1;
		// caso o cpf seja diferente do cpf da classe ExtensaoAcesso
		// Inicia o vetor de String e inicia o vetor de boolean como false
		try {
			// seta a variavel user com o usuário na sessão
			HttpSession session = (HttpSession) FacesContext.getCurrentInstance().getExternalContext()
					.getSession(false);
			setUser((Usuario) session.getAttribute("usuario"));
			// caso não tenha sido escolhida uma solicitação
			if (!isEscolhida) {
				// se tiver solicitação em análise com o usuário na sessão
				if (extdao.getAnalise(getUser()) != null && !extdao.getAnalise(getUser()).isEmpty()) {
					// seta a variavel extAcesso com o primeiro encontrado
					setExtAcesso(extdao.getAnalise(getUser()).get(0));
				} else {
					setExtAcesso(extdao.getSolicitado().get(0));
				}
			}
			// caso o cpf seja diferente do cpf da classe ExtensaoAcesso
			// Inicia o vetor de String e inicia o vetor de boolean como false
			if (!cpf.equals(getExtAcesso().getCpf())) {
				for (int i = 0; i < 3; i++) {
					comentario[i] = "";
					arquivoValido[i] = false;
				}
			}
			// seta o cpf com o cpf da classe extensaoAcesso
			cpf = getExtAcesso().getCpf();
			// seta o tipo do documento
			setDocumento(2);
			// seta a origem da página de navegação de arquivo
			setOrigem(4);
			// seta path com array de string que representam as linhas do
			// arquivo presente no destino informado
			if (temArquivos()) {
				setPath(ManipuladorArquivos.leitor(current + "" + delimitadorDiretorio + "destino_uploader"
						+ delimitadorDiretorio + "" + getExtAcesso().getCpf() + "" + delimitadorDiretorio + "files"));

				// seta pathAtual com a primeira linha do arquivo
				setPathAtual(getPath()[getDocumento()]);

				prepararArquivo();
			}
			setarMotivo();
			getExtAcesso().setUsuario(getUser());
			// Altera a o tipo de acesso para em analise
			setExtAcesso(extdao.update(getExtAcesso(), 1));
			getExtAcesso().setComentario(new Comentario());
			if (!cpf.equals(getExtAcesso().getCpf())) {
				for (int i = 0; i < 3; i++) {
					comentario[i] = "";
					arquivoValido[i] = false;
				}
			}
			cpf = getExtAcesso().getCpf();
		} catch (EntityNotFoundException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Prepara o arquivo e seta o documento
	 * 
	 * @param event
	 */
	public void setarDocumento(AjaxBehaviorEvent event) {
		int resultado = (Integer) ((UIOutput) event.getSource()).getValue();
		setDocumento(resultado);
		prepararArquivo();
	}

	/**
	 * Seta o path e a origem do arquivo, envia para o bean arquivoMB o caminho
	 * do arquivo
	 */
	public void prepararArquivo() {
		setPathAtual(getPath()[getDocumento()]);
		FacesContext context = FacesContext.getCurrentInstance();
		ELResolver resolver = context.getApplication().getELResolver();
		ArquivoMB bean = (ArquivoMB) resolver.getValue(context.getELContext(), null, "arquivoMB");
		bean.setPath(getPathAtual());
		bean.setOrigem(getOrigem());
	}

	public String getLabel() {
		if (temArquivos()) {
			return nomesArquivos[getDocumento() - 2];
		} else {
			return "Sem documento necessário";
		}
	}

	/**
	 * Converte a chave(cpf+data+tipoDocumento) para MD5
	 * 
	 * @return a chave formatada em MD5
	 */
	public String getAutenticacao() {
		try{
		String chave = "";
		chave = getExtAcesso().getCpf();
		if (temArquivos()) {
			String[] aux = getPathAtual().split("destino_uploader"), aux1;
			if(delimitadorDiretorioREGEX == null){
				setDelimitadorDiretorioREGEX();
			}
			aux1 = aux[1].split(delimitadorDiretorioREGEX);
			chave += aux1[2];
		} else {
			Date d = new Date();
			DateFormat fmt = new SimpleDateFormat("dd-MM-yyyy");
			chave += fmt.format(d);
		}
		chave += getDocumento();
		return AutenticacaoDocumentos.getChaveSeguranca(chave);
		} catch (Exception e){
			return "Não foi possivel calcular a autenticacao";
		}
	}

	public String terminarValidacao() {
		boolean validado = true;
		// Caso tenha um arquivo valido e os outros não, é necessário setar o
		// válido como inválido
		if (getArquivoValido()[2] && (!getArquivoValido()[0] || !getArquivoValido()[1])) {
			FacesUtil.addMsggError("Marque a validação da solicitação como \"Inválida\"!");
			return "/pages/autenticado/validadorAcessos.xhtml?faces-redirect=false";
		}

		for (int i = 0; i < 3; i++) {
			// caso tenha um arquivo inválido, validado recebe falso
			if (!getArquivoValido()[i]) {
				validado = false;
			}
			setDocumento(i + 2);
			if (i < 2) {
				getNomes()[i] = getLabel();
			}
		}
		// seta os comentários a certa da requisição em questão
		Comentario c = getExtAcesso().getComentario();
		c.setDOC1Valido(getArquivoValido()[0] ? 1 : 0);
		c.setDOC2Valido(getArquivoValido()[1] ? 1 : 0);
		c.setValidacao(getArquivoValido()[2] ? 1 : 0);
		c.setDescricaoDOC1(getComentario()[0]);
		c.setDescricaoDOC2(getComentario()[1]);
		c.setDescricaoValidacao(getComentario()[2]);
		if (validado) {
			// seta status para validadado
			setExtAcesso(extdao.update(getExtAcesso(), 2));
		} else {
			// seta status para rejeitado
			setExtAcesso(extdao.update(getExtAcesso(), 3));
		}
		// Envia um email informando a situação do cadastro
		// ple.dftrans@gmail.com
		Mail.sendEmailAcessosValidacao(getExtAcesso(), getArquivoValido(), getComentario(), getNomes());
		return "/pages/autenticado/validador/validadorIndex.xhtml?faces-redirect=true";
	}

	public boolean temArquivos() {
		if (getExtAcesso() != null && getExtAcesso().getMotivo() != 3) {
			return true;
		} else {
			return false;
		}
	}

	/**
	 * Seta o motivo de ter sido rejeitado o cadastro Habilita ou desabilita box
	 * de subir arquivo
	 */
	public void setarMotivo() {
		switch (getExtAcesso().getMotivo()) {
		// necessário subir pelo menos um arquivo
		case (1):
			setMotivo("Matrícula em mais de uma IE");
			nomesArquivos[0] = Parametros.getParameter("doc_decl_ie");
			existeArquivo[0] = false;
			nomesArquivos[1] = "Arquivo não requerido";
			existeArquivo[1] = true;
			break;
		// necessário subir os dois arquivos
		case (2):
			setMotivo("Estágio Obrigatório");
			nomesArquivos[0] = Parametros.getParameter("doc_decl_estagio");
			existeArquivo[0] = false;
			nomesArquivos[1] = Parametros.getParameter("doc_grade");
			existeArquivo[1] = false;
			break;
		// não é necessário subir arquivo
		case (3):
			setMotivo("Insuficiência de Acessos");
			nomesArquivos[0] = "Arquivo não requerido";
			existeArquivo[0] = true;
			nomesArquivos[1] = "Arquivo não requerido";
			existeArquivo[1] = true;
			break;
		// necessário subir os dois arquivos
		case (4):
			setMotivo("Alteração de endereço/IE");
			nomesArquivos[0] = Parametros.getParameter("doc_end");
			existeArquivo[0] = false;
			nomesArquivos[1] = Parametros.getParameter("doc_end_ie");
			existeArquivo[1] = false;
			break;
		default:
			setMotivo("Imotivado (Solicitação Irregular!)");
			nomesArquivos[0] = "Solicitação Irregular";
			existeArquivo[0] = true;
			nomesArquivos[1] = "Solicitação Irregular";
			existeArquivo[1] = true;
			break;
		}
	}

	/**
	 * Filtro para valores descritivos e numéricos dos status;
	 * 
	 * @param value
	 * @param filter
	 * @param locale
	 * @return valores filtrados
	 */
	public boolean filterStatus(Object value, Object filter, Locale locale) {
		String filterText = (filter == null) ? null : filter.toString().trim();
		// Retorna filtro vazio
		if (filterText == null || filterText.equals("")) {
			return true;
		}
		int i;
		// Converte filtro descritivo em numérico
		if ("Duplicidade".length() >= filterText.length()
				? "Duplicidade".substring(0, filterText.length()).equalsIgnoreCase(filterText)
				: filterText.substring(0, "Duplicidade".length()).equalsIgnoreCase("Duplicidade")) {
			filterText = "-1";
		}
		if ("Solicitado".length() >= filterText.length()
				? "Solicitado".substring(0, filterText.length()).equalsIgnoreCase(filterText)
				: filterText.substring(0, "Solicitado".length()).equalsIgnoreCase("Solicitado")) {
			filterText = "0";
		}
		if ("Em analise".length() >= filterText.length()
				? "Em analise".substring(0, filterText.length()).equalsIgnoreCase(filterText)
				: filterText.substring(0, "Em analise".length()).equalsIgnoreCase("Em analise")) {
			filterText = "1";
		}
		if ("Aprovado".length() >= filterText.length()
				? "Aprovado".substring(0, filterText.length()).equalsIgnoreCase(filterText)
				: filterText.substring(0, "Aprovado".length()).equalsIgnoreCase("Aprovado")) {
			filterText = "2";
		}
		if ("Rejeitado".length() >= filterText.length()
				? "Rejeitado".substring(0, filterText.length()).equalsIgnoreCase(filterText)
				: filterText.substring(0, "Rejeitado".length()).equalsIgnoreCase("Rejeitado")) {
			filterText = "3";
		}
		if ("Cartao Impresso".length() >= filterText.length()
				? "Cartao Impresso".substring(0, filterText.length()).equalsIgnoreCase(filterText)
				: filterText.substring(0, "Cartao Impresso".length()).equalsIgnoreCase("Cartao Impresso")) {
			filterText = "4";
		}
		if ("Cartao Entregue".length() >= filterText.length()
				? "Cartao Entregue".substring(0, filterText.length()).equalsIgnoreCase(filterText)
				: filterText.substring(0, "Cartao Entregue".length()).equalsIgnoreCase("Cartao Entregue")) {
			filterText = "5";
		}
		if (value == null) {
			return false;
		}
		// Trata filtro não numérico ou fora do escopo
		try {
			i = Integer.parseInt(filterText);
		} catch (NumberFormatException e) {
			i = 100;
		}
		return (Integer) value == i;
	}
	
	/**
	 * Filtro para valores descritivos e numéricos dos motivos;
	 * 
	 * @param value
	 * @param filter
	 * @param locale
	 * @return valores filtrados
	 */
	public boolean filterMotivo(Object value, Object filter, Locale locale) {
		String filterText = (filter == null) ? null : filter.toString().trim();
		// Retorna filtro vazio
		if (filterText == null || filterText.equals("")) {
			return true;
		}
		int i;
		// Converte filtro descritivo em numérico
		if ("Alteracao de endereço/IE".length() >= filterText.length()
				? "Alteracao de endereço/IE".substring(0, filterText.length()).equalsIgnoreCase(filterText)
				: filterText.substring(0, "Alteracao de endereço/IE".length()).equalsIgnoreCase("Alteracao de endereço/IE")) {
			filterText = "4";
		}
		if ("Insuficiencia de Acessos".length() >= filterText.length()
				? "Insuficiencia de Acessos".substring(0, filterText.length()).equalsIgnoreCase(filterText)
				: filterText.substring(0, "Insuficiencia de Acessos".length()).equalsIgnoreCase("Insuficiencia de Acessos")) {
			filterText = "3";
		}
		if ("Estagio Obrigatorio".length() >= filterText.length()
				? "Estagio Obrigatorio".substring(0, filterText.length()).equalsIgnoreCase(filterText)
				: filterText.substring(0, "Estagio Obrigatorio".length()).equalsIgnoreCase("Estagio Obrigatorio")) {
			filterText = "2";
		}
		if ("Matricula em mais de uma IE".length() >= filterText.length()
				? "Matricula em mais de uma IE".substring(0, filterText.length()).equalsIgnoreCase(filterText)
				: filterText.substring(0, "Matricula em mais de uma IE".length()).equalsIgnoreCase("Matricula em mais de uma IE")) {
			filterText = "1";
		}
		if (value == null) {
			return false;
		}
		// Trata filtro não numérico ou fora do escopo
		try {
			i = Integer.parseInt(filterText);
		} catch (NumberFormatException e) {
			i = 100;
		}
		return (Integer) value == i;
	}

	// getteres and setteres
	public Usuario getUser() {
		return user;
	}

	public void setUser(Usuario user) {
		this.user = user;
	}

	public boolean[] getArquivoValido() {
		return arquivoValido;
	}

	public void setArquivoValido(boolean[] arquivoValido) {
		this.arquivoValido = arquivoValido;
	}

	public String[] getComentario() {
		return comentario;
	}

	public void setComentario(String[] comentario) {
		this.comentario = comentario;
	}

	public ExtensaoAcesso getExtAcesso() {
		return extAcesso;
	}

	public void setExtAcesso(ExtensaoAcesso extAcesso) {
		this.extAcesso = extAcesso;
	}

	public String getMotivo() {
		return motivo;
	}

	public void setMotivo(String motivo) {
		this.motivo = motivo;
	}

	public int getDocumento() {
		return documento;
	}

	public void setDocumento(int documento) {
		this.documento = documento;
	}

	public String[] getPath() {
		return path;
	}

	public void setPath(String[] path) {
		this.path = path;
	}

	public String getPathAtual() {
		return pathAtual;
	}

	public void setPathAtual(String pathAtual) {
		this.pathAtual = pathAtual;
	}

	public int getOrigem() {
		return origem;
	}

	public void setOrigem(int origem) {
		this.origem = origem;
	}

	public String[] getNomes() {
		return nomes;
	}

	public void setNomes(String[] nomes) {
		this.nomes = nomes;
	}

	public ListDataModel<ExtensaoAcesso> getExtAcessos() {
		return extAcessos;
	}

	public void setExtAcessos(ListDataModel<ExtensaoAcesso> extAcessos) {
		this.extAcessos = extAcessos;
	}

	public boolean isEscolhida() {
		return isEscolhida;
	}

	public void setEscolhida(boolean isEscolhida) {
		this.isEscolhida = isEscolhida;
	}

	public String[] getNomesArquivos() {
		return nomesArquivos;
	}

	public void setNomesArquivos(String[] nomesArquivos) {
		this.nomesArquivos = nomesArquivos;
	}

	public boolean[] getExisteArquivo() {
		return existeArquivo;
	}

	public void setExisteArquivo(boolean[] existeArquivo) {
		this.existeArquivo = existeArquivo;
	}
}
