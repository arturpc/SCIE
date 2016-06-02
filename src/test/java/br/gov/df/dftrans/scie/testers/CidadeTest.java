<<<<<<< HEAD
package br.gov.df.dftrans.scie.testers;
import java.util.ArrayList;
import java.util.List;

import org.junit.Test;

import br.gov.df.dftrans.scie.dao.CidadeDAO;
import br.gov.df.dftrans.scie.domain.Cidade;
import br.gov.df.dftrans.scie.domain.UF;
import br.gov.df.dftrans.scie.exceptions.EntityNotFoundException;
import br.gov.df.dftrans.scie.exceptions.InsertException;

public class CidadeTest {
	@Test
	public void testAddAll(){
        List<Cidade> cidades = new ArrayList<Cidade>();
        UF df = new UF(1, "DF");
        CidadeDAO cidDao = CidadeDAO.CidadeDAO();
        cidades.add(new Cidade("SUDOESTE", df));
        cidades.add(new Cidade("GAMA", df));
        cidades.add(new Cidade("RECANTO DAS EMAS", df));
        cidades.add(new Cidade("TAGUATINGA", df));
        cidades.add(new Cidade("GUARA", df));
        cidades.add(new Cidade("NUCLEO BANDEIRANTE", df));
        cidades.add(new Cidade("CRUZEIRO", df));
        try {
			cidDao.add(cidades);
		} catch (InsertException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (EntityNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
}
=======
package br.gov.df.dftrans.scie.testers;
import java.util.ArrayList;
import java.util.List;

import org.junit.Test;

import br.gov.df.dftrans.scie.dao.CidadeDAO;
import br.gov.df.dftrans.scie.domain.Cidade;
import br.gov.df.dftrans.scie.domain.UF;
import br.gov.df.dftrans.scie.exceptions.EntityNotFoundException;
import br.gov.df.dftrans.scie.exceptions.InsertException;

public class CidadeTest {
	@Test
	public void testAddAll(){
        List<Cidade> cidades = new ArrayList<Cidade>();
        UF df = new UF(1, "DF");
        CidadeDAO cidDao = CidadeDAO.cidadeDAO();
        cidades.add(new Cidade("SUDOESTE", df));
        cidades.add(new Cidade("GAMA", df));
        cidades.add(new Cidade("RECANTO DAS EMAS", df));
        cidades.add(new Cidade("TAGUATINGA", df));
        cidades.add(new Cidade("GUARA", df));
        cidades.add(new Cidade("NUCLEO BANDEIRANTE", df));
        cidades.add(new Cidade("CRUZEIRO", df));
        try {
			cidDao.add(cidades);
		} catch (InsertException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (EntityNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
}
>>>>>>> origin/master
