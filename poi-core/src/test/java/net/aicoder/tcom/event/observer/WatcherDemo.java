package net.aicoder.tcom.event.observer;

import java.util.Observable;
import java.util.Observer;

public class WatcherDemo implements Observer {
    @Override
    public void update(Observable o, Object arg) {
        if(arg.toString().equals("openWindows")){
            System.out.println("已经打开窗口");
        }
    }
}