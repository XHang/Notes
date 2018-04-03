
package com.cxh.gc;

import java.util.ArrayList;
import java.util.List;

/**
 * 程序目标：
 * 产生大量垃圾对象，然后启动时使用
 * 报告垃圾收集的数据信息
 * 附带试验的命令行
 * -XX:+PrintGCDetails 打印GC日志
 *-Xloggc:gc.txt	   GC日志存储到gc.txt
 *-XX:+UseConcMarkSweepGC  使用CMS垃圾收集器
 * @author cxh
 */
public class GCTest {
	public static void main(String[] args) throws Exception {
		List<Object> list = new ArrayList<Object>();
		int count=0;
		int point=0;
		while(true) {
			new Object();//建无引用的对象，触发Minor GC
			new Object();	
			new Object();	
			new Object();	
			new Object();	
			new Object();	
			new Object();	
			new Object();	
			new Object();	
			Object o = new Object();	
			list.add(o);	//对象添加进数组，使其有引用，可以提升到老年代，触发老年代Full GC
			Thread.sleep(100);
 			count++;

			//每隔100次去掉列表的一个对象，避免无节制的添加导致不断的Full GC
			if(count%500 == 0) {
				list=new ArrayList<Object>();
			}
		}
	}
}
