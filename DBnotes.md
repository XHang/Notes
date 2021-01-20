#  数据库知识

> 暂时记录一些有意思的知识点

# 第一节:查询语句

1. distinct 关键字可以去掉重复记录

   使用方法`select distinct field from table_name;`

   这样是只查询一个字段，而且相同字段的记录会被剔除。

   如果是这样使用的话`select distinct * from table_name`

   那么完全相同的两个记录会被过滤

2. max方法，是一个聚合方法，只能查询出一条最大的记录。

   比如说现在有一张表，表里面有姓名，部门，薪水三个字段。

   要求，让你查出薪水最高的那个家伙的记录。

   错误示范就是`select max(薪水) from 表名

   这样只能查询出一条记录，而且连姓名也查不出来，而且要知道，薪水最高的人不止一个。

   所以，正确的sql语句是`select * from 表名 where 薪水=(select max(薪水) from 表名 ) `


# 第二节： Oracle 和PG数据库之间的差异

1. Oracle数据库有一个方法*NVL*( string1, replace_with)

   功能是当String1为null时，函数返回replace_with

   否则返回String1

   PG数据库没有这个函数。但是要想实现这种功能的话，可以选择COALESCE(参数1，参数2，参数3.....)

   功能是返回所给参数中第一个不为空的元素.

   所以要实现NVL的功能的话，可以这样COALESCE(参数1,'0')

   这样如果参数1是空的话，返回的就是0了，也就是预设的默认值。

2. ORACLE数据库的表名可以小写，默认给你转成大小

   但是PG数据库要是大写了，查表的时候，表名就得大写加双引号。不然会报找不到相关的表

3. Oracle中,update语句中,update的字段值你想通过其他表查询到,然后用查询到的值update.

   做法很简单,就是

   ```
   UPDATE table1 t1
   SET (name, desc) = (SELECT t2.name, t2.desc
                            FROM table2 t2
                           WHERE t1.id = t2.id)
    WHERE t1.id='11'
   ```


   很好,看起来不错,但是pg数据库也想来这么一下,一运行,语法错误....

   怎么办呢?只能用一个黑科技了,就是`update from`sql

   ```
   UPDATE table 
   SET name = link.other_name,
   FROM
    (select * from list where id='11' ) as other_name
   WHERE
    id='11'
   ```

   大致就是这样了,唔~~~`update from`这种其实不是严格的sql语句规范哦

4. PG数据库的update语句不支持用表的别名,一定要用,只能加表的全称

5. Oracle和pg数据库的序列sql也不一样。
    假设序列名字叫
    pg的语法是：`SELECT nextval('seq_user_version')`  
    Oracle的语法是`select seq_user_version.nextval  from dual;`

6：关于日期的区别  
pg数据库日期函数为`now();`  
使用示例`select now() from dual;`  
Oracle数据库的日期函数为  `SYSDATE`
使用示例`select SYSDATE from dual;`  

# 第三节:一般数据库的禁忌

在为某些数据库建表时，要小心表名不能和数据库已有的关键字冲突。

经验证，冲突的表名有

1. user   在PG数据库已经挂了

# 第四节：MYSQL的知识

1. mysql有一个模式设置为`ONLY_FULL_GROUP_BY`

   这个设置在关闭状态下，Mysql的sql语句的检验就少了一项

   即：`不校验查询语句显示的字段是否包含在group字句或者聚合函数里面`

   像这类的查询语句，Mysql是睁一只眼，闭一只眼的

   `select username,age from user group by username`

   其实按照sql规范，这种的语句是不正确的，至少在oracle是会挂的。

   Mysql只要不设置`ONLY_FULL_GROUP_BY`这个的话，这种不严格的语句可以通过，这当然不好，要是你的应用换了数据库呢？像Oracle，原本在mysql可以放过的sql，在Oracle可就挂了诶，所以一般来说，开发组都会把这个限制加上去的。

   也就是说，以后写sql要注意点，如果sql包含了group by,那么显示的字段就一定要出现在group字句或者聚合函数里面。

   不过说起来，Mysql有一个坑的，哪怕你的字段出现在group by 里面，但是用函数包起来了，这个字段出现在select字句仍然会被认为不规范。。

2. 在mysql查询里面显示行数
    这个功能，如果在Oracle，直接ruwNum就可以搞定，但是，mysql没有这个行数，怎么办呢？可以用变量。
    在Mysql里面，变量用@定义。这道题用变量来做，是这样的
    假如有一张表，表名是t,查询该表所有记录，并显示它的行号，sql是这么写的
    `select t.*,@rowno:=@rowno+1 as rownum from t,(select @rowno:=0) t1; `
    其中@rowno:=1是给行号赋予初值，然后每次记录查出来都执行,`@rowno:=@rowno+1`，就起到行数的作用了  

3. Mysql引擎详解

    1. InnoDB 引擎

       该引擎是高可靠性，高性能的数据库引擎，也是Mysql的默认引擎

       除非配置了不同的默认引擎，否则create语句不带engine的语句会创建InnDB数据库表

       优点：高可靠性，高性能、支持事务，支持行锁、可处理大量数据

       缺点：执行速度较**MyISAM**慢

    2. Mylsam 引擎

       该引擎基于lsam引擎，表存在文件中，数据文件具有 `.MYD`（`MYData`）扩展名。索引文件具有`.MYI`（`MYIndex`）扩展名。表定义在数据库字典中

       优点：可以压缩表、速度比InnDB快

       缺点：不支持事务、外键、数据库缓存

    3. 

       

       


#  第五节：神奇的SQL
## 连续性问题
业务场景：小航，报表部门要你开发一个功能，能查询连续登录三个月及以上的登录用户。数据库表已经发给你了，最迟今晚完成。
表数据T

| userName | loginas(月) |
| -------- | ----------- |
| 李四     | 3           |
| 李四     | 4           |
| 李四     | 5           |
| 李四     | 6           |
| 李四     | 7           |
| 张三     | 2           |
| 张三     | 3           |
| 张三     | 4           |
| 张三     | 5           |
| 王五     | 1           |
| 王五     | 2           |
| 王五     | 5           |
| 王五     | 6           |
| 王五     | 2           |
| 陈胜     | 1           |
| 陈胜     | 3           |

所用数据库：Oracle
核心概念：连续的数a-连续的数b=固定的数。

比如说3,4,5  减去 1,2,3 得到的结果是2,2,2 这就是数学的魅力。
用这个概念，就可以实现需求了。

1. 首先根据userName和Loginas来降序排列 记为T，目的是把连续的记录挤在一起

2. 用前面查询出来的每一条记录，用其Loginas减去行数，即 
     `select T.*,(T1.Loginas-${runNum}) c from T`  记T1

3. 这个时候，如果是连续的记录，就有字段是一样的了，就是我们的字段c
    我们把字段C统计一下次数，就是用户连续登陆的次数了
     `select userName,count(T1.c) as c from T1 group by T1.userName,T1.c` 

      记T2

4. 接下来很简单吧，都有连续登陆的次数了，一个where语句，搞定！

   `select * from T2 where T2.c>3`

实战演练

```
field1	field2
2014	1
2014	2
2014	3
2014	4
2014	5
2014	7
2014	8
2014	9
2013	120
2013	121
2013	122
2013	124
2017	55
```

用mysql查询field2连续出现3次的记录

凡人们，颤抖吧

```
select * from (
SELECT
	field1,count(field1)  as c
FROM
	(
	SELECT
		t1.*,
		t1.field2 - t1.rownum  as c
	FROM
		(
		SELECT
			t.*,
			@rowno := @rowno + 1 AS rownum 
		FROM
			t,
			( SELECT @rowno := 0 ) x 
		ORDER BY
			field1,
			field2 
		) t1 
	) t2
	group by field1,c) t3
	where t3.c>3
