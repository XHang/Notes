# Hadoop 全家桶笔记

# 第一章：Hadoop

## 1. Hadoop简介

 hadoop是一个分布式应用系统。看起来好像是专门处理大数据的存储和取出的一套系统
 它不是一个软件。包含两个核心软件,一个是文件系统HDFS，一个是处理文件的引擎MapReduce

## 2. HDFS介绍

 HDFS：HDFS是一个分布式文件系统，高度容错性，一般的数据崩溃吓不了它

 适合部署在廉价的机器上，提供高吞吐量的数据访问，大数据集的应用特别适合它

 它的设计特点是：

1.  文件分块存储，一个文件可能分为几块，存在不同的节点上。
2. 大数据文件，至少是T级别的文件，G级别的没意思
3. 流式数据访问，倾向于一次写入，多次访问，希望不用动态改变文件内容，就算是改变只能在文件末尾添加
4. 廉价硬件，普通PC就可以搞定
5. 硬件故障，会将文件的一部分创建副本保存起来，这样即使原文件块损坏了，也能取到文件

HDFS的相关名词

1. Block：将一个文件进行分块，通常是64M。

2. NameNode:保存整个文件系统的目录信息、文件信息及分块信息，这是由唯一一台主机专门保存。当然这台主机如果出错，NameNode就失效了。在Hadoop2.*开始支持activity-standy模式

   > activity-standy即如果主NameNode失效，启动备用主机运行NameNode。 
   >
   > 另注：由于NameNode的特殊性，其实集群里面的每一台机器都需要知道NameNode的地址的

3. DataNode：分布在廉价的计算机上，用于存储Block块文件。

   > DataNode结点会先在NameNode上注册，这样它们的数据才可以被使用。


## 3. MapReduce介绍

MapReduce负责取出文件的逻辑

诸如取出数据的最大值，就是MapReduce的本事了

它的原理是这样的：

把输入集切割成若干个独立的数据集，由Map任务以完全并行的方式来处理它们，最后把处理的结果输入给*reduce*任务

 ## 4. Hadoop的安装

先决

1. Jdk1.8以上
2. ssh支持

安装步骤

1. 下载管网提供的二进制压缩包

2. 进入进入到解压目录

3. 编辑此文件 etc/hadoop/hadoop-env.sh。如下面所示

   ```
   set to the root of your Java installation
   export JAVA_HOME=/home/jdk/jdk1.8.0_11
   ```

4.  尝试运行该命令bin/hadoop

   > 放心，这个命令只是列出命令列表罢了，不会开启运行的

	接下来会配置Hadoop的运行方式，Hadoop的运行方式有三种

	1：单机模式  2：伪分布模式   3：集群模式

	这篇教程使用第二种，也就是伪分布模式

5. 编辑：etc/hadoop/core-site.xml:

   ```
   <configuration>
      		<property>		 	
      		<name>fs.defaultFS</name>     		
      		<value>hdfs://localhost:9000</value>
      		</property>
   </configuration>
   ```

6. 编辑：etc/hadoop/hdfs-site.xml

    ```
    <configuration>
    <property>
        <name>dfs.replication</name>
        <value>1</value>
    </property>
    </configuration>
    ```

    其中：` <value>1</value>`是文件块的数据备份个数。

    一般来说一个设置为3，数字无上限，多的可能无用，且占用空间，少之可能影响数据可靠性。、

7. 设置ssh的无密码登录。此步也可以省略。如果你是个抖M的话。想尝试每次开启hadoop都输入密码的话。

    > 为什么hadoop要用到ssh呢？
    >
    > 因为在伪分布式下运行时必须启动守护进程，而启动守护进程的前提是已经成功安装SSH。、

    怎么设置ssh的无密码登录呢？首先测下ssh是不是已经支持免密码登录了，输入

    `ssh localhost` 如果提示你要输入密码。那就是没开通免密码登录

    设置免密码登录的步骤

    1.  ssh-keygen -t rsa -P '' -f ~/.ssh/id_rsa
    2.  cat ~/.ssh/id_rsa.pub >> ~/.ssh/authorized_keys
    3. chmod 0600 ~/.ssh/authorized_keys

