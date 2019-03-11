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
	static String LevelsToTest = "3";
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
	    	case "CreditCardRegistrationEnroll":
	    		Enrollment_Data ED[] = Environment.getEnrollmentDetails(intLevel);
	    		for (int j = 0; j < CountryList.length; j++) {
	    			for (Enrollment_Data Enrollment: ED) {
		    			if (Enrollment.COUNTRY_CODE.contentEquals(CountryList[j][0]) ) {   //&& Enrollment.ENROLLMENT_ID.contentEquals("cc16323414")
		    				AccountDetails = Environment.getAddressDetails(Level, CountryList[j][0]);
		    				AccountDetails.Account_Type = "P";//Personal account
		    				data.add( new Object[] {Level, Enrollment, AccountDetails, Environment.getTaxDetails(CountryList[j][0])});
		    				break;
		    			}
	    			}
				}
	    		break;
	    	case "UserRegistration_Account_Data":
	    		for (int j = 0; j < CountryList.length; j++) {
	    			data.add( new Object[] {Level, Helper_Functions.getAddressDetails(Level, CountryList[j][0])});
				}
	    		break;
	    	case "AccountRegistration_Admin":
	    		if (Level == "7") {break;}//does not run in L7 unless this is changed. to protect prod test data from accidental being consumed.
	    		for (int j = 0; j < CountryList.length; j++) {
	    			AccountDetails = Helper_Functions.getFreshAccount(Level, CountryList[j][0]);
				}
	    		data.add( new Object[] {Level, AccountDetails});
	    		break;
	    	case "AccountRegistration_INET":
	    	case "AccountRegistration_WDPA":
	    		for (int j = 0; j < CountryList.length; j++) {
	    			AccountDetails = Helper_Functions.getFreshAccount(Level, CountryList[j][0]);
		    		data.add( new Object[] {Level, AccountDetails});
				}
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
			}
		}	
		return data.iterator();
	}
