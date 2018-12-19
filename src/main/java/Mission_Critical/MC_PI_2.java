package Mission_Critical;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;

import org.testng.annotations.Test;

import Data_Structures.Account_Data;

import org.testng.annotations.DataProvider;
import org.testng.annotations.Listeners;
import org.openqa.selenium.By;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import java.util.Iterator;
import java.util.List;
import SupportClasses.Environment;
import SupportClasses.Helper_Functions;
import SupportClasses.WebDriver_Functions;
import TestingFunctions.WIDM_Functions;

@Listeners(SupportClasses.TestNG_TestListener.class)

public class MC_PI_2{
	static String LevelsToTest = "2";
	static String CountryList[][];
	
	@BeforeClass
	public void beforeClass() {
		Environment.SetLevelsToTest(LevelsToTest);
		CountryList = Environment.getCountryList("smoke");
		//CountryList = new String[][]{{"US", "United States"}};
	}
	
	@DataProvider //(parallel = true)
	public static Iterator<Object[]> dp(Method m) {
		List<Object[]> data = new ArrayList<Object[]>();

		for (int i=0; i < Environment.LevelsToTest.length(); i++) {
			String Level = String.valueOf(Environment.LevelsToTest.charAt(i));
			String Invalid_Email[] = new String[] {"tencharacttencharacttencharacttencharacttencharacttencharact1234@tencharacttencharacttencharacttencharacttencharacttencharacttencharacttencharacttencharacttencharacttencharacttencharacttencharacttencharacttencharacttencharacttencharacttencharact12345678.com", " @,<#>$%;:”[]*\\|`!", "@Grp2_DOTCOMsyntelinc.com","Grp2@DOTCOM@syntelinc.com", "Grp2_DOTCOM@syntelinc", "Grp2_DOTCOMsyntelinc.com", "a@.c", ".GRP2_DOTCOM@syntelinc.com", "tencharacttencharacttencharacttencharacttencharacttencharact12345@accept.com"};
			String Valid_Emails[] = new String[] {"#NAME?/^&{}()~_+@fedex.com", "accept@tencharacttencharacttencharacttencharacttencharacttencharacttencharacttencharacttencharacttencharacttencharacttencharacttencharacttencharacttencharacttencharacttencharacttencharact1234567.com","a@b.c", "tencharacttencharacttencharacttencharacttencharacttencharact1234@accept.com", "tencharacttencharacttencharacttencharacttencharacttencharact1234@tencharacttencharacttencharacttencharacttencharacttencharacttencharacttencharacttencharacttencharacttencharacttencharacttencharacttencharacttencharacttencharacttencharacttencharact1234567.com", "GRP2_DOTCOMaaaa@syntelinc.com", "GRP2-DOTCOM@syntelinc.com"};		
						
			switch (m.getName()) { //Based on the method that is being called the array list will be populated.
				case "WIDM_Registration_Invalid_Email":
					for (int j=0; j < CountryList.length; j++) {				
						for (String Email: Invalid_Email) {
							data.add( new Object[] {Level, CountryList[j][0], Email});
						}
					}
					break;
				case "WIDM_Registration_Valid_Email":
					for (int j=0; j < CountryList.length; j++) {					 
						for (String Email: Valid_Emails) {
							data.add( new Object[] {Level, CountryList[j][0], Email});
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
				case "WFCL_Registration_Valid_Email":
					for (String Email: Valid_Emails) {
						data.add( new Object[] {Level, Helper_Functions.LoadUserID("L" + Level + "ValidEmail"), Email});
					}
					break;
				case "WFCL_Registration_Invalid_Email":
					for (String Email: Invalid_Email) {
						data.add( new Object[] {Level, Helper_Functions.LoadUserID("L" + Level + "InvalidEmail"), Email});
					}
					break;	
				case "WFCL_Registration_Valid_Email_Legacy":
					for (String Email: Valid_Emails) {
						Account_Data AccountDetails = Helper_Functions.getFreshAccount(Level,  "US");
						String UserID = Helper_Functions.LoadUserID("L" + Level + "WFCLLegacy");
			    		data.add( new Object[] {Level, AccountDetails, UserID, Email});
					}
					break;	
				case "WFCL_Registration_Invalid_Email_Legacy":
					for (String Email: Invalid_Email) {
						Account_Data AccountDetails = Helper_Functions.getFreshAccount(Level,  "US");
						String UserID = Helper_Functions.LoadUserID("L" + Level + "InvalidWFCLLegacy");
			    		data.add( new Object[] {Level, AccountDetails, UserID, Email});
					}
					break;	
				case "WFCL_Forgot_UserID_Email_Validation":
					for (int j=0; j < CountryList.length; j++) {	
						for (String Email: Invalid_Email) {
							data.add( new Object[] {Level, CountryList[j][0], Email});
						}
					}
					break;
					
					
			}
		}	
		return data.iterator();
	}

	@Test(dataProvider = "dp", description = "411835", enabled = true)
	public void WIDM_Registration_Valid_Email(String Level, String CountryCode, String Email){
		try {
			String Address[] = Helper_Functions.LoadAddress(CountryCode);
			String UserName[] = Helper_Functions.LoadDummyName("WIDM", Level);
			String UserId = Helper_Functions.LoadUserID("L" + Level + "WIDM" + CountryCode);
			WIDM_Functions.WIDM_Registration(Address, UserName, UserId, Email);
			Helper_Functions.PrintOut("Email Address: " + Email + " validated.", false);
		}catch (Exception e) {
			Assert.fail(e.getMessage());
		}
	}
	
	@Test(dataProvider = "dp", description = "411835", enabled = true)
	public void WIDM_Registration_Invalid_Email(String Level, String CountryCode, String Email){
		try {
			String Address[] = Helper_Functions.LoadAddress(CountryCode);
			String UserName[] = Helper_Functions.LoadDummyName("WIDM", Level);
			String UserId = Helper_Functions.LoadUserID("L" + Level + "WIDM" + CountryCode);
			WebDriver_Functions.ChangeURL("WIDM", CountryCode, true);
			WebDriver_Functions.Click(By.linkText("Sign Up Now!"));
			//Enter all of the form data
			WIDM_Functions.WIDM_Registration_Input(Address, UserName, UserId, Email);
			WebDriver_Functions.Click(By.id("createUserID"));
			WebDriver_Functions.WaitForText(By.id("emailinvalid"), "Email address is not valid.");
			WebDriver_Functions.takeSnapShot("Invalid Email.png");
			Helper_Functions.PrintOut("Email Address: " + Email + " validated. Error message recieved.", false);
		}catch (Exception e) {
			Assert.fail(e.getMessage());
		}
	}
	
	@Test(dataProvider = "dp", description = "420303", enabled = false)
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

	@Test(dataProvider = "dp", description = "411836", enabled = false)
	public void WFCL_Registration_Valid_Email(String Level, String UserID, String Email) {
		try {
			String Address[] = Helper_Functions.LoadAddress("US");
			String ContactName[] = Helper_Functions.LoadDummyName("Create", Level);
			String Result = Arrays.toString(WFCL_Functions.WFCL_UserRegistration(UserID, ContactName, Email, Address));
			Helper_Functions.PrintOut(Result, false);
		}catch (Exception e) {
			Assert.fail(e.getMessage());
		}
	}
	
	@Test(dataProvider = "dp", description = "411836", enabled = false)
	public void WFCL_Registration_Invalid_Email(String Level, String UserID, String Email) {
		try {
			String Address[] = Helper_Functions.LoadAddress("US");
			String ContactName[] = Helper_Functions.LoadDummyName("Create", Level);
			try {
				WFCL_Functions.WFCL_UserRegistration(UserID, ContactName, Email, Address);
			}catch (Exception ExpectedError) {}
			WebDriver_Functions.WaitForText(By.id("emailinvalid"), "Email address is not valid.");
			WebDriver_Functions.takeSnapShot("Invalid Email.png");
		}catch (Exception e) {
			Assert.fail(e.getMessage());
		}
	}
	
	@Test(dataProvider = "dp", description = "411836", enabled = false)
	public void WFCL_Registration_Valid_Email_Legacy(String Level, Account_Data AccountDetails, String UserID, String Email){
		try {
			String Account = AccountDetails.Account_Number;
			String AddressDetails[] = new String[] {AccountDetails.Billing_Address_Line_1, AccountDetails.Billing_Address_Line_2, AccountDetails.Billing_City, AccountDetails.Billing_State, AccountDetails.Billing_State_Code, AccountDetails.Billing_Zip, AccountDetails.Billing_Country_Code};
			String ContactName[] = Helper_Functions.LoadDummyName("Leg", Level);
			String Result[] = WFCL_Functions.WDPA_Registration(ContactName, UserID, Email, Account, AddressDetails);
			Helper_Functions.PrintOut(Arrays.toString(Result), false);
		}catch (Exception e) {
			Assert.fail(e.getMessage());
		}
	}
	
	@Test(dataProvider = "dp", description = "411836", enabled = false)
	public void WFCL_Registration_Invalid_Email_Legacy(String Level, Account_Data AccountDetails, String UserID, String Email){
		try {
			String Account = AccountDetails.Account_Number;
			String AddressDetails[] = new String[] {AccountDetails.Billing_Address_Line_1, AccountDetails.Billing_Address_Line_2, AccountDetails.Billing_City, AccountDetails.Billing_State, AccountDetails.Billing_State_Code, AccountDetails.Billing_Zip, AccountDetails.Billing_Country_Code};
			String ContactName[] = Helper_Functions.LoadDummyName("Leg", Level);
			try {
				WFCL_Functions.WDPA_Registration(ContactName, UserID, Email, Account, AddressDetails);
			}catch (Exception ExpectedError) {}
			WebDriver_Functions.WaitPresent(By.id("emailinvalid"));
			WebDriver_Functions.WaitForText(By.id("emailinvalid"), "Email address is not valid.");
			WebDriver_Functions.takeSnapShot("Invalid Email.png");
		}catch (Exception e) {
			Assert.fail(e.getMessage());
		}
	}
	
	@Test(dataProvider = "dp")
	public void WFCL_Forgot_UserID_Email_Validation(String Level, String CountryCode, String Email) {
		try {
			try {
				WFCL_Functions.Forgot_User_Email(CountryCode, Email);
			}catch (Exception ExpectedError) {}
			WebDriver_Functions.WaitPresent(By.id("emailerror"));
			WebDriver_Functions.WaitForText(By.id("emailerror"), "Email address is not valid.");
			WebDriver_Functions.takeSnapShot("Invalid Email.png");
		}catch (Exception e) {
			Assert.fail(e.getMessage());
		}
	}

}