package com.shamming.ui;

import com.shamming.Main;
import com.shamming.hardware.Clock_thread;
import com.shamming.hardware.HardDisk;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

public class MainWindow {
    public JPanel panel1;
    public JButton ButtonStart;
    public JButton ButtonAdd;
    public JButton ButtonExit;
    public JTextField textFieldCurrent;
    public JTextArea textAreaMemory;
    public JTextField textFieldReadyQueue;
    public JTextField textFieldBlockQueue1;
    public JTextField textFieldBlockQueue2;
    public JTextField textFieldBlockQueue4;
    public JTextField textFieldBlockQueue5;
    public JTextArea textAreaBuffer;
    public JTextArea textAreaMain;
    public JTextArea textAreaStatus;
    public JTextField textFieldFallBack;


    public MainWindow() {
    textAreaMain.setLineWrap(true);
    textAreaBuffer.setLineWrap(true);
    textAreaStatus.setLineWrap(true);
    textAreaMemory.setLineWrap(true);



    textAreaMain.setEditable(false);
    textAreaBuffer.setEditable(false);
    textAreaStatus.setEditable(false);
    textAreaMemory.setEditable(false);

    textFieldCurrent.setEditable(false);
    textFieldBlockQueue1.setEditable(false);
    textFieldBlockQueue2.setEditable(false);
    textFieldBlockQueue4.setEditable(false);
    textFieldBlockQueue5.setEditable(false);
    textFieldFallBack.setEditable(false);
    textFieldReadyQueue.setEditable(false);

    ButtonStart.addActionListener(new ActionListener() {
        @Override
        public void actionPerformed(ActionEvent e) {
            try {
                Main.main_application();
            } catch (IOException ex) {
                throw new RuntimeException(ex);
            }
        }
    });
    ButtonAdd.addActionListener(new ActionListener() {
        @Override
        public void actionPerformed(ActionEvent e) {
            synchronized (Clock_thread.class) {
                FileDialog dialog = new FileDialog(new Frame(),"选择作业",FileDialog.LOAD);
                dialog.setVisible(true);
                String dir = dialog.getDirectory();
                if(dir == null || dir.length() == 0) return; // not submit
                //System.out.println(dir);
                synchronized (HardDisk.class) {
                    try {
                        HardDisk.submit(dir);
                    } catch (IOException ex) {
                        throw new RuntimeException(ex);
                    }
                }
                //System.out.println("---添加成功---");
            }
        }
    });

    ButtonExit.addActionListener(new ActionListener() {
        @Override
        public void actionPerformed(ActionEvent e) {
            String mainText = Main.mainWindow.textAreaMain.getText();
            String mainBuffer = Main.mainWindow.textAreaBuffer.getText();
            String mainStatus = Main.mainWindow.textAreaStatus.getText();
            StringBuilder sb = new StringBuilder();
            sb.append("作业/进程调度事件：").append("\n");
            sb.append(mainText).append("\n\n");
            sb.append("缓冲区处理事件：").append("\n");
            sb.append(mainBuffer).append("\n\n");
            sb.append("状态统计信息：").append("\n");
            sb.append(mainStatus);
            String fileName = "ProcessResults-" + Clock_thread.COUNTTIME + "-JTYX.txt";
            String dir = "./output2";
            File file = new File(dir + File.separator + fileName);
            if(file.exists()) {
                file.delete(); //delete the file that has existed
            }
            try {
                file.createNewFile();
            } catch (IOException ex) {
                throw new RuntimeException(ex);
            }
            FileWriter fw = null;
            try {
                fw = new FileWriter(file);
                fw.write(sb.toString());
                fw.close();
                //System.out.println("write successfully");
                //System.out.println(file.getAbsoluteFile());
            } catch (IOException ex) {
                throw new RuntimeException(ex);
            }
            System.exit(0);
        }
    });
    }
}
