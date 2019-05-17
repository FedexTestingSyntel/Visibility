package WFCL_Application;

import static org.junit.Assert.assertThat;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import org.apache.http.HttpEntity;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.hamcrest.CoreMatchers;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Listeners;
import org.testng.annotations.Test;
import Data_Structures.Account_Data;
import Data_Structures.USRC_Data;
import Data_Structures.WIDM_Data;
import SupportClasses.Environment;
import SupportClasses.General_API_Calls;
import SupportClasses.Helper_Functions;
import SupportClasses.WebDriver_Functions;
import USRC_Application.USRC_API_Endpoints;
import WIDM_Application.WIDM_Endpoints;

@Listeners(SupportClasses.TestNG_TestListener.class)

public class WFCL_API_Functions { 

	static String LevelsToTest = "2";  
	static String CountryList[][]; 

	@BeforeClass
	public void beforeClass() {
		Environment.SetLevelsToTest(LevelsToTest);
		CountryList = Environment.getCountryList("US");
		Helper_Functions.MyEmail = "accept@fedex.com";
	}
	 
	@DataProvider (parallel = true)
	public static Iterator<Object[]> dp(Method m) {
		List<Object[]> data = new ArrayList<Object[]>();
		
		for (int i = 0; i < Environment.LevelsToTest.length(); i++) {
			String Level = String.valueOf(Environment.LevelsToTest.charAt(i));
			Account_Data Account_Info = null;
			switch (m.getName()) {
				case "WFCL_INET_Account_Registration":
		    	case "WFCL_Link_Account":
		    		for (int j = 0; j < CountryList.length; j++) {
		    			Account_Info = Helper_Functions.getFreshAccount(Level, CountryList[j][0]);
		    			data.add( new Object[] {Level, Account_Info});
					}
		    		break;
			}
		}	
		return data.iterator();
	}
	
