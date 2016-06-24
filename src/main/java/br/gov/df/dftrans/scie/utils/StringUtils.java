package br.gov.df.dftrans.scie.utils;

import br.gov.df.dftrans.scie.annotations.StringUpperCase;
import br.gov.df.dftrans.scie.annotations.StringFormatter;
import java.lang.reflect.Field;
import java.text.Normalizer;

public class StringUtils {
	/**
	 * Caso o atributo do objeto tenha a anota��o StringUpperCase, passa para
	 * maiusculo; Caso o atributo do objeto tenha a anota��o StringFormatter,
	 * passa pra maiusculo e tira acentos
	 * 
	 * @param object
	 */
	public static void parserObject(Object object) {
		Class objectClass = object.getClass();

		for (Field field : objectClass.getDeclaredFields()) {
			// consegue acesso aos atributos do objeto
			field.setAccessible(true);
			// verifica se tem a anota��o @StringUpperCase
			if (field.isAnnotationPresent(StringUpperCase.class)) {
				try {
					// passa os dados do atributo com a anota��o para mai�sculo
					field.set(object, field.get(object)
							.toString().toUpperCase());
				} catch (Exception ex) {
					ex.printStackTrace();
				}
			}
			// verifica se tem a anota��o @StringFormatter
			if (field.isAnnotationPresent(StringFormatter.class)) {
				try {
					// passa os dados do atributo com a anota��o para mai�sculo
					field.set(object, field.get(object)
							.toString().toUpperCase());
					// retira acentos
					field.set(object, Normalizer.normalize(field.get(object)
							.toString(), Normalizer.Form.NFD)
							.replaceAll("[^\\p{ASCII}]", "").trim());
				} catch (Exception ex) {
					ex.printStackTrace();
				}
			}

		}
	}

}
