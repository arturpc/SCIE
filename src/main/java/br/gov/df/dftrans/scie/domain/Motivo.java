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
@Table(name = "tb_motivo_val_segunda_via")
@NamedQueries({ @NamedQuery(name = Motivo.MOTIVO_GET_ALL, query = "SELECT m FROM Motivo m"),
		@NamedQuery(name = Motivo.MOTIVO_FIND_BY_ID, query = "SELECT m FROM Motivo m WHERE m.id = :id") })
public class Motivo implements Serializable {

	public static final String MOTIVO_GET_ALL = "Motivo.getAll";
	public static final String MOTIVO_FIND_BY_ID = "Motivo.consultarPorId";

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	@Column(name = "id_motivo")
	private int id;

	@Column(name = "ds_motivo")
	private String motivo;

	@Override
	public String toString() {
		return "Motivo [id=" + id + ", motivo=" + motivo + "]";
	}

	// getteres and setteres
	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getMotivo() {
		return motivo;
	}

	public void setMotivo(String motivo) {
		this.motivo = motivo;
	}

}
