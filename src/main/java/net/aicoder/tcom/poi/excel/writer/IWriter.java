package net.aicoder.tcom.poi.excel.writer;

import org.apache.poi.common.usermodel.HyperlinkType;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;

public interface IWriter {

	public void writeValue(Object value, String format);

	public void writeValue(Object value);

	public void writeFormula(String formula);

	public void writeComment(String comment);

	public void writeHyperlink(HyperlinkType linkType, String linkAddress);

	public Workbook getWorkbook();

	public void setWorkbook(Workbook workbook);

	public Sheet getSheet();

	public void setSheet(Sheet sheet);

	public Row getRow();

	public void setRow(Row row);

	public Cell getCell();

	public void setCell(Cell cell);

	public String getFormat();

	public void setFormat(String format);
}