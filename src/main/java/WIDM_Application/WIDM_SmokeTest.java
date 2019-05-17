package WIDM_Application;

import static org.junit.Assert.assertThat;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;

import org.testng.annotations.Test;

import Data_Structures.Account_Data;
import Data_Structures.USRC_Data;
import Data_Structures.User_Data;
import Data_Structures.WIDM_Data;

import org.testng.annotations.DataProvider;
import org.testng.annotations.Listeners;
import org.hamcrest.CoreMatchers;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import java.util.Iterator;
import java.util.List;
import SupportClasses.Environment;
import SupportClasses.Helper_Functions;
import SupportClasses.WebDriver_Functions;
import USRC_Application.USRC_API_Endpoints;

@Listeners(SupportClasses.TestNG_TestListener.class)

public class WIDM_SmokeTest{
	static String LevelsToTest = "2";
	static String CountryList[][];
	
	@BeforeClass
	public void beforeClass() {
		Environment.SetLevelsToTest(LevelsToTest);
		CountryList = Environment.getCountryList("smoke");
	}
	
	@DataProvider (parallel = true)
	public static Iterator<Object[]> dp(Method m) {
		List<Object[]> data = new ArrayList<Object[]>();

		for (int i=0; i < Environment.LevelsToTest.length(); i++) {
			String Level = String.valueOf(Environment.LevelsToTest.charAt(i));
			int intLevel = Integer.parseInt(Level);
			switch (m.getName()) { //Based on the method that is being called the array list will be populated.
		    	case "WIDM_ResetPasswordSecret":
		    		User_Data User_Info[] = Environment.Get_UserIds(intLevel);
		    		for (int j = 0; j < CountryList.length; j++) {
		    			for (int k = 0; k < User_Info.length; k++) {
		    				if (User_Info[k].USER_ID.contains("WIDM")) {
		    					data.add( new Object[] {Level, CountryList[j][0], User_Info[k].USER_ID, User_Info[k].PASSWORD, User_Info[k].SECRET_ANSWER_DESC});
		    					break;
		    				}
		    			}
					}
		    		break;
				case "WIDM_ForgotUserID":
					data.add( new Object[] {Level, Helper_Functions.MyEmail});   //level, email
					break;
				case "WIDM_Registration":
				case "WIDM_Registration_ErrorMessages":
					for (int j=0; j < CountryList.length; j++) {
						data.add( new Object[] {Level, CountryList[j][0]});
					}
					break;
				case "WIDM_RegistrationEFWS":
					data.add( new Object[] {Level, CountryList[0][0]});
					break;
				case "WIDM_Login":
					//test login for email as user id and legacy user.
					User_Info = Environment.Get_UserIds(intLevel);
					boolean Email_As_UserID = false, Legacy_User = false;
		    		for (int j = 0; j < CountryList.length; j++) {
		    			for (int k = 0; k < User_Info.length; k++) {
		    				if (Legacy_User == true && Email_As_UserID == true) {
		    					break;
		    				}else if (User_Info[k].Address_Info.Country_Code.contentEquals(CountryList[j][0]) && User_Info[k].USER_ID.contains("@") && !Email_As_UserID) {
		    					data.add( new Object[] {Level, User_Info[k].USER_ID, User_Info[k].PASSWORD});
		    					Email_As_UserID = true;
		    				}else if (User_Info[k].Address_Info.Country_Code.contentEquals(CountryList[j][0]) && !User_Info[k].USER_ID.contains("@") && !Legacy_User) {
		    					data.add( new Object[] {Level, User_Info[k].USER_ID, User_Info[k].PASSWORD});
		    					Legacy_User = true;
		    				}
		    			}
					}
					break;
				case "WIDM_ResetPassword_Email":
					User_Info = Environment.Get_UserIds(intLevel);
		    		for (int j = 0; j < CountryList.length; j++) {
		    			for (int k = 0; k < User_Info.length; k++) {
		    				if (User_Info[k].Address_Info.Country_Code.contentEquals(CountryList[j][0]) && User_Info[k].EMAIL_ADDRESS.contains(Helper_Functions.MyEmail)) {
		    					data.add( new Object[] {Level, User_Info[k].USER_ID, User_Info[k].PASSWORD});
		    					break;
		    				}
		    			}
					}
					break;
				case "AAAUserCreate_Email_As_UserId":
					for (int j = 0; j < CountryList.length; j++) {
						Account_Data Account_Info = Helper_Functions.getAddressDetails(Level, CountryList[j][0]);
						Account_Info.UserId = "L" + Level + "Email" + Helper_Functions.CurrentDateTime() + Helper_Functions.getRandomString(2) + "@accept.com";
						Account_Info.Email = Account_Info.UserId;
						data.add( new Object[] {Level, Account_Info, "true"});
					}
					break;
			}
		}	
		return data.iterator();
	}
 
