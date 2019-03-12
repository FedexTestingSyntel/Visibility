package USRC_Application;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import org.junit.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.DataProvider;

import Data_Structures.PRDC_Data;
import Data_Structures.USRC_Data;
import Data_Structures.User_Data;
import PRDC_Application.PRDC_API_Endpoints;
import SupportClasses.Environment;
import SupportClasses.Helper_Functions;
import org.testng.annotations.Test;
import static org.junit.Assert.assertThat;
import static org.hamcrest.CoreMatchers.containsString;

//import org.testng.annotations.Listeners;
//@Listeners(SupportClasses.TestNG_TestListener.class)

public class USRC_General {

	static String LevelsToTest = "3"; //Can but updated to test multiple levels at once if needed. Setting to "23" will test both level 2 and level 3.

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
			case "CreateUsers_AddressDetails":
				/////////////currently not working for non US
				ArrayList<String[]> AddressDetails = new ArrayList<String[]>();
				AddressDetails = Helper_Functions.getExcelData(Helper_Functions.DataDirectory + "\\AddressDetails.xls",  "Countries");//load the relevant information from excel file.
				String Phone = "9011111111", Email = "FillerEmail@fedex.com";
				for (int j = 0 ; j < AddressDetails.size(); j++) {
					String CountryList[] = AddressDetails.get(j);
					if (CountryList[6].contentEquals("FR")) {
						if (CountryList[5].contains(" ")){
							CountryList[5] = CountryList[5].replaceAll(" ", "");
						}
						String ContactDetails[] = new String[]{"FirstName", "", "LastName", Phone, Email, CountryList[0], CountryList[1], CountryList[3], CountryList[4], CountryList[5], CountryList[6], ""};
						data.add(new Object[] {strLevel, ContactDetails});
						break;
					}		
					//[Address_Line_1, Address_Line_2, City, State, State_Code, Zip, Country_Code, Region, Country]
				}
				break;		
			case "CheckLogin":
				//loading the OAuth token and having all of the variables set.
				PRDC_Data.LoadVariables(strLevel);
				User_Data UD[] = Environment.Get_UserIds(intLevel);
				for (int k = 0; k < UD.length; k++) {
    				if (UD[k].EMAIL_ADDRESS.contentEquals("")) {
    					data.add(new Object[] {strLevel, UD[k].SSO_LOGIN_DESC, UD[k].USER_PASSWORD_DESC});
    				}
    			}
				break;
			case "Check_FDM_Status":
				//loading the OAuth token and having all of the variables set.
				UD = Environment.Get_UserIds(intLevel);
				for (int k = 0; k < UD.length; k++) {
					if(UD[k].FDM_STATUS.contentEquals("")) {
    					data.add(new Object[] {strLevel, UD[k].SSO_LOGIN_DESC, UD[k].USER_PASSWORD_DESC});
    				}
    			}
				break;
			case  "UpdateValue":
			case "Check_WCRV_Status":
				//loading the OAuth token and having all of the variables set.
				UD = Environment.Get_UserIds(intLevel);
				for (int k = 0; k < UD.length; k++) {
					if(UD[k].WCRV_ENABLED.contentEquals("Enabled")) {
    					data.add(new Object[] {strLevel, UD[k].SSO_LOGIN_DESC, UD[k].USER_PASSWORD_DESC});
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
	    
	   // while (data.size() > 300) {
	 //   	data.remove(data.size() - 1);
	  //  }
	    System.out.println(data.size() + " scenarios.");
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
		fdx_login_fcl_uuid = USRC_API_Endpoints.Login(USRC_Details.GenericUSRCURL, UserID, Password);
		UUID = fdx_login_fcl_uuid[1];
			
		Helper_Functions.PrintOut(UserID + "/" + Password + "--" + UUID, false);
	}
	
	@Test (dataProvider = "dp", enabled = false)
	public void CreateUsers_AddressDetails(String Level, String ContactDetails[]) {
		USRC_Data USRC_Details = USRC_Data.LoadVariables(Level);
		String UUID = null, fdx_login_fcl_uuid[] = {"","", ""};
		//1 - Login, get cookies and uuid
		String UserID = "L" + USRC_Details.Level + "UpdatePassword" + Helper_Functions.CurrentDateTime() + Helper_Functions.getRandomString(2);
		String Password = "Test1234";
			
		//create the new user

		String Response = USRC_API_Endpoints.NewFCLUser(USRC_Details.REGCCreateNewUserURL, ContactDetails, UserID, Password);
			
		//check to make sure that the userid was created.
		assertThat(Response, containsString("successful\":true"));
			
		//get the cookies and the uuid of the new user
		fdx_login_fcl_uuid = USRC_API_Endpoints.Login(USRC_Details.GenericUSRCURL, UserID, Password);
		UUID = fdx_login_fcl_uuid[1];
			
		Helper_Functions.PrintOut(UserID + "/" + Password + "--" + UUID, false);
	}
		
	@Test (dataProvider = "dp", enabled = true)
	public void CheckLogin(String Level, String UserID, String Password) {
		Environment.getInstance().setLevel(Level);
		USRC_Data USRC_Details = USRC_Data.LoadVariables(Level);
		
		String Cookies = null, fdx_login_fcl_uuid[] = null;
		//get the cookies and the uuid of the user
		fdx_login_fcl_uuid = USRC_API_Endpoints.Login(USRC_Details.GenericUSRCURL, UserID.replaceAll(" ", ""), Password.replaceAll(" ", ""));
		
		//in case cannot login will check two of the generic other passwords
		if (fdx_login_fcl_uuid == null && Password.contentEquals("Test1234")){
			Password = "Test12345";
			fdx_login_fcl_uuid = USRC_API_Endpoints.Login(USRC_Details.GenericUSRCURL, UserID.replaceAll(" ", ""), Password.replaceAll(" ", ""));
		}else if (fdx_login_fcl_uuid == null && Password.contentEquals("Test12345")){
			Password = "Test1234";
			fdx_login_fcl_uuid = USRC_API_Endpoints.Login(USRC_Details.GenericUSRCURL, UserID.replaceAll(" ", ""), Password.replaceAll(" ", ""));
		}
		
		String ContactDetailsParsed[][] = {{"UUID_NBR", ""},//index 0 and set below
				{"SSO_LOGIN_DESC", UserID},
				{"USER_PASSWORD_DESC", Password},
				{"SECRET_QUESTION_DESC", ""}, 
				{"SECRET_ANSWER_DESC", ""},
				{"FIRST_NM", ""}, 
				{"LAST_NM", ""}, 
				{"STREET_DESC", ""}, 
				{"CITY_NM", ""}, 
				{"STATE_CD", ""}, 
				{"POSTAL_CD", ""}, 
				{"COUNTRY_CD", ""}, 
				{"ACCOUNT_NUMBER", "F"},//index 12 and set below
				{"WCRV_ENABLED", "F"},//index 13 and set below
				{"GFBO_ENABLED", "F"},//index 14 and set below
				{"WGRT_ENABLED", "F"},//index 15 and set below
				{"WDPA_ENABLED", "F"},//index 16 and set below
				{"PASSKEY", "F"},//index 17 and set below
				{"FDM_STATUS", "F"}, //index 18 below
				{"EMAIL_ADDRESS", ""}
				};
		
		if (fdx_login_fcl_uuid != null){
			Cookies = fdx_login_fcl_uuid[0];
			ContactDetailsParsed[0][1] = fdx_login_fcl_uuid[1];//save the uuid
			
			String AccountRetrievalRequest = USRC_API_Endpoints.AccountRetrievalRequest(USRC_Details.GenericUSRCURL, Cookies);
			
			//Check if user has GFBO access
			if (AccountRetrievalRequest.contains("appName\":\"fclgfbo\",\"roleCode\":\"")){
				ContactDetailsParsed[14][1] = "T";
			}
			//Check if user has WGRT access
			if (AccountRetrievalRequest.contains("appName\":\"fclrates\",\"roleCode\":\"")){
				ContactDetailsParsed[15][1] = "T";
			}
			//Check if user has WDPA access
			if (AccountRetrievalRequest.contains("appName\":\"fclpickup\",\"roleCode\":\"")){
				ContactDetailsParsed[16][1] = "T";
			}
			//Check if the user is administered, not able to narrow down if they are IPAS or WADM
			if (AccountRetrievalRequest.contains("appName\":\"fclpasskey\",\"roleCode\":\"")) {
				ContactDetailsParsed[17][1] = "T";
			}
			
			String Response = USRC_API_Endpoints.RecipientProfile(USRC_Details.GenericUSRCURL, Cookies);
			if (Response.contains("recipientProfileEnrollmentStatus\":\"ENROLLED")) {
				ContactDetailsParsed[18][1] = Response;//store all of the FDM details
			}
			
			ContactDetailsParsed[12][1] = USRC_API_Endpoints.Parse_AccountRetrievalRequest_AccountNumber(AccountRetrievalRequest);
			String ContactDetailsResponse = USRC_API_Endpoints.ViewUserProfileWIDM(USRC_Details.ViewUserProfileWIDMURL, Cookies);
			ContactDetailsParsed = USRC_API_Endpoints.Parse_ViewUserProfileWIDM(ContactDetailsResponse, ContactDetailsParsed);
			
			PRDC_Data PRDC_D = PRDC_Data.LoadVariables(Level);
			ContactDetailsParsed[13][1] = WCRV_Access(PRDC_D.AccountsURL, Cookies);
		}else {
			//will save the current time of the failure.
			ContactDetailsParsed = new String[][]{{"SSO_LOGIN_DESC", UserID.replaceAll(" ", "")},
					{"ERROR", Helper_Functions.CurrentDateTime(true)}};
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
	public void Check_FDM_Status(String Level, String UserID, String Password) {
		USRC_Data USRC_Details = USRC_Data.LoadVariables(Level);
		
		String Cookies = null, fdx_login_fcl_uuid[] = null;
		//get the cookies and the uuid of the user
		fdx_login_fcl_uuid = USRC_API_Endpoints.Login(USRC_Details.GenericUSRCURL, UserID.replaceAll(" ", ""), Password.replaceAll(" ", ""));
		
		//in case cannot login will check two of the generic other passwords
		if (fdx_login_fcl_uuid == null && Password.contentEquals("Test1234")){
			Password = "Test12345";
			fdx_login_fcl_uuid = USRC_API_Endpoints.Login(USRC_Details.GenericUSRCURL, UserID.replaceAll(" ", ""), Password.replaceAll(" ", ""));
		}else if (fdx_login_fcl_uuid == null && Password.contentEquals("Test12345")){
			Password = "Test1234";
			fdx_login_fcl_uuid = USRC_API_Endpoints.Login(USRC_Details.GenericUSRCURL, UserID.replaceAll(" ", ""), Password.replaceAll(" ", ""));
		}
		
		String ContactDetailsParsed[][] = {{"UUID_NBR", ""},//index 0 and set below
				{"SSO_LOGIN_DESC", UserID.replaceAll(" ", "")},
				{"USER_PASSWORD_DESC", Password.replaceAll(" ", "")},
				{"FDM_STATUS", "F"} //index 3 below
				};
		
		if (fdx_login_fcl_uuid != null){
			Cookies = fdx_login_fcl_uuid[0];
			ContactDetailsParsed[0][1] = fdx_login_fcl_uuid[1];//save the uuid
			
			String Response = USRC_API_Endpoints.RecipientProfile(USRC_Details.GenericUSRCURL, Cookies);
			if (Response.contains("recipientProfileEnrollmentStatus\":\"ENROLLED")) {
				ContactDetailsParsed[3][1] = Response;//store all of the FDM details
			}
		}else {
			Assert.fail("Not able to login");
		}
		
		String FileName = Helper_Functions.DataDirectory + "\\TestingData.xls";
		boolean updatefile = Helper_Functions.WriteToExcel(FileName, "L" + Level, ContactDetailsParsed, 1);
		Helper_Functions.PrintOut("Contact Details: " + Arrays.toString(ContactDetailsParsed), true);
		if (!updatefile) {
			Assert.fail("Not able to update file.");
		}
	}
	
	@Test (dataProvider = "dp", enabled = true)
	public void Check_WCRV_Status(String Level, String UserID, String Password) {
		USRC_Data USRC_Details = USRC_Data.LoadVariables(Level);
		
		String Cookies = null, fdx_login_fcl_uuid[] = null;
		//get the cookies and the uuid of the user
		fdx_login_fcl_uuid = USRC_API_Endpoints.Login(USRC_Details.GenericUSRCURL, UserID.replaceAll(" ", ""), Password.replaceAll(" ", ""));
		
		//in case cannot login will check two of the generic other passwords
		if (fdx_login_fcl_uuid == null && Password.contentEquals("Test1234")){
			Password = "Test12345";
			fdx_login_fcl_uuid = USRC_API_Endpoints.Login(USRC_Details.GenericUSRCURL, UserID.replaceAll(" ", ""), Password.replaceAll(" ", ""));
		}else if (fdx_login_fcl_uuid == null && Password.contentEquals("Test12345")){
			Password = "Test1234";
			fdx_login_fcl_uuid = USRC_API_Endpoints.Login(USRC_Details.GenericUSRCURL, UserID.replaceAll(" ", ""), Password.replaceAll(" ", ""));
		}
		
		String ContactDetailsParsed[][] = {{"UUID_NBR", ""},//index 0 and set below
				{"SSO_LOGIN_DESC", UserID.replaceAll(" ", "")},
				{"USER_PASSWORD_DESC", Password.replaceAll(" ", "")},
				{"WCRV_ENABLED", "F"} //index 3 below
				};
		
		if (fdx_login_fcl_uuid != null){
			Cookies = fdx_login_fcl_uuid[0];
			ContactDetailsParsed[0][1] = fdx_login_fcl_uuid[1];//save the uuid
			
			PRDC_Data PRDC_D = PRDC_Data.LoadVariables(Level);
			ContactDetailsParsed[3][1] = WCRV_Access(PRDC_D.AccountsURL, Cookies);
		}else {
			Assert.fail("Not able to login");
		}
		
		String FileName = Helper_Functions.DataDirectory + "\\TestingData.xls";
		boolean updatefile = Helper_Functions.WriteToExcel(FileName, "L" + Level, ContactDetailsParsed, 1);
		Helper_Functions.PrintOut("Contact Details: " + Arrays.toString(ContactDetailsParsed), true);
		if (!updatefile) {
			Assert.fail("Not able to update file.");
		}
	}
	
	@Test (dataProvider = "dp", enabled = false)
	public void CheckIfUserInvalidLogin(String Level, String UserID, String Password) {
		String fdx_login_fcl_uuid[] = null;
		USRC_Data USRC_Details = USRC_Data.LoadVariables(Level);
		//get the cookies and the uuid of the user
		fdx_login_fcl_uuid = USRC_API_Endpoints.Login(USRC_Details.GenericUSRCURL, UserID, Password);
		if (fdx_login_fcl_uuid == null){
			Assert.fail(UserID);
		}
	}
	
	@Test (dataProvider = "dp", enabled = false)
	public void UpdateValue(String Level, String UserID, String Password) {
		String ContactDetailsParsed[][] = {{"SSO_LOGIN_DESC", UserID},
				{"WCRV_ENABLED", "T"}
				};
		
		String FileName = Helper_Functions.DataDirectory + "\\TestingData.xls";
		boolean updatefile = Helper_Functions.WriteToExcel(FileName, "L" + Level, ContactDetailsParsed, 0);
		Helper_Functions.PrintOut("Contact Details: " + Arrays.toString(ContactDetailsParsed), true);
		if (!updatefile) {
			Assert.fail("Not able to update file.");
		}
	}
	
	
	public String WCRV_Access(String Accounts_URL, String Cookies) {
		String AccountDetails = PRDC_API_Endpoints.Accounts_Call(Accounts_URL, Cookies);
		String WCRV_Access = "";
		if (AccountDetails.contains("displayRateSheetFlag\":true") && AccountDetails.contains("discountPricingFlag\":true") && AccountDetails.contains("accountCountryEnabledFlag\":true")){
			WCRV_Access = "T";
		}else if (!AccountDetails.contains("displayRateSheetFlag")){
			Helper_Functions.PrintOut("PRDC call did not return premission status.", false);
			WCRV_Access = "F";
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
