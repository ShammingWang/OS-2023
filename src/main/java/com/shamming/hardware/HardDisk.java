package com.shamming.hardware;

import com.shamming.entity.Instruction;
import com.shamming.entity.Job;
import com.shamming.util.Output;

import java.io.*;
import java.util.ArrayList;
import java.util.PriorityQueue;

public class HardDisk {
    public static ArrayList<Job> tempJobs;
    public static PriorityQueue<Job> jobFallbackQueue;

    public static Integer last; // the last job in
    static {
        tempJobs = new ArrayList<>();
        jobFallbackQueue = new PriorityQueue<>();
        last = 0;
    }
    public static String dir = "input2";

    public static void init() throws IOException {
        //String dir = "input1";
        String filename = dir + File.separator + "jobs-input.txt";
        BufferedReader bfr = new BufferedReader(new FileReader(filename));
        while(true) {
            String line = bfr.readLine();
            if(line == null || line.length() == 0) {
                break;
            }
            String[] jobData = line.split(",");
            Integer JobsID = Integer.parseInt(jobData[0].trim());
            Integer Priority = Integer.parseInt(jobData[1].trim());
            Integer InTimes = Integer.parseInt(jobData[2].trim());
            Integer InstrucNum = Integer.parseInt(jobData[3].trim());
            tempJobs.add(new Job(JobsID, Priority, InTimes, InstrucNum, getInstructionsById(dir, JobsID)));
        }
    }
    private static ArrayList<Instruction> getInstructionsById(String str, Integer jobsID) throws IOException {
        ArrayList<Instruction> instructions = new ArrayList<>();
        //String dir = "input1";
        String filename = str + File.separator + jobsID.toString() + ".txt";
        BufferedReader bfr = new BufferedReader(new FileReader(filename));
        while(true) {
            String line = bfr.readLine();
            if(line == null || line.length() == 0) {
                break;
            }
            String[] instructionData = line.split(",");
            Integer Instruc_ID = Integer.parseInt(instructionData[0].trim());
            Integer Instruc_State = Integer.parseInt(instructionData[1].trim());
            Integer L_Address = Integer.parseInt(instructionData[2].trim());
            instructions.add(new Instruction(Instruc_ID, Instruc_State, L_Address));
        }
        return instructions; // return the ans
    }

    public static void submit(String root) throws IOException {
        String filename = root + File.separator + "jobs-input.txt";
        BufferedReader bfr = new BufferedReader(new FileReader(filename));
        while(true) {
            String line = bfr.readLine();
            if(line == null || line.length() == 0) {
                break;
            }
            String[] jobData = line.split(",");
            Integer JobsID = Integer.parseInt(jobData[0].trim());
            Integer Priority = Integer.parseInt(jobData[1].trim());
            int InTimes;
            synchronized (Clock_thread.class) {
                InTimes = Clock_thread.COUNTTIME;
            }
            Integer InstrucNum = Integer.parseInt(jobData[3].trim());
            Job job = new Job(JobsID, Priority, InTimes, InstrucNum, getInstructionsById(root, JobsID));
            String s = "新增作业:" + job.JobsID + "," + job.InTimes + "," + job.InstrucNum;
            Output.print(s);
            jobFallbackQueue.add(job);
            // directly put into the job fall back queue
        }
    }
}
