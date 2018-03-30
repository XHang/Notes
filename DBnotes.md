# 数据库知识
## 第X章： Oracle 和PG数据库之间的差异

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

   ​