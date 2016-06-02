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
import javax.persistence.Table;

import org.hibernate.validator.constraints.br.CPF;

@Entity
@Table(name = "tb_autorizacao_representante")
@NamedQueries({
    @NamedQuery(name = AutorizacaoRepresentante.AUTORIZACAO_GET_ALL, query = "SELECT a FROM AutorizacaoRepresentante a"),
    @NamedQuery(name = AutorizacaoRepresentante.AUTORIZACAO_FIND_BY_CPF, 
    	query = "SELECT a FROM AutorizacaoRepresentante a WHERE a.cpf = :cpf and a.instituicao.id = :id_instituicao"),
    @NamedQuery(name = AutorizacaoRepresentante.AUTORIZACAO_FIND_BY_CPF_ONLY, 
	query = "SELECT a FROM AutorizacaoRepresentante a WHERE a.cpf = :cpf"),
    @NamedQuery(name = AutorizacaoRepresentante.AUTORIZACAO_FIND_BY_ID, query = "SELECT a FROM AutorizacaoRepresentante a WHERE a.id = :id")
})
public class AutorizacaoRepresentante implements Serializable{

	public static final String AUTORIZACAO_GET_ALL = "AutorizacaoRepresentante.getAll";
	public static final String AUTORIZACAO_FIND_BY_CPF = "AutorizacaoRepresentante.consultarPorCPF";
	public static final String AUTORIZACAO_FIND_BY_CPF_ONLY = "AutorizacaoRepresentante.consultarPorCPFApenas";
	public static final String AUTORIZACAO_FIND_BY_ID = "AutorizacaoRepresentante.consultarPorCodigo";
	
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	@Column(name="id_autorizacao_representante")
	private int id;
	
	@Column(name="nr_cpf")
	@CPF
	private String cpf;
	
	@ManyToOne
	@JoinColumn(name="id_instituicao",referencedColumnName="id_instituicao")
	private InstituicaoEnsino instituicao;

	//construtores
	public AutorizacaoRepresentante(){
	}
	
	public AutorizacaoRepresentante(String cpf, InstituicaoEnsino instituicao){
		setCpf(cpf);
		setInstituicao(instituicao);
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
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		AutorizacaoRepresentante other = (AutorizacaoRepresentante) obj;
		if (id != other.id)
			return false;
		return true;
	}
	//sobrescrita toString
	
	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("Id = "+getId()+"\n");
		sb.append("CPF = "+getCpf()+"\n");
		sb.append("Instituicao = "+getInstituicao()+"\n");
		return sb.toString();
	}
	
	//getteres and setteres
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

	public InstituicaoEnsino getInstituicao() {
		return instituicao;
	}

	public void setInstituicao(InstituicaoEnsino instituicao) {
		this.instituicao = instituicao;
	}
	
}
