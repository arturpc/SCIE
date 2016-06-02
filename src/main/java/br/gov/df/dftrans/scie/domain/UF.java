<<<<<<< HEAD
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
@Table(name = "tb_uf")
@NamedQueries({ @NamedQuery(name = UF.UF_GET_ALL, query = "SELECT u FROM UF u"),
		@NamedQuery(name = UF.UF_FIND_BY_UF, query = "SELECT u FROM UF u WHERE u.uf = :uf"),
		@NamedQuery(name = UF.UF_FIND_BY_ID, query = "SELECT u FROM UF u WHERE u.id = :id") })
public class UF implements Serializable {

	public static final String UF_GET_ALL = "UF.getAll";
	public static final String UF_FIND_BY_UF = "UF.consultarPorUF";
	public static final String UF_FIND_BY_ID = "UF.consultarPorCodigo";

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	@Column(name = "id_uf")
	private int id;

	@Column(name = "sg_uf", unique = true)
	private String uf;

	// construtores
	public UF() {
	}

	public UF(int id, String UF) {
		this.id = id;
		this.uf = UF;
	}

	public UF(String UF) {
		this.uf = UF;
	}

	// sobrescrita toString
	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("Id = " + getId() + "\n");
		sb.append("UF = " + getUf() + "\n");
		return sb.toString();
	}

	// hashCode and equals
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + id;
		result = prime * result + ((uf == null) ? 0 : uf.hashCode());
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
		UF other = (UF) obj;
		if (id != other.id)
			return false;
		if (uf == null) {
			if (other.uf != null)
				return false;
		} else if (!uf.equals(other.uf))
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

	public String getUf() {
		return uf;
	}

	public void setUf(String uf) {
		this.uf = uf;
	}

}
=======
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
@Table(name = "tb_uf")
@NamedQueries({ @NamedQuery(name = UF.UF_GET_ALL, query = "SELECT u FROM UF u"),
		@NamedQuery(name = UF.UF_FIND_BY_UF, query = "SELECT u FROM UF u WHERE u.uf = :uf"),
		@NamedQuery(name = UF.UF_FIND_BY_ID, query = "SELECT u FROM UF u WHERE u.id = :id") })
public class UF implements Serializable {

	public static final String UF_GET_ALL = "UF.getAll";
	public static final String UF_FIND_BY_UF = "UF.consultarPorUF";
	public static final String UF_FIND_BY_ID = "UF.consultarPorCodigo";

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	@Column(name = "id_uf")
	private int id;

	@Column(name = "sg_uf", unique = true)
	private String uf;

	// construtores
	public UF() {
	}

	public UF(int id, String uf) {
		this.id = id;
		this.uf = uf;
	}

	public UF(String uf) {
		this.uf = uf;
	}

	// sobrescrita toString
	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("Id = " + getId() + "\n");
		sb.append("UF = " + getUf() + "\n");
		return sb.toString();
	}

	// hashCode and equals
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + id;
		result = prime * result + ((uf == null) ? 0 : uf.hashCode());
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
		UF other = (UF) obj;
		if (id != other.id)
			return false;
		if (uf == null) {
			if (other.uf != null)
				return false;
		} else if (!uf.equals(other.uf))
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

	public String getUf() {
		return uf;
	}

	public void setUf(String uf) {
		this.uf = uf;
	}

}
>>>>>>> origin/master