```





## EXPLAIN的使用

这个可以用于查看,mysql在处理select语句时，是怎么进行的，有没有用索引，有没有关联表等

用法是

`EXPLAIN {sql}`

sql填写sql语句后，直接执行，就可以查询到相关信息了，如

| id   | select_type | table | partitions | type  | possible_keys | key     | key_len | ref   | rows | filtered | Extra |
| ---- | ----------- | ----- | ---------- | ----- | ------------- | ------- | ------- | ----- | ---- | -------- | ----- |
| 1    | SIMPLE      | site  |            | const | PRIMARY       | PRIMARY | 4       | const | 1    | 100.00   |       |

解释如下：

1. id:用于标识每一个执行select的动作，一个sql语句可能有多条记录，多个id，因为一个sql语句里面可能不止执行一次select，比如说包含子查询的sql语句，数据库会先执行子查询，再执行主查询

   看id可以知道sql扫表的顺序,id越大的，扫表时的位置就越靠前

   但是有可能id是一样的，这个时候，就看table列，table列靠前的那一行，越先读取

2. select_type，顾名思义，就是select的类型，有以下几种类型

   1. `SIMPLE`(简单SELECT，不使用UNION或子查询等)

   2. `PRIMARY`，一般是这个sql里面有子查询，则最外面的查询会被标记为`PRIMARY`

      举例：

      `explain select * from A where A.id = (select B.id from B where B.id=1)`

      | id   | select_type | table | partitions | type  | possible_keys | key     | key_len | ref   | rows | filtered | Extra |
      | ---- | ----------- | ----- | ---------- | ----- | ------------- | ------- | ------- | ----- | ---- | -------- | ----- |
      | 1    | PRIMARY     | A     |            | All   |               |         |         |       | 5    | 20       |       |
      | 2    | SUBQUERY    | B     |            | const | PRIMARY       | PRIMARY | 4       | const | 1    | 100      |       |

   3. `SUBQUERY ` 子查询

   4. `UNION`  当一个查询在  `UNION`  语句后，那边这个查询就是`UNION`  

   5. UNION RESULT    `UNION`   两个表之后的查询结果

3. table 很明显就是表名，有时候是表名的简写

4. type 对表的访问类型

   | 访问类型    | 解释                                                         | sql举例                                                      |
   | ----------- | ------------------------------------------------------------ | ------------------------------------------------------------ |
   | system      | 表只有一行记录，这个是const的特例，一般不会出现，可以忽略    | 实际上只有一个记录也出不来这个访问类型（暂时忽略）           |
   | const       | 表示通过索引一次就找到了                                     | select * from r_user where id = 1                            |
   | eq_ref      | 唯一性索引扫描                                               | 很多教程都是用表连接，连接条件是id相等。但实际测试的时候，发现不是`eq_ref`，而是`const` |
   | null        | 不用扫表或者扫索引                                           | SELECT 5*7<br /> SELECT MAX(id) FROM student                 |
   | ALL         | 全表扫描                                                     |                                                              |
   | index       | 也是全扫描，只不过扫描的是索引树，比all快一点，比其他都慢<br />这可能发生在两种方式中<br /><br />1. 查询时使用了覆盖索引。说成人话就是，查询的字段有做索引，且where语句查询的字段也在索引内。这种情况，Extra会显示Using index<br /><br />2. 从索引树进行全表扫描，按照索引顺序查找顺序行<br />一般出现在用索引进行排序的情况下 |                                                              |
   | range       | 索引范围查询，比如in、<>之类的查询符，当然查询的字段肯定加了索引，否则怎么叫**索引**范围查询呢？ |                                                              |
   | index_merge | 对两个以上的索引条件进行and或者or查询，最后去交集或者并集    |                                                              |

5. possible_keys  可能应用在该表的索引，不一定使用

6. Extra 字段，包含不适合在其他列显示，但是非常重要的额外信息

   1. Using filesort 查询中用到排序，但是优化器找不到可用的索引排序，只好使用外部排序，这种排序需要在磁盘和内存中交换数据，占用磁盘io，效率极低
   2. Using tempporary 查询中用了临时表来排序，效率也是一言难尽
   3. Using index  使用了索引进行排序
   4. Using join buffer  使用了join表的缓存区，多见于join表很多的SQL语句
   5. impossible where  查询不到数据
   6. distinct  优化器优化了distinct  操作，找到同一个值的记录后，就停止找同样值的记录
   7. 



## mysql变量的使用	

在mysql中，变量的表示是以@变量名开头来表示的

如`@var` `@temp`  的等等



一般说来，mysql的变量一般用于数据库的过程编写，但是，其实，也可以用于sql语句中的一些变化。

举个例子：现在我有一个表，表里有一个字段X，这个字段要求是递增的。

现在就要把表里每一行记录里面的X字段，都按照一定的排序来递增记录。

在sql中，就可以用变量来记录这个递增值。

参考的sql语句如下

```
 set @var=0;
 update table set x=@var:=@var+1
 order by create_time ASC
