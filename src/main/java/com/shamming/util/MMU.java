package com.shamming.util;

import com.shamming.entity.PageTable;

public class MMU {
    public static PageTable PageTableCreate() {
        PageTable pageTable = new PageTable();
        if(pageTable.PageIn(16)) { // if the memory is enough
            return pageTable;
        }
        return null;
    }
    public static Integer physical(PageTable pageTable, Integer logicAddress) {
        return pageTable.transform(logicAddress);
    }
}
