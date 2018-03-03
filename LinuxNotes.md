

# linux的技能

## ununtu 添加开机启动项目

几种方式

1. 编辑` /etc/rc.local `文本，把启动项目的脚本添加到里面，当然要在exit之前

2. 编写自己的脚本文件，并通过命令添加到开机启动项里面

   1. 创建一个脚本文件，后缀名为`sh`,假设为`run_server.sh`

      > 别忘了改权限

   2. 将脚本文件拷贝到`/etc/init.d` 文件夹里面，当然，链接过去也行

      > 文件后缀名去掉吧。。。

   3. 然后执行这个命令`sudo update-rc.d run_server defaults 90`

      其中90这个数字越大，启动的顺序就越晚

   4. OK ，顺手敲一个poweroff 

      > 诶诶诶诶，我的电脑怎么关机了？

      ​

## ununtu 软件相关

`apt install 软件名`   可以安装置顶软件到

`apt-cache search keyword` 通过关键字搜索软件

`apt-get remove softname1` 通过软件名卸载软件

`dpkg -l` 可以查看安装的软件列表    
`apt-get remove --purge` 名字    可以删除软件   



## centos 软件相关



## 常见知识点
​
1. openssh安装后如何启动？  
  命令：/etc/init.d/ssh start  必须在su权限下运行

## VIM编辑器怎么入坑  

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


## 远程传输知识

1. 利用putt传文件到远程服务器  

putt客户端下载下来一般都有那个pscp.exe文件。进入该文件对应的文件夹，敲入cmd命令  
`pscp 发送的源文件   服务器用户名@服务器地址:home`
 敲入后输入密码，即可发送！  

`eg:pscp jdk-8u131-linux-x64.rpm cxh@192.168.21.248:/home`  

注意：有时候发送过去但是找不到文件或者发送时提示`permission denied ` 
就是访问被拒绝了，这时候你得手动更改远程服务器的文件夹为可读可写  chmod 777 xxxx  
注：pscp -r 后面指定文件夹名可以远程传输文件夹  
​

## linux网络相关知识
### Ubuntu的防火墙设置

开启防火墙并设置为开机启动

`ufw enable`

### 初次网络配置
当你第一次安装完centos7系统后，并不能直接使用网络，这是因为，默认情况下，新安装的系统没有在启动系统的时候也启动网络
​
这时候呢？我们需要改下配置文件
​
首先，你应该用这个命令
​
`nmcli d`
​
查看当前主机中那个网络接口没有启动
​
记下未启动的接口
​
然后编辑文件
​
`vi /etc/sysconfig/network-scripts/ifcfg-网络接口名`
​
文件大致如下
​
```
TYPE=Ethernet
BOOTPROTO=dhcp
DEFROUTE=yes
PEERDNS=yes
PEERROUTES=yes
IPV4_FAILURE_FATAL=no
IPV6INIT=yes
IPV6_AUTOCONF=yes
IPV6_DEFROUTE=yes
IPV6_PEERDNS=yes
IPV6_PEERROUTES=yes
IPV6_FAILURE_FATAL=no
IPV6_ADDR_GEN_MODE=stable-privacy
NAME=enp0s3
UUID=f57d075a-9c72-413e-bb25-646f5cf430cc
DEVICE=enp0s3
ONBOOT=yes
```
​
其中你需要改的是BOOTPROTO这个配置和ONBOOT这个配置
​
BOOTPROTO：值有static和dhcp 分别代表本机ip地址的取得是静态的还是动态的（dhcp）
​
ONBOOT：值有yes和no 分别代表系统启动是是否随之启动网络接口。 这种情况，当然是要选择yes
​
改完后保存。
​
然后键入此命令`systemctl restart network`
​
即可重启网络
​
完毕

centos版：
`yum list installed`
`yum remove 软件名`
​
mv [选项] 源文件或目录 目标文件或目录  
2. 删除文件夹命令
​
`rm -rf  文件夹路径 ` 
​
1. 解压命令
  `tar -xzvf file.tar.gz`  
​
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
​
9. pwd命令    
  `pwd`命令可以查看当前所在的路径（centos）  
​
​
10. 获取java安装目录  
  利用`which java`得到路径1  which java是打印出java命令文件的路径    
  `ls -lrt+路径1`得到 箭头后的路径2  
  `ls -lrt+路径2`就ok了  
  默认情况下，用rpm安装后的java在`/usr/java/jdk1.8.0_144/`  
