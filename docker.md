# Docker笔记

# 未完成点

1. `docker swarm init`  是什么意思

2. 第九章加粗点不理解


# 一：什么是Docker?

Docker是一个容器技术。可以让我们的服务器程序置身其中，提供独立的环境配置

比如说环境变量，比如说库，比如说依赖。

为什么要使用它，当然有以下几个优点

## 1.1 Docker的优点

1. 一键部署

2. 启动快速

3. 适用于基于windows和linux的程序

4. 方便控制程序的运行环境，比如说说虚拟机，VM参数，以及各种环境变量

5. 降低风险，测试程序时，可以使用和正式环境一样的镜像配置。

   发布时只需替换掉旧镜像即可，这降低了发布的风险

6. 环境配置文件，即`Dockerfile`可以置于版本控制之下，跟踪环境的修改

7. 很方便的在不同的容器中，维护相同的配置。而无需在多台服务器上，维护相同的配置

8. 环境的配置是自动化的，开发团队无需手动配置环境

9. 应用程序没有系统依赖性，便于CI/CD（持续集成和持续部署）

## 1.2 Docker缺点

1. 在应用程序多加了一层`Docker`部署，增加了复杂性

2. 容器共享相同的内核，因此如果内核出错，Docker容器全部都得挂

3. 早些年，Docker只能运行在LInux，在Windows运行很复杂

   现在，这个缺点已经不太明显了

   Docker提供了`Docker for Windows`

   提供了完整的工具链，你可以在Windows上面开发服务器应用程序，并用Docker镜像运行起来

   但是，这样开发的Docker程序只能用于Linux环境上

   就连`Docker for Windows`想要最终运行Docker 程序，都是在Windows创建一个linux虚拟机，用于运行Docker引擎

4. Docker运行需要Root权限，在某些监管严格的行业里，开发人员不太能取到ROOT权限。

5. Docker还不清楚是否满足某些安全标准（PCI）

6. Docker不包含在`Red Hat Enterprise Linux 6`中，需要从`Docker`官网下载

   而这个网站，是一个`不受信任的来源`

   有些公司，可能只允许来自官方/可信来源的软件才能安装在机器上



## 1.3 Docker的一些概念

Images:机翻把它翻译成图像，其实它的术语叫做映像。

映像是一个可执行包，里面包含可执行代码，环境变量，库、以及一些配置文件

容器：是映像运行时的一个实例，





# 二：快速入门

## 2.1 安装Docker

首先安装Docker

笔者使用的是Docker for windows 版本

这个版本可以让你在windows上开发Docker的应用程序

但是有几点要求

1. 你的系统必须是win10  企业版或者专业版

   > 只有如此，win10 系统才有HYPER-V
   >
   > 而Docker需要HYPER-V

2. 必须开启虚拟化

   > 这个需要在BIOS中开启，看到英特尔虚拟化技术几个英文单词，把它启用就行了

笔者只遇到这两个问题，最后怎么解决的呢。

给微软充钱，如果充钱一次不能解决问题，那就充两次。

> 谁叫你的系统是Win10 家庭版~~~



> 另外说一点，想在Linux上运行Docker，只需要下载Docker引擎就行（毕竟Docker是Linux的亲儿子）
>
> 关于怎么安装，官网说的很清楚了，用官网的教程没有任何问题，所以，这里也不介绍了
>
> 最后只要能运行HelloWorld镜像，那就没问题

## 2.2 运行一个HelloWorld映像

在cmd命令行键入`docker --version`

看到`Docker version 18.09.1, build 4c52b90`

即表示您已成功安装Docker

然后下面是正菜在容器上

```cmd
docker run hello-world
```

> 用cmd执行

执行后的结果，你可以看下，它其实就是打印一堆话而已。这堆话还解释了Docker做了什么事

1. Docker客户端通知了Docker守护程序
2. Docker守护程序从Docker Hub中把HelloWorld映像拉到本地了
3. Docker守护程序使用HelloWorld映像创建了一个容器，用于运行一个只输出文字的可执行程序
4. Docker将输出流传到Docker客户端，然后到你的命令行上

## 2.3 列出本地映像仓库有什么东西

cmd命令行`docker image ls`

如果你执行了上面那章命令，你应该可以看到



## 2.4 列出容器

`docker container ls --all`

包括历史运行过的容器

`docker container ls`  默认只显示运行的



## 2.5 停止容器

`docker container stop ${container_name}`

## 2.6 删除容器

`docker container rm ${container_name}`

这样即使你查询所有的容器列表`docker container ls --all`

也看不到被删除的那个容器了

## 2.7 进入一个已经运行中的容器

`docker container attach c5ad98657d7e`

`c5ad98657d7e`就是容器的名称

可以通过`docker container ls`看到，`CONTAINER ID  `那一栏就是

## 2.8 启动一个已经停止的容器

命令:` docker container start {container_id}`

关于容器ID，可以从容器列表中观察到

以上





#  三：进阶：在容器上运行Ubuntu的映像

