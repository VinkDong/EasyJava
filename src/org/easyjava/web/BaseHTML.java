package org.easyjava.web;

import java.util.List;
import java.util.Map;

import org.easyjava.file.EXml;

public class BaseHTML {
		public String initHtml(String html) {
			
			return html;
			
		}
		public String headHtml(String html){
			
			return html;
		}
		
		public String footHtml(String html){
			
			return html;
		}
		
		public String scanCss(String page){
			List<Map<String, String>> list = EXml.read(EGlobal.PATH+"/config.xml","css");
			String css = "";
			String level = "";
		    for (char x :page.toCharArray())
		    	if(x==47) level+="../";
			for(Map<String, String>ls : list){
				for(String x: ls.get("value").split(";"))
				css =css +"\t\t"+String.format("<link rel='stylesheet' href='%s' type='text/css' />\n ",level+x);
			}
			return css;	
		}
		
		public String pageCss(String page){
			List<Map<String, String>> list = EXml.read(EGlobal.PATH+"/config.xml","css-once");
			String css="",level = "";
		    for (char x :page.toCharArray())
		    	if(x==47) level+="../";
			for(Map<String, String>ls : list){
				if(!ls.get("page").equals(page)) continue;
				for(String x: ls.get("value").split(";"))
				css =css +"\t\t"+String.format("<link rel='stylesheet' href='%s' type='text/css' />\n ",level+x);
			}
			return css;		
			
		}
		
		/**
		 * 
		 * @param page \?
		 * @return
		 */
		public String scanJs(String page){
			List<Map<String, String>> list = EXml.read(EGlobal.PATH+"/config.xml","js");
			String js = "";
			String level = "";
		    for (char x :page.toCharArray())
		    	if(x==47) level+="../";
			for(Map<String, String>ls : list){
				for(String x: ls.get("value").split(";"))
				js =js +"\t\t"+String.format("<script type=\"text/javascript\" src=\"%s\"></script>\n",level+x);
			}
			return js;
		}
		
		public String pageJs(String page){
			List<Map<String, String>> list = EXml.read(EGlobal.PATH+"/config.xml","js-once");
			String js = "";
			String level = "";
		    for (char x :page.toCharArray())
		    	if(x==47) level+="../";			
			for(Map<String, String>ls : list){
				if(!ls.get("page").equals(page)) continue;
				for(String x: ls.get("value").split(";"))
				js =js +"\t\t"+String.format("<script type=\"text/javascript\" src=\"%s\"></script>\n ",level+x);
			}
			return js;		
		}
		
		public String completeHTML(String page){			
			page = page.substring(1);
			String html = 
					"<!DOCTYPE html>\n"
					+ "<html lang='en'>\n"
					+ "<head>\n"
					+ scanCss(page)
					+ pageCss(page)
					+ scanJs(page)
					+ pageJs(page)
					+ "\t</head>\n"
					+ "\t<body>\n"
					+ "\t</body>\n"
					+ "</html>\n";
			return html;
		}
		
		public String getHeader(String page){
			page = page.substring(1);
			String html = 
					  "<head>\n"
					+ "<meta charset='utf-8'/>\n"
					+ "<meta name='viewport' content='initial-scale=1'/>\n"
					+ "<meta name='description' content='EasyJava'/>\n"
					+ "<meta name='keywords' content='EasyJava'/>\n"
					+ "<meta name='generator' content='EasyJava'/>\n"
					+ "<title>在这里开启你的网站开发之路</title>\n"
					+ scanCss(page)
					+ pageCss(page)
					+ scanJs(page)
					+ pageJs(page)
					+ "\t</head>\n";
			return html;			
		}
}
