# 版本控制笔记

所谓版本控制，指的就是Git或者SVN那一类能对代码托管的软件。

不过SVN已经凉了，所以本笔记主要就是讲Git的使用

> SVN和GIT最大的不同点，就是GIT是分布式的，每个使用GIT的电脑都是一个GIT仓库。
>
> 这样就算中心的Git仓库挂了，还有千千万万个分布式的Git站起来。

# 一：Git快速入门

步骤

1. 安装，忽略，不会安装Git就告别程序员这门行业吧。亲

2. 配置个人信息，就是告诉Git以后每次提交的用户名是谁。

   通过以下命令来完成

   ```
   $ git config --global user.name "Your Name"
   $ git config --global user.email "email@example.com"
   ```

   > `--global` 表示全局配置哦，机器的每个仓库都得听这段配置的话

3. 创建一个版本库，亦或是称仓库

   1. 首先新建一个文件夹，就假定路径是`/folder`

   2. 进入这个文件夹，然后执行`git init`

      > 如果成功，则会有这段提示`Initialized empty Git repository in xxx/folder/.git/`
      >
      > 然后在文件夹里面，会多出一个`.git`的文件夹，这里面是跟踪代码的文件夹，没事别动它

4. 提交文件

   1. 想提交文件，首先你得有一个文件，create it！，当然是要在仓库里面创建

      > 假设创建的文件就叫file.txt

   2. 在文件夹所属的文件执行此命令`git add file.txt`  ,这将该文件加入到下次要提交的文件列表里面。

   3. 最后在仓库的任意文件夹执行`git commit -m "first commit"。`

      > 该命令可以将版本控制里面有改动的文件全部提交

      > -m 后面接的是本次提交的注释

      > 提交成功的反馈是`1 file changed, 1 insertion(+)`
      >
      > 意思是有一个文件改动提交了，改动的内容是添加一行

# 二： git常用命令

   `git checkout  file` 可以取消file里面的改动，把该文件里面的内容覆盖为版本库里面最新的内容

> 特殊情况是
>
> 1：文件已经添加到暂存区，但是未提交，此时check out 其实是没用的
>
> 需要使用 `git reset --hard HEAD` 此时工作空间的文件内容全部替换为最新版本的内容
>
> 2： 文件已经添加到暂存区，但是又进行了修改。
> 这个时候，使用这个命令，将会把这个文件的改动恢复跟暂存区里面的一样。

   `git status`命令可以让我们时刻掌握仓库当前的状况，比如说哪些文件改动过了

   `git diff`  可以查看当前仓库所有改动的文件内容

   > 如果文件名是中文，或者文件内容含中文，则这个命令显示出来的某些内容会乱码
   >
   > 在第三章将解决乱码问题

` git reset --hard HEAD~1` 将当前的仓库的版本回退到前一个版本。

> 如果单独使用`git reset`,可以将文件从暂存区解放出来。

> `HEAD`是当前仓库的版本，`HEAD~1`是回退到前一个版本，那么以此类推`HEAD~2`就是回退到前2个版本

> -hard 参数是强制回退

> 不过回退之后，再使用`git log`后会发现，当前版本之后的提交记录消失了。
>
> 如果想回到未来咋办？

1. 如果你知道未来的版本号，就是commit id，git log会显示的那一大串字符串代码，使用此命令

   `git reset --hard <commit id>`即可回退到未来。版本号没必要写全，只要git能区分开来就行。

2. 如果不知道未来的版本号，使用这个命令`git reflog`

   可以查看git 的命令历史，当然版本号也可以看出来。



## 2.1 git remote 远程仓库相关命令

`git remote remove 远程仓库名` 移除本地仓库和远程仓库的关联。

`git remote -v`  可以查看当前本地仓库关联的远程仓库。

返回的数据类似于

```
origin  git@github.com:XHang/test.git (fetch)
origin  git@github.com:XHang/test.git (push)
```

显示了`fetch`和`push`，命令时，使用的远程仓库。如果你的本地仓库不具备推送到远程仓库的能力。

你看不到push的

`git push origin master`

将当前工作空间的数据推送送名为`origin`远程仓库的`master`分支  

