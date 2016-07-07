package br.gov.df.dftrans.scie.domain;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToOne;
import javax.persistence.Table;

import org.hibernate.validator.constraints.Email;
import org.hibernate.validator.constraints.br.CPF;

import br.gov.df.dftrans.scie.annotations.StringFormatter;
import br.gov.df.dftrans.scie.annotations.StringUpperCase;

@Entity
@Table(name = "tb_representante_instituicao")
@NamedQueries({ @NamedQuery(name = Representante.REPRESENTANTE_GET_ALL, 
		query = "SELECT r FROM Representante r where r.ativo = 1"),
		@NamedQuery(name = Representante.REPRESENTANTE_FIND_BY_CPF, 
		query = "SELECT r FROM Representante r WHERE r.cpf = :cpf and r.ativo = 1"),
		@NamedQuery(name = Representante.REPRESENTANTE_FIND_BY_USER, 
		query = "SELECT r FROM Representante r WHERE r.usuario = :usuario and r.ativo = 1"),
		@NamedQuery(name = Representante.REPRESENTANTE_FIND_BY_ID, 
		query = "SELECT r FROM Representante r WHERE r.id = :id and r.ativo = 1") })
public class Representante implements Serializable {

	public static final String REPRESENTANTE_GET_ALL = "Representante.getAll";
	public static final String REPRESENTANTE_FIND_BY_CPF = "Representante.consultarPorCPF";
	public static final String REPRESENTANTE_FIND_BY_ID = "Representante.consultarPorCodigo";
	public static final String REPRESENTANTE_FIND_BY_USER = "Representante.consultarPorUsuario";

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	@Column(name = "id_representante")
	private int id;

	@StringUpperCase
	@StringFormatter
	@Column(name = "nm_representante")
	private String nome;

	@Column(name = "nr_cpf", unique = true)
	@CPF
	private String cpf;

	// Diretor = 1
	// Secretário = 2
	@Column(name = "st_cargo")
	private int cargo;

	@Column(name = "ds_email")
	@Email
	private String email;

	@Column(name = "ds_telefone")
	private String telefone;
	
	@Column(name = "dt_nascimento")
	private Date dataNascimento;
	
	//st_ativo = 1 (Ativo)
	//st_ativo = 0 (Inativo)
	@Column(name = "st_ativo")
	private int ativo = 1;
	
	//st_cadastro = 0 (Não Aprovado)
	//st_cadastro = 1 (Aprovado)
	@Column(name = "st_cadastro")
	private int cadastro = 0;
	
	@OneToOne
	@JoinColumn(name = "id_instituicao", referencedColumnName = "id_instituicao")
	private InstituicaoEnsino instituicao;
	
	@OneToOne(cascade = CascadeType.ALL)
	@JoinColumn(name = "id_usuario", referencedColumnName = "id_usuario")
	private Usuario usuario;

	// construtores
	public Representante() {
	}

	public Representante(String nome, String cpf, int cargo, 
			String email, String telefone, Date dataNascimento, 
			InstituicaoEnsino instituicao) {
		setNome(nome);
		setCpf(cpf);
		setCargo(cargo);
		setEmail(email);
		setTelefone(telefone);
		setDataNascimento(dataNascimento);
		setInstituicao(instituicao);
	}

	// sobrescrita toString

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("Id = " + getId() + "\n");
		sb.append("Nome = " + getNome() + "\n");
		sb.append("CPF = " + getCpf() + "\n");
		sb.append("Cargo = " + getCargo() + "\n");
		sb.append("Email = " + getEmail() + "\n");
		sb.append("Telefones = " + telefone + "\n");
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
		Representante other = (Representante) obj;
		if (id != other.id){
			return false;
		}
		return true;
	}

	/**
	 * cria um List de string, separando os telefones do Representante
	 * 
	 * @return List de String que representam os telefones do Representante
	 */
	public List<String> getTelefone() {
		ArrayList<String> contato = new ArrayList<String>();
		if (telefone != null) {
			String[] aux = telefone.split(",");
			for (String s : aux) {
				contato.add(s);
			}
		}
		return contato;
	}

	/**
	 * Formata o DDD e retorna o arrayList de telefones em string
	 * 
	 * @return nova String de telefones
	 */
	public String getTelefoneString() {
		ArrayList<String> temp = (ArrayList<String>) getTelefone();
		String retorno = "";
		for (String s : temp) {
			retorno += "(" + s.substring(0, 2) + ")" + s.substring(2, 6) 
			+ "-" + s.substring(6, s.length()) + "\n";
		}
		return retorno.substring(0, retorno.length());
	}

	/**
	 * Formatação para impressão do campo telefone
	 * 
	 * @return uma String que representa os telefones formatados
	 */
	public String getTelefoneString2() {
		ArrayList<String> temp = (ArrayList<String>) getTelefone();
		String retorno = "";
		for (String s : temp) {
			if (temp.size() < 9) {
				retorno += "61" + s.replaceAll("\"", "") + ",";
			}
		}
		if (retorno.length() > 1) {
			retorno = retorno.substring(0, retorno.length() - 1);
		}
		if ("61".equals(retorno)) {
			return null;
		} else {
			return retorno;
		}
	}

	// getteres and setteres
	public void setTelefone(String telefone) {
		this.telefone = telefone;
	}

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

	public String getCpf() {
		return cpf;
	}

	public void setCpf(String cpf) {
		this.cpf = cpf;
	}

	public int getCargo() {
		return cargo;
	}

	public void setCargo(int cargo) {
		this.cargo = cargo;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public void setTelefone(List<String> telefone) {
		String contato = "";
		for (String s : telefone) {
			contato += s + ",";
		}
		this.telefone = contato.substring(0, contato.length() - 1);
	}

	public Date getDataNascimento() {
		return dataNascimento;
	}

	public void setDataNascimento(Date dataNascimento) {
		this.dataNascimento = dataNascimento;
	}

	public int getAtivo() {
		return ativo;
	}

	public void setAtivo(int ativo) {
		this.ativo = ativo;
	}

	public int getCadastro() {
		return cadastro;
	}

	public void setCadastro(int cadastro) {
		this.cadastro = cadastro;
	}

	public InstituicaoEnsino getInstituicao() {
		return instituicao;
	}

	public void setInstituicao(InstituicaoEnsino instituicao) {
		this.instituicao = instituicao;
	}

	public Usuario getUsuario() {
		return usuario;
	}

	public void setUsuario(Usuario usuario) {
		this.usuario = usuario;
	}

	
}
