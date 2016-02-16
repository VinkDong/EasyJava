package org.easyjava.web;

import java.util.List;
import java.util.Map;

import org.easyjava.file.file;
import org.easyjava.file.xml;

public class baseHTML {
		public String initHtml(String html) {
			
			
			return html;
			
		}
		public String headHtml(String html){
			
			return html;
		}
		
		public String footHtml(String html){
			
			return html;
		}
		
		public String scanCss(){
			List<Map<String, String>> list = xml.read(global.PATH+"/config.xml","css");
			String css = "";
			for(Map<String, String>ls : list){
				for(String x: ls.get("value").split(";"))
				css =css +"\t\t"+String.format("<link rel='stylesheet' href='%s' type='text/css' />\n ",x);
			}
			return css;	
		}
		
		public String pageCss(String page){
			List<Map<String, String>> list = xml.read(global.PATH+"/config.xml","css-once");
			String css = "";
			for(Map<String, String>ls : list){
				if(!ls.get("page").equals("upgrade")) continue;
				for(String x: ls.get("value").split(";"))
				css =css +"\t\t"+String.format("<link rel='stylesheet' href='%s' type='text/css' />\n ",x);
			}
			return css;		
			
		}
		
		public String scanJs(String js){
			return js;
		}
		
		public String completeHTML(){
			String html = 
					   "<html>\n"
					+ "\t<head>\n"
					+ scanCss()
					+ pageCss("upgrade")
					+ "\t</head>\n"
					+ "\t<body>\n"
					+ "\t</body>\n"
					+ "</html>\n";
			return html;
			
		}
}
