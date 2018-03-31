package net.aicoder.tcom.poi.config;

import java.util.List;
import java.util.Map;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlTransient;
import javax.xml.bind.annotation.XmlType;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import net.aicoder.tcom.poi.PoiConstant;
import net.aicoder.tcom.tools.util.AiStringUtil;

@XmlAccessorType(XmlAccessType.PROPERTY)
// @XmlAccessorType(XmlAccessType.FIELD)
@XmlType(propOrder = {})
public class AreaDefine {
	private static final Log log = LogFactory.getLog(BookDefine.class);

	private String id; //配置ID
	private String name;
	private String type;
	private String range;
	
	private String fillModel; //填充类型:FillModel, Row, Column
	private String beginCell; //起始单元格
	private String endCell; //结束单元格
	private String notNull; //非空检查行/列 
	private int titleBegin; //标题起始行/列 
	private int titleNum; //标题行数/列数
	private int tplBegin; //模板开始行/列
	private int tplNum; //模板行数/列数
	private int dataBegin; //数据起始行/列
	private int dataEnd; //数据结束行/列
	private String variable; //变量名
	private String sheetName; //输出Sheet名称

	private int notNullRoCNum = -1; //非空行或列对应的数值，如果行模式填充则为列序号，如果列模式填充则为行序号
	private int shiftRowNum = 0; //需要移动的行数，即为当前数据插入的行数
	private int dataRoCNum = 0; //数据行或列对应的数值，如果行模式填充则与dataRowNum相等，如果列模式填充则与dataColNum相等

	private AreaDefine parentAreaDefine; //父区域定义
	private List<AreaDefine> subAreaDefineList; //子区域定义
	private List<RowDefine> rowDefineList; //行定义
	private List<ColumnDefine> columnDefineList; //列定义

	private RangePosition rangePosition; //区域位置定义
	private VariableAppoint areaVarAppoint; //变量
	
	private AreaOriginPosition areaOriginPosition; //开始坐标，定义所对应Sheet的绝对位置，子元素所引用的相对位置的参考原点	

	public AreaDefine(){
		super();
	}

	/**
	 * 重置区域定义，主要进行以下处理：
	 * 1. 将变量的Map进行设置，如果已设定的变量，则沿用，并在既有变量上增加属性及子属性的定义
	 * 2. 重置区域的起始位置
	 * @param variablesMap
	 */
	public void resetDefine(Map<String, VariableDefine> variablesMap) {
		resetDefine(variablesMap,null,null);
	}
	
	private void resetDefine(Map<String, VariableDefine> variablesMap, VariableDefine parentDdefaultVarDefine,
			AreaOriginPosition parentAreaOriginPosition) {
		parserAreaDefaultVar(variablesMap);

		VariableDefine defaultVarDefine = null;
		if (this.areaVarAppoint != null) {
			defaultVarDefine = this.areaVarAppoint.getVariableDefine();
		}else{
			defaultVarDefine = parentDdefaultVarDefine;
		}

		this.areaOriginPosition= calAreaOriginPosition(this);
		
		if (this.rowDefineList != null) {
			for (int index = 0; index < this.rowDefineList.size(); index++) {
				RowDefine rowDefine = this.rowDefineList.get(index);
				rowDefine.resetDefine(variablesMap, defaultVarDefine,this.areaOriginPosition);
			}
		}
		
		if (this.columnDefineList != null) {
			for (int index = 0; index < this.columnDefineList.size(); index++) {
				ColumnDefine colDefine = this.columnDefineList.get(index);
				colDefine.resetDefine(variablesMap, defaultVarDefine,this.areaOriginPosition);
			}
		}
		
		if (this.subAreaDefineList != null) {
			for (int index = 0; index < this.subAreaDefineList.size(); index++) {
				AreaDefine areaDefine = this.subAreaDefineList.get(index);
				areaDefine.resetDefine(variablesMap, defaultVarDefine,this.areaOriginPosition);
				areaDefine.setParentAreaDefine(this);
			}
		}
		
		this.shiftRowNum = calDIShiftRowNum();
		this.dataRoCNum = calDataRoCNum();
		
		log.debug("areaBegRow=" + this.getRangePosition().getBegRow()
				+ "; areaEndRow=" + this.getRangePosition().getEndRow()
				+ "; dataBegRow=" + (this.getRangePosition().getBegRow()
				+ this.getDataBegin()) + "; dataRowNum="
				+ this.getDataRoCNum() + "; dataEndRow="
				+ (this.getRangePosition().getEndRow() + this.getDataEnd())
				+ "; tplRow=" + this.getTplBegin() + "; tplRowNum="
				+ this.getTplNum());
	}

