package com.shamming.hardware;

import com.shamming.entity.Instruction;
import com.shamming.entity.PCB;
import com.shamming.util.MMU;
import com.shamming.util.Output;

public class CPU2023 implements Runnable {
    private Integer PC; //Program Counter: this is the PC pointer of CPU sensing the next instruction to be executed
    private Instruction IR; //Instruction register
    private Integer PSW; // Program State Word: indicates that the program is in user state or core state

    public static boolean impulse;

    public static boolean isCloseInterrupted;
    public static boolean flag_IO;
    //public static boolean isScheduled;

    public void CPU_PRO(PCB pcb) { // the site protection function of CPU
        //PCB pcb = Memory.currentProcess;
        pcb.PC = this.PC;
        pcb.IR = this.IR;
    }
    public void CPU_REC(PCB pcb) { // the site recovery function of CPU
        //PCB pcb = Memory.currentProcess;
        this.PC = pcb.PC;
        this.IR = pcb.job.instructions.get(PC);
    }
    private void execute() {  // cpu execute the current instruction

        PCB pcb = null;
        synchronized (Memory.class) {
            if(Memory.currentProcess == null) {
                //if(PSW == 0)
                Output.print("CPU空闲");
                return; // cpu free
            } else {
                pcb = Memory.currentProcess;
            }
        }
        if(pcb.isOver()) {
            //System.out.println("当前进程没有可以执行的指令");
            return; // is over and wait
        }

        CPU_REC(pcb); //fetch the instruction

        if(IR != null && IR.Instruc_State == 5 && flag_IO) {
            //System.out.println(IR);
            //System.out.println(Clock_thread.COUNTTIME + ":IO堵塞了");
            return;
        }
        if(IR != null && IR.Instruc_State == 6 && flag_IO) {
            //System.out.println(IR);
            //System.out.println(Clock_thread.COUNTTIME + ":IO堵塞了");
            return;
        }

        PC += 1;
        //System.out.println(Clock_thread.COUNTTIME + "准备执行checkPage");
        checkPage(); // check the logic address for every instruction
        //execute the instruction
        isCloseInterrupted = true;  // can't schedule
        // close the interruption
        //Output.print("执行进程ID=" + Memory.currentProcess.ProID.toString());
        IR.execute(); //
        pcb.sumRunTime += IR.getExecuteTime(); // add the time for execution time //
        CPU_PRO(pcb); //
        isCloseInterrupted = false; // open the interruption

    }

    private void checkPage() {
        PCB pcb;
        pcb = Memory.currentProcess;
        if(!pcb.pageTable.hasPageIn(IR.L_Address)) {
            Integer outPage = Memory.currentProcess.pageTable.allocate(IR.L_Address); // allocate the block for the page
            Output.PP.add(Clock_thread.COUNTTIME); // Output
            Output.PP.add(pcb.ProID); // Output
            String s1 = "缺页中断:" + pcb.ProID + ":" + outPage + "," + IR.L_Address;
            Output.print(s1);
        }
        while(true) {
            synchronized (Memory.class) {
                if(Memory.interruption == null) {
                    break;
                }
            }
            //System.out.println("当前中断还未处理!!!");
        }

        String s = "运行进程:" + pcb.ProID + ":" + IR.Instruc_ID + "," + IR.Instruc_State + "," + IR.L_Address +
                "," + MMU.physical(pcb.pageTable, IR.L_Address);
        Output.print(s); //
    }

    @Override
    public void run() {
        while(true) {
            //boolean flag;
            synchronized (CPU2023.class) {
                if(impulse) {
                    this.execute(); // launch cpu execute
                    impulse = false;
                }
            }
        }
    }
}