​
11. 
   centos查看网络端口占用:
   ` firewall-cmd --zone=public --list-ports`  
   开启或者关闭firewalld（centos7的防火墙）:
    `systemctl start firewalld`and`systemctl stop firewalld`    
   永远禁用centos防火墙:
   `systemctl disable firewalld.service `  
​
12. 
   centos修改主机名:`hostnamectl set-hostname 主机名`
   `hostnamectl --static` 可以查看主机名
​
13.
`centos`关机命令
`reboot`  重启
`poweroff` 立刻关机
​
14.
在linux文件系统路径中
~代表用户目录
如：`~/`就是`/home/make/`
​
15. 
   wget是一个在控制台可以从各个协议上下载东西的工具
   如这条命令
   `wget https://mirrors.tuna.tsinghua.edu.cn/apache/hadoop/common/hadoop-2.7.4/hadoop-2.7.4.tar.gz`
   直接在控制台执行，就可以从镜像网站下载hadoop压缩包
​
16：
看下在环境变量配置这里
`export PATH=$PATH:$HADOOP_HOME/sbin:$HADOOP_HOME/bin`
这种配法有什么用？
​
17. 
   更新`yum`源  
   有时候自带的`yum`版本落后了，下载的软件都是老版本的，这时候就要考虑更换下yum的版本了 
   首先备份旧的yum源`/etc/yum.repos.d/CentOS-Base.repo`
   然后在/etc/yum.repos.d/目录下用wget命令下载镜像的repo文件，确保下载下来的文件重命名  为：`CentOS-Base.repo`  
    `yum clean all`  
    `yum makecache`
   运行以上两个命令生成yum的缓存  
​
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
​
20. 切换用户登录，centos：login -f username 
   注：1. 加f参数不用输入密码  
​
21. 在ssh客户端切换登录会退出哦
​
22. 查看用户所在的组`groups username` 一般说来
​
23. 添加组 `groupadd name`
​
24. 将某文件或者文件夹的
​
25. 归属到某一个组中
   `chown groupname /var/run/httpd.pid`  将/var/run/httpd.pid此文件的所有权归属到groupname这个组中
​
   -R可以递归整个目录的归属权
​
## 文件描述符

含义：在linux中，文件描述符是linux为了高效管理已打开的文件而创建的索引，其值是一个非负整数，用于指代被打开的文件。  
虽说系统有多少内存就可以打开多少个文件，但是实际运行最大文件打开数是系统内存的10%，此外，系统还会为单一线程能打开的文件数做限制，（用户级限制），查看该用户级限制命令：`ulimit -a`  PS：`-aH`  是查看硬的限制  
怎么增加系统的文件描述符
执行`vi /etc/security/limits.conf `
输入内容
​
    * soft nofile 65536
​
    * hard nofile 131072
​
    * soft nproc 2048
​
    * hard nproc 4096
​
解释：* 代表所有用户  
soft或者hard代表为其限制的是硬配置还是软配置（软配置超过警告，硬配置超过会fail）
nofile(可打开的文件描述符的最大数)  
nproc(单个用户可用的最大进程数量)
​
​
•            
## sysctl命令

这个命令可以修改内核的运行参数
参数 -a 可以查看所有可读的变量
参数 key  可以查看这个key的值  eg:`sysctl vm.max_map_count`。 
参数  key=value  可以为运行参数设置值
​
如果想永久设置值的话，可以编辑`/etc/sysctl.conf的vm.max_map_count`
​
然后在文件的末尾追加`key=value` 保存，重启机器，就可以看到改变了

## Linux线程

​
技能1： 查看端口占用的线程

命令`lsof -i:端口`

## 怎么恢复启动方式为命令行？

 很简单，就为`/lib/systemd/system/multi-user.target `
 创建一个快捷方式引用到`/etc/systemd/system/default.target`
 这种快捷方式在linux被称为软连接（你们名词就不能统一一下吗？）
 为一个文件创建软连接，主要是通过ln命令来实现的
​
 命令格式
​
 `ln 参数 源文件路径 目标文路径`
​
 其中-s 是为源文件创建软连接，-f是强制执行（慎用）
​
 那么，最后的命令就是
​
 `ln -s /lib/systemd/system/multi-user.target  /etc/systemd/system/default.target` 
​

## 怎么安装centos的桌面
虽然说centos用命令行启动就挺好的，但是也难保有时候你需要用图形化界面来启动linux。
比如说万恶的weblogic，就需要用图形化界面来安装
要想安装centos的桌面，首先你要下载桌面应用程序，目前，linux比较主流的桌面应用程序分为两种
这里只介绍其中一种，那就是：`GNOME Desktop`
执行步奏
1. 执行此命令下载桌面应用程序
   ```
   yum -y groups install "GNOME Desktop" 
   ```
    然后喝杯JAVA冷静下吧，文件估计有800多M
