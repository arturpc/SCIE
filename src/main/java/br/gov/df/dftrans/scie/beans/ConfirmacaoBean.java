package br.gov.df.dftrans.scie.beans;

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
import br.gov.df.dftrans.scie.domain.Comentario;
import br.gov.df.dftrans.scie.domain.InstituicaoCurso;
import br.gov.df.dftrans.scie.domain.InstituicaoEnsino;
import br.gov.df.dftrans.scie.domain.LogValidacaoCadastro;
import br.gov.df.dftrans.scie.domain.Usuario;
import br.gov.df.dftrans.scie.exceptions.EntityNotFoundException;
import br.gov.df.dftrans.scie.exceptions.InsertException;
import br.gov.df.dftrans.scie.utils.AutenticacaoDocumentos;
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
	private ArrayList<Boolean> exists = new ArrayList<Boolean>(), existsLog = new ArrayList<Boolean>();
	private String[] path;
	private String current = Parametros.getParameter("root_upload"), pathAtual;
	private int documento, origem;
	private List<InstituicaoCurso> cadastrados;
	private InstituicaoCursoDAO instdao = InstituicaoCursoDAO.InstituicaoCursoDAO();
	private LogDAO logdao = LogDAO.LogDAO();
	private DocumentoPendenciaDAO docdao = DocumentoPendenciaDAO.DocumentoPendenciaDAO();
	private Usuario usuario;
	private ValidadorBean validadorBean;

	public void init() {
		exists = new ArrayList<Boolean>();
		existsLog = new ArrayList<Boolean>();
		HttpSession session = (HttpSession) FacesContext.getCurrentInstance().getExternalContext().getSession(false);
		setInstituicao((InstituicaoEnsino) session.getAttribute("instituicao"));
		setPath(ManipuladorArquivos
				.leitor(current + "\\destino_uploader\\" + getInstituicao().getId() + "\\files"));
		for (String temp : getPath()) {
			if (temp.equals("0")) {
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
		path = null;
		documento = -1;
		if (getCadastrados() != null) {
			cadastrados.clear();
		}
		try {
			HttpSession session = (HttpSession) FacesContext.getCurrentInstance().getExternalContext().getSession(false);
			setUsuario((Usuario) session.getAttribute("usuario"));
				if (logdao.getAnalisysUser(getUsuario()) != null) {
					setInstituicao(logdao.getAnalisysUser(getUsuario()).getInstituicao());
				} else {
					setInstituicao(logdao.getOpens().get(0).getInstituicao());
				}
				setPath(ManipuladorArquivos
						.leitor(current + "\\destino_uploader\\" + getInstituicao().getId() + "\\files"));
				int cont = 0;
				for (String temp : getPath()) {
					if (temp.equals("0")) {
						exists.add(false);
					} else {
						exists.add(true);
					}
					existsLog.add(logdao.existsLog(instituicao.getId(), docdao.getByNro(cont).getId()));
					cont++;
				}
				setPathAtual(getPath()[0]);
				setDocumento(0);
				setOrigem(2);
				prepararArquivo();
				setCadastrados(instdao.getCursos(getInstituicao()));
				System.out.println(instituicao.getId());
				LogValidacaoCadastro log;
				for (int i = 0; i < existsLog.size(); i++) {
					if (existsLog.get(i)) {
						log = logdao.get(instituicao.getId(), docdao.getByNro(i).getId());
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
		if (getInstituicao() != null && getInstituicao().getRepresentante() != null) {
			int i = getInstituicao().getRepresentante().getCargo();
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
		if (exists.get(getDocumento()) && aux.length > 1 && !aux[1].toLowerCase().equals("pdf")) {
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
		FileUploadView filebean = (FileUploadView) resolver.getValue(context.getELContext(), null, "fileUploadView");
		return filebean.getDescricaoArquivos()[getDocumento()];
	}

	public String getAutenticacao() {
		String chave = "";
		chave = getInstituicao().getCnpj();
		chave += getInstituicao().getId();
		chave += getInstituicao().getRepresentante().getNome();
		chave += getInstituicao().getRepresentante().getCpf();
		String[] aux = getPathAtual().split("destino_uploader"), aux1;
		aux1 = aux[1].split("\\\\");
		chave += aux1[2];
		chave += getDocumento();
		chave = chave.replaceAll("/", "").replaceAll(" ", "").replaceAll("-", "");
		return AutenticacaoDocumentos.getChaveSeguranca(chave);
	}

	public void prepararArquivo() {
		setPathAtual(getPath()[getDocumento()]);
		FacesContext context = FacesContext.getCurrentInstance();
		ELResolver resolver = context.getApplication().getELResolver();
		ArquivoMB bean = (ArquivoMB) resolver.getValue(context.getELContext(), null, "arquivoMB");
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
		String email = getInstituicao().getRepresentante().getEmail();
		String nome = getInstituicao().getRepresentante().getNome();
		FacesContext context = FacesContext.getCurrentInstance();
		ELResolver resolver = context.getApplication().getELResolver();
		UsuarioBean bean = (UsuarioBean) resolver.getValue(context.getELContext(), null, "usuarioMB");
		bean.setEmail(email);
		bean.setNome(nome);
		return "/pages/confirmacaoCadastroUsuario.xhtml?faces-redirect=true";

	}

	public String terminarValidacao() {
		LogValidacaoCadastro log = null;
		FacesContext context = FacesContext.getCurrentInstance();
		ELResolver resolver = context.getApplication().getELResolver();
		validadorBean = (ValidadorBean) resolver.getValue(context.getELContext(), null, "validadorMB");
		String[] comentario = validadorBean.getComentario();
		boolean[] arquivoValido = validadorBean.getArquivoValido();
		boolean validado = true;
		System.out.println(existsLog.size());
		for (int i = 0; i <= existsLog.size(); i++) {
			System.out.println("i = "+ i);
			if (i == 7) {
				log = new LogValidacaoCadastro(getUsuario(), getInstituicao(), comentario[i], arquivoValido[i] ? 2 : 3);
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
						log = logdao.getAnalisys(getUsuario(), getInstituicao(), docdao.getByNro(i));
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
			getInstituicao().setSituacao(2);
			instensdao.update(getInstituicao());
			HttpSession session = (HttpSession) FacesContext.getCurrentInstance().getExternalContext().getSession(false);
			session.setAttribute("instituicao", getInstituicao());
		}
		Mail.sendEmailValidation(getInstituicao().getRepresentante());
		
		return "/pages/autenticado/validador/validadorIndex.xhtml?faces-redirect=true";
	}

	public String getInformacoesCEP() {
		String cep = getInstituicao().getEndereco().getCep();
		return ValidadorCEP.getEndereco(cep).toString().replaceAll("\n", "<br\\>");
	}
}
