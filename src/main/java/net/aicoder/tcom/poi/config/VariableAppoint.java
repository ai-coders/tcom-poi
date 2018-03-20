package net.aicoder.tcom.poi.config;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import net.aicoder.tcom.poi.PoiConstant;
import net.aicoder.tcom.tools.util.AiStringUtil;

/**
 * 变量的组装器，其目的为将变量定义(VariableDefine), 数据值(value)，以及所有相关变量定义的Map绑定在一起
 * @author StoneShi
 *
 */
public class VariableAppoint {
	private static final Log log = LogFactory.getLog(VariableAppoint.class);

	private String configStr; //配置字串，从配置文件解析而来
	private String preVarStr; //
	private String varStr;
	private String postVarStr;
	private String varFormat;
	private boolean isProperty = true;

	private VariableDefine variableDefine; //当前变量的定义

	private Map<String, VariableDefine> variablesMap; //所有变更定义的Map,Key为变量名，Value为变量定义
	private Object value; //变量对应的数值

	public VariableAppoint() {
		super();
	}

	public VariableAppoint(Map<String, VariableDefine> variablesMap) {
		super();
		setVariablesMap(variablesMap);
	}

	public void parserVariable(String configStr) {
		isProperty = false;
		parserVariable(configStr, isProperty);
	}

	public void parserVariable(String configStr, boolean isProperty) {
		parserVariable(configStr, null, isProperty);
	}

	public void parserVariable(String configStr,
			VariableDefine defaultObjDefine, boolean isProperty) {
		setConfigStr(configStr);
		setProperty(isProperty);
		this.variableDefine = parserVaribaleDefine(configStr, variablesMap,
				defaultObjDefine, isProperty);
	}

	public String dataString(Map<String, Object> outData, Object elderVardata,
			VariableDefine elderVarDefine) {
		Object dataValue = dataValue(outData, elderVardata, elderVarDefine);
		if (dataValue == null) {
			return null;
		} else {
			return dataValue.toString();
		}
	}

	public Object dataValue(Map<String, Object> outData, Object elderVardata,
			VariableDefine elderVarDefine) {
		Object value = null;
		Object varValue = null;

		if (variableDefine != null) {
			varValue = variableDefine.dataValue(outData, elderVardata,
					elderVarDefine);
		}
		value = wrapDataValue(varValue);

		log.trace("dataValue>> " + value);

		return value;
	}

	public String dataString(Map<String, Object> outData) {
		Object dataValue = dataValue(outData);
		if (dataValue == null) {
			return null;
		} else {
			return dataValue.toString();
		}
	}

	public Object dataValue(Map<String, Object> outData) {
		Object value = null;
		Object varValue = null;

		if (variableDefine != null) {
			varValue = variableDefine.dataValue(outData);
		}
		value = wrapDataValue(varValue);

		return value;
	}

	public Object dataValue(Object elderVardata, VariableDefine elderVarDefine) {
		Object value = null;
		Object varValue = null;

		if (variableDefine != null) {
			varValue = variableDefine.dataValue(elderVardata, elderVarDefine);
			Map<String, String> varTranMap = variableDefine.getVarTranMap();
			if (varTranMap != null) {
				if (varValue != null) {
					String key = varValue.toString();
					if (varTranMap.containsKey(key)) {
						varValue = varTranMap.get(key);
					}
				}
			}
		}
		value = wrapDataValue(varValue);

		return value;
	}

	private Object wrapDataValue(Object varValue) {
		Object value = null;
		if (AiStringUtil.isEmpty(preVarStr)
				&& AiStringUtil.isEmpty(postVarStr)) {
			value = varValue;
		} else {
			StringBuffer varSB = new StringBuffer();
			if (!AiStringUtil.isEmpty(preVarStr)) {
				varSB.append(preVarStr);
			}
			if (varValue != null) {
				varSB.append(varValue);
			}
			if (!AiStringUtil.isEmpty(postVarStr)) {
				varSB.append(postVarStr);
			}
			value = varSB.toString();
		}
		return value;
	}

