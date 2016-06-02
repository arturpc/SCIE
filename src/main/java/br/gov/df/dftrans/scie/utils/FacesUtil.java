package br.gov.df.dftrans.scie.utils;

import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;

@ManagedBean(name = "MB_Menseger")
@ViewScoped
public class FacesUtil {
	/**
	 * Mensagem de sucesso na tela xhtml
	 * 
	 * @param msg
	 */
	public static void addMsgInfo(String msg) {
		FacesMessage facesMessage = new FacesMessage(FacesMessage.SEVERITY_INFO, msg, msg);
		FacesContext facesContext = FacesContext.getCurrentInstance();
		facesContext.addMessage(null, facesMessage);
	}

	/**
	 * Mensagem de erro na tela xhtml
	 * 
	 * @param msg
	 */
	public static void addMsggError(String msg) {
		FacesMessage facesMessage = new FacesMessage(FacesMessage.SEVERITY_ERROR, msg, msg);
		FacesContext facesContext = FacesContext.getCurrentInstance();
		facesContext.addMessage(null, facesMessage);
	}
}
