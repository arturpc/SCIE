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

import br.gov.df.dftrans.scie.annotations.StringFormatter;
import br.gov.df.dftrans.scie.annotations.StringUpperCase;
import br.gov.df.dftrans.scie.dao.UFDAO;

@Entity
@Table(name = "tb_cidade")
@NamedQueries({
    @NamedQuery(name = Cidade.CIDADE_GET_ALL, query = "SELECT c FROM Cidade c"),
    @NamedQuery(name = Cidade.CIDADE_FIND_BY_NOME, 
    	query = "SELECT c FROM Cidade c WHERE c.nome = :nome"),
    @NamedQuery(name = Cidade.CIDADE_FIND_BY_NOME_UF, 
	query = "SELECT c FROM Cidade c WHERE c.nome = :nome and c.uf.uf = :uf"),
    @NamedQuery(name = Cidade.CIDADE_FIND_BY_ID, query = "SELECT c FROM Cidade c WHERE c.id = :id")
    })
public class Cidade implements Serializable {
	

	public static final String CIDADE_GET_ALL = "Cidade.getAll";
	public static final String CIDADE_FIND_BY_NOME = "Cidade.consultarPorNome";
	public static final String CIDADE_FIND_BY_NOME_UF = "Cidade.consultarPorNomeUf";
	public static final String CIDADE_FIND_BY_ID = "Cidade.consultarPorCodigo";
	
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	@Column(name="id_cidade")
	private int id;
	
	@StringUpperCase
	@StringFormatter
	@Column(name="nm_cidade")
	private String nome;
	
	@ManyToOne(cascade = CascadeType.ALL)
	@JoinColumn(name="id_uf",referencedColumnName="id_uf")
	private UF uf;
	
	//construtores
	public Cidade() {
	}
	
	/**
	 * Método Construtor
	 * @param id
	 * @param nome
	 * @param uf
	 */
	public Cidade(int id, String nome, UF uf){
		this.id = id;
		this.nome = nome;
		this.uf = uf;
	}
	
	/**
	 * Método Construtor
	 * @param id
	 * @param nome
	 * @param uf
	 */
	public Cidade(int id, String nome, String uf){
		this.id = id;
		this.nome = nome;
		UFDAO dao = new UFDAO();
		this.uf = dao.getByUF(uf);
	}
	
	/**
	 * Método Construtor
	 * @param nome
	 * @param uf
	 */
	public Cidade(String nome, String uf){
		this.nome = nome;
		UFDAO dao = new UFDAO();
		this.uf = dao.getByUF(uf);
	}
	
	public Cidade(String nome, UF uf){
		this.nome = nome;
		this.uf = uf;
	}
	
	//hashCode and equals
	
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
		Cidade other = (Cidade) obj;
		if (id != other.id){
			return false;
		}
		return true;
	}


	//sobrescrita toString
	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("Id = "+getId()+"\n");
		sb.append("Nome = "+getNome()+"\n");
		sb.append("Uf = "+getUf()+"\n");
		return sb.toString();
	}
	
	
	//getteres and setteres
	
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public String getNome() {
		return nome;
	}
	public void setNome(String nome) {
		this.nome = nome;
	}
	public UF getUf() {
		return uf;
	}
	public void setUf(UF uf) {
		this.uf = uf;
	}
	
}
