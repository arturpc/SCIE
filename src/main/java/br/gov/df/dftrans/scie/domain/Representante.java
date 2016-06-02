//?
package br.gov.df.dftrans.scie.domain;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;

import org.hibernate.validator.constraints.Email;
import org.hibernate.validator.constraints.br.CPF;

import br.gov.df.dftrans.scie.annotations.StringFormatter;
import br.gov.df.dftrans.scie.annotations.StringUpperCase;

@Entity
@Table(name = "tb_representante_instituicao")
@NamedQueries({ @NamedQuery(name = Representante.REPRESENTANTE_GET_ALL, query = "SELECT r FROM Representante r"),
		@NamedQuery(name = Representante.REPRESENTANTE_FIND_BY_CPF, query = "SELECT r FROM Representante r WHERE r.cpf = :cpf"),
		@NamedQuery(name = Representante.REPRESENTANTE_FIND_BY_ID, query = "SELECT r FROM Representante r WHERE r.id = :id") })
public class Representante implements Serializable {

	public static final String REPRESENTANTE_GET_ALL = "Representante.getAll";
	public static final String REPRESENTANTE_FIND_BY_CPF = "Representante.consultarPorCPF";
	public static final String REPRESENTANTE_FIND_BY_ID = "Representante.consultarPorCodigo";

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

	// construtores
	public Representante() {
	}

	public Representante(String nome, String cpf, int cargo, String email, String telefone) {
		setNome(nome);
		setCpf(cpf);
		setCargo(cargo);
		setEmail(email);
		setTelefone(telefone);
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
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Representante other = (Representante) obj;
		if (id != other.id)
			return false;
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
			retorno += "(" + s.substring(0, 2) + ")" + s.substring(2, 6) + "-" + s.substring(6, s.length()) + "\n";
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
		if (retorno.equals("61")) {
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

}