	private AreaOriginPosition calAreaOriginPosition(AreaDefine areaDefine){
		AreaOriginPosition areaOriginPosition = new AreaOriginPosition();
		Position areaPosition;
		Position dataPosition;
		
		areaPosition = areaDefine.rangePosition.getBeginCell();
		areaOriginPosition.setAreaPositionDefine(areaPosition);
		
		if(areaPosition == null){
			dataPosition = calOriginDataPositionByEelement(areaDefine);
		}else{
			dataPosition = new Position(areaPosition);
			if(PoiConstant.FILL_MODEL_COLUMN.equals(getFillModel().toUpperCase())){
				int columnNo = dataPosition.getColumn() + areaDefine.dataBegin;
				dataPosition.setColumn(columnNo);
			}else{
				int rowNo = dataPosition.getRow() + areaDefine.dataBegin;
				dataPosition.setRow(rowNo);
			}
		}
		
		areaOriginPosition.setDataPositionDefine(dataPosition);
		return areaOriginPosition;
	}

	private Position calOriginDataPositionByEelement(AreaDefine areaDefine){
		int firstRow = 0; 
		int firstCol = 0; 

		if (areaDefine.subAreaDefineList != null && areaDefine.subAreaDefineList.size()>0) {
			AreaDefine elementFirst = areaDefine.subAreaDefineList.get(0);
			AreaOriginPosition subAreaOriginPosition = calAreaOriginPosition(elementFirst);
			Position subAreaDataPosition;
			if(subAreaOriginPosition.getAreaPositionDefine() != null){
				subAreaDataPosition = subAreaOriginPosition.getAreaPositionDefine();
			}else{
				subAreaDataPosition = subAreaOriginPosition.getDataPositionDefine();
			}
			firstRow = subAreaDataPosition.getRow();
			firstCol = subAreaDataPosition.getColumn();
		}
		
		if (areaDefine.rowDefineList != null && areaDefine.rowDefineList.size()>0) {
			RowDefine elementFirst = areaDefine.rowDefineList.get(0);
			List<CellDefine> cellDefineList = elementFirst.getCellDefineList();
			if(cellDefineList != null && cellDefineList.size() > 0){
				CellDefine cellFirst = elementFirst.getCellDefineList().get(0);
				int elementFirstRow = cellFirst.getRangePosition().getBegRow();
				int elementFirstCol = cellFirst.getRangePosition().getBegCol();
				if (elementFirstRow > 0) {
					if(elementFirstRow < firstRow || firstRow ==0 ){
						firstRow = elementFirstRow;
					}
				}
				if (elementFirstCol > 0) {
					if(elementFirstCol < firstCol || firstCol ==0 ){
						firstCol = elementFirstCol;
					}
				}
			}
			
		}
		
		if (areaDefine.columnDefineList != null && areaDefine.columnDefineList.size()>0) {
			ColumnDefine elementFirst = areaDefine.columnDefineList.get(0);
			List<CellDefine> cellDefineList = elementFirst.getCellDefineList();
			if(cellDefineList != null && cellDefineList.size() > 0){
				CellDefine cellFirst = elementFirst.getCellDefineList().get(0);
				int elementFirstRow = cellFirst.getRangePosition().getBegRow();
				int elementFirstCol = cellFirst.getRangePosition().getBegCol();
				if (elementFirstRow > 0) {
					if(elementFirstRow < firstRow || firstRow ==0 ){
						firstRow = elementFirstRow;
					}
				}
				if (elementFirstCol > 0) {
					if(elementFirstCol < firstCol || firstCol ==0 ){
						firstCol = elementFirstCol;
					}
				}
			}
		}
		
		Position dataPosition = new Position(firstRow,firstCol);
		return dataPosition;
	}
	
