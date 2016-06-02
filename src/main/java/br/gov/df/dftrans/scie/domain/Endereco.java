package br.gov.df.dftrans.scie.domain;

import java.io.Serializable;

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

import br.gov.df.dftrans.scie.annotations.StringFormatter;
import br.gov.df.dftrans.scie.annotations.StringUpperCase;

@Entity
@Table(name = "tb_endereco")
@NamedQueries({ @NamedQuery(name = Endereco.ENDERECO_GET_ALL, query = "SELECT e FROM Endereco e"),
		@NamedQuery(name = Endereco.ENDERECO_FIND_BY_CEP, query = "SELECT e FROM Endereco e WHERE e.cep = :cep"),
		@NamedQuery(name = Endereco.ENDERECO_FIND_BY_ID, query = "SELECT e FROM Endereco e WHERE e.id = :id") })
public class Endereco implements Serializable {

	public static final String ENDERECO_GET_ALL = "Endereco.getAll";
	public static final String ENDERECO_FIND_BY_CEP = "Endereco.consultarPorCep";
	public static final String ENDERECO_FIND_BY_ID = "Endereco.consultarPorCodigo";

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	@Column(name = "id_endereco")
	private int id;

	@ManyToOne
	@JoinColumn(name = "id_cidade", referencedColumnName = "id_cidade")
	private Cidade cidade;

	@Column(name = "nr_cep")
	private String cep;

	@StringUpperCase
	@StringFormatter
	@Column(name = "nm_bairro")
	private String bairro;

	@StringUpperCase
	@StringFormatter
	@Column(name = "ds_logradouro")
	private String logradouro;

	@StringUpperCase
	@StringFormatter
	@Column(name = "ds_complemento")
	private String complemento;

	// construtores
	public Endereco() {
	}

	public Endereco(String cep, String bairro, String logradouro, String complemento, Cidade cidade) {
		setCep(cep);
		setBairro(bairro);
		setLogradouro(logradouro);
		setComplemento(complemento);
		setCidade(cidade);
	}
	// sobrescrita toString

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("Id = " + getId() + "\n");
		sb.append("Cidade = " + getCidade() + "\n");
		sb.append("Cep = " + getCep() + "\n");
		sb.append("Bairro = " + getBairro() + "\n");
		sb.append("Logradouro = " + getLogradouro() + "\n");
		sb.append("Complemento = " + getComplemento() + "\n");

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
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Endereco other = (Endereco) obj;
		if (id != other.id)
			return false;
		return true;
	}

	// getteres and setteres
	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public Cidade getCidade() {
		return cidade;
	}

	public void setCidade(Cidade cidade) {
		this.cidade = cidade;
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
