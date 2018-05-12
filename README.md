
目录：  
[TOC]
------------
**参考书籍** ：Java多线程的核心编程技术   
----
简介：个人学习多线程所记录；  
------
### 1.Java多线程技能
#### 1.1 进程和多线程的概念及线程的优点？
  讲到多线程技术时，就不得不提及"进程"这个概念了。百度对进程的接受如下：  
**进程**：是操作系统的基础，是一次程序的执行，是一个程序及其数据在处理机上顺序执行时所发生的活动；是程序在一个数据集合运行的过程，他是系统的资源分配和调度的一个独立单位。你可以将操作系统中运行的.exe程序理解成一个“进程”，进程是受操作系统管理的基本运行单元。  
**那么什么是线程呢？**  
我们可以把它理解成进程中独立运行的子程序。比如，QQ.exe运行时就有很多的子任务在同时运行，也就是说可以在同一时间执行不同的功能。  
**使用多线程的优点**：它可以最大限度地利用CPU的空闲时间来处理其他的任务，比如一边让操作系统处理正在由打印机打印的数据，一边编辑word文档。而CPU在这些任无之间不停的切换，由于切换速度十分快，给使用者的感受就是这些任务似乎是同事运行的，所以使用多线程后，可以在同一时间内运行更多不同种类的任务。  

**注意**：多线程是异步的，所以千万不要把Eclipse里代码的执行顺序当成线程的执行顺序，<b>线程被调用的时机是随机的</b>。

#### 1.2 使用多线程
一个进程正在运行时至少会有一个线程在运行，这种情况在java中也是存在的。这些线程在后台运行，如main()方法的线程就是这样，而且它是由JVM创建的。  
** 继承Thread类** 
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

#### 1.3 currentThread()方法  
currentThread()方法可返回代码段正在被哪个线程调用的信息。   
#### 1.4 isAlive()方法    
功能是判断当前的线程是否出于活动状态  
#### 1.5 sleep()方法   
作用是在指定的毫秒数内让当前"正在执行的线程"休眠(暂停执行)。这个“正在执行的线程”是指this.currentThread()返回的线程   
#### 1.6 getId()  
作用是取得线程的唯一标识。   
#### 1.7停止线程   
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
#### 1.8 暂停线程  
暂停线程意味着此线程可以恢复运行。可以使用suspend()方法暂停。使用resume()方法恢复线程的运行。  
**注意**  
在java1.2之后这些方法已被弃用，因为它们有可能造成严重的系统错误和异常。  
首先suspend()方法不会释放线程所占用的资源(独占)。如果使用该方法将某个线程挂起，则可能会使其他等待资源的线程死锁。而resume()方法本身并无问题，但是不能独立于suspend()方法存在。   
在者，在使用suspend与resume方法是也容易出现因为线程的暂停而导致数据不同步的情况。(不同步)  
其次调用stop()可能会导致严重的系统故障。因为该方法会使线程立刻中断指令执行，不管这段方法是否执行完毕。如果这个线程正在做重要的操作，对程序的运行起着支撑作用，这时如果突然中断其执行则会导致系统崩溃。   
现在，这些方法已经不适合挂起和终止线程了，但是可以在run()方法中设置一些标志，通过在线程内部检测标志判断并调用wait()方法和notify()方法操作线程的挂起、恢复和正常终止。   
#### 1.9 yield方法   
yield方法的作用是放弃当前的CPU资源，将他交个其它的任务去占用CPU执行时间。但放弃的时间不确定，有可能刚刚放弃，马上又获得CPU时间片。  
#### 1.10 线程的优先级    
线程可以划分优先级，优先级较高的线程得到的CPU资源较多，也是CPU优先执行优先级较高的线程对象中的任务。
设置线程优先级有助于帮“线程规划器”确定在下一次选择哪一个线程来优先执行。设置线程的优先级使用setPriority()方法，优先级分为1~10这10个级别，大于或小于他们或抛
IllegalArgumentException()。另外，线程的优先级是有继承性的，如线程A去启动线程B，那么线程B和A的优先级相同。  
线程的优先级与代码执行顺序无关，它具有一定的规则性，也就是CPU尽量将执行资源让给优先级比较高的线程。  
*优先级具有随机性*  
优先级高的优先执行完run()，这个结果不能说得太肯定，因为线程的优先级还具有“随机性”，也就是优先性高的不一定每一次都先执行完。  
那么得出一个结论：不要把线程的优先级与运行结果的顺序作为衡量的标准，优先级较高的线程并不一定每一次都先执行完run()中的任务。也就是说线程优先级和打印顺序无关，不要将两者相关联，他们的关系具有不确定性和随机性。  
#### 1.11 守护线程(daemon)  
在java线程中有两种线程：用户线程、守护线程。  
守护线程是一种特殊的线程，特征：陪伴，进程中没有其他非守护线程，它也没有存在的意义了，则自动销毁，典型的守护形成是垃圾回收线程。  
举例：任何一个守护线程都是整个jvm中所有非守护线程的“保姆”，只要当前jvm存在一个非守护线程，守护线程就在工作，当最后一个非守护线程结束时，它才跟着结束。
**注意**:守护线程通常不能用来替代应用程序管理各个服务的生命周期。  
典型的应用是GC（垃圾回收器），他就是一个称职的守护者。  
#### 1.12 终结器**   
当不需要内存资源时，可以通过垃圾回收器来回收他们，但对于其他一些资源，如文件句柄或者套接字句柄，当不在需要他们时，需要显示的交还给操作系统。为了实现这个功能，垃圾回收器对那些定义了finalize方法的对象会进行特殊处理：在回收器释放他们后，调用他们的finalize方法，从而保证一些持久化的资源被释放掉。由于终结器可以在某个JVM管理的线程中运行，因此终结器访问的任何状态都可能被多线程访问，这样就必须对其访问操作进行同步。终结器不能保证他们将在何时运行，甚至是否会运行，并且复杂的终结器通常还会在对象上产生巨大的性能开销。  
避免使用终结器

---------------------------

