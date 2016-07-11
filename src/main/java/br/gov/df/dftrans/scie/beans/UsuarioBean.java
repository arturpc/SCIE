package br.gov.df.dftrans.scie.beans;

import java.io.IOException;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.context.FacesContext;
import javax.servlet.http.HttpSession;

import br.gov.df.dftrans.scie.dao.RepresentanteDAO;
import br.gov.df.dftrans.scie.dao.UsuarioDAO;
import br.gov.df.dftrans.scie.domain.InstituicaoEnsino;
import br.gov.df.dftrans.scie.domain.Representante;
import br.gov.df.dftrans.scie.domain.Usuario;
import br.gov.df.dftrans.scie.exceptions.EntityNotFoundException;
import br.gov.df.dftrans.scie.exceptions.InsertException;
import br.gov.df.dftrans.scie.exceptions.UsuarioExistenteException;
import br.gov.df.dftrans.scie.utils.FacesUtil;
import br.gov.df.dftrans.scie.utils.Mail;

@ManagedBean(name = "usuarioMB")
@SessionScoped
public class UsuarioBean {
	private RepresentanteDAO repdao = RepresentanteDAO.RepresentanteDAO();
	private Representante representante;
	private Usuario user;
	private String email, nome, CPF;
	private UsuarioDAO usrdao = UsuarioDAO.UsuarioDAO();
	private int perfil;

	/**
	 * Cadastra usuário no banco e envia email
	 */
	public void init() {
		HttpSession session = (HttpSession) FacesContext.getCurrentInstance()
				.getExternalContext().getSession(false);
		setRepresentante((Representante) session.getAttribute("representante"));
		setUser(usrdao.getByEmail(getEmail()));
		if (getUser() == null) {
			try {
				setUser(new Usuario(getEmail(), getNome(), 0, getCPF()));
				Mail.sendEmailUser(getUser());
				setUser(usrdao.add(getUser()));
				if(getPerfil() == 0){
					getRepresentante().setUsuario(getUser());
					setRepresentante(repdao.update(getRepresentante()));
				}
			}catch (InsertException e) {
				e.printStackTrace();
			} catch (UsuarioExistenteException e){
				try {
					FacesUtil.addMsggError(e.getMessage());
					FacesContext.getCurrentInstance()
					.getExternalContext().redirect("instituicao/confirmacaoCadastro.xhtml");
				} catch (IOException e1) {
					e1.printStackTrace();
				}
			}
		} else {
			try {
				FacesContext.getCurrentInstance()
				.getExternalContext().redirect("index.xhtml");
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	/**
	 * Cadastra um usuario
	 */
	public void cadastrarUsuario() {
		setUser(usrdao.getByEmail(getEmail()));
		if (getUser() == null) {
			try {
				setUser(new Usuario(getEmail(), getNome(), getPerfil(), getCPF()));
				Mail.sendEmailUser(getUser());
				setUser(usrdao.add(getUser()));
				if(getPerfil() == 0){
					setRepresentante(repdao.get(getRepresentante().getId()));
					getRepresentante().setUsuario(getUser());
					setRepresentante(repdao.update(getRepresentante()));
				}
				setEmail("");
				setNome("");
				setPerfil(2);
			} catch (EntityNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (InsertException e) {
				e.printStackTrace();
			} catch (UsuarioExistenteException e){
				FacesUtil.addMsggError(e.getMessage());
				setEmail("");
				setNome("");
				setPerfil(2);
			}
		} else {
			FacesUtil.addMsggError("O usuário com email " + getEmail() + " já existe!");
			setEmail("");
			setNome("");
			setPerfil(2);
		}
	}

	//redirecionamentos
	
	public String voltar() {
		return "/pages/index.xhtml?faces-redirect=true";
	}

	//getteres and setteres
	
	public Usuario getUser() {
		return user;
	}

	public void setUser(Usuario user) {
		this.user = user;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getNome() {
		return nome;
	}

	public void setNome(String nome) {
		this.nome = nome;
	}

	public int getPerfil() {
		return perfil;
	}

	public void setPerfil(int perfil) {
		this.perfil = perfil;
	}

	public String getCPF() {
		return CPF;
	}

	public void setCPF(String cPF) {
		CPF = cPF;
	}

	public Representante getRepresentante() {
		return representante;
	}

	public void setRepresentante(Representante representante) {
		this.representante = representante;
	}
}
