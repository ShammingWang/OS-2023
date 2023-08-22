package com.shamming.software;
import com.shamming.entity.PCB;
import com.shamming.entity.Buffer;
import com.shamming.hardware.Clock_thread;
import com.shamming.util.FileOperator;
import com.shamming.util.MMU;
import com.shamming.util.Output;


public class WriteBufferFromPCB implements Runnable{

    @Override
    public void run() {
        String data;
        synchronized (Buffer.class) {
            PCB process = Buffer.currentProcess;
            synchronized (Clock_thread.class) {
                process.data = Clock_thread.COUNTTIME + "[进行写文件操作]\n"; // data source
            }
            Integer L_Address = process.job.instructions.get(process.PC).L_Address; // set
            Integer physicalAddress = MMU.physical(process.pageTable, L_Address);
            data = process.data;
            Output.bufferPrint("P操作(num)");
            String str = "拷贝入缓冲区:" + process.ProID + ":" + process.PC + ",读取PCB操作:"
                    + L_Address + "," + physicalAddress
                    + "," + "0xff" + "," + "-";
            Output.bufferPrint(str);
        }
        Buffer.write(data);
        Output.bufferPrint("V操作(num)", 1);
        Output.bufferPrint("V操作(order2)", 1);
    }
}
