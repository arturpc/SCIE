package br.gov.df.dftrans.scie.beans;

import java.io.Serializable;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import javax.el.ELResolver;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.context.FacesContext;

import org.hibernate.validator.constraints.NotEmpty;
import org.hibernate.validator.constraints.br.CPF;


import br.gov.df.dftrans.scie.dao.CidadeDAO;
import br.gov.df.dftrans.scie.dao.EnderecoDAO;
import br.gov.df.dftrans.scie.dao.InstituicaoEnsinoDAO;
import br.gov.df.dftrans.scie.dao.RepresentanteDAO;
import br.gov.df.dftrans.scie.dao.UFDAO;
import br.gov.df.dftrans.scie.domain.Cidade;
import br.gov.df.dftrans.scie.domain.Endereco;
import br.gov.df.dftrans.scie.domain.InstituicaoEnsino;
import br.gov.df.dftrans.scie.domain.Representante;
import br.gov.df.dftrans.scie.domain.UF;
import br.gov.df.dftrans.scie.exceptions.EntityNotFoundException;
import br.gov.df.dftrans.scie.service.CidadeService;
import br.gov.df.dftrans.scie.utils.FacesUtil;
import br.gov.df.dftrans.scie.utils.Parametros;
import br.gov.df.dftrans.scie.view.CursoViewBean;
import br.gov.df.dftrans.scie.view.FileUploadView;

import javax.servlet.http.HttpSession;

@ManagedBean(name = "InstituicaoMB", eager = true)
@SessionScoped
public class InstituicaoBean implements Serializable {

	private int id;
	@CPF(message = "CPF informado inv�lido. Por favor informe um CPF v�lido")
	@NotEmpty(message = "CPF � um campo obrigat�rio!!!")
	private String codCPF, codCPFsemMascara;
	private InstituicaoEnsino instituicao;
	private Endereco endereco;
	private Cidade cidade;
	private UF uf;
	private RepresentanteDAO repdao = RepresentanteDAO.RepresentanteDAO();
	private InstituicaoEnsinoDAO instdao = InstituicaoEnsinoDAO.InstituicaoEnsinoDAO();
	private UFDAO ufdao = UFDAO.UFDAO();
	private CidadeDAO ciddao = CidadeDAO.CidadeDAO();
	private EnderecoDAO enddao = EnderecoDAO.EnderecoDAO();
	private Representante representante = null;
	private CursoViewBean cursoBean;
	private String[] contato = new String[5];
	private String contato1 = "";
	private String contato2 = "";
	private String contato3 = "";
	private String  contato4 = "";
	private String contato5 = "";
	private ArrayList<String> telefones;
	private String topMessage = Parametros.getParameter("atualizar_cadastro_top");
	private String obsMessage = Parametros.getParameter("atualizar_cadastro_obs");
	private boolean cpfEncontrado = false;
	private FileUploadView fileUploader;
	private String cep;
	private String bairro;
	private String logradouro;
	private String complemento;
	private Date date = Calendar.getInstance().getTime();
	private DateFormat fmt = new SimpleDateFormat("dd/MM/yyyy");
	private String hoje = fmt.format(date);

	
	public void init() {
		// DBService.init();
	}

	public List<InstituicaoEnsino> getAllInstituicoes() {
		return instdao.get();

	}

	public String pesquisar() {
		FacesContext context = FacesContext.getCurrentInstance();
		ELResolver resolver = context.getApplication().getELResolver();
		AutenticacaoBean meuBeanAut = (AutenticacaoBean) resolver.getValue(context.getELContext(), null, "loginBean");
		if (!meuBeanAut.isCaptcha()) {
			FacesUtil.addMsggError("Digite os n�meros no campo abaixo");
			return "/pages/instituicaoHome.xhtml?faces-redirect=false";
		}
		codCPFsemMascara = removeMascara(getCodCPF());
		try {
			setInstituicao(instdao.getById(getId()));
		} catch (EntityNotFoundException e) {
			e.printStackTrace();
		}
		if (instituicao == null) {
			setInstituicao(new InstituicaoEnsino());
			setEndereco(new Endereco());
			CidadeService meuBean = (CidadeService) resolver.getValue(context.getELContext(), null, "CidadeService");
			if (!meuBean.getCidades().isEmpty()) {
				setCidade(meuBean.getCidades().get(0));
			} else {
				setCidade(new Cidade());
				setUf(getCidade().getUf());
			}
			setRepresentante(new Representante(null, codCPFsemMascara, 1, null, null, null, getInstituicao()));
		} else {
			/*
			 * if (autdao.getByCpf(codCPFsemMascara, instituicao.getId()) ==
			 * null) { FacesUtil.addMsggError("O cpf " + codCPF +
			 * " n�o est� autorizado."); return
			 * "instituicaoHome.xhtml?faces-redirect=false"; }
			 */
			setEndereco(instituicao.getEndereco());
			setBairro(getEndereco().getBairro());
			setLogradouro(getEndereco().getLogradouro());
			setComplemento(getEndereco().getComplemento());
			setCidade(endereco.getCidade());
			setUf(getCidade().getUf());
			setCep(getEndereco().getCep());
			setRepresentante(repdao.getByCPF(codCPFsemMascara));
			if (getRepresentante() == null) {
				setRepresentante(new Representante(null, codCPFsemMascara, 1, null, null, null, getInstituicao()));
			}
			// instituicao.setRepresentante(getRepresentante());
		}
		if (!getRepresentante().getTelefone().isEmpty()) {
			String[] contatos = new String[5];
			for (int i = 0; i < getRepresentante().getTelefone().toArray().length; i++) {
				contatos[i] = (String) getRepresentante().getTelefone().toArray()[i];
			}
			setContato(contatos);
			setContato1(getContato()[0]);
			setContato2(getContato()[1]);
			setContato3(getContato()[2]);
			setContato4(getContato()[3]);
			setContato5(getContato()[4]);
		}
		HttpSession sessao = (HttpSession) FacesContext.getCurrentInstance().getExternalContext().getSession(false);
		sessao.setAttribute("instituicao", getInstituicao());
		sessao.setAttribute("representante", getRepresentante());
		return "/pages/instituicao/atualizarCadastro.xhtml?faces-redirect=true";
	}

