# Nginx服务器的安装和使用

## 一：what this ？

Nginx是一个高效的HTTP和反向代理服务器，也可用做负载均衡，HTTP缓存等服务器

## 二：为什么要学习它？

1. 流行，很多公司在用
2. 提供了一些方便的功能，值得学习

## 三:怎么下载安装

本教程使用的系统版本是centos7:请核对你的系统版本再往下观看

Nginx官网提供的安装教程并不是要你去官网下载二进制包或者源码包自己安装

> 不过这种方式也是行的，只不过不知道怎么安装罢了

而是要你使用yun install nginx 命令自动安装。

当然，单纯的使用这个命令进行安装肯定是挂了，因为yun仓库并没有nginx的安装包可以使用

这个时候，就需要我们配置一个新的yum源了。

步骤如下

1. 创建这个文件`/etc/yum.repos.d/nginx.repo`

2. 补充一下内容到新建的文件中

   ```
   [nginx]
   name=nginx repo
   baseurl=http://nginx.org/packages/OS/OSRELEASE/$basearch/
   gpgcheck=0
   enabled=1
   ```

   > 其中，OS根据操作系统的不同而不同，如果你的系统是centos，OS应该被替换成centos。
   >
   > 以及OSRELEASE，假设你的centos的版本是7，那么它应该被替换成7.
   >
   > 具体请看官网的说明





