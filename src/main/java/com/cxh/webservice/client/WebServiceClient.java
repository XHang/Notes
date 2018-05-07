package com.cxh.webservice.client;

import java.util.ArrayList;
import java.util.List;

import org.apache.cxf.interceptor.Interceptor;
import org.apache.cxf.jaxws.JaxWsProxyFactoryBean;
import org.apache.cxf.message.Message;

import com.cxh.interceptors.WebServiceInInterceptors;

/**
 * 简单演示下 cxf 的拦截器配置和编写，该代码仅供参考，不可运行
 * @author Administrator
 *
 */
public class WebServiceClient  {
	public static void main(String[] args) {
		try {
			JaxWsProxyFactoryBean factory = new JaxWsProxyFactoryBean();
			factory.setServiceClass(Object.class/*webService服务端提供的Service*/);
			factory.setAddress("目标地址");
			//注册一个祛除特殊字符的拦截器
			factory.setInInterceptors(getInterceptor());
			Object service = (Object) factory.create();
			//service handler Business
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public static List<Interceptor<? extends Message>> getInterceptor() {
		List<Interceptor<? extends Message>> list = new ArrayList<>();
		list.add(new WebServiceInInterceptors());
		return list;
	}
}
