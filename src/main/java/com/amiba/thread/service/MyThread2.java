package com.amiba.thread.service;


import com.amiba.thread.entity.MyList;
import com.amiba.thread.entity.Service;

public class MyThread2 extends Thread{
    private Service service;
    public MyThread2(Service service){
        super();
        this.service=service;
    }
    @Override
    public void run() {
    }
}
