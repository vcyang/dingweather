package db;

import java.util.ArrayList;
import java.util.List;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

public class DingWeatherDB {
	//此类用来实现对数据库的操作，包括对省、市、区3张表的增删改查、
	//在这个项目中，仅能允许一个类对数据库进行操作，因此这里需采用单例模式
	
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
	
	//增加一个根据输入的城市名称查找数据库信息，然后返回城市名字和城市代码的方法；
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
}
