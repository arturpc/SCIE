package br.gov.df.dftrans.scie.view;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.DateFormat;
import java.text.Normalizer;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
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
import br.gov.df.dftrans.scie.dao.FrequenciaDAO;
import br.gov.df.dftrans.scie.dao.InstituicaoEnsinoDAO;
import br.gov.df.dftrans.scie.domain.Estudante;
import br.gov.df.dftrans.scie.domain.Frequencia;
import br.gov.df.dftrans.scie.domain.InstituicaoEnsino;
import br.gov.df.dftrans.scie.domain.MesReferencia;
import br.gov.df.dftrans.scie.domain.Representante;
import br.gov.df.dftrans.scie.exceptions.EntityNotFoundException;
import br.gov.df.dftrans.scie.exceptions.InsertException;
import br.gov.df.dftrans.scie.exceptions.PlanilhaException;
import br.gov.df.dftrans.scie.utils.AutenticacaoDocumentos;
import br.gov.df.dftrans.scie.utils.ManipuladorArquivos;
import br.gov.df.dftrans.scie.utils.Parametros;

@ManagedBean(name = "FrequenciaMB")
@SessionScoped
public class FrequenciaBean {
	private List<MesReferencia> meses = new ArrayList<MesReferencia>();
	private MesReferencia mes = null;
	private int valorMes;
	@SuppressWarnings("deprecation")
	private int dataMes = new Date().getMonth();
	@SuppressWarnings("deprecation")
	private int dataAno = new Date().getYear();
	private InstituicaoEnsino instituicao;
	private Set<Frequencia> frequencias;
	private StreamedContent fileout;
	private String icone = "//resources//images//unchecked-icon.png";
	private String fileNameUploaded;
	private DateFormat fmt = new SimpleDateFormat("dd-MM-yyyy");
	private String current = Parametros.getParameter("root_upload");
	private FrequenciaDAO freqdao = FrequenciaDAO.FrequenciaDAO();
	private String chave = "";
	private boolean erroProcessamento;
	private String[] nomeMeses = { "Janeiro", "Fevereiro", "Março", "Abril", 
            "Maio", "Junho", "Julho", "Agosto","Setembro", "Outubro", 
            "Novembro", "Dezembro" };
	private String delimitadorDiretorio = Parametros.getParameter("delimitador_diretorios");
	private String delimitadorDiretorioREGEX;
	private Representante representante;

