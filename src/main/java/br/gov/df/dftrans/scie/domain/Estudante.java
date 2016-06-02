package br.gov.df.dftrans.scie.domain;

import java.io.Serializable;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

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

import org.hibernate.validator.constraints.br.CPF;

import br.gov.df.dftrans.scie.exceptions.PlanilhaException;
import br.gov.df.dftrans.scie.utils.CPFValidator;

@Entity
@Table(name = "tb_estudante")
@NamedQueries({ @NamedQuery(name = Estudante.ESTUDANTE_GET_ALL, query = "SELECT e FROM Estudante e"),
		@NamedQuery(name = Estudante.ESTUDANTE_FIND_BY_ID, query = "SELECT e FROM Estudante e WHERE e.id = :id"),
		@NamedQuery(name = Estudante.ESTUDANTE_FIND_BY_CPF, query = "SELECT e FROM Estudante e WHERE e.cpf = :cpf") })
public class Estudante implements Serializable {

	public static final String ESTUDANTE_GET_ALL = "Estudante.getAll";
	public static final String ESTUDANTE_FIND_BY_ID = "Estudante.findById";
	public static final String ESTUDANTE_FIND_BY_CPF = "Estudante.findByCPF";

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	@Column(name = "id_estudante")
	private int id;

	@Column(name = "ds_matricula")
	private String matricula;

	@Column(name = "nm_estudante")
	private String nome;

	@Column(name = "dt_nascimento")
	@Temporal(value = TemporalType.DATE)
	private Date dataNascimento;

	@Column(name = "nm_responsavel")
	private String responsavel;

	@ManyToOne
	@JoinColumn(name = "id_instituicao", referencedColumnName = "id_instituicao")
	private InstituicaoEnsino Instituicao;

	@Column(name = "nm_curso")
	private String curso;

	@Column(name = "ds_nivel_curso")
	private String nivel;

	@Column(name = "nr_periodo")
	private int periodo;

	@Column(name = "nr_cpf")
	@CPF
	private String cpf;

	@ManyToOne
	@JoinColumn(name = "id_endereco", referencedColumnName = "id_endereco")
	private Endereco endereco;

	// construtor
	public Estudante() {
	}

	// hashCode and equals
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((matricula == null) ? 0 : matricula.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Estudante other = (Estudante) obj;
		if (matricula == null) {
			if (other.matricula != null)
				return false;
		} else if (!matricula.equals(other.matricula))
			return false;
		return true;
	}

	// sobrescrita toString

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("Estudante:\n");
		sb.append("Id = " + getId() + "\n");
		sb.append("Matricula = " + getMatricula() + "\n");
		sb.append("Nome = " + getNome() + "\n");
		sb.append("Nascimento = " + getDataNascimento() + "\n");
		sb.append("Responsavel = " + getResponsavel() + "\n");
		sb.append("Instituicao = " + getInstituicao() + "\n");
		sb.append("Periodo = " + getPeriodo() + "\n");
		sb.append("CPF = " + getCpf() + "\n");
		sb.append("Endereco = " + getEndereco() + "\n");
		sb.append("Curso = " + getCurso() + "\n");
		sb.append("Nivel = " + getNivel() + "\n");
		return sb.toString();
	}

	// getteres and setteres
	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getMatricula() {
		return matricula;
	}

	public void setMatricula(String matricula) {
		this.matricula = matricula;
	}

	public String getNome() {
		return nome;
	}

	public void setNome(String nome) {
		this.nome = nome;
	}

	public String getDataNascimento() {
		DateFormat fmt = new SimpleDateFormat("dd/MM/yy");
		return fmt.format(dataNascimento);
	}

	public void setDataNascimento(Date dataNascimento) {
		this.dataNascimento = dataNascimento;
	}

	public String getResponsavel() {
		return responsavel;
	}

	public void setResponsavel(String responsavel) {
		this.responsavel = responsavel;
	}

	public InstituicaoEnsino getInstituicao() {
		return Instituicao;
	}

	public void setInstituicao(InstituicaoEnsino instituicao) {
		Instituicao = instituicao;
	}

	public int getPeriodo() {
		return periodo;
	}

	public void setPeriodo(int periodo) {
		this.periodo = periodo;
	}

	public String getCpf() {
		return cpf;
	}

	public void setCpf(String cpf) {
		if (CPFValidator.isValid(cpf)) {
			this.cpf = cpf;
		} else {
			throw new PlanilhaException("CPF do estudante (" + cpf + ") inválido!");
		}
	}

	public Endereco getEndereco() {
		return endereco;
	}

	public void setEndereco(Endereco endereco) {
		this.endereco = endereco;
	}

	public String getCurso() {
		return curso;
	}

	public void setCurso(String curso) {
		this.curso = curso;
	}

	public String getNivel() {
		return nivel;
	}

	public void setNivel(String nivel) {
		this.nivel = nivel;
	}

}
