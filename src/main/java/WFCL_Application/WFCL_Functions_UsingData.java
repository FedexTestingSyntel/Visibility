package WFCL_Application;

import static org.junit.Assert.assertThat;

import java.util.Arrays;

import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.openqa.selenium.By;
import Data_Structures.Account_Data;
import Data_Structures.Enrollment_Data;
import Data_Structures.Tax_Data;
import Data_Structures.USRC_Data;
import Data_Structures.User_Data;
import SupportClasses.DriverFactory;
import SupportClasses.Environment;
import SupportClasses.Helper_Functions;
import SupportClasses.WebDriver_Functions;
import USRC_Application.USRC_API_Endpoints;

public class WFCL_Functions_UsingData{
	
	public static boolean ContactInfo_Page(Account_Data Account_Info, boolean Submit) throws Exception{
		//wait for secret questions to load.
		WebDriver_Functions.WaitPresent(By.cssSelector("#reminderQuestion option[value=SP2Q1]"));
		WebDriver_Functions.CheckBodyText("Country/Territory");
		
		WebDriver_Functions.Type(By.id("firstName"), Account_Info.FirstName);
		WebDriver_Functions.Type(By.name("initials"), Account_Info.MiddleName);
		WebDriver_Functions.Type(By.id("lastName"), Account_Info.LastName);
		WebDriver_Functions.Type(By.id("email"), Account_Info.Email);
		WebDriver_Functions.Type(By.id("retypeEmail"), Account_Info.Email);
		
		if (WebDriver_Functions.isPresent(By.id("country"))) {
			try { // added on 5/9/19 due to MFXR pages have not editable country codes.
				WebDriver_Functions.Select(By.id("country"), Account_Info.Billing_Address_Info.Country_Code.toUpperCase(), "v");
			}catch (Exception e){
				System.err.println("Not able to select country from dropdown.");
			}
		}
		
		WebDriver_Functions.Type(By.id("address1"), Account_Info.Billing_Address_Info.Address_Line_1);
		WebDriver_Functions.Type(By.name("address2"), Account_Info.Billing_Address_Info.Address_Line_2);

		if (WebDriver_Functions.isPresent(By.name("city"))) {
			WebDriver_Functions.Type(By.name("city"),Account_Info.Billing_Address_Info.City);
		}else if (WebDriver_Functions.isPresent(By.name("city1"))){
			WebDriver_Functions.Type(By.name("city1"), Account_Info.Billing_Address_Info.City);
		}

		if (Account_Info.Billing_Address_Info.State_Code != "" && WebDriver_Functions.isPresent(By.name("state"))){
			try {
				//for the legacy registration page such as WDPA the code must be entered as text
				WebDriver_Functions.Type(By.name("state"), Account_Info.Billing_Address_Info.State_Code);
			}catch (Exception e){}
		}
		if (Account_Info.Billing_Address_Info.State_Code != "" && WebDriver_Functions.isPresent(By.id("state"))) {
			try{
				WebDriver_Functions.Select(By.id("state"), Account_Info.Billing_Address_Info.State_Code,  "v");
			}catch (Exception e){}
		}

		WebDriver_Functions.Type(By.id("zip"), Account_Info.Billing_Address_Info.Zip);
		WebDriver_Functions.Type(By.id("phone"), Account_Info.Billing_Address_Info.Phone_Number);
		WebDriver_Functions.Type(By.id("uid"), Account_Info.User_Info.USER_ID);
		WebDriver_Functions.Type(By.id("password"), Account_Info.User_Info.PASSWORD);
		WebDriver_Functions.Type(By.id("retypePassword"), Account_Info.User_Info.PASSWORD);
		WebDriver_Functions.Select(By.id("reminderQuestion"), "SP2Q1", "v"); //"What is your mother's first name?"
		WebDriver_Functions.Type(By.id("reminderAnswer"), "mom");

		if (WebDriver_Functions.isPresent(By.id("acceptterms")) && !WebDriver_Functions.isSelected(By.id("acceptterms"))) {
			WebDriver_Functions.Click(By.id("acceptterms"));
		}

		Helper_Functions.PrintOut("Name: " + Account_Info.FirstName + " " + Account_Info.MiddleName + " " + Account_Info.LastName + "    UserID:" + Account_Info.User_Info.USER_ID, true);

		WebDriver_Functions.takeSnapShot("ContactInformation.png");
		//if the submit flag is sent as true will submit the page.
		if (Submit) {
			if (WebDriver_Functions.isPresent(By.id("iacceptbutton"))) {
				WebDriver_Functions.Click(By.id("iacceptbutton"));
			}else if (WebDriver_Functions.isPresent(By.id("createUserID"))) {
				WebDriver_Functions.Click(By.id("createUserID"));
			}
			
			if (WebDriver_Functions.isPresent(By.id("nucaptcha-answer"))){
				Helper_Functions.PrintOut("Captcha is present on page. Waiting for manaual entry.", true);
				WebDriver_Functions.takeSnapShot("ContactInformation Captcha.png");
				WebDriver_Functions.WaitNotPresent(By.id("nucaptcha-answer"));
			}

		}

		return true;
	}//end WFCL_ContactInfo_Page
	
	public static boolean Account_Entry_Screen(Account_Data Account_Info) throws Exception{
		if (WebDriver_Functions.isPresent(By.id("accountNumber"))){
			WebDriver_Functions.Type(By.id("accountNumber"), Account_Info.Account_Number);
			WebDriver_Functions.Type(By.id("nickName"), Account_Info.Account_Nickname);
			WebDriver_Functions.takeSnapShot("AccountInformation.png");
			WebDriver_Functions.Click(By.id("createUserID"));
			
			WebDriver_Functions.WaitNotVisable(By.className("busyDiv"));
		}else if (WebDriver_Functions.isPresent(By.name("newAccountNumber"))){
			WebDriver_Functions.Type(By.name("newAccountNumber"), Account_Info.Account_Number);
			WebDriver_Functions.Type(By.name("newNickName"), Account_Info.Account_Nickname);
			WebDriver_Functions.takeSnapShot("AccountInformation.png");
			WebDriver_Functions.Click(By.name("submit"));
		}else {
			return false;
		}
		
		if ((WebDriver_Functions.isPresent(By.id("accountNumber")) || WebDriver_Functions.isPresent(By.name("newAccountNumber"))) && !Thread.currentThread().getStackTrace()[2].getMethodName().contentEquals("Account_Entry_Screen")) {
			//will attempt to enter the account number again if still on page. Will only attempt once.
			try {
				if (WebDriver_Functions.isPresent(By.id("accountNumber"))){
					WebDriver_Functions.Type(By.id("accountNumber"), Account_Info.Account_Number);
					WebDriver_Functions.Type(By.id("nickName"), Account_Info.Account_Nickname);
					WebDriver_Functions.Click(By.id("createUserID"));
				}else if (WebDriver_Functions.isPresent(By.name("newAccountNumber"))){
					WebDriver_Functions.Type(By.name("newAccountNumber"), Account_Info.Account_Number);
					WebDriver_Functions.Type(By.name("newNickName"), Account_Info.Account_Nickname);
					WebDriver_Functions.Click(By.name("submit"));
				}
			}catch (Exception e) {
				//quick recheck if still on account page
			}
		}
		
		return true;
	}//end WFCL_AccountEntryScreen
	
	//need to add here for the different applications.
	public static boolean Verify_Confirmaiton_Page(String Application, Account_Data Account_Info) throws Exception {
		String CC = Account_Info.Billing_Address_Info.Country_Code.toUpperCase();
		try{
			switch (Application) {
			case "FDDT":
				if (WebDriver_Functions.CheckBodyText("The FedEx Discount Detail Tool is not available for your Account type.")) {
					Helper_Functions.PrintOut("The FedEx Discount Detail Tool is not available for your Account type.");
				}else {
					////Add this later to check the confirmation page for valid account.
					Helper_Functions.PrintOut("need to update later for confirmation page validation, Verify_Confirmaiton_Page, WFCL_Functions_UsingData");
				}
				break;
			case "INET":
			case "GFBO":
				WebDriver_Functions.WaitForText(By.xpath("//*[@id='content']/div/table/tbody/tr[1]/td[2]/table[2]/tbody/tr[3]/td/table/tbody/tr[1]/td[2]/table/tbody/tr[2]/td/b"), Account_Info.User_Info.USER_ID);
				if (!Account_Info.Masked_Account_Number.contentEquals("")) {
					WebDriver_Functions.WaitForText(By.xpath("//*[@id='content']/div/table/tbody/tr[1]/td[2]/table[2]/tbody/tr[3]/td/table/tbody/tr[2]/td[2]/table/tbody/tr[2]/td/b"), Account_Info.Masked_Account_Number);
				}else {
					System.err.println("Masked_Account_Number has not been set.");
				}
				if (!Account_Info.Account_Nickname.contentEquals("")) {
					WebDriver_Functions.WaitForText(By.xpath("//*[@id='content']/div/table/tbody/tr[1]/td[2]/table[2]/tbody/tr[3]/td/table/tbody/tr[3]/td[2]/table/tbody/tr[2]/td/b"), Account_Info.Account_Nickname);
				}else {
					System.err.println("Account_Nickname has not been set.");
				}
				
				//WebDriver_Functions.WaitForText(By.xpath("//*[@id='content']/div/table/tbody/tr[1]/td[2]/table[2]/tbody/tr[3]/td/table/tbody/tr[3]/td[4]/table/tbody/tr[2]/td/table/tbody/tr/td[2]/b"), AddressDetails[0] + "\n" + AddressDetails[2] + ", " + AddressDetails[4] + " " + AddressDetails[5] + "\n" + AddressDetails[6].toLowerCase());
				break;
			case "WDPA":
				WebDriver_Functions.WaitForText(By.xpath("//*[@id='content']/div/table/tbody/tr[1]/td[2]/p[2]/table[2]/tbody/tr[3]/td/table[2]/tbody/tr/td[1]/table/tbody/tr[2]/td/b"), Account_Info.User_Info.USER_ID);
				break;
			case "WFCL_Link"://WFCL linkage page
				if (CC.contains("US")){
					WebDriver_Functions.WaitForText(By.xpath("//*[@id='rightColumn']/table/tbody/tr/td[1]/div/div[1]/div[2]/table/tbody/tr[1]/td[2]"), Account_Info.User_Info.USER_ID);
					if (!Account_Info.Masked_Account_Number.contentEquals("")) {
						WebDriver_Functions.WaitForText(By.xpath("//*[@id='rightColumn']/table/tbody/tr/td[1]/div/div[1]/div[2]/table/tbody/tr[2]/td[2]"), Account_Info.Masked_Account_Number);    //will need to update due to account masking
					}
				}else {
					WebDriver_Functions.WaitForBodyText(Account_Info.User_Info.USER_ID);
				}
				break;
			case "WFCL_CC":
				WebDriver_Functions.WaitOr_TextToBe(By.xpath("//*[@id='rightColumn']/table/tbody/tr/td[1]/div/div[1]/div[2]/table/tbody/tr[1]/td[2]"), Account_Info.User_Info.USER_ID, By.xpath("//*[@id='rightColumn']/table/tbody/tr/td[1]/div/div/div[2]/table/tbody/tr[1]/td[2]"),  Account_Info.User_Info.USER_ID);
				break;
			case "WFCL_CREATE":
				WebDriver_Functions.WaitForTextPresentIn(By.xpath("//*[@id='rightColumn']/table/tbody/tr/td[1]/div/div/div[2]/table/tbody/tr/td[2]"), Account_Info.User_Info.USER_ID);
				break;
			}
		}catch (Exception e){
			Helper_Functions.PrintOut(e.getMessage() + "\nNotAble to validate " + Application, true);
			throw e;
		}
		
		WebDriver_Functions.takeSnapShot(Application + " Confirmation Page.png");
		return true;
		
	}
	
