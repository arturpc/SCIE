package br.gov.df.dftrans.scie.service;

import java.util.ArrayList;
import java.util.List;
import javax.annotation.PostConstruct;
import javax.faces.bean.ApplicationScoped;
import javax.faces.bean.ManagedBean;

import br.gov.df.dftrans.scie.dao.UFDAO;
import br.gov.df.dftrans.scie.domain.UF;
 
@ManagedBean(name="UFService", eager = true)
@ApplicationScoped
public class UFService {
	private List<UF> ufs;

	public UFService() {
	}
     
    @PostConstruct
    public void init() {
        ufs = new ArrayList<UF>();
        UFDAO dao = UFDAO.uFDAO();
		ufs.addAll(dao.get());
    }
     
    public List<UF> getUFs() {
        return ufs;
    } 
    
   public UF getUfPorSigla(String sigla){
	  UF ret = null;
	   for(UF uf:ufs){
		   if(uf.getUf().equals(sigla)){
			   ret = uf;
			   break;
			   //return uf;
		   }
	   }
	   return ret;
   }
}
