package db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DingWeatherOpenHelper extends SQLiteOpenHelper{
	
	public static final String DB_NAME="DingWeatherDatabase";
	
	public static final String CREATE_PROVINCE="create table Province(id integer primary key autoincrement, province_name text, province_code text)";
	
	public static final String CREATE_CITY="create table City(id integer primary key autoincrement, city_name text, city_code text, province_id integer)";
	
	public static final String CREATE_COUNTY="create table County(id integer primary key autoincrement, county_name text, county_code text, city_id integer)";
	
	public static final String CREATE_ALLCITIES="create table AllCities(id integer primary key autoincrement, city_name text, city_code text)";
	
	public DingWeatherOpenHelper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version){
		super(context, name, factory, version);
	}
	
	@Override
	public void onCreate(SQLiteDatabase db){
		//创建数据库及表
		db.execSQL(CREATE_PROVINCE);
		db.execSQL(CREATE_CITY);
		db.execSQL(CREATE_COUNTY);
		db.execSQL(CREATE_ALLCITIES);
	}
	
	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion){
		//升级数据库及表
	}

}