	//will return the updated Account_Data with the uuid added.
	public static Account_Data Account_Linkage(Account_Data Account_Info) throws Exception{
		Helper_Functions.PrintOut("Attempting to register with " + Account_Info.Account_Number, true);
		String CountryCode = Account_Info.Billing_Address_Info.Country_Code.toUpperCase();

		if (CountryCode.contentEquals("US") || CountryCode.contentEquals("CA")){
			WebDriver_Functions.ChangeURL("FCLLink", CountryCode, true);
			WebDriver_Functions.Click(By.xpath("//input[(@name='accountType') and (@value = 'linkAccount')]"));//select the link account radio button
			WebDriver_Functions.WaitPresent(By.cssSelector("#reminderQuestion option[value=SP2Q1]"));//wait for secret questions to load.
			ContactInfo_Page(Account_Info, true); //enters all of the details
		}else{
			WebDriver_Functions.ChangeURL("FCLLinkInter", CountryCode, true);
			WebDriver_Functions.WaitPresent(By.cssSelector("#reminderQuestion"));
			ContactInfo_Page(Account_Info, true); //enters all of the details
		}

		String UUID = WebDriver_Functions.GetCookieUUID();
		
		//Step 2 Account information
		Account_Entry_Screen(Account_Info);
		InvoiceOrCCValidaiton(Account_Info);
		Verify_Confirmaiton_Page("WFCL_Link", Account_Info);

		Account_Info.User_Info.UUID_NBR = UUID; 
		Helper_Functions.WriteUserToExcel(Account_Info.User_Info.USER_ID, Account_Info.User_Info.PASSWORD);
		return Account_Info;
	}//end WFCL_AccountLinkage
	
	//will return the updated Account_Data with the uuid added.
	public static Account_Data Account_Linkage_FDDT(Account_Data Account_Info) throws Exception{
		Helper_Functions.PrintOut("Attempting to register with " + Account_Info.Account_Number, true);
		String CountryCode = Account_Info.Billing_Address_Info.Country_Code.toUpperCase();

		WebDriver_Functions.ChangeURL("FDDT", CountryCode, true);
		
		if (WebDriver_Functions.isPresent(By.id("btnGoBack"))) {
			WebDriver_Functions.Click(By.id("btnGoBack"));
		}
				
		//sign in if needed
		WebDriver_Functions.WaitPresent(By.id("userId")); //added for now as always need to login
		if (WebDriver_Functions.isPresent(By.id("userId"))) {
			WebDriver_Functions.Type(By.id("userId"), Account_Info.User_Info.USER_ID);
			WebDriver_Functions.Type(By.id("password"), Account_Info.User_Info.PASSWORD);
			WebDriver_Functions.Click(By.id("login-button"));
		}

		WebDriver_Functions.WaitNotPresent(By.id("userId"));
		//Step 2 Account information
		Account_Entry_Screen(Account_Info);
		AddressMismatchPage(Account_Info);
		InvoiceOrCCValidaiton(Account_Info);
		
		Verify_Confirmaiton_Page("FDDT", Account_Info);
		
		String UUID = WebDriver_Functions.GetCookieUUID();
		Account_Info.User_Info.UUID_NBR = UUID; 
		//Helper_Functions.WriteUserToExcel(Account_Info.User_Info.USER_ID, Account_Info.User_Info.PASSWORD);
		return Account_Info;
	}//end Account_Linkage_FDDT

	//will return the updated Account_Data with the uuid added.
	public static String[] Account_Linkage(User_Data User_Info, Account_Data Account_Info) throws Exception{
		Helper_Functions.PrintOut("Attempting to link " + User_Info.USER_ID + " with " + Account_Info.Account_Number, true);
		String CountryCode = Account_Info.Billing_Address_Info.Country_Code.toUpperCase();
		String UUID = WebDriver_Functions.GetCookieUUID();
		if (UUID == null) {
			//if user is not logged in then do so.
			 WebDriver_Functions.Login(User_Info.USER_ID, User_Info.PASSWORD);
			 UUID = WebDriver_Functions.GetCookieUUID();
		}

		WebDriver_Functions.ChangeURL("FCLLINKACCOUNT", CountryCode, false);

		//Step 2 Account information
		Account_Entry_Screen(Account_Info);
		
		if (WebDriver_Functions.CheckBodyText("This Account Number is already registered for this application")) {
			throw new Exception("Account " + Account_Info.Account_Number + " is already linked to user " + User_Info.USER_ID);
		}

		Verify_Confirmaiton_Page("INET", Account_Info);

		return new String[] {User_Info.USER_ID, Account_Info.Account_Number};
	}//end Account_Linkage
	public static void AddressMismatchPage(Account_Data Account_Info) throws Exception {
		if (WebDriver_Functions.isPresent(By.name("address1"))) {
			WebDriver_Functions.WaitPresent(By.name("address1"));
			WebDriver_Functions.Type(By.name("address1"), Account_Info.Billing_Address_Info.Address_Line_1);
			WebDriver_Functions.Type(By.name("address2"), Account_Info.Billing_Address_Info.Address_Line_2);
			WebDriver_Functions.Type(By.name("city"), Account_Info.Billing_Address_Info.City);
			try {
				WebDriver_Functions.Type(By.name("state"), Account_Info.Billing_Address_Info.State_Code);
				WebDriver_Functions.Type(By.name("zip"), Account_Info.Billing_Address_Info.Zip);
				if (Account_Info.Billing_Address_Info.Country_Code.toLowerCase().contentEquals("ca")) {
					WebDriver_Functions.Select(By.name("country"), "ca_english", "v");
				}else {
					WebDriver_Functions.Select(By.name("country"), Account_Info.Billing_Address_Info.Country_Code.toLowerCase(), "v");
				}
				
			}catch (Exception e) {}
		
			WebDriver_Functions.takeSnapShot("Address Mismatch.png");
			WebDriver_Functions.Click(By.name("submit"));
		}
		
	}
	
	public static boolean INET_Registration(Account_Data Account_Info) throws Exception{
		String CountryCode =  Account_Info.Billing_Address_Info.Country_Code.toUpperCase();

		WebDriver_Functions.ChangeURL("INET", CountryCode, false);
		//Register the account number for INET
		WebDriver_Functions.WaitPresent(By.name("accountNumberOpco"));
		WebDriver_Functions.Select(By.name("accountNumberOpco"), "1",  "i");
		WebDriver_Functions.takeSnapShot("INET Account Selection.png");
		WebDriver_Functions.Click(By.className("buttonpurple"));

		//need to add clicking continue button
		Verify_Confirmaiton_Page("INET", Account_Info);

		//checking the INET page
		WebDriver_Functions.Click(By.xpath("//*[@id='content']/div/table/tbody/tr[1]/td[2]/table[2]/tbody/tr[3]/td/table/tbody/tr[1]/td[4]/table/tbody/tr/td/table/tbody/tr[1]/td[2]/a/img"));
		WebDriver_Functions.WaitPresent(By.xpath("//*[@id='appTitle']"));
		 
		//String UUID = WebDriver_Functions.GetCookieUUID();
		//Helper_Functions.PrintOut("Finished WFCL_AccountRegistration  " + UserId + "/" + Account_Info.User_Info.PASSWORD + "--" + AccountNumber + "--" + UUID, true);
		//String ReturnValue[] = new String[] {UserId, AccountNumber, UUID, "INET:" + InetFlag};
		//Helper_Functions.WriteUserToExcel(UserId, Account_Info.User_Info.PASSWORD);
		//return Arrays.toString(ReturnValue);
		return true;
	}
	
	public static String Account_Number_Masking(Account_Data Account_Info, Account_Data Account_Info_Mismatch) throws Exception{
		boolean InetFlag = false;
		String CountryCode = Account_Info.Billing_Address_Info.Country_Code;
		
		Account_Linkage(Account_Info_Mismatch) ;

		//WebDriver_Functions.ChangeURL("https://" + strLevelURL + "/shipping/shipEntryAction.do?origincountry=" + CountryCode.toLowerCase());
		WebDriver_Functions.ChangeURL("INET", CountryCode, false);
		//Register the account number for INET
		if (CountryCode.contains("US") || CountryCode.contains("CA")){
			WebDriver_Functions.WaitPresent(By.name("accountNumberOpco"));
			WebDriver_Functions.Select(By.name("accountNumberOpco"), "1",  "i");
			WebDriver_Functions.takeSnapShot("INET Account Selection.png");
			WebDriver_Functions.Click(By.className("buttonpurple"));
			//need to add clicking continue button

			//address mismatch page should appear, enter the correct details.
			AddressMismatchPage(Account_Info);
			
			//Confirmation from INET registration
			Verify_Confirmaiton_Page("INET", Account_Info);
			
			WebDriver_Functions.Click(By.xpath("//*[@id='content']/div/table/tbody/tr[1]/td[2]/table[2]/tbody/tr[3]/td/table/tbody/tr[1]/td[4]/table/tbody/tr/td/table/tbody/tr[1]/td[2]/a/img"));
			WebDriver_Functions.WaitPresent(By.xpath("//*[@id='appTitle']"));
		}

		WebDriver_Functions.ChangeURL("AdminReg", CountryCode, false);
		try{
			if (WebDriver_Functions.isPresent(By.name("accountNumber"))){ 
				WebDriver_Functions.Select(By.name("accountNumber"), "1", "i");
				WebDriver_Functions.Click(By.name("submit"));	
			}

			//address mismatch page should appear, enter the correct details.
			AddressMismatchPage(Account_Info);
			InvoiceOrCCValidaiton(Account_Info);
			
		}catch (Exception e2){
			Helper_Functions.PrintOut("Failure with admin registriaton.", true);
		};
		
		String UUID = WebDriver_Functions.GetCookieUUID();
		Helper_Functions.PrintOut("Finished WFCL_AccountRegistration  " + Account_Info.User_Info.USER_ID + "/" + Account_Info.User_Info.PASSWORD + "--" + Account_Info.Account_Number + "--" + UUID, true);
		String ReturnValue[] = new String[] {Account_Info.User_Info.USER_ID, Account_Info.Account_Number, UUID, "INET:" + InetFlag};
		Helper_Functions.WriteUserToExcel(Account_Info.User_Info.USER_ID, Account_Info.User_Info.PASSWORD);
		return Arrays.toString(ReturnValue);
	}//end WFCL_AccountRegistration
	
