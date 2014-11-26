package activity;

import service.AutoUpdateService;
import util.HttpCallBackListener;
import util.HttpUtils;
import util.Utility;

import com.example.dingweather.R;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

public class ShowWeatherActivity extends Activity implements OnClickListener{
	
	private Button backToChoose;
	private Button refreshData;
	private TextView cityNameText;
	private TextView publishTimeText;
	private LinearLayout weatherInfoLayout;
	private TextView currentDateText;
	private TextView weatherInfo;
	private TextView tempLowText;
	private TextView tempHighText;
	
	@Override
	public void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.wether_layout);
		
		backToChoose=(Button)findViewById(R.id.bt_back_to_choose);
		refreshData=(Button)findViewById(R.id.bt_refresh);
		cityNameText=(TextView)findViewById(R.id.tv_city_name);
		publishTimeText=(TextView)findViewById(R.id.tv_publish_text);
		weatherInfoLayout=(LinearLayout)findViewById(R.id.lv_weatherinfo_layout);
		currentDateText=(TextView)findViewById(R.id.tv_current_date);
		weatherInfo=(TextView)findViewById(R.id.tv_weatherinfo);
		tempLowText=(TextView)findViewById(R.id.tv_temp_low);
		tempHighText=(TextView)findViewById(R.id.tv_temp_high);
		
		String name=getIntent().getStringExtra("cityName");
		Log.d("showName", "获得城市名： "+name);
		
		cityNameText.setText(name);
		String cityCode=getIntent().getStringExtra("cityCode");
		if(!TextUtils.isEmpty(cityCode)){
			//在同步的过程中隐藏各类控件
			publishTimeText.setText("同步中...");
			weatherInfoLayout.setVisibility(View.INVISIBLE);
			queryWeatherInfo(cityCode);
		}else{
			//展示已有的天气信息
			showWeather();
		}
		
		backToChoose.setOnClickListener(this);
		refreshData.setOnClickListener(this);
	}
	
	@Override
	public void onClick(View v){
		switch(v.getId()){
		case R.id.bt_back_to_choose:
			Intent intent=new Intent(this, SearchCityActivity.class);
			intent.putExtra("is_from_weatheractivity", true);
			startActivity(intent);
			finish();
			break;
		case R.id.bt_refresh:
			publishTimeText.setText("同步中。。。");
			SharedPreferences pref=PreferenceManager.getDefaultSharedPreferences(this);
			String cityCode=pref.getString("cityId", "");
			if(!TextUtils.isEmpty(cityCode)){
				queryWeatherInfo(cityCode);
			}
			break;
		default:
			break;
		}
	}
	
	//此处API不知能不能用，先测试一下
	public void queryWeatherInfo(final String cityCode){
		String address="http://www.weather.com.cn/data/cityinfo/"+cityCode+".html";
		HttpUtils.sendHttpRequest(address, new HttpCallBackListener(){
			@Override
			public void onFinish(String response){
				Utility.handleWeatherResponse(ShowWeatherActivity.this, response);
				runOnUiThread(new Runnable(){
					@Override
					public void run(){
						showWeather();
					}
				});
			}
			
			@Override
			public void onError(Exception e){
				runOnUiThread(new Runnable(){
					@Override
					public void run(){
				//		Toast.makeText(WeatherActivity.this, "同步失败！", Toast.LENGTH_SHORT).show();
						publishTimeText.setText("同步失败！");
					}
				});
			}
		});
		
	}
	
	public void showWeather(){
		//从已保存的sharedPreference文件中提取数据并设置到控件上显示
		SharedPreferences pref=PreferenceManager.getDefaultSharedPreferences(this);
		//检验是否能获取本地SharedPreferences文件数据
		String cityName=pref.getString("cityName", "");
		String publishTime=pref.getString("publishTime", "");
		String currentDate=pref.getString("currentDate", "");
		String weatherDescript=pref.getString("weatherDescript", "");
		String tempLow=pref.getString("tempLow", "");
		String tempHigh=pref.getString("tempHigh", "");
		Log.d("UtilitySharedPreGetTest", cityName+":"+tempLow+":"+tempHigh+":"+weatherDescript+":"+publishTime);
		
		cityNameText.setText(cityName);
		publishTimeText.setText("今天"+publishTime+"发布");
		currentDateText.setText(currentDate);
		weatherInfo.setText(weatherDescript);
		tempLowText.setText(tempLow);
		tempHighText.setText(tempHigh);
		
		publishTimeText.setVisibility(View.VISIBLE);
		weatherInfoLayout.setVisibility(View.VISIBLE);
		Intent intent=new Intent(this, AutoUpdateService.class);
		startService(intent);
	}
	
}
