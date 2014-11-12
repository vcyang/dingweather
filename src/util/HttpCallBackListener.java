package util;

public interface HttpCallBackListener {
	
	public abstract void onFinish(String result);
	
	public abstract void onError(Exception e);

}
