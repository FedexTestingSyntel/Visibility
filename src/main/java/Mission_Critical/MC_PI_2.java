package Mission_Critical;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import org.testng.annotations.Test;
import Data_Structures.Account_Data;
import Data_Structures.User_Data;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Listeners;
import org.openqa.selenium.By;
import org.testng.Assert;
import org.testng.SkipException;
import org.testng.annotations.BeforeClass;
import java.util.Iterator;
import java.util.List;
import SupportClasses.DriverFactory;
import SupportClasses.Environment;
import SupportClasses.Helper_Functions;
import SupportClasses.WebDriver_Functions;
import WFCL_Application.WFCL_Functions;
import WIDM_Application.WIDM_Functions;

@Listeners(SupportClasses.TestNG_TestListener.class)

public class MC_PI_2{
	static String LevelsToTest = "3";
	static String CountryList[][];
	final boolean EnableCompleted = false;
	
	@BeforeClass
	public void beforeClass() {
		Environment.SetLevelsToTest(LevelsToTest);
		CountryList = Environment.getCountryList("smoke");
		//CountryList = Environment.getCountryList("fr");
	}
	
	@DataProvider (parallel = true)
	public static Iterator<Object[]> dp(Method m) {
		List<Object[]> data = new ArrayList<Object[]>();

		for (int i=0; i < Environment.LevelsToTest.length(); i++) {
			String Level = String.valueOf(Environment.LevelsToTest.charAt(i));
			int intLevel = Integer.parseInt(Level);
			
			//Special characters as from Stories 428282 and 428283
			String Invalid_Email[] = new String[] {"tencharacttencharacttencharacttencharacttencharacttencharact1234@tencharacttencharacttencharacttencharacttencharacttencharacttencharacttencharacttencharacttencharacttencharacttencharacttencharacttencharacttencharacttencharacttencharacttencharact12345678.com", "@Grp2DOTCOMsyntelinc.com","Grp2@DOTCOM@syntelinc.com", "Grp2DOTCOM@syntelinc", "Grp2DOTCOMsyntelinc.com", "a@.c", "@b.c", "a@b.", ".GRP2DOTCOM@syntelinc.com", "tencharacttencharacttencharacttencharacttencharacttencharact12345@accept.com"};
			String Invalid_Email_Special[] = new String[] {"acc ept@gmail.com", "abc@@cdf.dfg", "ac,c@fed.com", "ac<c@fed.com", "ac#c@fed.com", "ac>c@fed.com", "a$cc@fed.com", "a%cc@fed.com", "ac;c@fed.com", "a:cc@fed.com", "ac\"c@fed.com", "ac[c@fed.com", "a]cc@fed.com", "ac*c@fed.com", "ac\\c@fed.com", "ac|c@fed.com", "acc`@fed.com", "a!cc@fed.com"};

			//String Invalid_Email_Special[] = new String[] {"acc`@fed.com", "ac*c@fed.com", "ac[c@fed.com", "ac\\c@fed.com", "ac\"c@fed.com"};
			
			String Valid_Emails[] = new String[] {"accept@tencharacttencharacttencharacttencharacttencharacttencharacttencharacttencharacttencharacttencharacttencharacttencharacttencharacttencharacttencharacttencharacttencharacttencharact1234567.com","a@b.c", "tencharacttencharacttencharacttencharacttencharacttencharact1234@accept.com", "tencharacttencharacttencharacttencharacttencharacttencharact1234@tencharacttencharacttencharacttencharacttencharacttencharacttencharacttencharacttencharacttencharacttencharacttencharacttencharacttencharacttencharacttencharacttencharacttencharact1234567.com"};	
			String Valid_Emails_Special[] = new String[] {"ac=c@fed.com", "a.cc@fed.com", "a/cc@fed.com", "a?cc@fed.com", "a^cc@fed.com", "ac'c@fed.com", "ac&c@fed.com", "ac{c@fed.com", "ac}c@fed.com", "ac(c@fed.com", "ac)c@fed.com", "a~cc@fed.com", "ac_c@fed.com", "a-cc@fed.com", "a+cc@fed.com", "s-r'i=p./?a^&r{}n(a)_+r@fedex.com"};			

			//The below special char
			
			switch (m.getName()) { //Based on the method that is being called the array list will be populated.
				case "WIDM_Registration_Email_Validation":
				case "WFCL_Registration_Email_Validation":
				case "WFCL_Forgot_UserID_Email_Validation":
				case "WIDM_Forgot_UserID_Email_Validation":
					for (int j=0; j < CountryList.length; j++) {				
						for (String Email: Invalid_Email) {
							data.add( new Object[] {Level, CountryList[j][0], Email, true});
						}
						for (String Email: Valid_Emails) {
							data.add( new Object[] {Level, CountryList[j][0], Email, false});
						}
						
						for (String Email: Invalid_Email_Special) {
							data.add( new Object[] {Level, CountryList[j][0], Email, true});
						}
						for (String Email: Valid_Emails_Special) {
							data.add( new Object[] {Level, CountryList[j][0], Email, false});
						}
					}
					break;
				case "WIDM_Registration_Email_Error":
					for (int j=0; j < CountryList.length; j++) {				
						data.add( new Object[] {Level, CountryList[j][0], Invalid_Email, true});
						data.add( new Object[] {Level, CountryList[j][0], Invalid_Email_Special, true});
						data.add( new Object[] {Level, CountryList[j][0], Valid_Emails, false});
						data.add( new Object[] {Level, CountryList[j][0], Valid_Emails_Special, false});
					}
					break;
					
				case "WIDM_Reset_Password_Secret_SamePassword_Error":
				case "WFCL_Reset_Password_Secret_SamePassword_Error":
					User_Data UD[] =  Environment.Get_UserIds(intLevel);
					for (int j = 0; j < CountryList.length; j++) {
		    			for (int k = 0; k < UD.length; k++) {
		    				if ((m.getName().contains("WIDM") && UD[k].SSO_LOGIN_DESC.contains("WIDM")) || (m.getName().contains("WFCL") && UD[k].SSO_LOGIN_DESC.contains("Create"))) {
		    					data.add( new Object[] {Level, CountryList[j][0], UD[k].SSO_LOGIN_DESC, UD[k].USER_PASSWORD_DESC, UD[k].SECRET_ANSWER_DESC});
		    					UD[k].SSO_LOGIN_DESC = "";
		    					break;
		    				}
		    			}
					}
		    		break;
				case "WIDM_Email_BounceBack":
					if (Level.contentEquals("2")) {
						data.add( new Object[] {Level, "L2BadEmail110718T134301xv", "Test1234", Helper_Functions.getRandomString(22) + "@gmail.com"});
					}else if (Level.contentEquals("3")) {
						data.add( new Object[] {Level, "L3USCreate120518T194625px", "Test1234", Helper_Functions.getRandomString(22) + "@gmail.com"});
					}
					break;
				case "WIDM_Force_Change_Password":
					if (Level.contentEquals("2")) {
						data.add( new Object[] {Level, "L2UpdatePassword122818T141800oo", "Test1234"});
					}else if (Level.contentEquals("3")) {
						data.add( new Object[] {Level, "L3WIDMUS101618T162632", "Test1234"});
					}
					break;
				case "WFCL_Force_Change_Password":
					if (Level.contentEquals("2")) {
						data.add( new Object[] {Level, "L2UpdatePassword122718T135503lf", "Test1234"});
					}
					break;
				case "WFCL_Registration_Email_Validation_Legacy":
					for (int j=0; j < CountryList.length; j++) {				
						for (String Email: Invalid_Email) {
							Account_Data AccountDetails = Helper_Functions.getFreshAccount(Level,  "US");
							String UserID = Helper_Functions.LoadUserID("L" + Level + "InvalidWFCLLegacy");
				    		data.add( new Object[] {Level, AccountDetails, UserID, Email, true});
						}
						for (String Email: Valid_Emails) {
							Account_Data AccountDetails = Helper_Functions.getFreshAccount(Level,  "US");
							String UserID = Helper_Functions.LoadUserID("L" + Level + "WFCLLegacy");
				    		data.add( new Object[] {Level, AccountDetails, UserID, Email, false});
						}
					}
					break;
				case "WFCL_Reset_Password_Email_Validation":
					for (int j=0; j < CountryList.length; j++) {				
						for (String Email: Invalid_Email) {
							boolean ErrorExpected = false;
							if (Email.length() < 5) {
								ErrorExpected = true;//only for the min and max length should we get error.
							}
							data.add( new Object[] {Level, CountryList[j][0], Email, ErrorExpected});
						}
						for (String Email: Valid_Emails) {
							data.add( new Object[] {Level, CountryList[j][0], Email, false});
						}
					}
					break;
		    	case "BR_TaxID":
	    			String EnrollmentID[] = Helper_Functions.LoadEnrollmentIDs("BR");
	    			ArrayList<String[]> TaxInfo = Helper_Functions.getTaxInfo("BR");
	    			for (String Tax[]: TaxInfo) {
	    				if (Tax[4].contentEquals("B")) {
	    					data.add(new Object[] {Level, EnrollmentID, Tax, true});		//REMOVE Comment after demo
	    				}else {
	    					data.add(new Object[] {Level, EnrollmentID, Tax, false});
	    				}
	    				
	    			}
	    			break;
		    	case "TH_Welcome_Email_Disabled":
	    			EnrollmentID = Helper_Functions.LoadEnrollmentIDs("TH");
	    			data.add(new Object[] {Level, EnrollmentID[0], "TH", null, false});
	    			data.add(new Object[] {Level, EnrollmentID[0], "TH", null, true});
	    			break; 
				case "WFCL_Email_BounceBack_Email_Error":
					for (int j=0; j < CountryList.length; j++) {
						String UserID = "", Password = "Test1234";
						if (intLevel == 2) {
							UserID = "L2UpdatePassword012919T123552nj";
						}
						
						//data.add( new Object[] {Level, CountryList[j][0], UserID, Password, Invalid_Email, true});
						//data.add( new Object[] {Level, CountryList[j][0], UserID, Password, Invalid_Email_Special, true});
						data.add( new Object[] {Level, CountryList[j][0], UserID, Password, Valid_Emails, false});
						data.add( new Object[] {Level, CountryList[j][0], UserID, Password, Valid_Emails_Special, false});
					}
					break;
	    		
			}
		}
		System.out.println("Starting " + m.getName() + " : There are " + data.size() + " scenarios.");
		return data.iterator();
	}
	
