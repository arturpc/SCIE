package br.gov.df.dftrans.scie.domain;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;

import org.hibernate.validator.constraints.Email;

import br.gov.df.dftrans.scie.annotations.StringFormatter;
import br.gov.df.dftrans.scie.annotations.StringUpperCase;
import br.gov.df.dftrans.scie.utils.GeradorUsuario;

@Entity
@Table(name = "tb_usuario")
@NamedQueries({
		@NamedQuery(name = Usuario.USUARIO_FIND_BY_AUTENTICACAO, query = "SELECT u FROM Usuario u WHERE u.login = :login and u.senha = :senha "),
		@NamedQuery(name = Usuario.USUARIO_GET_ALL, query = "SELECT u FROM Usuario u"),
		@NamedQuery(name = Usuario.USUARIO_FIND_BY_EMAIL, query = "SELECT u FROM Usuario u where u.email = :email"),
		@NamedQuery(name = Usuario.USUARIO_FIND_BY_LOGIN, query = "SELECT u FROM Usuario u where u.login = :login") })
public class Usuario implements Serializable {

	public static final String USUARIO_FIND_BY_AUTENTICACAO = "Usuario.getByAutenticacao";
	public static final String USUARIO_FIND_BY_EMAIL = "Usuario.getByEmail";
	public static final String USUARIO_FIND_BY_LOGIN = "Usuario.getByLogin";
	public static final String USUARIO_GET_ALL = "Usuario.getAll";

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	@Column(name = "id_usuario")
	private int id;

	@Column(name = "nm_login", unique = true)
	private String login;

	@Column(name = "ds_senha")
	private String senha;

	@Column(name = "ds_email")
	@Email
	private String email;

	@StringUpperCase
	@StringFormatter
	@Column(name = "ds_nome")
	private String nome;

	// 0 = instituicao
	// 1 = administrador
	// 2 = validador
	@Column(name = "st_perfil")
	private int perfil;

	@Column(name = "st_reset_senha")
	private int reset = 0;

	// construtores
	public Usuario() {
	}

	public Usuario(String login, String senha, String email, String nome, int perfil) {
		setLogin(login);
		setSenha(senha);
		setEmail(email);
		setNome(nome);
		setPerfil(perfil);
	}

	public Usuario(String email, String nome, int perfil) {
		setLogin(GeradorUsuario.gerarLogin(nome));
		setSenha(GeradorUsuario.gerarSenha());
		setEmail(email);
		setNome(nome);
		setPerfil(perfil);
	}

	// sobrescrita hash code and equals

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
		Usuario other = (Usuario) obj;
		if (id != other.id)
			return false;
		return true;
	}

	// sobrescrita toString

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("Id = " + getId() + "\n");
		sb.append("Login = " + getLogin() + "\n");
		sb.append("Senha = " + getSenha() + "\n");
		sb.append("Nome = " + getNome() + "\n");
		return sb.toString();
	}

	// getteres and setteres
	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getLogin() {
		return login;
	}

	public void setLogin(String login) {
		this.login = login;
	}

	public String getSenha() {
		return senha;
	}

	public void setSenha(String senha) {
		this.senha = senha;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getNome() {
		return nome;
	}

	public void setNome(String nome) {
		this.nome = nome;
	}

	public int getReset() {
		return reset;
	}

	public void setReset(int reset) {
		this.reset = reset;
	}

	public int getPerfil() {
		return perfil;
	}

	public void setPerfil(int perfil) {
		this.perfil = perfil;
	}

}
