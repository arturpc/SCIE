package br.gov.df.dftrans.scie.exceptions;

/**
 * Exce��o para quando localizar usu�rio com este login
 * 
 * @author artur
 *
 */
public class UsuarioExistenteException extends Exception{
	public UsuarioExistenteException() {
		super();
	}

	public UsuarioExistenteException(String message) {
		super(message);
	}
}