1. 拉出Ubuntu的映像并在运行的容器上运行命令行bash

   `docker run --interactive --tty ubuntu bash`

   > --interactive   打开标准输入，即使没有连接
   >
   > （当然是为这个容器打开标准输入了，输入的信息会传给容器）
   >
   > --tty 分配一个假的tty（终端）（就是显示一个假的命令行提示符，类似于`root@51c8aa991c30:/#`）
   >
   > bash就是Unix shell
   >
   > 另外，进入到linux后，敲入hostname。你会看到容器的ID
   >
   > exit将退出shell，也会结束容器

# 四：进阶：在容器上运行Nginx的映像

cmd 命令行`docker run --detach --publish 80:80 --name webserver nginx`

>-- detach 在后台运行该容器，并打印容器的ID
>
>--publish 80:80  开启宿主机的80端口，映射到Docker容器的80端口上
>
>-- name 就是手动给创建的容器创建名称，如果没有指定容器名称的话，默认是随机生成的

执行该命令完后，可以在宿主机上直接敲127.0.0.1,在浏览器上访问

# 五：关于Docker的一些外部配置

如果你使用的是Docker for windows 版本，则在任务栏下面，有一条鲸鱼图标，右击打开setting

可以看到一些外部配置

可以修改的配置有

1. 系统启动时启动Docker？
2. 自动更新？
3. 将本地的硬盘共享到容器里？
4. 限制Docker可用的资源
5. 下载容器使用代理

# 六：进阶：使用容器部署我们的应用

这次要达成的目标是，写一个py语言的服务器程序，并用它来创建一个镜像，运行之，查看效果

首先介绍下一个新概念`Dockerfile`

这其实是一个文件来着，里面记录了怎么创建一个镜像。

包括这个镜像应该有的依赖，应该有的环境变量，应该有的服务器代码。

简而言之，一个应用程序要正常运行应该拥有什么东西，都写在里面了

`Dockerfile` 文件示例

```
# 使用官方Python运行时作为父镜像
FROM python:2.7-slim

# 设置工作目标为app  WORKDIR可以理解CD
WORKDIR /app

# 拷贝当前的目录内容到app里面
COPY . /app

# 安装运行所要依赖的py库和文件，在requirements里面定义了
RUN pip install --trusted-host pypi.python.org -r requirements.txt

# 开放80端口，使宿主机能通过80端口与容器互通有无
EXPOSE 80

# 定义环境变量
ENV NAME World

# 当容器运行时，在容器中执行app.py文件
CMD ["python", "app.py"]
```

仅有这个文件还不够，因为我们Dockerfile 里面还写了两个文件

`requirements.txt`

```
Flask
Redis
```

> 该文件定义了Python的库依赖
>
> 有Flask和Redis 的Python库依赖

`app.py`

```python
from flask import Flask
from redis import Redis, RedisError
import os
import socket

# Connect to Redis
redis = Redis(host="redis", db=0, socket_connect_timeout=2, socket_timeout=2)

app = Flask(__name__)

@app.route("/")
def hello():
    try:
        visits = redis.incr("counter")
    except RedisError:
        visits = "<i>cannot connect to Redis, counter disabled</i>"

    html = "<h3>Hello {name}!</h3>" \
           "<b>Hostname:</b> {hostname}<br/>" \
           "<b>Visits:</b> {visits}"
    return html.format(name=os.getenv("NAME", "world"), hostname=socket.gethostname(), visits=visits)

if __name__ == "__main__":
    app.run(host='0.0.0.0', port=80)
```

> 该文件是用py语言编写的一个服务器程序

好了，以上三个文件都在同一个文件夹内对吧

接下来

1. 根据Dockerfile 创建镜像,在当前文件夹内，打开cmd，执行此命令

   ` docker build --tag=friendhello .`

   > --tag=friendhello   就是将创建的镜像名称设置为friendhello 
   >
   > 后面应该跟着Dockerfile文件的路径，但是我们的Dockerfile就在当前目录下，所以一个`.`即可

   这样你就构造了一个镜像，可以通过`docker image ls`查看你创建的镜像

2. 通过`docker run -p 4000:80 friendlyhello  运行该镜像`

3. 在宿主机上的浏览器敲入`http://127.0.0.1:4000`查看效果

值得一提的是，虽然我们在requirements加了Python的redis库

但是机器上，并没有运行redis服务器，所以，在这个服务器上，redis服务是不可用的



##  6.1 Dockerfile  文件详解

Dockerfile 是用于创建镜像的配置文件

里面主要是命令的集合，而且一行一个命令，有些命令是必须的，有些命令则不是必须的

在该文件里面，可以使用的命令如下

1. `FROM  xxxxx`用哪个作为父镜像，或者说，在哪个基本环境里面开发我们的镜像

2. `VOLUME  {path}`将容器里面的一个目录（路径是`/path`）映射到主机的一个文件

   使该目录里面的文件可以持久化存在
   
3. ARG  用于定义变量，变量可以赋值，也可以不赋值
   
   语法
   
   ```
   ARG VERSION=latest
   ARG GO_VERSION
   ```
   
   不赋值的变量可以在执行构造镜像的时候再指定，详细可以看下面的build命令
   
   变量可以在Dockerfile直接引用到
   
   eg:
   
   ```
   FROM golang:${GO_VERSION}
   ```
   
   就是直接指定父镜像的版本了
   
