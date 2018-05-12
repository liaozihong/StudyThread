package com.amiba.lock.test;

import com.amiba.lock.service.MyService;

public class Test {
    public static void main(String[] args) throws InterruptedException{
        MyService myService=new MyService();
        Thread awaitA=new Thread(new Runnable() {
            @Override
            public void run() {
                myService.awaitA();
            }
        },"A");
        Thread awaitB=new Thread(new Runnable() {
            @Override
            public void run() {
                myService.awaitB();
            }
        },"B");
        awaitA.start();
        awaitB.start();
        Thread.sleep(3000);
        Thread signalA=new Thread(new Runnable() {
            @Override
            public void run() {
                myService.sigalAll_A();
            }
        },"AA");
        signalA.start();
    }
}
