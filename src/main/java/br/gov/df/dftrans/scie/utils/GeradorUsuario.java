package br.gov.df.dftrans.scie.utils;

import java.text.Normalizer;
import java.util.Random;

import br.gov.df.dftrans.scie.dao.UsuarioDAO;
import br.gov.df.dftrans.scie.exceptions.UsuarioExistenteException;

public class GeradorUsuario {

	private static UsuarioDAO usrdao = UsuarioDAO.UsuarioDAO();

	/**
	 * Gera uma senha aleatoria com digitos de 0 a 122
	 * 
	 * @return senha aleatoria
	 */
	public static String gerarSenha() {
		String resultado = "";
		int seed;
		for (int i = 0; i < 6; i++) {
			seed = (int) (Math.random() * 123);
			while (!((seed >= 48 && seed <= 57) || (seed >= 97 && seed <= 122))) {
				seed = (int) (Math.random() * 100);
			}
			resultado += (char) seed;
		}
		return resultado;
	}

	/**
	 * Gera um login nome.ultimoNome (sem acentos)
	 * 
	 * @param nome
	 * @return login gerado
	 */
	public static String gerarLogin(String nome, int perfil) throws UsuarioExistenteException{
		String resultado = "";
		if(perfil == 0){
			resultado = nome;
			if(usrdao.getByLogin(resultado) != null){
				throw new UsuarioExistenteException("J� existe um "
						+ "usu�rio com o login "+ nome);
			}
		}else{
			String[] aux = nome.split(" ");
			String iterador = "";
			int i = 1;
			resultado += Normalizer.normalize(aux[0].toLowerCase(), Normalizer.Form.NFD)
					.replaceAll("[^\\p{ASCII}]", "");
			resultado += ".";
			iterador = Normalizer.normalize(aux[aux.length - i].toLowerCase(),
					Normalizer.Form.NFD)
					.replaceAll("[^\\p{ASCII}]", "");
			while (usrdao.getByLogin(resultado + iterador) != null) {
				// Caso login tenha no banco, ele pega nome.sobrenome, inv�s de
				// ultimo nome, ate chegar nome.nome
				i++;
				iterador = Normalizer.normalize(aux[aux.length - i].toLowerCase(),
						Normalizer.Form.NFD)
						.replaceAll("[^\\p{ASCII}]", "");
				if(i == aux.length){
					throw new UsuarioExistenteException("J� existe um "
							+ "usu�rio com o login "+ resultado + iterador);
				}
			}
			resultado += iterador;	
		}
		return resultado;
	}

	// public static void main(String[] args) {
	// String teste = gerarSenha();
	// }
}