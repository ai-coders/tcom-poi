package net.aicoder.tcom.poi.config;

import java.util.Map;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlTransient;
//import javax.xml.bind.annotation.XmlTransient;
import javax.xml.bind.annotation.XmlType;
import javax.xml.bind.annotation.XmlValue;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import net.aicoder.tcom.tools.util.AiStringUtil;

@XmlAccessorType(XmlAccessType.PROPERTY)
// @XmlAccessorType(XmlAccessType.FIELD)
// @XmlType(propOrder = {"pos","configStr"})
@XmlType(propOrder = {})
public class CellDefine {
	private static final Log log = LogFactory.getLog(CellDefine.class);

	private String id;
	private String pos;
	private String notNull;
	private String configStr;
	
	private VariableAppoint propertyVar;

	private RangePosition rangePosition;
	private AreaOriginPosition areaOriginPosition; //开始坐标，定义所对应Sheet的绝对位置，子元素所引用的相对位置的参考原点	

	private int relativeRow = 0;
	private int relativeCol = 0;

	//Constructor
	public CellDefine(){
		super();
	}

	// reset Define
	/**
	 * 重置单元格的定义，主要处理内容：
	 * 1. 依据单元格的变量作为属性设置到变量Map之中
	 * @param variablesMap
	 * @param areaDefaultObjDefine
	 */
	public void resetDefine(Map<String, VariableDefine> variablesMap,
			VariableDefine areaDefaultObjDefine, AreaOriginPosition areaOriginPosition) {
		VariableAppoint varAppoint = null;
		String configStr = this.configStr;

		if (!AiStringUtil.isEmpty(configStr)) {
			VariableDefine defaultObjDefine = areaDefaultObjDefine;
			boolean isProperty = true;
			
			varAppoint = new VariableAppoint(variablesMap);
			varAppoint.parserVariable(configStr,defaultObjDefine, isProperty);
			log.debug("CellPos=" + this.getPos() + "; CellVar=" + varAppoint.dumpStr());
		}
		
		this.propertyVar = varAppoint;
		
		this.areaOriginPosition = areaOriginPosition;
		resetRelativeNo();
	}
	
	private void resetRelativeNo(){
		if(this.areaOriginPosition != null){
			Position originPosition = this.areaOriginPosition.getDataPositionDefine();
			this.relativeRow = this.getRangePosition().getBegRow() - originPosition.getRow();
			this.relativeCol = this.getRangePosition().getBegCol() - originPosition.getColumn();
		}
	}

	// getter/setter
	public String getId() {
		return id;
	}

	@XmlAttribute
	public void setId(String id) {
		this.id = id;
	}

	public String getPos() {
		return pos;
	}

	@XmlAttribute
	public void setPos(String pos) {
		this.pos = pos;
		this.rangePosition = new RangePosition(pos);
	}

	public String getNotNull() {
		return notNull;
	}

	@XmlAttribute
	public void setNotNull(String notNull) {
		if(notNull != null){
			this.notNull = notNull.toUpperCase();
		}
	}

	public String getConfigStr() {
		return configStr;
	}

	@XmlValue
	public void setConfigStr(String configStr) {
		this.configStr = configStr;
	}

	public RangePosition getRangePosition() {
		return rangePosition;
	}

	public VariableAppoint getPropertyVar() {
		return propertyVar;
	}

	public AreaOriginPosition getAreaOriginPosition() {
		return areaOriginPosition;
	}

	@XmlTransient
	public void setAreaOriginPosition(AreaOriginPosition areaOriginPosition) {
		this.areaOriginPosition = areaOriginPosition;
	}
	
	public int getRelativeRow() {
		return relativeRow;
	}

	public int getRelativeCol() {
		return relativeCol;
	}
}