package com.amiba.lock.service;

import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class MyService {
    private Lock lock = new ReentrantLock();
    public Condition conditionA = lock.newCondition();
    public Condition conditionB = lock.newCondition();

    public void awaitA() {
        try {
            lock.lock();
            System.out.println("awaitA begin ThreadName=" + Thread.currentThread().getName() + "  time=" + System.currentTimeMillis());
            conditionA.await();
            System.out.println("awaitA end ThreadName=" + Thread.currentThread().getName() + "  time=" + System.currentTimeMillis());
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            lock.unlock();
        }
    }

    public void awaitB() {
        try {
            lock.lock();
            System.out.println("awaitB begin ThreadName=" + Thread.currentThread().getName() + "  time=" + System.currentTimeMillis());
            conditionB.await();
            System.out.println("awaitB end ThreadName=" + Thread.currentThread().getName() + "  time=" + System.currentTimeMillis());
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            lock.unlock();
        }
    }

    public void sigalAll_A() {
        try {
            lock.lock();
            System.out.println("sigalAll_A ThreadName=" + Thread.currentThread().getName() + "  time=" + System.currentTimeMillis());
            conditionB.signalAll();
        } finally {
            lock.unlock();
        }
    }

    public void awaitAll_B() {
        try {
            lock.lock();
            System.out.println("sigalAll_B ThreadName=" + Thread.currentThread().getName() + "  time=" + System.currentTimeMillis());
            conditionB.signalAll();
        } finally {
            lock.unlock();
        }
    }
}
