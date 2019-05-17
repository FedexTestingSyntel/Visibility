package WIDM_Application;

import static org.junit.Assert.assertThat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.lang.reflect.Method;
import org.hamcrest.CoreMatchers;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Listeners;
import org.testng.annotations.Test;
import Data_Structures.Account_Data;
import Data_Structures.USRC_Data;
import Data_Structures.User_Data;
import Data_Structures.WIDM_Data;
import SupportClasses.Environment;
import SupportClasses.Helper_Functions;
import USRC_Application.USRC_API_Endpoints;

@Listeners(SupportClasses.TestNG_TestListener.class)

public class WIDM_SOAPClient {
	static String LevelsToTest = "3";
	static String CountryList[][];
	
	@BeforeClass
	public void beforeClass() {
		Environment.SetLevelsToTest(LevelsToTest);
		
		CountryList = Environment.getCountryList("smoke");
		//CountryList = Environment.getCountryList("JP");
		Helper_Functions.MyEmail = "OtherEmail@accept.com";
	}
	
	@DataProvider (parallel = true)
	public static Iterator<Object[]> dp(Method m) {
		List<Object[]> data = new ArrayList<Object[]>();

		for (int i=0; i < Environment.LevelsToTest.length(); i++) {
			String Level = String.valueOf(Environment.LevelsToTest.charAt(i));
			int intLevel = Integer.parseInt(Level);
			
			User_Data User_Info[];
			
			String EmailUserIdFlag = "true", NotEmailUserIdFlag = "false", IgnoreEmailUserIdFlag = null;
			
			switch (m.getName()) { //Based on the method that is being called the array list will be populated.
				case "AAAUserCreate":
					for (int j = 0; j < CountryList.length; j++) {
						Account_Data Account_Info = Helper_Functions.getAddressDetails(Level, CountryList[j][0]);
						data.add( new Object[] {Level, Account_Info});
					}
					break;
				case "AAAUserCreate_Email_As_UserId":
				case "AAAUserCreate_Email_As_UserId_Mismatch":
					for (int j = 0; j < CountryList.length; j++) {
						Account_Data Account_Info = Helper_Functions.getAddressDetails(Level, CountryList[j][0]);
						data.add( new Object[] {Level, Account_Info, EmailUserIdFlag});
					}
					break;
				case "AAAUserUpdate":
					User_Info = Environment.Get_UserIds(intLevel);
		    		for (int j = 0; j < CountryList.length; j++) {
		    			for (int k = 0; k < User_Info.length; k++) {
		    				if (User_Info[k].Address_Info.Country_Code.contentEquals(CountryList[j][0]) && !User_Info[k].SECRET_ANSWER_DESC.contentEquals("") && User_Info[k].USER_TYPE.contentEquals("NON_MANAGED")) {
		    					data.add( new Object[] {Level, User_Info[k]});
		    					break;
		    				}
		    			}
					}
		    		break;
				case "AAAUserUpdate_Email_As_UserId":
					User_Info = Environment.Get_UserIds(intLevel);
		    		for (int j = 0; j < CountryList.length; j++) {
		    			for (int k = 0; k < User_Info.length; k++) {
		    				if (User_Info[k].USER_ID.contains("@") && User_Info[k].Address_Info.Country_Code.contentEquals(CountryList[j][0]) && !User_Info[k].SECRET_ANSWER_DESC.contentEquals("") && User_Info[k].USER_TYPE.contentEquals("NON_MANAGED")) {
		    					data.add( new Object[] {Level, User_Info[k]});
		    					break;
		    				}
		    			}
					}
		    		break;
			}
		}	
		return data.iterator();
	}

	@Test(dataProvider = "dp")
	public void AAAUserCreate(String Level, Account_Data Account_Info){
		try {
			WIDM_Data WIDM_Info = WIDM_Data.LoadVariables(Level);
			Account_Data.Set_UserId(Account_Info, "L" + Level + "WIDMCreate" + Account_Info.Billing_Address_Info.Country_Code);
			String Response = WIDM_Endpoints.AAA_User_Create(WIDM_Info.EndpointUrl, Account_Info, null);
			Account_Data.Print_High_Level_Details(Account_Info);
			assertThat(Response, CoreMatchers.containsString("<transactionId>"));
			Helper_Functions.PrintOut(Response);
			Helper_Functions.WriteUserToExcel(Account_Info.User_Info.USER_ID, Account_Info.User_Info.PASSWORD);
		}catch (Exception e) {
			Assert.fail(e.getMessage());
		}
	}
	
