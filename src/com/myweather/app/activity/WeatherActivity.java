package com.myweather.app.activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.View.OnClickListener;
import android.widget.FrameLayout;
import android.widget.FrameLayout.LayoutParams;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.jeremyfeinstein.slidingmenu.lib.SlidingMenu;
import com.jeremyfeinstein.slidingmenu.lib.app.SlidingFragmentActivity;
import com.myweather.app.R;
import com.myweather.app.activity.ChooseAreaFragment.HandlerChoose;
import com.myweather.app.adapter.WeatherPagerAdapter;
import com.myweather.app.customview.DotIndicatorView;
import com.myweather.app.service.AutoUpdateWeatherService;
import com.myweather.app.util.Utility;

public class WeatherActivity extends SlidingFragmentActivity implements HandlerChoose{
	public SlidingMenu menu;  
	public ViewPager vp;
	public WeatherPagerAdapter wpa;
	
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
	 * ����鿴����
	 */
	private ImageView img_listcity;
	
	/**
	 * �Զ���Բ��ָʾ��
	 */
	private DotIndicatorView dot;
	/*private TextView big_after_tomorrow;
	private TextView big_after_tomorrow_date;
	private ImageView img_big_after_tomorrow;
	private TextView big_after_tomorrow_temp;
	
	private TextView real_big_after_tomorrow;
	private TextView real_big_after_tomorrow_date;
	private ImageView img_real_big_after_tomorrow;
	private TextView real_big_after_tomorrow_temp;*/
	
	/**
	 * ��ʾ�ڼ������е�������Ϣ  Ĭ��Ϊ1
	 
	private int position = 1;*/
	
	@Override
	public void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		//setContentView(R.layout.weather_layout);
		//setTitle("ViewPager");
		
