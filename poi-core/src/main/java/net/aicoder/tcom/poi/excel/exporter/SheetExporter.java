package net.aicoder.tcom.poi.excel.exporter;

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
import org.apache.poi.ss.util.CellRangeAddress;

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
import net.aicoder.tcom.poi.excel.event.EventEngine;
import net.aicoder.tcom.poi.excel.writer.DataWriter;
import net.aicoder.tcom.poi.util.ExcelUtil;
import net.aicoder.tcom.tools.util.AiStringUtil;

public class SheetExporter {
	private static final Log log = LogFactory.getLog(SheetExporter.class);

	private SheetDefine sheetDefine; //依据xml配置文件的设定而产生的配置
	private SheetConfig tplSheetConfig; //模板SheetConfig,从Sheet的配置栏位中直接读取
	
	private Workbook outWorkbook; //输出工作簿
	private Sheet tplSheet; //模板Sheet
	private Sheet outSheet; //当前输出的Sheet

	private Object repeatDataItem; //重复输出Sheet所依据的对象（List中的单个元素）
	private int repeatIndex = -1; //当前Sheet对应（List中的单个元素的序号）

	private IDataContext dataContext; //上下文，包括输出数据OutData
	private IDataOper dataOper; //数据导出的操作对象，可以通过Spring的配置注入
	
	private Map<String, AreaInfo> areaInfoMap; //实际的区域信息
	private EventEngine eventEngine;	

	// construct
	public SheetExporter() {
		super();
	}

	public SheetExporter(SheetDefine sheetDefine) {
		super();
		this.sheetDefine = sheetDefine;
	}

	public void doExport(IDataContext dataContext) {
		setDataContext(dataContext);

		doExport();
	}

	public void doExport(IDataContext dataContext, Object repeatData,
			int repeatIndex) {
		setDataContext(dataContext);
		setRepeatData(repeatData);
		setRepeatIndex(repeatIndex);

		doExport();
	}

	public void doExport() {
		// 开始时间
		Long begTime = System.currentTimeMillis();
		log.debug("Export sheet begin:" + new Date(begTime));

		if (dataContext == null) {
			dataContext = new DefaultDataContext();
		}
		
		VariableAppoint elementNameVar = sheetDefine.getSheetVar();
		if (elementNameVar != null
				&& elementNameVar.getVariableDefine() != null) {
			String key = elementNameVar.getVariableDefine().getVarName();
			dataContext.putOneOutData(key, repeatDataItem); //repeatData为List中的单个元素数值
		}

		if (dataOper != null) {
			dataOper.setDataContext(dataContext);
			dataOper.preProduce();
		}

		exportInitialize();

		if (dataOper != null) {
			dataOper.setDataContext(dataContext);
			dataOper.produce();
		}
		
		resetAreaDefine();
		
		for (int areaIndex = 0; areaIndex < sheetDefine.getAreaDefineList()
				.size(); areaIndex++) {
			AreaDefine areaDefine = sheetDefine.getAreaDefineList().get(
					areaIndex);
			log.debug("Export Area>> index=" + areaIndex + ";id="
					+ areaDefine.getId());
			String areaId = areaDefine.getId();
			AreaInfo areaInfo = this.areaInfoMap.get(areaId);

			exportArea(areaInfo, repeatDataItem);
		}

		exportTerminate();

		if (dataOper != null) {
			dataOper.setDataContext(dataContext);
			dataOper.postProduce();
		}

		// 结束时间
		Long endTime = System.currentTimeMillis();
		log.debug("Export sheet end:" + new Date(endTime) + ";Run time:"
				+ (endTime - begTime));
	}
	
