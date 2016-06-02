package br.gov.df.dftrans.scie.view;
import java.util.ArrayList;
import java.util.List;
import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;

import br.gov.df.dftrans.scie.dao.CidadeDAO;
import br.gov.df.dftrans.scie.dao.UFDAO;
import br.gov.df.dftrans.scie.domain.Cidade;
import br.gov.df.dftrans.scie.domain.UF;
import br.gov.df.dftrans.scie.service.CidadeService;
import br.gov.df.dftrans.scie.service.UFService;
 
@ManagedBean
public class SelectOneMenu {
     
    private String option;
    private UFDAO ufdao = UFDAO.UFDAO();
    private CidadeDAO ciddao = CidadeDAO.CidadeDAO();
    private UF uf = ufdao.getByUF("DF"); 
    private List<UF> ufs = new ArrayList<UF>();
    private Cidade cidade = new Cidade(0,"Selecione uma Cidade", uf);
    private List<Cidade> cidades = new ArrayList<Cidade>();
     
    @ManagedProperty("#{UFService}")
    private UFService ufService;
    
    @ManagedProperty("#{CidadeService}")
    private CidadeService cidadeService;
     
    @PostConstruct
    public void init() {
        ufs.addAll(ufdao.get());
        cidades.addAll(ciddao.get());
    }
 
    public String getOption() {
        return option;
    }
 
    public void setOption(String option) {
        this.option = option;
    }
 
    public UF getUf() {
        return uf;
    }
 
    public void setUf(UF uf) {
        this.uf = uf;
    }
 
    public List<UF> getUfs() {
        return ufs;
    }
 
    public void setUfService(UFService ufService) {
        this.ufService = ufService;
    }
    
    public void setCidadeService(CidadeService cidadeService) {
        this.cidadeService = cidadeService;
    }

	public List<Cidade> getCidades() {
		return cidades;
	}

	public void setCidades(List<Cidade> cidades) {
		this.cidades = cidades;
	}

	public Cidade getCidade() {
		return cidade;
	}

	public void setCidade(Cidade cidade) {
		this.cidade = cidade;
	}
    
    
}