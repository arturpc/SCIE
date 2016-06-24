package br.gov.df.dftrans.scie.beans;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Locale;
import java.util.Scanner;

import javax.el.ELResolver;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.validator.ValidatorException;

import org.hibernate.validator.constraints.NotEmpty;
import org.hibernate.validator.constraints.br.CPF;

import br.gov.df.dftrans.scie.dao.AutorizacaoRepresentanteDAO;
import br.gov.df.dftrans.scie.dao.CidadeDAO;
import br.gov.df.dftrans.scie.dao.EnderecoDAO;
import br.gov.df.dftrans.scie.dao.InstituicaoEnsinoDAO;
import br.gov.df.dftrans.scie.dao.RepresentanteDAO;
import br.gov.df.dftrans.scie.dao.UFDAO;
import br.gov.df.dftrans.scie.domain.AutorizacaoRepresentante;
import br.gov.df.dftrans.scie.domain.CEP;
import br.gov.df.dftrans.scie.domain.Cidade;
import br.gov.df.dftrans.scie.domain.Endereco;
import br.gov.df.dftrans.scie.domain.InstituicaoEnsino;
import br.gov.df.dftrans.scie.domain.Representante;
import br.gov.df.dftrans.scie.domain.UF;
import br.gov.df.dftrans.scie.exceptions.DAOExcpetion;
import br.gov.df.dftrans.scie.exceptions.EntityNotFoundException;
import br.gov.df.dftrans.scie.service.CidadeService;
import br.gov.df.dftrans.scie.utils.FacesUtil;
import br.gov.df.dftrans.scie.utils.Parametros;
import br.gov.df.dftrans.scie.utils.ValidadorCEP;
import br.gov.df.dftrans.scie.view.CursoViewBean;
import br.gov.df.dftrans.scie.view.FileUploadView;

import javax.servlet.http.HttpSession;

@ManagedBean(name = "InstituicaoMB", eager = true)
@SessionScoped
public class InstituicaoBean implements Serializable {

	private String codInepEmec;
	@CPF(message = "CPF informado inválido. Por favor informe um CPF válido")
	@NotEmpty(message = "CPF é um campo obrigatório!!!")
	private String codCPF, codCPFsemMascara;
	private InstituicaoEnsino instituicao;
	private Endereco endereco;
	private Cidade cidade;
	private UF uf;
	private InstituicaoEnsinoDAO instdao = InstituicaoEnsinoDAO.InstituicaoEnsinoDAO();
	private AutorizacaoRepresentanteDAO autdao = 
			AutorizacaoRepresentanteDAO.AutorizacaoRepresentanteDAO();
	private UFDAO ufdao = UFDAO.UFDAO();
	private CidadeDAO ciddao = CidadeDAO.CidadeDAO();
	private EnderecoDAO enddao = EnderecoDAO.EnderecoDAO();
	private Representante representante = null;
	private CursoViewBean cursoBean;
	private String[] contato = new String[5];
	private String contato1 = "", contato2 = "", contato3 = "", contato4 = "", contato5 = "";
	private ArrayList<String> telefones;
	private String topMessage = Parametros.getParameter("atualizar_cadastro_top");
	private String obsMessage = Parametros.getParameter("atualizar_cadastro_obs");
	private boolean cpfEncontrado = false;
	private FileUploadView fileUploader;
	private String cep;
	private String bairro;
	private String logradouro;
	private String complemento;

	public void init() {
		// DBService.init();
	}