### 2. 对象及变量的并发访问  
#### 2.1 synchronize同步方法
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
**同步不具有继承性，同步不可被继承**  
也就是说，即使父类的方法中有同步关键词，等子类继承后，该方法还是的加上同步关键词，不然不同步。   
``` java  
public class Main {
    synchronized public void serviceMethod() {
        try {
            System.out.println("int main 下一步 sleep begin threadName="
                    + Thread.currentThread().getName() + "  time=" + System.currentTimeMillis());
            Thread.sleep(5000);
            System.out.println("int main 下一步 sleep end threadName="
                    + Thread.currentThread().getName() + "  time=" + System.currentTimeMillis());
        }catch (InterruptedException e){
            e.printStackTrace();
        }
    }
}
public class Sub extends Main {
    @Override
    public  void serviceMethod() {
        try {
            System.out.println("int Sub 下一步 sleep begin threadName="
                    + Thread.currentThread().getName() + "time=" + System.currentTimeMillis());
            Thread.sleep(5000);
            System.out.println("int Sub 下一步 sleep end threadName="
                    + Thread.currentThread().getName() + "time=" + System.currentTimeMillis());
            super.serviceMethod();
        }catch (InterruptedException e){
            e.printStackTrace();
        }
    }
}
public class MyThread extends Thread {
    private Sub sub;
    public MyThread(Sub sub){
        super();
        this.sub=sub;
    }
    @Override
    public void run() {
        sub.serviceMethod();
    }
}
public class MyThread2 extends Thread{
    private Sub sub;
    public MyThread2(Sub sub){
        super();
        this.sub=sub;
    }
    @Override
    public void run(){
        sub.serviceMethod();
    }
}
public static void main(String[] args) {
        Sub sub=new Sub();
        MyThread myThread=new MyThread(sub);
        MyThread2 myThread2=new MyThread2(sub);
        myThread.start();
        myThread2.start();
}
运行结果如下：  
int Sub 下一步 sleep begin threadName=Thread-1time=1525829379962
int Sub 下一步 sleep begin threadName=Thread-0time=1525829379962
int Sub 下一步 sleep end threadName=Thread-1time=1525829384963
int Sub 下一步 sleep end threadName=Thread-0time=1525829384963
int main 下一步 sleep begin threadName=Thread-1  time=1525829384963
int main 下一步 sleep end threadName=Thread-1  time=1525829389964
int main 下一步 sleep begin threadName=Thread-0  time=1525829389964
int main 下一步 sleep end threadName=Thread-0  time=1525829394965
```  
我们可以看出上面两条线程执行的时间是一致的，也就是那么是非同步了。因为子类的serviceMethod()并非同步的，若要同步，只需给它加上synchronized，由此也说明同步不具备继承性。  
#### 2.2 synchronized同步语句块  
用关键字synchronized声明方法在某些情况下是有弊端的，如A线程调用方法执行一个长时间的任务，那么B方法则必须等待比较长的时间，这样的情况下，可以使用synchronized同步语句块来解决。   
``` java
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
```  
通过上面的关键代码可以看出，加入把synchronized加在方法上，会导致执行时间变长，因为会执行多次sleep，这是我们可以使用同步块来解决，简单描述，当一个线程访问
Object的一个同步代码块时，*另一个线程仍然可以访问该object对象中的非synchronized(this)同步代码块*，同时，该代码块也将持有当前调用对象的锁。  
**synchronized代码间的同步性**：当一个线程访问object的一个synchronized(this)代码块时，其他线程再次访问这个synchronized(this)时，将会被阻塞，这说明synchronized
使用的“对象监视器”是一个。  
**将任意对象作为对象监视器**  
多个线程调用同一个对象中的不同名称的synchronized同步方法或synchronized(this)同步代码块时，调用的效果就是按顺序执行，也就是同步，阻塞。  
这说明synchronized和synchronized(this)分别有两种作用：  
synchronized同步方法：  
1)对其他synchronized和synchronized调用呈阻塞状态；
2)同一时间只有一个线程可以执行synchronized方法中的代码。
synchronized(this)同步代码块:  
1)对其他synchronized和synchronized调用呈阻塞状态；
2)同一时间只有一个线程可以执行synchronized|(this)同步代码块中的代码。  
JAVA还支持“任意对象”作为“对象监视器”来实现同步的功能，这个“任意对象”大多数是实例变量及方法的参数，使用格式为synchronized(非this对象x)同步代码块；  
1)在多个线程持有“对象监视器”为同一对象的前提下，同一时间只有一个线程可以执行synchronized(非this对象x)同步代码块中的代码。  
2)当持有“对象监视器”为同一个对象的前提下，同一时间内只有一个线程可以执行synchronized(非this对象x)同步代码块中的代码。  
``` java
public class Service {
    private String userName;
    private String userPass;
    /**
     * 对象监视器
     */
    private String anyString=new String();
    public void setUserNameUserPass(String userName,String userPass){
        try{
            synchronized (anyString) {
                System.out.println("名称="+Thread.currentThread().getName()+" 在"
                        +System.currentTimeMillis()+"  进入代码块");
                Thread.sleep(3000);
                System.out.println("名称="+Thread.currentThread().getName()+" 在"
                        +System.currentTimeMillis()+"  离开代码块");
            }
        }catch (InterruptedException e){
            e.printStackTrace();
        }
    }
}

public class MyThread extends Thread {
    private Service service;
    public MyThread(Service service){
        super();
        this.service=service;
    }
    @Override
    public void run() {
        service.setUserNameUserPass("a","AA");
    }
}
public class MyThread2 extends Thread{
    private Service service;
    public MyThread2(Service service){
        super();
        this.service=service;
    }
    @Override
    public void run() {
        service.setUserNameUserPass("b","BB");
    }
}
public static void main(String[] args) {
        Service sub=new Service();
        MyThread myThread=new MyThread(sub);
        MyThread2 myThread2=new MyThread2(sub);
        myThread.start();
        myThread2.start();
}
运行结果如下：
名称=Thread-0 在1525833830402  进入代码块
名称=Thread-0 在1525833833403  离开代码块
名称=Thread-1 在1525833833403  进入代码块
名称=Thread-1 在1525833836404  离开代码块

```  
锁非this对象具有一定的优点：如果一个类中有很多个synchronized的话，虽然能实现同步，但会受到阻塞，所以影响运行效率，这时是用同步代码块锁非this对象，它与其他同步方法是异步的，不会与其他锁this同步方法争抢this锁，则可以大大提高效率。
将上面的Service改一下：  
``` java
public class Service {
    private String userName;
    private String userPass;
    public void setUserNameUserPass(String userName,String userPass){
        try{
             String anyString=new String();
            synchronized (anyString) {
                System.out.println("名称="+Thread.currentThread().getName()+" 在"
                        +System.currentTimeMillis()+"  进入代码块");
                Thread.sleep(3000);
                System.out.println("名称="+Thread.currentThread().getName()+" 在"
                        +System.currentTimeMillis()+"  离开代码块");
            }
        }catch (InterruptedException e){
            e.printStackTrace();
        }
    }
}
运行结果如下：
名称=Thread-0 在1525834546959  进入代码块
名称=Thread-1 在1525834546959  进入代码块
名称=Thread-1 在1525834549960  离开代码块
名称=Thread-0 在1525834549960  离开代码块
```  
**注意**：使用synchronized(非this对象x)格式进行同步操作时，对象监视器(示例中的anyString)必须是同一个对象，如果不是，结果就是异步调用了，会交叉运行。  
线程的执行顺序是不稳定的，就可能出现问题，如当A和B两个线程执行带有分支判断的方法时，就会出现逻辑上的错误，有可能出现脏读，下面通过一个案例来查看，
``` java
public class Service {
    public MyList addServiceMethod(MyList list, String data){
        try{
            if(list.getSize()<1){
                //模拟取数据
                Thread.sleep(2000);
                list.add(data);
            }
        }catch (InterruptedException e){
            e.printStackTrace();
        }
        return list;
    }
}
public class MyThread extends Thread {
    private MyList myList;
    public MyThread(MyList myList){
        super();
        this.myList=myList;
    }
    @Override
    public void run() {
       Service service=new Service();
       service.addServiceMethod(myList,"aaaa");
    }
}
public class MyThread2 extends Thread{
    private MyList myList;
    public MyThread2(MyList myList){
        super();
        this.myList=myList;
    }
    @Override
    public void run() {
        Service service=new Service();
        service.addServiceMethod(myList,"bbbb");
    }
}
public static void main(String[] args) {
        try {
            MyList myList = new MyList();
            MyThread myThread = new MyThread(myList);
            MyThread2 myThread2 = new MyThread2(myList);
            myThread.start();
            myThread2.start();
            Thread.sleep(5000);
            System.out.println("最后的大小是" + myList.getSize());
        }catch (InterruptedException e){
            e.printStackTrace();
        }
}
执行结果如下：
最后的大小是2
```  
上述结果很明显就出现了脏读现象，原因是两个线程以异步的方式返回list参数的size()大小。解决的方法是同步化。更改Service  
``` java
public class Service {
    public MyList addServiceMethod(MyList list, String data){
        try{
            synchronized (list) {
                if (list.getSize() < 1) {
                    //模拟取数据
                    Thread.sleep(2000);
                    list.add(data);
                }
            }
        }catch (InterruptedException e){
            e.printStackTrace();
        }
        return list;
    }
}
执行结果如下：
最后的大小是1
```
由于list参数对象在项目中是一份实例，是单例的，而且也正需要对list参数的getSize()方法做同步的调用，所以就对list参数进行同步处理。  
**细化验证三个结论**  
“synchronized(非this对象x)”格式的写法是将x对象本身作为“对象监视器”，这样就得出三个结论：  
1)当多个线程同时执行synchronized(x){}同步代码块时呈同步效果。  
2)当其他线程执行x对象方法里面的synchronized同步方法时呈同步效果。
3)当其他线程执行x对象方法里面的synchronized(this)代码块时也呈现同步效果。  
但需要注意：如果其他线程调用不加synchronized关键字的方法时，还是异步调用。  
简单的提一下思路，加入有一个A类，和一个B类，B类中有一个方法，serviceMethod(A类)中使用同步块synchronized(A类)，这是加入A类中也存在同步方法或同步代码块，则他们也是呈现同步效果的。  
**静态同步synchronized方法与synchronized(class)代码块**  
关键词synchronized还可以应用在static静态方法上，如果这样写，那是对当前的*.java文件对应的Class类进行持锁。那一个示例说明：  
``` java
public class Service {
    synchronized public static void printA() {
        try {
            System.out.println("线程名称："
                    + Thread.currentThread().getName() + "  time=" + System.currentTimeMillis() + "进入printA");
            Thread.sleep(5000);
            System.out.println("线程名称："
                    + Thread.currentThread().getName() + "  time=" + System.currentTimeMillis() + "离开printA");
        }catch (InterruptedException e){
            e.printStackTrace();
        }
    }
    synchronized public static void printB() {
        try {
            System.out.println("线程名称："
                    + Thread.currentThread().getName() + "  time=" + System.currentTimeMillis() + "进入printB");
            Thread.sleep(5000);
            System.out.println("线程名称："
                    + Thread.currentThread().getName() + "  time=" + System.currentTimeMillis() + "离开printB");
        }catch (InterruptedException e){
            e.printStackTrace();
        }
    }
    synchronized public void printC() {
        try {
            System.out.println("线程名称："
                    + Thread.currentThread().getName() + "  time=" + System.currentTimeMillis() + "进入printC");
            Thread.sleep(5000);
            System.out.println("线程名称："
                    + Thread.currentThread().getName() + "  time=" + System.currentTimeMillis() + "离开printC");
        }catch (InterruptedException e){
            e.printStackTrace();
        }
    }
}
public static void main(String[] args) {
        try {
            Service service=new Service();
            Thread myThread = new Thread(new Runnable() {
                @Override
                public void run() {
                    service.printA();
                }
            });
            Thread myThread2 = new Thread(new Runnable() {
                @Override
                public void run() {
                    service.printB();
                }
            });
             Thread myThread3 = new Thread(new Runnable() {
                @Override
                public void run() {
                    service.printC();
                }
            });
            myThread.start();
            myThread2.start();
            myThread3.start();
            Thread.sleep(1000);
        }catch (InterruptedException e){
            e.printStackTrace();
        }
}
运行结果：
线程名称：Thread-0  time=1525844996442进入printA
线程名称：Thread-2  time=1525844996442进入printC
线程名称：Thread-0  time=1525845001442离开printA
线程名称：Thread-2  time=1525845001442离开printC
线程名称：Thread-1  time=1525845001442进入printB
线程名称：Thread-1  time=1525845006443离开printB
```  
从结果上看，A与C是异步的，原因是持有不同的锁，一个是对象锁，另一个是class锁，而Class可以对类的所有对象实例起作用。用一个例子来说明  
``` java
public static void main(String[] args) {
        try {
            Service service=new Service();
            Service service1=new Service();
            MyThread myThread = new MyThread(service);
            MyThread2 myThread2 = new MyThread2(service1);
            myThread.start();
            myThread2.start();
            Thread.sleep(1000);
        }catch (InterruptedException e){
            e.printStackTrace();
        }
}
执行结果：
线程名称：Thread-0  time=1525845461717进入printA
线程名称：Thread-0  time=1525845466717离开printA
线程名称：Thread-1  time=1525845466717进入printB
线程名称：Thread-1  time=1525845471717离开printB
```
可以看出，结果是同步的，说明synchronized静态方法，尽管是不同实例对象调用，也可以使其同步。同步synchronized(class)代码块的作用和synchronized static方法的作用是一样，
**String类型的常量池特性**大多数情况下，同步synchronized代码块都不使用String作为所对象，而改用其它，因为String类型在jvm中具有String常量池缓存的功能。  
**同步synchronized方法无限等待与解决**  
同步方法容易造成死循环，假如：  
在一个服务类里，有同步方法A()和同步方法B()，有两个线程分别取调用他们，先调用A在调用B，这是如果A方法出现死循环，则b将会无线等待。  
解决：我们可以将同步方法改成同步代码块，分别给他们new一个Object()的对象监视器，让他们各不影响。  
##### 多线程的死锁  
发生原因：因为不同的线程都在等待根本不可能被释放的锁，从而导致所有的任务都无法继续完成。只要互相等待对方释放资源就有可能出现死锁。   
在多线程程序中，死锁是必须避免的，它会导致程序假死。可以利用jdk自带的工具来监测是否有死锁的现象，用CMD命令，进入JDK的安装目录，进入bin，执行jps命令。  
会得到运行的线程Run的id值是3244(举例)，在执行jstack命令:jstack -l 3244，查看结果  
**所对象的改变**：在将任何数据类型作为同步锁时，需要注意的是，是否有多个线程同时持有锁对象，如果同时持有相同锁对象，则这些线程是同步的，如果是分别获得锁对象，那么线程间是异步的。示例：  
``` java
public class Service {
    private String lock="123";
    public void testMethod(){
        try{
            synchronized (lock){
                System.out.println("begin threadName="
                        + Thread.currentThread().getName() + "  time=" + System.currentTimeMillis());
                lock="456";
                Thread.sleep(2000);
                System.out.println("end threadName="
                        + Thread.currentThread().getName() + "  time=" + System.currentTimeMillis());
            }
        }catch (InterruptedException e){
            e.printStackTrace();
        }
    }
}
 public static void main(String[] args) {
        try {
            Service service=new Service();
            Thread myThread = new Thread(new Runnable() {
                @Override
                public void run() {
                    service.testMethod();
                }
            },"A");
            Thread myThread2 = new Thread(new Runnable() {
                @Override
                public void run() {
                    service.testMethod();
                }
            },"B");
            myThread.start();
            Thread.sleep(50);
            myThread2.start();
        }catch (InterruptedException e){
            e.printStackTrace();
        }
    }
    执行结果：
begin threadName=Thread-A  time=1525854170948
begin threadName=Thread-B  time=1525854170948
end threadName=Thread-A  time=1525854175948
end threadName=Thread-B  time=1525854175948
```  
明显结果是异步的，因为50毫秒过后线程B取得的锁已经是456.继续试验，将Thread.sleep(50)去掉,这时在试验，结果如下：  
``` java
begin threadName=A  time=1525854877060
end threadName=A  time=1525854879061
begin threadName=B  time=1525854879061
end threadName=B  time=1525854881061
```
结果是同步的，虽然锁改成了“456”，但是A和B争抢的锁是“123”。这里还要注意：只要对象不变，即使对象的属性发生改变，其结果也是同步的。  
#### 2.3 关键字volatile  
主要作用：使多个变量在多个线程中可见，强制从公共栈堆中取得变量的值，而不是从线程私有数据栈中取得变量的值。  

