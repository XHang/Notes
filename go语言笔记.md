# go语言笔记

# 一：GO语言简述

首先，它是一门编程语言，它需要编译，然后才能执行。

它天生支持高并发应用

它由Google开发

OK，介绍完毕

# 二：入门

1. 构建开发环境

   首先下载go语言的编译器和执行环境
   
   `<https://golang.google.cn/dl/>`
   
   可以选择不同的系统下载不同的环境
   
2. 安装，这步就不用说了

3. 设置工作目录路径到环境变量里面

   为了上下文描述方便，暂时记工作目录为${work_path}

4. 构建`${work_path}\go\src\hello ` 文件夹路径

5. 在文件夹路径最下面构建一个go类型的文件，里面包含了这些东西

   ```go
   package main
   import "fmt"
   func main(){
   	fmt.Printf("hello, world\n")
   }
   ```

6. 在`${work_path}\go\src\hello`下面打开命令行，执行以下命令，编译源文件

   `go build `

7. 可以看到生成了一个exe文件，执行它看下效果吧

8. OK，一个hello world程序完毕

# 三：go语言的代码组织

一般我们把go代码，写在工作空间里面，这个工作空间的概念，类似于git上面的存储库。

工作空间有两个目录

1. src包含go源文件

   src里面包含go源代码和依赖项（也是go源代码）

   源代码都是放在不同的包（package）里面的

   类比java的package，我想你懂得的吧

2. bin包含可执行文件

