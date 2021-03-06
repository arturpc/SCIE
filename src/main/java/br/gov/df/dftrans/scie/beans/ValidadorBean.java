package br.gov.df.dftrans.scie.beans;
import java.io.File;

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
	private File[] aquivos = new File[8];
	//inicia o usu�rio com o usu�rio da sess�o
	public void init(){
		HttpSession session = (HttpSession) FacesContext
				.getCurrentInstance().getExternalContext().getSession(false);
        setUser((Usuario) session.getAttribute("usuario"));
	}
	
	public void reset(){
		arquivoValido = new boolean[8];
		comentario = new String[8];
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

	public File[] getAquivos() {
		return aquivos;
	}

	public void setAquivos(File[] aquivos) {
		this.aquivos = aquivos;
	}
	
	
}
