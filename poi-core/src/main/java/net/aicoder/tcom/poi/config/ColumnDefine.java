package net.aicoder.tcom.poi.config;

import java.util.List;
import java.util.Map;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlTransient;
//import javax.xml.bind.annotation.XmlTransient;
import javax.xml.bind.annotation.XmlType;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import net.aicoder.tcom.tools.util.AiStringUtil;

@XmlAccessorType(XmlAccessType.PROPERTY)
// @XmlAccessorType(XmlAccessType.FIELD)
@XmlType(propOrder = {})
public class ColumnDefine {
	private static final Log log = LogFactory.getLog(BookDefine.class);

	private String id;
	private String name;
	private String type;
	private String beginCell;
	private String endCell;
	private String variable;
	private List<CellDefine> cellDefineList;
	
	private RangePosition rangePosition;
	private VariableAppoint varAppoint;
	
	private int relativeRow = 0; //相对应的行，与区域定义的起始单元格相比较

	private AreaOriginPosition areaOriginPosition; //开始坐标，定义所对应Sheet的绝对位置，子元素所引用的相对位置的参考原点	
	
	//Constructor
	public ColumnDefine(){
		super();
	}
	
	/**
	 * 重置行及单元格的配置定义，主要处理的内容为：
	 * 1. 将当前配置的变量，设置到变量Map之中，Row应无对应的变量设置
	 * 2. 重置所有单元格的设置，将单元格的变量作为属性设置到变量Map之中
	 * @param variablesMap
	 * @param areaDefaultVarDefine
	 */
	public void resetDefine(Map<String, VariableDefine> variablesMap,
			VariableDefine areaDefaultVarDefine, AreaOriginPosition areaOriginPosition){
		parserVarAppoint(variablesMap);

		VariableDefine defaultVarDefine = null;
		if(this.varAppoint != null){
			defaultVarDefine = this.varAppoint.getVariableDefine();
		}else{
			defaultVarDefine = areaDefaultVarDefine;
		}
		
		this.areaOriginPosition = areaOriginPosition;
		resetCellsDefine(variablesMap,defaultVarDefine,areaOriginPosition);
	}
	
	private void resetCellsDefine(Map<String, VariableDefine> variablesMap,
			VariableDefine defaultVarDefine, AreaOriginPosition areaOriginPosition){
		for(CellDefine cell:this.cellDefineList){
			log.debug("Cell=" + cell.getPos());
			cell.resetDefine(variablesMap, defaultVarDefine,areaOriginPosition);
		}
	}	

	private void parserVarAppoint(Map<String, VariableDefine> variablesMap) {
		VariableAppoint varAppoint = null;
		String configStr = this.variable;

		if (!AiStringUtil.isEmpty(configStr)) {
			varAppoint = new VariableAppoint(variablesMap);
			varAppoint.parserVariable(configStr);
			log.debug("CellAreaDefaultVar=" + varAppoint.dumpStr());
		}
		
		this.varAppoint = varAppoint;
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

	public String getVariable() {
		return variable;
	}

	@XmlAttribute
	public void setVariable(String variable) {
		this.variable = variable;
	}
	
	public List<CellDefine> getCellDefineList() {
		return cellDefineList;
	}

	//@XmlElementWrapper(name = "Cells") 
	@XmlElement(name = "Cell")
	public void setCellDefineList(List<CellDefine> cellDefineList) {
		this.cellDefineList = cellDefineList;
	}

	public RangePosition getRangePosition() {
		return rangePosition;
	}

	public VariableAppoint getVarAppoint() {
		return varAppoint;
	}
	public int getRelativeRow() {
		return relativeRow;
	}

	@XmlTransient
	public void setRelativeRow(int relativeRow) {
		this.relativeRow = relativeRow;
	}

	public AreaOriginPosition getAreaOriginPosition() {
		return areaOriginPosition;
	}

	@XmlTransient
	public void setAreaOriginPosition(AreaOriginPosition areaOriginPosition) {
		this.areaOriginPosition = areaOriginPosition;
	}

}