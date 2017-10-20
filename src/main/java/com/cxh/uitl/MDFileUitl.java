package com.cxh.uitl;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.nio.MappedByteBuffer;
import java.nio.channels.ByteChannel;
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
		MappedByteBuffer mappedByteBuffer = channel.map(FileChannel.MapMode.READ_WRITE, 0, 0);
		ByteBuffer buffere = ByteBuffer.allocate(2046);
//		mappedByteBuffer.put
		file.close();
	}
}
