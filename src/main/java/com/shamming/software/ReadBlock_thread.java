package com.shamming.software;

import com.shamming.entity.PCB;
import com.shamming.hardware.CPU2023;
import com.shamming.hardware.Clock_thread;
import com.shamming.hardware.Memory;
import com.shamming.util.Output;

public class ReadBlock_thread implements Runnable{ // HardDisk to buffer to pcb
    @Override
    public void run() {
        synchronized (Memory.class) {
            int startTime;
            synchronized (Clock_thread.class) {
                startTime = Clock_thread.COUNTTIME;
            }
            while(true) {
                int currentTime;
                synchronized (Clock_thread.class) {
                    currentTime = Clock_thread.COUNTTIME;
                }
                if(currentTime - startTime >= 2) { // have past 2 seconds
                    Output.bufferPrint("V操作(mutex)");
                    PCB process = Memory.readBlockQueue.poll();
                    Memory.readyQueue.add(process);
                    synchronized (CPU2023.class) {
                        CPU2023.isCloseInterrupted = false; // open the interruption
                    }
                    break;
                }
            }
        }
    }
}
