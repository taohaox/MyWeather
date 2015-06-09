package com.myweather.app.db;

import java.util.ArrayList;
import java.util.List;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.myweather.app.model.City;
import com.myweather.app.model.County;
import com.myweather.app.model.Province;

public class MyWeatherDB {
	/**
	 * ���ݿ�����
	 */
	public static final String DB_NAME="my_weather";
	
	/**
	 * ���ݿ�汾 
	 */
	public static final int VERSION = 1;
	
	public static MyWeatherDB myweatherDB;
	private SQLiteDatabase db;
	/**
	 * ˽�й��췽��
	 * @param context
	 */
	private MyWeatherDB(Context context){
		MyWeatherDBOpenHelper dbHelper = new MyWeatherDBOpenHelper(context, DB_NAME, null, VERSION);
		db = dbHelper.getWritableDatabase();
	}
	
	/**
	 * ��ȡ MyWeatherDB ʵ��
	 * @param context
	 * @return
	 */
	public synchronized static MyWeatherDB getInstance(Context context){
		if(myweatherDB==null){
			myweatherDB = new MyWeatherDB(context);
		}
		return myweatherDB;
		
	}
	
	/**
	 * ����provinceʵ�������ݿ�
	 * @param province
	 */
	public void saveProvince(Province province){
		ContentValues values = new ContentValues();
		
		values.put("province_name", province.getProvinceName());
		values.put("province_code", province.getProvinceCode());
		db.insert("province", null, values);
	}
	/**
	 * ����cityʵ�������ݿ�
	 * @param city
	 */
	public void saveCity(City city){
		ContentValues values = new ContentValues();
		values.put("city_name", city.getCityName());
		values.put("city_code", city.getCityCode());
		values.put("province_id", city.getProvinceId());
		db.insert("city", null, values);
	}
	public void saveCounty(County county){
		ContentValues values = new ContentValues();
		values.put("county_name", county.getCountyName());
		values.put("county_code", county.getCountyCode());
		values.put("city_id", county.getCityId());
		db.insert("county", null, values);
	}
	/**
	 * �����ݿ��ȡȫ������ʡ����Ϣ
	 * @return ȫ������ʡ����Ϣlist
	 */
	public List<Province> loadProvince(){
		List<Province> list = new ArrayList<Province>();
		Cursor cursor = null;
		try {
			cursor = db.query("province", null, null, null, null, null, null);
			while(cursor.moveToNext()){
				int id = cursor.getInt(cursor.getColumnIndex("id"));
				String provinceName = cursor.getString(cursor.getColumnIndex("province_name"));
				String provinceCode = cursor.getString(cursor.getColumnIndex("province_code"));
				list.add(new Province(id, provinceName, provinceCode));
			}
		} catch (Exception e) {
			e.printStackTrace();
		}finally{
			if(cursor!=null){
				cursor.close();
			}
		}
		
		
		return list;
	}
	
	/**
	 * ����ָ��ʡ��id�µ����г���
	 * @param provinceId   ʡ��id
	 * @return ����ָ��ʡ��id�µ����г���
	 */
	public List<City> loadCity(int provinceId){
		List<City> list = new ArrayList<City>();
		Cursor cursor = null;
		try {
			cursor = db.query("city", null, "province_id=?", new String[]{provinceId+""}, null, null, null);
			while(cursor.moveToNext()){
				int id = cursor.getInt(cursor.getColumnIndex("id"));
				String cityName = cursor.getString(cursor.getColumnIndex("city_name"));
				String cityCode = cursor.getString(cursor.getColumnIndex("city_code"));
				list.add(new City(id, cityName, cityCode, provinceId));
			}
		} catch (Exception e) {
			e.printStackTrace();
		}finally{
			if(cursor!=null){
				cursor.close();
			}
		}
		
		return list;
	}
	/**
	 * ����ָ�������µ���/��
	 * @param cityId  ָ���ĳ���id
	 * @return ����ָ�������µ���/��
	 */
	public List<County> loadCounty(int cityId){
		List<County> list = new ArrayList<County>();
		Cursor cursor = null;
		try {
			cursor = db.query("county", null, "city_id=?", new String[]{cityId+""}, null, null, null);
			while(cursor.moveToNext()){
				int id = cursor.getInt(cursor.getColumnIndex("id"));
				String countyName = cursor.getString(cursor.getColumnIndex("county_name"));
				String countyCode = cursor.getString(cursor.getColumnIndex("county_code"));
				list.add(new County(id, countyName, countyCode, cityId));
			}
		} catch (Exception e) {
			e.printStackTrace();
		}finally{
			if(cursor!=null){
				cursor.close();
			}
		}
		return list;
	}
}