2. 执行以下命令，开启图形化界面。
   ```
   startx 
   ```
   以上   

linux的技能

常见知识点

openssh安装后如何启动？
命令：/etc/init.d/ssh start 必须在su权限下运行

怎么用主机访问VirtualBox呢？ 见图。用PuTTY即可访问

vin编辑器怎么使用？
用vi 文件路径 即可打开一个文本文件
初次进入是以命令行模式打开的，要编辑此文件，敲入i即可进入编辑模式。
编辑模式下按esc回退到命令行模式
将光标移动到某处，按dd即可删除该行
「x」：每按一次，删除光标所在位置的“后面”一个字符。
「#x」：例如，「6x」表示删除光标所在位置的“后面”6个字符。
「X」：大写的X，每按一次，删除光标所在位置的“前面”一个字符。
「#X」：例如，「20X」表示删除光标所在位置的“前面”20个字符。
按下：号
输入wq!强制保存并退出。。

利用putt传文件到远程服务器

putt客户端下载下来一般都有那个pscp.exe文件。进入该文件对应的文件夹，敲入cmd命令
pscp 发送的源文件 服务器用户名@服务器地址:home

敲入后输入密码，即可发送！
eg:pscp jdk-8u131-linux-x64.rpm cxh@192.168.21.248:/home
注意：有时候发送过去但是找不到文件或者发送时提示permission denied 就是访问被拒绝了，这时候你得手动更改远程服务器的文件夹为可读可写 chmod 777 xxxx
注：pscp -r 后面指定文件夹名可以远程传输文件夹

linux网络相关知识

初次网络配置

当你第一次安装完centos7系统后，并不能直接使用网络，这是因为，默认情况下，新安装的系统没有在启动系统的时候也启动网络

这时候呢？我们需要改下配置文件

首先，你应该用这个命令

nmcli d

查看当前主机中那个网络接口没有启动

记下未启动的接口

然后编辑文件

vi /etc/sysconfig/network-scripts/ifcfg-网络接口名

文件大致如下

TYPE=Ethernet
BOOTPROTO=dhcp
DEFROUTE=yes
PEERDNS=yes
PEERROUTES=yes
IPV4_FAILURE_FATAL=no
IPV6INIT=yes
IPV6_AUTOCONF=yes
IPV6_DEFROUTE=yes
IPV6_PEERDNS=yes
IPV6_PEERROUTES=yes
IPV6_FAILURE_FATAL=no
IPV6_ADDR_GEN_MODE=stable-privacy
NAME=enp0s3
UUID=f57d075a-9c72-413e-bb25-646f5cf430cc
DEVICE=enp0s3
ONBOOT=yes
其中你需要改的是BOOTPROTO这个配置和ONBOOT这个配置

BOOTPROTO：值有static和dhcp 分别代表本机ip地址的取得是静态的还是动态的（dhcp）

ONBOOT：值有yes和no 分别代表系统启动是是否随之启动网络接口。 这种情况，当然是要选择yes

改完后保存。

然后键入此命令systemctl restart network

即可重启网络

完毕

linux常用命令

1.安装，删除命令：

Ubuntu版：
dpkg -l 可以查看安装的软件列表
apt-get remove --purge 名字 可以删除软件

centos版： yum list installed yum remove 软件名

mv [选项] 源文件或目录 目标文件或目录

删除文件夹命令
rm -rf 文件夹路径

解压命令 tar -xzvf file.tar.gz