8. 接下来创建分布式文件系统吧，执行此条命令

    `bin/hdfs namenode -format`

9. 然后是开启NameNode和DataNode

    `sbin/start-dfs.sh`

10. 完毕

## 6. 安装的一些问题

1. 你成功按照上面的命令把hadoop安装上去了，但是在执行hdfs相关的命令时，报连接拒绝

   解决办法，编辑`HDFS-site.xml`

   加上这段配置

   ```
   <property>
       <name>dfs.data.dir</name>
       <value>/home/cxh/hadoop-3.0.3/data</value>
   </property>
   ```

   其中路径要是绝对路径

## 7. HDFS的命令

这一部分主要是演示HDFS的一些简单的命令，这些简单的命令可以作为安装Hadoop第一个尝试的地方。

验证你的Hadoop是否安装成功了

### 7.1 创建一个文件夹

命令：` bin/hdfs dfs -mkdir /user`

### 7.2:将一个linux文件系统中存在的文件放在分布式文件系统中

` bin/hdfs dfs -put etc/hadoop/*.xml input`

### 7.3 从分布式文件系统里面拿文件

`bin/hdfs dfs -get output output`

### 7.4 关闭NameNode和DataNode

执行这个命令

`sbin/stop-dfs.sh`

### 7.5 列出文件列表

`./bin/hdfs dfs -ls <path>`

如上，就可以列出指定路径下面的文件和文件夹列表

### 7.6 查看文件内容

`./bin/hdfs dfs -cat <filepath>`

### 7.7 删除指定文件/文件夹

`./bin/hdfs dfs -rm /user/cxh.txt`

删除文件夹跟删除文件一样，不过还要加上 -r参数

意思就是递归删除咯

其实很多命令跟linux命令的习惯是一样的。。



## 7.8 查看节点信息

输入命令：`./bin/hadoop dfsadmin -report`

显示的结果如下

```

Configured Capacity: 0 (0 B)
Present Capacity: 0 (0 B)
DFS Remaining: 0 (0 B)
DFS Used: 0 (0 B)
DFS Used%: 0.00%
Replicated Blocks:
        Under replicated blocks: 0
        Blocks with corrupt replicas: 0
        Missing blocks: 0
        Missing blocks (with replication factor 1): 0
        Pending deletion blocks: 0
Erasure Coded Block Groups:
        Low redundancy block groups: 0
        Block groups with corrupt internal blocks: 0
        Missing block groups: 0
        Pending deletion blocks: 0

```

没错，你的数据节点挂了

如果是正常的，显示的结果如下

```

```



## 7.9 安全模式相关

1. 查看当前是否属于安全模式

   `hadoop dfsadmin -safemode get`




## 8 YARN的部署

YARN是Hadoop的资源管理系统，致力于管理Hadoop的MapReduce 框架，将

ResourceManger(资源管理 )和JobScheduling/JobMonitoring (任务调度监控)分开

如果你想在Hadoop上运行MapReduce 作业，最好还是把这货更新上。

怎么部署呢？假定你已经完成了Hadoop的安装。那么你就可以看下面的YARN的部署了

1. 配置这个文件`etc/hadoop/mapred-site.xml `

   ```
   <configuration>
       <property>
           <name>mapreduce.framework.name</name>
           <value>yarn</value>
       </property>
   </configuration>
   ```

2. 配置这个文件`etc/hadoop/yarn-site.xml`

   ```
   <configuration>
       <property>
           <name>yarn.nodemanager.aux-services</name>
           <value>mapreduce_shuffle</value>
       </property>
   </configuration>
   ```

3. 开启YARN

   `sbin/start-yarn.sh`

4. 搞定，想关闭YRAN的话，执行这个命令

   ` sbin/stop-yarn.sh`

## 9:Hadoop的编程使用

这章主要是使用Java，用Hadoop完成一些小功能，比如说，完成一个字段统计的功能。

**第一步：搭建一个简单的Mavel项目吧**

pom文件如下所示

