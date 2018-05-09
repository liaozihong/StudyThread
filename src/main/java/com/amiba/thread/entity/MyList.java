package com.amiba.thread.entity;

import java.util.ArrayList;
import java.util.List;

public class MyList {
    private List list=new ArrayList<>();
    synchronized public void add(String data){
            list.add(data);
    }
    synchronized public int getSize(){
        return list.size();
    }
}