	@Test(dataProvider = "dp")
	public void WFCL_Link_Account(String Level, Account_Data Account_Info){
  		try{  	
  			String Response = "";
  			Account_Data.Set_Dummy_Contact_Name(Account_Info);
  			
  			//Account_Info.User_Info.USER_ID = "L2A784800580US051319T101455zhiz";
  			if (Account_Info.User_Info.USER_ID.contentEquals("")) {
  				// Create the user id
  	  			WIDM_Data WIDM_Info = WIDM_Data.LoadVariables(Level);
  	  			Account_Data.Set_UserId(Account_Info, "L" + Level + "A" + Account_Info.Account_Number + Account_Info.Billing_Address_Info.Country_Code);
  	  			Response = WIDM_Endpoints.AAA_User_Create(WIDM_Info.EndpointUrl, Account_Info, null);
  	  			Account_Data.Print_High_Level_Details(Account_Info);
  	  			assertThat(Response, CoreMatchers.containsString("<transactionId>"));
  	  			Helper_Functions.PrintOut(Response);
  	  			Helper_Functions.WriteUserToExcel(Account_Info.User_Info.USER_ID, Account_Info.User_Info.PASSWORD);
  			}
  			
  			//login with the user id created above.
  			USRC_Data USRC_Details = USRC_Data.LoadVariables(Level);
  			String fdx_login_fcl_uuid[] = null;
  			//get the cookies and the uuid of the user
  			fdx_login_fcl_uuid = USRC_API_Endpoints.Login(USRC_Details.GenericUSRCURL, Account_Info.User_Info.USER_ID, Account_Info.User_Info.PASSWORD);
  			Assert.assertNotNull(fdx_login_fcl_uuid);
  			
  			//try and link the account number
  			String Level_Url = WebDriver_Functions.LevelUrlReturn();
  			String URL = Level_Url + "/fcl/FclValidateAndCreateAction.do";
  			HttpPost httppost = new HttpPost(URL);

  			httppost.addHeader("Content-Type", "application/x-www-form-urlencoded");
  			httppost.addHeader("Accept", "*/*");
  			httppost.addHeader("Host", Level_Url.replace("https://", ""));
  			httppost.addHeader("Cookie", fdx_login_fcl_uuid[0]);
	
  	  		List<NameValuePair> urlParameters = new ArrayList<NameValuePair>();
  	  		urlParameters.add(new BasicNameValuePair("ask", "COMMAND_1"));
  	  		urlParameters.add(new BasicNameValuePair("cancel_registration", ""));
  	  		urlParameters.add(new BasicNameValuePair("cancelUserId", "Cancel"));
  	  		urlParameters.add(new BasicNameValuePair("createUserID", "Continue"));
  	  		urlParameters.add(new BasicNameValuePair("email", ""));
  	  		urlParameters.add(new BasicNameValuePair("companyName", ""));
  	  		urlParameters.add(new BasicNameValuePair("contactName", ""));
  	  		urlParameters.add(new BasicNameValuePair("creditCardNumber", ""));
  	  		urlParameters.add(new BasicNameValuePair("invoiceNumberB", ""));
  	  		urlParameters.add(new BasicNameValuePair("invoiceNumberA", ""));
  	  		urlParameters.add(new BasicNameValuePair("country1", Account_Info.Billing_Address_Info.Country_Code.toLowerCase()));
  	  		urlParameters.add(new BasicNameValuePair("zip", Account_Info.Billing_Address_Info.Zip));
  	  		urlParameters.add(new BasicNameValuePair("state", Account_Info.Billing_Address_Info.State_Code));
  	  		urlParameters.add(new BasicNameValuePair("city", Account_Info.Billing_Address_Info.City));
  	  		urlParameters.add(new BasicNameValuePair("address2", Account_Info.Billing_Address_Info.Address_Line_2));
  	  		urlParameters.add(new BasicNameValuePair("address1", Account_Info.Billing_Address_Info.Address_Line_1));
  	  		urlParameters.add(new BasicNameValuePair("lastName", Account_Info.LastName));
  	  		urlParameters.add(new BasicNameValuePair("initials", Account_Info.MiddleName));
  	  		urlParameters.add(new BasicNameValuePair("firstName", Account_Info.FirstName));
  	  		urlParameters.add(new BasicNameValuePair("nickName", Account_Info.Account_Nickname));
  	  		urlParameters.add(new BasicNameValuePair("accountNumber", Account_Info.Account_Number));
  	  		urlParameters.add(new BasicNameValuePair("displayScreen", "addressScreen"));
  	  		urlParameters.add(new BasicNameValuePair("deviceID", ""));
  	  		urlParameters.add(new BasicNameValuePair("country", Account_Info.Billing_Address_Info.Country_Code.toLowerCase()));
  	  		urlParameters.add(new BasicNameValuePair("allvalid", "false"));
  	  		urlParameters.add(new BasicNameValuePair("programIndicator", "p314t9n1ey"));
  	  		urlParameters.add(new BasicNameValuePair("locale", Account_Info.Billing_Address_Info.Country_Code.toLowerCase() + "_" + Account_Info.LanguageCode.toLowerCase()));
  	  		urlParameters.add(new BasicNameValuePair("languageCode", Account_Info.LanguageCode.toLowerCase()));
  	  		urlParameters.add(new BasicNameValuePair("countryCode", Account_Info.Billing_Address_Info.Country_Code.toLowerCase()));
  	  		urlParameters.add(new BasicNameValuePair("afterwardsURL", Level_Url + ":443/fcl/web/jsp/oadr.jsp"));
  	  		urlParameters.add(new BasicNameValuePair("step3URL", "https%3A//.fedex.com%3A443/fcl/web/jsp/fclValidateAndCreate.jsp")); //set to prod
  	  		urlParameters.add(new BasicNameValuePair("fclHost", Level_Url));
  	  		urlParameters.add(new BasicNameValuePair("appName", "oadr"));

  	  		httppost.setEntity(new UrlEncodedFormEntity(urlParameters));
  	  			
  	  		HttpEntity entity = httppost.getEntity();
  	  		String Request = EntityUtils.toString(entity, "UTF-8");
  	  		Response = General_API_Calls.HTTPCall(httppost, Request);	
  	  		
  	  		Helper_Functions.PrintOut(Response);
  	  		
  			//return Account_Info;
  		}catch (Exception e){
  			e.printStackTrace();
  			//return null;
  		}
  	}
	
