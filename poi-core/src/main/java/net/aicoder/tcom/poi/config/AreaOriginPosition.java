package net.aicoder.tcom.poi.config;

public class AreaOriginPosition {
	private Position areaPositionDefine; //区域开始坐标(定义)，定义所对应Sheet的绝对位置，子元素所引用的相对位置的参考原点	
	private Position dataPositionDefine; //数据项开始坐标(定义)，定义所对应Sheet的绝对位置，子元素所引用的相对位置的参考原点	

	private Position areaPositionActual; //区域开始坐标(实际)，定义所对应Sheet的绝对位置，子元素所引用的相对位置的参考原点	
	private Position dataPositionActual; //数据项开始坐标(实际)，定义所对应Sheet的绝对位置，子元素所引用的相对位置的参考原点	

	public Position getAreaPositionDefine() {
		return areaPositionDefine;
	}
	public void setAreaPositionDefine(Position areaPositionDefine) {
		this.areaPositionDefine = areaPositionDefine;
	}
	public Position getAreaPositionActual() {
		return areaPositionActual;
	}
	public void setAreaPositionActual(Position areaPositionActual) {
		this.areaPositionActual = areaPositionActual;
	}
	public Position getDataPositionDefine() {
		return dataPositionDefine;
	}
	public void setDataPositionDefine(Position dataPositionDefine) {
		this.dataPositionDefine = dataPositionDefine;
	}

	public Position getDataPositionActual() {
		return dataPositionActual;
	}
	public void setDataPositionActual(Position dataPositionActual) {
		this.dataPositionActual = dataPositionActual;
	}
}