4. RUN 简单的说，就是执行命令了，它有两种语法形式
   
   ```
   RUN <command>
   RUN ["executable", "param1", "param2"]
   ```
   
   第一种就是单独的执行某个命令，不用带"<",">"号的
   
   第二种就是执行带参数的命令
   
5. CMD 这个也是用于执行命令的，所不同的是，一个Dockerfile  只能有一个CMD命令，如果有多个，则会生效最后一个
   
   举例子：
   
   ```
   CMD ["elasticsearch"]
   ```
   
6. USER xxx 可以使下面的命令以指定的用户运行，或者直到run命令时再指定用户名也可以，不过，如果这两种方式都没有指定的话，默认是以root用户来运行的
   
7. 

   
   
   

   

   

   

   

## 6.2 用docker部署一个Springboot程序

先决条件

1. 安装有docker，并能运行helloWorld镜像
2. 有一个SpringBoot程序，能编译成jar独立运行，换言之，有个可以运行jar文件

步骤

1. 编写DockerFile文件

   ```
   FROM openjdk:8-jdk-alpine
   VOLUME /tmp
   ARG JAR_FILE
   COPY ${JAR_FILE} app.jar
   ENTRYPOINT ["java","-jar","/app.jar"]
   ```

   解释一下：

   1. 所有docker文件上面都要包含父镜像，也就是`FROM xxx`

      对这个应用而言，最合适的镜像，自然就是java运行环境的镜像了

   2. VOLUME `/tmp`  将容器的`/tmp`映射到本机的一个文件中，使容器的`/tmp`能够永久保存

   3. `ARG JAR_FILE`  声明一个构建参数，该构建参数用于指定jar文件的路径

   4. `COPY ${JAR_FILE} app.jar` 拷贝本机的jar文件到容器里面

   5. `ENTRYPOINT ["java","-jar","/app.jar"]` 在容器里面运行`java -jar /app.jar`命令

2. 运行构建命令

   `docker build --build-arg JAR_FILE=SpringBootExample.jar --tag springboot .`

3. 运行构建出来的镜像`docker run springBoot`

**问题**

最后一步，运行run命令时，报`Error: Invalid or corrupt jarfile /app.jar`

看意思就大致清楚是`app.jar`的问题

于是就想从镜像中，把它拉出来，经过搜索引擎帮助，通过导出镜像文件的方法，成功将镜像文件内容导出

发现。。。

在镜像文件里面，app.jar是一个文件夹，不是熟悉的jar文件结构

好吧，进一步发现

我们的构建命令是错误的

`docker build --build-arg=./xxx.jar --t springBoot`

没加键值对鬼知道`xxx.jar`赋值到哪个变量

而且其实有警告来着`One or more build-args [sdfhgdfjh] were not consumed`

再吐槽一下：SpringBoot的docker给的示例构建命令是

```
$ docker build --build-args=build/libs/*.jar -t myorg/myapp .
```

简直误人子弟



## 6.3 用docker部署war包

前提

1. 有一个war包
2. 有一个docker运行环境，至少能运行helloWorld镜像

步骤

1. 编写Dockerfile文件

   ```
   FROM tomcat
   # 如果你即将部署的应用恰好叫ROOT.war 那么需要把原tomcat的ROOT应用删除
   run rm -rf /usr/local/tomcat/webapps/ROOT
   COPY ROOT.war /usr/local/tomcat/webapps/ROOT.war
   ENTRYPOINT ["catalina.sh","run"]
   ```

2. 构建镜像

   `docker build --tag weixin .`

3. 运行镜像

   ` docker run --publish 10086:8080 weixin`

4. 你的应用已经部署在宿主机的10086端口，赶紧访问下吧

遇到的BUG

1. 由于没有`run rm -rf /usr/local/tomcat/webapps/ROOT`

   导致访问根目录还是tomcat默认提供的root应用

2. 运行镜像报错，报一大堆错，最后在最底下发现是端口绑定问题

3. 也不算BUG,就是构建镜像时，发现一句话

   `Sending build context to Docker daemon  1.031GB`

   卧槽，我的war包才几十M不到，怎么要发送这么大数据到docker守护程序里面

   其实是因为docker映像构建时，会把当前文件夹以及子文件夹的数据发送给docker守护程序

   有些文件可能我们并不需要，这时候只需要在Dockerfile同位置的文件夹下面建立一个`.dockerignore`文件

   里面可参考的内容如下

   ```
   # 这是一个docker构建忽略文件
   # 忽略classes文件夹
   /*classes
   #忽略tar类型文件
   *.tar
   ```

   



# 七：分享我们的镜像

镜像一个大的优点，就是它便于携带，可以灵活在不同的物理机上迁移，要做到这一点，你就要学会怎么把镜像分享到其他物理机上。

这里涉及到两个概念

1. registry  （机翻叫注册表） 这是一个存储库的集合

   > 账户也在注册表上，所以实际上，我们可以创建一个账户，账户里面，可以创建多个存仓库啦
   >
   > 另外一提，注册表的提供商有Docker，也有很多公共注册表，甚至你可以自己创建一个私有的注册表来使用
   >
   > 怎么样，看起来像不像git？ github就类似于注册表，我们在github上的用户相当于注册表中的用户
   >
   > 而github里面的存储库，也就相当于docker里面的存储库了
   >
   > 而且，git的提供商不仅只有github，码云也算一个吧

