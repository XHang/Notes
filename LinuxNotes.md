

# linux的技能

## 第一章 :常見的Linux命令和知识

1. 查看日期`date`

2. 查看日历`cal`

   可携带的参数` cal [month] [year]`

   查看整年的日历情况`cal 2005`

3. 自带计算器`bc`

   进入计算后可以用`scale=number `这个命令使得出的结构保留number位小数点

   `quit`退出程序

4. `ctrl+d`代表Linux系统下键盘输入结束,切记切记

5. `[shift]+{[PageUP]|[Page Down]}` 可以在当前命令行翻页

6. `ctrl+alt+{[F1][F2][F3][F4][]F5}{F6}`可以切换到几个控制台下面

7. `who`可以查看当前有谁登录你的机子

8. `shutdow`可以关机

9. `uname -r`k可以查看linux内核的基本信息

10. `echo $xxx` 可以打印出xxx这个变量出来,$代表后面的字符串是变量

### 1.1 linux 的命令帮助

   ​	一般来说,拿到一个命令后,想知道这个命令有什么选项

   ​	可以在命令后面跟上 `--help`

   	取得该命令的说明

   ​	如果想知道这个命令详细手册

   	可以键入`man 命令` 来查看这个命令的手册,不过很长就是了

> 如果man提示说没有这个实体，确保命令没敲错的情况下，尝试执行`yum install -y man-pages`
>
> 安装man手册

   ​	特别注意的是,man这个命令执行后在左上角会出现`man 数字`

   ​	不同的数字代表不同的含义

   	具体的话

| 代号   | 代表内容                                     |
| ---- | ---------------------------------------- |
| 1    | 使用者在shell环境中可以操作的指令或可执行档                 |
| 2    | 系统核心可呼叫的函数与工具等                           |
| 3    | 一些常用的函数(function)与函式库(library)，大部分为C的函式库(libc) |
| 4    | 装置档案的说明，通常在/dev下的档案                      |
| 5    | 设定档或者是某些档案的格式                            |
| 6    | 游戏(games)                                |
| 7    | 惯例与协定等，例如Linux档案系统、网路协定、ASCII code等等的说明  |
| 8    | 系统管理员可用的管理指令                             |
| 9    | 跟kernel有关的文件                             |

   就是这样了

   > 1,3,5 代表的含义还请记住的说

   man命令的话,大致分为几部分

| 代号          | 内容说明                             |
| ----------- | -------------------------------- |
| NAME        | 简短的指令、资料名称说明                     |
| SYNOPSIS    | 简短的指令下达语法(syntax)简介              |
| DESCRIPTION | 较为完整的说明，这部分最好仔细看看！               |
| OPTIONS     | 针对SYNOPSIS 部分中，有列举的所有可用的选项说明     |
| COMMANDS    | 当这个程式(软体)在执行的时候，可以在此程式(软体)中下达的指令 |
| FILES       | 这个程式或资料所使用或参考或连结到的某些档案           |
| SEE ALSO    | 可以参考的，跟这个指令或资料有相关的其他说明！          |
| EXAMPLE     | 一些可以参考的范例                        |

   还有的话,在man里面键入`/`然后输入字符串,可以检索字符串

## 第二章：linux文件的档案

### 1.1 档案权限

首先，档案权限是什么鬼东西

所谓档案，目前先理解为文件，那么，这章主要讲的就是linux系统下的文件权限

要想将明白这一点，先理解一下文件属性中的三种权限

1. 文件的拥有者：就是文件的主人，一般情况下，拥有这个文件的所有权，可以查看，修改文件内容

   其他不能修改，甚至也不能看这个文件的内容

2. 文件的所属的群组：群组这种概念，其实就是一堆用户组成的一个小团体，被称为群组

   如果设置文件的权限为群组。那么群组内部的每一个人都可以拥有这个文件的所有权，包括

   查看，删除等等

   > PS 一个用户可以加入多个群组

3. 其他人的权限,就是不属于以上两种用户的其他用户。

