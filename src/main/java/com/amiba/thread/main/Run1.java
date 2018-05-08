package com.amiba.thread.main;

import com.amiba.thread.entity.PublicVar;
import com.amiba.thread.service.MyThread;
import com.amiba.thread.service.MyThread2;


public class Run1 {
    public static void main(String[] args) {
            MyThread myThread=new MyThread();
            Thread t1=new Thread(myThread);
            t1.start();
    }
}