		 //��ʼ�������˵�  
        initSlidingMenu();  
        initView();
        initListener();
	}
	private void initListener() {
img_update.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				//pb_update.setProgress(0);
				//Toast.makeText(WeatherActivity.this, "��ʼ��������", 0).show();
				WeatherFragment wf =  (WeatherFragment) wpa.getItem(vp.getCurrentItem());
				wf.update_weather(getC1(),"weather");
				Intent intent = new Intent(WeatherActivity.this,AutoUpdateWeatherService.class);
				startService(intent);	
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
				Intent intent = new Intent(WeatherActivity.this,DescWeatherInfoActivity.class);
				intent.putExtra("position", getPosition());
				intent.putExtra("weatherid", getC1());
				WeatherActivity.this.startActivity(intent);
			}
		});
		img_sendmsg.setOnClickListener(new OnClickListener() {
	
			@Override
			public void onClick(View v) {
		
			}
		});
		
	}
	private int getPosition(){
		return vp.getCurrentItem()+1;
	}
	
	private String getC1(){
		SharedPreferences sp = getSharedPreferences(Utility.CITY_CONFIG+getPosition(),MODE_PRIVATE);
		return sp.getString("c1", "");
	}
	private void initView() {
		vp = new ViewPager(this);
		vp.setId("VP".hashCode());
		wpa = new WeatherPagerAdapter(getSupportFragmentManager());
		vp.setAdapter(wpa);
		FrameLayout fl = new FrameLayout(this);
		
		
		
		LinearLayout bottom_menu = (LinearLayout) LayoutInflater.from(this).inflate(R.layout.bottom_menu, null);
		//�˵����ĸ��ؼ�
		img_sendmsg = (ImageView) bottom_menu.findViewById(R.id.img_sendmsg);
		img_listcity = (ImageView) bottom_menu.findViewById(R.id.img_listcity);
		img_getdesc = (ImageView) bottom_menu.findViewById(R.id.img_getdesc);
		img_update = (ImageView) bottom_menu.findViewById(R.id.img_update);
		
		fl.addView(vp);
		fl.addView(bottom_menu);
		
		dot = new DotIndicatorView(this, null);
		fl.addView(dot);
		
		//�õ��ײ��˵���FrameLayout�еĲ���
		bottom_menu = (LinearLayout) fl.findViewById(R.id.ll_bottom_menu);
		LayoutParams params = (LayoutParams) bottom_menu.getLayoutParams();
		//�������߶�����Ϊ40dp  ���˵�λת��
		params.height=(int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 40, getResources().getDisplayMetrics());
		//����������Ϊ�ڵײ���ʾ
		params.gravity = Gravity.BOTTOM;
		//ʹbottom_menuӦ���������
		bottom_menu.setLayoutParams(params);
		
		
		
		
		setContentView(fl);
		vp.setOnPageChangeListener(new OnPageChangeListener() {
			@Override
			public void onPageScrollStateChanged(int arg0) { }

			@Override
			public void onPageScrolled(int arg0, float arg1, int arg2) { }

			@Override
			public void onPageSelected(int position) {
				switch (position) {
				case 0:
					getSlidingMenu().setTouchModeAbove(SlidingMenu.TOUCHMODE_NONE);
					dot.setPosition(getPosition());
					dot.postInvalidate();
					break;
				case 1:
					getSlidingMenu().setTouchModeAbove(SlidingMenu.TOUCHMODE_MARGIN);
					dot.setPosition(getPosition());
					dot.postInvalidate();
					break;
				case 2:
					getSlidingMenu().setTouchModeAbove(SlidingMenu.TOUCHMODE_MARGIN);
					dot.setPosition(getPosition());
					dot.postInvalidate();
					break;
				case 3:
					getSlidingMenu().setTouchModeAbove(SlidingMenu.TOUCHMODE_MARGIN);
					dot.setPosition(getPosition());
					dot.postInvalidate();
					break;
				case 4:
					getSlidingMenu().setTouchModeAbove(SlidingMenu.TOUCHMODE_MARGIN);
					dot.setPosition(getPosition());
					dot.postInvalidate();
					break;
				case 5:
					getSlidingMenu().setTouchModeAbove(SlidingMenu.TOUCHMODE_MARGIN);
					dot.setPosition(getPosition());
					dot.postInvalidate();
					break;
					
				}
			}

		});
		
		vp.setCurrentItem(0);
		getSlidingMenu().setTouchModeAbove(SlidingMenu.TOUCHMODE_NONE);
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case android.R.id.home:
			toggle();
			return true;	
		}		
		return super.onOptionsItemSelected(item);
	}
	/** 
     * ��ʼ�������˵� 
     */  
    private void initSlidingMenu() {  
    	// ���û����˵�����ͼ
		setBehindContentView(R.layout.menu_frame);
		getSupportFragmentManager().beginTransaction().replace(R.id.menu_frame, new ChooseAreaFragment()).commit();		

		// ʵ���������˵�����
		menu = getSlidingMenu();
		// ���û�����Ӱ�Ŀ��
		menu.setShadowWidthRes(R.dimen.shadow_width);
		// ���û�����Ӱ��ͼ����Դ
		menu.setShadowDrawable(R.drawable.shadow);
		// ���û����˵���ͼ�Ŀ��
		menu.setBehindOffsetRes(R.dimen.slidingmenu_offset);
		// ���ý��뽥��Ч����ֵ
		menu.setFadeDegree(0.7f);
		// ���ô�����Ļ��ģʽ
		menu.setTouchModeAbove(SlidingMenu.TOUCHMODE_FULLSCREEN);
		if(getActionBar()!=null){
			getActionBar().setDisplayHomeAsUpEnabled(true);
		}
		
    }  
      
    @Override  
    public void onBackPressed() {  
        //������ؼ��رջ����˵�  
        if (menu.isMenuShowing()) {  
            menu.showContent();  
        } else {  
            super.onBackPressed();  
        }  
    }
	@Override
	public void showWeather(String countyid) {
		System.out.println("�ҵõ��˳���id:"+countyid+"������viewpager�еĵ�"+vp.getCurrentItem()+"��view,����"+wpa.getCount()+"��view");
		int position = vp.getCurrentItem()+1;
		WeatherFragment wf =  (WeatherFragment) wpa.getItem(vp.getCurrentItem());
		wf.countycode = countyid;
		wf.update_weather(countyid, "county");
		wpa.notifyDataSetChanged();//֪ͨ����Fragment
		menu.showContent();  
	}  
	
	
}
