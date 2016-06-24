package br.gov.df.dftrans.scie.view;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.context.FacesContext;

import org.primefaces.event.TransferEvent;
import org.primefaces.model.DualListModel;

import br.gov.df.dftrans.scie.dao.CursoDAO;
import br.gov.df.dftrans.scie.dao.InstituicaoCursoDAO;
import br.gov.df.dftrans.scie.domain.Curso;
import br.gov.df.dftrans.scie.domain.InstituicaoCurso;
import br.gov.df.dftrans.scie.domain.InstituicaoEnsino;
import br.gov.df.dftrans.scie.exceptions.EntityNotFoundException;
import br.gov.df.dftrans.scie.exceptions.InsertException;

@ManagedBean(name = "MBCursoView")
@SessionScoped
public class CursoViewBean implements Serializable {

	private DualListModel<Curso> dualListCurso;
	private String curso;
	private List<InstituicaoCurso> cadastrados;
	private List<Curso> cursos, target, cadastroAnterior;
	private List<Curso> cursosMomento;
	private InstituicaoEnsino instituicao;
	private InstituicaoCurso instCurso = new InstituicaoCurso();
	private InstituicaoCursoDAO instdao = InstituicaoCursoDAO.InstituicaoCursoDAO();
	private Boolean detalharCursos = new Boolean(false), fimCadastro = new Boolean(false);

	/**
	 * Inicia variáveis
	 */
	public void init() {
		CursoDAO dao = CursoDAO.CursoDAO();
		setCursosMomento(dao.getAll());
		setTarget(new ArrayList<Curso>());
		setCursos(new ArrayList<Curso>());
		setDualListCurso(new DualListModel<Curso>(getCursosMomento(), getTarget()));
		setDetalharCursos(new Boolean(false));
		setCadastroAnterior(getTarget());
		updateTarget();
	}

	/**
	 * Verifica se o detalhamento do curso foi finalizado e tinha cursos
	 * (target)
	 * 
	 * @return
	 */
	public String finalizar() {
		if (getDetalharCursos() && !getTarget().isEmpty()) {
			FacesMessage msg = new FacesMessage("É preciso finalizar "
					+ "o detalhamento de cursos!");
			msg.setSeverity(FacesMessage.SEVERITY_INFO);
			FacesContext.getCurrentInstance().addMessage("Cursos não detalhados!", msg);
			return "/pages/instituicao/cadastrarCursos.xhtml?faces-redirect=false";
		} else {
			return "/pages/instituicao/confirmacaoCadastro.xhtml?faces-redirect=true";
		}
	}

	/**
	 * Constrói saída de cursos para ser apresentado na tela
	 */
	public void detalharCurso() {
		if (!getDetalharCursos()) {
			// pega os cursos do listDataModel
			setTarget(getDualListCurso().getTarget());
			try {
				// seleciona os cursos pelo id da instituicao
				List<Curso> cursos = instdao.getCursos(getInstituicao().getId());
				while (!cursos.isEmpty()) {
					for (Curso c : getTarget()) {
						// tira duplicidade de cursos
						if (cursos.contains(c)) {
							getTarget().remove(c);
							cursos.remove(c);
							break;
						}
					}
				}
			} catch (EntityNotFoundException e) {
				e.printStackTrace();
			}
		}
		// se ainda tiver cursos
		if (!getTarget().isEmpty()) {
			// seta a instituição curso com o primeiro curso encontrado
			setInstCurso(instdao.get(instituicao, getTarget().get(0)));
			if (getInstCurso() == null) {
				setInstCurso(new InstituicaoCurso(getInstituicao(), 
						getTarget().get(0)));
			}
		}
		// seta detalhar curso para true
		setDetalharCursos(new Boolean(true));
		// seta o listDataModel
		setDualListCurso(new DualListModel<Curso>(getCursosMomento(), getTarget()));
	}