	public String atualizar() {
		/*
		 * if
		 * (!ValidadorCEP.existeCEP(removeMascara(getInstituicao().getEndereco()
		 * .getCep()))) { FacesUtil.addMsggError("Cep informado inv�lido");
		 * return
		 * "/pages/instituicao/atualizarCadastro.xhtml?faces-redirect=false"; }
		 */
		InstituicaoEnsino inst = getInstituicao();
		inst.setCnpj(removeMascara(getInstituicao().getCnpj()));
		getEndereco().setCep(getCep());
		getEndereco().setBairro(getBairro());
		getEndereco().setCidade(getCidade());
		getEndereco().setLogradouro(getLogradouro());
		getEndereco().setComplemento(getComplemento());
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
			if (!"".equals(s)) {
				telefones.add(s);
			}
		}
		rep.setTelefone(telefones);
		inst.setEndereco(end);
		if (getRepresentante().getCadastro() != 1
				&& getRepresentante().getInstituicao().getId() == getInstituicao().getId()) {
			rep.setInstituicao(inst);
			setInstituicao(instdao.update(inst));
		} else {
			rep.setInstituicao(getInstituicao());
		}
		HttpSession session = (HttpSession) FacesContext.getCurrentInstance().getExternalContext().getSession(false);
		session.setAttribute("instituicao", instituicao);
		FacesContext context = FacesContext.getCurrentInstance();
		ELResolver resolver = context.getApplication().getELResolver();
		fileUploader = (FileUploadView) resolver.getValue(context.getELContext(), null, "fileUploadView");
		fileUploader.setInst(getInstituicao());
		cursoBean = (CursoViewBean) resolver.getValue(context.getELContext(), null, "MBCursoView");
		cursoBean.setInstituicao(getInstituicao());
		cursoBean.updateTarget();
		if(getEditavel()){
			getRepresentante().setCadastro(1);
		}
		setRepresentante(repdao.update(getRepresentante()));
		HttpSession sessao = (HttpSession) FacesContext.getCurrentInstance().getExternalContext().getSession(false);
		sessao.setAttribute("instituicao", getInstituicao());
		sessao.setAttribute("representante", getRepresentante());
		return "/pages/instituicao/arquivosCadastro.xhtml?faces-redirect=true";
	}

	public InstituicaoEnsino getInstituicao() {
		return instituicao;
	}

	public void setInstituicao(InstituicaoEnsino instituicao) {
		this.instituicao = instituicao;
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
			if (getInstituicao().getEndereco() != null) {
				getInstituicao().setEndereco(enddao.getByCEP(removeMascara(cep)));
				
			}
			if(getInstituicao().getEndereco() == null){
				getInstituicao().setEndereco(new Endereco());
				getInstituicao().getEndereco().setCep(removeMascara(cep));
			}
			if(getInstituicao().getEndereco().getBairro() != null){
				setBairro(getInstituicao().getEndereco().getBairro());
			}if(getInstituicao().getEndereco().getLogradouro() != null){
				setLogradouro(getInstituicao().getEndereco().getLogradouro());
			}
			setComplemento("");
			Cidade cid;
			if(getInstituicao().getEndereco().getCidade() != null){
				cid = ciddao.get(getInstituicao().getEndereco().getCidade().getNome(),
					getInstituicao().getEndereco().getCidade().getUf().getUf());
				if (cid != null) {
					setCidade(ciddao.get(getInstituicao().getEndereco().getCidade().getNome(),
							getInstituicao().getEndereco().getCidade().getUf().getUf()));
				}
			} else {
				cid = ciddao.get("BRASILIA","DF");
			}
			
		}
	}

	public boolean getEditavel() {
		if (getRepresentante().getCadastro() != 1
				&& getRepresentante().getInstituicao().getId() == getInstituicao().getId()) {
			return false;
		} else {
			return true;
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

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getHoje() {
		return hoje;
	}

	public void setHoje(String hoje) {
		this.hoje = hoje;
	}

}
