package com.shamming.software;

import com.shamming.entity.Buffer;
import com.shamming.entity.PCB;
import com.shamming.hardware.CPU2023;
import com.shamming.hardware.Clock_thread;
import com.shamming.hardware.Memory;
import com.shamming.util.MMU;
import com.shamming.util.Output;

public class ReadBuffer2Mem implements Runnable {

    @Override
    public void run() {
        //Output.log("开始从缓冲区写到PCB的线程");
        Buffer.currentProcess.data = Buffer.read();
        Output.bufferPrint("P操作(order1)", 1);
        synchronized (Buffer.class) {
            PCB process = Buffer.currentProcess;
            Integer L_Address = process.job.instructions.get(process.PC).L_Address; //
            Integer physicalAddress = MMU.physical(process.pageTable, L_Address);
            Output.bufferPrint("P操作(num)", 1);
            String str = "拷贝出缓冲区:" + process.ProID + ":" + process.PC + ",内存PCB写操作:"
                    + L_Address + "," + physicalAddress
                    + "," + "0xff" + "," + "-";
            Output.bufferPrint(str);
        }
        Output.bufferPrint("V操作(num)", 2);

        int lastTime = Clock_thread.COUNTTIME;
        int pastTime = 0;
        while(true) {
            synchronized (Clock_thread.class) {
                if(Clock_thread.COUNTTIME > lastTime) {
                    lastTime = Clock_thread.COUNTTIME;
                    pastTime += 1;
                }
            }
            if(pastTime >= 2) {
                Output.print("重新进入就绪队列:" + Buffer.currentProcess.ProID + "," + Buffer.currentProcess.getRestInstructionNum());
                synchronized (Memory.class) {
                    Memory.readyQueue.add(Memory.readBlockQueue.poll());
                    Buffer.currentProcess = null;
                }
                CPU2023.flag_IO = false; //stop IO
                break;
            }
        }

    }
}
