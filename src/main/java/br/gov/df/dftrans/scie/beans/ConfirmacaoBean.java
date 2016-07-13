package br.gov.df.dftrans.scie.beans;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.el.ELResolver;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.component.UIOutput;
import javax.faces.context.FacesContext;
import javax.faces.event.AjaxBehaviorEvent;
import javax.servlet.http.HttpSession;

import br.gov.df.dftrans.scie.dao.DocumentoPendenciaDAO;
import br.gov.df.dftrans.scie.dao.InstituicaoCursoDAO;
import br.gov.df.dftrans.scie.dao.InstituicaoEnsinoDAO;
import br.gov.df.dftrans.scie.dao.LogDAO;
import br.gov.df.dftrans.scie.dao.RepresentanteDAO;
import br.gov.df.dftrans.scie.domain.Comentario;
import br.gov.df.dftrans.scie.domain.InstituicaoCurso;
import br.gov.df.dftrans.scie.domain.InstituicaoEnsino;
import br.gov.df.dftrans.scie.domain.LogValidacaoCadastro;
import br.gov.df.dftrans.scie.domain.Representante;
import br.gov.df.dftrans.scie.domain.Usuario;
import br.gov.df.dftrans.scie.exceptions.EntityNotFoundException;
import br.gov.df.dftrans.scie.exceptions.InsertException;
import br.gov.df.dftrans.scie.utils.AutenticacaoDocumentos;
import br.gov.df.dftrans.scie.utils.FacesUtil;
import br.gov.df.dftrans.scie.utils.Mail;
import br.gov.df.dftrans.scie.utils.ManipuladorArquivos;
import br.gov.df.dftrans.scie.utils.Parametros;
import br.gov.df.dftrans.scie.utils.ValidadorCEP;
import br.gov.df.dftrans.scie.view.ArquivoMB;
import br.gov.df.dftrans.scie.view.FileUploadView;

@ManagedBean(name = "confirmacaoMB")
@SessionScoped
public class ConfirmacaoBean {
	private InstituicaoEnsino instituicao;
	private InstituicaoEnsinoDAO instensdao = InstituicaoEnsinoDAO.InstituicaoEnsinoDAO();
	private ArrayList<Boolean> exists = new ArrayList<Boolean>();
	private ArrayList<Boolean> existsLog = new ArrayList<Boolean>();
	private String[] path;
	private String current = Parametros.getParameter("root_upload");
	private String pathAtual;
	private int documento; 
	private int origem;
	private List<InstituicaoCurso> cadastrados;
	private InstituicaoCursoDAO instdao = InstituicaoCursoDAO.InstituicaoCursoDAO();
	private LogDAO logdao = LogDAO.LogDAO();
	private DocumentoPendenciaDAO docdao = DocumentoPendenciaDAO.DocumentoPendenciaDAO();
	private Usuario usuario;
	private ValidadorBean validadorBean;
	private RepresentanteDAO repdao = RepresentanteDAO.RepresentanteDAO();
	private Representante representante;

	public void init() {
		exists = new ArrayList<Boolean>();
		existsLog = new ArrayList<Boolean>();
		HttpSession session = (HttpSession) FacesContext.getCurrentInstance()
				.getExternalContext().getSession(false);
		setInstituicao((InstituicaoEnsino) session.getAttribute("instituicao"));
		setRepresentante((Representante) session.getAttribute("representante"));
		setPath(ManipuladorArquivos
				.leitor(current + "\\destino_uploader\\" 
	                    + getRepresentante().getCpf() + "\\files"));
		for (String temp : getPath()) {
			if ("0".equals(temp)) {
				exists.add(false);
			} else {
				exists.add(true);
			}
		}
		setPathAtual(getPath()[0]);
		setDocumento(0);
		setOrigem(1);
		prepararArquivo();
		try {
			setCadastrados(instdao.getCursos(getInstituicao()));
		} catch (EntityNotFoundException e) {
			e.printStackTrace();
		}
	}

