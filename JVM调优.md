# JVM 调优

# 第一章：概述

想继续JVM调优，必须先理解JVM虚拟机的内存模型

总的来说，JVM的堆内存在物理上分为两部分

1. **Young Generation** （年轻一代）
2. **Old Generation** （老一代）




# 第二章：年轻一代

young Generation是所有新对象创建的地方

当young generation 充满时，垃圾回收就会开始执行

> 这种垃圾回收被称为`Minor GC`

Young Generation分为三个部分

即一个`Eden Memory` 和两个`Survivor Memory`

有关Young Generation的几个重要观点

1. 绝大多数新对象创建的内存区域，都是在`Eden Memory`里面

2. 当`Eden Memory`充满时，JVM会执行`Minor GC` ,并将还存活的对象全都移动到其中一个

   `Survivor Memory`内存区域中去

3. `Minor GC` 也会检查`Survivor Memory`内存区域中存活的对象，并将其移动到另一个`Survivor Memory`内存中。所以每次执行完后，其中一个`Survivor Memory`内存区域总是空的

4. 经过多次`Minor GC`的摧残后还能活下来的对象，将被移动到养老院，也就是**Old Generation** （老一代）的内存空间。

   到底有多少次`Minor GC`呢？这就涉及到一个门槛问题了   ​
# 第三章：年老一代

Old Generation 内存中包含的对象一般都是活的很久，并经过多次的垃圾回收后还存活的。

通常垃圾回收是在Old Generation的内存满的时候中执行的。

Old Generation的垃圾回收又被称为`Major GC` 通常需要更多的时间

# 第四章 ：垃圾回收

一个很重要的特性：

所有垃圾回收都是`时间静止`时间，也就是说，当垃圾回收执行时，所有应用程序线程都会停止，直至操作完成


