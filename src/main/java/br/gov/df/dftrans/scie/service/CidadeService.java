package br.gov.df.dftrans.scie.service;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.faces.bean.ApplicationScoped;
import javax.faces.bean.ManagedBean;

import br.gov.df.dftrans.scie.dao.CidadeDAO;
import br.gov.df.dftrans.scie.domain.Cidade;


@ManagedBean(name="CidadeService", eager = true)
@ApplicationScoped
public class CidadeService implements Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private List<Cidade> cidades;
	public CidadeService() {
	}
    public void init() {
        cidades = new ArrayList<Cidade>();
        CidadeDAO ciddao = CidadeDAO.cidadeDAO();
		cidades.addAll(ciddao.get());
    }
     
    public List<Cidade> getCidades() {
        return cidades;
    }
    
    public Cidade getCidadePorNome(String nome){
    	Cidade cidade = null;
    	for(Cidade cid: cidades){
    		if(cid.getNome().equals(nome)){
    			cidade = cid;
    			//return cid;
    			break;
    		}
    	}
    	return cidade;
    }
   
}
