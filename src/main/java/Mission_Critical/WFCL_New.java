package Mission_Critical;

import org.testng.annotations.Test;
import Data_Structures.Account_Data;

import org.testng.annotations.AfterClass;
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
	static List<String> Users = new ArrayList<String>();

	@BeforeClass
	public void beforeClass() {
		Environment.SetLevelsToTest(LevelsToTest);
		//CountryList = Environment.getCountryList("US");
		//CountryList = new String[][]{{"JP", "Japan"}, {"MY", "Malaysia"}, {"SG", "Singapore"}, {"AU", "Australia"}, {"NZ", "New Zealand"}, {"HK", "Hong Kong"}, {"TW", "Taiwan"}, {"TH", "Thailand"}};
		//CountryList = new String[][]{{"SG", "Singapore"}, {"AU", "Australia"}, {"NZ", "New Zealand"}, {"HK", "Hong Kong"}};
		//CountryList = Environment.getCountryList("FR");
		CountryList = Environment.getCountryList("CA");
		//Helper_Functions.MyEmail = "accept@fedex.com";
		//CountryList = new String[][]{{"jj", "Singapore"}, {"CA", "Australia"}};
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
		    		for (int j = 0; j < CountryList.length; j++) {
		    			String EnrollmentID[] = Helper_Functions.LoadEnrollmentIDs(CountryList[j][0]);
		    			if (EnrollmentID != null) {
		    				data.add( new Object[] {Level, EnrollmentID[0], CountryList[j][0]});
		    			}
					}
		    		break;
		    	case "UserRegistration_Account_Data":
		    		Helper_Functions.MyEmail = "accept@fedex.com";
		    		for (int j = 0; j < CountryList.length; j++) {
		    			data.add( new Object[] {Level, Helper_Functions.getAddressDetails(Level, CountryList[j][0])});
		    			data.add( new Object[] {Level, Helper_Functions.getAddressDetails(Level, CountryList[j][0])});
		    			data.add( new Object[] {Level, Helper_Functions.getAddressDetails(Level, CountryList[j][0])});
		    			data.add( new Object[] {Level, Helper_Functions.getAddressDetails(Level, CountryList[j][0])});
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
		    		for (int j = 0; j < CountryList.length; j++) {
		    			for (int k = 0; k < Helper_Functions.DataClass[intLevel].length; k++) {
		    				if (Helper_Functions.DataClass[intLevel][k].COUNTRY_CD.contentEquals(CountryList[j][0])) {
		    					data.add( new Object[] {Level, CountryList[j][0], Helper_Functions.DataClass[intLevel][k].SSO_LOGIN_DESC, Helper_Functions.DataClass[intLevel][k].USER_PASSWORD_DESC + "A", Helper_Functions.DataClass[intLevel][k].SECRET_ANSWER_DESC});
		    					break;
		    				}
		    			}
					}
		    		break;
		    	case "Reset_Password_Email":
		    		for (int j = 0; j < CountryList.length; j++) {
		    			for (int k = 0; k < Helper_Functions.DataClass[intLevel].length; k++) {
		    				if (Helper_Functions.DataClass[intLevel][k].COUNTRY_CD.contentEquals(CountryList[j][0])) {
		    					data.add( new Object[] {Level, CountryList[j][0], Helper_Functions.DataClass[intLevel][k].SSO_LOGIN_DESC, Helper_Functions.DataClass[intLevel][k].USER_PASSWORD_DESC});
		    					break;
		    				}
		    			}
					}
		    		break;
		    	case "AccountRegistration_INET_Test":
		    		String Accounts[] = {"642454684", "642167308", "642455400", "797691380", "642454749", "642167405", "642455184", "797691500", "642454765", "642167529", "642455206", "797691640", "642454781", "642167626", "642455222", "797691860", "642454803", "642167723", "642455249", "797692280", "642454820", "642167820", "642455281", "797692700", "642454846", "642167928", "642455303", "797391700", "642454862", "642168142", "642455320"};
		    		for (String A: Accounts) {
		    			data.add( new Object[] {Level, A});
		    		}
		    		
		    		//data.add( new Object[] {Level, "640695480"});
		    		//data.add( new Object[] {Level, "640695447"});
		    		//data.add( new Object[] {Level, "106408335"});
			    	//data.add( new Object[] {Level, "633504580"});
		    		break;
			}
		}	
		return data.iterator();
	}

	@Test(dataProvider = "dp")
	public void CreditCardRegistrationEnroll(String Level, String EnrollmentID, String CountryCode) {
		try {
			String CreditCard[] = Helper_Functions.LoadCreditCard("V");
			String ShippingAddress[] = Helper_Functions.LoadAddress(CountryCode), BillingAddress[] = ShippingAddress;
			String UserId = Helper_Functions.LoadUserID("L" + Level + CountryCode + "CC");
			String ContactName[] = Helper_Functions.LoadDummyName(CountryCode + "CC", Level);
			String TaxInfo[] = Helper_Functions.getTaxInfo(CountryCode).get(0);
			String Result[] = WFCL_Functions.CreditCardRegistrationEnroll(EnrollmentID, CreditCard, ShippingAddress, BillingAddress, ContactName, UserId, Helper_Functions.MyEmail, false, TaxInfo);
			Helper_Functions.PrintOut(Arrays.toString(Result), false);
		}catch (Exception e) {
			Assert.fail(e.getMessage());
		}
		
		/*
		try {
			String CreditCard[] = Helper_Functions.LoadCreditCard("V");
			String ShippingAddress[] = Helper_Functions.LoadAddress(CountryCode), BillingAddress[] = ShippingAddress;
			String UserId = Helper_Functions.LoadUserID("L" + Level + CountryCode + "CC");
			String ContactName[] = Helper_Functions.LoadDummyName(CountryCode + "CC", Level);
			String TaxInfo[] = Helper_Functions.getTaxInfo(CountryCode).get(0);
			String Result[] = WFCL_Functions.CreditCardRegistrationEnroll(EnrollmentID, CreditCard, ShippingAddress, BillingAddress, ContactName, UserId, Helper_Functions.MyEmail, false, TaxInfo);
			Helper_Functions.PrintOut(Arrays.toString(Result), false);
		}catch (Exception e) {
			Assert.fail(e.getMessage());
		}
		*/
	}

	@Test(dataProvider = "dp")
	public void UserRegistration_Account_Data(String Level, Account_Data AccountDetails) {
		try {
			AccountDetails.Level = Level;
			AccountDetails.UserId = Helper_Functions.LoadUserID("L" + Level  + AccountDetails.Billing_Country_Code + "Create");
			AccountDetails = Account_Data.Set_Dummy_Contact_Name(AccountDetails);
			String Result = Arrays.toString(WFCL_Functions_UsingData.WFCL_UserRegistration(AccountDetails));
			Helper_Functions.PrintOut(Result, false);
		}catch (Exception e) {
			Assert.fail(e.getMessage());
		}
	}
	
	@Test(dataProvider = "dp", priority = 3)//since this method will consume an acocunt number run after others have completed
	public void AccountRegistration_Admin(String Level, Account_Data AccountDetails) {
		try {
			String UserName[] = Helper_Functions.LoadDummyName("INET", Level);
			String CountryCode = AccountDetails.Billing_Country_Code;
			String Account = AccountDetails.Account_Number;
			String AddressDetails[] = new String[] {AccountDetails.Billing_Address_Line_1, AccountDetails.Billing_Address_Line_2, AccountDetails.Billing_City, AccountDetails.Billing_State, AccountDetails.Billing_State_Code, AccountDetails.Billing_Zip, AccountDetails.Billing_Country_Code};
			String UserID = Helper_Functions.LoadUserID("L" + Level + Account + CountryCode);
			String Result = WFCL_Functions.WFCL_AccountRegistration_INET(UserName, UserID, Helper_Functions.MyEmail, Account, AddressDetails);
			WFCL_Functions.Admin_Registration(CountryCode, Account);
			Helper_Functions.PrintOut(Result, false);
		}catch (Exception e) {
			Assert.fail(e.getMessage());
		}
	}
	
	@Test(dataProvider = "dp", priority = 1)
	public void AccountRegistration_INET(String Level, Account_Data AccountDetails){
		try {
			String CountryCode = AccountDetails.Billing_Country_Code;
			AccountDetails = Account_Data.Set_Dummy_Contact_Name(AccountDetails);
			AccountDetails.UserId = Helper_Functions.LoadUserID("L" + Level + "Inet" + CountryCode);
			//create user id and link to account number.
			AccountDetails = WFCL_Functions_UsingData.Account_Linkage(AccountDetails);
			//register the userid to INET
			WFCL_Functions_UsingData.INET_Registration(AccountDetails);
			String Result[] = new String[] {AccountDetails.UserId, AccountDetails.UUID, AccountDetails.Account_Number};
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
			String UserID = Helper_Functions.LoadUserID("L" + Level + CountryCode + Thread.currentThread().getId() + "WDPA");
			String Result[] = WFCL_Functions.WDPA_Registration(ContactName, UserID, Helper_Functions.MyEmail, Account, AddressDetails);
			Helper_Functions.PrintOut(Arrays.toString(Result), false);
		}catch (Exception e) {
			Assert.fail(e.getMessage());
		}
	}
	
	@Test(dataProvider = "dp")
	public void Password_Reset_Secret(String Level, String Country, String UserId, String newPassword, String SecretAnswer){
		try {
			String Result = WFCL_Functions.WFCL_Secret_Answer(Country, UserId, newPassword, SecretAnswer);
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
			Account_Data AccountDetails = Account_Lookup.Account_DataAccountDetails(Account, Level);
			AccountDetails = Account_Data.Set_Dummy_Contact_Name(AccountDetails);
			AccountDetails.UserId = Helper_Functions.LoadUserID("L" + Level + "Acc" + Account);
			//create user id and link to account number.
			AccountDetails = WFCL_Functions_UsingData.Account_Linkage(AccountDetails);
			//register the userid to INET
			WFCL_Functions_UsingData.INET_Registration(AccountDetails);
			String Result[] = new String[] {AccountDetails.UserId, AccountDetails.UUID, AccountDetails.Account_Number};
			Helper_Functions.PrintOut(Arrays.toString(Result), false);
			Users.add(AccountDetails.UserId);
		}catch (Exception e) {
			Assert.fail(e.getMessage());
		}
	}
	
	@AfterClass
	public void afterClass() {
		if (Users != null) {
			System.out.print("Users Created: ");
			for (String U: Users) {
				System.out.print(U + ", ");
			}
		}
	}


}


/*
 * 
 
	
	@Test(dataProvider = "dp")
	public void UserRegistration(String Level, String CountryCode) {
		try {
			String Address[] = Helper_Functions.LoadAddress(CountryCode);
			String UserID = Helper_Functions.LoadUserID("L" + Level  + Address[6] + "Create");
			String ContactName[] = Helper_Functions.LoadDummyName("Create", Level);
			String Result = Arrays.toString(WFCL_Functions.WFCL_UserRegistration(UserID, ContactName, Helper_Functions.MyEmail, Address));
			Helper_Functions.PrintOut(Result, false);
		}catch (Exception e) {
			Assert.fail(e.getMessage());
		}
	}
	*/
