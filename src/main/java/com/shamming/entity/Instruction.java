package com.shamming.entity;

import com.shamming.hardware.CPU2023;
import com.shamming.hardware.Clock_thread;
import com.shamming.hardware.Memory;
import com.shamming.software.*;
import com.shamming.util.FileOperator;
import com.shamming.util.MMU;
import com.shamming.util.Output;

import javax.swing.plaf.metal.MetalMenuBarUI;
import java.time.Clock;

public class Instruction {
    public Integer Instruc_ID; //the identity of the instruction
    public Integer Instruc_State; //the type of the instruction
    public Integer L_Address; //the logic address the instruction access


    public Integer getExecuteTime() {
        if(Instruc_State == 0) return 1;
        return 2;
    }

    @Override
    public String toString() {
        return "Instruction{" +
                "Instruc_ID=" + Instruc_ID +
                ", Instruc_State=" + Instruc_State +
                ", L_Address=" + L_Address +
                '}';
    }

    public Instruction(Integer instruc_ID, Integer instruc_State, Integer l_Address) {
        Instruc_ID = instruc_ID;
        Instruc_State = instruc_State;
        L_Address = l_Address;
    }

    public void execute() {

        if(Instruc_State == 0 || Instruc_State == 1) {
            doType01();
        } else if(Instruc_State == 2) {
            doType2();
        }
        else if(Instruc_State == 3) {
            doType3();
        }
        else if(Instruc_State == 5) {
            doType5();
        }
        else if(Instruc_State == 6) {
            doType6();
//            doType01();
        }
        else {
            System.out.println("Error");
        }
        //doType01();
    }

    private void doType5() { // has locked cpu
        synchronized (Memory.class) {
            CPU2023.flag_IO = true; // set that ignore the IO operator
            //CPU2023.isCloseInterrupted = true; // close the interruption
            PCB process = Memory.currentProcess;

            // at this time, we need to use the swap area file
            FileOperator.createSwapFile(process.ProID, MMU.physical(process.pageTable, L_Address));
            String text;
            Integer physicalAddress = MMU.physical(process.pageTable, L_Address); // map the physical address
            synchronized (Clock_thread.class) {
                text = Clock_thread.COUNTTIME + ":[进行读文件操作]\n";
            }
            FileOperator.writeSwapFile(process.ProID, physicalAddress, text);

            Output.BB4.add(Clock_thread.COUNTTIME); // Output
            Output.BB4.add(Memory.currentProcess.ProID); // Output
            Output.print("阻塞进程:4," + process.ProID);
            Buffer.currentProcess = process; // the process enters the buffer
            Memory.readBlockQueue.add(process); // id = 4
            Memory.currentProcess = null;
        }

        //ProcessScheduling_thread.low_level_scheduling(); //

        new Thread(new WriteBufferFromSwap()).start(); //
        new Thread(new ReadBuffer2Mem()).start(); //

        // 卡死在这里了 //
        int lastTime = Clock_thread.COUNTTIME;
        int pastTime = 0;
        while(true) {
            synchronized (Clock_thread.class) {
                if(Clock_thread.COUNTTIME > lastTime) {
                    lastTime = Clock_thread.COUNTTIME;
                    pastTime += 1;
                }
            }
            if(pastTime >= 1) {
                break;
            }
        }
    }

    private void doType6() {
        synchronized (Memory.class) {
            CPU2023.flag_IO = true; // set that ignore the IO operator
            PCB process = Memory.currentProcess;
            Output.BB5.add(Clock_thread.COUNTTIME); // Output
            Output.BB5.add(Memory.currentProcess.ProID); //Output
            Output.print("阻塞进程:5," + process.ProID);
            Buffer.currentProcess = process; // the process enters the buffer
            Memory.writeBlockQueue.add(process); // id = 5
            Memory.currentProcess = null;
        }
        new Thread(new WriteBufferFromPCB()).start();
        new Thread(new ReadBuffer2Swap()).start();

        int lastTime = Clock_thread.COUNTTIME;
        int pastTime = 0;
        while(true) {
            synchronized (Clock_thread.class) {
                if(Clock_thread.COUNTTIME > lastTime) {
                    lastTime = Clock_thread.COUNTTIME;
                    pastTime += 1;
                }
            }
            if(pastTime >= 1) {
                break;
            }
        }

    }

    private void doType3() {
        synchronized (Memory.class) {
            Memory.outputBlockQueue.add(Memory.currentProcess);  // block queue id = 2
            Output.BB2.add(Clock_thread.COUNTTIME);
            Output.BB2.add(Memory.currentProcess.ProID); //
            String s = "阻塞进程:2," + Memory.currentProcess.ProID;
            Output.print(s);
            Memory.currentProcess = null;
        }
        new Thread(new OutputBlock_thread()).start(); // set clock 2 seconds
    }

    private void doType2() {
        synchronized (Memory.class) {
            Memory.inputBlockQueue.add(Memory.currentProcess);  // block queue id = 1
            Output.BB1.add(Clock_thread.COUNTTIME);
            Output.BB1.add(Memory.currentProcess.ProID); //
            String s = "阻塞进程:1," + Memory.currentProcess.ProID;
            Output.print(s);
            Memory.currentProcess = null;
        }
        new Thread(new InputBlock_thread()).start(); // set clock 2 seconds
    }

    private void doType01() {
        int executeTime = getExecuteTime(); // get current instruction execution time
        int lastTime;
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
            if(pastTime >= executeTime) {
                break;
            }
        }

        synchronized (ProcessScheduling_thread.class) {
            ProcessScheduling_thread.Times -= executeTime;
        }
    }
}
