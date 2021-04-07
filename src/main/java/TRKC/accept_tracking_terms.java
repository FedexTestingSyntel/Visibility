package TRKC;

import java.util.ArrayList;
import java.util.List;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONObject;
import API_Functions.General_API_Calls;

public class accept_tracking_terms {
	
	/// Still needs to be tested
	
	public static String AcceptTrackingTermsRequest(String cookie){
  		try{
  			TRKC_Data TRKC_Details = TRKC_Data.LoadVariables();
  			HttpPost httppost = new HttpPost(TRKC_Details.TrackingGenericURL);
  				
  			JSONObject AcceptTrackingTermsRequest = new JSONObject()
  	  				.put("appType", "WTRK")
  	  				.put("appDeviceType", "DESKTOP")
  	  				.put("uniqueKey", "")
  	  				.put("processingParameters", "");
  			
  			JSONObject main = new JSONObject()
  	  				.put("AcceptTrackingTermsRequest", AcceptTrackingTermsRequest);

  			String json = main.toString();

  			httppost.addHeader("Content-Type", "application/x-www-form-urlencoded");
  			httppost.addHeader("Content-Type", "charset=UTF-8");
  			httppost.addHeader("Cookie", cookie);
  			
  			List<NameValuePair> urlParameters = new ArrayList<NameValuePair>();
  			urlParameters.add(new BasicNameValuePair("action", "acceptTrackingTerms"));
  			urlParameters.add(new BasicNameValuePair("format", "json"));
  			urlParameters.add(new BasicNameValuePair("version", "1"));
  			urlParameters.add(new BasicNameValuePair("locale", "en_US"));
  			urlParameters.add(new BasicNameValuePair("data", json));

  			httppost.setEntity(new UrlEncodedFormEntity(urlParameters));
  			/*
  			action: acceptTrackingTerms
  			locale: en_US
  			version: 1
  			format: json  
  			data: {"AcceptTrackingTermsRequest":{"appType":"WTRK","appDeviceType":"","uniqueKey":"","processingParameters":{}}}
  			*/
  			String Response = General_API_Calls.HTTPCall(httppost, json);	
			
  			return Response;
  		}catch (Exception e){
  			//e.printStackTrace();
  			return null;
  		}
  	}
}
