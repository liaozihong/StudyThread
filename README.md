# 多线程学习

### 进程和多线程的概念及线程的优点？
  讲到多线程技术时，就不得不提及"进程"这个概念了。百度对进程的接受如下：  
**进程**是操作系统的基础，是一次程序的执行，是一个程序及其数据在处理机上顺序执行时所发生的活动；是程序在一个数据集合运行的过程，他是系统的资源分配和调度的一个独立单位。你可以将操作系统中运行的.exe程序理解成一个“进程”，进程是受操作系统管理的基本运行单元。  
**那么什么是线程呢？**  
我们可以把它理解成进程中独立运行的子程序。比如，QQ.exe运行时就有很多的子任务在同时运行，也就是说可以在同一时间执行不同的功能。  
**使用多线程的优点：**它可以最大限度地利用CPU的空闲时间来处理其他的任务，比如一边让操作系统处理正在由打印机打印的数据，一边编辑word文档。而CPU在这些任无之间不停的切换，由于切换速度十分快，给使用者的感受就是这些任务似乎是同事运行的，所以使用多线程后，可以在同一时间内运行更多不同种类的任务。  

**注意：**多线程是异步的，所以千万不要把Eclipse里代码的执行顺序当成线程的执行顺序，<b>线程被调用的时机是随机的</b>。

### 使用多线程
##### java多线程编程与技术
一个进程正在运行时至少会有一个线程在运行，这种情况在java中也是存在的。这些线程在后台运行，如main()方法的线程就是这样，而且它是由JVM创建的。  
**继承Thread类** 
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
**实现Runnable接口**  
如果欲创建的线程类已有一个父类，这是不能直接继承Thread，因为java不支持多继承，所以就需要实现Runnable接口来应对这样的情况，边实现边继承。  
下面来看一些Thread的构造方法
Thread拥有8个构造函数：
其中有两个Thread(Runnable target) 和Thread(Runnable target,String name),可以传递Runnable接口，不关如此，还可以传入一个Thread类的对象，这样完全可以将一个Thread对象的run()交由其它线程调用。

**实例变量与线程安全**    
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
此处插入一个注意点：Thread直接调用run()和调用start()的区别:  
1. start()方法来启动线程，真正实现了多线程运行，这时无需等待run方法体代码执行完毕而直接继续执行下面的代码：  
通过调用Thread类的start()方法来启动一个线程，
这时此线程是处于就绪状态，
并没有运行。
然后通过此Thread类调用方法run()来完成其运行操作的，
这里方法run()称为线程体，
它包含了要执行的这个线程的内容，
Run方法运行结束，
此线程终止，
而CPU再运行其它线程，

2. run（）方法当作普通方法的方式调用，程序还是要顺序执行，还是要等待run方法体执行完毕后才可继续执行下面的代码：  
而如果直接用Run方法，
这只是调用一个方法而已，
程序中依然只有主线程--这一个线程，
其程序执行路径还是只有一条，
这样就没有达到写线程的目的。