	public Object createNewObject(Map<String, Object> impData) {
		Object objValue = null;
		if (variableDefine != null) {
			objValue = variableDefine.createNewObject(impData);
		}
		return objValue;
	}

	public void putValue(Map<String, Object> impData) {
		Object varValue = this.getValue();
		putValue(varValue, impData);
	}

	public void putValue(Object varValue, Map<String, Object> impData) {
		Object value = unWrapDataValue(varValue);
		if (variableDefine != null) {
			variableDefine.putValue(value, impData);
		}
		log.debug("varName=" + this.dumpStr() + "; varValue=" + varValue);
	}

	private Object unWrapDataValue(Object varValue) {
		Object value = null;
		if (varValue == null) {
			return value;
		}
		if (AiStringUtil.isEmpty(preVarStr)
				&& AiStringUtil.isEmpty(postVarStr)) {
			value = varValue;
			return value;
		} else {
			String valueStr = varValue.toString();
			if (!AiStringUtil.isEmpty(preVarStr)
					&& valueStr.length() >= preVarStr.length()) {
				String preStr = valueStr.substring(0, preVarStr.length());
				if (preVarStr.equals(preStr)) {
					valueStr = valueStr.substring(preVarStr.length());
				}
			}
			if (!AiStringUtil.isEmpty(postVarStr)
					&& valueStr.length() >= postVarStr.length()) {
				String postStr = valueStr.substring(valueStr.length()
						- postVarStr.length());
				if (postVarStr.equals(postStr)) {
					valueStr = valueStr.substring(0, valueStr.length()
							- postVarStr.length());
				}
			}
		}
		return value;
	}

	public String dumpStr() {
		StringBuffer varSB = new StringBuffer();
		if (!AiStringUtil.isEmpty(preVarStr)) {
			varSB.append(preVarStr);
		}
		if (variableDefine != null) {
			varSB.append(variableDefine.dumpStr());
		}
		if (!AiStringUtil.isEmpty(postVarStr)) {
			varSB.append(postVarStr);
		}

		return varSB.toString();
	}

	public static VariableDefine parserVaribaleDefine(String configStr,
			Map<String, VariableDefine> variablesMap,
			VariableDefine defaultObjDefine, boolean isProperty) {
		VariableDefine varDefine = null;

		String className = "";
		String varType = ""; //VariableDefine.VAR_TYPE_OBJECT;
		List<String> classList = AiStringUtil
				.splitByFlag(configStr, "{", "}");
		if (classList.size() > 0) {
			className = classList.get(0);
			if (className.length() > 2) {
				String bStr = className.substring(0, 1);
				//String eStr = className.substring(className.length() - 1);
				if (bStr.equals("*")) {
					className = className.substring(1, className.length())
							.trim();
					varType = VariableDefine.VAR_TYPE_LIST;
				}
			}
		}

		List<String> varCfgList = AiStringUtil.splitByFlag(configStr, "$[",
				"]");
		if (varCfgList.size() > 0) {
			String varStr = varCfgList.get(0);
			varDefine = parserVarStrNoCalssName(variablesMap, defaultObjDefine,
					varStr);
		}

		if (varDefine != null) {
			if (!AiStringUtil.isEmpty(className)) {
				if (isProperty) {
					VariableDefine objDefine = varDefine.getParent();
					if (objDefine != null) {
						objDefine.setClassName(className);
						objDefine.setVarType(varType);
					}
				} else {
					varDefine.setClassName(className);
					varDefine.setVarType(varType);
				}
			}
			varDefine.setVarNameList();
		}

		return varDefine;
	}

