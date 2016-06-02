package br.gov.df.dftrans.scie.view;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.Serializable;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

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

import br.gov.df.dftrans.scie.dao.DocumentoPendenciaDAO;
import br.gov.df.dftrans.scie.dao.LogDAO;
import br.gov.df.dftrans.scie.domain.DocumentoPendencia;
import br.gov.df.dftrans.scie.domain.InstituicaoEnsino;
import br.gov.df.dftrans.scie.domain.LogValidacaoCadastro;
import br.gov.df.dftrans.scie.exceptions.EntityNotFoundException;
import br.gov.df.dftrans.scie.exceptions.InsertException;
import br.gov.df.dftrans.scie.utils.AutenticacaoDocumentos;
import br.gov.df.dftrans.scie.utils.FacesUtil;
import br.gov.df.dftrans.scie.utils.ManipuladorArquivos;
import br.gov.df.dftrans.scie.utils.Parametros;

@ManagedBean(name = "fileUploadView")
@SessionScoped
public class FileUploadView implements Serializable {

	public ArrayList<Boolean> isUploaded = new ArrayList<Boolean>();
	private String fileNameUploaded;
	private InstituicaoEnsino inst;
	private String chave = "", iconeAto, iconeCNPJ, iconeDiretor, iconeEndereco, iconeCurso = "", iconeEstatuto = "",
			iconeConvenio = "";
	private String aux[];
	public final int ATO_RECONHECIMENTO = 0;
	public final int INSCRICAO_CNPJ = 1;
	public final int ATO_DESIGNACAO_DIRETOR_SECRETARIO = 2;
	public final int COMPROVANTE_ENDERECO = 3;
	public final int AUTORIZACAO_CURSO_SUPERIOR = 4;
	public final int ESTATUTO = 5;
	public final int CONVENIO_ESTADO = 6;
	public final String nomesArquivos[] = { "ATO", "CNPJ", "DIRETOR", "ENDERECO", "CURSO", "ESTATUTO", "CONVENIO" };
	public final String descricaoArquivos[] = { Parametros.getParameter("doc_ato_name"),
			Parametros.getParameter("doc_cnpj_name"), Parametros.getParameter("doc_dir_name"),
			Parametros.getParameter("doc_end_name"), Parametros.getParameter("doc_sup_name"),
			Parametros.getParameter("doc_est_name"), Parametros.getParameter("doc_conv_name") };
	private String current = Parametros.getParameter("root_upload");
	private Date date = Calendar.getInstance().getTime();
	private DateFormat fmt = new SimpleDateFormat("dd-MM-yyyy");
	private String[] arquivosAtuais;
	private LogDAO logdao = LogDAO.LogDAO();
	private LogValidacaoCadastro log;
	private DocumentoPendenciaDAO docdao = DocumentoPendenciaDAO.DocumentoPendenciaDAO();
	private String msgPortaria = Parametros.getParameter("cadastro_arquivos_portaria");
	private String delimitadorDiretorio = Parametros.getParameter("delimitador_diretorios");
	private String delimitadorDiretorioREGEX;

