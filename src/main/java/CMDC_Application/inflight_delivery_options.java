package CMDC_Application;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONObject;

import API_Functions.General_API_Calls;
import Data_Structures.Shipment_Data;
import INET_Application.eMASS_Scans;
import SupportClasses.Environment;
import SupportClasses.Helper_Functions;

public class inflight_delivery_options {

	public static String inflightDeliveryOptions(String TrackingNumber, String shipDate, String uniqueIdentifier){
		try{
			CMDC_Data CMDC_D = CMDC_Data.LoadVariables(Environment.getInstance().getLevel());
			HttpPost httppost = new HttpPost(CMDC_D.CMDCURL);
			
			JSONObject processingParameters = new JSONObject()
			        .put("anonymousTransaction", false)
			        .put("clientId", "WTRK")
			        .put("returnDetailedErrors", true)
			        .put("returnLocalizedDateTime", false);
			
			JSONObject uniqueTrackingNumber = new JSONObject()
			        .put("trackingNumber", TrackingNumber)
			        .put("shipDate", shipDate)
			        .put("uniqueIdentifier", uniqueIdentifier);
			
			JSONObject InflightDeliveryOptionsRequest = new JSONObject()
		        .put("processingParameters", processingParameters)
		        .put("uniqueTrackingNumber", uniqueTrackingNumber);
			
			JSONObject Main = new JSONObject()
			        .put("InflightDeliveryOptionsRequest", InflightDeliveryOptionsRequest);
			
  			String Request = Main.toString();

  			httppost.addHeader("Content-Type", "application/x-www-form-urlencoded");

  			List<NameValuePair> urlParameters = new ArrayList<NameValuePair>();
  			urlParameters.add(new BasicNameValuePair("action", "inflightDeliveryOptions"));
  			urlParameters.add(new BasicNameValuePair("format", "json"));
  			urlParameters.add(new BasicNameValuePair("version", "1"));
  			urlParameters.add(new BasicNameValuePair("locale", "en_US"));
  			urlParameters.add(new BasicNameValuePair("data", Request));
  			httppost.setEntity(new UrlEncodedFormEntity(urlParameters));
  			
			String Response = General_API_Calls.HTTPCall(httppost, Request);
		
			return Response;
		}catch (Exception e){
			e.printStackTrace();
			return e.toString();
		}
			/*
			Sample url:  https://wwwdrt.idev.fedex.com/commonDataCal/commondata  
			{"InflightDeliveryOptionsRequest":{"processingParameters":{"anonymousTransaction":false,"clientId":"WTRK","returnDetailedErrors":true,"returnLocalizedDateTime":false},"uniqueTrackingNumber":{"trackingNumber":"794946863510","shipDate":"2019-07-02T00:00:00-06:00","uniqueIdentifier":"2458667000~794946863510~FX"}}}
			{"InflightDeliveryOptionsResponse":{"successful":true,"transactionDetailList":[],"packageCount":"1","masterTrackingNumber":"794946863510","deliveryOptionsList":[{"deliveryOption":{"actionList":[],"status":"UNAVAILABLE","type":"RESCHEDULE"}},{"deliveryOption":{"actionList":[],"status":"UNAVAILABLE","type":"REROUTE"}},{"deliveryOption":{"actionList":[],"status":"UNAVAILABLE","type":"REDIRECT\u005fHOLD\u005fAT\u005fLOCATION"}},{"deliveryOption":{"actionList":[],"status":"UNAVAILABLE","type":"SIGNATURE\u005fRELEASE"}},{"deliveryOption":{"actionList":[{"action":"ADD"}],"status":"ENABLED","type":"DELIVERY\u005fINSTRUCTIONS"}},{"deliveryOption":{"actionList":[{"action":"ADD"}],"status":"ENABLED","type":"DELIVERY\u005fSUSPENSIONS"}}],"exclusionList":[{"exclusion":{"displayText":"This option is not available for this shipment.","key":"3027"}},{"exclusion":{"displayText":"This option is not available for this shipment at this time.  Please try again later.","key":"3112"}},{"exclusion":{"displayText":"This option is not available for this shipment.","key":"3115"}}]}}
			*/
	}
	