2. repositories  存储库，也叫仓库，是一个镜像的集合，我们之前创建的那个镜像，就是放在这里面的

要分享我们的镜像，

1. 首先你在注册表里面，要有一个用户。现在我们去`hub.docker.com`注册一个用户吧

2. 然后在本地上登录它

   `docker login`

   > 不要用邮箱做用户名登录哦

3. 然后给你要推上去的镜像起个别名

   `docker push username/repository:tag`

   用户名，仓库两个元素缺一不可，必须准确，后面的tag倒可以忽略，不过建议补上。不然就缺了版本了

   > 你问要是不准确咋办，很简单，就推不上去而已
   >
   > 对了，仓库的话，你需要去网上登录这个账户创建一个，到时会让你定义一个仓库的名字的

4. 最后将我们的镜像推上去

   `docker push username/repository:tag`

   

   搞定

# 八：从远程拉下我们的镜像运行

嘛，其实哪需要专门敲命令来拉镜像

其实只需要一个运行命令

`docker run -p 4000:80 username/repository:tag`

只要本地没有这个镜像，就会从远程的仓库拿，根本不需要pull命令

以上

> 执行以上命令，会将镜像放在本地，下次执行，就直接从本地拿了



# 九：在Docker里面运行服务

服务，通俗的理解，是应用程序的某个部分，比如，对于视频网站YouTube而言

用户登录模块，算是一个服务

视频上载模块，算是一个服务

视频下载模块，算是一个服务

一个服务，只运行在一个镜像上

在Docker上，运行、扩展、定义服务，需要通过写一个`docker-compose.yml`文件

`docker-compose.yml`文件定义了Docker容器，在生产环境上是如何工作的

以下是一个示例的`docker-compose.yml`文件内容

```
version: "3"
services:
  web:
  	# 别傻傻的复制后直接运行，这地方要改下的说
    image: username/repo:tag
    deploy:
      replicas: 5
      resources:
        limits:
          cpus: "0.1"
          memory: 50M
      restart_policy:
        condition: on-failure
    ports:
      - "4000:80"
    networks:
      - webnet
networks:
  webnet:
```

这个文件做了几件事情

1. 从本地或者远程仓库中拉去名字为`username/repo:tag`的映像
2. 启用5个容器运行该镜像，限制每个容器使用最多10%的cpu和50M的内存
3. 如果一个失败了，立即重启该容器
4. **将宿主机的4000端口映射到容器的80端口** 
5. **让每一个容器共享负载平衡网络的端口80**
6. **使用默认设置定义webnet网络（它是一个负载平衡的覆盖网络）**

要在Docker创建一个分布式服务，首先要

1. 创建`docker-compose.yml`文件

2. 运行cmd命令

   ```
   docker swarm init
   ```

> 启动swarm（集群）模式并使当前计算机成为集群管理器

3.  运行命令 

`docker stack deploy -c docker-compose.yml getstartedlab`

> 从 docker-compose.yml读取配置并部署一个新的栈
>
> 并起名为getstartedlab
>
> 当然，如果`getstartedlab`已存在，就是更新这个栈啦

4. 查看效果

   命令行键入`docker service ls`

   查看你有多少服务

   你也可以敲入`docker container ls`查看，因为分布式的每一个服务，都是一个容器，所以当然能显示出来啦

   但是有个缺点，它没有过滤不是服务的容器。不管容器是不是服务器，它都会显示出来

5. 继续查看效果

   总的来说，就是在宿主机上访问`http://localhost:4000/`

   并且多次执行以查看情况。

   你会发现**Hostname**在每次访问都不一样

   这就是负载均衡正常运行的一个实锤了

   > Hostname 就是主机的ID，Docker默认的特性。别告诉我说你忘了

6. 想更新这个栈，很简单，比如说，我微服务想弄多点，我要弄6个

   也就是说`docker-compose.yml`

   这个文件里面的`replicas`设置为6

   然后改完，保存。

   只需再执行`docker stack deploy -c docker-compose.yml getstartedlab`

   不需要停止容器或者停止栈

   

## 9.1 删除服务

   步骤1：删除放置服务的栈

   `docker stack rm {stack_Name}`

   > docker stack ls 可以查看你当前有哪些栈，这个其实不用我讲吧

   步骤2：离开集群

   `docker swarm leave --force`
# 十 用Docker实现集群

所谓集群，就是运行Docker的一组计算机。

集群的计算机，可以是虚拟的，也可以是物理的，但是加入集群后，它门被称为节点

你可以用Docker命令管理集群，而执行这个Docker命令的容器，被称为集群管理器

Docker的集群管理器可以指定多个策略来运行节点中的容器

Docker集群管理器是唯一可以执行Docker命令的容器。其他的容器只能提供服务。

说了这么多，来实际设置自己的集群吧。

前提

1. win10，有hyperv功能

   > 如果没有hyperv功能，也可以安装virtualbox ，但这就不在下面的讨论访问内了，你可以去官网找找看教程

2. 安装Docker 

步骤

1. 首先你要在想成为集群管理器的机子上执行`docker swarm init`