	private static VariableDefine parserVarStrNoCalssName(
			Map<String, VariableDefine> variablesMap,
			VariableDefine areaObjDefine, String varStr) {
		VariableDefine varDefine = null;

		if (AiStringUtil.isEmpty(varStr)) {
			return varDefine;
		}

		String cfgStr = varStr;
		int parenthesisLPos;
		int parenthesisRPos;

		parenthesisLPos = varStr.indexOf("(");
		parenthesisRPos = varStr.indexOf(")");
		String tranMapStr = "";

		if (parenthesisLPos > 0 && parenthesisRPos > 0) {
			tranMapStr = varStr.substring(parenthesisLPos + 1, parenthesisRPos);
			if (varStr.length() == parenthesisRPos + 1) {
				cfgStr = varStr.substring(0, parenthesisLPos);
			} else {
				cfgStr = varStr.substring(0, parenthesisLPos)
						+ varStr.substring(parenthesisRPos + 1);
			}
		}

		String groupTypeStr = "";
		String[] splitStrs = cfgStr.split(",");
		for (String str : splitStrs) {
			String tmpStr = str.trim().toUpperCase();
			if (PoiConstant.GROUP_TYPE_GROUP.equals(tmpStr)
					|| PoiConstant.GROUP_TYPE_MERGE.equals(tmpStr)) {
				groupTypeStr = tmpStr;
			} else {
				String varNameStr = str.trim();
				varDefine = parserVarNameStr(variablesMap, areaObjDefine,
						varNameStr);
			}
		}

		if (varDefine != null && !AiStringUtil.isEmpty(tranMapStr)) {
			parserTranMap(varDefine, tranMapStr);
		}

		if (varDefine != null && !AiStringUtil.isEmpty(groupTypeStr)) {
			varDefine.setGroupType(groupTypeStr);
		}

		return varDefine;
	}

	private static VariableDefine parserVarNameStr(
			Map<String, VariableDefine> variablesMap,
			VariableDefine areaObjDefine, String varStr) {
		VariableDefine varDefine = null;

		if (AiStringUtil.isEmpty(varStr)) {
			return varDefine;
		}
		String varNameStr = varStr.trim();
		if (AiStringUtil.isEmpty(varNameStr)) {
			return varDefine;
		}

		VariableDefine leftObjDefine = null;
		char firstChar = varNameStr.charAt(0);
		if (firstChar == ':' || firstChar == '.') {
			leftObjDefine = areaObjDefine;
		} else {
			leftObjDefine = null;
		}
		varDefine = leftObjDefine;

		// String holderType;
		String[] splitStrs = varNameStr.split(":");
		int strNum = splitStrs.length;

		String varPreFlag = "";// PoiConstant.VAR_PRE_FLAG_NULL;
		for (int idx = 0; idx < strNum; idx++) {
			String oneStr = splitStrs[idx].trim();
			if (idx == 0) {
				varPreFlag = "";// PoiConstant.VAR_PRE_FLAG_NULL;
			} else {
				varPreFlag = ":";// PoiConstant.VAR_PRE_FLAG_COLON;
			}
			varDefine = parserVarNameSplitByColon(variablesMap, leftObjDefine,
					oneStr, varPreFlag);
			leftObjDefine = varDefine;
		}

		return varDefine;
	}

	private static VariableDefine parserVarNameSplitByColon(
			Map<String, VariableDefine> variablesMap,
			VariableDefine leftObjDefine, String varStr, String varPreFlag) {
		VariableDefine varDefine = null;

		VariableDefine parent = null;
		Map<String, VariableDefine> subObjects = null;
		if (leftObjDefine != null) {
			subObjects = leftObjDefine.getSubObjects();
			parent = leftObjDefine;
			varDefine = leftObjDefine;
		} else {
			subObjects = variablesMap;
			parent = null;
			varDefine = null;
		}

		if (AiStringUtil.isEmpty(varStr)) {
			return varDefine;
		}
		String varNameStr = varStr.trim();
		if (AiStringUtil.isEmpty(varNameStr)) {
			return varDefine;
		}

		String[] splitStrs = varNameStr.split("\\.");
		int splitNameNum = splitStrs.length;

		String currVarPreFlag;
		for (int idx = 0; idx < splitNameNum; idx++) {
			String oneNameStr = splitStrs[idx].trim();

			String varFullName = "";
			String parentVarFullName = "";
			String varName = "";

			if (idx == 0) {
				currVarPreFlag = varPreFlag;
			} else {
				currVarPreFlag = ".";// PoiConstant.VAR_PRE_FLAG_POINT;
			}

			if (AiStringUtil.isEmpty(oneNameStr)) {
				// do nothing
			} else {
				if (subObjects.containsKey(oneNameStr)) {
					varDefine = subObjects.get(oneNameStr);
				} else {
					varDefine = new VariableDefine();
					varDefine.setVarPreFlag(currVarPreFlag);

					varDefine.setParent(parent);
					varName = oneNameStr;
					varDefine.setVarName(varName);
					if (parent != null) {
						parentVarFullName = parent.getVarFullName();
					}
					varFullName = parentVarFullName + currVarPreFlag + varName;
					varDefine.setVarFullName(varFullName);

					subObjects.put(varName, varDefine);
				}

				subObjects = varDefine.getSubObjects();
				parent = varDefine;
			}
		}

		return varDefine;
	}

