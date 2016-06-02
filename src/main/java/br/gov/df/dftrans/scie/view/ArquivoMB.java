package br.gov.df.dftrans.scie.view;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;

import org.apache.pdfbox.exceptions.COSVisitorException;
import org.apache.pdfbox.util.PDFMergerUtility;
import org.primefaces.model.DefaultStreamedContent;
import org.primefaces.model.StreamedContent;

import br.gov.df.dftrans.scie.utils.Parametros;

@ManagedBean(name = "arquivoMB")
@SessionScoped
public class ArquivoMB implements Serializable {
	private String path = null;
	private DefaultStreamedContent streamedContent1, streamedContent2, streamedContent3;
	private InputStream is1 = null, is2 = null, is3 = null;
	private boolean imagem;
	private boolean arquivo;
	private int origem;
	private String delimitadorDiretorio = Parametros.getParameter("delimitador_diretorios");
	private String delimitadorDiretorioREGEX;

	/**
	 * se for um arquivo pdf,doc, docx ou odt seta imagem false(arquivo não é de
	 * imagem) se não seta como true pois é uma imagem
	 */
	public void init() {
		setDelimitadorDiretorioREGEX();
		String aux[] = getPath().split("\\.");
		if ("pdfdocxodt".contains(aux[1].toLowerCase())) {
			setImagem(false);
		} else {
			setImagem(true);
		}
	}
	
	/**
	 * Procedimentos iniciais em caso de acesso a tela de concatenação de arquivos
	 */
	public void setupConcatena() {
		setOrigem(6);
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
	 * Fazer dowload de PDF
	 */
	public void initPDF() {
		try {
			String aux[] = getPath().split("\\.");
			is1 = new FileInputStream(aux[0] + ".pdf");
			streamedContent1 = new DefaultStreamedContent(is1, "application/pdf");
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * fazer Dowload de imagem
	 */
	public void initIMG() {
		if (isImagem()) {
			try {
				String contentType;
				String aux[] = getPath().split("\\.");
				contentType = "image/" + aux[1];
				aux = getPath().split(delimitadorDiretorioREGEX);
				is2 = new FileInputStream(path);
				streamedContent2 = new DefaultStreamedContent(is2, contentType, aux[1]);// aux[1]
																						// =
																						// extensão
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	/**
	 * fazer Dowload de .doc
	 */
	public void initDOC() {
		if (!isImagem()) {
			try {
				String contentType;
				String aux[] = getPath().split("\\.");
				contentType = "application/" + aux[1];
				is3 = new FileInputStream(path);
				aux = getPath().split(delimitadorDiretorioREGEX);
				streamedContent3 = new DefaultStreamedContent(is3, contentType, aux[aux.length - 1]);// aux[aux.length-1]
																										// =
																										// extensão
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	/**
	 * Redireciona a página de acordo com a origem
	 * 
	 * @return String com direcionamento da próxima página
	 */
	public String voltar() {
		if (getOrigem() == 1) {
			return "/pages/instituicao/confirmacaoCadastro.xhtml?faces-redirect=true";
		}
		if (getOrigem() == 2) {
			return "/pages/autenticado/validador/validadorCadastro.xhtml?faces-redirect=true";
		}
		if (getOrigem() == 3) {
			return "/pages/autenticado/validador/validadorSolicitacao.xhtml?faces-redirect=true";
		}
		if (getOrigem() == 4) {
			return "/pages/autenticado/validador/validadorAcessos.xhtml?faces-redirect=true";
		}
		if (getOrigem() == 5) {
			return "/pages/autenticado/validador/validadorAcessos.xhtml?faces-redirect=true";
		}
		return null;
	}

	// getteres and setteres
	public boolean isImagem() {
		return imagem;
	}

	public void setImagem(boolean imagem) {
		this.imagem = imagem;
	}

	public int getOrigem() {
		return origem;
	}

	public void setOrigem(int origem) {
		this.origem = origem;
	}

	public StreamedContent getDOC() {
		initDOC();
		return streamedContent3;
	}

	public DefaultStreamedContent getStreamedContent1() {
		initPDF();
		return streamedContent1;
	}

	public void setStreamedContent1(DefaultStreamedContent streamedContent) {
		this.streamedContent1 = streamedContent;
	}

	public void setStreamedContent3(DefaultStreamedContent streamedContent3) {
		this.streamedContent3 = streamedContent3;
	}

	public String getPath() {
		return path;
	}

	public void setPath(String path) {
		this.path = path;
	}

	public DefaultStreamedContent getStreamedContent2() {
		initIMG();
		return streamedContent2;
	}

	public void setStreamedContent2(DefaultStreamedContent streamedContent) {
		this.streamedContent2 = streamedContent;
	}

	public DefaultStreamedContent getStreamedContent3() {
		initDOC();
		return streamedContent3;
	}

	public boolean isArquivo() {
		return arquivo;
	}

	public void setArquivo(boolean arquivo) {
		this.arquivo = arquivo;
	}
}