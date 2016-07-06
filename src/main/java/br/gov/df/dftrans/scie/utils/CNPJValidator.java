package br.gov.df.dftrans.scie.utils;

import java.io.Serializable;

import br.gov.df.dftrans.scie.annotations.CNPJ;

/**
 * Valida CNPJ
 * 
 * @author 9317295
 *
 */
public class CNPJValidator implements Serializable {

	private static final long serialVersionUID = 8856838765083713270L;

	private static final char[] ZEROS = { '0', '0', '0', '0', '0', '0', '0',
			'0', '0', '0', '0', '0', '0', '0' };

	public void initialize(CNPJ constraintAnnotation) {
	}

	/**
	 * Método que retorna se um dado cnpj é valido (bem formatado)
	 * @param value
	 * @return boolean
	 */
	public boolean isValid(String value) {
		if (value == null || value.length() == 0) {
			return true;
		}
		String cnpj = value;

		if (cnpj.length() < 14) {
			cnpj = new String(ZEROS, 0, 14 - cnpj.length()) + cnpj;
		}

		return isValidCNPJ(cnpj);
	}

	/**
	 * Verifica se bate os digitos validadores do CNPJ
	 * 
	 * @param cnpj
	 * @return true se baterem ou false se não baterem
	 */
	private static boolean isValidCNPJ(String cnpj) {

		if ((cnpj == null) || "00000000000000".equals(cnpj)) {
			return false;
		}

		try {
			Integer digit1 = calculateDigit(cnpj.substring(0, 12));
			Integer digit2 = calculateDigit(cnpj.substring(0, 12) + "" + digit1);
			System.out.println(cnpj.substring(0, 12) + digit1.toString() 
			+ digit2.toString());
			return cnpj.equals(cnpj.substring(0, 12) + digit1.toString() 
			+ digit2.toString());
		} catch (Exception e) {
			return false;
		}
	}

	/**
	 * Calculo de validação dos dígitos do CNPJ
	 * 
	 * @param str
	 * @return o calculo
	 */
	private static int calculateDigit(String str) {
		int soma, iterador, resultado, numero, peso;
		soma = 0;
		peso = 2;
		for (iterador = str.length() - 1; iterador >= 0; iterador--) {
			numero = (int) (str.charAt(iterador) - 48);
			soma = soma + (numero * peso);
			peso = peso + 1;
			if (peso == 10){
				peso = 2;
			}
		}
		resultado = soma % 11;
		if ((resultado == 0) || (resultado == 1)){
			return 0;
		}
		else{
			return 11 - resultado;
		}
	}
}
