package com.amiba.lock.test;

import com.amiba.lock.service.ConsumerAndProducer;
import com.amiba.thread.entity.MyList;

import java.util.ArrayList;
import java.util.List;

public class ConsumeAndProducerTest {
    volatile static boolean flag=true;
    public static void main(String[] args) {

        ConsumerAndProducer consumerAndProducer=new ConsumerAndProducer();
        List<String> list=new ArrayList<>();
        for(int i=0;i<3;i++){
            list.add("aaaaaaaa      "+i);
        }
       new Thread(new Runnable() {
            @Override
            public void run() {
                for(int i=0;i<list.size();i++) {
                    consumerAndProducer.producer(list.get(i));
                    if(i==list.size()){
                        flag=false;
                    }
                }
            }
        },"TP").start();
        new Thread(new Runnable() {
            @Override
            public void run() {
                while (flag) {
                    consumerAndProducer.consumer();
                }
            }
        },"TC").start();
        new Thread(new Runnable() {
            @Override
            public void run() {
                while (flag) {
                    consumerAndProducer.consumer();
                }
            }
        },"TC2").start();

    }
}
