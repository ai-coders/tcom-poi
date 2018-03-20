package net.aicoder.tcom.poi.config;

import net.aicoder.tcom.poi.PoiConstant;
import net.aicoder.tcom.tools.util.AiStringUtil;

/**
 * 区域位置，开始单元格、结束单元格
 * @author StoneShi
 *
 */
public class RangePosition {
	private String rangeStr; //位置定义字串，如：B2:V10
	private String rangeType; //NULL,ROW,COL,RANGE
	
	private Position beginCell; //开始单元格
	private Position endCell; //结束单元格
	
	public RangePosition(){
		super();
	}
	
	public RangePosition(String beginCellStr, String endCellStr){
		super();
		this.setBeginCell(beginCellStr);
		this.setEndCell(endCellStr);
	}
	
	public RangePosition(RangePosition rangePosition){
		super();
		resetPosition(rangePosition);
	}
	
	public RangePosition(String rangeStr){
		setRangeStr(rangeStr);
		parserRangeStr();
	}
	
	public void resetPosition(RangePosition position){
		this.rangeStr = position.getRangeStr();
		this.rangeType = position.getRangeType();
		this.beginCell = new Position(position.getBeginCell());
		this.endCell  = new Position(position.getEndCell());
	}
	
	public void setRowPosition(int rowNo){
		setRowPosition(rowNo,rowNo);
	}
	
	public void setRowPosition(int rowBegNo,int rowEndNo){
		this.setRangeType(PoiConstant.RANGE_TYPE_ROW);
		this.beginCell = new Position();
		this.beginCell.setRow(rowBegNo);
		this.endCell = new Position();
		this.endCell.setRow(rowEndNo);
	}
	
	public void shiftRows(int rowNum){
		int begRow = this.getBegRow() + rowNum;
		int endRow = this.getEndRow() + rowNum;
		
		this.beginCell.setRow(begRow);
		this.endCell.setRow(endRow);
	}
	
	public void shiftColumns(int colNum){
		int begCol = this.getBegCol() + colNum;
		int endCol = this.getEndCol() + colNum;
		
		this.beginCell.setColumn(begCol);
		this.endCell.setColumn(endCol);
	}
	
	/**
	 * 开始单元格与结束单元格为同一行
	 * @return
	 */
	public boolean isSameRow(){
		if(getBegRow() == getEndRow()){
			return true;
		}else{
			return false;
		}
	}
	
	public int getRowNum(){
		int rowNum = 0;
		int rowBeg = this.getBegRow();
		int rowEnd = this.getEndRow();
		
		if(rowBeg == 0 && rowEnd ==0){
			rowNum = 0;
		}else if(rowBeg == 0 || rowEnd ==0){
			rowNum = 1;
		}else{
			rowNum = rowEnd - rowBeg + 1;
		}
		return rowNum;
	}
	
	public int getColNum(){
		int colNum = 0;
		int colBeg = this.getBegCol();
		int colEnd = this.getEndCol();
		
		if(colBeg == 0 && colEnd ==0){
			colNum = 0;
		}else if(colBeg == 0 || colEnd ==0){
			colNum = 1;
		}else{
			colNum = colEnd - colBeg + 1;
		}
		return colNum;
	}
	
	// getter/setter
	public int getBegRow(){
		return getBeginCell().getRow();
	}
	
	public int getBegCol(){
		return getBeginCell().getColumn();
	}
	
	public int getEndRow(){
		return getEndCell().getRow();
	}
	
	public int getEndCol(){
		return getEndCell().getColumn();
	}
	
	private void parserRangeStr(){
		if(AiStringUtil.isEmpty(this.rangeStr)){
			setRangeType(PoiConstant.RANGE_TYPE_NULL);
			return;
		}
		
		String begStr;
		String endStr;
		int idxSplit = this.rangeStr.indexOf(":");
		if(idxSplit >= 0){
			begStr = this.rangeStr.substring(0, idxSplit);
			endStr = this.rangeStr.substring(idxSplit + 1);
			setBeginCell(begStr);
			setEndCell(endStr);
		}else{
			begStr = this.rangeStr;
			endStr = null;
			setBeginCell(begStr);
			setEndCell(endStr);
		}
	}

	public String getRangeStr() {
		return rangeStr;
	}
	public void setRangeStr(String rangeStr) {
		this.rangeStr = rangeStr;
	}
	public String getRangeType() {
		return rangeType;
	}
	public void setRangeType(String rangeType) {
		this.rangeType = rangeType;
	}
	public Position getBeginCell() {
		return beginCell;
	}
	public void setBeginCell(Position beginCell) {
		this.beginCell = beginCell;
	}
	public void setBeginCell(String beginCellStr){
		this.beginCell = new Position(beginCellStr);
	}
	
	public Position getEndCell() {
		return endCell;
	}
	public void setEndCell(Position endCell) {
		this.endCell = endCell;
	}
	public void setEndCell(String endCellStr){
		this.endCell = new Position(endCellStr);
	}
}
