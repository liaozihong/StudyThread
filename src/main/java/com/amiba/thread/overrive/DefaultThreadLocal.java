package com.amiba.thread.overrive;

public class DefaultThreadLocal extends ThreadLocal{
    @Override
    protected Object initialValue() {
        return "我是默认值 第一次get 不在为null";
    }
}
