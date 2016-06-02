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
import javax.persistence.Table;

@Entity
@Table(name = "tb_comentario_validacao")
@NamedQueries({ @NamedQuery(name = Comentario.COMENTARIO_GET_ALL, query = "SELECT c FROM Comentario c"),
		@NamedQuery(name = Comentario.COMENTARIO_FIND_BY_ID, query = "SELECT c FROM Comentario c WHERE c.id = :id") })
public class Comentario implements Serializable {

	public static final String COMENTARIO_GET_ALL = "ComentarioSegundaVia.getAll";
	public static final String COMENTARIO_FIND_BY_ID = "ComentarioSegundaVia.consultarPorId";

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	@Column(name = "id_comentario")
	private int id;

	@Column(name = "st_validacao_BO")
	private int boValido;

	@Column(name = "st_validacao_taxa")
	private int taxaValida;

	@Column(name = "st_validacao_doc1")
	private int DOC1Valido;

	@Column(name = "st_validacao_doc2")
	private int DOC2Valido;

	@Column(name = "st_validacao")
	private int validacao;

	@Column(name = "ds_validacao_bo")
	private String descricaoBO;

	@Column(name = "ds_validacao_taxa")
	private String descricaoTAXA;

	@Column(name = "ds_validacao_doc1")
	private String descricaoDOC1;

	@Column(name = "ds_validacao_doc2")
	private String descricaoDOC2;

	@Column(name = "ds_validacao")
	private String descricaoValidacao;

	@Column(name = "ds_comentario")
	private String descricaoComentario;

	@ManyToOne(cascade = CascadeType.ALL)
	@JoinColumn(name = "id_motivo", referencedColumnName = "id_motivo")
	private Motivo motivo;

	// getteres and setteres
	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public int getBoValido() {
		return boValido;
	}

	public void setBoValido(int boValido) {
		this.boValido = boValido;
	}

	public int getTaxaValida() {
		return taxaValida;
	}

	public void setTaxaValida(int taxaValida) {
		this.taxaValida = taxaValida;
	}

	public int getValidacao() {
		return validacao;
	}

	public void setValidacao(int validacao) {
		this.validacao = validacao;
	}

	public String getDescricaoBO() {
		return descricaoBO;
	}

	public void setDescricaoBO(String descricaoBO) {
		this.descricaoBO = descricaoBO;
	}

	public String getDescricaoTAXA() {
		return descricaoTAXA;
	}

	public void setDescricaoTAXA(String descricaoTAXA) {
		this.descricaoTAXA = descricaoTAXA;
	}

	public String getDescricaoValidacao() {
		return descricaoValidacao;
	}

	public void setDescricaoValidacao(String descricaoValidacao) {
		this.descricaoValidacao = descricaoValidacao;
	}

	public Motivo getMotivo() {
		return motivo;
	}

	public void setMotivo(Motivo motivo) {
		this.motivo = motivo;
	}

	public String getDescricaoComentario() {
		return descricaoComentario;
	}

	public void setDescricaoComentario(String descricaoComentario) {
		this.descricaoComentario = descricaoComentario;
	}

	public int getDOC1Valido() {
		return DOC1Valido;
	}

	public void setDOC1Valido(int dOC1Valido) {
		DOC1Valido = dOC1Valido;
	}

	public int getDOC2Valido() {
		return DOC2Valido;
	}

	public void setDOC2Valido(int dOC2Valido) {
		DOC2Valido = dOC2Valido;
	}

	public String getDescricaoDOC1() {
		return descricaoDOC1;
	}

	public void setDescricaoDOC1(String descricaoDOC1) {
		this.descricaoDOC1 = descricaoDOC1;
	}

	public String getDescricaoDOC2() {
		return descricaoDOC2;
	}

	public void setDescricaoDOC2(String descricaoDOC2) {
		this.descricaoDOC2 = descricaoDOC2;
	}

}
