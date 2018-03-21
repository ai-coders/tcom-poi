package net.aicoder.tcom.poi.excel.event;

import java.util.Vector;
//import java.util.EventObject;

public class EventEngine {

	// 监听器列表，监听器的注册则加入此列表
	private Vector<EventListener> listenerList = new Vector<EventListener>();

	// 注册监听器
	public void addListener(EventListener eventListener) {
		listenerList.add(eventListener);
	}

	// 撤销注册
	public void removeListener(EventListener eventListener) {
		listenerList.remove(eventListener);
	}

	// 接受外部事件
	public void fireEvents(EventObject event) {
		for (EventListener eventListener : listenerList) {
			eventListener.handleEvent(event);
		}
	}
}
