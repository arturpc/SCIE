package br.gov.df.dftrans.scie.view;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import javax.el.ELResolver;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.context.FacesContext;
import javax.servlet.http.HttpSession;
import org.apache.pdfbox.exceptions.COSVisitorException;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.edit.PDPageContentStream;
import org.apache.pdfbox.pdmodel.font.PDType1Font;
import org.primefaces.event.FileUploadEvent;
import org.primefaces.model.UploadedFile;
import br.gov.df.dftrans.scie.dao.ExtensaoAcessoDAO;
import br.gov.df.dftrans.scie.domain.ExtensaoAcesso;
import br.gov.df.dftrans.scie.exceptions.InsertException;
import br.gov.df.dftrans.scie.utils.AutenticacaoDocumentos;
import br.gov.df.dftrans.scie.utils.FacesUtil;
import br.gov.df.dftrans.scie.utils.Mail;
import br.gov.df.dftrans.scie.utils.ManipuladorArquivos;
import br.gov.df.dftrans.scie.utils.Parametros;

@ManagedBean(name = "fileUploadAcessosView")
@SessionScoped
public class FileUploadAcessosView {

	public ArrayList<Boolean> isUploaded = new ArrayList<Boolean>();
	private String fileNameUploaded;
	private String chave = "", iconeDOC1 = "", iconeDOC2 = "", cpf = "";
	private String aux[];
	public final int DOC1 = 2;
	public final int DOC2 = 3;
	public final String nomesArquivos[] = { "", "", "documento1", "documento2" };
	public final String descricaoArquivos[] = { "", "", Parametros.getParameter("doc_decl_ie"), "" };
	public final boolean arquivos[][] = { { false, true }, { false, false }, { true, true }, { false, false } };
	private String current = Parametros.getParameter("root_upload");
	private Date date = Calendar.getInstance().getTime();
	private DateFormat fmt = new SimpleDateFormat("dd-MM-yyyy");
	private ExtensaoAcesso ext;
	private ExtensaoAcessoDAO dao = ExtensaoAcessoDAO.ExtensaoAcessoDAO();
	private String delimitadorDiretorio = Parametros.getParameter("delimitador_diretorios");
	private String delimitadorDiretorioREGEX;
	private boolean esconderArquivos[] = { false, true };