2. 打开hyperv管理界面，新建一个虚拟交换机`Virtual Switch` 并命名为`myswitch`

   保证`共享此网络适配器`复选框选中

3. 在cmd敲入

   ```
   docker-machine create -d hyperv --hyperv-virtual-switch "myswitch" myvm1
   docker-machine create -d hyperv --hyperv-virtual-switch "myswitch" myvm2
   ```

   这个命令需要下载一个iso文件，可能会等较长时间（50多MB）建议设置梯子

   顺便一提，这里两个命令的作用是，在你的hyperv虚拟机上，创建两个虚拟机.

   一个叫`myvm1`

   一个叫`myvm2`

   注：如果执行上面那个命令后，控制台卡在了`(myvm1) Waiting for host to start...`

   请打开网络适配器，看看里面是不是有一个名为`switch`的适配器连不上网络，显示网线被拔断

   如果是，可能是你的虚拟交换机`switch`的网卡配置有问题，选了一个·不能上网的网卡

   笔者就是这样的。。

4. 以上两个命令执行完，相信你也可以看到`hyperv`管理器里面多出了两个虚拟机。

   如果你想再验证下，可以敲`docker-machine ls`  

   你可以看到这两个虚拟机的IP地址以及名字等一些信息

---

通过以上的操作，我们建立两个虚拟机，并且安装了docker

其实吧，上面的操作，即使你不用虚拟机，而是有多个物理机，同样也可以做到

而正式开发上，肯定也是这样的，不可能把集群全安装一个机器上，节点全都是虚拟机吧

那怎么做的。 

前提，你真的要有起码两台物理机，其中一条安装Docker，用于执行命令，另外一条至少安装有ssh，可以远程登录

1. 取得安装ssh机器的ssh私钥（请参考linux教程）

2. 然后在安装了docker的机器上，执行命令

   ```
   docker-machine create \
     --driver generic \
     --generic-ip-address=144.202.59.222 \
     --generic-ssh-key ~/.ssh/id_rsa \
     vm
   ```

   注意啦，如果你是在windows执行该命令的

   `\`都要换成 `` ` 前面再加一个空格哦

   windows和linux的多行编辑符不一样

   然而还是有一个问题，就是在windows上，ssh key总是访问不到

   其实是因为docker认的windows文件夹分隔符只能是`/` 这不是规范，这是docker的特性

   最后还是不知道怎么肥四就弄好了。。ORZ

   执行以上命令，你的云主机就自动创建了docker并在客户端记录了一个machine的记录了

   你可以通过`docker-machine ls `来查看机器列表 

   有时会出现timeOut的情况，你可以加延迟超时时间

   `docker-machine ls -t 30` 默认10s超时，这次改成30秒超时

---

通过以上两种方式，相信你已经弄了几个虚拟机/物理机，并把它们都加入到了docker-machine列表里面了

接下来

1. 我们选其中一个物理机/虚拟机，让它成为swarm集群管理器

   `docker-machine ssh myvm1 "docker swarm init --advertise-addr <myvm1 ip>"`

   这样，我们就把machine列表中，一个叫`myvm1 `的机器设置为集群管理器了

   使用这个命令，你不用专门跑到那个集群管理器来执行这个命令

   这个命令本来就是远程ssh来执行的哦

   `--advertise-addr`是这个集群对外的地址，将来加入集群的服务器需要用到这个地址

   这个地址其实还有一个端口，不过我们没指定，就默认是`2377`了

   另外，如果你要自定义端口的话，切记不要用`2376` 那个是docker守护程序的端口

   最后，执行那个命令，返回响应会包含一个命令

   `docker swarm join --token SWMTKN-1-0sd6ihyygbxlyvdahsuawb31qknugpm5lqdvjqn9mtafkcpo88-4dbnwwxo334w3rtk49ml8wlpu 144.202.59.222:2377`

   执行这个命令，可以将对应的docker主机加入到集群中。

   可以在其他装有docker的机子上执行

   当然，也可以用docker-machine工具来执行

2. 加入集群

   其实用的就是上面那个命令

   `docker swarm join --token SWMTKN-1-15w7ii8xmnfjpc6tl80kq0d96vymr7a3n0ajlr3jrsm3xgu9s7-1079vd27rdy2xt781hanslcz2 192.168.200.126:2377`

   > 忘记了Token和IP咋办，只要你还知道集群管理器是哪个
   >
   > 执行这个命令`docker swarm join-token manager`
   >
   > 就可以重现那个join命令

   不过我们可以用`docker-machine ssh {machine_name} "{command}"` 来远程执行

   执行完命令你可以看到

   `This node joined a swarm as a worker.`

   欢呼把，庆幸吧，还好你没遇见timeOut问题

   我就遇见了。。。嘛，docker告诉我，虽然执行过程timeout了，但是实际上还在后台执行

   我可以在集群管理器执行`docker info` 来查看集群情况

   > 其实不止集群，很多东西都爆出来了。。

3. 在集群管理器上，执行`docker node ls`

   可以查看加入集群的节点

4. 如果想离开集群，可以在想离开集群的节点上执行`docker swarm leave`

   ---

