package com.shamming;

import com.shamming.hardware.CPU2023;
import com.shamming.hardware.Clock_thread;
import com.shamming.hardware.HardDisk;
import com.shamming.software.JobIn_thread;
import com.shamming.software.PageInterruption_thread;
import com.shamming.software.ProcessScheduling_thread;
import com.shamming.ui.MainWindow;
import com.shamming.util.DataFresh;

import javax.swing.*;
import java.io.IOException;

public class Main {
    public static MainWindow mainWindow = new MainWindow();
    public static void main_application() throws IOException {
        HardDisk.init(); // init the temporary job array
        new Thread(new PageInterruption_thread()).start(); // launch the interruption response thread
        new Thread(new Clock_thread()).start(); // launch the clock thread
        new Thread(new JobIn_thread()).start(); // launch the job-in thread
        new Thread(new ProcessScheduling_thread()).start();
        CPU2023 cpu = new CPU2023(); // create a cpu
        new Thread(cpu).start();
        new Thread(new DataFresh()).start(); //
        //System.out.println(HardDisk.tempJobs.size());
    }

    public static void main(String[] args) {
        JFrame frame = new JFrame("OS-2023 CS202 9203010723 Shamming Wang");
        frame.setContentPane(mainWindow.panel1);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(1080, 720);
        frame.setVisible(true);
    }
}
