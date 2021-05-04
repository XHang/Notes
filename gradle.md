# 安装

忽略，这事找个官方看一下就行，过于简单，不解释

# 概念

1. **Conventions**

   被翻译成约束，其实是gradle为了使各种应用的构建变得更加方便而做的一种机制

   要知道，gradle不止应用于java项目。

   针对java项目构建的一连套要做的事情，就是约束了

2. gradle有几个固定的构建阶段，或者称构建的生命周期

   1. 初始化： 设置好构建环境并决定有哪些项目将参与其中
   2. 配置：构造和配置任务执行的过程，然后根据运行的任务来决定要运行的子任务，以及顺序
   3. 运行：运行上一步配置好的任务

3. 构建脚本

   很容易就可以看出构建脚本，其实就是一些可执行的代码，但是，我们更多的是使用声明式的语句来执行这个过程。

   而不是直接把执行过程一整套写在构建脚本里。

   不过，理解一下怎么在构建脚本写这些执行过程也很有益处，这可以让你更熟悉gradle的API文档。

   也可以防患于未然，万一你接手的代码，里面的构建脚本全都是这种执行过程，你怎么看懂？

# 最佳实践

1. 良好的构建脚本应该更多的使用声明性配置而不是写一大堆的逻辑判断语句

   > 构建脚本，比如说build.gradle
   >
   > 就跟写接口一样，尽量在主逻辑中调用方法而不是写一堆程序来实现

   

   

   

# Gradle Wrapper

这是一个gradle构建的脚本，他使用项目自己的gradle进行构建，并且在必要的时候自己下载下来。

因此，你不需要费什么功夫下载gradle来配置开发环境，只需要运行这个脚本就行了

## 我该怎么添加Wrapper到新项目中

这需要你安装了gradle在你的电脑上，在这种情况下，你可以在gradle的`Build Setup tasks`任务列表中找到`wrapper`任务。

这样就可以在项目目录里面生成`Wrapper`文件了。

> 最好把这个文件添加到版本控制里面，以便其他开发人员可以用到。
>
> 只是，某些组织可能不允许提交二进制文件。。

生成的Wrapper文件有几个，分别位于gradle的目录和姓名的根目录中，下面说明这些文件的作用

1. `gradle-wrapper.jar`: 包含下载gradle的代码
2. `gradle-wrapper.properties`: Wrapper运行时需要用到的配置，但不是全部，比如说，代理相关的配置，就要在其他的配置文件中配置
3. `gradlew`**,** `gradlew.bat`： Wrapper的执行文件，用于执行构建

生成Wrapper后，可以在找到`gradle/wrapper/gradle-wrapper.properties`

它存储了该项目的gradle的一些相关信息，包含

1. 使用

## 使用Wrapper来运行项目

没什么好说的，直接就是一顿`gradlew build`就完事了。

官方文档总是在冷饭重炒。

另外，也尽量使用gradlew 来执行构建的一切任务，而不是 原始的gradle

## 升级Wrapper来适配最新的gradle

你就是想升级获得更多~~bug~~,特性，那怎么办呢？

1. 变更`gradle-wrapper.properties`文件中的`distributionUrl`属性

2. 运行`wrapper`任务，具体的话，就是`gradle wrapper --gradle-version 7.0`

   这样的话，就把Wrapper升级到了gradle 7.0

    不过，像`gradle-version`这样的可选项，还有一大把，比如说

   - **--distribution-type** 后面可以跟bin或者all，all就是把源代码也给你下载下来，你甚至可以debug gradle的内容
   - **--gradle-distribution-url**  这个比较屌，使用它，就废弃了`--distribution-type`和`gradle-version`这两个选项，因为它已经包含了后两者的内容了，如果你想自己托管gradle的发行版，然后从这个地方下载的话，那这个选项还是挺有用的
   - **--gradle-distribution-sha256-sum** 验证下载下来的gradle发行版

## 自定义Wrapper

好家伙，