4. Root用户的权限。。。开玩笑，Root用户还需要什么权限，所有文件在他眼里都是透明的   

#### 1.1.1记录用户和群主的档案

       	1. 记录所有系统账号和一般账号以及root账号的信息`/etc/passwd`
     	2. 记录个人的密码`/etc/shadow`
     	3. 记录群组的相关信息`/etc/group`

#### 1.1.2 查看文件所属的权限

一般来说，使用`ls -l`就可以查看当前目录下所有文件夹和文件的所有权信息

出来的信息大概是酱紫的

```
drwxr-xr-x.  5 root root   48 2月  12 15:32 ..
-rw-------.  1 cxh  cxh   703 3月   1 17:43 .bash_history
-rw-r--r--.  1 cxh  cxh    18 8月   3 2016 .bash_logout
-rw-r--r--.  1 cxh  cxh   193 8月   3 2016 .bash_profile
-rw-r--r--.  1 cxh  cxh   231 8月   3 2016 .bashrc
```

要着重理解下前几列的意思

第一列标识文件的类型

1. `d`代表是目录
2. `-`代表是文件
3. ` l`代表连接档
4. ` b` 代表随机性存取设置
5. `c `代表一次性读取设备，如`键盘  鼠标`

接下来的每三列中，都分别代表文件拥有者，群组，其他人对这个文件的权限

r代表可读

w代表可写

x代表可执行

-代表没有权限

> 特别注意的是，如果某个文件夹的某组权限只有`r`权限，那么他将不能进入该文件夹。虽然它的权限是可读
>
> 但是进入文件夹是需要可执行权限的

第二栏的一列数字表示有多少档名连结到此节点(i-node)：

第三栏表示这个档案(或目录)的『拥有者帐号』

第四栏表示这个档案的所属群组

第五栏为这个档案的容量大小，预设单位为bytes；`-h`参数可以调整单位

第六栏为这个档案的建档日期或者是最近的修改日期

最后一档就是文件或者文件夹名，前面加一点表示该文件或者文件夹是隐藏的

#### 1.1.3 更改权限相关的命令

- `chgrp` ：改变档案所属群组

  群组大全可在`/etc/group`查阅，如果输入了不存在的群组名，报错没商量

- `chown` ：改变档案拥有者,`/etc/passwd`可查阅所有账号

- `chmod` ：改变档案的权限, SUID, SGID, SBIT等等的特性

  基本命令格式是 `chmod xxx 路径`

  xxx既可以是数字，也可以是字母

  如果你想写数字的话，记住，有权限代表1，无权限代表0

  所以如果你想修改xx.mp4的权限为rw-r--r--

  也就是文件使用者可读可写不可执行，其他人只有可读权限

  有权限代表1，无权限代表0，所以上面的权限字符也可以写成110100100

  把它变成八进制，就是你想要chmod的那组数字了

  以上

#### 1.1.4 权限对于文件的意义

可读可写可执行应该都懂的

但是有一点需要注意，可写并不代表有删除该文件夹的权利

以上

#### 1.1.5 权限对于文件夹的意义

`r`可读代表可以查阅该文件夹下面所有的子文件夹和文件，也就是说，你对这个文件夹有ls的权限

`w` 可写代表你可以对文件夹下面的文件或者子文件夹进行添加，删除，重命名，移动的权限等等

`x`文件夹不能被执行，所以执行权限其实指的是你有进入该文件夹的权利。






 ###  1.2 复制

复制的命令简而言之就是`cp` 但是要注意的是，复制后的文件的使用者和群组是执行复制命令的用户

一般来说，复制完的文件都要修改下权限

当然可以使用`-a`参数复制整个文件的属性



### 1.3 Linux文件扩展名

基本上,linux是没有所谓的扩展名的.

一个文件能不能被执行,最终要看权限有没有`x`有的话就是可执行的.

但是话说回来,一个存文本文件的权限有`x`难道这个纯文本就可以执行吗?

当然不行,一个文件要想被执行,除了权限必须有`x`外,它的内容也必须能够被执行

