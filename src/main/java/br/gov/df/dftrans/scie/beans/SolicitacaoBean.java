package br.gov.df.dftrans.scie.beans;

import java.io.File;
import java.util.Locale;

import javax.el.ELResolver;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.component.UIOutput;
import javax.faces.context.FacesContext;
import javax.faces.event.AjaxBehaviorEvent;
import javax.faces.model.ListDataModel;
import javax.servlet.http.HttpSession;

import br.gov.df.dftrans.scie.dao.SolicitacaoDAO;
import br.gov.df.dftrans.scie.domain.Comentario;
import br.gov.df.dftrans.scie.domain.Solicitacao;
import br.gov.df.dftrans.scie.domain.Usuario;
import br.gov.df.dftrans.scie.exceptions.EntityNotFoundException;
import br.gov.df.dftrans.scie.utils.AutenticacaoDocumentos;
import br.gov.df.dftrans.scie.utils.FacesUtil;
import br.gov.df.dftrans.scie.utils.Mail;
import br.gov.df.dftrans.scie.utils.ManipuladorArquivos;
import br.gov.df.dftrans.scie.utils.Parametros;
import br.gov.df.dftrans.scie.view.ArquivoMB;
import br.gov.df.dftrans.scie.view.FileUploadEstudanteView;

@ManagedBean(name = "solicitacaoMB")
@SessionScoped
public class SolicitacaoBean {

	private Usuario user;
	private boolean[] arquivoValido = new boolean[3];
	private String[] comentario = new String[3];
	private String[] nomes = new String[3];
	private Solicitacao solicitacao;
	private String motivo;
	private String[] path;
	private String current = Parametros.getParameter("root_upload");
	private String pathAtual;
	private int documento;
	private int origem;
	private SolicitacaoDAO soldao = SolicitacaoDAO.SolicitacaoDAO();
	private ListDataModel<Solicitacao> solicitacoes;
	private boolean isEscolhida = false;
	private String delimitadorDiretorio = Parametros.getParameter("delimitador_diretorios");
	private String delimitadorDiretorioREGEX;
	private String cpf = "00000000000";
	private String[] files;

	public String[] getFiles() {
		return files;
	}

	public void setFiles(String[] files) {
		this.files = files;
	}

	// inicia solicitac�es
	public void start() {
		setSolicitacoes(new ListDataModel<Solicitacao>(soldao.get()));
	}

	/**
	 * M�todo respos�vel pela valida��o da solicita��o setEscolhida(true) =
	 * solicita��o foi escolhida
	 * 
	 * @return a nova URL no qual a aplica��o ser� direcionada
	 */

	public String validarSolicitacao() {
		setSolicitacao((Solicitacao) FacesContext
				.getCurrentInstance().getExternalContext().getRequestMap()
				.get("solicitacao"));
		setEscolhida(true);
		return "/pages/autenticado/validador/"
				+ "validadorSolicitacao.xhtml?faces-redirect=true";

	}
	
	/**
	 * M�todo respos�vel por desalocar uma solicita��o
	 * 
	 * @return a nova URL no qual a aplica��o ser� direcionada
	 */

	public String desalocarSolicitacao() {
		setSolicitacao((Solicitacao) FacesContext.
				getCurrentInstance().getExternalContext().getRequestMap()
				.get("solicitacao"));
		soldao.update(getSolicitacao(), 0);
		return "/pages/autenticado/validador/validadorIndex.xhtml?faces-redirect=true";

	}

	/**
	 * M�todo respons�vel por tratar caracteres reservados em expres�es
	 * regulares
	 */
	public void setDelimitadorDiretorioREGEX() {
		if (".\\dDwW*+?sS^$|".contains(delimitadorDiretorio)) {
			delimitadorDiretorioREGEX = "\\" + delimitadorDiretorio;
		} else {
			delimitadorDiretorioREGEX = delimitadorDiretorio;
		}
	}

