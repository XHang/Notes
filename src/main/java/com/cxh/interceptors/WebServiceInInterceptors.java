package com.cxh.interceptors;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import org.apache.cxf.binding.soap.SoapMessage;
import org.apache.cxf.phase.AbstractPhaseInterceptor;
import org.apache.cxf.phase.Phase;


public class WebServiceInInterceptors extends AbstractPhaseInterceptor<SoapMessage> {
	public WebServiceInInterceptors() {
		//在流全部读取完毕后调用
		super(Phase.POST_STREAM);
	}

	@Override
	public void handleMessage(SoapMessage message)  {
		InputStream in = message.getContent(InputStream.class);
		try {
			String msg = inputStreamConversionString(in, "utf-8");
			//替换特殊字符-power by stackOverFlow 
			msg = msg.replaceAll("[\\000]*", "");
			InputStream byteInput = new ByteArrayInputStream(msg.getBytes("utf-8"));
			//偷梁换柱，把系统原本的输入流替换为修改后的输入流
			message.setContent(InputStream.class, byteInput);
		} catch (Exception e) {
			e.printStackTrace();
		}finally{
			try{
					if(in!=null){
						in.close();
					}
				} catch (IOException e) {
					throw new RuntimeException("关闭输入流失败",e);
				}
		}
	}
	
	/**
	    * 将字节流转换成字符串
	    * @param in 需要转换的字节流
	    * @param characterSet字符编码
	    * @return 转换完毕的字符串
	    * @throws IOException
	    */
	      public static String inputStreamConversionString(InputStream in,String characterSet) throws IOException{
			   BufferedReader read = new BufferedReader(new InputStreamReader(in,characterSet));
			   StringBuilder sb = new StringBuilder();
			   String line = "";
			   while((line = read.readLine())  !=  null){
				   sb.append(line);
			   }
			   return sb.toString();
		}
}
