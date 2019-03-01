package Mission_Critical;

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
	static String LevelsToTest = "6";  
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
			int intLevel = Integer.parseInt(Level);
			switch (m.getName()) { //Based on the method that is being called the array list will be populated.
		    	case "CreditCardRegistrationEnroll_All":
		    		for (int j = 0; j < CountryList.length; j++) {
		    			String EnrollmentID[][] = Helper_Functions.LoadAllEnrollmentIDs(CountryList[j][0]);
		    			for(String Enrollment[]: EnrollmentID) {
		    				if (Enrollment[2] != null && Enrollment[2].contains("Feb19") && Enrollment[7].contains("L" + Level)  ) {
		    					String UserId = Helper_Functions.LoadUserID("L" + Level + Enrollment[0] + "CC");
		    					data.add( new Object[] {Level, UserId, Enrollment});
		    				}
		    			}
					}
		    		break;
		    	case "DiscountValidate":
		    		User_Data UD[] = Environment.Get_UserIds(intLevel);
		    		for (int j = 0; j < CountryList.length; j++) {
		    			for (int k = 1; k < UD.length; k++) {
		    				//user must have an account number and be checking below to see if pass key user.
		    				if (!UD[k].ACCOUNT_NUMBER.contentEquals("") && UD[k].PASSKEY.contentEquals("T") && !UD[k].SSO_LOGIN_DESC.contentEquals("L6RegGrdUS")) {
		    					data.add( new Object[] {Level, CountryList[j][0], UD[k].SSO_LOGIN_DESC, UD[k].USER_PASSWORD_DESC});
		    					break;
		    				}
		    			}
					}
		    		break;
			}
		}	
		return data.iterator();
	}

	@Test(dataProvider = "dp", enabled = false)
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
	
	@Test(dataProvider = "dp", enabled = true)
	public void DiscountValidate(String Level, String CountryCode, String UserId, String Password) {
		try {
			
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
			WebDriver_Functions.Login(UserId, Password);
			
			Enrollment_Data ED[] = Environment.getEnrollmentDetails(Integer.parseInt(Level));

			for (Enrollment_Data EN: ED) {
				if (EN != null && EN.IDENTIFIER.contains("Feb19")) {
					WebDriver_Functions.ChangeURL("DT_" + EN.ENROLLMENT_ID, EN.COUNTRY_CODE, false);
					//WebDriver_Functions.takeSnapShot(En[0] + " DT Value.png");
					String DTValue = WebDriver_Functions.GetBodyText();
					if (DTValue.contains("Discount program is pending") && !DTValue.contains(EN.ENROLLMENT_ID)) {
						Helper_Functions.PrintOut("     For " + EN.ENROLLMENT_ID + " the enrollment id is not correct in the DT file", false);
					}

					String ApplyNowUrlOne = "";
					if (!EN.AEM_LINK.contentEquals("")) {
						WebDriver_Functions.ChangeURL(EN.AEM_LINK, EN.COUNTRY_CODE, false);
						ApplyNowUrlOne = WebDriver_Functions.getURLByLinkText("APPLY NOW");
						//String ApplyNowUrlTwo = WebDriver_Functions.getURLByLinkText("Apply Now");//orange button
					}
		
					String ExpectedUrl = WebDriver_Functions.ChangeURL("Enrollment_" + EN.ENROLLMENT_ID, EN.COUNTRY_CODE, false);
					
					if (!ApplyNowUrlOne.contentEquals("") && !ExpectedUrl.contentEquals(ApplyNowUrlOne)) {
						DT_Status[10][1] += EN.ENROLLMENT_ID + ", ";
					}
					
					if (WebDriver_Functions.isPresent(By.name("Apply Now"))) {
						//if passcode is needed to be entered on discount page.
						if (WebDriver_Functions.isPresent(By.name("passCode")) || WebDriver_Functions.isPresent(By.name("membershipID"))) {
							DT_Status[4][1] += EN.ENROLLMENT_ID + ", ";
						}else {
							DT_Status[3][1] += EN.ENROLLMENT_ID + ", ";
							WebDriver_Functions.Click(By.name("Apply Now"));
							if (!WebDriver_Functions.CheckBodyText(DTValue)) {
								DT_Status[5][1] += EN.ENROLLMENT_ID + ", ";
							}
						}
					}
					
					if (WebDriver_Functions.CheckBodyText(DTValue)) {
							DT_Status[0][1] += EN.ENROLLMENT_ID + ", ";
							//WebDriver_Functions.takeSnapShot(EN.ENROLLMENT_ID + " Discount Matching.png");
						
							try {
								//select the apply discount radio button and continue.
								if (WebDriver_Functions.isPresent(By.name("continue"))) {
									WebDriver_Functions.Click(By.xpath("//input[(@name='whichBillingAddress') and (@value = 'useContactAddress')]"));
									//WebDriver_Functions.takeSnapShot(EN.ENROLLMENT_ID + " Apply Discount.png");
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
							 			WebDriver_Functions.ElementMatches(By.xpath("//*[@id='content']/div/div[2]/p[2]/font"), "You have existing discounts for FedEx Express®, FedEx Ground®, and/or FedEx Freight® services on your account. Those discounts will be replaced with new discounts should you choose to enroll in this program. For more information about the new discounts, call a FedEx Advantage® representative at 1.800.434.9918.", 116519);
							 			//WebDriver_Functions.takeSnapShot(EN.ENROLLMENT_ID + " Discount Conflict.png");
							 			
							 			if (WebDriver_Functions.isPresent(By.name("lastFourDigits"))){
							 				InvoiceOrCCValidaiton();
							 			}else {
							 				WebDriver_Functions.Click(By.name("submit"));
							 			}
							 			
							 			WebDriver_Functions.WaitForTextNotPresentIn(By.className("mainheader1"), "We are processing your request. Please wait...");
							 		}
							 		
							 		WebDriver_Functions.WaitForText(By.xpath("//*[@id='rightColumn']/table/tbody/tr/td[1]/div/div[1]/div[2]/table/tbody/tr[1]/td[2]"), UserId);
							 		if (!WebDriver_Functions.CheckBodyText(DTValue)) {
										DT_Status[7][1] += EN.ENROLLMENT_ID + ", ";
						 			}
							 		//WebDriver_Functions.takeSnapShot(EN.ENROLLMENT_ID + " Confirmation.png");
								}else {
									DT_Status[8][1] += EN.ENROLLMENT_ID + ", ";
								}
							}catch(Exception e) {
								DT_Status[8][1] += EN.ENROLLMENT_ID + ", ";
							}
							
						}else {
							if (WebDriver_Functions.CheckBodyText("Sorry, we cannot find the web page you are looking for.")) {
								DT_Status[2][1] += EN.ENROLLMENT_ID + ", ";
							}else {
								DT_Status[1][1] += EN.ENROLLMENT_ID + ", ";//DiscountNotMatchingOnLandingPage
							}
						}
				}
			}
			
			String Results = "";
			for(String[] Line: DT_Status){
				if (!Line[1].contentEquals("")) {
					Results += Arrays.toString(Line)  + "\n   ";
				}
			}
			Helper_Functions.PrintOut(Results, false);
			
			/*
			Helper_Functions.PrintOut("\nDiscountAppearingCorrectly: " + DiscountAppearingCorrectly + "\n" + 
										"DiscountNotMatching: " + DiscountNotMatching + "\n" + 
										"SorryMessage: " + SorryMessage + "\n" + 
										"WFCLMarketing: " + WFCLMarketing + "\n" + 
										"EnrollmentIDRequired: " + EnrollmentIDRequired + "\n" + 
										"WFCLMarketingIncorrectDiscount: " + WFCLMarketingIncorrectDiscount + "\n" + 
										"DiscountAppledSuccessfully: " + DiscountAppledSuccessfully, false);
		*/
		}catch (Exception e) {
			e.printStackTrace();
			Assert.fail(e.getMessage());
		}
	}
	
	@Test (enabled = false)
	public void DTFileCheck() {
		try {
			String Enrollments[][] = Helper_Functions.LoadAllEnrollmentIDs("US");
			Environment.getInstance().setLevel("3");
			String EnrollmentsPresent = "", EnrollmentsNotPresent = "", NonGeneric = "";
			for(String En[] : Enrollments) {
				if (En[2].contains("Feb19")) {
					WebDriver_Functions.ChangeURL("DT_" + En[0], En[1], false);
					if (WebDriver_Functions.CheckBodyText("FedEx") && 
							!WebDriver_Functions.CheckBodyText("Sorry, we cannot find the web page you are looking for.") && 
							!WebDriver_Functions.CheckBodyText("Page Not Found")) {
						EnrollmentsPresent += En[0] + ", ";
					}else {
						EnrollmentsNotPresent += En[0] + ", ";
					}
					
					if (!WebDriver_Functions.CheckBodyText("Discount program is pending.")) {
						NonGeneric += En[0] + ", ";
					}
				}

			}
			
			Helper_Functions.PrintOut("EnrollmentsPresent: " + EnrollmentsPresent + "\n" + 
										"EnrollmentsNotPresent: " + EnrollmentsNotPresent + "\n" + 
										"Non generic discounts: " + NonGeneric, false);
		}catch (Exception e) {
			Assert.fail(e.getMessage());
		}
	}
	
	@Test
	public void TesitngRollingBounce(){
		try {
			String Level = "6";
			Environment.getInstance().setLevel(Level);
			boolean flag = false;
			String LevelURL = WebDriver_Functions.LevelUrlReturn(Integer.parseInt(Level));
			String URL = "https://www.fedex.com/fcl/web/jsp/app.jsp";
			URL = URL.replace("https://www.fedex.com", LevelURL);
			for (int i = 1;; i++) {
				DriverFactory.getInstance().getDriver().get(URL);
				if (!WebDriver_Functions.CheckBodyText("Application Access Page")) {
					Helper_Functions.PrintOut("\nPage is not present " + Helper_Functions.CurrentDateTime());
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
						
					}
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
	
	private static void InvoiceOrCCValidaiton() throws Exception{
		if (WebDriver_Functions.isPresent(By.name("invoiceNumberA"))){
			String InvoiceA = "750000000", InvoiceB = "750000001";
			WebDriver_Functions.Type(By.name("invoiceNumberA"), InvoiceA);
			WebDriver_Functions.Type(By.name("invoiceNumberB"), InvoiceB);
			WebDriver_Functions.Click(By.className("buttonpurple"));
			WebDriver_Functions.WaitNotPresent(By.name("invoiceNumberB"));
		}else if (WebDriver_Functions.isPresent(By.name("lastFourDigits"))) {
			String CCNumber = "4460";/////////need to make this dynamic
			WebDriver_Functions.Type(By.name("lastFourDigits"), CCNumber);//this is just a guess as the number most commonly used. 
			WebDriver_Functions.Click(By.name("submit"));
			WebDriver_Functions.WaitNotPresent(By.name("lastFourDigits"));
		}
	}
}