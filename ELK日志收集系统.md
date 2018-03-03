# ELK日志收集系统

ELK日志收集系统是一个分布式的，收集服务器上面的日志，并将其展示的一款服务器软件  
ELK由三个组件组成  
1. E表示Elasticsearch，是日志分析引擎
2. L表示Logstash 是日志搜集器，负责搜集服务器上面的日志，备选的其他搜集器还有Beats
3. K表示Kibana 就是负责web页面展示的一款组件，支持扩展
  建议安装顺序为Elasticsearch，kibana，Logstash

## 第一章：Elasticsearch快速入门

1. 下载Elasticsearch组件
2. 解压
  3.运行
  注意点1：
  Elasticsearch开启过程中，你可以看到这个日志重复出现`][INFO ][o.e.e.NodeEnvironment    ] [1xBUNqo]`
  告诉你，`1xBUNqo`就是自动生成的Elasticsearch节点名，嘛，你当然自定义一个节点名
  用这个命令启动`./elasticsearch -Ecluster.name=my_cluster_name -Enode.name=my_node_name`  
  注意点2：
  后台运行Elasticsearch后，可以键入`curl http://localhost:9200/` 查看运行结果  
  注意3：  
  Elasticsearch启动后，默认开启的是9200来提供服务  
  注意点4：
  开启Elasticsearch过程中，注意这一行
  `publish_address {127.0.0.1:9300}`它告诉你哪个ip地址和端口可以访问到Elasticsearch的服务。  
  后面还有一个bound address。没设置的情况不能从其他主机访问Elasticsearch的服务（经验之谈）  

### 问题

1. 运行时报`failed error='Cannot allocate memory' (errno=12)`,可能的原因是elasticsearch运行要分配的内存太大，java虚拟机扛不住  
  解决办法： 修改elasticsearch的`jvm.options`,将

  -Xms2g
  -Xmx2g
   修改成

  -Xms512m
  -Xmx512m

2. 运行时报org.elasticsearch.bootstrap.StartupException: java.lang.RuntimeException: can not run elasticsearch as root  
  意思就是说，不能用root用户来运行elasticsearch服务器。所以我们要为其创建一个用户和用户组。关于这个内容，请参考上面的linux部分

3. 切换了用户运行报：
  `Could not register mbeans java.security.AccessControlException: access denied ("javax.management.MBeanTrustPermission" "register")`
  这大概是因为你现在执行的时候用的用户对这个文件没有所有权，所以挂了，怎么办，诶，递归操作一下，把这个文件夹里面的文档的所有权全交给你当前登录用户的组就行了。  

4. 改了elasticsearch配置文件后报错了  
  1 . `max file descriptors [4096] for elasticsearch process is too low, increase to at least [65536]`  
  这个的意思是说用于elasticsearch的文件描述符过低，现在是4096，要求增加到65536.
  文件描述符的意思和含义请参照上面linux的知识
  2. `max virtual memory areas vm.max_map_count [65530] is too low, increase to at least [262144]`
    这个是虚拟内存的`vm.max_map_count [65530]`太低了，至少设置到262144
    用`sysctl`命令即可实现
### 元字段

每一个文档都有与之关联的元字段，例如_index

## 第二章：kibana快速入门

1. 解压
2. 修改config里面的kibana配置文件，将
  `elasticsearch.url: "http://192.168.21.254:9200/`修改为elasticsearch的服务器地址。
3. 如果远程主机要访问kibana的服务，请把`#server.host: "localhost"`此行取消注释，并设置为非回环地址
4. 运行bin里面的可执行文件  
5. 如果一切正常的话，访问kibana的ip地址+端口，可以看到kibana为你展现的前端页面，不过，在食用之前，你需要在页面设置一个模式。。


这么简单的吗？当然不是，接下来让我们加载一些简单的数据集。。。。。

### 2.1 加载一些简单的数据集

**前提，kibana和elasticelasticsearch能互通有无**

步骤1 ：请先下载这三个文件

