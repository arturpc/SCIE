package br.gov.df.dftrans.scie.domain;

import java.util.Date;

import javax.faces.context.FacesContext;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.servlet.http.HttpServletRequest;

@Entity
@Table(name = "tb_log_banco")
@NamedQueries({
		@NamedQuery(name = LogAlteracaoBanco.LOG_FIND_BY_ID, query = "SELECT l FROM LogAlteracaoBanco l WHERE l.id = :id") })
public class LogAlteracaoBanco {

	public static final String LOG_FIND_BY_ID = "LogAlteracaoBanco.findById";

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	@Column(name = "id_log_banco")
	private int id;

	@Column(name = "ds_ip")
	private String ip;

	@Column(name = "ds_operacao")
	private String operacao;

	@Column(name = "ds_tabela")
	private String tabela;

	@Column(name = "id_linha")
	private int idLinha;

	@Column(name = "dt_atualizacao")
	@Temporal(value = TemporalType.TIMESTAMP)
	private Date data;

	// construtores
	public LogAlteracaoBanco() {
	}

	public LogAlteracaoBanco(String operacao, String tabela, int idLinha) {
		HttpServletRequest httpServletRequest = (HttpServletRequest) FacesContext.getCurrentInstance()
				.getExternalContext().getRequest();
		setIp(httpServletRequest.getRemoteAddr());
		setOperacao(operacao);
		setTabela(tabela);
		setIdLinha(idLinha);
		setData(new Date());
	}

	// sobrescrita toString
	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("Id = " + getId() + "\n");
		sb.append("Ip = " + getIp() + "\n");
		sb.append("Operacao = " + getOperacao() + "\n");
		sb.append("Tabela = " + getTabela() + "\n");
		sb.append("Linha = " + getIdLinha() + "\n");
		sb.append("Data = " + getData() + "\n");
		return sb.toString();
	}

	// getteres and setteres
	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getIp() {
		return ip;
	}

	public void setIp(String ip) {
		this.ip = ip;
	}

	public String getOperacao() {
		return operacao;
	}

	public void setOperacao(String operacao) {
		this.operacao = operacao;
	}

	public String getTabela() {
		return tabela;
	}

	public void setTabela(String tabela) {
		this.tabela = tabela;
	}

	public int getIdLinha() {
		return idLinha;
	}

	public void setIdLinha(int idLinha) {
		this.idLinha = idLinha;
	}

	public Date getData() {
		return data;
	}

	public void setData(Date data) {
		this.data = data;
	}
}