`git remote set-url origin  <URL>`  可以更改本地仓库对应的远程仓库 origin是远程仓库名 然后URL就是仓库地址了



## 2.2 git branch 分支相关命令

1. `git branch` 就是查看当前仓库有多少分支，标有`*`号就是当前仓库的分支

2. `git merge dev` 合并dev分支到当前分支

3. `git branch -d <name>`  删除指定分支

4. 将本地分支和远程分支关联

   命令``git push --set-upstream origin SpringDataJpa``

   需要改动的地方

   1. origin  远程仓库名 可以通过`git remote  -v  ` 看到
   2. SpringDataJpa 远程分支名 可以运行 `git branch -a` 查看远程所有分支 



## 2.3 标签的命令

`git show  <tagname>`  可以查看指定标签，打在了哪次提交上，注释是什么？

` git tag -a v0.1 -m "version 0.1 released" 1094adb`

​	为版本号为1094adb的提交记录打上v0.1的标签，注释为`version 0.1 released`

`git tag -d v0.1`  删除`v0.1`的标签

`git push origin v1.0`推送`v1.0`的标签到远程仓库

> `git push origin --tags`一次性推送本地的所有标签

`git push origin :refs/tags/<tagname>`    可以删除一个远程标签 



## 2.4 git 代理的配置

命令

`git config --global https.proxy http://127.0.0.1:1080`

酱紫即可



## 2.5 git 用户名和密码的设置

其实说起来，也没什么，只是之前一直设置的是全局的用户名和邮箱。

但是如果你同时拥有两个git账号，那么对于某账号的其中一个仓库，就需要单独设置用户名和密码了

命令要自己找









   

# 三：Git的奇技淫巧

## 3.1 git log 中文乱码

步骤

1. 设置系统的环境变量`LESSCHARSET=utf-8`
2. 好像可以了

## 3.2 某些文件名乱码

添加一些配置到`.gitconfig`文件中

```
[i18n]
	commitencoding = utf-8
	logoutputencoding = utf-8
[core]
	quotepath = false
[gui]
	encoding = utf-8
```

## 3.3 git diff 乱码

待定，我不会

## 3.4 仓库里面删除文件

就是说，你在文件管理器里面，把仓库里面的一个文件删除掉了。

这么做，有两种情况

1. 你确实想删掉这个文件

   好，这个时候，这个文件确实在文件夹里面被删掉了，但是git不知道诶。

   使用 `git rm file` and `git commit -m “delete”` 告诉git它被我删掉了

2. 你手贱

   想要恢复的话，可以`git checkout  file `  

3. 你不想删除本地文件，只想从git仓库里把它一笔勾销

   `git rm --cache <filepath>`


## 3.5 添加远程仓库

假定这个git仓库是github

1. 首先，在你的github仓库，添加一个仓库，这个都会吧,然后拿到一串仓库的地址，如

   `https://github.com/my-named/test.git` 记为<address>

2. 运行此命令`git remote add origin <address>`

   > 要在仓库文件夹下面执行，这样的话，这个仓库，就和远程仓库关联了

3. 将本地的仓库推送到远程仓库中,在仓库所在的文件夹执行

   `git push -u origin master`

   > `-u origin master`的意思是将远程的master分支和本地的master关联起来。
   >
   > 以后提交的时候，就可以直接git push

   你可能需要输入用户名和密码。。坑爹。。使用ssh认证吧

使用ssh认证

1. 在你的`Git Bash `或者是linux的shell界面里面。执行此命令

   `ssh-keygen -t rsa -C "youremail@example.com"` 一路回车即可

   > 邮箱要替换为你自己的邮箱，对，就是github的那个

2. 成功的话，可以在用户的主目录里面看到密钥文件`id_rsa`和公钥文件`id_rsa.pub`

3. 将公钥文件里面的内存，粘贴到github里面的`SSH Key `里面，添加进去

4. 最后，将本地仓库和远程仓库关联起来，使用ssh的方式

   `git remote add origin git@github.com:XHang/test.git`

5. 可以提交了，不需要密码了多爽！

## 3.6 git 忽略特殊文件

