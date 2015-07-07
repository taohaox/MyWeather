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
	 * ʵʱ�¶�
	 */
	private TextView real_time_temp;
	/**
	 * �¶�����
	 */
	private TextView temp_range;
	/**
	 * �¶�����
	 */
	private TextView temp_desc;
	/**
	 * ��������
	 */
	private TextView wind_desc;
	/**
	 * ����������
	 */
	private TextView air;
	/**
	 * pm2.5
	 */
	private TextView pm2_5;
	/**
	 * ���������ڼ�
	 */
	private TextView week_today;
	/**
	 * ����鿴������ϸ��Ϣ
	 */
	private TextView more_click;
	/**
	 * ����ʱ��������ʱ��Ĳ�ֵ
	 */
	private TextView update_time;
	
	
	/**
	 * ���������ĵ���
	 */
	private TextView address;
	/**
	 * ������ʾ
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
				update_time.setText("ͬ����...");
				
			}
		});
		queryWeatherBycountycode(countycode,type);
		
		
	}
	/**
	 * �ӱ��ػ�ȡ ������Ϣ�ļ�  �õ������ݸ�ֵ�������ؼ�
	 */
	private void showWeatherInfo(final int position) {
		SharedPreferences sp = getActivity().getSharedPreferences(Utility.CITY_CONFIG+position, getActivity().MODE_PRIVATE);
		c1 = sp.getString("c1", "");
		if(c1.equals("")){
			if(position==1){
				/*AlertDialog.Builder builder = new Builder(getActivity());
				builder.setTitle("��ʾ");
				builder.setMessage("�����ֻ���λ������δ����,������Ҫ���Լ�ָ�������ڵĳ���,Ϊ�������Ĳ���,����б�Ǹ!");
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
			
			
			//��Ҫ��ʵʱ�����ӿ��ж�ȡ
			sp = getActivity().getSharedPreferences(Utility.REAL_WEATHER_CONFIG+position, Context.MODE_PRIVATE);
			String temp1 = sp.getString("temp1", "");  //�¶�a
			String temp2 = sp.getString("temp2", "");  //�¶�b
			String temNow = sp.getString("temNow", "");  //����
			String stateDetailed = sp.getString("stateDetailed", "");
			String current_time = sp.getString("current_time", "");
			
			temp_desc.setText(stateDetailed);
			wind_desc.setText(sp.getString("windState", ""));
			temp_range.setText(temp1+"/"+temp2+"��C");
			week_today.setText(Utility.getTodayOfWeek(new Date()));
			update_time.setText(Utility.getUpdate_time(current_time));
			real_time_temp.setText(temNow);
			
			
			SimpleDateFormat sf = new SimpleDateFormat("M��dd��");
			Date date = new Date();
			int day = date.getDay();
			
			Calendar c = Calendar.getInstance();
			c.setTime(new Date());
			
			//�õ��ó���δ�������������Ϣ
			sp = getActivity().getSharedPreferences(Utility.WEATHER_CONFIG+position, Context.MODE_PRIVATE);
			
			String level = sp.getString("level", "");
			String aqi = sp.getString("aqi", "");
			String core = sp.getString("core", "");
			if("".equals(core)){
				pm2_5.setText("aqi:"+aqi+" ����Ҫ��Ⱦ��");
			}else{
				pm2_5.setText("aqi:"+aqi+" ��Ҫ��Ⱦ��:"+core);
			}
			
			air.setText("����"+level);
			
			//����
			today.setText("����");
			today_date.setText(sf.format(new Date()));
			String img1 = "d"+sp.getString("fa1", "99");
			String img2 = "n"+sp.getString("fb1", "");
			
			
			img_today1.setImageResource(Utility.getImage(img1));
			img_today2.setImageResource(Utility.getImage(img2));
			temp.setText(temp1+"~"+temp2+"��C");
			
			//�ڶ���
			c.add(Calendar.DATE, 1);
			tomorrow.setText(Utility.getTodayOfWeek(c.getTime()));
			tomorrow_date.setText(sf.format(c.getTime()));
			
			String img3 = "d"+sp.getString("fa2", "");
			String img4 = "n"+sp.getString("fb2", "");
			img_tomorrow1.setImageResource(Utility.getImage(img3));
			img_tomorrow2.setImageResource(Utility.getImage(img4));
			tomorrow_temp.setText(sp.getString("fc2", "")+"~"+sp.getString("fd2", "")+"��C");
			
			//������
			c.add(Calendar.DATE, 1);
			after_tomorrow.setText(Utility.getTodayOfWeek(c.getTime()));
			after_tomorrow_date.setText(sf.format(c.getTime()));
			String img5 = "d"+sp.getString("fa3", "");
			String img6 = "n"+sp.getString("fb3", "");
			img_after_tomorrow1.setImageResource(Utility.getImage(img5));
			img_after_tomorrow2.setImageResource(Utility.getImage(img6));
			after_tomorrow_temp.setText(sp.getString("fc3", "")+"~"+sp.getString("fd3", "")+"��C");
		}
	}
	public void location_updateweather() {
		update_time.setText("ͬ����...");
		Location location = Utility.getLocation(getActivity());
		String address = "http://api.map.baidu.com/geocoder?location="+location.getLatitude()+","+location.getLongitude()+"&output=json&key=8cIYigmRBuU8IcquWLHvSAIB";
		HttpUtil.sendHttpRequest(address, new HttpCallbackListener() {
			
			@Override
			public void onFinish(String response) {
				String city = Utility.handlerLocationGetWeather(response);
				String city1 = "";
				if(city.equals("")){
					Toast.makeText(getActivity(), "��ȡλ����Ϣ����", 0).show();
					return;
				}
	        	if(city.contains("��")){
	        		city1 = city.substring(0, city.indexOf("��"));
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
	 * ���ʷ�����  ��ȡ��������
	 * @param code
	 * @param type
	 */
	private void queryWeatherBycountycode(String code,String type) {
		//http://www.weather.com.cn/data/list3/city190601.xml
		//http://www.weather.com.cn/data/cityinfo/101190601.html
		
		//��ȡ�ҳ���ϸ��������Ϣ  
		//http://m.weather.com.cn/atad/101010100.html
		update_time.setText("ͬ����...");
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
		final String url = sp.getString("c1", ""); //c1Ϊ�������   �Ǽ������ص�xml�е�url����
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
	 * ��λ��ʱ���ȵ��ø���ʵʱ�����ķ��� ������ȡweatherid 
	 * ֮�󽫻��ڷ�������� update_weather(weatherid, "weather") ����������
	 * @param position  �ڼ�������
	 * @param py     ���е�����  (ƴ��)
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
	 * ��ʼ�����ּ����¼�
	 */
	private void initListener() {
		/**
		 * �����������
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
	 * ��ʼ����ͼ
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
	 * ��ʾ���ȶԻ���
	 */
	private void showProgressDialog() {
		if(progressdialog==null){
			progressdialog = new ProgressDialog(getActivity());
			progressdialog.setMessage("���ڼ���...");
			//���ò��ܰ�����ȡ��
			progressdialog.setCanceledOnTouchOutside(false);
		}
		progressdialog.show();
	}
	/**
	 * ȡ�����ȶԻ���
	 */
	private void closeProgressDialog(){
		if(progressdialog!=null){
			progressdialog.dismiss();
		}
	}
}
