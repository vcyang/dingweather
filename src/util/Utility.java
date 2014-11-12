package util;

import android.text.TextUtils;
import db.City;
import db.County;
import db.DingWeatherDB;
import db.Province;

public class Utility {

	public synchronized static boolean handleProvinceResponse(DingWeatherDB db, String response){
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
}
