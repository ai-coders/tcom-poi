package net.aicoder.tcom.poi.config;

/**
 * 区域配置，这是依据当前Sheet中配置参数而获取的数据
 * @author StoneShi
 *
 */
public class AreaConfig {
	private int index;
	
	private String id; //_TBL_	TH
	private String name;
	private String type;
	private String fillModel; //区域填充类型	CELL
	private String beginCell; //起始单元格	ID
	private String endCell; //结束单元格	0
	private String range;
	private String notNull; //非空检查列
	private int titleBegin; //标题起始行(相对起始位)
	private int titleNum; //标题行数
	private int tplBegin; //模板记录行
	private int tplNum; //模板记录行数
	private int dataBegin; //数据起始行(相对起始位)
	private int dataEnd; //数据结束行(相对结束位)
	private String defaultVariable; //缺省数据对象名
	
	private RangePosition rangePosition;
	//private AreaDefine areaDefine;

	// construct
	public AreaConfig(){
		super();
	}

	public void resetAreaPosition(){
		rangePosition = new RangePosition(beginCell,endCell);
	}

	// setter/getter
	public int getIndex() {
		return index;
	}
	public void setIndex(int index) {
		this.index = index;
	}
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	public String getFillModel() {
		return fillModel;
	}
	public void setFillModel(String fillModel) {
		this.fillModel = fillModel;
	}
	public String getBeginCell() {
		return beginCell;
	}
	public void setBeginCell(String beginCell) {
		this.beginCell = beginCell;
	}
	public String getEndCell() {
		return endCell;
	}
	public void setEndCell(String endCell) {
		this.endCell = endCell;
	}
	public String getRange() {
		return range;
	}
	public void setRange(String range) {
		this.range = range;
	}
	public int getTitleBegin() {
		return titleBegin;
	}
	public void setTitleBegin(int titleBegin) {
		this.titleBegin = titleBegin;
	}
	public int getTitleNum() {
		return titleNum;
	}
	public void setTitleNum(int titleNum) {
		this.titleNum = titleNum;
	}
	public int getDataBegin() {
		return dataBegin;
	}
	public void setDataBegin(int dataBegin) {
		this.dataBegin = dataBegin;
	}
	public int getDataEnd() {
		return dataEnd;
	}
	public void setDataEnd(int dataEnd) {
		this.dataEnd = dataEnd;
	}
	public String getNotNull() {
		return notNull;
	}
	public void setNotNull(String notNull) {
		this.notNull = notNull;
	}
	public int getTplBegin() {
		return tplBegin;
	}
	public void setTplBegin(int tplBegin) {
		this.tplBegin = tplBegin;
	}
	public int getTplNum() {
		return tplNum;
	}
	public void setTplNum(int tplNum) {
		this.tplNum = tplNum;
	}
	public String getDefaultVariable() {
		return defaultVariable;
	}
	public void setDefaultVariable(String defaultVariable) {
		this.defaultVariable = defaultVariable;
	}

	public RangePosition getRangePosition() {
		return rangePosition;
	}
	public void setRangePosition(RangePosition areaPosition) {
		this.rangePosition = areaPosition;
	}
}
