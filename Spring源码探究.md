# 关于Action方法返回值
对于Action返回的对象类型，有可能是单个对象，也有可能是集合  
那么Spring是如何返回对应对象的JSON的？

首先，基本的请求接口是`DispatcherServlet` 类的`doDispatch`方法

`	mv = ha.handle(processedRequest, response, mappedHandler.getHandler());`

963行执行了我们的Action方法，返回了一个ModelAndView对象，进去看看

追踪到440行，也就是AnnotationMethodHandlerAdapter类的invokeHandlerMethod方法

该行请求了我们的方法，并得到了我们返回的对象



估计在948  `AnnotationMethodHandlerAdapter`这行是关键