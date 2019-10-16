package USRC;

import java.util.ArrayList;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONObject;

import API_Functions.General_API_Calls;

public class enterprise_customer {

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
	
}
