package TRKC_Application;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONObject;
import SupportClasses.General_API_Calls;
import SupportClasses.WebDriver_Functions;

public class TRKC_Endpoints {
	
	public static String TrackingPackagesRequest(String TrackingNumber){
  		try{
  			String LevelURL = WebDriver_Functions.LevelUrlReturn();
  			HttpPost httppost = new HttpPost( LevelURL + "/trackingCal/track");

  			JSONObject processingParameters = new JSONObject();
  				
  			//	{"TrackPackagesRequest":{"appType":"WTRK","appDeviceType":"DESKTOP","supportHTML":true,"supportCurrentLocation":true,"uniqueKey":"","processingParameters":{},"trackingInfoList":[{"trackNumberInfo":{"trackingNumber":"794946908060","trackingQualifier":"","trackingCarrier":""}}]}}
  			JSONObject trackNumberInfo = new JSONObject()
  	  				.put("trackingNumber", TrackingNumber)
  	  				.put("trackingQualifier", "")
  	  				.put("trackingCarrier", "");
  			
  			JSONObject trackingInfoListElement = new JSONObject()
  	  				.put("trackNumberInfo", trackNumberInfo);
  			
  			Object trackingInfoListArray[] = new Object[] {trackingInfoListElement};
  			
  			JSONObject TrackPackagesRequest = new JSONObject()
  	  				.put("appType", "WTRK")
  	  				.put("appDeviceType", "DESKTOP")
  	  				.put("supportHTML", true)
  	  				.put("supportCurrentLocation", true)
  	  				.put("uniqueKey", "")
  	  				.put("processingParameters", processingParameters)
  	  				.put("trackingInfoList", trackingInfoListArray);
  			
  			JSONObject main = new JSONObject()
  				.put("TrackPackagesRequest", TrackPackagesRequest);

  			String json = main.toString();
  				
  			httppost.addHeader("Content-Type", "application/x-www-form-urlencoded");
  			httppost.addHeader("Content-Type", "charset=UTF-8");
  			
  			List<NameValuePair> urlParameters = new ArrayList<NameValuePair>();
  			urlParameters.add(new BasicNameValuePair("action", "trackpackages"));
  			urlParameters.add(new BasicNameValuePair("format", "json"));
  			urlParameters.add(new BasicNameValuePair("version", "1"));
  			urlParameters.add(new BasicNameValuePair("locale", "en_US"));
  			urlParameters.add(new BasicNameValuePair("data", json));

  			httppost.setEntity(new UrlEncodedFormEntity(urlParameters));
  			String Response = General_API_Calls.HTTPCall(httppost, json);	
			
  			return Response;
  		}catch (Exception e){
  			//e.printStackTrace();
  			return null;
  		}
  	}
	
	public static String TrackingProfileRequest(String Cookie){
  		try{
  			String LevelURL = WebDriver_Functions.LevelUrlReturn();
  			HttpPost httppost = new HttpPost( LevelURL + "/trackingCal/track");

  			JSONObject processingParameters = new JSONObject();
  				
  			//	{"TrackingProfileRequest":{"appType":"WTRK","appDeviceType":"DESKTOP","uniqueKey":"","processingParameters":{}}}
  			JSONObject TrackingProfileRequest = new JSONObject()
  				.put("appType", "WTRK")
  				.put("appDeviceType", "DESKTOP")
  				.put("uniqueKey", "")
  				.put("processingParameters", processingParameters);
  				
  			JSONObject main = new JSONObject()
  				.put("TrackingProfileRequest", TrackingProfileRequest);

  			String json = main.toString();
  				
  			httppost.addHeader("Content-Type", "application/x-www-form-urlencoded");
  			httppost.addHeader("Content-Type", "charset=UTF-8");
  			
  			httppost.addHeader("Cookie", Cookie);
  			
  			List<NameValuePair> urlParameters = new ArrayList<NameValuePair>();
  			urlParameters.add(new BasicNameValuePair("action", "getTrackingProfile"));
  			urlParameters.add(new BasicNameValuePair("format", "json"));
  			urlParameters.add(new BasicNameValuePair("version", "1"));
  			urlParameters.add(new BasicNameValuePair("locale", "en_US"));

  			httppost.setEntity(new UrlEncodedFormEntity(urlParameters));
  			String Response = General_API_Calls.HTTPCall(httppost, json);	
			
  			return Response;
  		}catch (Exception e){
  			//e.printStackTrace();
  			return null;
  		}
  	}
	
	public static String AcceptTrackingTermsRequest(String Cookie){
  		try{
  			String LevelURL = WebDriver_Functions.LevelUrlReturn();
  			HttpPost httppost = new HttpPost( LevelURL + "/trackingCal/track");

  			JSONObject processingParameters = new JSONObject();
  				
  			//	{"AcceptTrackingTermsRequest":{"appType":"WTRK","appDeviceType":"DESKTOP","uniqueKey":"","processingParameters":{}}}
  			JSONObject AcceptTrackingTermsRequest = new JSONObject()
  				.put("appType", "WTRK")
  				.put("appDeviceType", "DESKTOP")
  				.put("uniqueKey", "")
  				.put("processingParameters", processingParameters);
  				
  			JSONObject main = new JSONObject()
  				.put("AcceptTrackingTermsRequest", AcceptTrackingTermsRequest);

  			String json = main.toString();
  				
  			httppost.addHeader("Content-Type", "application/x-www-form-urlencoded");
  			httppost.addHeader("Content-Type", "charset=UTF-8");
  			
  			httppost.addHeader("Cookie", Cookie);
  			
  			List<NameValuePair> urlParameters = new ArrayList<NameValuePair>();
  			urlParameters.add(new BasicNameValuePair("action", "acceptTrackingTerms"));
  			urlParameters.add(new BasicNameValuePair("format", "json"));
  			urlParameters.add(new BasicNameValuePair("version", "1"));
  			urlParameters.add(new BasicNameValuePair("locale", "en_US"));

  			httppost.setEntity(new UrlEncodedFormEntity(urlParameters));
  			String Response = General_API_Calls.HTTPCall(httppost, json);	
			
  			return Response;
  		}catch (Exception e){
  			//e.printStackTrace();
  			return null;
  		}
  	}
}