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
 
	public CidadeConverter() {
	}
	
    public Object getAsObject(FacesContext fc, UIComponent uic, String value) {
    	Object obj = null;
    	if(!value.isEmpty()) {
            try {
                CidadeService service = (CidadeService) fc.getExternalContext().getApplicationMap().get("CidadeService");
                obj = service.getCidadePorNome(value);
            } catch(NumberFormatException e) {
                throw new ConverterException(new FacesMessage(FacesMessage.SEVERITY_ERROR, "Conversion Error", "Cidade inválida."));
            }
        }
        return obj;
    }
 
    public String getAsString(FacesContext fc, UIComponent uic, Object object) {
    	String ret = null;
    	if(object != null) {
            ret =  String.valueOf(((Cidade) object).getNome());
        }
        return ret;
    }   
}
