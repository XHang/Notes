# consul 

# 一：它是什么

它是一个服务网格的提供商。可以把微服务注册到上面去，可以提供配置中心。

使用它，再加上外部的代码，就可以组成一个服务网格框架

# 二：入门

## 2.1 安装

## 2.2 运行它

consul有两种运行模式，客户端和服务端

客户端负责

1. 注册服务
2. 运行状态检查并将查询转发到服务器

客户端必须在运行consul的每个数据节点上运行，因为它是数据的实际来源



服务端负责

1. xxxx

以docker举例，想运行consul，可以运行此命令

```
docker run --net=host --name=consul-service --mount source=consul,target=/consul/data consul agent -server -bootstrap-expect=1 -node=consul-service-node -bind=127.0
.0.1 -client=192.168.99.102 -ui
```

解释几个参数

1. `-bind`consul绑定在哪个ip地址，后面如果有其他consul扩容，需要用到这个ip地址

2. `-client`接受客户端绑定地址，设置为`127.0.0.1`时只能本机访问。

   设置为`192.168.99.102`只有同个网段才可以访问

   