不过虽然linux没有扩展名,但是我们还是可以藉由文件扩展名来了解这个文件是什么类型.

虽然这个文件扩展名仅仅只是用于方便罢了

### 1.4 linux的文件架构

linux文件架构指的是linux系统都有哪些文件夹,代表的含义是什么,用于干什么的.

这个架构一般遵循的FHS规范.规范了Linux下面有这些文件夹

|                | 可分享的(shareable)       | 不可分享的(unshareable) |
| -------------- | --------------------- | ------------------ |
| 不变的(static)    | /usr (软件放置处)          | /etc (配置文件)        |
|                | /opt (第三方协力软件)        | /boot (开机与核心档)     |
| 可变动的(variable) | /var/mail (使用者邮件信箱)   | /var/run (程序相关)    |
|                | /var/spool/news (新闻组) | /var/lock (程序相关)   |

可分享的:可以分享给其他系统挂载使用的目录

不可分享的：自己机器上面运作的驱动文件或者是与程序有关的socket文件等.

不变的:数据不经常变动,如函数库,文件说明文件,主机配置文件等等

可变动的:经常改变的数据，例如登录文件、一般用户可自行收受的新闻组等。

其中

- / (root, 根目录)：与开机系统有关；

- /usr (unix software resource)：与软件安装/执行有关；

- /var (variable)：与系统运作过程有关

#### 1.4.1 根目录

根目录是linux系统`一个比较重要的目录,不仅所有目录都是从根目录衍生过来的.而且根目录跟

开机/还原/系统修复有关.

建议根目录分区的大小不要太大,太大的话,可能你会放一些数据进去,

并且根目录最好也不要安装什么软件之类的,这样会加大出问题的风险

根目录应该还有这些次目录

`/bin:`放置指令文件的目录,这些执行文件一般能被root账号和一般账号使用.

​	指令文件一般有:cat, chmod, chown, date, mv, mkdir, cp, bash等等常用的指令

`/boot:`开机才会用到的文件,包括Linux核心文件以及开机选单与开机所需配置文件

`/dev :`存放设备文件的文件夹

`/etc:`存放系统主要的配置文件,建议不要放可执行文件在这里面

​	该文件夹下面比较重要的目录有

​	`/etc/init.d/`所有服务的预设启动script都是放在这里的,例如启动或者关闭iptables

​		`/etc/init.d/iptables start』、『/etc/init.d/ iptables stop』`

​	`/etc/xinetd.d/`：这就是所谓的super daemon管理的各项服务的配置文件目录

​	`/etc/X11/`：与X Window有关的各种配置文件都在这里

`home` 我就不说了,你懂的吧,特别的,home目录有两个代号    

​         `~ ：`代表目前这个用户的家目录，而`~dmtsai：`则代表dmtsai的家目录

`/lib`放置的是在开机时会用到的函式库，以及在/bin或/sbin底下的指令会呼叫的函式库。

`/media` 可移除的装置,例如软盘、光盘、DVD等等装置都暂时挂载于此

`/mnt`想暂时挂载一些额外的装置,可以放在这里

`/sbin` 为开机过程中所需要的，里面包括了开机、修复、还原系统所需要的指令

`/lost+found` 使用标准的ext2/ext3文件系统格式才会产生的一个目录

​	目的在于当文件系统发生错误时， 将一些遗失的片段放置到这个目录下

`/proc` 这个目录本身是一个『虚拟文件系统(virtual filesystem)』他放置的数据都是在内存当中

​	不占任何硬盘空间

`/sys` 也是一个虚拟的文件系统，主要是记录与核心相关的信息

> 注意,开机过程只有根目录会被挂载,因此,任何与开机有关的目录,都必须和跟目录放在同一个分区里面
>
> 有哪些跟开机相关的目录呢?
>
> - /etc：配置文件
> - /bin：重要执行档
> - /dev：所需要的装置文件
> - /lib：执行档所需的函式库与核心所需的模块
> - /sbin：重要的系统执行文件



### 1.5 linux的目录

特别提醒:每一个目录下面都会存在两个特殊的目录,分别是`.`和`..`分别代表此层和上一层的意思

​	特别的,根目录也存在这两个特殊的目录,但是他们的其实指的都是同一个目录-根目录

### 1.6 关于文件夹和文件其他相关的命令

1. 查看所给的路径的最后档名

   `basename /etc/sysconfig/network `

   结果显示的是`network`

2. 查看目录名称

   `dirname /etc/sysconfig/network `

   结果显示的是`/etc/sysconfig `

   感觉有点白痴,但实际上,当完整档名非常长的时候,它还是有用武之地​

3. 查找文件

   通过find来查找文件，可以指定的参数如下

   -name {fileName}以文件名查找

   `/`  用法`find  / -name nginx.pid`  就是在根目录及子文件夹下面查找一个文件名为`nginx.pid`的文件

   

# 第三章：linun软件相关

## 3.1：ununtu 软件相关

`apt install 软件名`   可以安装置顶软件到

`apt-cache search keyword` 通过关键字搜索软件

`apt-get remove softname1` 通过软件名卸载软件

`dpkg -l` 可以查看安装的软件列表    
`apt-get remove --purge` 名字    可以删除软件   



## 3.2：centos 软件相关





## 3.3:搭建SSR梯子

参考

`https://www.jianshu.com/p/cc8e3bf2ca08`

