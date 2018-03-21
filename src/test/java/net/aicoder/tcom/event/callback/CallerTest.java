package net.aicoder.tcom.event.callback;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

@SuppressWarnings("unused")
public class CallerTest {
	@Test
	public void testCaller() {
		Caller call = new Caller();
		call.call(new ICallBack() {

			@Override
			public void callBack() {
				System.out.println("终于回调成功了！");

			}
		});
	}

}
