package com.myweather.app.activity;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.Location;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.myweather.app.R;
import com.myweather.app.util.CharacterParser;
import com.myweather.app.util.HttpCallbackListener;
import com.myweather.app.util.HttpUtil;
import com.myweather.app.util.URLEncoderUtil;
import com.myweather.app.util.Utility;

public class WeatherFragment extends Fragment{
	/**
	 * 实时温度
	 */
	private TextView real_time_temp;
	/**
	 * 温度区间
	 */
	private TextView temp_range;
	/**
	 * 温度描述
	 */
	private TextView temp_desc;
	/**
	 * 风力描述
	 */
	private TextView wind_desc;
	/**
	 * 空气的描述
	 */
	private TextView air;
	/**
	 * pm2.5
	 */
	private TextView pm2_5;
	/**
	 * 今天是星期几
	 */
	private TextView week_today;
	/**
	 * 点击查看更多详细信息
	 */
	private TextView more_click;
	/**
	 * 更新时间与现在时间的差值
	 */
	private TextView update_time;
	
	
	/**
	 * 天气播报的地名
	 */
	private TextView address;
	/**
	 * 进度显示
	 */
	private ProgressDialog progressdialog;
	
	
	private TextView today;
	private TextView today_date;
	private ImageView img_today1;
	private ImageView img_today2;
	private TextView temp;
	
	private TextView tomorrow;
	private TextView tomorrow_date;
	private ImageView img_tomorrow1;
	private ImageView img_tomorrow2;
	private TextView tomorrow_temp;
	
	private TextView after_tomorrow;
	private TextView after_tomorrow_date;
	private ImageView img_after_tomorrow1;
	private ImageView img_after_tomorrow2;
	private TextView after_tomorrow_temp;
	View view;
	public int position;
	public String countycode;
	String c1;
	public WeatherFragment(int position){
		this(position, null);
	}
	