	// Y=○,N=X
	private static void parserTranMap(VariableDefine varDefine,
			String tranMapStr) {
		Map<String, String> varTranMap = new HashMap<String, String>();
		String[] splitStrs = tranMapStr.split(",");
		for (String str : splitStrs) {
			String key;
			String value;

			int pos = 0;
			pos = str.indexOf("=");
			if (pos > 0) {
				key = str.substring(0, pos).trim();
				value = str.substring(pos + 1);
				varTranMap.put(key, value);
			}
		}

		varDefine.setVarTranMap(varTranMap);
	}

	// getter/setter
	public String getConfigStr() {
		return configStr;
	}

	public void setConfigStr(String configStr) {
		this.configStr = configStr;
		splitConfigStr(configStr);
	}

	private void splitConfigStr(String str) {
		if (AiStringUtil.isEmpty(str)) {
			return;
		}

		int leftBrace;
		int rightBrace;
		int leftBracket;
		int righBracket;

		leftBrace = str.indexOf("{");
		rightBrace = str.indexOf("}");
		leftBracket = str.indexOf("$[");
		righBracket = str.indexOf("]");

		String preStr = "";
		String postStr = "";

		int leftPos = -1;
		int rightPos = -1;
		if (leftBrace >= 0 && rightBrace > leftBrace) {
			leftPos = leftBrace;
			rightPos = rightBrace;
		}

		if (leftBracket >= 0 && righBracket > leftBracket) {
			if ((leftPos < 0) || (leftPos > righBracket)) {
				leftPos = leftBracket;
			}
			if ((rightPos < 0) || (rightPos < leftBracket)) {
				rightPos = righBracket;
			}
		}
		if (leftPos >= 0) {
			preStr = str.substring(0, leftPos);
			// setPreVarStr(preStr);
			this.preVarStr = preStr;
			this.varStr = str.substring(leftPos + 1);
		}
		if (rightPos > 0) {
			postStr = str.substring(rightPos + 1, str.length());
			// setPostVarStr(postStr);
			this.postVarStr = postStr;
			int begPos;
			if (leftPos < 0) {
				begPos = 0;
			} else {
				begPos = leftPos + 1;
			}
			this.varStr = str.substring(begPos, rightPos);
		}

		if (leftPos < 0 || rightPos < 0) {
			this.preVarStr = str;
			this.varStr = "";
			this.postVarStr = "";
		}
	}

	public String getPreVarStr() {
		return preVarStr;
	}

	public String getVarStr() {
		return varStr;
	}

	public String getPostVarStr() {
		return postVarStr;
	}

	public String getVarFormat() {
		return varFormat;
	}

	public void setVarFormat(String varFormat) {
		this.varFormat = varFormat;
	}

	public boolean isProperty() {
		return isProperty;
	}

	public void setProperty(boolean isProperty) {
		this.isProperty = isProperty;
	}

	public VariableDefine getVariableDefine() {
		return variableDefine;
	}

	public void setVariableDefine(VariableDefine variableDefine) {
		this.variableDefine = variableDefine;
	}

	public Map<String, VariableDefine> getVariablesMap() {
		return variablesMap;
	}

	public void setVariablesMap(Map<String, VariableDefine> variablesMap) {
		this.variablesMap = variablesMap;
	}

	public Object getValue() {
		return value;
	}

	public void setValue(Object value) {
		this.value = value;
	}
}
