package net.aicoder.tcom.poi.config;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import net.aicoder.tcom.tools.util.BeanUtil;
import net.aicoder.tcom.tools.util.AiStringUtil;

/**
 * 变量定义，依据变量定义的字符串 + 前置定义的变量解析出变量的定义
 * 
 * @author StoneShi
 *
 */
public class VariableDefine {
	private static final Log log = LogFactory.getLog(VariableDefine.class);

	public static String VAR_TYPE_LIST = "L";
	public static String VAR_TYPE_OBJECT = "O";
	public static String VAR_TYPE_PROPERTY = "P";

	public static String VAR_PRE_FLAG_LIST = ":";
	public static String VAR_PRE_FLAG_OBJECT = ".";

	private String className; //变量对应的类名
	private String varName; //变量名，当前这一节
	private String varFullName; //变量名，全名，包括所有父级的名称

	private String varType; // List, Object, Property
	private String varPreFlag; // :,.,""
	private String groupType; // group, merge，对应Excel的分组及单元格合并

	private VariableDefine parent; //父变量
	
	private List<VariableDefine> varNameList; //变量名解析后分解成各段存放为List
	private Map<String, VariableDefine> subObjects = new HashMap<String, VariableDefine>(); //子成员变量

	private Map<String, String> varTranMap; //原始数值与输出数值的转换对应表，这个情况想复杂了，应交由导出程序生成数据时处理
	
	/**
	 * 
	 * @param outData
	 * @param elderVardata
	 * @param elderVarDefine
	 * @return
	 */
	public Object dataValue(Map<String, Object> outData, Object elderVardata,
			VariableDefine elderVarDefine) {
		Object value = null;

		if (elderVardata == null || elderVarDefine == null) {
			value = dataValue(outData);
		} else {
			value = dataValue(elderVardata, elderVarDefine);
		}

		return value;
	}

	/**
	 * 从输出数据的Map中获取当前变量的数值
	 * 只能获取具体对象或属性的值，如果是从List中取值，必须先通过循环获取List中具体对象后才能取值
	 * @param outData
	 * @return
	 */
	public Object dataValue(Map<String, Object> outData) {
		Object varValue = null;

		List<VariableDefine> currVarNameList = this.getVarNameList();
		if (currVarNameList == null || currVarNameList.size() == 0) {
			return varValue;
		}

		for (int index = 0; index < currVarNameList.size(); index++) {
			VariableDefine currVar = currVarNameList.get(index);
			String currVarName = currVar.getVarName();
			if (index == 0) {
				varValue = outData.get(currVarName);
			} else {
				if (varValue != null && !AiStringUtil.isEmpty(currVarName)) {
					try {
						varValue = BeanUtil.getPropertyValue(varValue,
								currVarName);
					} catch (Exception e) {
						log.error("getPropertyValue error! " + e.toString()
								+ ">> varValue=" + varValue + "currVarName="
								+ currVarName);
					}
				} else {
					varValue = null;
				}
			}
		}
		varValue = dataValueByTranMap(varValue);
		return varValue;
	}

	/**
	 * 依据传入父对象或祖先对象及其对应定义，获取当前变量的数值
	 * @param elderVardata
	 * @param elderVarDefine
	 * @return
	 */
	public Object dataValue(Object elderVardata, VariableDefine elderVarDefine) {
		Object value = null;

		if (elderVardata == null || elderVarDefine == null) {
			return value;
		}

		List<VariableDefine> elderVarNameList = elderVarDefine.getVarNameList();
		List<VariableDefine> currVarNameList = this.getVarNameList();

		if (elderVarNameList == null || currVarNameList == null
				|| currVarNameList.size() < elderVarNameList.size()) {
			return value;
		}

		boolean isSonOfElder = true;
		for (int strIdx = 0; strIdx < elderVarNameList.size(); strIdx++) {
			VariableDefine elderVar = elderVarNameList.get(strIdx);
			VariableDefine currVar = currVarNameList.get(strIdx);
			if (!elderVar.equals(currVar)) {
				isSonOfElder = false;
				break;
			}
		}
		if (isSonOfElder) {
			value = dataValue(elderVardata, elderVarNameList.size());
		}

		return value;
	}