	public WeatherFragment(int position, String countycode) {
		this.position=position;
		this.countycode = countycode;
		setRetainInstance(true);
	}
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		if(savedInstanceState!=null){
			position = savedInstanceState.getInt("position");
		}
		view = inflater.inflate(R.layout.weather_layout	, null);
		return view;
	}
	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		initView();
		if("".equals(countycode)||countycode==null){
			showWeatherInfo(position);
		}else{
			update_weather(countycode,"county");
			countycode = "";
		}
		initListener();
	}
	@Override
	public void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		outState.putInt("position", position);
	}
	public void update_weather(String countycode,String type){
		getActivity().runOnUiThread(new Runnable() {
			
			@Override
			public void run() {
				update_time.setText("同步中...");
				
			}
		});
		queryWeatherBycountycode(countycode,type);
		
		
	}
	/**
	 * 从本地获取 天气信息文件  得到的数据赋值到各个控件
	 */
	private void showWeatherInfo(final int position) {
		SharedPreferences sp = getActivity().getSharedPreferences(Utility.CITY_CONFIG+position, getActivity().MODE_PRIVATE);
		c1 = sp.getString("c1", "");
		if(c1.equals("")){
			if(position==1){
				/*AlertDialog.Builder builder = new Builder(getActivity());
				builder.setTitle("提示");
				builder.setMessage("由于手机定位功能尚未开发,所以需要您自己指定您所在的城市,为您带来的不便,我深感抱歉!");
				builder.setNegativeButton("OK", new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						
					}
				}
				);
				builder.show();*/
				location_updateweather();
				
			}else if(position==2){
				update_weather("010101","county");//bj
			}else if(position==3){
				update_weather("020101","county");//sh
			}else if(position==4){
				update_weather("280601","county");//sz
			}else if(position==5){
				update_weather("280101","county");//gz
			}
			
		}else{
			String city_name = sp.getString("c3", "");
			address.setText(city_name);
			
			
			//需要从实时天气接口中读取
			sp = getActivity().getSharedPreferences(Utility.REAL_WEATHER_CONFIG+position, Context.MODE_PRIVATE);
			String temp1 = sp.getString("temp1", "");  //温度a
			String temp2 = sp.getString("temp2", "");  //温度b
			String temNow = sp.getString("temNow", "");  //描述
			String stateDetailed = sp.getString("stateDetailed", "");
			String current_time = sp.getString("current_time", "");
			
			temp_desc.setText(stateDetailed);
			wind_desc.setText(sp.getString("windState", ""));
			temp_range.setText(temp1+"/"+temp2+"°C");
			week_today.setText(Utility.getTodayOfWeek(new Date()));
			update_time.setText(Utility.getUpdate_time(current_time));
			real_time_temp.setText(temNow);
			
			
			SimpleDateFormat sf = new SimpleDateFormat("M月dd日");
			Date date = new Date();
			int day = date.getDay();
			
			Calendar c = Calendar.getInstance();
			c.setTime(new Date());
			
			//拿到该城市未来三天的天气信息
			sp = getActivity().getSharedPreferences(Utility.WEATHER_CONFIG+position, Context.MODE_PRIVATE);
			
			String level = sp.getString("level", "");
			String aqi = sp.getString("aqi", "");
			String core = sp.getString("core", "");
			if("".equals(core)){
				pm2_5.setText("aqi:"+aqi+" 无主要污染物");
			}else{
				pm2_5.setText("aqi:"+aqi+" 主要污染物:"+core);
			}
			
			air.setText("空气"+level);
			
			//当天
			today.setText("今天");
			today_date.setText(sf.format(new Date()));
			String img1 = "d"+sp.getString("fa1", "99");
			String img2 = "n"+sp.getString("fb1", "");
			
			
			img_today1.setImageResource(Utility.getImage(img1));
			img_today2.setImageResource(Utility.getImage(img2));
			temp.setText(temp1+"~"+temp2+"°C");
			
			//第二天
			c.add(Calendar.DATE, 1);
			tomorrow.setText(Utility.getTodayOfWeek(c.getTime()));
			tomorrow_date.setText(sf.format(c.getTime()));
			
			String img3 = "d"+sp.getString("fa2", "");
			String img4 = "n"+sp.getString("fb2", "");
			img_tomorrow1.setImageResource(Utility.getImage(img3));
			img_tomorrow2.setImageResource(Utility.getImage(img4));
			tomorrow_temp.setText(sp.getString("fc2", "")+"~"+sp.getString("fd2", "")+"°C");
			
			//第三天
			c.add(Calendar.DATE, 1);
			after_tomorrow.setText(Utility.getTodayOfWeek(c.getTime()));
			after_tomorrow_date.setText(sf.format(c.getTime()));
			String img5 = "d"+sp.getString("fa3", "");
			String img6 = "n"+sp.getString("fb3", "");
			img_after_tomorrow1.setImageResource(Utility.getImage(img5));
			img_after_tomorrow2.setImageResource(Utility.getImage(img6));
			after_tomorrow_temp.setText(sp.getString("fc3", "")+"~"+sp.getString("fd3", "")+"°C");
		}
	}
	public void location_updateweather() {
		update_time.setText("同步中...");
		Location location = Utility.getLocation(getActivity());
		String address = "http://api.map.baidu.com/geocoder?location="+location.getLatitude()+","+location.getLongitude()+"&output=json&key=8cIYigmRBuU8IcquWLHvSAIB";
		HttpUtil.sendHttpRequest(address, new HttpCallbackListener() {
			
			@Override
			public void onFinish(String response) {
				String city = Utility.handlerLocationGetWeather(response);
				String city1 = "";
				if(city.equals("")){
					Toast.makeText(getActivity(), "读取位置信息错误", 0).show();
					return;
				}
	        	if(city.contains("市")){
	        		city1 = city.substring(0, city.indexOf("市"));
	        	}
	        	String py = CharacterParser.getInstance().getSelling(city1);
				Log.e("abc","city:"+CharacterParser.getInstance().getSelling(city1) );
				update_real_weather(position,py,city);
			}
			
			@Override
			public void onError(Exception e) {
				// TODO Auto-generated method stub
				
			}
		});
	}

	/**
	 * 访问服务器  获取返回数据
	 * @param code
	 * @param type
	 */
	private void queryWeatherBycountycode(String code,String type) {
		//http://www.weather.com.cn/data/list3/city190601.xml
		//http://www.weather.com.cn/data/cityinfo/101190601.html
		
		//获取灰常详细的天气信息  
		//http://m.weather.com.cn/atad/101010100.html
		update_time.setText("同步中...");
		if("county".equals(type)){
			String address = "http://www.weather.com.cn/data/list3/city"+code+".xml";
			HttpUtil.sendHttpRequest(address,new HttpCallbackListener() {
				
				@Override
				public void onFinish(String response) {
					String strs[] = response.split("\\|");
					queryWeatherBycountycode(strs[1],"weather");
				}
				
				@Override
				public void onError(Exception e) {
					e.printStackTrace();
				}
			});
		}else if("weather".equals(type)){
			HttpUtil.sendHttpRequest(URLEncoderUtil.getUrlEncoder(code, URLEncoderUtil.FORECAST_V),new HttpCallbackListener() {
				
				@Override
				public void onFinish(String response) {
					Utility.handleWeatherResponse(getActivity(), response,position);
						
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
		SharedPreferences sp = getActivity().getSharedPreferences(Utility.CITY_CONFIG+position, getActivity().MODE_PRIVATE);
		final String url = sp.getString("c1", ""); //c1为天气编号   是即将返回的xml中的url属性
		final String code = sp.getString("c4", "");
		final String pmcode = sp.getString("c5", "");
		String httpaddr = "http://flash.weather.com.cn/wmaps/xml/"+code+".xml";
		HttpUtil.sendHttpRequest(httpaddr, new HttpCallbackListener() {
			
			@Override
			public void onFinish(String response) {
				File file = new File(getActivity().getApplication().getFilesDir(),"xml");
				try {
					BufferedWriter bf = new BufferedWriter(new FileWriter(file));
					response = "<?xml version=\"1.0\" ?>"+response;
					bf.write(response);
					bf.close();
				} catch (IOException e) {
					e.printStackTrace();
				} 
				Utility.handleRealWeather(getActivity(),url,position,file);
				
				update_pm2(position, pmcode);
			}
			
			@Override
			public void onError(Exception e) {
				e.printStackTrace();
			}
		});
	}
	/**
	 * 定位的时候先调用更新实时天气的方法 用来获取weatherid 
	 * 之后将会在方法里调用 update_weather(weatherid, "weather") 走正常流程
	 * @param position  第几座城市
	 * @param py     城市的名称  (拼音)
	 */
	public void update_real_weather(final int position,final String py,final String city){
		String httpaddr = "http://flash.weather.com.cn/wmaps/xml/"+py+".xml";
		HttpUtil.sendHttpRequest(httpaddr, new HttpCallbackListener() {
			
			@Override
			public void onFinish(String response) {
				File file = new File(getActivity().getApplication().getFilesDir(),"xml");
				try {
					BufferedWriter bf = new BufferedWriter(new FileWriter(file));
					response = "<?xml version=\"1.0\" ?>"+response;
					bf.write(response);
					bf.close();
				} catch (IOException e) {
					e.printStackTrace();
				} 
				String weatherid = Utility.handleRealWeather(getActivity(),position,file,py,city);
				update_weather(weatherid, "weather");
			}
			
			@Override
			public void onError(Exception e) {
				e.printStackTrace();
			}
		});
	}
	public void update_pm2(final int position,String city){
		String address;
		try {
			address = "http://apis.baidu.com/apistore/aqiservice/aqi?city="+URLEncoder.encode(city,"utf-8");
			HttpUtil.sendHttpRequestByBaiduAPI(address, new HttpCallbackListener() {
				
				@Override
				public void onFinish(String response) {
					Utility.handlerPM2(getActivity(), position, response);
					getActivity().runOnUiThread(new Runnable() {
						
						@Override
						public void run() {
							showWeatherInfo(position);
						}
					});
				}
				
				@Override
				public void onError(Exception e) {
					e.printStackTrace();
				}
			});
		} catch (UnsupportedEncodingException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		
	}
	/**
	 * 初始化各种监听事件
	 */
	private void initListener() {
		/**
		 * 点击更多详情
		 */
		more_click.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(getActivity(),DescWeatherInfoActivity.class);
				intent.putExtra("position", position);
				intent.putExtra("weatherid", c1);
				getActivity().startActivity(intent);
			}
		});
	}


	/**
	 * 初始化视图
	 */
	private void initView() {
		
		
		real_time_temp = (TextView) view.findViewById(R.id.real_time_temp);
		temp_range = (TextView) view.findViewById(R.id.temp_range);
		temp_desc = (TextView) view.findViewById(R.id.temp_desc);
		wind_desc = (TextView) view.findViewById(R.id.wind_desc);
		air = (TextView) view.findViewById(R.id.air);
		pm2_5 = (TextView) view.findViewById(R.id.pm2_5);
		week_today = (TextView) view.findViewById(R.id.week_today);
		more_click = (TextView) view.findViewById(R.id.more_click);
		update_time = (TextView) view.findViewById(R.id.update_time);
		address = (TextView) view.findViewById(R.id.address);
		
		today = (TextView) view.findViewById(R.id.today);
		today_date = (TextView) view.findViewById(R.id.today_date);
		img_today1 = (ImageView) view.findViewById(R.id.img_today1);
		img_today2 = (ImageView) view.findViewById(R.id.img_today2);
		temp = (TextView) view.findViewById(R.id.temp);
		
		tomorrow = (TextView) view.findViewById(R.id.tomorrow);
		tomorrow_date = (TextView) view.findViewById(R.id.tomorrow_date);
		img_tomorrow1 = (ImageView) view.findViewById(R.id.img_tomorrow1);
		img_tomorrow2 = (ImageView) view.findViewById(R.id.img_tomorrow2);
		tomorrow_temp = (TextView) view.findViewById(R.id.tomorrow_temp);
		
		after_tomorrow = (TextView) view.findViewById(R.id.after_tomorrow);
		after_tomorrow_date = (TextView) view.findViewById(R.id.after_tomorrow_date);
		img_after_tomorrow1 = (ImageView) view.findViewById(R.id.img_after_tomorrow1);
		img_after_tomorrow2 = (ImageView) view.findViewById(R.id.img_after_tomorrow2);
		after_tomorrow_temp = (TextView) view.findViewById(R.id.after_tomorrow_temp);
		
		/*big_after_tomorrow = (TextView) findViewById(R.id.big_after_tomorrow);
		big_after_tomorrow_date	 = (TextView) findViewById(R.id.big_after_tomorrow_date);
		img_big_after_tomorrow = (ImageView) findViewById(R.id.img_big_after_tomorrow);
		big_after_tomorrow_temp = (TextView) findViewById(R.id.big_after_tomorrow_temp);
		
		real_big_after_tomorrow = (TextView) findViewById(R.id.real_big_after_tomorrow);
		real_big_after_tomorrow_date = (TextView) findViewById(R.id.real_big_after_tomorrow_date);
		img_real_big_after_tomorrow = (ImageView) findViewById(R.id.img_real_big_after_tomorrow);
		real_big_after_tomorrow_temp = (TextView) findViewById(R.id.real_big_after_tomorrow_temp);*/
	}
	
	/**
	 * 显示进度对话框
	 */
	private void showProgressDialog() {
		if(progressdialog==null){
			progressdialog = new ProgressDialog(getActivity());
			progressdialog.setMessage("正在加载...");
			//设置不能按返回取消
			progressdialog.setCanceledOnTouchOutside(false);
		}
		progressdialog.show();
	}
	/**
	 * 取消进度对话框
	 */
	private void closeProgressDialog(){
		if(progressdialog!=null){
			progressdialog.dismiss();
		}
	}
}