	private void resetAreaDefine(){
		 eventEngine = new EventEngine();
		 
		for(Map.Entry<String, AreaInfo> entry : this.areaInfoMap.entrySet()){
			AreaInfo areaInfo = entry.getValue();
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
			+ "AreaPositionActual =" + areaPosition.toString() 
			+ "DataPositionActual =" + dataPosition.toString());
			
			eventEngine.addListener(areaInfo);
			log.debug("addListener >>" + areaInfo.getId());
		}
	}
	
	private void exportArea(AreaInfo areaInfo, Object elderData){
		AreaDefine areaDefine = areaInfo.getAreaDefine();
		
		String fillModel = areaDefine.getFillModel();
		if (fillModel != null) {
			fillModel = fillModel.toUpperCase();
		}
		log.debug("Begin exportArea>> areaId = " + areaDefine.getId() + "; fillModel=" + fillModel);
		
		if(PoiConstant.FILL_MODEL_FIXED.equals(fillModel)){
			expFixedArea(areaInfo, elderData);
		}else{
			expDynamicArea(areaInfo, elderData);
		}
	}

	private void expFixedArea(AreaInfo areaInfo, Object elderData) {
		AreaDefine areaDefine = areaInfo.getAreaDefine();
		
		if(areaDefine.getRowDefineList() == null){
			return;
		}

		Object elderVarData = null;
		VariableDefine elderVarDefine = null;
		VariableAppoint varAppoint = areaDefine.getAreaVarAppoint();
		if (varAppoint != null) {
			elderVarData = this.areaOutData(areaDefine, elderData);
			elderVarDefine = varAppoint.getVariableDefine();
		} else {
			elderVarData = repeatDataItem;
			VariableAppoint elementNameVar = sheetDefine.getSheetVar();
			VariableAppoint repeatByVar = sheetDefine.getRepeatByVar();
			if (elementNameVar != null) {
				elderVarDefine = elementNameVar.getVariableDefine();
			} else if (repeatByVar != null) {
				elderVarDefine = repeatByVar.getVariableDefine();
			}
		}

		Map<String, Object> outData = this.getDataContext().getOutData();
		for (int rowIdx = 0; rowIdx < areaDefine.getRowDefineList().size(); rowIdx++) {
			RowDefine rowDefine = areaDefine.getRowDefineList().get(rowIdx);
			for (CellDefine cellDefine : rowDefine.getCellDefineList()) {
				VariableAppoint propertyVar = cellDefine.getPropertyVar();
				Object propValue = propertyVar.dataValue(outData, elderVarData,
						elderVarDefine);
				writeCellValue(propValue, areaInfo, cellDefine);
			}
		}		
	}
	
	private void expDynamicArea(AreaInfo areaInfo, Object elderData){
		AreaDefine areaDefine = areaInfo.getAreaDefine();
		List<Object> areaOutDataList = areaOutDataList(areaDefine, elderData);
		
		if (areaOutDataList == null) {
			return;
		}
		
		String fillModel = areaDefine.getFillModel();
		if (fillModel != null) {
			fillModel = fillModel.toUpperCase();
		}
		
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
			
			currDataPositionActual.setRow(currDIRow);
			currDataPositionActual.setColumn(currDICol);
			log.debug("expDynamicArea>> currDataPositionActual=" + currDataPositionActual.toString());
		}
		DataItemInfo currDIPosition = new DataItemInfo(areaInfo);
		Position diPositionActual = currAreaOriginPosition.getDataPositionActual();
		
		int dataNum = areaOutDataList.size();
		for (int dataIdx = 0; dataIdx < dataNum; dataIdx++) {
			Object oneData = areaOutDataList.get(dataIdx);
			log.debug("expDynamicArea>> dataIndex=" + dataIdx + ";oneData="
					+ oneData + ";diPositionActual=" + diPositionActual.toString());
			currDIPosition.setDIIndex(dataIdx);
			currDIPosition.setDIPosition(new Position(diPositionActual));
			currDIPosition.setShiftRowNum(areaDefine.getShiftRowNum());
			
			expAreaByOneData(oneData,areaInfo,currDIPosition);
			
			if(PoiConstant.FILL_MODEL_ROW.equals(fillModel)){
				int nexDIRowNo = diPositionActual.getRow() + areaDefine.getDataRoCNum();
				diPositionActual.setRow(nexDIRowNo);
			}else{
				int nexDIColNo = diPositionActual.getColumn() + areaDefine.getDataRoCNum();
				diPositionActual.setColumn(nexDIColNo);
			}
		}
	}
	
	private void expAreaByOneData(Object oneData, AreaInfo areaInfo, DataItemInfo currDIPosition){
		AreaDefine areaDefine = areaInfo.getAreaDefine();
		String fillModel = areaDefine.getFillModel();
		if (fillModel != null) {
			fillModel = fillModel.toUpperCase();
		}
		log.debug("Begin exportArea>> areaId = " + areaDefine.getId() + "; fillModel=" + fillModel);
		
		switch (fillModel) {
		case PoiConstant.FILL_MODEL_AUTO:
			break;
		case PoiConstant.FILL_MODEL_ROW:
			//expRowArea(areaInfo, elderData);
			expDIRows(oneData, areaInfo, currDIPosition);
			break;
		case PoiConstant.FILL_MODEL_COLUMN:
			//expColumnArea(areaInfo, elderData);
			fillDICols(oneData, areaInfo, currDIPosition);
			break;
		default: // FILL_MODEL_FIXED
			expFixedArea(areaInfo, oneData);
		}
		
		List<AreaDefine> subAreaDefineList = areaDefine.getSubAreaDefineList();
		if(subAreaDefineList != null){
			for (int areaIndex = 0; areaIndex < subAreaDefineList.size(); areaIndex++) {
				AreaDefine subArea = subAreaDefineList.get(areaIndex);
				String areaId = subArea.getId();
				AreaInfo subAreaInfo = this.areaInfoMap.get(areaId);
				exportArea(subAreaInfo, oneData);
			}
		}
	}
	
	/**
	 * 
	 * @param oneData
	 * @param areaInfo
	 */
	private void expDIRows(Object oneData, AreaInfo areaInfo,DataItemInfo currDIPosition){
		if(oneData == null){
			return;
		}
		
		if(currDIPosition.getDIIndex() > 0){
			//依据模板的行数向下移动
			int currDIBegRow = currDIPosition.getDIPosition().getRow();
			int lastRowNum = outSheet.getLastRowNum();
			int shiftDIRowNum = currDIPosition.getShiftRowNum();
			if (shiftDIRowNum > 0) {
				outSheet.shiftRows(currDIBegRow - 1, lastRowNum, shiftDIRowNum);
			}
			eventEngine.fireEvents(currDIPosition);
			log.debug("expDIRows>> dataIndex=" + currDIPosition.getDIIndex() + ";currDIBegRow="
					+ currDIBegRow + ";lastRowNum=" + lastRowNum + ";shiftDIRowNum=" + shiftDIRowNum);
			
			//应用模板格式
			applyTemplate(oneData, areaInfo, currDIPosition);
		}
		
		//填充内容
		fillDIRows(oneData,areaInfo,currDIPosition);
	}
	
	private void applyTemplate(Object oneData, AreaInfo areaInfo,DataItemInfo currDIPosition) {
		AreaDefine area = areaInfo.getAreaDefine();

		if (area.getRangePosition().isSameRow()) {
			log.debug("applyTemplate>> isSameRow=" + area.getRangePosition().isSameRow());
			return;
		}

		applyTplMergedRegions(oneData, areaInfo, currDIPosition);
		applyTPlRows(oneData, areaInfo, currDIPosition);
	}

	private void applyTplMergedRegions(Object oneData, AreaInfo areaInfo,DataItemInfo currDIPosition) {
		//AreaDefine area = areaInfo.getAreaDefine();
		
		List<CellRangeAddress> currTplMergedRegionList = areaInfo.getTplMergedRegionList();
		Position currPosition = currDIPosition.getDIPosition();
		log.debug("applyTplMergedRegions>> currPosition=" +currPosition.toString() 
		+ ";currTplMergedRegionList num=" + currTplMergedRegionList.size());
		
		//int begRow = area.getTplBegin() - 1;
		int begRow = 0;
		for (CellRangeAddress tplRange : currTplMergedRegionList) {
			int firstRow = currPosition.getRow() - 1
					+ (tplRange.getFirstRow() - begRow);
			int lastRow = currPosition.getRow() - 1
					+ (tplRange.getLastRow() - begRow);
			int firstCol = tplRange.getFirstColumn();
			int lastCol = tplRange.getLastColumn();

			CellRangeAddress outRange = new CellRangeAddress(firstRow, lastRow,
					firstCol, lastCol);
			log.debug("CellRangeAddress=" + outRange.toString());
			outSheet.addMergedRegion(outRange);
		}
	}

	private void applyTPlRows(Object oneData, AreaInfo areaInfo,DataItemInfo currDIPosition) { // 未来每条数据对应多个模块Rows时需要扩充
		List<Row> currTplRowList = areaInfo.getTplRowList();
		Position currPosition = currDIPosition.getDIPosition();
				
		for (int index = 0; index < currTplRowList.size(); index++) {
			Row tplRow = currTplRowList.get(index);
			int outRowNo = currPosition.getRow() - 1 + index;
			Row outRow = outSheet.getRow(outRowNo);
			if (outRow == null) {
				outRow = outSheet.createRow(outRowNo);
			}
			log.debug("applyTPlRows>> currPosition=" + currPosition.toString() 
			+ "outRowNo=" +outRowNo);
			boolean isCopyValue = true;
			ExcelUtil.copyRow(outWorkbook, tplRow, outRow, isCopyValue);
		}
	}
	
	private void fillDIRows(Object oneData, AreaInfo areaInfo,DataItemInfo currDIPosition){
		AreaDefine areaDefine = areaInfo.getAreaDefine();
		
		if(areaDefine.getRowDefineList() == null || oneData == null){
			return;
		}
		Object elderVardata = oneData;
		VariableDefine elderVarDefine = null;
		if (areaDefine.getAreaVarAppoint() != null) {
			elderVarDefine = areaDefine.getAreaVarAppoint().getVariableDefine();
		}

		Map<String, Object> outData = this.getDataContext().getOutData();
		for (RowDefine rowDefine : areaDefine.getRowDefineList()) {
			for (CellDefine cellDefine : rowDefine.getCellDefineList()) {
				VariableAppoint propertyVar = cellDefine.getPropertyVar();
				Object propValue = propertyVar.dataValue(outData, elderVardata,
						elderVarDefine);
				writeCellValue(propValue, areaInfo, cellDefine);
			}
		}
	}

	private void fillDICols(Object oneData, AreaInfo areaInfo,DataItemInfo currDIPosition){
		AreaDefine areaDefine = areaInfo.getAreaDefine();
		
		if(areaDefine.getColumnDefineList() == null || oneData == null){
			return;
		}
		Object elderVardata = oneData;
		VariableDefine elderVarDefine = null;
		if (areaDefine.getAreaVarAppoint() != null) {
			elderVarDefine = areaDefine.getAreaVarAppoint().getVariableDefine();
		}

		Map<String, Object> outData = this.getDataContext().getOutData();
		for (ColumnDefine colDefine : areaDefine.getColumnDefineList()) {
			for (CellDefine cellDefine : colDefine.getCellDefineList()) {
				VariableAppoint propertyVar = cellDefine.getPropertyVar();
				Object propValue = propertyVar.dataValue(outData, elderVardata,
						elderVarDefine);
				writeCellValue(propValue, areaInfo, cellDefine);
			}
		}
	}
	
	private void writeCellValue(Object propValue, AreaInfo areaInfo, CellDefine cellDefine) {
		AreaDefine areaDefine = areaInfo.getAreaDefine();
		Position diPosition = areaDefine.getAreaOriginPosition().getDataPositionActual();

		Sheet sheet = this.outSheet;
		int rowNo = diPosition.getRow() + cellDefine.getRelativeRow() - 1;
		int columnNo = diPosition.getColumn()+ cellDefine.getRelativeCol() - 1;
		Row row = sheet.getRow(rowNo);
		if (row == null) {
			row = sheet.createRow(rowNo);
		}
		Cell cell = row.getCell(columnNo);
		if (cell == null) {
			cell = row.createCell(columnNo);
		}
		DataWriter dataWriter = new DataWriter(outWorkbook, outSheet, row, cell);
		String format = cellDefine.getPropertyVar().getVarFormat();
		dataWriter.writeValue(propValue, format);
		log.debug("writeCell>> row=" + rowNo + ";col=" + columnNo + ";value="
				+ propValue);
	}
	
	private Object areaOutData(AreaDefine area, Object elderData){
		Object dataObj = null;
		VariableAppoint areaDefaultVar = area.getAreaVarAppoint();
		if (areaDefaultVar != null) {
			//Object elderVardata = repeatDataItem;
			Object elderVardata = elderData;
			VariableDefine elderVarDefine = null;
			VariableAppoint elementNameVar = sheetDefine.getSheetVar();
			VariableAppoint repeatByVar = sheetDefine.getRepeatByVar();
			if (elementNameVar != null) {
				elderVarDefine = elementNameVar.getVariableDefine();
			} else if (repeatByVar != null) {
				elderVarDefine = repeatByVar.getVariableDefine();
			}
			Map<String, Object> outData = this.getDataContext().getOutData();
			dataObj = areaDefaultVar.dataValue(outData, elderVardata,
					elderVarDefine);
		}
		return dataObj;
	}
	
	@SuppressWarnings("unchecked")
	private List<Object> areaOutDataList(AreaDefine area, Object elderData) {
		List<Object> dataList = null;

		//Object dataObj = areaOutData(area);
		Object dataObj = areaOutData(area, elderData);
		if (dataObj != null && dataObj instanceof List) {
			dataList = (List<Object>) dataObj;
		}

		return dataList;
	}
	
	private void exportInitialize() {
		openOutSheet();
	}

	private void exportTerminate() {
		outSheet.setForceFormulaRecalculation(true);
	}
	
	public void loadTplSheet(){
		String tplSheetName = sheetDefine.getTplSheet();
		int tplSheetIndex = -100;

		if (AiStringUtil.isEmpty(tplSheetName)) {
			tplSheet = null;
		} else {
			tplSheet = outWorkbook.getSheet(tplSheetName);
			tplSheetIndex = outWorkbook.getSheetIndex(tplSheet);
			this.tplSheetConfig = new SheetConfig(tplSheet);
			initDefineAreaInfo();
		}
		
		log.debug("TplSheet>> index=" + tplSheetIndex + ";name =" + tplSheetName) ;
	}
	
	private void initDefineAreaInfo(){
		this.areaInfoMap = new HashMap<String, AreaInfo>();
		
		for (int areaIndex = 0; areaIndex < sheetDefine.getAreaDefineList()
				.size(); areaIndex++) {
			AreaDefine area = sheetDefine.getAreaDefineList().get(
					areaIndex);
			initAreaInfoByAreaDefine(area);
		}
	}
	
	private void initAreaInfoByAreaDefine(AreaDefine areaDefine){
		String areaId = areaDefine.getId();
		AreaConfig areaConfig = tplSheetConfig.getAreaConfig(areaId);
		
		AreaInfo areaInfo = new AreaInfo(areaDefine, areaConfig);
		areaInfo.loadTpl(tplSheet);
		this.areaInfoMap.put(areaId, areaInfo);
		
		List<AreaDefine> subAreaDefineList = areaDefine.getSubAreaDefineList();
		if(subAreaDefineList != null){
			for (int areaIndex = 0; areaIndex < subAreaDefineList.size(); areaIndex++) {
				AreaDefine subArea = subAreaDefineList.get(areaIndex);
				initAreaInfoByAreaDefine(subArea);
			}
		}
	}

	private void openOutSheet() {
		String tplSheetName = sheetDefine.getTplSheet();
		String outSheetName = outSheetName();
		int outSheetIndex = -100;
		if (tplSheet != null) {
			if(!AiStringUtil.isEmpty(outSheetName)){
				if(tplSheetName.toUpperCase().equals(outSheetName.toUpperCase())){
					outSheet = tplSheet;
				}else{
					int tplSheetIndex = outWorkbook.getSheetIndex(tplSheet);
					outSheet = outWorkbook.cloneSheet(tplSheetIndex);
					outSheetIndex = outWorkbook.getSheetIndex(outSheet);
					outWorkbook.setSheetName(outSheetIndex, outSheetName);
				}
			}
		} else {
			if (AiStringUtil.isEmpty(outSheetName)) {
				outSheet = outWorkbook.createSheet();
			} else {
				outSheetIndex = outWorkbook.getSheetIndex(outSheetName);
				if (outSheetIndex < 0) {
					outSheet = outWorkbook.createSheet(outSheetName);
				}
			}
		}
		log.debug("OutSheet>> index=" + outSheetIndex + ";name ="
				+ outSheetName);
	}

	private String outSheetName() {
		String sheetName = "";

		VariableAppoint repeatDataVar = sheetDefine.getRepeatByVar();
		VariableAppoint sheetNameVar = sheetDefine.getSheetNameVar();
		VariableAppoint sheetVar = sheetDefine.getSheetVar();

		Object elderObj = repeatDataItem;
		VariableDefine elderVarDefine = null;
		if (repeatDataVar != null) {
			elderVarDefine = repeatDataVar.getVariableDefine();
		}else if (sheetVar != null) {
			elderVarDefine = sheetVar.getVariableDefine();
		}

		sheetName = sheetNameVar.dataValue(elderObj, elderVarDefine).toString();
		log.debug("outSheetName ="+sheetName);

		return sheetName;
	}

	// getter/setter
	public SheetDefine getSheetDefine() {
		return sheetDefine;
	}

	public void setSheetDefine(SheetDefine sheetDefine) {
		this.sheetDefine = sheetDefine;
	}

	public Workbook getOutWorkbook() {
		return outWorkbook;
	}

	public void setOutWorkbook(Workbook outWorkbook) {
		this.outWorkbook = outWorkbook;
	}

	public Object getRepeatData() {
		return repeatDataItem;
	}

	public void setRepeatData(Object repeatData) {
		this.repeatDataItem = repeatData;
	}

	public int getRepeatIndex() {
		return repeatIndex;
	}

	public void setRepeatIndex(int repeatIndex) {
		this.repeatIndex = repeatIndex;
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