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
import java.util.Calendar;
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

import br.gov.df.dftrans.scie.dao.DeclaracaoDAO;
import br.gov.df.dftrans.scie.dao.EstudanteDAO;
import br.gov.df.dftrans.scie.dao.InstituicaoCursoDAO;
import br.gov.df.dftrans.scie.dao.InstituicaoEnsinoDAO;
import br.gov.df.dftrans.scie.domain.Curso;
import br.gov.df.dftrans.scie.domain.Declaracao;
import br.gov.df.dftrans.scie.domain.Estudante;
import br.gov.df.dftrans.scie.domain.InstituicaoEnsino;
import br.gov.df.dftrans.scie.domain.Representante;
import br.gov.df.dftrans.scie.exceptions.EntityNotFoundException;
import br.gov.df.dftrans.scie.exceptions.InsertException;
import br.gov.df.dftrans.scie.exceptions.PlanilhaException;
import br.gov.df.dftrans.scie.utils.AutenticacaoDocumentos;
import br.gov.df.dftrans.scie.utils.ManipuladorArquivos;
import br.gov.df.dftrans.scie.utils.Parametros;

@ManagedBean(name = "DeclaracaoMB")
@SessionScoped
public class DeclaracaoBean {
	private StreamedContent fileout;
	private String icone = "//resources//images//unchecked-icon.png";
	private String fileNameUploaded;
	private InstituicaoEnsino inst;
	private String current = Parametros.getParameter("root_upload");
	private Date date = Calendar.getInstance().getTime();
	private DateFormat fmt = new SimpleDateFormat("dd-MM-yyyy");
	private String chave = "";
	private InstituicaoCursoDAO instdao = InstituicaoCursoDAO.InstituicaoCursoDAO();
	private DeclaracaoDAO decdao = DeclaracaoDAO.DeclaracaoDAO();
	private List<Curso> cursos;
	private Set<Declaracao> declaracoes;
	private boolean erroProcessamento;
	private String delimitadorDiretorio = Parametros.getParameter("delimitador_diretorios");
	private String delimitadorDiretorioREGEX;
	private Representante representante;
	
	
	/**
	 * M�todo construtor.
	 */
	public DeclaracaoBean() {
		HttpSession sessao = (HttpSession) FacesContext
				.getCurrentInstance().getExternalContext().getSession(false);
		setInst((InstituicaoEnsino) sessao.getAttribute("instituicao"));
		try {
			setCursos(instdao.getCursos(getInst().getId()));
		} catch (EntityNotFoundException e) {
			e.printStackTrace();
		}
	}

	/**
	 * inicia declara��es
	 */
	public void init() {
		setDelimitadorDiretorioREGEX();
		// pega os dados da instituicao da sess�o
		HttpSession session = (HttpSession) FacesContext
				.getCurrentInstance().getExternalContext().getSession(false);
		setInst((InstituicaoEnsino) session.getAttribute("instituicao"));
		try {
			// popula list de Declaracao com todas as declara��es a partir de
			// uma institui��o
			ArrayList<Declaracao> dec = (ArrayList<Declaracao>) 
					decdao.getInst(getInst());
			// evitar duplicdade
			setDeclaracoes(new HashSet<Declaracao>());
			for (Declaracao decl : dec) {
				getDeclaracoes().add(decl);
			}
		} catch (EntityNotFoundException e) {
			e.printStackTrace();
		}
	}

