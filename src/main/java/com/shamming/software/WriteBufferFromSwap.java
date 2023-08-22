package com.shamming.software;

import com.shamming.entity.Buffer;
import com.shamming.entity.PCB;
import com.shamming.hardware.CPU2023;
import com.shamming.hardware.Clock_thread;
import com.shamming.hardware.Memory;
import com.shamming.util.FileOperator;
import com.shamming.util.MMU;
import com.shamming.util.Output;

public class WriteBufferFromSwap implements Runnable {

    @Override
    public void run() {
        //Output.log("开始从交换区写到缓冲区的线程");
        String data;
        synchronized (Buffer.class) {
            //Output.log("Buffer.class 加锁成功");
            PCB process = Buffer.currentProcess;
            Integer L_Address = process.job.instructions.get(process.PC).L_Address; // set
            Integer physicalAddress = MMU.physical(process.pageTable, L_Address);
            data = FileOperator.readLastLineSwapFile(process.ProID,
                    MMU.physical(process.pageTable, L_Address)); //
            Output.bufferPrint("P操作(num)");
            String str = "拷贝入缓冲区:" + process.ProID + ":" + process.PC + ",磁盘文件读操作:"
                    + L_Address + "," + physicalAddress
                    + "," + "0xff" + "," + "DiskEx_" + process.ProID;
            Output.bufferPrint(str);
        }
        Buffer.write(data); // write for 1 second
        Output.bufferPrint("V操作(num)", 1);
        Output.bufferPrint("V操作(order1)", 1);
    }
}
