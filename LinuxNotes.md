# linux的技能

## 常见知识点

1. openssh安装后如何启动？  
  命令：/etc/init.d/ssh start  必须在su权限下运行

2. 怎么用主机访问VirtualBox呢？
  见图。用PuTTY即可访问

3. vin编辑器怎么使用？  
  用vi  文件路径  即可打开一个文本文件  
  初次进入是以命令行模式打开的，要编辑此文件，敲入i即可进入编辑模式。  
  编辑模式下按esc回退到命令行模式  
  将光标移动到某处，按dd即可删除该行  
   「x」：每按一次，删除光标所在位置的“后面”一个字符。  
  「#x」：例如，「6x」表示删除光标所在位置的“后面”6个字符。  
  「X」：大写的X，每按一次，删除光标所在位置的“前面”一个字符。  
  「#X」：例如，「20X」表示删除光标所在位置的“前面”20个字符。  
  按下：号  
  输入wq!强制保存并退出。。    

4. 利用putt传文件到远程服务器  

putt客户端下载下来一般都有那个pscp.exe文件。进入该文件对应的文件夹，敲入cmd命令  
`pscp 发送的源文件   服务器用户名@服务器地址:home`

 敲入后输入密码，即可发送！  
`eg:pscp jdk-8u131-linux-x64.rpm cxh@192.168.21.248:/home`  
注意：有时候发送过去但是找不到文件或者发送时提示`permission denied ` 
就是访问被拒绝了，这时候你得手动更改远程服务器的文件夹为可读可写  chmod 777 xxxx  
注：pscp -r 后面指定文件夹名可以远程传输文件夹  



## linux常用命令

1.安装，删除命令：  

Ubuntu版：  
`dpkg -l` 可以查看安装的软件列表    
`apt-get remove --purge` 名字    可以删除软件    

centos版：
`yum list installed`
`yum remove 软件名`

mv [选项] 源文件或目录 目标文件或目录  
2. 删除文件夹命令

`rm -rf  文件夹路径 ` 

1. 解压命令
  `tar -xzvf file.tar.gz`  

2. 设置linux的环境变量  
    vi  /etc/proifle 在其末尾添加这几句 
```
    export JAVA_HOME="xxx"
    export PATH="$PATH:$JAVA_HOME/bin"
    export JRE_HOME="$JAVA_HOME/jre"
    export CLASSPATH=".:$JAVA_HOME/lib:$JRE_HOME/lib"
       
```
即可设置jdk的环境变量。 
`source /etc/profile`更新一下。。  

9. pwd命令    
  `pwd`命令可以查看当前所在的路径（centos）  


10. 获取java安装目录  
  利用`which java`得到路径1  which java是打印出java命令文件的路径    
  `ls -lrt+路径1`得到 箭头后的路径2  
  `ls -lrt+路径2`就ok了  
  默认情况下，用rpm安装后的java在`/usr/java/jdk1.8.0_144/`  

11. 
   centos查看网络端口占用:
   ` firewall-cmd --zone=public --list-ports`  
   开启或者关闭firewalld（centos7的防火墙）:
    `systemctl start firewalld`and`systemctl stop firewalld`    
   永远禁用centos防火墙:
   `systemctl disable firewalld.service `  

12. 
   centos修改主机名:`hostnamectl set-hostname 主机名`
   `hostnamectl --static` 可以查看主机名

13.
`centos`关机命令
`reboot`  重启
`poweroff` 立刻关机

14.
在linux文件系统路径中
~代表用户目录
如：`~/`就是`/home/make/`

15. 
   wget是一个在控制台可以从各个协议上下载东西的工具
   如这条命令
   `wget https://mirrors.tuna.tsinghua.edu.cn/apache/hadoop/common/hadoop-2.7.4/hadoop-2.7.4.tar.gz`
   直接在控制台执行，就可以从镜像网站下载hadoop压缩包

16：
看下在环境变量配置这里
`export PATH=$PATH:$HADOOP_HOME/sbin:$HADOOP_HOME/bin`
这种配法有什么用？

17. 
   更新`yum`源  
   有时候自带的`yum`版本落后了，下载的软件都是老版本的，这时候就要考虑更换下yum的版本了 
   首先备份旧的yum源`/etc/yum.repos.d/CentOS-Base.repo`
   然后在/etc/yum.repos.d/目录下用wget命令下载镜像的repo文件，确保下载下来的文件重命名  为：`CentOS-Base.repo`  
    `yum clean all`  
    `yum makecache`
   运行以上两个命令生成yum的缓存  