	/**
	 * Cadastra InstituicaoCurso
	 */
	public void cadastrarCurso() {
		try {
			for (int i = 1; i <= getInstCurso().getAno(); i++) {
				InstituicaoCurso inst = new InstituicaoCurso();
				inst.setCurso(getInstCurso().getCurso());
				inst.setAno(i);
				inst.setInicio(getInstCurso().getInicio());
				inst.setInstituicao(getInstCurso().getInstituicao());
				inst.setTurno(getInstCurso().getTurno());
				instdao.add(inst);
			}
		} catch (InsertException e) {
			e.printStackTrace();
		}
		getTarget().remove(0);
		try {
			// seta cadastrados usando como parâmetro o curso adicionado
			setCadastrados(instdao.getCursos(instituicao));
		} catch (EntityNotFoundException e) {
			e.printStackTrace();
		}
		// se tiver acabado os cursos, seta pra true o fim do cadastro
		if (getTarget().isEmpty()) {
			setFimCadastro(new Boolean(true));
		} else {
			// Constroi a list de cursos com os restantes
			detalharCurso();
		}
	}

	public void onTransfer(TransferEvent event) {
		setDualListCurso(new DualListModel<Curso>(getCursosMomento(),
				getDualListCurso().getTarget()));
	}

	/**
	 * atualiza cursos
	 */
	public void updateTarget() {
		try {
			setTarget(instdao.getCursos(instituicao.getId()));
			setCadastrados(instdao.getCursos(instituicao));
		} catch (EntityNotFoundException e) {
			e.printStackTrace();
		}
		setDualListCurso(new DualListModel<Curso>(getCursosMomento(), getTarget()));
	}

	// getteres and setteres

	/**
	 * Devolve e inicializa um DualListModel
	 * @return
	 */
	public DualListModel<Curso> getDualListCurso() {
		if (this.dualListCurso == null) {
			this.dualListCurso = new DualListModel<Curso>(
					getCursos(), new ArrayList<Curso>());
		}
		return this.dualListCurso;
	}

	public List<Curso> getCursosMomento() {
		return cursosMomento;
	}

	public void setCursosMomento(List<Curso> cursosMomento) {
		this.cursosMomento = cursosMomento;
	}

	public String getCurso() {
		return curso;
	}

	public void setCurso(String curso) {
		this.curso = curso;
	}

	public Boolean getDetalharCursos() {
		return detalharCursos;
	}

	public void setDetalharCursos(Boolean detalharCursos) {
		this.detalharCursos = detalharCursos;
	}

	public List<InstituicaoCurso> getCadastrados() {
		return cadastrados;
	}

	public void setCadastrados(List<InstituicaoCurso> cadastrados) {
		this.cadastrados = cadastrados;
	}

	public Boolean getFimCadastro() {
		return fimCadastro;
	}

	public void setFimCadastro(Boolean fimCadastro) {
		this.fimCadastro = fimCadastro;
	}

	public List<Curso> getCadastroAnterior() {
		return cadastroAnterior;
	}

	public void setCadastroAnterior(List<Curso> cadastroAnterior) {
		this.cadastroAnterior = cadastroAnterior;
	}

	public List<Curso> getCursos() {
		return cursos;
	}

	public void setCursos(List<Curso> cursos) {
		this.cursos = cursos;
	}

	public void setDualListCurso(DualListModel<Curso> dualListCurso) {
		this.dualListCurso = dualListCurso;
	}

	public List<Curso> getTarget() {
		return target;
	}

	public void setTarget(List<Curso> cursosPresentes) {
		this.target = cursosPresentes;
	}

	public InstituicaoEnsino getInstituicao() {
		return instituicao;
	}

	public void setInstituicao(InstituicaoEnsino instituicao) {
		this.instituicao = instituicao;
	}

	public InstituicaoCurso getInstCurso() {
		return instCurso;
	}

	public void setInstCurso(InstituicaoCurso instCurso) {
		this.instCurso = instCurso;
	}

}