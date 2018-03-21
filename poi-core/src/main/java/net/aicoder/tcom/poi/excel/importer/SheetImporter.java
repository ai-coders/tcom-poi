package net.aicoder.tcom.poi.excel.importer;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;

import net.aicoder.tcom.poi.PoiConstant;
import net.aicoder.tcom.poi.config.AreaConfig;
import net.aicoder.tcom.poi.config.AreaDefine;
import net.aicoder.tcom.poi.config.AreaOriginPosition;
import net.aicoder.tcom.poi.config.CellDefine;
import net.aicoder.tcom.poi.config.ColumnDefine;
import net.aicoder.tcom.poi.config.Position;
import net.aicoder.tcom.poi.config.RowDefine;
import net.aicoder.tcom.poi.config.SheetConfig;
import net.aicoder.tcom.poi.config.SheetDefine;
import net.aicoder.tcom.poi.config.VariableAppoint;
import net.aicoder.tcom.poi.config.VariableDefine;
import net.aicoder.tcom.poi.data.IDataContext;
import net.aicoder.tcom.poi.data.IDataOper;
import net.aicoder.tcom.poi.data.impl.DefaultDataContext;
import net.aicoder.tcom.poi.excel.AreaInfo;
import net.aicoder.tcom.poi.excel.DataItemInfo;
import net.aicoder.tcom.poi.util.ExcelUtil;
import net.aicoder.tcom.tools.util.AiStringUtil;

public class SheetImporter {
	private static final Log log = LogFactory.getLog(SheetImporter.class);

	private SheetConfig sheetConfig;
	private SheetDefine sheetDefine;
	private Workbook impWorkbook;
	private Sheet impSheet;

	private IDataContext dataContext;
	private IDataOper dataOper;

	// construct
	public SheetImporter() {
		super();
	}

	public SheetImporter(SheetDefine sheetDefine, SheetConfig sheetConfig) {
		setSheetDefine(sheetDefine);
		setSheetConfig(sheetConfig);
	}

	public void doImport(Sheet sheet) {
		setImpSheet(sheet);

		doImport();
	}

	public void doImport() {
		if (sheetDefine == null || sheetConfig == null) {
			return;
		}
		if (impSheet == null) {
			return;
		}
		// 开始时间
		Long begTime = System.currentTimeMillis();
		log.debug("Import sheet begin:" + new Date(begTime));

		if (dataContext == null) {
			dataContext = new DefaultDataContext();
		}
		if (dataOper != null) {
			dataOper.setDataContext(dataContext);
			dataOper.preProduce();
		}

		importInitialize();

		for (int areaIndex = 0; areaIndex < sheetDefine.getAreaDefineList()
				.size(); areaIndex++) {
			AreaDefine areaDefine = sheetDefine.getAreaDefineList().get(
					areaIndex);
			String areaId = areaDefine.getId();
			AreaConfig areaConfig = sheetConfig.getAreaConfig(areaId);
			AreaInfo areaInfo = new AreaInfo(areaDefine, areaConfig) ;
			resetAreaInfo(areaInfo);

			importArea(areaInfo);
		}

		if (dataOper != null) {
			dataOper.setDataContext(dataContext);
			dataOper.produce();
		}

		importTerminate();

		if (dataOper != null) {
			dataOper.setDataContext(dataContext);
			dataOper.postProduce();
		}

		// 结束时间
		Long endTime = System.currentTimeMillis();
		log.debug("Import sheet end:" + new Date(endTime) + ";Run time:"
				+ (endTime - begTime));
	}
	