有些时候，你不想提交某些文件，但是执行git status 总是报告有未提交的改动。

某些强迫症患者肯定不开心了。

有什么解决办法呢？当然有，就是`.gitignore` 文件

该文件可以放到仓库内，git会自动读取该文件，过滤掉不需要提交的文件的。

那么这个文件里面的内容是什么呢？

如下

```
# Compiled class file
*.class
# Log file
*.log
# BlueJ files
*.ctxt
# Mobile Tools for Java (J2ME)
.mtj.tmp/
# Package Files #
*.jar
*.war
*.nar
*.ear
*.zip
*.tar.gz
*.rar
# virtual machine crash logs, see http://www.java.com/en/download/help/error_hotspot.xml
hs_err_pid*
.idea
```

这就是里面文件的内容，要过滤的是什么，我想大家都应该知道了吧。

> 1. 由于ignore的存在，所以有时候你想提交一个文件，但是提交不了，最大的可能就是该文件被忽略了。
>
>    你可以用 `git add -f file `强制添加该文件
>
>    或者用`git check-ignore -v file`看是ignore的哪一行过滤了该文件，有必要的情况下，修改规则。
>
>    该命令可能的结果如下
>
>    ```
>    .gitignore:3:*.class    App.class
>    ```
>
>    这就是说，在第三行，有一个`*.class`的过滤规则规律了这个文件



如果你的过滤规则要设置成全局的的话。

可以在全局的`.gitconfig`文件里面追加这一段配置

```
[core]
	excludesfile = ~/.gitignore
```

实际位置就是`C:/users/{myusername}/.gitignore`

> 但是你不能把这个路径直接put上去，否则配置文件会报错

其实就是指定全局的过滤文件的位置

以上。

另外多余说下，在windows里面，怎么创建一个`.gitignore` 的文件

其实很简单

先创建一个`.gitignore.`

然后。。。诶，就好了、

> power by stackoverflow.com

## 3.7 可以设置git命令的别名

暂时忽略，没有计划学这个。



## 3.8 克隆远程库

这一节的目的就是将git里面的远程仓库克隆到本地

第一步：

你要有远程仓库的地址.

可以是https协议：`https://github.com/XHang/test.git`

可以是ssh协议：`git@github.com:XHang/test.git`



第二步：执行这个命令

`git clone git@github.com:XHang/test.git`

可以在任何一个文件夹里面执行，执行完毕后，就会在该文件夹里面生成一个仓库的文件夹

这样clone出来的本地仓库会默认跟远程仓库同步起来。

所以`commit`后就顺手敲一个git push就可以推到远程仓库了



## 3.9 git 开启新分支

比如你现在有一个代码量很大的作业，今天改了50%的量，想直接提交，那可不行。

由于你提交的代码是未完成品，别人一使用你的代码出问题了咋办？

这个时候，就可以使用分支了，我们可以在本地开一个分支。把改动都提交到这个分支里面。

等到工作完成100%了，再合并分支到主分支里面，这样岂不是很好？

既然这么好，现在就来试验一下。

第一步：创建一个分支并切换到这个分支，使用这个命令

` git checkout -b dev`

> 以上命令相当这两个命令
>
> ` git branch dev`   //创建dev分支
>
> ` git checkout dev` 将工作空间切换到dev分支

第二步：放心在这个仓库进行修改然后不断的添加，提交吧。



第三步：把分支合并到主分支上面，使用此命令

`git merge dev`

这样就把dev分支合并到主分支了，当然，执行这个命令的时候，工作空间的当前分支必须是主分支



## 3.10 解决合并分支的冲突

假设你新建了一个分支，然后改了点东西，提交。

然后在主分支，也改了点东西，然后提交。

好巧不巧的是，改的地方都是同一行。

然后你尝试合并分支，要死，冲突了。咋办？

```
Auto-merging file.txt
CONFLICT (content): Merge conflict in file.txt
Automatic merge failed; fix conflicts and then commit the result.         --令人闻风丧胆的冲突信息
```

这个时候

冲突的文件已经用特殊的符号表示冲突的地方了。

如下所示

