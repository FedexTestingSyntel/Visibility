package Mission_Critical;

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
		//CountryList = Environment.getCountryList("BR");
		//CountryList = Environment.getCountryList("AE");
		//CountryList = Environment.getCountryList("high");
		Helper_Functions.MyEmail = "accept@fedex.com";
		//CountryList = new String[][]{{"US", ""}, {"CA", ""}};
	}
	
	@DataProvider (parallel = true)
	public static Iterator<Object[]> dp(Method m) {
		List<Object[]> data = new ArrayList<Object[]>();

		for (int i=0; i < Environment.LevelsToTest.length(); i++) {
			String Level = String.valueOf(Environment.LevelsToTest.charAt(i));
			Account_Data AccountDetails = null;
			int intLevel = Integer.parseInt(Level);
			//int intLevel = Integer.parseInt(Level);
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
		    		if (Level == "7") {
		    			break;
		    		}
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
		    				if (UD[k].COUNTRY_CD.contentEquals(CountryList[j][0])) {
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
		    	case "AccountRegistration_INET_Test":
		    		/*String Accounts[] = {"642454684", "642167308", "642455400", "797691380", "642454749", "642167405", "642455184", "797691500", "642454765", "642167529", "642455206", "797691640", "642454781", "642167626", "642455222", "797691860", "642454803", "642167723", "642455249", "797692280", "642454820", "642167820", "642455281", "797692700", "642454846", "642167928", "642455303", "797391700", "642454862", "642168142", "642455320"};
		    		for (String A: Accounts) {
		    			data.add( new Object[] {Level, A});
		    		}
		    		*/
		    		
		    		data.add( new Object[] {Level, "642178660"});
		    		//data.add( new Object[] {Level, "644280888"});
		    		//data.add( new Object[] {Level, "642260243"});
			    	//data.add( new Object[] {Level, "633504580"});
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
			
			/*
			String CreditCard[] = Helper_Functions.LoadCreditCard("M");
			String CountryCode = Enrollment.COUNTRY_CODE;
			String ShippingAddress[] = Helper_Functions.LoadAddress(CountryCode), BillingAddress[] = ShippingAddress;
			String UserId = Helper_Functions.LoadUserID("L" + Level + CountryCode + "CC");
			String ContactName[] = Helper_Functions.LoadDummyName(CountryCode + "CC", Level);
			String TaxInfo[] = Helper_Functions.getTaxInfo(CountryCode).get(0);
			String EnrollmentID[] = new String[] {Enrollment.ENROLLMENT_ID, Enrollment.COUNTRY_CODE, Enrollment.PROGRAM_NAME, Enrollment.PASSCODE, Enrollment.MEMBERSHIP_ID};
			String Result[] = WFCL_Functions.CreditCardRegistrationEnroll(EnrollmentID, CreditCard, ShippingAddress, BillingAddress, ContactName, UserId, Helper_Functions.MyEmail, false, TaxInfo);
			Helper_Functions.PrintOut(Arrays.toString(Result), false);
			*/
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
			String Result = WFCL_Functions.Forgot_User_Email(CountryCode, Email);
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
			
			/*
			Account_Data.Print_Account_Address(Account_Info);
			String CountryCode = Account_Info.Billing_Country_Code;
			String Account = Account_Info.Account_Number;
			String AddressDetails[] = new String[] {Account_Info.Billing_Address_Line_1, Account_Info.Billing_Address_Line_2, Account_Info.Billing_City, Account_Info.Billing_State, Account_Info.Billing_State_Code, Account_Info.Billing_Zip, Account_Info.Billing_Country_Code};

			String ContactName[] = Helper_Functions.LoadDummyName("WDPA", Level);
			String UserID = Helper_Functions.LoadUserID("L" + Level + CountryCode + Thread.currentThread().getId() + "WDPA");
			String Result[] = WFCL_Functions.WDPA_Registration(ContactName, UserID, Helper_Functions.MyEmail, Account, AddressDetails);
			Helper_Functions.PrintOut(Arrays.toString(Result), false);
			*/
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
	
	@Test(dataProvider = "dp", priority = 1, enabled = true)
	public void AccountRegistration_INET_Test(String Level, String Account){
		try {
			Account_Data AccountDetails = Account_Lookup.Account_DataAccountDetails(Account, Level, "FX");
			AccountDetails = Account_Data.Set_Dummy_Contact_Name(AccountDetails);
			AccountDetails.UserId = Helper_Functions.LoadUserID("L" + Level + "Acc" + Account + "N");
			//create user id and link to account number.
			AccountDetails = WFCL_Functions_UsingData.Account_Linkage(AccountDetails);
			//register the userid to INET
			WFCL_Functions_UsingData.INET_Registration(AccountDetails);
			String Result[] = new String[] {AccountDetails.UserId, AccountDetails.Password, AccountDetails.Account_Number, AccountDetails.UUID};
			Helper_Functions.PrintOut(Arrays.toString(Result), false);
		}catch (Exception e) {
			Assert.fail(e.getMessage());
		}
	}
	
	@Test(dataProvider = "dp", priority = 1, enabled = false)
	public void Link_account_to_user(String Level, String Account, String UserID, String Password){
		try {
			Account_Data AccountDetails = Account_Lookup.Account_DataAccountDetails(Account, Level, "FX");
			AccountDetails = Account_Data.Set_Dummy_Contact_Name(AccountDetails);
			AccountDetails.UserId = Helper_Functions.LoadUserID("L" + Level + "Acc" + Account);
			
			//String Result = WFCL_Functions_UsingData.Account_Number_Masking(Account_Info, Account_Info_Mismatch);
			//Helper_Functions.PrintOut(Result, false);
		}catch (Exception e) {
			Assert.fail(e.getMessage());
		}
	}
}