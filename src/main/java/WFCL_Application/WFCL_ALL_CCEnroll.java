package WFCL_Application;

import org.testng.annotations.Test;
import Data_Structures.Enrollment_Data;
import Data_Structures.User_Data;
import org.testng.annotations.BeforeClass;
import org.openqa.selenium.By;
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

public class WFCL_ALL_CCEnroll{
	static String LevelsToTest = "2";  
	static String CountryList[][]; 
	static List<String> Users = new ArrayList<String>();

	@BeforeClass
	public void beforeClass() {
		Environment.SetLevelsToTest(LevelsToTest);
		CountryList = Environment.getCountryList("full");
		Helper_Functions.MyEmail = "accept@fedex.com";
	}
	
	@DataProvider (parallel = true)
	public static Iterator<Object[]> dp(Method m) {
		List<Object[]> data = new ArrayList<Object[]>();

		for (int i=0; i < Environment.LevelsToTest.length(); i++) {
			String Level = String.valueOf(Environment.LevelsToTest.charAt(i));
			int intLevel = Integer.parseInt(Level);
			switch (m.getName()) { //Based on the method that is being called the array list will be populated.
		    	case "CreditCardRegistrationEnroll_All":
		    		for (int j = 0; j < CountryList.length; j++) {
		    			String EnrollmentID[][] = Helper_Functions.LoadAllEnrollmentIDs(CountryList[j][0]);
		    			for(String Enrollment[]: EnrollmentID) {
		    				if (Enrollment[1].contains(CountryList[j][0])) {
		    					String UserId = Helper_Functions.LoadUserID("L" + Level + Enrollment[0] + "CC");
		    					data.add( new Object[] {Level, UserId, Enrollment});
		    					break;
		    				}
		    			}
					}
		    		break;
		    	case "DiscountValidate":
		    		User_Data User_Info[] = User_Data.Get_UserIds(intLevel);
		    		for (int j = 0; j < CountryList.length; j++) {
		    			for (int k = 1; k < User_Info.length; k++) {
		    				//user must have an account number and be checking below to see if pass key user.
		    				if (!User_Info[k].ACCOUNT_NUMBER.contentEquals("") && User_Info[k].PASSKEY.contentEquals("T") && User_Info[k].Address_Info.Country_Code.contentEquals(CountryList[j][0]) && User_Info[k].USER_ID.contains("CC")){
		    					data.add( new Object[] {Level, CountryList[j][0], User_Info[k].USER_ID, User_Info[k].PASSWORD});
		    					break;
		    				}
		    			}
					}
		    		break;
			}
		}	
		return data.iterator();
	}

	@Test(dataProvider = "dp", enabled = true)
	public void CreditCardRegistrationEnroll_All(String Level, String UserId, String EnrollmentID[]) {
		try {
			Helper_Functions.PrintOut("Erollment with " + EnrollmentID[0], false);
			String CreditCard[] = Helper_Functions.LoadCreditCard("M");
			String CountryCode = EnrollmentID[1];
			String ShippingAddress[] = Helper_Functions.LoadAddress(CountryCode), BillingAddress[] = ShippingAddress;
			String ContactName[] = Helper_Functions.LoadDummyName(CountryCode + "CC", Level);
			String TaxInfo[] = Helper_Functions.getTaxInfo(CountryCode).get(0);
			String Result[] = WFCL_Functions.CreditCardRegistrationEnroll(EnrollmentID, CreditCard, ShippingAddress, BillingAddress, ContactName, UserId, Helper_Functions.MyEmail, false, TaxInfo);
			
			Helper_Functions.PrintOut(Arrays.toString(Result), false);

			String Data[][] = new String[][] {{"Enrollment_Code", EnrollmentID[0]}, {"L" + Level, "T"}, {"PROGRAM_NAME", EnrollmentID[2].replace("L" + Level, "")}};
			Helper_Functions.WriteToExcel(Helper_Functions.DataDirectory + "\\EnrollmentIds.xls", "EnrollmentIds", Data, 0);
		}catch (Exception e) {
			Assert.fail(e.getMessage());
		}
	}
	
