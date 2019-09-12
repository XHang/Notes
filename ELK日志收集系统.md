# ELK日志收集系统

ELK日志收集系统是一个分布式的搜索和数据分析引擎将其展示的一款服务器软件 
ELK由三个组件组成  

1. E表示Elasticsearch，是日志分析引擎
2. L表示Logstash 是日志搜集器，负责搜集服务器上面的日志，备选的其他搜集器还有Beats
3. K表示Kibana 就是负责web页面展示的一款组件，支持扩展
    建议安装顺序为Elasticsearch，kibana，Logstash



# 第一章：Elasticsearch快速入门

>  注意：以下文档基于elastic7.3.1 ，是笔者编写时，最新的elastic的版本了

首先，我们为什么要使用Elasticsearch

因为这货可以快速，近实时的存储，分析，搜索大数据。

可以用在

1. 大型的网上商城，可以用Elasticsearch来存储商品的目录和库存，并可以提供搜索和自动填充建议
2. 收集，汇总，分析日志。以便挖掘你感兴趣的信息
3. 分析/商业需求

> 查询和分析的效率在近实时，大致几秒的时间
>
> 当然要视数据量大小而定。
>
> 比如说：从数据的插入到可检索这段时间，大致是1s左右





## 1.1 安装和配置

1. 下载Elasticsearch组件

2. 解压

3. 运行

    > 运行会打印大量日志，这里挑选几个关键信息进行解释
    >
    > 1. `[INFO ][o.e.e.NodeEnvironment ] [1xBUNqo]` :`1xBUNqo`就是自动生成的Elasticsearch节点名
    >
    >    > ./elasticsearch -Ecluster.name=my_cluster_name -Enode.name=my_node_name  可以自定义节点
    >
    > 2. `publish_address {127.0.0.1:9z300}`  哪个ip地址和端口可以访问到Elasticsearch的服务
    >
    > 3. `bound address`   没设置的情况不能从其他主机访问Elasticsearch的服务

    > 用./elasticsearch >> 1.log & 可以将启动过程的日志存在1.log文件下，不用再输出到控制台了

    > 如果你是用docker开启的，那么。。。启动命令是这个
    >
    > > $ docker run -d --name elasticsearch --net somenetwork -p 9200:9200 -p 9300:9300 -e "discovery.type=single-node" elasticsearch:7.3.1

4. 启动完后，可以键入`curl http://localhost:9200/` 查看运行结果 

**几个小问题**

1. 启动过程中报了这个错误

   ```
   [1]: the default discovery settings are unsuitable for production use; at least one of [discovery.seed_hosts, discovery.seed_providers, cluster.initial_master_nodes] must be configured
   ```

   这个问题大致是讲你的es正在用生产模式运行，而生产模式必须配置几个配置项

   所以解决办法是

   1. 不要用生产模式运行，用开发模式运行，只需要在elastic的配置文件加上这个配置项

      ```
      discovery.type : single-node
      ```

   2. 它说加，那就加,同样在你的elastic加上这段配置

      ```
      cluster.initial_master_nodes: ["node-1"]
      ```

**配置文件解释**

elastic的配置文件就放在config目录下一个叫`elasticsearch.yml`的文件

里面可以配置的有

1. `discovery.type : single-node`  不要校验集群配置，以单机运行
2. `path.data : /elastic-data`  数据存放的目录
3. 

## 1.2 概念

1. doc 文档：是elastic里面最小的数据单元，类比于数据库的一条记录

   在elastic通常是json数据，而json里面的字段就相当于数据库的字段

2. index 索引，这里的索引跟你认识的索引可能还不太一样。

   这里的索引类似于数据库的表，它里面存放了很多文档。

   这些文档都是相同或者类似的。

   比如说商品索引，订单索引。

   索引的名词必须是小写的，不能以下划线开头，且不能包含逗号

3. type(过时)，在elastic7.0被去除，type的出现，是为了解决同一索引下的某些文档，某个字段是它独有的。

   假如说，有一个商品的index，这个index里面既有生鲜商品的数据，也有电器的数据。

   而电器，就有一个特别的字段：保修期。为了使这个字段是电器这类商品所独有的，所以搞了一个type。

   所以elastic的数据组成其实是：index->type->doc

   type其实才应该算是类比数据库的表。

   不过话说回来，type已经凉凉了，所以老大只能由index来当了

4. shard 碎片，指的是索引数据的分块。

   数据为什么要分块，因为一整块数据太大的话，单台服务器存不下，所以要分块，存在不同的服务器里面。

   那么也就是，一个索引可能会分成几个数据块，存在不同的服务器中，每一块，就称为一块shard。

   primary shard数量在创建索引的时候，就已经固定了，后面是无法更改的，即使再增加服务器，也不会增加shard的数量

   但是这样的话增加服务器就无法提升性能，那有个毛线用？

   其实还可以增加replica的数量来提升性能的，毕竟replica是也是可以承担请求的

5. replica 副本，其实是另一个shard ，只不过里面存的数据是另一块shard的复制本。这样可以防止shard数据丢失，即使丢失了也会恢复。另外，也提升了shard里面数据读取的吞吐量。

   因为replica 作为一个特别的shard ，也是可以对外提供服务的，这样就可以把请求分担给replica 那边，降低shard 并发量
   
   一般来说，一个index有5个shard ，而一个shard ，有一个replica 
   
   这样就需要两个es节点，而这是最小高可用配置。
   
6. id ：用来标识唯一doc的一个字段，在插入新doc时，可以手动指定，也可以让es自动生成

   手动指定的情况：迁移旧系统的数据到es中，需要保留旧系统的数据完整性，此时就需要手动指定id

   自动生成的情况：从头开发一个新组件，数据直接插入到es中，可以使用es自动生成的id.

   es自动生成的id使用GUID 算法，可以保证在分布式系统下不会冲突

   关于如何手动和自动让es处理ID

   请参阅下面的**1.2.3 往索引放东西**

## 1.2: 索引

索引是一类相似文档的集合，类比于数据库。

比如说，有客户数据的索引，有商品目录的索引。等等等等。

索引由名称（必须全都是小写）来标识。

在对文档执行索引，搜索，更新和删除操作时也会用到这个索引名称

### 1.2.1 建立索引

创建索引的方法很简单,只需要执行这个put命令

`curl -X PUT "localhost:9200/customer?pretty`

or  `PUT /customer`

没错,这样就创建了一个名为customer的索引。后面的pretty的意思是漂亮的打印json格式的响应（如果有的话）

```
{
  "acknowledged" : true,
  "shards_acknowledged" : true,
  "index" : "customer"
}
```

