package net.aicoder.tcom.poi.excel.writer;

import java.util.Calendar;
import java.util.Date;

import org.apache.poi.ss.usermodel.Cell;
//import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;

import net.aicoder.tcom.poi.util.CellType;

public class DataWriter extends AbstractWriter{
	public DataWriter(){
		super();
	}
	
	public DataWriter(Workbook workbook, Sheet sheet, Row row, Cell cell){
		super(workbook, sheet, row,cell);
	}
	
	@Override
	public void writeValue(Object value) {
		if(value == null){
			cell.setCellType(CellType.BLANK.getCode());
			return;
		}
		
		IWriter writer = getDataWriterByValue(value);
		writer.setWorkbook(workbook);
		writer.setSheet(sheet);
		writer.setRow(row);
		writer.setCell(cell);
		writer.setFormat(format);

		writer.writeValue(value);
	}

	public IWriter getDataWriterByValue(Object value) {
		if (value instanceof Number)
			return new NumberWriter();
		else if (value instanceof String || value instanceof StringBuffer)
			return new StringWriter();
		else if (value instanceof Date || value instanceof Calendar)
			return new DateWriter();
		else if (value instanceof Boolean)
			return new BooleanWriter();
		return new StringWriter();
	}
}
