package com.myweather.app.util;

import java.net.URLEncoder;
import java.security.InvalidKeyException;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

public class URLEncoderUtil {
	private static final char last2byte = (char) Integer.parseInt("00000011", 2);
    private static final char last4byte = (char) Integer.parseInt("00001111", 2);
    private static final char last6byte = (char) Integer.parseInt("00111111", 2);
    private static final char lead6byte = (char) Integer.parseInt("11111100", 2);
    private static final char lead4byte = (char) Integer.parseInt("11110000", 2);
    private static final char lead2byte = (char) Integer.parseInt("11000000", 2);
    private static final char[] encodeTable = new char[] { 'A', 'B', 'C', 'D',
            'E', 'F', 'G', 'H', 'I', 'J', 'K', 'L', 'M', 'N', 'O', 'P', 'Q',
            'R', 'S', 'T', 'U', 'V', 'W', 'X', 'Y', 'Z', 'a', 'b', 'c', 'd',
            'e', 'f', 'g', 'h', 'i', 'j', 'k', 'l', 'm', 'n', 'o', 'p', 'q',
            'r', 's', 't', 'u', 'v', 'w', 'x', 'y', 'z', '0', '1', '2', '3',
            '4', '5', '6', '7', '8', '9', '+', '/'
    };
    //AppId：1c5f77329cc6da87 
    //Private_Key：07b225_SmartWeatherAPI_6e2befe
    public static final String PRIVATE_KEY = "07b225_SmartWeatherAPI_6e2befe";
    public static final String  APPID = "1c5f77329cc6da87";
    /**
     * 指数基础
     */
    public static final String  INDEX_F = "index_f";
    /**
     * 指数常规
     */
    public static final String  INDEX_V = "index_v";
    /**
     * 3 天常规预报(24 小时) 基础
     */
    public static final String  FORECAST_F = "forecast_f";
    /**
     * 3 天常规预报(24 小时) 常规
     */
    public static final String  FORECAST_V = "forecast_v";
    
    public static SimpleDateFormat sf = new SimpleDateFormat("yyyyMMddHHmm");
 
    public static String standardURLEncoder(String data, String key) {
        byte[] byteHMAC = null;
        String urlEncoder = "";
        try {
            Mac mac = Mac.getInstance("HmacSHA1");
            SecretKeySpec spec = new SecretKeySpec(key.getBytes(), "HmacSHA1");
            mac.init(spec);
            byteHMAC = mac.doFinal(data.getBytes());
            if (byteHMAC != null) {
                String oauth = encode(byteHMAC);
                if (oauth != null) {
                    urlEncoder = URLEncoder.encode(oauth, "utf8");
                }
            }
        } catch (InvalidKeyException e1) {
            e1.printStackTrace();
        } catch (Exception e2) {
            e2.printStackTrace();
        }
        return urlEncoder;
    }
 
    public static String encode(byte[] from) {
        StringBuffer to = new StringBuffer((int) (from.length * 1.34) + 3);
        int num = 0;
        char currentByte = 0;
        for (int i = 0; i < from.length; i++) {
            num = num % 8;
            while (num < 8) {
                switch (num) {
                case 0:
                    currentByte = (char) (from[i] & lead6byte);
                    currentByte = (char) (currentByte >>> 2);
                    break;
                case 2:
                    currentByte = (char) (from[i] & last6byte);
                    break;
                case 4:
                    currentByte = (char) (from[i] & last4byte);
                    currentByte = (char) (currentByte << 2);
                    if ((i + 1) < from.length) {
                        currentByte |= (from[i + 1] & lead2byte) >>> 6;
                    }
                    break;
                case 6:
                    currentByte = (char) (from[i] & last2byte);
                    currentByte = (char) (currentByte << 4);
                    if ((i + 1) < from.length) {
                        currentByte |= (from[i + 1] & lead4byte) >>> 4;
                    }
                    break;
                }
                to.append(encodeTable[currentByte]);
                num += 6;
            }
        }
        if (to.length() % 4 != 0) {
            for (int i = 4 - to.length() % 4; i > 0; i--) {
                to.append("=");
            }
        }
        return to.toString();
    }
    /**
     * 得到可以向weather.com.cn 发送的 url 
     * @param areaid  地区id
     * @param type   需要返回数据的类型
     * @return  合成可以向中国天气网发送请求合法的url
     */
    public static String getUrlEncoder(String areaid,String type) {
        try {
        	Date date = new Date();
            //需要加密的数据    date 为当前时间 格式为yyyyMMddHHmm
            String public_key = "http://open.weather.com.cn/data/?areaid="+areaid+"&type="+type+"&date="+sf.format(date)+"&appid="+APPID;  
            //密钥  
            String str = standardURLEncoder(public_key, PRIVATE_KEY);
            str = "http://open.weather.com.cn/data/?areaid="+areaid+"&type="+type+"&date="+sf.format(date)+"&appid="+APPID.substring(0, 6)+"&key="+str;
            return str;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "";
    }
}
