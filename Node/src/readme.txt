Maven的pom文件详解
	1：<modelVersion>4.0.0</modelVersion>描述这个项目遵从那个版本,最低是4.0.0,maven官方要求
	2：Maven项目聚合做法
		假设现在有项目A,项目A1,项目A2，要求项目A是负责聚合项目A1和A2，实现多个模块联合编译，实现起来很简单
		只需要在A的pom文件中，添加这么一段配置
		<modules>
   				<module>A1</module>
   				<module>A2</module>
		</modules>
		注：A1通过A项目的pom文档的artifactId而确定
		这样，编译A项目，就会把A1和A2项目一起编译
		接下来有个需求，项目A1和A2使用同一个依赖，难道要各自使用各自的依赖包吗？
		以上叫做模块聚合，接下来就是模块间的继承，这继承，第一个就是能子项目继承父项目引用的依赖包
		假设父项目的pom文件是这样的
		<modelVersion>4.0.0</modelVersion>  
		<groupId>com.Example.main</groupId>              
		<artifactId>Parent-Moduel</artifactId>       
		<version>1.0.2</version>            
		<packaging>pom</packaging>  
		<name>Simple-main</name>
		那么子项目就可以这么写：
		<parent>
   			<groupId>com.Example.main</groupId>
   			<artifactId>Parent-Moduel</artifactId>
   			<version>1.0.2</version>
   			<relativePath>../pom.xml</relativePath>  <!--本例中此处是可选的-->
		</parent>
		值得注意的是<relativePath>标签，如果pom的层次关系就像本例中的那样只隔一层，则可以省略这个。maven同样可以找到子pom。
		子pom中引入<parent>标签后，就会从父pom继承<version>等属性了
		父类添加这样的依赖：
		<dependencyManagement>
			   <dependencies>
			      <dependency>
			           <groupId>javax.servlet</groupId>
			          <artifactId>servlet-api</artifactId>
			          <version>2.5</version>
			      </dependency>
			   </dependencies>
		</dependencyManagement>
		子pom如果需要引用该jar包，则直接引用即可！不需要加入<version>，便于统一管理。当然如果你加version的话，表明这个依赖是子项目特有的
		然后插件也可以这么管理
		主项目：
		<build>
		   <pluginManagement>
		      <plugins>
		          <plugin>
		               <groupId>org.apache.maven.plugins</groupId>
		               <artifactId>maven-source-plugin</artifactId>
		               <version>2.1.1</version>
		          </plugin>
		      </plugins>
		   </pluginManagement>
		</build>
		子项目：
		<build>   
		   <plugins>
		      <plugin>
		           <groupId>org.apache.maven.plugins</groupId>
		           <artifactId>maven-source-plugin</artifactId>
		      </plugin>
		   </plugins>
		</build>
		不用加version了，便于管理。
		BigDecimal的学习
		构造函数
		BigDecimal(double val);
		BigDecimal(int val);
		BigDecimal(String val);
		分别可以将double，int，String代表的数字转成BigDecimal对象
		
		比较坑的一点是，项目的maven依赖视图能找到某依赖，但是在其pom文件找不到。这样编译时总是找不到某程序包，还以为出了什么鬼的bug，结果万万没想到，居然是没写依赖。。。
		
		HttpClient分两个阶段版本，有些时候下错了就悲剧了
		目前最新阶段的最新版本的依赖
			<dependency>
  		 <groupId>org.apache.httpcomponents</groupId>
    <artifactId>httpclient</artifactId>
    <version>4.5.2</version>
		</dependency>
		其HttpPost对象可以设置实体内容。而get不行！
		其HttpClient对象这么创建：CloseableHttpClient httpclient = HttpClients.createDefault(); 
		
--------------------------------------------------------------------------NOSQL--------------------------------------------------------------
NOSQL意为非关系型数据库，分为几种
1：基于键值对的-redis最强
2：列存储，Hbase、Cassandra这种
3：文档存储：MongoDB
4：图片存储：Neo4j、Versant
5：xml存储：Berkeley DB Xml还有XBASE，ORACLE很早已经支持这种存储方式了
--------------------1：redis的基础运用场景------------------
1：多web项目中共享一个session
2：分布式缓存，由于redis提供了几大语言的接口，比如java，.net,c等语言。
		因此对于异质平台间进行数据交换起到了作用，因此它可以用作大型系统的分布式缓存，
		并且其setnx的锁常被用于”秒杀“，”抢红包“这种电商活动场景中。
		
在Eclipse运行Maven命令没反应或者爆这个错：-Dmaven.multiModuleProjectDirectory system property is not set. Check $M2_HOME environment variable and mvn script match.
理由。安装的Maven太高级了，Eclipse不认识他，安装个旧版本的。
或者进入eclipse的Window->Preference->Java->Installed JREs->Edit，在Default VM arguments中设置-Dmaven.multiModuleProjectDirectory=$M2_HOME
告诉Eclipse去哪里找这个高版本的Maven..前提一定要设置M2_HOME这个环节变量啊，别告诉我你不会设置。

有时候下载依赖失败后再次下载就不行了。你得手动去删除依赖的那个文件夹的_maven.repositories和_lastUpdate....这两个文件，然后重新update项目。
还有，仓库有时候依赖是个pom文件，建议不要引用该依赖，要下那些有jar包的该依赖

2017-5-30日值6月1日bug

写接口时切记沟通发送报文和接受报文的编码！
接口一定要用HTTPClient测试一下！
Springmvc控制器方法上的produces 要注意一下，仅当Accept请求头和produces指明的媒体类型一致时，
比如说produces=text/plain   发来请求的请求头中Accept=text/plain  ，该请求才会被接受！，否则报错！、
坑爹的网上教程说该注解可以指定respode的返回数据类型。。。。

@ResponseBody  其返回的数据类型主要看你的classpath有没有相关的第三方jar包，比如说jackson.jar
这样的话返回的数据就会转成json格式输出
一般情况下开发的框架都是加了json相关的jar包，也就是说@ResponseBody返回的都是json数据。
所以要返回一个xml格式的。。。你还是老老实实的用响应流来输出吧。。
注：就算@ResponseBody返回一个字符串，就平台来说，也会给你字符串加上两个隐形的翅膀（字符串首尾加括号）
什么，你想setContentType("text/plain; charset=utf-8");
@ResponseBody到最后还是会变成application/javascript;charset=utf-8的。。。





		
		
		
		
		
		
		