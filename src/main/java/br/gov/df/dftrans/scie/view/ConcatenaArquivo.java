package br.gov.df.dftrans.scie.view;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;

import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.context.FacesContext;
import javax.imageio.ImageIO;
import javax.servlet.http.HttpSession;

import org.apache.pdfbox.exceptions.COSVisitorException;
import org.apache.pdfbox.util.PDFMergerUtility;
import org.primefaces.event.FileUploadEvent;
import org.primefaces.model.DefaultStreamedContent;
import org.primefaces.model.StreamedContent;
import org.primefaces.model.UploadedFile;

import br.gov.df.dftrans.scie.domain.InstituicaoEnsino;
import br.gov.df.dftrans.scie.utils.Parametros;

@ManagedBean(name = "fileUploadConcatenaView")
@SessionScoped
public class ConcatenaArquivo {

	private String fileNameUploaded;
	private String iconeUploaded = "";
	private String cpf = "";
	private String current = Parametros.getParameter("root_upload");
	private String delimitadorDiretorio = Parametros.getParameter("delimitador_diretorios");
	private String delimitadorDiretorioREGEX;
	private String aux[];
	private Boolean isUploaded = new Boolean(false);
	private DefaultStreamedContent streamedContent;
	private InputStream is = null;
	private int origem;
	
	public ConcatenaArquivo(){
		
	}

