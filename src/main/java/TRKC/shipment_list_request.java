package TRKC;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONObject;

import API_Functions.General_API_Calls;

public class shipment_list_request {
	
	public static String totalNumberOfShipmentsParam = "totalNumberOfShipments";
	
	public static String shipmentListRequest(String Cookie, String PageToken){
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
		String ShipmentListResponse = shipmentListRequest(Cookies, "1");
		
		String Tracking_Details[][] = {
				{"REQUEST_RUN_DATE", "requestRunDate"},
				{"TOTAL_NUMBER_OF_SHIPMENTS", totalNumberOfShipmentsParam}};
		
		for (String[] NewElement: Tracking_Details) {
			Details = Arrays.copyOf(Details, Details.length + 1);
			NewElement[1] = API_Functions.General_API_Calls.ParseStringValue(ShipmentListResponse, NewElement[1]);
			Details[Details.length - 1] = NewElement;
		}

		return Details;
	}
	
/*	Works but causing memory issues with how big the full page list is.
 * 
 * public static String[][] getATRKShipmentList(String details[][], String cookies) {
		String pageIndex = "1";
		int pageSize = 500;
		String shipmentListResponse = ShipmentListRequest(cookies, pageIndex);

		String Tracking_Details[][] = {
				{"REQUEST_RUN_DATE", "requestRunDate"},
				{"TOTAL_NUMBER_OF_SHIPMENTS", "totalNumberOfShipments"}};

		details = Arrays.copyOf(details, details.length + 2);
		Tracking_Details[0][1] = API_Functions.General_API_Calls.ParseStringValue(shipmentListResponse, Tracking_Details[0][1]);
		Tracking_Details[1][1] = API_Functions.General_API_Calls.ParseStringValue(shipmentListResponse, Tracking_Details[1][1]);
		details = Tracking_Details;
		int totalNumberOfShipments = Integer.parseInt(details[1][1]);
		CopyOnWriteArrayList<String> shipmentLightList = new CopyOnWriteArrayList<String>();
		String shipmentLightListPage = "";
		if (shipmentLightList.size() == 0) {
			// Save first page.
			shipmentLightListPage = API_Functions.General_API_Calls.ParseStringValue(shipmentListResponse, "shipmentLightList");
			shipmentLightList.add(shipmentLightListPage);
		}
		
		while (Integer.parseInt(pageIndex) <= (totalNumberOfShipments - 500)) {
			pageIndex = (Integer.parseInt(pageIndex) + pageSize) + "";
			shipmentListResponse = ShipmentListRequest(cookies, pageIndex);
			shipmentLightListPage = API_Functions.General_API_Calls.ParseStringValue(shipmentListResponse, "shipmentLightList");
			shipmentLightList.add(shipmentLightListPage);
		}
		
		String fullshipmentListResponse = "";
		for (String page: shipmentLightList) {
			if (fullshipmentListResponse.contentEquals("")) {
				// remove the last character ']'
				page = page.substring(0, page.length() - 1);
				fullshipmentListResponse = page;
			} else {
				// remove the first character '[' and the last character ']' 
				page = page.substring(1, page.length() - 1);
				fullshipmentListResponse += page;
			}
		}
		
		// add final bracket to close the element.
		fullshipmentListResponse += "]";
		
		// Add only the shipment list.
		details = Arrays.copyOf(details, details.length + 1);
		details[details.length - 1] = new String[] {"shipmentListResponse", fullshipmentListResponse};
		
		return details;
	}*/
}
