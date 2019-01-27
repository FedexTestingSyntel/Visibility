package CXS_Support;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import org.junit.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.DataProvider;
import CXS_Support.USRC_Data;
import Data_Structures.User_Data;
import SupportClasses.Environment;
import SupportClasses.Helper_Functions;
import org.testng.annotations.Listeners;
import org.testng.annotations.Test;
import static org.junit.Assert.assertThat;
import static org.hamcrest.CoreMatchers.containsString;

@Listeners(SupportClasses.TestNG_TestListener.class)

public class USRC_General {

	static String LevelsToTest = "7"; //Can but updated to test multiple levels at once if needed. Setting to "23" will test both level 2 and level 3.

	@BeforeClass
	public void beforeClass() {
		Environment.SetLevelsToTest(LevelsToTest);
	}
	
	@DataProvider (parallel = true)
	public Iterator<Object[]> dp(Method m) {
	    List<Object[]> data = new ArrayList<>();
	    
	    for (int i = 0; i < Environment.LevelsToTest.length(); i++) {
	    	String strLevel = "" + Environment.LevelsToTest.charAt(i);
	    	int intLevel = Integer.parseInt(strLevel);
	    	//loading the OAuth token and having all of the variables set.
	    	USRC_Data.LoadVariables(strLevel);
	    	
			switch (m.getName()) { //Based on the method that is being called the array list will be populated.
			case "CreateUsers":
				for (int j = 0 ; j < 1; j++) {
					data.add(new Object[] {strLevel, j});
				}
				break;
			case "CheckLogin":
				//loading the OAuth token and having all of the variables set.
				PRDC_Data.LoadVariables(strLevel);
				User_Data UD[] = Environment.Get_UserIds(intLevel);
				for (int k = 0; k < UD.length; k++) {
    				if (UD[k].ERROR.contentEquals("")) {
    					data.add(new Object[] {strLevel, UD[k].SSO_LOGIN_DESC, UD[k].USER_PASSWORD_DESC});
    					//break;
    				}
    			}
				break;
			case "CheckIfUserInvalidLogin":
				UD = Environment.Get_UserIds(intLevel);
				for (int k = 0; k < UD.length; k++) {
    				if (UD[k].FIRST_NM.contentEquals("")) {
    					data.add(new Object[] {strLevel, UD[k].SSO_LOGIN_DESC, UD[k].USER_PASSWORD_DESC});
    				}
    			}
				break;
				
			}//end switch MethodName
		}
		return data.iterator();
	}
	
	@Test (dataProvider = "dp", enabled = false)
	public void CreateUsers(String Level, int ContactPosition) {
		USRC_Data USRC_Details = USRC_Data.LoadVariables(Level);
		String UUID = null, fdx_login_fcl_uuid[] = {"","", ""};
		//1 - Login, get cookies and uuid
		String UserID = "L" + USRC_Details.Level + "UpdatePassword" + Helper_Functions.CurrentDateTime() + Helper_Functions.getRandomString(2);
		String Password = "Test1234";
			
		//create the new user
		String ContactDetails[] = USRC_Data.ContactDetailsList.get(ContactPosition % USRC_Data.ContactDetailsList.size());
		ContactDetails[4] = "Saqqqqwwwweeeerrrr.OSV@FEDEX.COM";
		String Response = USRC_API_Endpoints.NewFCLUser(USRC_Details.REGCCreateNewUserURL, ContactDetails, UserID, Password);
			
		//check to make sure that the userid was created.
		assertThat(Response, containsString("successful\":true"));
			
		//get the cookies and the uuid of the new user
		fdx_login_fcl_uuid = USRC_API_Endpoints.Login(USRC_Details.LoginUserURL, UserID, Password);
		UUID = fdx_login_fcl_uuid[1];
			
		Helper_Functions.PrintOut(UserID + "/" + Password + "--" + UUID, false);
	}
	
