package com.myweather.app.util;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Field;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.location.Location;
import android.location.LocationManager;
import android.text.TextUtils;
import android.widget.Toast;

import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.search.core.SearchResult;
import com.baidu.mapapi.search.geocode.GeoCodeOption;
import com.baidu.mapapi.search.geocode.GeoCodeResult;
import com.baidu.mapapi.search.geocode.GeoCoder;
import com.baidu.mapapi.search.geocode.OnGetGeoCoderResultListener;
import com.baidu.mapapi.search.geocode.ReverseGeoCodeOption;
import com.baidu.mapapi.search.geocode.ReverseGeoCodeResult;
import com.myweather.app.R;
import com.myweather.app.db.MyWeatherDB;
import com.myweather.app.model.City;
import com.myweather.app.model.County;
import com.myweather.app.model.Province;

public class Utility {
	/**
	 * ���������Ϣ���ļ���,������к����� �������� �ŵ�һ�ĳ��о�Ϊ city1
	 */
	public static final String CITY_CONFIG = "city";
	public static final String WEATHER_CONFIG = "weather";
	public static final String REAL_WEATHER_CONFIG = "real_weather";
	
	public static ProgressDialog progressdialog = null;
	/**
	 * �������ؽ��  �����ݲ������ݿ�
	 * @param db  ���ݿ������  
	 * @param response  ��ʽΪ  01|����,02|�Ϻ�,03|���,04|����,05|������,06|����,07|����,08|���ɹ�,09|�ӱ�,10|ɽ��,11|����,12|ɽ��,13|�½�,14|����,15|�ຣ,16|����,17|����,18|����,19|����,20|����,21|�㽭,22|����,23|����,24|����,25|����,26|����,27|�Ĵ�,28|�㶫,29|����,30|����,31|����,32|���,33|����,34|̨��
	 * @param id   �ϼ����е� ���   ���û���ϼ�����  ��Ϊ0
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
	 * ���͸��������ĸ�ʽ  :  
	 * �������������ص�json����,�������������ݴ洢������
	 * @param context
	 * @param response  ���صĸ�ʽΪ {"c":
			{"c1":"101060605","c2":"tongyu","c3":"ͨ��","c4":"baicheng","c5":"�׳�","c6":"jilin","c7":"����",
			"c8":"china","c9":"�й�","c10":"3","c11":"0436","c12":"137200","c13":122.788000,"c14":44.745000,
			"c15":"151","c16":"AZ9436","c17":"+8"},
			"f":{"f1":[
				           {"fa":"","fb":"00","fc":"","fd":"19","fe":"","ff":"6","fg":"","fh":"1","fi":"04:04|19:38"},
				           {"fa":"00","fb":"00","fc":"30","fd":"22","fe":"6","ff":"4","fg":"1","fh":"1","fi":"04:04|19:38"},
				           {"fa":"04","fb":"03","fc":"28","fd":"18","fe":"4","ff":"7","fg":"1","fh":"1","fi":"04:05|19:38"}
			           ],
			     "f0":"201506261800"
			    }
			}  
			���� c���������Ϣ  f����������Ϣ   ������μ�:http://openweather.weather.com.cn/Public/font/SmartWeatherAPI_Lite_WebAPI_3.0.2.pdf
	 *@param position �����������ڵڼ�
	 *@return �����ϼ����е�ƴ��  �������ʷ����� ��ȡʵʱ����
	 */
	public static void handleWeatherResponse(Context context,String response,int position){
		/**/

		try {
			JSONObject jsonobject = new JSONObject(response);
			JSONObject c = jsonobject.getJSONObject("c");
			String c1 = c.getString("c1");
			String c2 = c.getString("c2");
			String c3 = c.getString("c3");
			String c4 = c.getString("c4");
			String c5 = c.getString("c5");
			String c6 = c.getString("c6");
			String c7 = c.getString("c7");
			String c8 = c.getString("c8");
			String c9 = c.getString("c9");
			String c10 = c.getString("c10");
			String c11 = c.getString("c11");
			String c12 = c.getString("c12");
			String c13 = c.getString("c13");
			String c14 = c.getString("c14");
			String c15 = c.getString("c15");
			String c16 = c.getString("c16");
			String c17 = c.getString("c17");
			
			JSONObject f = jsonobject.getJSONObject("f");
			
			
			JSONArray f1 = f.getJSONArray("f1");
			
			//������������
			JSONObject day1 = f1.getJSONObject(0);
			String fa1 = day1.getString("fa");
			String fb1 = day1.getString("fb");
			String fc1 = day1.getString("fc");
			String fd1 = day1.getString("fd");
			String fe1 = day1.getString("fe");
			String ff1 = day1.getString("ff");
			String fg1 = day1.getString("fg");
			String fh1 = day1.getString("fh");
			String fi1 = day1.getString("fi");
			
			JSONObject day2 = f1.getJSONObject(1);
			String fa2 = day2.getString("fa");
			String fb2 = day2.getString("fb");
			String fc2 = day2.getString("fc");
			String fd2 = day2.getString("fd");
			String fe2 = day2.getString("fe");
			String ff2 = day2.getString("ff");
			String fg2 = day2.getString("fg");
			String fh2 = day2.getString("fh");
			String fi2 = day2.getString("fi");
			
			JSONObject day3 = f1.getJSONObject(2);
			String fa3 = day3.getString("fa");
			String fb3 = day3.getString("fb");
			String fc3 = day3.getString("fc");
			String fd3 = day3.getString("fd");
			String fe3 = day3.getString("fe");
			String ff3 = day3.getString("ff");
			String fg3 = day3.getString("fg");
			String fh3 = day3.getString("fh");
			String fi3 = day3.getString("fi");
			//����ʱ��
			
			String f0 = f.getString("f0");
			
			saveCityinfo(context,c1,c2,c3,c4,c5,c6,c7,c8,c9,c10,c11,c12,c13,c14,c15,c16,c17,position);
			saveCityWeatherinfo(context,f0,fa1,fb1,fc1,fd1,fe1,ff1,fg1,fh1,fi1,fa2,fb2,fc2,fd2,fe2,ff2,fg2,fh2,fi2,fa3,fb3,fc3,fd3,fe3,ff3,fg3,fh3,fi3,position);
		} catch (JSONException e) {
			e.printStackTrace();

		}
		
				
	}
	/*
	 * ������е�������Ϣ  
	 */
	private static void saveCityWeatherinfo(Context context,String f0, String fa1,
			String fb1, String fc1, String fd1, String fe1, String ff1,
			String fg1, String fh1, String fi1, String fa2, String fb2,
			String fc2, String fd2, String fe2, String ff2, String fg2,
			String fh2, String fi2, String fa3, String fb3, String fc3,
			String fd3, String fe3, String ff3, String fg3, String fh3,
			String fi3, int position) {
		SharedPreferences sp = context.getSharedPreferences(WEATHER_CONFIG+position, Context.MODE_PRIVATE);
		Editor editor = sp.edit();
		if(isDay()){
			editor.putString("fa1", fa1);
			editor.putString("fc1", fc1);
			editor.putString("fe1", fe1);
			editor.putString("fg1", fg1);
		}
		editor.putString("f0", f0);
		
		editor.putString("fb1", fb1);
		editor.putString("fd1", fd1);
		
		editor.putString("ff1", ff1);
		
		editor.putString("fh1", fh1);
		editor.putString("fi1", fi1);
		
		editor.putString("fa2", fa2);
		editor.putString("fb2", fb2);
		editor.putString("fc2", fc2);
		editor.putString("fd2", fd2);
		editor.putString("fe2", fe2);
		editor.putString("ff2", ff2);
		editor.putString("fg2", fg2);
		editor.putString("fh2", fh2);
		editor.putString("fi2", fi2);
		
		editor.putString("fa3", fa3);
		editor.putString("fb3", fb3);
		editor.putString("fc3", fc3);
		editor.putString("fd3", fd3);
		editor.putString("fe3", fe3);
		editor.putString("ff3", ff3);
		editor.putString("fg3", fg3);
		editor.putString("fh3", fh3);
		editor.putString("fi3", fi3);
		
		editor.commit();
	}
	/**
	 * ���������Ϣ
	 */
	private static void saveCityinfo(Context context,String c1, String c2, String c3,
			String c4, String c5, String c6, String c7, String c8, String c9,
			String c10, String c11, String c12, String c13, String c14,
			String c15, String c16, String c17,int position) {
		SharedPreferences sp = context.getSharedPreferences(CITY_CONFIG+position, Context.MODE_PRIVATE);
		String eq_c1 = sp.getString("c1", "");
		if(eq_c1.equals(c1)){
			return;
		}
		Editor editor = sp.edit();
		editor.putString("position", ""+position);
		editor.putString("c1",c1);
		editor.putString("c2",c2);
		editor.putString("c3",c3);
		editor.putString("c4",c4);
		editor.putString("c5",c5);
		editor.putString("c6",c6);
		editor.putString("c7",c7);
		editor.putString("c8",c8);
		editor.putString("c9",c9);
		editor.putString("c10",c10);
		editor.putString("c11",c11);
		editor.putString("c12",c12);
		editor.putString("c13",c13);
		editor.putString("c14",c14);
		editor.putString("c15",c15);
		editor.putString("c16",c16);
		editor.putString("c17",c17);
		
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
	 * @param ptime  ����ʱ�� ��ʽΪyyyy-MM-dd HH:mm:ss
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
	/**
	 * �ж��Ƿ��ǰ���
	 * @return ����ǰ��� ����true
	 */
	public static boolean isDay(){
		Calendar c = Calendar.getInstance();
		c.setTime(new Date());
		int hour = c.get(Calendar.HOUR_OF_DAY);
		if(hour>=18||hour<=8){
			return false;
		}
		return true;
	}
	
	/**
	 * ������������ص�ʵʱ����  ��ʽΪ
	 * <hunan dn="nay">
<city cityX="249" cityY="98" cityname="�żҽ�" centername="�żҽ�" fontColor="FFFFFF" pyName="zhangjiajie" state1="1" state2="1" stateDetailed="����" tem1="25" tem2="35" temNow="28" windState="���Ϸ�С��3��" windDir="����" windPower="2��" humidity="72%" time="22:00" url="101251101"/>
<city cityX="196.2" cityY="164.8" cityname="����" centername="����" fontColor="FFFFFF" pyName="xiangxi" state1="1" state2="1" stateDetailed="����" tem1="26" tem2="33" temNow="27" windState="������С��3��"windDir="���Ϸ�" windPower="1��" humidity="87%" time="22:00" url="101251501"/>
<city cityX="331.75" cityY="121.4" cityname="����" centername="����" fontColor="FFFFFF" pyName="changde" state1="1" state2="1" stateDetailed="����" tem1="27" tem2="36" temNow="30" windState="�Ϸ�С��3��"windDir="�Ϸ�" windPower="1��" humidity="83%" time="22:00" url="101250601"/>
</hunan>
	 * @param url  ���ص�xml��city��ǩ������  ����ȷ������ȡ��һ����ǩ��ֵ
	 * @param position   �ڼ�������
	 */
	public static void handleRealWeather(Context context,String url,int position,File in){
		DocumentBuilderFactory builderFactory = DocumentBuilderFactory.newInstance(); 
		try {
			DocumentBuilder builder = builderFactory.newDocumentBuilder();
			Document document = builder.parse(in);
			Element root = document.getDocumentElement();
			NodeList nodelist = root.getElementsByTagName("city");
			for(int i=0;i<nodelist.getLength();i++){
				Element e = (Element) nodelist.item(i);
				String url_e = e.getAttribute("url");
				if(url.equals(url_e)){
					String state1 = e.getAttribute("state1");
					String state2 = e.getAttribute("state2");
					String temp1 = e.getAttribute("tem1");
					String temp2 = e.getAttribute("tem2");
					String stateDetailed = e.getAttribute("stateDetailed");
					String temNow = e.getAttribute("temNow");
					String windState = e.getAttribute("windState");
					String windDir = e.getAttribute("windDir");
					String windPower = e.getAttribute("windPower");
					String humidity = e.getAttribute("humidity");
					String time = e.getAttribute("time");
					String current_time = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());
					saveRealWeatherInfo(context,url,state1,state2,stateDetailed,temNow,windState,windDir,windPower,humidity,position,current_time,time,temp1,temp2);
				}
			}
		} catch (ParserConfigurationException e) {
			e.printStackTrace();
		} catch (SAXException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	/**
	 * ����ʵʱ������ REAL_WEATHER_CONFIG+position �ļ���
	 * @param temp2 
	 * @param temp1 
	 */
	private static void saveRealWeatherInfo(Context context, String url,
			String state1, String state2, String stateDetailed, String temNow,
			String windState, String windDir, String windPower, String humidity,int position,String current_time,String time, String temp1, String temp2) {
		SharedPreferences sp = context.getSharedPreferences(REAL_WEATHER_CONFIG+position, context.MODE_PRIVATE);
		Editor editor = sp.edit();
		editor.putString("url",url );
		editor.putString("state1",state1 );
		editor.putString("state2",state2 );
		editor.putString("stateDetailed",stateDetailed );
		editor.putString("temNow",temNow );
		editor.putString("windState",windState );
		editor.putString("windDir",windDir );
		editor.putString("windPower", windPower);
		editor.putString("humidity", humidity);
		editor.putString("current_time", current_time);
		editor.putString("time", time);
		editor.putString("temp1", temp1);
		editor.putString("temp2", temp2);
		editor.commit();
		
	}
	
	/**
	 * ����pm2.5 ���ص�json   ������real_weather���ļ��� 
	 * @param context
	 * @param position   �ڼ�������
	 * @param response   ���صĽ��
	 */
	public static void handlerPM2(Context context,int position,String response){
		//��ȡ����������������ݵ�sp
		SharedPreferences sp = context.getSharedPreferences(REAL_WEATHER_CONFIG+position, context.MODE_APPEND);
		Editor editor = sp.edit();
		
		try {
			JSONArray arr = new JSONArray(response);
			JSONObject obj = arr.getJSONObject(0);
			String pm2_5 = obj.getString("pm2_5");
			String quality = obj.getString("quality");
			editor.putString("pm2_5", pm2_5);
			editor.putString("quality", quality);
			editor.commit();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * ��ȡ���ص�λ����Ϣ
	 */
	public static Location getLocation(Context context){
		LocationManager locationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
		List<String> list = locationManager.getProviders(true);
		String provider = "";
		if(list.contains(LocationManager.GPS_PROVIDER)){
			provider = LocationManager.GPS_PROVIDER;
		}else if(list.contains(LocationManager.NETWORK_PROVIDER)){
			provider = LocationManager.NETWORK_PROVIDER;
		}else{
			Toast.makeText(context, "û�п��õ�λ���ṩ�� "+list.size(), 0).show();
			return null;
		}
		Location location = locationManager.getLastKnownLocation(provider);
		return location;
	}
	
	
	public static void getCity(Context context,OnGetGeoCoderResultListener listener){
		GeoCoder mSearch = GeoCoder.newInstance();
		mSearch.setOnGetGeoCodeResultListener(listener);
		Location location = getLocation(context);
		mSearch.reverseGeoCode(new ReverseGeoCodeOption().location(new LatLng(location.getLatitude(), location.getLongitude())));  
	}
}
