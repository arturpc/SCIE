<<<<<<< HEAD
package br.gov.df.dftrans.scie.utils;

import java.io.Serializable;

import br.gov.df.dftrans.scie.annotations.CPF;

/**
 * Valida CPF
 * 
 * @author 9317295
 *
 */
public class CPFValidator implements Serializable {

	private static final long serialVersionUID = 8856838765083713270L;

	private static final int[] pesoCPF = { 11, 10, 9, 8, 7, 6, 5, 4, 3, 2 };

	private static final char[] ZEROS = { '0', '0', '0', '0', '0', '0', '0', '0', '0', '0', '0' };

	public void initialize(CPF constraintAnnotation) {
	}

	public static boolean isValid(String value) {
		if (value == null || value.length() == 0) {
			return true;
		}
		String cpf = value;

		if (cpf.length() < 11) {
			cpf = new String(ZEROS, 0, 11 - cpf.length()) + cpf;
		}

		return isValidCPF(cpf);
	}

	/**
	 * Valida Cpf
	 * 
	 * @param cpf
	 * @return true se for valido ou valse
	 */
	private static boolean isValidCPF(String cpf) {

		if ((cpf == null) || cpf.equals("11111111111") || cpf.equals("22222222222") || cpf.equals("33333333333")
				|| cpf.equals("44444444444") || cpf.equals("55555555555") || cpf.equals("66666666666")
				|| cpf.equals("77777777777") || cpf.equals("88888888888") || cpf.equals("99999999999")
				|| cpf.equals("00000000000")) {
			return false;
		}
		try {
			Integer digit1 = calculateDigit(cpf.substring(0, 9), pesoCPF);
			Integer digit2 = calculateDigit(cpf.substring(0, 9) + digit1, pesoCPF);
			return cpf.equals(cpf.substring(0, 9) + digit1.toString() + digit2.toString());
		} catch (Exception e) {
			return false;
		}
	}

	/**
	 * Calculo dos digitos verificadores
	 * 
	 * @param str
	 * @param peso
	 * @return a soma
	 */
	private static int calculateDigit(String str, int[] peso) {
		int soma = 0;
		for (int indice = str.length() - 1, digit; indice >= 0; indice--) {
			digit = Integer.parseInt(str.substring(indice, indice + 1));
			soma += digit * peso[peso.length - str.length() + indice];
		}
		soma = 11 - soma % 11;
		return soma > 9 ? 0 : soma;
	}

}
=======
package br.gov.df.dftrans.scie.utils;

import java.io.Serializable;

import br.gov.df.dftrans.scie.annotations.CPF;

/**
 * Valida CPF
 * 
 * @author 9317295
 *
 */
public class CPFValidator implements Serializable {

	private static final long serialVersionUID = 8856838765083713270L;

	private static final int[] PESOCPF = { 11, 10, 9, 8, 7, 6, 5, 4, 3, 2 };

	private static final char[] ZEROS = { '0', '0', '0', '0', '0', '0', '0', '0', '0', '0', '0' };

	public void initialize(CPF constraintAnnotation) {
	}

	public static boolean isValid(String value) {
		if (value == null || value.length() == 0) {
			return true;
		}
		String cpf = value;

		if (cpf.length() < 11) {
			cpf = new String(ZEROS, 0, 11 - cpf.length()) + cpf;
		}

		return isValidCPF(cpf);
	}

	/**
	 * Valida Cpf
	 * 
	 * @param cpf
	 * @return true se for valido ou valse
	 */
	private static boolean isValidCPF(String cpf) {

		if ((cpf == null) || cpf.equals("11111111111") || cpf.equals("22222222222") || cpf.equals("33333333333")
				|| cpf.equals("44444444444") || cpf.equals("55555555555") || cpf.equals("66666666666")
				|| cpf.equals("77777777777") || cpf.equals("88888888888") || cpf.equals("99999999999")
				|| cpf.equals("00000000000")) {
			return false;
		}
		try {
			Integer digit1 = calculateDigit(cpf.substring(0, 9), PESOCPF);
			Integer digit2 = calculateDigit(cpf.substring(0, 9) + digit1, PESOCPF);
			return cpf.equals(cpf.substring(0, 9) + digit1.toString() + digit2.toString());
		} catch (Exception e) {
			return false;
		}
	}

	/**
	 * Calculo dos digitos verificadores
	 * 
	 * @param str
	 * @param peso
	 * @return a soma
	 */
	private static int calculateDigit(String str, int[] peso) {
		int soma = 0;
		for (int indice = str.length() - 1, digit; indice >= 0; indice--) {
			digit = Integer.parseInt(str.substring(indice, indice + 1));
			soma += digit * peso[peso.length - str.length() + indice];
		}
		soma = 11 - soma % 11;
		return soma > 9 ? 0 : soma;
	}

}
>>>>>>> origin/master
