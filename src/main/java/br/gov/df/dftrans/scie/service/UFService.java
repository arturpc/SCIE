package br.gov.df.dftrans.scie.service;

import java.util.ArrayList;
import java.util.List;
import javax.annotation.PostConstruct;
import javax.faces.bean.ApplicationScoped;
import javax.faces.bean.ManagedBean;

import br.gov.df.dftrans.scie.dao.UFDAO;
import br.gov.df.dftrans.scie.domain.UF;
import br.gov.df.dftrans.scie.exceptions.EntityNotFoundException;
import br.gov.df.dftrans.scie.exceptions.InsertException;
 
@ManagedBean(name="UFService", eager = true)
@ApplicationScoped
public class UFService {
     
    private List<UF> ufs;
     
    @PostConstruct
    public void init() {
        ufs = new ArrayList<UF>();
        UFDAO dao = UFDAO.UFDAO();
		ufs.addAll(dao.get());
    }
     
    public List<UF> getUFs() {
        return ufs;
    } 
    
   public UF getUfPorSigla(String sigla){
	   for(UF uf:ufs){
		   if(uf.getUf().equals(sigla)){
			   return uf;
		   }
	   }
	   return null;
   }
}