``` java
public class MyThread extends Thread {
    private boolean isRunning=true;

    public boolean isRunning() {
        return isRunning;
    }
    public void setRunning(boolean isRunning){
        this.isRunning=isRunning;
    }

    @Override
    public void run() {
        System.out.println("進入run了");
        while (isRunning){
        }
        System.out.println("线程停止了！");
    }
}
 public static void main(String[] args) {
        try {
            MyThread myThread=new MyThread();
            myThread.start();
            Thread.sleep(1000);
            myThread.setRunning(false);
            System.out.println("已经被赋值成false");
        }catch (InterruptedException e){
            e.printStackTrace();
        }
    }
执行结果如下：
進入run了
已经被赋值成false
```
从结果我们可以看出，System.out.println("线程停止了！")从未被执行，原因：  
在启动MyThread.java线程时，变量isRunning=true，存在于公共栈堆及线程的私有栈堆中，在jvm被设置为-server模式时为了线程运行的效率，线程一直在私有栈堆中取得isRunning的值是true，而代码myThread.setRunning(false)虽然被执行，更新的确是公共栈堆的isRunning的值，所以一直处于死循环状态。简单的说就是，私有栈堆中的值和公共栈堆中的值不同步造成的。解决问题的方法是使用volatile关键字，他的作用是从强制当前线程从公共栈堆中取值。  
``` java
修改Mythead
volatile private boolean isRunning=true;
执行结果为：
進入run了
已经被赋值成false
线程停止了！
```
使用volatile关键字增加了实例变量在多个线程之间的可见性。但volatile关键字最致命的缺点是不支持原子性。  

关键字volatile与synchronized进行比较：  
1)关键字volatile是线程同步的轻量级实现，所以volatile性能肯定比synchronized好，并且volatile只能修饰于变量，而synchronized可以修饰方法和代码块，随着jdk新版本的发布，synchronized关键字在执行效率上得到了很大的提升，在开发中synchronized关键字的比例较大。  
2)多线程访问volatile不会发生阻塞，synchronized会。  
3)valatile保证数据的可见性，但不能保证原子性，但synchronized都可以，因为它不会将私有内存中的数据做同步。  
4)volatile解决的是变量在多个线程之间的可见性，而synchronized关键字解决的是多个线程之间访问资源的同步性。  

**注意**：线程安全包含原子性和可见性两个方面。java的同步机制都是围绕这两个方面来确保线程安全的。  
关键字volatile的主要使用场景是在多个线程中可以感知实例变量被更改了，并且可以获得最新的值使用，也就是用多线程读取共享变量时可以获得最新值使用。  
关键字volatile提示线程每次从共享内存中读取变量，而不是从私有内存中取得，这样就保证了同步数据的可见性。但需注意，如果修改实例变量中的数据，比如i++，则这样的操作其实不是一个原子操作，也就是非线程安全的，他的操作步骤如下：  
1)从内存中取出i的值；  
2)计算i的值；  
3)将i写到内存中；  
这是假如在第二步时，另一个线程页修改了i，这时就会出现脏数据。解决的办法当然是用synchronized，之前也说了volatile本身并不处理数据的原子性，而是强制数据的读写及时影响到主内存的。  
用图来演示关键词volatile非线程安全的原因，变量在内存中工作过程如下图：  
我们可以得出结论：  
1)read和load阶段：从主存复制变量到当前线程中；  
2)use和assign阶段：执行代码，改变共享数值；  
3)store和write阶段：用工作内存数据刷新主存对应变量的值；  
![image](G:\studyThreadImage\threadStream.PNG)  
在多线程环境中，use和assign是多次出现的，但这一操作并不是原子性的，也就是在read和load之后，如果主内存count变量发生变化后，线程工作内存中的值由于已经加载，不会产生对应的变化，也就是私有内存和公共内存中的变量不同步，所以计算出来的结果会和预期的不一样，就出现了非线程安全。
对于用volatile修饰的变量，jvm虚拟机只是保证从主存加载到线程工作的值是最新的。解决的是变量读时的可见性问题，但无法保证原子性，对于多个线程访问同一实例变量还需要加锁同步。  
**使用int类型进行i++时，还可以使用AtomicInteger原子类实现**，原子操作是不能分割的整体，没有其他线程能够中断或检查正在原子操作的变量。一个原子类型就是原子操作可用的类型，他可以在没有锁的情况下做到线程安全。  
**注意原子类也不完全安全**，方法是原子性的，但是方法与方法之间的调用却不是原子性的，所以需要同步。
**synchronized代码块有volatile同步的功能**  
关键字synchronized可以使多个线程访问同一个资源具有同步性，而且它还具有将线程工作内存中的私有变量与公有内存中的变量同步的功能，在下面中验证：
``` java
public class Service {
    private boolean isRunning=true;

    public void runMethod(){
        while(isRunning){

        }
        System.out.println("停下来了");
    }
    public void stopMethod(){
        isRunning=false;
    }
}
public static void main(String[] args) {
        try {
            Service service=new Service();
            Thread thread1=new Thread(new Runnable() {
                @Override
                public void run() {
                    service.runMethod();
                }
            },"A");
            thread1.start();
            Thread.sleep(1000);
            Thread thread2=new Thread(new Runnable() {
                @Override
                public void run() {
                    service.stopMethod();
                }
            },"B");
            thread2.start();
            System.out.println("已经发起停止的命令");
        }catch (InterruptedException e){
            e.printStackTrace();
        }
}
运行结果如下：
已经发起停止的命令
```
得到这个结果是各线程间的数据值没有可视性造成的，而关键字synchronized可以具有可视性，更改Service的代码：
``` java
public class Service {
    private boolean isRunning=true;

    public void runMethod(){
        String anyString=new String();
        while(isRunning){
            synchronized (anyString){
                
            }
        }
        System.out.println("停下来了");
    }
    public void stopMethod(){
        isRunning=false;
    }
}
结果如下：
已经发起停止的命令
停下来了
```
正常退出了，关键字synchronized可以保证在同一时刻，只有一个线程可以执行某一个方法或某一个代码块，它包含两个特性：互斥性和可见性。它不仅可以解决一个线程看到对象不一致的状态，还可以保证进入同步方法或者同步代码块的每个线程，都看到由同一个锁保护之前所有的修改结果。  
学习多线程并发，着重“外练互斥，内练可见”，重要技术点。  

---------------

### 3. 线程之间的通信  
线程是操作系统中独立的个体，但这些个体如果不经过特殊的处理就不能成为一个整体。线程间的通信就是成为整体额的必用方案之一。使线程间进行通信后，系统间的交互性会更强大，在大大提高CUP利用率的同时还会使程序员对各线程任务在处理的过程中进行有效的把控与监督。
#### 3.1 等待/通知机制  
**不使用等待/通知机制实现线程间的通信**  
假如利用sleep()结合while(true)死循环来实现多个线程间的通信，虽然可以通信，但存在弊端，就是线程不停的通过while语句轮询机制来检测某一个条件，这样会浪费CPU资源。
如果轮询的时间间隔很大，有可能会取不到想要得到的数据。所以需要一种机制来实现减少CPU的资源浪费，而且还可以在多个线程间通信，那就是“wait/notify”机制。
**等待/通知机制的实现**  
方法wait()的作用是当前执行代码的线程进行等待，wait()方法是Object类的方法，该方法用来将当前线程置入“预执行队列”，并且在wait()所在的代码行处停止运行，直到接到通知或中断为止。在调用wait()之前，线程必须获得该对象得对象级别锁，即只能在同步方法或同步块中调用wait()方法。在执行wait()方法后，当前线程释放锁。在从wait()返回前，线程与其他线程竞争重新获得锁，如果调用wait()时没有持有适当的锁，则抛出IllegalMonitorStateException，它是RunntimeException的一个子类，因此，不需要try-catch抛出。  
方法notify()也要在同步方法或同步块中调用，即在调用之前，线程也必须获得该对象的对象级别锁，如果调用notify()是没有持有适当的锁，也会抛出IllegalMonitorStateException。该方法用来通知那些可能等待该对象的对象锁的其他线程，如果有多个线程等待，则由线程规划器随机挑选一个呈wait状态的线程，对其发出通知notify()方法后，当前线程不会马上释放锁，呈wait状态的线程也不能马上获得该对象锁，要等到执行notify()方法的线程将程序执行完，也就是退出synchronized代码块后，当前线程才会释放锁，而呈wait状态所在的线程才可以获取该对象锁。当第一个获得了该对象锁的wait线程运行完毕后，他会释放该对象锁，此时如果没有再次使用notify语句，则该对象呈空闲状态，其他wait状态等待的线程由于没有得到该对象的通知，还会继续阻塞在wait状态，直到这个对象发出一个notify或notifyAll。  

总的来说就是：wait是线程停止运行，而notify是停止的线程继续运行。  