1. 第一步：下载安装包

`wget --no-check-certificate https://raw.githubusercontent.com/Ellean/ShadowsocksRR_Auto_Installer/master/ShadowsocksRR.sh`

2. 第二步·：修改权限`chmod +x ShadowsocksRR.sh`

3. 开始安装SSR ，并把安装日志保存到文件中  `./ShadowsocksRR.sh 2>&1 | tee shadowsocksR.log`

4. 进行安装操作，包括选择端口，加密协议，密码等等

5. 安装完毕，开始下载SSR客户端享受吧

6. 最后列出几个服务端可能用到的命令

   | 功能 | 命令                            |
   | ---- | ------------------------------- |
   | 启动 | /etc/init.d/shadowsocks start   |
   | 停止 | /etc/init.d/shadowsocks stop    |
   | 重启 | /etc/init.d/shadowsocks restart |
   | 状态 | /etc/init.d/shadowsocks status  |
   | 卸载 | ./shadowsocksR.sh uninstall     |

7. 出了点问题

   ```
     File "/usr/local/shadowsocks/server.py", line 221, in <module>
       main()
     File "/usr/local/shadowsocks/server.py", line 39, in main
       config = shell.get_config(False)
     File "/usr/local/shadowsocks/../shadowsocks/shell.py", line 169, in get_config
       with open(config_path, 'rb') as f:
   IOError: [Errno 2] No such file or directory: '-d'
   
   ```

看错误大致能理解，不知为何，SSR服务器拿到的配置文件路径是`-d`

于是乎，把源码文件给改了，写死了配置文件路径

就在文件`/usr/local/shadowsocks/../shadowsocks/shell.py`的169行

## 3.4 SSH 连接工具

openssh安装后如何启动？  
命令：/etc/init.d/ssh start  必须在su权限下运行



如何免密登录 

情景介绍，假设你现在两台服务器，其中一条想通过无密码，ssh远程连接到另外一条服务器

你需要按照以下步骤

1. 通过一个安装了ssh的机器，执行以下命令`ssh-keygen -t dsa`

   > 使用dsa的密钥类型生成一个公钥和私钥。

   执行过程一路Next，中级要你输入密码你可不要输入了，毕竟我们需要免密登录

2. 将公钥的内容复制到需要免密登录的服务器上的，具体路径是`~/.ssh/authorized_keys`

   如果上面没有这个路径，你需要自己建一个，注意`.ssh`和`authorized_keys`文件当前用户要能执行，

   其他用户最多只有执行权限，所以这两个文件都要修改权限为`600`

