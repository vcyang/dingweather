package activity;

import com.example.dingweather.R;

import util.HttpCallBackListener;
import util.HttpUtils;
import util.Utility;
import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.text.TextUtils;
import android.view.View;
import android.view.Window;
import android.widget.LinearLayout;
import android.widget.TextView;
//import android.widget.Toast;

public class WeatherActivity extends Activity {

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
		
		cityNameText=(TextView)findViewById(R.id.tv_city_name);
		publishTimeText=(TextView)findViewById(R.id.tv_publish_text);
		weatherInfoLayout=(LinearLayout)findViewById(R.id.lv_weatherinfo_layout);
		currentDateText=(TextView)findViewById(R.id.tv_current_date);
		weatherInfo=(TextView)findViewById(R.id.tv_weatherinfo);
		tempLowText=(TextView)findViewById(R.id.tv_temp_low);
		tempHighText=(TextView)findViewById(R.id.tv_temp_high);
		String selectedCounty=getIntent().getStringExtra("county_code");
		if(!TextUtils.isEmpty(selectedCounty)){
			//��ͬ���Ĺ��������ظ���ؼ�
			publishTimeText.setText("ͬ���С�����");
			cityNameText.setVisibility(View.INVISIBLE);
			weatherInfoLayout.setVisibility(View.INVISIBLE);
			queryWeatherCode(selectedCounty);
		}else{
			showWeather();
		}
	}
	
	private void queryWeatherCode(String countyCode){
		//��װһ����ַ������queryFromServer()�������в�ѯ
		String address="http://www.weather.com.cn/data/list3/city"+countyCode+".xml";
		queryFromServer(address, "countyCode");
	}
	
	private void queryWeatherInfo(String weatherCode){
		//��װһ����ַ������queryFromServer�������в�ѯ
		String address="http://www.weather.com.cn/data/cityinfo/"+weatherCode+".html";
		queryFromServer(address, weatherCode);
	}
	
	private void queryFromServer(String address, final String type){
		//������Ҫ�Դ����type�����жϣ����ݵ������롢��������Ĳ�ͬ���в�ͬ�Ĳ�ѯ������
		HttpUtils.sendHttpRequest(address, new HttpCallBackListener(){
			@Override
			public void onFinish(final String response){
				if("countyCode".equals(type)){
					//�������JSON���ݽ��������weatherCode
					if(!TextUtils.isEmpty(response)){
						String[] array=response.split("\\|");
						if(array!=null&&array.length==2){
							String weatherCode=array[1];
							queryWeatherInfo(weatherCode);
						}	
					}
				}else if("weatherCode".equals(type)){
					Utility.handleWeatherResponse(WeatherActivity.this, response);
					runOnUiThread(new Runnable(){
						@Override
						public void run(){
							showWeather();
						};
					});
				}
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
	
	private void showWeather(){
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
	}
}