```
<dependency>
	<groupId>org.apache.hadoop</groupId>
	<artifactId>hadoop-common</artifactId>
	<version>3.0.3</version>
</dependency>
<dependency>
	<groupId>junit</groupId>
	<artifactId>junit</artifactId>
	<version>4.4</version>
	<scope>test</scope>
</dependency>
<dependency>
	<groupId>org.apache.hadoop</groupId>
	<artifactId>hadoop-hdfs</artifactId>
	<version>3.0.3</version>
</dependency>
<dependency>
	<groupId>org.apache.hadoop</groupId>
	<artifactId>hadoop-mapreduce-client-core</artifactId>
	<version>3.0.3</version>
</dependency>
<dependency>
	<groupId>org.apache.hadoop</groupId>
	<artifactId>hadoop-client</artifactId>
	<version>3.0.3</version>
</dependency>
<dependency>
	<groupId>com.oracle</groupId>
	<artifactId>jdk.tools</artifactId>
	<version>1.0</version>
	<scope>system</scope>
	<systemPath>${JAVA_HOME}/lib/tools.jar</systemPath>
</dependency>
```

该配置主要参考非官方教程，因为实在在官方文档找不到需要依赖的项，所以只能如此了。

如果有更官方的依赖项，欢迎补充。

要注意的是

1. 本教程使用的hadoop是3.0.3，所以依赖的版本全都是3.0.3

   如果你的的hadoop版本不是3.0.3.需要改下依赖性的version。

2. `jdk.tools`是安装jdk就自带的一个jar包，这里使用了系统的绝对路径来指定这个jar包的路径。

   你的路径可能未必跟我一样，可能也需要更改

**第二步：编写一个简单的WordCount**

即计算单词数的小程序。程序的代码如下

```
public class WordCount  {
    public static class TokenizerMapper
            extends Mapper<Object, Text, Text, IntWritable> {
        private final static IntWritable one = new IntWritable(1);
        private Text word = new Text();

        public void map(Object key, Text value, Context context) throws IOException, InterruptedException {
            StringTokenizer itr = new StringTokenizer(value.toString());
            while (itr.hasMoreTokens()) {
                word.set(itr.nextToken());
                context.write(word, one);
            }
        }
    }

    public static class IntSumReducer
            extends Reducer<Text,IntWritable,Text,IntWritable> {
        private IntWritable result = new IntWritable();
        public void reduce(Text key, Iterable<IntWritable> values, Context context) throws IOException, InterruptedException {
            int sum = 0;
            for (IntWritable val : values) {
                sum += val.get();
            }
            result.set(sum);
            context.write(key, result);
        }
    }
    public static void main(String [] args) throws IOException, ClassNotFoundException, InterruptedException {
        Configuration conf = new Configuration();
        Job job = Job.getInstance(conf, "word count");
        job.setJarByClass(WordCount.class);
        job.setMapperClass(TokenizerMapper.class);
        job.setCombinerClass(IntSumReducer.class);
        job.setReducerClass(IntSumReducer.class);
        job.setOutputKeyClass(Text.class);
        job.setOutputValueClass(IntWritable.class);
        FileInputFormat.addInputPath(job, new Path("/input"));
        FileOutputFormat.setOutputPath(job, new Path("/output"));
        System.exit(job.waitForCompletion(true) ? 0 : 1);
    }
}
```

程序需要指定下输入的数据和输出的数据

主要在这两句指定

```
 FileInputFormat.addInputPath(job, new Path("/input"));
 FileOutputFormat.setOutputPath(job, new Path("/output"));
```

这里面的`/inut`和`/output`是分布式文件系统里面的文件夹，可别搞错了

input文件里面放我们需要统计字数的数据文件，output文件夹里面放生成的结果文件。





**第三步：设定输入数据和输出路径**

首先创建两个文件文件，假定内容如下

文件1：`Hello World Bye World`

文件2：`Hello Hadoop Goodbye Hadoop`

然后把他们put到分布式系统里面的input文件夹。

OK，输入数据准备完毕。

输出呢？其实只需要创建一个output文件夹即可。

怎么创建，嗯，上面的HDFS命令已经有了。



**第四步：设定环境变量**

如下所示

```
export JAVA_HOME=/usr/java/default
export PATH=${JAVA_HOME}/bin:${PATH}
export HADOOP_CLASSPATH=${JAVA_HOME}/lib/tools.jar
```

仅供参考



**第五步：运行程序**

