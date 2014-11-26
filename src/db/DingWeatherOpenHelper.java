package db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DingWeatherOpenHelper extends SQLiteOpenHelper{
		
	public static final String CREATE_ALLCITIES="create table AllCities(id integer primary key autoincrement, city_name text, city_code text)";
	
	public DingWeatherOpenHelper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version){
		super(context, name, factory, version);
	} 
	
	@Override
	public void onCreate(SQLiteDatabase db){
		//创建数据库及表
		db.execSQL(CREATE_ALLCITIES);
	}
	
	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion){
		//升级数据库及表
	}

}
