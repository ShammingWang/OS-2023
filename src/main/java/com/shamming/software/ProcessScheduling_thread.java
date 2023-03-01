package com.shamming.software;

import com.shamming.hardware.Clock_thread;

public class ProcessScheduling_thread implements Runnable{
    private int Times = 1;
    private int lastTime = -1; //used for sensing the system clock change
    @Override
    public void run() {
        while(true) {
            synchronized (Clock_thread.class) {
                if(lastTime < Clock_thread.COUNTTIME) { // sense the system time change
                    lastTime = Clock_thread.COUNTTIME;  //
                    Times -= 1; // reduce the rest time slice
                    if(Times == 0) {
                        System.out.println("the time slice runs out");
                        ProcessScheduling(); // use algorithm to schedule the process
                        Times = 3;
                    }
                }
            }
        }
    }

    private void ProcessScheduling() {

    }
}