**注意**:关键字synchronized可以将任何一个Object对象作为同步对象看待，而java为每个Object都实现了wait()和notify()方法，他们必须用在被synchronized同步的Object的临界区内。通过wait()方法可以处于临界区内的线程进入等待状态，同时释放被同步的锁。而notify操作可以唤醒一个因调用了wait操作而处于阻塞状态的线程，使其进入就绪状态。被重新唤醒的线程会试图重新获得临界区的控制权，也就是锁，并继续执行临界区内wait之后的代码。如果发出notify操作时没有处于阻塞状态的线程，那么命令被忽略。  
再次针对这几个方法进行解释：  
wait()可以使调用该方法的线程释放共享资源的锁，然后从运行状态退出，进入等候对列，直到被再次唤醒。  
notify()可以随机唤醒等候队列中等待同一共享资源的“一个”线程，并使该线程退出等候队列，进入可运行状态，就是notify()方法仅通知一个线程。  
notifyAll()方法可以使所有正在等待队列中等待同一共享资源的“全部”线程从等待状态退出，进入可运行状态。此时，优先级最高的那个线程最先执行，但也有可能随机运行，取决于JVM虚拟机的实现。  
下面用一张图来描述线程切换。  
![image](G:\studyThreadImage\threadSwitching.PNG)  
1)新创建一个新的线程对象后， 再调用它的start()方法， 系统会为此线程分配CPU资源， 使其处千Runnable (可运行）状态， 这是一个准备运行的阶段。如果线程抢占到CPU资 源， 此线程就处千Running (运行）状态。
2) Runnable状态和Running状态可相互切换， 因为有可能线程运行一段时间后， 有其他高优先级的线程抢占了CPU资源， 这时此线程就从Running状态变成Runnable状态。  
线程进入Runnable状态大体分为如下5种情况：  
调用sleep()方法后经过的时间超过了指定的休眠时间。  
线程调用的阻塞IO已经返回， 阻塞方法执行完毕。  
线程成功地获得了试图同步的监视器。  
线程正在等待某个通知， 其他线程发出了通知。  
处千挂起状态的线程洞用了resume恢复方法。  
3) Blocked 是阻塞的意思， 例如遇到了一个IO操作， 此时CPU处于空闲状态， 可能会转而把CPU时间片分配给其他线程， 这时也可以称为“暂停”状态。 Blocked状态结束后、
进入Runnable状态， 等待系统重新分配资源。   
出现阻塞的清况大体分为如下5种：  
线程调用sleep 方法， 主动放弃占用的处理器资源。  
线程调用了阻塞式IO方法， 在该方法返回前， 该线程被阻塞。  
线程试图获得一个同步监视器， 但该同步监视器正被其他线程所持有。  
线程等待某个通知。  
程序调用了suspend 方法将该线程挂起。此方法容易导致死锁， 尽量避免使用该方法。  
每个锁对象都有两个队列，一个是就绪对列，一个是阻塞队列。就绪对了存储将要获得锁的线程，阻塞队列存储被阻塞的线程。一个线程被唤醒后，才会进入就绪队列，等待CPU的调度；反之，一个线程被wait之后，就会进入阻塞队列，等待下一次唤醒。  
**方法wait()锁释放与notify锁不释放**  
当方法wait()被执行后，锁被自动释放，但执行完notify方法，锁却不释放，下面用一个例子测试：  
``` java
public class Service {
    public void testMethod(Object lock) {
        try{
            synchronized (lock) {
                System.out.println ("begin wait()");
                lock.wait();
                System.out.println(" end wait()");
            }
        }catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
public static void main(String[] args)  throws InterruptedException{
            Service service=new Service();
            Object lock=new Object();
            Thread thread1=new Thread(new Runnable() {
                @Override
                public void run() {
                    service.testMethod(lock);
                }
            },"A");
            thread1.start();
            Thread thread2=new Thread(new Runnable() {
                @Override
                public void run() {
                    service.testMethod(lock);
                }
            },"B");
            thread2.start();
}
執行結果：
begin wait()
begin wait()
```
从上面可以看出，wait会释放锁，如果将wait改成sleep，就形成了同步效果。还有一个notify()被执行后，不释放锁要测试，  
``` java
public class Service {
    public void testMethod(Object lock) {
        try{
            synchronized (lock) {
                System.out.println ("begin wait() ThreadName="+Thread.currentThread().getName () + "time="+System.currentTimeMillis());
                lock.wait();
                System.out.println(" end wait() ThreadName="+Thread.currentThread().getName () + "time="+System.currentTimeMillis());
            }
        }catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
    public void synNotifyMethod(Object lock) {
        try{
            synchronized (lock) {
                System.out.println("begin notify() ThreadName="+Thread.currentThread().getName () + "time="+System.currentTimeMillis());
                lock.notify();
                Thread.sleep(5000);
                System.out.println("end notify() ThreadName="+Thread.currentThread().getName () + "time="+System.currentTimeMillis());
            }
        }catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
public static void main(String[] args)  throws InterruptedException{
            Service service=new Service();
            Object lock=new Object();
            Thread thread1=new Thread(new Runnable() {
                @Override
                public void run() {
                    service.testMethod(lock);
                }
            },"A");
            thread1.start();
            Thread thread2=new Thread(new Runnable() {
                @Override
                public void run() {
                    service.synNotifyMethod(lock);
                }
            },"B");
            thread2.start();
        Thread thread3=new Thread(new Runnable() {
            @Override
            public void run() {
                service.synNotifyMethod(lock);
            }
        },"C");
        thread3.start();
    }
执行结果：
begin wait() ThreadName=Atime=1525924560460
begin notify() ThreadName=Btime=1525924560460
end notify() ThreadName=Btime=1525924565461
 end wait() ThreadName=Atime=1525924565461
begin notify() ThreadName=Ctime=1525924565462
end notify() ThreadName=Ctime=1525924570462
```
从上面我们可以看出，必须执行完notify方法所在的synchronized代码块后才释放锁。  
**注意**：带线程呈wait状态时，调用线程的interrupt()方法是会出现InterruptedException异常。  
总结：  
l)执行完同步代码块就会释放对象的锁。   
2)在执行同步代码块的过程中，遇到异常而导致线程终止， 锁也会被释放。  
3) 在执行同步代码块的过程中，执行了锁所属对象的 wait() 方法，这个线程会释放对象锁，而此线程对象会进入线程等待池中， 等待被唤醒。  
**只通知一个线程**  
调用方法notify一次只随机通知一个线程进行唤醒。  
当多次调用notify方法时，会随机将等待wait状态的线程进行唤醒。
**唤醒所有线程**  
前面通过调用多次notify方法来实现唤醒多个线程，但不能保证全部唤醒，因此未了唤醒所有线程，可以使用notifyAll方法。  
**方法wait(long)的使用**  
带一个参数的wait(long)方法的功能是等待某一时间内是否有线程对锁进行唤醒，如果超过这个时间则自动唤醒，使用notify可唤醒，可无视long时间。  
**过早唤醒**  
如果通知过早唤醒，则会打乱程序正常的运行逻辑，如notify在wait之前运行，那么wait将会一直等待下去。  
在使用wait/notify模式时，还需**注意：**wait等待的条件发生了变化，也容易造成程序逻辑的混乱。下面举个例子说明：  
``` java
public class Service {
    private String lock;
    public Service(String lock){
        super();
        this.lock=lock;
    }
    public void subtract(){
        try{
            synchronized (lock){
                if(ValueObject.list.size()==0){
                    System.out.println("wait begin threadName="
                            + Thread.currentThread().getName() + "  time=" + System.currentTimeMillis());
                    lock.wait();
                    System.out.println("wait end threadName="
                            + Thread.currentThread().getName() + "  time=" + System.currentTimeMillis());
                }
                ValueObject.list.remove(0);
                System.out.println("list size="+ValueObject.list.size());
            }
        }catch(InterruptedException e){
            e.printStackTrace();
        }
    }
}
public class Add {
    private String lock;
    public Add(String lock){
        super();
        this.lock=lock;
    }
    public void add(){
        synchronized (lock){
            ValueObject.list.add("anyString");
            lock.notifyAll();
        }
    }
}
public static void main(String[] args) throws InterruptedException {
        String lock = new String("");
        Service service = new Service(lock);
        Add add = new Add(lock);
        Thread thread1 = new Thread(new Runnable() {
            @Override
            public void run() {
                service.subtract();
            }
        }, "A");
        thread1.start();
        Thread thread2 = new Thread(new Runnable() {
            @Override
            public void run() {
                service.subtract();
            }
        }, "B");
        thread2.start();
        Thread.sleep(1000);
        Thread thread3 = new Thread(new Runnable() {
            @Override
            public void run() {
                add.add();
            }
        }, "C");
        thread3.start();
    }
结果如下：
wait begin threadName=A  time=1525932205488
wait begin threadName=B  time=1525932205489
wait end threadName=B  time=1525932206492
list size=0
wait end threadName=A  time=1525932206492
Exception in thread "A" java.lang.IndexOutOfBoundsException: Index: 0, Size: 0
```  
出现这样异常的原因是因为有两个实现删除 removeO 操作的线程， 它们在 Thread. sleep(1000) ; 之前都执行了 waitO 方法， 呈等待状态， 当加操作的线程在 1 秒之后被运行时，通知了所有呈等待状态额的减操作的线程，那么第一个实现减操作的线程能正确删除list中索引为0的数据，但第二个实现减操作的线程就出现了索引溢出异常，因为list中仅仅添加了一个数据，也只能删除一个数据，没有第二个可供数据。解决方法就是将if改成while，让他能不断的判断。  
``` java
public void subtract(){
        try{
            synchronized (lock){
                while(ValueObject.list.size()==0){
                    System.out.println("wait begin threadName="
                            + Thread.currentThread().getName() + "  time=" + System.currentTimeMillis());
                    lock.wait();
                    System.out.println("wait end threadName="
                            + Thread.currentThread().getName() + "  time=" + System.currentTimeMillis());
                }
                ValueObject.list.remove(0);
                System.out.println("list size="+ValueObject.list.size());
            }
        }catch(InterruptedException e){
            e.printStackTrace();
        }
    }
```
**生产者/消费者实现**  
等待/通知模式最经典的案例就是“生产者/消费者”模式，下面用一个demo演示：  
``` java
public class Producer {
    private String lock;

    public Producer(String lock) {
        super();
        this.lock = lock;
    }

    public void setValue() {
        try {
            synchronized (lock) {
                if (!ValueObject.value.equals("")) {
                    lock.wait();
                }
                String value = System.currentTimeMillis() + "_" + System.nanoTime();
                System.out.println("set的值是" + value);
                ValueObject.value = value;
                lock.notify();
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
public class Consumer {
    private String lock;
    public Consumer(String lock){
        super();
        this.lock=lock;
    }
    public void getValue(){
        try{
            synchronized (lock){
                if(ValueObject.value.equals("")){
                    lock.wait();
                }
                System.out.println("get的值是"+ValueObject.value);
                ValueObject.value="";
                lock.notify();
            }
        }catch (InterruptedException e){
            e.printStackTrace();
        }
    }
}
public class ValueObject {
    public static String value="";
    public static List list=new ArrayList();
}
public static void main(String[] args) throws InterruptedException {
        String lock=new String("");
        Producer producer=new Producer(lock);
        Consumer consumer=new Consumer(lock);
        Thread t1=new Thread(new Runnable() {
            @Override
            public void run() {
                while (true) {
                    producer.setValue();
                }
            }
        });
        Thread t2=new Thread(new Runnable() {
            @Override
            public void run() {
                while (true) {
                    consumer.getValue();
                }
            }
        });
        t1.start();
        t2.start();
    }
    结果：
    get的值是1525933641624_591203203258692
set的值是1525933641624_591203203272566
get的值是1525933641624_591203203272566
set的值是1525933641624_591203203312865..............
```
上述例子是1个生产者对应一个消费者的数据交换，在此例的基础上，设计出多个生产者和多个消费者，那么运行过程中极有可能出现假死。也就是所有线程呈WAITING等待状态。   
**多生产与多消费，操作值-假死**  
“假死” 的现象其实就是线程进入WAITING等待状态。如果全部线程都进入WAITING状态，则程序就不再执行任何业务功能了，整个项目呈停止状态。这在使用生产者与消费者模式时经常遇到。对上面的例子进行改动:  
``` java
public void setValue() {
        try {
            synchronized (lock) {
                if (!ValueObject.value.equals("")) {
                    System.out.println("生产者"+Thread.currentThread().getName()+"WAITING了◇");
                    lock.wait();
                }
                String value = System.currentTimeMillis() + "_" + System.nanoTime();
                ValueObject.value = value;
                lock.notify();
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
    public void getValue(){
        try{
            synchronized (lock){
                while(ValueObject.value.equals("")){
                    System.out.println("消费者"+Thread.currentThread().getName()+"WAITING了★");
                    lock.wait();
                }
                ValueObject.value="";
                lock.notify();
            }
        }catch (InterruptedException e){
            e.printStackTrace();
        }
    }
     public static void main(String[] args) throws InterruptedException {
        String lock = new String("");
        Producer producer = new Producer(lock);
        Consumer consumer = new Consumer(lock);
        Thread[] pThread = new Thread[2];
        Thread[] cThread = new Thread[2];
        for (int i = 0; i < 2; i++) {
            pThread[i] = new Thread(new Runnable() {
                @Override
                public void run() {
                    while (true) {
                        producer.setValue();
                    }
                }
            }, "生产者" + (i + 1));
            cThread[i] = new Thread(new Runnable() {
                @Override
                public void run() {
                    while (true) {
                        consumer.getValue();
                    }
                }
            }, "消费者" + (i + 1));
            pThread[i].start();
            cThread[i].start();
        }
        Thread.sleep(5000);
        Thread[] threadArray = new Thread[Thread.currentThread().getThreadGroup().activeCount()];
        Thread.currentThread().getThreadGroup().enumerate(threadArray);
        for (int i = 0; i < threadArray.length; i++) {
            System.out.println(threadArray[i].getName() + "  " + threadArray[i].getState());
        }
    }
执行结果：
生产者生产者2WAITING了◇
生产者生产者1WAITING了◇
消费者消费者2WAITING了★
消费者消费者1WAITING了★
main  RUNNABLE
Monitor Ctrl-Break  RUNNABLE
生产者1  WAITING
消费者1  WAITING
生产者2  WAITING
消费者2  WAITING
```
该程序运行很有可能呈“假死”，呈假死状态的进程中所有的线程都呈WAITING状态，是什么原因导致的呢？  
在代码中确实使用了wait/notify进行通信，但不能保证notify唤醒的是异类，也许是同类，比如“生产者”唤醒“生产者”，或“消费者”唤醒“消费者”的情况，如果一直这样积少成多下去，会导致所有线程都不能运行，大家都在等待，呈WAITING状态，程序最后呈假死，不能继续运行下去。  
**多生产与多消费：操作值**  
解决“假死”的情况其实也很简单，将Producer.java与Consumer.java文件中的notify()改成notifyAll()方法即可，他的原理就是不光通知同类线程，也包括异类，这样就不会出现假死的情况了。  
**一生产与一消费：操作栈**  
用一个示例解释，生产者向堆栈List对象中放入数据，是消费者从List堆栈中取出数据。List最大容量是1，实验环境一消费者一生产者：  
``` java
public class Mystack {
    private List list=new ArrayList<>();
    synchronized public void push(){
        try{
            if(list.size()==1){
                this.wait();
            }
            list.add("anyString="+Math.random());
            this.notify();
            System.out.println("push="+list.size());
        }catch (InterruptedException e){
            e.printStackTrace();
        }
    }
    synchronized  public String pop(){
        String returnValue="";
        try{
            if(list.size()==0){
                System.out.println("pop操作中的："+Thread.currentThread().getName()+"线程呈wait状态");
                this.wait();
            }
            returnValue=""+list.get(0);
            list.remove(0);
            this.notify();
            System.out.println("pop="+list.size());
        }catch (InterruptedException e){
            e.printStackTrace();
        }
        return returnValue;
    }
}
public class Producer {
    private Mystack mystack;
    public Producer(Mystack mystack){
        super();
        this.mystack=mystack;
    }
    public void pushService(){
        mystack.push();
    }
}
public class Consumer {
    private Mystack mystack;
    public Consumer(Mystack mystack){
        super();
        this.mystack=mystack;
    }
    public void popService(){
        System.out.println("pop="+mystack.pop());
    }
}
public static void main(String[] args) throws InterruptedException {
        Mystack mystack=new Mystack();
        Producer producer=new Producer(mystack);
        Consumer consumer=new Consumer(mystack);
        Thread t1=new Thread(new Runnable() {
            @Override
            public void run() {
                while(true){
                    producer.pushService();
                }
            }
        },"A");
        Thread t2=new Thread(new Runnable() {
            @Override
            public void run() {
                while (true){
                    consumer.popService();
                }
            }
        },"B");
        t1.start();
        t2.start();
    }
    结果如下：  
    pop=anyString=0.660422273550075
push=1
pop=0
pop=anyString=0.65509279541902
push=1
pop=0
pop=anyString=0.05130509780476822
push=1
pop=0
```
运行结果是size()不会大于1，通过使用生产者/消费者，容器size值不会大于1，在0和1之间进行交替，也就是生产和消费这两个过程在交替执行。  
**一生产与多消费--操作栈：解决wait条件改变与假死**  
本示例是使用一个生产者向堆栈List对象中放入数据，而多个消费者从List栈堆中取出数据。List最大容量还是1。
``` java
Mystack mystack=new Mystack();
        Producer producer=new Producer(mystack);
        Consumer consumer1=new Consumer(mystack);
        Consumer consumer2=new Consumer(mystack);
        Consumer consumer3=new Consumer(mystack);
        Consumer consumer4=new Consumer(mystack);
        Thread t1=new Thread(new Runnable() {
            @Override
            public void run() {
                while(true){
                    producer.pushService();
                }
            }
        },"A");
        Thread t2=new Thread(new Runnable() {
            @Override
            public void run() {
                while (true){
                    consumer1.popService();
                }
            }
        },"B");
        Thread t3=new Thread(new Runnable() {
            @Override
            public void run() {
                while (true){
                    consumer2.popService();
                }
            }
        },"B");
        Thread t4=new Thread(new Runnable() {
            @Override
            public void run() {
                while (true){
                    consumer3.popService();
                }
            }
        },"C");
        Thread t5=new Thread(new Runnable() {
            @Override
            public void run() {
                while (true){
                    consumer4.popService();
                }
            }
        },"D");
        t1.start();
        t2.start();
        t3.start();
        t4.start();
        t5.start();
    }
结果如下：
push=1
pop=0
pop=anyString=0.5100315127910089
pop操作中的：C线程呈wait状态
pop操作中的：B线程呈wait状态
pop操作中的：B线程呈wait状态
pop操作中的：D线程呈wait状态
push=1
pop=0
pop=anyString=0.5807279164044403
pop操作中的：C线程呈wait状态
Exception in thread "B" java.lang.IndexOutOfBoundsException: Index: 0, Size: 0
	at java.util.ArrayList.rangeCheck(ArrayList.java:657)
	at java.util.ArrayList.get(ArrayList.java:433)
	at com.amiba.thread.entity.Mystack.pop(Mystack.java:27)
	at com.amiba.thread.entity.Consumer.popService(Consumer.java:12)
	at com.amiba.thread.main.Run1$2.run(Run1.java:29)
	at java.lang.Thread.run(Thread.java:748)
```
此问题的出现是因为在Mystack.java类中使用了if语句作为条件判断，导致条件改变时没有得到及时的响应，所以多个呈wait状态的线程被唤醒，继而执行list.remove(0)代码而出现异常，解决办法，将if换成while即可。  
运行后，没有出现异常，但是却出现了“假死”，再次解决，把notify换成notifyAll方法。  
**多生产与一消费：操作栈**  
使用生产者向堆栈List对象中放入数据，使用消费者从List堆栈中取出数据。List最大容量是1，实验环境是多个生产者与一个消费者。  
``` java
public class Mystack {
    private List list=new ArrayList<>();
    synchronized public void push(){
        try{
            while(list.size()==1){
                this.wait();
            }
            list.add("anyString="+Math.random());
            this.notifyAll();
            System.out.println("push="+list.size());
        }catch (InterruptedException e){
            e.printStackTrace();
        }
    }
    synchronized  public String pop(){
        String returnValue="";
        try{
            while(list.size()==0){
                System.out.println("pop操作中的："+Thread.currentThread().getName()+"线程呈wait状态");
                this.wait();
            }
            returnValue=""+list.get(0);
            list.remove(0);
            this.notifyAll();
            System.out.println("pop="+list.size());
        }catch (InterruptedException e){
            e.printStackTrace();
        }
        return returnValue;
    }
}
public static void main(String[] args) throws InterruptedException {
        Mystack mystack=new Mystack();
        Producer producer1=new Producer(mystack);
        Producer producer2=new Producer(mystack);
        Producer producer3=new Producer(mystack);
        Producer producer4=new Producer(mystack);
        Thread t1=new Thread(new Runnable() {
            @Override
            public void run() {
                while(true){
                    producer1.pushService();
                }
            }
        },"A");
        Thread t2=new Thread(new Runnable() {
            @Override
            public void run() {
                while (true){
                    producer2.pushService();
                }
            }
        },"B");
        Thread t3=new Thread(new Runnable() {
            @Override
            public void run() {
                while (true){
                    producer3.pushService();
                }
            }
        },"B");
        Thread t4=new Thread(new Runnable() {
            @Override
            public void run() {
                while (true){
                    producer4.pushService();
                }
            }
        },"C");
        t1.start();
        t2.start();
        t3.start();
        t4.start();
        Consumer consumer5=new Consumer(mystack);
        Thread t5=new Thread(new Runnable() {
            @Override
            public void run() {
                while (true){
                    consumer5.popService();
                }
            }
        },"E");
        t5.start();
    }
    结果：无限存取。
```
**多生产与多消费：操作栈**  
使用生产者向堆栈List对象中放入数据，使用消费者从List堆栈中取出数据。List最大容量是1，实验环境是多个生产者与多个消费者。  
``` java
public static void main(String[] args) throws InterruptedException {
        Mystack mystack=new Mystack();
        Producer producer1=new Producer(mystack);
        Producer producer2=new Producer(mystack);
        Producer producer3=new Producer(mystack);
        Producer producer4=new Producer(mystack);
        Thread t1=new Thread(new Runnable() {
            @Override
            public void run() {
                while(true){
                    producer1.pushService();
                }
            }
        },"A");
        Thread t2=new Thread(new Runnable() {
            @Override
            public void run() {
                while (true){
                    producer2.pushService();
                }
            }
        },"B");
        Thread t3=new Thread(new Runnable() {
            @Override
            public void run() {
                while (true){
                    producer3.pushService();
                }
            }
        },"B");
        Thread t4=new Thread(new Runnable() {
            @Override
            public void run() {
                while (true){
                    producer4.pushService();
                }
            }
        },"C");
        t1.start();
        t2.start();
        t3.start();
        t4.start();
        Consumer consumer5=new Consumer(mystack);
        Consumer consumer6=new Consumer(mystack);
        Consumer consumer7=new Consumer(mystack);
        Consumer consumer8=new Consumer(mystack);
        Thread t5=new Thread(new Runnable() {
            @Override
            public void run() {
                while (true){
                    consumer5.popService();
                }
            }
        },"E");
        Thread t6=new Thread(new Runnable() {
            @Override
            public void run() {
                while (true){
                    consumer5.popService();
                }
            }
        },"F");
        Thread t7=new Thread(new Runnable() {
            @Override
            public void run() {
                while (true){
                    consumer5.popService();
                }
            }
        },"G");
        Thread t8=new Thread(new Runnable() {
            @Override
            public void run() {
                while (true){
                    consumer5.popService();
                }
            }
        },"H");
        t5.start();
        t6.start();
        t7.start();
        t8.start();
    }
```
上面的运行结果依旧是正常的，list对象的size()并不会超过一。  
**通过管道进行线程间通信：字符流**  
管道流(pipeStream)是一种特殊的流，用于在不同线程间直接传送数据。一个线程发送数据到输出管道，另一个线程从输入管道中读取数据。通过使用管道，实现不同线程间的通信，而无须借助于类似临时文件之类的东西。  
JDK1.4提供了4个类来使线程间可以进行通信：  
1)PipedInputStream和PipedOutputStream  
2)PipedReader和PipedWriter  
简单测试：  
``` java
public class WriteData {
    public void writeMethod(PipedOutputStream outputStream) {
        try {
            System.out.println("write  :");
            for (int i = 0; i < 300; i++) {
                String outData = "" + (i + 1);
                outputStream.write(outData.getBytes());
                System.out.print(outData);
            }
            System.out.println();
            outputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
public class ReadData {
    public void readMethod(PipedInputStream inputStream) {
        try {
            System.out.println("read    :");
            byte[] byteArray = new byte[20];
            int readLength = inputStream.read(byteArray);
            while (readLength != -1) {
                String newData = new String(byteArray, 0, readLength);
                System.out.print(newData);
                readLength = inputStream.read(byteArray);
            }
            System.out.println();
            inputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
public static void main(String[] args) throws InterruptedException {
        try{
            WriteData writeData=new WriteData();
            ReadData readData=new ReadData();
            PipedInputStream pipedInputStream=new PipedInputStream();
            PipedOutputStream pipedOutputStream=new PipedOutputStream();
            pipedOutputStream.connect(pipedInputStream);
            Thread threadRead=new Thread(new Runnable() {
                @Override
                public void run() {
                    readData.readMethod(pipedInputStream);
                }
            });
            threadRead.start();
            Thread.sleep(2000);
            Thread threadWrite=new Thread(new Runnable() {
                @Override
                public void run() {
                    writeData.writeMethod(pipedOutputStream);
                }
            });
            threadWrite.start();
        }catch (IOException e){
            e.printStackTrace();
        }
    }
运行结果如下：
read    :
write  :
1234567891011121314151617181920212223242526272829303132333435363738394041424344454647484950515253545556575859606162636465666768697071727374757677787980818283
279280281282283284285286287288289290291292293294295296297298299300
```
使用代码inputStream.connect(outputStream)或outputStream.connect(inputStream)的作用是两个Stream之间产生通信连接，这样才可以将数据进行输出与输入。  
在此实验中，两个线程通过管道流成功进行数据的传输，首先是读取线程new Thread(inputstream)启动，由于当时没有数据被写入，所以线程阻塞在int readLength=in.read(byteArray);代码中，直到有数据被写入，才能继续向下运行。  
**通过管道进行线程间的通信：字符流**  
管道中还可以传递字符流。可用String、char[]替代上面的例子操作，结果一样。  
**实战:等待/通知之交叉备份**  
加深理解印象，创建20个线程，其中10个是将数据备份到A数据库中，另外10条备份到B数据库，并且交叉运行。示例如下：  
``` java
public class DBTools {
    volatile private boolean prevIsA = false;

    synchronized public void backupA() {
        try {
            while (prevIsA == true) {
                wait();
            }
            for (int i = 0; i < 5; i++) {
                System.out.println("★★★★★");
            }
            prevIsA = true;
            notifyAll();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    synchronized public void backupB() {
        try {
            while (prevIsA == false) {
                wait();
            }
            for (int i = 0; i < 5; i++) {
                System.out.println("☆☆☆☆☆");
            }
            prevIsA = false;
            notifyAll();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
public static void main(String[] args) throws InterruptedException {
        DBTools dbTools = new DBTools();
        for (int i = 0; i < 20; i++) {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    dbTools.backupA();
                }
            }).start();
            new Thread(new Runnable() {
                @Override
                public void run() {
                    dbTools.backupB();
                }
            }).start();
        }
}
运行结果：
★★★★★
★★★★★
★★★★★
★★★★★
★★★★★
☆☆☆☆☆
☆☆☆☆☆
☆☆☆☆☆
☆☆☆☆☆
☆☆☆☆☆      重复进行20次
```
交替打印的原理是使用如下代码作为标记：  
volatile private boolean prevIsA = false;  
实现了A和B线程交替备份效果。  
#### 3.2 方法Join的使用  
使用情况：主线程创建并启动子线程，如果子线程中要进行大量的耗时运算，主线程往往将早于子线程结束之前结束。这时，如果主线程想等待子线程执行完成之后再结束，比如子线程处理一个数据，主线程要取得这个数据中的值，就要用到join()方法了。方法join()的作用是等待线程对象销毁。用一个例子来帮助理解  
``` java
public class MyThread  extends Thread{
    @Override
    public void run() {
        try{
            int secondValue=(int)(Math.random()*10000);
            System.out.println(secondValue);
            Thread.sleep(secondValue);
        }catch (InterruptedException e){
            e.printStackTrace();
        }
    }
}
public static void main(String[] args) throws InterruptedException {
        MyThread myThread=new MyThread();
        myThread.start();
        System.out.println("我想当myThread对象执行完在执行？");
        System.out.println("我想知道上面sleep里的值是什么");
        System.out.println("答案：根本不能确定");
    }
    结果：
我想当myThread对象执行完在执行？
我想知道上面sleep里的值是什么
答案：根本不能确定
7447
```
这时我们可以用join来解决，修改代码：  
``` java
 public static void main(String[] args) throws InterruptedException {
        MyThread myThread=new MyThread();
        myThread.start();
        myThread.join();
        System.out.println("我做到了");
    }
    结果：
    3739
我做到了
```
方法join的作用是使所属的线程对象x正常执行run()方法中的任务，而使当前线程z进行无限期阻塞，等待线程x销毁后在继续执行线程z后面的代码。  
方法join具有使线程排队运行的作用，有些类似同步的运行结果，join与synchronized的区别：join在内部使用wait()方法进行等待，而synchronized关键字使用的是“对象监视器”原理做同步。  
**注意**：在join过程中，如果当前线程对象被中断，则当前线程出现异常。如方法join()与interrupt()方法彼此遇到，则会出现异常。  
**方法join(long)的使用**设定等待的时间，但是，如果目标线程本身有sleep(2000),在设定join(2000)，运行结果还是等待2秒。  
**join(2000)和sleep(2000)的区别**  
方法join(long) 的功能在内部是使用wait(long)方法来实现的，所以join(long)方法具有释放锁的特点，它的源码如下：  
``` java
public final synchronized void join(long millis)
    throws InterruptedException {
        long base = System.currentTimeMillis();
        long now = 0;

        if (millis < 0) {
            throw new IllegalArgumentException("timeout value is negative");
        }

        if (millis == 0) {
            while (isAlive()) {
                wait(0);
            }
        } else {
            while (isAlive()) {
                long delay = millis - now;
                if (delay <= 0) {
                    break;
                }
                wait(delay);
                now = System.currentTimeMillis() - base;
            }
        }
    }
```
当执行wait(long)方法后，当前线程的锁被释放，那么其他线程就可以调用此线程的同步方法了。  
而sleep(long)方法却不释放锁，假如A方法拥有B方法的对象锁，这是A方法sleep(5000),其他方法要调用B中的方法，需要到达5秒后才可获得锁。而如果是用join，则会释放  
**方法join()后面的代码提前运行：出现意外**，你可仔细观察，对这些意外进行认知解释。  
#### 3.3 类ThreadLocadl的使用  
变量值的共享可以使用public static变量的形式，所有的线程都使用同一public static变量。如果想实现每一个线程都有自己的共享变量该如何解决呢？JDK中提供的类
ThreadLocal正是为了解决这样的问题。  
类ThreadLocal主要解决的就是每个线程绑定自己的值，可以将ThreadLocal类比喻成全局存放数据的盒子，盒子中可以存放每个线程的私有数据。  
ThreadLocal可以通过set和get来对值的存取，默认为null，类ThreadLocal解决的是变量在不同线程间的隔离性，也就是每个线程都拥有自己的值，不同线程中的值是可以放入ThreadLocal中进行保存的。用例子来体现ThreadLocal的隔离性   
``` java
public class Tools {
    public static ThreadLocal<Date> t1=new ThreadLocal<>();
}
public class MyThread  extends Thread{
    @Override
    public void run() {
        try{
            for(int i=0;i<20;i++){
                if(Tools.t1.get()==null){
                    Tools.t1.set(new Date());
                }
                System.out.println("A"+Tools.t1.get().getTime());
                Thread.sleep(100);
            }
        }catch (InterruptedException e){
            e.printStackTrace();
        }
    }
}
public class MyThread2 extends Thread{
    @Override
    public void run() {
        try{
            for(int i=0;i<20;i++){
                if(Tools.t1.get()==null){
                    Tools.t1.set(new Date());
                }
                System.out.println("B"+Tools.t1.get().getTime());
                Thread.sleep(100);
            }
        }catch (InterruptedException e){
            e.printStackTrace();
        }
    }
}
public static void main(String[] args) throws InterruptedException {
        MyThread t1=new MyThread();
        t1.start();
        Thread.sleep(1000);
        MyThread2 t2=new MyThread2();
        t2.start();
}
```
上述例子，取出的结果是截然不同的，也证明了ThreadLocal的隔离性。  
**解决get()返回null值**  
为ThreadLocal设置默认值  
``` java
public class DefaultThreadLocal extends ThreadLocal{
    @Override
    protected Object initialValue() {
        return "我是默认值 第一次get 不在为null";
    }
}
public static void main(String[] args) throws InterruptedException {
        DefaultThreadLocal tt=new DefaultThreadLocal();
        System.out.println(tt.get());
    }
    结果为设置的默认值
```
#### 3.4 类InheritableThreadLocal的使用   
使用类InheritableThreadLocal可以在子线程获取父线程继承下来的值，并且还可进行更改值，例子如下：   
``` java
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
public class Tools {

    public static InheritableThreadLocalExt inheritableThreadLocalExt=new InheritableThreadLocalExt();
}
public class MyThread  extends Thread{
    @Override
    public void run() {
        try{
            for(int i=0;i<10;i++){
                System.out.println("在线程A中取值"+Tools.inheritableThreadLocalExt.get());
                Thread.sleep(100);
            }
        }catch (InterruptedException e){
            e.printStackTrace();
        }
    }
}
public static void main(String[] args) throws InterruptedException {
        for (int i = 0; i < 10; i++) {
            System.out.println("     Main线程中取值" + Tools.inheritableThreadLocalExt.get());
            Thread.sleep(100);
        }
        Thread.sleep(5000);
        MyThread myThread = new MyThread();
        myThread.start();
    }
    结果为：
     Main线程中取值1525949545055
     Main线程中取值1525949545055
     Main线程中取值1525949545055
     Main线程中取值1525949545055
在线程A中取值1525949545055我在子线程加的~~~
在线程A中取值1525949545055我在子线程加的~~~
在线程A中取值1525949545055我在子线程加的~~~
在线程A中取值1525949545055我在子线程加的~~~
```

