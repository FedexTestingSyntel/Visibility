package CXS_Support;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.apache.http.Header;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.json.JSONObject;
import SupportClasses.Helper_Functions;

public class USRC_API_Endpoints {
	
	public static String LoginFullResponse(String URL, String UserID, String Password){
  		try{
  			HttpPost httppost = new HttpPost(URL);

  			JSONObject processingParameters = new JSONObject()
  				.put("anonymousTransaction", true)
  				.put("clientId", "WGRT")
  				.put("clientVersion", "3")
  				.put("returnDetailedErrors", false)
  				.put("returnLocalizedDateTime", false);
  				
  			JSONObject LogInRequest = new JSONObject()
  				.put("processingParameters", processingParameters)
  				.put("userName", UserID)
  				.put("password", Password);
  				
  			JSONObject main = new JSONObject()
  				.put("LogInRequest", LogInRequest);

  			String json = main.toString();
  				
  			httppost.addHeader("Content-Type", "application/x-www-form-urlencoded");

  			List<NameValuePair> urlParameters = new ArrayList<NameValuePair>();
  			urlParameters.add(new BasicNameValuePair("action", "LogIn"));
  			urlParameters.add(new BasicNameValuePair("format", "json"));
  			urlParameters.add(new BasicNameValuePair("version", "1"));
  			urlParameters.add(new BasicNameValuePair("locale", "en_US"));
  			urlParameters.add(new BasicNameValuePair("data", json));

  			httppost.setEntity(new UrlEncodedFormEntity(urlParameters));
  			Helper_Functions.PrintOut("Get cookie from USRC for " + UserID + "/" + Password, true);
  			
  			String Response = General_API_Calls.HTTPCall(httppost, json);	
			
  			return Response;
  		}catch (Exception e){
  			e.printStackTrace();
  			return null;
  		}
  	}
	
	public static String Logout(String URL){
  		try{
  			HttpPost httppost = new HttpPost(URL);

  			JSONObject processingParameters = new JSONObject()
  				.put("anonymousTransaction", false)
  				.put("clientId", "WGRT")
  				.put("clientVersion", "3")
  				.put("returnDetailedErrors", true)
  				.put("returnLocalizedDateTime", false);
  				
  			JSONObject LogInRequest = new JSONObject()
  				.put("processingParameters", processingParameters);
  				
  			JSONObject main = new JSONObject()
  				.put("LogOutRequest", LogInRequest);

  			String json = main.toString();
  				
  			httppost.addHeader("Content-Type", "application/x-www-form-urlencoded");

  			List<NameValuePair> urlParameters = new ArrayList<NameValuePair>();
  			urlParameters.add(new BasicNameValuePair("action", "logout"));
  			urlParameters.add(new BasicNameValuePair("format", "json"));
  			urlParameters.add(new BasicNameValuePair("version", "1"));
  			urlParameters.add(new BasicNameValuePair("locale", "en_US"));
  			urlParameters.add(new BasicNameValuePair("data", json));

  			httppost.setEntity(new UrlEncodedFormEntity(urlParameters));
  			String Response = General_API_Calls.HTTPCall(httppost, json);
  			return Response;
  		}catch (Exception e){
  			e.printStackTrace();
  			return null;
  		}
  	}
	
