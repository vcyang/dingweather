package util;


import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import org.json.JSONObject;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.text.TextUtils;
import db.AllCities;
import db.City;
import db.County;
import db.DingWeatherDB;
import db.Province;

public class Utility {

	public synchronized static boolean handleAllCitiesResponse(DingWeatherDB db, String response){
		if(!TextUtils.isEmpty(response)){
			String[] responseArray=response.split(";");
			if(responseArray!=null&&responseArray.length>0){
				for(int i=0;i<responseArray.length;i++){
					String[] array=responseArray[i].split(":");
					//将城市名和代码添加进AllCity类的cityName和cityCode里
					//用db.saveAllCities(AllCity)将数据保存进数据库
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
	
	
/*	public synchronized static boolean handleProvinceResponse(DingWeatherDB db, String response){
		if(!TextUtils.isEmpty(response)){
			String[] responseArray=response.split(",");
			if(responseArray!=null&&responseArray.length>0){
				for(int i=0; i<responseArray.length;i++){
					String[] array=responseArray[i].split("\\|");
					Province province=new Province();
					province.setProvinceCode(array[0]);
					province.setProvinceName(array[1]);
					db.saveProvince(province);
				}
				return true;
			}
		}
		return false;
	}
	
	public synchronized static boolean handleCityResponse(DingWeatherDB db, String response, int province_id){
		if(!TextUtils.isEmpty(response)){
			String[] responseArray=response.split(",");
			if(responseArray!=null&&responseArray.length>0){
				for(String c:responseArray){
					String[] array=c.split("\\|");
					City city=new City();
					city.setCityCode(array[0]);
					city.setCityName(array[1]);
					city.setProvinceId(province_id);
					db.saveCity(city);
				}
				return true;
			}
		}
		return false;
	}
	
	public synchronized static boolean handleCountyResponse(DingWeatherDB db, String response, int city_id){
		if(!TextUtils.isEmpty(response)){
			String[] responseArray=response.split(",");
			if(responseArray!=null&&responseArray.length>0){
				for(String c:responseArray){
					String[] array=c.split("\\|");
					County county=new County();
					county.setCountyCode(array[0]);
					county.setCountyName(array[1]);
					county.setCityId(city_id);
					db.saveCounty(county);
				}
				return true;
			}
		}
		return false;
	}
	*/
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
			saveWeatherInfo(context, cityName, cityId, tempLow, tempHigh, weatherDescript, publishTime);
		}catch(Exception e){
			e.printStackTrace();
		}
	}
	
	public static void saveWeatherInfo(Context context, String cityName, String cityId, String tempLow, String tempHigh, String weatherDescript, String publishTime){
		SimpleDateFormat sdf=new SimpleDateFormat("YYYY年M月D日", Locale.CHINA);
		String date=sdf.format(new Date());
		SharedPreferences.Editor editor=PreferenceManager.getDefaultSharedPreferences(context).edit();
		editor.putBoolean("city_selected", true);
		editor.putString("cityName", cityName);
		editor.putString("cityId", cityId);
		editor.putString("tempLow", tempLow);
		editor.putString("tempHigh", tempHigh);
		editor.putString("weatherDescript", weatherDescript);
		editor.putString("publishTime", publishTime);
		editor.putString("currentDate", date);
		editor.commit();
	}
}
