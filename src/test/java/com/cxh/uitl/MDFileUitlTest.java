package com.cxh.uitl;

import org.junit.Test;

public class MDFileUitlTest {
	
	@Test
	public void dealMDLineFeedTest() throws Exception{
		new MDFileUitl().dealMDLineFeed("D:\\readme.md");
	}
}
