package br.gov.df.dftrans.scie.utils;

import java.util.ResourceBundle;

public class MessageUtils {

	private static final String MESSAGE = "messages";

	public static final String ENTITY_NOT_FOUND_KEY = "error_message_not_found";
	public static final String INSERT_EXCEPTION_KEY = "error_message_insert";
	public static final String ALREADY_EXISTS_EXCEPTION_KEY = "error_message_already_exists";
	public static final String ENVIROMENT_KEY = "enviroment";

	/**
	 * Arquivo de parametrização de mensagens
	 * 
	 * @param key
	 * @return a mensagem de acordo com a chave parametrizada
	 */
	public static String getString(String key) {
		return ResourceBundle.getBundle(MESSAGE).getString(key);
	}
}
