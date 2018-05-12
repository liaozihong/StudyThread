package com.amiba.thread.entity;


import java.io.InputStream;

public class Producer {
    private String lock;

    public Producer(String lock) {
        super();
        this.lock = lock;
    }

    public void subtract() {
        try {
            synchronized (lock) {
                for (int i = 0; i < MyList.getMaxCount(); i++) {
                    while (MyList.getSize()==0) {
                        MyList.add("aaa" + i);
                    }
                    lock.wait();
                }
                MyList.flag=false;
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
