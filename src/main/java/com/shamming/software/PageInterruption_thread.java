package com.shamming.software;

import com.shamming.entity.Interruption;
import com.shamming.entity.PageTable;
import com.shamming.hardware.Memory;
import com.shamming.util.Output;

import java.util.ArrayList;
import java.util.Objects;

public class PageInterruption_thread implements Runnable {

    @Override
    public void run() {
        while(true) {
            synchronized (Memory.class) {
                if(Memory.interruption != null) {
                    if(Objects.equals(Memory.interruption.interruptionType, Interruption.Interruption_PageIn)) {
                        PageIn(Memory.interruption.pageTable, Memory.interruption.logicAddress);
                    }
                    else if(Objects.equals(Memory.interruption.interruptionType, Interruption.Interruption_PageOut)) {
                        PageOut(Memory.interruption.pageTable);
                    }
                    Memory.interruption = null;  // clear up the interruption
                }
            }
        }
    } // listening to the page interruption request

    void PageIn(PageTable pageTable, Integer logicAddress) {
        pageTable.PageIn(logicAddress);
        //Output.log("响应PageIn中断成功");
    }
    void PageOut(PageTable pageTable) {
        Integer outPage = pageTable.getOutPage();  //LRU algorithm
        pageTable.PageOut(outPage);
        //Output.log("响应PageOut中断成功");
    }

}
