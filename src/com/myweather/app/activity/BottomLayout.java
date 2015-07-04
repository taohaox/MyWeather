package com.myweather.app.activity;


import com.myweather.app.R;

import android.content.Context;
import android.view.LayoutInflater;
import android.widget.LinearLayout;

public class BottomLayout extends LinearLayout{

	public BottomLayout(Context context) {
		super(context);
		LayoutInflater.from(context).inflate(R.layout.bottom_menu, this);
	}

}
