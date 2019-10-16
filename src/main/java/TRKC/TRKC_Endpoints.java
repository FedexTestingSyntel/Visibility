package TRKC;

import java.util.ArrayList;
import java.util.List;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONObject;
import API_Functions.General_API_Calls;

public class TRKC_Endpoints {
	
	public static String ShipmentOptionWatchListRequest(String Cookie,String trackingNumber, String trackingQualifier, String trackingCarrier){
  		try{
  			TRKC_Data TRKC_Details = TRKC_Data.LoadVariables();
  			HttpPost httppost = new HttpPost(TRKC_Details.TrackingWatchListURL);

  			JSONObject processingParameters = new JSONObject();
  			
  			JSONObject trackNumberInfoListElement = new JSONObject()
  					.put("trackingNumber", trackingNumber)
  	  				.put("trackingQualifier", trackingQualifier)
  	  				.put("trackingCarrier", trackingCarrier);
  			
  			Object trackNumberInfoList[] = new Object[] {trackNumberInfoListElement};
  			
  			//{"ShipmentOptionWatchListRequest":{"appType":"WTRK","appDeviceType":"DESKTOP","uniqueKey":"","processingParameters":{},"trackNumberInfoList":[{"trackingNumber":"794809791131","trackingQualifier":"2458704000~794809791131~FX","trackingCarrier":"FDXE"}],"watchList":true}}
  			JSONObject ShipmentOptionWatchListRequest = new JSONObject()
  				.put("appType", "WTRK")
  				.put("appDeviceType", "DESKTOP")
  				.put("uniqueKey", "")
  				.put("processingParameters", processingParameters)
  				.put("trackNumberInfoList", trackNumberInfoList)
  				.put("watchList", true);
  				
  			JSONObject main = new JSONObject()
  				.put("ShipmentOptionWatchListRequest", ShipmentOptionWatchListRequest);

  			String json = main.toString();
  				
  			httppost.addHeader("Accept", "application/json");
  			httppost.addHeader("Content-Type", "application/json");
  			httppost.addHeader("Cookie", Cookie);
  			
  			String Response = General_API_Calls.HTTPCall(httppost, json);	
			
  			//{"successful":true,"cxsTransactionId":null,"output":{"successful":true,"alerts":null,"cxsalerts":null}}
  			return Response;
  		}catch (Exception e){
  			//e.printStackTrace();
  			return null;
  		}
  	}
	
	public static String AcceptTrackingTermsRequest(String Cookie){
  		try{
  			TRKC_Data TRKC_Details = TRKC_Data.LoadVariables();
  			HttpPost httppost = new HttpPost(TRKC_Details.TrackingGenericURL);

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