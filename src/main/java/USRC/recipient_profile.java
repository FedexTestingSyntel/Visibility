package USRC;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONObject;

import API_Functions.General_API_Calls;
import Data_Structures.Address_Data;

public class recipient_profile {

	
	public static String RecipientProfile(String Cookie){
		//String url = "https://www" + LevelUrlReturn(Level) + "/userCal/user";    //delete this later, just a refernce for now on the url i used before
		USRC_Data USRC_Details = USRC_Data.USRC_Load();
		HttpPost httppost = new HttpPost(USRC_Details.GenericUSRCURL);

		JSONObject anonymousTransaction = new JSONObject()
		.put("anonymousTransaction", false)
		.put("clientId", "WCDO")
		.put("clientVersion", "3")
		.put("returnDetailedErrors", false)
		.put("returnLocalizedDateTime", false);
		
		JSONObject processingParameters = new JSONObject()
		.put("processingParameters", anonymousTransaction);
		
		JSONObject RecipientProfileRequest = new JSONObject()
		.put("RecipientProfileRequest", processingParameters);

		String json = RecipientProfileRequest.toString();

		httppost.addHeader("Content-Type", "application/x-www-form-urlencoded");
		httppost.addHeader("Accept", "application/json");
		httppost.addHeader("Content-Type", "application/json");
		httppost.addHeader("Cookie", Cookie);
		httppost.addHeader("X-locale", "en_US");
		httppost.addHeader("X-version", "1.0");

		List<NameValuePair> urlParameters = new ArrayList<NameValuePair>();
		urlParameters.add(new BasicNameValuePair("action", "getRecipientProfile"));
		urlParameters.add(new BasicNameValuePair("format", "json"));
		urlParameters.add(new BasicNameValuePair("version", "1"));
		urlParameters.add(new BasicNameValuePair("locale", "en_US"));
		urlParameters.add(new BasicNameValuePair("data", json));

		String Response;
		try {
			httppost.setEntity(new UrlEncodedFormEntity(urlParameters));
			Response = General_API_Calls.HTTPCall(httppost, json);
			return Response;
		} catch (Exception e) {
			return e.getMessage();
		}
	}
	
	public static Address_Data getFirstFDMAddress (String RecipientProfileResponse) {
		String contactAndAddress = API_Functions.General_API_Calls.ParseStringValue(RecipientProfileResponse, "contactAndAddress");
		
		Address_Data Address = new Address_Data();
		Address.Address_Line_1 = API_Functions.General_API_Calls.ParseStringValue(contactAndAddress, "streetLine");
		//since the response uses streetLine for both address line 1 and address line 2 rremove first.
		contactAndAddress = contactAndAddress.replaceFirst("\"streetLine\"", "");
		
		Address.Address_Line_2 = API_Functions.General_API_Calls.ParseStringValue(contactAndAddress, "streetLine");
		if (Address.Address_Line_2 == null) {
			// if the user does not have address line two replace with empty value.
			Address.Address_Line_2 = "";
		}
		
		Address.City = API_Functions.General_API_Calls.ParseStringValue(contactAndAddress, "city");
		Address.State_Code = API_Functions.General_API_Calls.ParseStringValue(contactAndAddress, "stateOrProvinceCode");
		Address.Phone_Number = API_Functions.General_API_Calls.ParseStringValue(contactAndAddress, "phoneNumber");
		Address.PostalCode = API_Functions.General_API_Calls.ParseStringValue(contactAndAddress, "postalCode");
		Address.Country_Code = API_Functions.General_API_Calls.ParseStringValue(contactAndAddress, "countryCode");
		Address.Residential  = API_Functions.General_API_Calls.ParseStringValue(contactAndAddress, "residential");
		
		return Address;
	}
	
	public static String[][] FDM_Access(String Details[][], String Cookies) {
		String RecipientProfileResponse = USRC.recipient_profile.RecipientProfile(Cookies);
		if (RecipientProfileResponse == null || !RecipientProfileResponse.contains("\"successful\":true")) {
			// call did not complete successfully
			return Details;
		}
		Details = Arrays.copyOf(Details, Details.length + 1);
		
		if (RecipientProfileResponse.contains("recipientProfileEnrollmentStatus\":\"ENROLLED")) {
			Details[Details.length - 1] = new String[] {"FDM_STATUS", RecipientProfileResponse};//store all of the FDM details
		}else {
			Details[Details.length - 1] = new String[] {"FDM_STATUS", "false"};//not yet enrolled for FDM
		}
		
/*		Details = Arrays.copyOf(Details, Details.length + 1);
		Details[Details.length - 1] = new String[] {"RECIPIENT_PROFILE", RecipientProfileResponse};*/
		
		return Details;
	}
	
}
