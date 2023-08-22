package com.shamming.util;

import java.io.*;

public class FileOperator {
    public static void deleteFile(File file){
        //判断文件不为null或文件目录存在
        if (file == null || !file.exists()) {
            // flag = 0;
            //System.out.println("文件删除失败,请检查文件路径是否正确");
            return;
        }
        if(file.isFile()) {
            file.delete();
            return;
        }
        //取得这个目录下的所有子文件对象
        File[] files = file.listFiles();
        //if(files == null) return;
        //遍历该目录下的文件对象
        for (File f: files){
            //打印文件名
            String name = file.getName();
            //System.out.println(name);
            //判断子目录是否存在子目录,如果是文件则删除
            if (f.isDirectory()) {
                deleteFile(f);
            }else {
                f.delete();
            }
        }
        //删除空文件夹  for循环已经把上一层节点的目录清空。
        file.delete();
    }
    public static void createSwapFile(Integer id, Integer physicalAddress) {
        String s = "./HardDisk" + File.separator + "DiskExchangeArea_" + id + File.separator
                + physicalAddress + ".txt";
        File file = new File(s);
        try {
            file.createNewFile();
            //System.out.println("文件创建成功");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    public static void writeSwapFile(Integer id, Integer physicalAddress,String text) {
        String s = "./HardDisk" + File.separator + "DiskExchangeArea_" + id + File.separator
                + physicalAddress + ".txt";
        File file = new File(s);
        try {
            FileWriter fw = new FileWriter(file, true); // append mode
            fw.write(text);
            fw.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
    public static String readLastLineSwapFile(Integer id, Integer physicalAddress) { // read last line
        String s = "./HardDisk" + File.separator + "DiskExchangeArea_" + id + File.separator
                + physicalAddress + ".txt";
        File file = new File(s);
        String line = "";
        try {
            BufferedReader br = new BufferedReader(new FileReader(file));
//            if(!file.exists()) { //
//                createSwapFile(id, physicalAddress);
//            }
            while(true) {
                line = br.readLine();
                if(line == null || line.length() == 0) break;
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return line;
    }
}