**注意**：在使用InheritableThreadLocal类需要注意一点的是，如果子线程在取得值的同时，主线程将InheritableThreadLocal的值进行更改，那么子线程取到的值依旧是旧的 

----------------------

### Lock的使用
#### 4.1 使用ReentrantLock类  
在java多线程中，可以使用synchronized关键字来实现线程之间的同步互斥，但在jdk1.5中新增加了ReentrantLock类也能达到同样的效果，并且在扩展功能上更加强大，比如具有嗅探锁定，多路分支通知等功能，而且在使用上也能比synchronized更加灵活。  
测试代码如下：
``` java
public class MyService {
    private Lock lock=new ReentrantLock();
    public void testMethod(){
        lock.lock();
        for(int i=0;i<5;i++){
            System.out.println("ThreadName="+Thread.currentThread().getName()+"  "+(i+1));
        }
        lock.unlock();
    }
}
public static void main(String[] args) {
        MyService myService=new MyService();
        Thread t1=new Thread(new Runnable() {
            @Override
            public void run() {
                myService.testMethod();
            }
        });
        Thread t2=new Thread(new Runnable() {
            @Override
            public void run() {
                myService.testMethod();
            }
        });
        Thread t3=new Thread(new Runnable() {
            @Override
            public void run() {
                myService.testMethod();
            }
        });
        Thread t4=new Thread(new Runnable() {
            @Override
            public void run() {
                myService.testMethod();
            }
        });
        t1.start();
        t2.start();
        t3.start();
        t4.start();
    }
    结果如下：
    ThreadName=Thread-0  1
ThreadName=Thread-0  2
ThreadName=Thread-0  3
ThreadName=Thread-0  4
ThreadName=Thread-0  5
ThreadName=Thread-1  1
ThreadName=Thread-1  2
ThreadName=Thread-1  3
ThreadName=Thread-1  4
ThreadName=Thread-1  5
ThreadName=Thread-3  1
ThreadName=Thread-3  2
ThreadName=Thread-3  3
ThreadName=Thread-3  4
ThreadName=Thread-3  5
ThreadName=Thread-2  1
ThreadName=Thread-2  2
ThreadName=Thread-2  3
ThreadName=Thread-2  4
ThreadName=Thread-2  5
```
上面调用ReentrantLock对象的lock()获得锁，调用unlock()方法释放锁。  
从结果上看，它们是分组打印的，因为当前线程拥有锁，但线程之间的执行时随机的。   
再进一步观察ReentrantLock的同步  
```java
public class MyService {
    private Lock lock=new ReentrantLock();
    public void methodA(){
        try {
            lock.lock();
            System.out.println("methodA begin ThreadName=" + Thread.currentThread().getName() + "  time=" + System.currentTimeMillis());
            Thread.sleep(2000);
            System.out.println("methodA end ThreadName=" + Thread.currentThread().getName() + "  time=" + System.currentTimeMillis());
        }catch (InterruptedException e){
            e.printStackTrace();
        }finally {
            lock.unlock();
        }
    }
    public void methodB(){
        try {
            lock.lock();
            System.out.println("methodB begin ThreadName=" + Thread.currentThread().getName() + "  time=" + System.currentTimeMillis());
            Thread.sleep(2000);
            System.out.println("methodB end ThreadName=" + Thread.currentThread().getName() + "  time=" + System.currentTimeMillis());
        }catch (InterruptedException e){
            e.printStackTrace();
        }finally {
            lock.unlock();
        }
    }
public static void main(String[] args) {
        MyService myService=new MyService();
        Thread tA=new Thread(new Runnable() {
            @Override
            public void run() {
                myService.methodA();
            }
        },"A");
        Thread tAA=new Thread(new Runnable() {
            @Override
            public void run() {
                myService.methodA();
            }
        },"AA");
        Thread tB=new Thread(new Runnable() {
            @Override
            public void run() {
                myService.methodB();
            }
        },"B");
        Thread tBB=new Thread(new Runnable() {
            @Override
            public void run() {
                myService.methodB();
            }
        },"BB");
        tA.start();
        tAA.start();
        tB.start();
        tBB.start();
    }
执行结果：
methodA begin ThreadName=A  time=1526009497337
methodA end ThreadName=A  time=1526009499338
methodB begin ThreadName=B  time=1526009499338
methodB end ThreadName=B  time=1526009501339
methodA begin ThreadName=AA  time=1526009501339
methodA end ThreadName=AA  time=1526009503341
methodB begin ThreadName=BB  time=1526009503341
methodB end ThreadName=BB  time=1526009505341
```
实验说明，调用lock.lock()代码的线程就持有了“对象监视器”，其他线程只有等待锁被释放是再次争抢。效果和使用synchronized一样。  
**使用Condition实现等待/通知**  
关键字synchronized与wait()和notify()方法结合可以实现等待/通知模式，类
ReentrantLock也可以有同样的效果。但需借助有Condition对象。Condition类是jdk5出现的技术，使用它有更好的灵活性，比如实现多路通知功能，也就是在一个lock对象里面可以创建多个Condition(即对象监视器)实例，线程对象可以注册在指定的Condition中，从而可以有选择性地进行线程通知，在调度线程上更加灵活。  
使用wait/notify进行通知时，被通知的线程是JVM随机选择的，但使用ReentrantLock结合Condition类是可以实现“选择性通知”这个功能的。这个功能是非常重要的。而且在Condition类中是默认提供的。  
而synchronized就相当于整个Lock对象中只有一个单一的Condition对象，所有的线程都注册在它一个对象的身上，线程开始notifyAll()时，需要通知所有的WAITING状态的线程，没有选择权，会出现相当大的效率问题。  
下面简单介绍下Condition，
创建一个Condition，可按下面的方式创建。
``` java
Lock lock=new ReentranLock;
Condition condition=lock.newCondition();
```
等待方法为condition.await()；  
**注意**：在调用await()方法之前一定要调用lock.lock()代码获得同步监视器，不然会有异常报错信息提示监视器出错。  
下来用一个完整例子帮助理解：  
``` java
public class MyService2 {
    private Lock lock=new ReentrantLock();
    private Condition condition=lock.newCondition();
    public void await(){
        try{
            lock.lock();
            System.out.println("  await时间为 "+System.currentTimeMillis());
            condition.await();
        }catch (InterruptedException e){
            e.printStackTrace();
        }finally {
            lock.unlock();
        }
    }
    public void singal(){
        try{
            lock.lock();
            System.out.println("singal 时间为"+System.currentTimeMillis());
            condition.signal();
        }finally {
            lock.unlock();
        }
    }
}
public static void main(String[] args) throws InterruptedException{
        MyService2 myService2=new MyService2();
        Thread t1=new Thread(new Runnable() {
            @Override
            public void run() {
                myService2.await();
            }
        });
        t1.start();
        Thread.sleep(2000);
        myService2.singal();
    }
    结果如下：
await时间为 1526016681449
singal 时间为1526016683446
```
成功实现了等待/通知模式、  
Object类中的wait()方法相当于Condition中的await方法。  
Object类中的wait(long)方法相当于Condition中的await(long time,TimeUnit unit)方法。  
Object中的notify相当于Condition中的signal()方法。  
Object中的notify相当于Condition中的signalAll()方法。  
如果想唤醒部分线程改怎么处理呢？这时就有必要使用多个Condition对象了，也就是Condition对象可以唤醒部分指定线程，有助于提升程序运行的效率。可以先对线程进行分组，然后在唤醒指定组中的线程。  
示例如下：  
``` java
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
public static void main(String[] args) throws InterruptedException{
        MyService myService=new MyService();
        Thread awaitA=new Thread(new Runnable() {
            @Override
            public void run() {
                myService.awaitA();
            }
        },"A");
        Thread awaitB=new Thread(new Runnable() {
            @Override
            public void run() {
                myService.awaitB();
            }
        },"B");
        awaitA.start();
        awaitB.start();
        Thread.sleep(3000);
        Thread signalA=new Thread(new Runnable() {
            @Override
            public void run() {
                myService.sigalAll_A();
            }
        },"AA");
        signalA.start();
    }
    执行结果如下：
awaitA begin ThreadName=A  time=1526017851414
awaitB begin ThreadName=B  time=1526017851415
sigalAll_A ThreadName=AA  time=1526017854414
awaitB end ThreadName=B  time=1526017854415
```
可以看到，只有A被唤醒了，由此实验可以得出，ReentranLock对象可以唤醒指定种类的线程这是控制部分线程行为的方便方式。  
**生产者/消费者模式**，基本原理和第三者所讲一样，同理可得即可。  
**公平锁和非公平锁**  
公平与非公平：锁Lock分为“公平锁”和“非公平锁”，公平锁表示线程获取锁的顺序是按照线程加锁的顺序来分配的。即先来先得的FIFO先进先出顺序。而非公平锁就是一种获取锁的抢占机制，是随机获得锁的，和公平锁不一样的就是先来不一定先得到锁，这个方法可能造成某些线程一直拿不到锁，结果也就不公平了。  
下面演示下这两个锁  
``` java
public class Service {
    private ReentrantLock lock;

    public Service(boolean isFair) {
        super();
        lock = new ReentrantLock(isFair);
    }

    public void serviceMethod() {
        try {
            lock.lock();
            System.out.println("ThreadName=" + Thread.currentThread().getName() + "获得锁定！");
        } finally {
            lock.unlock();
        }
    }
}
public static void main(String[] args) {
        final Service service = new Service(true);
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                System.out.println("★线程" + Thread.currentThread().getName() + "运行了");
                service.serviceMethod();
            }
        };
        Thread[] threadArray = new Thread[10];
        for (int i = 0; i < 10; i++) {
            threadArray[i] = new Thread(runnable);
        }
        for (int i = 0; i < 10; i++) {
            threadArray[i].start();
        }
}
运行结果：
★线程Thread-1运行了
ThreadName=Thread-1获得锁定！
★线程Thread-2运行了
ThreadName=Thread-2获得锁定！
★线程Thread-3运行了
ThreadName=Thread-3获得锁定！
★线程Thread-4运行了
ThreadName=Thread-4获得锁定！
★线程Thread-7运行了
ThreadName=Thread-7获得锁定！
★线程Thread-6运行了
ThreadName=Thread-6获得锁定！
★线程Thread-9运行了
ThreadName=Thread-9获得锁定！
★线程Thread-5运行了
ThreadName=Thread-5获得锁定！
★线程Thread-0运行了
ThreadName=Thread-0获得锁定！
★线程Thread-8运行了
ThreadName=Thread-8获得锁定！
```
打印的结果基本呈有序状态，这就是公平锁的特点。  
``` java
public static void main(String[] args) {
        final Service service = new Service(false);
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                System.out.println("★线程" + Thread.currentThread().getName() + "运行了");
                service.serviceMethod();
            }
        };
        Thread[] threadArray = new Thread[10];
        for (int i = 0; i < 10; i++) {
            threadArray[i] = new Thread(runnable);
        }
        for (int i = 0; i < 10; i++) {
            threadArray[i].start();
        }
    }
结果：
★线程Thread-0运行了
ThreadName=Thread-0获得锁定！
★线程Thread-1运行了
ThreadName=Thread-1获得锁定！
★线程Thread-2运行了
ThreadName=Thread-2获得锁定！
★线程Thread-3运行了
ThreadName=Thread-3获得锁定！
★线程Thread-4运行了
ThreadName=Thread-4获得锁定！
★线程Thread-6运行了
★线程Thread-5运行了
ThreadName=Thread-6获得锁定！
ThreadName=Thread-5获得锁定！
★线程Thread-7运行了
ThreadName=Thread-7获得锁定！
★线程Thread-9运行了
★线程Thread-8运行了
ThreadName=Thread-9获得锁定！
ThreadName=Thread-8获得锁定！
```
非公平锁基本都是乱序的，说明先start()启动的线程不代表先获得锁。  
**方法getHoldCount()、getQueueLength()和getWaitQueueLength()的测试**  
int getHoldCount()的作用是查询当前线程保持此锁定的个数，也就是调用lock()方法的次数。  
int getQueueLength()的作用是返回正等带获取此锁定的线程估计数，比如有5个线程，1个线程首先执行await(),那么getQueueLength返回的方法值为4，说明有4个线程同时在等待lock的释放。  
方法int getWaitQueueLength()的作用是返回等待与此锁定相关的给定条件Condition的线程估计数，比如有5个线程，每个线程都执行了同一个condition对象的await方法，则调用getWaitQueueLength(Condition condition)方法时返回的值为5.  

