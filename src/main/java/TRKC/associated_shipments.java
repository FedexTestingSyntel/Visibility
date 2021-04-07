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
import SupportClasses.Helper_Functions;

public class associated_shipments {
	public static String AssociatedShipmentRequest(String trackingNumber, String trackingQualifier, String trackingCarrier){
  		try{
  			TRKC_Data TRKC_Details = TRKC_Data.LoadVariables();
  			HttpPost httppost = new HttpPost(TRKC_Details.TrackingGenericURL);

  			JSONObject processingParameters = new JSONObject();
  	
  			JSONObject trackingNumberInfoList = new JSONObject()
  					.put("trackingNumber", trackingNumber)
  					.put("trackingQualifier", trackingQualifier)
  					.put("trackingCarrier", trackingCarrier);
  			
  			JSONObject masterTrackingNumberInfoList = new JSONObject()
  					.put("trackingNumberInfo", trackingNumberInfoList)
  	  				.put("associatedType", "GMPS");
  				
  			//{"ShipmentListRequest":{"appType":"WTRK","appDeviceType":"DESKTOP","uniqueKey":"","processingParameters":{},"pageSize":"500","pageToken":"1","sort":"PRIORITY","isSummaryCount":false,"updatedSinceTs":"","shipmentFilterList":[]}}
  			JSONObject AssociatedShipmentRequest = new JSONObject()
  	  				.put("appType", "WTRK")
  	  				.put("appDeviceType", "DESKTOP")
  	  				.put("uniqueKey", "")
  	  				.put("processingParameters", processingParameters)
  	  				.put("masterTrackingNumberInfo", masterTrackingNumberInfoList);
  			
  			JSONObject main = new JSONObject()
  	  				.put("AssociatedShipmentRequest", AssociatedShipmentRequest);

  			String json = main.toString();
  				
  			//httppost.addHeader("Content-Type", "text/html"); // even though will see text/html in console will need to user for -url below.
  			httppost.addHeader("Content-Type", "application/x-www-form-urlencoded");
  			httppost.addHeader("Content-Type", "charset=UTF-8");
  			
  			List<NameValuePair> urlParameters = new ArrayList<NameValuePair>();
  			urlParameters.add(new BasicNameValuePair("action", "getAssociatedShipments"));
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
	
	public static String[][] getAssociatedShipment(String Details[][], String trackingNumber, String trackingQualifier, String trackingCarrier) {
		String AssociatedShipmentResponse = AssociatedShipmentRequest(trackingNumber, trackingQualifier, trackingCarrier);
		if (Helper_Functions.isNullOrUndefined(AssociatedShipmentResponse) || !AssociatedShipmentResponse.contains("\"successful\":true")) {
			// call did not complete successfully
			return Details;
		}
		
		String Tracking_Details[] = {"associatedShipmentResponse", AssociatedShipmentResponse};
		
		if (AssociatedShipmentResponse.contains("code\":\"1041\"")) {
			// if there are no multipiece shipments associated then just update false to save space.
			Tracking_Details[1] = "false";
		} else {
			Helper_Functions.PrintOut("Associated shipment: " + trackingNumber);
		}
		Details = Arrays.copyOf(Details, Details.length + 1);
		Details[Details.length - 1] = Tracking_Details;
		
		return Details;
	}

}
