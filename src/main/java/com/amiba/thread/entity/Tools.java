package com.amiba.thread.entity;

import com.amiba.thread.overrive.InheritableThreadLocalExt;

import java.util.Date;

public class Tools {
    public static ThreadLocal<Date> t1=new ThreadLocal<>();

    public static InheritableThreadLocalExt inheritableThreadLocalExt=new InheritableThreadLocalExt();
}
