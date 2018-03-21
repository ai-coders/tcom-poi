package net.aicoder.tcom.event.listener;

public interface EventListener extends java.util.EventListener {
    //事件处理
    public void handleEvent(EventObject event);
}