上面的，就是响应数据了

如果你想指定新索引的shard和replica

则请求如下

```
{
	"settings": {
		"number_of_shards": 5,
		"number_of_replicas": 1
	}
}
```

解释，该索引需要分成5个shard，每个shard有一个replica





### 1.2.2 显示当前所有的索引

`GET /_cat/indices?v`  or  `curl -X GET "localhost:9200/_cat/indices?v"`

前者可以在`Kibana`的控制台执行，后者调用CURL命令直接发起http请求

调用的结果大概是这样的

| health | status | index       | uuid                   | pri  | rep  | docs.count | docs.deleted | store.size | pri.store.size |
| ------ | ------ | ----------- | ---------------------- | ---- | ---- | ---------- | ------------ | ---------- | -------------- |
| yellow | open   | customer    | 9XO49lV_Rom9K1pslti4Rw | 5    | 1    | 0          | 0            | 1.1kb      | 1.1kb          |
| yellow | open   | shakespeare | fEWOYruRTRqI-DnZa7oDA  | 5    | 1    | 111396     | 0            | 22.3mb     | 22.3mb         |
| yellow | open   | bank        | j4gWS47cSh29dd8Jm2Fm5g | 5    | 1    | 1000       | 0            | 474.2kb    | 474.2kb        |

> 为了方便查看，做成表格形式的，实际上没有表格格式

这个结果告诉我们

1. 我们现在有三个索引，分别是`customer`,`shakespeare`, `bank`
2. `customer`索引有5个主分片`primary shards`和1个副本`replica ` 

> 主分片和副本的值默认情况就是这样的

3. docs.count 它包括多少个文档

4. 它的健康状态是黄色

   > 之所以健康状态是黄色：默认情况下，elasticsearch会为索引创建一个副本`replica ` 
   >
   > 这个副本原本是应该分配到另一个节点上的，但是我们现在只有一个节点在运行。所以这个副本是分配不了的。如果此时再有一个节点加入群集，使得这个副本能分配，那么健康状态就会变成绿色了
   
5. store.size 索引占了多大的空间

6. pri.store.size 索引中主要的shard块占据了多大的空间

7. docs.deleted 索引删除了几个文档?

### 1.2.3 往索引放东西

我们要把一些简单的客户文档编入`customer`索引中,这个客户文档的ID为1.

放置的命令是

`PUT /customer/_doc/1?pretty`

```
{  "name": "John Doe"}
```



预期得到的响应数据是

```
{
  "_index" : "customer",
  "_type" : "_doc",
  "_id" : "1",
  "_version" : 1,
  "result" : "created",
  "_shards" : {
    "total" : 2,
    "successful" : 1,
    "failed" : 0
  },
  "_seq_no" : 0,
  "_primary_term" : 1
}
```

从上面的响应数据，可以知道

1. ` "result" : "created" &&  "successful" : 1` 恭喜你，放置成功了

2. `  "_id" : "1"`  这个其实就是我们请求URL的那个`1`，这个是一个restful格式的请求

3. "_version" : 1 表示文档的修改次数，目前是新创建，当然就是1了

4. _shards.total 总共本来要写入两个shard

5. _shards.successful 但是只成功了一个，成功的是主shard。

   没写入的是replica

   

值得注意的是：`elasticsearch`在将文档编入索引之前，不一定需要先创建索引。

像上面的例子，如果本身不存在`customer`索引的话，执行上面的那个文档编入命令时会自动创建`customer`索引

另外，如果在编入文档之前，你不想指定ID，可以，由elasticsearch帮你手动指定也行

但是命令得这么写

```
POST /customer/_doc?pretty
{
  "name": "Jane Doe"
}
```

你每次执行这个命令时，都是在编入一个新的文档。

返回的响应数据跟上面并没有什么不同。唯一不同的是。。。

唔，id部分长这样子`"_id": "mdRWYmMBhPe_HfySEFdr"`

毕竟是elasticsearch自动生成的，就别奢望能有多好了。

> PS：为什么要用post动词而不是put动词呢？
>
> 这涉及到一个RFC的规范问题，PUT命令一般指的是服务端存入(更新)客户端的任何消息。不会多加什么处理。
>
> POST就比较复杂了，客户端发来的数据，服务端还要多加处理一下，比如这次，服务端自动生成ID了。
>
> 使用的场景比较多，容易造成滥用就是了。

**特别说明一下：**

新增文档和全量替换文档restAPI，甚至请求参数都是一样，而记录的有无决定了操作是更新，还是插入。

但是如果我一定要执行的是插入呢？

可以用这个restAPI

`POST /index/_create/_id`  或者`PUT /index/_doc/_id?op_type=create`

请求体和上面的示例一模一样

所不同的是，如果要插入的记录id已存在，会无法插入，报

`[2]: version conflict, document already exists (current version [1])`



### 1.2.4 检索文档

`GET /customer/_doc/1?pretty`

意思就是检索`customer`索引下ID为`1`的文档，而且要漂亮的把响应打出来。

这就是你想要的漂亮数据：

```
{
  "_index": "customer",
  "_type": "_doc",
  "_id": "1",
  "_version": 1,
  "found": true,
  "_source": {
    "name": "John Doe"
  }
}
```

其中能提取的信息如下

1. ` "found": true`代表你成功检索到了文档
2. ` "_id": "1"`你要检索ID为1的文档，不是吗？这个文档的ID就是1
3. `_source":xxx`这就是上一步往索引放入的东西，它是一个完整的json文档

### 1.2.5 删除索引

在`elasticsearch`中，你也可以做到删库跑人，只要你掌握了这个技能。。笑~

删除索引很简单，只需要

`DELETE /customer?pretty`

预期响应的数据是

```
{
  "acknowledged": true
}
```

这样，就删除了一个名为`customer`的索引了。

怎么验证是否成功删除,看看当前所有的索引吧，

### 1.2.6 小小的总结

在这里，你应该掌握了索引的增删查了吧。

那么，仔细回顾下相关的请求的URL

```
PUT /customer
PUT /customer/_doc/1
{
  "name": "John Doe"
}
GET /customer/_doc/1
DELETE /customer
```

会发现一个规律，实际上这也是elasticsearch访问数据的模式

`<REST Verb> /<Index>/<Type>/<ID>`

翻译下？

REST动词/索引名/类型/ID

这种访问模式在elasticsearch所有的api中都非常普遍，要尽量掌握

顺带一提，REST动词指的是

1. GET  请求获取数据用的
2. PUT 放置数据用的
3. DELETE 删除数据用的
4. POST