```

搞定

## 递归查询问题

以常见的用水性质为例

一般的水司，用水性质都是分层的。

例如说

1. 居民生活用水
   1. 城镇居民用水
   2. 社会福利机构
   3. 教育机构
2. 工商用水
   1. 采矿业
   2. 制造业
   3. 建筑业

这只是两层结构，实际随着业务发展，可以是三层，甚至四层

现在问题来了，已知最低一层的记录ID，要求查到它最顶层的用水性质ID.

而且，使用sql完成

现在，给你一个用水性质的表结构（table_name = nature）

| 主键（id） | 用水性质（nature） | 父级ID(parent_id) |
| ---------- | ------------------ | ----------------- |
| 1          | 居民生活用水       | 0                 |
| 2          | 工商用水           | 0                 |
| 3          | 城镇居民用水       | 1                 |
| 4          | 社会福利机构       | 1                 |
| 5          | 教育机构           | 1                 |
| 6          | 采矿业             | 2                 |
| 7          | 制造业             | 2                 |
| 8          | 建筑业             | 2                 |

那么，sql怎么写呢？要求制造业最顶层的用水性质

如果你用的是mysql，想用mysql的sql语句实现递归查询。。。tan90 不可棱

所以只能用存储过程。定义一个存储过程，用来递归查询

下面直接写存储过程的解决方案

```
CREATE  PROCEDURE `getTopId`( OUT topid INT, IN originid INT )
BEGIN
	//声明一个中间变量，放父ID
	DECLARE parentId INT;
	//执行一个查询语句，并将ID和父ID赋值到两个变量中
	SELECT parent_id,id INTO parentId,topid FROM nature WHERE id = originid;
	//如果查询出的父ID不为0，表示还没查到顶层的数据，还需要递归call调用
	IF parentId <> 0 THEN CALL getTopId ( topid, parentId );	
	//结束IF语句块
	END IF; 
	//仅查询结果，去掉也可
	SELECT topid;

