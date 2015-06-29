package com.myweather.app.adapter;

import java.util.ArrayList;
import java.util.List;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.myweather.app.activity.WeatherFragment;

public class WeatherPagerAdapter extends FragmentPagerAdapter{
		private List<Fragment> mFragments; 
		public WeatherPagerAdapter(FragmentManager fm){
			super(fm);
			mFragments = new ArrayList<Fragment>();
			for(int i=1;i<=5;i++){
				mFragments.add(new WeatherFragment(i));
			}
		}


		public WeatherPagerAdapter(FragmentManager supportFragmentManager,
				int parseInt, String countycode) {
			super(supportFragmentManager);
			mFragments = new ArrayList<Fragment>();
			for(int i=1;i<=5;i++){
				if(i==parseInt){
					mFragments.add(new WeatherFragment(i,countycode));
				}else{
					mFragments.add(new WeatherFragment(i));
				}
				
			}
		}

		@Override
		public Fragment getItem(int arg0) {
			return mFragments.get(arg0);
		}

		@Override
		public int getCount() {
			return mFragments.size();
		}
		/**
		 * 删除指定位置的城市页面
		 * @param position 第几个城市
		 */
		public void removeFragment(int position){
			if(position-1<0){
				return;
			}
			mFragments.remove(position-1);
		}
		/**
		 * 在指定位置添加城市页面
		 * @param position   第几个城市
		 * @param wf   添加的页面 
		 */
		public void addFragment(int position,WeatherFragment wf){
			if(position-1<0){
				return;
			}
			mFragments.add(position-1, wf);
		}
	}