	/**
	 * 依据传入父对象或祖先对象及其对应定义所对应名称的层级，获取当前变量的数值
	 * @param elderVardata
	 * @param elderVarNameLen
	 * @return
	 */
	public Object dataValue(Object elderVardata, int elderVarNameLen) {
		Object varValue = null;

		List<VariableDefine> currVarNameList = this.getVarNameList();
		if (currVarNameList == null || currVarNameList.size() < elderVarNameLen) {
			return varValue;
		}

		varValue = elderVardata;
		for (int index = elderVarNameLen; index < currVarNameList.size(); index++) {
			VariableDefine currVar = currVarNameList.get(index);
			String currVarName = currVar.getVarName();
			if (varValue != null && !AiStringUtil.isEmpty(currVarName)) {
				try {
					varValue = BeanUtil.getPropertyValue(varValue, currVarName);
				} catch (Exception e) {
					log.error("getPropertyValue error! " + e.toString()
							+ ">> varValue=" + varValue + "currVarName="
							+ currVarName);
				}
			} else {
				varValue = null;
			}
		}
		varValue = dataValueByTranMap(varValue);
		return varValue;
	}

	/**
	 * 为导入数据创建当前变量对应的对象
	 * @param impData
	 * @return
	 */
	public Object createNewObject(Map<String, Object> impData) {
		Object objValue = null;
		String className;
		
		if (AiStringUtil.isEmpty(this.className)){
			Object varData = getVarData(this, impData);
			className = varData.getClass().getName();
		}else{
			className = this.className;
		}
		
		objValue = newObject(className);
		putValue(objValue, impData);
		return objValue;
	}

	/**
	 * 为导入数据，将传入数据设置为当前变量所对应的数值
	 * @param objValue
	 * @param impData
	 */
	public void putValue(Object objValue, Map<String, Object> impData) {
		VariableDefine varDefine = this;
		List<VariableDefine> varNameList = varDefine.getVarNameList();
		if (varNameList == null || impData == null) {
			return;
		}
		Object parentData = null;
		Object varData = null;
		int varNameListSize = varNameList.size();
		if (varNameListSize <= 1) {
			putValueToImpData(varDefine, impData, objValue);
		} else {
			for (int index = 0; index < varNameListSize - 1; index++) {
				VariableDefine theVarDefine = varNameList.get(index);
				if (index == 0) {
					varData = getVarData(theVarDefine, impData);
				} else {
					varData = getVarData(theVarDefine, parentData);
				}
				parentData = varData;
			}
			putValueToVarData(varDefine, parentData, objValue);
		}
	}

	/**
	 * 
	 * @param varDefine
	 * @param impData
	 * @return
	 */
	@SuppressWarnings("unchecked")
	private Object getVarData(VariableDefine varDefine,
			Map<String, Object> impData) {
		Object varValue = null;
		String varName = varDefine.getVarName();
		String varType = varDefine.getVarType();
		if (impData.containsKey(varName)) {
			Object tmpVarValue = impData.get(varName);
			if (VariableDefine.VAR_TYPE_LIST.equals(varType)) {
				List<Object> listValue = (List<Object>) tmpVarValue;
				if (listValue == null) {
					listValue = new ArrayList<Object>();
					putValueToImpData(varDefine, impData, listValue);
				}
				if (listValue.size() == 0) {
					//varValue = newObject(varDefine); //marked by stone 20171124
					String className = varDefine.getClassName();
					varValue = newObject(className);
					listValue.add(varValue);
				} else {
					varValue = listValue.get(listValue.size() - 1);
				}
			} else {
				varValue = tmpVarValue;
			}
		} else {
			//varValue = newObject(varDefine); //marked by stone 20171124
			String className = varDefine.getClassName();
			varValue = newObject(className);
			putValueToImpData(varDefine, impData, varValue);
		}
		return varValue;
	}
	
