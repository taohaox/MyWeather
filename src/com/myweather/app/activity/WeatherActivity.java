package com.myweather.app.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.view.MenuItem;
import android.view.Window;

import com.jeremyfeinstein.slidingmenu.lib.SlidingMenu;
import com.jeremyfeinstein.slidingmenu.lib.app.SlidingFragmentActivity;
import com.myweather.app.R;
import com.myweather.app.adapter.WeatherPagerAdapter;
import com.myweather.app.service.AutoUpdateWeatherService;

public class WeatherActivity extends SlidingFragmentActivity{
	public SlidingMenu menu;  
	public ViewPager vp;
	public WeatherPagerAdapter wpa;
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
	}
	private void initView() {
		String countycode = getIntent().getStringExtra("countycode");
		int position = getIntent().getIntExtra("position", 1);
		System.out.println("countycode:"+countycode+" position:"+position);
		vp = new ViewPager(this);
		vp.setId("VP".hashCode());
		if(countycode!=null&&!"".equals(countycode)){
			wpa = new WeatherPagerAdapter(getSupportFragmentManager(),position,countycode);
		}else{
			wpa = new WeatherPagerAdapter(getSupportFragmentManager());
		}
		
		vp.setAdapter(wpa);
		setContentView(vp);
		vp.setOnPageChangeListener(new OnPageChangeListener() {
			@Override
			public void onPageScrollStateChanged(int arg0) { }

			@Override
			public void onPageScrolled(int arg0, float arg1, int arg2) { }

			@Override
			public void onPageSelected(int position) {
				switch (position) {
				case 0:
					getSlidingMenu().setTouchModeAbove(SlidingMenu.TOUCHMODE_FULLSCREEN);
					break;
				default:
					getSlidingMenu().setTouchModeAbove(SlidingMenu.TOUCHMODE_MARGIN);
					break;
				}
			}

		});
		
		vp.setCurrentItem(0);
		getSlidingMenu().setTouchModeAbove(SlidingMenu.TOUCHMODE_FULLSCREEN);
		
		Intent intent = new Intent(this,AutoUpdateWeatherService.class);
		startService(intent);
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
	
	
}
