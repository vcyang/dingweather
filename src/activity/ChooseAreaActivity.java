package activity;

import java.util.ArrayList;
import java.util.List;

import util.HttpCallBackListener;
import util.HttpUtils;
import util.Utility;

import com.example.dingweather.R;

import db.City;
import db.County;
import db.DingWeatherDB;
import db.Province;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.text.TextUtils;
import android.view.Window;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

public class ChooseAreaActivity extends Activity {
	
	public static final int PROVINCE_LEVEL=0;
	public static final int CITY_LEVEL=1;
	public static final int COUNTY_LEVEL=2;
	
	private ProgressDialog dialog;
	
	private TextView titleText;
	private ListView areaList;
	
	private DingWeatherDB dingWeatherDB;
	private ArrayAdapter<String> adapter;
	
	private List<String> dataList=new ArrayList<String>();
	private List<Province> provinceList;
	private List<City> cityList;
	private List<County> countyList;
	
	private Province selectedProvince;
	private City selectedCity;
	
	private int currentLevel;
	
	private boolean isFromWeatherActivity;

	@Override
	public void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		
		isFromWeatherActivity=getIntent().getBooleanExtra("is_from_weatheractivity", false);
		SharedPreferences pref=PreferenceManager.getDefaultSharedPreferences(this);
		if(pref.getBoolean("city_selected", false)&&!isFromWeatherActivity){
			Intent intent=new Intent(this, WeatherActivity.class);
			startActivity(intent);
			finish();
		}
		
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.choose_area);//��д����ʱ��ʾError������ʱ�п��ܳ���ע�⣡
		titleText=(TextView)findViewById(R.id.tv_title_text);
		areaList=(ListView)findViewById(R.id.lv_area_list);
		adapter=new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, dataList);
		areaList.setAdapter(adapter);
		dingWeatherDB=DingWeatherDB.getInstance(this);
		areaList.setOnItemClickListener(new OnItemClickListener(){
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id){
				if(currentLevel==PROVINCE_LEVEL){
					selectedProvince=provinceList.get(position);
					queryCities();
				}else if(currentLevel==CITY_LEVEL){
					selectedCity=cityList.get(position);
					queryCounties();
				}else if(currentLevel==COUNTY_LEVEL){
					String countyCode=countyList.get(position).getCountyCode();
					Intent intent=new Intent(ChooseAreaActivity.this, WeatherActivity.class);
					intent.putExtra("county_code", countyCode);
					startActivity(intent);
					finish();
				}
			}
		});
		queryProvinces();
	}
	
	private void queryProvinces(){
		provinceList=dingWeatherDB.loadProvince();
		if(provinceList.size()>0){
			dataList.clear();
			for(Province p:provinceList){
				dataList.add(p.getProvinceName());
			}
			adapter.notifyDataSetChanged();
			areaList.setSelection(0);
			titleText.setText("�й�");
			currentLevel=PROVINCE_LEVEL;
		}else{
			queryFromServer(null, "province");
		}
	}
	
	private void queryCities(){
		cityList=dingWeatherDB.loadCity(selectedProvince.getId());
		if(cityList.size()>0){
			dataList.clear();
			for(City c:cityList){
				dataList.add(c.getCityName());
			}
			adapter.notifyDataSetChanged();
			areaList.setSelection(0);
			titleText.setText(selectedProvince.getProvinceName());
			currentLevel=CITY_LEVEL;
		}else{
			queryFromServer(selectedProvince.getProvinceCode(), "city");
		}
	}
	
	/*�����ݿ��в���county*/
	private void queryCounties(){
		countyList=dingWeatherDB.loadCounty(selectedCity.getCityId());
		if(countyList.size()>0){
			dataList.clear();
			for(County c:countyList){
				dataList.add(c.getCountyName());
			}
			adapter.notifyDataSetChanged();
			areaList.setSelection(0);
			titleText.setText(selectedCity.getCityName());
			currentLevel=COUNTY_LEVEL;
		}else{
			queryFromServer(selectedCity.getCityCode(), "county");
		}
	}
	
	//��ǰ��address API�����ã���ȡ���˵�ַ�����룬�������ң������Ĳ��еĻ��͵��Լ�д��Ȼ������Լ��ķ���������
	
	private void queryFromServer(String code, final String type){
		//���ݴ���ĳ��б����ڷ��������Ҷ�Ӧ��city��county��type�����жϵ�ǰ�ҵ���city����county
		String address="";
		if(!TextUtils.isEmpty(code)){
			address="http://www.weather.com.cn/data/list3/city"+code+".xml";
		}else{
			address="http://www.weather.com.cn/data/list3/city.xml";
		}
		showProgressDialog();
		HttpUtils.sendHttpRequest(address, new HttpCallBackListener(){
			@Override
			public void onFinish(String response){
				boolean result=false;
				if("province".equals(type)){
					result=Utility.handleProvinceResponse(dingWeatherDB, response);
				}else if("city".equals(type)){
					result=Utility.handleCityResponse(dingWeatherDB, response, selectedProvince.getId());
				}else if("county".equals(type)){
					result=Utility.handleCountyResponse(dingWeatherDB, response, selectedCity.getCityId());
				}
				if(result){
					runOnUiThread(new Runnable(){
						@Override
						public void run(){
							closeProgressDialog();
							if("province".equals(type)){
								queryProvinces();
							}else if("city".equals(type)){
								queryCities();
							}else if("county".equals(type)){
								queryCounties();
							}
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
						Toast.makeText(ChooseAreaActivity.this, "����ʧ��", Toast.LENGTH_SHORT).show();
					}
				});
			}
		});
	}
	
	private void showProgressDialog(){
		//��ʾ���ؽ���
		if(dialog==null){
			dialog=new ProgressDialog(this);
			dialog.setTitle("�����������..");
			dialog.setMessage("loading������");
			dialog.setCanceledOnTouchOutside(false);
		}
		dialog.show();
	}
	
	private void closeProgressDialog(){
		//�رս�����ʾ
		if(dialog!=null){
			dialog.dismiss();
		}
	}
	
	@Override
	public void onBackPressed(){
		//�����˻ص���һ���˵�
		if(currentLevel==CITY_LEVEL){
			queryProvinces();
		}else if(currentLevel==COUNTY_LEVEL){
			queryCities();
		}else{
			if(isFromWeatherActivity){
				Intent intent=new Intent(ChooseAreaActivity.this, WeatherActivity.class);
				startActivity(intent);
			}
			finish();
		}
	}
}
