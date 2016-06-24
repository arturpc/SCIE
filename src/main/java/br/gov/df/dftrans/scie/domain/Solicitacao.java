package br.gov.df.dftrans.scie.domain;

import java.io.Serializable;
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
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import org.hibernate.validator.constraints.Email;
import org.hibernate.validator.constraints.br.CPF;

@Entity
@Table(name = "tb_solicitacao_segunda_via")
@NamedQueries({ @NamedQuery(name = Solicitacao.SOLICITACAO_GET_ALL, 
		query = "SELECT s FROM Solicitacao s"),
		@NamedQuery(name = Solicitacao.SOLICITACAO_FIND_BY_USUARIO, 
		query = "SELECT s FROM Solicitacao s where s.usuario = :usuario"),
		@NamedQuery(name = Solicitacao.SOLICITACAO_FIND_BY_CPF, 
		query = "SELECT s FROM Solicitacao s WHERE s.cpf = :cpf"),
		@NamedQuery(name = Solicitacao.SOLICITACAO_FIND_BY_ID, 
		query = "SELECT s FROM Solicitacao s WHERE s.id = :id"),
		@NamedQuery(name = Solicitacao.SOLICITACAO_GET_SOLICITADO, 
		query = "SELECT s FROM Solicitacao s WHERE s.status = 0 order by s.atualizacao"),
		@NamedQuery(name = Solicitacao.SOLICITACAO_GET_ANALISYS, 
		query = "SELECT s FROM Solicitacao s WHERE s.status = 1 and s.usuario = :usuario"),
		@NamedQuery(name = Solicitacao.SOLICITACAO_GET_APROVED, 
		query = "SELECT s FROM Solicitacao s WHERE s.status = 2 and s.cpf = :cpf "
				+ "order by s.atualizacao"),
		@NamedQuery(name = Solicitacao.SOLICITACAO_GET_REJECTED, 
		query = "SELECT s FROM Solicitacao s WHERE s.status = 3 and s.cpf = :cpf "
				+ "order by s.atualizacao"), })
public class Solicitacao implements Serializable {

	public static final String SOLICITACAO_GET_ALL = "Solicitacao.getAll";
	public static final String SOLICITACAO_FIND_BY_USUARIO = "Solicitacao.consultarPorUsuario";
	public static final String SOLICITACAO_FIND_BY_CPF = "Solicitacao.consultarPorCPF";
	public static final String SOLICITACAO_FIND_BY_ID = "Solicitacao.consultarPorId";
	public static final String SOLICITACAO_GET_SOLICITADO = "Solicitacao.getSolicitado";
	public static final String SOLICITACAO_GET_ANALISYS = "Solicitacao.getAnalisys";
	public static final String SOLICITACAO_GET_APROVED = "Solicitacao.getAproved";
	public static final String SOLICITACAO_GET_REJECTED = "Solicitacao.getRejected";

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	@Column(name = "id_solicitacao")
	private int id;

	@Column(name = "nr_cpf")
	@CPF
	private String cpf;

	// -1 = em duplicidade
	// 0 = solicitado
	// 1 = em análise
	// 2 = aprovado
	// 3 = rejeitado
	// 4 = impresso
	// 5 = entregue
	@Column(name = "st_solicitacao")
	private int status;

	// 0 = Desmotivado (Irregular)
	// 1 = Perda
	// 2 = Roubo ou Furto
	// 3 = Cartão Danificado ou Inutilizado
	@Column(name = "st_motivo")
	private int motivo;

	@Column(name = "ds_observacao")
	private String obs;
	
	@Column(name = "nr_nsu_pagamento")
	private String nsuPagamento;

	@Column(name = "ds_email")
	@Email
	private String email;

	@Column(name = "dt_atualizacao")
	@Temporal(value = TemporalType.DATE)
	private Date atualizacao;

	@ManyToOne(cascade = CascadeType.ALL)
	@JoinColumn(name = "id_usuario", referencedColumnName = "id_usuario")
	private Usuario usuario;

	@OneToOne(cascade = CascadeType.ALL)
	@JoinColumn(name = "id_comentario", referencedColumnName = "id_comentario")
	private Comentario comentario;

	// getteres and setteres
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

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public Date getAtualizacao() {
		return atualizacao;
	}

	public void setAtualizacao(Date atualizacao) {
		this.atualizacao = atualizacao;
	}

	public Usuario getUsuario() {
		return usuario;
	}

	public void setUsuario(Usuario usuario) {
		this.usuario = usuario;
	}

	public int getStatus() {
		return status;
	}

	public void setStatus(int status) {
		this.status = status;
	}

	public int getMotivo() {
		return motivo;
	}

	public void setMotivo(int motivo) {
		this.motivo = motivo;
	}

	public String getObs() {
		return obs;
	}

	public void setObs(String obs) {
		this.obs = obs;
	}

	public Comentario getComentario() {
		return comentario;
	}

	public void setComentario(Comentario comentario) {
		this.comentario = comentario;
	}

	public String getNsuPagamento() {
		return nsuPagamento;
	}

	public void setNsuPagamento(String nsuPagamento) {
		this.nsuPagamento = nsuPagamento;
	}
}