	// construtor
	public FileUploadView() {
		setDelimitadorDiretorioREGEX();
		resetUploaded();
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
	 * faz upload do ato
	 * 
	 * @param fileUploadEvent
	 */
	public void doUploadAto(FileUploadEvent fileUploadEvent) {
		doUpload(fileUploadEvent, ATO_RECONHECIMENTO);
		try {
			DocumentoPendencia doc = docdao.getByDesc(descricaoArquivos[ATO_RECONHECIMENTO]);
			log = logdao.get(getInst(), doc);
			// grava o log da ação efetuada pelo usuário no banco
			if (log == null) {
				logdao.add(new LogValidacaoCadastro(null, getInst(), doc, null));
			} else {
				logdao.update(log, ATO_RECONHECIMENTO);
			}
		} catch (EntityNotFoundException e1) {
			e1.printStackTrace();
		} catch (InsertException e2) {
			e2.printStackTrace();
		}
	}

	/**
	 * Faz upload do documento que comprova o CNPJ
	 * 
	 * @param fileUploadEvent
	 */
	public void doUploadCNPJ(FileUploadEvent fileUploadEvent) {
		doUpload(fileUploadEvent, INSCRICAO_CNPJ);
		try {
			DocumentoPendencia doc = docdao.getByDesc(descricaoArquivos[INSCRICAO_CNPJ]);
			log = logdao.get(getInst(), doc);
			// grava o log da ação efetuada pelo usuário no banco
			if (log == null) {
				logdao.add(new LogValidacaoCadastro(null, getInst(), doc, null));
			} else {
				logdao.update(log, INSCRICAO_CNPJ);
			}
		} catch (EntityNotFoundException e1) {
			e1.printStackTrace();
		} catch (InsertException e2) {
			e2.printStackTrace();
		}
	}

	/**
	 * Faz upload do documento que comprova o diretor
	 * 
	 * @param fileUploadEvent
	 */
	public void doUploadDiretor(FileUploadEvent fileUploadEvent) {
		doUpload(fileUploadEvent, ATO_DESIGNACAO_DIRETOR_SECRETARIO);
		try {
			DocumentoPendencia doc = docdao.getByDesc(descricaoArquivos[ATO_DESIGNACAO_DIRETOR_SECRETARIO]);
			log = logdao.get(getInst(), doc);
			// grava o log da ação efetuada pelo usuário no banco
			if (log == null) {
				logdao.add(new LogValidacaoCadastro(null, getInst(), doc, null));
			} else {
				logdao.update(log, ATO_DESIGNACAO_DIRETOR_SECRETARIO);
			}
		} catch (EntityNotFoundException e1) {
			e1.printStackTrace();
		} catch (InsertException e2) {
			e2.printStackTrace();
		}
	}

	/**
	 * faz upload do documento que comprova o endereço
	 * 
	 * @param fileUploadEvent
	 */
	public void doUploadEndereco(FileUploadEvent fileUploadEvent) {
		doUpload(fileUploadEvent, COMPROVANTE_ENDERECO);
		try {
			DocumentoPendencia doc = docdao.getByDesc(descricaoArquivos[COMPROVANTE_ENDERECO]);
			log = logdao.get(getInst(), doc);
			// grava o log da ação efetuada pelo usuário no banco
			if (log == null) {
				logdao.add(new LogValidacaoCadastro(null, getInst(), doc, null));
			} else {
				logdao.update(log, COMPROVANTE_ENDERECO);
			}
		} catch (EntityNotFoundException e1) {
			e1.printStackTrace();
		} catch (InsertException e2) {
			e2.printStackTrace();
		}
	}

	/**
	 * faz upload do documento que comprova o cursoSuperior
	 * 
	 * @param fileUploadEvent
	 */
	public void doUploadCursoSuperior(FileUploadEvent fileUploadEvent) {
		doUpload(fileUploadEvent, AUTORIZACAO_CURSO_SUPERIOR);
		try {
			DocumentoPendencia doc = docdao.getByDesc(descricaoArquivos[AUTORIZACAO_CURSO_SUPERIOR]);
			log = logdao.get(getInst(), doc);
			// grava o log da ação efetuada pelo usuário no banco
			if (log == null) {
				logdao.add(new LogValidacaoCadastro(null, getInst(), doc, null));
			} else {
				logdao.update(log, AUTORIZACAO_CURSO_SUPERIOR);
			}
		} catch (EntityNotFoundException e1) {
			e1.printStackTrace();
		} catch (InsertException e2) {
			e2.printStackTrace();
		}
	}

	/**
	 * Faz upload do documento que comprova o estatuto
	 * 
	 * @param fileUploadEvent
	 */
	public void doUploadEstatuto(FileUploadEvent fileUploadEvent) {
		doUpload(fileUploadEvent, ESTATUTO);
		try {
			DocumentoPendencia doc = docdao.getByDesc(descricaoArquivos[ESTATUTO]);
			log = logdao.get(getInst(), doc);
			// grava o log da ação efetuada pelo usuário no banco
			if (log == null) {
				logdao.add(new LogValidacaoCadastro(null, getInst(), doc, null));
			} else {
				logdao.update(log, ESTATUTO);
			}
		} catch (EntityNotFoundException e1) {
			e1.printStackTrace();
		} catch (InsertException e2) {
			e2.printStackTrace();
		}
	}

	/**
	 * faz upload do documento que comprova o convenio
	 * 
	 * @param fileUploadEvent
	 */
	public void doUploadConvenio(FileUploadEvent fileUploadEvent) {
		doUpload(fileUploadEvent, CONVENIO_ESTADO);
		try {
			DocumentoPendencia doc = docdao.getByDesc(descricaoArquivos[CONVENIO_ESTADO]);
			log = logdao.get(getInst(), doc);
			// grava o log da ação efetuada pelo usuário no banco
			if (log == null) {
				logdao.add(new LogValidacaoCadastro(null, getInst(), doc, null));
			} else {
				logdao.update(log, CONVENIO_ESTADO);
			}
		} catch (EntityNotFoundException e1) {
			e1.printStackTrace();
		} catch (InsertException e2) {
			e2.printStackTrace();
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
		// seta InstituiçãoEnsino com a da sessao
		HttpSession session = (HttpSession) FacesContext.getCurrentInstance().getExternalContext().getSession(false);
		setInst((InstituicaoEnsino) session.getAttribute("instituicao"));
		if (inst == null) {
			try {
				FacesContext.getCurrentInstance().getExternalContext().redirect("instituicaoHome.xhtml");
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		// cria o arquivo abstrato
		File file = new File(current + "" + delimitadorDiretorio + "destino_uploader" + delimitadorDiretorio + ""
				+ inst.getId() + "" + delimitadorDiretorio + "" + fmt.format(date) + "" + delimitadorDiretorio + ""
				+ documento + "" + delimitadorDiretorio);
		file.mkdirs();
		file = new File(current + delimitadorDiretorio + "destino_uploader" + delimitadorDiretorio + "" + inst.getId()
				+ delimitadorDiretorio + "files");
		if (!file.exists()) {
			// escreve no arquivo o array iniciado como 0
			conteudo = new ArrayList<String>();
			conteudo.add("0");
			conteudo.add("0");
			conteudo.add("0");
			conteudo.add("0");
			conteudo.add("0");
			conteudo.add("0");
			conteudo.add("0");
			conteudo.add("0");
			ManipuladorArquivos.escritor(current + delimitadorDiretorio + "destino_uploader" + delimitadorDiretorio + ""
					+ inst.getId() + delimitadorDiretorio + "files", conteudo);
		} else {
			// ler o arquivo
			String caminhos[] = ManipuladorArquivos.leitor(current + delimitadorDiretorio + "destino_uploader"
					+ delimitadorDiretorio + "" + inst.getId() + delimitadorDiretorio + "files");
			// exclui arquivo documento
			file = new File(caminhos[documento]);
			file.delete();
		}
		file = null;
		aux = fileNameUploaded.split("\\.");
		// se a extensão do arquivo for pdf
		if (aux[1].equals("pdf")) {
			// copia o pdf
			copiarArquivoPDF(delimitadorDiretorio + "destino_uploader" + delimitadorDiretorio + "" + inst.getId() + ""
					+ delimitadorDiretorio + "" + fmt.format(date) + "" + delimitadorDiretorio + "" + documento + ""
					+ delimitadorDiretorio + "" + nomesArquivos[documento], uploadedFile, documento);
		} else {
			// copia o arquivo
			copiarArquivo(delimitadorDiretorio + "destino_uploader" + delimitadorDiretorio + "" + inst.getId() + ""
					+ delimitadorDiretorio + "" + fmt.format(date) + "" + delimitadorDiretorio + "" + documento + ""
					+ delimitadorDiretorio + "" + nomesArquivos[documento], uploadedFile, documento);
		}
		setIsUploaded(new Boolean(true), documento);
		setIconeUploaded(documento);
		// ler o arquivo
		String files[] = ManipuladorArquivos.leitor(current + "" + delimitadorDiretorio + "destino_uploader"
				+ delimitadorDiretorio + "" + inst.getId() + "" + delimitadorDiretorio + "files");
		files[documento] = current + "" + delimitadorDiretorio + "destino_uploader" + delimitadorDiretorio + ""
				+ inst.getId() + "" + delimitadorDiretorio + "" + fmt.format(date) + "" + delimitadorDiretorio + ""
				+ documento + "" + delimitadorDiretorio + "" + nomesArquivos[documento] + "." + aux[1];
		conteudo = new ArrayList<String>();
		for (String temp : files) {
			// add as linhas do arquivo em um array de string
			conteudo.add(temp);
		}
		// faz a cópia do arquivo
		ManipuladorArquivos.escritor(current + "" + delimitadorDiretorio + "destino_uploader" + delimitadorDiretorio
				+ "" + inst.getId() + "" + delimitadorDiretorio + "files", conteudo);
	}

	/**
	 * Cria arquivo PDF e escreve a chave de segurança nesse arquivo, chave
	 * corresponde ao cnpj+id+representante+cpf+data+documento em MD5
	 * 
	 * @param path
	 * @param uploadedFile
	 * @param documento
	 */
	public void copiarArquivoPDF(String path, UploadedFile uploadedFile, int documento) {
		try {
			// escreve no arquivo o conteúdo do UploadedFile(API primeFaces)
			FileOutputStream os = new FileOutputStream(current + path + "." + aux[1]);
			os.write(uploadedFile.getContents());
			os.close();
			// Manipula arquivo pdf
			PDDocument doc = PDDocument.load(current + path + "." + aux[1]);
			PDPage page = new PDPage();
			doc.addPage(page);
			PDPageContentStream contentStream = new PDPageContentStream(doc, page);
			contentStream.beginText();
			contentStream.moveTextPositionByAmount(100, 700);
			contentStream.setFont(PDType1Font.HELVETICA, 12);
			// Cria chave única que estará presente no PDF
			chave = inst.getCnpj();
			chave += inst.getId();
			chave += inst.getRepresentante().getNome();
			chave += inst.getRepresentante().getCpf();
			chave += fmt.format(new Date());
			chave += documento;
			chave = chave.replaceAll(delimitadorDiretorioREGEX, "").replaceAll(" ", "").replaceAll("-", "");
			contentStream.drawString("Autenticação: " + AutenticacaoDocumentos.getChaveSeguranca(chave));
			contentStream.endText();
			contentStream.close();
			doc.save(current + path + "." + aux[1]);
		} catch (IOException e) {
			System.out.println("Erro de gravação do arquivo\n");
			e.printStackTrace();
		} catch (COSVisitorException e) {
			// TODO Auto-generated catch block
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
			FileOutputStream os = new FileOutputStream(current + path + "." + aux[1]);
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
			chave = inst.getCnpj();
			chave += inst.getId();
			chave += inst.getRepresentante().getNome();
			chave += inst.getRepresentante().getCpf();
			DateFormat fmt = new SimpleDateFormat("dd/MM/yyyy");
			chave += fmt.format(new Date());
			chave += documento;
			chave = chave.replaceAll(delimitadorDiretorioREGEX, "").replaceAll(" ", "").replaceAll("-", "");
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
	 * seta o ícone referente ao upload
	 * 
	 * @param documento
	 */
	public void setIconeUploaded(int documento) {
		switch (documento) {
		case (0):
			setIconeAto("//resources//images//checked-icon.png");
			break;
		case (1):
			setIconeCNPJ("//resources//images//checked-icon.png");
			break;
		case (2):
			setIconeDiretor("//resources//images//checked-icon.png");
			break;
		case (3):
			setIconeEndereco("//resources//images//checked-icon.png");
			break;
		case (4):
			setIconeCurso("//resources//images//checked-icon.png");
			break;
		case (5):
			setIconeEstatuto("//resources//images//checked-icon.png");
			break;
		case (6):
			setIconeConvenio("//resources//images//checked-icon.png");
			break;
		}
	}

	/**
	 * Verifica se existe ícone em curso, estatuto ou convenio
	 * 
	 * @param i
	 * @return true se existir false se não
	 */
	public boolean getExists(int i) {
		switch (i) {
		case (1):
			if (!getIconeCurso().isEmpty()) {
				return true;
			}
			break;
		case (2):
			if (!getIconeEstatuto().isEmpty()) {
				return true;
			}
			break;
		case (3):
			if (!getIconeConvenio().isEmpty()) {
				return true;
			}
			break;
		}
		return false;
	}

	public String arquivos() {
		for (int i = 0; i < 4; i++) {
			if (!isUploaded.get(i)) {
				FacesUtil.addMsggError("É necessário anexar o documento: " + descricaoArquivos[i] + "!");
				return "/pages/instituicao/arquivosCadastro.xhtml?faces-redirect=false";
			}
		}
		return "/pages/instituicao/cadastrarCursos.xhtml?faces-redirect=true";
	}

	/**
	 * Reseta o upload
	 */
	public void resetUploaded() {
		isUploaded.add(new Boolean(false));
		isUploaded.add(new Boolean(false));
		isUploaded.add(new Boolean(false));
		isUploaded.add(new Boolean(false));
		isUploaded.add(new Boolean(false));
		isUploaded.add(new Boolean(false));
		isUploaded.add(new Boolean(false));
		iconeAto = "//resources//images//unchecked-icon.png";
		iconeCNPJ = "//resources//images//unchecked-icon.png";
		iconeDiretor = "//resources//images//unchecked-icon.png";
		iconeEndereco = "//resources//images//unchecked-icon.png";
		iconeCurso = "";
		iconeEstatuto = "";
		iconeConvenio = "";
	}

	/**
	 * seta o upload e o ícone checked se for válido e unchecked se der erro
	 */
	public void setUploaded() {
		HttpSession session = (HttpSession) FacesContext.getCurrentInstance().getExternalContext().getSession(false);
		setInst((InstituicaoEnsino) session.getAttribute("instituicao"));
		File file = new File(current + "" + delimitadorDiretorio + "destino_uploader" + delimitadorDiretorio + ""
				+ inst.getId() + "" + delimitadorDiretorio + "files");
		if (file.exists()) {
			arquivosAtuais = ManipuladorArquivos.leitor(current + "" + delimitadorDiretorio + "destino_uploader"
					+ delimitadorDiretorio + "" + inst.getId() + "" + delimitadorDiretorio + "files");
			// seta o array de boolean caso tenha 0 false, outro valor true
			for (int i = 0; i < 7; i++) {
				isUploaded.set(i, new Boolean(!arquivosAtuais[i].equals("0")));
			}
			// campos true o ícone recebe checked
			iconeAto = "//resources//images//" + ((isUploaded.get(0)) ? "" : "un") + "checked-icon.png";
			iconeCNPJ = "//resources//images//" + ((isUploaded.get(1)) ? "" : "un") + "checked-icon.png";
			iconeDiretor = "//resources//images//" + ((isUploaded.get(2)) ? "" : "un") + "checked-icon.png";
			iconeEndereco = "//resources//images//" + ((isUploaded.get(3)) ? "" : "un") + "checked-icon.png";
			iconeCurso = (isUploaded.get(4)) ? "//resources//images//checked-icon.gif" : "";
			iconeEstatuto = (isUploaded.get(5)) ? "//resources//images//checked-icon.gif" : "";
			iconeConvenio = (isUploaded.get(6)) ? "//resources//images//checked-icon.gif" : "";
		} else {
			// se o arquivo não existir, reseta o upload
			resetUploaded();
		}
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
		this.isUploaded.set(documento, isUploaded);
	}

	public String getFileNameUploaded() {
		return fileNameUploaded;
	}

	public void setFileNameUploaded(String fileNameUploaded) {
		this.fileNameUploaded = fileNameUploaded;
	}

	public InstituicaoEnsino getInst() {
		return inst;
	}

	public void setInst(InstituicaoEnsino inst) {
		this.inst = inst;
	}

	public String getIconeAto() {
		return iconeAto;
	}

	public void setIconeAto(String iconeAto) {
		this.iconeAto = iconeAto;
	}

	public String getIconeCNPJ() {
		return iconeCNPJ;
	}

	public void setIconeCNPJ(String iconeCNPJ) {
		this.iconeCNPJ = iconeCNPJ;
	}

	public String getIconeDiretor() {
		return iconeDiretor;
	}

	public void setIconeDiretor(String iconeDiretor) {
		this.iconeDiretor = iconeDiretor;
	}

	public String getIconeEndereco() {
		return iconeEndereco;
	}

	public void setIconeEndereco(String iconeEndereco) {
		this.iconeEndereco = iconeEndereco;
	}

	public String getIconeCurso() {
		return iconeCurso;
	}

	public void setIconeCurso(String iconeCurso) {
		this.iconeCurso = iconeCurso;
	}

	public String getIconeEstatuto() {
		return iconeEstatuto;
	}

	public void setIconeEstatuto(String iconeEstatuto) {
		this.iconeEstatuto = iconeEstatuto;
	}

	public String getIconeConvenio() {
		return iconeConvenio;
	}

	public void setIconeConvenio(String iconeConvenio) {
		this.iconeConvenio = iconeConvenio;
	}

	public String getMsgPortaria() {
		return msgPortaria;
	}

	public void setMsgPortaria(String msgPortaria) {
		this.msgPortaria = msgPortaria;
	}

	public String[] getDescricaoArquivos() {
		return descricaoArquivos;
	}

}
