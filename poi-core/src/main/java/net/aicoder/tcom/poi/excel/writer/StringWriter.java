package net.aicoder.tcom.poi.excel.writer;

import net.aicoder.tcom.poi.util.CellType;

//import org.apache.poi.ss.usermodel.Cell;
//import org.apache.poi.ss.usermodel.CellType;

public class StringWriter extends AbstractWriter {
	@Override
	public void writeValue(Object value) {
        cell.setCellType(CellType.STRING.getCode());
        cell.setCellValue(value.toString());
	}
}