	@Test(dataProvider = "dp", description = "411835", enabled = true)
	public void WIDM_Registration_Email_Validation(String Level, String CountryCode, String Email, boolean ErrorExpected){
		try {
			String UserId = Helper_Functions.LoadUserID("L" + Level + "WIDM" + CountryCode);
			WebDriver_Functions.ChangeURL("WIDM", CountryCode, true);
			WebDriver_Functions.Click(By.linkText("Sign Up Now!"));
			WIDM_Functions.WIDM_Registration_Input(null, null, UserId, Email);
			WebDriver_Functions.Click(By.id("createUserID"));
			
			if (WebDriver_Functions.CheckBodyText("Email address is not valid.") && ErrorExpected) {
				WebDriver_Functions.WaitForText(By.id("emailinvalid"), "Email address is not valid.");
				WebDriver_Functions.takeSnapShot("Invalid Email.png");
				Helper_Functions.PrintOut("Email Address: " + Email + " validated. Error message recieved.", false);
			}else if (WebDriver_Functions.CheckBodyText("Email address is not valid.")){
				throw new Exception("Error present on page.");
			}else {
				WebDriver_Functions.takeSnapShot("Valid Email.png");
				Helper_Functions.PrintOut("Email Address: " + Email + " validated.", false);
			}
		}catch (Exception e) {
			Assert.fail(e.getLocalizedMessage());
		}
	}
	
