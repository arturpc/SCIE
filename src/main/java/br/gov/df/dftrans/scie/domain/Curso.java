package br.gov.df.dftrans.scie.domain;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;

import br.gov.df.dftrans.scie.annotations.StringFormatter;
import br.gov.df.dftrans.scie.annotations.StringUpperCase;

@Entity
@Table(name = "tb_curso")
@NamedQueries({ @NamedQuery(name = Curso.CURSO_GET_ALL, query = "SELECT c FROM Curso c"),
		@NamedQuery(name = Curso.CURSO_FIND_BY_EMEC, query = "SELECT c FROM Curso c WHERE c.codEmec = :codEmec"),
		@NamedQuery(name = Curso.CURSO_FIND_BY_CURSO, query = "SELECT c FROM Curso c WHERE c.curso = :curso"),
		@NamedQuery(name = Curso.CURSO_FIND_BY_CURSO_NIVEL, query = "SELECT c FROM Curso c WHERE c.curso = :curso and c.nivel = :nivel"),
		@NamedQuery(name = Curso.CURSO_FIND_BY_ID, query = "SELECT c FROM Curso c WHERE c.id = :id") })
public class Curso implements Serializable {

	public static final String CURSO_GET_ALL = "Curso.getAll";
	public static final String CURSO_FIND_BY_EMEC = "Curso.consultarPorEMEC";
	public static final String CURSO_FIND_BY_CURSO = "Curso.consultarPorCurso";
	public static final String CURSO_FIND_BY_CURSO_NIVEL = "Curso.consultarPorCursoNivel";
	public static final String CURSO_FIND_BY_ID = "Curso.consultarPorCodigo";

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	@Column(name = "id_curso")
	private int id;

	@StringUpperCase
	@StringFormatter
	@Column(name = "ds_curso")
	private String curso;

	@Column(name = "cd_emec")
	private String codEmec;

	@StringUpperCase
	@StringFormatter
	@Column(name = "ds_nivel")
	private String nivel;

	// construtores
	public Curso() {
	}

	public Curso(String curso, String cdEmec, String nivel) {
		setCurso(curso);
		setCodEmec(cdEmec);
		setNivel(nivel);
	}

	public Curso(String curso, String nivel) {
		setCurso(curso);
		setNivel(nivel);
	}

	// hashCode and equals
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((codEmec == null) ? 0 : codEmec.hashCode());
		result = prime * result + id;
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
		Curso other = (Curso) obj;
		if (codEmec == null) {
			if (other.codEmec != null)
				return false;
		} else if (!codEmec.equals(other.codEmec))
			return false;
		if (id != other.id)
			return false;
		return true;
	}

	// sobrescrita toString

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("Id = " + getId() + "\n");
		sb.append("Curso = " + getCurso() + "\n");
		sb.append("Codigo INEP/E-mec = " + getCodEmec() + "\n");
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

	public String getCurso() {
		return curso;
	}

	public void setCurso(String curso) {
		this.curso = curso;
	}

	public String getCodEmec() {
		return codEmec;
	}

	public void setCodEmec(String codEmec) {
		this.codEmec = codEmec;
	}

	public String getNivel() {
		return nivel;
	}

	public void setNivel(String nivel) {
		this.nivel = nivel;
	}

}
