package com.myweather.app.service;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.IBinder;
import android.os.SystemClock;

import com.myweather.app.receiver.AlarmReceiver;
import com.myweather.app.util.HttpCallbackListener;
import com.myweather.app.util.HttpUtil;
import com.myweather.app.util.URLEncoderUtil;
import com.myweather.app.util.Utility;

public class AutoUpdateWeatherService extends Service{

	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		new Thread(new Runnable() {
			
			@Override
			public void run() {
				updateWeather(1);
				updateWeather(2);
				updateWeather(3);
				updateWeather(4);
				updateWeather(5);
			}

			
		}).start();
		AlarmManager am = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
		long triggerAtTime = SystemClock.elapsedRealtime()+6*3600*1000;
		Intent i = new Intent(this,AlarmReceiver.class);
		PendingIntent pd = PendingIntent.getBroadcast(this, 0, i, 0);
		am.set(AlarmManager.ELAPSED_REALTIME_WAKEUP, triggerAtTime, pd);
		return super.onStartCommand(intent, flags, startId);
	}
	public  void updateWeather(final int position) {
			SharedPreferences sp = getSharedPreferences(Utility.CITY_CONFIG+position, Context.MODE_PRIVATE);
			String code = sp.getString("c1", "");
			if(code!=""){
				HttpUtil.sendHttpRequest(URLEncoderUtil.getUrlEncoder(code, URLEncoderUtil.FORECAST_V),new HttpCallbackListener() {
					@Override
					public void onFinish(String response,InputStream in) {
						Utility.handleWeatherResponse(getApplicationContext(), response,position);
						update_real_weather(position);
						
					}
					
					@Override
					public void onError(Exception e) {
						//Toast.makeText(WeatherActivity.this, e.getMessage(), 0).show();
						e.printStackTrace();
					}
				});
			}
			
	}
	public void update_real_weather(final int position){
		SharedPreferences sp = getSharedPreferences(Utility.CITY_CONFIG+position, MODE_PRIVATE);
		final String url = sp.getString("c1", ""); //c1为天气编号   是即将返回的xml中的url属性
		String code = sp.getString("c4", "");
		String httpaddr = "http://flash.weather.com.cn/wmaps/xml/"+code+".xml";
		HttpUtil.sendHttpRequest(httpaddr, new HttpCallbackListener() {
			
			@Override
			public void onFinish(String response,InputStream in) {
				File file = new File(getFilesDir(),"xml");
				try {
					BufferedWriter bf = new BufferedWriter(new FileWriter(file));
					response = "<?xml version=\"1.0\" ?>"+response;
					bf.write(response);
					bf.close();
				} catch (IOException e) {
					e.printStackTrace();
				} 
				Utility.handleRealWeather(getApplicationContext(),url,position,file);
			}
			
			@Override
			public void onError(Exception e) {
				e.printStackTrace();
			}
		});
	}

}
