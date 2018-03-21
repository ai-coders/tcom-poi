package net.aicoder.tcom.poi.excel.writer;

//import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellType;

public class StringWriter extends AbstractWriter {
	@Override
	public void writeValue(Object value) {
        cell.setCellType(CellType.STRING);
        cell.setCellValue(value.toString());
	}
}