```
second branch commit
fuck
<<<<<<< HEAD
master conflict
=======
dev conflict
>>>>>>> dev
```

在这个冲突的文件自己修改下吧，比如说修改成下面这样

```
second branch commit
fuck
master conflict
```

修改完毕后，直接add然后commit吧。

然后你再运行这个命令

```
git log --graph --pretty=oneline --abbrev-commit
```

可以清楚的看到分支合并的情况。



## 3.11 BUG分支管理

情景介绍：你现在手头上有一个2天的代码量正在编写，突然，boss抛给你一个bug，要求你2小时内解决它。

问题来了，你现在的工作区里面的代码还没完成，提交不了，但是这个bug又需要在工作区内完成。

咋办？

解决办法：

1. 可以先把当前工作区的现场保存起来，使用此命令`git stash`

2. 在然后从master主分支创建一个BUG分支，解决完BUG合并主分支，顺便再把BUG分支删除

3. 切换到工作分支，恢复现场。`git stash apply` ,继续搬砖吧

   > 使用`git stash apply`，保存的现场没有被删除。
   >
   > 使用`git stash pop`  恢复现场并把快照删除。
   >
   > 另外，使用`git stash list` 可以查看保存的快照。
   >
   > 然后使用`git stash apply stash@{0}`可以恢复指定的现场



## 3.12 标签的使用

git中，有一个东西，叫标签，可以为某个提交记录起一个名字。

因为每次提交生成的版本号（commit id）又臭又长，不好记。

所以如果你要标记某个提交记录的话，就可以用label.

那么，怎么标记某个提交记录呢？

命令行`git tag v1.0` 这样就为当前分支，当前最新的提交记录打一个标签，叫`v1.0`了

使用``git tag` `可以查看所有标签

那如果你想为其他提交记录打标签呢？

1. 先找那次提交记录的版本ID

2. 使用这个命令行`git tag v0.9 f52c633`

   搞定

标签的其他知识

1. 默认情况下，push命令不会把本地的标签推送到远程
2. 删除本地和远程的标签得先删除本地的标签，然后再删除远程



## 3.13 为git设置ssh key

设置ssh key的作用是在git上进行某些敏感操作时（比如说push）不需要进行身份验证

要做到这一点

1. 生成一对秘钥对

   > 使用linux shell或者windows上面的git bash命令行，敲入
   >
   > ` ssh-keygen -t rsa -C "1083594261@qq.com"`
   >
   > 按提示操作，操作完毕后会在指定的目录生成私钥和公钥文件

2. 将公钥文件打开，复制公钥文件到git托管服务上

3. 将私钥保存在` /home/you/.ssh`文件夹下，对于window上，也是保存在个人文件夹下面的`.ssh`文件夹

4. 完毕

**出现问题**

1. 就算你做到了以上步骤，但是push项目时git还是要求你输入用户名和密码，这时候你要考虑你的项目的push路径是否正确

   一般来说，只有push路径是ssh协议的，才不需要身份验证

2. 如果你在生成ssh key时填入了密码，那么在每次push时，你仍然要录入ssh key的密码

   除非一开始你没用录入ssh key的密码

   没有其他方案能绕过录入ssh key这个动作。。



## 3.14 git 捡樱桃

什么捡樱桃，其实应该还算比较常见的应用场景了。

比如你开发了一个功能1，并且提交了

隔几天领导不要这个功能了，你只能不情愿的把它删除，再提交

但是，隔几天领导又想要这个功能了，于是你只能人肉补回代码吗？

不不不

使用`git cherry-pick commit1`命令，把`commit1`提交的数据合并到当前分支，并产生一条新的分支。

你只需要注意合并冲突就行了

捡樱桃什么的，还真是字如其名呢。。

## 3.15  git 暂存工作区

git 有一个功能，可以将你目前添加到工作区，但是还没提交的更改，给暂存起来，把工作区恢复到上一次提交的情况。

这种时候，就特别适合工作做到一半，突然间要你修改一个bug。

这时候你的工作还不能提交，所以最好就是把目前所做的更改占存起来，把工作区打扫的干干净净的。

修改BUG提交后，再恢复占存区，继续工作。