	public static String[] CreditCardRegistrationEnroll(Enrollment_Data Enrollment, Account_Data Account_Info, Tax_Data Tax_Info) throws Exception{
		try {
			//go to the INET page just to load the cookies
			WebDriver_Functions.ChangeURL("INET", Account_Info.Billing_Address_Info.Country_Code, true);
			WebDriver_Functions.ChangeURL_EnrollmentID(Enrollment, false, false);
			//WebDriver_Functions.ChangeURL("Enrollment_" + Enrollment.ENROLLMENT_ID, Account_Info.Billing_Address_Info.Country_Code, false);

			WebDriver_Functions.takeSnapShot("Discount Page.png");
			
			if (WebDriver_Functions.isPresent(By.name("Apply Now"))) {
				//apply link from marketing page
				WebDriver_Functions.Click(By.name("Apply Now"));
			}
			
			if (WebDriver_Functions.isPresent(By.linkText("Finish registering for a FedEx account"))) {
				WebDriver_Functions.Click(By.linkText("Finish registering for a FedEx account"));
			}else if (WebDriver_Functions.isPresent(By.name("signupnow"))) {
				WebDriver_Functions.Click(By.name("signupnow"));
			}

			//Step 1: Enter the contact information.
			ContactInfo_Page(Account_Info, true); //enters all of the details
			Account_Info.User_Info.UUID_NBR = WebDriver_Functions.GetCookieUUID();
			
			//Step 2: Enter the credit card details
			WebDriver_Functions.WaitPresent(By.id("CCType"));

			Account_Info = WFCL_CC_Page(Account_Info, Tax_Info);
			
			//Step 3: Confirmation Page 
			Verify_Confirmaiton_Page("WFCL_CC", Account_Info);
			Account_Info.Account_Number =  WebDriver_Functions.GetText(By.xpath("//*[@id='acctNbr']"));
			Account_Data.Set_Account_Nickname(Account_Info, "");

			boolean InetFlag = false, AdminFlag = false;

			//Check the INET page
			InetFlag = Check_INET_Enrolled(Account_Info.Billing_Address_Info.Country_Code);

			//Check the Administration page
			AdminFlag = Check_WADM_Enrolled(Account_Info.Billing_Address_Info.Country_Code);
			
			Helper_Functions.PrintOut("Finished CreditCardRegistrationEnroll  " + Account_Info.User_Info.USER_ID + "/" + Account_Info.User_Info.PASSWORD + "--" + Account_Info.Account_Number + "--" + Account_Info.User_Info.UUID_NBR, true);
			
			String ReturnValue[] = new String[] {Account_Info.User_Info.USER_ID, Account_Info.User_Info.PASSWORD, Account_Info.Account_Number, Account_Info.User_Info.UUID_NBR, Account_Data.Last_Four_of_Credit_Card(Account_Info), "INET:" + InetFlag, "Admin:" + AdminFlag};
			
			Helper_Functions.WriteUserToExcel(Account_Info.User_Info.USER_ID, Account_Info.User_Info.PASSWORD);
			return ReturnValue;
		}catch (Exception e) {
			Account_Data.Print_High_Level_Details(Account_Info);
			e.printStackTrace();
			throw e;
		}
	}//end CreditCardRegistrationEnroll
	
	public static boolean Check_INET_Enrolled(String CountryCode) {
		try{
			WebDriver_Functions.ChangeURL("INET", CountryCode, false);
			//wait for the from section to be loaded
			WebDriver_Functions.WaitPresent(By.id("module.from._headerTitle"));
			//wait for the to section to be loaded
			WebDriver_Functions.WaitPresent(By.id("module.to._headerTitle"));
			//wait for the package and shipment details section to load.
			WebDriver_Functions.WaitPresent(By.id("module.psd._headerTitle"));
			WebDriver_Functions.takeSnapShot("INET page.png");
			return true;
		}catch (Exception e2){
			Helper_Functions.PrintOut("User not registered for INET", true);
			return false;
		}
	}
	
	public static boolean Check_WADM_Enrolled(String CountryCode) {
		try{
			WebDriver_Functions.ChangeURL("WADM", CountryCode, false);
			//wait for the WADM pages to load
			WebDriver_Functions.WaitPresent(By.id("main"));
			WebDriver_Functions.WaitPresent(By.id("adminsScrollTable123"));
			WebDriver_Functions.WaitPresent(By.id("addAdmins"));
			WebDriver_Functions.WaitPresent(By.id("main"));
			WebDriver_Functions.takeSnapShot("WADM Page.png");
			return true;
		}catch (Exception e2){
			Helper_Functions.PrintOut("User not registered for WADM", true);
			return false;
		}
	}

	//will return a string array with 0 as the user id and 1 as the password, 2 is the uuid
	public static String[] WFCL_UserRegistration(Account_Data Account_Info) throws Exception{
		String CountryCode = Account_Info.Billing_Address_Info.Country_Code;
		try {
			WebDriver_Functions.ChangeURL("Pref", CountryCode, true);//navigate to email preferences page to load cookies
			WebDriver_Functions.WaitClickable(By.id("registernow"));
			WebDriver_Functions.ClickIfPresent(By.id("registernow"));

			ContactInfo_Page(Account_Info, true); //enters all of the details

			String UUID = WebDriver_Functions.GetCookieUUID();
			
			if ("US".contains(Account_Info.Billing_Address_Info.Country_Code)) {
				//Confirmation page
				Verify_Confirmaiton_Page("WFCL_CREATE", Account_Info);
			}
			
			if (UUID == null) {
				throw new Exception("Error, user not created.");
			}
			
			Helper_Functions.PrintOut("Finished WFCL_UserRegistration  " + Account_Info.User_Info.USER_ID + "/" + Account_Info.User_Info.PASSWORD + " -- " + UUID, true);
			String ReturnValue[] = new String[]{Account_Info.User_Info.USER_ID, Account_Info.User_Info.PASSWORD,  UUID};
			Helper_Functions.WriteUserToExcel(Account_Info.User_Info.USER_ID, Account_Info.User_Info.PASSWORD);//Write User to file for later reference
			return ReturnValue;
		}catch (Exception e) {
			if (e.getMessage().contains("[(@name='accountType') and (@value = 'noAccount')]")){
				Helper_Functions.PrintOut("Radio button not present for " + CountryCode + " to do User id creation.", true);
			}
			throw e;
		}
	}//end WFCL_UserRegistration
	
	//will return a string array with 0 as the user id and 1 as the password, 2 is the uuid
	public static void WFCL_UserRegistration_Captcha(Account_Data Account_Info, int Attempts) throws Exception{
		String CountryCode = Account_Info.Billing_Address_Info.Country_Code;
		try {
			WebDriver_Functions.ChangeURL("Pref", CountryCode, true);//navigate to email preferences page to load cookies
			WebDriver_Functions.WaitClickable(By.id("registernow"));
			WebDriver_Functions.ClickIfPresent(By.id("registernow"));
			
			WebDriver_Functions.ChangeURL(WebDriver_Functions.GetCurrentURL() + "&captcha=true", CountryCode, false);
			
			ContactInfo_Page(Account_Info, false); //enters all of the details

			if (WebDriver_Functions.isPresent(By.id("iacceptbutton"))) {
				WebDriver_Functions.Click(By.id("iacceptbutton"));
			}else if (WebDriver_Functions.isPresent(By.id("createUserID"))) {
				WebDriver_Functions.Click(By.id("createUserID"));
			}
			
			int Lockout = 4; // four attempts
			if (Attempts > Lockout) {
				Helper_Functions.PrintOut("Warning, user should be locked out after " + Lockout + " attempts. Recieved " + Attempts);
			}
			for (int i = 1 ; i < Lockout + 1; i++) {
				//if (WebDriver_Functions.isPresent(By.id("nucaptcha-answer"))){
				//	Helper_Functions.PrintOut("Captcha is present on page. Waiting for manaual entry.", true);
				//	Helper_Functions.Wait(15);
				//}
				WebDriver_Functions.WaitPresent(By.id("nucaptcha-answer"));
				WebDriver_Functions.Type(By.id("nucaptcha-answer"), "Att" + i);
				WebDriver_Functions.takeSnapShot("Captcha attempt " + i + " of " + Lockout + ".png");
				
				if (WebDriver_Functions.isPresent(By.id("iacceptbutton"))) {
					WebDriver_Functions.Click(By.id("iacceptbutton"));
				}else if (WebDriver_Functions.isPresent(By.id("createUserID"))) {
					WebDriver_Functions.Click(By.id("createUserID"));
				}
				
			}
			
			if (Attempts == Lockout) {
				WebDriver_Functions.CheckBodyText("FedEx cannot process your request with the information entered. Please call 1.800.GoFedEx 1.800.463.3339 to open an account.");
				WebDriver_Functions.takeSnapShot("Captcha LockedOut.png");
			}else {
				Helper_Functions.PrintOut("Captcha is present on page. Waiting for manaual entry.", true);
				//infinite loop while element on page.
				while (WebDriver_Functions.isPresent(By.id("nucaptcha-answer"))) {
					Helper_Functions.Wait(15);
				}
				
			}
			
		}catch (Exception e) {}
	}//end WFCL_UserRegistration
	
	public static boolean Admin_Registration(Account_Data Account_Info) throws Exception {
		try{
			WebDriver_Functions.ChangeURL("AdminReg", Account_Info.Billing_Address_Info.Country_Code, false);
			
			if (WebDriver_Functions.isPresent(By.name("accountNumber"))){ 
				WebDriver_Functions.Select(By.name("accountNumber"), "1", "i");
				WebDriver_Functions.Click(By.name("submit"));
			}

			Helper_Functions.Wait(2);
			
			if (WebDriver_Functions.isPresent(By.name("invoiceNumberA")) || WebDriver_Functions.isPresent(By.name("creditCardNumber"))) {
				InvoiceOrCCValidaiton(Account_Info);
			}
			
			WebDriver_Functions.WaitPresent(By.name("companyName"));
			String CompanyName = "Company" + Helper_Functions.CurrentDateTime();
			WebDriver_Functions.Type(By.name("companyName"), CompanyName);
			WebDriver_Functions.takeSnapShot("WADM CompanyName.png");
			WebDriver_Functions.Click(By.className("buttonpurple"));

			WebDriver_Functions.WaitPresent(By.cssSelector("#confirmation > div > div.fx-col.col-3 > div > h3"));
			Helper_Functions.PrintOut("Registered for Admin. Current URL:" + WebDriver_Functions.GetCurrentURL(), true);
			WebDriver_Functions.takeSnapShot("WADM Registration Confirmaiton.png");
			WebDriver_Functions.Click(By.cssSelector("#confirmation > div > div.fx-col.col-3 > div > p:nth-child(6) > a"));//click shipping admin link
			WebDriver_Functions.WaitForText(By.cssSelector("#main > h1"), "Admin Home: " + CompanyName);
			//remove the account number from local storage as it is now locked to the company that was just created.
			Helper_Functions.RemoveAccountFromAccount_Numbers(Environment.getInstance().getLevel(), Account_Info.Account_Number);
		}catch (Exception e){
			Helper_Functions.PrintOut("Failure with admin registriaton.", true);
			throw e;
		};
		
		return true;
	}
	
	public static String[] WDPA_Registration(Account_Data Account_Info) throws Exception{
 		try {
 			String CountryCode = Account_Info.Billing_Address_Info.Country_Code;
 			WebDriver_Functions.ChangeURL("WDPA", CountryCode, true);
 			WebDriver_Functions.Click(By.name("signupnow"));
 	
 			ContactInfo_Page(Account_Info, true); //enters all of the details
 			
	 		//Step 2 Account information
 			Account_Entry_Screen(Account_Info);
	 		
 			Verify_Confirmaiton_Page("WDPA", Account_Info);
		    Account_Info.User_Info.UUID_NBR = WebDriver_Functions.GetCookieUUID();
		    
		    WebDriver_Functions.Click(By.xpath("//*[@id='content']/div/table/tbody/tr[1]/td[2]/p[2]/table[2]/tbody/tr[3]/td/table[2]/tbody/tr/td[2]/table/tbody/tr/td/table/tbody/tr[1]/td[2]/a/img"));
		    WebDriver_Functions.WaitPresent(By.id("module.account._headerTitle"));
		    
		    String ReturnValue[] = new String[] {Account_Info.User_Info.USER_ID, Account_Info.User_Info.PASSWORD, Account_Info.Account_Number, Account_Info.User_Info.UUID_NBR};
		    Helper_Functions.WriteUserToExcel(Account_Info.User_Info.USER_ID, Account_Info.User_Info.PASSWORD);
		    return ReturnValue;
 		}catch (Exception e) {
 			e.printStackTrace();
 			throw e;
 		}
 	}//end WFCL_AccountRegistration_WDPA
	