END
```

其中 存储过程的语法：声明变量，赋值变量，使用变量。判断IF语法，声明语法

麻雀虽小，五脏俱全

看语句就能理解，这里就不多描述了

把上面的存储过程转成函数。试试？

奉上函数代码

```
CREATE  FUNCTION `getTopId`( originid INT ) RETURNS int(11)
BEGIN	
	DECLARE parentId INT;
	DECLARE topId INT;
	SELECT parent_id,id INTO parentId,topId FROM usenature WHERE id = originid;
	IF parentId <> 0 THEN  set topId = getTopId ( parentId );	
	END IF; 
	return topId;

END
```





教训：

1. 函数里面调用函数不是 foo = function(bar)  而是set foo =  function(bar) 

2. 调用函数时发生`Recursive stored functions and triggers are not allowed.`

   最后发现？？？好吧，Mysql的函数不支持递归调用




存储过程OK了，但是怎么调用呢？

1. 存储过程调用存储过程已经有示例了

2. 在sql语句调用存储过程的话

   待定





PS：表设计上，考虑到这种递归写法对开发人员比较高的要求，实际上会加一个冗余字段`path`

记录每一个记录的路径，比如说，制造业，那么它的path就是1,7

这样你无需编写递归的sql，直接sql like去查询即可



##  使用case when语句动态更新字段值

示例sql

```
UPDATE bill  SET print_count =
	CASE 
		WHEN ( print_count IS NULL ) 
		THEN 1 
		ELSE print_count + 1
    END 
WHERE id = 13
```

这个sql的功能就是更新某个账单的打印次数

如果账单的打印次数为null,就更新打印次数为1

否则，就用原来的值再加1



## 连接Join的使用

那些什么左连接，右连接，全连接,内连接，就不解释了

今天要介绍的是，一个特立独行的json

`corss join`

唔~还是不怎么理解，貌似是求并集，把两个表的结果集合并起来

很少用就是了

## Exists的特点

使用exists的sql子句只会返回true或者false。例如  `select * from user where id=? and exists (select * from vip where user_id=?)`      
这样的话，会先查找vip表里面是否有指定的用户id,如果有的话，把该用户从user表查出来.    
所以exists是子查询如果包含行，返回true，否则返回false  
注:值的一提的是，如果sql子句是:`where exists (null)` 那么这个子句会返回true。  

> 以mysql为例，其他数据库不做保证

**group by count 问题**

有一个需求，要你对数据进行分组后再计算分组后的大小

你可能顺手就

`select count(*) from xxxx group by xxxx`

OK，拿到数据了，但是仔细一看，，不科学啊，本来`group by` 分组后只有一条，但是`count`之后只有一条

原因在于，如果group by 后进行分组，拿到的其实不是分组的大小，而是第一个分组的大小（第一个分组里面有多少记录）

如果想拿到分组的大小，只能用子查询了



## mysql distinct的特性

都应该知道，distinct的作用是保证数据的唯一性，你指定一个字段，那么查询出来的记录，里面的字段就是唯一的。

不过有几点尴尬的地方

1. select只能显示被distinct限定的字段，也就是说，如果你要展现整个表的所有字段，那么所有字段都得经过distinct处理
2. distinct和count的组合有个特性，如果被distinct的字段，在查询出来的结果集里面，该字段值都是空的，那么，count 查询出来的结果就是0

第2点特性实在是太丧心病狂了，按理来说，都是空的字段就都视为相同的记录不就行了,为了达到这一点，我们可以对distinct里面的字段进行处理

假设你的sql如下：

```
SELECT
	count( DISTINCT field1,field2, field3 ) 
FROM
	`table` 
WHERE
	`field1` IN ( 'xxxx','xxxx' ) 
```

不巧，你要查的几条记录里面的field2字段值全是空的。

那么这条sql会得到一个荒唐的结论，0

正确改法：

```
SELECT
	count( DISTINCT field1, CASE WHEN field2 IS NULL THEN 1 ELSE field2 END, field3 ) 
FROM
	`table` 
WHERE
	`field1` IN ( 'xxxx','xxx' ) 

