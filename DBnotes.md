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

#  第五节：神奇的查询
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







# 第五节：decimal字段类型  

这个类型比较特殊，比如说，它定义成decimal(5,4)  
这个的意思，就是说，这个字段把整数部分和小数部分算在一起，其数字的个数不能超过5个，其中，小数的个数占了4个，也就意味着  
整数部位只能一位。  
所以，你想插入11.4啊，10.0啊，统统行不通。
然后，特殊的是，你却可以插入9.99999999 虽然你看到小数部分个数是4，但是它允许你小数部分不限制加。。。  
不过，实际存到数据库里面的，其实是截断过的   



