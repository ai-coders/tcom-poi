package net.aicoder.tcom.poi.excel.writer;

import org.apache.poi.common.usermodel.HyperlinkType;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.ClientAnchor;
import org.apache.poi.ss.usermodel.Comment;
import org.apache.poi.ss.usermodel.CreationHelper;
import org.apache.poi.ss.usermodel.Drawing;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.Hyperlink;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.ss.usermodel.RichTextString;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;

import net.aicoder.tcom.tools.util.AiStringUtil;

public abstract class AbstractWriter implements IWriter {
	private static final String FORMULA_PREFIX = "=";
	private static final String FORMULA_HYPERLINK = "HYPERLINK";

	protected Workbook workbook;
	protected Sheet sheet;
	protected Row row;
	protected Cell cell;
	protected String format;

	public AbstractWriter() {
		super();
	}
	
	public AbstractWriter(Workbook workbook, Sheet sheet, Row row, Cell cell) {
		super();
		this.setWorkbook(workbook);
		this.setSheet(sheet);
		this.setRow(row);
		this.setCell(cell);
	}
	
	@Override
	public void writeValue(Object value, String format) {
		setFormat(format);

		writeValue(value);
	}

	@Override
	public void writeFormula(String formulaStr) {
		if (AiStringUtil.isEmpty(formulaStr)) {
			return;
		}
		
        //CellType srcCellType = srcCell.getCellTypeEnum();
		try {
			String tempStr = formulaStr;
			if (isRealForumla(tempStr)) {
				cell.setCellType(CellType.FORMULA);
				cell.setCellFormula(tempStr.substring(1));
				if (isHyperLinkFormula(tempStr)) {
					setLinkStyle(cell);
				}
			} else {
				cell.setCellType(CellType.STRING);
				cell.setCellValue(tempStr);
			}
		} catch (Throwable tx) {
			cell.setCellType(CellType.STRING);
			cell.setCellValue(formulaStr);
		}
	}

	private boolean isRealForumla(String str) {
		return str != null && str.startsWith(FORMULA_PREFIX);
	}

	private boolean isHyperLinkFormula(String str) {
		boolean isHperLink = false;
		int hperLinkFlagLen = FORMULA_HYPERLINK.length();
		if (str != null && str.length() > hperLinkFlagLen) {
			if (FORMULA_HYPERLINK.equalsIgnoreCase(str.substring(0,
					hperLinkFlagLen))) {
				isHperLink = true;
			}
		}
		return isHperLink;
	}

	@SuppressWarnings("rawtypes")
	@Override
	public void writeComment(String commentStr) {
		if (AiStringUtil.isEmpty(commentStr)) {
			return;
		}

		CreationHelper factory = workbook.getCreationHelper();
		Drawing drawing = sheet.createDrawingPatriarch();
		ClientAnchor anchor = factory.createClientAnchor();
		Comment comment = drawing.createCellComment(anchor);
		RichTextString str = factory.createRichTextString(commentStr);
		comment.setString(str);
		//comment.setAuthor("Ai Coder");
		cell.setCellComment(comment);
	}

	@Override
	public void writeHyperlink(HyperlinkType linkType, String linkAddress) {
		//CreationHelper createHelper = workbook.getCreationHelper();
		CreationHelper createHelper = workbook.getCreationHelper();
		Hyperlink link = createHelper.createHyperlink(linkType);
		link.setAddress(linkAddress);
		cell.setHyperlink(link);
		setLinkStyle(cell);
	}
	
	private void setLinkStyle(Cell cell){
		CellStyle linkStyle = workbook.createCellStyle();
		Font linkFont = workbook.createFont();
		linkFont.setUnderline(Font.U_SINGLE);
		linkFont.setColor(IndexedColors.BLUE.getIndex());
		linkStyle.setFont(linkFont);
		cell.setCellStyle(linkStyle);
	}

	@Override
	public Workbook getWorkbook() {
		return workbook;
	}

	@Override
	public void setWorkbook(Workbook workbook) {
		this.workbook = workbook;
	}

	@Override
	public Sheet getSheet() {
		return sheet;
	}

	@Override
	public void setSheet(Sheet sheet) {
		this.sheet = sheet;
	}

	@Override
	public Row getRow() {
		return row;
	}

	@Override
	public void setRow(Row row) {
		this.row = row;
	}

	@Override
	public Cell getCell() {
		return cell;
	}

	@Override
	public void setCell(Cell cell) {
		this.cell = cell;
	}

	@Override
	public String getFormat() {
		return format;
	}

	@Override
	public void setFormat(String format) {
		this.format = format;
	}
}