	@Test(dataProvider = "dp", enabled = false)
	public void DiscountValidate(String Level, String CountryCode, String UserId, String Password) {
		
		String DT_Status[][] = new String[][] {{"DiscountAppearingCorrectly", ""}, 
			{"DiscountNotMatchingOnLandingPage", ""}, //1
			{"SorryMessage", ""}, 
			{"WFCLMarketing", ""}, //3
			{"EnrollmentIDRequired", ""}, 
			{"WFCLMarketingIncorrectDiscount", ""},//5 
			{"IncorrectDiscountOnConfilctPage", ""}, 
			{"IncorrectDiscountOnConfirmationPage", ""},  //7
			{"UnableToCompleteEndToEnd", ""}, 
			{"CompletedEndToEnd", ""}, //9
			{"Incorrect Link on Apply now button:", ""}};
			
		try {
			WebDriver_Functions.Login(UserId, Password);
			
			/*
			String Login_Cookie = WebDriver_Functions.GetCookieValue("fdx_login");
			USRC_Data User_Info = USRC_Data.LoadVariables(Level);
			String Credit_Card = USRC_API_Endpoints.AccountRetrieval_Then_EnterpriseCustomer(User_Info.GenericUSRCURL, "fdx_login=" + Login_Cookie);
			*/
			
			Enrollment_Data ED[] = Environment.getEnrollmentDetails(Integer.parseInt(Level));

			for (Enrollment_Data EN: ED) {
				try {
					if (EN != null && !EN.AEM_LINK.contentEquals("") && (!EN.PASSCODE.contentEquals("") || !EN.MEMBERSHIP_ID.contentEquals(""))) { //
						WebDriver_Functions.ChangeURL("DT_" + EN.ENROLLMENT_ID, EN.COUNTRY_CODE, false);
						WebDriver_Functions.takeSnapShot(EN.ENROLLMENT_ID + " DT Value.png");
						String DTValue = WebDriver_Functions.GetBodyText();

						String ApplyNowUrlOne = "";
						if (!EN.AEM_LINK.contentEquals("")) {
							WebDriver_Functions.ChangeURL(EN.AEM_LINK, EN.COUNTRY_CODE, false);
							ApplyNowUrlOne = WebDriver_Functions.getURLByLinkText("APPLY NOW");
							//String ApplyNowUrlTwo = WebDriver_Functions.getURLByLinkText("Apply Now");//orange button
						}
			
						//when membership/pass codes are not needed then check the link of the apply now button.
						if (EN.MEMBERSHIP_ID.contentEquals("") && EN.PASSCODE.contentEquals("")) {
							String ExpectedUrl = WebDriver_Functions.ChangeURL("Enrollment_" + EN.ENROLLMENT_ID, EN.COUNTRY_CODE, false);
							if (!ApplyNowUrlOne.contentEquals("") && !ExpectedUrl.contentEquals(ApplyNowUrlOne)) {
								DT_Status[10][1] += EN.ENROLLMENT_ID + ", ";
							}
						}else {
							if (WebDriver_Functions.isPresent(By.id("field_Passcode"))) {
								WebDriver_Functions.Type(By.id("field_Passcode"), EN.PASSCODE);
							}
							if (WebDriver_Functions.isPresent(By.id("field_Membership ID"))) {
								WebDriver_Functions.Type(By.id("field_Membership ID"), EN.MEMBERSHIP_ID);
							}
							WebDriver_Functions.Click(By.cssSelector("div#overview button[type=\"submit\"]"));
							String RedirectURL = WebDriver_Functions.CloseNewTabAndNavigateInCurrent();
						    String ExpectedURL = WebDriver_Functions.ChangeURL_EnrollmentID(EN, false, false);
						    if (!RedirectURL.contentEquals(ExpectedURL)) {
						    	DT_Status[10][1] += EN.ENROLLMENT_ID + ", ";
						    }
						}
						
						if (WebDriver_Functions.isPresent(By.name("Apply Now"))) {
							DT_Status[3][1] += EN.ENROLLMENT_ID + ", ";
							if (WebDriver_Functions.isPresent(By.name("passCode")) || WebDriver_Functions.isPresent(By.name("membershipID"))) {
								DT_Status[4][1] += EN.ENROLLMENT_ID + ", ";
								//if pass code is needed to be entered on discount page.
								if (WebDriver_Functions.isPresent(By.name("passCode"))) {
									WebDriver_Functions.Type(By.name("passCode"), EN.PASSCODE);
								}
								//if membershipID is needed to be entered on discount page.
								if (WebDriver_Functions.isPresent(By.name("membershipID"))) {
									WebDriver_Functions.Type(By.name("membershipID"), EN.MEMBERSHIP_ID);
								}
							}

							WebDriver_Functions.takeSnapShot("Discount Page.png");
							//apply link from marketing page
							WebDriver_Functions.Click(By.name("Apply Now"));
							
							WebDriver_Functions.Click(By.name("Apply Now"));
							if (!WebDriver_Functions.CheckBodyText(DTValue)) {
								DT_Status[5][1] += EN.ENROLLMENT_ID + ", ";
							}
						}
						
						if (WebDriver_Functions.CheckBodyText(DTValue)) {
							DT_Status[0][1] += EN.ENROLLMENT_ID + ", ";
							WebDriver_Functions.takeSnapShot(EN.ENROLLMENT_ID + " Discount Matching.png");
							//Apply_Discount(DT_Status, EN, Credit_Card, DTValue, UserId);
						}else {
							if (WebDriver_Functions.CheckBodyText("Sorry, we cannot find the web page you are looking for.")) {
								DT_Status[2][1] += EN.ENROLLMENT_ID + ", ";
							}else {
								DT_Status[1][1] += EN.ENROLLMENT_ID + ", ";//DiscountNotMatchingOnLandingPage
							}
						}
					}
				}catch (Exception e) {
					Helper_Functions.PrintOut("Error on enrollment: " + EN.ENROLLMENT_ID);
				}
			}
		}catch (Exception e) {
			e.printStackTrace();
			Assert.fail(e.getMessage());
		}
		
		String Results = "";
		for(String[] Line: DT_Status){
			if (!Line[1].contentEquals("")) {
				Results += Arrays.toString(Line)  + "\n   ";
			}
		}
		Helper_Functions.PrintOut(Results, false);
	}
	

	
	@Test (enabled = false)
	public void TesitngRollingBounce(){
		try {
			String Level = "7";
			Environment.getInstance().setLevel(Level);
			boolean flag = false;
			String LevelURL = WebDriver_Functions.LevelUrlReturn(Integer.parseInt(Level));
			String URL = "https://www.fedex.com/fcl/web/jsp/app.jsp";
			URL = URL.replace("https://www.fedex.com", LevelURL);
			for (int i = 1;; i++) {
				DriverFactory.getInstance().getDriver().get(URL);
				if (!WebDriver_Functions.CheckBodyText("Application Access Page")) {
					Helper_Functions.PrintOut("\nPage is not present " + Helper_Functions.CurrentDateTime());
					WebDriver_Functions.takeSnapShot("Page not up.png");
					Helper_Functions.PrintOut(WebDriver_Functions.GetBodyText());
					for (int j = 1; ;j++) {
						DriverFactory.getInstance().getDriver().get(URL);
						if (WebDriver_Functions.CheckBodyText("Application Access Page")) {
							break;
						}else if (j % 30 == 0) {
							System.out.print(Helper_Functions.CurrentDateTime()  + " _ ");
						}
					}
					while(!WebDriver_Functions.CheckBodyText("Application Access Page")) {
						DriverFactory.getInstance().getDriver().get(URL);
					}
					WebDriver_Functions.takeSnapShot("Page Back Online.png");
					Helper_Functions.PrintOut("Page Back Online " + Helper_Functions.CurrentDateTime());
				}
				if (!WebDriver_Functions.CheckBodyText("WFCL3010") && !flag) {
					Helper_Functions.PrintOut("Updated");
					flag = true;
				}
				
				if (i % 30 == 0) {
					System.out.println(Helper_Functions.CurrentDateTime() + " ");
				}else {
					System.out.print(i + " ");
				}
			}
		}catch (Exception e) {
			Assert.fail(e.getMessage());
		}
		Helper_Functions.PrintOut("Done");
	}
	