18. 
   为wget设置代理，其实很简单
   修改其配置文件` vi /etc/wgetrc  ` 
   里面有教你如何设置代理，将其设置为有运行ss软件的机子的ip地址，端口设置为ss的端口即可。
    当然ss要开：允许局域网连接，并且要设置代理的机子和开ss的机子在同一个网段上
19. centos添加用户
    首先登录 root 账号
    执行： `useradd 用户名`命令创建一个新用户
    执行`passwd username` 为新用户设置新密码并激活
    centos删除用户 ：`userdel -rf grid` 删除用户的所有信息.
    不加参数的仅仅只是删除用户，用户的信息没有被删除。  

20. 切换用户登录，centos：login -f username 
   注：1. 加f参数不用输入密码  

21. 在ssh客户端切换登录会退出哦

22. 查看用户所在的组`groups username` 一般说来

23. 添加组 `groupadd name`

24. 将某文件或者文件夹的

25. 归属到某一个组中
   `chown groupname /var/run/httpd.pid`  将/var/run/httpd.pid此文件的所有权归属到groupname这个组中

   -R可以递归整个目录的归属权

### 文件描述符
含义：在linux中，文件描述符是linux为了高效管理已打开的文件而创建的索引，其值是一个非负整数，用于指代被打开的文件。  
虽说系统有多少内存就可以打开多少个文件，但是实际运行最大文件打开数是系统内存的10%，此外，系统还会为单一线程能打开的文件数做限制，（用户级限制），查看该用户级限制命令：`ulimit -a`  PS：`-aH`  是查看硬的限制  
怎么增加系统的文件描述符
执行`vi /etc/security/limits.conf `
输入内容

	* soft nofile 65536

	* hard nofile 131072

	* soft nproc 2048

	* hard nproc 4096

解释：* 代表所有用户  
soft或者hard代表为其限制的是硬配置还是软配置（软配置超过警告，硬配置超过会fail）
nofile(可打开的文件描述符的最大数)  
nproc(单个用户可用的最大进程数量)


​            
### sysctl命令
这个命令可以修改内核的运行参数
参数 -a 可以查看所有可读的变量
参数 key  可以查看这个key的值  eg:`sysctl vm.max_map_count`。 
参数  key=value  可以为运行参数设置值

如果想永久设置值的话，可以编辑`/etc/sysctl.conf的vm.max_map_count`

然后在文件的末尾追加`key=value` 保存，重启机器，就可以看到改变了

### 磁盘命令相关

`du -h --max-depth=1 /`

可以查看根目录下所有一级文件夹的内存占有大小

`df -h`

可以查看各个文件系统的空间占用容量大小

### Linux线程

技能1： 查看端口占用的线程

### 安装JDK

作为一名程序员，如果连linux的jdk都不会安装，那也太out了,接下来我们来学怎么安装jdk

前提：linux不能含有open jdk ，如果已经安装了一个open jdk,请先卸载

卸载open jdk 步骤

1. 先查找下你本机有没有安装linux，使用这个命令`rpm -qa | grep  java`

   > 详解命令：
   > rpm -qa 是列出所有安装过的包 
   > `|`是前面命令的执行结果当做后面命令的输入，也就是列出所有安装过的包。
   > ​     并作为grep命令的标准输入
   > grep 是一个文本搜索工具，一般来说，后面带两个字符串 第一个字符串是要搜索的关键字
   > 第二个字符串是要搜索的文档 ，由于我们要搜索的文档已经用`|`连接起来了。
   > 所以就不用再带第二个字符串了

2. 找到open jdk相关的包的名字，然后执行 `rpm -e --nodeps 包名` 就可以将open jdk 卸载了

   > 详解命令 rpm -e 就是删除包了  `nodeps` 参数是指忽略依赖，强制卸载

   其他待定，因为我删除后jdk又神奇的恢复到oracle jdk了

   所以这个坑，先挖着

   填坑吧，因为我手贱把VM的磁盘文件全删了

   怎么安装oracle jdk？

   1. 不用我说了吧，去官网下载包，丢到linux服务器中
   2. 执行这个命令`rpm -iv jdk-8u161-linux-x64.rpm `
   3. 接下来就是等待安装完成了
   4. ​

   ### 怎么恢复启动方式为命令行？
   很简单，就为`/lib/systemd/system/multi-user.target `
   创建一个快捷方式引用到`/etc/systemd/system/default.target`
   这种快捷方式在linux被称为软连接（你们名词就不能统一一下吗？）
   为一个文件创建软连接，主要是通过ln命令来实现的

   ​

