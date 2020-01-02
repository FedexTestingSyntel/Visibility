package REGC;

import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.json.JSONObject;

import API_Functions.General_API_Calls;
import Data_Structures.Account_Data;
import Data_Structures.User_Data;
import SupportClasses.Helper_Functions;
import USRC.USRC_Data;

public class create_new_user {
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
			.put("phoneNumber", User_Info.PHONE_NUMBER)
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

	//c[] = 0 - First name, 1 - middle name, 2 - last name, 3 - phone number, 4 - email address, 5 - address line 1, 6 - address line 2, 7 - city, 8 - state code, 9 - zip code, 10 - country
	//User = the UserID that is being created
	public static String NewFCLUser(String C[], String User, String Password){
		REGC_Data REGC_Details = REGC_Data.LoadVariables();
		HttpPost httppost = new HttpPost(REGC_Details.CreateNewUserURL);
		
		// Will default to sending correct flag when attempting email as user id. --6/28/19
		boolean emailAsUserId = false;
		if (User.contains("@")) {
			emailAsUserId = true;
		}
		
		JSONObject main = new JSONObject()
			.put("firstName", C[0])
			.put("middleName", C[1])
			.put("lastName", C[2])
			.put("deviceId", "optional")
			.put("addressLine1", C[5])
			.put("addressLine2", C[6])
			.put("city", C[7])
			.put("stateCode", C[8])
			.put("zipCode", C[9])
			.put("countryCode", C[10])
			.put("emailAddress", C[4])
			.put("emailAsUserId", emailAsUserId)
			.put("phoneNumber", C[3])
			.put("userId", User)
			.put("password", Password)
			.put("reenterPassword", Password)
			.put("secretQuestion", "SP2Q1")
			.put("secretAnswer", "mom");

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
			
			if (Response.contains("successful\":true")) {
				//Write User to file for later reference
				Helper_Functions.WriteUserToExcel(User , Password);
			}
		} catch (Exception e) {
			e.printStackTrace();
			Response = e.getLocalizedMessage();
		}
		return Response;
	}
	
	public static String NewFCLUser(User_Data User_Info){
		USRC_Data USRC_Details = USRC_Data.USRC_Load();
		HttpPost httppost = new HttpPost(USRC_Details.REGCCreateNewUserURL);
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
			.put("phoneNumber", User_Info.PHONE_NUMBER)
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
	
	public static String NewFCLUser(Account_Data Account_Info){		
		USRC_Data USRC_Details = USRC_Data.USRC_Load();
		HttpPost httppost = new HttpPost(USRC_Details.REGCCreateNewUserURL);
		JSONObject main = new JSONObject()
			.put("firstName", Account_Info.FirstName)
			.put("middleName", Account_Info.MiddleName)
			.put("lastName", Account_Info.LastName)
			.put("deviceId", "optional")
			.put("addressLine1", Account_Info.Billing_Address_Info.Address_Line_1)
			.put("addressLine2", Account_Info.Billing_Address_Info.Address_Line_2)
			.put("city", Account_Info.Billing_Address_Info.City)
			.put("stateCode", Account_Info.Billing_Address_Info.State_Code)
			.put("zipCode", Account_Info.Billing_Address_Info.PostalCode)           
			.put("countryCode", Account_Info.Billing_Address_Info.Country_Code)
			.put("emailAddress", Account_Info.Email)
			.put("phoneNumber", Account_Info.Billing_Address_Info.Phone_Number)
			.put("userId", Account_Info.User_Info.USER_ID)
			.put("password", Account_Info.User_Info.PASSWORD)
			.put("reenterPassword", Account_Info.User_Info.PASSWORD)
			.put("secretQuestion", Account_Info.User_Info.SECRET_QUESTION_DESC)
			.put("secretAnswer", Account_Info.User_Info.SECRET_ANSWER_DESC);

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