	private void resetAreaInfo(AreaInfo areaInfo){
		AreaDefine areaDefine = areaInfo.getAreaDefine();
		AreaConfig areaConfig = areaInfo.getAreaConfig();
		
		AreaOriginPosition areaOriginPosition = areaDefine.getAreaOriginPosition();
		Position areaPositionDefine = areaOriginPosition.getAreaPositionDefine();
		Position dataPositionDefine = areaOriginPosition.getDataPositionDefine();
		
		int areaRow = 0; int areaCol = 0;
		int dataRow = 0; int dataCol = 0;
		if(areaConfig != null){
			areaRow = areaConfig.getRangePosition().getBegRow();
			areaCol = areaConfig.getRangePosition().getBegCol();
			
			dataRow = areaRow + dataPositionDefine.getRow() - areaPositionDefine.getRow();
			dataCol = areaCol + dataPositionDefine.getColumn() - areaPositionDefine.getColumn();
		}else{
			areaRow = areaPositionDefine.getRow();
			areaCol = areaPositionDefine.getColumn();
			
			dataRow = dataPositionDefine.getRow();
			dataCol = dataPositionDefine.getColumn();
		}
		Position areaPosition = new Position(areaRow,areaCol);
		Position dataPosition = new Position(dataRow,dataCol);
		
		areaOriginPosition.setAreaPositionActual(areaPosition);
		areaOriginPosition.setDataPositionActual(dataPosition);
		log.debug("areaInfo >> id=" + areaInfo.getId() 
		+ ", AreaPositionActual =" + areaPosition.toString() 
		+ ", DataPositionActual =" + dataPosition.toString());
		
		//log.debug("addListener >>" + areaInfo.getId());
	}
	
	private void importArea(AreaInfo areaInfo){
		AreaDefine areaDefine = areaInfo.getAreaDefine();
		
		String fillModel = areaDefine.getFillModel();
		log.debug("Begin importArea>> areaId = " + areaDefine.getId() + "; fillModel=" + fillModel);
		
		if(PoiConstant.FILL_MODEL_FIXED.equalsIgnoreCase(fillModel)){
			impFixedArea(areaInfo);
		}else{
			impDynamicArea(areaInfo);
		}
	}

	private void impFixedArea(AreaInfo areaInfo) {
		Map<VariableDefine, List<VariableAppoint>> areaDataMap = new HashMap<VariableDefine, List<VariableAppoint>>();

		DataItemInfo currDIPosition = new DataItemInfo(areaInfo);
		impDIRows(areaDataMap, areaInfo, currDIPosition);
	}
	
	private void impDynamicArea(AreaInfo areaInfo) {
		boolean isFinished = false;
		
		AreaDefine areaDefine = areaInfo.getAreaDefine();
		String fillModel = areaDefine.getFillModel();

		AreaOriginPosition currAreaOriginPosition = areaDefine.getAreaOriginPosition();
		if(areaDefine.getParentAreaDefine() != null){
			AreaOriginPosition parentAreaOriginPosition = areaDefine.getParentAreaDefine().getAreaOriginPosition();
			Position parentDataPositionActual = parentAreaOriginPosition.getDataPositionActual();
			Position parentDataPositionDefine = parentAreaOriginPosition.getDataPositionDefine();
			
			Position currDataPositionActual = currAreaOriginPosition.getDataPositionActual();
			Position currDataPositionDefine = currAreaOriginPosition.getDataPositionDefine();
			
			int currDIRow = parentDataPositionActual.getRow() + currDataPositionDefine.getRow()
					- parentDataPositionDefine.getRow();
			int currDICol = parentDataPositionActual.getColumn() + currDataPositionDefine.getColumn()
					- parentDataPositionDefine.getColumn();
			if(currDataPositionActual == null){
				currDataPositionActual = new Position();
				currAreaOriginPosition.setDataPositionActual(currDataPositionActual);
			}
			currDataPositionActual.setRow(currDIRow);
			currDataPositionActual.setColumn(currDICol);
			log.debug("expDynamicArea>> currDataPositionActual=" + currDataPositionActual.toString());
		}
		DataItemInfo currDIPosition = new DataItemInfo(areaInfo);
		Position diPositionActual = currAreaOriginPosition.getDataPositionActual();
		
		int dataIdx = 0;
		do {
			log.debug("expDynamicArea>> dataIndex=" + dataIdx + ";diPositionActual=" + diPositionActual.toString());
			currDIPosition.setDIIndex(dataIdx);
			currDIPosition.setDIPosition(new Position(diPositionActual));
			//currDIPosition.setShiftRowNum(areaDefine.getShiftRowNum());

			isFinished = impOneDataOfArea(areaInfo, currDIPosition);
			
			if(PoiConstant.FILL_MODEL_ROW.equalsIgnoreCase(fillModel)){
				int nexDIRowNo = diPositionActual.getRow() + areaDefine.getDataRoCNum();
				diPositionActual.setRow(nexDIRowNo);
			}else{
				int nexDIColNo = diPositionActual.getColumn() + areaDefine.getDataRoCNum();
				diPositionActual.setColumn(nexDIColNo);
			}
			
		} while (isFinished == false);
	}
	
