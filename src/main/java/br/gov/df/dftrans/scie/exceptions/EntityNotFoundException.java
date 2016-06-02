package br.gov.df.dftrans.scie.exceptions;

import static br.gov.df.dftrans.scie.utils.MessageUtils.*;

/**
 * Exce��o para quando n�o localizar algum objeto
 * 
 * @author 9317295
 *
 */
public class EntityNotFoundException extends Exception {

	public EntityNotFoundException() {
		super(getString(ENTITY_NOT_FOUND_KEY));
	}

	public EntityNotFoundException(String message) {
		super(message);
	}
}