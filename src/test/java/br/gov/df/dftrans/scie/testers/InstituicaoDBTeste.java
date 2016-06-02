package br.gov.df.dftrans.scie.testers;
import org.junit.Ignore;
import org.junit.Test;

import br.gov.df.dftrans.scie.dao.InstituicaoEnsinoDAO;
import br.gov.df.dftrans.scie.domain.InstituicaoEnsino;
import br.gov.df.dftrans.scie.domain.UF;
import br.gov.df.dftrans.scie.exceptions.InsertException;

public class InstituicaoDBTeste {
	@Test
	@Ignore
	public void getAll(){
		InstituicaoEnsino inst = new InstituicaoEnsino();
		InstituicaoEnsinoDAO dao = new InstituicaoEnsinoDAO();
		//inst = dao.consultarPorEmec("9");
	}
	
	@Test
	@Ignore
	public void getByImec(){
		InstituicaoEnsino inst = new InstituicaoEnsino();
		InstituicaoEnsinoDAO dao = new InstituicaoEnsinoDAO();
		inst = dao.getByInepEmec("9");
		System.out.println(inst.getCnpj());
	}
	
	@Test
	@Ignore
	public void salvarInstituicao() throws InsertException{
		InstituicaoEnsino inst = new InstituicaoEnsino();
		InstituicaoEnsinoDAO dao = new InstituicaoEnsinoDAO();
		inst.setCnpj("1");
		inst.setCodInepEmec("8");
		dao.add(inst);
	}
	
	@Test
	public void testupdate(){
		InstituicaoEnsinoDAO dao = new InstituicaoEnsinoDAO();
		InstituicaoEnsino inst = dao.getByInepEmec("9");
		inst.setCnpj("66666666666666");
		inst.setNomeInstituicao("SESI");
		inst.getEndereco().getCidade().setUf(new UF(0,"DF"));
		dao.update(inst);
		System.out.println(inst.getEndereco().getCidade().getUf().getUf());
		inst.getEndereco().getCidade().setUf(new UF(4,"AP"));
		System.out.println(inst.getEndereco().getCidade().getUf().getUf());
	}
}