	@Test(dataProvider = "dp", description = "411835", enabled = true)
	public void WIDM_Registration_Email_Error(String Level, String CountryCode, String[] Emails, boolean ErrorExpected){
		try {
			WebDriver_Functions.ChangeURL("WIDM", CountryCode, true);
			WebDriver_Functions.Click(By.linkText("Sign Up Now!"));
			WIDM_Functions.WIDM_Registration_Input(null, null, "", "accept@fedex.com");
			
			String IncorrectResponse = "";
			
			for (int i = 0 ; i < Emails.length; i++) {
				WebDriver_Functions.Type(By.id("email"), Emails[i]);
				WebDriver_Functions.Type(By.id("retypeEmail"), Emails[i]);
				WebDriver_Functions.Click(By.id("createUserID"));
				
				if (ErrorExpected && WebDriver_Functions.CheckBodyText("Email address is not valid.")) {
					WebDriver_Functions.WaitForText(By.id("emailinvalid"), "Email address is not valid.");
					WebDriver_Functions.takeSnapShot("Invalid Email.png");
					Helper_Functions.PrintOut("Email Address: " + Emails[i] + " validated. Error message recieved.", false);
				}else if (ErrorExpected || WebDriver_Functions.CheckBodyText("Email address is not valid.")){
					IncorrectResponse += Emails[i];
				}
			}
			
			if (!IncorrectResponse.contentEquals("")) {
				Assert.fail(IncorrectResponse);
			}
			
		}catch (Exception e) {
			Assert.fail(e.getLocalizedMessage());
		}
	}
	
