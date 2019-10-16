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
import SupportClasses.Environment;

public class tracking_profile {

	public static String TrackingProfileRequest(String Cookie){
  		try{
  			if (Environment.getInstance().getLevel().contentEquals("6")) {
  				// The tracking call will fail on L6
  				return null;
  			}
  			TRKC_Data TRKC_Details = TRKC_Data.LoadVariables();
  			HttpPost httppost = new HttpPost(TRKC_Details.TrackingGenericURL);

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
  			urlParameters.add(new BasicNameValuePair("data", json));
  			urlParameters.add(new BasicNameValuePair("action", "getTrackingProfile"));
  			urlParameters.add(new BasicNameValuePair("format", "json"));
  			urlParameters.add(new BasicNameValuePair("version", "1"));
  			urlParameters.add(new BasicNameValuePair("locale", "en_US"));

  			httppost.setEntity(new UrlEncodedFormEntity(urlParameters));
  			String Response = General_API_Calls.HTTPCall(httppost, json);	
			
  			return Response;
  			//{"TrackingProfileResponse":{"successful":true,"uniqueKey":"4655735F427D7D64134D791355717A765C5B484F444B565D44547E","userLoginName":"","userFirstName":"","userContactName":"","userEmail":"","userLastActivityDate":"2019\u002d09\u002d18T23\u003a33\u003a42.000\u002b00\u003a00","estimatedShipmentTotal":"0","needsToAcceptTerms":"1","isFullInSightUser":"0","isAdvanceNotice":"0","isLargeUserType":"0","isExtraordinaryUserType":"0","isPackageRevoked":"0","isSVIDChanged":"","isUserLockedOut":"0","packageRevokedKey":"","userPackageNameKey":"My InSight\u005f2019\u002d09\u002d18T23\u003a33\u003a42.000\u002b00\u003a00","userKey":"4655735F427D7D64","hasSingletonUpdatesInPast60Days":"0","hasMatchedIn23Days":"0","isDvxCust":"0","isPriorityAlert":"0","tzcode":"","igcenabled":"0","alerts":null,"cxsalerts":null}}
  		}catch (Exception e){
  			//e.printStackTrace();
  			return null;
  		}
  	}
	
	public static String[][] getATRK_Profile(String Details[][], String Cookies) {
		String TrackingProfileResponse = TrackingProfileRequest(Cookies);
		if (TrackingProfileResponse == null || !TrackingProfileResponse.contains("\"successful\":true")) {
			// call did not complete successfully
			return Details;
		}
		
		String NEEDS_TO_ACCEPT_TERMS = "NEEDS_TO_ACCEPT_TERMS";
		boolean checkTotalShipmentCount = false;
		
		String Tracking_Details[][] = {
				{"IS_LARGE_USER_TYPE", "isLargeUserType"},
				{NEEDS_TO_ACCEPT_TERMS, "needsToAcceptTerms"}};
		
		for (String[] NewElement: Tracking_Details) {
			Details = Arrays.copyOf(Details, Details.length + 1);
			NewElement[1] = API_Functions.General_API_Calls.ParseStringValue(TrackingProfileResponse, NewElement[1]);
			if (NewElement[1] != null) {
				if (NewElement[1].contentEquals("0")) {
					NewElement[1] = "false";
					if (NewElement[0].contentEquals(NEEDS_TO_ACCEPT_TERMS)) {
						checkTotalShipmentCount = true;
					}
				} else if (NewElement[1].contentEquals("1")){
					NewElement[1] = "true";
				}
			}
			Details[Details.length - 1] = NewElement;
		}
		
		if (checkTotalShipmentCount) {
			Details = Arrays.copyOf(Details, Details.length + 1);
			Details[Details.length - 1] = new String[] {"TRACKING_PROFILE", TrackingProfileResponse};
			Details = TRKC.shipment_list_request.getATRK_ShipmentList(Details, Cookies);
		}
		return Details;
	}
}