	/**
	 * 
	 * @param varDefine
	 * @param parentData
	 * @return
	 */
	@SuppressWarnings("unchecked")
	private Object getVarData(VariableDefine varDefine, Object parentData) {
		Object varValue = null;
		String varName = varDefine.getVarName();
		String varType = varDefine.getVarType();

		if (parentData == null || AiStringUtil.isEmpty(varName)) {
			log.error("parentData or varName is null! " + varDefine.dumpStr()
					+ ">> parentData=" + parentData + "; varName=" + varName);
			return varValue;
		}

		boolean isNewObject = false;
		List<Object> listValue = null;
		try {
			Object tmpVarValue = BeanUtil.getPropertyValue(parentData, varName);
			if (VariableDefine.VAR_TYPE_LIST.equals(varType)) {
				listValue = (List<Object>) tmpVarValue;
				if (listValue == null) {
					listValue = new ArrayList<Object>();
					isNewObject = true;
				} else if (listValue.size() == 0) {
					//varValue = newObject(varDefine); //marked by stone 20171124
					String className = varDefine.getClassName();
					varValue = newObject(className);
					listValue.add(varValue);
				} else {
					varValue = listValue.get(listValue.size() - 1);
				}
			} else {
				if (tmpVarValue == null) {
					//varValue = newObject(varDefine);
					varValue = BeanUtil
							.newObjectByProperty(parentData, varName);
					isNewObject = true;
				} else {
					varValue = tmpVarValue;
				}
			}
		} catch (Exception e) {
			log.error("getPropertyValue error! " + e.toString()
					+ ">> parentVarData=" + parentData + "; currVarName="
					+ varName);
		}

		if (isNewObject) {
			putValueToVarData(varDefine, parentData, varValue);
		}
		return varValue;
	}

	/**
	 * 
	 * @param varDefine
	 * @param impData
	 * @param varValue
	 */
	private void putValueToImpData(VariableDefine varDefine,
			Map<String, Object> impData, Object varValue) {
		String varName = varDefine.getVarName();
		String varType = varDefine.getVarType();
		
		varValue = putValueByTranMap(varValue);

		if (VariableDefine.VAR_TYPE_LIST.equals(varType)) {
			List<Object> listValue = new ArrayList<Object>();
			listValue.add(varValue);
			impData.put(varName, listValue);
		} else {
			impData.put(varName, varValue);
		}
	}

	/**
	 * 
	 * @param varDefine
	 * @param parentData
	 * @param varValue
	 */
	@SuppressWarnings("unchecked")
	private void putValueToVarData(VariableDefine varDefine, Object parentData,
			Object varValue) {
		String varName = varDefine.getVarName();
		String varType = varDefine.getVarType();

		varValue = putValueByTranMap(varValue);

		List<Object> listValue = null;
		if (VariableDefine.VAR_TYPE_LIST.equals(varType)) {
			try {
				Object tmpVarValue = null;
				tmpVarValue = BeanUtil.getPropertyValue(parentData, varName);
				if (tmpVarValue == null) {
					listValue = new ArrayList<Object>();
					try {
						BeanUtil.setPropertyValue(parentData, varName,
								listValue);
					} catch (Exception e) {
						log.error("set parentData error! " + e.toString()
								+ ">> propName=" + varName + ";listValueSize="
								+ listValue.size());
					}
				} else {
					listValue = (List<Object>) tmpVarValue;
				}
				listValue.add(varValue);
			} catch (Exception e) {
				log.error("getPropertyValue error! " + e.toString()
						+ ">> parentVarData=" + parentData + "; currVarName="
						+ varName);
			}

		} else {
			try {
				BeanUtil.setPropertyValue(parentData, varName, varValue);
			} catch (Exception e) {
				log.error("set parentData error! " + e.toString()
						+ ">> propName=" + varName + ";varValue=" + varValue);
			}
		}
	}

	/**
	 * 将所获取的原始数据转换成所对应的输出数值
	 * 如：配置了(Y=V)，即如果原始数值为"Y"时，则输出的数值为"V"
	 * @param value
	 * @return
	 */
	private Object dataValueByTranMap(Object value) {
		Object tranValue = null;
		if (varTranMap == null || varTranMap.size() == 0) {
			return value;
		}
		String key = "";
		if (value == null) {
			key = "null";
		} else {
			key = value.toString();
		}
		if (varTranMap.containsKey(key)) {
			tranValue = varTranMap.get(key);
		} else {
			tranValue = value;
		}
		return tranValue;
	}
	