	// construtor
	public FileUploadAcessosView() {
		setDelimitadorDiretorioREGEX();
		HttpSession session = (HttpSession) FacesContext.getCurrentInstance().getExternalContext().getSession(false);
		setCpf((String) session.getAttribute("estudante"));
		// seta a situação do arquivo e i ícone que sera setada pro usuário
		setUpload();
		ext = new ExtensaoAcesso();
		// perda
		ext.setMotivo(1);
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
	 * verifica se o arquivo já subiu e seta o ícone de uncheckead ou discheked
	 * ou checked se o arquivo ja tiver subido
	 */
	public void setUpload() {
		isUploaded = new ArrayList<Boolean>();
		File file = new File(current + "" + delimitadorDiretorio + "destino_uploader" + delimitadorDiretorio + ""
				+ getCpf() + "" + delimitadorDiretorio + "" + fmt.format(date) + "" + delimitadorDiretorio + "" + DOC1
				+ "" + delimitadorDiretorio + "" + nomesArquivos[DOC1] + ".pdf");
		if (file.exists()) {
			isUploaded.add(new Boolean(true));
			iconeDOC1 = "//resources//images//checked-icon.png";
		} else {
			isUploaded.add(new Boolean(false));
			iconeDOC1 = "//resources//images//" + (esconderArquivos[0] ? "dis" : "un") + "checked-icon.png";
		}
		file = new File(current + "" + delimitadorDiretorio + "destino_uploader" + delimitadorDiretorio + "" + getCpf()
				+ "" + delimitadorDiretorio + "" + fmt.format(date) + "" + delimitadorDiretorio + "" + DOC2 + ""
				+ delimitadorDiretorio + "" + nomesArquivos[DOC2] + ".pdf");
		if (file.exists()) {
			isUploaded.add(new Boolean(true));
			iconeDOC2 = "//resources//images//checked-icon.png";
		} else {
			isUploaded.add(new Boolean(false));
			iconeDOC2 = "//resources//images//" + (esconderArquivos[1] ? "dis" : "un") + "checked-icon.png";
		}

	}

	public void doUpload(FileUploadEvent fileUploadEvent, int documento) {
		ArrayList<String> conteudo;
		// trata o arquvo que o usuário subiu
		UploadedFile uploadedFile = fileUploadEvent.getFile();
		// seta o nome do arquivo
		setFileNameUploaded(uploadedFile.getFileName());
		// seta o tamanho do arquivo
		long fileSizeUploaded = uploadedFile.getSize() / 1000;
		String infoAboutFile = "<br/> Arquivo recebido: <b>" + fileNameUploaded + "</b><br/>"
				+ "Tamanho do Arquivo: <b>" + fileSizeUploaded + " KBs</b>";
		FacesContext facesContext = FacesContext.getCurrentInstance();
		facesContext.addMessage(null, new FacesMessage("Sucesso", infoAboutFile));
		// seta estudante com a da sessao
		HttpSession session = (HttpSession) FacesContext.getCurrentInstance().getExternalContext().getSession(false);
		setCpf((String) session.getAttribute("estudante"));
		if (getCpf() == null) {
			try {
				FacesContext.getCurrentInstance().getExternalContext().redirect("estudanteAcessosHome.xhtml");
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		// cria o arquivo abstrato
		File file = new File(current + "" + delimitadorDiretorio + "destino_uploader" + delimitadorDiretorio + ""
				+ getCpf() + "" + delimitadorDiretorio + "" + fmt.format(date) + "" + delimitadorDiretorio + ""
				+ documento + "" + delimitadorDiretorio);
		file.mkdirs();
		file = new File(current + "" + delimitadorDiretorio + "destino_uploader" + delimitadorDiretorio + "" + getCpf()
				+ "/files");
		if (!file.exists()) {
			// escreve no arquivo o array iniciado como 0
			conteudo = new ArrayList<String>();
			conteudo.add("0");
			conteudo.add("0");
			conteudo.add("0");
			conteudo.add("0");
			ManipuladorArquivos.escritor(current + "" + delimitadorDiretorio + "destino_uploader" + delimitadorDiretorio
					+ "" + getCpf() + delimitadorDiretorio + "files", conteudo);
		} else {
			// ler o arquivo
			String caminhos[] = ManipuladorArquivos.leitor(current + "" + delimitadorDiretorio + "destino_uploader"
					+ delimitadorDiretorio + getCpf() + "/files");
			// exclui arquivo documento
			file = new File(caminhos[documento]);
			file.delete();
		}
		file = null;

		aux = fileNameUploaded.split("\\.");
		// se a extensão do arquivo for pdf
		if (aux[aux.length - 1].equals("pdf")) {
			// copia o pdf
			copiarArquivoPDF(delimitadorDiretorio + "destino_uploader" + delimitadorDiretorio + "" + getCpf() + ""
					+ delimitadorDiretorio + "" + fmt.format(date) + "" + delimitadorDiretorio + "" + documento + ""
					+ delimitadorDiretorio + "" + nomesArquivos[documento], uploadedFile, documento);
		} else {
			// copia o arquivo
			copiarArquivo(delimitadorDiretorio + "destino_uploader" + delimitadorDiretorio + "" + getCpf() + ""
					+ delimitadorDiretorio + "" + fmt.format(date) + "" + delimitadorDiretorio + "" + documento + ""
					+ delimitadorDiretorio + "" + nomesArquivos[documento], uploadedFile, documento);
		}
		setIsUploaded(new Boolean(true), documento);
		setIconeUploaded(documento);
		// ler o arquivo
		String files[] = ManipuladorArquivos.leitor(current + "" + delimitadorDiretorio + "destino_uploader"
				+ delimitadorDiretorio + "" + getCpf() + "" + delimitadorDiretorio + "files");
		files[documento] = current + "" + delimitadorDiretorio + "destino_uploader" + delimitadorDiretorio + ""
				+ getCpf() + "" + delimitadorDiretorio + "" + fmt.format(date) + "" + delimitadorDiretorio + ""
				+ documento + "" + delimitadorDiretorio + "" + nomesArquivos[documento] + "." + aux[aux.length - 1];
		conteudo = new ArrayList<String>();
		for (String temp : files) {
			// add as linhas do arquivo em um array de string
			conteudo.add(temp);
		}
		// faz a cópia do arquivo
		ManipuladorArquivos.escritor(current + "" + delimitadorDiretorio + "destino_uploader" + delimitadorDiretorio
				+ "" + getCpf() + "" + delimitadorDiretorio + "files", conteudo);
	}

	/**
	 * Cria arquivo PDF e escreve a chave de segurança nesse arquivo, chave
	 * corresponde ao cpf+data+documento em MD5
	 * 
	 * @param path
	 * @param uploadedFile
	 * @param documento
	 */
	public void copiarArquivoPDF(String path, UploadedFile uploadedFile, int documento) {
		try {
			// escreve no arquivo o conteúdo do UploadedFile(API primeFaces)
			FileOutputStream os = new FileOutputStream(current + path + "." + aux[aux.length - 1]);
			os.write(uploadedFile.getContents());
			os.close();
			// Manipula arquivo pdf
			PDDocument doc = PDDocument.load(current + path + "." + aux[aux.length - 1]);
			PDPage page = new PDPage();
			doc.addPage(page);
			PDPageContentStream contentStream = new PDPageContentStream(doc, page);
			contentStream.beginText();
			contentStream.moveTextPositionByAmount(100, 700);
			contentStream.setFont(PDType1Font.HELVETICA, 12);
			// Cria chave única que estará presente no PDF
			chave = getCpf();
			chave += fmt.format(new Date());
			chave += documento;
			contentStream.drawString("Autenticação: " + AutenticacaoDocumentos.getChaveSeguranca(chave));
			contentStream.endText();
			contentStream.close();
			doc.save(current + path + "." + aux[aux.length - 1]);
		} catch (IOException e) {
			System.out.println("Erro de gravação do arquivo\n");
			e.printStackTrace();
		} catch (COSVisitorException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Escreve um arquivo qualquer e cria um novo arquivo PDF com a chave MD5
	 * nele
	 * 
	 * @param path
	 * @param uploadedFile
	 * @param documento
	 */
	public void copiarArquivo(String path, UploadedFile uploadedFile, int documento) {
		try {
			// escreve no arquivo o conteúdo do UploadedFile(API primeFaces)
			FileOutputStream os = new FileOutputStream(current + path + "." + aux[aux.length - 1]);
			os.write(uploadedFile.getContents());
			os.close();
			// Manipula arquivo pdf
			PDDocument doc = new PDDocument();
			PDPage page = new PDPage();
			doc.addPage(page);
			PDPageContentStream contentStream = new PDPageContentStream(doc, page);
			contentStream.beginText();
			contentStream.moveTextPositionByAmount(100, 700);
			contentStream.setFont(PDType1Font.HELVETICA, 12);
			// Cria chave única que estará presente no PDF
			chave = getCpf();
			chave += fmt.format(new Date());
			chave += documento;
			contentStream.drawString("Autenticação: " + AutenticacaoDocumentos.getChaveSeguranca(chave));
			contentStream.endText();
			contentStream.close();
			doc.save(current + path + ".pdf");
		} catch (IOException e) {
			System.out.println("Erro de gravação do arquivo\n");
			e.printStackTrace();
		} catch (COSVisitorException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Setar o label de visualização do campo na tela de acordo com o motivo da
	 * extensão
	 */
	public void atualizarCampos() {
		switch (getExt().getMotivo()) {
		case (1):
			descricaoArquivos[2] = Parametros.getParameter("doc_decl_ie");
			descricaoArquivos[3] = "";
			break;
		case (2):
			descricaoArquivos[2] = Parametros.getParameter("doc_decl_estagio");
			descricaoArquivos[3] = Parametros.getParameter("doc_grade");
			break;
		case (3):
			descricaoArquivos[2] = "Neste caso serão confrontados os arquivos já anexados do comprovante de residência do estudante e o endereço de sua Instituição de Ensino no cadastro já realizado. NÃO HÁ NECESSIDADE DE ENVIO DE ARQUIVO!!!";
			descricaoArquivos[3] = "";
			break;
		case (4):
			descricaoArquivos[2] = Parametros.getParameter("doc_end");
			descricaoArquivos[3] = Parametros.getParameter("doc_end_ie");
			break;
		}
		esconderArquivos = arquivos[getExt().getMotivo() - 1];
		setUpload();
	}

	/**
	 * Verifica os arquivos obrigatórios que o estudante deve anexar
	 * 
	 * @return o redirecionamento da próxima página
	 */
	public String arquivos() {
		for (int i = 0; i < 2; i++) {
			if (!esconderArquivos[i] && !isUploaded.get(i)) {
				FacesUtil.addMsggError("É necessário anexar o documento: " + descricaoArquivos[i] + "!");
				return "/pages/estudante/estudanteAcessosArquivos.xhtml?faces-redirect=false";
			}
		}
		try {
			ext.setAtualizacao(new Date());
			ext.setCpf(getCpf());
			dao.add(ext);
		} catch (InsertException e) {
			e.printStackTrace();
			return "/pages/error.xhtml?faces-redirect=true";
		}
		Mail.sendEmailAcessos(ext.getEmail());
		return "/pages/estudante/confirmacaoAcessos.xhtml?faces-redirect=true";
	}

	/**
	 * seta o ícone referente ao upload
	 * 
	 * @param documento
	 */
	public void setIconeUploaded(int documento) {
		switch (documento) {
		case (2):
			setIconeDOC1("//resources//images//checked-icon.png");
			break;
		case (3):
			setIconeDOC2("//resources//images//checked-icon.png");
			break;
		}
	}
	
	public String concatenaArquivos() {
		FacesContext context = FacesContext.getCurrentInstance();
		ELResolver resolver = context.getApplication().getELResolver();
		ConcatenaArquivo bean = (ConcatenaArquivo) resolver.getValue(context.getELContext(), null, "fileUploadConcatenaView");
		bean.setOrigem(2);
		return "/pages/estudante/concatenaArquivos.xhtml?faces-redirect=true";
	}

	// icone que sera setado
	public void doUploadDOC1(FileUploadEvent fileUploadEvent) {
		doUpload(fileUploadEvent, DOC1);
	}

	public void doUploadDOC2(FileUploadEvent fileUploadEvent) {
		doUpload(fileUploadEvent, DOC2);
	}

	// getteres and setteres
	public ArrayList<Boolean> getIsUploaded() {
		return isUploaded;
	}

	public void setIsUploaded(ArrayList<Boolean> isUploaded) {
		this.isUploaded = isUploaded;
	}

	public Boolean getIsUploaded(int documento) {
		return this.isUploaded.get(documento);
	}

	public void setIsUploaded(Boolean isUploaded, int documento) {
		this.isUploaded.set(documento - 2, isUploaded);
	}

	public String getFileNameUploaded() {
		return fileNameUploaded;
	}

	public void setFileNameUploaded(String fileNameUploaded) {
		this.fileNameUploaded = fileNameUploaded;
	}

	public String getIconeDOC1() {
		return iconeDOC1;
	}

	public void setIconeDOC1(String iconeDOC1) {
		this.iconeDOC1 = iconeDOC1;
	}

	public String getIconeDOC2() {
		return iconeDOC2;
	}

	public void setIconeDOC2(String iconeDOC2) {
		this.iconeDOC2 = iconeDOC2;
	}

	public String getCpf() {
		return cpf;
	}

	public void setCpf(String cpf) {
		this.cpf = cpf;
	}

	public ExtensaoAcesso getExt() {
		return ext;
	}

	public void setExt(ExtensaoAcesso ext) {
		this.ext = ext;
	}

	public String[] getDescricaoArquivos() {
		return descricaoArquivos;
	}

	public boolean[] getEsconderArquivos() {
		return esconderArquivos;
	}

	public void setEsconderArquivos(boolean[] esconderArquivos) {
		this.esconderArquivos = esconderArquivos;
	}
}