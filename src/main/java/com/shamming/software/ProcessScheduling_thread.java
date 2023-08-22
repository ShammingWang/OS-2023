package com.shamming.software;

import com.shamming.entity.Job;
import com.shamming.entity.PCB;
import com.shamming.hardware.CPU2023;
import com.shamming.hardware.Clock_thread;
import com.shamming.hardware.HardDisk;
import com.shamming.hardware.Memory;
import com.shamming.util.FileOperator;
import com.shamming.util.MMU;
import com.shamming.util.Output;

import java.io.File;
import java.time.Clock;

public class ProcessScheduling_thread implements Runnable{
    public static int Times = 3;
    //private int lastTime = -1; //used for sensing the system clock change

    public static boolean impulse;

    @Override
    public void run() {
        while(true) {
            synchronized (CPU2023.class) {
                if(!CPU2023.isCloseInterrupted) ProcessScheduling(); // use algorithm to schedule the process
            }
            synchronized (ProcessScheduling_thread.class) {
                if(impulse) {
                    impulse = false;
                    synchronized (CPU2023.class) {
                        CPU2023.impulse = true;
                    }
                }
            }
        }
    }


    public static void ProcessScheduling() {
        synchronized (ProcessScheduling_thread.class) {
            high_level_scheduling();
            low_level_scheduling();
        }
    }

    public static void high_level_scheduling() {
        while(!HardDisk.jobFallbackQueue.isEmpty()) {
            synchronized (Memory.class) {
                if(Memory.processNum >= 4) break;
            }
            Job job = HardDisk.jobFallbackQueue.poll(); //get and remove the top element
            assert job != null; // assert the job is not null
            PCB pcb = new PCB(job); // try to create the process
            //System.out.println(job); // debug
            if(pcb.pageTable != null) { // the memory is not full
                synchronized (Memory.class) {
                    Memory.processNum += 1;
                }
                //System.out.println("进程创建成功！" + "-->" + pcb.ProID);
                String s1 = "创建进程:" + pcb.ProID + "," + MMU.physical(pcb.pageTable, 16) + ",分页式";
                Output.print(s1);

                String filePath = "./HardDisk" + File.separator + "DiskExchangeArea_" + pcb.ProID;
                File dir = new File(filePath);
                FileOperator.deleteFile(dir);  // update the file time
                dir.mkdir(); // make dir

                Memory.readyQueue.add(pcb);
                String s2 = "进入就绪队列:" + pcb.ProID + ":" + (pcb.job.InstrucNum - pcb.PC);
                Output.print(s2);
                //HardDisk.jobFallbackQueue.poll(); //pop the peek element
                synchronized (Clock_thread.class) {
                    pcb.InTimes = Clock_thread.COUNTTIME; //set the time of the process firstly getting in to the ready queue
                }
            }
            else { // space allocation failed
                HardDisk.jobFallbackQueue.add(job);
                break;
            }
        }
    }



    public static void low_level_scheduling() {
        PCB process = Memory.currentProcess; //change name
        boolean case1 = !Memory.readyQueue.isEmpty() && process == null;
        boolean case2 = Times <= 0; //time slice runs out
        boolean case3 = (process != null && process.isOver());
        boolean case4 = (process != null && !process.isOver() && process.job.instructions.get(process.PC).getExecuteTime() > Times);
        if(!case1 && !case2 && !case3 && !case4) return;
        //Output.log("low_level_scheduling");
        synchronized (Memory.class) {
            if(Memory.currentProcess != null) {
                if(!Memory.currentProcess.isOver()) {
                    Memory.readyQueue.add(Memory.currentProcess);
                    Output.print("重新进入就绪队列:" + Memory.currentProcess.ProID + "," + Memory.currentProcess.getRestInstructionNum());
                } else {  //is over
                    Memory.currentProcess.free();
                    PCB pcb = Memory.currentProcess;
                    String s = pcb.ProID + ":" + pcb.job.InTimes + "+" + pcb.InTimes + "+" + pcb.sumRunTime;
                    Output.sb_over.append(Clock_thread.COUNTTIME).append(":[").append(s).append("]\n"); //
                    //Output.print(s);
                    Memory.processNum -= 1;
                }
            }
            Memory.currentProcess = Memory.readyQueue.poll();
            //assert Memory.currentProcess != null;
            //System.out.println("进程" + Memory.currentProcess.ProID + "上处理机运行" + Clock_thread.COUNTTIME);
        }
        Times = 3; //init time slice
    }
}
