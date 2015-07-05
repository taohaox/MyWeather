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
	 * 自定义圆点指示器
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
        initListener();
	}
	private void initListener() {
img_update.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				//pb_update.setProgress(0);
				//Toast.makeText(WeatherActivity.this, "开始更新天气", 0).show();
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
		//菜单的四个控件
		img_sendmsg = (ImageView) bottom_menu.findViewById(R.id.img_sendmsg);
		img_listcity = (ImageView) bottom_menu.findViewById(R.id.img_listcity);
		img_getdesc = (ImageView) bottom_menu.findViewById(R.id.img_getdesc);
		img_update = (ImageView) bottom_menu.findViewById(R.id.img_update);
		
		fl.addView(vp);
		fl.addView(bottom_menu);
		
		dot = new DotIndicatorView(this, null);
		fl.addView(dot);
		
		//得到底部菜单在FrameLayout中的参数
		bottom_menu = (LinearLayout) fl.findViewById(R.id.ll_bottom_menu);
		LayoutParams params = (LayoutParams) bottom_menu.getLayoutParams();
		//将参数高度设置为40dp  用了单位转换
		params.height=(int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 40, getResources().getDisplayMetrics());
		//将参数设置为在底部显示
		params.gravity = Gravity.BOTTOM;
		//使bottom_menu应用这个参数
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
		wpa.notifyDataSetChanged();//通知更新Fragment
		menu.showContent();  
	}  
	
	
}
