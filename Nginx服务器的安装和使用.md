# Nginx服务器的安装和使用

## 一：what this ？

Nginx是一个高效的HTTP和反向代理服务器，也可用做负载均衡，HTTP缓存等服务器

## 二：为什么要学习它？

1. 流行，很多公司在用
2. 提供了一些方便的功能，值得学习

**都提供了什么功能**
1. 反向代理（路由+服务发现）
2. 静态资源服务器
3. 负载均衡
4. 熔断机制（健康检查）

> 基本可以和SpringCloud-Netflix打个平手
>
> 不过这货某些加强版功能需要美元，有钱尽管上

**Nginx的组成**
由一个主线程和若干个工作线程组成
主线程用于加载和评估配置,并管理工作线程
工作线程用于处理请求
工作线程的个数可以通过配置文件调整

## 三:怎么下载安装

本教程使用的系统版本是centos7:请核对你的系统版本再往下观看

Nginx官网提供的安装教程并不是要你去官网下载二进制包或者源码包自己安装

> 不过这种方式也是行的，只不过不知道怎么安装罢了

而是要你使用yum install nginx 命令自动安装。

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
   

完成了仓库的配置的话,就可以着手进行安装了
安装的命令就是上面提到的`yum install nginx`

## 四:开始使用

### 3.1 常规操作
**启动**
首先确保你通过以上方式安装了啊
按照后,nginx的路径应该就设置到环境变量path里面了
所以,可以直接敲入命令`nginx`
但是你怎么知道启动起来了呢?
在机器上访问该网站`127.0.0.1` 
看到`Welcome to nginx!`就可以得知启动成功了

> 默认情况下，Nginx启动时会保存主进程的PID到某个文件中
>
> 官网给出的路径是`/usr/local/nginx/logs/nginx.pid`
>
> 但是实际上，测试给出的路径是`/run/nginx.pid`
>
> 当然，Nginx的配置文件也可以更改路径啦
>
> 通过`pid`简单指令

**关闭 **
强制关闭:`nginx -s stop`
正常关闭:`nginx -s quit`

> 谁启动的,就应该由谁关闭

重启加载配置文件:`nginx -s reload `
> 内部的工作流程是酱紫的
主线程接受到此命令,校验配置文件是否有效
如果有效,启动新的主线程,并请求工作线程关闭,工作线程则拒绝接受新的请求,并完成当前的请求后关闭工作线程
如果无效,回滚配置,使用旧的配置

重新打开日志文件`nginx -s reopen `

**使用其他连接方法**

在默认情况下，如果你没指定连接处理方法，那么Nginx会选择一个平台支持的更有效的方法。

但是，如果需要，可以用`use`显式使用连接处理方法。

支持一下处理方法

`select` 

`poll` 

`kqueue`

`epoll` 

> Linux 2.6+ 上支持使用，该连接处理方法比传统的堵塞轮询效率要好上不少。
>
> 主要机制是：使用一个线程，当IO流程写入事件时，唤醒一份读取线程，并将IO流的位置告诉对方。
>
> 从而读取数据

`/dev/poll`

`eventport`

#### 3.1.1 控制Nginx

所谓控制Nginx，就是控制Nginx启动，关闭等行为。启动和关闭上文已经讲了。

下面讲点特殊的。

对了，上文有提到怎么拿Nginx的PID对吧。在那里划重点，这章要考的。

我们将学会用发送信号给Nginx告诉它，我们将如何操作它。

用到的关键命令是KILL

例如`kill -s QUIT 20410`

`-s`后面跟着的，就是信号名

`20410`就是PID啦

主进程接受的信号名有以下几种

`TERM`     `INT`快速关闭

`QUIT`  关闭进程，优雅滴

`HUP`  热加载配置文件

`USR1`重新打开日志文件

> 用法是当你把日志文件的名字给改了的话，Nginx还是会往那个文件写日志
>
> 如果你想让Nginx重新生成一个日志文件并写入日志的话，就可以使用这个命令。

`USR2` 重新用Nginx的可执行文件创建工作进程和主进程，并关闭旧的工作进程和主进程。