### 1.2.7 修改数据

这里说的修改数据可不是指修改索引，而是指修改索引中的文档。

修改命令和往索引里面放东西的命令(索引单个文档)是一样的

原本我们想往索引编入一个文档，是这么编入的

```
PUT /customer/_doc/1?pretty
{
  "name": "John Doe"
}
```

现在想把这个文档的数据修改一下，比如说，人名改成hahaha

命令如下

```
PUT /customer/_doc/1?pretty
{
  "name": "hahaha"
}
```

其实与其说是修改命令，倒不如说是替换，把旧数据替换成新数据，不就是修改了嘛。

所以，**上面的修改数据，实际上是用替换方式**

> es 内部对于这种替换式的更新数据，是先把目标doc置为delete，但并没有物理删除，然后再新增一条数据，id就是被物理删除的那个

此外，执行这个替换的命令之后，响应数据也是会变的

```
{
  "_index": "customer",
  "_type": "_doc",
  "_id": "1",
  "_version": 2,
  "result": "updated",
  "_shards": {
    "total": 2,
    "successful": 1,
    "failed": 0
  },
  "_seq_no": 1,
  "_primary_term": 1
}
```

其中`"result": "updated"`表示我们update了一个文档，`"_version": 2`进行了一次替换操作，版本升级，变成2

更新命令还可以这么写

```
POST /test/_update/1?pretty
{
  "doc":{"sex":"程序"}
}

```

这个命令做了一件事，就是修改了文档里面sex的字段值

这次更新数据就只是局部更新而已，不需要全量替换

> 这种修改数据的方式被称为partial update
>
> 相较于全量替换
>
> 1. 它减少了网络请求所花费的实际
> 2. 减少了查询和修改的时间间隔，有效的降低了并发冲突
>
> 其实这些部分替换的方式·，在es内部，也是先load出所有数据，再全量替换的。

用上面的PUT命令也可以实现哦。



最后，你还可以**用脚本来修改数据**

1. 直接在请求体塞脚本代码

```
POST /customer/_doc/1/_update?pretty
{
  "script" : "ctx._source.age += 5"
}
或者
"script": {
    "source": "ctx._source.likes++"
 }
```

作用是将ID为1的json文档中的age属性的加上5

其中，命令中的`ctx._source`指的是即将要更新的源文档。

> 前提是要有age这个字段

2. 预先定义脚本，然后使用脚本的标识符，更新数据

   步骤

   1. 首先要写一个脚本文件，然后放在es安装目录下/config/scripts目录下
   2. 

   

以上的命令都是只更新一个文档用的。

事实上，还有一种命令可以给定一个条件，更新多个文档。类似于sql语句的`update-wehere`语句

### 1.2.8 使用条件批量更新文档

可以使用`_update_by_query  `api   来对索引的每一个文档进行先查询，再更新，而不更改整个源文件

类似于sql语句的`update-wehere`语句

使用`_update_by_query`要小心版本冲突

因为`_update_by_query`在启动索引时会拿一份索引的拷贝，并使用内部版本来控制索引。

所以如果在拿索引拷贝到处理索引请求的之间，文档发生变化，将会导致版本冲突。

当且仅当版本号一致时，文档更新并且版本号会叠加。

> 不明白的地方：启动索引时是指什么时候
>
> 文档发生变化是指什么变化，修改，删除都算吗？
>
> 现在的情况下是，我单独更新文档后，再使用上面的那个命令，并没有遇到版本冲突。。
>
> 关于这个问题，可能还需要考虑一下。(莫名想起了乐观锁)

> 内部版本不支持将0作为版本号，所以版本号为0的文档无法使用`_update_by_query`进行更新，即使使用了，也会挂

> 一旦更新和查询的过程出现失败，则整个`_update_by_query`将会终止，但是已经执行的更新是生效的.
>
> 也就是说，`_update_by_query`这个过程不会回滚，只会中止。

那么第一步，要知道怎么查询

比如这个命令:`POST customer/_update_by_query?conflicts=proceed`

响应数据大致如此

```
{
  "took": 534,
  "timed_out": false,
  "total": 3,
  "updated": 3,
  "deleted": 0,
  "batches": 1,
  "version_conflicts": 0,
  "noops": 0,
  "retries": {
    "bulk": 0,
    "search": 0
  },
  "throttled_millis": 0,
  "requests_per_second": -1,
  "throttled_until_millis": 0,
  "failures": []
}
```

其实这个命令只是简单的检测有没有导致`_update_by_query`的版本冲突

### 1.2.9 删除文档

命令：`DELETE /customer/_doc/2?pretty`

删除`customer`索引中ID为2的文档

当然删除也可以先查询指定的记录后删除。

不过使用ID删除，肯定是效率最高的。

**内部删除的机制**

es对于删除操作并没有真正把目标从磁盘中物理删除，

而是将删除的目标记录置为delete，外部查是查不到的。

当es发现可用磁盘空间不够时，。就会来一波垃圾清除，把这些delete的记录全都物理删除。



## 1.3 映射

为您的数据设置恰当的映射是非常有必要的，这可以让你的数据变得结构化，可视化，而不必看那么乱糟糟的数据结构。

虽然Elastic会自动对你的数据进行自动映射，但有谁比你更了解数据的组成结构呢？

总的来说，映射能做的事有

1. 哪些字符串字段应该被看做是全局字段

2. 哪些字段包含地理，数字，日期信息？

3. 是否应该将文档所有字段的值编入`_all`字段中？

   > 这个_all字段在elasticsearch 6.0.0 已经被弃用了

4. 日期的格式化形式

### 1.3.1 映射的类型

要创建一个映射，先得确定你要创建什么类型的映射

映射分为两类

1. `Meat_Field`类型  就是元字段类型，元字段就是elastic里面内置的字段，比如说`_index`,`_type`,`_id`

2. Fields 或者properties 映射

   这个就是文档中的字段映射了.

   Elastic支持许多数据类型，可以在映射中为字段指定数据类型，主要有

   字符串类：text和keyWord

   > text :表示这个字段是一个全文类型的字段，比如说文章，或者商品的描述之类的,这种字段的数据被编入索引之前，通常会被分析器分析里面的术语列表，也就是还会继续细分字段。
>
   > 特别的，由于这种字段的数据一般比较大，所以不用它来聚合和排序。
>
   > 当然，某些重要的数据可能还是会用来聚合的，这又是一个值得大书特书的教程了。。
