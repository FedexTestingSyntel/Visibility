package REGC_Application;

import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.json.JSONObject;

import Data_Structures.User_Data;
import SupportClasses.General_API_Calls;

public class REGC_Endpoints {
	public static String NewFCLUser(String CreateUserURL, User_Data User_Info){			
		HttpPost httppost = new HttpPost(CreateUserURL);
		JSONObject main = new JSONObject()
			.put("firstName", User_Info.FIRST_NM)
			.put("middleName", User_Info.MIDDLE_NM)
			.put("lastName", User_Info.LAST_NM)
			.put("deviceId", "optional")
			.put("addressLine1", User_Info.Address_Info.Address_Line_1)
			.put("addressLine2", User_Info.Address_Info.Address_Line_2)
			.put("city", User_Info.Address_Info.City)
			.put("stateCode", User_Info.Address_Info.State_Code)
			.put("zipCode", User_Info.Address_Info.PostalCode)
			.put("countryCode", User_Info.Address_Info.Country_Code)
			.put("emailAddress", User_Info.EMAIL_ADDRESS)
			.put("phoneNumber", User_Info.PHONE)
			.put("userId", User_Info.USER_ID)
			.put("password", User_Info.PASSWORD)
			.put("reenterPassword", User_Info.PASSWORD)
			.put("secretQuestion", User_Info.SECRET_QUESTION_DESC)
			.put("secretAnswer", User_Info.SECRET_ANSWER_DESC);

		String json = main.toString();
				
		httppost.addHeader("Content-Type", "application/json");
		httppost.addHeader("Accept", "application/json");
		httppost.addHeader("X-clientid", "WERL");
		httppost.addHeader("X-locale", "en_US");
		httppost.addHeader("X-version", "1.0");
				
		StringEntity params;
		String Response;
		try {
			params = new StringEntity(json.toString());
			httppost.setEntity(params);
			//Note that an email will be triggered after registration
			Response = General_API_Calls.HTTPCall(httppost, json);	
		} catch (Exception e) {
			e.printStackTrace();
			Response = e.getLocalizedMessage();
		}
		return Response;
	}
}
