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
		
		cityNameText.setText(getIntent().getStringExtra("cityName"));
		String cityCode=getIntent().getStringExtra("cityCode");
		if(!TextUtils.isEmpty(cityCode)){
			//��ͬ���Ĺ��������ظ���ؼ�
			publishTimeText.setText("ͬ���С�����");
			weatherInfoLayout.setVisibility(View.INVISIBLE);
			queryWeatherInfo(cityCode);
		}else{
			//չʾ���е�������Ϣ
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
			publishTimeText.setText("ͬ���С�����");
			SharedPreferences pref=PreferenceManager.getDefaultSharedPreferences(this);
			String weatherCode=pref.getString("cityId", "");
			if(!TextUtils.isEmpty(weatherCode)){
				queryWeatherInfo(weatherCode);
			}
			break;
		default:
			break;
		}
	}
	
	//�˴�API��֪�ܲ����ã��Ȳ���һ��
	public void queryWeatherInfo(final String cityCode){
		String address="http://m.weather.com.cn/data/"+cityCode+".html";
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
				//		Toast.makeText(WeatherActivity.this, "ͬ��ʧ�ܣ�", Toast.LENGTH_SHORT).show();
						publishTimeText.setText("ͬ��ʧ�ܣ�");
					}
				});
			}
		});
		
	}
	
	public void showWeather(){
		//���ѱ����sharedPreference�ļ�����ȡ���ݲ����õ��ؼ�����ʾ
		SharedPreferences pref=PreferenceManager.getDefaultSharedPreferences(this);
		cityNameText.setText(pref.getString("cityName", ""));
		publishTimeText.setText("����"+pref.getString("publishTime", "")+"����");
		currentDateText.setText(pref.getString("currentDate", ""));
		weatherInfo.setText(pref.getString("weatherDescript", ""));
		tempLowText.setText(pref.getString("tempLow", ""));
		tempHighText.setText(pref.getString("tempHigh", ""));
		publishTimeText.setVisibility(View.VISIBLE);
		weatherInfoLayout.setVisibility(View.VISIBLE);
		Intent intent=new Intent(this, AutoUpdateService.class);
		startService(intent);
	}

}
