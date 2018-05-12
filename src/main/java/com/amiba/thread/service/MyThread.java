package com.amiba.thread.service;

import com.amiba.thread.entity.Tools;

import java.util.Date;

public class MyThread  extends Thread{
    @Override
    public void run() {
        try{
            for(int i=0;i<10;i++){
                System.out.println("在线程A中取值"+Tools.inheritableThreadLocalExt.get());
                Thread.sleep(100);
            }
        }catch (InterruptedException e){
            e.printStackTrace();
        }
    }
}
