package br.gov.df.dftrans.scie.exceptions;

import static br.gov.df.dftrans.scie.utils.MessageUtils.*;

/**
 * 
 * @author 9317295
 *
 */
public class InsertException extends Exception {

	public InsertException() {
		super(getString(INSERT_EXCEPTION_KEY));
	}

	public InsertException(String message) {
		super(message);
	}
}
