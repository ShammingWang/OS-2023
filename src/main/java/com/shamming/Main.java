package com.shamming;

import com.shamming.hardware.Clock_thread;
import com.shamming.software.JobIn_thread;
import com.shamming.software.ProcessScheduling_thread;

public class Main {
    public static void main(String[] args) {
        //System.out.println("hello world");
        //Clock_thread clk = new Clock_thread();
        new Thread(new Clock_thread()).start(); // launch the clock thread
        new Thread(new JobIn_thread()).start();
        new Thread(new ProcessScheduling_thread()).start();
    }
}
