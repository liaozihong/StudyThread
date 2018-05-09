package com.amiba.thread.entity;

public class Sub extends Main {
    @Override
    public  void serviceMethod() {
        try {
            System.out.println("int Sub 下一步 sleep begin threadName="
                    + Thread.currentThread().getName() + "time=" + System.currentTimeMillis());
            Thread.sleep(5000);
            System.out.println("int Sub 下一步 sleep end threadName="
                    + Thread.currentThread().getName() + "time=" + System.currentTimeMillis());
            super.serviceMethod();
        }catch (InterruptedException e){
            e.printStackTrace();
        }
    }
}