	private boolean impOneDataOfArea(AreaInfo areaInfo, DataItemInfo currDIPosition) {
		boolean isFinished = false;

		AreaDefine areaDefine = areaInfo.getAreaDefine();
		String fillModel = areaDefine.getFillModel();
		if (fillModel != null) {
			fillModel = fillModel.toUpperCase();
		}
		log.debug("Begin importArea>> areaId = " + areaDefine.getId() + "; fillModel=" + fillModel);
		
		Map<VariableDefine, List<VariableAppoint>> areaDataMap = new HashMap<VariableDefine, List<VariableAppoint>>();
		switch (fillModel) {
		case PoiConstant.FILL_MODEL_AUTO:
			break;
		case PoiConstant.FILL_MODEL_ROW:
			isFinished = impDIRows(areaDataMap, areaInfo, currDIPosition);
			break;
		case PoiConstant.FILL_MODEL_COLUMN:
			isFinished = impDICols(areaDataMap, areaInfo, currDIPosition);
			break;
		default: // FILL_MODEL_FIXED
			//impFixedArea(areaInfo);
		}
		
		List<AreaDefine> subAreaDefineList = areaDefine.getSubAreaDefineList();
		if(subAreaDefineList != null){
			for (int areaIndex = 0; areaIndex < subAreaDefineList.size(); areaIndex++) {
				AreaDefine subArea = subAreaDefineList.get(areaIndex);
				String areaId = subArea.getId();
				AreaConfig areaConfig = sheetConfig.getAreaConfig(areaId);
				//AreaInfo subAreaInfo = new AreaInfo(areaDefine, areaConfig) ;
				AreaInfo subAreaInfo = new AreaInfo(subArea, areaConfig) ;
				//resetAreaInfo(subAreaInfo); //？？？

				importArea(subAreaInfo);
			}
		}		
		
		return isFinished;
	}
	
	private boolean impDIRows(Map<VariableDefine, List<VariableAppoint>> areaDataMap,
			AreaInfo areaInfo,DataItemInfo currDIPosition) {
		boolean isFinished = false;

		AreaDefine areaDefine = areaInfo.getAreaDefine();
		if (areaDefine.getRowDefineList() == null) {
			isFinished = true;
			return isFinished;
		}
		VariableAppoint repeatVarAppoint = sheetDefine.getRepeatByVar();
		VariableAppoint elementVarAppoint = sheetDefine.getSheetVar();
		VariableAppoint areaVarAppoint = areaDefine.getAreaVarAppoint();
		if (areaVarAppoint == null) {
			if (elementVarAppoint != null) {
				areaVarAppoint = elementVarAppoint;
			} else if (repeatVarAppoint != null) {
				areaVarAppoint = repeatVarAppoint;
			}
		}

		for (RowDefine rowDefine : areaDefine.getRowDefineList()) {
			VariableAppoint rowVarAppoint = rowDefine.getVarAppoint();
			if (rowVarAppoint == null) {
				rowVarAppoint = areaVarAppoint;
			}
			VariableDefine varDefine = rowVarAppoint.getVariableDefine();
			List<VariableAppoint> varDataList = null;
			if (areaDataMap.containsKey(varDefine)) {
				varDataList = areaDataMap.get(varDefine);
			} else {
				varDataList = new ArrayList<VariableAppoint>();
				areaDataMap.put(varDefine, varDataList);
			}
			for (CellDefine cellDefine : rowDefine.getCellDefineList()) {
				isFinished = impCellValue(varDataList, areaInfo, cellDefine);
				if(isFinished == true){
					
					break;
				}
			}
			if(isFinished == true){
				break;
			}
		}

		if (isFinished == true) {
			return isFinished;
		}

		// 创建变量并设值
		Map<String, Object> outData = this.getDataContext().getOutData();

		VariableDefine areaVarDefine = areaVarAppoint.getVariableDefine();
		List<VariableAppoint> areaVarDataList = areaDataMap.get(areaVarDefine);

		if (areaVarDataList != null) {
			areaVarAppoint.createNewObject(outData);
			for (VariableAppoint varAppoint : areaVarDataList) {
				varAppoint.putValue(outData);
			}
		}

		for (VariableDefine varDefineKey : areaDataMap.keySet()) {
			if (!varDefineKey.equals(areaVarDefine)) {
				List<VariableAppoint> varDataList = areaDataMap
						.get(areaVarDefine);
				if (varDataList != null) {
					varDefineKey.createNewObject(outData);
					for (VariableAppoint varAppoint : varDataList) {
						varAppoint.putValue(outData);
					}
				}
			}
		}
		
		return isFinished;
	}
	
