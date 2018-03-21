package net.aicoder.tcom.event.observer;

import java.util.Observable;
import java.util.Observer;

import org.junit.Test;

public class ObserverTest {
	@Test
	public void testObserver() {
		Watched watched = new Watched();
		WatcherDemo watcherDemo = new WatcherDemo();
		watched.addObserver(watcherDemo);

		watched.addObserver(new Observer() {
			@Override
			public void update(Observable o, Object arg) {
				if (arg.toString().equals("closeWindows")) {
					System.out.println("已经关闭窗口");
				}
			}
		});
		// 触发打开窗口事件，通知观察者
		watched.notifyObservers("openWindows");
		// 触发关闭窗口事件，通知观察者
		watched.notifyObservers("closeWindows");

	}
}
