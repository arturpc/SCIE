package br.gov.df.dftrans.scie.converter;

import javax.faces.application.FacesMessage;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.convert.ConverterException;
import javax.faces.convert.FacesConverter;

import br.gov.df.dftrans.scie.service.CidadeService;
import br.gov.df.dftrans.scie.domain.Cidade;

@FacesConverter("cidadeConverter")
public class CidadeConverter implements Converter {
 
    public Object getAsObject(FacesContext fc, UIComponent uic, String value) {
        if(value != null && value.trim().length() > 0) {
            try {
                CidadeService service = (CidadeService) fc.getExternalContext().getApplicationMap().get("CidadeService");
                return service.getCidadePorNome(value);
            } catch(NumberFormatException e) {
                throw new ConverterException(new FacesMessage(FacesMessage.SEVERITY_ERROR, "Conversion Error", "Cidade inválida."));
            }
        }
        else {
            return null;
        }
    }
 
    public String getAsString(FacesContext fc, UIComponent uic, Object object) {
        if(object != null) {
            return String.valueOf(((Cidade) object).getNome());
        }
        else {
            return null;
        }
    }   
}