	private boolean impDICols(Map<VariableDefine, List<VariableAppoint>> areaDataMap,
			AreaInfo areaInfo,DataItemInfo currDIPosition) {
		boolean isFinished = false;	

		AreaDefine areaDefine = areaInfo.getAreaDefine();
		if (areaDefine.getColumnDefineList() == null) {
			isFinished = true;
			return isFinished;
		}
		VariableAppoint repeatVarAppoint = sheetDefine.getRepeatByVar();
		VariableAppoint elementVarAppoint = sheetDefine.getSheetVar();
		VariableAppoint areaVarAppoint = areaDefine.getAreaVarAppoint();
		if (areaVarAppoint == null) {
			if (elementVarAppoint != null) {
				areaVarAppoint = elementVarAppoint;
			} else if (repeatVarAppoint != null) {
				areaVarAppoint = repeatVarAppoint;
			}
		}

		for (ColumnDefine colDefine : areaDefine.getColumnDefineList()) {
			VariableAppoint colVarAppoint = colDefine.getVarAppoint();
			if (colVarAppoint == null) {
				colVarAppoint = areaVarAppoint;
			}
			VariableDefine varDefine = colVarAppoint.getVariableDefine();
			List<VariableAppoint> varDataList = null;
			if (areaDataMap.containsKey(varDefine)) {
				varDataList = areaDataMap.get(varDefine);
			} else {
				varDataList = new ArrayList<VariableAppoint>();
				areaDataMap.put(varDefine, varDataList);
			}
			for (CellDefine cellDefine : colDefine.getCellDefineList()) {
				isFinished = impCellValue(varDataList, areaInfo, cellDefine);
				if(isFinished == true){
					break;
				}
			}
			if(isFinished == true){
				break;
			}
		}

		if (isFinished == true) {
			return isFinished;
		}

		// 创建变量并设值
		Map<String, Object> outData = this.getDataContext().getOutData();

		VariableDefine areaVarDefine = areaVarAppoint.getVariableDefine();
		List<VariableAppoint> areaVarDataList = areaDataMap.get(areaVarDefine);

		if (areaVarDataList != null) {
			areaVarAppoint.createNewObject(outData);
			for (VariableAppoint varAppoint : areaVarDataList) {
				varAppoint.putValue(outData);
			}
		}

		for (VariableDefine varDefineKey : areaDataMap.keySet()) {
			if (!varDefineKey.equals(areaVarDefine)) {
				List<VariableAppoint> varDataList = areaDataMap
						.get(areaVarDefine);
				if (varDataList != null) {
					varDefineKey.createNewObject(outData);
					for (VariableAppoint varAppoint : varDataList) {
						varAppoint.putValue(outData);
					}
				}
			}
		}
				
		return isFinished;
	}
	