	public void init() {
		setDelimitadorDiretorioREGEX();
		path = null;
		documento = -1;
		try {
			// seta a variavel user com o usu�rio na sess�o
			HttpSession session = (HttpSession) FacesContext.
					getCurrentInstance().getExternalContext()
					.getSession(false);
			setUser((Usuario) session.getAttribute("usuario"));
			// caso n�o tenha sido escolhida uma solicita��o
			if (!isEscolhida) {
				// se tiver solicita��o em an�lise com o usu�rio na sess�o
				if (soldao.getAnalise(getUser()) != null && 
						!soldao.getAnalise(getUser()).isEmpty()) {
					setSolicitacao(soldao.getAnalise(getUser()).get(0));
				} else {
					setSolicitacao(soldao.getSolicitado().get(0));
				}
			}
			// seta path com array de string que representam as linhas do
			// arquivo presente no destino informado
			setPath(ManipuladorArquivos.leitor(current + "" 
                            + delimitadorDiretorio + "destino_uploader"
					+ delimitadorDiretorio + "" 
			               + getSolicitacao().getCpf() + "" + delimitadorDiretorio + "files"));
			setFiles(new String[2]);
			int flag = 0;
			for(String tmp : getPath()){
				if(flag < 2){
					files[flag++] = tmp;
				}
			}
			// seta pathAtual com a primeira linha do arquivo
			setPathAtual(getPath()[0]);
			// seta o tipo do documento
			setDocumento(0);
			setOrigem(3);
			prepararArquivo();
			// Seta o motivo de acordo com o motivo
			setarMotivo();
			// seta o usu�rio respons�vel pela solicita��o
			getSolicitacao().setUsuario(getUser());
			// atualiza a nova solicita��o no banco
			setSolicitacao(soldao.update(getSolicitacao(), 1));
			if (getSolicitacao().getComentario() == null) {
				getSolicitacao().setComentario(new Comentario());
			}
			// caso o cpf seja diferente do cpf da solicita��o
			// Inicia o vetor de String e inicia o vetor de boolean como false
			if (!cpf.equals(getSolicitacao().getCpf())) {
				for (int i = 0; i < 3; i++) {
					if(i < comentario.length){
						comentario[i] = "";
					}
					if(i < arquivoValido.length){
						arquivoValido[i] = false;
					}
				}
			}
			// seta o cpf com o cpf da solicita��o
			cpf = getSolicitacao().getCpf();
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
		ArquivoMB bean = (ArquivoMB) resolver.getValue(
				context.getELContext(), null, "arquivoMB");
		bean.setPath(getPathAtual());
		bean.setOrigem(getOrigem());
	}

	/**
	 * Setar o arquivo de upload
	 * 
	 * @return o bean do arquivo
	 */
	public String getLabel() {
		FacesContext context = FacesContext.getCurrentInstance();
		ELResolver resolver = context.getApplication().getELResolver();
		FileUploadEstudanteView filebean = (FileUploadEstudanteView) resolver.
				getValue(context.getELContext(), null,
				"fileUploadEstudanteView");
		return filebean.getDescricaoArquivos()[getDocumento()];
	}

	/**
	 * Converte a chave(cpf+data+tipoDocumento) para MD5
	 * 
	 * @return a chave formatada em MD5
	 */

	public String getAutenticacao() {
		try{
		String chave = "";
		chave = getSolicitacao().getCpf();
		String[] aux = getPathAtual().split("destino_uploader"), aux1;
		if(delimitadorDiretorioREGEX == null){
			setDelimitadorDiretorioREGEX();
		}
		aux1 = aux[1].split(delimitadorDiretorioREGEX);
		chave += aux1[2];
		chave += getDocumento();
		return AutenticacaoDocumentos.getChaveSeguranca(chave);
		} catch (Exception e){
			return "N�o foi possivel calcular a autenticacao";
		}
	}

	public String terminarValidacao() {
		boolean validado = true;
		// Caso tenha um arquivo valido e os outros n�o, � necess�rio setar o
		// v�lido como inv�lido
		if (getArquivoValido()[2] && (!getArquivoValido()[0] || !getArquivoValido()[1])) {
			FacesUtil.addMsggError("Marque a valida��o da "
					+ "solicita��o como \"Inv�lida\"!");
			return "/pages/autenticado/validadorSolicitacao.xhtml?faces-redirect=false";
		}
		for (int i = 0; i < 3; i++) {
			// caso tenha um arquivo inv�lido, validado recebe falso
			if (!getArquivoValido()[i]) {
				validado = false;
			}
			setDocumento(i);
			if (i < 2) {
				getNomes()[i] = getLabel();
			}
		}
		// seta os coment�rios a certa da requisi��o em quest�o
		Comentario c = getSolicitacao().getComentario();
		c.setBoValido(getArquivoValido()[0] ? 1 : 0);
		c.setTaxaValida(getArquivoValido()[1] ? 1 : 0);
		c.setValidacao(getArquivoValido()[2] ? 1 : 0);
		c.setDescricaoBO(getComentario()[0]);
		c.setDescricaoTAXA(getComentario()[1]);
		c.setDescricaoValidacao(getComentario()[2]);
		if (validado) {
			// seta status para validadado
			setSolicitacao(soldao.update(getSolicitacao(), 2));
		} else {
			// seta status para rejeitado
			setSolicitacao(soldao.update(getSolicitacao(), 3));
		}
		// Envia um email informando a situa��o do cadastro
		// ple.dftrans@gmail.com
		Mail.sendEmail2ViaValidacao(getSolicitacao(), 
				getArquivoValido(), getComentario(), getNomes(),getFiles());
		return "/pages/autenticado/validador/validadorIndex.xhtml?faces-redirect=true";
	}

	/**
	 * Seta o motivo de ter sido rejeitado o cadastro
	 */
	public void setarMotivo() {
		switch (getSolicitacao().getMotivo()) {
		case (1):
			setMotivo("Perda");
			break;
		case (2):
			setMotivo("Roubo ou Furto");
			break;
		case (3):
			setMotivo("Cart�o Danificado ou Inutilizado");
			break;
		default:
			setMotivo("Imotivado (Solicita��o Irregular!)");
			break;
		}
	}
	
	/**
	 * Filtro para valores descritivos e num�ricos dos status;
	 * 
	 * @param value
	 * @param filter
	 * @param locale
	 * @return valores filtrados
	 */
	public boolean filterStatus(Object value, Object filter, Locale locale) {
		String filterText = (filter == null) ? null : filter.toString().trim();
		// Retorna filtro vazio
		if (filterText == null || "".equals(filterText)) {
			return true;
		}
		int i;
		// Converte filtro descritivo em num�rico
		if ("Duplicidade".length() >= filterText.length()
				? "Duplicidade".substring(0, filterText.length())
						.equalsIgnoreCase(filterText)
				: filterText.substring(0, "Duplicidade".length())
				.equalsIgnoreCase("Duplicidade")) {
			filterText = "-1";
		}
		if ("Solicitado".length() >= filterText.length()
				? "Solicitado".substring(0, filterText.length())
						.equalsIgnoreCase(filterText)
				: filterText.substring(0, "Solicitado".length())
				.equalsIgnoreCase("Solicitado")) {
			filterText = "0";
		}
		if ("Em analise".length() >= filterText.length()
				? "Em analise".substring(0, filterText.length())
						.equalsIgnoreCase(filterText)
				: filterText.substring(0, "Em analise".length())
				.equalsIgnoreCase("Em analise")) {
			filterText = "1";
		}
		if ("Aprovado".length() >= filterText.length()
				? "Aprovado".substring(0, filterText.length())
						.equalsIgnoreCase(filterText)
				: filterText.substring(0, "Aprovado".length())
				.equalsIgnoreCase("Aprovado")) {
			filterText = "2";
		}
		if ("Rejeitado".length() >= filterText.length()
				? "Rejeitado".substring(0, filterText.length())
						.equalsIgnoreCase(filterText)
				: filterText.substring(0, "Rejeitado".length())
				.equalsIgnoreCase("Rejeitado")) {
			filterText = "3";
		}
		if ("Cartao Impresso".length() >= filterText.length()
				? "Cartao Impresso".substring(0, filterText.length())
						.equalsIgnoreCase(filterText)
				: filterText.substring(0, "Cartao Impresso".length())
				.equalsIgnoreCase("Cartao Impresso")) {
			filterText = "4";
		}
		if ("Cartao Entregue".length() >= filterText.length()
				? "Cartao Entregue".substring(0, filterText.length())
						.equalsIgnoreCase(filterText)
				: filterText.substring(0, "Cartao Entregue".length())
				.equalsIgnoreCase("Cartao Entregue")) {
			filterText = "5";
		}
		if (value == null) {
			return false;
		}
		// Trata filtro n�o num�rico ou fora do escopo
		try {
			i = Integer.parseInt(filterText);
		} catch (NumberFormatException e) {
			i = 100;
		}
		return (Integer) value == i;
	}
	
	/**
	 * Filtro para valores descritivos e num�ricos dos motivos;
	 * 
	 * @param value
	 * @param filter
	 * @param locale
	 * @return valores filtrados
	 */
	public boolean filterMotivo(Object value, Object filter, Locale locale) {
		String filterText = (filter == null) ? null : filter.toString().trim();
		// Retorna filtro vazio
		if (filterText == null || "".equals(filterText)) {
			return true;
		}
		int i;
		// Converte filtro descritivo em num�rico
		if ("Cartao danificado ou inutilizado".length() >= filterText.length()
				? "Cartao danificado ou inutilizado".
						substring(0, filterText.length())
						.equalsIgnoreCase(filterText)
				: filterText.substring(0, 
						"Cartao danificado ou inutilizado".length())
				.equalsIgnoreCase("Cartao danificado ou inutilizado")) {
			filterText = "3";
		}
		if ("Roubo ou Furto".length() >= filterText.length()
				? "Roubo ou Furto".substring(0, filterText.length())
						.equalsIgnoreCase(filterText)
				: filterText.substring(0, "Roubo ou Furto".length())
				.equalsIgnoreCase("Roubo ou Furto")) {
			filterText = "2";
		}
		if ("Perda".length() >= filterText.length()
				? "Perda".substring(0, filterText.length())
						.equalsIgnoreCase(filterText)
				: filterText.substring(0, "Perda".length())
				.equalsIgnoreCase("Perda")) {
			filterText = "1";
		}
		if (value == null) {
			return false;
		}
		// Trata filtro n�o num�rico ou fora do escopo
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

	public Solicitacao getSolicitacao() {
		return solicitacao;
	}

	public void setSolicitacao(Solicitacao solicitacao) {
		this.solicitacao = solicitacao;
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

	public ListDataModel<Solicitacao> getSolicitacoes() {
		return solicitacoes;
	}

	public void setSolicitacoes(ListDataModel<Solicitacao> solicitacoes) {
		this.solicitacoes = solicitacoes;
	}

	public boolean isEscolhida() {
		return isEscolhida;
	}

	public void setEscolhida(boolean isEscolhida) {
		this.isEscolhida = isEscolhida;
	}
}
