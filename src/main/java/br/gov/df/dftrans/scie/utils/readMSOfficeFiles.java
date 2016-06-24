package br.gov.df.dftrans.scie.utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Iterator;

import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.hwpf.HWPFDocument;
import org.apache.poi.hwpf.extractor.WordExtractor;
import org.apache.poi.poifs.filesystem.POIFSFileSystem;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.extractor.XSSFExcelExtractor;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.apache.poi.xwpf.extractor.XWPFWordExtractor;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.jopendocument.dom.spreadsheet.Sheet;
import org.jopendocument.dom.spreadsheet.SpreadSheet;
import org.jopendocument.model.TextDocument;

public class readMSOfficeFiles {
	public static void main(String[] args) throws IOException, Exception {
		File f = new File("C:\\Users\\2695936\\Desktop\\declaracao.ods");
			if (f.getName().toLowerCase().endsWith(".docx")) {
				System.out.println(new XWPFWordExtractor(
						new XWPFDocument(new FileInputStream(f)))
						.getText());
			} else if(f.getName().toLowerCase().endsWith(".doc")){
				String s = (new WordExtractor(new HWPFDocument(
						new POIFSFileSystem(new FileInputStream(f)))))
						.getText();
				System.out.println(s);
			}else if (f.getName().toLowerCase().endsWith(".xls")) {
				String s = "";
				HSSFWorkbook workbook = new HSSFWorkbook(new FileInputStream(f));
				HSSFSheet sheet = workbook.getSheetAt(0);
				Iterator<Row> rowIterator = sheet.iterator();
				while(rowIterator.hasNext()) {
			        Row row = rowIterator.next();
			        Iterator<Cell> cellIterator = row.cellIterator();
			        while(cellIterator.hasNext()) {
			            Cell cell = cellIterator.next();
			            switch(cell.getCellType()) {
			                case Cell.CELL_TYPE_BOOLEAN:
			                    s += cell.getBooleanCellValue() + "\t";
			                    break;
			                case Cell.CELL_TYPE_NUMERIC:
			                	s += cell.getNumericCellValue() + "\t";
			                    break;
			                case Cell.CELL_TYPE_STRING:
			                	s += cell.getStringCellValue() + "\t";
			                    break;
			            }
			        }
			        s += "\r\n";
			    }
				System.out.println(s);
			} else if (f.getName().toLowerCase().endsWith(".ods")) {
				String s = "";
				Sheet sheet = SpreadSheet.createFromFile(f).getSheet(0);
				for(int i = 0; i < sheet.getRowCount(); i++){
					for(int j = 0; j < sheet.getColumnCount(); j++){
						s += sheet.getCellAt(j, i).getTextValue() + "\t";
					}
					s += "\r\n";
				}
				System.out.println(s);
			} else if (f.getName().toLowerCase().endsWith(".odt")) {
				TextDocument document= new TextDocument();
				document.loadFrom(f);
				System.out.println(document);
			} else if (f.getName().toLowerCase().endsWith(".xlsx")) {
				System.out.println(new XSSFExcelExtractor(
						new XSSFWorkbook(new FileInputStream(f)))
						.getText());
			} else {
				System.out.println("Enter MS Office 2007+ files");
			}
	}
}