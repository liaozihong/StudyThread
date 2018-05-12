package com.amiba.thread.main;

import com.amiba.thread.entity.Tools;
import com.amiba.thread.overrive.DefaultThreadLocal;
import com.amiba.thread.service.MyThread;
import com.amiba.thread.service.MyThread2;

public class Run1 {
    public static void main(String[] args) throws InterruptedException {
        for (int i = 0; i < 10; i++) {
            System.out.println("     Main线程中取值" + Tools.inheritableThreadLocalExt.get());
            Thread.sleep(100);
        }
        Thread.sleep(5000);
        MyThread myThread = new MyThread();
        myThread.start();
    }
}
