package br.gov.df.dftrans.scie.validators;

import java.util.Map;

import javax.faces.application.FacesMessage;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.validator.FacesValidator;
import javax.faces.validator.Validator;
import javax.faces.validator.ValidatorException;

import org.primefaces.validate.ClientValidator;

@FacesValidator("namesValidator")
public class NamesValidator implements Validator, ClientValidator {

	public Map<String, Object> getMetadata() {
		// TODO Auto-generated method stub
		return null;
	}

	public String getValidatorId() {
		// TODO Auto-generated method stub
		return null;
	}

	/**
	 * valida nome vazio ou nulo
	 */
	public void validate(FacesContext context, UIComponent component, Object value) throws ValidatorException {
		if (value == null) {
			return;
		}

		if (value.toString().trim().isEmpty()) {
			throw new ValidatorException(new FacesMessage(FacesMessage.SEVERITY_ERROR, "Erro de Validação",
					"Espaços não é um valor válido!"));
		}
	}

}
