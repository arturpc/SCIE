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
    
    /**
     * M�todo que seta as vari�veis e estruturas da classe de servi�o UF
     */
    @PostConstruct
    public void init() {
        ufs = new ArrayList<UF>();
        UFDAO dao = UFDAO.UFDAO();
		ufs.addAll(dao.get());
    }
     
    public List<UF> getUFs() {
        return ufs;
    } 
    
    /**
     * M�todo que retorna determinada UF dada a sigla da UF
     * @param sigla
     * @return
     */
   public UF getUfPorSigla(String sigla){
	   for(UF uf:ufs){
		   if(uf.getUf().equals(sigla)){
			   return uf;
		   }
	   }
	   return null;
   }
}