	public static String Forgot_User_Email(String CountryCode, String Email) throws Exception{
		try{
			WebDriver_Functions.ChangeURL("INET", CountryCode, true);//go to the INET page just to load the cookies
			WebDriver_Functions.Click(By.name("forgotUidPwd"));
			//wait for text box for user id to appear
			WebDriver_Functions.Type(By.name("email"), Email);

			WebDriver_Functions.takeSnapShot("Forgot User Id.png");
			WebDriver_Functions.Click(By.xpath("//*[@id='module.forgotuseridandpassword._expanded']/table/tbody/tr/td[3]/form/table/tbody/tr[6]/td/input[2]"));
			WebDriver_Functions.WaitPresent(By.id("linkaction"));
			//String Text = WebDriver_Functions.GetText(By.xpath("//*[@id='content']/div/table/tbody/tr[1]/td/table/tbody/tr/td/table/tbody/tr[1]/td"));
			WebDriver_Functions.CheckBodyText(Email);
			WebDriver_Functions.takeSnapShot("Forgot User Confirmation.png");
			return Email + " - An email has been triggered and that test must be completed manually by to see the user list.";
		}catch(Exception e){
			throw e;
		}
	}//end Forgot_User_Email

	public static Account_Data WFCL_CC_Page(Account_Data Account_Info, Tax_Data Tax_Info) throws Exception{
		String CountryCode = Account_Info.Billing_Address_Info.Country_Code;
		Helper_Functions.PrintOut("WFCL_CC_Page recieved: " + Account_Info.Credit_Card_Number + ", " + CountryCode + " From: " + Thread.currentThread().getStackTrace()[2].getMethodName(), true);

		WebDriver_Functions.WaitForTextPresentIn(By.cssSelector("#CCType"), Account_Info.Credit_Card_Type.substring(0, 1));//wait for the credit card types to load, check for first character since Mastercard and MasterCard issue.

		WebDriver_Functions.Type(By.id("creditCardNumber"), Account_Info.Credit_Card_Number);
		WebDriver_Functions.Type(By.id("creditCardIDNumber"), Account_Info.Credit_Card_CVV);
		WebDriver_Functions.Select(By.id("monthExpiry"), Account_Info.Credit_Card_Expiration_Month, "t");
		WebDriver_Functions.Select(By.id("yearExpiry"), Account_Info.Credit_Card_Expiration_Year, "t");

		if (WebDriver_Functions.isPresent(By.name("editshipinfo"))) {
			WebDriver_Functions.Click(By.name("editshipinfo"));//the shipping address section
			WebDriver_Functions.ElementMatches(By.xpath("//*[@id='shipping-address-fields']/label[12]/span[1]"), "Country/Territory", 116577);
		}

		if (WebDriver_Functions.isPresent(By.name("editccinfo"))) {
			WebDriver_Functions.Click(By.name("editccinfo"));//edit the billing address
			if (WebDriver_Functions.isPresent(By.xpath("//*[@id='billing-address']/label[17]/span[1]"))) {
				WebDriver_Functions.ElementMatches(By.xpath("//*[@id='billing-address']/label[17]/span[1]"), "Country/Territory", 116577);
			}else if (WebDriver_Functions.isPresent(By.xpath("//*[@id='billing-address']/label[18]/span[1]"))) {//Non US field
				//not sure if this is valid for other regions WebDriver_Functions.ElementMatches(By.xpath("//*[@id='billing-address']/label[18]/span[1]"), "Country/Territory", 116577);
				//when checking BR seeing 
				WebDriver_Functions.ElementMatches(By.xpath("//*[@id='billing-address']/label[20]/span[1]"), "Country/Territory", 116577);
			}
			
			//if the zip code does not match what was enterd on previous page then update. This was change as part of the TNT features for the Jan19CL.
			if (WebDriver_Functions.isPresent(By.id("creditCardZipCode")) && !WebDriver_Functions.GetText(By.id("creditCardZipCode")).contentEquals(Account_Info.Billing_Address_Info.Zip)) {
				WebDriver_Functions.Type(By.id("creditCardZipCode"), Account_Info.Billing_Address_Info.Zip);
			}
		}

		if(WebDriver_Functions.isPresent(By.name("questionCd9"))){ //This is just filler currently. Selecting just to cover the shipping needs section, present on US page
			WebDriver_Functions.Select(By.name("questionCd9"), "1", "i");
			WebDriver_Functions.Select(By.name("questionCd10"), "1", "i");
			WebDriver_Functions.Select(By.name("questionCd11"), "1", "i");
		}

		//if the account type is set to "b" then do business reg
		if (Account_Info.Account_Type.toUpperCase().contentEquals("B")) {
			if (WebDriver_Functions.isPresent(By.xpath("//*[@id='accountTypeBus']"))) {
				WebDriver_Functions.Click(By.xpath("//*[@id='accountTypeBus']"));
			}else {
				WebDriver_Functions.Click(By.id("businessAcct"));//the US page has a checkbox
			}
			if (Account_Info.Company_Name.contentEquals("")) {
				Account_Info.Company_Name = "Company" + Helper_Functions.CurrentDateTime();//load a filler value if nothing was given.
			}
			WebDriver_Functions.Type(By.name("company"), Account_Info.Company_Name);
		}

		if (WebDriver_Functions.isPresent(By.name("indTaxID"))) {
			WebDriver_Functions.Type(By.name("indTaxID"), Tax_Info.TAX_ID); 
		}
		if (WebDriver_Functions.isPresent(By.name("indStateTaxID"))) {
			WebDriver_Functions.Type(By.name("indStateTaxID"), Tax_Info.STATE_TAX_ID);
		}else if (WebDriver_Functions.isPresent(By.name("vatNo"))) {
			WebDriver_Functions.Type(By.name("vatNo"), Tax_Info.STATE_TAX_ID);
		}
		
		String CreditCardTypes = WebDriver_Functions.GetText(By.id("CCType"));
		if (CreditCardTypes.contains(Account_Info.Credit_Card_Type)) {
			WebDriver_Functions.Select(By.id("CCType"), Account_Info.Credit_Card_Type, "t");
		}else if(Account_Info.Credit_Card_Type.toLowerCase().contentEquals("mastercard")){
			Account_Info.Credit_Card_Type = "Mastercard";//for US they use a lower case 'c'.
			WebDriver_Functions.Select(By.id("CCType"), Account_Info.Credit_Card_Type, "t");
		}

		WebDriver_Functions.takeSnapShot("CreditCard Entry.png");
		WebDriver_Functions.Click(By.id("Complete"));

		try {
			WebDriver_Functions.WaitForTextPresentIn(By.tagName("body"), "We are processing your request.");
			WebDriver_Functions.WaitForTextNotPresentIn(By.tagName("body"), "We are processing your request.");
		}catch (Exception e) {}

		//If the user is still on the CC entry page will try and enter a different credit card type.
		if (WebDriver_Functions.isPresent(By.id("monthExpiry")) && (Tax_Info == null || Tax_Info.ERROR_CODE.contentEquals("Valid"))) {
			//will try different cards three times.
			if (!Account_Info.LastName.contains("Attempt")) {
				Account_Info.LastName = "AttemptTwo" + Helper_Functions.getRandomString(6);
			}else if (Account_Info.LastName.contains("AttemptTwo")) {
				Account_Info.LastName = "AttemptThree" + Helper_Functions.getRandomString(6);
			}else {
				throw new Exception("User is still on the credit card entry page");
			}
			Helper_Functions.PrintOut("Error on Credit Card entry screen. Attempting to register with differnet credit card. " + Account_Info.LastName, true);
			//update the last name of the billing address, this is for tracing the attempt in logs.
			if (WebDriver_Functions.isPresent(By.name("editccinfo"))) {
				WebDriver_Functions.Click(By.name("editccinfo"));//edit the billing address
				WebDriver_Functions.Type(By.id("creditCardLastName"), Account_Info.LastName);
			}
			
			Account_Data.Set_Credit_Card(Account_Info, Environment.getCreditCardDetails(Environment.getInstance().getLevel(), Account_Info.Credit_Card_Type, Account_Info.Credit_Card_Number));
			
			return WFCL_CC_Page(Account_Info, Tax_Info);
		}
		//longwait.until(ExpectedConditions.presenceOfElementLocated(By.xpath("//*[@id='rightColumn']/table/tbody/tr/td[1]/div/div[1]/div[2]/table/tbody/tr[1]/td[2]")));//check that confirmation page loads
		return Account_Info; 
	}//end WFCL_CC_Page

	private static void InvoiceOrCCValidaiton(Account_Data Account_Info) throws Exception{
		//need to add a dynamic wait here for the processing
		Helper_Functions.Wait(2);
		
		if (WebDriver_Functions.isVisable(By.name("invoiceNumberA"))){
			WebDriver_Functions.Type(By.name("invoiceNumberA"), Account_Info.Invoice_Number_A);
			WebDriver_Functions.Type(By.name("invoiceNumberB"), Account_Info.Invoice_Number_B);
			WebDriver_Functions.takeSnapShot("Invoice_Validaiton.png");
			WebDriver_Functions.Click(By.className("buttonpurple"));
			WebDriver_Functions.WaitNotPresent(By.name("invoiceNumberB"));
			
		}else if (WebDriver_Functions.isVisable(By.name("creditCardNumber"))) {
			WebDriver_Functions.Type(By.name("creditCardNumber"), Account_Data.Last_Four_of_Credit_Card(Account_Info));
			WebDriver_Functions.takeSnapShot("CreditCard_Validaiton.png");
			WebDriver_Functions.Click(By.className("buttonpurple"));
			WebDriver_Functions.WaitNotPresent(By.name("creditCardNumber"));
		}
	}

	public static String WFCL_Secret_Answer(User_Data User_Info, String NewPassword) throws Exception{
 		WebDriver_Functions.ChangeURL("INET", User_Info.Address_Info.Country_Code, true);
 		
    	try{
    		//click the forgot password link
    		WebDriver_Functions.Click(By.name("forgotUidPwd"));
    		WebDriver_Functions.Type(By.name("userID"), User_Info.USER_ID);
    		
    		WebDriver_Functions.takeSnapShot("Password Reset.png");
            //click the option 1 button and try to answer with secret question
    		//WebDriver_Functions.Click(By.xpath("//*[@id='module.forgotuseridandpassword._expanded']/table/tbody/tr/td[1]/form/table/tbody/tr[6]/td/input[2]"));//updated the below as not working on 10-29-18
    		WebDriver_Functions.Click(By.id("ada_forgotpwdcontinue"));
                                
    		//click the Answer question button
    		WebDriver_Functions.Click(By.xpath("//*[@id='module.resetpasswordoptions._expanded']/table/tbody/tr/td[1]/form/table/tbody/tr[5]/td/input"));

            WebDriver_Functions.Type(By.name("answer"), User_Info.SECRET_ANSWER_DESC);
            WebDriver_Functions.takeSnapShot("Reset Password Secret.png");
            WebDriver_Functions.Click(By.name("action1"));
            
			WebDriver_Functions.Type(By.name("password"), NewPassword);
			WebDriver_Functions.Type(By.name("retypePassword"), NewPassword);
			WebDriver_Functions.takeSnapShot("New Password.png");
			WebDriver_Functions.Click(By.name("confirm"));
			WebDriver_Functions.WaitForText(By.xpath("//*[@id='content']/div/table/tbody/tr[1]/td/table/tbody/tr/td/table/tbody/tr[1]/td/h1"), "Thank you.");
			boolean loginAttempt = WebDriver_Functions.Login(User_Info.USER_ID, NewPassword);
			Helper_Functions.PrintOut(User_Info.USER_ID + " has had the password changed to " + NewPassword, true);
			
			//if recursive the password already changed once
			if (Thread.currentThread().getStackTrace()[2].getMethodName().contentEquals("WFCL_Secret_Answer")){
		    	return User_Info.USER_ID + " " + NewPassword;
			}else if (loginAttempt){
				return WFCL_Secret_Answer(User_Info,  User_Info.PASSWORD);//change the password back
			}else{
				throw new Exception("Error.");
			}
		}catch (Exception e){
			Helper_Functions.PrintOut("Secret quesiton " + User_Info.SECRET_ANSWER_DESC + " was not accepted.", true);
			throw e;
		}
	}//end ResetPasswordWFCLSecret

