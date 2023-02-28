package com.shamming.software;

import com.shamming.hardware.Clock_thread;

public class JobIn_thread implements Runnable {  // the thread of job request query class
    private int lastTime = -1;
    @Override
    public void run() {
        while (true) {
            synchronized (Clock_thread.class) {
                if(lastTime < Clock_thread.COUNTTIME) { // sense the system time change
                    lastTime = Clock_thread.COUNTTIME;  //
                    if(lastTime % 10 == 0) {
                        CheckJob();
                    }
                }
            }
        }
    }
    private void CheckJob() { //job request judge function
        System.out.println("check whether there are new jobs requesting in");
    }
}