	 //Takes in the userid, password, and the level and returns the cookies generated.
  	//{fdx_=[cookie], uuid};  0 is the cookie, 1 is the uuid     ex [fdx_login=ssodrt-cos2.97fb.364ddc40, gw0g0h657p]
  	public static String[] Login(String URL, String UserID, String Password){
  		try{
  			HttpClient httpclient = HttpClients.createDefault();
  				
  			HttpPost httppost = new HttpPost(URL);

  			JSONObject processingParameters = new JSONObject()
  				.put("anonymousTransaction", true)
  				.put("clientId", "WCDO")
  				.put("clientVersion", "3")
  				.put("returnDetailedErrors", false)
  				.put("returnLocalizedDateTime", false);
  				
  			JSONObject LogInRequest = new JSONObject()
  				.put("processingParameters", processingParameters)
  				.put("userName", UserID)
  				.put("password", Password);
  				
  			JSONObject main = new JSONObject()
  				.put("LogInRequest", LogInRequest);

  			String json = main.toString();
  				
  			httppost.addHeader("Content-Type", "application/x-www-form-urlencoded");
  			httppost.addHeader("Accept", "application/json");
  			httppost.addHeader("Content-Type", "application/json");

  			List<NameValuePair> urlParameters = new ArrayList<NameValuePair>();
  			urlParameters.add(new BasicNameValuePair("action", "LogIn"));
  			urlParameters.add(new BasicNameValuePair("format", "json"));
  			urlParameters.add(new BasicNameValuePair("version", "1"));
  			urlParameters.add(new BasicNameValuePair("locale", "en_US"));
  			urlParameters.add(new BasicNameValuePair("data", json));

  			httppost.setEntity(new UrlEncodedFormEntity(urlParameters));
  			Helper_Functions.PrintOut("Get cookie from USRC for " + UserID + "/" + Password, true);
  			HttpResponse Response = httpclient.execute(httppost);
  			Helper_Functions.PrintOut("response: " + EntityUtils.toString(Response.getEntity()), true);
  			Header[] Headers = Response.getAllHeaders();
  			//takes apart the headers of the response and returns the fdx_login cookie if present
  			String fdx_login = null, fcl_uuid = null, full_cookies = "", RequestHeaders = "";
  			for (Header Header : Headers) {
  				//String Test = header.getName() + "    " + header.getValue();PrintOut(Test, false);// used in debugging or if want to see other cookie values
  				if (Header.getName().contentEquals("Set-Cookie") && Header.getValue().contains("fdx_login")) {
  					fdx_login = Header.toString().replace("Set-Cookie: ", "").replace("; domain=.fedex.com; path=/; secure", "");
  				}else if (Header.getName().contentEquals("Set-Cookie") && Header.getValue().contains("fcl_uuid")) {
  					fcl_uuid = Header.toString().replace("Set-Cookie: fcl_uuid=", "").replace("; domain=.fedex.com; path=/", "");
  				}
  				if (Header.getName().contentEquals("Set-Cookie")) {
  					full_cookies += Header.toString(); 
  				}
  				RequestHeaders += Header + "___";
  			}
  			
  			String MethodName = "USRCLogin";
  			Helper_Functions.PrintOut(MethodName + " URL: " + httppost.toString() + "\n    " + 
					  MethodName + " Headers: " + RequestHeaders + "\n    " +
					  MethodName + " Request: " + json + "\n    " + 
					  MethodName + " Response: " + Response, true); 
					  
  			if (fdx_login == null) {
  				return null;
  			}
  			String HeaderArray[] = new String[] {fdx_login, fcl_uuid, full_cookies};
  			Helper_Functions.PrintOut("Headers" + Arrays.toString(HeaderArray), true);
  			return HeaderArray;
  		}catch (Exception e){
  			e.printStackTrace();
  			return null;
  		}
  	}
	
