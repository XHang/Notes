# 微信公众号开发
# 一：常识
1. 每一个用户针对每个公众号都会产生一个OpenID

    如果要做到多个公众号，都认识同一个用户，则需要去微信开放平台，把你的应用和公众号绑定到某一个开放平台账号上。

    绑定后，对于同一个用户的不同应用，OpenID还是不一样

    但是对于同一个开放平台账号下的公众号和应用，它们的UnionID是一样的

2. 你提供给微信的每一个对外接口，微信访问它，是有次数限制的说。

3. 微信调用我们的接口时，会传一个`access_token`来标识它是微信。

    我们当然就要有这个`access_token`来做匹配了。

    那我们怎么拿`access_token`呢？当然是请求微信的接口了。

    另注：请求微信的接口拿`access_token`的次数有限，送完为止。

    所以你不能发疯一样去请求`access_token`的值，幸好`access_token`有效期是**两个小时**

    所以，还是把它存起来吧

4. 公众平台接口调用仅支持80端口。就是说，你写的接口开放出去的话，开放端口必须是80

    微信才能调用到你的接口



# 二：公众号能干什么

1. 群发消息，简而言之就是向用户群群发消息

2. 被动回消息：说白了就是

   1. 我向公众号问：你在么你在么？
   2. 公众号回：我不在

   当然公众号也可以当哑巴。

   实际的工作是这样的

   1. 向公众号发消息
   2. 微信服务器将该消息发送到开发者预选设置的服务器地址
   3. 开发者回复一个消息或者回复一个特殊消息告诉微信服务器，我不想搭理他

   往来消息可以加密的说

3. 客服消息：就是客户发一个消息，公众号可以在48小时内，再返回不定数量的消息给客户

4. 模板消息：公众号主动向某个客户发消息，使用模板定制消息内容。

5. 在公众号内打开一个网页，进行业务处理

   这个公众号网页，一般需要两个东西

   1. 获取用户的基本信息（需要经过用户同意）
   2. 微信JS-SDK  就是微信自己开发的一个前端框架，开发者需要用这个框架开发网页，才能让微信开发平台认可你的网页

# 三：千里之路，始于足下

接下来从无到有，开发一个 个人的公众号

## 3.1：注册一个微信公众号

这个太简单了，访问

`https://mp.weixin.qq.com/cgi-bin/registermidpage?action=index&lang=zh_CN&token=`

先注册一个订阅号玩一玩

## 3.2 公众号的基本配置

登录你的微信公众号，使用你单身多年的手速，滑到最低页，看到没，开发，这次的目标是基本配置-服务器配置

当然，在这一步，你必须要：

有一个服务器--->它有一个外网的IP-------->需要跑一个web项目------>在这个项目，有一个接口

(专门用于微信服务器验证)

那么，在公众号的基本配置中

1. 服务器地址就是专门用于微信服务器验证的接口地址
2. 令牌可以随意填写，仅用于微信服务器验证
3. EncodingAESKey（消息加密密钥）
4. 消息加密方式：当然选安全模式了，不过目前你我功力尚浅，还是明文吧

点击提交，OK，得到结果：验证Token失败/成功

失败有很多情况，但我想，最常见的一个情况，是你还没写用于验证微信服务器的那个接口

鉴于我的门派是`JAVA`，下面就用Controller的Action来演示如何写验证微信服务器的接口

**微信验证服务器代码的编写**

思路：

1. 从请求头参数中获取到nonce、timestamp、signature的参数值
2. 取得先前在公众号网页设置的token,并和nonce与timestam的值进行字典排序，最后连成一个字符串
3. 对该字符串进行SHA1算法摘要抽取（你可以认为是加密，虽然摘要算法并不是加密）
4. 对摘要算法得出来的字符串和signature参数值进行比较，如果完全一致，则认为是微信服务器发来的

几点提醒

1. 字典排序可以使用Collections.sort方法，把指定的字符串集合传进去，可自动进行字典排序

   （毕竟在Java中，字符串默认的排序就是字典排序）