	public static String[][] getInflightDeliveryOptions(String Details[][], String trackingNumber, String Ship_Date, String trackingQualifier) throws Exception {
		String inflightDeliveryOptionsResponse = CMDC_Application.CMDC_Endpoints.inflightDeliveryOptions(trackingNumber, Ship_Date, trackingQualifier);
		
		if (Helper_Functions.isNullOrUndefined(inflightDeliveryOptionsResponse) || !inflightDeliveryOptionsResponse.contains("\"successful\":true")) {
			// call did not complete successfully
			return Details;
		}
		
/*		String RESCHEDULE = "false";
		String REROUTE = "false";
		String REDIRECT_HOLD_AT_LOCATION = "false";
		String SIGNATURE_RELEASE = "false";
		String DELIVERY_INSTRUCTIONS = "false";
		String DELIVERY_SUSPENSIONS = "false";
		
		if (inflightDeliveryOptionsResponse.contains("\"status\":\"ENABLED\",\"type\":\"RESCHEDULE\"")) {
			RESCHEDULE = "true";
		}
		
		if (inflightDeliveryOptionsResponse.contains("\"status\":\"ENABLED\",\"type\":\"REROUTE\"")) {
			REROUTE = "true";
		}
		
		if (inflightDeliveryOptionsResponse.contains("\"status\":\"ENABLED\",\"type\":\"REDIRECT_HOLD_AT_LOCATION\"")) {
			REDIRECT_HOLD_AT_LOCATION = "true";
		}
		
		if (inflightDeliveryOptionsResponse.contains("\"status\":\"ENABLED\",\"type\":\"SIGNATURE_RELEASE\"")) {
			SIGNATURE_RELEASE = "true";
		}
		
		if (inflightDeliveryOptionsResponse.contains("\"status\":\"ENABLED\",\"type\":\"DELIVERY_INSTRUCTIONS\"")) {
			DELIVERY_INSTRUCTIONS = "true";
		}
		
		if (inflightDeliveryOptionsResponse.contains("\"status\":\"ENABLED\",\"type\":\"DELIVERY_SUSPENSIONS\"")) {
			DELIVERY_SUSPENSIONS = "true";
		}*/
		
		String Tracking_Details[][] = {
				{"inflightDeliveryOptionsResponse", inflightDeliveryOptionsResponse},
				{"RESCHEDULE", "\"status\":\"ENABLED\",\"type\":\"RESCHEDULE\""},
				{"REROUTE", "\"status\":\"ENABLED\",\"type\":\"REROUTE\""},
				{"REDIRECT_HOLD_AT_LOCATION", "\"status\":\"ENABLED\",\"type\":\"REDIRECT_HOLD_AT_LOCATION\""},
				{"SIGNATURE_RELEASE", "\"status\":\"ENABLED\",\"type\":\"SIGNATURE_RELEASE\""},
				{"DELIVERY_INSTRUCTIONS", "\"status\":\"ENABLED\",\"type\":\"DELIVERY_INSTRUCTIONS\""},
				{"DELIVERY_SUSPENSIONS", "\"status\":\"ENABLED\",\"type\":\"DELIVERY_SUSPENSIONS\""}};
		
		// Add the new element to the details array
		for (String[] NewElement: Tracking_Details) {
			if (inflightDeliveryOptionsResponse.contains(NewElement[1])) {
				NewElement[1] = "true";
			}else {
				NewElement[1] = "false";
			}
			Details = Arrays.copyOf(Details, Details.length + 1);
			Details[Details.length - 1] = NewElement;
		}

		return Details;
	}
}
