package br.gov.df.dftrans.scie.converter;

import javax.el.ELResolver;
import javax.faces.application.FacesMessage;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.convert.ConverterException;
import javax.faces.convert.FacesConverter;

import br.gov.df.dftrans.scie.dao.MotivoDAO;
import br.gov.df.dftrans.scie.domain.Motivo;
import br.gov.df.dftrans.scie.service.MotivoService;

@FacesConverter("motivoConverter")
public class MotivoConverter implements Converter {
private MotivoDAO dao = MotivoDAO.motivoDAO();
	public MotivoConverter() {
	}
	/**
	 * Converte a string do forumlário em um objeto
	 */
	public Object getAsObject(FacesContext fc, UIComponent uic, String value) {
		MotivoService service = null;
		if (!value.isEmpty()) {
			try {
				FacesContext context = FacesContext.getCurrentInstance();
				ELResolver resolver = context.getApplication().getELResolver();
				service = (MotivoService) resolver.getValue(context.getELContext(), null, "MotivoService");
				
			} catch (NumberFormatException e) {
				throw new ConverterException(
						new FacesMessage(FacesMessage.SEVERITY_ERROR, "Conversion Error", "Motivo inválida."));
			}
		}
		return service.getMotivoPorNome(value);
		
	}

	/**
	 * Converte o objeto em uma string
	 */
	public String getAsString(FacesContext fc, UIComponent uic, Object object) {
		String ret = null;
		if (object != null) {
			ret = String.valueOf(((Motivo) object).getMotivo());
		}
		return ret;
		
	}
}