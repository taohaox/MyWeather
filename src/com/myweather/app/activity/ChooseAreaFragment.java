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
	 * װ��ʾ������
	 */
	private List<String> datalist;
	
	/**
	 * ʡ�б�
	 */
	private List<Province> provinceList;
	/**
	 * ���б�
	 */
	private List<City> cityList;
	
	/**
	 * ���б�
	 */
	private List<County> countyList;
	/**
	 * ѡ�е�ʡ��
	 */
	private Province selectedProvince;
	/**
	 * ѡ�еĳ���
	 */
	private City selectedCity;
	/**
	 * ѡ�еļ���
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
	 * ��ʼ�����е���ͼ,����
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
	 * ��ʼ�� ���еļ����¼�
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
	 * ��ѯȫ�����е�ʡ,���ȴ����ݿ� ��ѯ ,���û�в�ѯ����ȥ�������ϲ�ѯ
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
			title_text.setText("�й�");
			currentLevel = LEVEL_PROVINCE;
		}else{
			queryFromServer(null, "province");
		}
	}
	/**
	 * ��ѯѡ��ʡ�ڵ�������,���ȴ����ݿ� ��ѯ ,���û�в�ѯ����ȥ�������ϲ�ѯ
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
	 * ��ѯѡ�����ڵ�������,���ȴ����ݿ� ��ѯ ,���û�в�ѯ����ȥ�������ϲ�ѯ
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
	 * ���ݴ������Ĳ��� ����,����  ���������ϲ�ѯ ʡ��������
	 * @param code ʡ���ش���
	 * @param type ѡ�е����� "province" "city" "county"
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
					//���Ը��������涯��
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
						Toast.makeText(getActivity(), "����ʧ��"+msg, 0).show();
						
					}
				});
			}
		});
	}
	/**
	 * ��ʾ���ȶԻ���
	 */
	private void showProgressDialog() {
		if(progressdialog==null){
			progressdialog = new ProgressDialog(getActivity());
			progressdialog.setMessage("���ڼ���...");
			//���ò��ܰ�����ȡ��
			progressdialog.setCanceledOnTouchOutside(false);
		}
		progressdialog.show();
	}
	/**
	 * ȡ�����ȶԻ���
	 */
	private void closeProgressDialog(){
		if(progressdialog!=null){
			progressdialog.dismiss();
		}
	}
	/**
	 * �������ؼ�  ����
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