	private boolean impCellValue(List<VariableAppoint> varDataList, AreaInfo areaInfo, CellDefine cellDefine){
		boolean isFinished = false;
		
		AreaDefine areaDefine = areaInfo.getAreaDefine();
		Position diPosition = areaDefine.getAreaOriginPosition().getDataPositionActual();
		
		AreaConfig areaConfig = areaInfo.getAreaConfig();
		
		Position cellPosition = new Position();
		int rowNo = diPosition.getRow() + cellDefine.getRelativeRow();
		int colNo = diPosition.getColumn()+ cellDefine.getRelativeCol();
		cellPosition.setRow(rowNo);
		cellPosition.setColumn(colNo);
		
		boolean isBegSameAsEnd = areaDefine.getRangePosition().isSameRow();
		String fillModel = areaDefine.getFillModel();
		if(PoiConstant.FILL_MODEL_ROW.equalsIgnoreCase(fillModel) && areaConfig != null){
			int dataEndRow = areaConfig.getRangePosition().getEndRow()
					+ areaDefine.getDataEnd();
			if (!isBegSameAsEnd && dataEndRow > 0 && rowNo > dataEndRow) {
				isFinished = true;
				log.debug("FillModel="+ fillModel + ">> dataEndRow=" + dataEndRow + ","
						+ "rowNo=" + rowNo + "; isFinished");
				return isFinished;
			}
		}
		if(PoiConstant.FILL_MODEL_COLUMN.equalsIgnoreCase(fillModel) && areaConfig != null){
			int dataEndCol = areaConfig.getRangePosition().getEndCol()
					+ areaDefine.getDataEnd();
			if (!isBegSameAsEnd && dataEndCol > 0 && colNo > dataEndCol) {
				isFinished = true;
				log.debug("FillModel="+ fillModel + ">> dataEndCol=" + dataEndCol + ","
						+ "colNo=" + colNo + "; isFinished");
				return isFinished;
			}
		}

		VariableAppoint cellVarAppoint = cellDefine.getPropertyVar();
		Object cellValue = readCellValue(cellPosition);
		
		log.debug(cellVarAppoint.dumpStr() + ">> Position[" + cellPosition.getRow() + ","
				+ cellPosition.getColumn() + "]" + cellValue);

		if ((cellValue == null || cellValue.toString() == "")
				&& PoiConstant.NOT_NULL_FLAG.equalsIgnoreCase(cellDefine
						.getNotNull())) {
			isFinished = true;
			log.debug(cellVarAppoint.dumpStr() + ">> Position[" + cellPosition.getRow() + ","
					+ cellPosition.getColumn() + "]" + cellValue + "; isFinished");
			return isFinished;
		} else {
			cellVarAppoint.setValue(cellValue);
			varDataList.add(cellVarAppoint);
		}		
		
		return isFinished;
	}

	private Object readCellValue(Position position) {
		Object cellValue = null;
		int excelRowNo = position.getRow() - 1;
		int excelColNo = position.getColumn() - 1;

		Sheet sheet = this.impSheet;
		Row row = sheet.getRow(excelRowNo);
		if (row == null) {
			return cellValue;
		}
		Cell cell = row.getCell(excelColNo);
		if (cell == null) {
			return cellValue;
		}
		cellValue = ExcelUtil.readCellValue(cell);
		
		boolean isParameter = false;
		if(cellValue != null){
			isParameter = AiStringUtil.checkPrefix(cellValue.toString(), "$[");
			if(isParameter == true){
				cellValue = null;
			}
		}
		return cellValue;
	}

	private void importInitialize() {
	}

	private void importTerminate() {
	}

	// getter/setter
	public SheetConfig getSheetConfig() {
		return sheetConfig;
	}

	public void setSheetConfig(SheetConfig sheetConfig) {
		this.sheetConfig = sheetConfig;
	}

	public SheetDefine getSheetDefine() {
		return sheetDefine;
	}

	public void setSheetDefine(SheetDefine sheetDefine) {
		this.sheetDefine = sheetDefine;
	}

	public Workbook getImpWorkbook() {
		return impWorkbook;
	}

	public void setImpWorkbook(Workbook impWorkbook) {
		this.impWorkbook = impWorkbook;
	}

	public Sheet getImpSheet() {
		return impSheet;
	}

	public void setImpSheet(Sheet impSheet) {
		this.impSheet = impSheet;
	}

	public IDataContext getDataContext() {
		return dataContext;
	}

	public void setDataContext(IDataContext dataContext) {
		this.dataContext = dataContext;
	}

	public IDataOper getDataOper() {
		return dataOper;
	}

	public void setDataOper(IDataOper dataOper) {
		this.dataOper = dataOper;
	}
}