	private int calDIShiftRowNum(){
		AreaDefine areaDefine = this;
		
		int rowNum = 0;
		if(PoiConstant.FILL_MODEL_ROW.equals(getFillModel().toUpperCase())){
			if(areaDefine.getRangePosition().isSameRow()){
				rowNum = 0;
			}else{
				rowNum = calDataRoCNum();
			}
		}
		return rowNum;
	}
	
	private int calDataRoCNum(){
		AreaDefine areaDefine = this;
		
		int rocNum = 0;
		rocNum = areaDefine.getTplNum();

		if(rocNum == 0){
			Position begPosition = this.getAreaOriginPosition().getDataPositionDefine();
			Position endPosition = calEndDataPositionByEelement(this);
			
			if(PoiConstant.FILL_MODEL_COLUMN.equals(getFillModel().toUpperCase())){
				int columnNo = endPosition.getColumn() - begPosition.getColumn() + 1;
				rocNum = columnNo;
			}else{
				int rowNo = endPosition.getRow() - begPosition.getRow() + 1;
				rocNum = rowNo;
			}
		}
		
		return rocNum;
	}
	
	private Position calEndDataPositionByEelement(AreaDefine areaDefine){
		int endRow = 0; 
		int endCol = 0; 

		if (areaDefine.subAreaDefineList != null && areaDefine.subAreaDefineList.size()>0) {
			int idx = areaDefine.subAreaDefineList.size() - 1;
			AreaDefine elementFirst = areaDefine.subAreaDefineList.get(idx);
			AreaOriginPosition subAreaOriginPosition = calAreaOriginEndPosition(elementFirst);
			Position subAreaDataPosition;
			if(subAreaOriginPosition.getAreaPositionDefine() != null){
				subAreaDataPosition = subAreaOriginPosition.getAreaPositionDefine();
			}else{
				subAreaDataPosition = subAreaOriginPosition.getDataPositionDefine();
			}
			endRow = subAreaDataPosition.getRow();
			endCol = subAreaDataPosition.getColumn();
		}
		
		if (areaDefine.rowDefineList != null && areaDefine.rowDefineList.size()>0) {
			int idx = areaDefine.rowDefineList.size() - 1;
			RowDefine elementFirst = areaDefine.rowDefineList.get(idx);
			List<CellDefine> cellDefineList = elementFirst.getCellDefineList();
			if(cellDefineList != null && cellDefineList.size() > 0){
				int cellIdx = cellDefineList.size() - 1;
				CellDefine cellFirst = elementFirst.getCellDefineList().get(cellIdx);
				int elementFirstRow = cellFirst.getRangePosition().getBegRow();
				int elementFirstCol = cellFirst.getRangePosition().getBegCol();
				if (elementFirstRow > 0) {
					if(elementFirstRow > endRow || endRow ==0 ){
						endRow = elementFirstRow;
					}
				}
				if (elementFirstCol > 0) {
					if(elementFirstCol > endCol || endCol ==0 ){
						endCol = elementFirstCol;
					}
				}
			}
			
		}
		
		if (areaDefine.columnDefineList != null && areaDefine.columnDefineList.size()>0) {
			int idx = areaDefine.columnDefineList.size() - 1;
			ColumnDefine elementFirst = areaDefine.columnDefineList.get(idx);
			List<CellDefine> cellDefineList = elementFirst.getCellDefineList();
			if(cellDefineList != null && cellDefineList.size() > 0){
				CellDefine cellFirst = elementFirst.getCellDefineList().get(0);
				int elementFirstRow = cellFirst.getRangePosition().getBegRow();
				int elementFirstCol = cellFirst.getRangePosition().getBegCol();
				if (elementFirstRow > 0) {
					if(elementFirstRow > endRow || endRow ==0 ){
						endRow = elementFirstRow;
					}
				}
				if (elementFirstCol > 0) {
					if(elementFirstCol > endCol || endCol ==0 ){
						endCol = elementFirstCol;
					}
				}
			}
		}
		
		Position dataPosition = new Position(endRow,endCol);
		return dataPosition;
	}

