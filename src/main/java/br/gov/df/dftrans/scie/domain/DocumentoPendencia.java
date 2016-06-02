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

@Entity
@Table(name = "tb_documento_pendencia")
@NamedQueries({ @NamedQuery(name = DocumentoPendencia.DOCUMENTO_GET_ALL, query = "SELECT d FROM DocumentoPendencia d"),
		@NamedQuery(name = DocumentoPendencia.DOCUMENTO_FIND_BY_DESC, query = "SELECT d FROM DocumentoPendencia d WHERE d.descricao = :descricao"),
		@NamedQuery(name = DocumentoPendencia.DOCUMENTO_FIND_BY_NUMBER, query = "SELECT d FROM DocumentoPendencia d WHERE d.documento = :documento"),
		@NamedQuery(name = DocumentoPendencia.DOCUMENTO_FIND_BY_ID, query = "SELECT d FROM DocumentoPendencia d WHERE d.id = :id") })
public class DocumentoPendencia implements Serializable {

	public static final String DOCUMENTO_GET_ALL = "DocumentoPendencia.getAll";
	public static final String DOCUMENTO_FIND_BY_DESC = "DocumentoPendencia.consultarPorDoc";
	public static final String DOCUMENTO_FIND_BY_NUMBER = "DocumentoPendencia.consultarPorNro";
	public static final String DOCUMENTO_FIND_BY_ID = "DocumentoPendencia.consultarPorCodigo";

	public DocumentoPendencia(String descricao, int documento) {
		setDescricao(descricao);
		this.documento = documento;
	}

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	@Column(name = "id_documento")
	private int id;

	@Column(name = "ds_documento")
	private String descricao;

	@Column(name = "nr_documento")
	private int documento;

	// construtor
	public DocumentoPendencia() {
	}

	// sobrescrita toString
	@Override
	public String toString() {

		StringBuilder sb = new StringBuilder();
		sb.append("Id = " + getId() + "\n");
		sb.append("Descricao = " + getDescricao() + "\n");
		sb.append("Número = " + getDocumento() + "\n");
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
		DocumentoPendencia other = (DocumentoPendencia) obj;
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

	public String getDescricao() {
		return descricao;
	}

	public void setDescricao(String descricao) {
		this.descricao = descricao;
	}

	public int getDocumento() {
		return documento;
	}

	public void setDocumento(int documento) {
		this.documento = documento;
	}

}
