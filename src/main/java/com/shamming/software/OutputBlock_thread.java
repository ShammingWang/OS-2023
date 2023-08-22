package com.shamming.software;

import com.shamming.entity.PCB;
import com.shamming.hardware.CPU2023;
import com.shamming.hardware.Clock_thread;
import com.shamming.hardware.Memory;
import com.shamming.util.Output;

public class OutputBlock_thread implements Runnable {
    @Override
    public void run() {
        int lastTime = Clock_thread.COUNTTIME;
        synchronized (Clock_thread.class) {
            lastTime = Clock_thread.COUNTTIME;
        }
        int pastTime = 0;
        while(true) {
            synchronized (Clock_thread.class) {
               if(lastTime < Clock_thread.COUNTTIME) {
                   lastTime = Clock_thread.COUNTTIME;
                   pastTime += 1;
               }
            }
            if(pastTime >= 2) {
                synchronized (CPU2023.class) {
                    if(!CPU2023.isCloseInterrupted) {
                        synchronized (Memory.class) {
                            PCB pcb = Memory.outputBlockQueue.poll();
                            //Output.log(pcb.ProID.toString());
                            assert pcb != null;
                            Memory.readyQueue.add(pcb);
                            String s = "重新进入就绪队列:" + pcb.ProID + "," + pcb.getRestInstructionNum();
                            Output.print(s);
                        }
                        //ProcessScheduling_thread.low_level_scheduling(); // at least once
                        break;
                    }
                }
            }
        }
    }
}
