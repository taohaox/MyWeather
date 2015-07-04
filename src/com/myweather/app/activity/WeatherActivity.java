package com.myweather.app.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.view.MenuItem;
import android.view.Window;
import android.widget.ImageView;

import com.jeremyfeinstein.slidingmenu.lib.SlidingMenu;
import com.jeremyfeinstein.slidingmenu.lib.app.SlidingFragmentActivity;
import com.myweather.app.R;
import com.myweather.app.activity.ChooseAreaFragment.HandlerChoose;
import com.myweather.app.adapter.WeatherPagerAdapter;
import com.myweather.app.service.AutoUpdateWeatherService;

public class WeatherActivity extends SlidingFragmentActivity implements HandlerChoose{
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
		vp = new ViewPager(this);
		vp.setId("VP".hashCode());
		wpa = new WeatherPagerAdapter(getSupportFragmentManager());
		
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
	@Override
	public void showWeather(String countyid) {
		System.out.println("我得到了城市id:"+countyid+"现在是viewpager中的第"+vp.getCurrentItem()+"个view,共有"+wpa.getCount()+"个view");
		int position = vp.getCurrentItem()+1;
		WeatherFragment wf =  (WeatherFragment) wpa.getItem(vp.getCurrentItem());
		wf.countycode = countyid;
		wf.update_weather(countyid, "county");
		//wpa.removeFragment(position);
		//System.out.println("wpa.removeFragment(position); 共有"+wpa.getCount()+"个view");
		//wpa.addFragment(position, new WeatherFragment(position,countyid));
		//System.out.println("wpa.addFragment(position, new WeatherFragment(position)); 共有"+wpa.getCount()+"个view");
		wpa.notifyDataSetChanged();//通知更新Fragment
		menu.showContent();  
	}  
	
	
}