5. 切换当前shell环境为集群管理器

   首先我们把当前的控制台配置为直接和远程的VM通信（其实就是跟XSHELL一样）

   执行以下命令

   `docker-machine env {machine_name}`

   该命令执行后，会提示你运行某一行命令，我运行的命令是这个

   ```
   & "C:\Program Files\Docker\Docker\Resources\bin\docker-machine.exe" env myvm1 | Invoke-Expression
   ```

   其他人可能不一样，要紧盯控制台的输入哦

   ok，这些完毕后，你在控制台的任何输入，都会反馈到远程的服务器上，就相当于一个SSH了

   但是它又可以直接访问到本地的文件，可以说很牛*了

   > 想退出此shell怎么办
   >
   > ` & "C:\Program Files\Docker\Docker\Resources\bin\docker-machine.exe" env -u | Invoke-Expression`
   >
   > 这样你这个shell就会和远程服务器断开连接，以后执行的任何命令，都只是在本地执行了

6. 在集群管理器上部署一个应用

   其实跟之前部署单机的服务一样，只不过这个服务，现在变成集群服务了

   执行以下命令

   `docker stack deploy -c docker-compose.yml getstartedlab`

   

   OK ,搞定，你已经在两个集群管理器上面运行了两个相同的服务器

   此谓之集群啦

   想验证下？

   `docker-stack ps getstartedlab`

   看到输出的列表了吗？

   那就是服务啦

   

7. 验证服务的运行

   首先运行`docker-machine ls`列出注册在本机远程机器

   随便拿一个IP

   > (当然得是加入集群的那个机器的IP)

   然后访问`IP：PORT`

   > port是多少，请查找你的`docker-compose.yml`文件配置

# 十一：往集群添加一个可视化工具

前面我们创建一个集群，并在上面跑了一个很简单的镜像（服务）

现在我们再加一个服务：可视化工具，值得注意的是，这个工具，只能运行在集群管理器，因为它是一个监控集群的一个东西。

为了能够添加这么一个东西，我们需要

1. 修改`docker-compose.yml`,使其如下所示

```
version: "3"
services:
  web:
    image: choosedockers/test
    deploy:
      replicas: 6
      resources:
        limits:
          cpus: "0.1"
          memory: 50M
      restart_policy:
        condition: on-failure
    ports:
      - "4000:80"
    networks:
      - webnet
  visualizer:
    image: dockersamples/visualizer:stable
    ports:
      - "8080:8080"
    volumes:
      - "/var/run/docker.sock:/var/run/docker.sock"
    deploy:
      placement:
        constraints: [node.role == manager]
    networks:
      - webnet
networks:
  webnet:
```

本次其实只是添加了一个services，名称叫`visualizer`(所以位于`servies`的下一级)

新的东西还有

`volumes` 让可视化工具访问docker的套接字文件

`placement`使可视化工具只部署在集群管理器上

2. 还愣着干嘛   执行`docker stack deploy {stack_name}`更新栈配置啊

   > 对啦，从上面启动shell的env以来，我没特殊说明，就不要退出这个shell了
   >
   > 保证敲的命令，都能传输到集群管理器

3. 浏览器进入`{ip of myvm1:8080}`  或者`{ip of myvm2:8080}` 

> 8080配置文件有讲哦

> 你可以看到集群里面都有几个节点，都部署了什么
>
> 如果你觉得可视化工具欺骗了你。执行以下`docker stack ps {stack_name}`
>
> 可以看到更多内容哦

# 十二：添加redis依赖

现在我们尝试再往上面的集群添加一个redis服务

修改集群配置文件

`docker-compose.yml`

```
....
 redis:
    image: redis
    ports:
      - "6379:6379"
    volumes:
      - "/home/docker/data:/data"
    deploy:
      placement:
        constraints: [node.role == manager]
    command: redis-server --appendonly yes
    networks:
      - webnet
....
```

> 以上添加在services元素下面

如上文所示，我们添加了一个redis服务

> （其实也是一个镜像啦，这个是redis官方的docker镜像，名字叫做`redis`）

redis部署有几个规范，希望你能准守

1. redis要部署在集群管理器上，这样能使用相同的文件系统，确保它使用的是相同的主机

2. redis要能访问宿主机的`/home/docker/data`，使其映射到容器的`/data`上，使数据能永久存储

   不至于重启一次容器，数据就丢失了



部署文件加完后，我们还要新建文件夹`/home/docker/data`,确保这个路径是能够访问

最后一部呢？更新栈！

`docker stack deploy {stack_name}`

然后到浏览器`{ip of myvm1:4000}` 访问，看redis是不是正常可用了？

看看可视化工具，是不是有redis服务啦？

再看看栈的任务列表`docker stack ps {stajc_name}` 看看有没有redis服务

OK，完毕，就酱紫啦

   

   

   





# 十一：BUG

## 11.1 docker-machine ls 命令unknown

简而言之，就是执行这个命令时，显示的dockerMachine中docker版本是unknown

一开始并不知道是什么原因，于是只能去找日期，搜了一下，发现docker引擎在windows的日志位置在

`C:\Users\{user_name}\AppData\Local\Docker`   

再查了一下，发现在`C:\Users\10835\.docker\machine\machines\myvm1l`虚拟机文件夹里面的

