package com.amiba.lock.service;

import com.amiba.thread.entity.MyList;

import java.util.List;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

public class ConsumerAndProducer {
    private ReentrantLock lock = new ReentrantLock();
    private Condition condition = lock.newCondition();
    private boolean hasValue = false;

    public void producer(String url) {
        try {
            lock.lock();
            while (MyList.getSize() ==1) {
                condition.await();
            }
            MyList.add(url);
            condition.signalAll();
            //代表结束
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            lock.unlock();
        }
    }

    public void consumer() {
        try {
            lock.lock();
            while (MyList.getSize() == 0) {
                condition.await();
            }
            String url = MyList.getList().get(0).toString();
            System.out.println("ThreadName=" + Thread.currentThread().getName() + "    " + url);
            MyList.getList().remove(0);
            condition.signalAll();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            lock.unlock();
        }
    }
}
