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

public class get_shipment_summary {

	public static String ShipmentSummaryRequest(String Cookie, String summaryType){
  		try{
  			TRKC_Data TRKC_Details = TRKC_Data.LoadVariables();
  			HttpPost httppost = new HttpPost(TRKC_Details.TrackingGenericURL);

  			JSONObject processingParameters = new JSONObject();
  				
  			//{"ShipmentSummaryRequest":{"appType":"WTRK","appDeviceType":"DESKTOP","uniqueKey":"","processingParameters":{},"summaryType":"CARRIER_CODE_OR_OPERATING_COMPANY"}}
  			JSONObject ShipmentSummaryRequest = new JSONObject()
  	  				.put("appType", "WTRK")
  	  				.put("appDeviceType", "DESKTOP")
  	  				.put("uniqueKey", "")
  	  				.put("processingParameters", processingParameters)
  	  				.put("summaryType", summaryType);
  			
  			JSONObject main = new JSONObject()
  	  				.put("ShipmentListRequest", ShipmentSummaryRequest);

  			String json = main.toString();
  				
  			httppost.addHeader("Content-Type", "application/x-www-form-urlencoded");
  			httppost.addHeader("Content-Type", "charset=UTF-8");
  			httppost.addHeader("Cookie", Cookie);
  			
  			List<NameValuePair> urlParameters = new ArrayList<NameValuePair>();
  			urlParameters.add(new BasicNameValuePair("action", "getShipmentSummary"));
  			urlParameters.add(new BasicNameValuePair("format", "json"));
  			urlParameters.add(new BasicNameValuePair("version", "1"));
  			urlParameters.add(new BasicNameValuePair("locale", "en_US"));
  			urlParameters.add(new BasicNameValuePair("data", json));

  			httppost.setEntity(new UrlEncodedFormEntity(urlParameters));
  			String Response = General_API_Calls.HTTPCall(httppost, json);	
			
  			/*{"ShipmentSummaryResponse":{"successful":true,"errorList":[
  			 * {"code":"0","message":"Request was successfully processed.","source":null,"rootCause":null}],"shipmentSummary":
  			 * 	{"summaryType":"CARRIER\u005fCODE\u005fOR\u005fOPERATING\u005fCOMPANY",
  			 * 	"summaryList":[{"value":"FDEG","count":"632","displayValue":"FedEx Ground"},
  			 * 	{"value":"FX","count":"75688","displayValue":"FedEx Express"},
  			 * 	{"value":"FDFR","count":"148","displayValue":"FedEx Freight"}]
  			 * },"alerts":null,"cxsalerts":null}}
  			 */
  			
  			return Response;
  		}catch (Exception e){
  			//e.printStackTrace();
  			return null;
  		}
  	}
	
	public static String[][] getATRK_ShipmentList(String Details[][], String Cookies, String summaryType) {
		//////////////////////// NOT FINISHED
		String ShipmentSummaryResponse = ShipmentSummaryRequest(Cookies, summaryType);
		/*
		 * CARRIER_CODE_OR_OPERATING_COMPANY
		 * REFERENCE
		 * ESTIMATED_DELIVERY_DATE
		 * SHIP_DATE
		 * STATUS_CODE
		 * SHIPPER_COMPANY_NAME
		 * SHIPPER_COUNTRY
		 * SHIPPER_COUNTRY
		 * SHIPPER_POSTAL
		 * SHIPPER_STATE
		 * RECIPIENT_COMPANY_NAME
		 * RECIPIENT_COUNTRY
		 * RECIPIENT_POSTAL
		 * RECIPIENT_STATE
		 */
		String Tracking_Details[][] = {
				{"REQUEST_RUN_DATE", "requestRunDate"},
				{"TOTAL_NUMBER_OF_SHIPMENTS", "totalNumberOfShipments"}};
		
		for (String[] NewElement: Tracking_Details) {
			Details = Arrays.copyOf(Details, Details.length + 1);
			NewElement[1] = API_Functions.General_API_Calls.ParseStringValue(ShipmentSummaryResponse, NewElement[1]);
			Details[Details.length - 1] = NewElement;
		}

		return Details;
	}
}