	/**
	 * Inicializa instituicao com a instituicao na sessão, e seta uma array de
	 * frequencias com todas persistidas no banco
	 */
	public void init() {
		setDelimitadorDiretorioREGEX();
		HttpSession session = (HttpSession) FacesContext
				.getCurrentInstance().getExternalContext().getSession(false);
		setInstituicao((InstituicaoEnsino) session.getAttribute("instituicao"));
		try {
			ArrayList<Frequencia> freqlist = (ArrayList<Frequencia>) 
					freqdao.getInst(getInstituicao());
			setFrequencias(new HashSet<Frequencia>());
			for (Frequencia freq : freqlist) {
				getFrequencias().add(freq);
			}
		} catch (EntityNotFoundException e) {
			e.printStackTrace();
		}
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
	 * Método que trata upload de arquivos
	 * @param fileUploadEvent
	 */
	public void doUpload(FileUploadEvent fileUploadEvent) {
		int tipo = 0;
		// trata o arquvo que o usuário subiu
		UploadedFile uploadedFile = fileUploadEvent.getFile();
		// seta o nome do arquivo
		setFileNameUploaded(uploadedFile.getFileName());
		// seta o tamanho do arquivo
		long fileSizeUploaded = uploadedFile.getSize() / 1000;
		String infoAboutFile = "<br/> Arquivo recebido: <b>" 
                     + fileNameUploaded + "</b><br/>"
				+ "Tamanho do Arquivo: <b>" + fileSizeUploaded + " KBs</b>";
		// seta Instituicao com a da sessao
		HttpSession session = (HttpSession) FacesContext
				.getCurrentInstance().getExternalContext().getSession(false);
		setInstituicao((InstituicaoEnsino) session.getAttribute("instituicao"));
		if (instituicao == null) {
			try {
				FacesContext.getCurrentInstance()
				.getExternalContext().redirect("instituicaoHome.xhtml");
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		// cria o arquivo abstrato
		File file = new File(current + "" + delimitadorDiretorio 
				+ "destino_uploader" + delimitadorDiretorio + ""
				+ getInstituicao().getId() + "" + delimitadorDiretorio 
				+ "frequencias" + delimitadorDiretorio + ""
				+ getMes().getValue() + "" + delimitadorDiretorio);
		file.mkdirs();
		try {
			// pega o tipo do arquivo
			String[] aux = getFileNameUploaded().split("\\.");
			if (aux[1].toLowerCase().equals("ods")) {
				tipo = ManipuladorArquivos.ODS;
			}
			if (aux[1].toLowerCase().equals("xls")) {
				tipo = ManipuladorArquivos.XLS;
			}
			if (aux[1].toLowerCase().equals("xlsx")) {
				tipo = ManipuladorArquivos.XLSX;
			}
			String path = current + "" + delimitadorDiretorio 
					+ "destino_uploader" + delimitadorDiretorio + ""
					+ instituicao.getId() + "" + delimitadorDiretorio 
					+ "frequencias" + delimitadorDiretorio + ""
					+ getMes().getValue() + "" + delimitadorDiretorio 
					+ "frequencias." + aux[1];
			// escreve no arquivo o conteúdo do UploadedFile(API primeFaces)
			FileOutputStream os = new FileOutputStream(path);
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
			chave = instituicao.getCnpj();
			chave += instituicao.getId();
			setRepresentante((Representante) session.getAttribute("representante"));
			chave += getRepresentante().getNome();
			chave += getRepresentante().getCpf();
			chave += fmt.format(new Date());
			chave += 8;
			chave = chave.replaceAll(delimitadorDiretorioREGEX, "")
					.replaceAll(" ", "").replaceAll("-", "");
			contentStream.drawString("Autenticação: " 
                            + AutenticacaoDocumentos.getChaveSeguranca(chave));
			contentStream.endText();
			contentStream.close();
			doc.save(current + "" + delimitadorDiretorio + "destino_uploader" 
                            + delimitadorDiretorio + ""
					+ instituicao.getId() + "" + delimitadorDiretorio 
					+ "frequencias" + delimitadorDiretorio + ""
					+ getMes().getValue() + "" + delimitadorDiretorio 
					+ "frequencias"
					+ ManipuladorArquivos.ARQ_NAME[tipo] + ".pdf");
			String[][] temp = ManipuladorArquivos.lerPlanilha(path);
			persistirFrequencias(temp, AutenticacaoDocumentos
					.getChaveSeguranca(chave), tipo);
			if (!erroProcessamento) {
				setIcone("//resources//images//checked-icon.png");
			}
		} catch (IOException e) {
			e.printStackTrace();
		} catch (COSVisitorException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Método que mantêm frequências constantes do arquivo anexo na plataforma.
	 * @param temp
	 * @param chave
	 * @param tipo
	 * @return
	 */
	public String persistirFrequencias(String[][] temp, String chave, int tipo) {
		InstituicaoEnsino inst;
		InstituicaoEnsinoDAO instdao = InstituicaoEnsinoDAO.InstituicaoEnsinoDAO();
		FrequenciaDAO freqdao = FrequenciaDAO.FrequenciaDAO();
		ArrayList<Estudante> listest = new ArrayList<Estudante>();
		ArrayList<Frequencia> listfreq = new ArrayList<Frequencia>();
		Set<String> listcpf = new HashSet<String>();
		String campo;
		String erro = null;
		erroProcessamento = false;
		try {
			// verifica se existe o id da instituição de ensino
			erro = temp[3][1];
			if (!temp[3][1].trim().equals("" + getInstituicao().getId())) {
				throw new PlanilhaException(
						"Código de cadastro não reconhecido!<br/>Favor "
						+ "verifique se o código digitado é o mesmo "
						+ "apresentado na página de upload!!!");
			}
			inst = instdao.getById(Integer.parseInt(temp[3][1]));
			if (inst == null) {
				throw new PlanilhaException(
						"Código de cadastro não reconhecido!<br/>Favor "
						+ "verifique se o código digitado é o mesmo "
						+ "apresentado na página de upload!!!");
			}
			// verifica se o cnpj coincide com os dados da instituição
			campo = temp[3][3].replaceAll("\\.", "")
					.replaceAll(delimitadorDiretorioREGEX, "")
					.replaceAll("-", "").replaceAll(" ", "")
					.replaceAll("/", "").trim();
			if (campo == null || "null".equals(campo) || campo.isEmpty() 
					|| !inst.getCnpj().equals(campo)) {
				throw new PlanilhaException("CNPJ não confere com o "
						+ "cadastrado ( " + inst.getCnpj() + " )!");
			}
			// analiza caso específico xls, verifica a célula de forma
			// diferente, pelo fato de se ter mais de uma célula
			if (tipo == ManipuladorArquivos.XLS) {
				// verifica o nome da instituição
				campo = Normalizer.normalize(temp[3][5], Normalizer.Form.NFD)
						.replaceAll("[^\\p{ASCII}]", "")
						.toUpperCase().trim();
				if (campo == null || "null".equals(campo) || campo.isEmpty()
						|| !campo.contains(inst.getNomeInstituicao())) {
					throw new PlanilhaException(
							"Nome fantasia não confere com o "
							+ "cadastrado ( " 
							+ inst.getNomeInstituicao() 
							+ " )!");
				}
				campo = Normalizer.normalize(temp[3][7], Normalizer.Form.NFD)
						.replaceAll("[^\\p{ASCII}]", "")
						.toUpperCase().trim();
			} else {
				// verifica a nome da instituição para arquivos que não são xls
				campo = Normalizer.normalize(temp[3][6], Normalizer.Form.NFD)
						.replaceAll("[^\\p{ASCII}]", "")
						.toUpperCase().trim();
				if (campo == null || "null".equals(campo) || campo.isEmpty()
						|| !campo.contains(inst.getNomeInstituicao())) {
					throw new PlanilhaException(
					"Nome fantasia não confere com o cadastrado ( " 
					+ inst.getNomeInstituicao() + " )!");
				}
				// seta razão social
				campo = Normalizer.normalize(temp[3][8], Normalizer.Form.NFD)
						.replaceAll("[^\\p{ASCII}]", "")
						.toUpperCase().trim();
			}
			// verifica a razão social para arquivos que não são xls
			if (campo == null || "null".equals(campo) || campo.isEmpty() 
                            || !campo.contains(inst.getRazaoSocial())) {
				throw new PlanilhaException(
						"Razão Social não confere com a cadastrada ( " 
				+ inst.getRazaoSocial() + " )!");
			}
			// enquanto não for nulo
			for (int i = 6; (temp[i][1] != null && !temp[i][1].equals("null"))
					|| (temp[i][2] != null && !temp[i][2].equals("null"))
					|| (temp[i][3] != null && !temp[i][3].equals("null"))
					|| (temp[i][4] != null && !temp[i][4].equals("null"))
					|| (temp[i][5] != null && !temp[i][5].equals("null"))
					|| (temp[i][6] != null && !temp[i][6].equals("null"))
					|| (temp[i][7] != null && !temp[i][7].equals("null"))
					|| (temp[i][8] != null && !temp[i][8].equals("null"))
					|| (temp[i][9] != null && 
					!temp[i][9].equals("null")); i++) {
				Estudante est = new Estudante();
				Frequencia freq = new Frequencia();
				DateFormat fmt = new SimpleDateFormat("dd/MM/yyyy");
				// caso matricula seja nula ou vazia
				if (temp[i][1] == null || temp[i][1].equals("null") 
						|| temp[i][1].trim().isEmpty()) {
					throw new PlanilhaException("Matrícula (nula) inválida "
					+ "na linha " + i + "!");
				}
				// caso não seja nula seta a matricula do estudante
				est.setMatricula(temp[i][1]);
				// caso nome completo seja nulo
				if (temp[i][2] == null || temp[i][2].equals("null") 
						|| temp[i][2].trim().isEmpty()) {
					throw new PlanilhaException("Nome do aluno (nulo) inválido "
					+ "na linha " + i + "!");
				}
				// seta campo nome do estudante sem acentos ou caracteres
				// especiais
				campo = Normalizer.normalize(temp[i][2], Normalizer.Form.NFD)
						.replaceAll("[^\\p{ASCII}]", "")
						.toUpperCase();
				est.setNome(campo);
				// verifica problemas de formatação referente a data de
				// nascimento, campo inteiro(number format exception) trata pra
				// mim
				erro = temp[i][3];
				est.setDataNascimento(fmt.parse(temp[i][3]));
				erro = temp[i][4];
				// verifica problemas de formatação referente ao CPF
				campo = temp[i][4].replaceAll("\\.", "")
						.replaceAll(delimitadorDiretorioREGEX, "")
						.replaceAll(" ", "")
						.replaceAll("-", "").trim();
				if (campo == null || "null".equals(campo) || campo.isEmpty()) {
					throw new PlanilhaException("CPF do estudante (nulo) "
							+ "inválido na linha " + i + "!");
				}
				// verifica se só existem npumeros no CPF
				Pattern intsOnly = Pattern.compile("[0-9]+");
				Matcher makeMatch = intsOnly.matcher(campo);
				makeMatch.find();
				if (makeMatch.group().isEmpty()) {
					throw new PlanilhaException(
							"CPF do estudante " + temp[i][4] 
							+ " contem letras na linha " + i + "!");
				}
				if (!listcpf.add(campo)) {
					throw new PlanilhaException("Há CPF duplicado na linha " 
				+ i + "!");
				}
				// seta CPF para o estudante
				est.setCpf(campo);
				// verifica problemas de formatação referente ao nome do
				// responsável
				if (temp[i][5] == null || temp[i][5].equals("null") 
						|| temp[i][5].trim().isEmpty()) {
					throw new PlanilhaException("Nome do responsável (nulo) "
					+ "inválido na linha " + i + "!");
				}
				campo = Normalizer.normalize(temp[i][5], Normalizer.Form.NFD)
						.replaceAll("[^\\p{ASCII}]", "")
						.toUpperCase();
				// seta o nome do responsável
				est.setResponsavel(campo);
				// verifica problemas de formatação referente ao nome do curso
				if (temp[i][6] == null || temp[i][6].equals("null") 
						|| temp[i][6].trim().isEmpty()) {
					throw new PlanilhaException("Nome do Curso (nulo) "
					+ "inválido na linha " + i + "!");
				}
				campo = Normalizer.normalize(temp[i][6], Normalizer.Form.NFD)
						.replaceAll("[^\\p{ASCII}]", "")
						.toUpperCase();
				// seta o nome do curso
				est.setCurso(campo);
				// verifica problemas de formatação referente ao nível do curso
				if (temp[i][7] == null || temp[i][7].equals("null") 
						|| temp[i][7].trim().isEmpty()) {
					throw new PlanilhaException("Nível do Curso (nulo) "
							+ "inválido na linha " + i + "!");
				}
				campo = Normalizer.normalize(temp[i][7], Normalizer.Form.NFD)
						.replaceAll("[^\\p{ASCII}]", "")
						.toUpperCase();
				// seta o nível do curso
				est.setNivel(campo);
				est.setInstituicao(inst);
				// seta serie/ grau ou período pra verificar se é número
				erro = temp[i][8];
				est.setPeriodo(Integer.parseInt(temp[i][8]));
				// salva o estudante implementado
				listest.add(est);
				// seta frequencia do estudante implementado
				freq.setEstudante(est);
				freq.setAutenticacao(chave);
				freq.setInstituicao(inst);
				freq.setDataReferencia(getMes().getValue());
				freq.setAutenticacao(chave);
				campo = temp[i][9].toUpperCase();
				if (!campo.startsWith("P") && !campo.startsWith("A")) {
					throw new PlanilhaException("O valor " + campo
							+ " não é um valor válido para Presença/"
							+ "Ausência na linha " + i + "! Substitua "
							+ "por P ou A!");
				}
				if (campo.startsWith("P")) {
					freq.setFrequencia(0);
				}
				if (campo.startsWith("A")) {
					freq.setFrequencia(1);
				}
				listfreq.add(freq);
			}
			freqdao.add(listfreq);
			getFrequencias().addAll(listfreq);
		} catch (NumberFormatException e) {
			erroProcessamento = true;
			FacesContext facesContext = FacesContext.getCurrentInstance();
			facesContext.addMessage(null, new FacesMessage(FacesMessage
					.SEVERITY_ERROR, "Erro de Conversão do Arquivo!",
					"O valor " + erro + " não é um número válido!"));
		} catch (EntityNotFoundException e) {
			erroProcessamento = true;
		} catch (PlanilhaException e) {
			erroProcessamento = true;
			FacesContext facesContext = FacesContext.getCurrentInstance();
			facesContext.addMessage(null,
					new FacesMessage(FacesMessage.SEVERITY_ERROR, 
					"Erro de Conversão do Arquivo!", e.getMessage()));
		} catch (ParseException e) {
			erroProcessamento = true;
			FacesContext facesContext = FacesContext.getCurrentInstance();
			facesContext.addMessage(null, new FacesMessage(FacesMessage
					.SEVERITY_ERROR, "Erro de Conversão do Arquivo!",
					"O valor " + erro + " não é uma data válida!"));
		} catch (InsertException e) {
			erroProcessamento = true;
			FacesContext facesContext = FacesContext.getCurrentInstance();
			facesContext.addMessage(null, new FacesMessage(FacesMessage
					.SEVERITY_ERROR, "Erro de Banco de Dados!",
					"Erro de Inserção no Banco de Dados! "
					+ "Tente novamente em alguns instantes"));
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * Método Construtor
	 */
	public FrequenciaBean() {
		// pega instituicão da sessão
		HttpSession sessao = (HttpSession) FacesContext.getCurrentInstance()
				.getExternalContext().getSession(false);
		setInstituicao((InstituicaoEnsino) sessao.getAttribute("instituicao"));
		setMeses(new ArrayList<MesReferencia>());
		MesReferencia mesref;
		// seta o mes de referência
		switch (getDataMes()) {
		case 0:
			mesref = new MesReferencia(nomeMeses[11] + "" + delimitadorDiretorio 
					+ "" + (getDataAno() + 1899),
					Integer.parseInt((getDataAno() + 1900 - 1) + "" + 12));
			getMeses().add(mesref);
			mesref = new MesReferencia(nomeMeses[getDataMes()] + "" 
			+ delimitadorDiretorio + "" + (getDataAno() + 1900),
					Integer.parseInt((getDataAno() + 1900) 
							+ "" + (getDataMes() + 1)));
			getMeses().add(mesref);
			break;
		default:
			mesref = new MesReferencia(
					nomeMeses[getDataMes() - 1] + "" + delimitadorDiretorio 
					+ "" + (getDataAno() + 1900),
					Integer.parseInt((getDataAno() + 1900) + "" 
					+ getDataMes()));
			getMeses().add(mesref);
			mesref = new MesReferencia(nomeMeses[getDataMes()] + "" 
			+ delimitadorDiretorio + "" + (getDataAno() + 1900),
					Integer.parseInt((getDataAno() + 1900) 
							+ "" + (getDataMes() + 1)));
			getMeses().add(mesref);
			break;
		}
	}

	/**
	 * Download de arquivo xls
	 * 
	 * @return o arquivo xls
	 */
	public StreamedContent getXls() {
		InputStream stream = null;
		try {
			stream = new FileInputStream(current + "" + delimitadorDiretorio 
					+ "destino_uploader" + delimitadorDiretorio
					+ "templates" + delimitadorDiretorio + "frequencia.xls");
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		fileout = new DefaultStreamedContent(stream, "application/xls", "frequencia.xls");
		return fileout;
	}

	/**
	 * Download de arquivo xlsx
	 * 
	 * @return o arquivo xlsx
	 */
	public StreamedContent getXlsx() {
		InputStream stream = null;
		try {
			stream = new FileInputStream(current + "" + delimitadorDiretorio 
					+ "destino_uploader" + delimitadorDiretorio
					+ "templates" + delimitadorDiretorio + "frequencia.xlsx");
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		fileout = new DefaultStreamedContent(stream, "application/xlsx", "frequencia.xlsx");
		return fileout;
	}

	/**
	 * Dowload de arquivo ods
	 * 
	 * @return o arquivo ods
	 */
	public StreamedContent getOds() {
		InputStream stream = null;
		try {
			stream = new FileInputStream(current + "" + delimitadorDiretorio 
					+ "destino_uploader" + delimitadorDiretorio
					+ "templates" + delimitadorDiretorio + "frequencia.ods");
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		fileout = new DefaultStreamedContent(stream, "application/ods", "frequencia.ods");
		return fileout;
	}

	/**
	 * Método que seta a variável o nome do mês
	 */
	public void mostrarMes() {
		for (MesReferencia ref : getMeses()) {
			if (ref.getValue() == getValorMes()) {
				setMes(ref);
			}
		}
	}

	// getteres and setteres
	public int getDataMes() {
		return dataMes;
	}

	public void setDataMes(int dataMes) {
		this.dataMes = dataMes;
	}

	public int getDataAno() {
		return dataAno;
	}

	public void setDataAno(int dataAno) {
		this.dataAno = dataAno;
	}

	public List<MesReferencia> getMeses() {
		return meses;
	}

	public void setMeses(List<MesReferencia> meses) {
		this.meses = meses;
	}

	public MesReferencia getMes() {
		return mes;
	}

	public void setMes(MesReferencia mes) {
		this.mes = mes;
	}

	public InstituicaoEnsino getInstituicao() {
		return instituicao;
	}

	public void setInstituicao(InstituicaoEnsino instituicao) {
		this.instituicao = instituicao;
	}

	public Set<Frequencia> getFrequencias() {
		return frequencias;
	}

	public void setFrequencias(Set<Frequencia> frequencias) {
		this.frequencias = frequencias;
	}

	public StreamedContent getFileout() {
		return fileout;
	}

	public void setFileout(StreamedContent fileout) {
		this.fileout = fileout;
	}

	public String getFileNameUploaded() {
		return fileNameUploaded;
	}

	public void setFileNameUploaded(String fileNameUploaded) {
		this.fileNameUploaded = fileNameUploaded;
	}

	public String getIcone() {
		return icone;
	}

	public void setIcone(String icone) {
		this.icone = icone;
	}

	public int getValorMes() {
		return valorMes;
	}

	public void setValorMes(int valorMes) {
		this.valorMes = valorMes;
	}

	public Representante getRepresentante() {
		return representante;
	}

	public void setRepresentante(Representante representante) {
		this.representante = representante;
	}

	
}