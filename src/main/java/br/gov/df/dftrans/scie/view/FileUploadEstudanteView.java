package br.gov.df.dftrans.scie.view;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
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
import org.primefaces.model.DefaultStreamedContent;
import org.primefaces.model.StreamedContent;
import org.primefaces.model.UploadedFile;

import br.gov.df.dftrans.scie.dao.SolicitacaoDAO;
import br.gov.df.dftrans.scie.domain.Solicitacao;
import br.gov.df.dftrans.scie.exceptions.InsertException;
import br.gov.df.dftrans.scie.utils.AutenticacaoDocumentos;
import br.gov.df.dftrans.scie.utils.FacesUtil;
import br.gov.df.dftrans.scie.utils.Mail;
import br.gov.df.dftrans.scie.utils.ManipuladorArquivos;
import br.gov.df.dftrans.scie.utils.Parametros;

@ManagedBean(name = "fileUploadEstudanteView")
@SessionScoped
public class FileUploadEstudanteView {

	public ArrayList<Boolean> isUploaded = new ArrayList<Boolean>();
	private String fileNameUploaded;
	private String chave = "", iconeBO = "", iconeTAXA = "", cpf = "";
	private String aux[];
	public final int BO = 0;
	public final int TAXA = 1;
	public final String nomesArquivos[] = { "BO", "TAXA" };
	public final String descricaoArquivos[] = { Parametros.getParameter("doc_bo_name"),
			Parametros.getParameter("doc_taxa_name") };
	private String current = Parametros.getParameter("root_upload");
	private Date date = Calendar.getInstance().getTime();
	private DateFormat fmt = new SimpleDateFormat("dd-MM-yyyy");
	private StreamedContent fileout;
	private Solicitacao sol;
	private SolicitacaoDAO dao = SolicitacaoDAO.SolicitacaoDAO();
	private String delimitadorDiretorio = Parametros.getParameter("delimitador_diretorios");
	private String delimitadorDiretorioREGEX;

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

	// construtor
	public FileUploadEstudanteView() {
		setDelimitadorDiretorioREGEX();
		// seta CPF do estudante com o estudante da sessão
		HttpSession session = (HttpSession) FacesContext.getCurrentInstance().getExternalContext().getSession(false);
		setCpf((String) session.getAttribute("estudante"));
		setUpload();
		sol = new Solicitacao();
	}

