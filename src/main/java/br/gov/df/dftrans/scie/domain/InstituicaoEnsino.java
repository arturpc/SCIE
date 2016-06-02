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
import javax.persistence.OneToOne;
import javax.persistence.Table;
import org.hibernate.validator.constraints.br.CNPJ;

import br.gov.df.dftrans.scie.annotations.StringFormatter;
import br.gov.df.dftrans.scie.annotations.StringUpperCase;

@Entity
@Table(name = "tb_instituicao_ensino")
@NamedQueries({ @NamedQuery(name = InstituicaoEnsino.INSTITUICAO_GET_ALL, query = "SELECT i FROM InstituicaoEnsino i"),
		@NamedQuery(name = InstituicaoEnsino.INSTITUICAO_FIND_BY_INEP_EMEC, query = "SELECT i FROM InstituicaoEnsino i WHERE i.codInepEmec = :codInepEmec"),
		@NamedQuery(name = InstituicaoEnsino.INSTITUICAO_FIND_BY_REP_NAME, query = "SELECT i FROM InstituicaoEnsino i where i.representante.nome = :nome"),
		@NamedQuery(name = InstituicaoEnsino.INSTITUICAO_FIND_BY_ID, query = "SELECT i FROM InstituicaoEnsino i WHERE i.id = :id"),
		@NamedQuery(name = InstituicaoEnsino.INSTITUICAO_FIND_BY_OBJECT, query = "SELECT i FROM InstituicaoEnsino i WHERE i = :id") })
public class InstituicaoEnsino implements Serializable {

	public static final String INSTITUICAO_GET_ALL = "InstituicaoEnsino.getAll";
	public static final String INSTITUICAO_FIND_BY_INEP_EMEC = "InstituicaoEnsino.consultarPorInepEmec";
	public static final String INSTITUICAO_FIND_BY_ID = "InstituicaoEnsino.consultarPorCodigo";
	public static final String INSTITUICAO_FIND_BY_OBJECT = "InstituicaoEnsino.consultarPorObject";
	public static final String INSTITUICAO_FIND_BY_REP_NAME = "InstituicaoEnsino.consultarPorNomeRepresentante";

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	@Column(name = "id_instituicao")
	private int id;

	@StringUpperCase
	@StringFormatter
	@Column(name = "nm_instituicao")
	private String nomeInstituicao;

	@Column(name = "nr_cnpj")
	@CNPJ
	private String cnpj;

	@StringUpperCase
	@StringFormatter
	@Column(name = "nm_razao_social")
	private String razaoSocial;

	@Column(name = "cd_inep_emec")
	private String codInepEmec;

	@Column(name = "st_cadastro")
	private int situacao = 1;

	@Column(name = "cd_escola_seedf")
	String codEscola;

	// Instituição de Ensino Pública = 0;
	// Instituição de Ensino Privada = 1;
	@Column(name = "st_privada")
	int privada;

	@OneToOne
	@JoinColumn(name = "id_representante", referencedColumnName = "id_representante")
	private Representante representante;

	@ManyToOne
	@JoinColumn(name = "id_endereco", referencedColumnName = "id_endereco")
	private Endereco endereco;

	// construtores
	public InstituicaoEnsino() {
	}

	public InstituicaoEnsino(String nomeInstituicao, String cnpj, String razaoSocial, String codInepEmec,
			Endereco endereco, Representante representante, int situacao) {
		setNomeInstituicao(nomeInstituicao);
		setCnpj(cnpj);
		setRazaoSocial(razaoSocial);
		setCodInepEmec(codInepEmec);
		setEndereco(endereco);
		setRepresentante(representante);
		setSituacao(situacao);
	}

	public InstituicaoEnsino(String nomeInstituicao, String cnpj, String razaoSocial, String codInepEmec,
			Endereco endereco, Representante representante, int situacao, String codEscola) {
		setNomeInstituicao(nomeInstituicao);
		setCnpj(cnpj);
		setRazaoSocial(razaoSocial);
		setCodInepEmec(codInepEmec);
		setEndereco(endereco);
		setRepresentante(representante);
		setSituacao(situacao);
		setCodEscola(codEscola);
	}

	// sobrescrita toString
	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("Id = " + getId() + "\n");
		sb.append("CNPJ = " + getCnpj() + "\n");
		sb.append("Codigo INEP/E-mec = " + getCodInepEmec() + "\n");
		sb.append("Nome = " + getNomeInstituicao() + "\n");
		sb.append("Razao = " + getRazaoSocial() + "\n");
		sb.append("Situação Cadastro = " + getSituacao() + "\n");
		sb.append("Código Escola SEEDF = " + getCodEscola() + "\n");
		sb.append("Endereco = " + getEndereco() + "\n");
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
		InstituicaoEnsino other = (InstituicaoEnsino) obj;
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

	public String getNomeInstituicao() {
		return nomeInstituicao;
	}

	public void setNomeInstituicao(String instituicao) {
		this.nomeInstituicao = instituicao;
	}

	public String getCnpj() {
		return cnpj;
	}

	public void setCnpj(String cnpj) {
		this.cnpj = cnpj;
	}

	public String getRazaoSocial() {
		return razaoSocial;
	}

	public void setRazaoSocial(String razaoSocial) {
		this.razaoSocial = razaoSocial;
	}

	public String getCodInepEmec() {
		return codInepEmec;
	}

	public void setCodInepEmec(String codInepEmec) {
		this.codInepEmec = codInepEmec;
	}

	public Representante getRepresentante() {
		return representante;
	}

	public void setRepresentante(Representante representante) {
		this.representante = representante;
	}

	public Endereco getEndereco() {
		return endereco;
	}

	public void setEndereco(Endereco endereco) {
		this.endereco = endereco;
	}

	public int getSituacao() {
		return situacao;
	}

	public void setSituacao(int situacao) {
		this.situacao = situacao;
	}

	public String getCodEscola() {
		return codEscola;
	}

	public void setCodEscola(String codEscola) {
		this.codEscola = codEscola;
	}

}
