package com.myweather.app.activity;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import android.app.Activity;
import android.content.SharedPreferences;
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
			Toast.makeText(this, "��Ϣ��ȡ�򴫵ݵĴ���!", 0).show();
			finish();
			return;
		}
		String c3 = sp_city_info.getString("c3", " ");  //��
		String c5 = sp_city_info.getString("c5", " "); //��
		String c7 = sp_city_info.getString("c7", " ");//ʡ
		title_text.setText(c7+"->"+c5+"->"+c3);
		
		String c12 = sp_city_info.getString("c12", " ");//�ʱ�
		String c13 = sp_city_info.getString("c13", " ");//����
		String c14 = sp_city_info.getString("c14", " ");//γ��
		String c15 = sp_city_info.getString("c15", " ");//����
		String c16 = sp_city_info.getString("c16", " ");//�״��վ
		
		String f0 = sp_weather_info.getString("f0", " ");//Ԥ������ʱ��
		try {
			if(f0.length()==12)
			f0 = new SimpleDateFormat("yyyy-MM-dd HH:mm").format(new SimpleDateFormat("yyyyMMddHHmm").parse(f0));
		} catch (ParseException e) {
			e.printStackTrace();
		} 
		String fi = sp_weather_info.getString("fi1", " ");//�ճ�����ʱ��
		String fc = sp_weather_info.getString("fc1", " ");//�����¶�
		String fd = sp_weather_info.getString("fd1", " ");//�����¶�
		String fis[] = null;
		if(!fi.equals(" ")){
			fis = fi.split("\\|");
		}
		list.add("����#"+c3);
		list.add("Ԥ������ʱ��#"+f0);
		list.add("�����¶�#"+fc);
		list.add("�����¶�#"+fd);
		if(fis==null){
			list.add("�ճ�ʱ��#����");
			list.add("����ʱ��#����");
			
		}else{
			list.add("�ճ�ʱ��#"+fis[0]);
			list.add("����ʱ��#"+fis[1]);
		}
		
		list.add("�ʱ�#"+c12);
		list.add("��γ��#"+c13+"  ,  " +c14);
		list.add("����#"+c15);
		list.add("�״��վ#"+c16);
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
}