```

实际上就是让数据库在查询的时候，如果遇见field2是空的情况，那么就给它一个默认值1，否则就是原值



# 第五节：字段类型  



## 5.1 char和varchar类型

这两个都可以存放字符串。

但是char存储的是固定长度，如果你存储的数据不够这个长度，那么mysql会自动填充空格直到满足长度

> 查询char字符出来时，mysql引擎会自动将填充的空格去掉。

而varchar是可变长度，你存多少长度的数据，实际存储就是多长的数据（其实还要加1~2个字节存储长度）



## 5.2 decimal

这个类型比较特殊，比如说，它定义成decimal(5,4)  
这个的意思，就是说，这个字段把整数部分和小数部分算在一起，其数字的个数不能超过5个，其中，小数的个数占了4个，也就意味着  
整数部位只能一位。  
所以，你想插入11.4啊，10.0啊，统统行不通。
然后，特殊的是，你却可以插入9.99999999 虽然你看到小数部分个数是4，但是它允许你小数部分不限制加。。。  
不过，实际存到数据库里面的，其实是截断过的 



## 5.3 int类型

对于mysql来说，int类型有长度。

但是，这个长度并不是说这个字段最多只能存储几位的数字。非也非也。

对于mysql来说，它一个int字节只占8个字节，最大值永远只能是`2147483647`

那么mysql里面int的长度到底有什么用了。

只是用来填充0或者空格罢了。

比如一个字段定义为int(2).

你插入时，只插入一个3。

实际在显示时，会在3前面填充一个0或者空格到达2位。

当然，一般是填充空格啦，所以你在查询时并没有什么不同。

## 5.3 日期类型

1. date类型，该类型只存储具有日期部分，以`YYYY-MM-DD`格式检索和显示值
2. DATETIME类型，用于包含日期和时间部分的值。 `DATETIME`以`'YYYY-MM-DD HH:MM:SS'`格式检索和显示 值
3. TIMESTAMP数据类型，被用于同时包含日期和时间部分的值。 `TIMESTAMP`具有`'1970-01-01 00:00:01'`UTC到`'2038-01-19 03:14:07'`UTC 的范围





# 第七节：SQL语句

## 7.1 DDL语句

添加字段：alter table ${table_name} add column $(field_name) varchar(12)

修改字段 alter table ${table_name} change ${field_name} ${new_field_name} int(12);

删除语句： alter table ${table_name} drop column ${column_name}

## 7.2 DML语句

插入数据：insert into ${table} (column1,column2)  value (value1,value2);

更新语句： update ${table_name} set column=value,column1=value2;

# 第八节  Mysql BUG合集

##  8.1 导入数据时出错

### 8.1.2 BUG描述

在导入数据到数据库时，报`MySQL server has gone away`

### 8.1.3 BUG分析

这个错误提醒的意思是Mysql服务器断开连接。

断开连接有两种原因

1. 数据库bong沙卡拉卡了

   可以用`show global status like 'uptime';`

   去查询数据库存活的年龄。

   如果值太小，那就是说数据库曾经挂掉了，而又偷偷重启了

2. 连接被数据库关掉了

   这个不好怎么判断

但是不管那两种原因，最终还是要看日志文件来得知，到底发生了什么事情

关于mysql的日志，还有一些不为人知的事情，比如说，mysql日志其实分为几类。还有，日志文件路径如何配置等等



总之，我们看了mysql日志，终于知道

导致`MySQL server has gone away`的原因，是

`Got a packet bigger than 'max_allowed_packet' bytes`

说成汉语就是得到的二进制包大于`max_allowed_packet`的大小，所以挂了

原来mysql对于得到的二进制包的大小，有限制，不能大于`max_allowed_packet`的值

### 8.1.4  BUG 的解决

既然知道是`max_allowed_packet`值太小的缘故，那么把它调大点不就好了？

怎么调整？

1. 永久性调整，修改mysql配置文件的*************

2. 暂时性调整，执行

   `set global max_allowed_packet = 100 * 1024 * 1024*1024 * 1024;`

   里面的值随你调整

## 8.2 修改表结构一直被卡

嗯，就是执行alter语句一直卡住。如果看服务器线程，会发现`Waiting for table metadata lock`

就是说，数据库一直在等待获取锁。

这个时候，应该就能知道，肯定这个表的锁被其他线程拿了。

其中一种场景就是事务执行中，未结束。

解决办法很简单，要么找到事务未结束的原因，让它自动结束

要么，KIll his

```
select trx_state, trx_started, trx_mysql_thread_id, trx_query from information_schema.innodb_trx 
```

可查询事务列表

`trx_mysql_thread_id`即为线程ID

可通过Kill 线程ID使其狗带



## 8.3 column ambiguously defined

一般sql查询，不涉及到子查询的情况下，就算有重复的字段名也可查询出来。但是一旦执行了子查询，如果子查询后会有两个相同的字段。
  则会查询失败，报`column ambiguously defined`

## 8.4 invalid connection

这个异常总是在深夜半夜查询报出来。

搜了一下，说是MySQL有一个`wait_timeout`变量，该变量指明了一个数据库连接如果在`wait_timeout`内都没有使用，则数据库会主动断开这个链接，于是客户端使用这个失效的链接操作数据库时

就会报`invalid connection`

实际情况：

首先敲入show  variables查看会话变量，也就是每一次建立链接时使用的变量。

里面就有`wait_timeout`，单位是秒

为了方便重现错误，打算将`wait_timeout`设置为60，也就是60s超时

为了使每一个新连接的客户端，都使用新配置的wait_timeout，就要变量设置为全局变量，并且interactive_timeout也要设置为同一值

参考命令

```
set global  interactive_timeout=10;
set global  wait_timeout=10;
```



最后测试的结果发现，确实在Mysql服务器主动关闭数据库连接后的几秒后，主动再次查询就会报

invalid connection，但是如果在数据库连接关闭几分钟后再去尝试，就不会报错。

查了下git的相关讨论，发现

1. 一开始Mysql驱动包的go实现是即使数据库连接关闭，也会自动重新连接

2. 后来一个哥们提交了pr，说如果驱动程序检测到数据库连接异常前，已经向服务器写入了命令，那么驱动程序也不应该再重新连接数据库，因为这样可能会导致sql语句重复执行。而且，这也是驱动包的规范。

3. 所以现在就变成了，如果驱动包向数据库服务器写入了任何数据，之后连接异常，就不会再重新连接，而是直接返回invalid connection

   > （调试后发现数据库关闭连接后仍然能写入数据并且成功，只不过读取时才发现连接重置了）

4. 值得注意的是，第一次遇到连接异常后，会在连接的属性上补充上一次连接异常的信息。

   在下一次连接时，检测到这个信息，就会重新连接

   也就是说，第一次连接数据库报invalid connection，第二次就能重新连接了

5. 至于数据库关闭连接后，一段长时间后再去连接数据库，反而没问题的原因是

   连接被数据库完全关闭了，写入数据完全写入不了，这时候重新连接数据库就是安全的。

   驱动包确实也是这么做的，写入失败直接重连









# 第九节 数据库的特殊字符

数据库有些特殊字符，如果你的数据库里面有这些特殊字符的记录，在查询这些特殊字符时，就需要转义下

比如说，你的数据库记录里面有`%2`字符，想查出这些字符的记录。你的sql可能会这么写

`select * from table where field like %%2%`

这样的sql会查出很多你不想查下来的记录。

因为sql数据库会把`%`认为不是简单的百分号

所以，你需要转义

`select * from table where field like %\%2%`

`\`就是转义字符

但是转义字符怎么可能只有`\`

再介绍一个？`_`

这个字符也是like里面的用到的特殊字符，匹配任何单个字符

遇到这个`_`特殊字符的查询，也需要转义啦



# 第十节：存储程序和函数的语法

## 10. 1声明变量语法

`DECLARE var_name [, var_name] ... type [DEFAULT value]`

如语法所示，一个`DECLARE `语句只可以声明一种类型的变量，但是可以有多个不同名字的变量

示例

```
    DECLARE
	areaNo 	VARCHAR(255) ;
