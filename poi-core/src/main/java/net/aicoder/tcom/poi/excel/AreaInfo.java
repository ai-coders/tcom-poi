package net.aicoder.tcom.poi.excel;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.util.CellRangeAddress;

import net.aicoder.tcom.poi.PoiConstant;
import net.aicoder.tcom.poi.config.AreaConfig;
import net.aicoder.tcom.poi.config.AreaDefine;
import net.aicoder.tcom.poi.config.Position;
import net.aicoder.tcom.poi.excel.event.EventListener;
import net.aicoder.tcom.poi.excel.event.EventObject;

/**
 * 区域信息，在Sheet导入、导出时记录区域位置、相互间位置参考关系等信息
 * 
 * 在导入、导出时有：<br>
 * 配置信息：由AreaDefine + AreaConfig来确定当前区域的情况 <br>
 * 实际信息：导入、导出时实际的信息<br>
 * 
 * @author StoneShi
 *
 */
public class AreaInfo implements EventListener{
	private static final Log log = LogFactory.getLog(AreaInfo.class);

	private AreaDefine areaDefine;
	private AreaConfig areaConfig;
	
	private List<Row> tplRowList = null; // 导出区域模板所对应的行清单
	private List<CellRangeAddress> tplMergedRegionList = null; // 当前模板合并单元格的清单
	
	// construct
	public AreaInfo(){
		super();
	}

	public AreaInfo(AreaDefine areaDefine, AreaConfig areaConfig){
		super();
		setAreaDefine(areaDefine);
		setAreaConfig(areaConfig);
	}
	
	public void handleEvent(EventObject event){
		DataItemInfo position = null;
		Object eventObject = event;
		if(eventObject instanceof DataItemInfo ){
			position = (DataItemInfo) eventObject;
			shiftAreaRows(position);
		}
	}
	
	private void shiftAreaRows(DataItemInfo position){
		Position areaPositionActual = this.areaDefine.getAreaOriginPosition().getAreaPositionActual();
		Position dataPositionActual = this.areaDefine.getAreaOriginPosition().getDataPositionActual();
		
		log.debug("shiftAreaRows >> id=" + this.getId()
		+ "areaPosition Before: " + areaPositionActual.toString()
		+ "; dataPosition Before: " + dataPositionActual.toString()
		);
		
		int currRowNo = position.getDIPosition().getRow();
		int shiftRowNum = position.getShiftRowNum();
		
		int areaRow = areaPositionActual.getRow();
		if (areaRow > currRowNo) {
			areaPositionActual.setRow(areaRow + shiftRowNum);
		}
		int dataRow = dataPositionActual.getRow();
		if (dataRow > currRowNo) {
			dataPositionActual.setRow(dataRow + shiftRowNum);
		}
		log.debug("shiftAreaRows >> id=" + this.getId()
		+ "areaPosition  After: " + areaPositionActual.toString()
		+ "; dataPosition  After: " + dataPositionActual.toString()
		);
		
	}

	public void loadTpl(Sheet tplSheet) {
		String fillModel = this.areaDefine.getFillModel();
		if(fillModel!=null){
			fillModel = fillModel.toUpperCase();
		}
		switch (fillModel) {
		case PoiConstant.FILL_MODEL_AUTO:
			break;
		case PoiConstant.FILL_MODEL_ROW:
			loadTplRows(tplSheet);
			break;
		case PoiConstant.FILL_MODEL_COLUMN:
			break;
		default: // FILL_MODEL_FIXED
			break;
		}
	}
	
	private void loadTplRows(Sheet tplSheet) {
		AreaDefine area = this.areaDefine;
		
		int tplRow = getTplBegin();
		int tplRowNum = area.getTplNum();
		
		if (tplSheet == null || tplRow == 0) {
			return;
		}
		if (area.getRangePosition().isSameRow()) {
			return;
		}

		this.tplRowList = new ArrayList<Row>();

		int rowIdx; // row index = template row no - 1
		for (int idx = 0; idx < tplRowNum; idx++) {
			rowIdx = tplRow - 1 + idx; // row index = template row no - 1
			this.tplRowList.add(tplSheet.getRow(rowIdx));
		}

		int begRow = tplRow - 1;
		int endRow = begRow + area.getTplNum() - 1;

		this.tplMergedRegionList = new ArrayList<CellRangeAddress>();

		int sheetMergerCount = tplSheet.getNumMergedRegions();
		for (int i = 0; i < sheetMergerCount; i++) {
			CellRangeAddress mergedRegionAt = tplSheet.getMergedRegion(i);
			if (mergedRegionAt.getFirstRow() >= begRow && mergedRegionAt.getLastRow() <= endRow) {
				int firstRow = mergedRegionAt.getFirstRow() - begRow; 
				int lastRow = mergedRegionAt.getLastRow() - begRow; 
				int firstCol = mergedRegionAt.getFirstColumn(); 
				int lastCol  = mergedRegionAt.getLastColumn();
				this.tplMergedRegionList.add(new CellRangeAddress(firstRow,lastRow,firstCol,lastCol));
			}
		}
	}
	
	private int getTplBegin(){
		int tplBegin = 0;
		if(areaConfig != null){
			tplBegin = areaConfig.getRangePosition().getBegRow() + areaDefine.getTplBegin();
		}else{
			tplBegin = areaDefine.getRangePosition().getBegRow() + areaDefine.getTplBegin();
		}
		return tplBegin;
	}

	// getter/setter
	public String getId() {
		if(areaDefine == null){
			return null;
		}else{
			return this.areaDefine.getId();			
		}
	}

	public AreaDefine getAreaDefine() {
		return areaDefine;
	}

	public void setAreaDefine(AreaDefine aeraDefine) {
		this.areaDefine = aeraDefine;
	}

	public AreaConfig getAreaConfig() {
		return areaConfig;
	}

	public void setAreaConfig(AreaConfig areaConfig) {
		this.areaConfig = areaConfig;
	}

	public List<Row> getTplRowList() {
		return tplRowList;
	}

	public void setTplRowList(List<Row> tplRowList) {
		this.tplRowList = tplRowList;
	}

	public List<CellRangeAddress> getTplMergedRegionList() {
		return tplMergedRegionList;
	}

	public void setTplMergedRegionList(List<CellRangeAddress> tplMergedRegionList) {
		this.tplMergedRegionList = tplMergedRegionList;
	}

	public void setAiDefine(AreaInfo aiDefine) {
		setAreaDefine(aiDefine.getAreaDefine());
		setAreaConfig(aiDefine.getAreaConfig());
		
		setTplRowList(aiDefine.getTplRowList());
		setTplMergedRegionList(aiDefine.getTplMergedRegionList());
	}

}
