package PRDC_Application;

import org.apache.http.client.methods.HttpGet;

import SupportClasses.General_API_Calls;
import SupportClasses.Helper_Functions;

public class PRDC_API_Endpoints {

	public static String Accounts_Call(String URL, String Cookie){
		HttpGet httpGet = new HttpGet(URL);
		
		httpGet.addHeader("Content-Type", "application/json");
		httpGet.addHeader("X-clientid", "WCRV");
		httpGet.addHeader("X-locale", "en_US");
		httpGet.addHeader("X-version", "1.0");
		httpGet.addHeader("Cookie", Cookie);
		
		String Response;
		try {
			Helper_Functions.PrintOut("Account Call.", true);
			Response = General_API_Calls.HTTPCall(httpGet, "");
			return Response;
		} catch (Exception e) {
			return e.getMessage();
		}	
	}
	
}