	/**
	 * Metodo para setup da tela de concatenar arquivos. Inicia as estruturas e variáveis;
	 */
	public void init() {
		ArrayList<String> tipos = new ArrayList<String>();
		ArrayList<String> documentos = new ArrayList<String>();
		// pdf|jpe?g|gif|png|tiff|bmp
		tipos.add("pdf");
		tipos.add("jpg");
		tipos.add("jpeg");
		tipos.add("gif");
		tipos.add("png");
		tipos.add("bmp");
		documentos.add("documento.");
		documentos.add("1.");
		// seta Estudante com a da sessao
		HttpSession session = (HttpSession) FacesContext
				.getCurrentInstance().getExternalContext().getSession(false);
		setCpf((String) session.getAttribute("estudante"));
		if (getCpf() == null) {
			setCpf("" + ((InstituicaoEnsino) session
					.getAttribute("instituicao")).getId());
			if (getCpf().isEmpty()) {
				try {
					FacesContext.getCurrentInstance()
					.getExternalContext().redirect("index.xhtml");
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		for (String temp1 : documentos) {
			for (String temp : tipos) {
				File f = new File(current + delimitadorDiretorio 
						+ "destino_uploader" + delimitadorDiretorio + ""
						+ getCpf() + "" + delimitadorDiretorio 
						+ temp1 + temp);
				if (f.exists()) {
					f.delete();
				}
			}
		}
		isUploaded = new Boolean(false);
		setIconeUploaded();
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
	 * Método para concatenação de imagens. Aglomera duas imagens em um arquivo.
	 * @param path
	 */
	public void concatenaImagem(String path) {
		File file1 = new File(path);
		setAux(path);
		File file2 = new File(current + delimitadorDiretorio + "destino_uploader" 
                    + delimitadorDiretorio + "" + getCpf()
				+ "" + delimitadorDiretorio + "documento." + aux[1]);
		try {
			if (file2.exists()) {
				BufferedImage img1;
				BufferedImage img2;

				img1 = ImageIO.read(file1);
				img2 = ImageIO.read(file2);

				int widthImg1 = img1.getWidth();
				int heightImg1 = img1.getHeight();

				int maxWidth = widthImg1;

				int widthImg2 = img2.getWidth();
				int heightImg2 = img2.getHeight();

				maxWidth = (maxWidth > widthImg2 ? maxWidth : widthImg2);

				BufferedImage img = new BufferedImage(maxWidth,
						// Final image will have width and height as
						heightImg1 + heightImg2,
						// addition of widths and heights of
						// the images we already have
						BufferedImage.TYPE_INT_RGB);

				int y = 0;
				int x = 0;

				img.createGraphics().drawImage(img2, x, y, null);
				// 0,0 are the x and y positions

				y += heightImg2;

				img.createGraphics().drawImage(img1, x, y, null);
				// here width is mentioned as width of horizontally
				File final_image = new File(current + delimitadorDiretorio 
						+ "destino_uploader" + delimitadorDiretorio
						+ "" + getCpf() + "" + delimitadorDiretorio 
						+ "documento." + aux[1]);

				ImageIO.write(img, aux[1], final_image);
			} else {
				file1.renameTo(new File(current + delimitadorDiretorio 
						+ "destino_uploader" + delimitadorDiretorio + ""
						+ getCpf() + "" + delimitadorDiretorio 
						+ "documento." + aux[1]));
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	/**
	 * Método que trata o upload de determinado arquivo.
	 * @param fileUploadEvent
	 */
	public void doUpload(FileUploadEvent fileUploadEvent) {
		// trata o arquvo que o usuário subiu
		UploadedFile uploadedFile = fileUploadEvent.getFile();
		// seta o nome do arquivo
		setFileNameUploaded(uploadedFile.getFileName());
		// seta o tamanho do arquivo
		long fileSizeUploaded = uploadedFile.getSize() / 1000;
		String infoAboutFile = "<br/> Arquivo recebido: <b>" 
                    + fileNameUploaded + "</b><br/>"
				+ "Tamanho do Arquivo: <b>" + fileSizeUploaded + " KBs</b>";
		FacesContext facesContext = FacesContext.getCurrentInstance();
		facesContext.addMessage(null, new FacesMessage("Sucesso", infoAboutFile));
		// cria o arquivo abstrato
		File file = new File(current + "" + delimitadorDiretorio 
				+ "destino_uploader" + delimitadorDiretorio + ""
				+ getCpf() + "" + delimitadorDiretorio);
		file.mkdirs();
		file = null;
		setAux(fileNameUploaded);
		copiarArquivo(delimitadorDiretorio + "destino_uploader" 
                    + delimitadorDiretorio + "" + getCpf() + ""
				+ delimitadorDiretorio + "1", uploadedFile);
		setIsUploaded(new Boolean(true));
		setIconeUploaded();
		if (aux[aux.length - 1].equals("pdf")) {
			concatenaArquivoPDF(current + delimitadorDiretorio 
					+ "destino_uploader" + delimitadorDiretorio + ""
					+ getCpf() + "" + delimitadorDiretorio + "1." + aux[1]);
		} else {
			concatenaImagem(current + delimitadorDiretorio + "destino_uploader" 
                             + delimitadorDiretorio + "" + getCpf()
					+ "" + delimitadorDiretorio + "1." + aux[1]);
		}
	}

	/**
	 * Concatena dois arquivos pds juntando suas folhas
	 * @param path
	 */
	public void concatenaArquivoPDF(String path) {
		File file1 = new File(path);
		setAux(path);
		File file2 = new File(current + delimitadorDiretorio 
				+ "destino_uploader" + delimitadorDiretorio + "" + getCpf()
				+ "" + delimitadorDiretorio + "documento." + aux[1]);
		if (file2.exists()) {
			PDFMergerUtility ut = new PDFMergerUtility();
			ut.addSource(current + delimitadorDiretorio + "destino_uploader" 
                              + delimitadorDiretorio + "" + getCpf() + ""
					+ delimitadorDiretorio + "documento." + aux[1]);
			ut.addSource(current + delimitadorDiretorio + "destino_uploader" 
					+ delimitadorDiretorio + "" + getCpf() + ""
					+ delimitadorDiretorio + "1." + aux[1]);
			ut.setDestinationFileName(current + delimitadorDiretorio 
					+ "destino_uploader" + delimitadorDiretorio + ""
					+ getCpf() + "" + delimitadorDiretorio 
					+ "documento." + aux[1]);
			try {
				ut.mergeDocuments();
			} catch (COSVisitorException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
		} else {
			file1.renameTo(new File(current + delimitadorDiretorio 
					+ "destino_uploader" + delimitadorDiretorio + ""
					+ getCpf() + "" + delimitadorDiretorio 
					+ "documento." + aux[1]));
		}
	}

	/**
	 * Copia um arquivo para o caminho parâmetro
	 * @param path
	 * @param uploadedFile
	 */
	public void copiarArquivo(String path, UploadedFile uploadedFile) {
		try {
			// escreve no arquivo o conteúdo do UploadedFile(API primeFaces)
			FileOutputStream os = new FileOutputStream(current + path + "." + aux[1]);
			os.write(uploadedFile.getContents());
			os.close();
		} catch (IOException e) {
			System.out.println("Erro de gravação do arquivo\n");
			e.printStackTrace();
		}
	}

	/**
	 * fazer Dowload de documento concatenado
	 */
	public void initDOC() {
		try {
			String contentType;
			contentType = "application/" + aux[1];
			is = new FileInputStream(current + delimitadorDiretorio 
					+ "destino_uploader" + delimitadorDiretorio + ""
					+ getCpf() + "" + delimitadorDiretorio 
					+ "documento." + aux[1]);
			streamedContent = new DefaultStreamedContent(is, 
					contentType, "documento." + aux[1]);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Redireciona a página de acordo com a origem
	 * 
	 * @return String com direcionamento da próxima página
	 */
	public String voltar() {
		if (getOrigem() == 1) {
			return "/pages/estudante/estudante2ViaArquivos.xhtml?faces-redirect=true";
		}
		if (getOrigem() == 2) {
			return "/pages/estudante/" + "estudanteAcessosArquivos.xhtml?"
					+ "faces-redirect=true";
		}
		if (getOrigem() == 3) {
			return "/pages/instituicao/arquivosCadastro.xhtml?faces-redirect=true";
		}
		return null;
	}

	public String getFileNameUploaded() {
		return fileNameUploaded;
	}

	public void setFileNameUploaded(String fileNameUploaded) {
		this.fileNameUploaded = fileNameUploaded;
	}

	public String getCpf() {
		return cpf;
	}

	public void setCpf(String cpf) {
		this.cpf = cpf;
	}

	public String getIconeUploaded() {
		return iconeUploaded;
	}

	public void setIconeUploaded(String iconeUploaded) {
		this.iconeUploaded = iconeUploaded;
	}

	/**
	 * Método que seta a imagem de uploade de determinado arquivo.
	 */
	public void setIconeUploaded() {
		if (isUploaded) {
			this.iconeUploaded = "//resources//images//checked-icon.png";
		} else {
			this.iconeUploaded = "//resources//images//unchecked-icon.png";
		}
	}

	public Boolean getIsUploaded() {
		return isUploaded;
	}

	public void setIsUploaded(Boolean isUploaded) {
		this.isUploaded = isUploaded;
	}

	public int getOrigem() {
		return origem;
	}

	public void setOrigem(int origem) {
		this.origem = origem;
	}

	public StreamedContent getDOC() {
		initDOC();
		return streamedContent;
	}

	private void setAux(String path) {
		String aux[] = path.split("\\.");
		String temp = "";
		String aux1[] = new String[2];
		for (int i = 0; i < aux.length - 1; i++) {
			temp += aux[i];
		}
		aux1[0] = temp;
		aux1[1] = aux[aux.length - 1];
		this.aux = aux1;
	}

}