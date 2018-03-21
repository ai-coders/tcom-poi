package net.aicoder.tcom.poi.config;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;

import net.aicoder.tcom.poi.util.ExcelUtil;
import net.aicoder.tcom.tools.util.BeanUtil;
import net.aicoder.tcom.tools.util.AiStringUtil;

/**
 * Sheet配置，这是依据当前Sheet中配置参数而获取的数据,并将些参数设置到areaConfigList之中
 * 依据从Sheet中读取的资料来赋值
 * @author StoneShi
 *
 */
public class SheetConfig {
	private static final Log log = LogFactory.getLog(SheetConfig.class);

	private static int DEFINE_BEG_ROW = 2;
	private static int DEFINE_BEG_COL = 2; //B?

	private static String CONFIG_DEFINESS[][] = {
			{ "配置ID", "id", "String", "BY_VAL" },
			{ "填充类型", "fillModel", "String", "BY_VAL" },
			{ "起始单元格", "beginCell", "String", "BY_REF" },
			{ "结束单元格", "endCell", "String", "BY_REF" },
			{ "非空检查行/列", "notNull", "String", "BY_REF" },
			{ "标题起始行/列", "titleBegin", "int", "BY_VAL" },
			{ "标题行数/列数", "titleNum", "int", "BY_VAL" },
			{ "模板行/列", "tplBegin", "int", "BY_VAL" },
			{ "模板行数/列数", "tplNum", "int", "BY_VAL" },
			{ "数据起始行/列", "dataBegin", "int", "BY_VAL" },
			{ "数据结束行/列", "dataEnd", "int", "BY_VAL" },
			{ "变量名", "variable", "String", "BY_VAL" },
			{ "输出Sheet名", "sheetName", "String", "BY_VAL" }
			};
	
	private Sheet configSheet;

	private String sheetDefineId;
	private Map<String,AreaConfig> areaConfigMap;

	// construct
	public SheetConfig() {
		super();
	}
	
	public SheetConfig(Sheet sheet) {
		this.configSheet = sheet;
		loadConfig(sheet);
	}
	
	/**
	 * 依据区域定义的ID获取区域配置信息
	 * @param areaId
	 * @return
	 */
	public AreaConfig getAreaConfig(String areaId) {
		if(this.areaConfigMap == null){
			return null;
		}else{
			return this.areaConfigMap.get(areaId);
		}
	}

	/**
	 * 加载Sheet中的配置信息
	 * @param sheet
	 */
	public void loadConfig(Sheet sheet) {
		if (sheet == null) {
			this.sheetDefineId = null;
			return;
		}
		//this.sheetDefineId = getSheetDefineId(sheet);
		
		this.sheetDefineId = loadSheetDefineId(sheet);
		if (AiStringUtil.isEmpty(this.sheetDefineId)) {
			return;
		}

		areaConfigMap = new HashMap<String,AreaConfig>();
		
		Object cellValue = null;
		int rowNo = DEFINE_BEG_ROW;
		int colNo = DEFINE_BEG_COL + 1;

		for (int colIndex = 0;; colIndex++) {
			colNo = DEFINE_BEG_COL + 1 + colIndex;
			cellValue = readCell(sheet, rowNo - 1, colNo - 1);
			if (cellValue == null || cellValue.toString() == "") {
				break;
			} else {
				loadAreaConfg(sheet, colIndex);
			}
		}
	}

	
	/**
	 * 获取Sheet定义ID
	 * @param sheet
	 * @return
	 */
	private String loadSheetDefineId(Sheet sheet) {
		int rowNo = DEFINE_BEG_ROW;
		int colNo = DEFINE_BEG_COL;
		Object cellValue = readCell(sheet, rowNo - 1, colNo - 1);
		String defineId = "";
		if (cellValue == null) {
			defineId = null;
		} else {
			defineId = cellValue.toString();
		}
		return defineId;
	}

	private void loadAreaConfg(Sheet sheet, int colIndex) {
		AreaConfig areaConfig = new AreaConfig();

		int startRowNo = DEFINE_BEG_ROW;
		int currColNo = DEFINE_BEG_COL + 1 + colIndex;

		for (int rowIdx = 0; rowIdx < CONFIG_DEFINESS.length; rowIdx++) {
			String[] configStrs = CONFIG_DEFINESS[rowIdx];
			String propName = configStrs[1];
			String propType = configStrs[2];
			String cellType = configStrs[3];
			log.trace("param>> propName=" + propName
			//+ ",rowNoStr=" + rowNoStr
					+ ",propType=" + propType + ",cellType=" + cellType);

			if (AiStringUtil.isEmpty(propName)) {
				continue;
			}
			int excelRowNo = startRowNo + rowIdx - 1;
			int excelColNo = currColNo - 1;
			Object cellValue = readCell(sheet, excelRowNo, excelColNo, cellType);
			log.debug("param>> propName=" + propName + ",propType=" + propType
					+ ",cellType=" + cellType + ";ExcelPosition[" + excelRowNo
					+ "," + excelColNo + "]; value=" + cellValue);
			try {
				BeanUtil.setPropertyValue(areaConfig, propName, cellValue);
			} catch (Exception e) {
				log.error("set areaConfig error!!!" + e.toString()
						+ ";propName=" + propName + ";cellValue=" + cellValue);
			}
		}

		if (!AiStringUtil.isEmpty(areaConfig.getId())) {
			areaConfig.resetAreaPosition();
			//this.areaConfigList.add(areaConfig);
			this.areaConfigMap.put(areaConfig.getId(), areaConfig);
		}
	}
	
	private Object readCell(Sheet sheet, int excelRowNo, int excelColNo) {
		return readCell(sheet, excelRowNo, excelColNo, "BY_VAL");
	}

	private Object readCell(Sheet sheet, int excelRowNo, int excelColNo,
			String cellType) {
		Object value = null;
		Row row = null;
		Cell cell = null;
		row = sheet.getRow(excelRowNo);
		if (row == null) {
			return null;
		}
		cell = row.getCell(excelColNo);
		if (cell == null) {
			return null;
		}

		if ("BY_REF".equals(cellType)) {
			value = ExcelUtil.readCellFormula(cell);
		} else {
			value = ExcelUtil.readCellValue(cell);
		}
		return value;
	}
	
	// getter/setter
	public String getSheetDefineId() {
		return sheetDefineId;
	}

	public void setSheetDefineId(String sheetDefineId) {
		this.sheetDefineId = sheetDefineId;
	}
	
	public Sheet getConfigSheet() {
		return configSheet;
	}

	public void setConfigSheet(Sheet configSheet) {
		this.configSheet = configSheet;
	}
}