DECLARE
	areaId 	int(255) ;
```



## 10.2 游标

Oracle上面的游标学过了，Mysql其实也大同小异。

但是有几个特性，还是要着墨记下的

1. 只读
2. 只能在一个方向上遍历，不能跳过行或者反向
3. 游标声明必须在变量声明之后，处理程序之前声明

作用就是遍历select的每一个记录

首先要声明一个游标：

`DECLARE  变量名 CURSOR for select_sql;`

forExample

`DECLARE areaRecord CURSOR for select id,area_no FROM km_reading_area;`

这样就声明了一个游标

注意几点

1. 游标使用前必先打开

   `open 游标名;`

2. 不用时要关闭游标

   `close 游标名`

如何取得游标里面的元素

` fetch 游标名 into 变量1,变量2;` 

每次执行` fetch ` 语句后，游标都会往结果集下面移动一行

由此，可以使用循环的方式拿去结果集的一些信息，并做一些处理

常用的编码格式是酱紫的

```
-- 申明一个游标终止标识
DECLARE done INT DEFAULT FALSE;
-- 申明一个游标
DECLARE  游标名 CURSOR for select_sql;
-- 申明一个游标读到末尾时的一个触发动作。设置done为false
DECLARE CONTINUE HANDLER for NOT FOUND set done = true;
-- 打开游标
open 游标名;
-- 循环体
back loop;
	-- 拿取游标里面的字段
	fetch 游标名 into 变量1,变量2;
	-- 如果游标已经读取到末尾，跳出循环
	 if(done) 
	 	THEN LEAVE back; 
	 end if;
	-- 处理其他业务
	....
end loop;
-- 循环体
close 游标名;


```

## 10.3 创建存储过程的语法

```
CREATE PROCEDURE 过程名()
begin 
	-- 可以声明和处理一些程序了
	····
end 	
```

没有介绍如果有参数的语法。实际上，只消去mysql官方文档看一下就行了



## 10.4 创建函数的语法

```
CREATE FUNCTION 函数名(入参名 数据类型)  returns 返回值类型  DETERMINISTIC  
begin 
	····
