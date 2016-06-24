package br.gov.df.dftrans.scie.domain;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import org.hibernate.validator.constraints.br.CPF;

@Entity
@Table(name = "tb_prioridade")
@NamedQueries({ @NamedQuery(name = Prioridade.PRIORIDADE_GET_ALL, 
	query = "SELECT p FROM Prioridade p"),
	@NamedQuery(name = Prioridade.PRIORIDADE_FIND_BY_ID, 
	query = "SELECT p FROM Prioridade p WHERE p.id = :id") })
public class Prioridade implements Serializable{

	public static final String PRIORIDADE_GET_ALL = "Prioridade.getAll";
	public static final String PRIORIDADE_FIND_BY_ID = "Prioridade.consultarPorId";
	
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	@Column(name = "id_prioridade")
	private int id;
	
	@Column(name = "ds_cpf", unique = true)
	@CPF
	private String cpf;
	
	@Column(name = "st_prioridade")
	private int status;
	
	@Column(name = "dt_atualizacao")
	@Temporal(value = TemporalType.DATE)
	private Date atualizacao = new Date();

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getCpf() {
		return cpf;
	}

	public void setCpf(String cpf) {
		this.cpf = cpf;
	}

	public int getStatus() {
		return status;
	}

	public void setStatus(int status) {
		this.status = status;
	}

	public Date getAtualizacao() {
		return atualizacao;
	}

	public void setAtualizacao(Date atualizacao) {
		this.atualizacao = atualizacao;
	}
	
	
}
