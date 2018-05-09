package com.amiba.thread.service;

import com.amiba.thread.entity.Service;

public class Mythread3 extends Thread{
     private Service service;
    public Mythread3(Service service){
        super();
        this.service=service;
    }
    @Override
    public void run() {
    }
}
