package br.gov.df.dftrans.scie.beans;

import javax.el.ELResolver;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.context.FacesContext;
import javax.servlet.http.HttpSession;
import org.hibernate.validator.constraints.NotEmpty;
import org.hibernate.validator.constraints.br.CPF;

import br.gov.df.dftrans.scie.dao.PrioridadeDAO;
import br.gov.df.dftrans.scie.dao.SelectCPFDAO;
import br.gov.df.dftrans.scie.domain.Prioridade;
import br.gov.df.dftrans.scie.exceptions.InsertException;
import br.gov.df.dftrans.scie.utils.FacesUtil;
import br.gov.df.dftrans.scie.utils.Parametros;

@ManagedBean(name = "EstudanteMB")
@SessionScoped
public class EstudanteBean {

	@CPF(message = "CPF informado inválido. Por favor informe um CPF válido")
	@NotEmpty(message = "CPF é um campo obrigatório!!!")
	private String codCPF, codCPFsemMascara;
	private String valorTaxaAtual = Parametros.getParameter("vl_taxa");
	private String valorContaAtual = Parametros.getParameter("nr_conta");
	private String delimitadorDiretorio = Parametros.getParameter("delimitador_diretorios");
	private String delimitadorDiretorioREGEX;
	private boolean termo = false;
	private String nome;
	private String[][] agendamentos;
	@NotEmpty(message = "Senha é um campo obrigatório!!!")
	private String senha;

	public EstudanteBean() {
		setDelimitadorDiretorioREGEX();
	}

	/**
	 * Método responsável por tratar caracteres reservados em expresões
	 * regulares
	 */
	public void setDelimitadorDiretorioREGEX() {
		if (".\\dDwW*+?sS^$|".contains(delimitadorDiretorio)) {
			delimitadorDiretorioREGEX = "\\" + delimitadorDiretorio;
		} else {
			delimitadorDiretorioREGEX = delimitadorDiretorio;
		}
	}

	/**
	 * Pesquisa por alguma solicitação de segunda via pendente para o dado cpf
	 * 
	 * @return redirecionamento para a próxima página
	 */
	public String pesquisar() {
		FacesContext context = FacesContext.getCurrentInstance();
		ELResolver resolver = context.getApplication().getELResolver();
		AutenticacaoBean meuBean = (AutenticacaoBean) resolver
				.getValue(context.getELContext(), null, "loginBean");
		if (!meuBean.isCaptcha()) {
			FacesUtil.addMsggError("Digite os números no campo abaixo");
			return "/pages/estudanteHome.xhtml?faces-redirect=false";
		}
		codCPFsemMascara = removeMascara(codCPF);
		if (SelectCPFDAO.getSegundaVia(codCPFsemMascara)) {
			HttpSession session = (HttpSession) FacesContext.
					getCurrentInstance().getExternalContext().getSession(false);
			session.setAttribute("estudante", codCPFsemMascara);
			if(SelectCPFDAO.getAutenticacaoSITPASS(codCPFsemMascara, getSenha())){
				return "/pages/estudante/termoUso.xhtml?faces-redirect=true";
			}else{
				FacesUtil.addMsggError("A senha não confere com o CPF " + codCPF
						+ "! Verifique se o CPF e a "
						+ "Senha digitados estão corretos!");
				return "/pages/estudanteHome.xhtml?faces-redirect=false";
			}
		} else {
			FacesUtil.addMsggError("O CPF " + codCPF
					+ " não está cadastrado ou já possui solicitação pendente! "
					+ "Verifique se o CPF digitado está correto!");
			return "/pages/estudanteHome.xhtml?faces-redirect=false";
		}
	}

	/**
	 * Pesquisa por alguma solicitação de prioridade pendente para o dado cpf
	 * 
	 * @return redirecionamento para a próxima página
	 */
	public String pesquisarPrioridade() {
		FacesContext context = FacesContext.getCurrentInstance();
		ELResolver resolver = context.getApplication().getELResolver();
		AutenticacaoBean meuBean = (AutenticacaoBean) resolver.
				getValue(context.getELContext(), null, "loginBean");
		if (!meuBean.isCaptcha()) {
			FacesUtil.addMsggError("Digite os números no campo abaixo");
			return "/pages/estudanteNovo.xhtml?faces-redirect=false";
		}
		PrioridadeDAO dao = PrioridadeDAO.PrioridadeDAO();
		codCPFsemMascara = removeMascara(codCPF);
		if (dao.Exists(codCPFsemMascara)) {
			FacesUtil.addMsggError(
					"O CPF " + codCPF + " já possui solicitação pendente! "
						+ "Verifique se o CPF digitado está correto!");
			return "/pages/estudanteNovo.xhtml?faces-redirect=false";
		}
		Prioridade p = new Prioridade();

		p.setCpf(codCPFsemMascara);
		try {
			dao.add(p);
		} catch (InsertException e) {
			FacesUtil.addMsggError(
					"O CPF " + codCPF + " já possui solicitação pendente! "
						+ "Verifique se o CPF digitado está correto!");
			return "/pages/estudanteNovo.xhtml?faces-redirect=false";
		}
		HttpSession session = (HttpSession) FacesContext.getCurrentInstance()
				.getExternalContext().getSession(false);
		session.setAttribute("estudante", codCPFsemMascara);
		return "/pages/estudante/confirmacaoPrioridade.xhtml?faces-redirect=false";
	}

