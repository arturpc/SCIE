package br.gov.df.dftrans.scie.domain;

import java.io.Serializable;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

@Entity
@Table(name = "tb_instituicao_curso")
@NamedQueries({
		@NamedQuery(name = InstituicaoCurso.INSTITUICAOCURSO_GET_ALL, 
				query = "SELECT i FROM InstituicaoCurso i where i.fim = null"),
		@NamedQuery(name = InstituicaoCurso.INSTITUICAOCURSO_FIND_ALL_BY_CURSO, 
		query = "SELECT i FROM InstituicaoCurso i WHERE i.curso.id = :idCurso "
				+ "and i.instituicao.id = :idInstituicao and i.fim = null"),
		@NamedQuery(name = InstituicaoCurso.INSTITUICAOCURSO_FIND_BY_CURSO, 
		query = "SELECT i FROM InstituicaoCurso i WHERE i.curso.id = :idCurso "
				+ "and i.instituicao.id = :idInstituicao and i.fim = null"
				+ " and i.ano = (SELECT MAX(inst.ano) FROM InstituicaoCurso inst "
				+ "WHERE inst.instituicao = i.instituicao "
				+ "and inst.curso = i.curso and inst.fim = null)"),
		@NamedQuery(name = InstituicaoCurso.INSTITUICAOCURSO_FIND_BY_INSTITUICAO, 
		query = "SELECT i FROM InstituicaoCurso i "
				+ "WHERE i.instituicao.id = :id and "
				+ "i.fim = null and i.ano = (SELECT MAX(inst.ano) "
				+ "FROM InstituicaoCurso inst "
				+ "WHERE inst.instituicao = i.instituicao "
				+ "and inst.curso = i.curso and inst.fim = null)"),
		@NamedQuery(name = InstituicaoCurso.INSTITUICAOCURSO_FIND_ALL_BY_INSTITUICAO, 
		query = "SELECT i FROM InstituicaoCurso i "
				+ "WHERE i.instituicao = :inst and " + "i.curso = :curso "
						+ "and i.fim = null"),
		@NamedQuery(name = InstituicaoCurso.INSTITUICAOCURSO_FIND_BY_ID, 
		query = "SELECT i FROM InstituicaoCurso i WHERE i.id = :id and i.fim = null") })
public class InstituicaoCurso implements Serializable {

	public static final String INSTITUICAOCURSO_GET_ALL = 
			"InstituicaoCurso.getAll";
	public static final String INSTITUICAOCURSO_FIND_BY_CURSO = 
			"InstituicaoCurso.consultarPorCurso";
	public static final String INSTITUICAOCURSO_FIND_ALL_BY_CURSO = 
			"InstituicaoCurso.consultarTodosPorCurso";
	public static final String INSTITUICAOCURSO_FIND_BY_INSTITUICAO = 
			"InstituicaoCurso.consultarPorInstituicao";
	public static final String INSTITUICAOCURSO_FIND_ALL_BY_INSTITUICAO = 
			"InstituicaoCurso.consultarTodosPorInstituicao";
	public static final String INSTITUICAOCURSO_FIND_BY_ID = 
			"InstituicaoCurso.consultarPorCodigo";

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	@Column(name = "id_instituicao_curso")
	private int id;

	@OneToOne
	@JoinColumn(name = "id_instituicao", referencedColumnName = "id_instituicao")
	private InstituicaoEnsino instituicao;

	@OneToOne
	@JoinColumn(name = "id_curso", referencedColumnName = "id_curso")
	private Curso curso;

	@Column(name = "ds_turno")
	private String turno;

	@Column(name = "dt_inicio")
	@Temporal(value = TemporalType.DATE)
	private Date inicio;

	@Column(name = "dt_fim")
	@Temporal(value = TemporalType.DATE)
	private Date fim;

	@Column(name = "ds_ano_periodo")
	private int ano = 1;

	// construtores
	public InstituicaoCurso(InstituicaoEnsino ie, Curso curso, String turno, int ano) {
		setInstituicao(ie);
		setCurso(curso);
		setTurno(turno);
		setAno(ano);
		try {
			DateFormat fmt = new SimpleDateFormat("dd/MM/yyyy");
			inicio = fmt.parse(fmt.format(new Date()));
		} catch (ParseException e) {
			e.printStackTrace();
		}
	}

	public InstituicaoCurso(InstituicaoEnsino ie, Curso curso) {
		setInstituicao(ie);
		setCurso(curso);
		try {
			DateFormat fmt = new SimpleDateFormat("dd/MM/yyyy");
			inicio = fmt.parse(fmt.format(new Date()));
		} catch (ParseException e) {
			e.printStackTrace();
		}
	}

	public InstituicaoCurso() {
		try {
			DateFormat fmt = new SimpleDateFormat("dd/MM/yyyy");
			inicio = fmt.parse(fmt.format(new Date()));
		} catch (ParseException e) {
			e.printStackTrace();
		}
	}

	// sobrescrita toString();
	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("Instituicao = " + getInstituicao() + "\n");
		sb.append("Curso = " + getCurso() + "\n");
		sb.append("Turno = " + getTurno() + "\n");
		sb.append("Data Inicio = " + getInicio() + "\n");
		sb.append("Data Fim = " + getFim() + "\n");
		sb.append("Periodo/Ano = " + getAno() + "\n");
		return sb.toString();
	}

	// hashCode end equals

	@Override
	public boolean equals(Object arg0) {
		// TODO Auto-generated method stub
		return super.equals(arg0);
	}

	@Override
	public int hashCode() {
		// TODO Auto-generated method stub
		return super.hashCode();
	}

	// getteres and setteres

	public InstituicaoEnsino getInstituicao() {
		return instituicao;
	}

	public void setInstituicao(InstituicaoEnsino instituicao) {
		this.instituicao = instituicao;
	}

	public Curso getCurso() {
		return curso;
	}

	public void setCurso(Curso curso) {
		this.curso = curso;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getTurno() {
		return turno;
	}

	public void setTurno(String turno) {
		this.turno = turno;
	}

	public Date getInicio() {
		return inicio;
	}

	public void setInicio(Date inicio) {
		this.inicio = inicio;
	}

	public Date getFim() {
		return fim;
	}

	public void setFim(Date fim) {
		this.fim = fim;
	}

	public int getAno() {
		return ano;
	}

	public void setAno(int ano) {
		this.ano = ano;
	}

}
