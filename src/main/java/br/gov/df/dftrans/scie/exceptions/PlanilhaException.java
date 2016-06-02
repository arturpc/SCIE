package br.gov.df.dftrans.scie.exceptions;

/**
 * Exce��o corrida n�o verificada
 * 
 * @author 9317295
 *
 */
public class PlanilhaException extends RuntimeException {

	private String msg;

	public PlanilhaException(String msg) {
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