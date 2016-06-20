package br.gov.df.dftrans.scie.utils;

import java.io.File;
import java.io.Serializable;
import java.util.Properties;

import javax.activation.DataHandler;
import javax.activation.DataSource;
import javax.activation.FileDataSource;
import javax.mail.BodyPart;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;

import br.gov.df.dftrans.scie.domain.ExtensaoAcesso;
import br.gov.df.dftrans.scie.domain.Representante;
import br.gov.df.dftrans.scie.domain.Solicitacao;
import br.gov.df.dftrans.scie.domain.Usuario;

public class Mail implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private static String delimitadorDiretorio = Parametros.getParameter("delimitador_diretorios");
	private static String delimitadorDiretorioREGEX;

	/**
	 * Envia email para o usuário informando sua senha
	 * 
	 * @param usuario
	 */
	public static void sendEmailUser(Usuario usuario) {
		String titulo = "Cadastro SCIE - Usuário";
		String mensagem = "<b>Mensagem gerada automaticamente pelo sistema, n&atilde;o &eacute; necess&aacute;rio responder.</b>"
				+ "<br/>" + "<br/>" + "<br/>" + "Prezado(a) " + usuario.getNome() + ",<br/>"
				+ "Seu usu&aacute;rio foi criado com sucesso no Sistema de Cadastro de Institui&ccedil;&otilde;es de Ensino (SCIE) do DFTRANS;<br/>"
				+ "Para fazer o primeiro acesso pelo menu login utilize os dados abaixo:<br/>" + "<br/>"
				+ "<div style = \"padding-left:15em\"><b>Login = <i>" + usuario.getLogin() + "</i><br/>" + "Senha = <i>"
				+ usuario.getSenha() + "</i></b></div>" + "<br/>" + "<br/>"
				+ "Ap&oacute;s o primeiro acesso, o sistema solicitar&aacute; uma nova senha, escolhida ao seu crit&eacute;rio, que ser&aacute; usada nos demais acessos.<br/>"
				+ "Quaisquer d&uacute;vidas ou sugest&otilde;es utilize email <a href=\"mailto:ple@dftrans.df.gov.br\">ple@dftrans.df.gov.br</a> para nos contactar.<br/>"
				+ "Sua opini&atilde;o &eacute; muito importante para o aperfei&ccedil;oamento cont&iacute;nuo da presta&ccedil;&atilde;o dos nossos servi&ccedil;os.<br/>"
				+ "<br/>" + "Excelente Navega&ccedil;&atilde;o! =)<br/>" + "<br/>"
				+ "Transporte Urbano do Distrito Federal (DFTRANS) - Diretoria de Tecnologia da Informa&ccedil;&atilde;o (DTI)<br/>";
		sendEmail(usuario.getEmail(), titulo, mensagem, false, null,null);
	}

	/**
	 * Envia email para o representante de instituição informando o sucesso do
	 * cadastro
	 * 
	 * @param rep
	 */
	public static void sendEmailValidation(Representante rep) {
		String titulo = "Cadastro SCIE - Validação";
		String mensagem = "<b>Mensagem gerada automaticamente pelo sistema, n&atilde;o &eacute; necess&aacute;rio responder.</b>"
				+ "<br/>" + "<br/>" + "<br/>" + "Prezado(a) " + rep.getNome() + ",<br/>"
				+ "Seu cadastro no Sistema de Cadastro de Institui&ccedil;&otilde;es de Ensino (SCIE) do DFTRANS acaba de ser atualizado;<br/>"
				+ "Para verificar as pend&ecirc;ncias abertas, fechadas, e conclu&iacute;das acesse o sistema pelo menu login;<br/>"
				+ "<br/>"
				+ "Quaisquer d&uacute;vidas ou sugest&otilde;es utilize email <a href=\"mailto:ple@dftrans.df.gov.br\">ple@dftrans.df.gov.br</a> para nos contactar.<br/>"
				+ "Sua opini&atilde;o &eacute; muito importante para o aperfei&ccedil;oamento cont&iacute;nuo da presta&ccedil;&atilde;o dos nossos servi&ccedil;os.<br/>"
				+ "<br/>" + "Excelente Navega&ccedil;&atilde;o! =)<br/>" + "<br/>"
				+ "Transporte Urbano do Distrito Federal (DFTRANS) - Diretoria de Tecnologia da Informa&ccedil;&atilde;o (DTI)<br/>";
		sendEmail(rep.getEmail(), titulo, mensagem, true,null,null);

	}

	/**
	 * Envia email para o estudante informando o sucesso do cadastro da
	 * solicitação de segunda via de cartão PLE
	 * 
	 * @param email
	 */
	public static void sendEmail2Via(String email) {
		String titulo = "Solicitação de 2 Via Cartão PLE - Validação";
		String content = "<b>Mensagem gerada automaticamente pelo sistema, n&atilde;o &eacute; necess&aacute;rio responder.</b>"
				+ "<br/>" + "<br/>" + "<br/>" + "Prezado(a) estudante,<br/>"
				+ "Seu pedido de segunda via de cart&atilde;o do benef&iacute;cio PLE (Passe Livre Estudantil) do DFTRANS foi recebido com sucesso!!!<br/>"
				+ "Em breve voc&ecirc; receber&aacute; novas instru&ccedil;&otilde;es acerca do seu pedido por este email!<br/>"
				+ "<br/>"
				+ "Quaisquer d&uacute;vidas ou sugest&otilde;es utilize email <a href=\"mailto:ple@dftrans.df.gov.br\">ple@dftrans.df.gov.br</a> para nos contactar.<br/>"
				+ "Sua opini&atilde;o &eacute; muito importante para o aperfei&ccedil;oamento cont&iacute;nuo da presta&ccedil;&atilde;o dos nossos servi&ccedil;os.<br/>"
				+ "<br/>"
				+ "Transporte Urbano do Distrito Federal (DFTRANS) - Diretoria de Tecnologia da Informa&ccedil;&atilde;o (DTI)<br/>";
		sendEmail(email, titulo, content, true,null,null);
	}

	/**
	 * Envia email para o estudante informando o sucesso do cadastro da
	 * solicitação de extensão de acessos do cartão PLE
	 * 
	 * @param email
	 */
	public static void sendEmailAcessos(String email) {
		String titulo = "Solicitação de Extensão de Acessos Cartão PLE - Validação";
		String content = "<b>Mensagem gerada automaticamente pelo sistema, n&atilde;o &eacute; necess&aacute;rio responder.</b>"
				+ "<br/>" + "<br/>" + "<br/>" + "Prezado(a) estudante,<br/>"
				+ "Seu pedido de extens&atilde;o de acessos do benef&iacute;cio PLE (Passe Livre Estudantil) do DFTRANS foi recebido com sucesso!!!<br/>"
				+ "Em breve voc&ecirc; receber&aacute; novas instru&ccedil;&otilde;es acerca do seu pedido por este email!<br/>"
				+ "<br/>"
				+ "Quaisquer d&uacute;vidas ou sugest&otilde;es utilize email <a href=\"mailto:ple@dftrans.df.gov.br\">ple@dftrans.df.gov.br</a> para nos contactar.<br/>"
				+ "Sua opini&atilde;o &eacute; muito importante para o aperfei&ccedil;oamento cont&iacute;nuo da presta&ccedil;&atilde;o dos nossos servi&ccedil;os.<br/>"
				+ "<br/>"
				+ "Transporte Urbano do Distrito Federal (DFTRANS) - Diretoria de Tecnologia da Informa&ccedil;&atilde;o (DTI)<br/>";
		sendEmail(email, titulo, content, true, null, null);
	}

	/**
	 * Envia email para o estudante informando o resultado da solicitação de
	 * segunda via de cartão PLE
	 * 
	 * @param sol
	 * @param validados
	 * @param comentario
	 * @param nomes
	 */
	public static void sendEmail2ViaValidacao(Solicitacao sol, boolean[] validados, String[] comentario,
			String[] nomes,String[] files) {
		String titulo = "RESULTADO Solicitação 2 Via Cartão PLE - Validação";
		boolean[] b = new boolean[2];
		String content = "";
		content += "<b>Mensagem gerada automaticamente pelo sistema, n&atilde;o &eacute; necess&aacute;rio responder.</b>"
				+ "<br/>" + "<br/>" + "<br/>" + "Prezado(a) estudante,<br/>"
				+ "Seu pedido de segunda via de cart&atilde;o do benef&iacute;cio PLE (Passe Livre Estudantil) do DFTRANS foi <b>"
				+ (sol.getStatus() == 2 ? "APROVADO" : "REPROVADO")
				+ "</b> pelas seguintes justificativas:<br/><br/><hr/><div style = \"padding-left:15em\">";
		for (int i = 0; i < comentario.length; i++) {
			if (i == 2) {
				content += "Avalia&ccedil;&atilde;o da Solicita&ccedil;&atilde;o: <br/><b>"
						+ (validados[i] ? "Aprovado" : "Reprovado") + "</b><br/><br/>";
			} else {
				content += "Arquivo: <br/><b>" + nomes[i] + "</b><br/><br/>";
				content += "Avalia&ccedil;&atilde;o do Arquivo: <br/><b>" + (validados[i] ? "Aprovado" : "Reprovado")
						+ "</b><br/><br/>";
				b[i] = !validados[i];
			}
			content += "Justificativa:<br/>" + (comentario[i].equals("") ? "(Sem coment&aacute;rios)" : comentario[i])
					+ "<br/><br/>";
		}
		content += "</div><hr/><br/>Caso sua solicita&ccedil;&atilde;o tenha sido aprovada, em breve voc&ecirc; receber&aacute; novas instru&ccedil;&otilde;es para receber seu novo cart&atilde;o!<br/>"
				+ "<br/>"
				+ "Quaisquer d&uacute;vidas ou sugest&otilde;es utilize email <a href=\"mailto:ple@dftrans.df.gov.br\">ple@dftrans.df.gov.br</a> para nos contactar.<br/>"
				+ "Sua opini&atilde;o &eacute; muito importante para o aperfei&ccedil;oamento cont&iacute;nuo da presta&ccedil;&atilde;o dos nossos servi&ccedil;os.<br/>"
				+ "<br/>"
				+ "Transporte Urbano do Distrito Federal (DFTRANS) - Diretoria de Tecnologia da Informa&ccedil;&atilde;o (DTI)<br/>";
		sendEmail(sol.getEmail(), titulo, content, true,files, b);
	}

	/**
	 * Envia email para o estudante informando o resultado da solicitação de
	 * extensão de acessos do cartão PLE
	 * 
	 * @param ext
	 * @param validados
	 * @param comentario
	 * @param nomes
	 */
	public static void sendEmailAcessosValidacao(ExtensaoAcesso ext, boolean[] validados, String[] comentario,
			String[] nomes, String[] files) {
		String content = "";
		boolean[] b = new boolean[2];
		content += "<b>Mensagem gerada automaticamente pelo sistema, n&atilde;o &eacute; necess&aacute;rio responder.</b>"
				+ "<br/>" + "<br/>" + "<br/>" + "Prezado(a) estudante,<br/>"
				// linha diferente
				+ "Seu pedido de extens&atilde;o de acessos do benef&iacute;cio PLE (Passe Livre Estudantil) do DFTRANS foi <b>"
				+ (ext.getStatus() == 2 ? "APROVADO" : "REPROVADO")
				+ "</b> pelas seguintes justificativas:<br/><br/><hr/><div style = \"padding-left:15em\">";
		for (int i = 0; i < comentario.length; i++) {
			if (i == 2) {
				content += "Avalia&ccedil;&atilde;o da Solicita&ccedil;&atilde;o: <br/><b>"
						+ (validados[i] ? "Aprovado" : "Reprovado") + "</b><br/><br/>";
			} else {
				content += "Arquivo: <br/><b>" + nomes[i] + "</b><br/><br/>";
				content += "Avalia&ccedil;&atilde;o do Arquivo: <br/><b>" + (validados[i] ? "Aprovado" : "Reprovado")
						+ "</b><br/><br/>";
				b[i] = !validados[i];
			}
			content += "Justificativa:<br/>" + (comentario[i].equals("") ? "(Sem coment&aacute;rios)" : comentario[i])
					+ "<br/><br/>";
		}
		content += "</div><hr/><br/>Caso sua solicita&ccedil;&atilde;o tenha sido aprovada, em breve voc&ecirc; receber&aacute; a extensão de acessos automaticamente no seu cart&atilde;o<br/>"
				+ "<br/>"
				+ "Quaisquer d&uacute;vidas ou sugest&otilde;es utilize email <a href=\"mailto:ple@dftrans.df.gov.br\">ple@dftrans.df.gov.br</a> para nos contactar.<br/>"
				+ "Sua opini&atilde;o &eacute; muito importante para o aperfei&ccedil;oamento cont&iacute;nuo da presta&ccedil;&atilde;o dos nossos servi&ccedil;os.<br/>"
				+ "<br/>"
				+ "Transporte Urbano do Distrito Federal (DFTRANS) - Diretoria de Tecnologia da Informa&ccedil;&atilde;o (DTI)<br/>";
		sendEmail(ext.getEmail(), "RESULTADO Solicitação Extensão de Acessos Cartão PLE - Validação", content, true,files, b);
	}

	/**
	 * Envia um email
	 * 
	 * @param destinatario
	 * @param titulo
	 * @param mensagem
	 * @param copiaEmail
	 */
	public static void sendEmail(String destinatario, String titulo, String mensagem, boolean copiaEmail,String[] files, boolean[] isAnexo) {
		// Abstração de propriedades de conexão
		Properties props = new Properties();
		// seta o protocolo de transferência que está em um arquivo de
		// parâmetros
		props.setProperty("mail.transport.protocol", Parametros.getParameter("mail_transport_protocol"));
		// seta o endereço do servidor de emails
		props.setProperty("mail.host", Parametros.getParameter("mail_host"));
		// seta a porta que escuta o serviço de emails
		props.setProperty("mail.port", Parametros.getParameter("mail_port"));
		// seta o usuário que envia o email e a senha
		props.setProperty("mail.user", Parametros.getParameter("mail_user"));
		props.setProperty("mail.password", Parametros.getParameter("mail_password"));
		// API EMAIL, estabelece conexão com a máquina informada
		Session mailSession = Session.getDefaultInstance(props,
                new javax.mail.Authenticator() {
                     protected PasswordAuthentication getPasswordAuthentication() 
                     {
                           return new PasswordAuthentication(Parametros.getParameter("mail_user"), Parametros.getParameter("mail_password"));
                     }
                });
		
		mailSession.setDebug(new Boolean(Parametros.getParameter("debug")));
		Transport transport = null;
		try {
			// pega a conexão da sessão
			transport = mailSession.getTransport();
			MimeMessage message = new MimeMessage(mailSession);
			// titulo
			message.setSubject(titulo);
			// seta o remetente da mensagem
			message.setFrom(new InternetAddress(Parametros.getParameter("mail_user")));
			// cria a mensagem que sera retornada ao usuário
			String content = mensagem;
			content = content.replaceAll("ã", "&atilde;").replaceAll("Ã", "&Atilde;").replaceAll("ç", "&ccedil;")
					.replaceAll("Ç", "&Ccedil;").replaceAll("á", "&aacute;").replaceAll("Á", "&Aacute;")
					.replaceAll("é", "&eacute;").replaceAll("É", "&AEcute;").replaceAll("í", "&iacute;")
					.replaceAll("Í", "&Iacute;").replaceAll("ó", "&oacute;").replaceAll("Ó", "&Oacute;")
					.replaceAll("ú", "&uacute;").replaceAll("Ú", "&Uacute;").replaceAll("õ", "&otilde;")
					.replaceAll("Õ", "&Otilde;").replaceAll("â", "&acirc;").replaceAll("Â", "&UAcirc;")
					.replaceAll("ê", "&ecirc;").replaceAll("Ê", "&Ecirc;").replaceAll("ô", "&ocirc;")
					.replaceAll("Ô", "&Ocirc;");
			
			Multipart multipart = new MimeMultipart();
			BodyPart messageBodyPart = new MimeBodyPart();
			messageBodyPart.setContent(content, "text/html");
			multipart.addBodyPart(messageBodyPart);
			if(files != null){
				for(int i = 0; i < files.length; i++){
					if(isAnexo[i]){
					addAttachment(multipart, files[i]);
					}
				}
			}

	        message.setContent(multipart);

			// seta o destinatário
			message.addRecipient(Message.RecipientType.TO, new InternetAddress(destinatario));
			// envia uma cópia do email
			if (copiaEmail) {
				//message.addRecipient(Message.RecipientType.TO, new InternetAddress("ple@dftrans.df.gov.br"));
			}
			transport.connect();
			transport.sendMessage(message, message.getRecipients(Message.RecipientType.TO));
			transport.close();
		} catch (MessagingException e) {
			e.printStackTrace();
		}
	}
	
	private static void addAttachment(Multipart multipart, String filename) throws MessagingException {
	    DataSource source = new FileDataSource(filename);
	    BodyPart messageBodyPart = new MimeBodyPart();        
	    messageBodyPart.setDataHandler(new DataHandler(source));
		setDelimitadorDiretorioREGEX();
		String aux[] = filename.split(getDelimitadorDiretorioREGEX());
	    messageBodyPart.setFileName(aux[aux.length-1]);
	    multipart.addBodyPart(messageBodyPart);
	}
	
	/**
	 * Método responsável por tratar caracteres reservados em expresões
	 * regulares
	 */
	private static void setDelimitadorDiretorioREGEX() {
		if (".\\dDwW*+?sS^$|".contains(delimitadorDiretorio)) {
			delimitadorDiretorioREGEX = "\\" + delimitadorDiretorio;
		} else {
			delimitadorDiretorioREGEX = delimitadorDiretorio;
		}
	}

	public static String getDelimitadorDiretorioREGEX() {
		return delimitadorDiretorioREGEX;
	}

	public static void setDelimitadorDiretorioREGEX(String delimitadorDiretorioREGEX) {
		Mail.delimitadorDiretorioREGEX = delimitadorDiretorioREGEX;
	}

}