/*    Old before using data classes
 switch (m.getName()) { //Based on the method that is being called the array list will be populated.
		    	case "CreditCardRegistrationEnroll":
		    		for (int j = 0; j < CountryList.length; j++) {
		    			String EnrollmentID[] = Helper_Functions.LoadEnrollmentIDs(CountryList[j][0]);
		    			//EnrollmentID[0] = "cc16323414";
		    			data.add( new Object[] {Level, EnrollmentID});
					}
		    		break;
		    	case "UserRegistration":
		    		data.add( new Object[] {Level, "US"});
		    		break;
		    	case "AccountRegistration_Admin":
		    		if (Level == "7") {
		    			break;
		    		}
		    		AccountDetails = Helper_Functions.getFreshAccount(Level, "US");
		    		data.add( new Object[] {Level, AccountDetails});
		    		break;
		    	case "AccountRegistration_INET":
		    	case "AccountRegistration_WDPA":
					if (!Level.contentEquals("6")) {
						// don't Forcing skip due to level 6");
					}
		    		for (int j = 0; j < CountryList.length; j++) {
		    			AccountDetails = Helper_Functions.getFreshAccount(Level, CountryList[j][0]);
			    		data.add( new Object[] {Level, AccountDetails});
					}
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
		    					data.add( new Object[] {Level, CountryList[j][0], UD[k].SSO_LOGIN_DESC, UD[k].USER_PASSWORD_DESC + "A", UD[k].SECRET_ANSWER_DESC});
		    					break;
		    				}
		    			}
					}
		    		break;
		    	case "Reset_Password_Email":
		    		UD = Environment.Get_UserIds(intLevel);
		    		for (int j = 0; j < CountryList.length; j++) {
		    			for (int k = 0; k < UD.length; k++) {
		    				if (UD[k].COUNTRY_CD.contentEquals(CountryList[j][0])) {
		    					data.add( new Object[] {Level, CountryList[j][0], UD[k].SSO_LOGIN_DESC, UD[k].USER_PASSWORD_DESC});
		    					break;
		    				}
		    			}
					}
		    		break;
			}
	@Test(dataProvider = "dp") 
	public void CreditCardRegistrationEnroll(String Level, String EnrollmentID[]) {
		try {
			String CreditCard[] = Helper_Functions.LoadCreditCard("V");
			String CountryCode = EnrollmentID[1];
			String ShippingAddress[] = Helper_Functions.LoadAddress(CountryCode), BillingAddress[] = ShippingAddress;
			String UserId = Helper_Functions.LoadUserID("L" + Level + CountryCode + "CC");
			String ContactName[] = Helper_Functions.LoadDummyName(CountryCode + "CC", Level);
			String TaxInfo[] = Helper_Functions.getTaxInfo(CountryCode).get(0);
			String Result[] = WFCL_Functions.CreditCardRegistrationEnroll(EnrollmentID, CreditCard, ShippingAddress, BillingAddress, ContactName, UserId, Helper_Functions.MyEmail, false, TaxInfo);
			Helper_Functions.PrintOut(Arrays.toString(Result), false);
		}catch (Exception e) {
			Assert.fail(e.getMessage());
		}
	}
	
	@Test(dataProvider = "dp")
	public void UserRegistration(String Level, String CountryCode) {
		try {
			String Address[] = Helper_Functions.LoadAddress(CountryCode);
			String UserID = Helper_Functions.LoadUserID("L" + Level  + Address[6] + "Create");
			String ContactName[] = Helper_Functions.LoadDummyName("Create", Level);
			String Email = Helper_Functions.MyEmail;
			String Result = Arrays.toString(WFCL_Functions.WFCL_UserRegistration(UserID, ContactName, Email, Address));
			Helper_Functions.PrintOut(Result, false);
		}catch (Exception e) {
			Assert.fail(e.getMessage());
		}
	}
	
	@Test(dataProvider = "dp", priority = 3)//since this method will consume an account number run after others have completed
	public void AccountRegistration_Admin(String Level, Account_Data AccountDetails) {
		try {
			String UserName[] = Helper_Functions.LoadDummyName("INET", Level);
			String CountryCode = AccountDetails.Billing_Country_Code;
			String Account = AccountDetails.Account_Number;
			String AddressDetails[] = new String[] {AccountDetails.Billing_Address_Line_1, AccountDetails.Billing_Address_Line_2, AccountDetails.Billing_City, AccountDetails.Billing_State, AccountDetails.Billing_State_Code, AccountDetails.Billing_Zip, AccountDetails.Billing_Country_Code};
			String UserID = Helper_Functions.LoadUserID("L" + Level + Account + CountryCode);
			String Result[] = WFCL_Functions.WFCL_AccountRegistration_INET(UserName, UserID, Helper_Functions.MyEmail, Account, AddressDetails);
			Result = Arrays.copyOf(Result, Result.length + 1);
			Result[Result.length - 1] = "Admin:" + WFCL_Functions.Admin_Registration(CountryCode, Account);
			Helper_Functions.PrintOut(Arrays.toString(Result), false);
		}catch (Exception e) {
			Assert.fail(e.getMessage());
		}
	}
	
	@Test(dataProvider = "dp", enabled = true)
	public void AccountRegistration_INET(String Level, Account_Data AccountDetails){
		try {
			String CountryCode = AccountDetails.Billing_Country_Code;
			String Account = AccountDetails.Account_Number;
			String AddressDetails[] = new String[] {AccountDetails.Billing_Address_Line_1, AccountDetails.Billing_Address_Line_2, AccountDetails.Billing_City, AccountDetails.Billing_State, AccountDetails.Billing_State_Code, AccountDetails.Billing_Zip, AccountDetails.Billing_Country_Code};
			String ContactName[] = Helper_Functions.LoadDummyName(CountryCode, Level);
			String UserID = Helper_Functions.LoadUserID("L" + Level + "Inet" + CountryCode);
			String Result[] = WFCL_Functions.WFCL_AccountRegistration_INET(ContactName, UserID, Helper_Functions.MyEmail, Account, AddressDetails);
			Helper_Functions.PrintOut(Arrays.toString(Result), false);
		}catch (Exception e) {
			Assert.fail(e.getMessage());
		}
	}
	
	@Test(dataProvider = "dp")
	public void Forgot_User_Email(String Level, String CountryCode, String Email) {
		try {
			String Result = WFCL_Functions.Forgot_User_Email(CountryCode, Email);
			Helper_Functions.PrintOut(Result, false);
		}catch (Exception e) {
			Assert.fail(e.getMessage());
		}
	}
	
	@Test(dataProvider = "dp")
	public void AccountRegistration_WDPA(String Level, Account_Data AccountDetails){
		try {
			String CountryCode = AccountDetails.Billing_Country_Code;
			String Account = AccountDetails.Account_Number;
			String AddressDetails[] = new String[] {AccountDetails.Billing_Address_Line_1, AccountDetails.Billing_Address_Line_2, AccountDetails.Billing_City, AccountDetails.Billing_State, AccountDetails.Billing_State_Code, AccountDetails.Billing_Zip, AccountDetails.Billing_Country_Code};
			String ContactName[] = Helper_Functions.LoadDummyName("WDPA", Level);
			String UserID = Helper_Functions.LoadUserID("L" + Level + CountryCode + "WDPA");
			String Result[] = WFCL_Functions.WDPA_Registration(ContactName, UserID, Helper_Functions.MyEmail, Account, AddressDetails);
			Helper_Functions.PrintOut(Arrays.toString(Result), false);
		}catch (Exception e) {
			Assert.fail(e.getMessage());
		}
	}
	
	@Test(dataProvider = "dp")
	public void Password_Reset_Secret(String Level, String Country, String UserId, String newPassword, String SecretAnswer){
		try {
			String Result = WFCL_Functions.WFCL_Secret_Answer(Country, UserId, newPassword, SecretAnswer, false);
			Helper_Functions.PrintOut(Result, false);
		}catch (Exception e) {
			Assert.fail(e.getMessage());
		}
	}
	
	@Test(dataProvider = "dp")
	public void Reset_Password_Email(String Level, String CountryCode, String UserId, String Password) {
		try {
			String Result = WFCL_Functions.ResetPasswordWFCL_Email(CountryCode, UserId, Password);
			Helper_Functions.PrintOut(Result, false);
		}catch (Exception e) {
			Assert.fail(e.getMessage());
		}
	}	
*/
///////////////
	
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

	@Test(dataProvider = "dp")
	public void UserRegistration_Account_Data(String Level, Account_Data Account_Info) {
		try {
			Account_Data.Print_Account_Address(Account_Info);
			Account_Data.Set_UserId(Account_Info, "L" + Level  + Account_Info.Billing_Country_Code + "Create");
			Account_Data.Set_Dummy_Contact_Name(Account_Info);
			String Result = Arrays.toString(WFCL_Functions_UsingData.WFCL_UserRegistration(Account_Info));
			Helper_Functions.PrintOut(Result, false);
		}catch (Exception e) {
			Assert.fail(e.getMessage());
		}
	}
	
	@Test(dataProvider = "dp", priority = 9)//since this method will consume an account number run after others have completed
	public void AccountRegistration_Admin(String Level, Account_Data Account_Info) {
		try {
			Account_Data.Print_Account_Address(Account_Info);
			Account_Data.Set_UserId(Account_Info, "L" + Level  + Account_Info.Account_Number + Account_Info.Billing_Country_Code);
			Account_Data.Set_Dummy_Contact_Name(Account_Info);
			boolean INET_Result = WFCL_Functions_UsingData.INET_Registration(Account_Info);
			String Result[] = new String[] {Account_Info.UserId, Account_Info.Password, Account_Info.Account_Number, Account_Info.UUID, "INET: " + INET_Result};
			Result = Arrays.copyOf(Result, Result.length + 1);
			Result[Result.length - 1] = "Admin: " + WFCL_Functions.Admin_Registration(Account_Info.Billing_Country_Code, Account_Info.Account_Number);
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
	

}
