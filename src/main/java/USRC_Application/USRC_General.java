package USRC_Application;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import org.junit.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Listeners;
import Data_Structures.Account_Data;
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
@Listeners(SupportClasses.TestNG_TestListener.class)

public class USRC_General {

	static String LevelsToTest = "2"; //Can but updated to test multiple levels at once if needed. Setting to "23" will test both level 2 and level 3.

	@BeforeClass
	public void beforeClass() {
		Environment.SetLevelsToTest(LevelsToTest);
		Helper_Functions.MyEmail = "accept@fedex.com";
	}
	
	@DataProvider (parallel = true)
	public Iterator<Object[]> dp(Method m) {
	    List<Object[]> data = new ArrayList<>();
	    
	    for (int i = 0; i < Environment.LevelsToTest.length(); i++) {
	    	String strLevel = "" + Environment.LevelsToTest.charAt(i);
	    	int intLevel = Integer.parseInt(strLevel);
	    	//loading the OAuth token and having all of the variables set.
	    	USRC_Data.LoadVariables(strLevel);
	    	User_Data User_Info[];
			switch (m.getName()) { //Based on the method that is being called the array list will be populated.
			case "CreateUsers":
				for (int j = 0 ; j < 1; j++) {
					data.add(new Object[] {strLevel, j});
				}
				break;
			case "CreateUsers_User_Data":
				User_Info = Environment.Get_UserIds(intLevel);
				int counter = 0;
	    		for (int k = 0; k < User_Info.length; k++) {
	    			if (User_Info[k].Address_Info.Country_Code.contentEquals("US") && !User_Info[k].SECRET_ANSWER_DESC.contentEquals("")) {
	    				data.add( new Object[] {strLevel, User_Info[k]});
	    				counter++;
	    				if (counter > 10) {
	    					break;
	    				}
	    			}
	    		}
				break;
			case "CreateUsers_AddressDetails":
				/////////////currently not working for non US
				ArrayList<String[]> AddressDetails = new ArrayList<String[]>();
				AddressDetails = Helper_Functions.getExcelData(Helper_Functions.DataDirectory + "\\AddressDetails.xls",  "Countries");//load the relevant information from excel file.
				String Phone = "9011111111";
				for (int j = 0 ; j < AddressDetails.size(); j++) {
					String CountryList[] = AddressDetails.get(j);
					if ("US".contains(CountryList[6])) {
						if (CountryList[5].contains(" ")){
							CountryList[5] = CountryList[5].replaceAll(" ", "");
						}
						//[Address_Line_1, Address_Line_2, City, State, State_Code, Zip, Country_Code, Region, Country]
						String Email = Helper_Functions.getRandomString(10) + "@fedex.com";
						String ContactDetails[] = new String[]{"FirstName", "", "LastName", Phone, Email, CountryList[0], CountryList[1], CountryList[3], CountryList[4], CountryList[5], CountryList[6], ""};

						for (int count = 0; count < 1; count++) {
							data.add(new Object[] {strLevel, ContactDetails});
						}
						break;
					}		
					
				}
				break;		
			case "CheckLogin":
				//loading the OAuth token and having all of the variables set.
				PRDC_Data.LoadVariables(strLevel);
				User_Info = Environment.Get_UserIds(intLevel);
				for (int k = 0; k < User_Info.length; k++) {
    				if (User_Info[k].EMAIL_ADDRESS.contentEquals("")) {
    					data.add(new Object[] {strLevel, User_Info[k].USER_ID, User_Info[k].PASSWORD});
    				}
    			}
				break;
			case "Check_FDM_Status":
				//loading the OAuth token and having all of the variables set.
				User_Info = Environment.Get_UserIds(intLevel);
				for (int k = 0; k < User_Info.length; k++) {
					if(User_Info[k].FDM_STATUS.contentEquals("")) {
    					data.add(new Object[] {strLevel, User_Info[k].USER_ID, User_Info[k].PASSWORD});
    				}
    			}
				break;
			case "Create_Users_With_Account":
				String AccountsNumbers[] = new String[] {"267751013", "248337451", "231965173", "610957803", "913640888", "181517042", "341091594", "233986259", "641445983", "642335928", "191500563", "166529131", "231521232", "204180474", "119726441", "215894215", "343914865", "640361069", "194242689", "252289259", "246824118", "222105811", "259846196", "349054205", "126653476", "163277123", "233336513", "261289474", "175428445", "198132543", "181083565", "269480602", "213403788", "741364522", "645686624", "252963669", "131009666", "168477929", "261485095", "641181129", "164136604", "296436062", "154630287", "128420924", "265681735", "207924644", "221572629", "192332605", "429963761", "698000481", "111146730", "193195946", "123589327", "227735325"};
				Account_Data Account_Info = Environment.getAddressDetails(strLevel, "US");
				for (int k = 0; k < AccountsNumbers.length; k++) {
					data.add(new Object[] {strLevel, Account_Info, AccountsNumbers[k]});
    			}
				break;
			case  "UpdateValue":
			case "Check_WCRV_Status":
				//loading the OAuth token and having all of the variables set.
				User_Info = Environment.Get_UserIds(intLevel);
				for (int k = 0; k < User_Info.length; k++) {
					if(User_Info[k].WCRV_ENABLED.contentEquals("Enabled")) {
    					data.add(new Object[] {strLevel, User_Info[k].USER_ID, User_Info[k].PASSWORD});
    				}
    			}
				break;
			case "CheckIfUserInvalidLogin":
				User_Info = Environment.Get_UserIds(intLevel);
				for (int k = 0; k < User_Info.length; k++) {
    				if (User_Info[k].FIRST_NM.contentEquals("")) {
    					data.add(new Object[] {strLevel, User_Info[k].USER_ID, User_Info[k].PASSWORD});
    				}
    			}
				break;
				
			case "UpdateUserContactInformation":
				User_Info = Environment.Get_UserIds(intLevel);
				for (int k = 0; k < User_Info.length; k++) {
    				if (User_Info[k] != null && User_Info[k].USER_ID.contentEquals("L3FCLUse081616")) {
    					data.add(new Object[] {strLevel, User_Info[k]});
    					break;
    				}
    			}
				break;
			case "Testing_API_Login":
				User_Info = Environment.Get_UserIds(intLevel);
				data.add(new Object[] {strLevel, User_Info[0]});
			}//end switch MethodName
		}
	    
	   // while (data.size() > 300) {
	 //   	data.remove(data.size() - 1);
	  //  }
	    System.out.println(data.size() + " scenarios.");
		return data.iterator();
	}
	