# 任务
   task 是一个工作单元，gradle的构建，就是由多个task组建而成的一个任务链（或者称有向无环图）

   一个task，既可以是多个小task通过任务依赖机制组成，也可以是简单的执行某个动作。

   任务本身包括

   - 动作，就是做某事，比如说copy文件或者编译源代码
   - 输入，动作执行中需要用到的值，文件，以及目录
   - 输出，任务执行完毕修改或者生成的文件以及目录

   以上都是可选的，具体有没有做取决于任务执行的操作，有些任务，比如说[standard lifecycle tasks](https://docs.gradle.org/current/userguide/base_plugin.html#sec:base_tasks)

   甚至没有执行任何动作，它只是把多个任务聚合在一起而已

   > 这样做的目的可能仅为节省时间，毕竟，一次点击总比你多次点击要方便得多

    [incremental build](https://docs.gradle.org/current/userguide/more_about_tasks.html#sec:up_to_date_checks)

   gradle 的构建前不需要`clean`，因为它的增量构建是可用并且可靠的。

   gradle的增量构建会只构建变更的部分，没变更的会跳过。其原理是，检测任务的输入和输出是否已经被变更过。

   如果没有变更，gradle会认为该任务是最新，会跳过执行

   注意：至少要确保任务有一个输出，增量构建才能正常工作

   > 虽然一般来说，任务也至少有一个输出才对劲
   >
   > 另外，由于增量构建的存在，除非你确定你真的要执行clean操作，否则clean操作是没有必要再多余执行的了

## 自定义任务类型

gradle提供的任务类型已经不能满足你了，你可以自定义自己的任务类型，最好把源文件放在[*buildSrc*](https://docs.gradle.org/current/userguide/organizing_gradle_projects.html#sec:build_sources)目录或者打包在插件中。

然后，就像使用gradle的任务一样，使用你自定义的任务类型吧

## 自定义任务动作

原有任务你觉得不够好，想修饰一下？没问题

你可以写[Task.doFirst()](https://docs.gradle.org/current/dsl/org.gradle.api.Task.html#org.gradle.api.Task:doFirst(org.gradle.api.Action))或者[Task.doLast()](https://docs.gradle.org/current/dsl/org.gradle.api.Task.html#org.gradle.api.Task:doLast(org.gradle.api.Action))方法，在任务执行前或者执行后。执行你自己的构建逻辑

## 自定义任务和项目额外的properties

这些自定义的properties可以在你自定义的操作或者其他构建逻辑中用到，即使任务不是你写的，你也可以给它追加properties

## 自定义约束

约束是简化构建过程的一种方式，它非常好用，便于用户理解和使用。

你可以自己写一些插件，来配置构建中某些方面的默认值

## [A custom model](https://docs.gradle.org/current/userguide/implementing_gradle_plugins.html#modeling_dsl_like_apis)

将新概念添加到构建中，让构建不仅仅只是任务，文件，依赖这些东西。

这是通过 [*source sets*](https://docs.gradle.org/current/userguide/building_java_projects.html#sec:java_source_sets) 实现的

通知对构建进行适当的建模，可以大大提高构建的易用性和性能


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

根据项目的不同，它可能会生成不同的文件类型，对于多项目来说，生成的就是tar和zip，但是对lib来说，生成的就是jar，这个就得在`lib\build\libs`里面才能找到了

选项：

1. `-scan`

   构建时扫描，并把扫描结果放在网上，可以看到扫描过程发生的一些事情，比如说有哪些依赖，或者是构建过程中的内存占用情况

## 构建项目为jar

命令：``

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
version = '0.1.0'

tasks.named('jar') {
    manifest {
        attributes('Implementation-Title': project.name,
                   'Implementation-Version': project.version)
    }
}

java {
    withSourcesJar()
}
```

| 关键字         | 解释                                                         |
| -------------- | ------------------------------------------------------------ |
| plugins        | 子项目应用的插件，本示例仅应用了一个约束性插件，这会从插件门户里面拿来Gradle核心插件(b比如说`application` or `java-library`)以及其他约束性插件或者社区插件来应用配置 |
| dependencies   | 依赖项，这个依赖既可以是外部的，也可以是本项目的             |
| application    | 一种特殊的插件，应该只在为某个项目配置特定的东西时，写在特定项目的build.gradle。<br />application是一个application插件，它定义了APP项目的主类是`demo.app.App` |
| repositories   | 这声明了一些存储库，如上所示，这使我们可以使用插件门户里面的插件 |
| version        | 当打包成jar时，生成的jar文件后面可以携带版本号               |
| tasks.named... | 这一块代码可以在打jar包时，添加清单文件，也就是META-INF/MANIFEST.MF文件，<br />里面是一些键值对，内容就是上面attributes的内容，当然你可以乱写 |
| withSourcesJar | 在生成jar包时，也顺带打包源代码                              |



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

在多项目里面，该文件是必须的，因为要指明有哪些项目参与构建

在单项目里，该文件是可选的

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

## gradle.properties

设置代理



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



# 依赖

1. *configuration*：依赖有工作范围，有些依赖只用于编译中，有些依赖只用于运行中

   在grade里面，依赖的工作范围，被称为configuration
   
2. Artifact：构建后生成的文件或者目录，通常将这个用于其他项目，或者部署在主机系统上（在这种情况下，Artifact是单个文件）
   
   在被其他项目关联的情况下，Artifact是目录，这不用你讲Artifact发布上去
   
3. capability： 这个术语指的是一个组件或者多个组件提供的功能。通常通过` module versions.`类似的方式来声明组件具有某种
   
   `capability`；一个组件可以提供多个capability，而一个capability也可能被多个组件所提供（这种情况下不能同时使用这些组件，多余）
   
4. Component：[module](https://docs.gradle.org/current/userguide/dependency_management_terminology.html#sub:terminology_module).的任意一个单一版本，对于外部库来说，Component指的是库的发行版本
   
   在构建中，Component由插件定义，并且代表发版的文件。
   
   这里面包含`Artifact`以及描述这个`Component`的一些信息等
   
5. Configuration：将特定目标分组在一起命名的依赖项，提供对底层`module`以及`Artifact`的访问
   
6. `Dependency`: 不解释

7. Dependency constraint: 它定义了需求，能使项目正常运行而需要添加依赖项的需求

   这个东西可以缩小要使用依赖的版本集，或者缩小传递依赖的链条

   

   

   

   

   

   

   

   

## 如何添加依赖

## 如何刷新依赖

## gradle将如何下载您的依赖（仓库篇）

就跟maven一样，gradle从仓库中下载您的依赖。

所以，你需要配置一下仓库的地址



# 问题 

1. Could not reserve enough space for 2097152KB object heap

   复现操作： 刷新gradle项目
   
   尝试操作：下载安装64位的jdk，而不是32位
   
   成功

