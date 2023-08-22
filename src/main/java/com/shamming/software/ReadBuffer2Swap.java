package com.shamming.software;

import com.shamming.entity.Buffer;
import com.shamming.entity.PCB;
import com.shamming.hardware.CPU2023;
import com.shamming.hardware.Clock_thread;
import com.shamming.hardware.Memory;
import com.shamming.util.FileOperator;
import com.shamming.util.MMU;
import com.shamming.util.Output;

public class ReadBuffer2Swap implements Runnable {

    @Override
    public void run() {
        String data = Buffer.read();
        Output.bufferPrint("P操作(order2)",1);
        synchronized (Buffer.class) {
            PCB process = Buffer.currentProcess;
            Integer L_Address = process.job.instructions.get(process.PC).L_Address; //
            Integer physicalAddress = MMU.physical(process.pageTable, L_Address);
            FileOperator.writeSwapFile(process.ProID, physicalAddress, data); // write into the swap
            Output.bufferPrint("P操作(num)",1);
            String str = "拷贝出缓冲区:" + process.ProID + ":" + process.PC + ",磁盘文件写操作:"
                    + L_Address + "," + physicalAddress
                    + "," + "0xff" + "," + "DiskEx_" + process.ProID;
            Output.bufferPrint(str,1);
        }
        Output.bufferPrint("V操作(num)",2);

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
                    Memory.readyQueue.add(Memory.writeBlockQueue.poll());
                    Buffer.currentProcess = null;
                }
                CPU2023.flag_IO = false; //stop IO
                break;
            }
        }

    }
}
