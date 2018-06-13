# 数据库知识

> 暂时记录一些有意思的知识点

#第一节:查询语句

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

第三节：三范式



   ​