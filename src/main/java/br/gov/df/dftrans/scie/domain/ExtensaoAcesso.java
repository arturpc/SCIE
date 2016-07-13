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
@Table(name = "tb_extensao_acesso")
@NamedQueries({ @NamedQuery(name = ExtensaoAcesso.EXTENSAO_ACESSO_GET_ALL, 
		query = "SELECT e FROM ExtensaoAcesso e"),
		@NamedQuery(name = ExtensaoAcesso.EXTENSAO_ACESSO_FIND_BY_USUARIO, 
		query = "SELECT e FROM ExtensaoAcesso e where e.usuario = :usuario"),
		@NamedQuery(name = ExtensaoAcesso.EXTENSAO_ACESSO_FIND_BY_CPF, 
		query = "SELECT e FROM ExtensaoAcesso e WHERE e.cpf = :cpf"),
		@NamedQuery(name = ExtensaoAcesso.EXTENSAO_ACESSO_FIND_BY_ID, 
		query = "SELECT e FROM ExtensaoAcesso e WHERE e.id = :id"),
		@NamedQuery(name = ExtensaoAcesso.EXTENSAO_ACESSO_GET_SOLICITADO, 
		query = "SELECT e FROM ExtensaoAcesso e WHERE e.status = 0 order by e.atualizacao"),
		@NamedQuery(name = ExtensaoAcesso.EXTENSAO_ACESSO_GET_ANALISYS, 
		query = "SELECT e FROM ExtensaoAcesso e WHERE e.status = 1 "
				+ "and e.usuario = :usuario"),
		@NamedQuery(name = ExtensaoAcesso.EXTENSAO_ACESSO_GET_APROVED, 
		query = "SELECT e FROM ExtensaoAcesso e WHERE e.status = 2 "
				+ "and e.cpf = :cpf order by e.atualizacao"),
		@NamedQuery(name = ExtensaoAcesso.EXTENSAO_ACESSO_GET_REJECTED, 
		query = "SELECT e FROM ExtensaoAcesso e WHERE e.status = 3 "
				+ "and e.cpf = :cpf order by e.atualizacao"), })
public class ExtensaoAcesso implements Serializable {

	public static final String EXTENSAO_ACESSO_GET_ALL = 
			"ExtensaoAcesso.getAll";
	public static final String EXTENSAO_ACESSO_FIND_BY_USUARIO = 
			"ExtensaoAcesso.consultarPorUsuario";
	public static final String EXTENSAO_ACESSO_FIND_BY_CPF = 
			"ExtensaoAcesso.consultarPorCPF";
	public static final String EXTENSAO_ACESSO_FIND_BY_ID = 
			"ExtensaoAcesso.consultarPorId";
	public static final String EXTENSAO_ACESSO_GET_SOLICITADO = 
			"ExtensaoAcesso.getSolicitado";
	public static final String EXTENSAO_ACESSO_GET_ANALISYS = 
			"ExtensaoAcesso.getAnalisys";
	public static final String EXTENSAO_ACESSO_GET_APROVED = 
			"ExtensaoAcesso.getAproved";
	public static final String EXTENSAO_ACESSO_GET_REJECTED = 
			"ExtensaoAcesso.getRejected";

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	@Column(name = "id_extensao")
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
	@Column(name = "st_extensao")
	private int status;

	// 0 = Desmotivado (Irregular)
	// 1 = Perda
	// 2 = Roubo ou Furto
	// 3 = Cartão Danificado ou Inutilizado
	@Column(name = "st_motivo")
	private int motivo;

	@Column(name = "ds_observacao")
	private String obs;

	@Column(name = "ds_email")
	@Email
	private String email;
	
	@Column(name = "ds_nome")
	private String nome;

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

	public String getNome() {
		return nome;
	}

	public void setNome(String nome) {
		this.nome = nome;
	}
}