>

   数据类型：`long`, `integer`, `short`, `byte`, `double`, `float`, `half_float`, `scaled_float`

   日期类型：`date`

   布尔数据类型：`boolean`

   二进制数据类型`binary`

   范围式的数据类型：`integer_range`, `float_range`, `long_range`, `double_range`, `date_range`

   以及还有复杂的数据类型，如对象，数组

   GEO即地理数据类型：` Geo-point` 经纬度

   还有特殊的数据类型，比如说IP之类的。。

所以，这章，我们要学习如何手动创建映射。

**需要注意的点** 

1. 小心映射爆炸，BOOB~,就是定义的映射太多了，服务器撑不住了。

   你需要限制一下映射的数量

   `index.mapping.total_fields.limit` 指定索引中最大映射的值

   `index.mapping.depth.limit` 一个字段的最大深度，如果这个字段是在根对象级别定义的，则深度为1，如果  这个字段有一个对象映射，则深度为2.

   TODO 雾里看花，不是很懂

   `index.mapping.nested_fields.limit`  索引中嵌套字段的最大数量

   TODO 不理解

2. 更新索引的映射是不可能的，tan 90 只有删除映射才能过日子这样子。

   因为修改映射也就意味着已经索引的文档全部失效。

   所以，创建映射的时候要三思而后行

### 1.3.2 创建映射的小栗子

```
PUT my_index    							//创建一个索引-名字叫my_index
{
  "mappings": {
    "doc": { 								//添加一个名叫doc映射类型
      "properties": { 						//指定映射类型为properties
        "title":    { "type": "text"  }, 	//指定title的字段的数据类型为text
        "name":     { "type": "text"  }, 	//指定name的字段的数据类型为text
        "age":      { "type": "integer" },  //指定age的字段的数据类型为integer
        "created":  {
          "type":   "date", 
          "format": "strict_date_optional_time||epoch_millis"
        }
      }
    }
  }
}
```

这段代码完全可以复制到kibana网页的控制台上，执行之，就会将请求发送到Elastic

### 1.3.3  查找索引设置的映射

**得到一个索引里面所有的字段映射**

简单的一个查询URL就可以

`GET /{index}/_mapping/`

这样可以获取这个索引所有的映射

得到的东西如下·

```
{
  "bank" : {
    "mappings" : {
      "properties" : {
        "account_number" : {
          "type" : "long"
        },
        "address" : {
          "type" : "text",
          "fields" : {
            "keyword" : {
              "type" : "keyword",
              "ignore_above" : 256
            }
          }
        },
        "state" : {
          "type" : "text",
          "fields" : {
            "keyword" : {
              "type" : "keyword",
              "ignore_above" : 256
            }
          }
        }
      }
    }
  }
}
```

**得到该索引某字段的映射**

请求的URL变成了酱紫了

`GET /bank/_mapping/field/state`

这就是请求bank索引里面state字段设置的映射，返回的结果如下

```
{
  "bank" : {
    "mappings" : {
      "state" : {
        "full_name" : "state",
        "mapping" : {
          "state" : {
            "type" : "text",
            "fields" : {
              "keyword" : {
                "type" : "keyword",
                "ignore_above" : 256
              }
            }
          }
        }
      }
    }
  }
}
```









## 1.4 批量处理

除了能够索引(查找)，更新，和删除单个文件档外.

`elasticsearch`还提供了使用`_bulk`api批量执行上述任何操作的功能。

该功能十分有效，可以尽可能多的完成任务，避免过多的网络往返。

顾名思义，`_bulk`api，它的请求端点就是`_bulk`

举例说明

1. 批量更新或者插入指定的文档

   ```
   POST /customer/_doc/_bulk?pretty
   {"index":{"_id":"1"}}
   {"name": "John fdgfds" }
   {"index":{"_id":"2"}}
   {"name": "Jane Doevbcvbcv" }
   ```

   以上，就是将`customer`索引里面,ID为1和2的文档更新。

   ID为1的文档更新为`{"name": "John fdgfds" }`

   ID为2的文档更新为`{"name": "Jane Doevbcvbcv" }`

   如果并不存在ID为2的文档，将创建。

2. 更新文档且删除文档的操作

   ```
   POST /customer/_doc/_bulk?pretty
   {"update":{"_id":"1"}}
   {"doc": { "name": "John Doe becomes Jane Doe" } }
   {"delete":{"_id":"2"}}
   ```

   RT，这次呢，是将ID为1的文档更新为：`{ "name": "John Doe becomes Jane Doe" }`

   然后呢，就是将ID为2的文档删除。

   > 删除操作不需要加对应的源文档哦，只要传个标识过去就行了。

PS:唔，就是这个API如果批量操作中，有一个操作失败了，不会中止，而是继续执行。

最后返回的响应数据会为每一个操作(按照发送的顺序)提供一个状态，我们可以根据这个状态来判断特定操作是否失败了。

## 1.5:批量导入

其实就是将符合规范的json文档导入到索引里面。

点击这里下载示例文档

