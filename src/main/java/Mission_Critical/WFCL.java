package Mission_Critical;

import org.testng.annotations.Test;

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

public class WFCL{
	static String LevelsToTest = "7";
	static String CountryList[][];

	@BeforeClass
	public void beforeClass() {
		Environment.SetLevelsToTest(LevelsToTest);
		CountryList = Environment.getCountryList("smoke");
		
		Environment.getInstance().setLevel("3");
		Helper_Functions.AccountDetails("641179302");

		//CountryList = Environment.getCountryList("BR");
		//CountryList = Environment.getCountryList("full");
		//need to fix this and make dynamic;
		//CountryList = new String[][]{{"US", "United States"}, {"CA", "Canada"}};
		//CountryList = new String[][]{{"CA", "Canada"}};
		//CountryList = new String[][]{{"US", "United States"}};
		//CountryList = new String[][]{{"JP", "Japan"}};
		//CountryList = new String[][]{{"BR", "Brazil"}};
		//CountryList = new String[][]{{"GB", "Great Brittan"}};
		//CountryList = new String[][]{{"SI", "Slovenia"}, {"RO", "Romania"}};
		//CountryList = new String[][]{{"JP", "Japan"}, {"MY", "Malaysia"}, {"SG", "Singapore"}, {"AU", "Australia"}, {"NZ", "New Zealand"}, {"HK", "Hong Kong"}, {"TW", "Taiwan"}, {"TH", "Thailand"}};
		//CountryList = new String[][]{{"AU", "Australia"}, {"NZ", "New Zealand"}, {"HK", "Hong Kong"}, {"TH", "Thailand"}};
	}
	
