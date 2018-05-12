package com.amiba.lock.test;

import com.amiba.lock.service.MyService2;

public class Test2 {
    public static void main(String[] args) throws InterruptedException{
        MyService2 myService2=new MyService2();
        Thread t1=new Thread(new Runnable() {
            @Override
            public void run() {
                myService2.await();
            }
        });
        t1.start();
        Thread.sleep(2000);
        myService2.singal();
    }
}