> 除非你牛逼到能改Nginx的源码，生成自己的可执行文件，否则这个命令一般是用不上的。。

`WINCH` 关闭**主进程**



工作进程也可以接受一个信息用于控制，它允许接受的信号有

`TERM`   `INT`   杀死这个工作进程

`QUIT`  优雅的使工作进程自杀

`USR1` 重新打开日志文件











### 3.2 配置文件

#### 3.2.0 配置文件位置

首先,配置文件的目录默认位于
`/usr/local/nginx/conf`  
或者
` /usr/local/etc/nginx`
或者
`/etc/nginx`
这么多种路径,总有一个适合你

> 笔者在`/etc/nginx`

然后配置文件的名称叫做`nginx.conf`

#### 3.2.1 配置文件的语法
Nginx的配置文件语法有两种
1. 第一种是key-value形式的
`key value;` 也被叫做简单指令

2. 第二种类似于Json的对象语法,叫做块指令
```
http {
    server {
    }
}
```
其实按我的理解,块指令,就是给简单指令一个房子住的,让这个孤零零的简单指令有了归属,又或者是说,对于处在块指令的简单指令来说.块指令就是他的上下文.

另外,也有简单指令非常孤傲,它们不需要块指令罩着,直接就是孤零零的处在配置文件中,它们也有上下文,上下文就是这个配置文件,被称为主上下文.

#### 3.2.2 http配置

提供http服务器配置的上下文

里面可以定义多个http协议，不同接口的服务器。

一些小型的配置如下

1. include 引入其他http配置。比如说，types文件，这个文件里面是文件后缀和applicationType之间的对应关系，Nginx可以用请求的文件后缀，设置响应头的applicationType
2. default_type  一般请求后响应的默认文件类型
3. sendfile  一个开关项，用于优化服务器性能，可选择on或off
4. keepalive_timeout 后面跟着一个数字 ，主要是设置服务器连接能存活多少时间

#### 3.2.3  server 配置

该配置是http块配置里面的一个子配置。

主要用于定义服务器的，可以在http块里面定义多个server配置，也就是说，nginx可以配置多个服务器在上面

server里面有一些简单配置稍微说下

1. `listen `server的监听端口，默认是`80`，如果你不去配置它的话

2. `server_name`  设置块指令`server`的名字

   > 如果没有配，默认是空串，这样配置的server也用于处理请求头Host的异常情况
   >
   > 配了这个的话，可以匹配Host请求，从而决定由哪个server处理请求
   >
   > 有关更多情况，可以参阅下面的`3.3.1 Nginx如何处理请求`

3. ` error_page   500 502 503 504  /50x.html;`

   当http状态码不是200时，重定向到50x.html

##### 3.2.4 location的配置

location配置位于server配置下面，主要是对请求进行进一步的匹配，以取得资源

示例的配置如下

```
location ~\.(gif|jpg|png)$ {
                root /data/images;
}
```

如上文所述，使用正则表达式对请求的后缀进行匹配，如果后缀是`gif`  `jpg`   `png`

则接受此请求，将root对应值`/data/images` 文件系统里面的资源返回给客户端

> 注：`location ~\.(gif|jpg|png)$` 中的`~`即表示后面是正则表达式，应该使用正则表达式来匹配请求地址
>
> ​	如果是` location = /50x.html`  则是精确匹配

#### 3.2.5 events配置

提供上下文的配置，有些指定了影响连接的配置

里面有的配置是

1. worker_connections    工作线程建立连接的数量





#### 3.2.3 配置文件的一些注意点

1. 如果你是第一次安装NGINX，你可能会在配置文件看到这一段话。。

   `  include /etc/nginx/conf.d/*.conf;`

   意思是，还有其他的配置文件在生效。

   > 当时配页面和图片的显示时，配置一直不生效的原因就是
   >
   > 其他的配置文件覆盖了我们自定义的配置

#### 3.2.3 配置文件的一些度量单位

大小：可以用字节（不带后缀），千字节（`k`或 `K`），兆字节（`m`或`M`）指定