	@Test(dataProvider = "dp", description = "411832", enabled = EnableCompleted)
	public void WIDM_Forgot_UserID_Email_Validation(String Level, String CountryCode, String Email, boolean ErrorExpected) {
		try {
			try {
				WIDM_Functions.Forgot_User_WIDM(CountryCode, Email);
			}catch (Exception e1) {
				if (ErrorExpected) {
					WebDriver_Functions.WaitPresent(By.id("emailerror"));
					WebDriver_Functions.WaitForText(By.id("emailerror"), "Email address is not valid.");
					WebDriver_Functions.takeSnapShot("Invalid Email.png");
				}else {
					throw e1;
				}
			}
		}catch (Exception e) {
			Check_If_EFWS_Failure();
			Assert.fail(e.getMessage());
		}
	}
	
	@Test(dataProvider = "dp", description = "420303", enabled = EnableCompleted)
	public void WIDM_Email_BounceBack(String Level, String UserID, String Password, String Email){
		try {
			WebDriver_Functions.ChangeURL("WGTM_FID", "US", true);
			WebDriver_Functions.Type(By.id("username"), UserID);
			WebDriver_Functions.Type(By.id("password"), Password);
			WebDriver_Functions.Click(By.name("login"));
			
			WebDriver_Functions.WaitForText(By.className("uid"), UserID);
			WebDriver_Functions.takeSnapShot(" BounceBack Page.png");
			WebDriver_Functions.Type(By.name("email"), Email);
			WebDriver_Functions.Type(By.name("retypeEmail"), Email);
			WebDriver_Functions.Click(By.name("submit"));
			
			WebDriver_Functions.WaitForTextPresentIn(By.xpath("//*[@id='content']/div/table/tbody/tr[1]/td/table/tbody/tr/td/table/tbody/tr[1]/td"), "Please click on the confirmation link in the email to complete updating your information.");
			WebDriver_Functions.takeSnapShot(" BounceBack Submission.png");
		}catch (Exception e) {
			Assert.fail(e.getMessage());
		}
	}

	@Test(dataProvider = "dp", description = "411836", enabled = EnableCompleted)
	public void WFCL_Registration_Email_Validation(String Level, String CountryCode, String Email, boolean ErrorExpected) {
		try {
			String Address[] = Helper_Functions.LoadAddress(CountryCode);
			String ContactName[] = Helper_Functions.LoadDummyName("Create", Level);
			String UserId = Helper_Functions.LoadUserID("L" + Level + "WFCL" + CountryCode);
			if (ErrorExpected) {
				WebDriver_Functions.ChangeURL("Pref", CountryCode, true);//navigate to email preferences page to load cookies
				WebDriver_Functions.Click(By.id("registernow"));
				WFCL_Functions.WFCL_ContactInfo_Page(ContactName, Address, UserId, Email, true); //enters all of the details
				WebDriver_Functions.WaitForText(By.id("emailinvalid"), "Email address is not valid.");
				WebDriver_Functions.takeSnapShot("Invalid Email.png");
			}else {
				WFCL_Functions.WFCL_UserRegistration(UserId, ContactName, Email, Address);
				Helper_Functions.PrintOut("Email Address: " + Email + " validated.", false);
			}
		}catch (Exception e) {
			Check_If_EFWS_Failure();
			Assert.fail(e.getMessage());
		}
	}
	
