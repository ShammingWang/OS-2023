package com.shamming.software;

import com.shamming.entity.Job;
import com.shamming.hardware.Clock_thread;
import com.shamming.hardware.HardDisk;
import com.shamming.util.Output;

public class JobIn_thread implements Runnable {  // the thread of job request query class
    private int lastTime = -1;

    public static boolean impulse;

    @Override
    public void run() {
        while (true) {
            synchronized (Clock_thread.class) {
                if(lastTime < Clock_thread.COUNTTIME) { // sense the system time change
                    lastTime = Clock_thread.COUNTTIME;  //
                    if(lastTime % 10 == 0) {
                        CheckJob();
                    }
                }
            }
            synchronized (JobIn_thread.class) {
                if(impulse) {
                    impulse = false;
                    synchronized (ProcessScheduling_thread.class) {
                        ProcessScheduling_thread.impulse = true;
                    }
                }
            }
        }
    }
    private void CheckJob() { //job request judge function
        //System.out.println("check whether there are new jobs requesting in");
        synchronized (Clock_thread.class) {
            synchronized (HardDisk.class) {
                Integer i = HardDisk.last;
                while(i < HardDisk.tempJobs.size() && HardDisk.tempJobs.get(i).InTimes <= Clock_thread.COUNTTIME) {
                    Job job = HardDisk.tempJobs.get(i);
                    HardDisk.jobFallbackQueue.add(job);
                    String s = "新增作业:" + job.JobsID + "," + job.InTimes + "," + job.InstrucNum;
                    Output.print(s);
                    i += 1;
                }
                HardDisk.last = i; //update the last position
                //System.out.println(HardDisk.jobFallbackQueue);
                //System.out.println("jobFallbackQueue's size=" + HardDisk.jobFallbackQueue.size());
            }
        }
    }
}
