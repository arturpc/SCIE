package br.gov.df.dftrans.scie.utils;

import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class AutenticacaoDocumentos {
	/**
	 * Converte um texto para o padrão MD5
	 * 
	 * @param chaveTexto
	 * @return texto convertido
	 */
	public static String getChaveSeguranca(String chaveTexto) {
		byte bytesOriginais[] = chaveTexto.getBytes(), 
				complementoDeUm[] = new byte[bytesOriginais.length];
		MessageDigest m = null;
		try {
			m = MessageDigest.getInstance("MD5");
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		}
		int count = 0;
		for (byte b : bytesOriginais) {
			complementoDeUm[count] = (byte) ((b - (byte) 00000001) * (byte) -1);
			count++;
		}
		m.update(complementoDeUm, 0, chaveTexto.length());
		return new BigInteger(1, m.digest()).toString(16);
	}

}