	@Test(dataProvider = "dp", description = "411836", enabled = EnableCompleted)
	public void WFCL_Registration_Email_Validation_Legacy(String Level, Account_Data AccountDetails, String UserID, String Email, boolean ErrorExpected){
		try {
			String Account = AccountDetails.Account_Number;
			String AddressDetails[] = new String[] {AccountDetails.Billing_Address_Line_1, AccountDetails.Billing_Address_Line_2, AccountDetails.Billing_City, AccountDetails.Billing_State, AccountDetails.Billing_State_Code, AccountDetails.Billing_Zip, AccountDetails.Billing_Country_Code};
			String ContactName[] = Helper_Functions.LoadDummyName("FCL", Level);
			if (ErrorExpected) {
				try {
					DriverFactory.getInstance().getDriverWait().wait(3);
					WFCL_Functions.WDPA_Registration(ContactName, UserID, Email, Account, AddressDetails);
				}catch (Exception ExpectedError) {
				}finally {
					DriverFactory.getInstance().getDriverWait().wait(DriverFactory.WaitTimeOut);
				}
				WebDriver_Functions.WaitPresent(By.id("emailinvalid"));
				WebDriver_Functions.WaitForText(By.id("emailinvalid"), "Email address is not valid.");
				WebDriver_Functions.takeSnapShot("Invalid Email.png");
			}else {
				String Result[] = WFCL_Functions.WDPA_Registration(ContactName, UserID, Email, Account, AddressDetails);
				Helper_Functions.PrintOut(Arrays.toString(Result), false);
			}
		}catch (Exception e) {
			Check_If_EFWS_Failure();
			Assert.fail(e.getMessage());
		}
	}
	
	@Test(dataProvider = "dp", description = "411833", enabled = EnableCompleted)
	public void WFCL_Forgot_UserID_Email_Validation(String Level, String CountryCode, String Email, boolean ErrorExpected) {
		try {
			try {
				WFCL_Functions.Forgot_User_Email(CountryCode, Email);
			}catch (Exception e1) {
				if (ErrorExpected) {
					WebDriver_Functions.WaitPresent(By.id("emailerror"));
					WebDriver_Functions.WaitForText(By.id("emailerror"), "Email address is not valid.");
					WebDriver_Functions.takeSnapShot("Invalid Email.png");
				}else {
					throw e1;
				}
			}
		}catch (Exception e) {
			Check_If_EFWS_Failure();
			Assert.fail(e.getMessage());
		}
	}

	@Test(dataProvider = "dp", description = "411826", enabled = EnableCompleted)
	public void WFCL_Reset_Password_Email_Validation(String Level, String CountryCode, String Email, boolean ErrorExpected) {
		try {
			WebDriver_Functions.ChangeURL("INET", CountryCode, true);
    		WebDriver_Functions.Click(By.name("forgotUidPwd"));
    		WebDriver_Functions.Type(By.name("userID"), Email);
    		
    		WebDriver_Functions.takeSnapShot("Password Reset.png");
    		WebDriver_Functions.Click(By.id("ada_forgotpwdcontinue"));
    		if (ErrorExpected) {
    			WebDriver_Functions.WaitForText(By.id("useriderror"), "The User ID you entered is invalid.");
    			WebDriver_Functions.takeSnapShot("Password Reset Error Message.png");
    		}else {
    			if (!WebDriver_Functions.CheckBodyText("Reset your password by answering the secret question associated with your fedex.com User ID.")) {
    				throw new Exception("User did not process to next step of reset password flow.");
    			}
    		}
		}catch (Exception e) {
			Assert.fail(e.getMessage());
		}
	}

	@Test(dataProvider = "dp", description = "397664", enabled = EnableCompleted)
	public void WIDM_Reset_Password_Secret_SamePassword_Error(String Level, String CountryCode, String UserId, String Password, String SecretAnswer){
		try {
			WIDM_Functions.ResetPasswordWIDM_Secret(CountryCode, UserId, Password, SecretAnswer, true);
		}catch (Exception e) {
			Assert.fail(e.getMessage());
		}
	}
	
	@Test(dataProvider = "dp", description = "397668",  enabled = EnableCompleted)
	public void WFCL_Reset_Password_Secret_SamePassword_Error(String Level, String Country, String UserId, String Password, String SecretAnswer){
		try {
			String Result = WFCL_Functions.WFCL_Secret_Answer(Country, UserId, Password, SecretAnswer, true);
			Helper_Functions.PrintOut(Result, false);
		}catch (Exception e) {
			Assert.fail(e.getMessage());
		}
	}
	
	@Test(dataProvider = "dp", description = "419457", enabled = true)    //EnableCompleted
	public void BR_TaxID(String Level, String EnrollmentID[], String VatNumber[], boolean BuisnessAccount) {
		try {
			String CreditCard[] = Helper_Functions.LoadCreditCard("M");
			String ShippingAddress[] = Helper_Functions.LoadAddress("BR"), BillingAddress[] = ShippingAddress;
			String UserId = Helper_Functions.LoadUserID("L" + Level + EnrollmentID + "CC");
			String ContactName[] = Helper_Functions.LoadDummyName("BRCC", Level);

			String Result[] = WFCL_Functions.CreditCardRegistrationEnroll(EnrollmentID, CreditCard, ShippingAddress, BillingAddress, ContactName, UserId, Helper_Functions.MyEmail, BuisnessAccount, VatNumber);
			Helper_Functions.PrintOut(Arrays.toString(Result), false);
		}catch (Exception e) {
			Assert.fail(e.getMessage());
		}
	}
	