eg：`1024` `8k`  `8m`  

时间，可以用数字和以下后缀来表示时间

| 后缀 | 含义        |
| ---- | ----------- |
| ms   | 毫秒        |
| s    | 秒          |
| m    | 分钟        |
| h    | 小时        |
| d    | 天数        |
| w    | 周          |
| M    | 月（30天）  |
| y    | 年（365天） |


### 3.2 应用
#### 3.2.1 显示静态内容
什么是静态内容,就是html,图片之类的,不需要后端参与的文件内容
这次我们将尝试用Nginx,来显示html文件和一张图片
详细功能描述:
nginx能访问到文件系统路径`/data/www`里面的html文件,并通过一个URL路径,将该文件显示出来.
还能访问文件系统路径`/data/images`里面的图片文件,并通过一个URL路径,将图片显示出来
很简单.我们要做的事情有

1. 创建上文谈到的两个路径,并准备对应的文件放进行
> 只要有基本的linux操作知识就可以做到

2. 更改配置文件
直接把配置文件列出来吧(位于http的上下文中)
```
  server{ 
  # 将请求地址和`localhost`后面的`path` 进行最长匹配,能匹配到就用这个配置
        localhost /{
  # 匹配成功,返回/data/www文件夹里面的东西        
                root /data/wwww;
        }
        localhost /images{
                root /data;
        }
    }
```
> 说一声哈
一个配置文件可以包含多个server.
每个server可以用名称和端口进行区分

3. 让Nginx重新加载配置文件
    `nginx -s reload`

4. 假设我们现在在`/data/www`放了一个`1.html`的文件

  在`/data/images`里面放了一个1.jpg的文件

  然后访问`127.0.0.1/1.html`就可以访问到`1.html`

  访问`127.0.0.1/images/1.jpg`就可以访问到``



#### 3.2.2 作为代理服务器

先了解一下正向代理和反向代理

正向代理：就是代理客户端，去请求一些不存在的网络，比如说Google，YouTube等网站

反向代理：就是代理服务端，帮服务端接收来自客户端的请求，再把请求分发给服务端。

看到了吗，所谓正向和反向，就是相对于客户端而言的。就是个名词，不必太在意

Nginx有作为反向代理服务器的能力，这一章的作用就是教你怎么把Nginx调教成反向代理服务器的能力

具体的功能就是，当你访问80端口时，代理服务器将请求转发给8080端口的服务器。

而8080端口的服务器将根据请求匹配资源，返回静态资源文件。

完成这一段简单的功能只需编写配置文件和准备资源

配置文件如下

```
 server{
        listen 80;
        location / {
                 proxy_pass http://localhost:8080;
        }
        location ~\.(gif|jpg|png) {
                root /data/images;
        }

    }
    server {
        listen 8080;
        root /data/up1;
        location / {
        }
    }
```

> 该配置文件定义两个server
>
> 其中一个端口是8080，访问它的任何资源将被转向到`/data/up1`文件夹
>
> 另外一个端口是`80`，有两个URL适配，
>
> 一个是URL正则表达式匹配(正则表达式匹配URL的地址后缀是图片格式,匹配上就访问了本机文件系统的图片)
>
> 一个是最短URL匹配。匹配上就将请求转发到`http://localhost:8080` 也就是实现了代理服务器的作用

如配置文件所示，你需要建这么一个文件路径`/data/up1`并在里面放置一个html文件



做完以上步骤后，打开浏览器访问`127.0.0.1/1.html`

则Nginx会先将请求发送给80端口上面的服务器，80端口的服务器会把请求转发给8080端口的服务器。

响应消息再依次传递回来，完成请求。

> 还有一个教程设置fastCGI的代理服务器，因为Java版的没太大必要，所以就没弄了

#### 3.2.3 作为负载均衡器

负载均衡，就是将请求分发给各个服务器，避免只有单个服务器处理请求，无法适应并发量，吞吐量的要求

负载均衡可以优化资源利用率，最大化吞吐量，减少延迟。

