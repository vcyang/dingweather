package db;

public class City {
	
	private int id;
	
	private String city_name;
	
	private String city_code;
	
	private int province_id;
	
	public void setCityId(int id){
		this.id=id;
	}
	
	public int getCityId(){
		return id;
	}
	
	public void setCityName(String city_name){
		this.city_name=city_name;
	}
	
	public String getCityName(){
		return city_name;
	}
	
	public void setCityCode(String city_code){
		this.city_code=city_code;
	}
	
	public String getCityCode(){
		return city_code;
	}
	
	public void setProvinceId(int province_id){
		this.province_id=province_id;
	}
	
	public int getProvinceId(){
		return province_id;
	}
	
}