	/**
	 * verifica se o arquivo já subiu e seta o ícone de uncheckead ou discheked
	 * ou checked se o arquivo ja tiver subido
	 */
	public void setUpload() {
		isUploaded = new ArrayList<Boolean>();
		File file = new File(current + "" + delimitadorDiretorio + "destino_uploader" + delimitadorDiretorio + ""
				+ getCpf() + "" + delimitadorDiretorio + "" + fmt.format(date) + "" + delimitadorDiretorio + "" + BO
				+ "" + delimitadorDiretorio + "" + nomesArquivos[BO] + ".pdf");
		if (file.exists()) {
			isUploaded.add(new Boolean(true));
			iconeBO = "//resources//images//checked-icon.png";
		} else {
			isUploaded.add(new Boolean(false));
			iconeBO = "//resources//images//unchecked-icon.png";
		}
		file = new File(current + "" + delimitadorDiretorio + "destino_uploader" + delimitadorDiretorio + "" + getCpf()
				+ "" + delimitadorDiretorio + "" + fmt.format(date) + "" + delimitadorDiretorio + "" + TAXA + ""
				+ delimitadorDiretorio + "" + nomesArquivos[TAXA] + ".pdf");
		if (file.exists()) {
			isUploaded.add(new Boolean(true));
			iconeTAXA = "//resources//images//checked-icon.png";
		} else {
			isUploaded.add(new Boolean(false));
			iconeTAXA = "//resources//images//unchecked-icon.png";
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
		// seta Estudante com a da sessao
		HttpSession session = (HttpSession) FacesContext.getCurrentInstance().getExternalContext().getSession(false);
		setCpf((String) session.getAttribute("estudante"));
		if (getCpf() == null) {
			try {
				FacesContext.getCurrentInstance().getExternalContext().redirect("estudanteHome.xhtml");
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
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

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
			DateFormat fmt = new SimpleDateFormat("dd/MM/yyyy");
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
	 * Download de arquivo .doc
	 * 
	 * @return o arquivo .doc
	 */
	public StreamedContent getDOC() {
		InputStream stream = null;
		try {
			stream = new FileInputStream(current + "" + delimitadorDiretorio + "destino_uploader" + delimitadorDiretorio
					+ "templates" + delimitadorDiretorio + "declaracao.doc");
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		fileout = new DefaultStreamedContent(stream, "application/doc", "declaracao.doc");
		return fileout;
	}

	/**
	 * Download de arquivo .docx
	 * 
	 * @return o arquivo docx
	 */
	public StreamedContent getDOCX() {
		InputStream stream = null;
		try {
			stream = new FileInputStream(current + "" + delimitadorDiretorio + "destino_uploader" + delimitadorDiretorio
					+ "templates" + delimitadorDiretorio + "declaracao.docx");
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		fileout = new DefaultStreamedContent(stream, "application/docx", "declaracao.docx");
		return fileout;
	}

	/**
	 * Download de arquivo ODT
	 * 
	 * @return o arquivo odt
	 */
	public StreamedContent getODT() {
		InputStream stream = null;
		try {
			stream = new FileInputStream(current + "" + delimitadorDiretorio + "destino_uploader" + delimitadorDiretorio
					+ "templates" + delimitadorDiretorio + "declaracao.odt");
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		fileout = new DefaultStreamedContent(stream, "application/odt", "declaracao.odt");
		return fileout;
	}

	/**
	 * Upload do BO
	 * 
	 * @param fileUploadEvent
	 */
	public void doUploadBO(FileUploadEvent fileUploadEvent) {
		doUpload(fileUploadEvent, BO);
	}

	/**
	 * Upload da taxa
	 * 
	 * @param fileUploadEvent
	 */
	public void doUploadTAXA(FileUploadEvent fileUploadEvent) {
		doUpload(fileUploadEvent, TAXA);
	}

	/**
	 * seta o ícone de acordo com o levantamento dos arquivos necessários
	 * 
	 * @param documento
	 */
	public void setIconeUploaded(int documento) {
		switch (documento) {
		case (0):
			setIconeBO("//resources//images//checked-icon.png");
			break;
		case (1):
			setIconeTAXA("//resources//images//checked-icon.png");
			break;
		}
	}

	/**
	 * seta a solicitação e envia email para o usuário
	 * 
	 * @return o redirecionamento da próxima página
	 */

	public String arquivos() {
		for (int i = 0; i < 2; i++) {
			if (!isUploaded.get(i)) {
				FacesUtil.addMsggError("É necessário anexar o documento: " + descricaoArquivos[i] + "!");
				return "/pages/estudante/estudante2ViaArquivos.xhtml?faces-redirect=false";
			}
		}
		try {
			sol.setAtualizacao(new Date());
			sol.setCpf(getCpf());
			dao.add(sol);
		} catch (InsertException e) {
			e.printStackTrace();
			return "/pages/error.xhtml?faces-redirect=true";
		}
		Mail.sendEmail2Via(sol.getEmail());
		return "/pages/estudante/confirmacao2Via.xhtml?faces-redirect=true";
	}
	
	public String concatenaArquivos() {
		FacesContext context = FacesContext.getCurrentInstance();
		ELResolver resolver = context.getApplication().getELResolver();
		ConcatenaArquivo bean = (ConcatenaArquivo) resolver.getValue(context.getELContext(), null, "fileUploadConcatenaView");
		bean.setOrigem(1);
		return "/pages/concatenaArquivos.xhtml?faces-redirect=true";
	}

	// getteres and setteres

	public String getIconeBO() {
		return iconeBO;
	}

	public void setIconeBO(String iconeBO) {
		this.iconeBO = iconeBO;
	}

	public String getIconeTAXA() {
		return iconeTAXA;
	}

	public void setIconeTAXA(String iconeTAXA) {
		this.iconeTAXA = iconeTAXA;
	}

	public String getCpf() {
		return cpf;
	}

	public void setCpf(String cpf) {
		this.cpf = cpf;
	}

	public Solicitacao getSol() {
		return sol;
	}

	public void setSol(Solicitacao sol) {
		this.sol = sol;
	}

	public String[] getDescricaoArquivos() {
		return descricaoArquivos;
	}

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
		this.isUploaded.set(documento, isUploaded);
	}

	public String getFileNameUploaded() {
		return fileNameUploaded;
	}

	public void setFileNameUploaded(String fileNameUploaded) {
		this.fileNameUploaded = fileNameUploaded;
	}
}