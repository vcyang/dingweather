package activity;

import util.HttpCallBackListener;
import util.HttpUtils;
import util.Utility;

import com.example.dingweather.R;

import db.AllCities;
import db.DingWeatherDB;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

public class SearchCityActivity extends Activity implements OnClickListener{
	
	private DingWeatherDB db;
	private ProgressDialog dialog;
	
	private LinearLayout resultLayout;
	private EditText cityInput;
	private Button searchButton;
	private TextView resultText;
	private Button sureButton;
	
	private String cityName;
	private AllCities city;
	
	private boolean isFromShowWeatherActivity;

	@Override
	public void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		
		isFromShowWeatherActivity=getIntent().getBooleanExtra("is_from_weatheractivity", false);
		SharedPreferences pref=PreferenceManager.getDefaultSharedPreferences(this);
		if(pref.getBoolean("city_selected", false)&&!isFromShowWeatherActivity){
			Intent intent=new Intent(this, ShowWeatherActivity.class);
			startActivity(intent);
			finish();
		}
		
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.search_city);
		
		resultLayout=(LinearLayout)findViewById(R.id.ll_result);
		cityInput=(EditText)findViewById(R.id.et_input_cityname);
		searchButton=(Button)findViewById(R.id.bt_search_city);
		resultText=(TextView)findViewById(R.id.tv_show_cityname);
		sureButton=(Button)findViewById(R.id.bt_show_weather);
		
		resultLayout.setVisibility(View.INVISIBLE);
		cityName=cityInput.getText().toString().trim();
		searchButton.setOnClickListener(this);
		sureButton.setOnClickListener(this);
	}
	
	@Override
	public void onClick(View v){
		switch(v.getId()){
		case R.id.bt_search_city:
			city=db.searchCity(cityName);
			resultText.setText(city.getCityName());
			resultLayout.setVisibility(View.VISIBLE);
		case R.id.bt_show_weather:
			Intent intent=new Intent(SearchCityActivity.this, ShowWeatherActivity.class);
			intent.putExtra("cityName", cityName);
			intent.putExtra("cityCode", city.getCityCode());
			startActivity(intent);
			finish();
		}

	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu){
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item){
		switch(item.getItemId()){
		case R.id.update:
			updateDB();
			break;
		default:
			break;
		}
		return true;
	}
	
	public void showProgressDialog(){
		if(dialog==null){
			dialog=new ProgressDialog(SearchCityActivity.this);
			dialog.setTitle("加载数据库");
			dialog.setMessage("正在加载，很快的。。。");
			dialog.setCanceledOnTouchOutside(false);
		}
		dialog.show();
	}
	
	public void closeProgressDialog(){
		if(dialog!=null){
			dialog.dismiss();
		}
	}
	
	//此处的address要传入本机Apache服务器的地址，返回城市及编码数据；
	public void updateDB(){
		String address="";
		showProgressDialog();
		HttpUtils.sendHttpRequest(address, new HttpCallBackListener(){
			@Override
			public void onFinish(String response){
				boolean result=Utility.handleAllCitiesResponse(db, response);
				if(result){
					runOnUiThread(new Runnable(){
						@Override
						public void run(){
							closeProgressDialog();
						}
					});
				}
			}
			
			@Override
			public void onError(Exception e){
				runOnUiThread(new Runnable(){
					@Override
					public void run(){
						closeProgressDialog();
						Toast.makeText(SearchCityActivity.this, "加载失败", Toast.LENGTH_SHORT).show();
					}
				});
			}
		});				
	}
	
	
}
