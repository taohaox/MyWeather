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
	 * 显示第几座城市的天气信息  默认为1
	 
	private int position = 1;*/
	
	@Override
	public void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		//setContentView(R.layout.weather_layout);
		//setTitle("ViewPager");
		
		 //初始化滑动菜单  
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
     * 初始化滑动菜单 
     */  
    private void initSlidingMenu() {  
    	// 设置滑动菜单的视图
		setBehindContentView(R.layout.menu_frame);
		getSupportFragmentManager().beginTransaction().replace(R.id.menu_frame, new ChooseAreaFragment()).commit();		

		// 实例化滑动菜单对象
		menu = getSlidingMenu();
		// 设置滑动阴影的宽度
		menu.setShadowWidthRes(R.dimen.shadow_width);
		// 设置滑动阴影的图像资源
		menu.setShadowDrawable(R.drawable.shadow);
		// 设置滑动菜单视图的宽度
		menu.setBehindOffsetRes(R.dimen.slidingmenu_offset);
		// 设置渐入渐出效果的值
		menu.setFadeDegree(0.7f);
		// 设置触摸屏幕的模式
		menu.setTouchModeAbove(SlidingMenu.TOUCHMODE_FULLSCREEN);
		if(getActionBar()!=null){
			getActionBar().setDisplayHomeAsUpEnabled(true);
		}
		
    }  
      
    @Override  
    public void onBackPressed() {  
        //点击返回键关闭滑动菜单  
        if (menu.isMenuShowing()) {  
            menu.showContent();  
        } else {  
            super.onBackPressed();  
        }  
    }  
	
	
}