3. 把生成的秘钥，复制在客户端，然后ssh登录（没试过）

   其实我只是想记录取得秘钥的方法而已。。

   

   

   




## 3.5 VIM编辑器

1. 用vi  文件路径  即可打开一个文本文件  
2. 初次进入是以命令行模式打开的，要编辑此文件，敲入i即可进入编辑模式。  
3.  编辑模式下按esc回退到命令行模式  

4.  将光标移动到某处，按dd即可删除该行  

5. 「x」：每按一次，删除光标所在位置的“后面”一个字符。  

6.  「#x」：例如，「6x」表示删除光标所在位置的“后面”6个字符。  

7.  「X」：大写的X，每按一次，删除光标所在位置的“前面”一个字符。  

8.  「#X」：例如，「20X」表示删除光标所在位置的“前面”20个字符。  

9.  按下：号    输入wq!强制保存并退出。。    



## 3.6 搭建SS梯子

```
wget –no-check-certificate -O shadowsocks.sh https://raw.githubusercontent.com/teddysun/shadowsocks_install/master/shadowsocks.sh
```



```
chmod +x shadowsocks.sh

./shadowsocks.sh 2>&1 | tee shadowsocks.log
```

以上命令依次执行即可



## 3.8 安装BBR  加速用

 首先下载BBR.sh

然后运行。。。没了

但是，如果的系统内核低于4.22。要升级下系统内核

可以用`uname -a`  查看系统内核版本

升级步骤

1. `yum update -y`

2. ```
   rpm --import https://www.elrepo.org/RPM-GPG-KEY-elrepo.org
   rpm -Uvh http://www.elrepo.org/elrepo-release-7.0-2.el7.elrepo.noarch.rpm
   yum --enablerepo=elrepo-kernel install kernel-ml
   ```

3. 查看你的已安装的内核

   ```
   rpm -qa | grep kernel
   ```

   如果看到有比较新的版本内核，那就是成功

4. 然后嘛

   ```
   egrep ^menuentry /etc/grub2.cfg | cut -f {index} -d \'
   grub2-set-default {index} 
   ```

   `{index} `是执行`rpm -qa | grep kernel`命令后，从上往下数，从0开始。启动第几个内核

5. 重启

6. 重启完毕继续用`uname -a`  查看系统内核版本





# 第四章：Linux周边相关

## 4.1 远程传输知识

1. 利用putt传文件到远程服务器  

putt客户端下载下来一般都有那个pscp.exe文件。进入该文件对应的文件夹，敲入cmd命令  
`pscp 发送的源文件   服务器用户名@服务器地址:home`
 敲入后输入密码，即可发送！  

`eg:pscp jdk-8u131-linux-x64.rpm cxh@192.168.21.248:/home`  

注意：有时候发送过去但是找不到文件或者发送时提示`permission denied ` 
就是访问被拒绝了，这时候你得手动更改远程服务器的文件夹为可读可写  chmod 777 xxxx  
注：pscp -r 后面指定文件夹名可以远程传输文件夹  

# 第五章： Linux网络相关

## 5.1：Ubuntu的防火墙设置

开启防火墙并设置为开机启动

`ufw enable`

## 5.2：初次网络配置

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

## 5.3关闭防火墙

`systemctl stop firewalld.service`



## 5.4 设置网络的DNS

以centso为例

为了设置网络的DNS,你需要编辑一个配置文件

文件的位置在

`/etc/resolv.conf`

修改后的文件内容大致如下

```

nameserver 2001:19f0:300:1704::6
nameserver 8.8.8.8 #google域名服务器
nameserver 8.8.4.4 #google域名服务器
egrep ^menuentry /etc/grub2.cfg | cut -f 11 -d \'
```









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
3. 解压命令
    `tar -xzvf file.tar.gz`  
    
4. 设置linux的环境变量  
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
    ​
    ​

10. 获取java安装目录  
    利用`which java`得到路径1  which java是打印出java命令文件的路径    
    `ls -lrt+路径1`得到 箭头后的路径2  
    `ls -lrt+路径2`就ok了  
    默认情况下，用rpm安装后的java在`/usr/java/jdk1.8.0_144/`  
    ​

