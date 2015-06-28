package com.myweather.app.activity;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.myweather.app.R;
import com.myweather.app.db.MyWeatherDB;
import com.myweather.app.model.City;
import com.myweather.app.model.County;
import com.myweather.app.model.Province;
import com.myweather.app.util.HttpCallbackListener;
import com.myweather.app.util.HttpUtil;
import com.myweather.app.util.Utility;

public class ChooseAreaFragment extends ListFragment{
	public static final int LEVEL_PROVINCE=0;
	public static final int LEVEL_CITY=1;
	public static final int LEVEL_COUNTY=2;
	
	private ListView listview;
	private TextView title_text;
	private ProgressDialog progressdialog;
	private ArrayAdapter<String> adapter;
	private MyWeatherDB mDB;
	/**
	 * 装显示的数据
	 */
	private List<String> datalist;
	
	/**
	 * 省列表
	 */
	private List<Province> provinceList;
	/**
	 * 市列表
	 */
	private List<City> cityList;
	
	/**
	 * 县列表
	 */
	private List<County> countyList;
	/**
	 * 选中的省份
	 */
	private Province selectedProvince;
	/**
	 * 选中的城市
	 */
	private City selectedCity;
	/**
	 * 选中的级别
	 */
	private int currentLevel;
	
	private View view;
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		view = inflater.inflate(R.layout.choose_area, null);
		return view;
	}
	
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		initView();
		initListener();
		queryProvinces();
		
	}

	
	/**
	 * 初始化所有的视图,变量
	 */
	private void initView() {
		listview = (ListView) view.findViewById(android.R.id.list);
		title_text = (TextView) view.findViewById(R.id.title_text);
		datalist = new ArrayList<String>(); 
		adapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_list_item_1, datalist);
		listview.setAdapter(adapter);
		mDB = MyWeatherDB.getInstance(getActivity());
	}
	/**
	 * 初始化 所有的监听事件
	 */
	private void initListener() {
		listview.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				if(currentLevel==LEVEL_PROVINCE){
					selectedProvince = provinceList.get(position);
					queryCities();
				}else if(currentLevel == LEVEL_CITY){
					selectedCity = cityList.get(position);
					queryCounty();
				}else if(currentLevel==LEVEL_COUNTY){
					Intent intent = new Intent(getActivity(),WeatherActivity.class);
					String countycode =  countyList.get(position).getCountyCode();
					intent.putExtra("countycode", countycode);
					startActivity(intent);
					getActivity().finish();
				}
			}
			
		});
	}
	
	/**
	 * 查询全国所有的省,优先从数据库 查询 ,如果没有查询到再去服务器上查询
	 */
	private void queryProvinces(){
		provinceList = mDB.loadProvince();
		if(provinceList.size()>0){
			datalist.clear();
			for(Province p : provinceList){
				datalist.add(p.getProvinceName());
			}
			adapter.notifyDataSetChanged();
			listview.setSelection(0);
			title_text.setText("中国");
			currentLevel = LEVEL_PROVINCE;
		}else{
			queryFromServer(null, "province");
		}
	}
	/**
	 * 查询选中省内的所有市,优先从数据库 查询 ,如果没有查询到再去服务器上查询
	 */
	private void queryCities(){
		cityList = mDB.loadCity(Integer.parseInt(selectedProvince.getProvinceCode()));
		if(cityList.size()>0){
			datalist.clear();
			for(City c : cityList){
				datalist.add(c.getCityName());
			}
			adapter.notifyDataSetChanged();
			title_text.setText(selectedProvince.getProvinceName());
			listview.setSelection(0);
			currentLevel = LEVEL_CITY;
			title_text.setText(selectedProvince.getProvinceName());
		}else{
			queryFromServer(selectedProvince.getProvinceCode(), "city");
		}
	}
	/**
	 * 查询选中市内的所有县,优先从数据库 查询 ,如果没有查询到再去服务器上查询
	 */
	private void queryCounty(){
		countyList = mDB.loadCounty(Integer.parseInt(selectedCity.getCityCode()));
		if(countyList.size()>0){
			datalist.clear();
			for(County c: countyList){
				datalist.add(c.getCountyName());
			}
			title_text.setText(selectedCity.getCityName());
			adapter.notifyDataSetChanged();
			listview.setSelection(0);
			currentLevel = LEVEL_COUNTY;
		}else{
			queryFromServer(selectedCity.getCityCode(), "county");
		}
	}
	/**
	 * 根据传过来的参数 代号,类型  到服务器上查询 省市县数据
	 * @param code 省市县代号
	 * @param type 选中的类型 "province" "city" "county"
	 */
	private void queryFromServer(final String code,final String type){
		String address;
		if(!TextUtils.isEmpty(code)){
			address = "http://www.weather.com.cn/data/list3/city"+code+".xml";
		}else{
			address = "http://www.weather.com.cn/data/list3/city.xml";
		}
		showProgressDialog();
		
		HttpUtil.sendHttpRequest(address, new HttpCallbackListener() {
			boolean result = false;
			@Override
			public void onFinish(String response,InputStream in) {
				if(code!=null){
					result = Utility.handleResponse(mDB, response, Integer.parseInt(code));
				}else{
					result = Utility.handleResponse(mDB, response, 0);
				}
				
				if(result){
					//可以更新主界面动画
					getActivity().runOnUiThread(new Runnable() {
						@Override
						public void run() {
							closeProgressDialog();
							if(type.equals("province")){
								queryProvinces();
							}else if(type.equals("city")){
								queryCities();
							}else if(type.equals("county")){
								queryCounty();
							}
						}
					});
				}
			}
			
			@Override
			public void onError(Exception e) {
				e.printStackTrace();
				final String msg = e.getMessage();
				getActivity().runOnUiThread(new Runnable() {
					
					@Override
					public void run() {
						closeProgressDialog();
						Toast.makeText(getActivity(), "加载失败"+msg, 0).show();
						
					}
				});
			}
		});
	}
	/**
	 * 显示进度对话框
	 */
	private void showProgressDialog() {
		if(progressdialog==null){
			progressdialog = new ProgressDialog(getActivity());
			progressdialog.setMessage("正在加载...");
			//设置不能按返回取消
			progressdialog.setCanceledOnTouchOutside(false);
		}
		progressdialog.show();
	}
	/**
	 * 取消进度对话框
	 */
	private void closeProgressDialog(){
		if(progressdialog!=null){
			progressdialog.dismiss();
		}
	}
	/**
	 * 监听返回键  重载
	 */
	public void onBackPressed() {
		
		if(currentLevel==LEVEL_CITY){
			queryProvinces();
		}else if(currentLevel==LEVEL_PROVINCE){
			startActivity(new Intent(getActivity(),WeatherActivity.class));
			getActivity().finish();
		}else if(currentLevel==LEVEL_COUNTY){
			queryCities();
		}
	}
	
}