好了，不多逼逼了

**创建暂存工作区**

`git stash`



**删除暂存工作区**

git stash drop  xxx



**显示暂存工作区列表**

`git stash list --date=local`





## 3.16 从被Reset或者删除的分支恢复

如果你的分支被删除或者提交记录被掩盖了。怎么恢复代码呢？

有一个办法，基本思路是查看提交记录，然后从提交记录恢复代码





   

# 四：git的术语

`HEAD` : git中，用`HEAD`来指向当前分支，然后当前分支才是指向提交的

`commit id`:即提交ID，每次提交都会生成的一个复杂的字符串ID

> 用`git log` 可以查看每次提交的ID

暂存区：不知道能不能这么理解，就是每次用 `git add file`命令添加文件，实际是把文件添加到暂存区。

​	等到commit 后，再一次性把暂存区里面的文件提交过去。

​	如果你修改了文件却没add到暂存区，直接commit，那么是提交不过去的。



`Fast forward`  :这是一般情况下，git合并分支会采取的策略，就是快进模式 ，只是将master的指针指向合并的分支，所以合并的分支会很快。但是这样的模式，在删除分支后，分支的信息就没了。

如果要合并时要禁用`Fast forward`咋办？在合并时可以使用这个命令

`git merge --no-ff -m "merge with no-ff" dev`

> `--no-ff`  参数表示不使用`Fast forward`合并，而是使用普通的合并模式
>
> `-m "merge with no-ff"` 这个一看就是指定提交的信息，唔，因为这种合并方式最后是需要来一个提交的。
>
> 所以当然要指定提交信息
>
> 这样合并后的log能看出master曾经做过分支合并，而如果是



# 五：配置

1. 配置如果加`--global`参数是针对所有的仓库都有效，如果不加，只对当前仓库有效

2. 配置文件有两个存放目录，一个就是用户的主目录那里,叫 `.gitconfig` 

     一个就是仓库的`.git` 文件夹里,名称叫`config`

   配置文件的内容大致是

   ```
   [user]
   	name = stupid boss
   	email = example@gmain.com
   [i18n]
   	commitencoding = utf-8
   	logoutputencoding = utf-8
   [core]
   	quotepath = false
   [gui]
   	encoding = utf-8   
   ```

# 六 问题一览

## 6.1分支推送问题

今天推送某一个分支

报

```
fatal: The current branch SpringDataJpa has no upstream branch.
To push the current branch and set the remote as upstream, use

    git push --set-upstream origin SpringDataJpa
```

网上搜了一下，大概意思是说，该分支没有设置远程仓库的远程分支

解决办法很简单，git都给说了

`git push --set-upstream origin SpringDataJpa`

直接运行即可，命令已添加到章节2.2



## 6.2 PULL命令出现问题

出现以下问题

```
error: cannot lock ref 'xxx': ref xxx is at （一个commitID） but expected
```

大概出现的原因是本地git查看的ref和远程git的ref不匹配

可以通过这个命令清理不必要的文件并优化本地存储库

`git gc --prune=now`

然后再pull再试下



## 6.3 push爆413问题

意思就是说，你提交的文件数过多，体积过大，服务器拒绝了你这个提交请求。

不过一般来说，git的服务器是不会拒绝过大的push请求，所以，这个锅还是要有中间件，一般是Nginx来背。

解决办法很简单

1. Nginx针对git的提交，把限制提交的大小加大点

2. 分批次push

   先来看看第二个解决办法吧。。。

   首先要知道，push不能分批次提交，每个push，只能提交全部的数据上去。

   所以我们要走些弯路

   可以新建一个分支，然后把提交记录回滚到需提交的文件数比较少的时候，再提交上去。

   然后再把分支删除，接着合并，再提交剩下的。

   只是理论可行，实际上，我选了第三个方法

3. 使用ssh方式提交。。。

## 6.4 合并主分支到开发分支遇见问题

问题描述：`refusing to merge unrelated histories`

解决办法：`git merge master  --allow-unrelated-histories`

哈，它不允许，我让它允许不就得了，加一个参数`--allow-unrelated-histories`

OK



   

   

   

​    