	private AreaOriginPosition calAreaOriginEndPosition(AreaDefine areaDefine){
		AreaOriginPosition areaOriginPosition = new AreaOriginPosition();
		Position areaPosition;
		Position dataPosition;
		
		areaPosition = areaDefine.rangePosition.getEndCell();
		areaOriginPosition.setAreaPositionDefine(areaPosition);
		
		if(areaPosition == null){
			dataPosition = calEndDataPositionByEelement(areaDefine);
		}else{
			dataPosition = new Position(areaPosition);
			if(PoiConstant.FILL_MODEL_COLUMN.equals(getFillModel().toUpperCase())){
				int columnNo = dataPosition.getColumn() - areaDefine.dataEnd;
				dataPosition.setColumn(columnNo);
			}else{
				int rowNo = dataPosition.getRow() - areaDefine.dataEnd;
				dataPosition.setRow(rowNo);
			}
		}
		
		areaOriginPosition.setDataPositionDefine(dataPosition);
		return areaOriginPosition;
	}

	private void parserAreaDefaultVar(Map<String, VariableDefine> variablesMap) {
		VariableAppoint varAppoint = null;
		String configStr = this.variable;

		if (!AiStringUtil.isEmpty(configStr)) {
			varAppoint = new VariableAppoint(variablesMap);
			varAppoint.parserVariable(configStr);
			log.debug("AreaDefaultVar=" + varAppoint.dumpStr());
		}

		this.areaVarAppoint = varAppoint;
	}

	// getter/setter
	public String getId() {
		return id;
	}