	@Test (dataProvider = "dp", enabled = true)
	public void Testing_API_Login(String Level, User_Data User_Info) {
		String Response = USRC_API_Endpoints.Login_API_Load_Cookies(User_Info.USER_ID, User_Info.PASSWORD);
		Helper_Functions.PrintOut(Response);
	}
	
	@Test (dataProvider = "dp", enabled = false)
	public void CreateUsers_User_Data(String Level, User_Data User_Info) {
		USRC_Data USRC_Details = USRC_Data.LoadVariables(Level);
		String UUID = null, fdx_login_fcl_uuid[] = {"","", ""};
		//1 - Login, get cookies and uuid
		User_Info.USER_ID = Helper_Functions.LoadUserID("L" + Level + User_Info.Address_Info.Country_Code);
		User_Info.EMAIL_ADDRESS = Helper_Functions.getRandomString(10) + "@accept.com";
		User_Data.Set_Dummy_Contact_Name(User_Info, User_Info.Address_Info.Country_Code, Level);
		//create the new user
		String Response = USRC_API_Endpoints.NewFCLUser(USRC_Details.REGCCreateNewUserURL, User_Info);
			
		//check to make sure that the userid was created.
		assertThat(Response, containsString("successful\":true"));
			
		//get the cookies and the uuid of the new user
		fdx_login_fcl_uuid = USRC_API_Endpoints.Login(USRC_Details.GenericUSRCURL, User_Info.USER_ID, User_Info.PASSWORD);
		UUID = fdx_login_fcl_uuid[1];
		String Results[] = new String[] {User_Info.USER_ID,  User_Info.PASSWORD, UUID};
		Helper_Functions.PrintOut(Arrays.toString(Results), false);
		Helper_Functions.WriteUserToExcel(User_Info.USER_ID,  User_Info.PASSWORD);
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
		String UserID = Helper_Functions.LoadUserID("L" + Level + ContactDetails[10]);
		String Password = "Test1234";
			
		//create the new user

		String Response = USRC_API_Endpoints.NewFCLUser(USRC_Details.REGCCreateNewUserURL, ContactDetails, UserID, Password);
			
		//check to make sure that the userid was created.
		assertThat(Response, containsString("successful\":true"));
			
		//get the cookies and the uuid of the new user
		fdx_login_fcl_uuid = USRC_API_Endpoints.Login(USRC_Details.GenericUSRCURL, UserID, Password);
		UUID = fdx_login_fcl_uuid[1];
			
		Helper_Functions.PrintOut(UserID + "/" + Password + "--" + UUID, false);
		Helper_Functions.WriteUserToExcel(UserID, Password);
	}
		
