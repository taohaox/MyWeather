package com.myweather.app.receiver;

import com.myweather.app.service.AutoUpdateWeatherService;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.sax.StartElementListener;
import android.util.Log;

public class AlarmReceiver extends  BroadcastReceiver{

	@Override
	public void onReceive(Context context, Intent intent) {
		Log.e("aaa", "Æô¶¯ AlarmReceiver.");
		Intent i = new Intent(context,AutoUpdateWeatherService.class);
		context.startService(i);
	}

}