11.   centos查看网络端口占用:
       ` firewall-cmd --zone=public --list-ports`  
       开启或者关闭firewalld（centos7的防火墙）:
     `systemctl start firewalld`and`systemctl stop firewalld`    
       永远禁用centos防火墙:
       `systemctl disable firewalld.service `  
     
     开放端口给其他机器访问
     
     `firewall-cmd --zone=public --add-port=80/tcp --permanent  `
     
     最后使用
     
     `firewall-cmd --reload`  
     
     立即生效

12.   centos修改主机名:`hostnamectl set-hostname 主机名`
       `hostnamectl --static` 可以查看主机名

13.
​       `centos`关机命令
​       `reboot`  重启
​       `poweroff` 立刻关机
​       

14.
​       在linux文件系统路径中
​       ~代表用户目录
​       如：`~/`就是`/home/make/`

15.     wget是一个在控制台可以从各个协议上下载东西的工具
如这条命令
`wget https://mirrors.tuna.tsinghua.edu.com/apache/hadoop/common/hadoop-2.7.4/hadoop-2.7.4.tar.gz`
 直接在控制台执行，就可以从镜像网站下载hadoop压缩包
      
16. ： 看下在环境变量配置这里
      `export PATH=$PATH:$HADOOP_HOME/sbin:$HADOOP_HOME/bin`
      这种配法有什么用？

2. 
      更新`yum`源  
      有时候自带的`yum`版本落后了，下载的软件都是老版本的，这时候就要考虑更换下yum的版本了 
      首先备份旧的yum源`/etc/yum.repos.d/CentOS-Base.repo`
      然后在/etc/yum.repos.d/目录下用wget命令下载镜像的repo文件，确保下载下来的文件重命名  为：`CentOS-Base.repo`  
    `yum clean all`  
    `yum makecache`
      运行以上两个命令生成yum的缓存  
      ​
3. 
      为wget设置代理，其实很简单
      修改其配置文件` vi /etc/wgetrc  ` 
      里面有教你如何设置代理，将其设置为有运行ss软件的机子的ip地址，端口设置为ss的端口即可。
    当然ss要开：允许局域网连接，并且要设置代理的机子和开ss的机子在同一个网段上
4. centos添加用户
    首先登录 root 账号
    执行： `useradd 用户名`命令创建一个新用户
    执行`passwd username` 为新用户设置新密码并激活
    centos删除用户 ：`userdel -rf grid` 删除用户的所有信息.
    不加参数的仅仅只是删除用户，用户的信息没有被删除。  
    ​
5. 切换用户登录，centos：login -f username 
      注：1. 加f参数不用输入密码  
      ​
6. 在ssh客户端切换登录会退出哦
      ​
7. 查看用户所在的组`groups username` 一般说来
      ​
8. 添加组 `groupadd name`
      ​
9. 将某文件或者文件夹的
      ​
10. 归属到某一个组中
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
```
    * soft nofile 65536
    * hard nofile 131072
    * soft nproc 2048
    * hard nproc 4096
```
解释：* 代表所有用户  
soft或者hard代表为其限制的是硬配置还是软配置（软配置超过警告，硬配置超过会fail）
nofile(可打开的文件描述符的最大数)  
nproc(单个用户可用的最大进程数量)
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

> vm.max_map_count 这个是虚拟neoclassical

## Linux线程


技能1： 查看端口占用的线程

命令`lsof -i:端口`

技能2：如何查看进程pid的执行目录。

命令`ll /proc/pid`

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
IPADDR=xxx.xxx.xxx
NETMASK=255.255.255.0 
GATEWAY=xxx.xxx.xxx
```

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
vi /etc/proifle 在其末尾添加这几句 

``` 
export JAVA_HOME="xxx" export PATH="PATH:PATH:JAVA_HOME/bin" export JRE_HOME="JAVA_HOME/jre" export CLASSPATH=".:JAVA
HOME/jre"exportCLASSPATH=".:JAVA_HOME/lib:$JRE_HOME/lib"
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