	public String pesquisar() {
		FacesContext context = FacesContext.getCurrentInstance();
		ELResolver resolver = context.getApplication().getELResolver();
		AutenticacaoBean meuBeanAut = (AutenticacaoBean) resolver
				.getValue(context.getELContext(), null, "loginBean");
		if (!meuBeanAut.isCaptcha()) {
			FacesUtil.addMsggError("Digite os números no campo abaixo");
			return "/pages/instituicaoHome.xhtml?faces-redirect=false";
		}
		codCPFsemMascara = removeMascara(getCodCPF());
		if (getCodInepEmec().equals("")) {
			try {
				setInstituicao(getAutDAO()
						.getByCpf(codCPFsemMascara).getInstituicao());
			} catch (DAOExcpetion e) {
				FacesUtil.addMsggError("O cpf " + codCPF + " não está autorizado.");
				return "/pages/instituicaoHome.xhtml?faces-redirect=false";
			}
		} else {
			setInstituicao(instdao.getByInepEmec(codInepEmec));
		}
		if (instituicao == null) {
			setInstituicao(new InstituicaoEnsino());
			instituicao.setCodInepEmec(codInepEmec);
			setEndereco(new Endereco());
			CidadeService meuBean = (CidadeService) resolver
					.getValue(context.getELContext(), null, "CidadeService");
			if (!meuBean.getCidades().isEmpty()) {
				setCidade(meuBean.getCidades().get(0));
			} else {
				setCidade(new Cidade());
				setUf(getCidade().getUf());
			}
			setRepresentante(new Representante(null, codCPFsemMascara, 1, null, null));
			instituicao.setRepresentante(getRepresentante());
		} else {
			if (autdao.getByCpf(codCPFsemMascara, instituicao.getId()) == null) {
				FacesUtil.addMsggError("O cpf " + codCPF + " não está autorizado.");
				return "instituicaoHome.xhtml?faces-redirect=false";
			}
			setEndereco(instituicao.getEndereco());
			setBairro(getEndereco().getBairro());
			setLogradouro(getEndereco().getLogradouro());
			setComplemento(getEndereco().getComplemento());
			setCidade(endereco.getCidade());
			setUf(getCidade().getUf());
			setCep(getEndereco().getCep());
			RepresentanteDAO repDAO = RepresentanteDAO.RepresentanteDAO();
			setRepresentante(repDAO.getByCPF(codCPFsemMascara));
			if (getRepresentante() == null) {
				setRepresentante(new Representante(null, 
						codCPFsemMascara, 1, null, null));
			}
			instituicao.setRepresentante(getRepresentante());
		}
		if (!getRepresentante().getTelefone().isEmpty()) {
			String[] contatos = new String[5];
			for (int i = 0; i < getRepresentante().getTelefone()
					.toArray().length; i++) {
				contatos[i] = (String) getRepresentante()
						.getTelefone().toArray()[i];
			}
			setContato(contatos);
			setContato1(getContato()[0]);
			setContato2(getContato()[1]);
			setContato3(getContato()[2]);
			setContato4(getContato()[3]);
			setContato5(getContato()[4]);
		}
		HttpSession sessao = (HttpSession) FacesContext
				.getCurrentInstance().getExternalContext().getSession(false);
		sessao.setAttribute("instituicao", getInstituicao());
		return "/pages/instituicao/atualizarCadastro.xhtml?faces-redirect=true";
	}

	public String atualizar() {
		/*
		 * if
		 * (!ValidadorCEP.existeCEP(removeMascara(getInstituicao().getEndereco()
		 * .getCep()))) { FacesUtil.addMsggError("Cep informado inválido");
		 * return
		 * "/pages/instituicao/atualizarCadastro.xhtml?faces-redirect=false"; }
		 */
		InstituicaoEnsino inst = getInstituicao();
		inst.setCnpj(removeMascara(inst.getCnpj()));
		Endereco end = getEndereco();
		if (getCidade() == null) {
			setCidade(new Cidade());
			setUf(ufdao.getByUF("DF"));
			getCidade().setUf(getUf());
		}
		Cidade cid = getCidade();
		end.setCidade(cid);
		Representante rep = getRepresentante();
		rep.setCpf(removeMascara(rep.getCpf()));
		telefones = new ArrayList<String>();
		setContato();
		for (String s : getContato()) {
			if (!"".equals(s)){
				telefones.add(s);	
			}
		}
		rep.setTelefone(telefones);
		inst.setRepresentante(rep);
		inst.setEndereco(end);
		setInstituicao(instdao.update(inst));
		HttpSession session = (HttpSession) FacesContext
				.getCurrentInstance().getExternalContext().getSession(false);
		session.setAttribute("instituicao", instituicao);
		FacesContext context = FacesContext.getCurrentInstance();
		ELResolver resolver = context.getApplication().getELResolver();
		fileUploader = (FileUploadView) resolver.
				getValue(context.getELContext(), null, "fileUploadView");
		fileUploader.setInst(instituicao);
		cursoBean = (CursoViewBean) resolver.getValue(
				context.getELContext(), null, "MBCursoView");
		cursoBean.setInstituicao(instituicao);
		cursoBean.updateTarget();
		AutorizacaoRepresentante autrep = autdao.getByCpfInstNull(
				getInstituicao().getRepresentante().getCpf());
		if (autrep != null) {
			autrep.setInstituicao(getInstituicao());
			autdao.update(autrep);
		}
		return "/pages/instituicao/arquivosCadastro.xhtml?faces-redirect=true";
	}

	public InstituicaoEnsino getInstituicao() {
		return instituicao;
	}

	public void setInstituicao(InstituicaoEnsino instituicao) {
		this.instituicao = instituicao;
	}

	public String getCodInepEmec() {
		return codInepEmec;
	}

	public void setCodInepEmec(String codInepEmec) {
		this.codInepEmec = codInepEmec;
	}

	public InstituicaoEnsinoDAO getInstDao() {
		return instdao;
	}

	public void setInstDao(InstituicaoEnsinoDAO dao) {
		this.instdao = dao;
	}

	public String getCodCPF() {
		return codCPF;
	}

	public void setCodCPF(String codCPF) {
		this.codCPF = codCPF;
	}

	public Endereco getEndereco() {
		return endereco;
	}

	public void setEndereco(Endereco endereco) {
		this.endereco = endereco;
	}

