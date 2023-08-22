package com.shamming.entity;

import java.util.ArrayList;

public class Job implements Comparable{
    public Integer JobsID;
    public Integer Priority;
    public Integer InTimes; // arrival time
    public Integer InstrucNum;

    public ArrayList<Instruction> instructions;

    public Job(Integer jobsID, Integer priority, Integer inTimes, Integer instrucNum, ArrayList<Instruction> instructions) {
        JobsID = jobsID;
        Priority = priority;
        InTimes = inTimes;
        InstrucNum = instrucNum;
        this.instructions = instructions;
    }

    @Override
    public String toString() {
        return "{" +
                "Job=" + JobsID +
                '}';
    }

    @Override
    public int compareTo(Object job) {
        return this.Priority - ((Job)job).Priority;
    }
}
