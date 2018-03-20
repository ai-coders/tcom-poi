package net.aicoder.tcom.poi.excel.writer;

import org.apache.poi.ss.usermodel.Cell;

public class StringWriter extends AbstractWriter {
	@Override
	public void writeValue(Object value) {
        cell.setCellType(Cell.CELL_TYPE_STRING);
        cell.setCellValue(value.toString());
	}
}
