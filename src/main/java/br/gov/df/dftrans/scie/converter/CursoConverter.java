package br.gov.df.dftrans.scie.converter;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.convert.FacesConverter;

import org.primefaces.component.picklist.PickList;
import org.primefaces.model.DualListModel;

import br.gov.df.dftrans.scie.domain.Curso;

@FacesConverter(value = "cursoConverter")
public class CursoConverter implements Converter {

	
	public Object getAsObject(FacesContext arg0, UIComponent arg1, String arg2) {
		Object ret = null;
		if (arg1 instanceof PickList) {
			Object dualList = ((PickList) arg1).getValue();
			DualListModel<Curso> dl = (DualListModel<Curso>) dualList;
			for (Object o : dl.getSource()) {
				String id = "" + ((Curso) o).getId();
				if (arg2.equals(id)) {
					ret = o;
					break;
				}
			}
			if (ret == null)
				for (Object o : dl.getTarget()) {
					String id = "" + ((Curso) o).getId();
					if (arg2.equals(id)) {
						ret = o;
						break;
					}
				}
		}
		return ret;
	}

	public String getAsString(FacesContext arg0, UIComponent arg1, Object arg2) {
	    String str = "";
	    if (arg2 instanceof Curso) {
	        str = "" + ((Curso) arg2).getId();
	    }
	    return str;
	}
	
	
	
	

}
