package com.myweather.app.activity;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.myweather.app.R;
import com.myweather.app.adapter.DescWeatherAdapter;
import com.myweather.app.util.Utility;

public class DescWeatherInfoActivity extends Activity{
	private String weatherid = "";
	private int position = 0;
	private SharedPreferences sp_city_info ;
	private SharedPreferences sp_weather_info ;
	private TextView title_text;
	private ListView lv_desc;
	private ImageView back;
	private List<String> list;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.desc_weather_info);
		weatherid = getIntent().getCharSequenceExtra("weatherid").toString();
		position  = getIntent().getIntExtra("position", 0);
		
		
		initView();
		initData();
		initListener();
	}
	
	private void initListener() {
		back.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				finish();
			}
		});
	}

	private void initData() {
		String c1 = sp_city_info.getString("c1", "");
		if(!c1.equals(weatherid)){
			Toast.makeText(this, "信息读取或传递的错误!", 0).show();
			finish();
			return;
		}
		String c3 = sp_city_info.getString("c3", " ");  //县
		String c5 = sp_city_info.getString("c5", " "); //市
		String c7 = sp_city_info.getString("c7", " ");//省
		title_text.setText(c7+"->"+c5+"->"+c3);
		
		String c12 = sp_city_info.getString("c12", " ");//邮编
		String c13 = sp_city_info.getString("c13", " ");//经度
		String c14 = sp_city_info.getString("c14", " ");//纬度
		String c15 = sp_city_info.getString("c15", " ");//海拔
		String c16 = sp_city_info.getString("c16", " ");//雷达基站
		
		String f0 = sp_weather_info.getString("f0", " ");//预报发布时间
		try {
			if(f0.length()==12)
			f0 = new SimpleDateFormat("yyyy-MM-dd HH:mm").format(new SimpleDateFormat("yyyyMMddHHmm").parse(f0));
		} catch (ParseException e) {
			e.printStackTrace();
		} 
		String fi = sp_weather_info.getString("fi1", " ");//日出日落时间
		String fc = sp_weather_info.getString("fc1", " ");//白天温度
		String fd = sp_weather_info.getString("fd1", " ");//晚上温度
		String fis[] = null;
		if(!fi.equals(" ")){
			fis = fi.split("\\|");
		}
		list.add("城市#"+c3);
		list.add("预报发布时间#"+f0);
		list.add("白天温度#"+fc);
		list.add("晚上温度#"+fd);
		if(fis==null){
			list.add("日出时间#暂无");
			list.add("日落时间#暂无");
			
		}else{
			list.add("日出时间#"+fis[0]);
			list.add("日落时间#"+fis[1]);
		}
		
		list.add("邮编#"+c12);
		list.add("经纬度#"+c13+"  ,  " +c14);
		list.add("海拔#"+c15);
		list.add("雷达基站#"+c16);
		Location location= Utility.getLocation(this,WeatherFragment.locationManager);
		if(location==null){
			list.add("你所在的位置经纬度#无可用的定位器");
		}else{
			list.add("你位置的经纬度#"+Math.round((location.getLongitude()*1000))/1000.0+" , "+Math.round((location.getLatitude()*1000))/1000.0);
		}
		
		lv_desc.setAdapter(new DescWeatherAdapter(this, list));
	}

	private void initView() {
		sp_city_info = getSharedPreferences(Utility.CITY_CONFIG+position, MODE_PRIVATE);
		sp_weather_info = getSharedPreferences(Utility.WEATHER_CONFIG+position, MODE_PRIVATE);
		title_text = (TextView) findViewById(R.id.title_text_desc);
		lv_desc = (ListView) findViewById(R.id.lv_desc);
		back = (ImageView) findViewById(R.id.img_back_desc);
		list = new ArrayList<String>();
	}
	@Override
	public void onBackPressed() {
		super.onBackPressed();
	}
	@Override
	protected void onDestroy() {
		super.onDestroy();
	}
}