设置linux的环境变量
vi /etc/proifle 在其末尾添加这几句 ``` export JAVA_HOME="xxx" export PATH="PATH:PATH:JAVA_HOME/bin" export JRE_HOME="JAVA_HOME/jre" export CLASSPATH=".:JAVA
​H
​​ OME/jre"exportCLASSPATH=".:JAVA_HOME/lib:$JRE_HOME/lib"

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

   -R可以递归整个目录的归属


## 安装JDK

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
   其他待定，因为我删除后jdk又神奇的恢复到oracle jdk了

   所以这个坑，先挖着

   填坑吧，因为我手贱把VM的磁盘文件全删了

   怎么安装oracle jdk？

   1. 不用我说了吧，去官网下载包，丢到linux服务器中
   2. 执行这个命令`rpm -iv jdk-8u161-linux-x64.rpm `
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


## 磁盘命令相关

`du -h --max-depth=1 /`

可以查看根目录下所有一级文件夹的内存占有大小

`df -h`

可以查看各个文件系统的空间占用容量大小



## linux分区

### 一：什么是分区

linux上的分区就类似与windows上面的C盘，D盘。

一个硬盘这么大，那不可能直接就可以使用对吧。

给它划分几个范围，这就叫分区。

每一个分区都有自己的文件系统，互不影响

### 二：linux上相关的分区命令

1. 查看所有的分区

   `fdisk -l`

2. 删除分区

   假设查看所有分区后，得到以下信息

   ```
   /dev/sda1  
   /dev/sda2        
   /dev/sda3       
   /dev/sda5      
   /dev/sda6      
   /dev/sda7        
   /dev/sda8      
   /dev/sda9 
   ```
   则键入`fdisk  -/dev/sda`

   然后终端会提示你选择命令

   输入命令`d`

   然后会再提示你选择一个分区号

   注意了，划重点了，分区号就是上面的分区名最后的一个数字，千万别选错了，否则你就等着哭吧

   ​

## weblogic的安装和使用

### 安装

   本教程适用oracle weblogic 12.2.1C版本

   第一步：下载到服务器中，并解压出jar文件,然后用java -jar xxxx.jar 运行安装软件

   第二步：安装过程中，如果系统检测一切都正常的话，执行安装。

   不正常的情况下可能有一下几种

   1. weblogic 不支持openjdk。所以如果你的服务器安装的是openjdk，需要卸载并安装一个oracle的jdk

      > 可查看linux的jdk安装章节

   2. weblogic安装时要求系统必须有GUI窗口，所以你需要为你的服务器安装一个桌面

      > 详情请查看笔记（不存在的）

      其实下面的都没什么难度了，一路next即可。所以可以直接跳过这个教程了，再见

      注意一下：要把weblogic的安装目录记下来，不然下面建域的时候你就等着懵吧


   （诶，等等，怎么使用你还没说呢？这么快就想跑？）

   开玩笑的，现在假设我们已经安装好了weblogic了，接下来就是要建域了

### weblogic的建域步骤

   第一步：

   执行该命令：`export CONFIG_JVM_ARGS=-Djava.security.egd=file:/dev/./urandom`作用是

   设置`CONFIG_JVM_ARGS`环境变量，这将减少向导配置花费的世界

   第二步：

   进入该目录：`WLHOME/common/bin` 找到`config.sh`文件，然后执行此命令
   `sh config.sh`
   > 注：WLHOME就是weblogic的安装目录，所以如果你忘记了weblogic的安装目录，就等着哭吧
   > 顺带一提，本次实验的weblogic的安装目录是`/home/cxh/Oracle/Middleware/Oracle_Home/wlserver/common/bin`

   第三步：执行这个命令之后
   linux应该就弹出了一个GUI窗口

这个GUI会指引你一步一步配置域服务器。因为全都是中文，而且配置及其简单，这里就不多说了

只讲几个要点

1. 配置域服务器过程中会有一个选项，即要不要同时配置一个管理服务器。

   如果勾选的话，配置的域会有一个管理服务器，可以用来发布，部署项目。

   还是蛮方便的

2. 配置域服务器的管理服务器的监听地址时，请务必要将监听地址配为外网的IP，而不是本地回环地址

   不然的话，你这个管理服务器无法通过外网访问哦

### weblogic 的域管理服务器启动和停止

#### 启动

进入该文件夹

`{weblogic}/domains/base_domain/bin`

创建一个用于输出的文本文件（这个不用我写出来了吧），起名叫server.out

然后执行该命令

`sh startWebLogic.sh > ./server.out &`

这样的话就可以启动域管理器，并将控制台的输入，指向到server.out这个文件里面

#### 关闭

关闭的话只需要执行bin目录下面的

`sh stopWebLogic.sh`

第一次可能会比较慢

或者你也可以通过杀进程的方式来使weblogic停止

大概就是使用这个命令

`ps-ef | grep weblogic`

找到相关进程的pid 执行kill -9 pid 杀死该进程

更简单的，可以用这个命令

`lsof -i:端口` 输入端口即可看到这个端口被哪个进程占用了，其PID是多少。

`kill -9 pid`即可杀死该进程

## linux 使用影梭
影梭客户端sslocal，自行搜索下载配置
http链接转socket连接 polipo 自行搜索配置
两个运行即可OK










