package db;

public class AllCities {
	
	private int city_id;
	private String city_name;
	private String city_code;
	
	public void setCityId(int cityId){
		this.city_id=cityId;
	}
	
	public int getCityId(){
		return city_id;
	}
	
	public void setCityName(String cityName){
		this.city_name=cityName;
	}
	
	public String getCityName(){
		return city_name;
	}
	
	public void setCityCode(String cityCode){
		this.city_code=cityCode;
	}
	
	public String getCityCode(){
		return city_code;
	}

}
