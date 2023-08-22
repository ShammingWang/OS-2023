package com.shamming.entity;

import com.shamming.util.MMU;
import com.shamming.util.Output;
import com.shamming.util.Syn;

public class Buffer {

    static {
        num = new Syn(1);
        order = new Syn(0);
    }
    public static String data = null;
    public static Syn num;// pv obj to control the number
    public static Syn order;// pv obj to control the order
    public static PCB currentProcess; // the process which enters the buffer

    public static void write(String s) { // write into the buffer
        num.P();
        if(data == null) {
            data = s;
        }
        num.V();
        order.V();
    }
    public static String read() {
        order.P();
        String ans = null;
        num.P();
        if(data != null) {
            ans = data;
            data = null;
        }
        num.V();
        return ans;
    }
}
