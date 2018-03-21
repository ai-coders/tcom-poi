package net.aicoder.tcom.event.listener;

import org.junit.Test;

public class EventTest {
	
	@Test
	public void testEvent() {
		EventSource eventSource = new EventSource();

		eventSource.addListener(new EventListener() {
			@Override
			public void handleEvent(EventObject event) {
				event.doEvent();
				if (event.getSource().equals("closeWindows")) {
					System.out.println("doClose");
				}
			}

		});

		/*
		 * 传入openWindows事件，通知listener，事件监听器， 对open事件感兴趣的listener将会执行
		 *
		 */
		eventSource.fireListenerEvents(new EventObject("openWindows"));
	}
}