**方法hasQueuedThread()、hasQueuedThreads()和hasWaiters()的测试**  
方法boolean hasQueuedThread(Thread thread) 的作用是查询指定的线程是否正在等待获取此锁定。  
方法boolean hasQueuedThreads()的作用是查询是否有线程正在等待获取此锁定。  
方法hasWaiters(Condition condition)的作用是查询是否有线程正在等待与此锁定有关的condition条件。  

**方法isFair()、isHoldByCurrentThread()和isLocked()的测试**  
方法boolean isFair()的作用是判断是不是公平锁；  
方法boolean isHeldByCurrentThread()的作用是查询当前线程是否保持此锁定。  
方法boolean isLocked()的作用是查询此锁定是否有任意线程保持。  

**方法lockInterruptibly()、tryLock()、tryLock(long timeout,TimeUnit unit)的测试**   
方法void lockInterruptibly()的作用是：如果当前线程未被中断，则获取锁定，如果已经被中断则出现异常。  
方法boolean tryLock()的作用是：仅在调用时锁定未被另一个线程保持的情况下，才获取该锁定。  
方法boolean tryLock(long timeout,TimeUnit unit)的作用是，如果锁定在给定等待的时间内没有被另一个线程等待，且当前线程未被中断，则获取该锁定。  

#### 4.2 使用ReentrantReadWriteLock类  
类ReentrantLock具有完全互斥排他的效果，即同一时间只有一个线程在执行ReentrantLock.lock()方面的任务，这样做虽然保证了实例变量的线程安全性，但效率却是非常低下的，所以在JDK中提供了一种读写锁ReentrantReadWriteLock类，使用它可用加快运行效率，在某些不需要操作实例变量的方法中，完全可以使用读写锁ReentrantReadWriteLock来提升方法的运行速度。  
读写锁表示也有两个锁，一个是读操作相关的锁，也称共享锁；另一个是写操作相关的锁，也叫排他锁，也就是多个读锁之间不互斥，读锁与写锁互斥，写锁与写锁互斥。在没有线程Thread进行写入操作时，进行读取操作的多个Thread都可以获取读锁，而进入写入操作的Thread只有在获取写锁后才能进行写入操作，即多个Thread可以同时进行读取操作，但是同一时刻只允许一个Thread进行写入操作。  
**类ReentrantReadWriteLock的使用：读读共享**  
使用lock.readLock()读锁可以提高程序的运行效率，允许多个线程同时执行lock()方法后面的代码。  
**类ReentrantReadWriteLock的使用：写写互斥**  
写锁代码lock.writeLock()的效果就是同一时间只允许一个线程执行lock()方法后面的代码。  
**类ReentrantReadWriteLock的使用：读写互斥**  
假如同时有两个方法，读方法，写方法，两个方法都不同的读、写锁，则他们的操作时互斥的。  
**总结**  
“读写”、“写读”、“写写”都是互斥的，而“读读”是异步的，非互斥的。  

