package com.amiba.thread.main;

import com.amiba.thread.entity.MyList;
import com.amiba.thread.entity.Service;
import com.amiba.thread.entity.Sub;
import com.amiba.thread.service.MyThread;
import com.amiba.thread.service.MyThread2;
import com.amiba.thread.service.Mythread3;


public class Run1 {
    public static void main(String[] args) {
        try {
            Service service=new Service();
            Thread thread1=new Thread(new Runnable() {
                @Override
                public void run() {
                    service.runMethod();
                }
            },"A");
            thread1.start();
            Thread.sleep(1000);
            Thread thread2=new Thread(new Runnable() {
                @Override
                public void run() {
                    service.stopMethod();
                }
            },"B");
            thread2.start();
            System.out.println("已经发起停止的命令");
        }catch (InterruptedException e){
            e.printStackTrace();
        }
    }
}
