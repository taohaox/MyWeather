package com.myweather.app.util;

import android.text.TextUtils;

import com.myweather.app.db.MyWeatherDB;
import com.myweather.app.model.City;
import com.myweather.app.model.County;
import com.myweather.app.model.Province;

public class Utility {
	public synchronized static boolean handleResponse(MyWeatherDB db,String response,int id){
		if(!TextUtils.isEmpty(response)){
			String[] strs = response.split(",");
			if(strs!=null&&strs.length>0){
				for(String s : strs){
					String models[] = s.split("\\|");
					String code = models[0];
					String name = models[1];
					System.out.println("code:"+code+" name:"+name+" id:"+id);
					if(code.length()==2){
						db.saveProvince(new Province(name, code));
					}else if(code.length()==4){
						db.saveCity(new City(name, code, id));
					}else if(code.length()==6){
						db.saveCounty(new County(name, code, id));
					}else{
						return false;
					}
				}
				
			}
		}
		
		
		
		return true;
	}
}
