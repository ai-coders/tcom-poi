package net.aicoder.tcom.event.callback;

public class Caller {

    public void call(ICallBack callBack){
        System.out.println("start...");
        callBack.callBack();
        System.out.println("end...");
    }

}