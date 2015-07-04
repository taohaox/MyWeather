package com.myweather.app.adapter;

import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.myweather.app.R;

/**
 * 还是用list做数据源吧
 * @author winGyb
 *
 */
public class DescWeatherAdapter extends BaseAdapter{
	private List<String> list;
	private Context context;
	private LayoutInflater inflater;
	/**
	 * 
	 * @param context
	 * @param list  要显示的内容左右以#分隔
	 */
	public DescWeatherAdapter(Context context,List<String> list){
		this.list = list;
		this.context = context;
		this.inflater = LayoutInflater.from(context);
		
	}
	@Override
	public int getCount() {
		return list.size();
	}

	@Override
	public String getItem(int position) {
		return list.get(position);
	}

	@Override
	public long getItemId(int position) {
		return 0;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		String[] items = getItem(position).split("#");
		View view = null;
		ViewHolder viewHolder = null;
		if(convertView==null){
			view = inflater.inflate(R.layout.desc_weather_info_item, null);
			viewHolder = new ViewHolder();
			
			viewHolder.item = (TextView) view.findViewById(R.id.tv_desc_item);
			viewHolder.info = (TextView) view.findViewById(R.id.tv_desc_info);
			
			view.setTag(viewHolder); 
		}else{
			view = convertView;
			viewHolder = (ViewHolder) view.getTag();
		}
		
		
		viewHolder.item.setText(items[0]);
		viewHolder.info.setText(items[1]);
		
		return view;
	}
	
	private final class ViewHolder{
		TextView item;
		TextView info;
	}

}
