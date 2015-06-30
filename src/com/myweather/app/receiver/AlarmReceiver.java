package com.myweather.app.receiver;

import com.myweather.app.service.AutoUpdateWeatherService;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.sax.StartElementListener;

public class AlarmReceiver extends  BroadcastReceiver{

	@Override
	public void onReceive(Context context, Intent intent) {
		Intent intnet = new Intent(context,AutoUpdateWeatherService.class);
		context.startService(intent);
	}

}
