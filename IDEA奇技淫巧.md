# 一：IDEA

# 一：常见的快捷键

1. 快速打开控制台`Alt+4`



# 二：如何用IDEA导入一个Maven项目

点击左侧的File-new-project from existing sources 

选择pom文件，然后一路next即可



# 三：IDEA 使用外部tomcat热部署

顺带附带其他项目启动

其实很简单

![1530584260125](https://raw.githubusercontent.com/XHang/Notes/master/src/main/resources/IDEAPicture/1530584260125.png)

![1530584295113](https://raw.githubusercontent.com/XHang/Notes/master/src/main/resources/IDEAPicture/1530584295113.png)

![1530584398780](https://raw.githubusercontent.com/XHang/Notes/master/src/main/resources/IDEAPicture/1530584398780.png)

要选择第二个，才可以热部署

![1530584453322](https://raw.githubusercontent.com/XHang/Notes/master/src/main/resources/IDEAPicture/1530584453322.png)

这个可以选择外部和项目，这样启动连带外部的项目一起启动

![1530584551721](https://raw.githubusercontent.com/XHang/Notes/master/src/main/resources/IDEAPicture/1530584551721.png)

![1530584579593](https://raw.githubusercontent.com/XHang/Notes/master/src/main/resources/IDEAPicture/1530584579593.png)

选择第一个，当DEBUG时，就会弹出一个框，指示你想热部署还是重启什么的。

第二个也可以选，但是嘛，会经常性的热部署，有的电脑会很卡

# 四：让IDEA 显示所有的编译错误
尝试在Java编译器设置中启用“自动构建项目”？
貌似效果不是很明显，而且卡顿严重


# 使用IDEA内部的Debug或者Run按钮运行项目，新改代码没生效  
1. 解决办法很简单，找到Debug的运行配置，在最下方，有一个Before launch  
    选择BUild，双击，勾选那个xxx.war export 搞定  
    其实就是在点击那个按钮前，构建一下web应用。  

# 五：使用IDEA的 Live Templates生成代码

配置位置：直接搜索就行

怎么操作也很简单

下面介绍几个常用的模板

```
public static final Logger log = LoggerFactory.getLogger($CLASS$.class);

CLASS 变量名表达式 className()

快速生成log声明
```

第二个语法
```
 if ($parameter$.get$field$() !=null){
            $parameter$.set$field$( -$parameter$.get$field$()); 
}
```
参数说明
```
$parameter$   as        groovyScript("_1[0]", methodParameters())
field  as capitalize(camelCase(clipboard()))
```
效果
```
if (bill.getPayedfee() != null) {
            bill.setPayedfee(-bill.getPayedfee());
}
自动拿取方法的第一个参数，并填充到bill，然后从黏贴板拿到字符串，首先转驼峰，然后首字母大写。
其实复制数据库字段名就是这类的用法了

```

## 5.1 给人以鱼，不如授人予渔

其实`Live FileWater`难点只有一个，就是模板里面参数的使用

一般来说哪，它里面的可变参数有以下几种

1. `$END$` 代码生成后，鼠标跳转的位置

2. `$custom_variable$`  这是可变变量，变量名自己定义，而且还要指定这个变量的值是什么

   怎么操作自己摸索，重点是，变量的表达式有哪些，这才是本章的重点

   > 啰嗦了半天。。

好吧，直接介绍IDEA里面支持的表达式，

> 与其说是表达，倒不如说是内部提供的函数，支持嵌套调用哦
>
> 以下统称结果，即为表达式的结果

1. `clipboard()`    结果是系统剪贴板里面的内容

2. `camelCase(arg)`  传入一个参数，转成驼峰

3. `capitalize(arg)` 传入一个参数，将首字母改为大写

4. ` methodParameters()` 返回当前函数参数的列表

5. `fileName()` 返回当前文件名，包括扩展名

6. `groovyScript("you script",input_parameter)`

   这个是使用groovyScript脚本来处理变量，第二个参数即就是输入参数，支持内部的函数哦，比如说，传入

   `fileName()`就是对文件名进行处理啦



# 六：IDEA FileWater插件的使用

首先介绍下这个插件，它可以监听文件的改变，从而执行一些命令。

比如现在，我就在监听go文件的变化，并在变化的过程后生成easyJson的文件。

其实很容易用过，不过有一些特性需要着墨介绍下。

它可以根据scope语法，指定监听的文件范围，不在这个范围内的文件，就不会再监听了。

那么问题来了，这个scope语法格式是什么？

在此介绍答案

`file[项目名]:文件夹名//*&&!file:文件夹//*_easyjson.go`

里面的组成部分大概解释下

1. `//*` 此语法表示作用的效果包含子文件夹
2. `&&`是把两个范围条件组合起来。
3. `!`是非符号，意思就是说后面的范围语法要排除掉

举例子

`file[hello_world]:model//*&&!file:model//*_easyjson.go`

此语法的范围是`hello_world`项目里面的model文件夹及子文件夹，并且将model文件夹以及子文件夹里面以`_easyjson.go`结尾的文件忽略掉。






```

```