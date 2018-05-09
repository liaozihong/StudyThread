package com.amiba.thread.service;

import com.amiba.thread.entity.MyList;
import com.amiba.thread.entity.Service;
import com.amiba.thread.entity.Sub;

public class MyThread  {
     private boolean isRunning=true;

     public void runMethod(){
         while(isRunning){

         }
         System.out.println("停下来了");
     }
    public void stopMethod(){
        isRunning=false;
    }

}
