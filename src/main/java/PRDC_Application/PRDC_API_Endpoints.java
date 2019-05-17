package PRDC_Application;

import org.apache.http.client.methods.HttpGet;
import SupportClasses.General_API_Calls;

public class PRDC_API_Endpoints {

	public static String PRDC_Accounts_Call(String URL, String Cookie){
		HttpGet httpGet = new HttpGet(URL);
		
		httpGet.addHeader("Content-Type", "application/json");
		httpGet.addHeader("X-clientid", "WCRV");
		httpGet.addHeader("X-locale", "en_US");
		httpGet.addHeader("X-version", "1.0");
		httpGet.addHeader("Cookie", Cookie);
		
		String Response;
		try {
			Response = General_API_Calls.HTTPCall(httpGet, "");
			return Response;
		} catch (Exception e) {
			return e.getMessage();
		}	
	}
	
}