	@DataProvider (parallel = true)
	public static Iterator<Object[]> dp(Method m) {
		List<Object[]> data = new ArrayList<Object[]>();

		for (int i=0; i < Environment.LevelsToTest.length(); i++) {
			String Level = String.valueOf(Environment.LevelsToTest.charAt(i)), Account = null;
			int intLevel = Integer.parseInt(Level);
			//int intLevel = Integer.parseInt(Level);
			
			switch (m.getName()) { //Based on the method that is being called the array list will be populated.
		    	case "CreditCardRegistrationEnroll":
		    		for (int j = 0; j < CountryList.length; j++) {
		    			String EnrollmentID[] = Helper_Functions.LoadEnrollmentIDs(CountryList[j][0]);
		    			if (EnrollmentID != null) {
		    				data.add( new Object[] {Level, EnrollmentID});
		    			}
					}
		    		break;
		    	case "UserRegistration":
		    		data.add( new Object[] {Level, "US"});
		    		break;
		    	case "Address_Mismatch":
		    		if (Level == "7") {
		    			break;
		    		}
		    		Account = Helper_Functions.getExcelFreshAccount(Level, "us", true);
		    		data.add( new Object[] {Level, "US", Account, ""});
		    		data.add( new Object[] {Level, "US", Account, "Nickname"});
		    		break;
		    	case "AccountRegistration_Admin":
		    		if (Level == "7") {
		    			break;
		    		}
		    		for (int j = 0; j < CountryList.length; j++) {
		    			Account = Helper_Functions.getExcelFreshAccount(Level, CountryList[j][0], true);
		    			if (Account != null) {
		    				data.add( new Object[] {Level, CountryList[j][0], Account});
		    			}
					}
		    		//data.add( new Object[] {Level, "IN", "781428280"});///need to delete
		    		break;
		    	case "AccountRegistration_INET":
		    	case "AccountRegistration_WDPA":
		    	case "AccountRegistration_GFBO":
		    		for (int j = 0; j < CountryList.length; j++) {
		    			Account = Helper_Functions.getExcelFreshAccount(Level, CountryList[j][0], true);
			    		data.add( new Object[] {Level, CountryList[j][0], Account});
					}
		    		break;
		    	case "AccountRegistration_Linkage":
		    		for (int j = 0; j < CountryList.length; j++) {
		    			Account = Helper_Functions.getExcelFreshAccount(Level, CountryList[j][0], true);
			    		data.add( new Object[] {Level, CountryList[j][0], Account, "Nickname"});
			    		data.add( new Object[] {Level, CountryList[j][0], Account, ""});
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
		    	case "WADM_Invitaiton":
		    		UD = Environment.Get_UserIds(intLevel);
		    		for (int k = 0; k < UD.length; k++) {
	    				if (UD[k].SSO_LOGIN_DESC.contains("CC")) {
	    					data.add( new Object[] {Level, UD[k].SSO_LOGIN_DESC, UD[k].USER_PASSWORD_DESC, Helper_Functions.MyEmail});
	    					break;
	    				}
	    			}
		    		break;
			}
		}	
		return data.iterator();
	}

	@Test(dataProvider = "dp")
	public void CreditCardRegistrationEnroll(String Level, String EnrollmentID[]) {
		try {
			String CreditCard[] = Helper_Functions.LoadCreditCard("V");
			String CountryCode = EnrollmentID[1];
			String ShippingAddress[] = Helper_Functions.LoadAddress(CountryCode), BillingAddress[] = ShippingAddress;
			String UserId = Helper_Functions.LoadUserID("L" + Level + CountryCode + Thread.currentThread().getId() + "CC");
			String ContactName[] = Helper_Functions.LoadDummyName(CountryCode + "CC", Level);
			String[] TaxInfo = Helper_Functions.getTaxInfo(CountryCode).get(0);
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
			String Result = Arrays.toString(WFCL_Functions.WFCL_UserRegistration(UserID, ContactName, Helper_Functions.MyEmail, Address));
			Helper_Functions.PrintOut(Result, false);
		}catch (Exception e) {
			Assert.fail(e.getMessage());
		}
	}
	
	@Test(dataProvider = "dp", priority = 3)//since this method will consume an acocunt number run after others have completed
	public void AccountRegistration_Admin(String Level, String CountryCode, String Account) {
		try {			
			String UserName[] = Helper_Functions.LoadDummyName("INET", Level);
			String UserID = Helper_Functions.LoadUserID("L" + Level + Account + CountryCode);
			String AddressDetails[] = Helper_Functions.AccountDetails(Account);
			String Result[] = WFCL_Functions.WFCL_AccountRegistration_INET(UserName, UserID, Helper_Functions.MyEmail, Account, AddressDetails);
			Result = Arrays.copyOf(Result, Result.length + 1);
			Result[Result.length - 1] = "Admin:" + WFCL_Functions.Admin_Registration(CountryCode, Account);
			Helper_Functions.PrintOut(Arrays.toString(Result), false);
		}catch (Exception e) {
			Assert.fail(e.getMessage());
		}
	}
	
	@Test(dataProvider = "dp", priority = 1)
	public void AccountRegistration_INET(String Level, String CountryCode, String Account){
		try {
			
			Account = "167267025";
			//Account = "332082643";
			Helper_Functions.MyEmail = "accept@fedex.com";
			String AddressDetails[] = Helper_Functions.AccountDetails(Account);
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
	public void AccountRegistration_WDPA(String Level, String CountryCode, String Account){
		try {
			//Account = "754244860";
			String AddressDetails[] = Helper_Functions.AccountDetails(Account);
			String ContactName[] = Helper_Functions.LoadDummyName("WDPA", Level);
			String UserID = Helper_Functions.LoadUserID("L" + Level + Thread.currentThread().getId() + "WDPA");
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
	
	@Test(dataProvider = "dp", enabled = false)
	public void WADM_Invitaiton(String Level, String AdminUser, String Password, String Email){     //check on this, make sure that testing correct
		try {
			String ContactName[] = Helper_Functions.LoadDummyName("WADMInvite", Level);
			String Result[] = WFCL_Functions.WFCL_WADM_Invitaiton(AdminUser, Password, ContactName, Email);
			Helper_Functions.PrintOut(Arrays.toString(Result), false);
		}catch (Exception e) {
			Assert.fail(e.getMessage());
		}
	};
	  
	@Test(dataProvider = "dp", enabled = false)
	public void AccountRegistration_GFBO(String Level, String CountryCode, String Account){     //check on this, make sure that testing correct
		try {
			String AddressDetails[] = Helper_Functions.AccountDetails(Account);
			String ContactName[] = Helper_Functions.LoadDummyName(CountryCode, Level);
			String UserID = Helper_Functions.LoadUserID("L" + Level + "GFBO" + CountryCode);
			
			String Result[] = WFCL_Functions.WFCL_AccountRegistration_INET(ContactName, UserID, Helper_Functions.MyEmail, Account, AddressDetails);
			Result = Arrays.copyOf(Result, Result.length + 1);
			Result[Result.length - 1] = "GFBO:" + WFCL_Functions.WFCL_AccountRegistration_GFBO(ContactName, UserID, Helper_Functions.MyEmail, Account, AddressDetails);
			Helper_Functions.PrintOut(Arrays.toString(Result), false);
		}catch (Exception e) {
			Assert.fail(e.getMessage());
		}
	};
	
	@Test(dataProvider = "dp", priority = 3, enabled = true)//since this method will consume an acocunt number run after others have completed
	public void Address_Mismatch(String Level, String CountryCode, String FreshAccount, String Nickname) {
		try {
			String UserName[] = Helper_Functions.LoadDummyName("INET", Level);
			String UserID = Helper_Functions.LoadUserID("L" + Level + FreshAccount + CountryCode + Nickname);
			String AddressDetails[] = Helper_Functions.AccountDetails(FreshAccount);
			String AddressMismatch[] = new String[AddressDetails.length];
			System.arraycopy( AddressDetails, 0, AddressMismatch, 0, AddressDetails.length );
			//update the address with different data
			AddressMismatch[2] = "MEMPHIS";
			AddressMismatch[4] = "TN";
			AddressMismatch[5] = "38119";
			String Result = WFCL_Functions.WFCL_AdminReg_WithMismatch(UserName, UserID, Helper_Functions.MyEmail, FreshAccount, AddressDetails, AddressMismatch, Nickname);
			Helper_Functions.PrintOut(Result, false);
		}catch (Exception e) {
			Assert.fail(e.getMessage());
		}
	}
	
	@Test(dataProvider = "dp", priority = 1)
	public void AccountRegistration_Linkage(String Level, String CountryCode, String Account, String AccountNickname){
		try {
			//Account = "762983940"; //to manually force account number
			//Helper_Functions.MyEmail = "suguna.kumaraswami.osv@fedex.com";
			String AddressDetails[] = Helper_Functions.AccountDetails(Account);
			String ContactName[] = Helper_Functions.LoadDummyName(CountryCode, Level);
			String UserID = Helper_Functions.LoadUserID("L" + Level + "Link" + CountryCode);
			String Result = WFCL_Functions.WFCL_AccountLinkage(ContactName, UserID, Helper_Functions.MyEmail, Account, AddressDetails, AccountNickname);
			Helper_Functions.PrintOut(Result, false);
		}catch (Exception e) {
			Assert.fail(e.getMessage());
		}
	}
	
	
	
	  /*

	
	//Non smoke test scenarios

	//@Test
	public void WFCL_AccountRegistration_Rewards(){
		if (Environment == 7) { //Set to not work in production due to limited test data
			Assert.fail();
		}
		
		String Results[] = null;
		try {
			String LocAccountNumber = NonAdminAccounts[0];//us account
		String AccountAddress[] = Helper_Functions.AccountDetails(LocAccountNumber);
			Results = WFCL_RewardsRegistration(Helper_Functions.LoadDummyName(AccountAddress[6]), Helper_Functions.LoadUserID(strLevel + "Rewards"), LocAccountNumber, AccountAddress);
		}catch (Exception e) {
			Assert.fail();
		}
		PassOrFail = true;
		ResultsList.set(TestNumber, UpdateArrayList(ResultsList.get(TestNumber), 1, Arrays.toString(Results)));
	}
	
	
	
	//@Test
	public void WFCL_GFBO_Login(){
		try {
			WFCL_GFBO_Login(strGFBO, strPassword, "US");
		}catch (Exception e) {
			Assert.fail();
		}
		PassOrFail = true;
		ResultsList.set(TestNumber, UpdateArrayList(ResultsList.get(TestNumber), 1, strGFBO));
	};	
	
	//@Test
	public void WFCL_AccountRegistration_ISGT(){     //NEED to test
		String LocAccountNumber = NonAdminAccounts[0];//us account
		String Results[] = null;
		try {
			String Address[] = null;
			
			if (Environment == 7) {
				Address = Helper_Functions.LoadAddress("US", "TN", "10 FedEx Parkway");
			}else {
				Address = Helper_Functions.AccountDetails(LocAccountNumber);
			}
			
			Results = WFCL_AccountRegistration_ISGT(Helper_Functions.LoadDummyName(Address[6]), Helper_Functions.LoadUserID(strLevel + "ISGT"), LocAccountNumber, Address);
		}catch (Exception e) {
			Assert.fail();
		}
		PassOrFail = true;
		ResultsList.set(TestNumber, UpdateArrayList(ResultsList.get(TestNumber), 1, Arrays.toString(Results)));
	}

	//@Test
	public void WFCL_WADM_Invitaiton(){     //check on this, make sure that testing correct
		String Results[] = null;
		try {
			Results = WFCL_WADM_Invitaiton(strDummyWADMAdmin, strPassword, Helper_Functions.LoadDummyName("FBO"), strEmail);
		}catch (Exception e) {
			Assert.fail();
		}
		PassOrFail = true;
		ResultsList.set(TestNumber, UpdateArrayList(ResultsList.get(TestNumber), 1, Arrays.toString(Results)));
	};
	
	//@Test
	public void WFCL_GFBO_Invitaiton(){     //check on this, not sure how to get this working, GFBO does not reference elements and ids correctly
		String Results[] = null;
		try {
			Results = WFCL_GFBO_Invitaiton(strGFBO, strPassword, Helper_Functions.LoadDummyName("FBO"), strEmail, "2");
		}catch (Exception e) {
			Assert.fail();
		}
		PassOrFail = true;
		ResultsList.set(TestNumber, UpdateArrayList(ResultsList.get(TestNumber), 1, Arrays.toString(Results)));
	};	
	
	//@Test
	public void WFCL_CreditCardRegistration_US_ErrorMessage() {   //PPM#45905- Change Verbiage of CC Failure in OADR
		for (int i = 0; i < EnrollmentIDs.length; i++){
			if (EnrollmentIDs[i][1].contentEquals("US")){
				String EnrollmentId = EnrollmentIDs[i][0];
				try {
					String ErrorMessage = "FedEx Online Account Registration is not able to process your request at this time. Please call 1.800.463.3339 and ask for \"new account setup\" to connect with a new account customer service representative."; 
					WFCL_CreditCardRegistration_Error(Helper_Functions.LoadCreditCard("I"), EnrollmentId, Helper_Functions.LoadAddress("US"), Helper_Functions.LoadDummyName(), Helper_Functions.LoadUserID(strLevel), ErrorMessage);
				} catch (Exception e) {
					Assert.fail();
				}
				PassOrFail = true;
				ResultsList.set(TestNumber, UpdateArrayList(ResultsList.get(TestNumber), 1, "Correct"));
				break;
			}
		}
	}
	
	//@Test
	public void WFCL_UserRegistration_Captcha() {
		
		boolean Results = false;
		try {
			Results = WFCL_Captcha(strTodaysDate, Helper_Functions.LoadDummyName(), Helper_Functions.LoadAddress("US"));
		} catch (Exception e) {
			Assert.fail();
		}
		PassOrFail = true;
		ResultsList.set(TestNumber, UpdateArrayList(ResultsList.get(TestNumber), 1, Results + ""));
	}
	
	//@Test
	public void WFCL_UserRegistration_Captcha_Legacy() {
		boolean Results = false;
		try {
			Results = WFCL_Captcha_Legacy(strTodaysDate, Helper_Functions.LoadDummyName(), Helper_Functions.LoadAddress("US"));
		} catch (Exception e) {
			Assert.fail();
		}
		PassOrFail = true;
		ResultsList.set(TestNumber, UpdateArrayList(ResultsList.get(TestNumber), 1, Results + ""));
	}

	 */
	
    
}
