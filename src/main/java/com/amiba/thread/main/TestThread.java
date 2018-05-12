package com.amiba.thread.main;

import com.amiba.thread.entity.Consumer;
import com.amiba.thread.entity.MyList;
import com.amiba.thread.entity.Producer;

public class TestThread {
    public static void main(String[] args) throws InterruptedException{
        String lock=new String("");
        MyList.setMaxCount(5);
        Producer producer=new Producer(lock);
        Consumer consumer=new Consumer(lock);
        Thread tP=new Thread(new Runnable() {
            @Override
            public void run() {
                    producer.subtract();
            }
        });
        Thread tC=new Thread(new Runnable() {
            @Override
            public void run() {
                while (MyList.flag){
                    consumer.obtainData();
                }
            }
        },"C1");
        Thread tC2=new Thread(new Runnable() {
            @Override
            public void run() {
               while (MyList.flag){
                    consumer.obtainData();
                }
            }
        },"C2");
        Thread tC3=new Thread(new Runnable() {
            @Override
            public void run() {
                while (MyList.flag){
                    consumer.obtainData();
                }
            }
        },"C3");
        Thread tC4=new Thread(new Runnable() {
            @Override
            public void run() {
                while (MyList.flag){
                    consumer.obtainData();
                }
            }
        },"C4");
        tP.start();
        tC.start();
        tC2.start();
        tC3.start();
        tC4.start();
        System.out.println("结束了!");
    }
}
