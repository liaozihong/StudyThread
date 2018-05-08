package com.amiba.thread.entity;

public class PublicVar {
    synchronized public void service1(){
        System.out.println("大帅1");
        service2();
    }
    synchronized  public void service2(){
        System.out.println("大帅2");
        service3();
    }
    synchronized  public void service3(){
        System.out.println("大帅3");
    }
}
