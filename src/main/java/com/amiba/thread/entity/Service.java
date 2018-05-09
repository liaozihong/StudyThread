package com.amiba.thread.entity;

import java.util.List;

public class Service {
    private boolean isRunning=true;

    public void runMethod(){
        String anyString=new String();
        while(isRunning){
            synchronized (anyString){

            }
        }
        System.out.println("停下来了");
    }
    public void stopMethod(){
        isRunning=false;
    }
}
