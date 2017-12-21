package com.npu.carsecretary.msghandling;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;

public class TalkToRobot {
	
	
	public static String receiveMsg(String sendMsg) throws IOException{
	      //接入机器人，输入问题
		 String APIKEY = "你的key";
	        String INFO = URLEncoder.encode(sendMsg, "utf-8");//这里可以输入问题
	        String getURL = "http://www.tuling123.com/openapi/api?key=" + APIKEY + "&info=" + INFO;
	        URL getUrl = new URL(getURL);
	        HttpURLConnection connection = (HttpURLConnection) getUrl.openConnection();
	        connection.connect();

	        // 取得输入流，并使用Reader读取
	        BufferedReader reader = new BufferedReader(new InputStreamReader( connection.getInputStream(), "utf-8"));
	        StringBuffer sb = new StringBuffer("");
	       
	        String line = "";
	        while ((line = reader.readLine()) != null) {
	            sb.append(line);
	        }
	        reader.close();
	        // 断开连接
	        connection.disconnect();
	        String[] ss = new String[10];
	        String s = sb.toString();
	        String answer;
	        ss = s.split(":");
	        answer = ss[ss.length-1];
	        answer = answer.substring(1,answer.length()-2);
	        return answer;
	             
		
	}

}
