package util;


import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import org.json.JSONObject;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.text.TextUtils;
import android.util.Log;
import db.AllCities;
import db.DingWeatherDB;


public class Utility {

	public synchronized static boolean handleAllCitiesResponse(DingWeatherDB db, String response){
		if(!TextUtils.isEmpty(response)){
			String[] responseArray=response.split(";");
			if(responseArray!=null&&responseArray.length>0){
				for(int i=0;i<responseArray.length;i++){
					String[] array=responseArray[i].split(":");
					//���������ʹ�����ӽ�AllCity���cityName��cityCode��
					//��db.saveAllCities(AllCity)�����ݱ�������ݿ�
					AllCities cities=new AllCities();
					cities.setCityName(array[0]);
					cities.setCityCode(array[1]);
					db.saveAllCities(cities);
				}
				return true;
			}
		}
		return false;
	}

	public static void handleWeatherResponse(Context context, String response){
		try{
			JSONObject jsonObject=new JSONObject(response);
			JSONObject weatherInfo=jsonObject.getJSONObject("weatherinfo");
			String cityName=weatherInfo.getString("city");
			String cityId=weatherInfo.getString("cityid");
			String tempLow=weatherInfo.getString("temp1");
			String tempHigh=weatherInfo.getString("temp2");
			String weatherDescript=weatherInfo.getString("weather");
			String publishTime=weatherInfo.getString("ptime");
			//�����Ƿ�ɹ�����
			Log.d("UtilitySharedPreGet", cityName+":"+cityId+":"+tempLow+":"+tempHigh+":"+weatherDescript+":"+publishTime);
			
			saveWeatherInfo(context, cityName, cityId, tempLow, tempHigh, weatherDescript, publishTime);
		}catch(Exception e){
			e.printStackTrace();
		}
	}
	
	public static void saveWeatherInfo(Context context, String cityName, String cityId, String tempLow, String tempHigh, String weatherDescript, String publishTime){
		SimpleDateFormat sdf=new SimpleDateFormat("yyyy��M��d��", Locale.CHINA);
		SharedPreferences.Editor editor=PreferenceManager.getDefaultSharedPreferences(context).edit();
		editor.putBoolean("city_selected", true);
		editor.putString("cityName", cityName);
		editor.putString("cityId", cityId);
		editor.putString("tempLow", tempLow);
		editor.putString("tempHigh", tempHigh);
		editor.putString("weatherDescript", weatherDescript);
		editor.putString("publishTime", publishTime);
		editor.putString("currentDate", sdf.format(new Date()));
		editor.commit();
		//�����Ƿ�ɹ����浽����SharedPreferences�ļ�
		Log.d("SaveWeatherInfoend", cityName+":"+cityId+":"+tempLow+":"+tempHigh+":"+weatherDescript+":"+publishTime);
	}
}