将这个简单的项目打包成jar文件，并放在安装hadoop的机子上。

然后执行这个命令

`./bin/hadoop jar /home/cxh/wc.jar com.hadoop.example.WordCount`

如果你足够幸运，可以看到运行不报错，那很OK。不过笔者显然没那么幸运。

**问题1**

`找不到这个这个类：org.apache.hadoop.mapreduce.v2.app.MRAppMaster`

解决方案

1. 在`yarn-site.xml `追加配置

   ```
   <property>
          <name>yarn.application.classpath</name>
           <value>
           /hadoop_install/hadoop-2.6.5/etc/hadoop:/hadoop_install/hadoop-2.6.5/share/hadoop/common/lib/*:
           /hadoop_install/hadoop-2.6.5/share/hadoop/common/*:
           /hadoop_install/hadoop-2.6.5/share/hadoop/hdfs:
           /hadoop_install/hadoop-2.6.5/share/hadoop/hdfs/lib/*:
           /hadoop_install/hadoop-2.6.5/share/hadoop/hdfs/*:
           /hadoop_install/hadoop-2.6.5/share/hadoop/yarn/lib/*:
           /hadoop_install/hadoop-2.6.5/share/hadoop/yarn/*:
           /hadoop_install/hadoop-2.6.5/share/hadoop/mapreduce/lib/*:
           /hadoop_install/hadoop-2.6.5/share/hadoop/mapreduce/*:
           /hadoop_install/hadoop-2.6.5/contrib/capacity-scheduler/*.jar
           </value>
    </property>
   ```

   2. 重启服务器

   > 注意：`/hadoop_install/hadoop-2.6.5`请替换为你自己hadoop的安装路径
   >
   > 什么，你想把上面的路径配置成环境变量，虽然想法很好，但是不能生效

**问题2**

`Name node is in safe mode.`

这个其实很简单，因为hadoop处于安全模式下，是不允许修改文件的。

当然你可以退出安全模式。执行这个命令

`bin/hadoop dfsadmin -safemode leave `

**问题3**

`running beyond virtual memory limits. Current usage: 205.4 MB of 1 GB physical memory used; 2.5 GB of 2.1 GB virtual memory used. Killing container`

很简单，就是虚拟内存不够了，它使用了2.5GB的虚拟内存，但是你的机器只能提供2.1GB的内存，所以就挂了。

这个问题只要充钱买内存就能解决。。开玩笑。

你要设置虚拟内存，想当然的情况就是设置linux自己的虚拟内存，然鹅，笔者设置了之后还是不行。

最后可以使用这个,编辑`yarn-site.xml `添加此配置

```
<property>
  <name>yarn.nodemanager.vmem-pmem-ratio</name>
  <value>5</value>
</property>
```

这个是设置增加虚拟内存到物理内存的比率。默认是2.1。

**问题4**

`Error: org.apache.hadoop.hdfs.BlockMissingException: Could not obtain block: `

这个意思大概是说，无法获取块。

我查了一下，有篇文章说是因为块副本位置没有指定路径，于是默认配置为/tmp路径。

但是这个tmp文件经常会被linux进行大清除，所以，块副本数据也就被清扫了。

解决办法，第一步，先手动指定下块副本文件存放的位置，编辑`hdfs-site.xml `

```
    <property>
        <name>dfs.datanode.data.dir</name>
        <value>/home/cxh/hadoop-3.0.3/HDFSData</value>
    </property>
```

第二步：想方法恢复数据，我采取的做法是删除然后重新put进去。

第三步，重启hadoop

第四部：没了，尽情测试吧。反正LZ成功了。

## 10 在外网上，通过web访问Hadoop 的webUI

一般情况下，hadoop 提供的webUI都是只能在本地回环地址上面使用的。如果想在外网上访问Hadoop的外网UI的话。需要在`hdfs-site.xml`里面配置

```
<property>
  <name>dfs.http.address</name>
  <value>0.0.0.0:50070</value>
  <!--NameNode web管理端口,如果端口为0，将在空闲端口启动-->
</property>
```

然后重启hadoop,在外网上，键入地址`http://ip:50070`即可进入。

默认情况是这个端口啦，当然不能保证你可能会改配置。

## 11 BUG集合

