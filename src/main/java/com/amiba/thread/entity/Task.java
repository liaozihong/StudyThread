package com.amiba.thread.entity;

import java.util.regex.Pattern;

public class Task {
    private String getData1;
    private String getData2;
    public void doLongTimeTask() {
        try {
            System.out.println("begin task");
            Thread.sleep(3000);
            String privateGetData1 = "长时间处理任务后从远程返回的值 1 threadName=" +
                    Thread.currentThread().getName();
            String privateGetData2 = "长时间处理任务后从远程返回的值 2 threadName=" +
                    Thread.currentThread().getName();
            //在此处用同步块的好处是，只同步这两个关键的赋值，确保速率增快。
            synchronized (this) {
                getData1 = privateGetData1;
                getData2 = privateGetData2;
            }
            System.out.println(getData1);
            System.out.println(getData2);
            System.out.println("end task");
        }catch (InterruptedException e){
            e.printStackTrace();
        }
    }
}
