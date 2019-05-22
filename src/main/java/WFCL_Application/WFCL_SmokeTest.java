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

public class WFCL_SmokeTest{
	static String LevelsToTest = "7";
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
			Account_Data AccountDetails = null;
			int intLevel = Integer.parseInt(Level);
			switch (m.getName()) { //Based on the method that is being called the array list will be populated.
	    	case "WFCL_CreditCardRegistration":
	    		Enrollment_Data ED[] = Environment.getEnrollmentDetails(intLevel);
	    		for (int j = 0; j < CountryList.length; j++) {
	    			for (Enrollment_Data Enrollment: ED) {
		    			if (Enrollment.COUNTRY_CODE.contentEquals(CountryList[j][0]) ) {   //&& Enrollment.ENROLLMENT_ID.contentEquals("cc16323414")
		    				AccountDetails = Environment.getAddressDetails(Level, CountryList[j][0], "CreditCard");
		    				AccountDetails.Account_Type = "P";//Personal account
		    				data.add( new Object[] {Level, Enrollment, AccountDetails, Environment.getTaxDetails(CountryList[j][0])});
		    				break;
		    			}
	    			}
				}
	    		break;
	    	case "WFCL_CreateUserID":
	    		for (int j = 0; j < CountryList.length; j++) {
	    			data.add( new Object[] {Level, Helper_Functions.getAddressDetails(Level, CountryList[j][0])});
				}
	    		break;
	    	case "WFCL_AdminReg":
	    		if (Level == "7") {break;}//does not run in L7 unless this is changed. to protect prod test data from accidental being consumed.
	    		for (int j = 0; j < CountryList.length; j++) {
	    			AccountDetails = Helper_Functions.getFreshAccount(Level, CountryList[j][0]);
				}
	    		data.add( new Object[] {Level, AccountDetails});
	    		break;
	    	case "WFCL_INETReg":
	    	case "WFCL_WDPA":
	    		for (int j = 0; j < CountryList.length; j++) {
	    			AccountDetails = Helper_Functions.getFreshAccount(Level, CountryList[j][0]);
		    		data.add( new Object[] {Level, AccountDetails});
				}
	    		break;
	    	case "WFCL_Forgot_User_Email":
	    		for (int j = 0; j < CountryList.length; j++) {
		    		data.add( new Object[] {Level, CountryList[j][0], Helper_Functions.MyEmail});
				}
	    		break;
	    	case "WFCL_Reset_Password_Secret":
	    		User_Data User_Info[] = Environment.Get_UserIds(intLevel);
	    		for (int j = 0; j < CountryList.length; j++) {
	    			for (int k = 0; k < User_Info.length; k++) {
	    				if (User_Info[k].Address_Info.Country_Code.contentEquals(CountryList[j][0]) && !User_Info[k].SECRET_ANSWER_DESC.contentEquals("")) {
	    					data.add( new Object[] {Level, User_Info[k], User_Info[k].PASSWORD + "A"});
	    					break;
	    				}
	    			}
				}
	    		break;
	    	case "WFCL_Reset_Password_Email":
	    		User_Info = Environment.Get_UserIds(intLevel);
	    		for (int j = 0; j < CountryList.length; j++) {
	    			for (int k = 0; k < User_Info.length; k++) {
	    				//make sure have your email address set as the Helper_Functions.MyEmail
	    				if (User_Info[k].Address_Info.Country_Code.contentEquals(CountryList[j][0]) && User_Info[k].EMAIL_ADDRESS.contentEquals(Helper_Functions.MyEmail)) {
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
	public void WFCL_CreditCardRegistration(String Level, Enrollment_Data Enrollment_Info, Account_Data Account_Info, Tax_Data Tax_Info) {
		try {
			Account_Data.Print_Account_Address(Account_Info);
			Account_Data.Set_Credit_Card(Account_Info, Environment.getCreditCardDetails(Level, "V"));
			Account_Data.Set_UserId(Account_Info, "L" + Level + Account_Info.Billing_Address_Info.Country_Code + Enrollment_Info.ENROLLMENT_ID + "CC");
			Account_Data.Set_Dummy_Contact_Name(Account_Info);
			String Result[] = WFCL_Functions_UsingData.CreditCardRegistrationEnroll(Enrollment_Info, Account_Info, Tax_Info);
			Helper_Functions.PrintOut(Arrays.toString(Result), false);
			
		}catch (Exception e) {
			Assert.fail(e.getMessage());
		}
	}

	@Test(dataProvider = "dp")
	public void WFCL_CreateUserID(String Level, Account_Data Account_Info) {
		try {
			Account_Data.Print_Account_Address(Account_Info);
			Account_Data.Set_UserId(Account_Info, "L" + Level  + Account_Info.Billing_Address_Info.Country_Code + "Create");
			Account_Data.Set_Dummy_Contact_Name(Account_Info);
			String Result = Arrays.toString(WFCL_Functions_UsingData.WFCL_UserRegistration(Account_Info));
			Helper_Functions.PrintOut(Result, false);
		}catch (Exception e) {
			Assert.fail(e.getMessage());
		}
	}
	
	@Test(dataProvider = "dp", priority = 9)//since this method will consume an account number run after others have completed
	public void WFCL_AdminReg(String Level, Account_Data Account_Info) {
		try {
			Account_Data.Print_Account_Address(Account_Info);
			Account_Data.Set_Dummy_Contact_Name(Account_Info);
			Account_Data.Set_Account_Nickname(Account_Info, Account_Info.Account_Number + "_" + Account_Info.Billing_Address_Info.Country_Code);
			Account_Data.Set_UserId(Account_Info, "L" + Level  + Account_Info.Account_Number + Account_Info.Billing_Address_Info.Country_Code);
			//create user id and link to account number.
			Account_Info = WFCL_Functions_UsingData.Account_Linkage(Account_Info);
			boolean InetFlag = WFCL_Functions_UsingData.INET_Registration(Account_Info);
			String Result[] = new String[] {Account_Info.User_Info.USER_ID, Account_Info.User_Info.PASSWORD, Account_Info.Account_Number, Account_Info.User_Info.UUID_NBR, "Inet: " + InetFlag};
			Result = Arrays.copyOf(Result, Result.length + 1);
			Result[Result.length - 1] = "Admin: " + WFCL_Functions.Admin_Registration(Account_Info.Billing_Address_Info.Country_Code, Account_Info.Account_Number);
			Helper_Functions.PrintOut(Arrays.toString(Result), false);
		}catch (Exception e) {
			Assert.fail(e.getMessage());
		}
	}
	
	@Test(dataProvider = "dp")
	public void WFCL_INETReg(String Level, Account_Data Account_Info){
		try {
			Account_Data.Print_Account_Address(Account_Info);
			Account_Data.Set_Dummy_Contact_Name(Account_Info);
			Account_Data.Set_Account_Nickname(Account_Info, Account_Info.Account_Number + "_" + Account_Info.Billing_Address_Info.Country_Code);
			Account_Data.Set_UserId(Account_Info, "L" + Level + "Inet" + Account_Info.Billing_Address_Info.Country_Code);
			//create user id and link to account number.
			Account_Info = WFCL_Functions_UsingData.Account_Linkage(Account_Info);
			//register the user id to INET
			WFCL_Functions_UsingData.INET_Registration(Account_Info);
			String Result[] = new String[] {Account_Info.User_Info.USER_ID, Account_Info.User_Info.PASSWORD, Account_Info.Account_Number, Account_Info.User_Info.UUID_NBR};
			Helper_Functions.PrintOut(Arrays.toString(Result), false);
		}catch (Exception e) {
			Assert.fail(e.getMessage());
		}
	}
	
	@Test(dataProvider = "dp")
	public void WFCL_Forgot_User_Email(String Level, String CountryCode, String Email) {
		try {
			String Result = WFCL_Functions_UsingData.Forgot_User_Email(CountryCode, Email);
			Helper_Functions.PrintOut(Result, false);
			Helper_Functions.PrintOut("Email Triggered", false);
		}catch (Exception e) {
			Assert.fail(e.getMessage());
		}
	}
	
	@Test(dataProvider = "dp")
	public void WFCL_WDPA(String Level, Account_Data Account_Info){
		try {
			Account_Data.Print_Account_Address(Account_Info);
			Account_Data.Set_Dummy_Contact_Name(Account_Info);
			Account_Data.Set_Account_Nickname(Account_Info, Account_Info.Account_Number + "_" + Account_Info.Billing_Address_Info.Country_Code);
			Account_Data.Set_UserId(Account_Info, "L" + Level + "Wdpa" + Account_Info.Billing_Address_Info.Country_Code);
			
			String Result[] = WFCL_Functions_UsingData.WDPA_Registration(Account_Info);
			Helper_Functions.PrintOut(Arrays.toString(Result), false);
			
		}catch (Exception e) {
			Assert.fail(e.getMessage());
		}
	}
	
	@Test(dataProvider = "dp")
	public void WFCL_Reset_Password_Secret(String Level, User_Data User_Info, String newPassword){
		try {
			String Result = WFCL_Functions_UsingData.WFCL_Secret_Answer(User_Info, newPassword);
			Helper_Functions.PrintOut(Result, false);
		}catch (Exception e) {
			Assert.fail(e.getMessage());
		}
	}
	
	@Test(dataProvider = "dp")
	public void WFCL_Reset_Password_Email(String Level, User_Data User_Info) {
		try {
			String Result = WFCL_Functions_UsingData.ResetPasswordWFCL_Email(User_Info);
			Helper_Functions.PrintOut(Result, false);
			Helper_Functions.PrintOut("Email Triggered", false);
		}catch (Exception e) {
			Assert.fail(e.getMessage());
		}
	}	

}