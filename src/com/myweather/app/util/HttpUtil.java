package com.myweather.app.util;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import android.util.Log;

public class HttpUtil {
	/**
	 * 发送请求到服务器,
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
					if(listener!=null){
						listener.onFinish(sb.toString(), in);
					}
					System.out.println(sb.toString());
					Log.e("AAA", sb.toString());
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
	
}
