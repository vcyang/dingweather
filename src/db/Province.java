package db;

public class Province {
	
	private int id;
	
	private String province_name;
	
	private String province_code;
	
	public void setId(int id){
		this.id=id;
	}
	
	public int getId(){
		return id;
	}
	
	public void setProvinceName(String province_name){
		this.province_name=province_name;
	}
	
	public String getProvinceName(){
		return province_name;
	}
	
	public void setProvinceCode(String province_code){
		this.province_code=province_code;
	}
	
	public String getProvinceCode(){
		return province_code;
	}

}