	@Test(dataProvider = "dp")
	public void WIDM_Registration(String Level, String CountryCode){
		try {
			String Address[] = Helper_Functions.LoadAddress(CountryCode);
			String UserName[] = Helper_Functions.LoadDummyName("WIDM", Level);
			String UserId = Helper_Functions.LoadUserID("L" + Level + "WIDM" + CountryCode);
			WIDM_Functions.WIDM_Registration(Address, UserName, UserId, Helper_Functions.MyEmail);
		}catch (Exception e) {
			Assert.fail(e.getMessage());
		}
	}
	
	@Test(dataProvider = "dp")
	public void WIDM_RegistrationEFWS(String Level, String CountryCode){
		try {
			String Address[] = Helper_Functions.LoadAddress(CountryCode);
			String UserName[] = Helper_Functions.LoadDummyName("WIDM", Level);
			String UserId = Helper_Functions.LoadUserID("L" + Level + "EFWS");
			WIDM_Functions.WIDM_RegistrationEFWS(Address, UserName, UserId);
		}catch (Exception e) {
			Assert.fail(e.getMessage());
		}
	}
	
	@Test(dataProvider = "dp")
	public void WIDM_ResetPasswordSecret(String Level, String CountryCode, String UserId, String Password, String SecretAnswer){
		try {
			//WIDM_Functions.ResetPasswordWIDM_Secret(CountryCode, UserId, Password + "5", SecretAnswer, false);   //once force password is in affect need to send different password
			WIDM_Functions.ResetPasswordWIDM_Secret(CountryCode, UserId, Password, SecretAnswer, false);
			Helper_Functions.PrintOut("Password Changed.");
		}catch (Exception e) {
			Assert.fail(e.getMessage());
		}
	}
	
	@Test(dataProvider = "dp")
	public void WIDM_ForgotUserID(String Level, String Email){
		try {
			WIDM_Functions.Forgot_User_WIDM("US", Email);
			Helper_Functions.PrintOut("Email Triggered.");
		}catch (Exception e) {
			Assert.fail(e.getMessage());
		}
	}
	
	@Test(dataProvider = "dp")
	public void WIDM_Login(String Level, String UserId, String Password){
		try {
			boolean login = WebDriver_Functions.Login(UserId, Password, "WIDM");
			Helper_Functions.PrintOut(UserId);
			Assert.assertTrue(login);
		}catch (Exception e) {
			Assert.fail(e.getMessage());
		}
	}
	
	@Test(dataProvider = "dp")
	public void WIDM_ResetPassword_Email(String Level, String UserId, String Password){
		try {
			WIDM_Functions.Reset_Password_WIDM_Email(UserId, Password);
			Helper_Functions.PrintOut("Email Triggered.");
		}catch (Exception e) {
			Assert.fail(e.getMessage());
		}
	}

	@Test(dataProvider = "dp")
	public void WIDM_Registration_ErrorMessages(String Level, String CountryCode){
		try {
			String Address[] = Helper_Functions.LoadAddress(CountryCode);
			String Name[] = Helper_Functions.LoadDummyName(CountryCode, Level);
			String UserID = Helper_Functions.LoadUserID("L" + Level + "T");
			WIDM_Functions.WIDM_Registration_ErrorMessages(Address, Name, UserID) ;
		}catch (Exception e) {
			e.printStackTrace();
			Assert.fail(e.getMessage());
		}
	}
	
	//Added for May 19 and July 19 CL
	@Test(dataProvider = "dp")
	public void AAAUserCreate_Email_As_UserId(String Level, Account_Data Account_Info, String Email_As_UserId){
		try {
			WIDM_Data WIDM_Info = WIDM_Data.LoadVariables(Level);
			Account_Data.Set_Dummy_Contact_Name(Account_Info);
			//Account_Data.Set_UserId(Account_Info, "L" + Level + "WIDMCreate" + Account_Info.Billing_Country_Code);
			String Response = WIDM_Endpoints.AAA_User_Create(WIDM_Info.EndpointUrl, Account_Info, Email_As_UserId);
			Account_Data.Print_High_Level_Details(Account_Info);
			assertThat(Response, CoreMatchers.containsString("<transactionId>"));
			//login with the user to confirm created successfully.
			USRC_Data USRC_Details = USRC_Data.LoadVariables(Environment.getInstance().getLevel());
			String[] fdx_login_fcl_uuid = USRC_API_Endpoints.Login(USRC_Details.GenericUSRCURL, Account_Info.UserId, Account_Info.Password);
			
			Helper_Functions.WriteUserToExcel(Account_Info.UserId, Account_Info.Password);
			
			String ReturnValue[] = new String[] {Account_Info.UserId, Account_Info.Password, fdx_login_fcl_uuid[1]};
			Helper_Functions.PrintOut(Arrays.toString(ReturnValue), false);
			
		}catch (Exception e) {
			Assert.fail(e.getMessage());
		}
	}
	
}