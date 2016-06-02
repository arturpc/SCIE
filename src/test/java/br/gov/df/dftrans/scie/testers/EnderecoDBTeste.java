<<<<<<< HEAD
package br.gov.df.dftrans.scie.testers;
import org.junit.Test;

import br.gov.df.dftrans.scie.dao.EnderecoDAO;
import br.gov.df.dftrans.scie.domain.Endereco;
import br.gov.df.dftrans.scie.exceptions.EntityNotFoundException;


public class EnderecoDBTeste {

	@Test
	public void testupdate() throws EntityNotFoundException{
		
		EnderecoDAO enddao = EnderecoDAO.EnderecoDAO();
		Endereco end = enddao.getByCodigo(1);
		end.setBairro("DOTA2");
		enddao.update(end);
	}
	
}
=======
package br.gov.df.dftrans.scie.testers;
import org.junit.Test;

import br.gov.df.dftrans.scie.dao.EnderecoDAO;
import br.gov.df.dftrans.scie.domain.Endereco;
import br.gov.df.dftrans.scie.exceptions.EntityNotFoundException;


public class EnderecoDBTeste {

	@Test
	public void testupdate() throws EntityNotFoundException{
		
		EnderecoDAO enddao = EnderecoDAO.enderecoDAO();
		Endereco end = enddao.getByCodigo(1);
		end.setBairro("DOTA2");
		enddao.update(end);
	}
	
}
>>>>>>> origin/master