可以使用nginx作为非常有效的HTTP负载平衡器，将流量分配到多个应用程序服务器，并使用nginx提高Web应用程序的性能，可伸缩性和可靠性。

接下来介绍负载均衡最基本的配置

##### 3.2.1.1 编写负载均衡配置

```
http {
	# 服务器组配置
    upstream myapp1 {
        server srv1.example.com;
        server srv2.example.com;
        server srv3.example.com;
    }

    server {
        listen 80;

        location / {
            proxy_pass http://myapp1;
        }
    }
}
```

> 以上配置中，参与负载均衡的服务器是
>
> srv1.example.com、 srv2.example.com、srv3.example.com
>
> 由于没有配置负载均衡策略，所以默认是循环
>
> 简单的描述下请求的过程：
>
> 当请求匹配上配置上面的server时，会将请求分发给myapp1的服务器上面

##### 3.2.1.2 指定负载均衡策略

Nginx支持一下几种负载均衡策略

1. 循环，每个请求都将循环请求注册在Nginx的服务器（默认）

2. 最少连接，将把新请求分配给最少连接的那个服务器上（least_conn）

3. ip-hash 函数，基于客户端的IP地址，使用哈希函数决定新连接的处理服务器

   （用这个，可以确保某个客户端，一直访问到某个特定的服务端，前提是那个服务端可用）

4. 加权负载平衡

对于第一点，在使用上面的负载均衡配置就行，默认没有指定负载均衡策略的话，使用的就是循环

对于第二点，只需在`upstream` 块指令，里面加上`least_conn;`即可

对于第三点，只需在`upstream` 块指令，里面加上`ip_hash;`即可

> 此举可确保来自同一个客户端的请求，总是传递到同一服务器上

对应第四点，其实就是配置Nginx负载均衡更偏爱哪个server，可以在那个服务器组配置的server后面追加weight={权重}

比如说server srv1.example.com weight=3;

当有5个请求过来时，其中三个会转给srv1服务器上。

另外两个请求由其他服务器均摊

##### 3.2.1.3 健康检查

作为一个有理想的负载均衡器，怎么能不把健康检查（熔断器包含在内呢？）

所谓健康检查，在Nginx的实现时酱紫的

1. 如果和某服务端的失败连接超过次数配置`max_fails `

2. 该服务器在` fail_timeout` 时间内将不可用，新请求将不会再发给它了

3. ` fail_timeout`时间过了，尝试使用来自客户端的请求去请求这个服务端。

   如果这次响应了，服务器可以使用了



#### 3.2.4配置HTTPS服务器

作为一个有追求的反向代理，怎么可能不包含https?

假设我们需要为某个server指令块配置HTTPS访问，而且我们已经拥有了

证书*1（pem格式的）`cert.pem`

密钥文件*1（pem格式的）`privkey.pem`

好了，接下来，就只差配置啦，如下所示

```
server{
        listen 443 ssl;
        ssl_certificate     /etc/letsencrypt/live/www.thisisweb.tk/cert.pem;
        ssl_certificate_key /etc/letsencrypt/live/www.thisisweb.tk/privkey.pem;
        error_log  /var/log/nginx/serverDebug.log debug;
        server_name www.thisisweb.tk;
        ...
}
```

关键之处是listen 有一个ssl属性

以及server指令块需要证书和秘钥文件

其他的`ssl_protocols`和`ssl_ciphers` 可以不用填，会有默认值的

好了，配置完就去浏览器试下吧，知道怎么访问会把请求导向这个server配置吧。

> 但是笔者配置完后，访问https后一直没响应，表现就是一直堵塞着。
>
> 后面发现，是某国网络防火墙的问题，只好搭一个梯子翻过去了，OK，没问题了



其他几个优化，加强配置

1. 上文提到的服务器证书是公开的，因此不必考虑太多

   但是秘钥要严格限制访问权限，最好除了Nginx能访问外，其他程序都不能访问

