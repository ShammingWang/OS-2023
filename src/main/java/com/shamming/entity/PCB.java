package com.shamming.entity;

import com.shamming.hardware.Clock_thread;
import com.shamming.util.MMU;

import java.util.ArrayList;
import java.util.Objects;

public class PCB implements Comparable{
    public Integer ProID; // the process identity
    public Integer Priority; // the process priority number
    public Integer InTimes; // the time of creation for process
    public Integer EndTimes; // the time when the process is over
    public Integer PSW; // the state of the process
    public ArrayList<Integer> RunTimes; // the list of process runtime (begin time, lasting time)
    public Integer TurnTimes; // process turnover time
    public Integer InstrucNum; // the number of the instructions
    public Integer PC; // the same as in CPU
    public Instruction IR; // the same as in CPU

    public int sumRunTime;

    public ArrayList<Integer> infoListOfReadyQueue; // (RqNum,RqTimes)
    public ArrayList<Integer> infoListOfBlockQueueInput; // (BqNum1, BqTimes1)
    public ArrayList<Integer> infoListOfBlockQueueOutput; // (BqNum2, BqTimes2)

    public PageTable pageTable; // every process has a pageTable

    public Job job; //the job this process is created for

    public String data; // pcb space

    public PCB(Job job) {
        this.job = job;
        this.ProID = job.JobsID;
        this.Priority = job.Priority;
        synchronized (Clock_thread.class) {
            this.InTimes = Clock_thread.COUNTTIME;
        }
        this.EndTimes = null;
        this.PSW = 0; //0 means ready
        this.RunTimes = new ArrayList<>();
        this.TurnTimes = null;
        this.InstrucNum = job.InstrucNum;
        this.PC = 0;
        this.IR = null;
        this.pageTable = MMU.PageTableCreate(); // create a page table for this process
    }

    public boolean isOver() {
        return Objects.equals(PC, InstrucNum);
    }

    @Override
    public int compareTo(Object o) {
        return this.Priority - ((PCB)o).Priority;
    }

    public void free() {
        pageTable.free();
    }

    public Integer getRestInstructionNum() {
        return InstrucNum - PC;
    }

    @Override
    public String toString() {
        return "{" +
                "ID=" + ProID +
                '}';
    }
}