[accounts.json](https://github.com/elastic/elasticsearch/blob/master/docs/src/test/resources/accounts.json?raw=true)

下载到某个文件夹。

然后使用批量命令批量导入文档

```
curl -H "Content-Type: application/json" -XPOST "localhost:9200/bank/_doc/_bulk?pretty&refresh" --data-binary "@accounts.json"
```

你可以看下下载下来的json文档，里面的格式是不是跟第四节`批量更新或者插入指定的文档`的示例数据一模一样

得符合这种格式的文档，才能被`elasticsearch`识别。





## 1.6: API

以下APi均适用于kibana的控制台。

### 1.6.1 索引API

### 1.6.2 搜索API

`elasticsearch`就是为搜索而生的，没有搜索APi，怎么能行呢？

这一节，我们将使用搜索API，把想要的数据搜索出来。

使用搜索API，有两种方式

#### 1.6.2.1 将搜索条件加载请求头，也就是URL上

将搜索参数附加在REST URL的参数上，就是喜闻乐见的键值对参数啦

eg：`GET /bank/_search?q=*&sort=account_number:asc&pretty`

作用是

1. 查询`bank`索引下面的所有的文档，根据参数`q=*`
2. 根据文档里面的`account_number`字段进行升序(从小到大)排序
3. 漂亮的打印响应数据，根据参数`pretty`

```
{
  "took": 15,
  "timed_out": false,
  "_shards": {
    "total": 5,
    "successful": 5,
    "skipped": 0,
    "failed": 0
  },
  "hits": {
    "total": 1000,
    "max_score": null,
    "hits": [
      {
        "_index": "bank",
        "_type": "_doc",
        "_id": "0",
        "_score": null,
        "_source": {
          "xxx": 0,
          "xxx": 16623
        },
        "sort": [
          0
        ]
      },
....
```

响应数据的字段部分解释如下

1. `took` elasticsearch执行搜索所花费的时间，单位是毫秒

2. timed_out 告诉我们搜索是否超时了

3. 告诉我们搜索了多少碎片？以及搜索碎片成功/失败的次数

   对于某索引的搜索请求，会打到该索引所有的主/备shard上

   所以这次查询查了5个shard

4. `hits` 查询击中的记录详情

   1. total 击中数

   2. max_score：score是一个文档对于搜索，它是一个相关性的匹配，score值越大，表明该文档对于此搜索越相关。

      而max_score，自然就是这次查询，最相关的那个文档的score了。
      
   3. hits 查询结果的文档数据
   
5. _source  就是新增文档时传入的json串，再原封不动的返回回来
   
      
   
      
   
      

#### 1.6.2.2 将搜索参数放在请求体

也就是把请求参数构造成json，放在请求体中，现在基本用的都是这种方式

示例

请求头

```
GET / bank / _search
```

请求体（复杂）

```
{
	"query": {
		"match": {
			"host": "www.elastic.co"
		}
	}
	, "size": 20
  ,"sort": [
    {
      "bytes": {
        "order": "desc"
      }
    }
  ]
}
```

请求体里面的数据也被称为查询DSL（domain-specific language）

稍微介绍下几个用法

##### 1.6.2.2.1 查询所有数据

DSL示例

```
{
	"query": {
		"match_all": {}
	},
	"size": 1
}
```

query 里面是查询的定义

1. match_all 在该索引的目录下查询所有数据

2. match 和`match_all `有所不同，这个针对特定条件的查询

   就如同上面的，匹配host是`www.elastic.co`的记录

3. `bool` 使用并/或逻辑将几个小条件组合起来

##### 1.6.2.2.1 只用一个条件简单查询

DSL举例

```
GET /kibana_sample_data_logs/_search
{
	"query": {
		"match": {
			"host": "cdn.elastic-elastic-elastic.org"
		}
	}
}
```





##### 1.6.2.2.1 使用should或组合逻辑条件

DSL示例

   ```
   {
   	"query": {
   		"bool": {
   			"should": [{
   				"match": {
   					"host": "cdn.elastic-elastic-elastic.org"
   				}
   			}, {
   				"match": {
   					"ip": "175.165.156.162"
   				}
   			}]
   		}
   	}
   }
   ```

解释： 查询字段host的值为xxx或ip的值为xxxx的字段

##### 1.6.2.2.2 使用must并组合逻辑条件

DSL示例

```
{
	"query": {
		"bool": {
			"should": [{
				"match": {
					"host": "cdn.elastic-elastic-elastic.org"
				}
			}, {
				"match": {
					"ip": "175.165.156.162"
				}
			}]
		}
	}
}
```

解释：查询字段host的值为xxx且ip的值为xxxx的字段

> 条件不仅局限于must，还有`must_not`  非条件判断，使用方式和match一样

##### 1.6.2.2.2 使用`filter`,对值进行范围过滤

DSL示例

```
  {
   	"query": {
   		"bool": {
   			"should": [{
   				"match": {
   					"host": "cdn.elastic-elastic-elastic.org"
   				}
   			}
   			],"filter": {"range": {
   			  "bytes": {
   			    "gte": 6981,
   			    "lte": 10000
   			  }
   			}}
   		}
   	}
   }
```

解释： 查询字段host的值为xxx并且bytes的值介于xx和xx之间

##### 1.6.2.2.2 对查询结果进行排序

DSL 示例

```
GET /bank/_search/
{
	"query": {
	  "match_all": {}
	}
	, "size": 20
  ,"sort": [
    {
      "account_number": {
        "order": "desc"
      }
    }
  ]
}
```

解释：查询所有数据，并根据account_number字段进行降序排序

##### 1.6.2.2.2 对查询结果进行分页

查询DSL示例

```
GET /bank/_search/
{
	"query": {
	  "match_all": {}
	}, 
	"size": 20,
	"from": 2
}
```

解释：查询所有数据，但是提取前两行后面的数据，并且提取20条数据

##### 1.6.2.2.2 指定只查询几个字段

查询DSL举例

```
GET /bank/_search/
{
	"query": {
	  "match_all": {}
	}, 
	"_source": ["account_number","balance"]
}
```

解释：查询所有数据，但是只提取account_number和balance字段

##### 1.6.2.2.2 全文检索

查询DSL

```
GET /test/_search
{
  "query": {
    "match": {
      "content": "I game"
    }
  }
}
```

查询结果（仅显示hits.hits）

```
 {
        "_index" : "test",
        "_type" : "_doc",
        "_id" : "3",
        "_score" : 2.8399353,
        "_source" : {
          "title" : "myGame",
          "content" : "I love psp game,sony big da fa hao "
        }
      },
      {
        "_index" : "test",
        "_type" : "_doc",
        "_id" : "2",
        "_score" : 1.0511023,
        "_source" : {
          "title" : "mystory",
          "content" : "I love xxxx ,but her Don't love me"
        }
```

解释：这个索引test里面的content是一些句子

比如说

1. This is My Test content
2. I love xxxx ,but her Don't love me
3. I love psp game,sony big da fa hao 

elastic 会对这些句子进行分词，我们搜索的时候，也用空格给单词分词，当执行搜索时，elastic就会进行全文检索。

这个是默认行为，我们在查询的时候不用做指明说该查询是全文检索，不需要的

##### 1.6.2.2.2 短句搜索

这个刚好和全文搜索相反

全文搜索是将搜索词拆解，然后查询倒排索引找对应单词

而短句搜索则是不拆解搜索词，拿整个搜索词去匹配目标字段

DSL 示例

```
GET /test/_search
{
  "query": {
    "match_phrase": {
      "content": "I love"
    }
  }
}
```

响应示例

```
 {
        "_index" : "test",
        "_type" : "_doc",
        "_id" : "2",
        "_score" : 2.1022046,
        "_source" : {
          "title" : "mystory",
          "content" : "I love xxxx ,but her Don't love me"
        }
      },
      {
        "_index" : "test",
        "_type" : "_doc",
        "_id" : "3",
        "_score" : 2.0752437,
        "_source" : {
          "title" : "myGame",
          "content" : "I love psp game,sony big da fa hao "
        }
```

##### 1.6.2.2.2 高亮搜索

就是在搜索结果里面，对匹配的数据进行突出处理。

不会影响到搜索条件

DSL举例

```
{
	"query": {
		"match": {
			"content": "I love"
		}
	},
	"highlight": {
		"fields": {
			"content": {}
		}
	}
}
```

解释：对content字段进去全文检索，检索关键字是`I love`

并对检索的结果content字段进行关键字标亮

响应示例

```
"hits" : [
      {
        "_index" : "test",
        "_type" : "_doc",
        "_id" : "2",
        "_score" : 2.2955391,
        "_source" : {
          "title" : "mystory",
          "content" : "I love xxxx ,but her Don't love me"
        },
        "highlight" : {
          "content" : [
            "<em>I</em> <em>love</em> xxxx ,but her Don't <em>love</em> me"
          ]
        }
      },
```

看见没，在highlight里面，关键字用`<em></em>` 标出来了



#####　1.6.2.2.2 聚合查询

这里的聚合查询相当于了sql的group by查询，基本都是查询诸如统计某个字段相同的记录有多少个之类的。

现在假设你有一些银行账户存储在elasticsearch里面。银行账户里面有好几个省的数据，

现在你要统计一下，每一个省有多少个银行账户。

**用法1：将相同字段值进行分组，并查看每个分组的大小**

查询DSL如下所示

```
GET /bank/_search
{
  "size": 0,
  "aggs": {
    "group_by_state": {
      "terms": {
        "field": "state.keyword"
      }
    }
  }
}
```

解释

1. size 为0表示只展示聚合结果，实际查询的记录不要显示出来
2. aggs是聚合搜索的关键字
3. group_by_state   给聚合查询的结果起一个名字
4. terms  精确匹配字段和值的关系，也就是下面的field必须是state，或者说根据state进行分组
5. /bank/_search  表示查询的范围是bank这个索引

响应如下

```
{
  "took" : 1,
  "timed_out" : false,
  "_shards" : {
    "total" : 1,
    "successful" : 1,
    "skipped" : 0,
    "failed" : 0
  },
  "hits" : {
    "total" : {
      "value" : 1000,
      "relation" : "eq"
    },
    "max_score" : null,
    "hits" : [ ]
  },
  "aggregations" : {
    "group_by_state" : {
      "doc_count_error_upper_bound" : 0,
      "sum_other_doc_count" : 743,
      "buckets" : [
        {
          "key" : "TX",
          "doc_count" : 30
        },
        {
          "key" : "MD",
          "doc_count" : 28
        }
      ]
    }
  }
}

```

解释响应

1. sum_other_doc_count  表示还有743的分组情况没有在这个响应中体现。

**用法2：对相同字段值的记录进行分组，并对组内的所有元素的某个字段求一个平均值**

查询DSL

```
GET /bank/_search
{
  "size": 0,
  "aggs": {
    "group_by_state": {
      "terms": {
        "field": "state.keyword"
      },
      "aggs": {
        "average_balance": {
          "avg": {
            "field": "balance"
          }
        }
      }
    }
  }
}

```

解释：

1. 第二个aggs的意思是在第一个aggs的基础上，在执行下面的聚合操作
2. average_balance  ，作为一个有业务含义的字段名，将聚合操作的结果放在该字段并返回
3. avg  求平均的聚合操作
4.  "field": "balance"  指定balance字段进行聚合操作（就是上面的取平均数）



**用法3：对相同字段值的记录进行分组，并对组内的所有元素的某个字段求一个平均值，最后得到的结果按指定字段排序**

查询DSL

```
GET /bank/_search
{
  "size": 0,
  "aggs": {
    "group_by_state": {
      "terms": {
        "field": "state.keyword",
        "order": {
          "average_balance": "desc"
        }
      },
      "aggs": {
        "average_balance": {
          "avg": {
            "field": "balance"
          }
        }
      }
    }
  }
}
```

解释：

1. order表示对聚合匹配的结果，里面的balance字段进行降序排序

**用法4：对某字段进行范围划分，将并分组**

DSL示例

```
{

	"aggs": {
		"group_by_state": {
			"range": {
				"field": "balance",
				"ranges": [{
						"from": 1000,
						"to": 2000
					},
					{
						"from": 2000,
						"to": 100000
					}
				]
			},
			"aggs": {
				"avg_balace": {
					"avg": {
						"field": "balance"
					}
				}
			}
		}
	}
}
```

解释：就是将balance按照1000-2000，2000-100000分组

并对分组内的balance求平均值，得到该平均值和分组后的大小

结果如下

```
{
	"aggregations": {
		"group_by_state": {
			"buckets": [{
					"key": "0.0-50.0",
					"from": 0.0,
					"to": 50.0,
					"doc_count": 0,
					"avg_balace": {
						"value": null
					}
				},
				{
					"key": "50.0-1000.0",
					"from": 50.0,
					"to": 1000.0,
					"doc_count": 0,
					"avg_balace": {
						"value": null
					}
				}
			]
		}
	}
}
```






##### 1.6.2.2.2 DSL参数解释

1. query 

2. sort 里面是排序的定义
   1. FIELD 要排序的字段
   2. order以及后面的值是指明对应的字段是用于降序还是升序？（字段串的字段默认无法排序哦）

3. ` _source`里面可以定义仅返回几个字段，值可以是

   字符串数组

4. size  查询出来的条数

5. form  从头几个数据开始往后查询，为实现分页用的

##### 1.6.2.2.2 小知识

###### 1.6.2.2.2.1:text类型进行聚合和排序查询

一般来说，text类型的字段进行聚合查询和排序查询无太大含义，且消耗服务器资源巨大，默认情况下，elastic是禁用此类操作的，但是如果你偏要进行作死的话。

elastic就会提醒你

```
 {
        "type": "illegal_argument_exception",
        "reason": "Fielddata is disabled on text fields by default. Set fielddata=true on [content] in order to load fielddata in memory by uninverting the inverted index. Note that this can however use significant memory. Alternatively use a keyword field instead."
      }
```

解决办法，修改content字段映射的fielddata为true

修复方法

```
PUT /test/_mapping
{
  "properties":{
    "content":{
      "type":"text",
      "fielddata":true
    }
  }
}
```

> 话说回来，官方文档好像说不允许修改字段的mapping，但这个执行还是成功的


## 1.6.3 未归类的API







## 1.7: 问题

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

   1. `max virtual memory areas vm.max_map_count [65530] is too low, increase to at least [262144]`
      这个是虚拟内存的`vm.max_map_count [65530]`太低了，至少设置到262144
      用`sysctl`命令即可实

### 1.7.1 更新数据的脏读

假设一种场景：

线程AB同时对es的某个字段进行修改，他们同时获取到这个字段的值都是5

但是线程A修改后提交了，该值在es中变成了6

线程B比人家晚一步，但是也修改值变成了6，这一更新，就会导致数据出错。

原本预计字段值是7，但是最后变成了6.

这就是es存在的脏读现象

一般来说，对于数据的并发操作问题通常用两种解决方案来解决

1. 悲观锁
2. 乐观锁

关于这两种锁的原理这里就不多讲，原理很简单。

需要说的是，elastic支持的锁是乐观锁，它用一个元字段`_version`来作为版本号，这个版本号在修改和删除的情况下都会自增。

> 在现今的版本内，已经不再用`_version`作为外部乐观锁控制了，它已变成了内部的乐观锁控制了
>
> 在外部的乐观锁控制里，使用的是另外两个字段：`_seq_no`和`_primary_term`
>
> `_primary_term`在每一次primary shard重新分配时，都会自增
>
> `_seq_no` 在每一次修改，删除数据时都会自增。
>
> 当每次修改或者删除数据时，都要带这两个字段上去，这样才能实现乐观锁控制

如果要使用乐观锁的版本号进行并发控制的话，只需要在修改和删除的restAPI带上两个参数

1. if_seq_no   

2. if_primary_term

   或者

1. version

4. version_type=external

   > 举个例子：
   >
   > 1. `PUT /test/_doc/1?if_seq_no=9&if_primary_term=5`
   > 2. `PUT /test/_doc/1?version=10&version_type=external`

对于`if_seq_no   `和`if_primary_term`

这两个参数的值从查询的API请求即可拿到，并且要保证是最新的，否则的话，可能会爆如下错误

```
[1]: version conflict, required seqNo [9], primary term [5]. current document has seqNo [11] and primary term [5]
```

这意味着发起修改的数据不是最新的，需要重新组织请求数据

对于`version和version_type=external`

第一个参数要比当前文档的version要大，第二个参数使用固定值`external`

这个方式可以自定义自己的version生成方式，而不是es默认的递增。









## 1.8 elasticSerach 原理剖析

注意：随着时间的流逝，原有原理可能会被替代，所以这里原理剖析，不一定代表elastic源码就是这么做的。

这一章主要起到扫盲的作用

### 1.8.1 elastic是怎么优化全文检索的

关键字技术：分词+倒排索引

首先将数据里面的全文进行分词，然后归类建立索引。就这样。

举例

现在有一批电影数据，数据里面电影名被视为全文数据类型。

里面的数据大致是

1. 生化危机电影
2. 生化危机海报
3. 生化危机音乐
4. 生化危机新闻
5. 逆转裁判全版本下载

那么，elastic会对这些数据进行分词，然后合并相同的数据，标记相同的数据的来源，最后结果类似于

| 关键词   | ids     |
| -------- | ------- |
| 生化     | 1,2,3,4 |
| 危机z    | 1,2,3,4 |
| 电影     | 1       |
| 海报     | 2       |
| 音乐     | 3       |
| 新闻     | 4       |
| 逆转裁判 | 5       |
| 全版本   | 5       |
| 下载     | 5       |

如果现在进行全文检索，检索的关键字是`生化机`，则elastic会将关键字进行拆分，变成`生化`  `机`

然后分别去上面的全文索引去查关键字，很明显，1，2，3，4的记录都包含`生化`

虽然后面的`机`是匹配不到的，但是至少匹配到数据了，由于有索引，搜索的速度也不会低到哪去。

比数据库不知道高哪去了。

## 1.8.2 elastic  shard 和replica 同步

elastic的shard和replica之间的同步是异步多线程的执行。

为了避免异步数据并发请求问题，elastic内部也是有自己的一套version，这个version可能并不为外界所知。

总之它是为了实现乐观锁而设定的。

关于shard和replica之间的同步，有一个很有趣的场景。

就是说，有可能primary shard和replica shard之间的同步是针对同一条记录，并发去执行同步（修改）请求的。并且有可能后修改先执行了，那么对于先修改的请求，elastic会丢掉。

关于里面的实现机制，暂时仍不明朗，但是这个结论，目前应该是没问题的



## 1.9 elastic 集群管理

1. 首先是健康管理，请求这个rest API就可以查看集群内部的节点监控情况

   `GET _cluster/health`

   主要关注里面的status状态

   如果是red：说明不是所有索引的shard都是可用的。

   ​	有些shard数据可能丢失了

   如果是yellow：说明每个索引的shard都是active，

   ​	但是有部分replica是不可用的

   如果是green：说明所有索引的replica和replica都是可用的

   特别的，当elastic 第一次安装并请求这个API时，

   总是能到到yellow的结果。这是因为一般elastic第一次安装是，总是单机启动的，也就是只有一个节点shard，没有节点能存放replica，所以replica不可用。所以yellow了

返回值大致如下所示

```
{
  "cluster_name" : "docker-cluster",
  "status" : "yellow",
  "timed_out" : false,
  "number_of_nodes" : 1,
  "number_of_data_nodes" : 1,
  "active_primary_shards" : 4,
  "active_shards" : 4,
  "relocating_shards" : 0,
  "initializing_shards" : 0,
  "unassigned_shards" : 1,
  "delayed_unassigned_shards" : 0,
  "number_of_pending_tasks" : 0,
  "number_of_in_flight_fetch" : 0,
  "task_max_waiting_in_queue_millis" : 0,
  "active_shards_percent_as_number" : 80.0
}

```

解释：

1. number_of_nodes 节点数



### 1.9.1  elastic分布式机制和隐藏特性

分布式机制

1. 分片机制，就是数据分片

2. cluster discovery  集群发现

3. shard 负载均衡，就是将要分配的shard，均匀分配到已有的节点上

4. shard副本

5. 增加和减少节点时，数据会发生Rebalance（再平衡）

   举例子，假设之前集群有5个节点，但承受6T的数据量，那么总有一个节点它承受的负载更重一点。

   当有新节点加入进来时，那么负载重的节点，就会将一部分负载，转移到新加入的那个节点。

   使整个集群的数据重新均衡，这就是Rebalance

6. master节点

   master节点的作用：管理es的元数据，比如说索引的创建和市场，节点的增加和移动

   注意：master不会处理所有的请求，所有的请求是打到各个节点上面去的。

   所以master不会成为一个单点瓶颈

7. 节点对等，也就是，所有节点都能做同样的事，都能处理来自客户端的请求。

   里面的机制是这样的

   请求通过负载均衡发送到集群的的某个节点上，这个节点就会搜索所需要的数据存在哪些节点上，

   并且请求它们获取数据，再经过整合，发给客户端。

扩容方案

1. 垂直扩容，就是该配置的服务器替换旧服务器

   举例子，elastic的集群有6台服务器，每台1T的容量，也就是，总共可容纳6T的容量。

   然而，随着业务发展，数据量已经快突破6T，保守估计，最大可能增长到8T.

   那么，垂直扩容给出的解决方案是再买两台,每台2T容量，将集群中的旧两台服务器替换掉

   那么，新配置的集群最大容量就是：`1*4+2*2=8t`了
   
2. 水平扩容，其实和垂直扩容类似，只不过新买的服务器不是替换，而是作为新节点加入集群中，

   这样只需要买 两台1T容量的服务器，最终新集群可容纳的最大容量是：1*8 = 8T 

   

   当然，目前最流行的是水平扩容

   

### 1.9.2  elastic集群灾难恢复

举个灾难恢复的例子：

**背景：**

假设现在有一个索引，包含3个primary shard（(P0,P1,P2) ）和3个replica shard(R0,R1,R2)

它们均匀分布在三个节点里。

它们是这么分布的

1节点（master）：P0,P1

2节点：R0,R2

3节点：P2,R1

**灾难过程：**

1节点表示自己要挂了，然后就gg了

此时在挂的一瞬间，

整个集群的状态变成red，因为1节点上的两个primary shard丢失了，不是所有的primary shard 都处于active

**恢复过程：**

1. 自动选举新的master节点

2. 将丢失的primary shard的replica shard 提升为primary shard。

   > 这个步骤完成后，整个集群的状态变成了yellow。
   >
   > 因为提升后，所有的primary shard都active。
   >
   > 但消耗了replica shard，导致不是所有的replica都可用

3. 尝试启动GG的1节点，并将故障后的数据变更同步到GG的1节点。

   1节点的shard还是那两个shard，只不过数据被替换成最新的，并且从primary shard 降级为replica shard。

   该步骤完成后，集群的状态变成为green。

   （所有的primary shard 和 replica shard都active）

   







   

   





# 第二章：kibana快速入门

1. 解压
2. 修改config里面的kibana配置文件，将
    `elasticsearch.url: "http://192.168.21.254:9200/`修改为elasticsearch的服务器地址。
3. 如果远程主机要访问kibana的服务，请把`#server.host: "localhost"`此行取消注释，并设置为非回环地址
4. 运行bin里面的可执行文件  
5. 如果一切正常的话，访问kibana的ip地址+端口，可以看到kibana为你展现的前端页面，不过，在食用之前，你需要在页面设置一个模式。。

> 如果是docker的话，只需要执行这个命令：
>
> ```
> docker run -d --name kibana --net somenetwork -p 5601:5601 kibana:7.3.1
> ```


这么简单的吗？当然不是，接下来让我们加载一些简单的数据集。。。。。

## 2.1 加载2.1些简单的数据集

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

# 第三章 定义你的索引模式

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


# 第四章：Logstash日志存储管道

先不管`管道`是什么鬼，总之，它是Logstash存储日志的一个过程。这个过程需要二个必须的元素，还有一个可选的元素
1. input：这个其实是一个插件，负责从源收集，消费数据  
2. filter：这个可以根据你自己的指定来修改数据  
3. output：将数据写到目的地，一般是Elasticsearch  

## 4.1hello world

首先要看下你的logstash是否安装成功了，简单的话，执行这个命令  
`bin/logstash -e 'input { stdin { } } output { stdout {} }'`  
可以发现控制台先打印一串鬼画符，然后出现光标了。这时候，你输入什么，敲回车，就回显什么  
SUCCESS！这表示你的logstash已经能正常工作了  
解释：  

1. -e 可以让你以命令行的方式给logstash指定配置。  
2. 该示例的管道是从标准输入获取数据，并以结构化的数据输出到标准输出  

> PS： 小贴士，按ctrl-d 可以断开shell哦  
> ps:  如果执行以上命令抛了一个异常，说找不到这个文件`logstash-plain.log`请尝试用超级管理员执行命令  
## 4.2在logstash中分析日志

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

## 4.3 logstash4.3conf文件格式

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


## 4.4使用Grok Filter Plugin来解析log日志

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

## 4.5 Geoip过滤器插件为你的数据补充细节

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

## 4.6 将你的数据编入Elasticsearch

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

## 4.7在logstash使用多个输入和输出插件

在实际开发中，你的日志来源通常有多个地方，要输出的目的地也可能有多个，这个时候，单个input和output肯定不能满足需求。
在这一节中。你将创建一个logstash的管道，该管道从推特简讯和filebeat的client获取输入，也就是说，有两个输入。  
然后将信息发送到Elasticsearch集群，并且还会写入到文件。也就是说，有两个输出。  
那么问题来了，在中国，你怎么能访问到推特简讯？？？WTF！

## 4.8 logstash 是如何工作的？

logstash处理事件有三个阶段：输入生成事件，过滤修改事件，输出将事件运往其他地方.

输入和输出插件支持编码和解码，你不必使用单独的过滤器来处理这些事情。

来简单介绍下输入，输出，过滤，编解码。

### 4.8.1输入插件

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

### 4.8.2输出插件

输出是logstash管道的最后阶段，一个事件可以有多个输出，但是一旦所有输出处理完成，这个事件就结束了，一些常见的输出插件如下  

1. **elasticsearch** 如果你想将数据便捷，高效，且便于查询的保存起来，那么 Elasticsearch是你不错的选择
2. **file** 在磁盘中写入事件数据
3. **graphite** 将事件数据发送给graphite，这是一个开源的工具，可以存储和绘制指标
4. **statsd** 将事件数据发送给statsd

### 4.8.3 编解码器

编解码器一般是作为输出或者输出过程的一部分，作为流过滤器而存在的

编解码器可以让你把消息从序列化过程中分离开来，常用的编解码器包括json，msgpack，plain (text)

- **json** 使用json格式编解码数据
- **multiline** 将多行文本事件（例如java的堆栈异常）合并到单个事件中


### 4.8.4 log4.8.4j的输入插件

注：logstash的log4j插件其实已经过时了，现在推荐使用的是搭配filebeat来使用

使用方式

1. 配置你的日志输出方式为写入文件。

2. 安装并配置filebeat来收集这些日志并发送到logstash

3. 配置logstash来使用beats的输出插件

   

   其中1不解释，看log4j2的官方文档教你如何将服务器产生的日志写到文件中

   2的配置如下

```yaml
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


   ```