	public static String ResetPasswordWFCL_Email(User_Data User_Info) throws Exception{
    	try{
    		//WebDriver_Functions.Login(User_Info.USER_ID, User_Info.PASSWORD);

    		String Email = "--Could not retrieve email--";
    		
    		try {
    			USRC_Data USRC_Details = USRC_Data.LoadVariables(Environment.getInstance().getLevel());
    			String[] fdx_login_fcl_uuid = USRC_API_Endpoints.Login(USRC_Details.GenericUSRCURL, User_Info.USER_ID, User_Info.PASSWORD);
    			String ContactDetailsResponse = USRC_API_Endpoints.ViewUserProfileWIDM(USRC_Details.ViewUserProfileWIDMURL, fdx_login_fcl_uuid[0]);
    			String ContactDetailsParsed[][] = new String[][] {{"EMAIL_ADDRESS", ""}};
    			ContactDetailsParsed = USRC_API_Endpoints.Parse_ViewUserProfileWIDM(ContactDetailsResponse, ContactDetailsParsed);
    			Email = ContactDetailsParsed[0][1];
    		}catch (Exception e) {
    			Helper_Functions.PrintOut("Error " + e.getMessage() + "   " + Email, true);
    		}
    		
    		//trigger the password reset email
    		WebDriver_Functions.ChangeURL("INET", User_Info.Address_Info.Country_Code, true);
    		WebDriver_Functions.Click(By.name("forgotUidPwd"));
    		WebDriver_Functions.Type(By.name("userID"), User_Info.USER_ID);
    		
    		WebDriver_Functions.Click(By.id("ada_forgotpwdcontinue"));
    		//*[@id="ada_forgotpwdcontinue"]
    		//click the option 1 button and try to answer with secret question
    		WebDriver_Functions.Click(By.name("action2"));
    		WebDriver_Functions.takeSnapShot("Password Reset Email.png");
    		Helper_Functions.PrintOut("Completed ResetPasswordWFCL using " + User_Info.USER_ID + ". An email has been triggered and that test must be completed manually by " + Email, true);
    					
    		return Email;
    	}catch(Exception e){
    		Helper_Functions.PrintOut("General failure in ResetPasswordWFCL_Email", true);
    		throw e;
    	}
	}//end ResetPasswordWFCL_Email
    
	public static String[] WFCL_WADM_Invitaiton(User_Data User_Info, Account_Data Account_Info, String Email) throws Exception{	
		try {
			WebDriver_Functions.Login(User_Info.USER_ID, User_Info.PASSWORD);
			WebDriver_Functions.ChangeURL("WADM", "US", false);
			WebDriver_Functions.takeSnapShot("UserTable before invite.png");
			
			WebDriver_Functions.Click(By.id("createNewUsers"));
			WebDriver_Functions.WaitNotPresent(By.id("loading-div"));//wait for the loading overlay to not be present
			WebDriver_Functions.Type(By.name("userfirstName"), Account_Info.FirstName);
			WebDriver_Functions.Type(By.name("middleName"), Account_Info.MiddleName);
			WebDriver_Functions.Type(By.name("userlastName"), Account_Info.LastName);
			
			String Time = Helper_Functions.CurrentDateTime();
			String UniqueID = Time.substring(Time.length() - 13, 10);
			WebDriver_Functions.Type(By.name("userAlias"), UniqueID);//unique filler value as the time stamp of attempt
			WebDriver_Functions.Type(By.name("email"), Email);
			WebDriver_Functions.Click(By.id("addAccountButton"));
			WebDriver_Functions.WaitNotPresent(By.id("loading-div"));//wait for the loading overlay to not be present
			WebDriver_Functions.Click(By.xpath("//*[@id='tableBody']/tr/td[1]/input"));//add the first account number to user.
			WebDriver_Functions.Click(By.id("addAccounts"));
			WebDriver_Functions.Select(By.id("userAdminTypeSelect"), "company", "v");//make the user company admin user role
			WebDriver_Functions.Click(By.id("inviteUsers"));
			WebDriver_Functions.takeSnapShot("Invitation.png");
			WebDriver_Functions.Click(By.id("userSave"));
			
			WebDriver_Functions.WaitNotPresent(By.id("loading-div"));//wait for the loading overlay to not be present
			WebDriver_Functions.Type(By.xpath("//*[@id='manageTablesContainer']/div[1]/fieldset[1]/input"), UniqueID);
			WebDriver_Functions.Select(By.id("manageTableDropDown"), "Unique ID", "t"); // select to search by id
			WebDriver_Functions.Click(By.id("goSearch"));
			//Check if the invited user is listed on user tab
			WebDriver_Functions.WaitForText(By.xpath("//*[@id=\"tableBody\"]/tr/td[1]"), Account_Info.FirstName); //check first name
			WebDriver_Functions.WaitForText(By.xpath("//*[@id=\"tableBody\"]/tr/td[2]"), Account_Info.LastName); //check last name
			WebDriver_Functions.takeSnapShot("Invitation Sent.png");
			
			return new String[] {Email, UniqueID};
		}catch (Exception e) {
			e.printStackTrace();
			throw e;
		}
    }
    
	public static String[] WFCL_AccountRegistration_GFBO(Account_Data Account_Info) throws Exception{
		String CountryCode = Account_Info.Billing_Address_Info.Country_Code;
		
		//The below applicable countries list was updated on 1/30/18
		String GFBOCountry_List[] = {"SV", "LU", "MO", "GU", "AE", "AN", "AR", "AT", "AU", "AW", "BB", "BE", "BH", "BM", "BR", "BS", "CH", "CL", "CN", "CR", "CZ", "DE", "DK", "DO", "EE", "ES", "FI", "FR", "GB", "GD", "GP", "GR", "GT", "HK", "HU", "IE", "IN", "IT", "JM", "JP", "KN", "KR", "KV", "KW", "KY", "LC", "LT", "LV", "MQ", "MX", "MY", "NL", "NO", "NZ", "PA", "PL", "PT", "RU", "SE", "SG", "TC", "TH", "TR", "TT", "TW", "US", "UY", "VC", "VE", "VG", "VI", "CA", "CW", "BQ", "SX", "MF", "BW", "LS", "MW", "MZ", "NA", "SI", "ZA", "SZ", "ZM", "VN", "BG", "HR", "RO", "SK", "QA", "PH", "AD", "AF", "AL", "AM", "AO", "AZ", "BA", "BD", "BF", "BI", "BJ", "BT", "BY", "CD", "CF", "CG", "CI", "CM", "CV", "CY", "DJ", "DZ", "EG", "ER", "ET", "GA", "GE", "GH", "GI", "GL", "GM", "GN", "GQ", "GW", "IL", "IQ", "IR", "IS", "JO", "KE", "KG", "KZ", "LB", "LI", "LK", "LR", "LY", "MA", "MC", "MD", "ME", "MG", "MK", "ML", "MR", "MT", "MU", "MV", "NE", "NG", "NP", "OM", "PK", "PS", "RE", "RS", "RW", "SA", "SC", "SD", "SL", "SN", "SO", "SY", "TD", "TG", "TM", "TN", "TZ", "UA", "UG", "UZ"};
 		if (!Arrays.asList(GFBOCountry_List).contains(CountryCode)){
 			Helper_Functions.PrintOut("Please check that " + CountryCode + " is a valid country to register for GFBO. It is not valid in test levels as of 1/30/18", true);
 		}
 		
 		try {
 			WebDriver_Functions.ChangeURL("GFBO", CountryCode, false);

 			//wait for secret questions to load.
 			WebDriver_Functions.WaitPresent(By.cssSelector("#reminderQuestion option[value=SP2Q1]"));

 			//enters all of the details
 			ContactInfo_Page(Account_Info, true);
		    
	 		//Step 2 Account information
 			Account_Entry_Screen(Account_Info);
	 		
		    //Confirmation page for FBO
	 		//wait.until(ExpectedConditions.presenceOfElementLocated(By.id("selectbillingmedium1:j_idt24")));//the header for FBO
	 		if (WebDriver_Functions.CheckBodyText("FedEx Billing Online Registration")) {
	 			throw new Exception("Error on Confirmaiton page");
	 		}
	 		WebDriver_Functions.takeSnapShot("Confirmation.png");
	 		WebDriver_Functions.Click(By.id("selectbillingmedium1:continueId"));  
	 		
	 		//Registration Confirmation Page
	 		Verify_Confirmaiton_Page("GFBO", Account_Info);

			//Make sure the link on the confirmation page navigates to the correct page.
	 		WebDriver_Functions.WaitForText(By.xpath("//*[@id='content']/div/table/tbody/tr[1]/td[2]/table[2]/tbody/tr[3]/td/table/tbody/tr[1]/td[4]/table/tbody/tr/td/table/tbody/tr[2]/td"), "        FedEx Billing Online");			
			WebDriver_Functions.Click(By.xpath("//*[@id='content']/div/table/tbody/tr[1]/td[2]/table[2]/tbody/tr[3]/td/table/tbody/tr[1]/td[4]/table/tbody/tr/td/table/tbody/tr[1]/td[2]/a/img"));
			
			//The GFBO confirmation page based on if account number is set for invoices.
			if (!DriverFactory.getInstance().getDriver().findElements(By.id("splash2or3optionsForm:splash3OptionsSubmit")).isEmpty()){
				WebDriver_Functions.takeSnapShot("Congratulations.png");
				WebDriver_Functions.Click(By.id("splash2or3optionsForm:splash3OptionsSubmit"));
			}
			
			//Will now land on the GFBO page
			//wait for the GFBO page to load
			WebDriver_Functions.WaitPresent(By.id("mainContentId:accSmyCmdLink"));
			WebDriver_Functions.takeSnapShot("Page.png");
			
			String UUID = WebDriver_Functions.GetCookieUUID();
		    String ReturnValue[] = new String[] {Account_Info.User_Info.USER_ID, Account_Info.Account_Number, UUID};
		    Helper_Functions.WriteUserToExcel(Account_Info.User_Info.USER_ID, Account_Info.User_Info.PASSWORD);
		    return ReturnValue;
 		}catch (Exception e) {
 			throw e;
 		}
 	}//end WFCL_AccountRegistration_GFBO
    
	public static String[] WFCL_AccountRegistration_INET(Account_Data Account_Info) throws Exception{
		boolean InetFlag = false;
		String CountryCode = Account_Info.Billing_Address_Info.Country_Code;

		if (CountryCode.contentEquals("US") || CountryCode.contentEquals("CA")){
			WebDriver_Functions.ChangeURL("FCLLink", CountryCode, true);
			WebDriver_Functions.Click(By.xpath("//input[(@name='accountType') and (@value = 'linkAccount')]"));//select the link account radio button
			WebDriver_Functions.WaitPresent(By.cssSelector("#reminderQuestion option[value=SP2Q1]"));//wait for secret questions to load.
			ContactInfo_Page(Account_Info, true);//enters all of the details
		}else{
			WebDriver_Functions.ChangeURL("FCLLinkInter", CountryCode, true);
			WebDriver_Functions.WaitPresent(By.cssSelector("#reminderQuestion"));
			ContactInfo_Page(Account_Info, true);//enters all of the details
		}
		
		//Step 2 Account information
		Account_Info.User_Info.UUID_NBR = WebDriver_Functions.GetCookieUUID();//print out the UUID for reference
		Account_Info.Account_Nickname = Account_Info.Account_Number + "_" + CountryCode;
		Account_Entry_Screen(Account_Info);

		Verify_Confirmaiton_Page("WFCL_Link", Account_Info);

		//WebDriver_Functions.ChangeURL("https://" + strLevelURL + "/shipping/shipEntryAction.do?origincountry=" + CountryCode.toLowerCase());
		WebDriver_Functions.ChangeURL("INET", CountryCode, false);
		//Register the account number for INET
		if (CountryCode.contains("US") || CountryCode.contains("CA")){
			WebDriver_Functions.WaitPresent(By.name("accountNumberOpco"));
			WebDriver_Functions.Select(By.name("accountNumberOpco"), "1",  "i");
			WebDriver_Functions.takeSnapShot("INET Account Selection.png");
			WebDriver_Functions.Click(By.className("buttonpurple"));
			//need to add clicking continue button

			//Confirmation from INET registration
			try{//EACI form
				Verify_Confirmaiton_Page("INET", Account_Info);
				InetFlag = true;
			}catch (Exception e){
				Helper_Functions.PrintOut("Not able to Verify data on INET registration page.", true);
			}

			WebDriver_Functions.Click(By.xpath("//*[@id='content']/div/table/tbody/tr[1]/td[2]/table[2]/tbody/tr[3]/td/table/tbody/tr[1]/td[4]/table/tbody/tr/td/table/tbody/tr[1]/td[2]/a/img"));
			WebDriver_Functions.WaitPresent(By.xpath("//*[@id='appTitle']"));
		}

		String ReturnValue[] = new String[] {Account_Info.User_Info.USER_ID, Account_Info.User_Info.PASSWORD, Account_Info.Account_Number, Account_Info.User_Info.UUID_NBR, "INET:" + InetFlag};
		Helper_Functions.WriteUserToExcel(Account_Info.User_Info.USER_ID, Account_Info.User_Info.PASSWORD);
		return ReturnValue;
	}//end WFCL_AccountRegistration
		