	@Test (dataProvider = "dp", enabled = true)
	public void Create_Users_With_Account(String Level, Account_Data Account_Info, String Account_Number) {
		USRC_Data USRC_Details = USRC_Data.LoadVariables(Level);
		
		//Account_Data Account_Info = Account_Lookup.Account_DataAccountDetails("642893505", Level, "FX");
		//1 - Login, get cookies and uuid
		Account_Info.Email = "accept@fedex.com";
		Account_Info.User_Info.USER_ID = "L" + USRC_Details.Level + "Account" + Account_Number;
		Account_Data.Set_Dummy_Contact_Name(Account_Info);
		
		String Response = USRC_API_Endpoints.NewFCLUser(USRC_Details.REGCCreateNewUserURL, Account_Info);
			
		//check to make sure that the userid was created.
		assertThat(Response, containsString("successful\":true"));
		String Results[] = new String[] {Account_Info.User_Info.USER_ID, Account_Info.User_Info.PASSWORD};
		Helper_Functions.WriteUserToExcel(Account_Info.User_Info.USER_ID, Account_Info.User_Info.PASSWORD);
		Helper_Functions.PrintOut(Arrays.toString(Results), false);
	}
	
	@Test (dataProvider = "dp", enabled = false)
	public void UpdateUserContactInformation(String Level, User_Data User_Info) {
		USRC_Data USRC_Details = USRC_Data.LoadVariables(Level);
		String fdx_login_fcl_uuid[] = {"","", ""};
		//1 - Login, get cookies and uuid
		fdx_login_fcl_uuid = USRC_API_Endpoints.Login(USRC_Details.GenericUSRCURL, User_Info.USER_ID, User_Info.PASSWORD);
		
		User_Info.FIRST_NM = User_Info.FIRST_NM + "Edit";
		String Response = USRC_API_Endpoints.UpdateUserContactInformationWIDM(USRC_Details.UpdateUserContactInformationWIDMURL, User_Info, fdx_login_fcl_uuid[0]);
			
		//check to make sure that the userid was created.
		assertThat(Response, containsString("successful\":true"));
			
	}
	
	@Test (dataProvider = "dp", enabled = false)
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
		
		String Details[][] = {{"UUID_NBR", ""},//index 0 and set below
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
				{"FDM_STATUS", "F"}, //index 12 below
				{"EMAIL_ADDRESS", ""}
				};
		
		if (fdx_login_fcl_uuid != null){
			Cookies = fdx_login_fcl_uuid[0];
			Details[0][1] = fdx_login_fcl_uuid[1];//save the uuid

			Details = App_Role_Info_Check(Level, Details, Cookies);

			String Response = USRC_API_Endpoints.RecipientProfile(USRC_Details.GenericUSRCURL, Cookies);
			if (Response.contains("recipientProfileEnrollmentStatus\":\"ENROLLED")) {
				Details[12][1] = Response;//store all of the FDM details
			}

			String ContactDetailsResponse = USRC_API_Endpoints.ViewUserProfileWIDM(USRC_Details.ViewUserProfileWIDMURL, Cookies);
			Details = USRC_API_Endpoints.Parse_ViewUserProfileWIDM(ContactDetailsResponse, Details);
			
			Details = WCRV_Access(Level, Details, Cookies);
		}else {
			//will save the current time of the failure.
			Details = new String[][]{{"SSO_LOGIN_DESC", UserID},
					{"ERROR", Helper_Functions.CurrentDateTime(true)}};
		}
		
		String FileName = Helper_Functions.DataDirectory + "\\TestingData.xls";
		boolean updatefile = Helper_Functions.WriteToExcel(FileName, "L" + Level, Details, 1);
		Helper_Functions.PrintOut("Contact Details: " + Arrays.toString(Details), true);
		if (!updatefile) {
			Assert.fail("Not able to update file.");
		}else if (fdx_login_fcl_uuid == null) {
			Assert.fail("Not able to login");
		}
	}
	
	public String[][] WCRV_Access(String Level, String Details[][], String Cookies) {
		PRDC_Data PRDC_D = PRDC_Data.LoadVariables(Level);
		String AccountDetails = PRDC_API_Endpoints.PRDC_Accounts_Call(PRDC_D.AccountsURL, Cookies);
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
	
}
