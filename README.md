# 多线程学习

### 进程和多线程的概念及线程的优点？
  讲到多线程技术时，就不得不提及"进程"这个概念了。百度对进程的接受如下：  
<b>进程</b>是操作系统的基础，是一次程序的执行，是一个程序及其数据在处理机上顺序执行时所发生的活动；是程序在一个数据集合运行的过程，他是系统的资源分配和调度的一个独立单位。你可以将操作系统中运行的.exe程序理解成一个“进程”，进程是受操作系统管理的基本运行单元。  
<b>那么什么是线程呢？</b>
我们可以把它理解成进程中独立运行的子程序。比如，QQ.exe运行时就有很多的子任务在同时运行，也就是说可以在同一时间执行不同的功能。  
<b>使用多线程的优点：</b>它可以最大限度地利用CPU的空闲时间来处理其他的任务，比如一边让操作系统处理正在由打印机打印的数据，一边编辑word文档。而CPU在这些任无之间不停的切换，由于切换速度十分快，给使用者的感受就是这些任务似乎是同事运行的，所以使用多线程后，可以在同一时间内运行更多不同种类的任务。  

<b>注意：</b>多线程是异步的，所以千万不要把Eclipse里代码的执行顺序当成线程的执行顺序，<b>线程被调用的时机是随机的</b>。

### 使用多线程
##### java多线程编程与技术
一个进程正在运行时至少会有一个线程在运行，这种情况在java中也是存在的。这些线程在后台运行，如main()方法的线程就是这样，而且它是由JVM创建的。  
<b>继承Thread类</b>  
在java的JDK开发中，已经自带有对多线程的支持，实现多线程编程的方法有两种，一是继承Thread类，另一种是继承Runnable接口。  
从Thread的源码上看Thread类实现了Runnable借口，他们之间具有多态性。  
在使用继承Thread类的方式实现多线程时，最大的局限就是不支持多继承，因为java语言的特点就是单根继承，所以为了支持多继承，完全可以实现Runnable接口的方式，一边实现一边继承，但这两种方式在工作时的性质是一样的，没有本质区别。

线程被调用执行的顺序是随机的，具有随机性，这里用一个小例子来证实。
``` JAVA
1. MyThread类
public class MyThread extends Thread {
    @Override
    public void run() {
        try {
            for (int i = 0; i < 10; i++) {
                int time = (int) (Math.random() * 1000);
                Thread.sleep(time);
                System.out.println("run=" + Thread.currentThread().getName());
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
2、调用类
public static void main(String[] args) {
        try {
            MyThread thread = new MyThread();
            thread.setName("myThread");
            thread.start();
            for (int i = 0; i < 10; i++) {
                int time = (int) (Math.random() * 1000);
                Thread.sleep(time);
                System.out.println("main=" + thread.currentThread().getName());
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
}
执行结果如下:
run=myThread
main=main
run=myThread
main=main
run=myThread
main=main
run=myThread
main=main
main=main  。。。。。。。。。
```
<b>实现Runnable接口</b>
如果欲创建的线程类已有一个父类，这是不能直接继承Thread，因为java不支持多继承，所以就需要实现Runnable接口来应对这样的情况，边实现边继承。  
下面来看一些Thread的构造方法
Thread拥有8个构造函数：
其中有两个Thread(Runnable target) 和Thread(Runnable target,String name),可以传递Runnable接口，不关如此，还可以传入一个Thread类的对象，这样完全可以将一个Thread对象的run()交由其它线程调用。

<b>实例变量与线程安全</b>  
自定义线程类的实例变量针对其他线程可以有共享和不共享之分，这在多个线程之间进行交互是一个技术点。  
数据不共享：变量不共享  
如：同是创建三个线程，每个线程都有各自的count，自己减少自己的count值，相互没相关。  
数据共享：多个线程可以访问同一个变量，如投票功能、点赞。
测试代码如下：  
``` java
public class MyThread extends Thread {
    private int count=5;
    @Override
    public void run() {
        super.run();
        count--;
        System.out.println("由 "+this.currentThread().getName()+"  计算，coutn="+count);
    }
}
public static void main(String[] args) {
        MyThread myThread=new MyThread();
        Thread a=new Thread(myThread,"A");
        Thread b=new Thread(myThread,"B");
        Thread c=new Thread(myThread,"C");
        Thread d=new Thread(myThread,"D");
        Thread e=new Thread(myThread,"E");
        a.start();
        b.start();
        c.start();
        d.start();
        e.start();
}
结果如下：
由 C  计算，coutn=3
由 B  计算，coutn=3
由 A  计算，coutn=2
由 D  计算，coutn=1
由 E  计算，coutn=0
```
我们可以发现，C和B打印出的都是3，产生了“非线程安全”问题，而我们想要得到的结果却不是重复的。而是依次递减的。在某些jvm中，i--的操作要分为如下3步：  
1)取得原有i值
2)计算i-1.
3)对i进行赋值。
在这三个步骤中，如果有多个线程同时访问，那么一定会出现非线程安全问题。
那么这时可给run()加一个同步关键字synchronized，确保其线程安全。  
``` java
由 B  计算，coutn=4
由 D  计算，coutn=3
由 C  计算，coutn=2
由 E  计算，coutn=1
由 A  计算，coutn=0
```
