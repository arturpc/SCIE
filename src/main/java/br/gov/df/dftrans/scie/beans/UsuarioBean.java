package br.gov.df.dftrans.scie.beans;

import java.io.IOException;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.context.FacesContext;

import br.gov.df.dftrans.scie.dao.UsuarioDAO;
import br.gov.df.dftrans.scie.domain.Usuario;
import br.gov.df.dftrans.scie.exceptions.InsertException;
import br.gov.df.dftrans.scie.utils.FacesUtil;
import br.gov.df.dftrans.scie.utils.Mail;

@ManagedBean(name = "usuarioMB")
@SessionScoped
public class UsuarioBean {
	private Usuario user;
	private String email, nome;
	private UsuarioDAO usrdao = UsuarioDAO.UsuarioDAO();
	private int perfil;

	/**
	 * Cadastra usuário no banco e envia email
	 */
	public void init() {
		setUser(usrdao.getByEmail(getEmail()));
		if (getUser() == null) {
			setUser(new Usuario(getEmail(), getNome(), 0));
			try {
				Mail.sendEmailUser(getUser());
				usrdao.add(getUser());
			} catch (InsertException e) {
				e.printStackTrace();
			}
		} else {
			try {
				FacesContext.getCurrentInstance().getExternalContext().redirect("index.xhtml");
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
			setUser(new Usuario(getEmail(), getNome(), getPerfil()));
			try {
				Mail.sendEmailUser(getUser());
				usrdao.add(getUser());
				setEmail("");
				setNome("");
				setPerfil(2);
			} catch (InsertException e) {
				e.printStackTrace();
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

}