----------------------

### 5. 定时器Timer
#### 5.1 定时器Timer的使用  
在Jdk库中Timer类主要负责计划任务的功能，也就是在指定时间开始执行某一个任务。  
Timer类的主要作用就是设置计划任务，但封装任务的类却是TimerTask类，执行计划任务的代码要放入TimerTask的子类中，因为TimerTask是一个抽象类。  
**方法schedule(TimerTask task,Date time)的测试**  
该方法的作用是在指定的日期执行一次某一任务。  
``` java
/**
     * 创建成守护进程
     */
    private static Timer timer = new Timer(true);

    static public class MyTask extends TimerTask {
        @Override
        public void run() {
            System.out.println("运行了，时间为" + new Date());
        }
    }

    public static void main(String[] args) {
        try {
            MyTask task = new MyTask();
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            String dateString = "2018-05-12 16:25:00";
            Date dateRef = sdf.parse(dateString);
            System.out.println("字符串时间：" + dateRef.toLocaleString() + "当前时间：" + new Date().toLocaleString());
            timer.schedule(task, dateRef);
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }
    结果：
    字符串时间：2018-5-12 16:25:00当前时间：2018-5-12 16:24:34
```
程序运行完迅速结束当前线程，并且TimerTask中的任务不再被执行，应为进程已经结束了。  
**计划时间早于当前时间：提前运行的结果**，如果执行任务的时间过早于当前时间，则立即执行Task任务。  
**多个TimerTask任务及延时的测试**  
TimerTask是以**队列**的方式一个一个被顺序执行的，所以执行的时间有可能和预期的时间不一致，因为前面的任务有可能消耗时间较长，则后面的任务运行的时间也会被延迟。  
**方法schedule(TimerTask task,Date firstTime,long period)的测试**  
该方法的作用是在指定的日期之后，按指定的间隔周期无限循环地执行某一任务。  
参数一：要运行的目标方法；二：开始时间；三：时间间隔(ms)  
如果时间设早了，则马上运行。  
**TimerTask类的cancel()方法**  
作用是将自身任务从任务队列中移除。  
**Timer类的cancel()方法**  
他与TimerTask中的cancel方法不一样，它的作用是清除任务队列中的全部任务。  
**注意**：cancel()方法有时并不一定会停止执行任务，而是正常执行，这是因为Timer类的cancel()方法有时并没有争抢到queue锁，所以TimerTask类中的任务才会继续正常执行。    
**方法scheduleAtFixedRate(TimerTask task,Date firstTime,long period)的测试**  
方法schedule和方法scheduleAtFixedRate都会按照顺序执行，所以不用考虑非线程安全。  
方法schedule和scheduleAtFixedRate主要的区别只在于不延时的情况。  
方法schedule：如果执行方法没有被延时，那么他下一次的执行时间间隔，是参考上一次“开始”的时间的；
方法scheduleAtFixedRate：如果执行方法没有被延时，那么他下一次的执行时间间隔，是参考上一次“结束”的时间的；
方法schedule不具有追赶执行性，而scheduleAtFixedRate具有。

