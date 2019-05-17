package WFCL_Application;

import java.util.Arrays;

import org.openqa.selenium.By;

import SupportClasses.DriverFactory;
import SupportClasses.Environment;
import SupportClasses.Helper_Functions;
import SupportClasses.WebDriver_Functions;

public class WFCL_Functions{
	
	public static String[] CreditCardRegistrationEnroll(String EnrollmentID[], String CreditCardDetils[], String AddressDetails[], String BillingAddressDetails[], String Name[], String UserId, String Email, boolean BusinessAccount, String TaxInfo[]) throws Exception{
		try {
			String CountryCode = AddressDetails[6];
			//go to the INET page just to load the cookies
			//WebDriver_Functions.ChangeURL("INET", CountryCode, true);
			WebDriver_Functions.ChangeURL("Enrollment_" + EnrollmentID[0], CountryCode, true);

			//add the country code to the screenshot name 
			DriverFactory.setScreenshotPath(DriverFactory.getScreenshotPath() + CountryCode + " ");
			
			if (WebDriver_Functions.isPresent(By.name("Apply Now"))) {
				//if passcode is needed to be entered on discount page.
				if (WebDriver_Functions.isPresent(By.name("passCode"))) {
					WebDriver_Functions.Type(By.name("passCode"), EnrollmentID[3]);
				}
				if (WebDriver_Functions.isPresent(By.name("membershipID"))) {
					WebDriver_Functions.Type(By.name("membershipID"), EnrollmentID[4]);
				}
				
				WebDriver_Functions.takeSnapShot("Discount Page.png");
				//apply link from marketing page
				WebDriver_Functions.Click(By.name("Apply Now"));
			}
			
			if (WebDriver_Functions.isPresent(By.linkText("Finish registering for a FedEx account"))) {
				WebDriver_Functions.Click(By.linkText("Finish registering for a FedEx account"));
			}else if (WebDriver_Functions.isPresent(By.name("signupnow"))) {
				WebDriver_Functions.Click(By.name("signupnow"));
			}

			//Step 1: Enter the contact information.
			WFCL_ContactInfo_Page(Name, AddressDetails, UserId, Email, true); //enters all of the details

			//Step 2: Enter the credit card details
			WebDriver_Functions.WaitPresent(By.id("CCType"));
			boolean Multicard = true;
			if(TaxInfo != null && !TaxInfo[3].contains("Valid")) {
				Multicard = false;
				CreditCardDetils = WFCL_CC_Page(CreditCardDetils, AddressDetails, BillingAddressDetails, BusinessAccount, Multicard, TaxInfo);
				WebDriver_Functions.takeSnapShot("Invalid attempt " +  TaxInfo[3] + ".png");
				return new String[] {"Invalid attempt : " +  TaxInfo[3] };
			}
			CreditCardDetils = WFCL_CC_Page(CreditCardDetils, AddressDetails, BillingAddressDetails, BusinessAccount, Multicard, TaxInfo);
			
			//Step 3: Confirmation Page
			//Domestic and international will be different
			WebDriver_Functions.WaitForBodyText(UserId);    //will not check if in correct location
			//WebDriver_Functions.WaitOr_TextToBe(By.xpath("//*[@id='rightColumn']/table/tbody/tr/td[1]/div/div[1]/div[2]/table/tbody/tr[1]/td[2]"), UserId, By.xpath("//*[@id='rightColumn']/table/tbody/tr/td[1]/div/div/div[2]/table/tbody/tr[1]/td[2]"),  UserId);
			String AccountNumber = WebDriver_Functions.GetText(By.xpath("//*[@id='acctNbr']"));
			WebDriver_Functions.takeSnapShot("Confirmation.png");

			boolean InetFlag = false, AdminFlag = false;

			//Check the INET page
			InetFlag = Check_INET_Enrolled(CountryCode);

			//Check the Administration page
			AdminFlag = Check_WADM_Enrolled(CountryCode);
			
			String UUID = WebDriver_Functions.GetCookieValue("fcl_uuid");
			Helper_Functions.PrintOut("Finished CreditCardRegistrationEnroll  " + UserId + "/" + Helper_Functions.myPassword + "--" + AccountNumber + "--" + UUID, true);

			String LastFourOfCC = CreditCardDetils[1].substring(CreditCardDetils[1].length() - 4, CreditCardDetils[1].length());
			String ReturnValue[] = new String[] {UserId, AccountNumber, UUID, LastFourOfCC, "INET:" + InetFlag, "Admin:" + AdminFlag};
			
			Helper_Functions.WriteUserToExcel(UserId, Helper_Functions.myPassword);
			return ReturnValue;
		}catch (Exception e) {
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
			//navigates to the US home page as other regions may not be present.
			WebDriver_Functions.ChangeURL("HOME", "US", false);
			WebDriver_Functions.ChangeURL("WADM", "US", false);
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
	public static String[] WFCL_UserRegistration(String UserId, String[] Name, String Email, String AddressDetails[]) throws Exception{
		try {
			String CountryCode = AddressDetails[6];
			WebDriver_Functions.ChangeURL("Pref", CountryCode, true);//navigate to email preferences page to load cookies
			WebDriver_Functions.Click(By.id("registernow"));
			
			WFCL_ContactInfo_Page(Name, AddressDetails, UserId, Email, true); //enters all of the details

			//Confirmation page
			WebDriver_Functions.WaitForText(By.xpath("//*[@id='rightColumn']/table/tbody/tr/td[1]/div/div/h2"), "Login Information");
			WebDriver_Functions.WaitForText(By.xpath("//*[@id='rightColumn']/table/tbody/tr/td[1]/div/div/div[2]/table/tbody/tr/td[2]"), UserId);
			WebDriver_Functions.takeSnapShot("RegistrationConfirmation.png");
			String UUID = WebDriver_Functions.GetCookieValue("fcl_uuid");
			Helper_Functions.PrintOut("Finished WFCL_UserRegistration  " + UserId + "/" + Helper_Functions.myPassword + " -- " + UUID, true);
			String ReturnValue[] = new String[]{UserId, UUID};
			Helper_Functions.WriteUserToExcel(UserId, Helper_Functions.myPassword);//Write User to file for later reference
			return ReturnValue;
		}catch (Exception e) {
			if (e.getMessage().contains("[(@name='accountType') and (@value = 'noAccount')]")){
				Helper_Functions.PrintOut("Radio button not present for " + AddressDetails[6] + " to do User id creation.", true);
			}
			throw e;
		}
	}//end WFCL_UserRegistration
	
	public static String[] WFCL_AccountRegistration_INET(String Name[], String UserId, String Email, String AccountNumber, String AddressDetails[]) throws Exception{
		boolean InetFlag = false;
		String CountryCode = AddressDetails[6].toUpperCase();

		if (CountryCode.contentEquals("US") || CountryCode.contentEquals("CA")){
			WebDriver_Functions.ChangeURL("FCLLink", CountryCode, true);
			WebDriver_Functions.Click(By.xpath("//input[(@name='accountType') and (@value = 'linkAccount')]"));//select the link account radio button
			WebDriver_Functions.WaitPresent(By.cssSelector("#reminderQuestion option[value=SP2Q1]"));//wait for secret questions to load.
			WFCL_ContactInfo_Page(Name, AddressDetails, UserId, Email, true); //enters all of the details
		}else{
			WebDriver_Functions.ChangeURL("FCLLinkInter", CountryCode, true);
			WebDriver_Functions.WaitPresent(By.cssSelector("#reminderQuestion"));
			WFCL_ContactInfo_Page(Name, AddressDetails, UserId, Email, true); //enters all of the details
		}
		

		//Step 2 Account information
		String UUID = WebDriver_Functions.GetCookieValue("fcl_uuid");//print out the UUID for reference
		String AccountNickname = AccountNumber + "_" + CountryCode;
		WFCL_AccountEntryScreen(AccountNumber, AccountNickname);

		if (CountryCode.contains("US")){
			WebDriver_Functions.WaitForText(By.xpath("//*[@id='rightColumn']/table/tbody/tr/td[1]/div/div[1]/div[2]/table/tbody/tr[1]/td[2]"), UserId);
			//WebDriver_Functions.WaitForText(By.xpath("//*[@id='rightColumn']/table/tbody/tr/td[1]/div/div[1]/div[2]/table/tbody/tr[2]/td[2]"), AccountNumber);    //will need to update due to account masking
		}
		WebDriver_Functions.takeSnapShot("RegistrationConfirmation.png");
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
				WebDriver_Functions.WaitForText(By.xpath("//*[@id='content']/div/table/tbody/tr[1]/td[2]/table[2]/tbody/tr[3]/td/table/tbody/tr[1]/td[2]/table/tbody/tr[2]/td/b"), UserId);
				//WebDriver_Functions.WaitForText(By.xpath("//*[@id='content']/div/table/tbody/tr[1]/td[2]/table[2]/tbody/tr[3]/td/table/tbody/tr[2]/td[2]/table/tbody/tr[2]/td/b"), AccountNumber);
				WebDriver_Functions.WaitForText(By.xpath("//*[@id='content']/div/table/tbody/tr[1]/td[2]/table[2]/tbody/tr[3]/td/table/tbody/tr[3]/td[2]/table/tbody/tr[2]/td/b"), AccountNickname);
				WebDriver_Functions.WaitForText(By.xpath("//*[@id='content']/div/table/tbody/tr[1]/td[2]/table[2]/tbody/tr[3]/td/table/tbody/tr[3]/td[4]/table/tbody/tr[2]/td/table/tbody/tr/td[2]/b"), AddressDetails[0] + "\n" + AddressDetails[2] + ", " + AddressDetails[4] + " " + AddressDetails[5] + "\n" + AddressDetails[6].toLowerCase());
				InetFlag = true;
			}catch (Exception e){
				Helper_Functions.PrintOut("Not able to Verify data on INET registration page.", true);
			}
			
			WebDriver_Functions.takeSnapShot("INET Confirmation.png");
			WebDriver_Functions.Click(By.xpath("//*[@id='content']/div/table/tbody/tr[1]/td[2]/table[2]/tbody/tr[3]/td/table/tbody/tr[1]/td[4]/table/tbody/tr/td/table/tbody/tr[1]/td[2]/a/img"));
			WebDriver_Functions.WaitPresent(By.xpath("//*[@id='appTitle']"));
		}

		Helper_Functions.PrintOut("Finished WFCL_AccountRegistration  " + UserId + "/" + Helper_Functions.myPassword + "--" + AccountNumber + "--" + UUID, true);
		String ReturnValue[] = new String[] {UserId, AccountNumber, UUID, "INET:" + InetFlag};
		Helper_Functions.WriteUserToExcel(UserId, Helper_Functions.myPassword);
		return ReturnValue;
	}//end WFCL_AccountRegistration