11.   centos查看网络端口占用:
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
      `wget https://mirrors.tuna.tsinghua.edu.com/apache/hadoop/common/hadoop-2.7.4/hadoop-2.7.4.tar.gz`
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

3. centos查看网络端口占用:
    ` firewall-cmd --zone=public --list-ports`  
    开启或者关闭firewalld（centos7的防火墙）:
      `systemctl start firewalld`and`systemctl stop firewalld`    
    永远禁用centos防火墙:
    `systemctl disable firewalld.service `  
    
4. 
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
      `wget https://mirrors.tuna.tsinghua.edu.com/apache/hadoop/common/hadoop-2.7.4/hadoop-2.7.4.tar.gz`
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
   >
   >  > 顺带一提，本次实验的weblogic的安装目录是`/home/cxh/Oracle/Middleware/Oracle_Home/wlserver/common/bin`

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

### 如何删除weblogic上面的域

步骤

1. 停止与域相关的进程和服务器

2. 找到这个文件`${Oracle_HOME}/domain-registry.xml`

   删除相关域的配置

   如这段配置：

   ```
   <domain location="/home/weblogic/Oracle/Middleware/Oracle_Home/user_projects/domains/base_domain"/>
   ```

   删除这段配置表示你准备删除名为`base_domain`的域

3. 找到这个文件`$WLS_HOME/common/nodemanager/nodemanager.domains`

   删除这段配置

   ```
   base_domain=/home/weblogic/Oracle/Middleware/Oracle_Home/user_projects/domains/base_domain
   ```

4. 删除域相关的应用程序和文件夹

   eg：`$MW_HOME/user_projects/applications/testDomain`  

   ​	and  `$MW_HOME/user_projects/domains/testDomain`

5. OK ,删除完毕！

## linux 使用影梭
影梭客户端sslocal，自行搜索下载配置
http链接转socket连接 polipo 自行搜索配置
两个运行即可OK

# 第四章：命令集锦

## 4.1 文本类命令

1. more命令

   该命令类似于cat，cat命令是一次性把文件全部打印在控制台上.

   more命令可以分页显示，支持搜索之类什么鬼的

   举例 `more -100 +200 ./log.log`  就是从200行开始，分页显示log.log文件，每页显示100行



## 4.2 网络通信相关

1. curl 命令

   举例`curl -H 'Content-Type: application/x-ndjson' -XPOST 'url' --data-binary @accounts.json`

   其中-H 参数是指定请求的`Content-Type`

   `--data-binary`自然就是指定传输的文件了，没错，这个请求要传一个二进制文件过去。后面的@带文件路径

## 4.3 系统命令

重启  `reboot`



## 4.4 命令重定向(解释:>&1之类的)

命令重定向，意思就是可以把输入或者输出的命令重定向到其他，如文件，或标准输入输出

在shell中，文件描述符通常有三个

`0` 代表标准输入

`1`代表表示输出

`2`代表标准错误

怎么认识它们？

标准输入就是键盘输入，标准输出就是控制台输出，或者说黑不溜秋的那个台子输出的东西

标准错误也是输出到控制台，不过里面的信息全都是错误。

就这么简单。

看样子还挺简单？

好，那么问题来了

请问`2>&1` 代表什么意思

代表将标准错误输出到标准输出。

就是错误能打印到控制台上

就这么简单

另外：还有一些输出重定向命令

`>>` 把前面命令的输出内容，写入到另一个地方

比如将一个文件的内容追加到另一个文件中，可以使用这个命令

`cat id_dsa.pub >> authorized_keys `

## 4.5 压缩命令

关于linux下tar 命令的使用

这个命令一般是用于解压，以及压缩文件的

常见的命令格式如下`tar -zxvf xx.tar.gz`







其中 `-zxvf`是参数，tar命令支持的参数如下