	/**
	 * Pesquisa por alguma solicitação de extensão de acessos ainda pendente
	 * para o dado cpf
	 * 
	 * @return redirecionamento para a próxima página
	 */
	public String pesquisarAcessos() {
		FacesContext context = FacesContext.getCurrentInstance();
		ELResolver resolver = context.getApplication().getELResolver();
		AutenticacaoBean meuBean = (AutenticacaoBean) resolver
				.getValue(context.getELContext(), null, "loginBean");
		if (!meuBean.isCaptcha()) {
			FacesUtil.addMsggError("Digite os números no campo abaixo");
			return "/pages/estudanteAcessosHome.xhtml?faces-redirect=false";
		}
		codCPFsemMascara = removeMascara(codCPF);
		if (SelectCPFDAO.getAcessos(codCPFsemMascara)) {
			HttpSession session = (HttpSession) FacesContext
					.getCurrentInstance().getExternalContext()
					.getSession(false);
			session.setAttribute("estudante", codCPFsemMascara);
			if(SelectCPFDAO.getAutenticacaoSITPASS(codCPFsemMascara, getSenha())){
				return "/pages/estudante/termoUsoAcesso.xhtml?faces-redirect=true";
			}else{
				FacesUtil.addMsggError("A senha não confere com o CPF " + codCPF
						+ "! Verifique se o CPF e a "
						+ "Senha digitados estão corretos!");
				return "/pages/estudanteAcessosHome.xhtml?faces-redirect=false";
			}
		} else {
			FacesUtil.addMsggError("O CPF " + codCPF
					+ " não está cadastrado ou já possui solicitação pendente! "
					+ "Verifique se o CPF digitado está correto!");
			return "/pages/estudanteAcessosHome.xhtml?faces-redirect=false";
		}
	}

	/**
	 * Pesquisa pelos agendamentos já realizados para o dado cpf e o dado nome
	 * 
	 * @return redirecionamento para a próxima página
	 */
	public String pesquisarAgendamentos() {
		FacesContext context = FacesContext.getCurrentInstance();
		ELResolver resolver = context.getApplication().getELResolver();
		AutenticacaoBean meuBean = (AutenticacaoBean) resolver
				.getValue(context.getELContext(), null, "loginBean");
		if (!meuBean.isCaptcha()) {
			FacesUtil.addMsggError("Digite os números no campo abaixo");
			return "/pages/estudanteAgendamento.xhtml?faces-redirect=false";
		}
		codCPFsemMascara = removeMascara(codCPF);
		if (!(SelectCPFDAO.contains(codCPFsemMascara))) {
			FacesUtil.addMsggError("O CPF " + codCPF + " não está cadastrado, "
					+ "ou ainda não foi processado!");
			return "/pages/estudanteAgendamento.xhtml?faces-redirect=false";
		} else {
			HttpSession session = (HttpSession) FacesContext
					.getCurrentInstance().getExternalContext()
					.getSession(false);
			session.setAttribute("estudante", codCPFsemMascara);
			if(SelectCPFDAO.getAutenticacaoSITPASS(codCPFsemMascara, getSenha())){
				return "/pages/estudante/"
						+ "consultarAgendamento.xhtml?faces-redirect=true";
			}else{
				FacesUtil.addMsggError("A senha não confere com o CPF " + codCPF
						+ "! Verifique se o CPF e a "
						+ "Senha digitados estão corretos!");
				return "/pages/estudanteAgendamento.xhtml?faces-redirect=false";
			}
		}
	}

	/**
	 * Faz uma confirmação se o usuário aceitou o termo
	 * 
	 * @return redirecionamento de acordo com a resposta
	 */
	public String verificarTermo() {
		if (!getTermo()) {
			FacesUtil.addMsggError("Você deve aceitar o Termo para continuar!");
			return "/pages/estudante/termoUso.xhtml?faces-redirect=false";
		} else {
			return "/pages/estudante/estudante2ViaArquivos.xhtml?faces-redirect=true";
		}
	}

