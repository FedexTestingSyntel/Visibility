package WFCL_Application;

import org.testng.annotations.Test;
import Data_Structures.Account_Data;
import Data_Structures.Enrollment_Data;
import Data_Structures.Tax_Data;
import Data_Structures.User_Data;
import org.testng.annotations.BeforeClass;
import org.testng.Assert;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Listeners;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import SupportClasses.*;

@Listeners(SupportClasses.TestNG_TestListener.class)

public class WFCL_New{
	static String LevelsToTest = "3";  
	static String CountryList[][]; 

	@BeforeClass
	public void beforeClass() {
		Environment.SetLevelsToTest(LevelsToTest);
		CountryList = Environment.getCountryList("US");
		//CountryList = new String[][]{{"JP", "Japan"}, {"MY", "Malaysia"}, {"SG", "Singapore"}, {"AU", "Australia"}, {"NZ", "New Zealand"}, {"HK", "Hong Kong"}, {"TW", "Taiwan"}, {"TH", "Thailand"}};
		//CountryList = new String[][]{{"SG", "Singapore"}, {"AU", "Australia"}, {"NZ", "New Zealand"}, {"HK", "Hong Kong"}};
		//CountryList = Environment.getCountryList("JP");
		CountryList = Environment.getCountryList("SA");
		//CountryList = Environment.getCountryList("high");
		//Helper_Functions.MyEmail = "accept@fedex.com";
		//CountryList = new String[][]{{"AU", ""}, {"JP", ""}};
	}
	 
