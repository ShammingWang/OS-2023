package com.shamming.util;

public class Syn {
    int s = 0;
    public Syn() {
        //default 0
    }

    public Syn(int s) {
        this.s = s;
    }

    public synchronized void P() { // object lock
        s--;
        if (s < 0) {
            try {
                this.wait();
            } catch (Exception e) {
                System.out.println("error");
            }
        }
    }

    public synchronized void V() { // object lock
        s++;
        if (s <= 0) {
            this.notify();
        }
    }
}