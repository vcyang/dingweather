package activity;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

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
import android.util.Log;
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
		
		db=DingWeatherDB.getInstance(this);
		
		resultLayout=(LinearLayout)findViewById(R.id.ll_result);
		cityInput=(EditText)findViewById(R.id.et_input_cityname);
		searchButton=(Button)findViewById(R.id.bt_search_city);
		resultText=(TextView)findViewById(R.id.tv_show_cityname);
		sureButton=(Button)findViewById(R.id.bt_show_weather);
		
		resultLayout.setVisibility(View.INVISIBLE);
		searchButton.setOnClickListener(this);
		sureButton.setOnClickListener(this);
	}
	
	@Override
	public void onClick(View v){
		switch(v.getId()){
		case R.id.bt_search_city:
			cityName=cityInput.getText().toString().trim();
			if(cityName!=null){
				city=db.searchCity(cityName);
			}else{
				Toast.makeText(this, "地名不能为空", Toast.LENGTH_SHORT).show();
				return;
			}			
			if(city==null){
				Toast.makeText(this, "不存在此城市", Toast.LENGTH_SHORT).show();
				return;
			}
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
//			createDatabaseTest("weather.txt");
			showProgressDialog();
			createDatabase("weather.txt", new HttpCallBackListener(){
				@Override
				public void onFinish(String response){
					boolean result=Utility.handleAllCitiesResponse(db, response);
					if(result){
						runOnUiThread(new Runnable(){
							@Override
							public void run(){
								Toast.makeText(SearchCityActivity.this, "加载成功", Toast.LENGTH_SHORT).show();
								closeProgressDialog();
							}
						});
					}
				}
				
				@Override
				public void onError(Exception e){
					Toast.makeText(SearchCityActivity.this, "加载数据库失败！", Toast.LENGTH_SHORT).show();
					e.printStackTrace();
				}
			});
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
			dialog.setMessage("正在加载，请稍等~");
			dialog.setCanceledOnTouchOutside(false);
		}
		dialog.show();
	}
	
	public void closeProgressDialog(){
		if(dialog!=null){
			dialog.dismiss();
		}
	}
		
	//从assets文件中读取城市编码的信息并加载进数据库
	public void createDatabase(final String fileName, final HttpCallBackListener listener){
		new Thread(new Runnable(){
			@Override
			public void run(){
				InputStream in=null;
				BufferedReader reader=null;
				try{
					in = getResources().getAssets().open(fileName);
					reader=new BufferedReader(new InputStreamReader(in, "GB2312"));
					StringBuilder strb=new StringBuilder();
					String line="";
					while((line=reader.readLine())!=null){
						strb.append(line);
					}
					if(listener!=null){
						listener.onFinish(strb.toString());
					}
				}catch(IOException e){
					if(listener!=null){
						listener.onError(e);
					}
				}
				if(reader!=null){
					try{
						reader.close();
					}catch(Exception e){
						e.printStackTrace();
					}
				}
			}
		}).start();
	}	
	
	@Override
	public void onBackPressed(){
		if(isFromShowWeatherActivity){
			Intent intent= new Intent(SearchCityActivity.this, ShowWeatherActivity.class);
			startActivity(intent);
		}
		finish();
	}
}