`config.json`丢失了，不知道是不是傻子软件把它删了，这种情况，只能再创建虚拟机了







# 十二： docker命令

## 12.1 docker-machine 命令

1. `docker-machine ls`

   列出本机注册的docker机器，包括远程的，也包括虚拟机的，其实都一视同仁啦

   值得注意的是，如果当前的shell是在列出的机器里面

   则那个机器会标出`*`号(活动计算机)

2. `docker-machine start <machine-name>`

   开启远程机命令

   就是说，如果你的远程机关了，执行这个命令，可以远程开启

   这个非常666

   但是，你这个槽老头子坏得很，我不信VPS也可以开机

   实际上，我只是试了本地上面的虚拟机罢了
3. `docker-machine ssh` 登录安装docker的机子
   
   
   



   

   

   

## 12.2 docker-stack  命令

栈：是一组相关联的服务，它们共享依赖关系，同步协调和扩展

> 一组相关的服务，能让你想到什么，没错，就是集群，分布式服务器
>
> 同一个意思的不同说法罢了

栈相关的命令

1. `docker-stack ps {stack_name}`

   列出栈里面的任务

2. `docker stack deploy {stack_name}`

   部署一个栈，如果这个栈已存在，则更新。

   一般情况下，都是改动`docker-compose.yml`文件后，再执行这个，就可以生效

   

## 12.3 docker build命令

   顾名思义，这个命令是专门用于构建镜像用的

   搭配Dockerfile即可构建一个可用镜像

   命令格式为` docker build [OPTIONS] [PATH | URL |...]`

   可用参数有

   1. `--build-arg`  设置构建参数

      一般参数都定义在了Dockerfile文件里了

      如

      ```
      FROM openjdk:8-jdk-alpine
      VOLUME /tmp
      ARG JAR_FILE
      COPY ${JAR_FILE} app.jar
      ENTRYPOINT ["java","-jar","/app.jar"]
      ```

      如上面所示，声明构建参数就是`ARG JAR_FILE` 我们在使用`--build-arg` 构建镜像时，可以使用这个命令

      `docker build --build-args=target/*.jar`

      指定参数

   2. `--t ` 设置镜像的名称和标签

      应该是必须的，因为我们需要这个名称作为镜像的标识符，从而加载运行镜像

## 12.4 docker save命令（导出镜像文件）

命令:`docker save -o ./spring.tar springboot`

解释：`-o ./spring.tar` 指定输出文件

最后一个参数固定是镜像名称



## 12.5 docker run 命令

很简单，这是一个运行镜像的命令，该命令会生成一个容器

基本命令格式如下

` docker run [OPTIONS] IMAGE [COMMAND] [ARG...]`

`OPTIONS`也就是选项

可用的选项如下

` --publish xx:yy`  映射容器的端口yy到宿主机端口xx,简写可以为`-p`可以在run里面写多个，意味着这个镜像将暴露多个端口到宿主机上

`-e "key=value"` 设置环境变量的

`--net` 使用各种网络配置，不提供此参数以默认配置运行

默认情况下，容器的网络配置是通过虚拟的网卡实现的，需要配置发布主机映射到容器的端口，才能从主机访问到容器的端口，不过，也有配置，让容器使用主机所有的网络配置。

`--net=host  ` 使容器和主机使用相同的网络配置

` --privileged` 使用真正的特权模式，不是默认的残废特权模式，那个模式执行不了`sysctl`  会告诉你它是只读的



后面的`COMMAND`和`ARG...`是容器开始运行是要执行的命令，如果有指定，则dockerfile里面的CMD指令会被忽略

`COMMAND`和`ARG...`  可以执行多个命令

举例：

`docker run customer-elastic /bin/bash -c "sysctl -w vm.max_map_count=262144;elasticsearch"`

```
$ docker run -d --name elasticsearch --net somenetwork -p 9200:9200 -p 9300:9300 -e "discovery.type=single-node" elasticsearch:tag
```

> 启动elasticsearch时，设置discovery.type=single-node将使elastic以单机模式运行

举例

`docker run  --name consul --net=host  --mount source=consul,target=/consul/data consul agent -client   10.0.75.2 -server -ui  -bind  127.0.0.1 -bootstrap-expect=1`

运行consul镜像，单个节点且自身为leader

## 12.6 docker exec 命令

这个命令可以向docker的容器发一条命令让它执行

用法：

` docker exec [OPTIONS] CONTAINER COMMAND [ARG...]`

解释：

1. OPTIONS是可选的选项，有以下选项

   1. -i  打开标准输入流，可以往控制台录入命令了，即使连接没打开也是如此
   2. -t 分配一个假的tty窗口给你
   3. `-u` 后面带用户名，表示以这个用户名的权限运行这个命令，比如说`-u root `就是以root权限运行命令
   
2. CONTAINER  一般是容器ID

3. COMMAND 要执行的命令是？

4. [ARG...]  如果要执行的命令有参数，从这里面录入

示例：向容器发送一条命令，执行bash，也就是打开Shell，并且打开一个tty和标准输出流，以便控制。
命令：

`docker exec -ti d97ea7a4b9f2 /bin/bash`

## 12.7 docker start 命令