	@DataProvider (parallel = true)
	public static Iterator<Object[]> dp(Method m) {
		List<Object[]> data = new ArrayList<Object[]>();

		for (int i=0; i < Environment.LevelsToTest.length(); i++) {
			String Level = String.valueOf(Environment.LevelsToTest.charAt(i));
			Account_Data Account_Info = null;
			int intLevel = Integer.parseInt(Level);
			//int intLevel = Integer.parseInt(Level);
			switch (m.getName()) { //Based on the method that is being called the array list will be populated.
		    	case "CreditCardRegistrationEnroll":
		    		Enrollment_Data ED[] = Environment.getEnrollmentDetails(intLevel);
		    		for (int j = 0; j < CountryList.length; j++) {
		    			for (Enrollment_Data Enrollment: ED) {
			    			if (Enrollment.COUNTRY_CODE.contentEquals(CountryList[j][0]) ) {   //&& Enrollment.ENROLLMENT_ID.contentEquals("cc16323414")
			    				Account_Info = Environment.getAddressDetails(Level, CountryList[j][0]);
			    				Account_Info.Account_Type = "P";//Personal account
			    				data.add( new Object[] {Level, Enrollment, Account_Info, Environment.getTaxDetails(CountryList[j][0])});
			    				break;
			    			}
		    			}
					}
		    		break;
		    	case "UserRegistration_Account_Data_Captcha":
		    	case "UserRegistration_Account_Data":
		    		for (int j = 0; j < CountryList.length; j++) {
		    			Account_Info = Helper_Functions.getAddressDetails(Level, CountryList[j][0]);
		    			data.add( new Object[] {Level, Account_Info});
					}
		    		break;
		    	case "AccountRegistration_Admin":
		    		if (Level == "7") {
		    			break;
		    		}
		    		for (int j = 0; j < CountryList.length; j++) {
		    			Account_Info = Helper_Functions.getFreshAccount(Level, CountryList[j][0]);
					}
		    		data.add( new Object[] {Level, Account_Info});
		    		break;
		    	case "AccountRegistration_INET":
		    	case "AccountRegistration_WDPA":
		    		for (int j = 0; j < CountryList.length; j++) {
		    			Account_Info = Helper_Functions.getFreshAccount(Level, CountryList[j][0]);
		    			data.add( new Object[] {Level, Account_Info});
					}
		    		break;
		    	case "AccountRegistration_Take_Account_Online":
		    		
		    		break;
		    	case "Forgot_User_Email":
		    		for (int j = 0; j < CountryList.length; j++) {
			    		data.add( new Object[] {Level, CountryList[j][0], Helper_Functions.MyEmail});
					}
		    		break;
		    	case "Password_Reset_Secret":
		    		User_Data UD[] = Environment.Get_UserIds(intLevel);
		    		for (int j = 0; j < CountryList.length; j++) {
		    			for (int k = 0; k < UD.length; k++) {
		    				if (UD[k].COUNTRY_CD.contentEquals(CountryList[j][0]) && !UD[k].SECRET_ANSWER_DESC.contentEquals("")) {
		    					data.add( new Object[] {Level, UD[k], UD[k].USER_PASSWORD_DESC + "A"});
		    					break;
		    				}
		    			}
					}
		    		break;
		    	case "Reset_Password_Email":
		    		UD = Environment.Get_UserIds(intLevel);
		    		for (int j = 0; j < CountryList.length; j++) {
		    			for (int k = 0; k < UD.length; k++) {
		    				//make sure have your email address set as the Helper_Functions.MyEmail
		    				if (UD[k].COUNTRY_CD.contentEquals(CountryList[j][0]) && UD[k].EMAIL_ADDRESS.contentEquals(Helper_Functions.MyEmail)) {
		    					data.add( new Object[] {Level, UD[k]});
		    					break;
		    				}
		    			}
					}
		    		break;
		    	case "FCLA_WADM_Invitaiton":
		    		UD = Environment.Get_UserIds(intLevel);
		    		for (int j = 0; j < CountryList.length; j++) {
		    			for (int k = 0; k < UD.length; k++) {
		    				//make sure have your email address set as the Helper_Functions.MyEmail
		    				if (UD[k].COUNTRY_CD.contentEquals(CountryList[j][0]) && UD[k].PASSKEY.contains("T")) {
		    					data.add( new Object[] {Level, UD[k]});
		    					break;
		    				}
		    			}
					}
		    		break;
		    	case "Link_Account_To_User":
		    		UD = Environment.Get_UserIds(intLevel);
		    		for (int j = 0; j < CountryList.length; j++) {
		    			for (int k = 30; k < UD.length; k++) {
		    				if (UD[k].COUNTRY_CD.contentEquals(CountryList[j][0]) && UD[k].PASSKEY.contains("F")) {
		    					Account_Info = Helper_Functions.getFreshAccount(Level, CountryList[j][0]);
		    					data.add( new Object[] {Level, UD[k], Account_Info});
		    					break;
		    				}
		    			}
					}
		    		break;
		    	case "WFCL_GFBO_Registration":
		    		UD = Environment.Get_UserIds(intLevel);
		    		for (int j = 0; j < CountryList.length; j++) {
		    			for (int k = 30; k < UD.length; k++) {
		    				if (UD[k].COUNTRY_CD.contentEquals(CountryList[j][0]) && UD[k].PASSKEY.contains("F") && UD[k].GFBO_ENABLED.contains("F")) {
		    					Account_Info = Helper_Functions.getFreshAccount(Level, CountryList[j][0]);
		    					data.add( new Object[] {Level, UD[k], Account_Info});
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
	public void CreditCardRegistrationEnroll(String Level, Enrollment_Data Enrollment_Info, Account_Data Account_Info, Tax_Data Tax_Info) {
		try {
			Account_Data.Print_Account_Address(Account_Info);
			Account_Data.Set_Credit_Card(Account_Info, Environment.getCreditCardDetails(Level, "V"));

			Account_Data.Set_UserId(Account_Info, "L" + Level + Account_Info.Billing_Country_Code + Enrollment_Info.ENROLLMENT_ID + "CC");
			Account_Data.Set_Dummy_Contact_Name(Account_Info);

			String Result[] = WFCL_Functions_UsingData.CreditCardRegistrationEnroll(Enrollment_Info, Account_Info, Tax_Info);
			Helper_Functions.PrintOut(Arrays.toString(Result), false);
		}catch (Exception e) {
			Assert.fail(e.getMessage());
		}
	}

	@Test(dataProvider = "dp", priority = 1)
	public void AccountRegistration_INET(String Level, Account_Data Account_Info){
		try {
			Account_Data.Print_Account_Address(Account_Info);
			Account_Data.Set_Dummy_Contact_Name(Account_Info);
			Account_Data.Set_Account_Nickname(Account_Info, Account_Info.Account_Number + "_" + Account_Info.Billing_Country_Code);
			Account_Data.Set_UserId(Account_Info, "L" + Level + "Inet" + Account_Info.Billing_Country_Code);
			//create user id and link to account number.
			Account_Info = WFCL_Functions_UsingData.Account_Linkage(Account_Info);
			//register the user id to INET
			
			WFCL_Functions_UsingData.INET_Registration(Account_Info);
			String Result[] = new String[] {Account_Info.UserId, Account_Info.Account_Number, Account_Info.UUID};
			Helper_Functions.PrintOut(Arrays.toString(Result), false);
		}catch (Exception e) {
			Assert.fail(e.getMessage());
		}
	}
	
	@Test(dataProvider = "dp", priority = 1)
	public void AccountRegistration_Take_Account_Online(String Level, Account_Data Account_Info){
		try {
			Account_Data.Print_Account_Address(Account_Info);
			Account_Data.Set_Dummy_Contact_Name(Account_Info);
			Account_Data.Set_Account_Nickname(Account_Info, Account_Info.Account_Number + "_" + Account_Info.Billing_Country_Code);
			Account_Data.Set_UserId(Account_Info, "L" + Level + "AccountOnline" + Account_Info.Billing_Country_Code);
			//create user id and link to account number.
			Account_Info = WFCL_Functions_UsingData.Account_Linkage(Account_Info);

			String Result[] = new String[] {Account_Info.UserId, Account_Info.Account_Number, Account_Info.UUID};
			Helper_Functions.PrintOut(Arrays.toString(Result), false);
		}catch (Exception e) {
			Assert.fail(e.getMessage());
		}
	}
	
	@Test(dataProvider = "dp")
	public void UserRegistration_Account_Data(String Level, Account_Data Account_Info) {
		try {
			Account_Data.Print_Account_Address(Account_Info);
			Account_Data.Set_UserId(Account_Info, "L" + Level  + Account_Info.Billing_Country_Code + "Create");
			Account_Data.Set_Dummy_Contact_Name(Account_Info);
			String Result[] = WFCL_Functions_UsingData.WFCL_UserRegistration(Account_Info);
			Helper_Functions.PrintOut(Arrays.toString(Result), false);
		}catch (Exception e) {
			Assert.fail(e.getMessage());
		}
	}
	
	@Test(dataProvider = "dp", enabled = false)
	public void UserRegistration_Account_Data_Captcha(String Level, Account_Data Account_Info) {
		try {
			Account_Data.Print_Account_Address(Account_Info);
			Account_Data.Set_UserId(Account_Info, "L" + Level  + Account_Info.Billing_Country_Code + "Create");
			Account_Data.Set_Dummy_Contact_Name(Account_Info);
			Account_Info.Email = Helper_Functions.getRandomString(12) + "@fedex.com";
			WFCL_Functions_UsingData.WFCL_UserRegistration_Captcha(Account_Info);
			Thread.sleep(10000);
		}catch (Exception e) {
			Assert.fail(e.getMessage());
		}
	}
	
	@Test(dataProvider = "dp", priority = 9)//since this method will consume an account number run after others have completed
	public void AccountRegistration_Admin(String Level, Account_Data Account_Info) {
		try {
			Account_Data.Print_Account_Address(Account_Info);
			Account_Data.Set_Account_Nickname(Account_Info, Account_Info.Account_Number + "_" + Account_Info.Billing_Country_Code);
			Account_Data.Set_UserId(Account_Info, "L" + Level  + Account_Info.Account_Number + Account_Info.Billing_Country_Code);
			Account_Data.Set_Dummy_Contact_Name(Account_Info);
			Account_Info = WFCL_Functions_UsingData.Account_Linkage(Account_Info);
			boolean INET_Result = WFCL_Functions_UsingData.INET_Registration(Account_Info);
			String Result[] = new String[] {Account_Info.UserId, Account_Info.Password, Account_Info.Account_Number, Account_Info.UUID, "INET: " + INET_Result};
			Result = Arrays.copyOf(Result, Result.length + 1);
			Result[Result.length - 1] = "Admin: " + WFCL_Functions.Admin_Registration(Account_Info.Billing_Country_Code, Account_Info.Account_Number);
			Helper_Functions.PrintOut(Arrays.toString(Result), false);
		}catch (Exception e) {
			Assert.fail(e.getMessage());
		}
	}
	
	@Test(dataProvider = "dp")
	public void Forgot_User_Email(String Level, String CountryCode, String Email) {
		try {
			String Result = WFCL_Functions_UsingData.Forgot_User_Email(CountryCode, Email);
			Helper_Functions.PrintOut(Result, false);
		}catch (Exception e) {
			Assert.fail(e.getMessage());
		}
	}
	
	@Test(dataProvider = "dp")
	public void AccountRegistration_WDPA(String Level, Account_Data Account_Info){
		try {
			Account_Data.Print_Account_Address(Account_Info);
			Account_Data.Set_Dummy_Contact_Name(Account_Info);
			Account_Data.Set_Account_Nickname(Account_Info, Account_Info.Account_Number + "_" + Account_Info.Billing_Country_Code);
			Account_Data.Set_UserId(Account_Info, "L" + Level + "Wdpa" + Account_Info.Billing_Country_Code);
			String Result[] = WFCL_Functions_UsingData.WDPA_Registration(Account_Info);
			Helper_Functions.PrintOut(Arrays.toString(Result), false);
			
		}catch (Exception e) {
			Assert.fail(e.getMessage());
		}
	}
	
	@Test(dataProvider = "dp")
	public void Password_Reset_Secret(String Level, User_Data User_Info, String newPassword){
		try {
			String Result = WFCL_Functions_UsingData.WFCL_Secret_Answer(User_Info, newPassword);
			Helper_Functions.PrintOut(Result, false);
		}catch (Exception e) {
			Assert.fail(e.getMessage());
		}
	}
	
	@Test(dataProvider = "dp")
	public void Reset_Password_Email(String Level, User_Data User_Info) {
		try {
			String Result = WFCL_Functions_UsingData.ResetPasswordWFCL_Email(User_Info);
			Helper_Functions.PrintOut(Result, false);
		}catch (Exception e) {
			Assert.fail(e.getMessage());
		}
	}	
	
	@Test(dataProvider = "dp", priority = 1, enabled = true)
	public void Link_Account_To_User(String Level, User_Data User_Info, Account_Data Account_Info){
		try {
			String Result[] = WFCL_Functions_UsingData.Account_Linkage(User_Info, Account_Info);
			Helper_Functions.PrintOut(Arrays.toString(Result), false);
		}catch (Exception e) {
			Assert.fail(e.getMessage());
		}
	}
	
	@Test(dataProvider = "dp", priority = 1, enabled = true)
	public void FCLA_WADM_Invitaiton(String Level, User_Data User_Info, Account_Data Account_Info, String Email) {
		try {
			////////////////////not finished
			String Result[] = WFCL_Functions_UsingData.WFCL_WADM_Invitaiton(User_Info, Account_Info, Email);
			Helper_Functions.PrintOut(Arrays.toString(Result), false);
		}catch (Exception e) {
			Assert.fail(e.getMessage());
		}
	}
	
	@Test(dataProvider = "dp", priority = 1, enabled = true)
	public void WFCL_GFBO_Registration(String Level, User_Data User_Info, Account_Data Account_Info) {
		try {
			Account_Info = Account_Lookup.Account_DataAccountDetails("700335631", Level, "FX");
			String Result[] = WFCL_Functions_UsingData.WFCL_GFBO_Registration(User_Info, Account_Info);
			Helper_Functions.PrintOut(Arrays.toString(Result), false);
		}catch (Exception e) {
			Assert.fail(e.getMessage());
		}
	}

}