	public void reset() {
		exists.clear();
		existsLog.clear();
		path = null;
		documento = -1;
		if (getCadastrados() != null) {
			cadastrados.clear();
		}
		try {
			HttpSession session = (HttpSession) FacesContext.getCurrentInstance()
					.getExternalContext().getSession(false);
			setUsuario((Usuario) session.getAttribute("usuario"));
				if (logdao.getAnalisysUser(getUsuario()) != null) {
					setRepresentante(logdao.getAnalisysUser(getUsuario())
							.getRepresentante());
				} else {
					setRepresentante(logdao.getOpens()
							.get(0).getRepresentante());
				}
				setInstituicao(getRepresentante().getInstituicao());
				setPath(ManipuladorArquivos
						.leitor(current + "\\destino_uploader\\" 
							+ getRepresentante().getCpf() + "\\files"));
				int cont = 0;
				for (String temp : getPath()) {
					if ("0".equals(temp)) {
						exists.add(false);
					} else {
						exists.add(true);
					}
					existsLog.add(logdao.existsLog(getRepresentante(), 
							docdao.getByNro(cont)));
					cont++;
				}
				setPathAtual(getPath()[0]);
				setDocumento(0);
				setOrigem(2);
				prepararArquivo();
				setCadastrados(instdao.getCursos(getInstituicao()));
				LogValidacaoCadastro log;
				for (int i = 0; i < existsLog.size(); i++) {
					if (existsLog.get(i)) {
						if (docdao.getByNro(i) != null){
							log = logdao.get(getRepresentante().getId(), 
									docdao.getByNro(i));
						}else{
							log = null;
						}
						if (log != null) {
							log.setUsuario(getUsuario());
							logdao.update(log, 1);
						}
					}
				}
		} catch (EntityNotFoundException e) {
			e.printStackTrace();
		}
	}

	public InstituicaoEnsino getInstituicao() {
		return instituicao;
	}

	public void setInstituicao(InstituicaoEnsino instituicao) {
		this.instituicao = instituicao;
	}

	public String getRepCargo() {
		if (getRepresentante() != null) {
			int i = getRepresentante().getCargo();
			return (i == 1) ? "Diretor" : ((i == 2) ? "Secretário" : null);
		}
		return null;
	}

	public ArrayList<Boolean> getExists() {
		return exists;
	}

	public boolean getExisteDocumento() {
		return exists.get(getDocumento());
	}

	public boolean getExisteImagem() {
		String[] aux = getPathAtual().split("\\.");
		if (exists.get(getDocumento()) && aux.length > 1 && 
				!aux[1].toLowerCase().equals("pdf")) {
			return true;
		}
		return false;
	}

