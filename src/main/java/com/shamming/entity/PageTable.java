package com.shamming.entity;

import com.shamming.hardware.Memory;
import com.shamming.util.MMU;
import com.shamming.util.Output;

import java.util.ArrayList;
import java.util.Arrays;

public class PageTable {
    private static final Integer MAXLENGTH = 16 + 1;
    private static final Integer MAX_ALLOCATE_NUM = 4;
    Integer[] mappingBlockNumber;

    Integer[] pastTimes;

    //boolean[] isAllocated;
    public PageTable() {
        mappingBlockNumber = new Integer[MAXLENGTH];
        pastTimes = new Integer[MAXLENGTH];
        Arrays.fill(pastTimes, 0); // set all 0
    }

    public boolean hasPageIn(Integer logicalAddress) {
        return mappingBlockNumber[logicalAddress] != null;
    }

    private Boolean check(Integer index) {
        return index >= 0 && index < MAXLENGTH;
    }

    private Integer allocatedNum() {
        int ans = 0;
        for(Integer flag : mappingBlockNumber) {
            if(flag != null) {
                ans += 1;
            }
        }
        return ans;
    }

    public Integer transform(Integer logicAddress) {
        if(!check(logicAddress)) {
            System.out.println("the logic address error");
            return null;
        }
        return mappingBlockNumber[logicAddress];
    }

    public ArrayList<Integer> getUsedPages() {
        ArrayList<Integer> usedPages = new ArrayList<>();
        for(int i = 0;i < MAXLENGTH - 1;i ++) {
            Integer page = mappingBlockNumber[i];
            if(page != null) {
                usedPages.add(i);  // the (i) is the logical address
            }
        }
        return usedPages;
    }

    public Integer getOutPage() {
        Integer maxPastTime = -1;
        Integer maxPastTimePage = null;
        ArrayList<Integer> usedPages = getUsedPages(); // get the used pages
//        for(Integer page : usedPages) {
//            System.out.println(page + ":" + pastTimes[page]);
//        }
        for(Integer page : usedPages) {
            if(maxPastTimePage == null || pastTimes[maxPastTimePage] > maxPastTime) {
                maxPastTimePage = page; // update the max pastime page
                maxPastTime = pastTimes[maxPastTimePage];
            }
        }
        return maxPastTimePage;
    }

    public void PageOut(Integer page) {
        if(mappingBlockNumber[page] == null) {
            System.out.println("the page has been null");
            return;
        }
        //mappingBlockNumber[page] = null; //bug code
        Integer physicalAddress = MMU.physical(this, page);
        if(physicalAddress == null) {
            Output.log("此时要换出的逻辑页面地址为" + page);
            Output.log("PageOut 函数中 physicalAddress为空");
            Output.log("PageOu失败！！！！");
            return;
        }
        mappingBlockNumber[page] = null; //release logic address
        Memory.clear(physicalAddress); // release the physical space
    }

    public Boolean PageIn(Integer logicAddress) {
        mappingBlockNumber[logicAddress] = Memory.allocate_block();
        if(mappingBlockNumber[logicAddress] < 0) { //the memory space is not enough
            return false;
        }
        ArrayList<Integer> usedPages = getUsedPages(); // get the used pages
        for(Integer page : usedPages) {
            pastTimes[page] += 1;
        }
        pastTimes[logicAddress] = 0; // set the pastime of new page to 0


//        for(Integer x : mappingBlockNumber) {
//            System.out.print(x + ",");
//        }
//        System.out.println();

        return true;
    }

    private void sendPageOutInterruption(Integer logicAddress) {
        Interruption interruption = new Interruption();
        interruption.interruptionType = Interruption.Interruption_PageOut; // page out
        interruption.pageTable = this;
        interruption.logicAddress = logicAddress;
        while(true) {
            synchronized (Memory.class) {
                if(Memory.interruption == null) {
                    Memory.interruption = interruption; // send the interruption to the memory
                    break;
                }
            }
        }
        //Output.log("发送PageOut中断成功");
    }

    public Integer allocate(Integer logicAddress) {
        if (!check(logicAddress)) {
            System.out.println("the logic address error");
            return -1;
        }
        if(mappingBlockNumber[logicAddress] != null) {  //the logic address has been allocated
            return null;
        }
        Integer outPage = null;
        if(allocatedNum() >= MAX_ALLOCATE_NUM) {
            sendPageOutInterruption(logicAddress);
            //do PageOut();
            outPage = getOutPage();
        }
        sendPageInInterruption(logicAddress);
//        String s1 = "缺页中断:进程ID" + id + ",PageOut " + outPage + ",PageIn " + logicAddress;
//        String s2 = "缺页中断:进程ID" + id + ",PageOut 空,PageIn " + logicAddress;
//        if(outPage == null) {
//            Output.print(s2);
//        } else {
//            Output.print(s1);
//        }
        //do PageIn();
        return outPage;
    }

    private void sendPageInInterruption(Integer logicAddress) {
        Interruption interruption = new Interruption();
        interruption.interruptionType = Interruption.Interruption_PageIn; // page in type
        interruption.pageTable = this;
        interruption.logicAddress = logicAddress;
        while(true) {
            synchronized (Memory.class) {
                if(Memory.interruption == null) {
                    Memory.interruption = interruption; // send the interruption to the memory
                    break;
                }
            }
        }
        //Output.log("发送PageIn中断成功");
    }
    void free() {
        ArrayList<Integer> usedPages = getUsedPages();
        usedPages.add(16);
        for(Integer usedPage : usedPages) {
            Integer physicalAddress = MMU.physical(this, usedPage);
            Memory.clear(physicalAddress);
        }
    }

//    Integer get(Integer index) {
//        if(index < 0 || index > mappingBlockNumber.length) {
//            return null; // means that index error
//        }
//        return mappingBlockNumber[index];
//    }

//    void find(Integer logicAddress) {
//        for(Integer physicalAddress : mappingBlockNumber) {
//            if(logicAddress.equals(physicalAddress)) {
//
//            }
//        }
//    }

}