	//c[] = 0 - First name, 1 - middle name, 2 - last name, 3 - phone number, 4 - email address, 5 - address line 1, 6 - address line 2, 7 - city, 8 - state code, 9 - zip code, 10 - country
	//User = the UserID that is being created
	public static String NewFCLUser(String CreateUserURL, String C[], String User, String Password){			
		HttpPost httppost = new HttpPost(CreateUserURL);
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
		} catch (Exception e) {
			e.printStackTrace();
			Response = e.getLocalizedMessage();
		}
		return Response;
	}
	
	//0 - First name, 1 - middle name, 2 - last name, 3 - phone number, 4 - email address, 5 - address line 1, 6 - address line 2, 7 - city, 8 - state code, 9 - zip code, 10 - country
	public static String Enrollment(String EnrollmentURL, String OAuth_Token, String ContactDetails[], String Cookie){
		String FirstName = ContactDetails[0], MiddleName = ContactDetails[1], LastName = ContactDetails[2], 
			PhoneNumber = ContactDetails[3], EmailAddress = ContactDetails[4], AddressLine1 = ContactDetails[5], 
			AddressLine2 = ContactDetails[6], City = ContactDetails[7], StateCode = ContactDetails[8], 
			Zip = ContactDetails[9], CountryCode = ContactDetails[10];
		
		HttpPost httppost = new HttpPost(EnrollmentURL);

		JSONObject contact = new JSONObject()
			.put("phoneNumber", PhoneNumber)
			.put("emailAddress", EmailAddress);
				
		JSONObject parsedPersonName = new JSONObject()
			.put("firstName", FirstName)
			.put("middleName", MiddleName)
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
		httppost.addHeader("Authorization", "Bearer " + OAuth_Token);
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

	public static String CreatePin(String CreatePinURL, String OAuth_Token, String Cookie, String ShareID, String PinType){
		try{
			HttpClient httpclient = HttpClients.createDefault();
			HttpPost httppost = new HttpPost(CreatePinURL);

			JSONObject main = new JSONObject()
			.put("addressVerificationId", ShareID)
			.put("pinType", PinType);

			String json = main.toString();
			
			httppost.addHeader("Content-Type", "application/json");
			httppost.addHeader("Authorization", "Bearer " + OAuth_Token);
			httppost.addHeader("X-clientid", "WERL");
			httppost.addHeader("X-locale", "en_US");
			httppost.addHeader("X-version", "1.0");
			httppost.addHeader("Cookie", Cookie);
			
			StringEntity params = new StringEntity(json.toString());
			httppost.setEntity(params);
			HttpResponse response = httpclient.execute(httppost);
			String result = EntityUtils.toString(response.getEntity());

			//Example, if the response does not contain success check that the correct cookies are being sent as a broken response may only have transaction id.
			//{"transactionId":"ae1816b4-0738-4849-bd2f-fca1df32837b","output":{"successful":true}} 
			return result;
		}catch (Exception e){
			return e.toString();
		}
	}
	
	/*(The below is current as of 05/22/18)
	 * Takes in the ShareID, Pin and Pin type and verify that the correct pin is entered.
	 * ShareID: The unique id for a given address
	 * Pin: The 6 digit number that is generated for an enrollment attempt. 
	 * PinType: Takes in the Org such as "SMS" or "POSTAL".
	 * 
	 * Returns the response from the given call.
	 */
	public static String VerifyPin(String VerifyPinURL, String OAuth_Token, String Cookie, String ShareID, String Pin, String PinType){
		try{
			HttpPost httppost = new HttpPost(VerifyPinURL);

			JSONObject main = new JSONObject()
			.put("addressVerificationId", ShareID)
			.put("pin", Pin)
			.put("pinType", PinType);

			String json = main.toString();
			
			httppost.addHeader("Content-Type", "application/json");
			httppost.addHeader("Authorization", "Bearer " + OAuth_Token);
			httppost.addHeader("X-clientid", "WERL");
			httppost.addHeader("X-locale", "en_US");
			httppost.addHeader("X-version", "1.0");
			httppost.addHeader("Cookie", Cookie);
			
			StringEntity params = new StringEntity(json.toString());
			httppost.setEntity(params);

			String Response = General_API_Calls.HTTPCall(httppost, json);	

			//Example
			//{"transactionId":"d13727b4-e528-4e50-9986-a51254f668b1","output":{"responseMessage":"Success"}} 
			
			return Response;
		}catch (Exception e){
			return e.toString();
		}
	}

	//need to test
	public String getPendingAddress(String PendingAddressURL, String OAuth_Token, String Cookie){
		HttpGet httpGet = new HttpGet(PendingAddressURL);
		
		httpGet.addHeader("Content-Type", "application/json");
		httpGet.addHeader("Authorization", "Bearer " + OAuth_Token);
		httpGet.addHeader("X-clientid", "WERL");
		httpGet.addHeader("X-locale", "en_US");
		httpGet.addHeader("X-version", "1.0");
		httpGet.addHeader("Cookie", Cookie);
		
		String Response;
		try {
			Response = General_API_Calls.HTTPCall(httpGet, "");
			return Response;
		} catch (Exception e) {
			return e.getMessage();
		}	
		
		/*  not sure what the below does, need to debug
		HttpResponse response = httpclient.execute(httpGet);
		StatusLine statusLine = response.getStatusLine();
		int statusCode = statusLine.getStatusCode();
		if (statusCode == HttpStatus.SC_OK) {
			body.append(statusLine + "\n");
			HttpEntity e = response.getEntity();
			String entity = EntityUtils.toString(e);
			body.append(entity);
		}else {
			body.append(statusLine + "\n");
		}*/
	}
	
	//Will take in the URL and the cookies and then return the users contact information. 
	public static String ViewUserProfileWIDM(String ViewUserProfileWIDMURL, String Cookie){
		HttpGet httpGet = new HttpGet(ViewUserProfileWIDMURL);
		httpGet.addHeader("Cookie", Cookie);
		
		String Response;
		try {
			Response = General_API_Calls.HTTPCall(httpGet, "");
			
			return Response;
			//Example: {"output":{"userProfile":{"profileLocked":false,"loginInformation":{"userId":"UserIDHere","secretQuestion":{"code":"SP2Q1","text":"What is your mother's first name?         "}},"userProfileAddress":{"contact":{"personName":{"firstName":"FthreeCreatewjjvyzm","middleName":"M","lastName":"Lqjdymaa"},"companyName":"","phoneNumber":"9011111111","faxNumber":"","emailAddress":"accept.osv@fedex.com"},"contactAncillaryDetail":{"phoneNumberDetails":[{"type":"HOME","number":{"countryCode":"","localNumber":"9011111111"},"permissions":{}},{"type":"MOBILE","number":{"countryCode":"","localNumber":""},"permissions":{}},{"type":"FAX","number":{"countryCode":"","localNumber":""},"permissions":{}}]},"address":{"streetLines":["10 FED EX PKWY",""],"city":"Collierville","stateOrProvinceCode":"TN","postalCode":"38017","countryCode":"US","residential":false}}}},"successful":true}
		} catch (Exception e) {
			return e.getMessage();
		}	
	}
	
	public static String[][] Parse_ViewUserProfileWIDM(String Response, String ParseCheck[][]) {
		String Parse[][] = {{"SECRET_QUESTION_DESC", "secretQuestion\":{\"code\":\"", "\",\""},
							{"FIRST_NM", "\"firstName\":\"", "\",\""}, 
							{"LAST_NM", "\"lastName\":\"", "\"},\""}, 
							{"STREET_DESC", "\"streetLines\":[\"", "\",\""}, 
							{"CITY_NM", "\"city\":\"", "\",\""}, 
							{"STATE_CD", "\"stateOrProvinceCode\":\"", "\",\""}, 
							{"POSTAL_CD", "\"postalCode\":\"", "\",\""}, 
							{"COUNTRY_CD", "\",\"countryCode\":\"", "\",\""}};
		
		for(int i = 0; i < ParseCheck.length; i++){
			for (int j = 0; j < Parse.length; j++) {
				if (Parse[j][0].contentEquals(ParseCheck[i][0])) {
					ParseCheck[i][1] = ParseRequest(Response, Parse[j][1], Parse[j][2]);
					if (ParseCheck[i][0].contentEquals("SECRET_QUESTION_DESC") && ParseCheck[i][1].contentEquals("SP2Q1") && Parse[j][0].contentEquals("SECRET_QUESTION_DESC")) {
						//this is a guess based on other automated tests. By default the other scripts put mom as the secret answer.
						for (int k = 0; k < ParseCheck.length; k++) {
							if (ParseCheck[k][0].contentEquals("SECRET_ANSWER_DESC") && ParseCheck[k][1].contentEquals("")) {
								ParseCheck[k][1] = "mom";
							}
						}
					}
				}
			}
		}
		return ParseCheck;
	}
	
	public static String ParseRequest(String s, String Start, String End) {
		if(s.contains(Start) && s.contains(End)) {
			String buffer = s.substring(s.indexOf(Start) + Start.length(), s.length());
			return buffer.substring(0, buffer.indexOf(End));
		}
		return "";
	}
	
	public static String RecipientProfile(String RecipientProfileURL, String Cookie){
		//String url = "https://www" + LevelUrlReturn(Level) + "/userCal/user";    //delete this later, just a refernce for now on the url i used before
		HttpPost httppost = new HttpPost(RecipientProfileURL);

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

	public static String AccountRetrievalRequest(String URL, String Cookie){
		//This takes the generic USRC url    EX: https://wwwdrt.idev.fedex.com/userCal/user
  		try{
  			HttpPost httppost = new HttpPost(URL);

  			JSONObject processingParameters = new JSONObject()
  	  				.put("anonymousTransaction", false)
  	  				.put("clientId", "WFCL")
  	  				.put("clientVersion", "1")
  	  				.put("returnDetailedErrors", true)
  	  				.put("returnLocalizedDateTime", false);
  	  				
  	  		JSONObject AccountRetrievalRequest = new JSONObject()
  	  				.put("processingParameters", processingParameters);
  	  				
  	  		JSONObject main = new JSONObject()
  	  				.put("AccountRetrievalRequest", AccountRetrievalRequest);

  			String json = main.toString();
  			
  			httppost.addHeader("Content-Type", "application/x-www-form-urlencoded");
  			httppost.addHeader("Accept", "text/plain");
  			httppost.addHeader("Cookie", Cookie);
  			
  			List<NameValuePair> urlParameters = new ArrayList<NameValuePair>();
  			urlParameters.add(new BasicNameValuePair("action", "getAllAccounts"));
  			urlParameters.add(new BasicNameValuePair("format", "json"));
  			urlParameters.add(new BasicNameValuePair("version", "1"));
  			urlParameters.add(new BasicNameValuePair("locale", "en_us"));
  			urlParameters.add(new BasicNameValuePair("data", json));

  			httppost.setEntity(new UrlEncodedFormEntity(urlParameters));

  			String Response = General_API_Calls.HTTPCall(httppost, json);	
			
  			return Response;
  		}catch (Exception e){
  			e.printStackTrace();
  			return null;
  		}
  	}
	
	public static String Parse_AccountRetrievalRequest_AccountNumber(String Response) {
		String Account_Start = "\"value\":\"", Account_End = "\"},\"displayName\"";
		
		String AccountNumbers = "";
		while (Response.contains("accountNumber")) {
			String Account = ParseRequest(Response, Account_Start, Account_End);
			
			if (Account.contentEquals("")) {
				String NickName_Start = "{\"accountNickname\":\"", NickName_End = "\",\"accountNumber\":{";
				Account = ParseRequest(Response, NickName_Start, NickName_End);
			}
			if (AccountNumbers.contentEquals("")) {
				AccountNumbers = Account;
			}else {
				AccountNumbers += "," + Account;
			}
			//remove the section that was just checked.
			Response = Response.substring(Response.indexOf(Account_End) + Account_End.length(), Response.length());
		}
		
		if (!AccountNumbers.contentEquals("")) {  //if no valid values were found then return null.
			return AccountNumbers;
		}
		return null;
	}
}
