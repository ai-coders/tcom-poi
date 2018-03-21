package net.aicoder.tcom.poi.excel;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import net.aicoder.tcom.poi.config.AreaConfig;
import net.aicoder.tcom.poi.config.AreaDefine;
import net.aicoder.tcom.poi.config.Position;
import net.aicoder.tcom.poi.excel.event.EventObject;

public class DataItemInfo extends EventObject{
	private static final Log log = LogFactory.getLog(DataItemInfo.class);
	private static final long serialVersionUID = 1L;
	
	private int diIndex =0;
	private Position diPosition;// = new Position();
	private int shiftRowNum = 0;

	public DataItemInfo(Object source){
		super(source);
	}
	
	public DataItemInfo(Object source, AreaDefine areaDefine, AreaConfig areaConfig){
		super(source);
	}
	
	public void doEvent(){
		Object source = this.getSource();
		log.debug("EventSoruce >>"+source.toString());
	}
	public int getDIIndex() {
		return diIndex;
	}

	public void setDIIndex(int currDIIndex) {
		this.diIndex = currDIIndex;
	}

	public Position getDIPosition() {
		return diPosition;
	}

	public void setDIPosition(Position currPosition) {
		this.diPosition = currPosition;
	}
	public int getShiftRowNum() {
		return shiftRowNum;
	}

	public void setShiftRowNum(int shiftRowNum) {
		this.shiftRowNum = shiftRowNum;
	}
}