	public static boolean Admin_Registration(String CountryCode, String AccountNumber) throws Exception {
		try{
			WebDriver_Functions.ChangeURL("ADMINREG", CountryCode, false);
			
			if (WebDriver_Functions.isPresent(By.name("accountNumber"))){ 
				WebDriver_Functions.Select(By.name("accountNumber"), "1", "i");
				WebDriver_Functions.Click(By.name("submit"));	
			}

			Helper_Functions.Wait(2);
			
			if (WebDriver_Functions.isPresent(By.name("invoiceNumberA")) || WebDriver_Functions.isPresent(By.name("creditCardNumber"))) {
				InvoiceOrCCValidaiton();
			}
			
			WebDriver_Functions.WaitPresent(By.name("companyName"));
			String CompanyName = "Company" + Helper_Functions.CurrentDateTime();
			WebDriver_Functions.Type(By.name("companyName"), CompanyName);
			WebDriver_Functions.takeSnapShot("WADM CompanyName.png");
			WebDriver_Functions.Click(By.className("buttonpurple"));


			WebDriver_Functions.WaitPresent(By.cssSelector("#confirmation > div > div.fx-col.col-3 > div > h3"));
			Helper_Functions.PrintOut("Registered for Admin. Current URL:" + DriverFactory.getInstance().getDriver().getCurrentUrl(), true);
			WebDriver_Functions.takeSnapShot("WADM Registration Confirmaiton.png");
			
			WebDriver_Functions.ChangeURL("WADM", CountryCode, false);
			WebDriver_Functions.WaitForText(By.cssSelector("#main > h1"), "Admin Home: ");
			//remove the account number from local storage as it is now locked to the company that was just created.
			Helper_Functions.RemoveAccountFromAccount_Numbers(Environment.getInstance().getLevel(), AccountNumber);
		}catch (Exception e){
			Helper_Functions.PrintOut("Failure with admin registriaton.", true);
			throw e;
		};
		
		return true;
	}

	
	public static String WFCL_AccountLinkage(String Name[], String UserId, String Email, String AccountNumber, String AddressDetails[], String AccountNickname) throws Exception{
		Helper_Functions.PrintOut("Attempting to register with " + AccountNumber, true);
		String CountryCode = AddressDetails[6].toUpperCase();

		if (CountryCode.contentEquals("US") || CountryCode.contentEquals("CA")){
			WebDriver_Functions.ChangeURL("FCLLink", CountryCode, true);
			WebDriver_Functions.Click(By.xpath("//input[(@name='accountType') and (@value = 'linkAccount')]"));//select the link account radio button
			WebDriver_Functions.WaitPresent(By.cssSelector("#reminderQuestion option[value=SP2Q1]"));//wait for secret questions to load.
			WFCL_ContactInfo_Page(Name, AddressDetails, UserId, Email,true); //enters all of the details
		}else{
			WebDriver_Functions.ChangeURL("FCLLinkInter", CountryCode, true);
			WebDriver_Functions.WaitPresent(By.cssSelector("#reminderQuestion"));
			WFCL_ContactInfo_Page(Name, AddressDetails, UserId, Email,true); //enters all of the details
		}

		//Step 2 Account information
		WFCL_AccountEntryScreen(AccountNumber, AccountNickname);

		if (CountryCode.contains("US")){
			WebDriver_Functions.WaitForText(By.xpath("//*[@id='rightColumn']/table/tbody/tr/td[1]/div/div[1]/div[2]/table/tbody/tr[1]/td[2]"), UserId);
			//set the AccountNumberCheck value to last three digits of the account number
			String AccountNumberCheck = AccountNumber.substring(AccountNumber.length() - 3, AccountNumber.length());
			if (AccountNickname.isEmpty()) {
				AccountNumberCheck = "My Account - " + AccountNumberCheck;
			}else {
				AccountNumberCheck = AccountNickname + " - " + AccountNumberCheck;
			}
			//the below was updated due to account masking and story 385883
			WebDriver_Functions.WaitForText(By.xpath("//*[@id='rightColumn']/table/tbody/tr/td[1]/div/div[1]/div[2]/table/tbody/tr[2]/td[2]"), AccountNumberCheck);
		}
		WebDriver_Functions.takeSnapShot("RegistrationConfirmation.png");
		
		String UUID = WebDriver_Functions.GetCookieValue("fcl_uuid");
		Helper_Functions.PrintOut("Finished WFCL_AccountLinkage  " + UserId + "/" + Helper_Functions.myPassword + "--" + AccountNumber + "--" + UUID, true);
		Helper_Functions.WriteUserToExcel(UserId, Helper_Functions.myPassword);
		String ReturnValue[] = new String[] {UserId, Helper_Functions.myPassword, AccountNumber, UUID};
		return Arrays.toString(ReturnValue);
	}//end WFCL_AccountRegistration

