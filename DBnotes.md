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
|      |             |       |            |       |               |         |         |       |      |          |       |
|      |             |       |            |       |               |         |         |       |      |          |       |



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



# 第五节：decimal字段类型  

这个类型比较特殊，比如说，它定义成decimal(5,4)  
这个的意思，就是说，这个字段把整数部分和小数部分算在一起，其数字的个数不能超过5个，其中，小数的个数占了4个，也就意味着  
整数部位只能一位。  
所以，你想插入11.4啊，10.0啊，统统行不通。
然后，特殊的是，你却可以插入9.99999999 虽然你看到小数部分个数是4，但是它允许你小数部分不限制加。。。  
不过，实际存到数据库里面的，其实是截断过的   



# 第七节：神奇的DDL语句

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









