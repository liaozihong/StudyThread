package com.amiba.thread.entity;

import java.util.ArrayList;
import java.util.List;

public class MyList {
    private static List list=new ArrayList<>();
    private static int maxCount;
    public static boolean flag=true;
    public static void add(String data){
            list.add(data);
    }
    synchronized  public static int getSize(){
        return list.size();
    }

     public static List getList() {
        return list;
    }

    public static int getMaxCount() {
        return maxCount;
    }

    public static void setMaxCount(int maxCount) {
        MyList.maxCount = maxCount;
    }
}