-c: 建立压缩档案
-x：解压
-t：查看内容
-r：向压缩归档文件末尾追加文件
-u：更新原压缩包中的文件
-z：有gzip属性的  gz   如果你不能确定归档文件是不是gz，建议不要用这个参数，先试一下，再试一下，连试三下
-j：有bz2属性的   bz2
-J ：有xz属性的   xz
-Z：有compress属性的
-v：显示所有过程
-O：将文件解开到标准输出
-f 指定归/解档文件名 后面必须带文件名 










# 第五章：其他知识

## 5.1 添加linux的虚拟内存

1. 首先可以根据这个命令查看当前系统的虚拟内存

   `free -m -h `  得到的结果如下

   ```
                total        used        free      shared  buff/cache   available
   Mem:           2.0G        792M         73M         20M        1.1G        998M
   Swap:          5.9G        776K        5.9G
   ```

   由上文可知，当前的虚拟内存为5.9G,很多了。不用设置了，本章完结。。。开玩笑的

2. 用管理员权限在根目录下面建一个文件夹。

   `mkdir /swap`

3. 创建一个虚拟内存文件

   `dd if=/dev/zero of=/swap/swapfile bs=8k count=512000`

   这个命令的意思就是创建一个名字教程swapfile 的文件，大小为8K*512000字节那么大，大概是4G吧。

   里面全部用null填充

4. 创建虚拟内存

   `mkswap /swap/swapfile  `

5. 启动虚拟内存

   `swapon /swap/swapfile  `

   > 关闭的话，是这个命令`swapoff /swap/swapfile `

6. OK，再用free -m 看下，虚拟内存是不是增加的。。

7. 其实追加的虚拟内存是临时的，貌似重启后就失效了，可以设置永久性的虚拟内存

## 5.2 忘记了Linux ROOT 密码咋办

前提是linux物理机就在你身边

1. 重启
2. 在提示你按下e键时，果断按下e建
3. 找到`Linux16`开头的第一行，在行末尾加`    init=/bin/sh` 
4. 然后按`ctrl+x` 进入单用户模式
5. 接下来执行一系列命令
   1. `mount -o remount,rw /`
   2. `passwd`
   3. 输入密码啦
   4. `touch /.autorelabel`
   5. `exec /sbin/init`
   6. 等待重启
6. 搞定



# 第六章 linux的命令

## 6.1 时间相关命令

1. 将当前时间以Unix时间戳表示  `date +%s`
2. 将Unix时间戳转换为日期时间  `date -d @1361542596` 
3. 指定日期格式的转换  `date -d @1543248000000 +"%Y-%m-%d %H:%M:%S"`
4. 查看当前时间和时区信息  `timedatectl`
5. 设置时区  `timedatectl set-timezone Asia/Shanghai`  
6. `timedatectl list-timezones`  查看所有时区名



## 6.2 线程和进相关命令

1. 给进程发信号 ：`kill -s {信号} {pid}`
2. 



# 第X章:Linux的零碎知识点

## x.1环境变量PATH是

一个比较重要的变量.

在输入一般命令的时候,如果这个命令的文件路径就在PATH环境变量中,那么不需要知道命令文件的所在位置.

直接敲入命令,就可以执行命令.

比如说ls,chmod这些命令.

举一反三,如果有一个命令在/test文件夹.但是你想在其他地方也能执行到这个命令的话.就把/test文件夹路径加在path变量中,怎么加呢?

```
PATH="$PATH":/test
```

另外,如果有两个同名的命令在不同的目录中,而且这些目录都加在了PATH环境变量中,那么执行那个同名命令的话.

先查询到的那个命令会先执行.

因此,如果你写一个坏命令,起名叫ls,然后加在PATH变量的最前面.

那么,你懂得..

> 有两个文件可以设置环境变量，分别是`/etc/profile`  and  `/etc/bash.bashrc`
>
> 经验证，后者可以生效，前者半死不活。不知道什么原因 



## x.2 :ununtu 添加开机启动项目

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



## x.4 改系统语言

`export LC_ALL=en_US.utf8`

学点英语也不错



















 
