package USRC;

import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.json.JSONObject;

import API_Functions.General_API_Calls;

public class fdm_enrollment {
	
	//0 - First name, 1 - middle name, 2 - last name, 3 - phone number, 4 - email address, 5 - address line 1, 6 - address line 2, 7 - city, 8 - state code, 9 - zip code, 10 - country
	public static String Enrollment(String ContactDetails[], String Cookie){
		USRC_Data USRC_Details = USRC_Data.USRC_Load();
		String FirstName = ContactDetails[0], LastName = ContactDetails[2], 
			PhoneNumber = ContactDetails[3], EmailAddress = ContactDetails[4], AddressLine1 = ContactDetails[5], 
			AddressLine2 = ContactDetails[6], City = ContactDetails[7], StateCode = ContactDetails[8], 
			Zip = ContactDetails[9], CountryCode = ContactDetails[10];
		// MiddleName = ContactDetails[1]
		
		HttpPost httppost = new HttpPost(USRC_Details.EnrollmentURL);

		JSONObject contact = new JSONObject()
			.put("phoneNumber", PhoneNumber)
			.put("emailAddress", EmailAddress);
				
		JSONObject parsedPersonName = new JSONObject()
			.put("firstName", FirstName)
			// .put("middleName", MiddleName)  removed on 8/20
			.put("lastName", LastName);
		
		contact.put("parsedPersonName", parsedPersonName);
				
		String streetLines[] = {AddressLine1, AddressLine2};
				
		JSONObject address = new JSONObject()
			.put("streetLines", streetLines)
			.put("city", City)
			.put("stateOrProvinceCode", StateCode)
			.put("postalCode", Zip)
			.put("countryCode", CountryCode);
				
		JSONObject main = new JSONObject()
			.put("address", address)
			.put("contact", contact);
				
		String json = main.toString();
		
		httppost.addHeader("Content-Type", "application/json");
		httppost.addHeader("Authorization", "Bearer " + USRC_Details.getOAuthToken());
		httppost.addHeader("X-clientid", "WERL");
		httppost.addHeader("X-locale", "en_US");
		httppost.addHeader("X-version", "1.0");
		httppost.addHeader("Cookie", Cookie);
				
		StringEntity params;
		String Response = null;
		try {
			params = new StringEntity(json.toString());
			httppost.setEntity(params);
			Response = General_API_Calls.HTTPCall(httppost, json);
		} catch (Exception e) {
			e.printStackTrace();
		}
				
		return Response;
	}
}