-----------------------------

### 6. 单例模式与多线程  
问题：如何使单例模式遇到多线程时安全的、正确的。  
#### 6.1 立即加载/"饿汗模式"  
什么是立即加载？  
立即加载就是使用类的时候已经将对象创建完毕。常见的实现办法就是直接new实例化，而立即加载从中文的语境来看，有“着急、“急迫”的含义，所以也称“饿汉模式”！  
立即加载/"饿汉模式"是在调用方法前，实例已经被创建。  
``` java
public void MyObject{
    //立即加载模式==饿汉模式
    private static MyObject myObject=new MyObject;
    private MyObject{
    }
    public static MyObject getInstance(){
        //此代码版本为立即加载
        //此版本代码的缺点是不能有其他实例变量
        //因为getInstance()没有同步
        //所以有可能出现非线程安全
        return myObject;
    }
}
```
#### 6.2 延迟加载/“懒汉模式”  
什么是延迟加载？延迟加载时在调用get()时实例才创建，常见的实现方法是在get()中进行new实例化，而延时加载中文的语境是“缓慢”，“不急迫”的含义，所以也称为“懒汉模式”。  
``` java
public void MyObject{
    //立即加载模式==懒汉模式
    private static MyObject myObject;
    private MyObject{
    }
    public static MyObject getInstance(){
        if(myObject!=null){
        }else{
            myObject=new MyObject;
        }
        return myObject;
    }
}
```
上面的实验虽然取得一个对象的示例，但如果在多线程的环境中，就会出现多个实例，与单例模式的初衷相背离。  
缺点：  
上面演示了单例模式的“懒汉”和“饿汉”，但是“懒汉”在多线程环境中会创建出多个实例，所以说是错误的单例模式。  
为“懒汉模式”加入同步关键词  
``` java
public void MyObject{
    //立即加载模式==懒汉模式
    private static MyObject myObject;
    private MyObject{
    }
    //整个方法上锁，设置同步方法效率太低了
    synchronized public static MyObject getInstance(){
        if(myObject!=null){
        }else{
            myObject=new MyObject;
        }
        return myObject;
    }
}
```
加入同步synchronized关键字得到相同实例的对象，但此种方式的效率却非常低下，是同步运行的。下一个线程想要获取，要等上一个线程释放锁，才能执行。  
那么改成同步块会不会好些呢？  
``` java
public void MyObject{
    //立即加载模式==懒汉模式
    private static MyObject myObject;
    private MyObject{
    }
    public static MyObject getInstance(){
        //这种写法跟直接在方法上加是没区别的，效率一样的低，全部代码被加锁。
        synchronized (MyObject.class){
            if(myObject!=null){
            }else{
                //模拟操作
                sleep(3000);
                myObject=new MyObject;
            }
        }
        return myObject;
    }
}
```
尝试在改善，只加部分代码锁呢
``` java
public void MyObject{
    //立即加载模式==懒汉模式
    private static MyObject myObject;
    private MyObject{
    }
    public static MyObject getInstance(){
            if(myObject!=null){
            }else{
                //模拟操作
                sleep(3000);
                //虽然代码被部分上锁，但还是有非线程安全
              synchronized (MyObject.class){
                myObject=new MyObject;
              }
            }
        return myObject;
    }
}
```
此种方法是同步synchronized语句块，虽然效率得到提升，但遇到多线程环境下，还是无法解决得到同一个实例对象的结果。那要如何解决“懒汉模式”的多线程问题呢？   
**使用DCL双检查所机制**    
``` java
public void MyObject{
    private volatile static MyObject myObject;
    private MyObject{
    }
    //使用双检测机制来解决这个问题，既保证了不需要同步代码的异步执行性
    //有保证了单例的结果
    public static MyObject getInstance(){
            if(myObject！=null){
            }else{
                //模拟操作
                sleep(3000);
                //虽然代码被部分上锁，但还是有非线程安全
              synchronized (MyObject.class){
                myObject=new MyObject;
              }
            }
        return myObject;
    }
}
```
使用双重检查锁功能，成功地解决了“懒汉模式”遇到的多线程问题。DCL也是大多数多线程结合单例模式使用的解决方案。  
#### 6.3 使用静态内置类实现单例模式  
DCL可以解决多线程单例模式的非线程安全，当然，使用其他的方法也能达到同样的效果,如下面的例子。  
``` java
public void MyObject{
    private static class MyObjectHandler{
        private static MyObject myObject=new MyObject();
    }
    private MyObject{
    }
    public static MyObject getInstance(){
        return MyObjectHandler.myObject;
    }
}
```
#### 6.4 序列化与反序列化的单例模式实现  
静态内置类可以达到线程安全问题，但如果遇到序列化对象是，使用默认的方式运行得到的结果却是多例的。  
这是需要在反序列化中使用readResolve()方法  
``` java
public void MyObject{
    private static class MyObjectHandler{
        private static MyObject myObject=new MyObject();
    }
    private MyObject{
    }
    protected Object readResolve() throws ObjectStreamException(){
        System.out.println("调用了readResolve方法");
        return MyObjectHandler.myObject;
    }
}
```
#### 6.5 使用static代码块实现单例模式  
静态代码块中的代码在使用类之前的时候就已经执行好了，所以应用静态代码块的这个特性来实现单例模式。  
``` java
public void MyObject{
   private static MyObject instance =null;
    private MyObject{
    }
    static{
        instance=new MyObject();
    }
    public static MyObject getInstance(){
        return instance;
    }
}
```
#### 6.6 使用enum枚举数据类型来创建单例模式  
枚举enum和静态代码块的特性相似，使用枚举类时，构造方法会被自动调用，也可以应用这个特性创建单例模式。  
``` java
public enum MyObject{
    enumFactory;
   private static MyObject instance;
    private MyObject{
      instance=new MyObject();
    }
    public static MyObject getInstance(){
        return instance;
    }
}
```
#### 6.7 完善使用enum枚举实现单例模式  
上面将枚举类进行曝露，违反了“职责单一原则”，下面进行完善：   
``` java
public void MyObject{
    public enum MyEnumSingleton{
        enumFactory;
        private static MyObject instance;
        private MyObject{
          instance=new MyObject();
        }
        public MyObject getInstance(){
            return instance;
        }
    }
    public static MyObject getInstances(){
        return  MyEnumSingleton.enumFactory.getInstance();
    }
}
```

--------------

### 7. 拾遗增补  
#### 7.1 线程的状态  
#### 7.2 线程组  
#### 7.3 使线程具有有序性  
#### 7.4 SimpleDateFormat非线程安全  
#### 7.5 线程中出现异常的处理  
#### 7.6 线程组内处理异常  
#### 7.7 线程异常处理的传递  






### 线程池  
Java通过Executors提供四种线程池，分别为：  
newCachedThreadPool创建一个可缓存线程池，如果线程池长度超过处理需要，可灵活回收空闲线程，若无可回收，则新建线程。  
newFixedThreadPool 创建一个定长线程池，可控制线程最大并发数，超出的线程会在队列中等待。  
newScheduledThreadPool 创建一个定长线程池，支持定时及周期性任务执行。  
newSingleThreadExecutor  创建一个单线程化的线程池，它只会用唯一的工作线程来执行任务，保证所有任务按照指定顺序(FIFO, LIFO, 优先级)执行。  











