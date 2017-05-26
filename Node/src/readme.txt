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
		
		
		