	private static void InvoiceOrCCValidaiton(String CCNumber) throws Exception{
		if (WebDriver_Functions.isPresent(By.name("invoiceNumberA"))){
			String InvoiceA = "750000000", InvoiceB = "750000001";
			WebDriver_Functions.Type(By.name("invoiceNumberA"), InvoiceA);
			WebDriver_Functions.Type(By.name("invoiceNumberB"), InvoiceB);
			WebDriver_Functions.Click(By.className("buttonpurple"));
			WebDriver_Functions.WaitNotPresent(By.name("invoiceNumberB"));
		}else if (WebDriver_Functions.isPresent(By.name("lastFourDigits"))) {
			WebDriver_Functions.Type(By.name("lastFourDigits"), CCNumber);//this is just a guess as the number most commonly used. 
			WebDriver_Functions.Click(By.name("submit"));
			WebDriver_Functions.WaitNotPresent(By.name("lastFourDigits"));
		}
	}
	
	public String[][] Apply_Discount(String DT_Status[][], Enrollment_Data EN, String Credit_Card, String DTValue, String UserId){
		try {
			//select the apply discount radio button and continue.
			if (WebDriver_Functions.isPresent(By.name("continue"))) {
				WebDriver_Functions.Click(By.xpath("//input[(@name='whichBillingAddress') and (@value = 'useContactAddress')]"));
				WebDriver_Functions.takeSnapShot(EN.ENROLLMENT_ID + " Apply Discount.png");
				WebDriver_Functions.Click(By.name("continue"));
		 		
		 		//after processing screen
		 		try {
		 			WebDriver_Functions.WaitForTextPresentIn(By.tagName("body"), "We are processing your request.");
		 			if (WebDriver_Functions.CheckBodyText("An error has occurred and we are unable to process your request. Please try again.")) {
						throw new Exception("An error has occurred and we are unable to process your request. Please try again.");
					}
		 			WebDriver_Functions.WaitForTextNotPresentIn(By.tagName("body"), "We are processing your request.");
				}catch (Exception e) {}
		 		
		 		if (WebDriver_Functions.CheckBodyText("An error has occurred and we are unable to process your request. Please try again.")) {
					throw new Exception("An error has occurred and we are unable to process your request. Please try again.");
				}
		 		
		 		if (WebDriver_Functions.isPresent(By.name("discountConflictResolution"))){
		 			if (!WebDriver_Functions.CheckBodyText(DTValue)) {
						DT_Status[6][1] += EN.ENROLLMENT_ID + ", ";
		 			}
		 			WebDriver_Functions.Click(By.xpath("//input[(@name='discountConflictResolution') and (@value = 'applyNewDiscount')]"));
		 			
		 			WebDriver_Functions.takeSnapShot(EN.ENROLLMENT_ID + " Discount Conflict.png");
		 			
		 			if (WebDriver_Functions.isPresent(By.name("lastFourDigits"))){
		 				InvoiceOrCCValidaiton(Credit_Card);
		 			}else {
		 				WebDriver_Functions.Click(By.name("submit"));
		 			}
		 			
		 			WebDriver_Functions.WaitForTextNotPresentIn(By.className("mainheader1"), "We are processing your request. Please wait...");
		 		}
		 		
		 		WebDriver_Functions.WaitForText(By.xpath("//*[@id='rightColumn']/table/tbody/tr/td[1]/div/div[1]/div[2]/table/tbody/tr[1]/td[2]"), UserId);
		 		if (!WebDriver_Functions.CheckBodyText(DTValue)) {
					DT_Status[7][1] += EN.ENROLLMENT_ID + ", ";
	 			}
		 		WebDriver_Functions.takeSnapShot(EN.ENROLLMENT_ID + " Confirmation.png");
			}else {
				DT_Status[8][1] += EN.ENROLLMENT_ID + ", ";
			}
		}catch(Exception e) {
			DT_Status[8][1] += EN.ENROLLMENT_ID + ", ";
		}
		return DT_Status;
	}
}