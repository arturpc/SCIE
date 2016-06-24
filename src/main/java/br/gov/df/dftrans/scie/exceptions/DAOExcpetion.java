package br.gov.df.dftrans.scie.exceptions;

/**
 * Exceção corrida da Dao não verificada
 * 
 * @author 9317295
 *
 */
public class DAOExcpetion extends RuntimeException {

	String msg;

	public DAOExcpetion(String msg) {
		super(msg);
		setMsg(msg);
	}

	public String getMsg() {
		return msg;
	}

	public void setMsg(String msg) {
		this.msg = msg;
	}
}