end
```

就这么简单，但是有一点其实还不是很理解

就是 `DETERMINISTIC` 的作用，去掉改值或者换其他值都会报错



## 10 . 5调用函数的语法

两种

1. select  函数名(入参变量1,入参变量2);
2. set 变量 = 函数名(入参变量1,入参变量2);

什么，你想用call 唔，很遗憾的告诉你，call只能用于存储过程。所以......死，了，这，条，心，吧



# 第十一节：DAO框架共性

1. 绝大多数dao框架，都不允许写这种sql语句  
   `select * from user where userName like '%?%'`  
   然后setString。  
   这样会报错说栏目数不对，实际栏目数1，预期栏目数0   
   这估计是因为占位符`?`写到了字符串里面  

# 第十二节：PG数据库知识

1. 如何查看pg数据库的版本  
   `select version();`   

2. 如何查看pg数据的连接资源  
   `SELECT * FROM pg_stat_activity WHERE datname='postgres';`

   > 条件的postgres指的数据库名  
   > 另注：你没看错，就是datname，而不是dataname  

   几个字段说下  

   1. `state`:运行状态，可以为几种值：  
   2. `active`:正在执行；  
   3. `idle:`等待新的命令    
   4. `idle in transaction`:后端是一个事务，但是尚未执行查询；  
   5. idle in transaction(aborted):和idle in transaction类似，除了事务执行出错

3. 连接资源太多，怎么kill
   `SELECT pg_terminate_backend(PID);`  
   pid的值需要从第二项的sql语句查询结果来寻找 

4. 死锁了怎么搞？
   什么情况下会死锁？当你发现一个DML语句总是卡住时，一个简单的select一次性通过的话，你就得考虑死锁的可能性了
   用这个sql语句可以看到死锁的进程

   ```
     WITH t_wait AS (
     SELECT
     	A .locktype,
     	A . DATABASE,
     	A .relation,
     	A .page,
     	A .tuple,
     	A .classid,
     	A .objid,
     	A .objsubid,
     	A .pid,
     	A .virtualtransaction,
     	A .virtualxid,
     	A,
     	transactionid,
     	b.query,
     	b.xact_start,
     	b.query_start,
     	b.usename,
     	b.datname
     FROM
     	pg_locks A,
     	pg_stat_activity b
     WHERE
     	A .pid = b.pid
     AND NOT A . GRANTED
     ),t_run AS (
     SELECT
     	A . MODE,
     	A .locktype,
     	A . DATABASE,
     	A .relation,
     	A .page,
     	A .tuple,
     	A .classid,
     	A .objid,
     	A .objsubid,
     	A .pid,
     	A .virtualtransaction,
     	A .virtualxid,
     	A,
     	transactionid,
     	b.query,
     	b.xact_start,
     	b.query_start,
     	b.usename,
     	b.datname
     FROM
     	pg_locks A,
     	pg_stat_activity b
     WHERE
     	A .pid = b.pid
     AND A . GRANTED
     ) SELECT
     r.locktype,
     r. MODE,
     r.usename r_user,
     r.datname r_db,
     r.relation :: regclass,
     r.pid r_pid,
     r.xact_start r_xact_start,
     r.query_start r_query_start,
     r.query r_query,
     w.usename w_user,
     w.datname w_db,
     w.pid w_pid,
     w.xact_start w_xact_start,
     w.query_start w_query_start,
     w.query w_query
     FROM
     t_wait w,
     t_run r
     WHERE
     r.locktype IS NOT DISTINCT
     FROM
     w.locktype
     AND r. DATABASE IS NOT DISTINCT
     FROM
     w. DATABASE
     AND r.relation IS NOT DISTINCT
     FROM
     w.relation
     AND r.page IS NOT DISTINCT
     FROM
     w.page
     AND r.tuple IS NOT DISTINCT
     FROM
     w.tuple
     AND r.classid IS NOT DISTINCT
     FROM
     w.classid
     AND r.objid IS NOT DISTINCT
     FROM
     w.objid
     AND r.objsubid IS NOT DISTINCT
     FROM
     w.objsubid
     ORDER BY
     r.xact_start
   ```

   看到r_pid没，kill it！

   其实吧，最重要的是建立死锁超时机制，死锁的线程一旦超时，就强制关闭，释放锁。
   死锁并发数高的话，很容易发生。

   什么情况下会死锁

   A线程拿着表A的锁，要访问表B.  
   B线程拿着表B的锁，要访问表A  

5. 不要太依赖工具，比如说Navicat 。这货会把太大的数据变成科学计数法。实测，用pg数据库的shell工具查出来的数据是正常的。。
   但是，比较坑的是，pg数据库支持用科学计数法表示的数字来作为查询语句的条件来查询数据。。

# 第十三节 Oracle数据库知识

1. 一般如果在执行DML语句出现这个错误：

   `ora-00054:resource busy and acquire with nowait specified ` 
   简而言之，就是数据库正在忙，待会再试吧。。
   ..... 开玩笑，怎么可能待会再试，这种情况一般是数据库正在执行事务甚至死锁，这时候就要查询哪里正在执行事务了

   ```
   select t2.username,t2.sid,t2.serial#,t2.logon_time
   from v$locked_object t1,v$session t2
   where t1.session_id=t2.sid order by t2.logon_time;
   ```


   这个语句可以查出数据库有哪些锁

# 第十四节：说一说优化

关于数据库的优化，其实有两种，一种是数据库本身的优化，一种是sql语句的优化

## 14.1 数据库本身的优化

这里面包含了表的设计，数据库的一些配置

首先解释一下，有哪些表的设计可以优化数据库的性能

1. 选择合适的字段类型,比如说如果一个字段是固定长度,则使用char类型,如果一个字段长度可变,则使用varChar

   长度在满足业务的情况下，尽量最短

2. 至少要一个索引，索引虽然会降低DML语句的效率，但是对于查询的语句，是提高效率的

## 14.2 sql语句本身的优化

对于sql语句的调优，可以借助一个语句`explain `  后面直接跟原始的语句

可以查看这个语句的效率

比如`explain select customer_id, customer_name from customers where customer_id='140385';`

返回如下

| id   | select_type | table     | partitions | type  | possible_keys | key     | key_len | ref   | rows | filtered | Extra |
| ---- | ----------- | --------- | ---------- | ----- | ------------- | ------- | ------- | ----- | ---- | -------- | ----- |
| 1    | SIMPLE      | customers |            | const | PRIMARY       | PRIMARY | 4       | const | 1    | 100.00   |       |
|      |             |           |            |       |               |         |         |       |      |          |       |
|      |             |           |            |       |               |         |         |       |      |          |       |

从以上我们可以看到

1. key  PRIMARY  有主键可以用
2. rows 1  只扫描了一行就拿到结果了（因为走的是索引）

关于sql的效率可通过上面的`explain`来获知

然后接下来，就是sql本身的调优经验了

1. 尽可能在`where` `order` `group by`上面使用索引来优化性能

2. 拆分多个或语句,用`union `语句来拆分组合

   比如说，有一个sql

   `select * from students where first_name like  'Ade%'  or last_name like 'Ade%' ;`

   > last_name 和first_name 均已经加入索引

   更快的做法是

   ```
   select * from students where first_name like  'Ade%'  union all select * from students where last_name  like  'Ade%'
   ```

3. 避免使用通配符的语句

   比如`select * from students where first_name like  '%Ade'  ;`

   这会导致使用全表扫描

4. 如果一定要使用通配符，可以考虑mysql的全文搜索

   但是首先需要对表进行一点修改

   `alter table students ADD FULLTEXT (first_name, last_name);`

   然后查询要这么查

   `select * from students where match(first_name, last_name) AGAINST ('Ade');`

   > 补缺点

# 第十五节：mysql的安装

这个安装教程适用于windows，zip归档的mysql。

1. 首先解压文件到你喜欢的目录，这个目录即是安装目录

2. 创建一个配置文件`my.ini`作为myql的配置文件

   里面填

   ```
   [mysqld]
   # mysql安装目录
   basedir=P:\Program\mysql-5.6.43-winx64
   # 设置mysql数据文件路径
   datadir=P:\Program\mysql-5.6.43-winx64\data
   ```

   > 注意一点：数据文件路径一般不要改动，如果你需要自定义的话。。
   >
   > 先创建你那个自定义的数据文件路径，然后把安装目录下面的`data`所有文件复制到自定义的那个文件路径里。
   >
   > 因为默认情况下，安装目录下面的data文件夹就是默认的数据文件夹
   >
   > 自定义的话，当然要把人家旧的数据迁移过去了

3. 安装mysql作为windows为服务

   `.\mysqld --install mysql`

   最后的参数`mysql`是服务名

4. 去windows服务窗口，启动服务

5. OK

**出现BUG**

1. 将mysql作为windows服务时，爆`Install/Remove of the Service Denied!`

   解决办法，以系统管理员权限运行

2. 启动服务失败，在数据文件夹里面找到一个err文件，看到这个报错

   ```
   Fatal error: Can't open and lock privilege tables: Table 'mysql.user' doesn't exist
   ```

   原因，自定义了数据文件夹，但是却没有把原来的数据文件夹的文件全部拷贝到新的数据文件夹里面

# 十六章 powerDesigner 的使用

powerDesigner是一款数据库建模工具

我们可以拿它进行

1. 数据库建模

2. 逆向数据库到模型中

   有几点要注意的

   1. 查看你的powerDesigner 是几位的，可以通过任务管理器查看这个软件的进程名，要下载对应的jdk位数
   2. 配置好Java_home
   3. 配置好数据库驱动版本，然后就可以连接数据库了

3. 导出数据库模型为其他文件

   

# 十七章：事务

在并发的从数据库操作数据时，一般会遇到这几个并发性问题。

1. 脏读：一个事务，读到了另一个事务未提交的记录，导致读到的数据是临时数据，有可能被提交从而转成正式数据，也有可能被回滚导致临时数据失效
2. 不可重复读：一个事务读取同样的记录两次，在这两次读取的中间，另一个事务对同样的记录进行修改并提交。于是前一个事务第二次读取的时候就和第一次读取的不一样了
3. 幻读：一个事务执行两次统计，比如说count(*)，在统计的中间，另一个事务对前一个事务查询的表新增或删除了某个记录，导致前一个事务统计的结果和前一个的结果不一致，产生了幻读。



