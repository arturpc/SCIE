package br.gov.df.dftrans.scie.converter;

import java.util.List;

import javax.el.ELResolver;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.convert.FacesConverter;

import br.gov.df.dftrans.scie.domain.MesReferencia;
import br.gov.df.dftrans.scie.view.FrequenciaBean;

@FacesConverter("mesReferenciaConverter")
public class MesReferenciaConverter implements Converter{
	private FrequenciaBean freq;
	private FacesContext context = FacesContext.getCurrentInstance();
	private ELResolver resolver = context.getApplication().getELResolver();
	
    public Object getAsObject(FacesContext fc, UIComponent uic, String value) {
        if(value != null && value.trim().length() > 0) {
        	freq = (FrequenciaBean) resolver.getValue(
        			context.getELContext(), null, "FrequenciaMB");
           	if(freq == null){
        		return 0;
        	}
        	List<MesReferencia> list = freq.getMeses();
        	if(list == null){
        		return 0;
        	}
        	for(MesReferencia ref:list){
        		if(value.trim().length() > 6){
        			if(ref.getDisplay().equals(value.trim())){
        				return ref.getValue();
        			}
        		}else{
        			if(ref.getValue() == Integer.parseInt(value.trim())){
        				return ref.getValue();
        			}
        		}
        	}
        	return 0;
        }
        else {
            return 0;
        }
    }
    
    public String getAsString(FacesContext fc, UIComponent uic, Object object) {
    	freq = (FrequenciaBean) resolver.getValue(context.getELContext(), null, "FrequenciaMB");
    	if(freq == null){
    		return null;
    	}
    	List<MesReferencia> list = freq.getMeses();
    	if(list == null){
    		return null;
    	}
    	if(object != null) {
    		for(MesReferencia ref:list){
    			if(object.getClass() == String.class){
    			if(ref.getValue() == Integer.parseInt(((String) object).trim())){
        			return ref.getDisplay();
        		}
    			}else{
    				if(ref.getValue() == (Integer) object){
            			return ref.getDisplay();
            		}	
    			}
    		}
    		return null;
        }
        else {
            return null;
        }
    }  
}
     
  