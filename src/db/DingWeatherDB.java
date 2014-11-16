package db;

import java.util.ArrayList;
import java.util.List;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

public class DingWeatherDB {
	//��������ʵ�ֶ����ݿ�Ĳ�����������ʡ���С���3�ű����ɾ�Ĳ顢
	//�������Ŀ�У���������һ��������ݿ���в����������������õ���ģʽ
	
	private SQLiteDatabase db;
	
	private DingWeatherOpenHelper helper;
	
	private static final String DB_NAME="DingWeatherDatabase";
	
	private static DingWeatherDB dingWeatherDB;
	
	public static final int VERSION=1;
	
	private DingWeatherDB(Context context){
		helper=new DingWeatherOpenHelper(context, DB_NAME, null, VERSION);
		db=helper.getWritableDatabase();
	}
	
	public synchronized static DingWeatherDB getInstance(Context context){
		if(dingWeatherDB==null){
			dingWeatherDB=new DingWeatherDB(context);
		}
		return dingWeatherDB;
	}
	
	public void saveAllCities(AllCities allCities){
		if(allCities!=null){
			ContentValues values=new ContentValues();
			values.put("city_name", allCities.getCityName());
			values.put("city_code", allCities.getCityCode());
			db.insert("AllCities", null, values);
		}
	}
	
	public List<AllCities> loadAllCities(){
		List<AllCities> list=new ArrayList<AllCities>();
		Cursor cursor=db.query("AllCities", null, null, null, null, null, null);
		if(cursor.moveToFirst()){
			do{
				AllCities cities=new AllCities();
				cities.setCityId(cursor.getInt(cursor.getColumnIndex("id")));
				cities.setCityName(cursor.getString(cursor.getColumnIndexOrThrow("city_name")));
				cities.setCityCode(cursor.getString(cursor.getColumnIndex("city_code")));
				list.add(cities);
			}while(cursor.moveToNext());
		}
		return list;
	}
	
	//����һ����������ĳ������Ʋ������ݿ���Ϣ��Ȼ�󷵻س������ֺͳ��д���ķ�����
	public AllCities searchCity(String cityName){
		AllCities city=null;
		if(cityName!=null){
			Cursor cursor=db.query("AllCities", null, "city_name=?", new String[]{cityName}, null, null, null);
			city=new AllCities();
			city.setCityName(cursor.getString(cursor.getColumnIndex("city_name")));
			city.setCityCode(cursor.getString(cursor.getColumnIndex("city_code")));
		}
		return city;
	}
	
	public void saveProvince(Province province){
		if(province!=null){
			ContentValues values=new ContentValues();
	//		values.put("id", province.getId());
			values.put("province_name", province.getProvinceName());
			values.put("province_code", province.getProvinceCode());		
			db.insert("Province", null, values);			
		}
	}
	
	public List<Province> loadProvince(){
		List<Province> list=new ArrayList<Province>();
		Cursor cursor=db.query("Province", null, null, null, null, null, null);
		if(cursor.moveToFirst()){
			Province province=new Province();
			do{
				province.setId(cursor.getInt(cursor.getColumnIndex("id")));
				province.setProvinceName(cursor.getString(cursor.getColumnIndex("province_name")));
				province.setProvinceCode(cursor.getString(cursor.getColumnIndex("province_code")));
				list.add(province);
			}while(cursor.moveToNext());
		}
		return list; 
	}
	
	public void saveCity(City city){
		if(city!=null){
			ContentValues values=new ContentValues();
			values.put("id", city.getCityId());
			values.put("city_name", city.getCityName());
			values.put("city_code", city.getCityCode());
			values.put("province_id", city.getProvinceId());
			db.insert("City", null, values);
		}
	}
	
	public List<City> loadCity(int provinceId){
		List<City> list=new ArrayList<City>();
		Cursor cursor=db.query("City", null, "province_id=?", new String[]{String.valueOf(provinceId)}, null, null, null);
		if(cursor.moveToFirst()){
			do{
				City city=new City();
				city.setCityId(cursor.getInt(cursor.getColumnIndex("id")));
				city.setCityName(cursor.getString(cursor.getColumnIndex("city_name")));
				city.setCityCode(cursor.getString(cursor.getColumnIndex("city_code")));
				city.setProvinceId(cursor.getInt(cursor.getColumnIndex("province_id")));
				list.add(city);
			}while(cursor.moveToNext());
		}
		return list;
	}
	
	public void saveCounty(County county){
		if(county!=null){
			ContentValues values=new ContentValues();
			values.put("id", county.getCountyId());
			values.put("county_name", county.getCountyName());
			values.put("county_code", county.getCountyCode());
			values.put("city_id", county.getCityId());
			db.insert("County", null, values);
		}
		
	}
	
	public List<County> loadCounty(int cityId){
		List<County> list=new ArrayList<County>();
		Cursor cursor=db.query("County", null, "city_id=?", new String[]{String.valueOf(cityId)}, null, null, null);
		if(cursor.moveToFirst()){
			do{
				County county=new County();
				county.setCountyId(cursor.getInt(cursor.getColumnIndex("id")));
				county.setCountyName(cursor.getString(cursor.getColumnIndex("county_name")));
				county.setCountyCode(cursor.getString(cursor.getColumnIndex("county_code")));
				county.setCityId(cursor.getInt(cursor.getColumnIndex("city_id")));
				list.add(county);
			}while(cursor.moveToNext());
		}
		return list;
	}
}