2. SSL操作会耗费额外的CPU资源。应该通过以下方式优化

   1. 工作进程数应不少于可用CPU核心数

   2. 启用`keepalive`来一次连接，多次请求，减少SSL握手阶段耗费的资源

   3. 重用SSL参数避免SSL连接并行和后续连接而导致的握手。

      也就是将SSL会话保存在缓冲区中，并通过timeOut淘汰过时的连接

      需要配置如下

      ```
      http {
          ssl_session_cache   shared:SSL:10m;
          ssl_session_timeout 10m;
      
          server {
              listen              443 ssl;
              server_name         www.example.com;
              keepalive_timeout   70;
              ssl_certificate     www.example.com.crt;
              ssl_certificate_key www.example.com.key;
              ssl_protocols       TLSv1 TLSv1.1 TLSv1.2;
              ssl_ciphers         HIGH:!aNULL:!MD5;
              ...
      ```

      如上，提供10M的缓存区用于存储SSL会话，10分钟后超时

3. 有些时候，你用了知名证书签发机构签发的证书，配置了Nginx的SSL协议，但是用浏览器测试时，浏览器弹出警告，说你的服务器证书不可信任。

   这出现的原因可能是，证书签发机构用的不是他们的证书来签署，而是用中间证书来签署你的证书。

   这个中间证书某些浏览器不认识，自然就不可信任了

   这种情况，一般来说，证书签发机构还会给你一组链式证书（eg:`bundle.crt`）

   这个时候，你需要把服务器证书加入到链式证书里面，使之可信,假设你的证书是

   （` www.example.com.crt`）

   将你已被签署的数字证书加入证书链的命令行如下

   `cat www.example.com.crt bundle.crt> www.example.com.chained.crt`

   > 就是将你的证书和证书链合并为一个新的文件

   将这个文件作为新的证书文件，配置进Nginx里面，就ok了

4. 可以配置一个server，既可以接收http请求,也可以接收https请求

   配置如下

   ```
   server {
       listen              80;
       listen              443 ssl;
       server_name         www.example.com;
       ssl_certificate     www.example.com.crt;
       ssl_certificate_key www.example.com.key;
       ...
   }
   ```

   很简单，就不解释了

5. 如何能在同个IP，多个server配置下，使用SSL？

   比如说，有一段配置如下

   ```
   server {
       listen          443 ssl;
       server_name     www.example.com;
       ssl_certificate www.example.com.crt;
       ...
   }
   
   server {
       listen          443 ssl;
       server_name     www.example.org;
       ssl_certificate www.example.org.crt;
       ...
   }
   ```

   配置中，两个server绑定的是同一个IP，只是域名不同。

   那么问题来了，如果一个https请求过来（ssl建立连接没有请求域名信息），Nginx要拿哪个证书给客户端

   > 别提SSL协议的拓展字段域名，现在先假设没这东西。

   答案是没得选，Nginx永远只拿默认的那个server配置的证书。

   这样当然会出问题，如果我访问的是`www.example.org`,但是却给我`www.example.com.crt`这个证书。

   证书就不可信了

   怎么解决？

   1. 抛弃同个IP，多个server配置，每一个server都绑定一个独有的IP地址。

      这样就可以通过IP地址拿到正确的证书了

   2. 让证书绑定两个域名，这样一个证书就可以作用于多个域名了

      可采取的方法是在证书的SubjectAltName字段中存储多个域名，使之绑定（然SubjectAltName字段小）

      可采取的方法是使用通配符的证书。

      这两个方法可以组合使用，改造好的证书文件，重新配置在Nginx配置文件的http块指令上

      如

      ```
      ssl_certificate     common.crt;
      ssl_certificate_key common.key;
      server {
          listen          443 ssl;
          server_name     www.example.com;
          ...
      }
      server {
          listen          443 ssl;
          server_name     www.example.org;
          ...
      }
      ```

      这样，http指令块里面用到https，所用的证书，都是这两个

   3. 第三个，就是千呼万唤始出来的SSL加强版，简而言之，就是在SSL协议中加了一个扩展字段。

      用于存储请求的域名（这个解决方案也叫做SNI）

      使用这个方法只需检查你安装的Nginx支不支持SNI就行了

      `nginx -V `

      看英语就行了

      支持就没什么话好讲的了，下班，走人。

      如果不支持，亲，在构建Nginx时要构建出一个能支持SNI版本的Nginx出来啊

      反正我用的是二进制分发版，开箱即用，这是极好的

      

      














