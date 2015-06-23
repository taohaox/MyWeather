package com.myweather.app.util;

import java.lang.reflect.Field;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.text.TextUtils;
import android.widget.Toast;

import com.myweather.app.R;
import com.myweather.app.db.MyWeatherDB;
import com.myweather.app.model.City;
import com.myweather.app.model.County;
import com.myweather.app.model.Province;

public class Utility {
	public static final String WEATHER_CONFIG_FILE = "weather.xml";
	public static ProgressDialog progressdialog = null;
	/**
	 * 解析返回结果  将数据插入数据库
	 * @param db
	 * @param response
	 * @param id
	 * @return
	 */
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
	/**
	 * 解析服务器返回的json数据,并将解析的数据存储到本地
	 * @param context
	 * @param response
	 */
	public static void handleWeatherResponse(Context context,String response){
		//json 格式  
		/*//{"weatherinfo":{"city":"北京","city_en":"beijing","date_y":"2015年6月18日","date":"","week":"星期四",
		"fchh":"11","cityid":"101010100","temp1":"32℃~19℃","temp2":"28℃~17℃","temp3":"32℃~20℃",
		"temp4":"32℃~21℃","temp5":"31℃~20℃","temp6":"33℃~21℃","tempF1":"89.6℉~66.2℉","tempF2":"82.4℉~62.6℉",
		"tempF3":"89.6℉~68℉","tempF4":"89.6℉~69.8℉","tempF5":"87.8℉~68℉","tempF6":"91.4℉~69.8℉",
		"weather1":"多云转雷阵雨","weather2":"雷阵雨转晴","weather3":"晴","weather4":"多云","weather5":"多云转晴",
		"weather6":"晴转多云","img1":"1","img2":"4","img3":"4","img4":"0","img5":"0","img6":"99","img7":"1",
		"img8":"99","img9":"1","img10":"0","img11":"0","img12":"1","img_single":"1","img_title1":"多云",
		"img_title2":"雷阵雨","img_title3":"雷阵雨","img_title4":"晴","img_title5":"晴","img_title6":"晴",
		"img_title7":"多云","img_title8":"多云","img_title9":"多云","img_title10":"晴","img_title11":"晴",
		"img_title12":"多云","img_title_single":"多云","wind1":"北风3-4级转微风","wind2":"微风","wind3":"微风",
		"wind4":"微风","wind5":"微风","wind6":"微风","fx1":"北风","fx2":"微风","fl1":"3-4级转小于3级",
		"fl2":"小于3级","fl3":"小于3级","fl4":"小于3级","fl5":"小于3级","fl6":"小于3级","index":"炎热",
		//"index_d":"天气炎热，建议着短衫、短裙、短裤、薄型T恤衫等清凉夏季服装。","index48":"","index48_d":"",
		//"index_uv":"中等","index48_uv":"","index_xc":"不宜","index_tr":"适宜","index_co":"较不舒适",
		//"st1":"31","st2":"17","st3":"25","st4":"18","st5":"31","st6":"21","index_cl":"较适宜","index_ls":"适宜",
		//"index_ag":"不易发"}}
*/		try {
			JSONObject jsonobject = new JSONObject(response);
			JSONObject weatherinfo = jsonobject.getJSONObject("weatherinfo");
			String city_name = weatherinfo.getString("city");
			String weatherid = weatherinfo.getString("cityid");
			String temp1 = weatherinfo.getString("temp1");
			String temp2 = weatherinfo.getString("temp2");
			String temp3 = weatherinfo.getString("temp3");
			String temp4 = weatherinfo.getString("temp4");
			String temp5 = weatherinfo.getString("temp5");
			String weather1 = weatherinfo.getString("weather1");
			String weather2 = weatherinfo.getString("weather2");
			String weather3 = weatherinfo.getString("weather3");
			String weather4 = weatherinfo.getString("weather4");
			String weather5 = weatherinfo.getString("weather5");
			
			String img1 = weatherinfo.getString("img1");
			String img2 = weatherinfo.getString("img2");
			String img3 = weatherinfo.getString("img3");
			String img4 = weatherinfo.getString("img4");
			String img5 = weatherinfo.getString("img5");
			String img6 = weatherinfo.getString("img6");
			String img7 = weatherinfo.getString("img7");
			String img8 = weatherinfo.getString("img8");
			String img9 = weatherinfo.getString("img9");
			String img10 = weatherinfo.getString("img10");
			
			String wind1 = weatherinfo.getString("wind1");
			String wind2 = weatherinfo.getString("wind2");
			String wind3 = weatherinfo.getString("wind3");
			String wind4 = weatherinfo.getString("wind4");
			String wind5 = weatherinfo.getString("wind5");
			
			String index_d = weatherinfo.getString("index_d");
			
			//String ptime = weatherinfo.getString("ptime");
			//ptime = new SimpleDateFormat("yyyy-MM-dd").format(new Date())+" "+ptime+":00";
			saveWeatherInfo(context,city_name,weatherid,temp1,temp2,temp3,temp4,temp5,weather1,weather2,weather3,weather4,weather5
					,img1,img2,img3,img4,img5,img6,img7,img8,img9,img10,wind1,wind2,wind3,wind4,wind5,index_d);
		} catch (JSONException e) {
			e.printStackTrace();
		}

	}
	/**
	 * 将服务器返回的所有天气信息写入 sharepreferences文件中
	 * 以后重构一下  要写在数据库中
	 */
	public static void saveWeatherInfo(Context context, String city_name,
			String weatherid, String temp1, String temp2, String temp3,
			String temp4, String temp5, String weather1, String weather2,
			String weather3, String weather4, String weather5, String img1,
			String img2, String img3, String img4, String img5, String img6,
			String img7, String img8, String img9, String img10, String wind1,
			String wind2, String wind3, String wind4, String wind5,
			String index_d) {
		SharedPreferences sdf = context.getSharedPreferences(WEATHER_CONFIG_FILE,Context.MODE_PRIVATE);
		Editor editor = sdf.edit();
		editor.putString("city_name", city_name);
		editor.putString("weatherid", weatherid);
		editor.putString("temp1", temp1);
		editor.putString("temp2", temp2);
		editor.putString("temp3", temp3);
		editor.putString("temp4", temp4);
		editor.putString("temp5", temp5);
		
		editor.putString("weather1", weather1);
		editor.putString("weather2", weather2);
		editor.putString("weather3", weather3);
		editor.putString("weather4", weather4);
		editor.putString("weather5", weather5);
		
		editor.putString("img1", img1);
		editor.putString("img2", img2);
		editor.putString("img3", img3);
		editor.putString("img4", img4);
		editor.putString("img5", img5);
		editor.putString("img6", img6);
		editor.putString("img7", img7);
		editor.putString("img8", img8);
		editor.putString("img9", img9);
		editor.putString("img10", img10);
		
		editor.putString("wind1", wind1);
		editor.putString("wind2", wind2);
		editor.putString("wind3", wind3);
		editor.putString("wind4", wind4);
		editor.putString("wind5", wind5);
		
		editor.putString("index_d", index_d);
		editor.putString("current_date", new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()));
		editor.commit();
	}
	/**
	 * 获取今天是星期几
	 * @return
	 */
	public static String getTodayOfWeek(Date date){
		String weekday="";
		Calendar c = Calendar.getInstance();
		c.setTime(date);
		int week = c.get(Calendar.DAY_OF_WEEK);
		switch (week) {
		case 1:
			weekday = "周日";
			break;
		case 2:
			weekday = "周一";
			break;
		case 3:
			weekday = "周二";
			break;
		case 4:
			weekday = "周三";
			break;
		case 5:
			weekday = "周四";
			break;
		case 6:
			weekday = "周五";
			break;
		case 7:
			weekday = "周六";
			break;
		default:
			break;
		}
		return weekday;
	}
	/**
	 * 获取今天是星期几
	 * @return
	 */
	public static String getTodayOfWeek(int day){
		String weekday="";
		
		switch (day) {
		case 1:
			weekday = "周日";
			break;
		case 2:
			weekday = "周一";
			break;
		case 3:
			weekday = "周二";
			break;
		case 4:
			weekday = "周三";
			break;
		case 5:
			weekday = "周四";
			break;
		case 6:
			weekday = "周五";
			break;
		case 7:
			weekday = "周六";
			break;
		default:
			break;
		}
		return weekday;
	}
	/**
	 * 获取更新时间到现在时间的间隔时间
	 * @param ptime  更新时间
	 * @return
	 */
	public static String getUpdate_time(String ptime){
		try {
			long updatedtime = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(ptime).getTime();
			long currenttime = System.currentTimeMillis();
			long intervaltime = currenttime-updatedtime;
			int seconds = (int) (intervaltime/1000);
			int min = seconds/60 ;
			if(min<60){
				return min+"分钟之前";
			}else if(min<3600){
				return min/60+"小时之前";
			}else if(min<(3600*24)){
				return min/(60*24)+"天之前";
			}
		} catch (ParseException e) {
			e.printStackTrace();
			return "更新失败";
		}
		return "更新失败";
	}
	/**
	 * 通过文件名获取文件id
	 * @param pic
	 * @return
	 */
	public static int getImage(String pic) {  
        if(pic==null||pic.trim().equals("")){  
         return R.drawable.ic_launcher;  
        }  
        Class draw = R.drawable.class;  
        try {  
         Field field = draw.getDeclaredField(pic);  
         return field.getInt(pic);  
        } catch (SecurityException e) {  
         return R.drawable.ic_launcher;  
        } catch (NoSuchFieldException e) {  
         return R.drawable.ic_launcher;  
        } catch (IllegalArgumentException e) {  
         return R.drawable.ic_launcher;  
        } catch (IllegalAccessException e) {  
         return R.drawable.ic_launcher;  
        }  
       }  
}
