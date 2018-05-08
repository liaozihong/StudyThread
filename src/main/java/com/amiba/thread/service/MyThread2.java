package com.amiba.thread.service;

import com.amiba.thread.entity.PublicVar;

public class MyThread2 extends Thread{
    @Override
    public void run(){
        super.run();
        PublicVar publicVar=new PublicVar();
        publicVar.service1();
    }
}
