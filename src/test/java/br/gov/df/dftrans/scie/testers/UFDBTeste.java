package br.gov.df.dftrans.scie.testers;
import java.util.ArrayList;
import java.util.List;

import org.junit.Ignore;
import org.junit.Test;

import br.gov.df.dftrans.scie.dao.UFDAO;
import br.gov.df.dftrans.scie.domain.UF;
import br.gov.df.dftrans.scie.exceptions.EntityNotFoundException;
import br.gov.df.dftrans.scie.exceptions.InsertException;

public class UFDBTeste {
	
	public UFDBTeste(){
		
	}
	
	@Test
	@Ignore
	public void testAddAll(){
        List<UF> ufs = new ArrayList<UF>();
        UFDAO ufDao = UFDAO.UFDAO();
        try {
            ufs.add(new UF("AC"));
            ufs.add(new UF("AL"));
            ufs.add(new UF("AP"));
            ufs.add(new UF("AM"));
            ufs.add(new UF("BA"));
            ufs.add(new UF("CE"));
            ufs.add(new UF("ES"));
            ufs.add(new UF("MA"));
            ufs.add(new UF("MT"));
            ufs.add(new UF("MS"));
            ufs.add(new UF("MG"));
            ufs.add(new UF("PA"));
            ufs.add(new UF("PB"));
            ufs.add(new UF("PR"));
            ufs.add(new UF("PE"));
            ufs.add(new UF("PI"));
            ufs.add(new UF("RJ"));
            ufs.add(new UF("RN"));
            ufs.add(new UF("RS"));
            ufs.add(new UF("RO"));
            ufs.add(new UF("RR"));
            ufs.add(new UF("SC"));
            ufs.add(new UF("SP"));
            ufs.add(new UF("SE"));
            ufs.add(new UF("TO"));
			ufDao.add(ufs);
		} catch (InsertException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (EntityNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	@Test
	public void testAdd(){
		UF uf = new UF();
		UFDAO ufdao = UFDAO.UFDAO();
		
		uf.setUf("DF");
		try {
			ufdao.add(uf);
		} catch (InsertException e2) {
			// TODO Auto-generated catch block
			e2.printStackTrace();
		}

	}
	

}