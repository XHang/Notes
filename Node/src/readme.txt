
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


openssh安装后如何启动？
命令：/etc/init.d/ssh start  必须在su权限下运行

怎么用主机访问VirtualBox呢？
见图。用PuTTY即可访问

vin编辑器怎么使用？
用vi  文件路径  即可打开一个文本文件
初次进入是以命令行模式打开的，要编辑此文件，敲入i即可进入编辑模式。
编辑模式下按esc回退到命令行模式
将光标移动到某处，按dd即可删除该行
 「x」：每按一次，删除光标所在位置的“后面”一个字符。
「#x」：例如，「6x」表示删除光标所在位置的“后面”6个字符。
「X」：大写的X，每按一次，删除光标所在位置的“前面”一个字符。
「#X」：例如，「20X」表示删除光标所在位置的“前面”20个字符。
按下：号
输入wq!强制保存并退出。。


利用putt传文件到远程服务器
putt客户端下载下来一般都有那个pscp.exe文件。进入该文件对应的文件夹，敲入cmd命令
pscp 发送的源文件   服务器用户名@服务器地址:home 敲入后输入密码，即可发送！
eg:pscp jdk-8u131-linux-x64.rpm cxh@192.168.21.253:/home
注意：有时候发送过去但是找不到文件或者发送时提示permission denied
就是访问被拒绝了，这时候你得手动更改远程服务器的文件夹为可读可写  chmod 777 xxxx
注：pscp -r 后面指定文件夹名可以远程传输文件夹

dpkg -l 可以查看安装的软件列表
apt-get remove --purge 名字    可以删除软件

mv [选项] 源文件或目录 目标文件或目录

rm -rf  文件夹路径

tar -xzvf file.tar.gz

vi  /etc/proifle 在其末尾添加这几句
export JAVA_HOME="xxx"
export PATH="$PATH:$JAVA_HOME/bin"
export JRE_HOME="$JAVA_HOME/jre"
export CLASSPATH=".:$JAVA_HOME/lib:$JRE_HOME/lib"
即可设置jdk的环境变量。

source /etc/profile更新一下。。

经测试可以成功的本地依赖。。。
 <dependency>
            <groupId>jdk.tools</groupId>
            <artifactId>jdk.tools</artifactId>
            <version>1.7</version>
            <scope>system</scope>
            <systemPath>${JAVA_HOME}/lib/tools.jar</systemPath>
</dependency>


@RequestParam(value = "custom", required = false)可以非必传参数。
多个Springmvc参数，字符串形式，必须加@RequestParam("factoryCode")标注参数名
一个参数就不需要


平台的特性？ajax不加这个东西就给我返回一个字符串dataType : "jsonp"？
嗯？要获得json对象只能加这个属性

------------------------------------------注意分割线---------------------------------------------------
在任务交付之前，一定要检查一遍代码
包括但不限于
代码的健壮性（为空判断，空指针检查）
代码的隐患检查（没小心判断为空就直接调用）
代码的语义规范检查：包括变量名大小写规范，类名规范
功能性检查，能否完成功能。。
特别注意：拷贝后修改的代码更是要仔细检查一遍！	
代码的类名方法名是否注释是否过时！	
------------------------------------------注意分割线---------------------------------------------------	

------------------------------------------马云家代码规范笔记-----------------------------------
前记：只记我还不知道的部分，一些烂大街的规范大家都懂得吧，一个好程序员不仅程序写的6，规范也要6
1：
	常量命名全部大写（很少用到，权且一记）
2：
	抽象类命名使用Abstract或Base开头；异常类命名使用Exception结尾；测试类命名以它要测试的类的名称开始，以Test结尾。
3：
	数组定义请用 String []  args 而不要用 String args [] 
	为什么？第一个这样写很明显的，前者是字符串数组类型，后者是它的应用。第二个这么写，前者很明显是字符串类型，后者是其数组类型的一个引用。
	单从语句执行情况没有区别，但是前者把数据类型和引用分开了，更清晰。
	况且，使用后者，万一出现这种情况，String args[],str ;也许你写的时候知道str是字符串类型，但是后来的维护人员想骂街，一行定义两个数据类型，坑！
4：
	包名统一小写啊（开发很少需要写包名，自己写项目的时候就要注意了）
5：
	接口类中的方法和属性不要加任何修饰符号（public 也不要加）
	个人倒觉得无可厚非，加了更明显的标注接口的功能，再者，接口本来代码就不多。简洁嘛，也没多大必要
	标注是为了开发时注意点，个人写就随意了
6：
	自己添加，代码里面的公有常量最好封装在一个工具包的枚举中，
	枚举出现的原因就是为了服务公有变量，枚举不能实例化符合工具类不能实例化这个需求。
7：
	接口类后缀名是Service，实现类后缀名是ServiceImpl
8:
	枚举类名建议带上Enum后缀，枚举成员名称需要全大写，单词间用下划线隔开。
9:
	业务层数据层方法规范
	获取单个对象用getxxx
	获取列表对象用listxxxx
	获取总数什么的用countxxxx
	插入方法的用insert或者save
	删除方法用remove或者delete
	修改方法用update
	注意：不是说阿里云叫你这么做你就非得怎么做，遵守项目组的规范，看看前人写的方法名，
	照犬画狗即可，当然如果项目组没规范可讲，你就根据上面的规范吧。。。。
10：抛异常时想清楚这个异常是什么情况的，不要直接在方法名上throws Exception 就行了

11：额，那个，把不需要的引入去除掉。
12:接手或者修改别人代码之前注意看别人是怎么实现了。。。！
13:抛异常把异常信息说清楚点。。。还有封装代码时思考一下这么做值得吗？
		
	
		
		
		
		