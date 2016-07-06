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
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

@Entity
@Table(name = "tb_log_validacao_cadastro")
@NamedQueries({ @NamedQuery(name = LogValidacaoCadastro.LOG_GET_ALL, 
query = "SELECT l FROM LogValidacaoCadastro l"),
		@NamedQuery(name = LogValidacaoCadastro.LOG_GET_ALL_OPEN, 
		query = "SELECT l FROM LogValidacaoCadastro l where l.validacao = 0"),
		@NamedQuery(name = LogValidacaoCadastro.LOG_GET_ALL_OPEN_INST, 
		query = "SELECT l FROM LogValidacaoCadastro l where l.validacao = 0 "
				+ "and l.representante = :rep"),
		@NamedQuery(name = LogValidacaoCadastro.LOG_GET_ALL_ANALISYS_INST, 
		query = "SELECT l FROM LogValidacaoCadastro l where l.validacao = 1 "
				+ "and l.representante = :rep"),
		@NamedQuery(name = LogValidacaoCadastro.LOG_GET_ALL_ANALISYS_USER, 
		query = "SELECT l FROM LogValidacaoCadastro l where l.validacao = 1 "
				+ "and l.usuario = :usuario"),
		@NamedQuery(name = LogValidacaoCadastro.LOG_GET_ALL_APROVED_INST, 
		query = "SELECT l FROM LogValidacaoCadastro l where l.validacao = 2 "
				+ "and l.representante = :rep"),
		@NamedQuery(name = LogValidacaoCadastro.LOG_GET_ALL_REJECT_INST, 
		query = "SELECT l FROM LogValidacaoCadastro l where l.validacao = 3 "
				+ "and l.representante = :rep"),
		@NamedQuery(name = LogValidacaoCadastro.LOG_GET_ANALISYS, 
		query = "SELECT l FROM LogValidacaoCadastro l where l.validacao = 1 "
				+ "and l.usuario = :usuario and l.representante = :representante "
				+ "and l.documento = :documento"),
		@NamedQuery(name = LogValidacaoCadastro.LOG_GET_ALL_APROVED, 
		query = "SELECT l FROM LogValidacaoCadastro l where l.validacao = 2 "
				+ "and l.representante.id = :id"),
		@NamedQuery(name = LogValidacaoCadastro.LOG_GET_ALL_REJECTED, 
		query = "SELECT l FROM LogValidacaoCadastro l where l.validacao = 3 "
				+ "and l.representante.id = :id"),
		@NamedQuery(name = LogValidacaoCadastro.LOG_FIND_BY_ID, 
		query = "SELECT l FROM LogValidacaoCadastro l WHERE l.id = :id"),
		@NamedQuery(name = LogValidacaoCadastro.LOG_FIND_BY_DADOS, 
		query = "SELECT l FROM LogValidacaoCadastro l "
				+ "WHERE l.representante = :representante "
				+ "and l.documento = :documento and l.validacao in (0,1)") })
public class LogValidacaoCadastro implements Serializable {

	public static final String LOG_GET_ALL = 
			"LogValidacaoCadastro.getAll";
	public static final String LOG_GET_ALL_OPEN = 
			"LogValidacaoCadastro.getAllOpen";
	public static final String LOG_GET_ALL_OPEN_INST = 
			"LogValidacaoCadastro.getAllOpenInst";
	public static final String LOG_GET_ALL_ANALISYS_INST = 
			"LogValidacaoCadastro.getAllAnalisysInst";
	public static final String LOG_GET_ALL_ANALISYS_USER = 
			"LogValidacaoCadastro.getAllAnalisysUser";
	public static final String LOG_GET_ALL_APROVED_INST = 
			"LogValidacaoCadastro.getAllAprovedInst";
	public static final String LOG_GET_ALL_REJECT_INST = 
			"LogValidacaoCadastro.getAllRejectInst";
	public static final String LOG_GET_ANALISYS = 
			"LogValidacaoCadastro.getAllAnalisys";
	public static final String LOG_GET_ALL_APROVED = 
			"LogValidacaoCadastro.getAllAproved";
	public static final String LOG_GET_ALL_REJECTED = 
			"LogValidacaoCadastro.getAllRejected";
	public static final String LOG_FIND_BY_ID = 
			"LogValidacaoCadastro.findById";
	public static final String LOG_FIND_BY_DADOS = 
			"LogValidacaoCadastro.findByDados";

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	@Column(name = "id_log")
	private int id;

	@ManyToOne(cascade = CascadeType.ALL)
	@JoinColumn(name = "id_usuario", referencedColumnName = "id_usuario")
	private Usuario usuario;

	@ManyToOne(cascade = CascadeType.ALL)
	@JoinColumn(name = "id_representante", referencedColumnName = "id_representante")
	private Representante representante;

	@Column(name = "dt_atualizacao")
	@Temporal(value = TemporalType.DATE)
	private Date atualizacao;

	@ManyToOne(cascade = CascadeType.ALL)
	@JoinColumn(name = "id_documento", referencedColumnName = "id_documento")
	private DocumentoPendencia documento;

	// Validacao = 0 (Pendente de Verificação)
	// Validacao = 1 (Em Análise)
	// Validacao = 2 (Aprovado)
	// Validacao = 3 (Não Aprovado)

	@Column(name = "st_validacao")
	int validacao;

	@Column(name = "ds_comentario")
	String comentario;

	// construtores
	public LogValidacaoCadastro() {
	}

	public LogValidacaoCadastro(Usuario usuario, Representante representante, 
			DocumentoPendencia documento,
			String comentario) {
		setUsuario(usuario);
		setDocumento(documento);
		setAtualizacao(new Date());
		setComentario(comentario);
		setRepresentante(representante);
	}
	
	public LogValidacaoCadastro(Representante representante, 
			DocumentoPendencia documento) {
		setRepresentante(representante);
		setDocumento(documento);
		setAtualizacao(new Date());
	}

	public LogValidacaoCadastro(Usuario usuario, Representante representante, 
			String comentario, int validacao) {
		setUsuario(usuario);
		setRepresentante(representante);
		setAtualizacao(new Date());
		setComentario(comentario);
		setValidacao(validacao);
	}

	// sobrescrita toString

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("Id = " + getId() + "\n");
		sb.append("Usuario = " + getUsuario() + "\n");
		sb.append("Representante = " + getRepresentante() + "\n");
		sb.append("Atualizacao = " + getAtualizacao() + "\n");
		sb.append("Documentos = " + getDocumento() + "\n");
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
		LogValidacaoCadastro other = (LogValidacaoCadastro) obj;
		if (id != other.id){
			return false;
		}
		return true;
	}

	// getteres and setteres
	public DocumentoPendencia getDocumento() {
		return documento;
	}

	public void setDocumento(DocumentoPendencia documento) {
		this.documento = documento;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public Usuario getUsuario() {
		return usuario;
	}

	public void setUsuario(Usuario usuario) {
		this.usuario = usuario;
	}

	public Date getAtualizacao() {
		return atualizacao;
	}

	public void setAtualizacao(Date atualizacao) {
		this.atualizacao = atualizacao;
	}

	public int getValidacao() {
		return validacao;
	}

	public void setValidacao(int validacao) {
		this.validacao = validacao;
	}

	public String getComentario() {
		return comentario;
	}

	public void setComentario(String comentario) {
		this.comentario = comentario;
	}

	public Representante getRepresentante() {
		return representante;
	}

	public void setRepresentante(Representante representante) {
		this.representante = representante;
	}
	
	

}
