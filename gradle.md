# 安装

忽略，这事找个官方看一下就行，过于简单，不解释

# 概念

# gradle 命令

## 初始化项目

命令:`gradle init`

该命令将会在一个空文件夹里面生成一个gradle项目

> 该命令内部使用了`wrapper`任务来生成`Gradle wrapper script`，也就是gradlew

## 执行测试用例

命令:`gradlew check`

运行子项目的所有测试

## 运行项目

命令：`gradlew run`

首次运行时，会下载你目前使用的gradle版本到`~/.gradle/wrapper/dists`文件夹里面，所以比较慢

该命令会根据application配置块里面的`mainClass`，来运行你的程序

## 构建项目

命令：gradlew build

这要多亏application插件，它会捆绑应用程序和所有依赖项，然后生成两个文件，分别是

1. `app/build/distributions/app.tar`
2. `app/build/distributions/app.zip`

这些文件甚至还含有一个脚本，使用单个命令，就可以启用程序

# 文件解构

## `build.gradle`

构建脚本，可以是子项目的构建脚本，也可以是针对所有子项目都生效的构建脚本(在**buildSrc / build.gradle**里面)

内容示例(这里仅是一个子项目的构建示例，其他项目同理)

```
plugins {
    id 'demo.java-application-conventions'
}

dependencies {
    implementation 'org.apache.commons:commons-text'
    implementation project(':utilities')
}

application {
    mainClass = 'demo.app.App' 
}

repositories {
    gradlePluginPortal() 
}
```

| 关键字       | 解释                                                         |
| ------------ | ------------------------------------------------------------ |
| plugins      | 子项目应用的插件，本示例仅应用了一个约束性插件，这会从插件门户里面拿来Gradle核心插件(b比如说`application` or `java-library`)以及其他约束性插件或者社区插件来应用配置 |
| dependencies | 依赖项，这个依赖既可以是外部的，也可以是本项目的             |
| application  | 一种特殊的插件，应该只在为某个项目配置特定的东西时，写在特定项目的build.gradle。<br />application是一个application插件，它定义了APP项目的主类是`demo.app.App` |
| repositories | 这声明了一些存储库，如上所示，这使我们可以使用插件门户里面的插件 |



## settings.gradle

它用于定义构建的名称和子项目名的设置文件

内容示例

```
rootProject.name = 'first_gradle'
include('app', 'list', 'utilities')
```

| 关键字           | 解释                                                     |
| ---------------- | -------------------------------------------------------- |
| rootProject.name | 定义了构建名称，建议设置一个固定名称，不要和文件夹名相同 |
| include          | 定义了这个构建由三个子项目构成，可以后续追加子项目       |

## ${build.name}.java-common-conventions.gradle

该文件处于`buildSrc\src\main\groovy`

该文件的配置是针对整个项目而言的，对下面的子项目也生效

内容示例

```
plugins {
    id 'java'   //确定整个项目都使用java作为开发语言
}

repositories {
    mavenCentral()   //定义这个来作为外部依赖的源
}

dependencies {
    constraints { //定义依赖约束
        implementation 'org.apache.commons:commons-text:1.9' 
    }

    testImplementation 'org.junit.jupiter:junit-jupiter-api:5.7.1' //使用junit作为单元测试框架

    testRuntimeOnly 'org.junit.jupiter:junit-jupiter-engine' 
}

tasks.named('test') {
    useJUnitPlatform() 
}
```

其他的，比如说jvm的编译器标识，或者jvm的版本兼容性，都可以在这里设置

## ${guild.name}.java-some.conventions.gradle

这一般代表两个文件

`java-library-conventions`  `  java-application-conventions`

文件内容示例

```
plugins {
    id 'demo.java-common-conventions'  //指定该项目可以共享java-common-conventions里面的配置

    id 'java-library'  //这与application和lib的细节相结合
}
```

# 依赖项

依赖配置一般在build.gradle文件里面

有些文件可能没有，但是如果有，它应该有下面的代码块

```
...

dependencies {
    implementation 'org.apache.commons:commons-text'  //表示依赖了一个外部项目
    implementation project(':utilities') //表示依赖了本地子项目，在构建时会构建相关的依赖
}

...
```





# 创建项目

# gradle项目的结构

```
├── gradle //含有wrapper files的文件夹
│   └── wrapper
│       ├── gradle-wrapper.jar
│       └── gradle-wrapper.properties
├── gradlew  //	Gradle wrapper 的启动脚本
├── gradlew.bat  //定义构建的名称和子项目名的设置文件
├── settings.gradle //定义构建的名称和子项目名的设置文件
├── buildSrc
│   ├── build.gradle  //buildSrc 的构建脚本，用于配置子项目共有的构建逻辑的配置
│   └── src
│       └── main
│           └── groovy //用Groovy 或者Kotlin DSL编写的常规插件的文件夹
│               ├── demo.java-application-conventions.gradle
│               ├── demo.java-common-conventions.gradle
│               └── demo.java-library-conventions.gradle
├── app
│   ├── build.gradle  //三个子项目（app, list and utilities）的构建脚本
│   └── src
│       ├── main  // 每一个项目的源代码文件夹
│       │   └── java
│       │       └── demo
│       │           └── app
│       │               ├── App.java
│       │               └── MessageUtils.java
│       └── test 
│           └── java
│               └── demo
│                   └── app
│                       └── MessageUtilsTest.java
├── list
│   ├── build.gradle  
│   └── src // 每一个项目的源代码文件夹
│       ├── main 
│       │   └── java
│       │       └── demo
│       │           └── list
│       │               └── LinkedList.java
│       └── test 
│           └── java
│               └── demo
│                   └── list
│                       └── LinkedListTest.java
└── utilities
    ├── build.gradle 
    └── src // 每一个项目的源代码文件夹
        └── main 
            └── java
                └── demo
                    └── utilities
                        ├── JoinUtils.java
                        ├── SplitUtils.java
                        └── StringUtils.java
```

## 创建包含多个子项目的gradle项目

1. 创建一个空文件夹
2. 在这个空文件夹里面打开命令行界面，执行命令`gradle init`
3. 该命令执行中，会要求你选择
   - 项目类型为：` Application`
   - 实现的语言为：java
   - Split functionality across multiple subprojects选yes
   - 对于其他的问题，采用默认值就OK了



# 如何添加依赖

# 如何刷新依赖



