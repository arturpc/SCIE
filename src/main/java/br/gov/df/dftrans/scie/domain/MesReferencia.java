package br.gov.df.dftrans.scie.domain;

public class MesReferencia {
	private String display;
	private int value;

	public MesReferencia(String diplay, int value) {
		setDisplay(diplay);
		setValue(value);
	}

	// getteres and setteres
	public String getDisplay() {
		return display;
	}

	public void setDisplay(String display) {
		this.display = display;
	}

	public int getValue() {
		return value;
	}

	public void setValue(int value) {
		this.value = value;
	}

}