	public static String[] WDPA_Registration(String Name[], String UserId, String Email, String AccountNumber, String AddressDetails[]) throws Exception{
 		try {
 			String CountryCode = AddressDetails[6].toUpperCase();
 			WebDriver_Functions.ChangeURL("WDPA", CountryCode, true);
 			if (WebDriver_Functions.isPresent(By.name("signupnow"))) {
 				WebDriver_Functions.Click(By.name("signupnow"));
 			}else if (WebDriver_Functions.isPresent(By.xpath("//*[@id='module.logon.newusers._expanded']/a[1]"))) {
 				WebDriver_Functions.Click(By.xpath("//*[@id='module.logon.newusers._expanded']/a[1]"));
 			}else if (WebDriver_Functions.isPresent(By.id("loginLink"))) {//Added for APAC
 				WebDriver_Functions.Click(By.id("loginLink"));
 			}
 			
 	
 			WFCL_ContactInfo_Page(Name, AddressDetails, UserId, Email, true); //enters all of the details
 			
	 		//Step 2 Account information
	 		String AccountNickname = AccountNumber + "_" + CountryCode;
	 		WFCL_AccountEntryScreen(AccountNumber, AccountNickname);
	 		
	 		WebDriver_Functions.WaitForText(By.xpath("//*[@id='content']/div/table/tbody/tr[1]/td[2]/p[2]/table[2]/tbody/tr[3]/td/table[2]/tbody/tr/td[1]/table/tbody/tr[2]/td/b"), UserId);
	 		//*[@id="content"]/div/table/tbody/tr[1]/td[2]/p[2]/table[2]/tbody/tr[3]/td/table[2]/tbody/tr/td[1]/table/tbody/tr[2]/td/b
	 		WebDriver_Functions.takeSnapShot("RegistrationConfirmation.png");
		    
		    WebDriver_Functions.Click(By.xpath("//*[@id='content']/div/table/tbody/tr[1]/td[2]/p[2]/table[2]/tbody/tr[3]/td/table[2]/tbody/tr/td[2]/table/tbody/tr/td/table/tbody/tr[1]/td[2]/a/img"));
		    WebDriver_Functions.WaitPresent(By.id("module.account._headerTitle"));
		    
		    String UUID = WebDriver_Functions.GetCookieValue("fcl_uuid");
		    Helper_Functions.PrintOut("Finished WFCL_AccountRegistration_WDPA  " + UserId + "/" + Helper_Functions.myPassword + "--" + AccountNumber + "--" + UUID, true);
		    String ReturnValue[] = new String[] {UserId, AccountNumber, UUID};
		    Helper_Functions.WriteUserToExcel(UserId, Helper_Functions.myPassword);
		    return ReturnValue;
 		}catch (Exception e) {
 			throw e;
 		}
 	}//end WFCL_AccountRegistration_WDPA
	
	public static String Forgot_User_Email(String CountryCode, String Email) throws Exception{
		try{
			WebDriver_Functions.ChangeURL("INET", CountryCode, true);//go to the INET page just to load the cookies
			WebDriver_Functions.Click(By.name("forgotUidPwd"));
			//wait for text box for user id to appear
			WebDriver_Functions.Type(By.name("email"),Email);

			WebDriver_Functions.takeSnapShot("Forgot User Id.png");
			WebDriver_Functions.Click(By.xpath("//*[@id='module.forgotuseridandpassword._expanded']/table/tbody/tr/td[3]/form/table/tbody/tr[6]/td/input[2]"));
			WebDriver_Functions.WaitPresent(By.id("linkaction"));
			String Text = WebDriver_Functions.GetText(By.xpath("//*[@id='content']/div/table/tbody/tr[1]/td/table/tbody/tr/td/table/tbody/tr[1]/td"));
			WebDriver_Functions.takeSnapShot("Forgot User Confirmation.png");
			Helper_Functions.PrintOut("Completed Forgot User Confirmation using " + Email + ". An email has been triggered and that test must be completed manually by to see the user list.", true);
			return Email;
		}catch(Exception e){
			throw e;
		}
	}//end Forgot_User_Email

