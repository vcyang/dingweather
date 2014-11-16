package service;

import receiver.AutoUpdateReceiver;
import util.HttpCallBackListener;
import util.HttpUtils;
import util.Utility;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.IBinder;
import android.os.SystemClock;
import android.preference.PreferenceManager;

public class AutoUpdateService extends Service{
	
	@Override
	public IBinder onBind(Intent intent){
		return null;
	}
	
	@Override
	public int onStartCommand(Intent intent, int flags, int startId){
		 new Thread(new Runnable(){
			 @Override
			 public void run(){
				 updateWeather();
			 }
		 }).start();
		AlarmManager manager=(AlarmManager)getSystemService(Context.ALARM_SERVICE);
		long triggerAtTime=SystemClock.elapsedRealtime()+8*60*60*1000;
		Intent i=new Intent(this, AutoUpdateReceiver.class);
		PendingIntent pi=PendingIntent.getBroadcast(this, 0, i, 0);
		manager.set(AlarmManager.ELAPSED_REALTIME, triggerAtTime, pi);
		return super.onStartCommand(intent, flags, startId);
	}
	
	
	//API已作废，查不到
	public void updateWeather(){
		SharedPreferences pref=PreferenceManager.getDefaultSharedPreferences(this);
		String countyCode=pref.getString("cityId", "");
		//以下地址已作废，查不到
		String address="Http://weather.com.cn/data/cityinfo/"+countyCode+".html";
		HttpUtils.sendHttpRequest(address, new HttpCallBackListener(){
			@Override
			public void onFinish(String response){
				Utility.handleWeatherResponse(AutoUpdateService.this, response);
			}
			
			@Override
			public void onError(Exception e){
				e.printStackTrace();
			}
		});
	}

}
