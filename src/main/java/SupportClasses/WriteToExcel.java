package SupportClasses;

import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

public class WriteToExcel {

  private static String[] columns = { "First Name", "Last Name", "Email","Date Of Birth" };

  public static boolean writeToExcel(String fileName, String sheetName, ArrayList<String[]> dataToWrite, int identifierPosition){
	  Workbook workbook = new XSSFWorkbook();
	  try {
		
		// Find the Sheet within the excel that needs to be updated.
	    Sheet sheet = null;
		for (int i = 0; i <= workbook.getNumberOfSheets(); i++) {
			sheet = workbook.getSheetAt(i);
			if (sheet.getSheetName().contentEquals(sheetName)) {
				break;
			}
		}

	    // Get the header row with the row identifiers.
	    Row headerRow = sheet.getRow(0);
	    
	    // The headers from the data that needs to be updated.
	    String dataHeadersArray[] = dataToWrite.get(0);
	    
	    int findKeyHeaderColumn = findCell(headerRow, dataHeadersArray[identifierPosition]);
	    if (findKeyHeaderColumn == -1) {
	    	throw new Exception("The Header identifier of " + dataHeadersArray[identifierPosition] + " is not present. -writeToExcel");
	    }
	    int keyHeaderColum = findKeyHeaderColumn;

	    // Update the dataToWrite to match the header format of the excel sheet.
	    ArrayList<String[]> dataToWriteHeadersMatching = new ArrayList<String[]>();
	    int relativePositionArray[] = new int[dataHeadersArray.length];
	    for (int pos = 0; pos < dataHeadersArray.length; pos++) {
	    	String dataHeader = dataHeadersArray[pos];
	    	relativePositionArray[pos] = findCell(headerRow, dataHeader);
	    	
///////////////////////////////////////////////NEED TO FINISH FROM HERE
	    }
	    for (int reOrder = 0; reOrder < dataToWrite.size(); reOrder++) {
	    	String newOrderedPositions[] = new String[dataHeadersArray.length];
	    	for (int pos = 0; pos < relativePositionArray.length; pos++) {
		    	String dataHeader = dataHeadersArray[pos];
		    	relativePositionArray[pos] = findCell(headerRow, dataHeader);
		    }
	    }
	    
	    for (int rowPos = 1; rowPos < dataToWrite.size(); rowPos++) {
	    	String dataRow[] = dataToWrite.get(rowPos);
		    int getRowToUpdate = findRowWithCellValue(sheet, dataHeadersArray[identifierPosition], 0);
		    
		    if (getRowToUpdate == -1) {
		    	// value not found to update, need to add a new row with the data.
		    	Row row = sheet.createRow(sheet.getPhysicalNumberOfRows()+ 1);
		    	
		    }
	    }


	    // Write the output to a file
	    FileOutputStream fileOut = new FileOutputStream("contacts.xlsx");
	    workbook.write(fileOut);
	    fileOut.close();
	    return true;
	}catch (Exception e) {
		Helper_Functions.PrintOut("Unable to write to file " + fileName + ". From writeToExcel()");
		return false;
	}finally {
		try {
			workbook.close();
		} catch (IOException e) {}
	}
  }
  
  private static int findCell(Row row, String cellContent) {
      int cellPosition = 0;
      for (Cell cell : row) {
    	  cellPosition++;
    	  //if (cell.getCellType() == Cell.CELL_TYPE_STRING) {
			  if (cell.getStringCellValue().trim().equals(cellContent)) {
				  return cellPosition;
			  }
		  //}
      }
      return -1;
  }
  
  private static int findRowWithCellValue(Sheet sheet, String cellContent, int cellNumber) {
      for (Row row: sheet) {
    	  Cell cell = row.getCell(cellNumber);
    	  //if (cell.getCellType() == Cell.CELL_TYPE_STRING) {
    		  if (cell.getStringCellValue().trim().equals(cellContent)) {
    			  return row.getRowNum();
    		  }
    	 //}
      }
      return -1;
  }

}