	//AddressDetails[] =  {Streetline1 - 0, Streetline2 - 1, City - 2, State - 3, StateCode - 4, postalCode - 5, countryCode - 6};
	//String Name[] = {FirstName, Middle Name, Last Name}
	public static boolean WFCL_ContactInfo_Page(String Name[], String AddressDetails[], String UserId, String Email, boolean Submit) throws Exception{
		WebDriver_Functions.CheckBodyText("Country/Territory");
		WebDriver_Functions.WaitPresent(By.cssSelector("#reminderQuestion option[value=SP2Q1]"));//wait for secret questions to load.

		WebDriver_Functions.Type(By.id("firstName"), Name[0]);
		WebDriver_Functions.Type(By.name("initials"), Name[1]);
		WebDriver_Functions.Type(By.id("lastName"), Name[2]);
		WebDriver_Functions.Type(By.id("email"), Email);
		WebDriver_Functions.Type(By.id("retypeEmail"), Email);
		WebDriver_Functions.Type(By.id("address1"), AddressDetails[0]);
		WebDriver_Functions.Type(By.name("address2"), AddressDetails[1]);

		if (WebDriver_Functions.isPresent(By.name("city"))) {
			WebDriver_Functions.Type(By.name("city"), AddressDetails[2]);
		}else if (WebDriver_Functions.isPresent(By.name("city1"))){
			WebDriver_Functions.Type(By.name("city1"), AddressDetails[2]);
		}

		if (AddressDetails[4] != null && AddressDetails[4] != ""){
			try{
				WebDriver_Functions.Select(By.id("state"), AddressDetails[4],  "v");
			}catch (Exception e){
				try {
					WebDriver_Functions.Type(By.name("state"), AddressDetails[4]);//for the legacy registration page such as WDPA the code must be entered as text
				}catch (Exception e2){}
			};
		}

		WebDriver_Functions.Type(By.id("zip"), AddressDetails[5]);
		String strPhone = Helper_Functions.ValidPhoneNumber(AddressDetails[6]);
		WebDriver_Functions.Type(By.id("phone"), strPhone);
		WebDriver_Functions.Type(By.id("uid"), UserId);
		WebDriver_Functions.Type(By.id("password"), Helper_Functions.myPassword);
		WebDriver_Functions.Type(By.id("retypePassword"), Helper_Functions.myPassword);
		WebDriver_Functions.Select(By.id("reminderQuestion"), "SP2Q1", "v"); //"What is your mother's first name?"
		WebDriver_Functions.Type(By.id("reminderAnswer"), "mom");

		if (WebDriver_Functions.isPresent(By.id("acceptterms")) && DriverFactory.getInstance().getDriver().findElement(By.id("acceptterms")).isSelected() == false) {
			WebDriver_Functions.Click(By.id("acceptterms"));
		}

		Helper_Functions.PrintOut("Name: " + Arrays.toString(Name) + "    UserID:" + UserId, true);

		WebDriver_Functions.takeSnapShot("ContactInformation.png");
		//if the submit flag is sent as true will submit the page.
		if (Submit) {
			if (WebDriver_Functions.isPresent(By.id("iacceptbutton"))) {
				WebDriver_Functions.Click(By.id("iacceptbutton"));
			}else if (WebDriver_Functions.isPresent(By.id("createUserID"))) {
				WebDriver_Functions.Click(By.id("createUserID"));
			}
		}
		
		//The Captcha image
		if (WebDriver_Functions.isPresent(By.id("nucaptcha-answer"))) {
			Helper_Functions.PrintOut("Captcha image appearing on page. Need to manually enter the value", true);
			Helper_Functions.Wait(10);
			WebDriver_Functions.WaitNotPresent(By.id("nucaptcha-answer"));
		}


		return true;
	}//end WFCL_ContactInfo_Page

	public static String[] WFCL_CC_Page(String[] CreditCardDetils, String ShippingAddress[], String BillingAddress[], boolean BusinessRegistration, boolean MultiCard, String TaxInfo[]) throws Exception{
		String CountryCode = BillingAddress[6];
		Helper_Functions.PrintOut("WFCL_CC_Page recieved: " + CreditCardDetils[0] + ", " + CountryCode + " From: " + Thread.currentThread().getStackTrace()[2].getMethodName(), true);
		WebDriver_Functions.GetCookieValue("fcl_uuid");

		WebDriver_Functions.WaitForTextPresentIn(By.cssSelector("#CCType"), CreditCardDetils[0].substring(0,1).toUpperCase());//wait for the credit card types to load

		WebDriver_Functions.Type(By.id("creditCardNumber"), CreditCardDetils[1]);
		WebDriver_Functions.Type(By.id("creditCardIDNumber"), CreditCardDetils[2]);
		WebDriver_Functions.Select(By.id("monthExpiry"), CreditCardDetils[3], "t");
		WebDriver_Functions.Select(By.id("yearExpiry"), CreditCardDetils[4], "t");

		if (WebDriver_Functions.isPresent(By.name("editshipinfo"))) {
			WebDriver_Functions.Click(By.name("editshipinfo"));//the shipping address section
			WebDriver_Functions.ElementMatches(By.xpath("//*[@id='shipping-address-fields']/label[12]/span[1]"), "Country/Territory", 116577);
		}

		if (WebDriver_Functions.isPresent(By.name("editccinfo"))) {
			WebDriver_Functions.Click(By.name("editccinfo"));//edit the billing address
			if (WebDriver_Functions.isPresent(By.xpath("//*[@id='billing-address']/label[17]/span[1]"))) {
				WebDriver_Functions.ElementMatches(By.xpath("//*[@id='billing-address']/label[17]/span[1]"), "Country/Territory", 116577);
			}else if (WebDriver_Functions.isPresent(By.xpath("//*[@id='billing-address']/label[20]/span[1]"))) {//Non US field    changed from 18 to 20 for emea
				WebDriver_Functions.ElementMatches(By.xpath("//*[@id='billing-address']/label[20]/span[1]"), "Country/Territory", 116577);
			} 
			
			//if the zip code does not match what was entered on previous page then update. This was change as part of the TNT features for the Jan19CL.
			if (WebDriver_Functions.isPresent(By.id("creditCardZipCode")) && !WebDriver_Functions.GetText(By.id("creditCardZipCode")).contentEquals(BillingAddress[5])) {
				WebDriver_Functions.Type(By.id("creditCardZipCode"), BillingAddress[5]);
			}
		}

		if(WebDriver_Functions.isPresent(By.name("questionCd9"))){ //This is just filler currently. Selecting just to cover the shipping needs section, present on US page
			WebDriver_Functions.Select(By.name("questionCd9"), "1", "i");
			WebDriver_Functions.Select(By.name("questionCd10"), "1", "i");
			WebDriver_Functions.Select(By.name("questionCd11"), "1", "i");
		}

		if (BusinessRegistration) {
			WebDriver_Functions.Click(By.xpath("//*[@id='accountTypeBus']"));
			WebDriver_Functions.Type(By.name("company"), "Company Name Here");
		}

		if (WebDriver_Functions.isPresent(By.name("indTaxID"))) {
			WebDriver_Functions.Type(By.name("indTaxID"), TaxInfo[1]); 
		}else if (WebDriver_Functions.isPresent(By.name("taxID"))) {    //using name since duplicate id on page
			WebDriver_Functions.Type(By.name("taxID"), TaxInfo[1]); 
		}else if (WebDriver_Functions.isPresent(By.id("companyRegNoId"))){
			if (TaxInfo != null) {
				WebDriver_Functions.Type(By.id("companyRegNoId"), TaxInfo[1]); //added on 01/24/18 for apac countries
			}else {
				WebDriver_Functions.Type(By.id("companyRegNoId"), "123456"); //added on 01/24/18 for apac countries
				System.err.println("Need to add a companyRegNoId for this country");
			}
		}
		
		if (WebDriver_Functions.isPresent(By.name("indStateTaxID"))) {
			WebDriver_Functions.Type(By.name("indStateTaxID"), TaxInfo[2]);
		}else if (WebDriver_Functions.isPresent(By.name("vatNo"))) {
			WebDriver_Functions.Type(By.name("vatNo"), TaxInfo[2]);
			WebDriver_Functions.Type(By.name("company"), "Vat " + TaxInfo[2]);
		}else if (WebDriver_Functions.isPresent(By.name("statetaxId"))) {
			WebDriver_Functions.Type(By.name("statetaxId"), TaxInfo[2]);
			WebDriver_Functions.Type(By.name("company"), "Vat " + TaxInfo[2]);
		}
		
		if (CreditCardDetils[0].contains("MasterCard") && !WebDriver_Functions.GetText(By.cssSelector("#CCType")).contains(CreditCardDetils[0])) {
			//For some of the countries the C will be lower case
			WebDriver_Functions.Select(By.id("CCType"), "Mastercard", "t");
		}else {
			WebDriver_Functions.Select(By.id("CCType"), CreditCardDetils[0], "t");
		}

		WebDriver_Functions.takeSnapShot("CreditCard Entry.png");
		WebDriver_Functions.Click(By.id("Complete"));

		try {
			WebDriver_Functions.WaitForTextPresentIn(By.tagName("body"), "We are processing your request.");
			WebDriver_Functions.WaitForTextNotPresentIn(By.tagName("body"), "We are processing your request.");
		}catch (Exception e) {}

		//If the user is still on the CC entry page will try and enter a different credit card type.
		if (TaxInfo != null && !TaxInfo[3].contains("Valid")) {
			boolean VatCheck = false;
			if (WebDriver_Functions.isPresent(By.xpath("//*[@id='vatNo']/label[1]/b"))) {
				VatCheck = WebDriver_Functions.ElementMatches(By.xpath("//*[@id='vatNo']/label[1]/b"), TaxInfo[3], Integer.parseInt(TaxInfo[5]));
			}else if (WebDriver_Functions.isPresent(By.xpath("//*[@id='taxInformation']/label/b"))) {   
				VatCheck = WebDriver_Functions.ElementMatches(By.xpath("//*[@id='taxInformation']/label/b"), TaxInfo[3], Integer.parseInt(TaxInfo[5]));
			}
			if (!VatCheck){
				throw new Exception("Vat error is not present on page"); 
			}else {
				WebDriver_Functions.takeSnapShot("Vat Error Page.png");
			}
			
			//*[@id="vatNo"]/label[1]/b
		}else if (WebDriver_Functions.isPresent(By.id("monthExpiry")) && MultiCard) {
			Helper_Functions.PrintOut("Error on Credit Card entry screen. Attempting to register with differnet credit card", true);
			
			//will enter a new name to make easier to trace in the logs.
			if (WebDriver_Functions.isPresent(By.name("editccinfo"))) {
				WebDriver_Functions.Click(By.name("editccinfo"));//edit the billing address
			}
			if (WebDriver_Functions.isPresent(By.id("creditCardFirstName"))) {
				WebDriver_Functions.Type(By.id("creditCardFirstName"), Helper_Functions.getRandomString(10));
			}
			
			String NewCreditCard[] = Helper_Functions.LoadCreditCard(CreditCardDetils[1]);
			return WFCL_CC_Page(NewCreditCard, ShippingAddress, BillingAddress, BusinessRegistration, MultiCard, TaxInfo);
		}
		//longwait.until(ExpectedConditions.presenceOfElementLocated(By.xpath("//*[@id='rightColumn']/table/tbody/tr/td[1]/div/div[1]/div[2]/table/tbody/tr[1]/td[2]")));//check that confirmation page loads
		return CreditCardDetils; //return the credit card used
	}//end WFCL_CC_Page