	public static String[] WFCL_GFBO_Registration(User_Data User_Info, Account_Data Account_Info) throws Exception{

 		try {
 			//if not logged in the do so
 			if (WebDriver_Functions.GetCookieUUID() == null) {
 				WebDriver_Functions.Login(User_Info.USER_ID, User_Info.PASSWORD);
 			}
 			
			WebDriver_Functions.ChangeURL("GFBO_Login", User_Info.Address_Info.Country_Code, false);
			
			if (WebDriver_Functions.isPresent(By.name("newAccountNumber"))){ 
				WebDriver_Functions.Type(By.name("newAccountNumber"), Account_Info.Account_Number);
				WebDriver_Functions.Type(By.name("newNickName"), Account_Info.Account_Nickname);
				WebDriver_Functions.Click(By.name("submit"));
			}

			if (WebDriver_Functions.CheckBodyText("This account number cannot register for FedEx Billing Online.")) {
				throw new Exception("This account number cannot register for FedEx Billing Online.");
			}
			
			AddressMismatchPage(Account_Info);
			InvoiceOrCCValidaiton(Account_Info);
			
			return new String[] {User_Info.USER_ID, User_Info.PASSWORD, Account_Info.Account_Number, "GFBO: "};
 		}catch (Exception e){
 			throw e;
 		}
	}
	
	
	public static String[] AEM_Discount_Validate(User_Data User_Info, Enrollment_Data Enrollment_Info) throws Exception {
		
		try {
			WebDriver_Functions.Login(User_Info.USER_ID, User_Info.PASSWORD);

			WebDriver_Functions.ChangeURL("DT_" + Enrollment_Info.ENROLLMENT_ID, Enrollment_Info.COUNTRY_CODE, false);
			WebDriver_Functions.takeSnapShot(Enrollment_Info.ENROLLMENT_ID + " DT Value.png");
			
			//Helper_Functions.Wait(2);
			//String DTValue = WebDriver_Functions.GetText(By.cssSelector("body > div.fxg-main-content > div > div"));
			String DTValue = WebDriver_Functions.GetBodyText();
			//this is specific to as12853311 in L6, need to find cleaner way to update.
			if (DTValue.contains(" Up to 29% off select FedEx Express U.S. shipping")) {
				DTValue = DTValue.replace(" Up to 29% off select FedEx Express U.S. shipping", "Up to 29% off select FedEx Express U.S. shipping");
			}
			Helper_Functions.PrintOut("DT Discount: ___" + DTValue + "___");
			
			String ApplyNowUrl = "";
			if (!Enrollment_Info.AEM_LINK.contentEquals("")) {
				WebDriver_Functions.ChangeURL(Enrollment_Info.AEM_LINK, Enrollment_Info.COUNTRY_CODE, false);
				ApplyNowUrl = WebDriver_Functions.getURLByLinkText("APPLY NOW");
			}
	
			String CodeRequired = "";
			
			//when membership/pass codes are not needed then check the link of the apply now button.
			if (Enrollment_Info.MEMBERSHIP_ID.contentEquals("") && Enrollment_Info.PASSCODE.contentEquals("")) {
				String ExpectedUrl = WebDriver_Functions.ChangeURL("Enrollment_" + Enrollment_Info.ENROLLMENT_ID, Enrollment_Info.COUNTRY_CODE, false);
				if (!ExpectedUrl.contentEquals(ApplyNowUrl)) {
					throw new Exception("Aem link does not match expected. " + ExpectedUrl + " " + ApplyNowUrl);
				}
			}else {
					if (WebDriver_Functions.isPresent(By.id("field_Passcode"))) {
						WebDriver_Functions.Type(By.id("field_Passcode"), Enrollment_Info.PASSCODE);
						CodeRequired = "Passcode: " + Enrollment_Info.PASSCODE;
					}
					if (WebDriver_Functions.isPresent(By.id("field_Membership ID"))) {
						WebDriver_Functions.Type(By.id("field_Membership ID"), Enrollment_Info.MEMBERSHIP_ID);
						if (!CodeRequired.contentEquals("")) {
							CodeRequired += " & MembershipId: " + Enrollment_Info.MEMBERSHIP_ID;
						}else {
							CodeRequired = "MembershipId: " + Enrollment_Info.MEMBERSHIP_ID;
						}
					}
					WebDriver_Functions.takeSnapShot(Enrollment_Info.ENROLLMENT_ID + " Code required.png");
					WebDriver_Functions.Click(By.cssSelector("div#overview button[type=\"submit\"]"));
					//since will open in new tab this will close and condense tabs.
					WebDriver_Functions.CloseNewTabAndNavigateInCurrent();
				}
			
				String BodyText = WebDriver_Functions.GetBodyText();//for sake of debug

				if (BodyText.contains(DTValue)) {
					WebDriver_Functions.takeSnapShot(Enrollment_Info.ENROLLMENT_ID + " Discount Matching.png");
					/*
					String Login_Cookie = WebDriver_Functions.GetCookieValue("fdx_login");
					USRC_Data User_Info = USRC_Data.LoadVariables(Level);
					String Credit_Card = USRC_API_Endpoints.AccountRetrieval_Then_EnterpriseCustomer(User_Info.GenericUSRCURL, "fdx_login=" + Login_Cookie);
					Apply_Discount(DT_Status, EN, Credit_Card, DTValue, UserId);
					*/
				}else {
					if (WebDriver_Functions.CheckBodyText("Sorry, we cannot find the web page you are looking for.")) {
						throw new Exception("Sorry, we cannot find the web page you are looking for. Need to check if the discount is loaded in environment correctly.");
					}else if (WebDriver_Functions.CheckBodyText("Apply now")){
						throw new Exception("Error when checking discount. Please check why the old marketing page is present.");
					}else {
						throw new Exception("Error when checking discount. Please check to see if discount is migrated.");
					}
				}
				
				if (CodeRequired.contentEquals("")) {
					return new String[] {Enrollment_Info.ENROLLMENT_ID};
				}else {
					return new String[] {Enrollment_Info.ENROLLMENT_ID, CodeRequired};
				}
				
		}catch (Exception e) {
			//added enrollment in front to make easier to see from failed responses.
			throw new Exception(Enrollment_Info.ENROLLMENT_ID + ": " + e.getMessage());
		}
	}
	
	public static String AEM_Error_Validation(Enrollment_Data Enrollment_Info, String Incorrect_Value) throws Exception {
		String passcode = Enrollment_Info.PASSCODE;
		String membership = Enrollment_Info.MEMBERSHIP_ID;
		String ErrorURL = WebDriver_Functions.LevelUrlReturn() + "/en-us/discount-programs/Error.html";
		
		if (!passcode.contentEquals("")) {
			Enrollment_Info.PASSCODE = Incorrect_Value;
			WebDriver_Functions.ChangeURL_EnrollmentID(Enrollment_Info, false, true);
			String CurrentURL = WebDriver_Functions.GetCurrentURL();
			assertThat(CurrentURL, CoreMatchers.containsString(ErrorURL));
			Enrollment_Info.PASSCODE = passcode;
		}
		
		if (!membership.contentEquals("")) {
			Enrollment_Info.MEMBERSHIP_ID = Incorrect_Value;
			WebDriver_Functions.ChangeURL_EnrollmentID(Enrollment_Info, false, true);
			String CurrentURL = WebDriver_Functions.GetCurrentURL();
			assertThat(CurrentURL, CoreMatchers.containsString(ErrorURL));
			Enrollment_Info.MEMBERSHIP_ID = membership;
		}

		return Enrollment_Info.ENROLLMENT_ID + " - Expected Error page is appearing. " + ErrorURL;
	}
	
	public static String[] WFCL_RewardsRegistration(Account_Data Account_Info) throws Exception{
 		try {
 			
 			WebDriver_Functions.ChangeURL("HOME", Account_Info.Billing_Address_Info.Country_Code, true);
 			WebDriver_Functions.ChangeURL("WFCLREWARDS", Account_Info.Billing_Address_Info.Country_Code, true);
 			
 			//click sign up now and begin registration
 			WebDriver_Functions.WaitClickable(By.name("signupnow"));
 			WebDriver_Functions.Click(By.name("signupnow"));
 			
 			if (WebDriver_Functions.isPresent(By.id("accountType2Radio"))) {
 				//select the radio button for the US and Canada region.
 				WebDriver_Functions.Click(By.id("accountType2Radio"));
 			}
 			ContactInfo_Page(Account_Info, true);
 			
 			Account_Info.User_Info.UUID_NBR = WebDriver_Functions.GetCookieUUID();
 			
 			boolean account_entry = Account_Entry_Screen(Account_Info);
 			if (!account_entry) {
 				throw new Exception("Not able to enter account number.");
 			}
 			AddressMismatchPage(Account_Info);
 			InvoiceOrCCValidaiton(Account_Info);
 			
 			//add a step here for the confirmation page.
 			boolean ConfrimationPageCheck = false;
 			if ("US_CA_us_ca_".contains(Account_Info.Billing_Address_Info.Country_Code)) {
 				
 			}else {
 				//This is the AEM button for "Go to My FedEx Rewards"
 				WebDriver_Functions.WaitPresent(By.cssSelector("body > div.fxg-main-content > div > div > div.fxg-wrapper > div.link.section > div > a"));
 				Helper_Functions.PrintOut("Current URL: " + WebDriver_Functions.GetCurrentURL() + "\n" + WebDriver_Functions.getURLByhref(By.cssSelector("body > div.fxg-main-content > div > div > div.fxg-wrapper > div.link.section > div > a"))); 
 				ConfrimationPageCheck = true;
 			}
 			
		    return new String[] {Account_Info.User_Info.USER_ID, Account_Info.User_Info.UUID_NBR, Account_Info.Account_Number, Account_Info.Billing_Address_Info.Country_Code, "Confirmation: " + ConfrimationPageCheck};
 		}catch (Exception e) {
 			throw e;
 		}
 	}//end WFCL_RewardsRegistration
	