	/**
	 * 将输入数据转换成对应保存的值，是以上的反向操作
	 * 如：配置了(Y=V)，即如果输入数值为"V"时，则输出的数值为""
	 * @param value
	 * @return
	 */
	private Object putValueByTranMap(Object value) {
		Object tranValue = null;
		if (varTranMap == null || varTranMap.size() == 0 || value == null) {
			return value;
		}
		if (varTranMap.containsValue(value)) {
			for (Entry<String, String> entry : varTranMap.entrySet()) {
				if (value.equals(entry.getValue())) {
					tranValue = entry.getKey();
					break;
				}
			}
		} else {
			tranValue = value;
		}
		return tranValue;
	}

	private Object newObject(String className) {
		Object objValue = null;
		//String className = currVarDefine.getClassName();
		if (className != null) {
			try {
				objValue = Class.forName(className).newInstance();
			} catch (ClassNotFoundException | InstantiationException
					| IllegalAccessException e) {
				log.error(e.toString());
			}
		} else {
			log.error(className + ">> Class Name is null!!!");
		}
		return objValue;
	}

	public String dumpStr() {
		StringBuffer varSB = new StringBuffer();
		if (!AiStringUtil.isEmpty(className)) {
			varSB.append("{");
			if (VAR_TYPE_LIST.equals(varType)) {
				varSB.append("*");
			}
			varSB.append(className);
			varSB.append("}");
		}

		varSB.append("$[");
		if (parent != null) {
			varSB.append(parent.getVarFullName());
		}
		varSB.append(varPreFlag);
		varSB.append(varName);

		if (!AiStringUtil.isEmpty(groupType)) {
			varSB.append(",");
			varSB.append(groupType);
		}

		if (varTranMap != null) {
			varSB.append(",(");
			int idx = 0;
			for (String key : varTranMap.keySet()) {
				if (idx++ != 0) {
					varSB.append(",");
				}
				varSB.append(key);
				varSB.append("=");
				varSB.append(varTranMap.get(key));
			}
			varSB.append(")");
		}
		varSB.append("]");

		return varSB.toString();
	}

	// getter/setter
	public String getClassName() {
		return className;
	}

	public void setClassName(String className) {
		this.className = className;
	}

	public String getVarName() {
		return varName;
	}

	public void setVarName(String varName) {
		this.varName = varName;
	}

	public String getVarFullName() {
		return varFullName;
	}

	public void setVarFullName(String varFullName) {
		this.varFullName = varFullName;
	}

	public Map<String, String> getVarTranMap() {
		return varTranMap;
	}

	public void setVarTranMap(Map<String, String> varTranMap) {
		this.varTranMap = varTranMap;
	}

	public String getVarType() {
		return varType;
	}

	public void setVarType(String varType) {
		this.varType = varType;
	}

	public String getVarPreFlag() {
		return varPreFlag;
	}

	public void setVarPreFlag(String varPreFlag) {
		this.varPreFlag = varPreFlag;
	}

	public String getGroupType() {
		return groupType;
	}

	public void setGroupType(String groupType) {
		this.groupType = groupType;
	}

	public VariableDefine getParent() {
		return parent;
	}

	public void setParent(VariableDefine parent) {
		this.parent = parent;
	}

	public Map<String, VariableDefine> getSubObjects() {
		return subObjects;
	}

	public void setSubObjects(Map<String, VariableDefine> subObjects) {
		this.subObjects = subObjects;
	}

	public void setVarNameList() {
		VariableDefine currVar = this;

		varNameList = new ArrayList<VariableDefine>();
		while (currVar != null) {
			varNameList.add(0, currVar);
			currVar = currVar.getParent();
		}
		for (int index = varNameList.size() - 1; index >= 0; index--) {
			VariableDefine varDefine = varNameList.get(index);
			String varType = varDefine.getVarType();
			if (VariableDefine.VAR_TYPE_LIST.equals(varType)) {
				//this.lastVarDefineTypeList = varDefine;
				break;
			}
		}
	}

	public List<VariableDefine> getVarNameList() {
		return varNameList;
	}
	