	public Cidade getCidade() {
		return cidade;
	}

	public void setCidade(Cidade cidade) {
		this.cidade = cidade;
	}

	public UF getUf() {
		return uf;
	}

	public void setUf(UF uf) {
		this.uf = uf;
	}

	public AutorizacaoRepresentanteDAO getAutDAO() {
		return autdao;
	}

	public void setAutDAO(AutorizacaoRepresentanteDAO autDAO) {
		this.autdao = autDAO;
	}

	public Representante getRepresentante() {
		return representante;
	}

	public void setRepresentante(Representante representante) {
		this.representante = representante;
	}

	public String getTopMessage() {
		return topMessage;
	}

	public void setTopMessage(String topMessage) {
		this.topMessage = topMessage;
	}

	public String getObsMessage() {
		return obsMessage;
	}

	public void setObsMessage(String obsMessage) {
		this.obsMessage = obsMessage;
	}

	public boolean isCpfEncontrado() {
		return cpfEncontrado;
	}

	public void setCpfEncontrado(boolean cpfEncontrado) {
		this.cpfEncontrado = cpfEncontrado;
	}

	private String removeMascara(String campo) {
		String resultado = campo;
		resultado = resultado.replace("-", "");
		resultado = resultado.replace(".", "");
		resultado = resultado.replace("(", "");
		resultado = resultado.replace(")", "");
		resultado = resultado.replace(" ", "");
		resultado = resultado.replace("/", "");
		resultado = resultado.replace("_", "");
		return resultado;
	}

	public void consultarCEP() {
		if (!removeMascara(cep).isEmpty() && removeMascara(cep).length() == 8) {
			if (getInstituicao() == null) {
				setInstituicao(new InstituicaoEnsino());
			}
			if (getInstituicao().getEndereco() == null) {
				getInstituicao().setEndereco(new Endereco());
				getInstituicao().getEndereco().setCep(removeMascara(cep));
			} else {
				getInstituicao().setEndereco(enddao.getByCEP(removeMascara(cep)));
			}
			setBairro(getInstituicao().getEndereco().getBairro());
			setLogradouro(getInstituicao().getEndereco().getLogradouro());
			setComplemento("");
			Cidade cid = ciddao.get(getInstituicao().getEndereco()
					.getCidade().getNome(),
					getInstituicao().getEndereco().getCidade().getUf().getUf());
			if (cid != null) {
				setCidade(ciddao.get(getInstituicao().getEndereco()
						.getCidade().getNome(),getInstituicao()
						.getEndereco().getCidade().getUf()
						.getUf()));
			}
			/*String cep = removeMascara(getInstituicao().getEndereco().getCep());
			CEP c = ValidadorCEP.getEndereco(cep);
			if (c != null) {
				setBairro(c.getBairro());
				getInstituicao().getEndereco().setBairro(c.getBairro());
				getEndereco().setBairro(c.getBairro());
				setLogradouro(c.getLogradouro());
				getInstituicao().getEndereco().setLogradouro(c.getLogradouro());
				getEndereco().setLogradouro(c.getLogradouro());
				setComplemento("");
				Cidade cid = ciddao.get(c.getCidade(), c.getUf());
				if (cid != null) {
					setCidade(ciddao.get(c.getCidade(), c.getUf()));
				}
			}
			*/
		}
	}

	public String[] getContato() {
		return contato;
	}

	public void setContato() {
		this.contato[0] = removeMascara(getContato1());
		this.contato[1] = removeMascara(getContato2());
		this.contato[2] = removeMascara(getContato3());
		this.contato[3] = removeMascara(getContato4());
		this.contato[4] = removeMascara(getContato5());
	}

	public void setContato(String[] contato) {
		this.contato = contato;
	}

	public String getContato1() {
		return contato1;
	}

	public void setContato1(String contato1) {
		this.contato1 = contato1;
	}

	public String getContato2() {
		return contato2;
	}

	public void setContato2(String contato2) {
		this.contato2 = contato2;
	}

	public String getContato3() {
		return contato3;
	}

	public void setContato3(String contato3) {
		this.contato3 = contato3;
	}

	public String getContato4() {
		return contato4;
	}

	public void setContato4(String contato4) {
		this.contato4 = contato4;
	}

	public String getContato5() {
		return contato5;
	}

	public void setContato5(String contato5) {
		this.contato5 = contato5;
	}

	public String getCep() {
		return cep;
	}

	public void setCep(String cep) {
		this.cep = cep;
	}

	public String getBairro() {
		return bairro;
	}

	public void setBairro(String bairro) {
		this.bairro = bairro;
	}

	public String getLogradouro() {
		return logradouro;
	}

	public void setLogradouro(String logradouro) {
		this.logradouro = logradouro;
	}

	public String getComplemento() {
		return complemento;
	}

	public void setComplemento(String complemento) {
		this.complemento = complemento;
	}

}
