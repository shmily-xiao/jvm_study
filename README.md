# jvm_study

-- 
https://www.cnblogs.com/wade-luffy/p/5969418.html

偏向锁：
  无锁状态，偏向锁状态，轻量级锁状态和重量级锁状态。锁可以升级但是不能降级。
  大多数情况下锁不仅不存在多线程竞争，而且总是由同一线程多次获得。偏向锁的目的是在某个线程获得锁之后，消除这个线程锁重入（CAS）的开销，看起来让这个线程得到了偏护。
|---|---|
偏向锁	| 加锁和解锁不需要额外的消耗，和执行非同步方法比仅存在纳秒级的差距	| 如果线程间存在锁竞争，会带来额外的锁撤销的消耗	| 适用于只有一个线程访问同步块场景
轻量级 | 竞争的线程不会阻塞，提高了程序的响应速度 | 如果始终得不到锁竞争的线程使用自旋会消耗CPU | 追求响应时间,锁占用时间很短
重量级锁 | 线程竞争不使用自旋，不会消耗CPU	| 线程阻塞，响应时间缓慢	| 追求吞吐量,锁占用时间较长
  
偏向线程id：
  在对象的mark word 里面存放偏向的那个线程的id

偏向时间戳：
  JVM对那种会有多线程加锁，但不存在锁竞争的情况也做了优化，听起来比较拗口，但在现实应用中确实是可能出现这种情况，因为线程之前除了互斥之外也可能发生同步关系，被同步的两个线程（一前一后）对共享对象锁的竞争很可能是没有冲突的。对这种情况，JVM用一个epoch表示一个偏向锁的时间戳（真实地生成一个时间戳代价还是蛮大的，因此这里应当理解为一种类似时间戳的identifier）
  

# OOM
在 堆、虚拟机栈和本地方法栈、方法区、运行时常量池（metaspaceSize）、本机直接内存（反射使用Unsafe::allocateMemory分配内存） 都可能会出现OOM或者是StackOverflowError。

# jstat
可以上orcale的官网去查看虚拟机的工具的用法。
gc的运行内核的理解：
```
S0C - survivor 0区域的容量，以KB为单位
S1C - survivor 1区域的容量，以KB为单位
Young Gen被划分为1个Eden Space和2个Suvivor Space。当对象刚刚被创建的时候，是放在Eden space。垃圾回收的时候，会扫描Eden Space和一个Suvivor Space。如果在垃圾回收的时候发现Eden Space中的对象仍然有效，则会将其复制到另外一个Suvivor Space。

就这样不断的扫描，最后经过多次扫描发现任然有效的对象会被放入Old Gen表示其生命周期比较长，可以减少垃圾回收时间。

S0U - survivor 0区域的使用大小，以KB为单位
S1U - survivor 1区域的使用大小，以KB为单位
EC - Eden区域的容量，以KB为单位
EU - Eden区域的使用，以KB为单位
OC - old区域的容量，以KB为单位
OU - old区域的使用，以KB为单位
MC - Metaspace元数据区的 Committed Size，以KB为单位
MU - Metaspace元数据区的使用大小，以KB为单位
在JDK8之前，类定义、字节码和常量等很少会变更的信息是放在持久代Perm Gen中的。不过在JDK8之后，Perm Gen已经被取消了，现在叫做Metaspace。Metaspace并不在java虚拟机中，它使用的是本地内存。Metaspace可以通过-XX:MaxMetaspaceSize来控制。

CCSC - Compressed class的Committed Size，以KB为单位
CCSU - Compressed class的使用大小，以KB为单位
Compressed Class Space，它是和-XX:+UseCompressedOops，-XX:+UseCompressedClassesPointers有关的。实际上是一个指针的压缩，可以使用32bits来表示之前64bits的指针。

YGC - young generation的GC次数
YGCT - young generation的GC时间
FGC - full GC的次数
FGCT - full GC的时间
GCT - 总的GC时间

这两个指标描述的比较少，原文的解释是：
Add CGC column to jstat for monitoring STW (stop the world) phases in concurrent GC.

CGC: 并发GC时候的次数
CGCT: 并发GC时候的所花费的时长
```
# jstack
```
jstack -l pid : 除堆栈外，显示关于锁的附加信息
jstack -F pid ：当正常输出的请求不被响应时，强制输出线程堆栈
jstack -m pid： 如果调用到本地方法的话，可以显示C/C++的堆栈
```
可以在自己的代码中使用 java.lang.Thread 的 getAllStackTraces() 来捕获虚拟机中的所有线程的StackTraceElement对象。