	@Test (dataProvider = "dp")
	public void CheckLogin(String Level, String UserID, String Password) {
		USRC_Data USRC_Details = USRC_Data.LoadVariables(Level);
		
		String Cookies = null, fdx_login_fcl_uuid[] = null;
		//get the cookies and the uuid of the user
		fdx_login_fcl_uuid = USRC_API_Endpoints.Login(USRC_Details.LoginUserURL, UserID.replaceAll(" ", ""), Password.replaceAll(" ", ""));
		String ContactDetailsParsed[][] = {{"UUID_NBR", ""},//index 0 and set below
				{"SSO_LOGIN_DESC", UserID.replaceAll(" ", "")},
				{"USER_PASSWORD_DESC", Password.replaceAll(" ", "")},
				{"SECRET_QUESTION_DESC", ""}, 
				{"SECRET_ANSWER_DESC", ""},
				{"FIRST_NM", ""}, 
				{"LAST_NM", ""}, 
				{"STREET_DESC", ""}, 
				{"CITY_NM", ""}, 
				{"STATE_CD", ""}, 
				{"POSTAL_CD", ""}, 
				{"COUNTRY_CD", ""}, 
				{"ACCOUNT_NUMBER", "Na"},//index 12 and set below
				{"WCRV_ENABLED", "Na"},//index 13 and set below
				{"GFBO_ENABLED", "Na"},//index 14 and set below
				{"WGRT_ENABLED", "Na"},//index 15 and set below
				{"WDPA_ENABLED", "Na"},//index 16 and set below
				{"PASSKEY", "NON-PASSKEY"},//index 17 and set below
				{"ERROR", ""}//index 18 and set below
				};
		
		if (fdx_login_fcl_uuid != null){
			Cookies = fdx_login_fcl_uuid[0];
			ContactDetailsParsed[0][1] = fdx_login_fcl_uuid[1];//save the uuid
			
			String AccountRetrievalRequest = USRC_API_Endpoints.AccountRetrievalRequest(USRC_Details.LoginUserURL, Cookies);
			//Check if user has GFBO access
			if (AccountRetrievalRequest.contains("appName\":\"fclgfbo\",\"roleCode\":\"ADMIN")) {
				ContactDetailsParsed[14][1] = "GFBO_ADMIN";
			}else if (AccountRetrievalRequest.contains("appName\":\"fclgfbo\",\"roleCode\":\"")){
				ContactDetailsParsed[14][1] = "GFBO_Standard";
			}
			//Check if user has WGRT access
			if (AccountRetrievalRequest.contains("appName\":\"fclrates\",\"roleCode\":\"ADMIN")) {
				ContactDetailsParsed[15][1] = "WGRT_ADMIN";
			}else if (AccountRetrievalRequest.contains("appName\":\"fclrates\",\"roleCode\":\"")){
				ContactDetailsParsed[15][1] = "WGRT_Standard";
			}
			//Check if user has WDPA access
			if (AccountRetrievalRequest.contains("appName\":\"fclpickup\",\"roleCode\":\"ADMIN")) {
				ContactDetailsParsed[16][1] = "WDPA_ADMIN";
			}else if (AccountRetrievalRequest.contains("appName\":\"fclpickup\",\"roleCode\":\"")){
				ContactDetailsParsed[16][1] = "WDPA_Standard";
			}
			//Check if the user is administered, not able to narrow down if they are IPAS or WADM
			if (AccountRetrievalRequest.contains("appName\":\"fclpasskey\",\"roleCode\":\"")) {
				ContactDetailsParsed[17][1] = "PASSKEY";
			}
			
			
			ContactDetailsParsed[12][1] = USRC_API_Endpoints.Parse_AccountRetrievalRequest_AccountNumber(AccountRetrievalRequest);
			String ContactDetailsResponse = USRC_API_Endpoints.ViewUserProfileWIDM(USRC_Details.ViewUserProfileWIDMURL, Cookies);
			ContactDetailsParsed = USRC_API_Endpoints.Parse_ViewUserProfileWIDM(ContactDetailsResponse, ContactDetailsParsed);
			
			PRDC_Data PRDC_D = PRDC_Data.LoadVariables(Level);
			ContactDetailsParsed[13][1] = WCRV_Access(PRDC_D.AccountsURL, Cookies);
		}else {
			//will save the current time of the failure.
			ContactDetailsParsed[17][1] = Helper_Functions.CurrentDateTime(true);
		}
		
		String FileName = Helper_Functions.DataDirectory + "\\TestingData.xls";
		boolean updatefile = Helper_Functions.WriteToExcel(FileName, "L" + Level, ContactDetailsParsed, 1);
		Helper_Functions.PrintOut("Contact Details: " + Arrays.toString(ContactDetailsParsed), true);
		if (!updatefile) {
			Assert.fail("Not able to update file.");
		}else if (fdx_login_fcl_uuid == null) {
			Assert.fail("Not able to login");
		}
	}
	
	@Test (dataProvider = "dp", enabled = false)
	public void CheckIfUserInvalidLogin(String Level, String UserID, String Password) {
		String fdx_login_fcl_uuid[] = null;
		USRC_Data USRC_Details = USRC_Data.LoadVariables(Level);
		//get the cookies and the uuid of the user
		fdx_login_fcl_uuid = USRC_API_Endpoints.Login(USRC_Details.LoginUserURL, UserID, Password);
		if (fdx_login_fcl_uuid == null){
			Assert.fail(UserID);
		}
	}
	
	public String WCRV_Access(String Accounts_URL, String Cookies) {
		String AccountDetails = PRDC_API_Endpoints.Accounts_Call(Accounts_URL, Cookies);
		String WCRV_Access = "";
		if (AccountDetails.contains("displayRateSheetFlag\":true") && AccountDetails.contains("discountPricingFlag\":true") && AccountDetails.contains("accountCountryEnabledFlag\":true")){
			WCRV_Access = "Enabled";
		}else if (!AccountDetails.contains("displayRateSheetFlag")){
			Helper_Functions.PrintOut("Warning, PRDC call did not return premission status.", false);
		}else {
			if (AccountDetails.contains("displayRateSheetFlag\":false")){
				//the user does not have the view rate sheet privilege
				WCRV_Access += "displayRateSheet_False ";
			}
			if (AccountDetails.contains("discountPricingFlag\":false")){
				//no account numbers have a discount applied
				WCRV_Access += "discountPricingFlag_False ";
			}
			if (AccountDetails.contains("accountCountryEnabledFlag\":false")){
				//no account numbers are from a valid country
				WCRV_Access += "accountCountryEnabledFlag_False";
			}
		}
		return WCRV_Access;
	}
}
