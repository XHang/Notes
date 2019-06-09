# postman的奇技淫巧

# 一：pre_request_script的使用

这个东西可以在请求执行前，对请求的一些要素进行修改

比如说

1. 自动添加请求头
2. 自动添加请求体
3. 自动添加URl参数

这里简单介绍下自动添加URL参数

附上脚本代码

```
//自动填充请求参数
var url = pm.request.url+""
var userParameter = 'parameterName=123'
if (url.indexOf("?")!=-1){
    url=url+"&"+userParameter
}else{
    url=url+"?"+userParameter
}
pm.request.url=url
```

这个脚本可以在请求发生前，为请求的URL加一个请求参数userParameter

pre_request_script可以设置为单个请求生效，也可以设置为整个集合的请求都生效

全局的，好像没有