	/**
	 * M�todo respons�vel por tratar caracteres reservados em expres�es
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
	 * faz upload do arquivo que o usu�rio subir
	 * 
	 * @param fileUploadEvent
	 */
	public void doUpload(FileUploadEvent fileUploadEvent) {
		int tipo = 0;
		// trata o arquivo que o usu�rio subiu
		UploadedFile uploadedFile = fileUploadEvent.getFile();
		// seta o nome do arquivo
		setFileNameUploaded(uploadedFile.getFileName());
		// seta o tamanho do arquivo
		long fileSizeUploaded = uploadedFile.getSize() / 1000;
		String infoAboutFile = "<br/> Arquivo recebido: <b>" 
                    + fileNameUploaded + "</b><br/>"
				+ "Tamanho do Arquivo: <b>" + fileSizeUploaded + " KBs</b>";
		// seta a instituicao com a da sessao
		HttpSession session = (HttpSession) FacesContext
				.getCurrentInstance().getExternalContext().getSession(false);
		setInst((InstituicaoEnsino) session.getAttribute("instituicao"));
		if (inst == null) {
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
				+ inst.getId() + "" + delimitadorDiretorio 
				+ "declaracoes" + delimitadorDiretorio + ""
				+ fmt.format(date) + "" + delimitadorDiretorio);
		// cria a �rvore de diret�rios de arquivos
		file.mkdirs();
		try {
			// descobre a extens�o do arquivo
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
			// pega o caminho do arquivo e escreve no arquivo
			String path = current + "" + delimitadorDiretorio 
					+ "destino_uploader" + delimitadorDiretorio + ""
					+ inst.getId() + "" + delimitadorDiretorio 
					+ "declaracoes" + delimitadorDiretorio + ""
					+ fmt.format(date) + "" + delimitadorDiretorio 
					+ "declaracoes." + aux[1];
			FileOutputStream os = new FileOutputStream(path);
			os.write(uploadedFile.getContents());
			os.close();
			// biblioteca para criar pdf
			PDDocument doc = new PDDocument();
			PDPage page = new PDPage();
			doc.addPage(page);
			PDPageContentStream contentStream = new PDPageContentStream(doc, page);
			contentStream.beginText();
			contentStream.moveTextPositionByAmount(100, 700);
			contentStream.setFont(PDType1Font.HELVETICA, 12);
			// concatenando as informa��es da institui��o para converter em MD5
			// e ser usada como chave
			chave = inst.getCnpj();
			chave += inst.getId();
			setRepresentante((Representante) session.getAttribute("representante"));
			chave += getRepresentante().getNome();
			chave += getRepresentante().getCpf();
			chave += fmt.format(new Date());
			chave += 7;
			chave = chave.replaceAll(delimitadorDiretorioREGEX, "")
					.replaceAll(" ", "").replaceAll("-", "");
			contentStream.drawString("Autentica��o: " 
					+ AutenticacaoDocumentos.getChaveSeguranca(chave));
			contentStream.endText();
			contentStream.close();
			// salva o pdf na pasta referente ao tipo do arquivo
			doc.save(current + "" + delimitadorDiretorio + 
					"destino_uploader" + delimitadorDiretorio 
					+ "" + inst.getId()
					+ "" + delimitadorDiretorio + "declaracoes" 
					+ delimitadorDiretorio + "" + fmt.format(date) + ""
					+ delimitadorDiretorio + "declaracoes" 
					+ ManipuladorArquivos.ARQ_NAME[tipo] + ".pdf");
			String[][] temp = ManipuladorArquivos.lerPlanilha(path);
			// persiste os dados da declara��o
			persistirDeclaracoes(temp, AutenticacaoDocumentos
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

	// 
	/**
	 * Seleciona arquivo xls
	 * @return
	 */
	public StreamedContent getXls() {
		InputStream stream = null;
		try {

			stream = new FileInputStream(current + "" + delimitadorDiretorio 
					+ "destino_uploader" + delimitadorDiretorio
					+ "templates" + delimitadorDiretorio + "declaracao.xls");
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		fileout = new DefaultStreamedContent(stream, "application/xls", "declaracao.xls");
		return fileout;
	}
	
	/**
	 * Seleciona um arquivo XLSX
	 * @return
	 */
	public StreamedContent getXlsx() {
		InputStream stream = null;
		try {
			stream = new FileInputStream(current + "" + delimitadorDiretorio 
					+ "destino_uploader" + delimitadorDiretorio
					+ "templates" + delimitadorDiretorio + "declaracao.xlsx");
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		fileout = new DefaultStreamedContent(stream, "application/xlsx", "declaracao.xlsx");
		return fileout;
	}

	/**
	 * Seleciona um arquivo ODS
	 * @return
	 */
	public StreamedContent getOds() {
		InputStream stream = null;
		try {
			stream = new FileInputStream(current + "" + delimitadorDiretorio 
					+ "destino_uploader" + delimitadorDiretorio
					+ "templates" + delimitadorDiretorio + "declaracao.ods");
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		fileout = new DefaultStreamedContent(stream, "application/ods", "declaracao.ods");
		return fileout;
	}

	/**
	 * Atrav�s da matriz recebida que representa a planilha de declara��es
	 * enviadas pelos representantes das institui��es, carrega as informa��es de
	 * declara��es de alunos no banco
	 * 
	 * @param temp
	 * @param chave
	 * @param tipo
	 * @return uma String que representa o novo redirecionamento
	 */
	public String persistirDeclaracoes(String[][] temp, String chave, int tipo) {
		InstituicaoEnsino inst;
		EstudanteDAO estdao = EstudanteDAO.EstudanteDAO();
		InstituicaoEnsinoDAO instdao = InstituicaoEnsinoDAO.InstituicaoEnsinoDAO();
		ArrayList<Estudante> listest = new ArrayList<Estudante>();
		ArrayList<Declaracao> listdecl = new ArrayList<Declaracao>();
		Set<String> listcpf = new HashSet<String>();
		String campo;
		String erro = null;
		erroProcessamento = false;
		try {
			// verifica se existe o id da institui��o de ensino
			erro = temp[3][1].trim();
			if (!temp[3][1].equals("" + getInst().getId())) {
				throw new PlanilhaException(
						"C�digo de cadastro n�o reconhecido!<br/>Favor "
						+ "verifique se o c�digo digitado � o mesmo "
						+ "apresentado na p�gina de upload!!!");
			}
			// seta institui��o com id presente na planilha
			inst = instdao.getById(Integer.parseInt(temp[3][1]));
			if (inst == null) {
				throw new PlanilhaException(
						"C�digo de cadastro n�o reconhecido!<br/>Favor "
						+ "verifique se o c�digo digitado � o mesmo "
						+ "apresentado na p�gina de upload!!!");
			}
			// verifica se o cnpj coincide com os dados da institui��o
			campo = temp[3][3].replaceAll("\\.", "").replaceAll(
					delimitadorDiretorioREGEX, "").replaceAll("-", "")
					.replaceAll(" ", "").replaceAll("/", "").trim();
			if (campo == null || "null".equals(campo) 
					|| campo.isEmpty() || !inst.getCnpj().equals(campo)) {
				throw new PlanilhaException("CNPJ n�o confere com "
						+ "o cadastrado ( " + inst.getCnpj() + " )!");
			}
			// analiza caso espec�fico xls, verifica a c�lula de forma
			// diferente, pelo fato de se ter mais de uma c�lula
			if (tipo == ManipuladorArquivos.XLS) {
				// verifica o nome da institui��o
				campo = Normalizer.normalize(temp[3][5],
						Normalizer.Form.NFD).replaceAll("[^\\p{ASCII}]", "")
						.toUpperCase().trim();
				if (campo == null || "null".equals(campo) || campo.isEmpty()
						|| !campo.contains(inst.getNomeInstituicao())) {
					throw new PlanilhaException(
							"Nome fantasia n�o confere "
							+ "com o cadastrado ( " 
					+ inst.getNomeInstituicao() + " )!");
				}
				campo = Normalizer.normalize(temp[3][7],
						Normalizer.Form.NFD).replaceAll("[^\\p{ASCII}]", "")
						.toUpperCase().trim();
			} else {
				// verifica a nome da institui��o para arquivos que n�o s�o xls
				campo = Normalizer.normalize(temp[3][6],
						Normalizer.Form.NFD).replaceAll("[^\\p{ASCII}]", "")
						.toUpperCase().trim();
				if (campo == null || "null".equals(campo) || campo.isEmpty()
						|| !campo.contains(inst.getNomeInstituicao())) {
					throw new PlanilhaException(
							"Nome fantasia n�o confere "
							+ "com o cadastrado ( " 
							+ inst.getNomeInstituicao() + " )!");
				}
				// seta raz�o social
				campo = Normalizer.normalize(temp[3][10], 
						Normalizer.Form.NFD).replaceAll("[^\\p{ASCII}]", "")
						.toUpperCase().trim();
			}
			// verifica a raz�o social para arquivos que n�o s�o xls
			if (campo == null || "null".equals(campo)
					|| campo.isEmpty() || !campo.contains(
							inst.getRazaoSocial())) {
				throw new PlanilhaException(
						"Raz�o Social n�o confere com a cadastrada ( " 
				+ inst.getRazaoSocial() + " )!");
			}
			// enquanto n�o for nulo
			for (int i = 6; (temp[i][1] != null && !temp[i][1].equals("null"))
					|| (temp[i][2] != null && !temp[i][2].equals("null"))
					|| (temp[i][3] != null && !temp[i][3].equals("null"))
					|| (temp[i][4] != null && !temp[i][4].equals("null"))
					|| (temp[i][5] != null && !temp[i][5].equals("null"))
					|| (temp[i][6] != null && !temp[i][6].equals("null"))
					|| (temp[i][7] != null && !temp[i][7].equals("null"))
					|| (temp[i][8] != null && !temp[i][8].equals("null"))
					|| (temp[i][9] != null && !temp[i][9].equals("null"))
					|| (temp[i][10] != null && !temp[i][10].equals("null"))
					|| (temp[i][11] != null 
					&& !temp[i][11].equals("null")); i++) {
				Estudante est = new Estudante();
				Declaracao decl = new Declaracao();
				DateFormat fmt = new SimpleDateFormat("dd/MM/yyyy");
				// caso matricula seja nula ou vazia
				if (temp[i][1] == null || temp[i][1].equals("null") 
						|| temp[i][1].trim().isEmpty()) {
					throw new PlanilhaException("Matr�cula (nula) "
							+ "inv�lida na linha " + i + "!");
				}
				// caso n�o seja nula seta a matricula do estudante
				est.setMatricula(temp[i][1]);
				// caso nome completo seja nulo
				if (temp[i][2] == null || temp[i][2].equals("null") 
						|| temp[i][2].trim().isEmpty()) {
					throw new PlanilhaException("Nome do aluno "
							+ "(nulo) inv�lido na linha " + i + "!");
				}
				// seta campo nome do estudante sem acentos ou caracteres
				// especiais
				campo = Normalizer.normalize(temp[i][2],
						Normalizer.Form.NFD).replaceAll("[^\\p{ASCII}]", "")
						.toUpperCase().trim();
				est.setNome(campo);
				// verifica problemas de formata��o referente a data de
				// nascimento, campo inteiro(number format exception) trata pra
				// mim
				erro = temp[i][3];
				est.setDataNascimento(fmt.parse(temp[i][3]));
				erro = temp[i][4];
				// verifica problemas de formata��o referente ao CPF
				campo = temp[i][4].replaceAll("\\.", "")
						.replaceAll(delimitadorDiretorioREGEX, "")
						.replaceAll(" ", "")
						.replaceAll("-", "");
				if (campo == null || "null".equals(campo) || campo.isEmpty()) {
					throw new PlanilhaException("CPF do estudante "
							+ "(nulo) inv�lido na linha " + i + "!");
				}
				// verifica se s� existem npumeros no CPF
				Pattern intsOnly = Pattern.compile("[0-9]+");
				Matcher makeMatch = intsOnly.matcher(campo);
				makeMatch.find();
				if (makeMatch.group().isEmpty()) {
					throw new PlanilhaException(
							"CPF do estudante " + temp[i][4] 
							+ " cont�m letras na linha " + i + "!");
				}
				if (!listcpf.add(campo)) {
					throw new PlanilhaException("H� CPF duplicado "
							+ "na linha " + i + "!");
				}
				// seta CPF para o estudante
				est.setCpf(campo);
				// verifica problemas de formata��o referente ao nome do
				// respons�vel
				if (temp[i][5] == null || temp[i][5].equals("null") 
						|| temp[i][5].trim().isEmpty()) {
					throw new PlanilhaException("Nome do respons�vel "
							+ "(nulo) inv�lido na linha " + i + "!");
				}
				campo = Normalizer.normalize(temp[i][5],
						Normalizer.Form.NFD).replaceAll("[^\\p{ASCII}]", "")
						.toUpperCase().trim();
				// seta o nome do respons�vel
				est.setResponsavel(campo);
				// verifica problemas de formata��o referente ao nome do curso
				if (temp[i][6] == null || temp[i][6].equals("null") 
						|| temp[i][6].trim().isEmpty()) {
					throw new PlanilhaException("Nome do Curso (nulo) "
							+ "inv�lido na linha " + i + "!");
				}
				campo = Normalizer.normalize(temp[i][6],Normalizer.Form.NFD)
						.replaceAll("[^\\p{ASCII}]", "")
						.toUpperCase().trim();
				// seta o nome do curso
				est.setCurso(campo);
				// verifica problemas de formata��o referente ao n�vel do curso
				if (temp[i][7] == null || temp[i][7].equals("null") 
						|| temp[i][7].trim().isEmpty()) {
					throw new PlanilhaException("N�vel do Curso (nulo) "
							+ "inv�lido na linha " + i + "!");
				}
				campo = Normalizer.normalize(temp[i][7], Normalizer.Form.NFD)
						.replaceAll("[^\\p{ASCII}]", "")
						.toUpperCase().trim();
				// seta o n�vel do curso
				est.setNivel(campo);
				est.setInstituicao(inst);
				// seta serie/ grau ou per�odo pra verificar se � n�mero
				erro = temp[i][8];
				est.setPeriodo(Integer.parseInt(temp[i][8]));
				// salva o estudante implementado
				listest.add(est);
				// seta declara��o do estudante implementado
				decl.setEstudante(est);
				decl.setAutenticacao(chave);
				decl.setCurso(temp[i][6]);
				decl.setDataAulaInicio(fmt.parse(temp[i][10]));
				decl.setDataAulaFim(fmt.parse(temp[i][11]));
				decl.setInstituicao(inst);
				listdecl.add(decl);
			}
			decdao.add(listdecl);
			getDeclaracoes().addAll(listdecl);
		} catch (NumberFormatException e) {
			erroProcessamento = true;
			FacesContext facesContext = FacesContext.getCurrentInstance();
			facesContext.addMessage(null, 
					new FacesMessage(FacesMessage.SEVERITY_ERROR,
							"Erro de Convers�o do Arquivo!",
					"O valor " + erro + " n�o � um n�mero v�lido!"));
		} catch (EntityNotFoundException e) {
			erroProcessamento = true;
		} catch (PlanilhaException e) {
			erroProcessamento = true;
			FacesContext facesContext = FacesContext.getCurrentInstance();
			facesContext.addMessage(null,
					new FacesMessage(FacesMessage.SEVERITY_ERROR,
						"Erro de Convers�o do Arquivo!", e.getMessage()));
		} catch (ParseException e) {
			erroProcessamento = true;
			FacesContext facesContext = FacesContext.getCurrentInstance();
			facesContext.addMessage(null, new FacesMessage(
					FacesMessage.SEVERITY_ERROR, "Erro de Convers�o "
							+ "do Arquivo!",
					"O valor " + erro + " n�o � uma data v�lida!"));
		} catch (InsertException e) {
			erroProcessamento = true;
			FacesContext facesContext = FacesContext.getCurrentInstance();
			facesContext.addMessage(null, new FacesMessage(
					FacesMessage.SEVERITY_ERROR, "Erro de Banco de Dados!",
					"Erro de Inser��o no Banco de Dados! "
					+ "Tente novamente em alguns instantes"));
			e.printStackTrace();
		}
		return null;
	}

	// getteres and setteres

	public StreamedContent getFileout() {
		return fileout;
	}

	public void setFile(StreamedContent fileout) {
		this.fileout = fileout;
	}

	public String getIcone() {
		return icone;
	}

	public void setIcone(String icone) {
		this.icone = icone;
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

	public List<Curso> getCursos() {
		return cursos;
	}

	public void setCursos(List<Curso> cursos) {
		this.cursos = cursos;
	}

	public Set<Declaracao> getDeclaracoes() {
		return declaracoes;
	}

	public void setDeclaracoes(Set<Declaracao> declaracoes) {
		this.declaracoes = declaracoes;
	}

	public Representante getRepresentante() {
		return representante;
	}

	public void setRepresentante(Representante representante) {
		this.representante = representante;
	}

	
}