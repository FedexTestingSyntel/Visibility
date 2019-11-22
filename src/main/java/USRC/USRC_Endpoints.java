package USRC;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
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
import API_Functions.General_API_Calls;
import Data_Structures.User_Data;
import SupportClasses.Helper_Functions;

public class USRC_Endpoints {
	
	
	///////////////////////////////not finished
	public static String UpdateUserContactInformationWIDM(String URL, User_Data User_Info, String Cookie){
  		try{;
  			HttpPost httppost = new HttpPost(URL);

  			String phoneNumberDetails[] = new String[] {"{\"type\":\"HOME\",\"number\":{\"countryCode\":\"1\",\"localNumber\":\"9011111111\"},\"permissions\":{}}, {\"type\":\"MOBILE\",\"number\":{\"countryCode\":\"\",\"localNumber\":\"\"},\"permissions\":{}}, {\"type\":\"FAX\",\"number\":{\"countryCode\":\"\",\"localNumber\":\"\"},\"permissions\":{}}"};
  			JSONObject contactAncillaryDetail = new JSONObject()
  				.put("phoneNumberDetails", phoneNumberDetails);
  			
  			
  			JSONObject personName = new JSONObject()
  					.put("firstName", User_Info.FIRST_NM)
  					.put("middleName", User_Info.MIDDLE_NM)
  					.put("lastName", User_Info.LAST_NM);
  			
  			String CompanyName = "Company"; //filler value
  			JSONObject contact = new JSONObject()
  					.put("personName", personName)
  					.put("companyName", CompanyName)
  					.put("phoneNumber", User_Info.PHONE_NUMBER)
  					.put("emailAddress", User_Info.EMAIL_ADDRESS)
  					.put("faxNumber", ""); // assumed no faxNumber
  			
  			String StreetLines[] = new String[] {User_Info.Address_Info.Address_Line_1, User_Info.Address_Info.Address_Line_2};
  			if (User_Info.Address_Info.Address_Line_2.isEmpty()){
  				StreetLines = new String[] {User_Info.Address_Info.Address_Line_1};
  			}
  			
  			JSONObject address = new JSONObject()
  				.put("streetLines", StreetLines)
  				.put("city", User_Info.Address_Info.City)
  				.put("stateOrProvinceCode", User_Info.Address_Info.State_Code)
  				.put("postalCode", User_Info.Address_Info.PostalCode)
  				.put("countryCode", User_Info.Address_Info.Country_Code);
  				
  			JSONObject parsedContactAddress = new JSONObject()
  				.put("contact", contact)
  	  			.put("contactAncillaryDetail", contactAncillaryDetail)
  	  			.put("address", address);
  			
  			JSONObject input = new JSONObject()
  				.put("deviceID", "Filler")
  	  	  		.put("parsedContactAddress", parsedContactAddress);
  			
  			JSONObject main = new JSONObject()
  	  				.put("input", input);
  			
  			String json = main.toString();
  			//Need to fix the below
  			json = json.replaceAll("\\\\", "");
  			json = json.replace("\":[\"{\"type", "\":[{\"type");
  			json = json.replace("permissions\":{}}\"]", "permissions\":{}}]");
  				
  			httppost.addHeader("Content-Type", "application/x-www-form-urlencoded");
  			httppost.addHeader("Cookie", Cookie);
  			
  			List<NameValuePair> urlParameters = new ArrayList<NameValuePair>();
  			urlParameters.add(new BasicNameValuePair("action", "LogIn"));
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
	
	public static String CreatePin(String Cookie, String ShareID, String PinType){
		try{
			USRC_Data USRC_Details = USRC_Data.USRC_Load();
			HttpClient httpclient = HttpClients.createDefault();
			HttpPost httppost = new HttpPost(USRC_Details.CreatePinURL);

			JSONObject main = new JSONObject()
			.put("addressVerificationId", ShareID)
			.put("pinType", PinType);

			String json = main.toString();
			
			httppost.addHeader("Content-Type", "application/json");
			httppost.addHeader("Authorization", "Bearer " + USRC_Details.getOAuthToken());
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
	public static String VerifyPin(String Cookie, String ShareID, String Pin, String PinType){
		try{
			USRC_Data USRC_Details = USRC_Data.USRC_Load();
			HttpPost httppost = new HttpPost(USRC_Details.VerifyPinURL);

			JSONObject main = new JSONObject()
			.put("addressVerificationId", ShareID)
			.put("pin", Pin)
			.put("pinType", PinType);

			String json = main.toString();
			
			httppost.addHeader("Content-Type", "application/json");
			httppost.addHeader("Authorization", "Bearer " + USRC_Details.getOAuthToken());
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
		USRC_Data USRC_Details = USRC_Data.USRC_Load();
		HttpGet httpGet = new HttpGet(USRC_Details.PendingAddressURL);
		
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
	public static String ViewUserProfileWIDM(String Cookie){
		USRC_Data USRC_Details = USRC_Data.USRC_Load();
		HttpGet httpGet = new HttpGet(USRC_Details.ViewUserProfileWIDMURL);
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
	
	public static String[][] getContact_Details(String Details[][], String Cookies) {
		String ContactDetailsResponse = USRC_Endpoints.ViewUserProfileWIDM(Cookies);
		if (ContactDetailsResponse == null || !ContactDetailsResponse.contains("\"successful\":true")) {
			// call did not complete successfully
			return Details;
		}
		
		String Contact_Details[][] = {{"SECRET_QUESTION_DESC", "code"}, // 0, hard coded below to set answer
				{"SECRET_ANSWER_DESC", ""}, // 1, hard coded below to set answer
				{"FIRST_NM", "firstName"},
				{"MIDDLE_NM", "middleName"},
				{"LAST_NM", "lastName"}, 
				{"PHONE_NUMBER", "phoneNumber"},
				{"EMAIL_ADDRESS", "emailAddress"}};
		
		for (String[] NewElement: Contact_Details) {
			Details = Arrays.copyOf(Details, Details.length + 1);
			NewElement[1] = API_Functions.General_API_Calls.ParseStringValue(ContactDetailsResponse, NewElement[1]);
			Details[Details.length - 1] = NewElement;
		}
		if (Contact_Details[0][1].contains("SP2Q1")) {
			//quess based on the generic answers, may not be the correct secret answer
			Contact_Details[1][1] = "mom";
		}
		
		String AddressDetailsResponse = API_Functions.General_API_Calls.ParseStringValue(ContactDetailsResponse, "address");
		// just to separate the address line 1 and address line 2.
		String AddressLine = API_Functions.General_API_Calls.ParseStringValue(AddressDetailsResponse, "streetLines");
		if (AddressLine != null) {
			Details = Arrays.copyOf(Details, Details.length + 2);
			String AddressLineOne = AddressLine.substring(2, AddressLine.indexOf("\",\""));
			Details[Details.length - 2] = new String[] {"STREET_DESC", AddressLineOne};
			String AddressLineTwo = AddressLine.substring(AddressLine.indexOf("\",\"") + 3, AddressLine.indexOf("\"]"));
			Details[Details.length - 1] = new String[] {"STREET_DESC_TWO", AddressLineTwo};
		}

		String Address_Details[][] = { 
				{"CITY_NM", "city"}, 
				{"STATE_CD", "stateOrProvinceCode"}, 
				{"POSTAL_CD", "postalCode"}, 
				{"COUNTRY_CD", "countryCode"},
				{"RESIDENTIAL", "residential"}};
		for (String[] NewElement: Address_Details) {
			Details = Arrays.copyOf(Details, Details.length + 1);
			NewElement[1] = API_Functions.General_API_Calls.ParseStringValue(AddressDetailsResponse, NewElement[1]);
			Details[Details.length - 1] = NewElement;
		}
		
		Details = Arrays.copyOf(Details, Details.length + 1);
		Details[Details.length - 1] = new String[] {"USER_PROFILE", ContactDetailsResponse};
		return Details;
	}
	
	
	//// REMOVE LATER
	public static String[][] Parse_ViewUserProfileWIDM(String Response, String ParseCheck[][]) {
		String Parse[][] = {{"SECRET_QUESTION_DESC", "secretQuestion\":{\"code\":\"", "\",\""},
							{"FIRST_NM", "\"firstName\":\"", "\",\""}, 
							{"LAST_NM", "\"lastName\":\"", "\"},\""}, 
							{"STREET_DESC", "\"streetLines\":[\"", "\",\""}, 
							{"CITY_NM", "\"city\":\"", "\",\""}, 
							{"STATE_CD", "\"stateOrProvinceCode\":\"", "\",\""}, 
							{"POSTAL_CD", "\"postalCode\":\"", "\",\""}, 
							{"COUNTRY_CD", "\",\"countryCode\":\"", "\",\""}, 
							{"EMAIL_ADDRESS", "emailAddress\":\"", "\"},\""}};
		
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
	
	// Remove this later should be using 
	public static String ParseRequest(String s, String Start, String End) {
		if(s.contains(Start) && s.contains(End)) {
			String buffer = s.substring(s.indexOf(Start) + Start.length(), s.length());
			return buffer.substring(0, buffer.indexOf(End));
		}
		return "";
	}

	public static String AccountRetrievalRequest(String Cookie){
		//This takes the generic USRC url    EX: https://wwwdrt.idev.fedex.com/userCal/user
  		try{
  			USRC_Data USRC_Details = USRC_Data.USRC_Load();
  			HttpPost httppost = new HttpPost(USRC_Details.GenericUSRCURL);

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
			/*
			{"AccountRetrievalResponse":{"customerAccountList":[{"customerAccountInfo":{"account":{"accountIdentifier":{"accountNickname":"My Account - 430","accountNumber":{"key":"9a4457a79152d55132661972048cfe9b","value":"<<Account number here>>"},"displayName":"My Account - 430-430"}},"activeAppRoleInfoList":[{"appRoleInfo":{"appName":"fclcro","roleCode":"ADMIN"}},{"appRoleInfo":{"appName":"fclrates","roleCode":"ADMIN"}},{"appRoleInfo":{"appName":"oadr","roleCode":"USER"}},{"appRoleInfo":{"appName":"fclgfbo","roleCode":"ADMIN"}},{"appRoleInfo":{"appName":"fclpickup","roleCode":"USER"}},{"appRoleInfo":{"appName":"fclpasskey","roleCode":"ADMIN"}},{"appRoleInfo":{"appName":"fclsupplies","roleCode":"USER"}},{"appRoleInfo":{"appName":"fclfreight","roleCode":"USER"}},{"appRoleInfo":{"appName":"fclsed","roleCode":"USER"}},{"appRoleInfo":{"appName":"oar","roleCode":"USER"}}]}}],"successful":true,"transactionDetailList":[]}}
			*/
  			
  			return Response;
  		}catch (Exception e){
  			e.printStackTrace();
  			return null;
  		}
  	}
	
	public static String[][] getApp_Role_Info_Check(String Details[][], String Cookies){
		String AccountRetrievalResponse = USRC_Endpoints.AccountRetrievalRequest(Cookies);
		if (AccountRetrievalResponse == null || !AccountRetrievalResponse.contains("\"successful\":true")) {
			// call did not complete successfully
			return Details;
		}
		
		String Contact_Details[][] = {{"FSM_ENABLED", "appName\":\"fclfsm\",\"roleCode"},
				{"WDPA_ENABLED", "appName\":\"fclpickup\",\"roleCode"},
				{"GFBO_ENABLED", "appName\":\"fclgfbo\",\"roleCode"},
				{"WGRT_ENABLED", "appName\":\"fclrates\",\"roleCode"},
				{"PASSKEY", "appName\":\"fclpasskey\",\"roleCode"}, 
				};

		for (String[] NewElement: Contact_Details) {
			NewElement[1] = API_Functions.General_API_Calls.ParseStringValue(AccountRetrievalResponse, NewElement[1]);
			if (NewElement[1] == null) {
				NewElement[1] = "false";
			}
		}
		
		String Account[][] = {{"ACCOUNT_NUMBERS", "accountNumber"}, //hard coded below for update
							{"AccountRetrievalResponse", AccountRetrievalResponse}};
		String AllAcountInfo = "";
		String customerAccountList = API_Functions.General_API_Calls.ParseStringValue(AccountRetrievalResponse, "customerAccountList");
		
		do {
			String customerAccountInfo = API_Functions.General_API_Calls.ParseStringValue(customerAccountList, "customerAccountInfo");
			String SingleAccountInfo = API_Functions.General_API_Calls.ParseStringValue(customerAccountInfo, "accountNumber");
			// if the account number is not present try and see if the nickname starts with account number.
			// WARNING, the 8 digit number may not be account number but standard practive is to put the account number as nickname.
			if (SingleAccountInfo == null) {
				break;
			}
			/*else if (SingleAccountInfo.contains("\"value\":\"\"")) {
				String AccountNickname = API_Functions.General_API_Calls.ParseStringValue(customerAccountInfo, "accountNickname");
				for (int pos = 0; pos < AccountNickname.length(); pos++){
					if (!Character.isDigit(AccountNickname.charAt(pos))) {
						break;
					}else if (pos == 8) {
						String TempAccount = AccountNickname.substring(0, 9);
						SingleAccountInfo = SingleAccountInfo.replace("\"value\":\"\"", "\"value\":\"" + TempAccount + "\"");
						break;
					}
				}
			}*/
			String accountKey = API_Functions.General_API_Calls.ParseStringValue(SingleAccountInfo, "key");
			String accountValue = API_Functions.General_API_Calls.ParseStringValue(SingleAccountInfo, "value");
			String EnterpriseCustomerResponse = EnterpriseCustomerRequest(Cookies, accountValue, accountKey);
			
			if (accountValue.contentEquals("")) {
				accountValue = API_Functions.General_API_Calls.ParseStringValue(EnterpriseCustomerResponse, "value");
				SingleAccountInfo = SingleAccountInfo.replace("\"value\":\"\"", "\"value\":\"" + accountValue + "\"");
			}
			if (EnterpriseCustomerResponse.contains("creditCard")) {
				SingleAccountInfo = SingleAccountInfo.replace("}", ", \"creditCard\":\"" + API_Functions.General_API_Calls.ParseStringValue(EnterpriseCustomerResponse, "creditCard") + "}");
			}
			
			customerAccountList = customerAccountList.replaceFirst("customerAccountInfo", "a");
			AllAcountInfo+= SingleAccountInfo;
		} while (customerAccountList.contains("customerAccountInfo"));
		Account[0][1] = AllAcountInfo;
		
		Details = API_Functions.General_API_Calls.coptToTwoDimArray(Details, Contact_Details);
		Details = API_Functions.General_API_Calls.coptToTwoDimArray(Details, Account);

		
/*		//This will check if the user is the owner of the account
		String Account_Value = General_API_Calls.Parse_API_For_Value(Contact_Details[0][1], "value");
		String Account_Key = General_API_Calls.Parse_API_For_Value(Contact_Details[0][1], "key");
		
		String Linage_Indicator = "NOTKNOWN";
		if (Account_Key != null && !Account_Key.contentEquals("") && Account_Value != null && !Account_Value.contentEquals("") ) {
			String EnterpriseCustomerRequest = USRC_Endpoints.EnterpriseCustomerRequest(Cookies, Account_Value, Account_Key);
			Linage_Indicator = General_API_Calls.Parse_API_For_Value(EnterpriseCustomerRequest, "linkageIndicator");
		}
		Details = Arrays.copyOf(Details, Details.length + 1);
		Details[Details.length - 1] = new String[] {"LINKAGE_INDICATOR", Linage_Indicator};*/
		
		return Details;
	}
	
	public static String Parse_AccountRetrievalRequest_AccountNumber(String Response) {
		String Account_Start = "\"value\":\"", Account_End = "\"},\"displayName\"";
		
		String AccountNumbers = "";
		while (Response.contains("accountNumber")) {
			String Account = ParseRequest(Response, Account_Start, Account_End);
			
			if (Account.contentEquals("")) {
				String NickName_Start = "{\"accountNickname\":\"", NickName_End = "\",\"accountNumber\":{";
				Account = ParseRequest(Response, NickName_Start, NickName_End);
				//if account nickname is blank will find the display name instead.
				if (Account.contentEquals("")) {
					String AccountNumber_Start = "displayName\":\"", AccountNumber_End = "\"";
					Account = ParseRequest(Response, AccountNumber_Start, AccountNumber_End);	
				}
			}
			Account = Account.replaceAll("\\u005f", "_");
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

	public static String EnterpriseCustomerRequest(String Cookie, String Account_Value, String Account_Key){
		//This takes the generic USRC url    EX: https://wwwdrt.idev.fedex.com/userCal/user
  		try{
  			USRC_Data USRC_Details = USRC_Data.USRC_Load();
  			HttpPost httppost = new HttpPost(USRC_Details.GenericUSRCURL);

  			JSONObject processingParameters = new JSONObject()
  	  				.put("anonymousTransaction", false)
  	  				.put("clientId", "WFCL")
  	  				.put("clientVersion", "1")
  	  				.put("returnDetailedErrors", true)
  	  				.put("returnLocalizedDateTime", false);
  			
  			JSONObject accountNumber = new JSONObject()
  	  				.put("value", Account_Value)
  	  				.put("key", Account_Key);
  			
  			String requestedProfile[] = {"EXPRESS", "LTLFREIGHT"};
  			JSONObject requestedProfileTypes = new JSONObject()
  	  				.put("requestedProfileType", requestedProfile);
  	  				
  	  		JSONObject EnterpriseCustomerRequest = new JSONObject()
  	  				.put("processingParameters", processingParameters)
  	  				.put("accountNumber", accountNumber)
  	  				.put("requestedProfileTypes", requestedProfileTypes);
  	  				
  	  		JSONObject main = new JSONObject()
  	  				.put("EnterpriseCustomerRequest", EnterpriseCustomerRequest);

  			String json = main.toString();
  			
  			httppost.addHeader("Content-Type", "application/x-www-form-urlencoded");
  			httppost.addHeader("Accept", "text/plain");
  			httppost.addHeader("Cookie", Cookie);
  			
  			List<NameValuePair> urlParameters = new ArrayList<NameValuePair>();
  			urlParameters.add(new BasicNameValuePair("action", "getEnterpriseCustomer"));
  			urlParameters.add(new BasicNameValuePair("format", "json"));
  			urlParameters.add(new BasicNameValuePair("version", "1"));
  			urlParameters.add(new BasicNameValuePair("locale", "en_us"));
  			urlParameters.add(new BasicNameValuePair("data", json));

  			httppost.setEntity(new UrlEncodedFormEntity(urlParameters));

  			String Response = General_API_Calls.HTTPCall(httppost, json);	
			/*
{"EnterpriseCustomerResponse":{"accountType":"INDIVIDUAL","customerAccountInfo":{"account":{"accountIdentifier":{"accountNickname":"My Account - 430","accountNumber":{"key":"9a4457a79152d55132661972048cfe9b","value":"<<Account Number>>"},"displayName":"My Account - 430-430"},"expressProfile":{"accountStatus":"ACTIVE","billingContact":{"address":{"streetLineList":[{"streetLine":"10 FED EX PKWY FL 2"}],"city":"COLLIERVILLE","stateOrProvinceCode":"TN","postalCode":"380178711","countryCode":"US","residential":false},"contact":{"emailAddress":"<<email>>","personName":{"firstName":"SEPT242018","lastName":"MACSAFARI"},"phoneNumber":"9011111111"}},"contactAndAddress":{"address":{"streetLineList":[{"streetLine":"10 FED EX PKWY FL 2"}],"city":"COLLIERVILLE","stateOrProvinceCode":"TN","postalCode":"380178711","countryCode":"US","residential":false},"contact":{"emailAddress":"<<email>>","personName":{"firstName":"SEPT242018","lastName":"MACSAFARI"},"phoneNumber":"9011111111"}},"creditCard":{"creditCardType":"MASTERCARD","expirationDate":"Aug-2019","holder":{"address":{"streetLineList":[{"streetLine":"10 FEDEX PKWY 2ND FL"}],"city":"COLLIERVILLE","stateOrProvinceCode":"TN","postalCode":"38017","countryCode":"US","residential":false},"contact":{"emailAddress":"<<email>>","personName":{"firstName":"SEPT242018","lastName":"MACSAFARI"},"phoneNumber":"1111111"}},"number":"<<CC Num>>"},"creditCardStatus":"CURRENT","shippingContact":{"address":{"streetLineList":[{"streetLine":"10 FED EX PKWY FL 2"}],"city":"COLLIERVILLE","stateOrProvinceCode":"TN","postalCode":"380178711","countryCode":"US","residential":false},"contact":{"emailAddress":"<<email>>","personName":{"firstName":"SEPT242018","lastName":"MACSAFARI"},"phoneNumber":"9011111111"}},"thirdDeclineBlockStatus":"NOT\u005fBLOCKED"},"linkageIndicator":"HIDDEN"},"activeAppRoleInfoList":[{"appRoleInfo":{"appName":"fclcro","roleCode":"ADMIN"}},{"appRoleInfo":{"appName":"fclrates","roleCode":"ADMIN"}},{"appRoleInfo":{"appName":"oadr","roleCode":"USER"}},{"appRoleInfo":{"appName":"fclgfbo","roleCode":"ADMIN"}},{"appRoleInfo":{"appName":"fclpickup","roleCode":"USER"}},{"appRoleInfo":{"appName":"fclpasskey","roleCode":"ADMIN"}},{"appRoleInfo":{"appName":"fclsupplies","roleCode":"USER"}},{"appRoleInfo":{"appName":"fclfreight","roleCode":"USER"}},{"appRoleInfo":{"appName":"fclsed","roleCode":"USER"}},{"appRoleInfo":{"appName":"oar","roleCode":"USER"}}],"inactiveAppNameInfoList":[{"appRoleInfo":{"appName":"fcltrk"}},{"appRoleInfo":{"appName":"fclfsm"}},{"appRoleInfo":{"appName":"fclswab"}},{"appRoleInfo":{"appName":"fclgtm"}},{"appRoleInfo":{"appName":"fcleclaims"}},{"appRoleInfo":{"appName":"ctsapp"}},{"appRoleInfo":{"appName":"fcled"}},{"appRoleInfo":{"appName":"fcleppt"}},{"appRoleInfo":{"appName":"fclrewards"}},{"appRoleInfo":{"appName":"fclandroid"}},{"appRoleInfo":{"appName":"fclfbst"}},{"appRoleInfo":{"appName":"fclbberry"}},{"appRoleInfo":{"appName":"fclfbc"}},{"appRoleInfo":{"appName":"fcldesk"}},{"appRoleInfo":{"appName":"fclfederate"}},{"appRoleInfo":{"appName":"fclbrand"}},{"appRoleInfo":{"appName":"gtm"}},{"appRoleInfo":{"appName":"fcltsmart"}},{"appRoleInfo":{"appName":"fclinsight"}},{"appRoleInfo":{"appName":"fcliphone"}},{"appRoleInfo":{"appName":"fclmobi"}},{"appRoleInfo":{"appName":"fclmyprofile"}},{"appRoleInfo":{"appName":"fclnrt"}},{"appRoleInfo":{"appName":"fclspl"}},{"appRoleInfo":{"appName":"fclcalltags"}},{"appRoleInfo":{"appName":"imadmin"}},{"appRoleInfo":{"appName":"fclmfx"}},{"appRoleInfo":{"appName":"mfx"}},{"appRoleInfo":{"appName":"fclnsg"}},{"appRoleInfo":{"appName":"fclssfr"}},{"appRoleInfo":{"appName":"sms"}},{"appRoleInfo":{"appName":"fclwpor"}},{"appRoleInfo":{"appName":"billing"}},{"appRoleInfo":{"appName":"custrateguide"}},{"appRoleInfo":{"appName":"doc\u005fdet"}},{"appRoleInfo":{"appName":"ediscount"}},{"appRoleInfo":{"appName":"fclck"}},{"appRoleInfo":{"appName":"fclfio"}},{"appRoleInfo":{"appName":"fclrebills"}},{"appRoleInfo":{"appName":"frtdispatch"}},{"appRoleInfo":{"appName":"ftn"}},{"appRoleInfo":{"appName":"gtm\u005fadmin"}},{"appRoleInfo":{"appName":"hsl"}},{"appRoleInfo":{"appName":"inet"}},{"appRoleInfo":{"appName":"insight"}},{"appRoleInfo":{"appName":"lce"}},{"appRoleInfo":{"appName":"passkey"}},{"appRoleInfo":{"appName":"pid"}},{"appRoleInfo":{"appName":"ShipAndGet"}},{"appRoleInfo":{"appName":"solutionadv"}},{"appRoleInfo":{"appName":"sratefinder"}},{"appRoleInfo":{"appName":"sso"}},{"appRoleInfo":{"appName":"ssoacctmgmt"}},{"appRoleInfo":{"appName":"ssomyfedex"}},{"appRoleInfo":{"appName":"ssopickup"}},{"appRoleInfo":{"appName":"ssoswab"}},{"appRoleInfo":{"appName":"ssotestapp1"}},{"appRoleInfo":{"appName":"testprog"}},{"appRoleInfo":{"appName":"fclwerl"}},{"appRoleInfo":{"appName":"fsm"}}]},"successful":true,"transactionDetailList":[]}}			
			*/
  			
  			return Response;
  		}catch (Exception e){
  			e.printStackTrace();
  			return null;
  		}
  	}

	//will return the last 4 characters of the credit card linked to an account number.
	public static String AccountRetrieval_Then_EnterpriseCustomer(String Cookie) {
		String AccountRetrievalResponse = AccountRetrievalRequest(Cookie);
		 ///{"key":"9a4457a79152d55132661972048cfe9b","value":"<<Account number here>>"},"displayName"
		 String Account_Key = ParseRequest (AccountRetrievalResponse, "{\"key\":\"" , "\",\"value\":\"");
		 String Account_Value = ParseRequest (AccountRetrievalResponse, "\"value\":\"" , "\"},\"displayName\"");
		 String EnterpriseCustomerResponse = EnterpriseCustomerRequest(Cookie, Account_Value, Account_Key);
		 		 
		 //"number":"<<CC Num>>"},"creditCardStatus"
		 if (EnterpriseCustomerResponse.contains("\"creditCardStatus\"")) {
			 //return the credit card linked to the account
			 String FullCreditCard = ParseRequest (EnterpriseCustomerResponse, "\"number\":\"" , "\"},\"creditCardStatus\"");
			 Helper_Functions.PrintOut("    " + Arrays.toString(new String[] {Account_Value, Account_Key, FullCreditCard}));
			 //return the last 4 digits of the credit card
			 return FullCreditCard.substring(FullCreditCard.length() - 4, FullCreditCard.length());
		 }else {
			 Helper_Functions.PrintOut(Arrays.toString(new String[] {Account_Value, Account_Key, "Not able to retrieve credit card."}));
			 return "";
		 }
	}
	
	//will return the account key and value, assumption that user has permission to see account number
	public static String[] Account_Key_and_Value(String Cookie) {
		 String AccountRetrievalResponse = AccountRetrievalRequest(Cookie);
		 ///{"key":"9a4457a79152d55132661972048cfe9b","value":"<<Account number here>>"},"displayName"
		 String Account_Key = ParseRequest (AccountRetrievalResponse, "{\"key\":\"" , "\",\"value\":\"");
		 String Account_Value = ParseRequest (AccountRetrievalResponse, "\"value\":\"" , "\"},\"displayName\"");
		 if (Account_Value.length() > 9 || Account_Value.length() < 8) {
			 Helper_Functions.PrintOut("Warning, account value may be incorrect. Make sure user has access to view full account number.");
		 }
		 return new String[] {Account_Key,Account_Value };
	} 
	
}