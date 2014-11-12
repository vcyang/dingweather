package util;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.HttpURLConnection;

public class HttpUtils {

	public static void sendHttpRequest(final String address, final HttpCallBackListener listener){
		new Thread(new Runnable(){
			@Override
			public void run(){
				HttpURLConnection connection=null;
				try{
					URL url=new URL(address);
					connection=(HttpURLConnection)url.openConnection();
					connection.setRequestMethod("GET");
					connection.setConnectTimeout(8000);
					connection.setReadTimeout(8000);
					InputStream in=connection.getInputStream();
					BufferedReader reader=new BufferedReader(new InputStreamReader(in));
					StringBuilder response=new StringBuilder();
					String line="";
					while((line=reader.readLine())!=null){
						response.append(line);
					}
					if(listener!=null){
						//������ɺ�ص�HttpCallBackListener��onfinish�������д���
						listener.onFinish(response.toString());
					}
				}catch(Exception e){
					//�ص���һ���ӿڽ��д���
					if(listener!=null){
						listener.onError(e);
					}
					
				}finally{
					if(connection!=null){
						connection.disconnect();						
					}
				}	
			}		
		}).start();
	}
}

