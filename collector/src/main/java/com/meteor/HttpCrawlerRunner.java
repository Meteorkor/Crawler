package com.meteor;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URL;
import java.util.Iterator;
import java.util.List;

import org.jsoup.Connection.Response;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

@Component
public class HttpCrawlerRunner implements ApplicationRunner{

	private final Logger logger = org.slf4j.LoggerFactory.getLogger(HttpCrawlerRunner.class);
	
	private String parentDir;
	
	@Override
	public void run(ApplicationArguments args) throws Exception {
		
		List<String> urls = args.getOptionValues("url");
		
		List<String> dir = args.getOptionValues("dir");
		if(urls==null || dir==null) {
			//추후 메시지 처리 해야
			return;
		}
		parentDir = dir.get(0); 
		
		for(String url : urls) {
			Document doc = Jsoup.connect(url).get();
			printImage(doc, url);
		}
		
	}
	
	private String downloadImage(String url) throws IOException {
	
		String [] urls = url.split("/"); 
		
		String fileName = urls[urls.length-1];
		fileName = fileName.split("\\?")[0];
		Response res = Jsoup.connect(url)
				.followRedirects(true)
				.ignoreContentType(true)
				.execute();
		
		writeFile(fileName, res.bodyAsBytes());
		return fileName;
		
	}
	private void writeFile(String fileName, byte[] bytes) throws IOException {
		StringBuilder stb = new StringBuilder();
		stb.append(parentDir);
		stb.append("/");
		stb.append(fileName);
		File file = new File(stb.toString());
		if(!file.exists()) {
			file.createNewFile();
			
		}
		FileOutputStream fileOutputStream = new FileOutputStream(file);
		fileOutputStream.write(bytes);
		fileOutputStream.close();
	}
	
	private String getContextPath(String parentUrl) {
		int lastIdx = parentUrl.lastIndexOf("/");
		String sub = parentUrl.substring(0,lastIdx);
		sub += "/";
		return sub;
	}
	private String getRootPath(String parentUrl) {
		
		String tempUrls[] = parentUrl.split("//");
		
		String rootPath = tempUrls[1].split("/")[0];
		rootPath = tempUrls[0]+"//" + rootPath;
//		rootPath += "/";
		return rootPath;
	}
	
	private void printImage(Document doc, String parentUrl) throws IOException {
		Elements elements = doc.select("img");
		Iterator<Element> iter = elements.iterator();
		String parentUrlContextPath = getContextPath(parentUrl);
		String rootPath = getRootPath(parentUrl);
		while(iter.hasNext()) {
			Element ele = iter.next();
			String src = ele.attr("data-src");
			if(src==null || "".equals(src)) {
				src = ele.attr("src");	
			}
			if(src.startsWith("http")) {
				 
			}else if(src.startsWith("/")) {
				src = rootPath + src;
			}else {
				src = parentUrlContextPath + src;
			}
			
			
			
			
			logger.info(String.format("[%s]%s", src, ele.toString()));
			
			try {
				
				
				String fileName = downloadImage(src); 
				ele.attr("src", fileName);	
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
		}
		writeFile("par.html", doc.html().getBytes());
		
	}
	

}