2. SHA1算法取摘要值，你可以自己实现，也可以使用apache的轮子

   先添加如下依赖

   ```
    <dependency>
               <groupId>commons-codec</groupId>
               <artifactId>commons-codec</artifactId>
               <version>1.10</version>
    </dependency>
   ```

   再调用该方法

   `DigestUtils.sha1Hex`

   把得到的明文解码成字节数组，然后送进去得到摘要字符串

   值得注意的就这几点


## 3.3 小功能：公众号自动回复消息

这个其实要分成两部份

1. 我们的公众号要能接受到用户发来的信息
2. 我的的公众号要能回复用户发开的消息

针对这个，我们要写一个接口来完成。

首先说明几点

1. 微信发来的消息是以XML为数据格式的（为什么不是Json呢）
2. 微信请求我们的接口地址，是一开始我们配置微信验证服务器时，填的服务器地址
3. 微信请求我们的接口使用Http协议，Post请求

有这几点，。至少接受消息这一步是我们能做的了

```java
 @PostMapping
    public String processMsg(@RequestBody  TextRequest request){
        System.out.println("微信传来的文本消息是"+request.getContent());
        return null;
  }
```

就是这么简单，XML解析扔给MVC的消息转换器去实现了。所以这个方法就是拿到数据传输对象，直接处理了

> 值的一提的是，由于微信服务器请求我们接口的地址全都使用的是，验证服务器的接口地址
>
> 所以你一个对外开放的接口，就要处理诸如微信服务器验证，消息接受之类琐碎的事。
>
> 本来是应该在代码里面判断是哪种事件
>
> 不过刚好,验证服务器接口和消息接口的请求方式都不一样。
>
> 一个是GET
>
> 一个是POST



接下来要处理消息返回给客户，处理上需要利用请求的数据，

原本是请求方，现在要变成响应方，原本是响应方，现在要变成请求方

然后返回XML格式给微信服务器即可

代码大致如此

```java
 @PostMapping(produces ="application/xml" )
    public TextResponse processMsg(@RequestBody TextRequest request){
        System.out.println("微信传来的文本消息是"+request.getContent());
        TextResponse responseVo = HttpMessageUtil.converResponseDTO(request);
        log.info("参数MsgId是{}",request.getMsgId());
        responseVo.setContent("你好");
        return responseVo;
}
```

简单的可以，如果这都有问题，那就是你人太粗心大意了。。。比如说笔者我。。ORZ

还有，这个微信小功能是个只会说你好的复读机，毕竟人类的本质就是复读机

几点小常识

1. 客户通过微信服务器发信息给你的服务器，如果你的服务器在三秒内没有回应，则微信会进行重新发送

   重试的次数总共有三次，如果这三次你的服务器都不理不睬，则系统会告诉客户

   `该公众号暂时无法提供服务 `

2. 至少返回success或者空串告诉微信服务器，收到收到，over

## 3.4 从微信服务器获取访问Token

所谓访问Token， 是你每次主动请求微信服务器都要带过去的一个标识

没有这个标识的话，请求会被拒绝

关于这个Token，其实还是我们主动要去请求微信服务器的，不过这次请求就不需要Token了

需要的是你和微信服务器约定好的一些暗号。

包括

1. appid  微信服务器光明正大的告诉你的
2. secret  微信服务器偷偷摸摸告诉你的

外加一个小小的固定参数，发起GET请求，就能拿到Token

这个GET请求的URL长这样

`https://api.weixin.qq.com/cgi-bin/token?grant_type=client_credential&appid=xxxx&secret=xxxx`

最后你可能拿到这样的响应

```
{
	access_token =xxx, 
	expires_in = 7200
}
```

xxx就是我们拿到的token了

7200  当然就是过期时间了



访问Token当然有一个过期时间

到了这个过期时间，就需要重新请求。

然而一天请求的Token其实是有限的，你得省着点用，最好

1. 将Token缓存起来

2. 在失效之前一直用旧的Token

3. 失效的前几十秒请求一个新的Token，要加并发锁，或者确保只有一个线程访问到

   这样能使服务平滑过渡

> 我猜可以使用缓存框架外加任务调度工具以及Java并发编程来完成这一功能



## 3.5 为你的微信公众号画妆

其实就是加几个小按钮在微信公众号了

想这种功能，总觉得只需要在微信提供的页面上配置就行了

