package com.amiba.thread.service;


import com.amiba.thread.entity.Tools;

import java.util.Date;

public class MyThread2 extends Thread{
    @Override
    public void run() {
        try{
            for(int i=0;i<20;i++){
                if(Tools.t1.get()==null){
                    Tools.t1.set(new Date());
                }
                System.out.println("B"+Tools.t1.get().getTime());
                Thread.sleep(100);
            }
        }catch (InterruptedException e){
            e.printStackTrace();
        }
    }
}
