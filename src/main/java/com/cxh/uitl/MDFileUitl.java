package com.cxh.uitl;

import java.io.File;
import java.io.RandomAccessFile;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;

public class MDFileUitl {
	
	/**
	 * 处理md文件的换行
	 * 即遇到换行符的话，并且前面没有两个空格的话，则往前面补充两个空格
	 * @param mdFilepath
	 * @throws Exception 
	 */
	public void dealMDLineFeed(String mdFilepath) throws Exception{
		RandomAccessFile file= new RandomAccessFile(new File(mdFilepath), "rw");
		FileChannel channel = file.getChannel();
		MappedByteBuffer mappedByteBuffer = channel.map(FileChannel.MapMode.READ_WRITE, 0, file.length());
		byte b=' ';
		int lave ;
		boolean flag = false;
		while(true){
			lave = mappedByteBuffer.remaining();
			if(lave <=0){
				break;
			}
			b = mappedByteBuffer.get();
			if(b == '\r' || b == '\n') {
				if(flag){
					continue;
				}
				//换行符向前推一个字节看是不是空格
				mappedByteBuffer.position(mappedByteBuffer.position()-1);
				if(mappedByteBuffer.get() == ' '){
					mappedByteBuffer.position(mappedByteBuffer.position()-2);
					if(mappedByteBuffer.get() == ' '){
						mappedByteBuffer.position(mappedByteBuffer.position()+2);
						flag=true;
						continue;
					}
					mappedByteBuffer.put((byte)' ');
					mappedByteBuffer.position(mappedByteBuffer.position()+2);
					flag=true;
					continue;
				}
				mappedByteBuffer.put((byte)' ');
				mappedByteBuffer.put((byte)' ');
				mappedByteBuffer.position(mappedByteBuffer.position()+1);
				flag=true;
				continue;
			}
			flag=false;
		}
		System.out.println("ok");
		file.close();
	}
}
