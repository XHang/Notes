# 怎么删除weblogic已经创建好的域

步骤

1. 停止与域相关的进程和服务器

2. 找到这个文件`${Oracle_HOME}/domain-registry.xml`

   删除相关域的配置

   如这段配置：

   ```
   <domain location="/home/weblogic/Oracle/Middleware/Oracle_Home/user_projects/domains/base_domain"/>
   ```

   删除这段配置表示你准备删除名为`base_domain`的域

3. 找到这个文件`$WLS_HOME/common/nodemanager/nodemanager.domains`

   删除这段配置

   ```
   base_domain=/home/weblogic/Oracle/Middleware/Oracle_Home/user_projects/domains/base_domain
   ```

4. 删除域相关的应用程序和文件夹

   eg：`$MW_HOME/user_projects/applications/testDomain`  

   ​	and  `$MW_HOME/user_projects/domains/testDomain`

5. OK ,删除完毕！