[jsonshakespeare.json](https://download.elastic.co/demos/kibana/gettingstarted/shakespeare_6.0.json)

[accounts.zip](https://download.elastic.co/demos/kibana/gettingstarted/accounts.zip)

 [logs.jsonl.gz](https://download.elastic.co/demos/kibana/gettingstarted/logs.jsonl.gz)

步骤2： 解压其中的两个压缩文件，并把这三个文件放在elasticelasticsearch根目录下

步骤3： 为字段设置映射

在`http://localhost:5601/app/kibana#/dev_tools/console?load_from=https://www.elastic.co/guide/en/kibana/current/snippets/tutorial-load-dataset/1.json`

页面下有一个控制台，将以下代码复制到控制台并点击运行按钮

```
PUT /shakespeare
{
 "mappings": {
  "doc": {
   "properties": {
    "speaker": {"type": "keyword"},
    "play_name": {"type": "keyword"},
    "line_id": {"type": "integer"},
    "speech_number": {"type": "integer"}
   }
  }
 }
}
PUT /logstash-2015.05.18
{
  "mappings": {
    "log": {
      "properties": {
        "geo": {
          "properties": {
            "coordinates": {
              "type": "geo_point"
            }
          }
        }
      }
    }
  }
}
PUT /logstash-2015.05.19
{
  "mappings": {
    "log": {
      "properties": {
        "geo": {
          "properties": {
            "coordinates": {
              "type": "geo_point"
            }
          }
        }
      }
    }
  }
}
PUT /logstash-2015.05.20
{
  "mappings": {
    "log": {
      "properties": {
        "geo": {
          "properties": {
            "coordinates": {
              "type": "geo_point"
            }
          }
        }
      }
    }
  }
}
```

在本机的linux控制台运行这三个命令来加载数据

Ps：在执行这三条命令时，所处的目录必须有这三个文件

```
curl -H 'Content-Type: application/x-ndjson' -XPOST 'localhost:9200/bank/account/_bulk?pretty' --data-binary @accounts.json
curl -H 'Content-Type: application/x-ndjson' -XPOST 'localhost:9200/shakespeare/doc/_bulk?pretty' --data-binary @shakespeare_6.0.json
curl -H 'Content-Type: application/x-ndjson' -XPOST 'localhost:9200/_bulk?pretty' --data-binary @logs.jsonl
```

最后，在kibana的控制台执行该命令查看是否操作成功`GET /_cat/indices?v`

结果应该如下所示

```
health status index               pri rep docs.count docs.deleted store.size pri.store.size
yellow open   bank                  5   1       1000            0    418.2kb        418.2kb
yellow open   shakespeare           5   1     111396            0     17.6mb         17.6mb
yellow open   logstash-2015.05.18   5   1       4631            0     15.6mb         15.6mb
yellow open   logstash-2015.05.19   5   1       4624            0     15.7mb         15.7mb
yellow open   logstash-2015.05.20   5   1       4750            0     16.4mb  

```

解释：

为什么在加载数据之前，要为字段设置映射

答： 

Mapping divides the documents in the index into logical groups

并且指定字段的特征，比如字段的可搜索性，是否被标记,哪些字段包含数字，日期，地理位置信息。。。

## 第四章 定义你的索引模式

加载到Elasticsearch 的每一组数据都有索引模式

> 索引模式是一串带有可选通配符的字符串，可以匹配多个索引
>
> 例如，在之前的例子中，有一些索引名称就包含YYYY.MM.DD格式的日期，比如说`logstash-2015.05 *`





解释：为什么在加载数据之前要设置映射

设置映射是定义如何存储和索引文档以及所包含字段的过程。例如，用映射来定义

1. 哪些字符串字段应该被视为全文字段

2. 哪些字段包含数字，日期或者地理信息

3. 文档中所有字段的值是否应该被索引到_all字段中（_all字段是一个特殊的*catch-all* 字段，它将所有其他字段的值连接成一个大字符串，使用空格作分割符，但不存储）

4. 日期值的格式

5. 自定义规则来控制映射（动态添加的字段的映射）

   映射有两种类型

   1.元字段 Meta-fields，元字段的含义在Elasticsearch章节有讲到

   2 字段或者属性，

 第五章Logstash 快速入门

1. 解压  
2. 准备logstash.conf文件  
3. 运行 `bin/logstash -f logstash.conf` 命令  


## 第五章：Logstash日志存储管道

先不管`管道`是什么鬼，总之，它是Logstash存储日志的一个过程。这个过程需要二个必须的元素，还有一个可选的元素
1. input：这个其实是一个插件，负责从源收集，消费数据  
2. filter：这个可以根据你自己的指定来修改数据  
3. output：将数据写到目的地，一般是Elasticsearch  

### 5.1hello world

首先要看下你的logstash是否安装成功了，简单的话，执行这个命令  
`bin/logstash -e 'input { stdin { } } output { stdout {} }'`  
可以发现控制台先打印一串鬼画符，然后出现光标了。这时候，你输入什么，敲回车，就回显什么  
SUCCESS！这表示你的logstash已经能正常工作了  
解释：  
1. -e 可以让你以命令行的方式给logstash指定配置。  
2. 该示例的管道是从标准输入获取数据，并以结构化的数据输出到标准输出  

> PS： 小贴士，按ctrl-d 可以断开shell哦  
> ps:  如果执行以上命令抛了一个异常，说找不到这个文件`logstash-plain.log`请尝试用超级管理员执行命令  
### 5.2在logstash中分析日志

在现代社会中，日志的处理可不是上面的简单hello world,现实的日志的处理可能有更多的input,filter,output插件
在下面的教程中，将教你怎么从web项目中取出日志并送进管道，解析这些日志，用来创建指定的特点fields，并将这些已经解析好的数据传到Elasticsearch集群中。当然，你不用在从控制台输入配置了，多累啊，现在开启配置文件时代
步骤
1. 下载一个日志文件[https://download.elastic.co/demos/logstash/gettingstarted/logstash-tutorial.log.gz](https://download.elastic.co/demos/logstash/gettingstarted/logstash-tutorial.log.gz "下载")，把它解压  
2. 配置Filebeat以将日志每行的发送到Logstash
> 简单介绍下Filebeat，这个是一个轻量的，资源友好的工具，它从服务器上面搜集日志，并将日志转发给Logstash实例进行处理
>   占用资源极少，`Beats input plugin`最大限度的减少了logstash实例的资源需求。  
> logstash在安装时就默认集成了Beats input plugin了
> Beats input plugin 使logstash能从Elastic Beats框架接受事件  
> PS：对于 Filebeat在另一个章节讲述，这里不说了

### 5.3 logstash.conf文件格式

这个文件其实就是logstash的配置文件
作用：  
1. 指定要使用的插件以及插件的配置
2. 可以引用配置的事件字段，处理符合条件的事件。  
  注： 运行logstash可执行文件时 用 -f参数来指定配置文件。
  logstash配置文件的格式大概如下所示，这个配置主要是配置logstash的每一个插件。。   
  包括：输入插件，输出插件，筛选器插件

      input {
        ...
      }
      
      filter {
        ...
      }
      
      output {
        ...
      }

3. 输入插件的配置格式

        input {
          file {
            path => "/var/log/messages"
            type => "syslog"
          }
       
          file {
            path => "/var/log/apache/access.log"
            type => "apache"
          }
        }

RT 酱紫的话，就为这个输入插件配置了两个文件输入
每一个文件输入都有路径和类型信息。
4. 插件的值
  插件的值，指的就是类似于上文的`type => "apache"`的值，这种值的支持一下几种类型
5. 数组
6. Lists
7. Boolean 
8. Bytes 
9. Codec  
  .....   
  要看更多的话，请点击[plugin-value-types](https://www.elastic.co/guide/en/logstash/current/configuration-file-structure.html#plugin-value-types)


### 5.4使用Grok Filter Plugin来解析log日志

以上的例子中，虽然我们成功的将日志信息事件打印到控制台了但是格式太乱了，我们有必要整治这货，怎么办呢？
这时候就需要Grok Filter Plugin来帮忙过滤日志信息了  
先推销下Grok Filter Plugin，它的特色如下  

1. 能解析日志信息并从日志从创建指定的命名字段  

2. Grok Filter Plugin是logstash默认可用的几个插件之一  

3. 能使非结构化的日志信息解析成可结构化的，可查询的日志信息，听起来是不是很心动？  

4. Grok Filter Plugin通过传过来的日志数据中查找模式，你需要决定哪种模式用来识别日志数据，以便取出你感兴趣的数据  
   **怎么使用Grok Filter Plugin？**   

5. 分析日志数据，确定使用哪种模式  
   开始分析上次实验的日志，可以分析：开头的ip和括号的时间戳很容易识别，适合用`％{COMBINEDAPACHELOG}`模式。  
   如果你不知道怎么构建这个模式的话，可以使用`Grok Debugger.`可以免费安装使用哦  

6. 在logstash的配置文件中，为你的管道写一个过滤器。上面的例子中，我们用的配置文件是first-pipeline.conf。  
   这次我们仍然来改它，只需要在过滤器部分补上。

   filter {

```
   grok {
       match => { "message" => "%{COMBINEDAPACHELOG}"}
   }

```

   }
   保存你的改变，不用重启logstash，因为在上次启动时我们已经用了自动加载配置文件的方式来启动了  
   但是你还是需要强制让filebeat重头开始读取日志文件，要做到这一点    

7. 关掉上次运行的filebeat，删除filebeat的注册表文件:`sudo rm data/registry`,这个注册表文件保存了filebeat收集的每一个文件的状态  删除这个文件将强制让filebeat从头开始读取它所搜集的所有文件  

8. 接下来，重启filebeat吧.再等待几分钟等filebeat自动加载配置文件，就可以把新的日志结构体打印出来了，厉害吧。  
   然而我还是感觉像鬼画符  

### 5.5 Geoip过滤器插件为你的数据补充细节

除了分析日志以达到更好的搜索结果外，还有一个插件可以在已存在的数据中获取补充信息
比如说，从现有数据获取ip地址信息，再从地址信息获取地理位置，并将该位置信息添加到日志中  

1. 怎么做呢？把以下代码复制到`first-pipeline.conf`文件里，关于过滤器部分

   geoip {

   ```
     source => "clientip"

   ```

     }

这个过滤器要求你指定包含ip地址的原字段的名称，在这个例子中，clientip 字段就包含了ip地址信息。
由于过滤器是按顺序进行过滤的，所以你得确保geoip过滤器位于Grok过滤器的后面，先让Grok过滤器创建字段，然后geoip过滤器才能根据字段获取ip地址信息  

1. 删除注册文件，然后重启Filebeat 。等logstash重新加载配置文件完毕，你就可以看到控制台打印出来的日志信息了
2. 这一步什么都不用做，看下打印出来的日志json数据，里面有一个`city_name`或者`country_name`就OK了

### 5.6 将你的数据编入Elasticsearch

**前提：Elasticsearch必须，在9200端口上运行，处于运行状态**  
在上面的几步中，我们已经将web日志分割成几个特定的字段，但是仅仅只是把数据输出到控制台是不是有点不爽，嗯，接下来，我们就要把数据送到
Elasticsearch了
步骤  

1. 编辑你的logstahs的配置文件`first-pipeline.conf`，改动output的插件，使其如下所示  

   output {

   ```
   elasticsearch {
       hosts => [ "localhost:9200" ]
   }

   ```

   }
   logstash使用http协议和elasticsearch连接，在这个例子中，我们假设logstash和elasticsearch在同一个机器上运行，所以使用了localhost，实际上，localhost也可以换成其他主机名

2. 保存你的改变，关闭filebeat，删除filebeat的注册文件，重启filebeat（秘技：重复操作）

3. 最后尝试用浏览器访问以下地址`localhost:9200/logstash-$DATE/_search?pretty&q=response=200`  
   或者在本机上运行此命令`localhost:9200/logstash-$DATE/_search?pretty&q=response=200`

> 注意:如果执行以上命令报了此错误：`index_not_found_exception`  
> 请确保$DATE代表的是索引的实际名称。要查看可用的索引的列表  
> 执行`curl 'localhost:9200/_cat/indices?v`
> 在这个地址发来的反馈中，有一列是index，其中内容大致如此`logstash-2017.12.15`  
> 这样的话，查看日志的地址就要变成酱紫：`/logstash-2017.12.15/_search?pretty&q=response=200`  

解释：索引使用的日期格式是基于UTC的，使用$DATE可以让显示出来的日期格式变为`YYYY.MM.DD`这种格式的  
最后结果还是在控制台上看日志，但是这次是在elasticsearch上面看日志的，换了一种方式，会感觉比较厉害  
而且不仅如此，观察url，会发现它带了`q=response=200`,这大概是查找日志中response字段等于200的日志    
基于这种考虑，我们可以执行另一个命令  
`curl -XGET 'localhost:9200/logstash-$DATE/_search?pretty&q=geoip.city_name=Buffalo'`  结果应该是查找日志中`geoip.city_name=Buffalo`的日志  

> 如果你学会为`Filebeat`加载Kibana索引模式,你可以在kibana的web页面中看到日志信息，有关信息，请参阅filebeat的入门教程

### 5.7在logstash使用多个输入和输出插件

在实际开发中，你的日志来源通常有多个地方，要输出的目的地也可能有多个，这个时候，单个input和output肯定不能满足需求。
在这一节中。你将创建一个logstash的管道，该管道从推特简讯和filebeat的client获取输入，也就是说，有两个输入。  
然后将信息发送到Elasticsearch集群，并且还会写入到文件。也就是说，有两个输出。  
那么问题来了，在中国，你怎么能访问到推特简讯？？？WTF！

### 5.8 logstash 是如何工作的？

logstash处理事件有三个阶段：输入生成事件，过滤修改事件，输出将事件运往其他地方.

输入和输出插件支持编码和解码，你不必使用单独的过滤器来处理这些事情。

来简单介绍下输入，输出，过滤，编解码。

#### 5.8.1输入插件

输入插件将数据传到logstash，一些常见的输入插件是

1. **file** 从文件系统中读取文件，这个过程就像linux的命令`tail -0F`
2. **syslog**  监听众所周知的514端口来分析 syslog messages ，根据RFC3164格式进行解析
3. **redis**  略
4. **beats** 处理由Filebeat发送的事件。
5. log4j插件  将开一个章节来讲解log4j插件如何使用

过滤器

过滤器是logstash管道的中间处理设备，如果符合条件，你可以组合一些条件过滤器，对事件进行操作。

一些有用的过滤器如下

1. **grok** 分析和构造任意文本，可将非结构化的日志分析成可结构化和可查询的最佳方法，lagstash内置了120中模式，慢慢找吧骚年
2. **mutate** 在事件字段上执行一般转换，你可以在事件上重命名，移除，覆盖，修改字段
3. **drop**  完全的drop一个事件，比如说，debug事件
4. **clone** 复制一个事件，可能会添加或者修改字段
5. **geoip** 从ip地址获取地理信息添加到数据中

#### 5.8.2输出插件

输出是logstash管道的最后阶段，一个事件可以有多个输出，但是一旦所有输出处理完成，这个事件就结束了，一些常见的输出插件如下  

1. **elasticsearch** 如果你想将数据便捷，高效，且便于查询的保存起来，那么 Elasticsearch是你不错的选择
2. **file** 在磁盘中写入事件数据
3. **graphite** 将事件数据发送给graphite，这是一个开源的工具，可以存储和绘制指标
4. **statsd** 将事件数据发送给statsd

#### 5.8.3 编解码器

编解码器一般是作为输出或者输出过程的一部分，作为流过滤器而存在的

编解码器可以让你把消息从序列化过程中分离开来，常用的编解码器包括json，msgpack，plain (text)

- **json** 使用json格式编解码数据
- **multiline** 将多行文本事件（例如java的堆栈异常）合并到单个事件中


#### 5.8.4 log4j的输入插件

注：logstash的log4j插件其实已经过时了，现在推荐使用的是搭配filebeat来使用

使用方式

1. 配置你的日志输出方式为写入文件。

2. 安装并配置filebeat来收集这些日志并发送到logstash

3. 配置logstash来使用beats的输出插件

   ​

   其中1不解释，看log4j2的官方文档教你如何将服务器产生的日志写到文件中

   2的配置如下

​```yaml
filebeat:
  prospectors:
    -
      paths:
        - /var/log/your-app/app.*.log
      input_type: log
output:
  logstash:
    hosts: ["your-logstash-host:5000"]
   ```
3 的配置，请查看上文

### filebea

#### filebeat快速入门
1. 下载，解压，配置`filebeat.yml`

  配置代码如下
      filebeat.prospectors:
       - type: log
           paths:
          - /home/logstash-5.6.4/testData/logstash-tutorial.log
              output.logstash:
          hosts: ["localhost:5044"]    

要求清空原来`filebeat.yml`的内容，然后把以上代码复制上去。

解释： `- /home/logstash-5.6.4/testData/logstash-tutorial.log`要根据具体平台而定，指向实际的日志文件路径。  
2. 运行`sudo ./filebeat -e -c filebeat.yml -d "publish"` 
> 第一次运行时可能会出现两个问题  
> 1. 抛出错误，只能用所有权用户来登陆系统，执行命令  
> 2. 抛出`more then one namespace configured accessing 'output' (source:'filebeat.yml')`
> 3. 运行是正常的，但是没有出现预想中的端口连接5044失败信息，一派祥和，风平浪静。  
>   叫你Y的清空`filebeat.yml`的内容，你没清空吧，不清空也行，这个错误大概是说只能配置一个output的配置，把其他多余的配置注释掉即可  
>   运行过程中，你可能会看到filebeat在试图连接5044这个端口，直到logstash开启一个Beats plugin之前，这个端口都不会有任何反馈。
>   所以你看到任何关于这个端口连接失败的信息都是正常的。  
>   笔者记：我怎么没看到呢？ 诡异，真诡异  
>   这其实是不正常的，你应该用将filebeat安装目录的所有权归于root用户，然后再执行以上命令。我猜你之前应该用的是非root用户执行这条命令对吧。。。。
#### 为  Filebeat Input 配置logstash
接下来，我们要创建一个Logstash的pipeline配置，该pipeline可以从Beats input plugin接受来自Beats的事件
以下文本就是配置pipeline的基本格式
	
	input {
	}
	# filter {
	#
	# }
	output {
	}
以上代码其实没有实用性，因为输入输出都没有有效的选项定义
接下来这个代码就给了输入输出几个选项定义

	input {
	    beats {
	        port => "5044"
	    }
	}
	# filter {
	#
	# }
	output {
	    stdout { codec => rubydebug }
	}

解释：input部分是配置了一个beats的输入插件，output部分是配置了一个输出，将输出打印到stdut  
3. 把以上的代码，创建一个`first-pipeline.conf`文件，并复制进去。  
  然后执行： `bin/logstash -f config/first-pipeline.conf --config.test_and_exit &`  
  如果打出的日志没有带fail字样，恭喜你，配置是有效的。  顺便告诉你`--config.test_and_exit`是专门用来校验配置文件是否有效的  
  那么，真正运行Logstash的命令应该是下面那个
  `bin/logstash -f config/first-pipeline.conf --config.reload.automatic &`
> `--config.reload.automatic`是自动配置重新加载。改了配置文件，无需重启即可生效  
> 在启动过程中，你可能会看到多个关于`pipelinelines.yml 被忽略的警告`，你可以大胆的无视它，因为这个文件其实是用于在单个Logstash实例中运行多个管道，在刚才的操作中，你只是在运行一个管道而已。  
> 笔者注：这个警告我没碰上，真是太幸运了？

**最后，当filebeat和logstash工作正常的时候，你应该能看到一系列事件写入控制台，恭喜你，本章节完成...才怪，还要优化下**
	
	"@timestamp" => 2017-11-09T01:44:20.071Z,
	        "offset" => 325,
	      "@version" => "1",
	          "beat" => {
	            "name" => "My-MacBook-Pro.local",
	        "hostname" => "My-MacBook-Pro.local",
	         "version" => "6.0.0"

   ```