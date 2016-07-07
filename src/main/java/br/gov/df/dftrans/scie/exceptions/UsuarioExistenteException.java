package br.gov.df.dftrans.scie.exceptions;

/**
 * Exceção para quando localizar usuário com este login
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