**currentThread()方法**  
currentThread()方法可返回代码段正在被哪个线程调用的信息。   
**isAlive()方法**  
功能是判断当前的线程是否出于活动状态  
**sleep()方法**  
作用是在指定的毫秒数内让当前"正在执行的线程"休眠(暂停执行)。这个“正在执行的线程”是指this.currentThread()返回的线程   
**getId()**  
作用是取得线程的唯一标识。   
####停止线程  
在java中有以下三种方法可以终止正在运行的线程：  
1)使用退出标志，使线程正常退出，也就是当run方法完成后程序终止；  
2)使用stop方法强行终止线程，不推荐，因为stop和suspend及resume一样，都是作废过期的方法，使用它们会发生不可预期的后果;  
3)使用interrupt方法中断线程。  
调用Interrupt()并不像for+break那样立竿见影，他仅仅是在当前线程中打了一个停止的标记，并不是真的停止线程。  
**判断线程是否是停止状态**  
1)this.interrupted()测试当前线程是否已经中断，执行后具有将状态标志置清除为false的功能；  
2)this.isInterrupted()测试线程是否已经中断状态，但不清除状态标志；  
注意：如果在sleep状态下停止某一线程，会进入catch语句，并且清除停止状态值，使之变成false  
**暴力停止**  
使用stop()方法停止线程是非常暴力的，调用时会抛出java.lang.ThreadDeach异常，在通常情况下，此异常不需要显示捕捉。  
使用stop释放锁的不良后果，会给数据造成不一致的结果。  
使用interrupt()与return结合使用也能停止线程的结果。不过还是建议使用“抛异常”的方法实现线程的停止，因为在catch块中还可以将异常上抛，使异常停止
的事件得以传播。  
**暂停线程**  
暂停线程意味着此线程可以恢复运行。可以使用suspend()方法暂停。使用resume()方法恢复线程的运行。  
**注意**  
在java1.2之后这些方法已被弃用，因为它们有可能造成严重的系统错误和异常。  
首先suspend()方法不会释放线程所占用的资源(独占)。如果使用该方法将某个线程挂起，则可能会使其他等待资源的线程死锁。而resume()方法本身并无问题，但是不能独立于suspend()方法存在。   
在者，在使用suspend与resume方法是也容易出现因为线程的暂停而导致数据不同步的情况。(不同步)  
其次调用stop()可能会导致严重的系统故障。因为该方法会使线程立刻中断指令执行，不管这段方法是否执行完毕。如果这个线程正在做重要的操作，对程序的运行起着支撑作用，这时如果突然中断其执行则会导致系统崩溃。   
现在，这些方法已经不适合挂起和终止线程了，但是可以在run()方法中设置一些标志，通过在线程内部检测标志判断并调用wait()方法和notify()方法操作线程的挂起、恢复和正常终止。   
**yield方法**  
yield方法的作用是放弃当前的CPU资源，将他交个其它的任务去占用CPU执行时间。但放弃的时间不确定，有可能刚刚放弃，马上又获得CPU时间片。  
**线程的优先级**
线程可以划分优先级，优先级较高的线程得到的CPU资源较多，也是CPU优先执行优先级较高的线程对象中的任务。
设置线程优先级有助于帮“线程规划器”确定在下一次选择哪一个线程来优先执行。设置线程的优先级使用setPriority()方法，优先级分为1~10这10个级别，大于或小于他们或抛
IllegalArgumentException()。另外，线程的优先级是有继承性的，如线程A去启动线程B，那么线程B和A的优先级相同。  
线程的优先级与代码执行顺序无关，它具有一定的规则性，也就是CPU尽量将执行资源让给优先级比较高的线程。  
*优先级具有随机性*，优先级高的优先执行完run()，这个结果不能说得太肯定，因为线程的优先级还具有“随机性”，也就是优先性高的不一定每一次都先执行完。  
那么得出一个结论：不要把线程的优先级与运行结果的顺序作为衡量的标准，优先级较高的线程并不一定每一次都先执行完run()中的任务。也就是说线程优先级和打印顺序无关，不要将两者相关联，他们的关系具有不确定性和随机性。  
##### 守护线程(daemon)  
在java线程中有两种线程：用户线程、守护线程。  
守护线程是一种特殊的线程，特征：陪伴，进程中没有其他非守护线程，它也没有存在的意义了，则自动销毁，典型的守护形成是垃圾回收线程。  
举例：任何一个守护线程都是整个jvm中所有非守护线程的“保姆”，只要当前jvm存在一个非守护线程，守护线程就在工作，当最后一个非守护线程结束时，它才跟着结束。
典型的应用是GC（垃圾回收器），他就是一个称职的守护者。  
### 对象及变量的并发访问  
“非线程安全”问题存在于“实例变量”中，如果是方法内部的私有变量，则不存在“非线程安全”问题，所得结果也就是“线程安全”的了。  
两个线程访问同一个对象中的同步方法是一定是线程安全的。
关键字synchronized取得的锁都是对象锁吗，而不是把一段代码或方法(函数)当成锁，哪个线程先执行带sychronized关键字的方法，哪个线程就持有该方法所属对象的锁，那么其他线程只能呈等待状态，前提是多个线程访问的都是同一个对象。  
同步的单词是synchronized，异步的单词是asynchroized。  
调用关键字synchronized声明的方法一定是排队运行的。“共享”，只有共享资源的读写访问才需要同步化，如果不是共享资源，那么根本就没有同步的必要。  
**脏读**  
发生脏读的情况是在读取实例变量时，此值已经被其他线程更改过了。下面举个实例：  
``` java
public class PublicVar {
    public String userName="A";
    public String passWord="AA";
    synchronized public void setValue(String userName,String passWord){
        try{
            this.userName=userName;
            Thread.sleep(5000);
            this.passWord=passWord;
            System.out.println("setValue method thread name="+Thread.currentThread().getName()
                +" username="+userName+" password="+passWord);
        }catch (InterruptedException e){
            e.printStackTrace();
        }
    }
    public void getValue(){
        System.out.println("getValue method thread name="+Thread.currentThread().getName()+" username="+
        userName+" password="+passWord);
    }
}
public class MyThread extends Thread {
    private PublicVar publicVar;
    public MyThread(PublicVar publicVar){
        super();
        this.publicVar=publicVar;
    }
    @Override
    public void run() {
        super.run();
        publicVar.setValue("B","BB");
    }
}
public static void main(String[] args) {
        try{
            PublicVar publicVar=new PublicVar();
            MyThread thread=new MyThread(publicVar);
            thread.start();
            Thread.sleep(200);
            publicVar.getValue();
        }catch (InterruptedException e){
            e.printStackTrace();
        }
}
得到的结果却是  
getValue method thread name=main username=B password=AA  
setValue method thread name=Thread-0 username=B password=BB
```  
上面的结果明显出现了脏读现象，因为getValue()不是同步的。解决方法是在getValue中加入同步方法。  
脏读一定会出现操作实例变量的情况下，这就是不同线程“争抢”实例变量的结果。  
**锁重入**  
可重入锁的概念是：自己可以再次获取自己的内部锁。比如有一条线程获得某个对象的锁，此时这个对象锁还没有释放，当其再次想要获取这个对象的锁的时候还是可以获取的。如果是不可重入锁的话，就会造成死锁。可重入锁也支持在父子类继承的环境下。实例：  
``` java
public class PublicVar {
    synchronized public void service1(){
        System.out.println("大帅1");
        service2();
    }
    synchronized  public void service2(){
        System.out.println("大帅2");
        service3();
    }
    synchronized  public void service3(){
        System.out.println("大帅3");
    }
}
public class MyThread extends Thread {
    @Override
    public void run() {
        super.run();
        PublicVar publicVar=new PublicVar();
        publicVar.service1();
    }
}
public static void main(String[] args) {
            MyThread myThread=new MyThread();
            Thread t1=new Thread(myThread);
            t1.start();
}
结果：  
大帅1
大帅2
大帅3
```  
关键字synchronized拥有锁重入的功能，也就是在使用synchronized时，当一个线程得到一个对象锁后，在次请求此对象锁时是可以再次得到该对象的锁的。这也证明在一个
synchronized方法/块的内部调用本类的其他synchronized方法/块时，是永远可以得到锁的。
**出现异常，锁自动释放**  
当一个线程执行的代码出现异常时，其所持有的锁会自动释放。  