1. 许久没登录hadoop了，某次启动后，执行一个命令，报端口9000无法连接。

   查了许久，没有发现问题，后来。。

   重新格式化分布式文件系统，重启，好了。。。

2. 某次启动后（好吧，其实之前还格式化过系统。。。）

   总之，在将数据放入系统时，报了一个错误，大概意思是说，没有数据节点。

   确实，后来找到报告也是说没有数据节点。

   最后我是怎么解决的呢？、

   很简单，删除原先分布式文件系统的一切东西，重新格式化一遍

   大概原因也明了了，因为我之前格式化文件系统没有考虑到hadoop的感受。

   导致老数据和新数据冲突了











# 第二章：HBase










	

	
# export JAVA_HOME=/usr/java/jdk1.6.0/
改为 export JAVA_HOME=/home/jdk/jdk1.8.0_11    根据自己的java安装目录自己填写！
第二步
	修改conf/hbase-site.xml文件
	添加：
	 <property>
    		<name>hbase.rootdir</name>
    		<value>file:///home/testuser/hbase</value>  	 //可以替换为HDFS的文件地址，比如说hdfs://localhost:9000/hbase
  </property>
  <property>
    		<name>hbase.zookeeper.property.dataDir</name>
    		<value>/home/testuser/zookeeper</value>  	//存放zookeeper的目录
  </property>
  第三步：
  	 启动bin/start-hbase.sh ，现在你可以访问：http://localhost:16010进入Hbase的管理界面。并且运行jps可以看到Hbase的守护进程
  	好了，下面你可以shell进行增删改查
  	
  	附上伪分布配置
  	首先停止Hbase，如果它正在运行的话
  	编辑 hbase-site.xml
  	加上
<property>
  <name>hbase.cluster.distributed</name>
  <value>true</value>
</property>
就是指示Hbase以分布式运行
修改hbase.rootdir文件路径，改为hdfs，上面已经有示例了


  	 

安装成功并启动Hbase
即可通过一下地址访问
192.168.21.253:16010  hbaseService