	private static void InvoiceOrCCValidaiton() throws Exception{
		InvoiceOrCCValidaiton("4460", "750000000", "750000001");
	}

	private static void InvoiceOrCCValidaiton(String CCNumber, String InvoiceA, String InvoiceB) throws Exception{
		if (WebDriver_Functions.isVisable(By.name("invoiceNumberA"))){
			WebDriver_Functions.Type(By.name("invoiceNumberA"), InvoiceA);
			WebDriver_Functions.Type(By.name("invoiceNumberB"), InvoiceB);
			WebDriver_Functions.Click(By.className("buttonpurple"));
			WebDriver_Functions.WaitNotPresent(By.name("invoiceNumberB"));
		}else if (WebDriver_Functions.isVisable(By.name("creditCardNumber"))) {
			WebDriver_Functions.Type(By.name("creditCardNumber"), CCNumber);//this is just a guess as the number most commonly used. 
			WebDriver_Functions.Click(By.className("buttonpurple"));
			WebDriver_Functions.WaitNotPresent(By.name("creditCardNumber"));
		}
		//Helper_Functions.PrintOut("breakpoint", false);
	}

    public static String WFCL_Secret_Answer(String CountryCode, String strUserName, String NewPassword, String SecretAnswer, boolean ErrorExpected) throws Exception{
 		WebDriver_Functions.ChangeURL("INET", CountryCode, true);
 		 
    	try{
    		//click the forgot password link
    		WebDriver_Functions.Click(By.name("forgotUidPwd"));
    		WebDriver_Functions.Type(By.name("userID"), strUserName);
    		
    		WebDriver_Functions.takeSnapShot("Password Reset.png");
            //click the option 1 button and try to answer with secret question
    		WebDriver_Functions.Click(By.id("ada_forgotpwdcontinue"));
                                
    		//click the continue button
    		WebDriver_Functions.Click(By.xpath("//*[@id='module.resetpasswordoptions._expanded']/table/tbody/tr/td[1]/form/table/tbody/tr[5]/td/input"));
    		
            WebDriver_Functions.Type(By.name("answer"), SecretAnswer);
            WebDriver_Functions.takeSnapShot("Reset Password Secret.png");
            WebDriver_Functions.Click(By.name("action1"));
            
			WebDriver_Functions.Type(By.name("password"),NewPassword);
			WebDriver_Functions.Type(By.name("retypePassword"),NewPassword);
			WebDriver_Functions.takeSnapShot("New Password.png");
			WebDriver_Functions.Click(By.name("confirm"));
			
			if (ErrorExpected) {
				WebDriver_Functions.WaitForText(By.xpath("//*[@id='content']/div/form/table/tbody/tr[6]/td/table/tbody/tr[1]/td/b"), "New password cannot be the same as the last password.");
				WebDriver_Functions.takeSnapShot("Same Password " + NewPassword + ".png");
				NewPassword = NewPassword + "5";
				WebDriver_Functions.Type(By.name("password"),NewPassword);
				WebDriver_Functions.Type(By.name("retypePassword"),NewPassword);
				WebDriver_Functions.takeSnapShot("New Password " + NewPassword + ".png");
				WebDriver_Functions.Click(By.name("confirm"));
			}
			
			WebDriver_Functions.WaitForText(By.xpath("//*[@id='content']/div/table/tbody/tr[1]/td/table/tbody/tr/td/table/tbody/tr[1]/td/h1"), "Thank you.");
			boolean loginAttempt = WebDriver_Functions.Login(strUserName, NewPassword);
			Helper_Functions.PrintOut(strUserName + " has had the password changed to " + NewPassword, true);
			if (NewPassword.contentEquals(Helper_Functions.myPassword) && loginAttempt && Thread.currentThread().getStackTrace()[2].getMethodName().contentEquals("WFCL_Secret_Answer")){
		    	return strUserName + " " + NewPassword;
			}else if (loginAttempt){
				return WFCL_Secret_Answer(CountryCode, strUserName, Helper_Functions.myPassword, SecretAnswer, false);//change the password back
			}else{
				throw new Exception("Error.");
			}
		}catch (Exception e){
			throw e;
		}
	}//end ResetPasswordWFCLSecret
	
