package TRKC;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONObject;

import API_Functions.General_API_Calls;

public class shipment_list_request {
	public static String ShipmentListRequest(String Cookie, String PageToken){
  		try{
  			TRKC_Data TRKC_Details = TRKC_Data.LoadVariables();
  			HttpPost httppost = new HttpPost(TRKC_Details.TrackingGenericURL);

  			JSONObject processingParameters = new JSONObject();
  			String BlankArray[] = {};
  				
  			//{"ShipmentListRequest":{"appType":"WTRK","appDeviceType":"DESKTOP","uniqueKey":"","processingParameters":{},"pageSize":"500","pageToken":"1","sort":"PRIORITY","isSummaryCount":false,"updatedSinceTs":"","shipmentFilterList":[]}}
  			JSONObject ShipmentListRequest = new JSONObject()
  	  				.put("appType", "WTRK")
  	  				.put("appDeviceType", "DESKTOP")
  	  				.put("uniqueKey", "")
  	  				.put("processingParameters", processingParameters)
  	  				.put("pageSize", "500")
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
	
	public static String[][] getATRK_ShipmentList(String Details[][], String Cookies) {
		String ShipmentListResponse = ShipmentListRequest(Cookies, "1");
		
		String Tracking_Details[][] = {
				{"REQUEST_RUN_DATE", "requestRunDate"},
				{"TOTAL_NUMBER_OF_SHIPMENTS", "totalNumberOfShipments"}};
		
		for (String[] NewElement: Tracking_Details) {
			Details = Arrays.copyOf(Details, Details.length + 1);
			NewElement[1] = API_Functions.General_API_Calls.ParseStringValue(ShipmentListResponse, NewElement[1]);
			Details[Details.length - 1] = NewElement;
		}

		return Details;
	}

}