好了，开发环境配置成功，接下来首先用自带的命令来操作。
PS：所以表名什么名之类的，都要用双引号括起来
创建表
create '表名', 'ColumnFamily名'
  list '表名'  列出表的信息
 插入数据
  推入数据  put  'test', 'row1', 'cf:a', 'value1'
  意思是插入在test表的row1，列是cf:a，值是value1
  注意：Hbase的列包括ColumnFamily的前缀，所以推入数据的列名为cf:a,随便写列名可是要受惩罚的
 查询数据
 	查询所有数据    scan 'test'
 	只获取一行数据  get 'test' ,'row1'
  警用表
  		想修改表，删除表或者更改某些设置需要警用表
  		disable 'test'
  	启用表
  		enable 'test'
  	删除表
  		drop 'test'
  	quit退出shell命令
  	./bin/stop-hbase.sh停止hbase数据库运行
  	
  	连接psftp
  	psftp -l 用户名 192.168.21.253
  	获取远程文件到主机
  	 get /home/Hbase/hbase-1.3.0/lib/metrics-core-2.2.0.jar
  	 
  	 
  	 pscp   LICENCE    god@192.168.1.105:/home/god
  	
  Failed to locate the winutils binary in the hadoop binary path 
 解压这个文件，设置bin的环境变量 hadoop-common-2.2.0-bin-master.zip
 不过没设置也没事？反正我设置了解决不了问题。



	  把分布式的一个文件拷贝到本地linux系统中
	  bin/hdfs dfs -get output 本地目录
	  
	  直接在分布式文件系统中查看某文件
	  bin/hdfs dfs -cat output/*
	  停止NameNode和DataNode的守护进程
	  sbin/stop-dfs.sh
	  接下来，我们就要搞事情了。。。额不，将Hbase和Hadoop结合起来


	  
	  在之前的教程中，我们成功的将Hbase以独立方式运行，并利用shell命令进行了增删改查任务
	  接下来，我们要设置Hbase的数据目录为HDFS分布式文件系统下面的目录，而非普通的linux文件目录
	  第一步：
	  	停止运行Hbase，如果它正在运行的话
	  	第二步：
	  		编辑hbase-site.xml，添加以下属性
			<property>
			  <name>hbase.cluster.distributed</name>
			  <value>true</value>
			</property>
			它指示Hbase以分布式模式下运行，每一个守护进程使用一个jvm示例
			第二步：设置Hbase存储数据的地方，之前是标准的linux文件系统
			现在有了hdfs，当然要搞它啦
			不过首先，看看你的hdfs的协议，url，端口
			请打开Hadoop的core-site.xml
			查看这个属性fs.defaultFS的值，将其修改到hbase.rootdir的值（hbase-site.xml文件）
			好了，重启一下Hbase吧，老天保佑不要报错。。。
			没问题
			接下来去HDFS看看文件夹建立成功了没？
			我就不用看了，因为我手动创建了文件夹。（文档其实说不用手动创建的。。。）
			怎么看，用HDFS的命令啊，摔！
			嗯，建几个表玩玩吧
			第三步：启动和停止备份的HBase Master（HMaster）服务器。
			老实说，一个单机启动这个服务器没啥意见，不过既然是为了测试和学习，可以尝试下
			注:（HMaster）服务器。用于控制集群，此外，你还可以启动多达9个的（HMaster）服务器
			local-master-backup.sh即可启用。
			启动之前，需要说明一下，对于要启动的备份（HMaster）服务器，
			要添加一个端口偏移量以表示这个备份服务器是针对哪个主服务器进行备份的。
			主服务器一般采用三个端口（默认为16010,16020和16030）
			假设偏移量为2，那么备份服务器的端口就是
			 16012/16022/16032, 
			以下命令使用端口
			 16012/16022/16032, 
			 16013/16023/16033, and 
			 16015/16025/16035.
			 启动三个备份服务器
			 ./bin/local-master-backup.sh 2 3 5
			 想杀死备份主服务器但是又不影响整个群集，可以在/tmp/hbase-USER-X-master.pid找到pid
			 用xargs kill -9 搞死他
			 
			 第四步：启动和停止其他RegionServers
			local-regionservers.sh命令允许您运行多个RegionServers。
			它的工作方式与local-master-backup.sh命令类似，您提供的每个参数表示实例的端口偏移量。
			每个RegionServer需要两个端口，默认端口为16020和16030.但是，由于默认端口由HMaster使用，
			因此其他RegionServers的基本端口不是默认端口，
			下面这个命令启动四个附加的RegionServers，
			在从16202/16302（基本端口16200/16300加2）开始的顺序端口上运行。
			.bin/local-regionservers.sh start 2 3 4 5
			
			要手动停止RegionServer，请使用local-regionservers.sh命令
			该命令需要两个参数：停止参数和服务器的偏移量
			eg：$ .bin/local-regionservers.sh stop 3


			
--------------------混乱的分割线----------------------
Hbase的配置详解
假定：你已经伪分布式并运行了Hbase和Hadoop
backup-masters
	一个存文本文件，不用找了，不存在的，列出主机备份主进程的主机，每行一个主机？
hadoop-metrics2-hbase.properties、
	用于连接HBase Hadoop的Metrics2框架，默认情况下只有里面已注释的示例。
hbase-env.cmd和hbase-env.sh
	用于Windows和Linux / Unix环境的脚本，用于设置HBase的工作环境，包括Java，Java选项和其他环境变量的位置。
	 有很多注释例子哦
 HBase的-policy.xml
 	RPC服务器使用的默认策略配置文件对客户端请求做出授权决策。 仅在启用HBase安全性时使用。
 hbase-site.xml
 	主要的Hbase的配置文件，这个文件覆盖了hbase-default.xml的默认配置。你也可以在
 	HBase16010端口的“ HBase Configuration”选项卡中查看集群的全部有效配置（默认值和覆盖值）
regionservers
	一个存文本文件，存放HBase集群中运行RegionServer的主机列表，默认情况下只有单个条目localhost 	
	它应包含主机名或IP地址列表，每行一个
注意：以分布式运行后，请保证将conf/里面的内容全部复制到每一个节点
			 
​			 
​			 
​			 
	附录：Hadoop的命令列表
	hadoop fs -ls  /  列出根目录下面的所有文件


			


	

 	

 
