package net.aicoder.tcom.poi.config;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import net.aicoder.tcom.tools.util.AiStringUtil;

@XmlAccessorType(XmlAccessType.PROPERTY)  
//@XmlAccessorType(XmlAccessType.FIELD)  
@XmlRootElement(name = "Book")  
@XmlType(propOrder = {})  
public class BookDefine {
	private static final Log log = LogFactory.getLog(BookDefine.class);

	private String id; //配置
	private String name; //配置名称
	private String importSheets; //导入的Sheet名称
	private String notImportSheets; //不导入的Sheet名称

	private List<SheetDefine> sheetDefineList; //Sheet定义列表
	
	private Map<String,SheetDefine> sheetDefineMap; //Sheet定义Map
	private Map<String,VariableDefine> variablesMap; //变量

	public SheetDefine getSheetDefine(String defineId){
		if(sheetDefineMap == null){
			return null;
		}else{
			SheetDefine sheetDefine = sheetDefineMap.get(defineId);
			return sheetDefine;
		}
	}
	
	/**
	 * 检查当前Sheet是否需要导入
	 * @param sheetName
	 * @return
	 */
	public boolean isImportSheet(String sheetName){
		boolean isImpSheet = false;
		if(AiStringUtil.isEmpty(sheetName)){
			return isImpSheet;
		}
		isImpSheet = checkImportSheet(sheetName);
		if(isImpSheet == false){
			return false;
		}
		isImpSheet = checkImportSheetByNot(sheetName);
		if(isImpSheet == false){
			return false;
		}
		boolean isTplSheet = isTemplateSheet(sheetName);
		if(isTplSheet){
			return false;
		}
		return isImpSheet;
	}
	
	private boolean checkImportSheet(String sheetName){
		boolean isImpSheet = false;
		if(AiStringUtil.isEmpty(importSheets)){
			isImpSheet =  true;
		}else{
			String[] strs = importSheets.split(",");
			for(String str:strs){
				isImpSheet = AiStringUtil.checkStrSameAs(sheetName,str);
				if(isImpSheet == true){
					break;
				}
			}
		}
		return isImpSheet;		
	}
	
	private boolean checkImportSheetByNot(String sheetName){ // true is import sheet
		boolean isImpSheet = true;
		if(AiStringUtil.isEmpty(notImportSheets)){
			return true;
		}
		String[] strs = notImportSheets.split(",");
		for(String str:strs){
			boolean isNotImpSheet = AiStringUtil.checkStrSameAs(sheetName,str);
			if(isNotImpSheet == true){
				return false;
			}
		}
		return isImpSheet;		
	}

	private boolean isTemplateSheet(String sheetName){
		boolean isImpSheet = false;
		for(SheetDefine sheetDefine:sheetDefineList){
			String defineTplSheet = sheetDefine.getTplSheet();
			if(sheetName.equalsIgnoreCase(defineTplSheet)){
				isImpSheet = true;
				break;
			}
		}
		return isImpSheet;
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

	public String getImportSheets() {
		return importSheets;
	}

	@XmlAttribute
	public void setImportSheets(String importSheets) {
		this.importSheets = importSheets;
	}

	public String getNotImportSheets() {
		return notImportSheets;
	}

	@XmlAttribute
	public void setNotImportSheets(String notImportSheets) {
		this.notImportSheets = notImportSheets;
	}

	public List<SheetDefine> getSheetDefineList() {
		return sheetDefineList;
	}

	@XmlElement(name = "Sheet")
	public void setSheetDefineList(List<SheetDefine> sheetDefineList) {
		this.sheetDefineList = sheetDefineList;
	}

	public void setVariablesMap(Map<String, VariableDefine> variablesMap) {
		this.variablesMap = variablesMap;
	}

	public Map<String, VariableDefine> getVariablesMap() {
		return variablesMap;
	}
	
	// reset BookDefine
	/**
	 * 重置Book定义
	 * 1. 创建变量Map
	 * 2. Sheet定义的重置
	 * @param variablesMap
	 */
	public void resetDefine(Map<String,VariableDefine> variablesMap){
		log.debug("Begin resetDefine!!!");
		if(variablesMap == null){
			variablesMap = new HashMap<String,VariableDefine>();
		}
		setVariablesMap(variablesMap);
		
		if(sheetDefineMap == null){
			sheetDefineMap = new HashMap<String,SheetDefine>();
		}
		for(SheetDefine sheetDefine:this.sheetDefineList){
			log.debug("SheetDefine=" + sheetDefine.getId());
			sheetDefineMap.put(sheetDefine.getId(), sheetDefine);
			sheetDefine.resetDefine(getVariablesMap());
		}
		log.debug("End resetDefine!!!");
	}
}