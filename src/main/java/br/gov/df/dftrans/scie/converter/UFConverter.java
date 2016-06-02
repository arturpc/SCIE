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
 
    public Object getAsObject(FacesContext fc, UIComponent uic, String value) {
        if(value != null && value.trim().length() > 0) {
            try {
                UFService service = (UFService) fc.getExternalContext().getApplicationMap().get("UFService");
                return service.getUfPorSigla(value);
            } catch(NumberFormatException e) {
                throw new ConverterException(new FacesMessage(FacesMessage.SEVERITY_ERROR, "Conversion Error", "UF inválida."));
            }
        }
        else {
            return null;
        }
    }
 
    public String getAsString(FacesContext fc, UIComponent uic, Object object) {
        if(object != null) {
            return String.valueOf(((UF) object).getUf());
        }
        else {
            return null;
        }
    }   
}
