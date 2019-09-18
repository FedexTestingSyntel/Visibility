package TRKC;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONObject;

import API_Functions.General_API_Calls;
import SupportClasses.WebDriver_Functions;

public class TRKC_Endpoints {
	
	
	public static String TrackingPackagesRequest(String TrackingNumberArray[]){
		Object trackingInfoListArray[] = new Object[TrackingNumberArray.length];
		
		for (int i = 0; i < TrackingNumberArray.length; i++) {
			String TrackingNumber = TrackingNumberArray[i];
			
			JSONObject trackNumberInfo = new JSONObject()
  	  				.put("trackingNumber", TrackingNumber)
  	  				.put("trackingQualifier", "")
  	  				.put("trackingCarrier", "");
  			
  			JSONObject trackingInfoListElement = new JSONObject()
  	  				.put("trackNumberInfo", trackNumberInfo);
  			
  			trackingInfoListArray[i] = trackingInfoListElement;
		}
		return TrackingPackagesRequest(trackingInfoListArray);
	}
	
	public static String TrackingPackagesRequest(String TrackingNumber){
		JSONObject trackNumberInfo = new JSONObject()
	  				.put("trackingNumber", TrackingNumber)
	  				.put("trackingQualifier", "")
	  				.put("trackingCarrier", "");
			
		JSONObject trackingInfoListElement = new JSONObject()
	  				.put("trackNumberInfo", trackNumberInfo);
			
		Object trackingInfoListArray[] = new Object[] {trackingInfoListElement};
		
		return TrackingPackagesRequest(trackingInfoListArray);
	}
	
	public static String TrackingPackagesRequest(Object trackingInfoListArray[]){
  		try{
  			String LevelURL = WebDriver_Functions.LevelUrlReturn();
  			HttpPost httppost = new HttpPost( LevelURL + "/trackingCal/track");

  			JSONObject processingParameters = new JSONObject();
  			
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
	
	public static String ShipmentOptionAddRequest(String Cookie,String trackingNumber, String trackingQualifier, String trackingCarrier){
  		try{
  			String LevelURL = WebDriver_Functions.LevelUrlReturn();
  			HttpPost httppost = new HttpPost( LevelURL + "/trackingCal/track");

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
	
	public static String ShipmentOptionWatchListRequest(String Cookie,String trackingNumber, String trackingQualifier, String trackingCarrier){
  		try{
  			String LevelURL = WebDriver_Functions.LevelUrlReturn();
  			HttpPost httppost = new HttpPost( LevelURL + "/trackingCal/track/v1/shipmentoptions/watch");

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

	public static String ShipmentListRequest(String Cookie, String PageToken){
  		try{
  			String LevelURL = WebDriver_Functions.LevelUrlReturn();
  			HttpPost httppost = new HttpPost( LevelURL + "/trackingCal/track");

  			JSONObject processingParameters = new JSONObject();
  			String BlankArray[] = {};
  				
  			//{"ShipmentListRequest":{"appType":"WTRK","appDeviceType":"DESKTOP","uniqueKey":"","processingParameters":{},"pageSize":"500","pageToken":"1","sort":"PRIORITY","isSummaryCount":false,"updatedSinceTs":"","shipmentFilterList":[]}}
  			JSONObject ShipmentListRequest = new JSONObject()
  	  				.put("appType", "WTRK")
  	  				.put("appDeviceType", "DESKTOP")
  	  				.put("uniqueKey", "")
  	  				.put("processingParameters", processingParameters)
  	  				.put("pageSize", "500") // test making this larger
  	  				.put("pageToken", PageToken)
  	  				.put("sort", "PRIORITY")
  	  				.put("isSummaryCount", false)
  	  				.put("updatedSinceTs", "")
  	  				.put("shipmentFilterList", BlankArray);
  			
  			JSONObject main = new JSONObject()
  	  				.put("ShipmentListRequest", ShipmentListRequest);

  			String json = main.toString();
  				
  			httppost.addHeader("Content-Type", "application/x-www-form-urlencoded");
  			httppost.addHeader("Content-Type", "charset=UTF-8");
  			httppost.addHeader("Cookie", Cookie);
  			
  			List<NameValuePair> urlParameters = new ArrayList<NameValuePair>();
  			urlParameters.add(new BasicNameValuePair("action", "getShipmentList"));
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
}