package com.shamming.util;

import com.shamming.Main;
import com.shamming.entity.Job;
import com.shamming.entity.PCB;
import com.shamming.entity.PageTable;
import com.shamming.hardware.CPU2023;
import com.shamming.hardware.Clock_thread;
import com.shamming.hardware.HardDisk;
import com.shamming.hardware.Memory;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.PriorityQueue;

public class DataFresh implements Runnable{

    private String getReadyQueue() {
        if(Memory.readyQueue == null) return "";
        PriorityQueue<PCB> queue = new PriorityQueue<>(Memory.readyQueue);
        StringBuilder ans = new StringBuilder();
        while(!queue.isEmpty()) {
            PCB pcb = queue.poll();
            ans.append(pcb.toString()).append(" ");
        }
        return ans.toString();
    }

    private String getBlockQueue1() {
        if(Memory.inputBlockQueue == null) return "";
        LinkedList<PCB> queue = new LinkedList<>(Memory.inputBlockQueue);
        StringBuilder ans = new StringBuilder();
        while(!queue.isEmpty()) {
            PCB pcb = queue.poll();
            ans.append(pcb.toString()).append(" ");
        }
        return ans.toString();
    }

    private String getBlockQueue2() {
        if(Memory.outputBlockQueue == null) return "";
        LinkedList<PCB> queue = new LinkedList<>(Memory.outputBlockQueue);
        StringBuilder ans = new StringBuilder();
        while(!queue.isEmpty()) {
            PCB pcb = queue.poll();
            ans.append(pcb.toString()).append(" ");
        }
        return ans.toString();
    }

    private String getBlockQueue4() {
        if(Memory.readBlockQueue == null) return "";
        LinkedList<PCB> queue = new LinkedList<>(Memory.readBlockQueue);
        StringBuilder ans = new StringBuilder();
        while(!queue.isEmpty()) {
            PCB pcb = queue.poll();
            ans.append(pcb.toString()).append(" ");
        }
        return ans.toString();
    }

    private String getBlockQueue5() {
        if(Memory.writeBlockQueue == null) return "";
        LinkedList<PCB> queue = new LinkedList<>(Memory.writeBlockQueue);
        StringBuilder ans = new StringBuilder();
        while(!queue.isEmpty()) {
            PCB pcb = queue.poll();
            ans.append(pcb.toString()).append(" ");
        }
        return ans.toString();
    }

    private String getCurrentProcess() {
        if(Memory.currentProcess != null) {
            return Memory.currentProcess.toString();
        }
        return "null";
    }

    private String getFallBack() {
        if(HardDisk.jobFallbackQueue == null) return "";
        PriorityQueue<Job> queue = new PriorityQueue<>(HardDisk.jobFallbackQueue);
        StringBuilder ans = new StringBuilder();
        while(!queue.isEmpty()) {
            ans.append(queue.poll().toString()).append(" ");
        }
        return ans.toString();
    }

    @Override
    public void run() {
        while(true) {
            try {
                Thread.sleep(50);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
            synchronized (CPU2023.class) {
                synchronized (Clock_thread.class) {
                    synchronized (Memory.class) {
                        synchronized (HardDisk.class) {
                            Main.mainWindow.textFieldCurrent.setText(getCurrentProcess());
                            Main.mainWindow.textFieldReadyQueue.setText(getReadyQueue());
                            Main.mainWindow.textFieldBlockQueue1.setText(getBlockQueue1());
                            Main.mainWindow.textFieldBlockQueue2.setText(getBlockQueue2());
                            Main.mainWindow.textFieldBlockQueue4.setText(getBlockQueue4());
                            Main.mainWindow.textFieldBlockQueue5.setText(getBlockQueue5());
                            //Main.mainWindow.textAreaMemory.
                            Main.mainWindow.textFieldFallBack.setText(getFallBack());
                            Output.statusPrint();

                            Main.mainWindow.textAreaMemory.setText(getPageTable());

                        }
                    }
                }
            }
        }
    }


    public String  getPageTable() {
        if(Memory.readyQueue == null) return "";
        PriorityQueue<PCB> queue = new PriorityQueue<>(Memory.readyQueue);
        if(Memory.currentProcess != null) {
            queue.add(Memory.currentProcess);
        }
        Integer[] mapLogicAddress = new Integer[16];
        Integer[] mapProcessID = new Integer[16];

        while(!queue.isEmpty()) {
            PCB process = queue.poll();
            PageTable pageTable = process.pageTable;
            ArrayList<Integer> usedPages = pageTable.getUsedPages();
            usedPages.add(16);
            for(Integer page : usedPages) {
                Integer physicalAddress = pageTable.transform(page);
                mapLogicAddress[physicalAddress] = page;
                mapProcessID[physicalAddress] = process.ProID;
            }
        }
        StringBuilder sb = new StringBuilder();

        for(int i = 0;i < 16;i ++) {
            if(mapLogicAddress[i] == null || mapLogicAddress[i] != 16) {
                sb.append(i).append("<--{id=").append(mapProcessID[i]).append(",").append(mapLogicAddress[i]).append("} ");
            }
            else {
                sb.append(i).append("<--{id=").append(mapProcessID[i]).append(",").append("PCB Addr").append("} ");
            }
            if(i % 2 == 1) sb.append("\n");
        }
        return sb.toString();
    }
}
