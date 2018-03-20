package net.aicoder.tcom.poi.config;

import java.util.ArrayList;
import java.util.HashMap;
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

/**
 * Sheet定义，依据xml文件的设置获取配置信息
 * @author StoneShi
 *
 */
@XmlAccessorType(XmlAccessType.PROPERTY)
//@XmlAccessorType(XmlAccessType.FIELD)  
//@XmlRootElement(name = "Sheet")  
@XmlType(propOrder = {})
public class SheetDefine {
	private static final Log log = LogFactory.getLog(SheetDefine.class);

	private String id; //定义ID
	private String variable; //该Sheet对应Default的变量
	private String tplSheet; //模板Sheet名称
	private String sheetName; //输出Sheet名称,可支持变量的方式动态生成
	private String repeatBy; //如果是输出多页时，依据什么来生成多页的配置，从SheetName变量中拆分而出，即SheetName中冒号之前的变量

	private List<AreaDefine> areaDefineList; //区域定义
	private VariableAppoint repeatByVar; //由以上repeatBy字串解析而来来变量定义
	private VariableAppoint sheetNameVar; //SheetName变量定义
	private VariableAppoint sheetVarAppoint; //Sheet数据的变量定义

	private SheetConfig sheetConfig;
	private Map<String, AreaDefine> areaDefineMap = new HashMap<String, AreaDefine>();

	public AreaDefine getAreaDefine(String defineId) {
		AreaDefine areaDefine = areaDefineMap.get(defineId);
		return areaDefine;
	}

	// getter/setter
	public String getId() {
		return id;
	}

	@XmlAttribute
	public void setId(String id) {
		this.id = id;
	}

	public String getVariable() {
		return variable;
	}

	@XmlAttribute
	public void setVariable(String variable) {
		this.variable = variable;
	}

	public String getTplSheet() {
		return tplSheet;
	}

	@XmlAttribute
	public void setTplSheet(String tplSheet) {
		this.tplSheet = tplSheet;
	}

	public String getRepeatBy() {
		return repeatBy;
	}

	@XmlAttribute
	public void setRepeatBy(String repeatBy) {
		this.repeatBy = repeatBy;
	}
	
	public String getSheetName() {
		return sheetName;
	}

	@XmlAttribute
	public void setSheetName(String sheetName) {
		this.sheetName = sheetName;
	}

	public List<AreaDefine> getAreaDefineList() {
		return areaDefineList;
	}

	//@XmlElementWrapper(name = "Areas") 
	@XmlElement(name = "Area")
	public void setAreaDefineList(List<AreaDefine> areaDefineList) {
		this.areaDefineList = areaDefineList;
	}
	
	public SheetConfig getSheetConfig() {
		return sheetConfig;
	}

	@XmlTransient
	public void setSheetConfig(SheetConfig sheetConfig) {
		this.sheetConfig = sheetConfig;
	}


	public VariableAppoint getRepeatByVar() {
		return repeatByVar;
	}

	public VariableAppoint getSheetVar() {
		return sheetVarAppoint;
	}

	public VariableAppoint getSheetNameVar() {
		return sheetNameVar;
	}

	/**
	 * 重置Sheet的定义
	 * 1. 设置Sheet的变量：SheetName对应的变量、设置多Sheet输出所依据的List变量、设置Sheet数据输出所对应的变量
	 * 2. 重新排序区域定义，按照区域定义的起始单元格来排序
	 * 3. 重新设置区域定义
	 * @param variablesMap
	 */
	public void resetDefine(Map<String, VariableDefine> variablesMap) {
		parserSheetNameVarDefine(variablesMap);
 		parserRepeatByVarDefine(variablesMap);
		parserSheetVarDefine(variablesMap);

		sortAreaDefineList();

		for (AreaDefine area : this.areaDefineList) {
			log.debug("AreaDefine=" + area.getId());
			areaDefineMap.put(area.getId(), area);
			area.resetDefine(variablesMap);
		}
	}

	/**
	 * 只考虑了行的排序，未考虑列模式的排序
	 */
	private void sortAreaDefineList() {
		if (this.areaDefineList == null) {
			return;
		}

		List<AreaDefine> areaDefineListNew = new ArrayList<AreaDefine>();

		for (int idx = 0; idx < this.areaDefineList.size(); idx++) {
			AreaDefine currArea = this.areaDefineList.get(idx);
			RangePosition currPosition = currArea.getRangePosition();
			if (idx == 0) {
				areaDefineListNew.add(idx, currArea);
			} else {
				for (int serIdx = idx - 1; serIdx >= 0; serIdx--) {
					RangePosition serPosition = areaDefineListNew.get(serIdx)
							.getRangePosition();
					if (currPosition.getBegRow() >= serPosition.getBegRow()) {
						areaDefineListNew.add(serIdx + 1, currArea);
						break;
					}
				}
			}
		}

		this.areaDefineList = areaDefineListNew;
	}

	private void parserSheetVarDefine(Map<String, VariableDefine> variablesMap) {
		VariableAppoint varAppoint = null;
		String configStr = this.variable;

		if (!AiStringUtil.isEmpty(configStr)) {
			varAppoint = new VariableAppoint(variablesMap);
			varAppoint.parserVariable(configStr);
			log.debug("AreaDefaultVar=" + varAppoint.dumpStr());
		}

		this.sheetVarAppoint = varAppoint;
	}
	
	private void parserSheetNameVarDefine(
			Map<String, VariableDefine> variablesMap) {
		VariableAppoint varAppoint = null;
		String configStr = this.sheetName;

		if (!AiStringUtil.isEmpty(configStr)) {
			VariableDefine defaultObjDefine = null;

			boolean isProperty = true;
			varAppoint = new VariableAppoint(variablesMap);
			varAppoint.parserVariable(configStr, defaultObjDefine, isProperty);
			log.debug("SheetName=" + varAppoint.dumpStr());
		}

		this.sheetNameVar = varAppoint;
	}
	
	private void parserRepeatByVarDefine(
			Map<String, VariableDefine> variablesMap) {
		VariableAppoint varAppoint = null;
		String tmpRepeatBy = "";
		if (AiStringUtil.isEmpty(this.repeatBy)){
			String [] aStr = this.sheetName.split(":");
			if (aStr.length > 1 && aStr[0].length() >2){
				String bStr = aStr[0].substring(0,2);
				if ("$[".equals(bStr)) {
					tmpRepeatBy = aStr[0] + "]";
				}else{
					tmpRepeatBy = aStr[0];
				}
			}
		}else{
			tmpRepeatBy = this.repeatBy;
		}
		String configStr = tmpRepeatBy;

		if (!AiStringUtil.isEmpty(configStr)) {
			varAppoint = new VariableAppoint(variablesMap);
			varAppoint.parserVariable(configStr);
			log.debug("RepeatBy=" + varAppoint.dumpStr());
		}
		this.repeatBy = configStr;
		this.repeatByVar = varAppoint;
	}

}
