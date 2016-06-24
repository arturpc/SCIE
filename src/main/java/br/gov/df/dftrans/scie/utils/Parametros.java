//?
package br.gov.df.dftrans.scie.utils;

import java.util.ResourceBundle;

public class Parametros {
	static ResourceBundle bundle = ResourceBundle.getBundle("properties");

	/**
	 * Arquivo de parametrização de mensagens
	 * 
	 * @param key
	 * @return a mensagem de acordo com a chave parametrizada
	 */
	public static String getParameter(String parameter) {
		return bundle.getString(parameter);
	}

}
