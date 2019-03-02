# Docker笔记

# 未完成点

1. `docker swarm init`  是什么意思

2. 第九章加粗点不理解

3. 

   

   

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

   

   

   

   


















