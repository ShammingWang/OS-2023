package com.shamming.util;

import com.shamming.Main;
import com.shamming.hardware.Clock_thread;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.LinkedList;

public class Output {
    //static File file;
//    static {
//        file = new File("./output/buffer.txt");
//        if(file.exists()) {
//            FileOperator.deleteFile(file);
//        }
//        try {
//            file.createNewFile();
//        } catch (IOException e) {
//            throw new RuntimeException(e);
//        } //init the buffer output
//    }

    public static StringBuffer sb_over = new StringBuffer();
    public static LinkedList<Integer> BB1 = new LinkedList<>();
    public static LinkedList<Integer> BB2 = new LinkedList<>();
    public static LinkedList<Integer> BB4 = new LinkedList<>();
    public static LinkedList<Integer> BB5 = new LinkedList<>();
    public static LinkedList<Integer> PP = new LinkedList<>();

    public static void print(String s) {
        synchronized (Clock_thread.class) {
            //System.out.println(Clock_thread.COUNTTIME + ":[" + s + "]");
            Main.mainWindow.textAreaMain.append(Clock_thread.COUNTTIME + ":[" + s + "]\n");
            Main.mainWindow.textAreaMain.setCaretPosition(Main.mainWindow.textAreaMain.getText().length());
        }
    }
    public static void log(String s) {
        System.out.println("-->(" + s + ")<--" + Clock_thread.COUNTTIME);
    }

    public static void bufferPrint(String s) {
        synchronized (Clock_thread.class) {
            Main.mainWindow.textAreaBuffer.append(Clock_thread.COUNTTIME + ":[" + s + "]\n");
            Main.mainWindow.textAreaBuffer.setCaretPosition(Main.mainWindow.textAreaBuffer.getText().length());
            //FileWriter fw = new FileWriter(file, true);
            //fw.write(Clock_thread.COUNTTIME + ":[" + s + "]\n");
            //fw.close();
        }
    }
    public static void bufferPrint(String s, Integer add) {
        synchronized (Clock_thread.class) {
            Main.mainWindow.textAreaBuffer.append((Clock_thread.COUNTTIME + add) + ":[" + s + "]\n");
            Main.mainWindow.textAreaBuffer.setCaretPosition(Main.mainWindow.textAreaBuffer.getText().length());
            //FileWriter fw = new FileWriter(file, true);
            //fw.write(Clock_thread.COUNTTIME + ":[" + s + "]\n");
            //fw.close();
        }
    }

    public static String getBB(String pre, LinkedList<Integer> BB) {
        if(BB.size() == 0) return pre + "]";
        StringBuilder sb = new StringBuilder(pre);
        for(int i = 0;i < BB.size(); i += 2) {
            Integer inTime = BB.get(i);
            Integer id = BB.get(i + 1);
            sb.append(inTime).append(",").append(id).append("/");
        }
        sb.deleteCharAt(sb.length() - 1);
        sb.append("]");
        return sb.toString();
    }
    public static void statusPrint() {
        StringBuilder ans = new StringBuilder();
        ans.append(sb_over).append("\n\n");
        ans.append(getBB("BB1:[阻塞队列1,键盘输入:", BB1)).append("\n");
        ans.append(getBB("BB2:[阻塞队列2,屏幕显示:", BB2)).append("\n");
        ans.append(getBB("BB4:[阻塞队列4,磁盘读文件:", BB4)).append("\n");
        ans.append(getBB("BB5:[阻塞队列5,磁盘写文件:", BB5)).append("\n");
        ans.append("\n");
        ans.append(getBB("PPPP:[PageInterruption:", PP));
        Main.mainWindow.textAreaStatus.setText(ans.toString());
        Main.mainWindow.textAreaStatus.setCaretPosition(Main.mainWindow.textAreaStatus.getText().length());
    }

}