然而，一旦你配置了开发者配置，就只能老老实实的通过接口请求然后开发那啥了

毛！

好吧

初期设计很简单，就是我们请求自己服务器的接口

把菜单配置传上去。

服务器再把菜单配置搞到微信上

完美






















## 3.6 在公众号内加菜单

在公众号内，你可以加菜单，但是限于页面有限。

其实你只能加三个一级菜单，五个二级菜单

然后，一级菜单最多4个汉字，二级菜单最多7个汉字，多出来的部分将会以“...”代替

一个菜单可以实现各种各样的功能。这些功能有(摘自微信官网教程)

>```
>1、click：点击推事件用户点击click类型按钮后，微信服务器会通过消息接口推送消息类型为event的结构给开发者（参考消息接口指南），并且带上按钮中开发者填写的key值，开发者可以通过自定义的key值与用户进行交互；
>2、view：跳转URL用户点击view类型按钮后，微信客户端将会打开开发者在按钮中填写的网页URL，可与网页授权获取用户基本信息接口结合，获得用户基本信息。
>3、scancode_push：扫码推事件用户点击按钮后，微信客户端将调起扫一扫工具，完成扫码操作后显示扫描结果（如果是URL，将进入URL），且会将扫码的结果传给开发者，开发者可以下发消息。
>4、scancode_waitmsg：扫码推事件且弹出“消息接收中”提示框用户点击按钮后，微信客户端将调起扫一扫工具，完成扫码操作后，将扫码的结果传给开发者，同时收起扫一扫工具，然后弹出“消息接收中”提示框，随后可能会收到开发者下发的消息。
>5、pic_sysphoto：弹出系统拍照发图用户点击按钮后，微信客户端将调起系统相机，完成拍照操作后，会将拍摄的相片发送给开发者，并推送事件给开发者，同时收起系统相机，随后可能会收到开发者下发的消息。
>6、pic_photo_or_album：弹出拍照或者相册发图用户点击按钮后，微信客户端将弹出选择器供用户选择“拍照”或者“从手机相册选择”。用户选择后即走其他两种流程。
>7、pic_weixin：弹出微信相册发图器用户点击按钮后，微信客户端将调起微信相册，完成选择操作后，将选择的相片发送给开发者的服务器，并推送事件给开发者，同时收起相册，随后可能会收到开发者下发的消息。
>8、location_select：弹出地理位置选择器用户点击按钮后，微信客户端将调起地理位置选择工具，完成选择操作后，将选择的地理位置发送给开发者的服务器，同时收起位置选择工具，随后可能会收到开发者下发的消息。
>9、media_id：下发消息（除文本消息）用户点击media_id类型按钮后，微信服务器会将开发者填写的永久素材id对应的素材下发给用户，永久素材类型可以是图片、音频、视频、图文消息。请注意：永久素材id必须是在“素材管理/新增永久素材”接口上传后获得的合法id。
>10、view_limited：跳转图文消息URL用户点击view_limited类型按钮后，微信客户端将打开开发者在按钮中填写的永久素材id对应的图文消息URL，永久素材类型只支持图文消息。请注意：永久素材id必须是在“素材管理/新增永久素材”接口上传后获得的合法id。
>```

好了，不瞎逼逼了，开始写代码，这次要实现的功能是

访问我们服务器的某个接口，在微信公众号上创建几个菜单

其实逻辑很简单

写一个接口，接收菜单数据，服务器内进行数据校验，没问题，发给微信服务器，让它创建菜单

这样就OK了

。。。。

才怪

如果你的公众号是个人的，那么就没什么用，因为你压根就没有这种接口权限

怎么办

1. 停止服务器配置

2. 在微信公众号提供的页面上自行修改

3. 用测试账号，丢一个地址，自己去玩玩

   `http://mp.weixin.qq.com/debug/cgi-bin/sandbox?t=sandbox/login`

### 3.6.1  invalid button size hint

创建菜单时失败，微信服务器给的反馈是

`invalid button size hint: [dQpcPA04351958]】`

打印出来的请求数据是

