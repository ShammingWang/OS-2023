package com.shamming.hardware;

import com.shamming.entity.Buffer;
import com.shamming.entity.Interruption;
import com.shamming.entity.PCB;

import java.util.LinkedList;
import java.util.PriorityQueue;

public class Memory {

    public static Interruption interruption;
    public static int processNum = 0;
    //
    static boolean[] isAllocated; // (isUsed[i] == true) means that the block [i]  is used
    //the default value is false in this array
    public static PriorityQueue<PCB> readyQueue = new PriorityQueue<>(); // the ready PriorityQueue
    //public static LinkedList<PCB> blockQueue = new LinkedList<>(); // the block queue
    public static LinkedList<PCB> inputBlockQueue = new LinkedList<>(); // the input block queue id = 1
    public static LinkedList<PCB> outputBlockQueue = new LinkedList<>(); // the input block queue id = 2
    public static LinkedList<PCB> readBlockQueue = new LinkedList<>(); // the input block queue id = 4
    public static LinkedList<PCB> writeBlockQueue = new LinkedList<>(); // the input block queue id = 5

    //public static Buffer buffer;  // create a new buffer for simultaneous work

    public static PCB currentProcess;

    static { //init the static variable space
        isAllocated = new boolean[16]; //the maximum size is 16
    }

    public static Integer allocate_block() {
        for(int i = 0;i < isAllocated.length;i ++) {
            if(!isAllocated[i]) {
                isAllocated[i] = true; //set the memory to be used
                return i;
            }
        }
        return -1; // means that the user memory space is full
    }

    public static void clear(Integer physicalAddress) {
        isAllocated[physicalAddress] = false;
    }

    public static Boolean isFull() {
        for (boolean b : isAllocated) {
            if (!b) {
                return false; // there is available memory space
            }
        }
        return true; // memory space runs out
    }

}
