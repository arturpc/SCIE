package br.gov.df.dftrans.scie.domain;

import java.io.Serializable;

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
import javax.persistence.OneToOne;
import javax.persistence.Table;

@Entity
@Table(name = "tb_frequencia")
@NamedQueries({
		@NamedQuery(name = Frequencia.FREQUENCIA_GET_ALL, 
		query = "SELECT f FROM Frequencia f ORDER BY f.dataReferencia DESC"),
		@NamedQuery(name = Frequencia.FREQUENCIA_FIND_BY_ID, 
		query = "SELECT f FROM Frequencia f WHERE f.id = :id "
				+ "ORDER BY f.dataReferencia DESC"),
		@NamedQuery(name = Frequencia.FREQUENCIA_FIND_BY_INST, 
		query = "SELECT f FROM Frequencia f WHERE f.instituicao = :instituicao "
				+ "ORDER BY f.dataReferencia DESC") })

public class Frequencia implements Serializable {

	public static final String FREQUENCIA_GET_ALL = "Frequencia.getAll";
	public static final String FREQUENCIA_FIND_BY_ID = "Frequencia.findById";
	public static final String FREQUENCIA_FIND_BY_INST = "Frequencia.findByInst";

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	@Column(name = "id_frequencia")
	private int id;

	@ManyToOne(cascade = CascadeType.ALL)
	@JoinColumn(name = "id_instituicao", referencedColumnName = "id_instituicao")
	private InstituicaoEnsino instituicao;

	@OneToOne(cascade = CascadeType.ALL)
	@JoinColumn(name = "id_estudante", referencedColumnName = "id_estudante")
	private Estudante estudante;

	@Column(name = "dt_referencia")
	private int dataReferencia;

	// 0 = Presente
	// 1 = Ausente
	@Column(name = "st_frequencia")
	private int frequencia;

	@Column(name = "ds_autenticacao")
	private String autenticacao;

	// construtor
	public Frequencia() {
	}

	// sobrescrita toString
	@Override
	public String toString() {

		StringBuilder sb = new StringBuilder();
		sb.append("Id = " + getId() + "\n");
		sb.append("Instituicao = " + getInstituicao() + "\n");
		sb.append("Estudante = " + getEstudante() + "\n");
		sb.append("Data Referencia = " + getDataReferencia() + "\n");
		sb.append("Frequencia = " + getFrequencia() + "\n");

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
		Frequencia other = (Frequencia) obj;
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

	public InstituicaoEnsino getInstituicao() {
		return instituicao;
	}

	public void setInstituicao(InstituicaoEnsino instituicao) {
		this.instituicao = instituicao;
	}

	public Estudante getEstudante() {
		return estudante;
	}

	public void setEstudante(Estudante estudante) {
		this.estudante = estudante;
	}

	public int getDataReferencia() {
		return dataReferencia;
	}

	public void setDataReferencia(int dataReferencia) {
		this.dataReferencia = dataReferencia;
	}

	public int getFrequencia() {
		return frequencia;
	}

	public String getFrequenciaString() {
		if (frequencia == 0) {
			return "Presente";
		} else {
			return "Ausente";
		}
	}

	public void setFrequencia(int frequencia) {
		this.frequencia = frequencia;
	}

	public String getAutenticacao() {
		return autenticacao;
	}

	public void setAutenticacao(String autenticacao) {
		this.autenticacao = autenticacao;
	}

}
