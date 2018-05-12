package com.amiba.thread.entity;


public class Consumer {
    private String lock;

    public Consumer(String lock) {
        super();
        this.lock = lock;
    }
    public void obtainData(){
//        try {
            synchronized (lock) {
                while(MyList.getSize()>0) {
                    System.out.println(Thread.currentThread().getName()+"  处理数据"+MyList.getList().get(0));
                    MyList.getList().remove(0);
                    lock.notifyAll();
                }
//                lock.wait();
            }
//        } catch (InterruptedException e) {
//            e.printStackTrace();
//        }
    }
}
