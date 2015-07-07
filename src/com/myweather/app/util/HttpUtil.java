package com.myweather.app.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import android.util.Log;



public class HttpUtil {
	/**
	 * 发送请求到服务器,  处理普通的请求
	 * @param address  请求地址
	 * @param listener  对返回结果进行处理的类
	 */
	public static void sendHttpRequest(final String address,final HttpCallbackListener listener){
		new Thread(new Runnable() {
			
			@Override
			public void run() {
				HttpURLConnection conn = null;
				try {
					System.out.println(address);
					URL url = new URL(address);
					conn = (HttpURLConnection) url.openConnection();
					conn.setRequestMethod("GET");
					conn.setConnectTimeout(8000);
					conn.setReadTimeout(8000);
					InputStream in = conn.getInputStream();
					BufferedReader br = new BufferedReader(new InputStreamReader(in));
					StringBuilder sb = new StringBuilder();
					String line ;
					while((line = br.readLine())!=null){
						sb.append(line);
					}
					br.close();
					if(listener!=null){
						listener.onFinish(sb.toString());
					}
					
					System.out.println(sb.toString());
					//Log.e("AAA", sb.toString());
				} catch (Exception e) {
					if(listener!=null){
						listener.onError(e);
					}
				}finally{
					conn.disconnect();
				}
			}
		}).start();
	}
	/**
	 * 用来连接百度api 接口
	 * @param address   例如: http://apis.baidu.com/apistore/aqiservice/aqi?city=%E5%8C%97%E4%BA%AC
	 * @param listener   回调接口
	 */
	public static void sendHttpRequestByBaiduAPI(final String address,final HttpCallbackListener listener){
		new Thread(new Runnable() {
			HttpURLConnection conn = null;
			@Override
			public void run() {
				try {
					URL url = new URL(address);
					conn = (HttpURLConnection) url.openConnection();
					conn.setRequestMethod("GET");
					conn.setRequestProperty("apikey", "1c03a1504ef3fcedd7d84ef8c6b2b0fa");
					conn.setConnectTimeout(5000);
					conn.setReadTimeout(5000);
					BufferedReader br =  new BufferedReader(new InputStreamReader(conn.getInputStream(),"UTF-8"));
					StringBuilder sb = new StringBuilder();
					String s = "";
					while((s = br.readLine())!=null){
						sb.append(s);
						sb.append("\r\n");
					}
					br.close();
					if(listener!=null){
						listener.onFinish(sb.toString());
					}
					Log.e("abc", sb.toString());
					
				}catch (IOException e) {
					
					if(listener!=null){
						listener.onError(e);
					}
				}finally{
					conn.disconnect();
				}
				
			}
		}).start();
		
	}
	
}