可以在命令的后面指定一个容器的id,以启动容器，比如说

docker start xxxx



# 十三： docker容器

概念：Docker容器是镜像运行的实例

容器里面的配置一般而言，是与宿主机一致的，也就是说，谁通过docker运行了这个镜像，所产生的容器的配置就和这个机器是一致的

> 目前用dockerfile生成镜像后运行容器，得到的结论就是这样的

这个特性可以用来适配应用的一些高配置要求，比如说elastic，就要求`vm.max_map_count`为262144。

少一个子都不行，所以推荐的做法是在宿主机上修改该值，然后再重新创建容器



   

# 十四： docker容器里面的数据

一般来说，容器产生的数据，都是放在容器的可写层里面，一旦容器被删除，则意味着数据将丢失

如果我们要持久化存储我们的数据，使容器即使删除了，也不会丢失数据。

那么我们就需要使用docker的两个功能

1. *volumes*
2. *bind mounts*

这两种方式的区别如下

1. *volumes*将数据存储在主机的文件系统里面，对于linux，是`/var/lib/docker/volumes/`

   非docker进程的程序不应该修改这部分的数据

   *volumes*由docker 创建和管理，可以显式的使用命令创建或者，或者docker可以在容器创建的时候创建它。

   *volumes*可以同时被其他容器使用，并且在没有容器使用时，也不会被自动移除，当然你可以使用

   `docker volume prune`来移除未使用的*volumes*

   *volumes*可以是命名的或者是匿名的，后者发生在未指定*volumes*的名称下，docker会随机给一个名称，保证是唯一的

2. **Bind mounts**  可以将数据存在在主机的任何位置上

   任何进程都可以修改它

   它依赖docker主机上面的特定目录结构。

   使用**Bind mounts** 时，主机上的特定目录结构会被装载到容器中。

   它的一个副作用是，容器里面的进程可以任意修改主机上面的敏感文件，可能会对进程造成影响

3. tmpfs mounts  

   这个是linux有的，这种方式不会将数据存储在磁盘里，而是内存中
   
4. named pipes: 这个用于容器和主机之间的通讯
   

   
## 14.1 *volumes*

*volumes*是docker官方推荐的一种持久化保持数据的方式，比起Bind mounts更好用，更安全。

它的特点有几个

1. 可以在多个容器之间共享*volumes*，可以是只读，也可以只读只写

2. 可以很方便的备份*volumes*，方便从一台docker主机将数据迁移到另一台docker主机

3. 如果没有显式的创建它，也就是，dockerfile没有指定，docker run也没有指定，那么在容器创建的时候就会创建一个匿名的*volumes*

   > 实际测试就是如此

   这会导致，你在dockerfile明明用了*volumes*指令，但是没有指定*volumes*名字，每次运行这个镜像生成的容器时，总会创建一个新的*volumes*，之前运行镜像所产生的数据，保存在另一个*volumes*了

   看起来就好像*volumes*没有起作用似得

4. 不用考虑*volumes*在主机系统里面具有什么特定的目录结构，让docker主机和容器之间耦合度降低

**volumes的几个命令**

1. 创建*volumes*

```
docker volume create  ${name}
```

2. 列出*volumes*的列表

   `docker volume ls`

3. 查看*volumes*的详情

   `docker volume inspect ${name}`

4. 移除volumes

   `docker volume rm my-vol`

**启动具有volumes的容器**（命令行指定）

```docker run -d --name devtest --mount source=myvol2,target=/app nginx:latest```

这个命令以分离模式启动了一个nginx的镜像，容器的名字叫做`devtest `,挂载了一个叫做`myvol2`的volumes

映射到容器的`/app`目录下

>  --mount 是一个基本通用的挂载命令参数，后面跟着键值对，用逗号分开。
>
>  可选的键值对有
>
>  1. type  默认应该是volumes，当然可以显式指定其他的，比如说`bind`  `tmpfs`
>  2. source 指定volumes的名称，这个volumes的名称不一定存在，如果不存在，在容器创建时，会自动创建，也可以连这个参数都不指定，那么docker会生成一个匿名的volumes
>  3. destination，映射到容器的哪个目录，可以使用别名，比如说·`dst`   `target`
>  4. readonly ,对这个容器是否只读
>
>  做一下
>
>  1. 首先用上面的命令创建一个容器
>  2. 进入这个容器的挂载目录`/app`下
>  3. 在`/app`目录下新建一个文件夹和文件
>  4. 停止并删除容器
>  5. 重新再用上面的命令创建容器
>  6. 进入容器的挂载目录下
>  7. 查看文件是否存在
>
>  预期结果：文件存在
>
>  证明结论：处于挂载目录下的文件，即使容器删除，数据也不会丢失，可以再次读取到

**启动具有volumes的容器**（dockerfile指定）

过程就不讲了，请参照上面的dockerfile解释

值得一说的是，dockerfile并不能指定volumes的名字，这意味着，如果你不在run命令指定volumes的名子，将会创建一个匿名volumes。

在dockerfile设计这个目的原因，据说是因为担心执行人执行run命令时，没有给volumes名字，这样起码数据还能保持在一个匿名volumes里面，不至于丢失。

反正就是酱紫






















