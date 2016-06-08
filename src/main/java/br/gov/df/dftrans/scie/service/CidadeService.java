package br.gov.df.dftrans.scie.service;

import java.util.ArrayList;
import java.util.List;

import javax.faces.bean.ApplicationScoped;
import javax.faces.bean.ManagedBean;

import br.gov.df.dftrans.scie.dao.CidadeDAO;
import br.gov.df.dftrans.scie.domain.Cidade;
import br.gov.df.dftrans.scie.domain.UF;


@ManagedBean(name="CidadeService", eager = true)
@ApplicationScoped
public class CidadeService {
     
    private List<Cidade> cidades;
    private UF df = new UF(1,"DF");
     
    public CidadeService() {
        cidades = new ArrayList<Cidade>();
        CidadeDAO ciddao = CidadeDAO.CidadeDAO();
		cidades.addAll(ciddao.get());
    }
     
    public List<Cidade> getCidades() {
        return cidades;
    }
    
    public Cidade getCidadePorNome(String nome){
    	for(Cidade cid: cidades){
    		if(cid.getNome().equals(nome)){
    			return cid;
    		}
    	}
    	return null;
    }
   
}