### 3.3 日志系统

#### 3.3.1 日志位置

日志存放位置可以从主进程的配置文件中拿到。

如果你没更改过位置，那么默认情况下，日志文件会存放在`/var/log/nginx`文件夹里面

里面包含了两个的日志，`access`和`error`日志，其实你还可以开启一个`debug`等级的日志，下面的讲述将讲到



#### 3.3.2 如何开启debug等级日志

如果你的Nginx是需要自己编译的，那么为了支持输出debug等级的日志，你编译的过程中，还需要加点东西。

但如果你的Nginx是官网提供的二进制文件，默认它就支持了debug日志文件输出

为了使debug日志输出，还需要改下配置文件`nginx.conf`

```
....
error_log  /var/log/nginx/debug.log debug;
....
....
http {
   	....
     .....
     .....
    server{
        .....
        error_log  /var/log/nginx/serverDebug.log debug;
        .....
     }
}
```

> ps 如果使用error_log时，仅配置了日志路径，没有配置日志等级。
>
> 比如说`error_log  /var/log/nginx/debug.log;`
>
> 那么，这个位置的日志输出等级debug将被禁用

如上所述，日志等级的配置需要通过`error_log`指令来完成。

而且还可以分级配置，诶，server再配一个，主上下文再配一个

而且还可以一级多个日志配置，配置上面没写，但你要喜欢，server上再配一个error_log也是完全OK的

而且不仅只有debug支持配置，warn，info都可以选择。

甚至你要记录来自特定的客户端的日志也行

```
error_log /path/to/log;
events {
    debug_connection 192.168.1.1;
    debug_connection 192.168.10.0/24;
}
```

> 前面配置`error_log /path/to/log;` 是为了看出效果来，如果都是debug，单单为这个客户端设置debug又有什么意义呢？
>
> 还有，注意一点，记录来自特定的客户端的日志要看到效果，还得明确客户端访问的是哪个端口。
>
> 如果对应端口的server配置有日志配置，那么这个客户端的debug日志会记录在对应端口的日志文件中。
>
> 除非对应的端口server配置没有日志配置，那它才会一级一级往上找，直至找到主上下文日志。



配置文件加上调试日志输出，你以为这样就可以看到debug日志了，too young too simple。

普通的Nginx启动是记录不了调试日志的，如果你的Nginx是分发的二进制，那么你应该还有这个可执行文件

`nginx-debug`

执行它，看看效果吧。



**讲的有点乱哈，其实要说明的只有一个前提和两个步骤而已**

前提：Nginx构建时有构建一个debug版本，或者你的Nginx是二进制分发的

步骤1 ：根据自己的需求，配置debug日志

步骤2：关闭普通的Nginx进程，开启Nginx-debug进程

搞定



> 最后说一下，日志配置还支持将日志记录在` cyclic memory buffer` 
>
> 如此配置的话，Nginx的效率就不太受日志影响，甚至可以在高负载下运行。
>
> 然后，我们可以用gdb脚本将日志提取出来
>
> 有关资料，请参阅官方文档

####  3.3.3 将日志记录到syslog中

按照我的理解，`syslog`日志其实就是一个日志服务器，专门从网络中接收日志信息，并存储起来。

所以，这一章所讲的就是将Nginx产生的日志，通过网络，传递给syslog日志。

唔，但是我觉得知道这个概念就行，先阶段没必要弄这个，就暂且这样吧。

挖坑预定~



###  3.3 Nginx工作原理

#### 3.3.1 Nginx如何处理请求

假设配置文件的server配置大致如此

```
server {
    listen     192.168.1.1:80   default_server;
    server_name example.org www.example.org;
    ...
}

server {
    listen      192.168.1.1:80;
    server_name example.net www.example.net;
    ...
}

server {
    listen      192.168.1.2:80 ;
    server_name example.com www.example.com;
    ...
}
```

