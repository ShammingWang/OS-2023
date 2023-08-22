package com.shamming.entity;

public class Interruption {

    public static final Integer Interruption_PageIn = 0;
    public static final Integer Interruption_PageOut = 1;
    public Integer interruptionType;

    public PageTable pageTable; // the page table for page in or out
    public Integer logicAddress; //the logic address for page in or out

}
