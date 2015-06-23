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
	 * �������ؽ��  �����ݲ������ݿ�
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
	 * �������������ص�json����,�������������ݴ洢������
	 * @param context
	 * @param response
	 */
	public static void handleWeatherResponse(Context context,String response){
		//json ��ʽ  
		/*//{"weatherinfo":{"city":"����","city_en":"beijing","date_y":"2015��6��18��","date":"","week":"������",
		"fchh":"11","cityid":"101010100","temp1":"32��~19��","temp2":"28��~17��","temp3":"32��~20��",
		"temp4":"32��~21��","temp5":"31��~20��","temp6":"33��~21��","tempF1":"89.6�H~66.2�H","tempF2":"82.4�H~62.6�H",
		"tempF3":"89.6�H~68�H","tempF4":"89.6�H~69.8�H","tempF5":"87.8�H~68�H","tempF6":"91.4�H~69.8�H",
		"weather1":"����ת������","weather2":"������ת��","weather3":"��","weather4":"����","weather5":"����ת��",
		"weather6":"��ת����","img1":"1","img2":"4","img3":"4","img4":"0","img5":"0","img6":"99","img7":"1",
		"img8":"99","img9":"1","img10":"0","img11":"0","img12":"1","img_single":"1","img_title1":"����",
		"img_title2":"������","img_title3":"������","img_title4":"��","img_title5":"��","img_title6":"��",
		"img_title7":"����","img_title8":"����","img_title9":"����","img_title10":"��","img_title11":"��",
		"img_title12":"����","img_title_single":"����","wind1":"����3-4��ת΢��","wind2":"΢��","wind3":"΢��",
		"wind4":"΢��","wind5":"΢��","wind6":"΢��","fx1":"����","fx2":"΢��","fl1":"3-4��תС��3��",
		"fl2":"С��3��","fl3":"С��3��","fl4":"С��3��","fl5":"С��3��","fl6":"С��3��","index":"����",
		//"index_d":"�������ȣ������Ŷ�������ȹ���̿㡢����T�����������ļ���װ��","index48":"","index48_d":"",
		//"index_uv":"�е�","index48_uv":"","index_xc":"����","index_tr":"����","index_co":"�ϲ�����",
		//"st1":"31","st2":"17","st3":"25","st4":"18","st5":"31","st6":"21","index_cl":"������","index_ls":"����",
		//"index_ag":"���׷�"}}
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
	 * �����������ص�����������Ϣд�� sharepreferences�ļ���
	 * �Ժ��ع�һ��  Ҫд�����ݿ���
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
	 * ��ȡ���������ڼ�
	 * @return
	 */
	public static String getTodayOfWeek(Date date){
		String weekday="";
		Calendar c = Calendar.getInstance();
		c.setTime(date);
		int week = c.get(Calendar.DAY_OF_WEEK);
		switch (week) {
		case 1:
			weekday = "����";
			break;
		case 2:
			weekday = "��һ";
			break;
		case 3:
			weekday = "�ܶ�";
			break;
		case 4:
			weekday = "����";
			break;
		case 5:
			weekday = "����";
			break;
		case 6:
			weekday = "����";
			break;
		case 7:
			weekday = "����";
			break;
		default:
			break;
		}
		return weekday;
	}
	/**
	 * ��ȡ���������ڼ�
	 * @return
	 */
	public static String getTodayOfWeek(int day){
		String weekday="";
		
		switch (day) {
		case 1:
			weekday = "����";
			break;
		case 2:
			weekday = "��һ";
			break;
		case 3:
			weekday = "�ܶ�";
			break;
		case 4:
			weekday = "����";
			break;
		case 5:
			weekday = "����";
			break;
		case 6:
			weekday = "����";
			break;
		case 7:
			weekday = "����";
			break;
		default:
			break;
		}
		return weekday;
	}
	/**
	 * ��ȡ����ʱ�䵽����ʱ��ļ��ʱ��
	 * @param ptime  ����ʱ��
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
				return min+"����֮ǰ";
			}else if(min<3600){
				return min/60+"Сʱ֮ǰ";
			}else if(min<(3600*24)){
				return min/(60*24)+"��֮ǰ";
			}
		} catch (ParseException e) {
			e.printStackTrace();
			return "����ʧ��";
		}
		return "����ʧ��";
	}
	/**
	 * ͨ���ļ�����ȡ�ļ�id
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
