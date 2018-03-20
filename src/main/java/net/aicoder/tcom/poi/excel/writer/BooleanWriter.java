package net.aicoder.tcom.poi.excel.writer;

public class BooleanWriter extends AbstractWriter {
	@Override
	public void writeValue(Object value) {
		if (value instanceof Boolean) {
			Boolean b = (Boolean) value;
			cell.setCellValue(b.booleanValue());
		} else {
			throw new IllegalArgumentException("value:[" + value
					+ "] is not boolean object in BooleanWriter");
		}
	}
}
