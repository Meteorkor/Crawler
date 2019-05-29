package com.meteor;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

//@RunWith(SpringRunner.class)
//@SpringBootTest
public class CrawlerApplicationTests {

	@Test
	public void contextLoads() {
		String url = "Https://asd11.co.kr/asdas/aas";
		int lastIdx = url.lastIndexOf("/");
		
		String sub = url.substring(0,lastIdx);
		System.out.println("sub : " + sub);
	}

	private String getRootPath(String parentUrl) {
		
		String tempUrls[] = parentUrl.split("//");
		
		String rootPath = tempUrls[1].split("/")[0];
		rootPath = tempUrls[0] + rootPath;
		return rootPath;
	}
}
