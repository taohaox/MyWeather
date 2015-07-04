package com.myweather.app.util;

import java.io.InputStream;

public interface HttpCallbackListener {
	
	void onError(Exception e);


	void onFinish(String response, InputStream in);
}
