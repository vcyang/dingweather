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
}