	public void setExists(ArrayList<Boolean> exists) {
		this.exists = exists;
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

	public int getDocumento() {
		return documento;
	}

	public void setDocumento(int documento) {
		this.documento = documento;
	}

	public int getOrigem() {
		return origem;
	}

	public void setOrigem(int origem) {
		this.origem = origem;
	}

	public ArrayList<Boolean> getExistsLog() {
		return existsLog;
	}

	public void setExistsLog(ArrayList<Boolean> existsLog) {
		this.existsLog = existsLog;
	}

	public Usuario getUsuario() {
		return usuario;
	}

	public void setUsuario(Usuario usuario) {
		this.usuario = usuario;
	}

	public String getLabel() {
		FacesContext context = FacesContext.getCurrentInstance();
		ELResolver resolver = context.getApplication().getELResolver();
		FileUploadView filebean = (FileUploadView) resolver
				.getValue(context.getELContext(), null, "fileUploadView");
		return filebean.getDescricaoArquivos()[getDocumento()];
	}

	public String getAutenticacao() {
		String chave = "";
		chave = removeMascara(getInstituicao().getCnpj());
		chave += getInstituicao().getId();
		chave += getRepresentante().getNome();
		chave += getRepresentante().getCpf();
		String[] aux = getPathAtual().split("destino_uploader");
		String[] aux1;
		aux1 = aux[1].split("\\\\");
		chave += aux1[2];
		chave += getDocumento();
		return AutenticacaoDocumentos.getChaveSeguranca(chave);
	}

	public void prepararArquivo() {
		setPathAtual(getPath()[getDocumento()]);
		FacesContext context = FacesContext.getCurrentInstance();
		ELResolver resolver = context.getApplication().getELResolver();
		ArquivoMB bean = (ArquivoMB) resolver.getValue(
				context.getELContext(), null, "arquivoMB");
		bean.setPath(getPathAtual());
		bean.setOrigem(getOrigem());
	}

	public void setarDocumento(AjaxBehaviorEvent event) {
		int resultado = (Integer) ((UIOutput) event.getSource()).getValue();
		setDocumento(resultado);
		prepararArquivo();
	}

	public List<InstituicaoCurso> getCadastrados() {
		return cadastrados;
	}

	public void setCadastrados(List<InstituicaoCurso> cadastrados) {
		this.cadastrados = cadastrados;
	}

	public String verifyUser() {
		String email = getRepresentante().getEmail();
		String nome = getRepresentante().getNome();
		String CPF = getRepresentante().getCpf();
		FacesContext context = FacesContext.getCurrentInstance();
		ELResolver resolver = context.getApplication().getELResolver();
		UsuarioBean bean = (UsuarioBean) resolver.getValue(
				context.getELContext(), null, "usuarioMB");
		bean.setEmail(email);
		bean.setNome(nome);
		bean.setCPF(CPF);
		return "/pages/confirmacaoCadastroUsuario.xhtml?faces-redirect=true";

	}

	public String terminarValidacao() {
		LogValidacaoCadastro log = null;
		FacesContext context = FacesContext.getCurrentInstance();
		ELResolver resolver = context.getApplication().getELResolver();
		validadorBean = (ValidadorBean) resolver.getValue(
				context.getELContext(), null, "validadorMB");
		String[] comentario = validadorBean.getComentario();
		boolean[] arquivoValido = validadorBean.getArquivoValido();
		boolean validado = true;
		boolean controle = true;
		
		for(int i = 0; i < existsLog.size(); i++){
			if (existsLog.get(i) && i != 7) {
				if(controle){
					controle = arquivoValido[i];
				}
			}
			if(!controle && arquivoValido[7]){
				FacesUtil.addMsggError("Marque a validação cadastro"
						+ " como \"Inválida\"!");
				return "/pages/autenticado/validador/"
						+ "validadorCadastro.xhtml?faces-redirect=true";
			}
		}
		
		
		
		for (int i = 0; i <= existsLog.size(); i++) {
			if (i == 7) {
				log = new LogValidacaoCadastro(getUsuario(), getRepresentante(),
						comentario[i], arquivoValido[i] ? 2 : 3);
				try {
					logdao.add(log);
				} catch (InsertException e) {
					e.printStackTrace();
				}
				if(!arquivoValido[i]){
					validado = false;
				}
			} else {
				if (existsLog.get(i)) {
					try {
						log = logdao.getAnalisys(getUsuario(), 
								getRepresentante().getId(), 
								docdao.getByNro(i));
					} catch (EntityNotFoundException e) {
						e.printStackTrace();
					}
					log.setComentario(comentario[i]);
					log.setAtualizacao(new Date());
					logdao.update(log, arquivoValido[i] ? 2 : 3);
					
					if(!arquivoValido[i]){
						validado = false;
					}
				}
			}
		}
		if(validado){
			try {
				setRepresentante(repdao.get(getRepresentante().getId()));
				getRepresentante().setCadastro(2);
				setRepresentante(repdao.update(getRepresentante()));
			} catch (EntityNotFoundException e) {
				e.printStackTrace();
			}
			if(getRepresentante().getCadastro() != 1){
				getInstituicao().setSituacao(2);
				setInstituicao(instensdao.update(getInstituicao()));
			}
			HttpSession session = (HttpSession) FacesContext.getCurrentInstance()
					.getExternalContext().getSession(false);
			session.setAttribute("instituicao", getInstituicao());
		}
		Mail.sendEmailValidation(getRepresentante());
		validadorBean.reset();
		return "/pages/autenticado/validador/validadorIndex.xhtml?faces-redirect=true";
	}

	public String getInformacoesCEP() {
		String cep = getInstituicao().getEndereco().getCep();
		return ValidadorCEP.getEndereco(cep).toString().replaceAll("\n", "<br\\>");
	}

	public Representante getRepresentante() {
		return representante;
	}

	public void setRepresentante(Representante representante) {
		this.representante = representante;
	}
	
	/**
	 * Método que retorna se o Representante podia alterar informações da Instituição.
	 * @return boolean
	 */
	public boolean getEditavel() {
		if (getRepresentante().getCadastro() != 1) {
			return false;
		} else {
			return true;
		}
	}
	
	private String removeMascara(String campo) {
		String resultado = campo;
		resultado = resultado.replace("-", "");
		resultado = resultado.replace(".", "");
		resultado = resultado.replace("(", "");
		resultado = resultado.replace(")", "");
		resultado = resultado.replace(" ", "");
		resultado = resultado.replace("/", "");
		resultado = resultado.replace("_", "");
		return resultado;
	}
}