    public static String ResetPasswordWFCL_Email(String CountryCode, String strUserName, String Password) throws Exception{
    	try{
    		WebDriver_Functions.Login(strUserName, Password);

    		String Email = "--Could not retrieve email--";
    		
    		try {
    			WebDriver_Functions.ChangeURL("WPRL", CountryCode, false);
    			WebDriver_Functions.WaitPresent(By.id("ci_fullname_val"));
    			String UserDetails = WebDriver_Functions.GetText(By.id("ci_fullname_val"));
    			Email = UserDetails.substring(UserDetails.lastIndexOf('\n') + 1, UserDetails.length());
    			WebDriver_Functions.takeSnapShot("Password Reset Email UserDetails.png");
    		}catch (Exception e) {
    			Helper_Functions.PrintOut("Error " + e.getMessage() + "   " + Email, true);
    		}
    		
    		//trigger the password reset email
    		WebDriver_Functions.ChangeURL("INET", CountryCode, true);
    		WebDriver_Functions.Click(By.name("forgotUidPwd"));
    		WebDriver_Functions.Type(By.name("userID"),strUserName);
    		
    		WebDriver_Functions.Click(By.id("ada_forgotpwdcontinue"));
    		//*[@id="ada_forgotpwdcontinue"]
    		//click the option 1 button and try to answer with secret question
    		WebDriver_Functions.Click(By.name("action2"));
    		WebDriver_Functions.takeSnapShot("Password Reset Email.png");
    		Helper_Functions.PrintOut("Completed ResetPasswordWFCL using " + strUserName + ". An email has been triggered and that test must be completed manually by " + Email, true);
    					
    		return Email;
    	}catch(Exception e){
    		Helper_Functions.PrintOut("General failure in ResetPasswordWFCL_Email", true);
    		throw e;
    	}
	}//end ResetPasswordWFCL_Email
    
    public static String[] TaxIDinformation(String CountryCode, boolean BusinessAccount){
    	String TaxID = "", StateTaxID = "";
    	switch (CountryCode) {
			case "GB":		//worked for account 643527529 from 8/30/18
				TaxID = "";
				StateTaxID = "GB2332322322312";
				break;
			case "BR":		//check that address matches
				TaxID = "999.999.999-99";//worked for personal account 615002461 back in 2017
				StateTaxID = "0962675512";
				break;
    	}
    	return new String[] {TaxID, StateTaxID};
    }
    
    public static String[] WFCL_WADM_Invitaiton(String UserID, String Password, String Name[], String Email) throws Exception{	
		try {
			WebDriver_Functions.Login(UserID, Password);
			WebDriver_Functions.ChangeURL("WADM", "US", false);
			
			WebDriver_Functions.takeSnapShot("UserTable before invite.png");
			
			WebDriver_Functions.Click(By.id("createNewUsers"));
			WebDriver_Functions.WaitNotPresent(By.id("loading-div"));//wait for the loading overlay to not be present
			WebDriver_Functions.Type(By.name("userfirstName"), Name[0]);
			WebDriver_Functions.Type(By.name("middleName"), Name[1]);
			WebDriver_Functions.Type(By.name("userlastName"), Name[2]);
			
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
			WebDriver_Functions.WaitForText(By.xpath("//*[@id=\"tableBody\"]/tr/td[1]"), Name[0]); //check first name
			WebDriver_Functions.WaitForText(By.xpath("//*[@id=\"tableBody\"]/tr/td[2]"), Name[2]); //check last name
			WebDriver_Functions.takeSnapShot("Invitation Sent.png");
			
			return new String[] {Email, UniqueID};
		}catch (Exception e) {
			e.printStackTrace();
			throw e;
		}
    }
    
	public static String WFCL_AdminReg_WithMismatch(String Name[], String UserId, String Email,String AccountNumber, String AddressDetails[], String AddressMismatch[], String AccountNickname) throws Exception{
		boolean InetFlag = false;
		Helper_Functions.PrintOut("Attempting to register with " + AccountNumber, true);
		String CountryCode = AddressDetails[6].toUpperCase();

		if (CountryCode.contentEquals("US") || CountryCode.contentEquals("CA")){
			WebDriver_Functions.ChangeURL("FCLLink", CountryCode, true);
			WebDriver_Functions.Click(By.xpath("//input[(@name='accountType') and (@value = 'linkAccount')]"));//select the link account radio button
			WebDriver_Functions.WaitPresent(By.cssSelector("#reminderQuestion option[value=SP2Q1]"));//wait for secret questions to load.
			WFCL_ContactInfo_Page(Name, AddressMismatch, UserId, Email, true); //enters all of the details
		}else{
			WebDriver_Functions.ChangeURL("FCLLinkInter", CountryCode, true);
			WebDriver_Functions.WaitPresent(By.cssSelector("#reminderQuestion"));
			WFCL_ContactInfo_Page(Name, AddressMismatch, UserId, Email, true); //enters all of the details
		}

		//Step 2 Account information
		//String AccountNickname = AccountNumber + "_" + CountryCode;
		WFCL_AccountEntryScreen(AccountNumber, AccountNickname);
		
		if (CountryCode.contains("US")){
			WebDriver_Functions.WaitForText(By.xpath("//*[@id='rightColumn']/table/tbody/tr/td[1]/div/div[1]/div[2]/table/tbody/tr[1]/td[2]"), UserId);
			//WebDriver_Functions.WaitForText(By.xpath("//*[@id='rightColumn']/table/tbody/tr/td[1]/div/div[1]/div[2]/table/tbody/tr[2]/td[2]"), AccountNumber);    //will need to update due to account masking
		}
		WebDriver_Functions.takeSnapShot("RegistrationConfirmation.png");
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
			AddressMismatchPage(AddressDetails);
			
			//Confirmation from INET registration
			try{//EACI form
				WebDriver_Functions.WaitForText(By.xpath("//*[@id='content']/div/table/tbody/tr[1]/td[2]/table[2]/tbody/tr[3]/td/table/tbody/tr[1]/td[2]/table/tbody/tr[2]/td/b"), UserId);
				//WebDriver_Functions.WaitForText(By.xpath("//*[@id='content']/div/table/tbody/tr[1]/td[2]/table[2]/tbody/tr[3]/td/table/tbody/tr[2]/td[2]/table/tbody/tr[2]/td/b"), AccountNumber);
				WebDriver_Functions.WaitForText(By.xpath("//*[@id='content']/div/table/tbody/tr[1]/td[2]/table[2]/tbody/tr[3]/td/table/tbody/tr[3]/td[2]/table/tbody/tr[2]/td/b"), AccountNickname);
				//WebDriver_Functions.WaitForText(By.xpath("//*[@id='content']/div/table/tbody/tr[1]/td[2]/table[2]/tbody/tr[3]/td/table/tbody/tr[3]/td[4]/table/tbody/tr[2]/td/table/tbody/tr/td[2]/b"), AddressDetails[0] + "\n" + AddressDetails[2] + ", " + AddressDetails[4] + " " + AddressDetails[5] + "\n" + AddressDetails[6].toLowerCase());
				InetFlag = true;
			}catch (Exception e){
				Helper_Functions.PrintOut("Not able to Verify data on INET registration page.", true);
			}
			
			WebDriver_Functions.takeSnapShot("INET Confirmation.png");
			WebDriver_Functions.Click(By.xpath("//*[@id='content']/div/table/tbody/tr[1]/td[2]/table[2]/tbody/tr[3]/td/table/tbody/tr[1]/td[4]/table/tbody/tr/td/table/tbody/tr[1]/td[2]/a/img"));
			WebDriver_Functions.WaitPresent(By.xpath("//*[@id='appTitle']"));
		}

