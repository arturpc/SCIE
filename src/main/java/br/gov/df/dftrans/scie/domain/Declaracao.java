package br.gov.df.dftrans.scie.domain;

import java.io.Serializable;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

@Entity
@Table(name = "tb_declaracao")
@NamedQueries({ @NamedQuery(name = Declaracao.DECLARACAO_GET_ALL, 
query = "SELECT d FROM Declaracao d"),
		@NamedQuery(name = Declaracao.DECLARACAO_FIND_BY_ID, 
		query = "SELECT d FROM Declaracao d WHERE d.id = :id"),
		@NamedQuery(name = Declaracao.DECLARACAO_FIND_BY_INST, 
		query = "SELECT d FROM Declaracao d WHERE d.instituicao = :instituicao") })
public class Declaracao implements Serializable {

	public static final String DECLARACAO_GET_ALL = "Declaracao.getAll";
	public static final String DECLARACAO_FIND_BY_ID = "Declaracao.findById";
	public static final String DECLARACAO_FIND_BY_INST = "Declaracao.findByInst";

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	@Column(name = "id_declaracao")
	private int id;

	@ManyToOne(cascade = CascadeType.ALL)
	@JoinColumn(name = "id_estudante", referencedColumnName = "id_estudante")
	private Estudante estudante;

	@ManyToOne(cascade = CascadeType.ALL)
	@JoinColumn(name = "id_instituicao", referencedColumnName = "id_instituicao")
	private InstituicaoEnsino instituicao;

	@Column(name = "nm_curso")
	private String curso;

	@Column(name = "nr_periodo")
	private int periodo;

	@Column(name = "dt_aula_inicio")
	@Temporal(value = TemporalType.DATE)
	private Date dataAulaInicio;

	@Column(name = "dt_aula_fim")
	@Temporal(value = TemporalType.DATE)
	private Date dataAulaFim;

	@Column(name = "ds_autenticacao")
	private String autenticacao;

	public Declaracao() {
	}

	// sborescrita toString
	@Override
	public String toString() {

		StringBuilder sb = new StringBuilder();
		sb.append("Id = " + getId() + "\n");
		sb.append("Estudante = " + getEstudante() + "\n");
		sb.append("Instituicao = " + getInstituicao() + "\n");
		sb.append("Curso = " + getCurso() + "\n");
		sb.append("Data Aula Inicio = " + getDataAulaInicio() + "\n");
		sb.append("Data Aula Fim= " + getDataAulaFim() + "\n");
		return sb.toString();
	}

	// hashCode and equals

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + id;
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj){
			return true;
		}
		if (obj == null){
			return false;
		}
		if (getClass() != obj.getClass()){
			return false;
		}
		Declaracao other = (Declaracao) obj;
		if (id != other.id){
			return false;
		}
		return true;
	}

	// getteres and setteres
	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public Estudante getEstudante() {
		return estudante;
	}

	public void setEstudante(Estudante estudante) {
		this.estudante = estudante;
	}

	public InstituicaoEnsino getInstituicao() {
		return instituicao;
	}

	public void setInstituicao(InstituicaoEnsino instituicao) {
		this.instituicao = instituicao;
	}

	public String getCurso() {
		return curso;
	}

	public void setCurso(String curso) {
		this.curso = curso;
	}

	public String getDataAulaInicio() {
		DateFormat fmt = new SimpleDateFormat("dd/MM/yy");
		return fmt.format(dataAulaInicio);
	}

	public void setDataAulaInicio(Date dataAulaInicio) {
		this.dataAulaInicio = dataAulaInicio;
	}

	public String getDataAulaFim() {
		DateFormat fmt = new SimpleDateFormat("dd/MM/yy");
		return fmt.format(dataAulaFim);
	}

	public void setDataAulaFim(Date dataAulaFim) {
		this.dataAulaFim = dataAulaFim;
	}

	public int getPeriodo() {
		return periodo;
	}

	public void setPeriodo(int periodo) {
		this.periodo = periodo;
	}

	public String getAutenticacao() {
		return autenticacao;
	}

	public void setAutenticacao(String autenticacao) {
		this.autenticacao = autenticacao;
	}
}
