package br.gov.df.dftrans.scie.utils;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Iterator;

import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.DateUtil;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.extractor.XSSFExcelExtractor;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.jopendocument.dom.spreadsheet.Sheet;
import org.jopendocument.dom.spreadsheet.SpreadSheet;

public class ManipuladorArquivos {

	public static final int ODS = 0, XLS = 1, XLSX = 2;
	public static final String[] ARQ_NAME = { "ods", "xls", "xlsx" };

	/**
	 * Ler um arquivo de no maximo 8 linhas
	 * 
	 * @param path
	 * @return Array de Strings com os dados do arquivo lido ou null caso não
	 *         consiga ler
	 */
	public static String[] leitor(String path) {
		try {
			BufferedReader buffRead = new BufferedReader(new FileReader(path));
			String linha = "";
			String[] resultado = new String[7];
			linha = buffRead.readLine();
			for (int i = 0; i < 7; i++) {
				if (linha != null) {
					resultado[i] = linha;
				} else
					break;
				linha = buffRead.readLine();
			}
			buffRead.close();
			return resultado;
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * Ler um arquivo completo
	 * 
	 * @param path
	 * @return ArrayList<String> com as linhas do arquivo lido ou null se não
	 *         conseguir ler
	 */
	public static ArrayList<String> leitorCompleto(String path) {
		try {
			BufferedReader buffRead = new BufferedReader(new InputStreamReader(new FileInputStream(path), "UTF-8"));
			String linha = "";
			ArrayList<String> resultado = new ArrayList<String>();
			linha = buffRead.readLine();
			while (linha != null) {
				resultado.add(linha);
				linha = buffRead.readLine();
			}
			buffRead.close();
			return resultado;
		} catch (IOException e) {
			System.out.println("Erro de leitura do arquivo\n");
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * Escreve um ArrayList<string> em um arquivo
	 * 
	 * @param path
	 * @param conteudo
	 */
	public static void escritor(String path, ArrayList<String> conteudo) {
		try {
			BufferedWriter buffWrite = new BufferedWriter(new FileWriter(path));
			for (String linha : conteudo) {
				buffWrite.append(linha + "\r\n");
			}
			buffWrite.close();
		} catch (IOException e) {
			System.out.println("Erro de gravação do arquivo\n");
			e.printStackTrace();
		}
	}

	/**
	 * Ler uma planilha excel
	 * 
	 * @param path
	 * @return uma matriz de String com cada célula da planilha
	 */
	@SuppressWarnings("resource")
	public static String[][] lerPlanilha(String path) {
		int tipo = 0;
		File f = new File(path);
		String string = "";
		DateFormat fmt = new SimpleDateFormat("dd/MM/yyyy");
		int rowNum = 2000, columnNum = 12;
		// verifica se a extenssão do arquivo e .xls
		if (f.getName().toLowerCase().endsWith(".xls")) {
			tipo = ManipuladorArquivos.XLS;
			// API de leitura de arquivo XLS(conjunto de planilha = workbook)
			HSSFWorkbook workbook = null;
			try {
				// cria uma abstração do arquivo
				workbook = new HSSFWorkbook(new FileInputStream(f));
			} catch (IOException e) {
				e.printStackTrace();
			}
			// pega a planilha 0
			HSSFSheet sheet = workbook.getSheetAt(0);
			Iterator<Row> rowIterator = sheet.iterator();
			rowNum = 0;
			columnNum = 0;
			boolean b = true;
			// itera até o fim da planilha contando o númelo de linhas
			while (rowIterator.hasNext()) {
				rowNum++;

				Row row = rowIterator.next();
				Iterator<Cell> cellIterator = row.cellIterator();
				// itera sobre todas as células do excel da linha
				while (cellIterator.hasNext()) {
					if (b) {
						// conta o número de colunas (células)
						columnNum++;
					}
					// atribui a célula
					Cell cell = cellIterator.next();
					// trata o conteúdo da célula
					switch (cell.getCellType()) {
					case Cell.CELL_TYPE_BOOLEAN:
						string += cell.getBooleanCellValue() + "\t";
						break;
					case Cell.CELL_TYPE_NUMERIC:
						// verifica se a celula é uma data
						if (DateUtil.isCellDateFormatted(cell)) {
							// formata a data no padrão "dd/MM/yyyy"
							string += fmt.format(cell.getDateCellValue()) + "\t";
						} else {
							// concatena o conteúdo da célula
							string += (int) cell.getNumericCellValue() + "\t";
						}
						break;
					case Cell.CELL_TYPE_STRING:
						// concatena o conteúdo da célula
						string += cell.getStringCellValue() + "\t";
						break;
					}
				}
				b = false;
				string += "\r\n";
			}
			// Verifica se o arquivo e um .ods
		} else if (f.getName().toLowerCase().endsWith(".ods")) {
			tipo = ManipuladorArquivos.ODS;
			// API PARA ODS
			Sheet sheet = null;
			try {
				// pega a planilha 0
				sheet = SpreadSheet.createFromFile(f).getSheet(0);
			} catch (IOException e) {
				e.printStackTrace();
			}
			rowNum = sheet.getRowCount();
			columnNum = sheet.getColumnCount();
			// itera sobre as linhas do planilha
			for (int i = 0; i < sheet.getRowCount(); i++) {
				// itera sobre as colunas da planilha
				for (int j = 0; j < sheet.getColumnCount(); j++) {
					// concatena o conteúdo da coluna na linha iterada
					string += sheet.getCellAt(j, i).getTextValue() + "\t";
				}
				string += "\r\n";
			}
			// Verifica se o arquivo e um .xlsx
		} else if (f.getName().toLowerCase().endsWith(".xlsx")) {
			tipo = ManipuladorArquivos.XLSX;
			try {
				// seta a string com o texto da planilha
				string = new XSSFExcelExtractor(new XSSFWorkbook(new FileInputStream(f))).getText().replaceAll("\n",
						"\r\n");
				// ignora o nome da planilha
				string = string.replace("DeclaracaoAlunos\r\n", "");
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		// cria a matriz com a quantidade de linhas e colunas contadas a cima
		String[][] retorno = new String[rowNum][columnNum];
		// separa as linhas
		String[] aux = string.split("\r\n");
		int i = 0, j = 0;
		// itera as linhas
		for (String temp : aux) {
			// itera sobre colunas
			String[] temp2 = temp.split("\t");
			// itera sobre células
			for (String temp3 : temp2) {
				// enquanto tiver dados na matriz
				if (i < retorno.length && j < retorno[0].length) {
					// seta matriz com a célula iterada
					retorno[i][j] = temp3;
				}
				j++;
			}
			i++;
			j = 0;
		}
		// for(int k = 0; k < retorno.length; k++){
		// for (int l = 0; l < retorno[0].length; l++){
		// if(retorno[k][l] != null){
		// System.out.println("array["+k+"]["+l+"] = "+retorno[k][l]);
		// }
		// }
		// }
		return retorno;
	}

	public static void main(String[] args) {
		lerPlanilha("C:\\Users\\2695936\\Desktop\\frequencia.xls");
	}
}