	public static String[] WFCL_RewardsLogin(String CountryCode, User_Data User_Info) throws Exception{
 		try {
 			WebDriver_Functions.ChangeURL("HOME", CountryCode, true);
 			WebDriver_Functions.ChangeURL("WFCLREWARDS", CountryCode, true);
 			
 			//login as the user
 			WebDriver_Functions.WaitPresent(By.name("username"));
 			WebDriver_Functions.Type(By.name("username"), User_Info.USER_ID);
 			WebDriver_Functions.Type(By.name("password"), User_Info.PASSWORD);
 			WebDriver_Functions.Click(By.name("login"));
 			
 			//wait until logged in
 			//WebDriver_Functions.WaitForBodyText("My FedEx");
 			//WebDriver_Functions.WaitForBodyText("REWARDS");
 			
 			//check the cookie values         //////////////////////////////////////need to work on this, cookies are not being returned
 			//String SMIDENTITY = WebDriver_Functions.GetCookieValue("SMIDENTITY");
 			//String SMSESSION = WebDriver_Functions.GetCookieValue("SMSESSION");
 			//String fdx_locale = WebDriver_Functions.GetCookieValue("fdx_locale");
 			String UUID = WebDriver_Functions.GetCookieUUID();
 			
 			if (UUID == null) {
 				throw new Exception("User id not logged out. " + UUID);
 			}
		    return new String[] {User_Info.USER_ID, CountryCode, UUID};
 		}catch (Exception e) {
 			throw e;
 		}
 	}
	
	public static String[] WFCL_Rewards_AEM_Link(String CountryCode, String LanguageCode) throws Exception{
 		try {
 			String ConfirmationLink = WebDriver_Functions.ChangeURL("WFCL_REWARDS_CONFIRMATION", CountryCode, LanguageCode, true);
 			
 			//Check the "Go to My FedEx Rewards" link
 			WebDriver_Functions.WaitPresent(By.xpath("/html/body/div[2]/div/div/div[2]/div[5]/div/a"));
 			String RewardsLink = WebDriver_Functions.getURLByhref(By.xpath("/html/body/div[2]/div/div/div[2]/div[5]/div/a"));
 			
 			String ExpectedURL = WebDriver_Functions.ChangeURL("WFCL_REWARDS_PAGE", CountryCode, false);
 			
 			assertThat(ExpectedURL, CoreMatchers.containsString(RewardsLink));
 			
		    return new String[] {CountryCode, LanguageCode, ConfirmationLink};
 		}catch (Exception e) {
 			throw e;
 		}
 	}
	
	public static String[] WFCL_Rewards_Logout(String CountryCode, String LanguageCode, User_Data User_Info) throws Exception{
 		try {
 			WebDriver_Functions.ChangeURL("WFCL_REWARDS_CONFIRMATION", CountryCode, true);
 			WebDriver_Functions.Login(User_Info.USER_ID, User_Info.PASSWORD);
 			WebDriver_Functions.ChangeURL("WFCL_REWARDS_CONFIRMATION", CountryCode, false);
 			WebDriver_Functions.ChangeURL("WFCL_REWARDS_PAGE", CountryCode, false);
 			String LogoutJsPUrl = WebDriver_Functions.ChangeURL("LOGOUT_JSP", CountryCode, LanguageCode, false);
 			
 			String CurrentURL = WebDriver_Functions.GetCurrentURL();
 			Helper_Functions.PrintOut("Current URL is: " + CurrentURL);
 			String ExpectedURL = WebDriver_Functions.ChangeURL("HOME", CountryCode, LanguageCode, false);
 			
 			assertThat(CurrentURL, CoreMatchers.containsString(ExpectedURL));
 			String UUID = WebDriver_Functions.GetCookieUUID();
 			Assert.assertNull(UUID);
		    return new String[] {CountryCode, LanguageCode, LogoutJsPUrl, CurrentURL};
 		}catch (Exception e) {
 			throw e;
 		}
 	}
	
	
}//End Class

