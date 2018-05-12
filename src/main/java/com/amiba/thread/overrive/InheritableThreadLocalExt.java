package com.amiba.thread.overrive;

import java.time.Instant;
import java.util.Date;

public class InheritableThreadLocalExt extends InheritableThreadLocal {
    /**
     * 设置默认值
     */
    @Override
    protected Object initialValue() {
        return System.currentTimeMillis();
    }

    /**
     * 子线程修改值
     */
    @Override
    protected Object childValue(Object parentValue) {
        return parentValue+"我在子线程加的~~~";
    }
}
