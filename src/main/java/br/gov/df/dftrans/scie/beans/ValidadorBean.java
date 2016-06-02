package br.gov.df.dftrans.scie.beans;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.context.FacesContext;
import javax.servlet.http.HttpSession;

import br.gov.df.dftrans.scie.domain.Usuario;

@ManagedBean(name="validadorMB")
@SessionScoped
public class ValidadorBean {
	
	private Usuario user;
	private boolean[] arquivoValido = new boolean[8];
	private String[] comentario = new String[8];
	
	//inicia o usuário com o usuário da sessão
	public void init(){
		HttpSession session = (HttpSession) FacesContext.getCurrentInstance().getExternalContext().getSession(false);
        setUser((Usuario) session.getAttribute("usuario"));
	}

	//getteres and setteres
	
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
	
	
}