	@Test(dataProvider = "dp")
	public void AAAUserCreate_Email_As_UserId_Mismatch(String Level, Account_Data Account_Info, String Email_As_UserId){
		try {
			WIDM_Data WIDM_Info = WIDM_Data.LoadVariables(Level);
			Account_Data.Set_Dummy_Contact_Name(Account_Info);
			Account_Info.User_Info.USER_ID = "L" + Level + "Email" + Helper_Functions.CurrentDateTime() + Helper_Functions.getRandomString(2) + "@accept.com";
			Account_Info.Email = "other@email.com";
			String Response = WIDM_Endpoints.AAA_User_Create(WIDM_Info.EndpointUrl, Account_Info, Email_As_UserId);
			Account_Data.Print_High_Level_Details(Account_Info);
			assertThat(Response, CoreMatchers.containsString(WIDM_Error_Codes.WIDM_Error_Code("98")));
			Helper_Functions.PrintOut(Response);
			Helper_Functions.WriteUserToExcel(Account_Info.User_Info.USER_ID, Account_Info.User_Info.PASSWORD);
		}catch (Exception e) {
			Assert.fail(e.getMessage());
		}
	}
	
	@Test(dataProvider = "dp")
	public void AAAUserCreate_Email_As_UserId(String Level, Account_Data Account_Info, String Email_As_UserId){
		try {
			WIDM_Data WIDM_Info = WIDM_Data.LoadVariables(Level);
			Account_Data.Set_Dummy_Contact_Name(Account_Info);
			Account_Info.User_Info.USER_ID = "L" + Level + "Email" + Helper_Functions.CurrentDateTime() + Helper_Functions.getRandomString(2) + "@accept.com";
			Account_Info.Email = Account_Info.User_Info.USER_ID;
			String Response = WIDM_Endpoints.AAA_User_Create(WIDM_Info.EndpointUrl, Account_Info, Email_As_UserId);
			Account_Data.Print_High_Level_Details(Account_Info);
			assertThat(Response, CoreMatchers.containsString("<transactionId>"));
			//login with the user to confirm created successfully.
			USRC_Data USRC_Details = USRC_Data.LoadVariables(Environment.getInstance().getLevel());
			String[] fdx_login_fcl_uuid = USRC_API_Endpoints.Login(USRC_Details.GenericUSRCURL, Account_Info.User_Info.USER_ID, Account_Info.User_Info.PASSWORD);
			
			Helper_Functions.WriteUserToExcel(Account_Info.User_Info.USER_ID, Account_Info.User_Info.PASSWORD);
			
			String ReturnValue[] = new String[] {Account_Info.User_Info.USER_ID, Account_Info.User_Info.PASSWORD, fdx_login_fcl_uuid[1]};
			Helper_Functions.PrintOut(Arrays.toString(ReturnValue), false);
			
		}catch (Exception e) {
			Assert.fail(e.getMessage());
		}
	}
	
	@Test(dataProvider = "dp")
	public void AAAUserUpdate(String Level, User_Data User_Info){
		try {
			WIDM_Data WIDM_Info = WIDM_Data.LoadVariables(Level);
			User_Data.Print_High_Level_Details(User_Info);
			User_Data.Set_Dummy_Contact_Name(User_Info, User_Info.Address_Info.Country_Code, Level);
			String Response = WIDM_Endpoints.AAA_User_Update(WIDM_Info.EndpointUrl, User_Info, null);
			User_Data.Print_High_Level_Details(User_Info);
			assertThat(Response, CoreMatchers.containsString("<transactionId>"));
		}catch (Exception e) {
			Assert.fail(e.getMessage());
		}
	}
	
	@Test(dataProvider = "dp")
	public void AAAUserUpdate_Email_As_UserId(String Level, User_Data User_Info){
		try {
			WIDM_Data WIDM_Info = WIDM_Data.LoadVariables(Level);
			User_Data.Print_High_Level_Details(User_Info);
			User_Data.Set_Dummy_Contact_Name(User_Info, User_Info.Address_Info.Country_Code, Level);
			String Response = WIDM_Endpoints.AAA_User_Update(WIDM_Info.EndpointUrl, User_Info, null);
			User_Data.Print_High_Level_Details(User_Info);
			assertThat(Response, CoreMatchers.containsString("<transactionId>"));
		}catch (Exception e) {
			Assert.fail(e.getMessage());
		}
	}
}