	@Test(dataProvider = "dp")
	public void WFCL_INET_Account_Registration(String Level, Account_Data Account_Info){
  		try{  	
  			String Response = "";
  			Account_Data.Set_Dummy_Contact_Name(Account_Info);
  			
  			//Account_Info.User_Info.USER_ID = "L2A784800580US051319T101455zhiz";
  			if (Account_Info.User_Info.USER_ID.contentEquals("")) {
  				// Create the user id
  	  			WIDM_Data WIDM_Info = WIDM_Data.LoadVariables(Level);
  	  			Account_Data.Set_UserId(Account_Info, "L" + Level + "A" + Account_Info.Account_Number + Account_Info.Billing_Address_Info.Country_Code);
  	  			Response = WIDM_Endpoints.AAA_User_Create(WIDM_Info.EndpointUrl, Account_Info, null);
  	  			Account_Data.Print_High_Level_Details(Account_Info);
  	  			assertThat(Response, CoreMatchers.containsString("<transactionId>"));
  	  			Helper_Functions.PrintOut(Response);
  	  			Helper_Functions.WriteUserToExcel(Account_Info.User_Info.USER_ID, Account_Info.User_Info.PASSWORD);
  			}
  			
  			//login with the user id created above.
  			USRC_Data USRC_Details = USRC_Data.LoadVariables(Level);
  			String fdx_login_fcl_uuid[] = null;
  			//get the cookies and the uuid of the user
  			fdx_login_fcl_uuid = USRC_API_Endpoints.Login(USRC_Details.GenericUSRCURL, Account_Info.User_Info.USER_ID, Account_Info.User_Info.PASSWORD);
  			Assert.assertNotNull(fdx_login_fcl_uuid);
  			
  			//try and link the account number
  			String Level_Url = WebDriver_Functions.LevelUrlReturn();
  			String URL = Level_Url + "/fcl/accountInfo1Action.do";
  			HttpPost httppost = new HttpPost(URL);

  			httppost.addHeader("Content-Type", "application/x-www-form-urlencoded");
  			httppost.addHeader("Accept", "*/*");
  			httppost.addHeader("Host", Level_Url.replace("https://", ""));
  			httppost.addHeader("Cookie", fdx_login_fcl_uuid[0]);
	
  	  		List<NameValuePair> urlParameters = new ArrayList<NameValuePair>();
  	  		urlParameters.add(new BasicNameValuePair("appName", "fclfsm"));
  	  		urlParameters.add(new BasicNameValuePair("step3URL", "https://wwwdev.idev.fedex.com/shipping/shipEntryAction.do?method=doRegistration&link=1&locale=en_US&urlparams=us&sType=F"));
  	  		urlParameters.add(new BasicNameValuePair("afterwardsURL", "https://wwwdev.idev.fedex.com/shipping/shipEntryAction.do?method=doEntry&link=1&locale=en_US&urlparams=us&sType=F&programIndicator=0"));
  	  		urlParameters.add(new BasicNameValuePair("countryCode", Account_Info.Billing_Address_Info.Country_Code.toLowerCase()));
  	  		urlParameters.add(new BasicNameValuePair("languageCode", Account_Info.LanguageCode.toLowerCase()));
  	  		urlParameters.add(new BasicNameValuePair("programIndicator", "1"));
  	  		urlParameters.add(new BasicNameValuePair("registrationType", "updateRegistration"));
  	  		urlParameters.add(new BasicNameValuePair("fclHost", Level_Url.replace("https://", "")));
  	  		urlParameters.add(new BasicNameValuePair("addressType", "shipping"));
  	  		urlParameters.add(new BasicNameValuePair("charEncoding", "ISO-8859-1"));
  	  		urlParameters.add(new BasicNameValuePair("opco", ""));
  	  		urlParameters.add(new BasicNameValuePair("deviceID", ""));
  	  		urlParameters.add(new BasicNameValuePair("accountOption", "enterAccountNumber"));
  	  		urlParameters.add(new BasicNameValuePair("newAccountNumber", Account_Info.Account_Number));
  	  		urlParameters.add(new BasicNameValuePair("newNickName", Account_Info.Account_Nickname));
  	  		urlParameters.add(new BasicNameValuePair("submit", "Continue  >>"));

  	  		httppost.setEntity(new UrlEncodedFormEntity(urlParameters));
  	  			
  	  		HttpEntity entity = httppost.getEntity();
  	  		String Request = EntityUtils.toString(entity, "UTF-8");
  	  		Response = General_API_Calls.HTTPCall(httppost, Request);	
  	  		
  	  		Helper_Functions.PrintOut(Response);
  	  		
  			//return Account_Info;
  		}catch (Exception e){
  			e.printStackTrace();
  			//return null;
  		}
  	}
	
}