	@XmlAttribute
	public void setId(String id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	@XmlAttribute
	public void setName(String name) {
		this.name = name;
	}

	public String getType() {
		return type;
	}

	@XmlAttribute
	public void setType(String type) {
		this.type = type;
	}

	public String getRange() {
		return range;
	}

	@XmlAttribute
	public void setRange(String range) {
		this.range = range;
		if (this.rangePosition == null) {
			this.rangePosition = new RangePosition();
		}
		this.rangePosition.setRangeStr(range);
	}

	public String getFillModel() {
		if(fillModel == null){
			fillModel = "";
		}
		return fillModel;
	}

	@XmlAttribute
	public void setFillModel(String fillModel) {
		this.fillModel = fillModel;
	}

	public String getBeginCell() {
		return beginCell;
	}

	@XmlAttribute
	public void setBeginCell(String beginCell) {
		this.beginCell = beginCell;

		if (this.rangePosition == null) {
			this.rangePosition = new RangePosition();
		}
		this.rangePosition.setBeginCell(beginCell);
	}

	public String getEndCell() {
		return endCell;
	}

	@XmlAttribute
	public void setEndCell(String endCell) {
		this.endCell = endCell;

		if (this.rangePosition == null) {
			this.rangePosition = new RangePosition();
		}
		this.rangePosition.setEndCell(endCell);
	}
	
	public String getNotNull() {
		return notNull;
	}

	@XmlAttribute
	public void setNotNull(String notNull) {
		this.notNull = notNull;
		Position position = new Position(notNull);
		if(PoiConstant.FILL_MODEL_COLUMN.equals(getFillModel().toUpperCase())){
			this.notNullRoCNum = position.getRow();
		}else{
			this.notNullRoCNum = position.getColumn();
		}
	}	

	public int getNotNullRoCNum() {
		return notNullRoCNum;
	}

	public int getTitleBegin() {
		return titleBegin;
	}

	@XmlAttribute
	public void setTitleBegin(int titleRow) {
		this.titleBegin = titleRow;
	}

	public int getTitleNum() {
		return titleNum;
	}

	@XmlAttribute
	public void setTitleNum(int titleNum) {
		this.titleNum = titleNum;
	}

	public int getTplBegin() {
		if (tplBegin == 0) {
			//tplBegin = this.position.getBegRow() + this.dataBegin;
			if(PoiConstant.FILL_MODEL_COLUMN.equals(getFillModel().toUpperCase())){
				tplBegin = this.rangePosition.getBegCol() + this.dataBegin;
			}else{
				tplBegin = this.rangePosition.getBegRow() + this.dataBegin;
			}
		}
		return tplBegin;
	}

	@XmlAttribute
	public void setTplBegin(int tplBegin) {
		this.tplBegin = tplBegin;
	}

	@XmlAttribute
	public void setTplNum(int tplNum) {
		this.tplNum = tplNum;
	}

	public int getTplNum() {
		if (tplNum == 0) {
			tplNum = this.getDataRoCNum();
		}
		return tplNum;
	}

	public int getDataBegin() {
		return dataBegin;
	}

	@XmlAttribute
	public void setDataBegin(int dataBegin) {
		this.dataBegin = dataBegin;
	}

	public int getDataEnd() {
		return dataEnd;
	}

	@XmlAttribute
	public void setDataEnd(int dataEnd) {
		this.dataEnd = dataEnd;
	}

	public String getVariable() {
		return variable;
	}

	@XmlAttribute
	public void setVariable(String variable) {
		this.variable = variable;
	}

	public List<RowDefine> getRowDefineList() {
		return rowDefineList;
	}
	
	public String getSheetName() {
		return sheetName;
	}

	@XmlAttribute
	public void setSheetName(String sheetName) {
		this.sheetName = sheetName;
	}

	// @XmlElementWrapper(name = "Rows")
	@XmlElement(name = "Area")
	public void setSubAreaDefineList(List<AreaDefine> subAreaDefineList) {
		this.subAreaDefineList = subAreaDefineList;
	}

	public List<AreaDefine> getSubAreaDefineList() {
		return subAreaDefineList;
	}

	// @XmlElementWrapper(name = "Rows")
	@XmlElement(name = "Row")
	public void setRowDefineList(List<RowDefine> rowDefineList) {
		this.rowDefineList = rowDefineList;
	}

	public List<ColumnDefine> getColumnDefineList() {
		return columnDefineList;
	}

	// @XmlElementWrapper(name = "Columns")
	@XmlElement(name = "Column")
	public void setColumnDefineList(List<ColumnDefine> columnDefineList) {
		this.columnDefineList = columnDefineList;
	}

	public int getShiftRowNum() {
		return shiftRowNum;
	}
	
	public int getDataRoCNum() {
		return this.dataRoCNum;
	}

	public RangePosition getRangePosition() {
		return rangePosition;
	}

	public VariableAppoint getAreaVarAppoint() {
		return areaVarAppoint;
	}
	
	// Added by stone 20180331
	public void setAreaVarAppoint(VariableAppoint areaVarAppoint) {
		this.areaVarAppoint = areaVarAppoint;
	}
	
	public AreaDefine getParentAreaDefine() {
		return parentAreaDefine;
	}
	
	@XmlTransient
	public void setParentAreaDefine(AreaDefine parentAreaDefine) {
		this.parentAreaDefine = parentAreaDefine;
	}

	public AreaOriginPosition getAreaOriginPosition() {
		return areaOriginPosition;
	}

	@XmlTransient
	public void setAreaOriginPosition(AreaOriginPosition areaOriginPosition) {
		this.areaOriginPosition = areaOriginPosition;
	}
}