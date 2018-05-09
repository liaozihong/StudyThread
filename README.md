# 多线程学习

### 进程和多线程的概念及线程的优点？
  讲到多线程技术时，就不得不提及"进程"这个概念了。百度对进程的接受如下：  
**进程**是操作系统的基础，是一次程序的执行，是一个程序及其数据在处理机上顺序执行时所发生的活动；是程序在一个数据集合运行的过程，他是系统的资源分配和调度的一个独立单位。你可以将操作系统中运行的.exe程序理解成一个“进程”，进程是受操作系统管理的基本运行单元。  
**那么什么是线程呢？**  
我们可以把它理解成进程中独立运行的子程序。比如，QQ.exe运行时就有很多的子任务在同时运行，也就是说可以在同一时间执行不同的功能。  
**使用多线程的优点：**它可以最大限度地利用CPU的空闲时间来处理其他的任务，比如一边让操作系统处理正在由打印机打印的数据，一边编辑word文档。而CPU在这些任无之间不停的切换，由于切换速度十分快，给使用者的感受就是这些任务似乎是同事运行的，所以使用多线程后，可以在同一时间内运行更多不同种类的任务。  

**注意：**多线程是异步的，所以千万不要把Eclipse里代码的执行顺序当成线程的执行顺序，<b>线程被调用的时机是随机的</b>。

### 1. 使用多线程
##### 1.1 java多线程编程与技术
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
*优先级具有随机性*  
优先级高的优先执行完run()，这个结果不能说得太肯定，因为线程的优先级还具有“随机性”，也就是优先性高的不一定每一次都先执行完。  
那么得出一个结论：不要把线程的优先级与运行结果的顺序作为衡量的标准，优先级较高的线程并不一定每一次都先执行完run()中的任务。也就是说线程优先级和打印顺序无关，不要将两者相关联，他们的关系具有不确定性和随机性。  
##### 守护线程(daemon)  
在java线程中有两种线程：用户线程、守护线程。  
守护线程是一种特殊的线程，特征：陪伴，进程中没有其他非守护线程，它也没有存在的意义了，则自动销毁，典型的守护形成是垃圾回收线程。  
举例：任何一个守护线程都是整个jvm中所有非守护线程的“保姆”，只要当前jvm存在一个非守护线程，守护线程就在工作，当最后一个非守护线程结束时，它才跟着结束。
典型的应用是GC（垃圾回收器），他就是一个称职的守护者。  
### 2. 对象及变量的并发访问  
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
**synchronized同步语句块**  
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
**注意**，使用synchronized(非this对象x)格式进行同步操作时，对象监视器(示例中的anyString)必须是同一个对象，如果不是，结果就是异步调用了，会交叉运行。  
线程的执行顺序是不稳定的，就可能出现问题，如当A和B两个线程执行带有分支判断的方法时，就会出现逻辑上的错误，有可能出现脏读，下面通过一个案例来查看，
``` java
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
**所对象的改变：**在将任何数据类型作为同步锁时，需要注意的是，是否有多个线程同时持有锁对象，如果同时持有相同锁对象，则这些线程是同步的，如果是分别获得锁对象，那么线程间是异步的。示例：  
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
**关键字volatile**  
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

**注意：**线程安全包含原子性和可见性两个方面。java的同步机制都是围绕这两个方面来确保线程安全的。  
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
### 3. 线程之间的通信  
线程是操作系统中独立的个体，但这些个体如果不经过特殊的处理就不能成为一个整体。线程间的通信就是成为整体额的必用方案之一。使线程间进行通信后，系统间的交互性会更强大，在大大提高CUP利用率的同时还会使程序员对各线程任务在处理的过程中进行有效的把控与监督。
#### 3.1 等待/通知机制  
**不使用等待/通知机制实现线程间的通信**  
假如利用sleep()结合while(true)死循环来实现多个线程间的通信，虽然可以通信，但存在弊端，就是线程不停的通过while语句轮询机制来检测某一个条件，这样会浪费CPU资源。
如果轮询的时间间隔很大，有可能会取不到想要得到的数据。所以需要一种机制来实现减少CPU的资源浪费，而且还可以在多个线程间通信，那就是“wait/notify”机制。  













































