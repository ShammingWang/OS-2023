package com.shamming.hardware;

public class Clock_thread implements Runnable {

    public static int COUNTTIME; //the real-time system clock

    @Override
    public void run() {
        while (true) {
               try {
                   Thread.sleep(1000);
               } catch (InterruptedException e) {
                   throw new RuntimeException(e);
               } // the sleep code should be outside the lock code
               synchronized (Clock_thread.class) {
                   //System.out.println(Thread.currentThread().getName() + " has locked");
                   COUNTTIME += 1;
                   System.out.println(COUNTTIME);
                   //System.out.println(Thread.currentThread().getName() + " has unlocked");
               }
        }
    }
}
