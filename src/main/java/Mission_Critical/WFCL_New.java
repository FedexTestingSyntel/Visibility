package Mission_Critical;

import org.testng.annotations.Test;
import Data_Structures.Account_Data;
import Data_Structures.User_Data;

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
	static String LevelsToTest = "2";  
	static String CountryList[][]; 
	static List<String> Users = new ArrayList<String>();

	@BeforeClass
	public void beforeClass() {
		Environment.SetLevelsToTest(LevelsToTest);
		CountryList = Environment.getCountryList("US");
		//CountryList = new String[][]{{"JP", "Japan"}, {"MY", "Malaysia"}, {"SG", "Singapore"}, {"AU", "Australia"}, {"NZ", "New Zealand"}, {"HK", "Hong Kong"}, {"TW", "Taiwan"}, {"TH", "Thailand"}};
		//CountryList = new String[][]{{"SG", "Singapore"}, {"AU", "Australia"}, {"NZ", "New Zealand"}, {"HK", "Hong Kong"}};
		//CountryList = Environment.getCountryList("BR");
		//CountryList = Environment.getCountryList("GU");
		//CountryList = Environment.getCountryList("high");
		//Helper_Functions.MyEmail = "accept@fedex.com";
		//CountryList = new String[][]{{"US", ""}, {"AU", ""}};
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
		    		
		    		data.add( new Object[] {Level, "697561862"});
		    		//data.add( new Object[] {Level, "642711326"});
		    		//data.add( new Object[] {Level, "642260243"});
			    	//data.add( new Object[] {Level, "633504580"});
		    		break;
			}
		}	
		return data.iterator();
	}

	@Test(dataProvider = "dp")
	public void CreditCardRegistrationEnroll(String Level, String EnrollmentID, String CountryCode) {
		try {
			String CreditCard[] = Helper_Functions.LoadCreditCard("M");
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
	public void UserRegistration_Account_Data(String Level, Account_Data AccountDetails) {
		try {
			AccountDetails.Level = Level;
			AccountDetails.UserId = Helper_Functions.LoadUserID("L" + Level  + AccountDetails.Billing_Country_Code + "Create");
			AccountDetails = Account_Data.Set_Dummy_Contact_Name(AccountDetails);
			AccountDetails.FirstName = "Resmi";
			AccountDetails.LastName = "Raveendran";
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
			String Result[] = WFCL_Functions.WFCL_AccountRegistration_INET(UserName, UserID, Helper_Functions.MyEmail, Account, AddressDetails);
			Result = Arrays.copyOf(Result, Result.length + 1);
			Result[Result.length - 1] = "Admin:" + WFCL_Functions.Admin_Registration(CountryCode, Account);
			Helper_Functions.PrintOut(Arrays.toString(Result), false);
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
	
	@Test(dataProvider = "dp", priority = 1, enabled = true)
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