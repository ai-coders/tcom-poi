package net.aicoder.tcom.poi.config;

import net.aicoder.tcom.tools.util.AiStringUtil;

/**
 * <p>
 * Title:一个单元格式的位置
 * </p>
 * <p>
 * Description:
 * 表示Excel种一个单元格式的位置,行用数字表示,从1开始, 列可用字符或数字开始,从'A'或1开始
 * </p>
 * <p>
 * Copyright: Copyright (c) 2005
 * </p>
 * <p>
 * Company:JEAP
 * </p>
 * 
 * @author Rong.Xiao
 * @version 1.0
 */
public class Position {
	private String positionStr = "";
	private String columnStr = "";
	private int rowNo = 0;
	private int columnNo = 0;

	/**
	 * 
	 */
	public Position(){
		super();
	}
	
	public Position(Position cellPosition){
		super();
		resetPosition(cellPosition);
	}

	/**
	 * @param rowNo
	 *            int:位置所在的行数
	 * @param columnNo
	 *            int:位置所在的列数
	 */
	public Position(int rowNo, int columnNo) {
		setRow(rowNo);
		setColumn(columnNo);
	}

	/**
	 * 用字符'A'-'Z'或'a'-'z'表示列号
	 * 
	 * @param rowNo int
	 * @param colStr String
	 */
	public Position(int rowNo, String colStr) {
		setRow(rowNo);
		setColumnStr(colStr);
		int colNo;
		colNo = colStr2ColNo(colStr);
		setColumn(colNo);
	}

	/**
	 * 用Excel表示行和列的方式,如"A1"
	 * 
	 * @param positionStr String
	 */
	public Position(String positionStr) {
		setPositionStr(positionStr);
	}
	
	public void resetPosition(Position position) {
		this.positionStr = position.getPositionStr();
		this.columnStr = position.getColumnStr();
		this.rowNo = position.getRow();
		this.columnNo = position.getColumn();
	}

	// getter/setter
	public String getPositionStr() {
		return positionStr;
	}

	public void setPositionStr(String positionStr) {
		this.positionStr = positionStr;
		
		if (AiStringUtil.isEmpty(positionStr)) {
			setRow(0);
			setColumn(0);
			return;
		}
		
		String str;
		String rowStr;
		String colStr;
		int colNo;

		str = positionStr.trim().toUpperCase();
		rowStr = AiStringUtil.getNumbers(str);
		colStr = AiStringUtil.splitNotNumber(str);
		colNo = colStr2ColNo(colStr);
		
		if (!AiStringUtil.isEmpty(rowStr)) {
			setRow(Integer.parseInt(rowStr));
		}
		if (!AiStringUtil.isEmpty(colStr)) {
			setColumnStr(colStr);
			setColumn(colNo);
		}
	}
	
	public String toString(){
		String posStr = "";
		posStr = this.columnStr + this.rowNo + "[" + this.rowNo + "," + this.columnNo + "]";
		return posStr;
	}

	public int getColumn() {
		return columnNo;
	}

	public int getRow() {
		return rowNo;
	}

	public void setColumn(int columnNo) {
	/*	
		if (columnNo <= 0)
			throw new IllegalArgumentException("columnNo [" + columnNo
					+ "] must be lager than 0!");
	*/
		this.columnNo = columnNo;
	}

	public void setRow(int rowNo) {
	/*	
		if (rowNo <= 0)
			throw new IllegalArgumentException("rowNo [" + rowNo
					+ "] must be lager than 0!");
	*/
		this.rowNo = rowNo;
	}
	
	public String getColumnStr() {
		return columnStr;
	}

	public void setColumnStr(String columnStr) {
		this.columnStr = columnStr;
	}

	public static int colStr2ColNo(String colStr) {
		int colNo = 0;
		if (AiStringUtil.isEmpty(colStr)) {
			colNo = 0;
		} else {
			char[] chs = colStr.trim().toUpperCase().toCharArray();
			int len = chs.length;
			for (int idx = 0; idx < len; idx++) {
				char ch = chs[len - idx - 1];
				colNo += (ch - 'A' + 1) * (int) Math.pow(26, idx);
			}
		}
		return colNo;
	}

	public static String colNo2ColStr(int colNo) {
		if (colNo <= 0) {
			return "";
		}

		StringBuffer colSB = new StringBuffer();

		int mod = 0; // 余数
		// int level = 0; //进位
		int num = colNo; // 模

		do {
			mod = num % 26;
			if (mod == 0) {
				mod = 26;
				num = num / 26 - 1;
			} else {
				num = num / 26;
			}
			char ch = (char) ('A' + mod - 1);
			colSB.insert(0, ch);
		} while (num > 0);

		return colSB.toString();
	}
}