	/**
	public void putValue(Object dataValue, Map<String, Object> impData,
			boolean isNewRecord) {
		List<VariableDefine> currVarNameList = this.getVarNameList();

		if (currVarNameList == null || impData == null) {
			return;
		}

		Object parentVarData = null;
		VariableDefine parentVarDefine = null;

		int varNameListSize = currVarNameList.size();
		for (int index = 0; index < varNameListSize; index++) {
			VariableDefine currVarDefine = currVarNameList.get(index);

			Object varValue = null;
			String varType = currVarDefine.getVarType();

			if (index < varNameListSize - 1) {
				if (VariableDefine.VAR_TYPE_LIST.equals(varType)) {
					varValue = doForVarTypeList(currVarDefine, parentVarData,
							parentVarDefine, dataValue, impData, isNewRecord);
				} else {
					varValue = doForVarTypeObject(currVarDefine, parentVarData,
							parentVarDefine, dataValue, impData, isNewRecord);
				}
				parentVarData = varValue;
				parentVarDefine = currVarDefine;
			} else { //last varName
				setValueToParentVarData(currVarDefine, parentVarData,
						parentVarDefine, dataValue, impData);
			}
		}
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	private Object doForVarTypeList(VariableDefine currVarDefine,
			Object parentVarData, VariableDefine parentVarDefine,
			Object dataValue, Map<String, Object> impData, boolean isNewRecord) {
		Object rtnObject = null;
		Object varValue = null;
		Object oneObjValue = null;
		List listValue = null;

		varValue = currVarDefine.dataValue(impData, parentVarData,
				parentVarDefine);
		if (varValue == null) {
			oneObjValue = newObject(currVarDefine);
			if (oneObjValue == null) {
				log.error("New Object is null!>> " + currVarDefine.dumpStr());
			} else {
				listValue = new ArrayList();
				listValue.add(oneObjValue);
				// setValue 将当前值(varValue)设置到上级对象中
				setValueToParentVarData(currVarDefine, parentVarData,
						parentVarDefine, listValue, impData);
			}
		} else {
			listValue = (List) varValue;
			if (listValue.size() == 0) {
				oneObjValue = newObject(currVarDefine);
				if (oneObjValue == null) {
					log.error("New Object is null!>> "
							+ currVarDefine.dumpStr());
				} else {
					listValue.add(oneObjValue);
				}
			} else {
				if (currVarDefine == lastVarDefineTypeList && isNewRecord) {
					oneObjValue = newObject(currVarDefine);
					if (oneObjValue == null) {
						log.error("New Object is null!>> "
								+ currVarDefine.dumpStr());
					} else {
						listValue.add(oneObjValue);
					}
				} else {
					oneObjValue = listValue.get(listValue.size() - 1);
				}
			}
		}
		rtnObject = oneObjValue;
		return rtnObject;
	}

	private Object doForVarTypeObject(VariableDefine currVarDefine,
			Object parentVarData, VariableDefine parentVarDefine,
			Object dataValue, Map<String, Object> impData, boolean isNewRecord) {
		Object varValue = null;

		//		if(isNewRecord == true && parentVarData == null){
		//			varValue = null;
		//		}else{
		varValue = currVarDefine.dataValue(impData, parentVarData,
				parentVarDefine);
		//		}
		if (varValue == null) {
			varValue = newObject(currVarDefine);
			if (varValue == null) {
				log.error("New Object is null!>> " + currVarDefine.dumpStr());
				return null;
			}
			// setValue 将当前值(varValue)设置到上级对象中
			setValueToParentVarData(currVarDefine, parentVarData,
					parentVarDefine, varValue, impData);
		}
		return varValue;
	}

	private void setValueToParentVarData(VariableDefine currVarDefine,
			Object parentVarData, VariableDefine parentVarDefine,
			Object dataValue, Map<String, Object> impData) {
		String propName = currVarDefine.getVarName();
		if (parentVarDefine == null) {
			impData.put(propName, dataValue);
		} else if (parentVarData != null) {
			//ReflectUtil.setpValue(parentVarData, propName, dataValue);
			try {
				BeanUtil.setPropertyValue(parentVarData, propName, dataValue);
			} catch (Exception e) {
				log.error("set parentVarData error! " + e.toString()
						+ ">> propName=" + propName + ";dataValue=" + dataValue);
			}
		} else {
			log.error("setpValue parentVarData is null!>> "
					+ currVarDefine.dumpStr());
		}
	}
	**/	
}
