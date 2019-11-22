package TRKC;

import java.util.ArrayList;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONObject;

import API_Functions.General_API_Calls;

public class shipment_option_add_request {
	
	public static String ShipmentOptionAddRequest(String Cookie, String trackingNumber, String trackingQualifier, String trackingCarrier){
  		try{
  			TRKC_Data TRKC_Details = TRKC_Data.LoadVariables();
  			HttpPost httppost = new HttpPost(TRKC_Details.TrackingGenericURL);

  			JSONObject processingParameters = new JSONObject();
  			
  			JSONObject trackNumberInfoListElement = new JSONObject()
  					.put("trackingNumber", trackingNumber)
  	  				.put("trackingQualifier", trackingQualifier)
  	  				.put("trackingCarrier", trackingCarrier);
  			
  			//{"ShipmentOptionAddRequest":{"appType":"WTRK","appDeviceType":"DESKTOP","uniqueKey":"","processingParameters":{},"trackNumberInfo":{"trackingNumber":"794809610102","trackingQualifier":"2458682000~794809610102~FX","trackingCarrier":"FDXE"}}}
  			JSONObject ShipmentOptionAddRequest = new JSONObject()
  				.put("appType", "WTRK")
  				.put("appDeviceType", "DESKTOP")
  				.put("uniqueKey", "")
  				.put("processingParameters", processingParameters)
  				.put("trackNumberInfo", trackNumberInfoListElement);
  				
  			JSONObject main = new JSONObject()
  				.put("ShipmentOptionAddRequest", ShipmentOptionAddRequest);

  			String json = main.toString();
  				
  			httppost.addHeader("Content-Type", "application/x-www-form-urlencoded");
  			httppost.addHeader("Content-Type", "charset=UTF-8");
  			httppost.addHeader("Cookie", Cookie);
  			
  			List<NameValuePair> urlParameters = new ArrayList<NameValuePair>();
  			urlParameters.add(new BasicNameValuePair("action", "addshipment"));
  			urlParameters.add(new BasicNameValuePair("format", "json"));
  			urlParameters.add(new BasicNameValuePair("version", "1"));
  			urlParameters.add(new BasicNameValuePair("locale", "en_US"));
  			urlParameters.add(new BasicNameValuePair("data", json));

  			httppost.setEntity(new UrlEncodedFormEntity(urlParameters));
  			
  			String Response = General_API_Calls.HTTPCall(httppost, json);	
			
  			//{"ShipmentOptionResponse":{"successful":true,"alerts":null,"cxsalerts":null}}
  			return Response;
  		}catch (Exception e){
  			//e.printStackTrace();
  			return null;
  		}
  	}

}
