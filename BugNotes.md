# BUG日记

# 一：未规范的BUG



1. cglib作为代理类的生成，生成Controller代理类。如果原来的Controller的requestMapping方法有private的话，那么cglib无法代理这个方法，造成的结果就是当前端访问到这个接口时，无法读取到IOC注入的变量。其他public方法就可以造成访问。

2. Arrays.asList的方法返回的不是我们常见的java.util.ArrayList，而是java.util.Arrays.ArrayList。两种名字一样实际可大不同。后者仅仅是对数组的一种封装而不能进行修改操作，执行修改操作都报UnsupportedOperationException异常
     然后Arrays.asList方法里面参数只能放引用类型，不能放基本类型，否则都会解析成数组类型。
       例如Arrays.asList(1,2,4);会把1，2，4当做整体，即数组。长度为1.

3. 如果要传一个数组给后端，ajax请求要加一个参数，traditional: true
     RT

```
$.ajax({
				url: 'xxxx',  
				traditional: true,
				type: "get",  
				dataType: "json",
				data:{
					parameter_1 : id,
					parameter_2 : arr_1,
					parameter_2 : arr_2 
				}

```

这样才能传到后端，当然了，arr_1和arr_2都是js的数组对象。
原因：阻止jquery对参数的深度序列化

4. 今天突然发现的，用Junit测试框架测试多线程的并发问题会有造成线程运行运行着突然挂了。

得在main函数的测试下才是正常的

5. cxf webService客户端运行时报异常：

```
Unmarshalling Error: Illegal character (NULL, unicode 0) encountered: not valid in any content
```

报的位置在执行请求，获取响应的地方。

其原因是webService服务端返回的报文存在不规范的字符，基本都是Ascll码的控制字符，如退格键，或者铃声符，null之类的。

一旦xml存在这些特殊字符，连解析都做不到。

解决办法很简单

1. 怼服务提供方，叫他提供正确的xml报文。
2. 自己实现一个拦截器修改响应的报文，祛除特殊字符。

代码实现：---在本项目中查看附带原代码

6. 报错了

```
org.springframework.jdbc.UncategorizedSQLException: PreparedStatementCallback; uncategorized SQLException for SQL [{sql expression} ]; SQL state [0A000]; error code [0]; ERROR: cached plan must not change result type; nested exception is org.postgresql.util.PSQLException: ERROR: cached plan must not change result type
```

今天遇到这个报错信息了，意思很简单，就是缓存计划不得更改结果类型。

引起的原因是因为数据库的某个表的表结构发生改动，但是应用程序里面的缓存还是老的。

所以。。就挂了。

解决办法很简单，没有什么BUG是一次重启解决不了的，如果有，清缓存吧。

7. Maven 使用tomcat7:run命令时报如下异常

```
严重: The Filter [xxxxFilter] was defined inconsistently in multiple fragments including fragment with name [jar:file:/D:/MavenRepositoryCopy/priject/model/1.0.1/model-1.0.1.jar!/] located at [jar:file:/D:/MavenRepositoryCopy/priject/model/1.0.1/model-1.0.1.jar!/]
```

意思就是一个名字叫xxxxFilter的过滤器在多个片段重复出现，冲突了。
顺带一提，那个Filter文件里面写了@WebFilter注解。
解决办法就是找到一个冲突的Filter，重命名下Filter的名称即可

8. tomcat运行时报servlct类的某个方法找不到.
   可能原因除了类冲突外,还有可能是运行的tomcat版本太高,所以挂

9. 要注意数据库驱动和数据库软件之间的对应关系，如果版本对不上。
    就会爆许多莫名其妙的BUG，比如说，今天就爆了数据库连接不上。。。

10. log4j2配置文件写了，而且依赖也加了，但是就是不生效。

    原因是项目里面含有两个log4j2的文件。

    一个内容很少的配置文件生效了。

# 二：普通BUG

## 2.1 日志和测试依赖BUg

### BUG环境：

Spring Boot 应用中，添加了测试框架依赖

如下

```
 <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-test</artifactId>
            <scope>test</scope>
</dependency>
```

原先项目已经依赖了一个日志框架

```
<dependency>
                <groupId>org.springframework.boot</groupId>
                <artifactId>spring-boot-starter-log4j2</artifactId>
</dependency>               
```

然后Spring Boot 版本是`2.0.1.RELEASE`

最后测试代码是

```
@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(classes  = WwisWebApplication.class)
@WebAppConfiguration
public class KmCustomerControllerTest {
    @Test
    public void list() {

    }
}
```

### 引发BUG的操作

执行测试代码

### BUG 现象：

控制台报错

```
java.lang.StackOverflowError
	at java.lang.reflect.InvocationTargetException.<init>(InvocationTargetException.java:72)
	at sun.reflect.GeneratedMethodAccessor1.invoke(Unknown Source)
	at sun.reflect.DelegatingMethodAccessorImpl.invoke(DelegatingMethodAccessorImpl.java:43)
	at java.lang.reflect.Method.invoke(Method.java:498)
	at org.apache.logging.log4j.util.StackLocator.getCallerClass(StackLocator.java:112)
	at org.apache.logging.log4j.util.StackLocator.getCallerClass(StackLocator.java:125)
	at org.apache.logging.log4j.util.StackLocatorUtil.getCallerClass(StackLocatorUtil.java:55)
```

### BUG解决方案：

以上一个依赖改为

```
 <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-test</artifactId>
            <scope>test</scope>
            <exclusions>
                <exclusion>
                    <artifactId>logback-classic</artifactId>
                    <groupId>ch.qos.logback</groupId>
                </exclusion>
                    <exclusion>
                        <groupId>org.springframework.boot</groupId>
                        <artifactId>spring-boot-starter-logging</artifactId>
                    </exclusion>
            </exclusions>
        </dependency>
```



# 三：有趣的BUG

## 3.1 时区引起的时间戳转日期错误

 ### BUG 环境

测试环境，使用Linux机器，使用Tomcat运行项目

### 引发BUG的操作

后台以时间戳保存一个日期值，渲染在浏览器上面，是昨天的日期

### BUG现象

保存的时间戳，渲染出来的，是昨天的日期。

### BUG解决方案

先说下，数据的传输是浏览器选定一个时间，前台传来时间戳，后台将其包含在json中，保存到数据库

后面再到浏览器进行一次操作，将该时间戳连同json反序列化成实体类（时间戳被反序列化成Date类型），保存到数据库中。

最后，在显示数据库的信息时，那个数据戳表示的日期滞后一天。

怀疑1：代码问题，本地测试无法复现BUG，将本地代码部署至测试环境，挂，还是出现BUG

怀疑2：JSON序列化问题，其实归根结底，还是怀疑1一样，做了重复性工作。无终而返

怀疑3：了解了时间戳原理。

> 时间戳同个时刻，在地球的任何角落，都是一样。但是会因为时区的不同，渲染出来的时间也会有所不同。

所以怀疑是Linux机器的时区不对，跟客户机不是一样的时区。

先把出问题的时间戳用linux机器的命令转成日期，发现确实滞后一天了。

板上钉钉！

最后改了linux的时区，改成东八区(原先是标准区)。搞定

小插曲，改为linux的时区后，没重启服务器前，服务器还是认旧的时区，得重启生效。



## 3.2 保存一个日期后，浏览器显示日期滞后

直接说解决方案

数据库时区改正













