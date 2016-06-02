package br.gov.df.dftrans.scie.domain;


public class CEP{
	private String uf;
	private String cidade;
	private String bairro;
	private String logradouro;
	private String complemento;
	

	//sobrescrita toString
	@Override
	public String toString() {
			StringBuilder sb = new StringBuilder();
			sb.append("CEP nos Correios: \n");
			sb.append("UF : "+getUf()+"\n");
			sb.append("Cidade : "+getCidade()+"\n");
			sb.append("Bairro : "+getBairro()+"\n");
			sb.append("Logradouro : "+getLogradouro()+"\n");
			sb.append("Complemento : "+getComplemento()+"\n");
		return sb.toString();
	}
	
	//getteres and setteres
	public String getUf() {
		return uf;
	}
	
	public void setUf(String uf) {
		this.uf = uf;
	}
	public String getCidade() {
		return cidade;
	}
	public void setCidade(String cidade) {
		this.cidade = cidade;
	}
	public String getBairro() {
		return bairro;
	}
	public void setBairro(String bairro) {
		this.bairro = bairro;
	}
	public String getLogradouro() {
		return logradouro;
	}
	public void setLogradouro(String logradouro) {
		this.logradouro = logradouro;
	}

	public String getComplemento() {
		return complemento;
	}

	public void setComplemento(String complemento) {
		this.complemento = complemento;
	}
	
	
	
}