	/**
	 * Faz uma confirmação se o usuário aceitou o termo
	 * 
	 * @return redirecionamento de acordo com a resposta
	 */

	public String verificarTermoAcessos() {
		if (!getTermo()) {
			FacesUtil.addMsggError("Você deve aceitar o Termo para continuar!");
			return "/pages/estudante/termoUsoAcesso.xhtml?faces-redirect=false";
		} else {
			return "/pages/estudante/"
			+ "estudanteAcessosArquivos.xhtml?faces-redirect=true";
		}
	}

	/**
	 * Remove caracteres especiais
	 * 
	 * @param campo
	 * @return a string inicial sem mask
	 */
	private String removeMascara(String campo) {
		String resultado = campo;
		resultado = resultado.replace("-", "");
		resultado = resultado.replace(".", "");
		resultado = resultado.replace("(", "");
		resultado = resultado.replace(")", "");
		resultado = resultado.replace(" ", "");
		resultado = resultado.replace(delimitadorDiretorioREGEX, "");
		return resultado;
	}

	public String textoAgendamentos() {
		String retorno = "";
		String nroCartao;
		String nroCPF;
		String s;
		setAgendamentos(SelectCPFDAO.getAgendamento(codCPFsemMascara, nome));
		for (int i = 0; i < getAgendamentos().length; i++) {
			if (getAgendamentos()[i][2] != null) {
				retorno += "<hr><h2>Prezado(a) " + 
			getAgendamentos()[i][0] + ",<br/></h2>"
						+ "<font color=\"red\"><b>Parab&eacute;ns, "
						+ "seu Passe Livre Estudantil est&aacute; "
						+ "liberado!</b></font><br/><br/>"
						+ "Seu cart&atilde;o ser&aacute; entregue "
						+ "no <font color=\"red\"><b>"+ 
						getAgendamentos()[i][5] +""
								+ "</b></font>.<br/><br/>"
						+ "Mas aten&ccedil;&atilde;o: Para "
						+ "<font color=\"red\"><b>maiores de 18 anos"
						+ "</b></font>, "
						+ "a entrega s&oacute; ser&aacute; realizada "
						+ "mediante a presen&ccedil;a do estudante, "
						+ "portando documento de <font color=\"red\">"
						+ "<b>identidade "
						+ "e CPF</b></font>. Ningu&eacute;m poder&aacute; "
						+ "retirar por voc&ecirc;.<br/>"
						+ "Para estudantes <font color=\"red\"><b>menores "
						+ "de 18 anos</b></font>, o pai, a m&atilde;e ou o "
						+ "respons&aacute;vel legal poder&aacute; receber "
						+ "o cart&atilde;o, mediante a "
						+ "apresenta&ccedil;&atilde;o "
						+ "do documento de <font color=\"red\">"
						+ "<b>identidade "
						+ "e CPF pr&oacute;prios e do estudante."
						+ "</b></font><br/><br/>"
						+ "<div align=\"center\">A entrega do "
						+ "seu cart&atilde;o est&aacute; agendada "
						+ "para:<br/>"
						+ "<font color=\"red\"><b>Dia " + 
						getAgendamentos()[i][2].substring(6, 8) + "/"
						+ getAgendamentos()[i][2].substring(4, 6) 
						+ "/" + getAgendamentos()[i][2].substring(0, 4)
						+ " no per&iacute;odo entre " + 
						getAgendamentos()[i][3] 
								+ " (" + getAgendamentos()[i][4]
						+ ").</b></font></div><br/>"
						+ "Fique atento para a data de entrega do ser "
						+ "cart&atilde;o Passe Livre Estudantil.<br/><br/>"
						+ "<font color=\"red\"><b>N&atilde;o perca "
						+ "esta data!!!</b></font><br/><br/>"
						+ "<font size=\"1\">Atenciosamente,<br/>"
						+ "Transporte Urbano do Distrito Federal - "
						+ "DFTRANS<br/>"
						+ "N&atilde;o responda a esta mensagem. Para maiores esclarecimentos, acesse o site <a href=\"http://www.passelivreestudantil.df.gov.br\">www.passelivreestudantil.df.gov.br</a> ou ligue 156, escolha a op&ccedil;&atilde;o 4 "
						+ "e depois escolha a op&ccedil;&atilde;o "
						+ "1.</font><br/><br/><hr/>";
			}
		}
		setAgendamentos(SelectCPFDAO.getCargaEmbarcada(codCPFsemMascara, nome));
		for (int i = 0; i < getAgendamentos().length; i++) {
			s = getAgendamentos()[i][1];
			nroCartao = s.substring(0, 1) + "." + 
			s.substring(1, 4) + "." + s.substring(4, 7) + "."
					+ s.substring(7, 10);
			s = getAgendamentos()[i][2];
			nroCPF = s.substring(0, 3) + "." 
			+ s.substring(3, 6) + "." 
			+ s.substring(6, 9) + "-" 
			+ s.substring(9, 11);
			retorno += "<hr><h2>Prezado(a) " 
			+ getAgendamentos()[i][0] + ",<br/></h2>"
					+ "<font color=\"red\"><b>Parab&eacute;ns, "
					+ "seu Passe Livre Estudantil j&aacute; "
					+ "est&aacute; liberado!</b></font><br/><br/>"
					+ "Informamos que seu cart&atilde;o de "
					+ "n&uacute;mero " + nroCartao
					+ " est&aacute; atualizado e desbloqueado para uso!<br/>"
					+ "<font color=\"red\"><b>ATEN&Ccedil;&Atilde;O!!! "
					+ "No primeiro acesso voc&ecirc; receber&aacute; "
					+ "a mensagem: \"CARTAO REGRAVADO COM AS CONFIGURACOES "
					+ "DO MES ATUAL\". "
					+ "Ser&aacute; necess&aacute;rio passar novamente o "
					+ "cart&atilde;o para liberar a catraca (apenas no "
					+ "primeiro acesso).</b></font><br/>"
					+ "Seguem suas informa&ccedil;&otilde;es "
					+ "para confer&ecirc;ncia:<br/><br/>"
					+ "<div align=\"center\">" + "Nome: "
					+ "<font color=\"red\"><b>" + 
					getAgendamentos()[i][0]
					+ "</b></font><br/>" + "CPF: <font "
							+ "color=\"red\"><b>" + 
					nroCPF + "</b></font><br/>"
					+ "N&uacute;mero do cart&atilde;o: "
					+ "<font color=\"red\"><b>" + nroCartao + "</b></font><br/>"
					+ "</div><br/><br/>"
					+ "Caso os dados acima n&atilde;o estejam corretos confira seu cadastro no site <a href=\"http://www.passelivreestudantil.df.gov.br\">www.passelivreestudantil.df.gov.br</a></br><br/>"
					+ "<font size=\"1\">Atenciosamente,<br/>" + 
					"Transporte Urbano do Distrito Federal - DFTRANS<br/>"
					+ "N&atilde;o responda a esta mensagem. Para maiores esclarecimentos, acesse o site <a href=\"http://www.passelivreestudantil.df.gov.br\">www.passelivreestudantil.df.gov.br</a> ou ligue 156, escolha a op&ccedil;&atilde;o 4 "
					+ "e depois escolha a op&ccedil;&atilde;o 1.</font><hr/>";
		}
		if (retorno.isEmpty()) {
			retorno = "<hr><h2>Prezado(a) " + getNome() + ",<br/></h2>"
					+ "Lamentamos mas seu Passe Livre Estudantil ainda "
					+ "n&atilde;o est&aacute; agendado para entrega. "
					+ "Estamos providenciando este agendamento no "
					+ "prazo mais breve poss&iacute;vel!<br/><br/>"
					+ "<font size=\"1\">Atenciosamente,<br/>" + 
					"Transporte Urbano do Distrito Federal - DFTRANS<br/>"
					+ "N&atilde;o responda a esta mensagem. "
					+ "Para maiores esclarecimentos, acesse o site "
					+ "<a href=\"http://www.passelivreestudantil.df.gov.br\">"
					+ "www.passelivreestudantil.df.gov.br</a> "
					+ "ou ligue 156, escolha a op&ccedil;&atilde;o 4 "
					+ "e depois escolha a op&ccedil;&atilde;o "
					+ "1.</font><br/><br/><hr/>";
		}
		return retorno;
	}

	// getteres and setteres
	public String getCodCPF() {
		return codCPF;
	}

	public void setCodCPF(String codCPF) {
		this.codCPF = codCPF;
	}

	public boolean getTermo() {
		return termo;
	}

	public void setTermo(boolean termo) {
		this.termo = termo;
	}

	public String getValorTaxaAtual() {
		return valorTaxaAtual;
	}

	public void setValorTaxaAtual(String valorTaxaAtual) {
		this.valorTaxaAtual = valorTaxaAtual;
	}

	public String getNome() {
		return nome;
	}

	public void setNome(String nome) {
		this.nome = nome;
	}

	public String[][] getAgendamentos() {
		return agendamentos;
	}

	public void setAgendamentos(String[][] agendamentos) {
		this.agendamentos = agendamentos;
	}

	public String getSenha() {
		return senha;
	}

	public void setSenha(String senha) {
		this.senha = senha;
	}

	public String getValorContaAtual() {
		return valorContaAtual;
	}

	public void setValorContaAtual(String valorContaAtual) {
		this.valorContaAtual = valorContaAtual;
	}
}
