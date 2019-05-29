package com.meteor;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Iterator;

import org.jsoup.Connection.Response;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

@Component
public class HttpCrawlerRunner implements ApplicationRunner{

	@Override
	public void run(ApplicationArguments args) throws Exception {
		String url = "https://google.com";
		
		Document doc = Jsoup.connect(url).get();

		
		printImage(doc);
		
		System.out.println("asdasd");
		
	}
	
	private String downloadImage(String url) throws IOException {
	
		String [] urls = url.split("/"); 
		
		String fileName = urls[urls.length-1];
		fileName = fileName.split("\\?")[0];
		Response res = Jsoup.connect(url)
				.ignoreContentType(true)
				.execute();
		
		writeFile(fileName, res.bodyAsBytes());
		return fileName;
		
	}
	private void writeFile(String fileName, byte[] bytes) throws IOException {
		StringBuilder stb = new StringBuilder();
		stb.append("C://com/work/temp/");
		stb.append(fileName);
		File file = new File(stb.toString());
		if(!file.exists()) {
			file.createNewFile();
			
		}
		FileOutputStream fileOutputStream = new FileOutputStream(file);
		fileOutputStream.write(bytes);
		fileOutputStream.close();
	}
	
	private void printImage(Document doc) throws IOException {
		System.out.println("========printImage=========");
		Elements elements = doc.select("img");
		Iterator<Element> iter = elements.iterator();
		
		while(iter.hasNext()) {
			Element ele = iter.next();
			String src = ele.attr("data-src");
			if(src==null || "".equals(src)) {
				src = ele.attr("src");	
			}
			
			System.out.println( String.format("[%s]%s", src, ele.toString()) );
			
			try {
				String fileName = downloadImage(src); 
				ele.attr("src", fileName);	
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
		}
		
		writeFile("par.html", doc.html().getBytes());
		
//		doc.select("img").forEach(System.out::println);
	}
	

}
