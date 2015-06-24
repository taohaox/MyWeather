package com.myweather.app.activity;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.myweather.app.R;
import com.myweather.app.util.HttpCallbackListener;
import com.myweather.app.util.HttpUtil;
import com.myweather.app.util.Utility;

public class WeatherActivity extends Activity{
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
	 * ���ˢ��
	 */
	private ImageView img_update;
	/**
	 * �����������Ϣ���͸�����
	 */
	private ImageView img_sendmsg;
	/**
	 * �������weather�����鿴��ϸ��Ϣ
	 */
	private ImageView img_getdesc;
	/**
	 * ����鿴�������е��¶�
	 */
	private ImageView img_listcity;
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
	private ImageView img_today;
	private TextView temp;
	
	private TextView tomorrow;
	private TextView tomorrow_date;
	private ImageView img_tomorrow;
	private TextView tomorrow_temp;
	
	private TextView after_tomorrow;
	private TextView after_tomorrow_date;
	private ImageView img_after_tomorrow;
	private TextView after_tomorrow_temp;
	
	private TextView big_after_tomorrow;
	private TextView big_after_tomorrow_date;
	private ImageView img_big_after_tomorrow;
	private TextView big_after_tomorrow_temp;
	
	private TextView real_big_after_tomorrow;
	private TextView real_big_after_tomorrow_date;
	private ImageView img_real_big_after_tomorrow;
	private TextView real_big_after_tomorrow_temp;
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.weather_layout);
		initView();
		String countycode = getIntent().getStringExtra("countycode");
		System.out.println("countycode:"+countycode);
		if("".equals(countycode)||countycode==null){
			//update_time.setText("ͬ����...");
			showWeatherInfo();
		}else{
			//update_time.setText("ͬ����...");
			update_weather(countycode,"county");
		}
		initListener();
		
		/*//����Ϊ1����ȡsp�ļ�����``   ӦΪд���ٶȹ���.
		TimerTask task = new TimerTask() {
			
			@Override
			public void run() {
				showWeatherInfo();
			}
		};
		Timer timer = new Timer();
		timer.schedule(task, 1000);*/
	}
	public void update_weather(String countycode,String type){
		
		queryWeatherBycountycode(countycode,type);
		try {
			Thread.sleep(500);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		showWeatherInfo();
	}
	/**
	 * �ӱ��ػ�ȡ ������Ϣ�ļ�  �õ������ݸ�ֵ�������ؼ�
	 */
	private void showWeatherInfo() {
		SharedPreferences sp = getSharedPreferences(Utility.WEATHER_CONFIG_FILE, MODE_PRIVATE);
		String weatherid = sp.getString("weatherid", "");
		if(weatherid.equals("")){
			AlertDialog.Builder builder = new Builder(this);
			builder.setTitle("��ʾ");
			builder.setMessage("�����ֻ���λ������δ����,������Ҫ���Լ�ָ�������ڵĳ���,Ϊ�������Ĳ���,����б�Ǹ!");
			builder.setNegativeButton("OK", new DialogInterface.OnClickListener() {
				
				@Override
				public void onClick(DialogInterface dialog, int which) {
					startActivity(new Intent(WeatherActivity.this,ChooseAreaActivity.class));
					WeatherActivity.this.finish();
				}
			}
			);
			builder.show();
		}else{
			String city_name = sp.getString("city_name", "");
			String temp1 = sp.getString("temp1", "");
			String weather1 = sp.getString("weather1", "");
			String current_date = sp.getString("current_date", "");
			temp_desc.setText(weather1);
			temp_range.setText(temp1);
			week_today.setText(Utility.getTodayOfWeek(new Date()));
			update_time.setText(Utility.getUpdate_time(current_date));
			address.setText(city_name);
			real_time_temp.setText((Integer.parseInt(temp1.substring(0, temp1.indexOf("~")-1))-3)+"");
			SimpleDateFormat sf = new SimpleDateFormat("M��dd��");
			Date date = new Date();
			int day = date.getDay();
			
			Calendar c = Calendar.getInstance();
			c.setTime(new Date());
			
			//����
			today.setText("����");
			today_date.setText(sf.format(new Date()));
			int id1 = Integer.parseInt(sp.getString("img1", ""));
			if(id1>33){
				id1=0;
			}
			String img1 = "d"+id1;
			img_today.setImageResource(Utility.getImage(img1));
			temp.setText(sp.getString("temp1", ""));
			
			//�ڶ���
			c.add(Calendar.DATE, 1);
			tomorrow.setText(Utility.getTodayOfWeek(c.getTime()));
			tomorrow_date.setText(sf.format(c.getTime()));
			int id2 = Integer.parseInt(sp.getString("img3", ""));
			if(id2>33){
				id2=0;
			}
			String img2 = "d"+id2;
			img_tomorrow.setImageResource(Utility.getImage(img2));
			tomorrow_temp.setText(sp.getString("temp2", ""));
			
			//������
			c.add(Calendar.DATE, 1);
			after_tomorrow.setText(Utility.getTodayOfWeek(c.getTime()));
			after_tomorrow_date.setText(sf.format(c.getTime()));
			int id3 = Integer.parseInt(sp.getString("img5", ""));
			if(id3>33){
				id3=0;
			}
			String img3 = "d"+id3;
			img_after_tomorrow.setImageResource(Utility.getImage(img3));
			after_tomorrow_temp.setText(sp.getString("temp3", ""));
			
			//������
			c.add(Calendar.DATE, 1);
			big_after_tomorrow.setText(Utility.getTodayOfWeek(c.getTime()));
			big_after_tomorrow_date.setText(sf.format(c.getTime()));
			int id4 = Integer.parseInt(sp.getString("img7", ""));
			if(id4>33){
				id4=0;
			}
			String img4 = "d"+id4;
			img_big_after_tomorrow.setImageResource(Utility.getImage(img4));
			big_after_tomorrow_temp.setText(sp.getString("temp4", ""));
			
			//������
			c.add(Calendar.DATE, 1);
			real_big_after_tomorrow.setText(Utility.getTodayOfWeek(c.getTime()));
			real_big_after_tomorrow_date.setText(sf.format(c.getTime()));
			int id5 = Integer.parseInt(sp.getString("img9", ""));
			if(id5>33){
				id5=0;
			}
			String img5 = "d"+id5;
			img_real_big_after_tomorrow.setImageResource(Utility.getImage(img5));
			real_big_after_tomorrow_temp.setText(sp.getString("temp5", ""));
			
		}
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
					Toast.makeText(WeatherActivity.this, e.getMessage(), 0).show();
					e.printStackTrace();
				}
			});
		}else if("weather".equals(type)){
			//String address = "http://www.weather.com.cn/data/cityinfo/"+code+".html";
			String address = "http://m.weather.com.cn/atad/"+code+".html";
			HttpUtil.sendHttpRequest(address,new HttpCallbackListener() {
				
				@Override
				public void onFinish(String response) {
					Utility.handleWeatherResponse(WeatherActivity.this, response);
				}
				
				@Override
				public void onError(Exception e) {
					Toast.makeText(WeatherActivity.this, e.getMessage(), 0).show();
					e.printStackTrace();
				}
			});
		}
		
		
	}
	
	/**
	 * ��ʼ�����ּ����¼�
	 */
	private void initListener() {
		img_update.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				showProgressDialog();
				Toast.makeText(WeatherActivity.this, "��ʼ��������", 0).show();
				SharedPreferences sp = WeatherActivity.this.getSharedPreferences(Utility.WEATHER_CONFIG_FILE, MODE_PRIVATE);
				update_weather(sp.getString("weatherid", ""),"weather");
				closeProgressDialog();
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
				Intent intent = new Intent(WeatherActivity.this,ChooseAreaActivity.class);
				startActivity(intent);
				finish();
			}
		});
		img_sendmsg.setOnClickListener(new OnClickListener() {
	
			@Override
			public void onClick(View v) {
		
			}
		});
	}


	/**
	 * ��ʼ����ͼ
	 */
	private void initView() {
		//�������ĸ�����ѡ��
		img_sendmsg = (ImageView) findViewById(R.id.img_sendmsg);
		img_listcity = (ImageView) findViewById(R.id.img_listcity);
		img_getdesc = (ImageView) findViewById(R.id.img_getdesc);
		img_update = (ImageView) findViewById(R.id.img_update);
		
		real_time_temp = (TextView) findViewById(R.id.real_time_temp);
		temp_range = (TextView) findViewById(R.id.temp_range);
		temp_desc = (TextView) findViewById(R.id.temp_desc);
		week_today = (TextView) findViewById(R.id.week_today);
		more_click = (TextView) findViewById(R.id.more_click);
		update_time = (TextView) findViewById(R.id.update_time);
		address = (TextView) findViewById(R.id.address);
		
		today = (TextView) findViewById(R.id.today);
		today_date = (TextView) findViewById(R.id.today_date);
		img_today = (ImageView) findViewById(R.id.img_today);
		temp = (TextView) findViewById(R.id.temp);
		
		tomorrow = (TextView) findViewById(R.id.tomorrow);
		tomorrow_date = (TextView) findViewById(R.id.tomorrow_date);
		img_tomorrow = (ImageView) findViewById(R.id.img_tomorrow);
		tomorrow_temp = (TextView) findViewById(R.id.tomorrow_temp);
		
		after_tomorrow = (TextView) findViewById(R.id.after_tomorrow);
		after_tomorrow_date = (TextView) findViewById(R.id.after_tomorrow_date);
		img_after_tomorrow = (ImageView) findViewById(R.id.img_after_tomorrow);
		after_tomorrow_temp = (TextView) findViewById(R.id.after_tomorrow_temp);
		
		big_after_tomorrow = (TextView) findViewById(R.id.big_after_tomorrow);
		big_after_tomorrow_date	 = (TextView) findViewById(R.id.big_after_tomorrow_date);
		img_big_after_tomorrow = (ImageView) findViewById(R.id.img_big_after_tomorrow);
		big_after_tomorrow_temp = (TextView) findViewById(R.id.big_after_tomorrow_temp);
		
		real_big_after_tomorrow = (TextView) findViewById(R.id.real_big_after_tomorrow);
		real_big_after_tomorrow_date = (TextView) findViewById(R.id.real_big_after_tomorrow_date);
		img_real_big_after_tomorrow = (ImageView) findViewById(R.id.img_real_big_after_tomorrow);
		real_big_after_tomorrow_temp = (TextView) findViewById(R.id.real_big_after_tomorrow_temp);
	}
	
	/**
	 * ��ʾ���ȶԻ���
	 */
	private void showProgressDialog() {
		if(progressdialog==null){
			progressdialog = new ProgressDialog(this);
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