		WebDriver_Functions.ChangeURL("ADMINREG", CountryCode, false);
		try{
			if (WebDriver_Functions.isPresent(By.name("accountNumber"))){ 
				WebDriver_Functions.Select(By.name("accountNumber"), "1", "i");
				WebDriver_Functions.Click(By.name("submit"));	
			}

			//address mismatch page should appear, enter the correct details.
			AddressMismatchPage(AddressDetails);
			
			if (WebDriver_Functions.isPresent(By.name("invoiceNumberA")) || WebDriver_Functions.isPresent(By.name("creditCardNumber"))) {
				InvoiceOrCCValidaiton();
			}	
		}catch (Exception e2){
			Helper_Functions.PrintOut("Failure with admin registriaton.", true);
		};
		
		String UUID = WebDriver_Functions.GetCookieValue("fcl_uuid");
		Helper_Functions.PrintOut("Finished WFCL_AccountRegistration  " + UserId + "/" + Helper_Functions.myPassword + "--" + AccountNumber + "--" + UUID, true);
		String ReturnValue[] = new String[] {UserId, AccountNumber, UUID, "INET:" + InetFlag};
		Helper_Functions.WriteUserToExcel(UserId, Helper_Functions.myPassword);
		return Arrays.toString(ReturnValue);
	}//end WFCL_AccountRegistration
    
	public static boolean WFCL_AccountEntryScreen(String AccountNumber, String AccountNickname) throws Exception{
		if (WebDriver_Functions.isPresent(By.id("accountNumber"))){
			WebDriver_Functions.Type(By.id("accountNumber"), AccountNumber);
			WebDriver_Functions.Type(By.id("nickName"), AccountNickname);
			WebDriver_Functions.takeSnapShot("AccountInformation.png");
			WebDriver_Functions.Click(By.id("createUserID"));
			WebDriver_Functions.WaitNotPresent(By.id("nickName"));
		}else if (WebDriver_Functions.isPresent(By.name("newAccountNumber"))){
			WebDriver_Functions.Type(By.name("newAccountNumber"), AccountNumber);
			WebDriver_Functions.Type(By.name("newNickName"), AccountNickname);
			WebDriver_Functions.takeSnapShot("AccountInformation.png");
			WebDriver_Functions.Click(By.name("submit"));
			WebDriver_Functions.WaitNotPresent(By.name("newNickName"));
		}

		//need to add a dynamic wait here for the processing
		Helper_Functions.Wait(5);
				
		if (WebDriver_Functions.isPresent(By.name("invoiceNumberA")) || WebDriver_Functions.isPresent(By.name("creditCardNumber"))) {
			InvoiceOrCCValidaiton();
		}

		if (WebDriver_Functions.isPresent(By.id("accountNumber"))|| WebDriver_Functions.isPresent(By.name("newAccountNumber"))) {
			WebDriver_Functions.Click(By.name("submit"));
			Helper_Functions.PrintOut("Warning, still on account entry screen. The address entered may be incorrect.", true);
			//return WFCL_AccountEntryScreen(AccountNumber, AccountNickname, Path);//this is an issue, need to fix later as link is an issue for AU 
		}else if (WebDriver_Functions.CheckBodyText("Request Access from the Account Administrator")) {
			//remove the account number from local storage as it is now locked to the company that was just created.
			Helper_Functions.RemoveAccountFromAccount_Numbers(Environment.getInstance().getLevel(), AccountNumber);
			Helper_Functions.PrintOut("Request Access from the Account Administrator", true);
			throw new Exception("Request Access from the Account Administrator");
		}
		

		WebDriver_Functions.WaitNotPresent(By.id("createUserID"));
		return true;
	}//end WFCL_AccountEntryScreen
	
	public static void AddressMismatchPage(String AddressDetails[]) throws Exception {
		WebDriver_Functions.WaitPresent(By.name("address1"));
		WebDriver_Functions.Type(By.name("address1"), AddressDetails[0]);
		WebDriver_Functions.Type(By.name("address2"), AddressDetails[1]);
		WebDriver_Functions.Type(By.name("city"), AddressDetails[2]);
		try {
			WebDriver_Functions.Type(By.name("state"), AddressDetails[4]);
			WebDriver_Functions.Type(By.name("zip"), AddressDetails[5]);
			WebDriver_Functions.Select(By.name("country"), AddressDetails[6].toLowerCase(), "v");
		}catch (Exception e) {}
		
		WebDriver_Functions.takeSnapShot("Address Mismatch.png");
		WebDriver_Functions.Click(By.name("submit"));
	}
	
	public static String[] Zip_TNT_Validation(String EnrollmentID, String CreditCardDetils[], String AddressDetails[], String BillingAddressDetails[], String Name[], String UserId,  String Email, boolean BusinessAccount, String TaxInfo[]) throws Exception{
		try {
			String CountryCode = AddressDetails[6];
			//go to the INET page just to load the cookies
			WebDriver_Functions.ChangeURL("INET", CountryCode, true);
			WebDriver_Functions.ChangeURL("Enrollment_" + EnrollmentID, CountryCode, false);

			//add the country code to the screenshot name
			DriverFactory.setScreenshotPath(DriverFactory.getScreenshotPath() + CountryCode + " ");
			
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
			WFCL_ContactInfo_Page(Name, AddressDetails, UserId, Email, true); //enters all of the details

			//Step 2: Enter the credit card details
			WebDriver_Functions.WaitPresent(By.id("CCType"));
			boolean Multicard = true;
			if(TaxInfo != null && !TaxInfo[3].contains("Valid")) {
				Multicard = false;
				CreditCardDetils = WFCL_CC_Page(CreditCardDetils, AddressDetails, BillingAddressDetails, BusinessAccount, Multicard, TaxInfo);
				/////////////////////////////user valid zip code here
				return new String[] {"Invalid attempt"};
			}
			CreditCardDetils = WFCL_CC_Page(CreditCardDetils, AddressDetails, BillingAddressDetails, BusinessAccount, Multicard, TaxInfo);
			
			//Step 3: Confirmation Page
			//Domestic and international will be different  
			WebDriver_Functions.WaitOr_TextToBe(By.xpath("//*[@id='rightColumn']/table/tbody/tr/td[1]/div/div[1]/div[2]/table/tbody/tr[1]/td[2]"), UserId, By.xpath("//*[@id='rightColumn']/table/tbody/tr/td[1]/div/div/div[2]/table/tbody/tr[1]/td[2]"),  UserId);
			String AccountNumber = WebDriver_Functions.GetText(By.xpath("//*[@id='acctNbr']"));
			WebDriver_Functions.takeSnapShot("Confirmation.png");

			boolean InetFlag = false, AdminFlag = false;

			//Check the INET page
			InetFlag = Check_INET_Enrolled(CountryCode);

			//Check the Administration page
			AdminFlag = Check_WADM_Enrolled(CountryCode);
			
			String UUID = WebDriver_Functions.GetCookieValue("fcl_uuid");
			Helper_Functions.PrintOut("Finished CreditCardRegistrationEnroll  " + UserId + "/" + Helper_Functions.myPassword + "--" + AccountNumber + "--" + UUID, true);

			String LastFourOfCC = CreditCardDetils[1].substring(CreditCardDetils[1].length() - 4, CreditCardDetils[1].length());
			String ReturnValue[] = new String[] {UserId, AccountNumber, UUID, LastFourOfCC, "INET:" + InetFlag, "Admin:" + AdminFlag};
			
			Helper_Functions.WriteUserToExcel(UserId, Helper_Functions.myPassword);
			return ReturnValue;
		}catch (Exception e) {
			e.printStackTrace();
			throw e;
		}
	}//end CreditCardRegistrationEnroll
    
	public static boolean WFCL_AccountRegistration_GFBO(String Name[], String UserId, String Email, String AccountNumber, String AddressDetails[]) throws Exception{
		String CountryCode = AddressDetails[6].toUpperCase();
		
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
	 		WFCL_ContactInfo_Page(Name, AddressDetails, UserId, Email,true);
		    
	 		//Step 2 Account information
	 		String AccountNickname = AccountNumber + "_" + AddressDetails[6];
	 		WFCL_AccountEntryScreen(AccountNumber, AccountNickname);
	 		
		    //Confirmation page for FBO
	 		//wait.until(ExpectedConditions.presenceOfElementLocated(By.id("selectbillingmedium1:j_idt24")));//the header for FBO
	 		if (WebDriver_Functions.CheckBodyText("FedEx Billing Online Registration")) {
	 			throw new Exception("Error on Confirmaiton page");
	 		}
	 		WebDriver_Functions.takeSnapShot("Confirmation.png");
	 		WebDriver_Functions.Click(By.id("selectbillingmedium1:continueId"));  
	 		
	 		//Registration Confirmation Page
	 		WebDriver_Functions.WaitForText(By.xpath("//*[@id='content']/div/table/tbody/tr[1]/td[2]/table[2]/tbody/tr[3]/td/table/tbody/tr[1]/td[2]/table/tbody/tr[2]/td/b"), UserId);
	 		WebDriver_Functions.WaitForText(By.xpath("//*[@id='content']/div/table/tbody/tr[1]/td[2]/table[2]/tbody/tr[3]/td/table/tbody/tr[2]/td[2]/table/tbody/tr[2]/td/b"), AccountNumber);
	 		WebDriver_Functions.WaitForText(By.xpath("//*[@id='content']/div/table/tbody/tr[1]/td[2]/table[2]/tbody/tr[3]/td/table/tbody/tr[3]/td[2]/table/tbody/tr[2]/td/b"), AccountNickname);
	 		WebDriver_Functions.takeSnapShot("RegistrationConfirmation.png");

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
			
			String UUID = WebDriver_Functions.GetCookieValue("fcl_uuid");
		    Helper_Functions.PrintOut("Finished WFCL_AccountRegistration_GFBO  " + UserId + "/" + Helper_Functions.myPassword + "--" + AccountNumber + "--" + UUID, false);
		    Helper_Functions.WriteUserToExcel(UserId, Helper_Functions.myPassword);
		    return true;
 		}catch (Exception e) {
 			throw e;
 		}
 	}//end WFCL_AccountRegistration_GFBO
    
	public static boolean WFCL_InfoSec_ChangePassword(String UserId, String Password, String NewPassword, String Email, boolean ErrorExpected) throws Exception {
		try {
			WebDriver_Functions.Login(UserId, Password, "WFCL");
			
			WebDriver_Functions.Type(By.name("password"), NewPassword);
			WebDriver_Functions.Type(By.name("retypePassword"), NewPassword);
			
			WebDriver_Functions.Type(By.name("email"), Email);
			WebDriver_Functions.Type(By.name("retypeEmail"), Email);
			
			
		} catch (Exception e) {
			throw e;
		}
		
		
		
		return true;
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
 			WebDriver_Functions.Type(By.name("password"), Helper_Functions.myPassword);
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
	 		
		    Helper_Functions.PrintOut("Finished WFCL_AccountRegistration_GFBO  " + UserId + "/" + Helper_Functions.myPassword + "--" + AccountNumber);
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
	
	public static String[] WFCL_RewardsRegistration(String Name[], String UserId, String AccountNumber, String AddressDetails[]) throws Exception{
		boolean RewardsFlag = false;
 		String CountryCode = AddressDetails[6].toUpperCase();
 		TestData.LoadTime();
 		
 		try {
 			String WFCLPath = strTodaysDate + " " + strLevel + " " + CountryCode + " WFCL ";
 			WebDriver_Functions.ChangeURL(("https://" + strLevelURL + "/fcl/ALL?enrollmentid=cc16323314&fedId=Epsilon&accountOption=link"), true);
 		 	WebDriver_Functions.Click(By.name("signupnow"));
	 		wait.until(ExpectedConditions.presenceOfElementLocated(By.cssSelector("#reminderQuestion option[value=SP2Q1]")));//wait for secret questions to load.
		 	WFCL_ContactInfo_Page(Name, AddressDetails, UserId); //enters all of the details
		 	captureScreenShot("WFCL", WFCLPath + "ContactInformation.png");
		 	WebDriver_Functions.Click(By.id("createUserID"));
 			
	 		//Step 2 Account information
	 		String AccountNickname = AccountNumber + "_" + CountryCode;
	 		WFCL_AccountEntryScreen(AccountNumber, AccountNickname, WFCLPath);
	 		
	 		wait.until(ExpectedConditions.textToBe(By.xpath("//*[@id='rightColumn']/table/tbody/tr/td[1]/div/div[1]/div[2]/table/tbody/tr[1]/td[2]"), UserId));
	 		wait.until(ExpectedConditions.textToBe(By.xpath("//*[@id='rightColumn']/table/tbody/tr/td[1]/div/div[1]/div[2]/table/tbody/tr[2]/td[2]"), AccountNumber));
		    captureScreenShot("WFCL", WFCLPath + "RegistrationConfirmation.png");
		    String UUID = WebDriver_Functions.GetCookieValue("fcl_uuid");
		    //Continue to My FedEx Rewards Link on the confirmation page
		    if (Environment >= 6) {//rewards only has a connection on L6/LP
		    	 WebDriver_Functions.Click(By.xpath("//*[@id='shipnow']/font"));
		    	 //need to add step to confirm rewards page
		    	 captureScreenShot("WFCL", WFCLPath + "Rewards Page.png");
		    	 RewardsFlag = true;
		    }else {
		    	Helper_Functions.PrintOut("Warning, Rewards only has connection on L6/Lp unable to validte rewards page.");
		    }
		    
		    Helper_Functions.PrintOut("Finished WFCL_AccountRegistration  " + UserId + "/" + Helper_Functions.myPassword + "--" + AccountNumber + "--" + UUID);
		    String ReturnValue[] = new String[] {UserId, AccountNumber, UUID, "Rewards:" + RewardsFlag};
		    WriteUserToFileAppend(ReturnValue);//Write User to file for later reference
		    return ReturnValue;
 		}catch (Exception e) {
 			GeneralFailure(e);
 			throw e;
 		}
 	}//end WFCL_RewardsRegistration
 

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