/*	
	
	/////////////////////////////////////////////////////////////////////////////////
	

	
	public static void WFCL_GFBO_Login(String UserId, String Password, String Country) throws Exception{
		TestData.LoadTime();
 		
 		try {
 			WebDriver_Functions.ChangeURL("https://" + strLevelURL + "/fcl/?appName=fclgfbo&locale=" + Country.toLowerCase() + "_en&step3URL=https%3A%2F%2F" + strLevelURL + "%2Ffedexbillingonline%2Fregistration.jsp%3FuserRegistration%3DY%26locale%3Den_" + Country.toUpperCase() + "&returnurl=https%3A%2F%2F" + strLevelURL + "%2Ffedexbillingonline%3F%26locale%3Den_" + Country.toUpperCase() + "&programIndicator", true);
 			String WFCLPath = strTodaysDate + " " + strLevel + " WFCL GFBO ";
 			//Login to FBO
 			WebDriver_Functions.Type(By.name("username"), UserId);
 			WebDriver_Functions.Type(By.name("password"), Password);
 			WebDriver_Functions.Click(By.name("login"));
 			
 			//After page loads
 			if (WebDriver_Functions.CheckBodyText("FedEx Billing Online") && WebDriver_Functions.CheckBodyText("Account Summary") && WebDriver_Functions.CheckBodyText("Account Aging Summary")) { //checking body text as their page does not user consistent id's for the different elements.
 				captureScreenShot("WFCL", WFCLPath + "Login.png");
 			}else {
 				throw new Exception("Unable to validate GFBO landing page.");
 			}
 		}catch (Exception e){
 			GeneralFailure(e);
 		}
	}
	
 	
 	
	public static String[] WFCL_AccountRegistration_ISGT(String Name[], String UserId, String AccountNumber, String AddressDetails[]) throws Exception{
		String CountryCode = AddressDetails[6].toUpperCase();
 		TestData.LoadTime();
 		
 		try {
 			String WFCLPath = strTodaysDate + " " + strLevel + CountryCode + " WFCL ISGT ";

 			String UserRegister[] = WFCL_AccountRegistration(Name, UserId, AccountNumber, AddressDetails, WFCLPath);   //uerid, accountnumber, uuid is returned
 	
 			WebDriver_Functions.ChangeURL("https://" + strLevelURL + "/" + CountryCode.toLowerCase() + "/fcl/pckgenvlp/insight/");
 			WebDriver_Functions.Click(By.xpath("/html/body/div[1]/div[2]/div[2]/div/div[4]/a[1]"));//click the Log In link
 			
 			//Login with the user.       //need to recheck if this is expected.
 			WebDriver_Functions.Type(By.name("username"), UserId);
 			WebDriver_Functions.Type(By.name("password"), Account_Info.User_Info.PASSWORD);
 			WebDriver_Functions.Click(By.name("login"));
 			
 			//The InSight confirmation page
 			ElementMatchesThrow(By.xpath("/html/body/table[2]/tbody/tr[2]/td/table[2]/tbody/tr/td[1]/table/tbody/tr[2]/td/table/tbody/tr[1]/td[1]/table/tbody/tr[2]/td/div/b"), UserId, 0);
 			captureScreenShot("WFCL", WFCLPath + " Confirmation.png");
 			String ReturnValue[] = {UserId, AccountNumber, UserRegister[2]};
		    WriteUserToFileAppend(ReturnValue);//Write User to file for later reference
		    return ReturnValue;
 		}catch (Exception e) {
 			GeneralFailure(e);
 			throw e;
 		}
 	}//end WFCL_AccountRegistration
 	

 	
	public static void WFCL_ReturnManager(String Name[], String UserId, String AccountNumber, String AddressDetails[]) throws Exception{       //////////Not finished
		TestData.LoadTime();
 		
 		try {
 			String WFCLPath = strTodaysDate + " " + strLevel + " WFCL GFBO ";
 			WebDriver_Functions.ChangeURL("https://" + strLevelURL + "/fcl/web/jsp/contactInfo.jsp?appName=fclgfbo&locale=" + AddressDetails[6].toLowerCase() + "_en&step3URL=https%3A%2F%2F" + strLevelURL + "%2Ffedexbillingonline%2Fregistration.jsp%3FuserRegistration%3DY%26locale%3Den_" + AddressDetails[6].toUpperCase() + "&afterwardsURL=https%3A%2F%2F" + strLevelURL + "%2Ffedexbillingonline%3F%26locale%3Den_" + AddressDetails[6].toUpperCase() + "&programIndicator");
 			wait.until(ExpectedConditions.presenceOfElementLocated(By.cssSelector("#reminderQuestion option[value=SP2Q1]")));//wait for secret questions to load.

	 		WFCL_ContactInfo_Page(Name, AddressDetails, UserId); //enters all of the details
	 		captureScreenShot("WFCL", WFCLPath + "ContactInformation.png");
	 		if (WebDriver_Functions.isPresent(By.xpath("//input[(@name='accountType') and (@value = 'linkAccount')]"))){
	 			WebDriver_Functions.Click(By.xpath("//input[(@name='accountType') and (@value = 'linkAccount')]"));//select the link account radio button
	 			WebDriver_Functions.Click(By.id("createUserID"));
	 		}else{
	 			WebDriver_Functions.Click(By.id("iacceptbutton"));
	 		}
		    
	 		//Step 2 Account information
	 		String AccountNickname = AccountNumber + "_" + AddressDetails[6];
	 		WFCL_AccountEntryScreen(AccountNumber, AccountNickname, WFCLPath);
	 		
		    //Confirmation page for FBO
	 		wait.until(ExpectedConditions.presenceOfElementLocated(By.id("selectbillingmedium1:j_idt24")));//the header for FBO
	 		captureScreenShot("WFCL", WFCLPath + "Confirmation.png");
	 		WebDriver_Functions.Click(By.id("selectbillingmedium1:continueId"));
	 		
	 		//Registration Confirmation Page
	 		wait.until(ExpectedConditions.textToBe(By.xpath("//*[@id='content']/div/table/tbody/tr[1]/td[2]/table[2]/tbody/tr[3]/td/table/tbody/tr[1]/td[2]/table/tbody/tr[2]/td/b"), UserId));
	 		wait.until(ExpectedConditions.textToBe(By.xpath("//*[@id='content']/div/table/tbody/tr[1]/td[2]/table[2]/tbody/tr[3]/td/table/tbody/tr[2]/td[2]/table/tbody/tr[2]/td/b"), AccountNumber));
	 		wait.until(ExpectedConditions.textToBe(By.xpath("//*[@id='content']/div/table/tbody/tr[1]/td[2]/table[2]/tbody/tr[3]/td/table/tbody/tr[3]/td[2]/table/tbody/tr[2]/td/b"), AccountNickname));
			captureScreenShot("WFCL", WFCLPath + "RegistrationConfirmation.png");

			//Make sure the link on the confirmation page navigates to the correct page.
			wait.until(ExpectedConditions.textToBe(By.xpath("//*[@id='content']/div/table/tbody/tr[1]/td[2]/table[2]/tbody/tr[3]/td/table/tbody/tr[1]/td[4]/table/tbody/tr/td/table/tbody/tr[2]/td"), "        FedEx Billing Online"));	
			WebDriver_Functions.Click(By.xpath("//*[@id='content']/div/table/tbody/tr[1]/td[2]/table[2]/tbody/tr[3]/td/table/tbody/tr[1]/td[4]/table/tbody/tr/td/table/tbody/tr[1]/td[2]/a/img"));

			//The GFBO confirmation page based on if account number is set for invoices.
			if (!driver.findElements(By.id("splash2or3optionsForm:splash3OptionsSubmit")).isEmpty()){
				captureScreenShot("WFCL", WFCLPath + "Congratulations.png");
				WebDriver_Functions.Click(By.id("splash2or3optionsForm:splash3OptionsSubmit"));
			}
			
			//Will now land on the GFBO page
			wait.until(ExpectedConditions.presenceOfElementLocated(By.id("mainContentId:accSmyCmdLink")));//wait for the GFBO page to load
			captureScreenShot("WFCL", WFCLPath + "Page.png");
	 		
		    Helper_Functions.PrintOut("Finished WFCL_AccountRegistration_GFBO  " + UserId + "/" + Account_Info.User_Info.PASSWORD + "--" + AccountNumber);
 		}catch (Exception e) {
 			GeneralFailure(e);
 		}
 	}//end WFCL_ReturnManager
	
	
	public static void WFCL_CreditCardRegistration_Error(String CreditCardDetils[], String enrollmentid, String AddressDetails[], String Name[], String UserId, String ErrorMessage) throws Exception{
 		TestData.LoadTime();

 		try {
 			String WFCLPath = strTodaysDate + " " + strLevel + " WFCL ";
 			WebDriver_Functions.ChangeURL("https://" + strLevelURL + "/cgi-bin/ship_it/interNetShip?origincountry=us&locallang=us&urlparams=us");//go to the INET page just to load the cookies
 			WebDriver_Functions.ChangeURL("https://" + strLevelURL + "/fcl/ALL?enrollmentid=" + enrollmentid + "&OpenAccount=yes&language=en&country=" + AddressDetails[6].toLowerCase());
 			
 			//Step 1: Enter the contact information.
 			wait.until(ExpectedConditions.presenceOfElementLocated(By.cssSelector("#reminderQuestion option[value=SP2Q1]")));//wait for secret questions to load.
	 		WFCL_ContactInfo_Page(Name, AddressDetails, UserId); //enters all of the details
 			if ((AddressDetails[6].contentEquals("US") || AddressDetails[6].contentEquals("CA"))){
 				WebDriver_Functions.Click(By.xpath("//input[(@name='accountType') and (@value = 'openAccount')]"));//select the open account radio button
 			}
	 		
	 		WebDriver_Functions.Click(By.id("createUserID"));

	 		//Step 2: Enter the credit card details
	 		wait.until(ExpectedConditions.presenceOfElementLocated(By.id("CCType")));
	 		WFCL_CC_Page(CreditCardDetils, AddressDetails[6], "WFCL", WFCLPath + "CC Information.png", false, false);
			ElementMatchesThrow(By.cssSelector("#invalidCreditcard > div"), ErrorMessage, 324229); 
			captureScreenShot("WFCL", WFCLPath + "OADR Error Message.png");
        }catch (Exception e) {
        	GeneralFailure(e);
        	throw e;
 		}
		
 	}//end WFCL_CreditCardRegistration_Error
	
	public static String[] WFCL_OpenAccountExistingUser(String UserId, String Password, String[] CreditCardDetils, String AddressDetails[]) throws Exception{
		try {		
	 		TestData.LoadTime();
	 		String WFCLPath = strTodaysDate + " " + strLevel + " WFCL OpenToExisting ";
			
	 		for (int i = 0; i < EnrollmentIDs.length; i++){
	 			if (EnrollmentIDs[i][1].contentEquals(AddressDetails[6].toUpperCase())){
	 				WebDriver_Functions.ChangeURL("https://" + strLevelURL + "/fcl/ALL?enrollmentid=" + EnrollmentIDs[i][0]);
	 				break;
	 			}else if (i == EnrollmentIDs.length - 1){
	 				Helper_Functions.PrintOut("Discount not found for " + AddressDetails[6].toUpperCase() + ".");
					throw new Exception("Discount not found");
	 			}
	 		}
			
	 		wait.until(ExpectedConditions.presenceOfElementLocated(By.name("username")));
	 		WebDriver_Functions.Type(By.name("username"), UserId);
	 		WebDriver_Functions.Type(By.name("password"), Password);
	 		WebDriver_Functions.Click(By.name("login"));
	 		Helper_Functions.PrintOut("Logged in with " + UserId + "/" + Password);
	 		
	 		WebDriver_Functions.Click(By.xpath("//input[(@name='whichBillingAddress') and (@value = 'useNewAddress')]"));
	 		captureScreenShot("WFCL", WFCLPath + "Open.png");
	 		WebDriver_Functions.Click(By.name("continue"));
	 		
	 		//Step 2: Enter the credit card details
	 		wait.until(ExpectedConditions.presenceOfElementLocated(By.id("CCType")));
	 		String UserDetails = driver.findElement(By.cssSelector("#billing-address-readonly > strong")).getText();
	 		CreditCardDetils = WFCL_CC_Page(CreditCardDetils, AddressDetails[6], "WFCL", WFCLPath + "CC Information.png", false, true);
	 		
			//Step 3: Confirmation Page
			try{
				//wait.until(ExpectedConditions.presenceOfElementLocated(By.xpath("//*[@id='rightColumn']/table/tbody/tr/td[1]/div/div[1]/h2")));//not sure why had .click here
				wait.until(ExpectedConditions.textToBe(By.xpath("//*[@id='rightColumn']/table/tbody/tr/td[1]/div/div[1]/div[2]/table/tbody/tr[1]/td[2]"), UserId));
				captureScreenShot("WFCL", WFCLPath + "CC Confirmation.png");
			}catch (Exception e){
				Helper_Functions.PrintOut("CC Confirmation page did not load.  " + UserDetails);
			}
	 		
			//Step 4: Check that account number is saved to user.
			String AccountNumber = driver.findElement(By.id("acctNbr")).getText();
			
			Helper_Functions.PrintOut("WFCL_OpenAccountExistingUser Did not fully complete, need to add a check to make sure account number created is linked to user. " + AccountNumber);
			Helper_Functions.PrintOut(UserDetails);
			return new String[] {UserId, AccountNumber};
		}catch (Exception e) {
			GeneralFailure(e);
			throw e;
		}
	}//end WFCL_OpenAccountExistingUser
	

 

	//will return a string array with 0 as the user id and 1 as the password, 2 is the uuid
	public static boolean WFCL_Captcha(String UserId, String[] Name, String AddressDetails[]) throws Exception{
 		TestData.LoadTime();
 		
 		try {
 			String WFCLPath = strTodaysDate + " " + strLevel + " WFCL ";
 		//Testing the newer gui
 			
 			WebDriver_Functions.ChangeURL("https://" + strLevelURL + "/fcl/web/jsp/contactInfo1.jsp?appName=oadr&locale=" + AddressDetails[6].toLowerCase() + "_en&step3URL=https%3A%2F%2F.fedex.com%3A443%2Ffcl%2Fweb%2Fjsp%2FfclValidateAndCreate.jsp&afterwardsURL=https%3A%2F%2F" + strLevelURL + "%3A443%2Ffcl%2Fweb%2Fjsp%2Foadr.jsp&programIndicator=p314t9n1ey&accountOption=link&captcha=true");
 			wait.until(ExpectedConditions.presenceOfElementLocated(By.cssSelector("#reminderQuestion option[value=SP2Q1]")));//wait for secret questions to load.
 			
 			//select the no account radio button
	 		WFCL_ContactInfo_Page(Name, AddressDetails, UserId); //enters all of the details
	 		WebDriver_Functions.Click(By.xpath("//input[(@name='accountType') and (@value = 'noAccount')]"));
	 		WebDriver_Functions.Click(By.id("createUserID"));

	 		WebDriver_Functions.WaitForText(By.id("directions-verbose-label"), "Type the moving characters");
	 		WebDriver_Functions.WaitForText(By.id("directions-label"), "Moving characters:");
	 		WebDriver_Functions.Click(By.id("my-nucaptcha-refresh"));
	 		captureScreenShot("WFCL", WFCLPath + "Image Captcha.png");
	 		
	 		WebDriver_Functions.Click(By.id("my-nucaptcha-audio"));
	 		WebDriver_Functions.WaitForText(By.id("directions-verbose-label"), "Type the characters you hear in the audio");
	 		WebDriver_Functions.WaitForText(By.id("directions-label"), "Characters you hear:");
	 		captureScreenShot("WFCL", WFCLPath + "Audio Captcha.png");
	 		
	 		//Enter an invalid answer
	 		WebDriver_Functions.Type(By.id("nucaptcha-answer"), "Wrong");
	 		WebDriver_Functions.Click(By.id("createUserID"));
	 		
	 		return true;
 		}catch (Exception e) {
 			GeneralFailure(e);
 			throw e;
 		}
 	}//end WFCL_Captcha
	
	//will return a string array with 0 as the user id and 1 as the password, 2 is the uuid
	public static boolean WFCL_Captcha_Legacy(String UserId, String[] Name, String AddressDetails[]) throws Exception{
 		TestData.LoadTime();
 		
 		try {
 			String WFCLPath = strTodaysDate + " " + strLevel + " WFCL Legacy ";
	 	//Now test the WFCL legacy, in this case testing the WDPA page
	 		String CountryCode = AddressDetails[6].toUpperCase();
	 		WebDriver_Functions.ChangeURL("https://" + strLevelURL + "/PickupApp/login?locale=en_" + CountryCode + "&programIndicator=4", true);
	 		WebDriver_Functions.Click(By.name("signupnow"));
	 		String currentURL =  driver.getCurrentUrl();
	 		WebDriver_Functions.ChangeURL(currentURL + "&captcha=true", false);//update the url to captcha
	 		WFCL_ContactInfo_Page(Name, AddressDetails, UserId); //enters all of the details
	 		WebDriver_Functions.Click(By.id("iacceptbutton"));
	 			
	 		WebDriver_Functions.WaitForText(By.id("directions-verbose-label"), "Type the moving characters");
	 		WebDriver_Functions.WaitForText(By.id("directions-label"), "Moving characters:");
	 		WebDriver_Functions.Click(By.id("my-nucaptcha-refresh"));
	 		MoveTo(By.id("iacceptbutton"));//added to make sure screenshots cover the area
	 		captureScreenShot("WFCL", WFCLPath + "Image Captcha.png");
	 		
	 		WebDriver_Functions.Click(By.id("my-nucaptcha-audio"));
	 		WebDriver_Functions.WaitForText(By.id("directions-verbose-label"), "Type the characters you hear in the audio");
	 		WebDriver_Functions.WaitForText(By.id("directions-label"), "Characters you hear:");
	 		MoveTo(By.id("iacceptbutton"));//added to make sure screenshots cover the area
	 		captureScreenShot("WFCL", WFCLPath + "Audio Captcha.png");

	 		return true;
 		}catch (Exception e) {
 			GeneralFailure(e);
 			throw e;
 		}
 	}//end WFCL_Captcha

    public static String[] WFCL_GFBO_Invitaiton(String UserID, String Password, String Name[], String Email, String UserRole) throws Exception{
		TestData.LoadTime();
	 		
		try {
			String WFCLPath = strTodaysDate + " " + strLevel + " GFBO Invite ";
			
			Login(UserID, Password);
			WebDriver_Functions.ChangeURL("https://" + strLevelURL + "/fedexbillingonline?locale=en_US");
			captureScreenShot("WFCL", WFCLPath + "UserTable before invite.png");
			
			WebDriver_Functions.Click(By.cssSelector("a#mainContentId\\3a myOptnCmdLink")); 
			WebDriver_Functions.Click(By.cssSelector("a#mainContentId\\3a manageUsersId")); 
			WebDriver_Functions.Click(By.xpath("//*[@value='Invite new user']"));//invite new user button, the id and other directions don't work and change on each attempt.
			WebDriver_Functions.Type(By.xpath("//*[@id='mainContentId:j_idt227']"), Name[0]);//first name
			WebDriver_Functions.Type(By.xpath("//*[@id='mainContentId:j_idt232']"), Name[2]);//last name
			WebDriver_Functions.Type(By.xpath("//*[@id='mainContentId:j_idt237']"), Email);//email
			
			WebDriver_Functions.Select(By.xpath("//*[@id='mainContentId:j_idt242']"), UserRole, "v"); //2=standard  3=view only
			
			captureScreenShot("WFCL", WFCLPath + "Invitation");
			WebDriver_Functions.Click(By.cssSelector("input#mainContentId\\3a inviteNewuserButton"));

			Helper_Functions.PrintOut("Finished " + Thread.currentThread().getStackTrace()[2].getMethodName());
			return new String[] {UserID, Arrays.toString(Name), Email};
		}catch (Exception e) {
			GeneralFailure(e);
			throw e;
		}
    }
    
}

*/