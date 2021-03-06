package br.gov.df.dftrans.scie.view;

import java.util.List;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.context.FacesContext;
import javax.servlet.http.HttpSession;

import br.gov.df.dftrans.scie.dao.InstituicaoEnsinoDAO;
import br.gov.df.dftrans.scie.dao.LogDAO;
import br.gov.df.dftrans.scie.domain.InstituicaoEnsino;
import br.gov.df.dftrans.scie.domain.LogValidacaoCadastro;
import br.gov.df.dftrans.scie.domain.Representante;
import br.gov.df.dftrans.scie.domain.Usuario;
import br.gov.df.dftrans.scie.exceptions.EntityNotFoundException;

@ManagedBean(name = "instIndexMB", eager = true)
@SessionScoped
public class InstituicaoIndexMB {

	private List<LogValidacaoCadastro> abertas;
	private List<LogValidacaoCadastro> aprovadas;
	private List<LogValidacaoCadastro> reprovadas;
	private List<LogValidacaoCadastro> emAnalise;
	private InstituicaoEnsino instituicao;
	private Usuario user;
	private String[] validacao = { "Pendente de Verifica��o", "Em An�lise",
            "Aprovado", "N�o Aprovado" };
	private LogDAO logdao = LogDAO.LogDAO();
	private InstituicaoEnsinoDAO instdao = InstituicaoEnsinoDAO.InstituicaoEnsinoDAO();
	private Representante representante;
	
	public InstituicaoIndexMB(){
		HttpSession session = (HttpSession) FacesContext
				.getCurrentInstance().getExternalContext().getSession(false);
		setRepresentante((Representante) session.getAttribute("representante"));
	}
	
	/**
	 * seta instituicao com a istituicao do usuario na sess�o seleciona todos os
	 * status em diferentes lists <abertas,aprovadas,reprovadas,emAnalise>
	 */
	public void init() {
		HttpSession session = (HttpSession) FacesContext
				.getCurrentInstance().getExternalContext().getSession(false);
		setUser((Usuario) session.getAttribute("usuario"));
		setRepresentante((Representante) session.getAttribute("representante"));
		setInstituicao(getRepresentante().getInstituicao());
		session.setAttribute("instituicao", getInstituicao());
		try {
			setAbertas(logdao.getOpensRep(getRepresentante()));
			setEmAnalise(logdao.getAnalisysRep(getRepresentante()));
			setAprovadas(logdao.getAprovedRep(getRepresentante()));
			setReprovadas(logdao.getRejectRep(getRepresentante()));
		} catch (EntityNotFoundException e) {
			e.printStackTrace();
		}
	}

	/**
	 * M�todo que retorna se determinado cadastro est� aprovado
	 * @return st_solicitacao = 2
	 */
	public boolean cadastroValido() {
		if (getInstituicao().getSituacao() == 2) {
			return true;
		} else {
			return false;
		}
	}

	// getteres and setteres

	public List<LogValidacaoCadastro> getAbertas() {
		return abertas;
	}

	public void setAbertas(List<LogValidacaoCadastro> abertas) {
		this.abertas = abertas;
	}

	public InstituicaoEnsino getInstituicao() {
		return instituicao;
	}

	public void setInstituicao(InstituicaoEnsino instituicao) {
		this.instituicao = instituicao;
	}

	public Usuario getUser() {
		return user;
	}

	public void setUser(Usuario user) {
		this.user = user;
	}

	public List<LogValidacaoCadastro> getAprovadas() {
		return aprovadas;
	}

	public void setAprovadas(List<LogValidacaoCadastro> aprovadas) {
		this.aprovadas = aprovadas;
	}

	public List<LogValidacaoCadastro> getReprovadas() {
		return reprovadas;
	}

	public void setReprovadas(List<LogValidacaoCadastro> reprovadas) {
		this.reprovadas = reprovadas;
	}

	public List<LogValidacaoCadastro> getEmAnalise() {
		return emAnalise;
	}

	public void setEmAnalise(List<LogValidacaoCadastro> emAnalise) {
		this.emAnalise = emAnalise;
	}

	public String[] getValidacao() {
		return validacao;
	}

	public void setValidacao(String[] validacao) {
		this.validacao = validacao;
	}

	public Representante getRepresentante() {
		return representante;
	}

	public void setRepresentante(Representante representante) {
		this.representante = representante;
	}
}