	@Test(dataProvider = "dp", description = "414270", enabled = true)   
	public void TH_Welcome_Email_Disabled(String Level, String EnrollmentID[], String VatNumber[], boolean BuisnessAccount) {
		try {
			String CreditCard[] = Helper_Functions.LoadCreditCard("M");
			String CountryCode = EnrollmentID[1];
			String ShippingAddress[] = Helper_Functions.LoadAddress(CountryCode), BillingAddress[] = ShippingAddress;
			String UserId = Helper_Functions.LoadUserID("L" + Level + EnrollmentID + "CC");
			String ContactName[] = Helper_Functions.LoadDummyName(CountryCode + "CC", Level);

			String Result[] = WFCL_Functions.CreditCardRegistrationEnroll(EnrollmentID, CreditCard, ShippingAddress, BillingAddress, ContactName, UserId, Helper_Functions.MyEmail, BuisnessAccount, VatNumber);
			Helper_Functions.PrintOut(Arrays.toString(Result), false);
			Assert.fail("Need to validate that no email was recieved.");
		}catch (Exception e) {
			Assert.fail(e.getMessage());
		}
	}
	
	public static void Check_If_EFWS_Failure() {
		String EFWS_Message = "Your registration request is not approved based on the information submitted.";
		if (WebDriver_Functions.CheckBodyText(EFWS_Message)) {
			Helper_Functions.PrintOut("The \"" + EFWS_Message + "\" message is present on the page. Check the reason for the denied status.", false);
			throw new SkipException("Forcing skip, will be deleted from report later");
		}
	}
	
	@Test(dataProvider = "dp", description = "414215", enabled = true)
	public void WFCL_Email_BounceBack_Email_Error(String Level, String CountryCode, String UserID, String Password, String[] Emails, boolean ErrorExpected){
		try {
			WebDriver_Functions.Login(UserID, Password, "WFCL");
			
			String IncorrectResponse = "";
			
			for (int i = 0 ; i < Emails.length; i++) {
				WebDriver_Functions.Type(By.name("email"), Emails[i]);
				WebDriver_Functions.Type(By.name("retypeEmail"), Emails[i]);
				WebDriver_Functions.Click(By.name("submit"));
				
				if (ErrorExpected && WebDriver_Functions.CheckBodyText("Email address is not valid.")) {
					//added a space " " from 02/04/19 execution
					WebDriver_Functions.WaitForText(By.xpath("//*[@id='module.adminmessage._expanded']/table/tbody/tr[2]/td/table/tbody/tr[3]/td/b"), "Email address is not valid.  ");
					WebDriver_Functions.takeSnapShot("Invalid Email.png");
					Helper_Functions.PrintOut("Email Address: " + Emails[i] + " validated. Error message recieved.", false);
				}else if (ErrorExpected || WebDriver_Functions.CheckBodyText("Email address is not valid.") || WebDriver_Functions.isPresent(By.name("email"))){
					IncorrectResponse += Emails[i] + "   ";
				}
				
				if(!WebDriver_Functions.isPresent(By.name("email"))) {
					WebDriver_Functions.Login(UserID, Password, "WFCL");
				}
			}
			
			if (!IncorrectResponse.contentEquals("")) {
				Assert.fail(IncorrectResponse);
			}
			
		}catch (Exception e) {
			Assert.fail(e.getLocalizedMessage());
		}
	}
	
	/*
	The below user stories are related to the InfoSec screen and could not be automated.
	397668
	397668
	425257
	
	Password Flag	
	L1Akshay110/Test1234
	L1Akshay111/Test1234	
	L2UpdatePassword122718T135503lz/Test1234
	L2UpdatePassword122718T135503lf/Test1234
	
	UserID Flag	
	L1Akshay112/Test1234
	L1Akshay113/Test1234	
	L2UpdatePassword122718T135503xw/Test1234
	L2UpdatePassword122718T135503ta/Test1234
	
	SecretQuestion Flag	
	L1Akshay114/Test1234	
	L2UpdatePassword122718T135503rh/Test1234
	*/
}