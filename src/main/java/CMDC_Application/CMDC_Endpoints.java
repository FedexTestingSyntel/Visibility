package CMDC_Application;

import java.util.ArrayList;
import java.util.List;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONObject;

import API_Functions.General_API_Calls;
import SupportClasses.Environment;

public class CMDC_Endpoints {

	public static String CountryDetailRequest(String CountryCode){
		try{
			CMDC_Data CMDC_D = CMDC_Data.LoadVariables(Environment.getInstance().getLevel());
			HttpPost httppost = new HttpPost(CMDC_D.CMDCURL);
			
			JSONObject processingParameters = new JSONObject()
			        .put("anonymousTransaction", true)
			        .put("clientId", "WERL")
			        .put("clientVersion", "1")
			        .put("returnDetailedErrors", true)
			        .put("returnLocalizedDateTime", false);
			
			JSONObject countryDetailControlParameters = new JSONObject()
			        .put("includeStateList", true);
			
			JSONObject CountryDetailRequest = new JSONObject()
		        .put("processingParameters", processingParameters)
		        .put("countryDetailControlParameters", countryDetailControlParameters)
		        .put("countryCode", CountryCode.toUpperCase());
			
			httppost.addHeader("Content-Type", "application/json");

			JSONObject Main = new JSONObject()
			        .put("CountryDetailRequest", CountryDetailRequest);
			
  			String Request = Main.toString();

  			List<NameValuePair> urlParameters = new ArrayList<NameValuePair>();
  			urlParameters.add(new BasicNameValuePair("action", "countryDetail"));
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
			Sample url:    
			.....
			*/
	}
	
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
}