```
{"button":[{"type":"click","name":"创建菜单","key":"CREATE_BUTTON"},{"name":"不要点！","sub_button":[{"type":"view","name":"谷歌呐","url":"http://www.google.com/"},{"type":"miniprogram","name":"wxa","url":"http://mp.weixin.qq.com","appid":"wx286b93c14bbf93aa","pagepath":"pages/lunar/index"},{"type":"click","name":"这里有个按钮","key":"V1001_GOOD"}]}]} 
```

请求地址是

```
【https://api.weixin.qq.com/cgi-bin/menu/create?access_token=18_DugTDIxR5JGR443ST_KG6i51TceKGeoj3Ra2TjLL5VZmRc19izkUTT0rA5ge81DGTUK1U7QnuKs-0HJa_-WOck2t5jLT8peJBBf8_VDBGeLXaSUF716VEkMWZUW1miuMSp5eypvzdcXjr5QhUSTeAEAGEF
```

用这个数据去官方提供的测试页面请求，发现没问题

猜测是程序问题，其实打印出来的请求数据不一定是真实的情况

所以呢，这次我们搬上`Wireshark`从网络层面查找问题

但是我们的请求是https，Wireshark拿不到加密后的数据

我们需要做点小动作，请看笔记后面的附录

## 3.7 菜单按钮触发事件

前置任务：

在微信公众号通过自定义创建按钮功能，建了一个点击类型的按钮。



这次要实现功能是，当用户点击公众号的一个按钮时，自动回复`不要动`

也就是按钮触发事件了

实际上，当用户点击公众号的一个按钮时，微信服务器会向我们的预留接口发送一段XML数据。

我们需要处理数据，然后返回消息给微信服务器

所以，不瞎比比了，直接码代码

然后特别说一点。官方文档没有说接收完事件后，该怎么返回消息给微信服务端

在`被动回复用户消息`有谈到

按教程去做就行

OK

这次比较简单





## 3.8 AccessToken的开发

目标：

1. 使用Ehcache存放请求得到的token，并在有效期前1分钟内，使用有效期内的Token
2. 在Token有效期结束前1分钟内，请求新的Token
3. 如有可能，token获取部分用同步锁锁住，避免并发获取Token，消耗请求次数

所用技术：

1. Ehchae 缓存框架存放AccessToken
2. 任务调度器触发前一分钟的AccessToken重新请求时间，使服务平滑过渡



### 3.7.1 Ehcache的使用

在使用Ehcache之前，有必要说下，其实在Java的缓存里，也有江湖的。

江湖的规范，就是一个叫做JCache的东西，是定义javax.cache API的规范，叫做Java临时缓存API（JSR-107）

而Ehcache，只是对JCache提出的规范的一种实现

我们当然不能只能使用Ehcache，而应该使用规范，这样以后想换其他缓存提供框架的话想换就换。

那么问题来了，怎么使用JCache呢？

1. 将`javax.cache:cache-api:1.x.y.jar`（其中`x.y`是依赖于版本的字符串）添加到classpath

   （当然是加依赖啦）

2. 加Ehcache到classpath中中，（这个还用多想吗？）

3. 编码，使用JCache 的api，它会自动找缓存提供程序的，我们只需要使用API就行

### 3.7.2  任务调度的实现：

以实现，具体看代码啦
















# 四：微信公众号服务的服务端架构推荐

   为了防止Token被并发获取，为了守护微信服务器的稳定，贯彻程序员的爱与和平（什么鬼）

   总之，一个良好的微信公众号服务端架构应该是酱紫的

   1. 一个业务处理端，处理实际业务

   2. 一个api-proxy服务端，专门用来接收-请求微信服务器的，将该层抽取出来可以进行调用频率的限制以及权限之类的配置

   3. Token中心控制端，所有微信服务的AccessToken都从该服务端获取，以避免重复获取Token

      (避免坑爹的微信服务器一天就只给你几次Token获取次数，用完那就拜拜了)

      为了防止并发获取，有必要加并发锁

   4. 一个前端服务器，就是专门用来显示页面的服务器

   以上内容

   仅供参考



# 附录

# 一：在Wireshark查看https内容

关键在于，你要有通信的SSL KEY 如此一来，把它设置到Wireshark里面

即可

但是我们用的是restTemplate

不清楚怎么拿到SSL KEY

关于这部分，我还需要再学习

请诸位等待，谢谢











