package com.shamming.hardware;

import com.shamming.software.JobIn_thread;

public class Clock_thread implements Runnable {

    public static int COUNTTIME; //the real-time system clock

    @Override
    public void run() {
        while (true) {
           try {
               Thread.sleep(300);
           } catch (InterruptedException e) {
               throw new RuntimeException(e);
           } // the sleep code should be outside the lock code
           synchronized (Clock_thread.class) {
               COUNTTIME += 1;
               //System.out.println("\n" + COUNTTIME);
               synchronized (JobIn_thread.class) {
                   JobIn_thread.impulse = true;
               }
           }
        }
    }
}
