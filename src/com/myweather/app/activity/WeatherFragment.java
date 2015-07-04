package com.myweather.app.activity;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.myweather.app.R;
import com.myweather.app.service.AutoUpdateWeatherService;
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
	 * 点击刷新
	 */
	private ImageView img_update;
	/**
	 * 点击将天气信息发送给好友
	 */
	private ImageView img_sendmsg;
	/**
	 * 点击进入weather官网查看详细信息
	 */
	private ImageView img_getdesc;
	/**
	 * 点击查看更多
	 */
	private ImageView img_listcity;
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
		//String countycode = getActivity().getIntent().getStringExtra("countycode");
		//System.out.println("countycode:"+countycode);
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
		update_time.setText("同步中...");
		queryWeatherBycountycode(countycode,type);
	}
	/**
	 * 从本地获取 天气信息文件  得到的数据赋值到各个控件
	 */
	private void showWeatherInfo(int position) {
		SharedPreferences sp = getActivity().getSharedPreferences(Utility.CITY_CONFIG+position, getActivity().MODE_PRIVATE);
		c1 = sp.getString("c1", "");
		if(c1.equals("")){
			if(position==1){
				AlertDialog.Builder builder = new Builder(getActivity());
				builder.setTitle("提示");
				builder.setMessage("由于手机定位功能尚未开发,所以需要您自己指定您所在的城市,为您带来的不便,我深感抱歉!");
				builder.setNegativeButton("OK", new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						
					}
				}
				);
				builder.show();
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
			
			
			getActivity();
			//拿到该城市未来三天的天气信息
			sp = getActivity().getSharedPreferences(Utility.WEATHER_CONFIG+position, Context.MODE_PRIVATE);
			//当天
			today.setText("今天");
			today_date.setText(sf.format(new Date()));
			String img1 = "d"+sp.getString("fa1", "");
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
			tomorrow_temp.setText(sp.getString("fd2", "")+"~"+sp.getString("fc2", "")+"°C");
			
			//第三天
			c.add(Calendar.DATE, 1);
			after_tomorrow.setText(Utility.getTodayOfWeek(c.getTime()));
			after_tomorrow_date.setText(sf.format(c.getTime()));
			String img5 = "d"+sp.getString("fa3", "");
			String img6 = "n"+sp.getString("fb3", "");
			img_after_tomorrow1.setImageResource(Utility.getImage(img5));
			img_after_tomorrow2.setImageResource(Utility.getImage(img6));
			after_tomorrow_temp.setText(sp.getString("fd3", "")+"~"+sp.getString("fc3", "")+"°C");
		}
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
		if("county".equals(type)){
			update_time.setText("同步中...");
			String address = "http://www.weather.com.cn/data/list3/city"+code+".xml";
			HttpUtil.sendHttpRequest(address,new HttpCallbackListener() {
				
				@Override
				public void onFinish(String response,InputStream in) {
					String strs[] = response.split("\\|");
					queryWeatherBycountycode(strs[1],"weather");
				}
				
				@Override
				public void onError(Exception e) {
					Toast.makeText(getActivity(), e.getMessage(), 0).show();
					e.printStackTrace();
				}
			});
		}else if("weather".equals(type)){
			//String address = "http://www.weather.com.cn/data/cityinfo/"+code+".html";
			//String address = "http://m.weather.com.cn/atad/"+code+".html";
			HttpUtil.sendHttpRequest(URLEncoderUtil.getUrlEncoder(code, URLEncoderUtil.FORECAST_V),new HttpCallbackListener() {
				
				@Override
				public void onFinish(String response,InputStream in) {
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
		String code = sp.getString("c4", "");
		String httpaddr = "http://flash.weather.com.cn/wmaps/xml/"+code+".xml";
		HttpUtil.sendHttpRequest(httpaddr, new HttpCallbackListener() {
			
			@Override
			public void onFinish(String response,InputStream in) {
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
	}
	/**
	 * 初始化各种监听事件
	 */
	private void initListener() {
		img_update.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				//pb_update.setProgress(0);
				//Toast.makeText(WeatherActivity.this, "开始更新天气", 0).show();
				
				SharedPreferences sp = getActivity().getSharedPreferences(Utility.CITY_CONFIG+position, getActivity().MODE_PRIVATE);
				update_weather(sp.getString("c1", ""),"weather");
				Intent intent = new Intent(getActivity(),AutoUpdateWeatherService.class);
				getActivity().startService(intent);	
			}
		});
		img_getdesc.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				
			}
		});
		img_listcity.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(getActivity(),DescWeatherInfoActivity.class);
				intent.putExtra("position", position);
				intent.putExtra("weatherid", c1);
				getActivity().startActivity(intent);
			}
		});
		img_sendmsg.setOnClickListener(new OnClickListener() {
	
			@Override
			public void onClick(View v) {
		
			}
		});
		
		
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
		//最下面四个操作选项
		img_sendmsg = (ImageView) view.findViewById(R.id.img_sendmsg);
		img_listcity = (ImageView) view.findViewById(R.id.img_listcity);
		img_getdesc = (ImageView) view.findViewById(R.id.img_getdesc);
		img_update = (ImageView) view.findViewById(R.id.img_update);
		
		real_time_temp = (TextView) view.findViewById(R.id.real_time_temp);
		temp_range = (TextView) view.findViewById(R.id.temp_range);
		temp_desc = (TextView) view.findViewById(R.id.temp_desc);
		wind_desc = (TextView) view.findViewById(R.id.wind_desc);
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
