package br.gov.df.dftrans.scie.converter;
 
import javax.faces.application.FacesMessage;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.convert.ConverterException;
import javax.faces.convert.FacesConverter;
 
import br.gov.df.dftrans.scie.domain.UF;
import br.gov.df.dftrans.scie.service.UFService;
 
@FacesConverter("ufConverter")
public class UFConverter implements Converter {
	public UFConverter() {
	}
    public Object getAsObject(FacesContext fc, UIComponent uic, String value) {
    	 UFService service = null;
    	if(!value.isEmpty()) {
            try {
                service = (UFService) fc.getExternalContext().getApplicationMap().get("UFService");
               
            } catch(NumberFormatException e) {
                throw new ConverterException(new FacesMessage(FacesMessage.SEVERITY_ERROR, "Conversion Error", "UF inválida."));
            }
        }
    	 return service.getUfPorSigla(value);
    }
 
    public String getAsString(FacesContext fc, UIComponent uic, Object object) {
        String ret = null;
        		
    	if(object != null) {
            ret = String.valueOf(((UF) object).getUf());
        }
        return ret;
    }   
}