一个请求过来了，它将和那个server匹配呢？

匹配步骤是酱紫的

1. 请求来啦

2. 提取请求里面请求头的`Host`信息和请求地址

3. 根据请求地址和server配置里面的listen进行匹配，匹配的上，但是有多个，继续下一步的匹配

4. 根据将请求头的Host和配置里面的`server_name`匹配继续匹配，匹配上，则该server就是目标生效的server

   > 可能会很好奇，咦，怎么listen里面有两个不同的IP呢？一个机器绑定多个IP吗？
   >
   > 我现在告诉你吧。。我也不知道。。。但是换ip，这种配置就有用了

意外情况：额，请求头里面根本没有`Host`信息，或者根本找不到一个能与之匹配的`server_name`

那么Nginx会找默认的server，一般情况下，默认的server是配置文件的第一个

或者你可以手动指定一个默认的server  在listen 简单指令那里加入`default_server`

也就是`listen      80  default_server;`

> ps：default_server 是listen的属性，不是server_name的属性

另外一个解法，当请求头没有`host`时，可以专门配置一个`server`来处理这种情况

```
server {
    listen      80;
    server_name "";
    return      444;
}
```

server_name 配置为空串正是为了处理没有`Host`请求的情况的）

对于这种情况，Nginx会返回一个Http状态码`444`（非标准状态码），表示服务器已经没什么返回的了。并关闭连接



匹配到了对应的server了，接下来要匹配location了

假设匹配到的server快指令下面的配置如下

```
server {
    listen      80;
    server_name example.org www.example.org;
    root        /data/www;

    location / {
        index   index.html index.php;
    }

    location ~* \.(gif|jpg|png)$ {
        expires 30d;
    }

    location ~ \.php$ {
        fastcgi_pass  localhost:9000;
        fastcgi_param SCRIPT_FILENAME
                      $document_root$fastcgi_script_name;
        include       fastcgi_params;
    }
}
```

然后，匹配步骤是酱紫的

1. 从请求Host后面，分段提取请求的URL字符串，进行匹配

   比如说请求的地址是`http://www.thisisweb.tk/cgi/1.php `

   那么先分段取出`/`   `/cgi` 与配置文件里面location的value进行匹配

   然后，如果配置文件里面有正则表达式，还要进行正则表达式匹配

   整个匹配过程就好像是入栈。

   匹配结果就是出栈，匹配不通过就一级一级往上退，直至到最顶层`/`location配置

2. 匹配成功了，能决定是哪个location处理请求了。

   接下来就看location里面的配置，有直接访问本地系统的文件，比如说`root  /data ;`

   也有将请求转发的

   ` proxy_pass http://localhost:8080;`

   看情况选择咯

3. 完毕，你还想知道什么？

   > 如果只有location为`/`得到匹配，比如说，请求地址是`http://www.thisisweb.tk/`
   >
   > 那么，怎么找资源返回前端。
   >
   > 根据上面配置文件里面，server块指令里面的`root`配置和`location /` 里面的配置
   >
   > Nginx先去查找`/data/www`文件夹里面有没有`index.html`文件
   >
   > 如果没有，再查找`/data/www`文件夹里面有没有`index.php`文件
   >
   > 如果还没有，应该404了

#### 3.3.2 TCP/UDP会话处理

  该节主要讲述的是Nginx在对于TCP和UDP的请求时，是如何处理的。

相对于正常使用的场景来说。有点底层了

先挖坑，后续可能再补





### 3.4 扩展

#### 3.4.1 使用njs对Nginx就行功能性扩展

njs是javaScript的一个子集。可以用它对NGinx进行功能性扩展

应用有

1. 在请求转发到上一层时，对请求进行复杂访问控制和安全性检测
2. 操作响应头
3. 编写内容处理器和过滤器

大致了解酱紫，以后用到再补充

没错，我又挖坑了

#### 3.4.2 其他

吹嘘Nginx有多厉害的一篇文章

`http://www.aosabook.org/en/nginx.html`

有空读读，还是有用的哦





## 五：问题

1. 配置好location后，没有生效

   

 





























 





