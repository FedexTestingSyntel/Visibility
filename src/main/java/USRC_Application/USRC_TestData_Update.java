package USRC_Application;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import org.junit.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.DataProvider;

import Data_Structures.ADMC_Data;
import Data_Structures.PRDC_Data;
import Data_Structures.USRC_Data;
import Data_Structures.User_Data;
import PRDC_Application.PRDC_API_Endpoints;
import SupportClasses.Environment;
import SupportClasses.Helper_Functions;
import org.testng.annotations.Test;

import ADMC_Application.ADMC_API_Endpoints;

public class USRC_TestData_Update {

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
	    	//USRC_Data.LoadVariables(strLevel);
	    	
	    	//load user ids since both of the below use that value
	    	User_Data UD[] = Environment.Get_UserIds(intLevel);
			switch (m.getName()) { //Based on the method that is being called the array list will be populated.	
			case "CheckLogin":
				for (int k = 0; k < UD.length; k++) {
    				if (UD[k].MIGRATION_STATUS.contentEquals("")) {
    					data.add(new Object[] {strLevel, UD[k].SSO_LOGIN_DESC, UD[k].USER_PASSWORD_DESC});
    				}
    			}
				break;
			case "CheckMigration":
				for (int k = 0; k < UD.length; k++) {
    				if (UD[k].MIGRATION_STATUS.contentEquals("")) {
    					data.add(new Object[] {strLevel, UD[k].SSO_LOGIN_DESC, UD[k].USER_PASSWORD_DESC});
    				}
    			}
				break;
			}//end switch MethodName
		}
	    System.out.println(data.size() + " scenarios.");
		return data.iterator();
	}

	@Test (dataProvider = "dp", enabled = true)
	public void CheckLogin(String Level, String UserID, String Password) {
		Environment.getInstance().setLevel(Level);
		USRC_Data USRC_Details = USRC_Data.LoadVariables(Level);
		
		String Cookies = null, fdx_login_fcl_uuid[] = null;
		//get the cookies and the uuid of the user
		fdx_login_fcl_uuid = USRC_API_Endpoints.Login(USRC_Details.GenericUSRCURL, UserID.replaceAll(" ", ""), Password.replaceAll(" ", ""));
		
		//in case cannot login will check two of the generic other passwords
		if (fdx_login_fcl_uuid == null){
			Password = "Test12345";
			fdx_login_fcl_uuid = USRC_API_Endpoints.Login(USRC_Details.GenericUSRCURL, UserID.replaceAll(" ", ""), Password.replaceAll(" ", ""));
		}
		if(fdx_login_fcl_uuid == null){
			Password = "Test1234";
			fdx_login_fcl_uuid = USRC_API_Endpoints.Login(USRC_Details.GenericUSRCURL, UserID.replaceAll(" ", ""), Password.replaceAll(" ", ""));
		}
		//this is the default key position to update the user table. Currently set to 1 for user id value.
		int keyPosition = 1;
		
		String Details[][] = {{"UUID_NBR", ""},//index 0 and set below
				{"SSO_LOGIN_DESC", UserID}, //set as the key position for making updates above. int keyPosition
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
				{"EMAIL_ADDRESS", ""}
				};
		
		if (fdx_login_fcl_uuid != null){
			Cookies = fdx_login_fcl_uuid[0];
			Details[0][1] = fdx_login_fcl_uuid[1];//save the uuid

			Details = App_Role_Info_Check(Level, Details, Cookies);

			Details = FDM_Access(Level, Details, USRC_Details.GenericUSRCURL, Cookies);

			String ContactDetailsResponse = USRC_API_Endpoints.ViewUserProfileWIDM(USRC_Details.ViewUserProfileWIDMURL, Cookies);
			Details = USRC_API_Endpoints.Parse_ViewUserProfileWIDM(ContactDetailsResponse, Details);
			
			Details = WCRV_Access(Level, Details, Cookies);
			
			Details = Migration_And_Manage_Check(Level, Details, Cookies);
		}else {
			//will save the current time of the failure.
			Details = new String[][]{{"SSO_LOGIN_DESC", UserID}, {"ERROR", Helper_Functions.CurrentDateTime(true)}};
			keyPosition = 0;
		}
		
		String FileName = Helper_Functions.DataDirectory + "\\TestingData.xls";
		boolean updatefile = Helper_Functions.WriteToExcel(FileName, "L" + Level, Details, keyPosition);
		Helper_Functions.PrintOut("Contact Details: " + Arrays.deepToString(Details), true);
		if (!updatefile) {
			Assert.fail("Not able to update file.");
		}else if (fdx_login_fcl_uuid == null) {
			Assert.fail("Not able to login");
		}
	}
	
	@Test (dataProvider = "dp", enabled = false)
	public void CheckMigration(String Level, String UserID, String Password) {
		Environment.getInstance().setLevel(Level);
		USRC_Data USRC_Details = USRC_Data.LoadVariables(Level);
		
		String Cookies = null, fdx_login_fcl_uuid[] = null;
		//get the cookies and the uuid of the user
		fdx_login_fcl_uuid = USRC_API_Endpoints.Login(USRC_Details.GenericUSRCURL, UserID.replaceAll(" ", ""), Password.replaceAll(" ", ""));
		
		//this is the default key position to update the user table. Currently set to 1 for user id value.
		int keyPosition = 1;
		
		String Details[][] = {{"UUID_NBR", ""},//index 0 and set below
				{"SSO_LOGIN_DESC", UserID},
				{"USER_PASSWORD_DESC", Password}
				};
		
		if (fdx_login_fcl_uuid != null){
			Cookies = fdx_login_fcl_uuid[0];
			Details[0][1] = fdx_login_fcl_uuid[1];//save the uuid
			Details = Migration_And_Manage_Check(Level, Details, Cookies);
		}else {
			//will save the current time of the failure.
			Details = new String[][]{{"SSO_LOGIN_DESC", UserID}, {"ERROR", Helper_Functions.CurrentDateTime(true)}};
			keyPosition = 0;
		}
		
		String FileName = Helper_Functions.DataDirectory + "\\TestingData.xls";
		boolean updatefile = Helper_Functions.WriteToExcel(FileName, "L" + Level, Details, keyPosition);
		Helper_Functions.PrintOut("Contact Details: " + Arrays.deepToString(Details), true);
		if (!updatefile) {
			Assert.fail("Not able to update file.");
		}else if (fdx_login_fcl_uuid == null) {
			Assert.fail("Not able to login");
		}
	}
	
	public String[][] FDM_Access(String Level, String Details[][], String USRCURL, String Cookies) {
		String Response = USRC_API_Endpoints.RecipientProfile(USRCURL, Cookies);
		Details = Arrays.copyOf(Details, Details.length + 1);
		
		if (Response.contains("recipientProfileEnrollmentStatus\":\"ENROLLED")) {
			Details[Details.length - 1] = new String[] {"FDM_STATUS", Response};//store all of the FDM details
		}else {
			Details[Details.length - 1] = new String[] {"FDM_STATUS", "F"};//not yet enrolled for FDM
		}
		return Details;
	}
	
	public String[][] WCRV_Access(String Level, String Details[][], String Cookies) {
		PRDC_Data PRDC_D = PRDC_Data.LoadVariables(Level);
		String AccountDetails = PRDC_API_Endpoints.Accounts_Call(PRDC_D.AccountsURL, Cookies);
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
		Details = Arrays.copyOf(Details, Details.length + 1);
		Details[Details.length - 1] = new String[] {"WCRV_ENABLED", WCRV_Access};
		return Details;
	}

	public String[][] App_Role_Info_Check(String Level, String Details[][], String Cookies){
		USRC_Data USRC_Details = USRC_Data.LoadVariables(Level);
		
		String AccountRetrievalRequest = USRC_API_Endpoints.AccountRetrievalRequest(USRC_Details.GenericUSRCURL, Cookies);
		
		String Parse[][] = {{"GFBO_ENABLED", "appName\":\"fclgfbo\",\"roleCode\":\""},
							{"WGRT_ENABLED", "appName\":\"fclrates\",\"roleCode\":\""}, 
							{"WDPA_ENABLED", "appName\":\"fclpickup\",\"roleCode\":\""}, 
							{"PASSKEY", "appName\":\"fclpasskey\",\"roleCode\":\""}
							};

		for (int j = 0; j < Parse.length; j++) {
			Details = Arrays.copyOf(Details, Details.length + 1);
			if (AccountRetrievalRequest.contains(Parse[j][1])) {
				Details[Details.length - 1] = new String[] {Parse[j][0], "T"};
			}else {
				Details[Details.length - 1] = new String[] {Parse[j][0], "F"};
			}
		}
		Details = Arrays.copyOf(Details, Details.length + 1);
		Details[Details.length - 1] = new String[] {"ACCOUNT_NUMBER", USRC_API_Endpoints.Parse_AccountRetrievalRequest_AccountNumber(AccountRetrievalRequest)};

	
		return Details;
	}
	
	public String[][] Migration_And_Manage_Check(String Level, String Details[][], String Cookies){
		ADMC_Data ADMC_Details = ADMC_Data.LoadVariables(Level);
		
		String AccountRetrievalRequest = ADMC_API_Endpoints.RoleAndStatus(ADMC_Details.RoleAndStatusURL, Cookies);
		
		//Example: {"successful": true,"output": {"migrationStatus": true,"userType": "COMPANY_ADMIN"}}
		//         {"output":{"migrationStatus":false,"userType":"NON_MANAGED"},"successful":true}
		
		String Parse[][] = {{"MIGRATION_STATUS", "migrationStatus\":", ","},
							{"USER_TYPE", "\"userType\":\"", "\""}, 
							};

		for (int j = 0; j < Parse.length; j++) {
			Details = Arrays.copyOf(Details, Details.length + 1);
			if (AccountRetrievalRequest.contains(Parse[j][1])) {
				//substring staring with the variable in question
				String temp = AccountRetrievalRequest.substring(AccountRetrievalRequest.indexOf(Parse[j][1]) + Parse[j][1].length(), AccountRetrievalRequest.length());
				String value_needed = temp.substring(0, temp.indexOf(Parse[j][2]));
				Details[Details.length - 1] = new String[] {Parse[j][0], value_needed};
			}else {
				Details[Details.length - 1] = new String[] {Parse[j][0], "NA"};
			}
		}
		
		if (Details[Details.length - 1][1].contains("NON_MANAGED") && Details[Details.length - 2][1].contains("false") ) {
			Details[Details.length - 2][1] = "F";
		}else if (!Details[Details.length - 1][1].contains("NON_MANAGED") && Details[Details.length - 2][1].contains("true") ) {
			Details[Details.length - 2][1] = "WADM";
		}else if (!Details[Details.length - 1][1].contains("NON_MANAGED") && Details[Details.length - 2][1].contains("false") ) {
			Details[Details.length - 2][1] = "IPAS";
		}
	
		return Details;
	}
	
}