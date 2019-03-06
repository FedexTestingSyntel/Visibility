package WaterfallApplications;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import org.openqa.selenium.By;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Listeners;
import org.testng.annotations.Test;

import Data_Structures.Enrollment_Data;
import Data_Structures.User_Data;
import SupportClasses.*;

@Listeners(SupportClasses.TestNG_TestListener.class)

public class OADR{
	static String LevelsToTest = "3";
	static String CountryList[][];
	
	@BeforeClass
	public void beforeClass() {
		Environment.SetLevelsToTest(LevelsToTest);
		CountryList = new String[][]{{"US", "United States"}};
	}
	
	@DataProvider (parallel = true)
	public static Iterator<Object[]> dp(Method m) {
		List<Object[]> data = new ArrayList<Object[]>();

		for (int i=0; i < LevelsToTest.length(); i++) {
			String Level = String.valueOf(LevelsToTest.charAt(i));
			int intLevel = Integer.parseInt(Level);

			switch (m.getName()) { //Based on the method that is being called the array list will be populated.
		    	case "OADR_Smoke_ApplyDiscount":
		    		User_Data UD[] = Environment.Get_UserIds(intLevel);
		    		boolean SixteenEight = false;//will do the diffferent discounts on different user ids.
			    	for (int j = 0; j < UD.length; j++) {
		    			if (UD[j].COUNTRY_CD.contentEquals("US") && UD[j].PASSKEY.contentEquals("T") ) {
		    				if (!SixteenEight) {
		    					// try the 16/8 discount
			    				data.add( new Object[] {Level, "ss90705920", "US", UD[j].SSO_LOGIN_DESC, UD[j].USER_PASSWORD_DESC});
		    					SixteenEight = true;
	    					}else {
	    						// try the American Society of Mechanical Engineers discount
			    				data.add( new Object[] {Level, "w6rdem3647", "US", UD[j].SSO_LOGIN_DESC, UD[j].USER_PASSWORD_DESC});
			    				break;
	    					}
		    			}
		    		}
		    	break;
		    	case "OADR_Apply_Discount_To_Account":
		    		UD = Environment.Get_UserIds(intLevel);
		    		Enrollment_Data ED[] = Environment.getEnrollmentDetails(intLevel);
		    		for (int j = 0; j < CountryList.length; j++) {
		    			for (Enrollment_Data Enrollment: ED) {
		    				if (Enrollment.COUNTRY_CODE.contentEquals(CountryList[j][0])) {
					    		for (int k = 0; k < UD.length; k++) {
				    				if (UD[k].COUNTRY_CD.contentEquals(CountryList[j][0]) && UD[k].PASSKEY.contentEquals("T")) {
				    					data.add( new Object[] {Level, Enrollment.ENROLLMENT_ID, CountryList[j][0], UD[j].SSO_LOGIN_DESC, UD[j].USER_PASSWORD_DESC});
					    				break;
				    				}
				    			}
		    				}
		    			}
		    		}
		    	break;
			}
		}
		return data.iterator();
	}
	
	
	@Test(dataProvider = "dp")
	public static void OADR_Smoke_ApplyDiscount(String Level, String EnrollmentID, String CountryCode, String UserId, String Password){
		try {
			String Result = OADR_ApplyDiscount(Level, EnrollmentID, CountryCode, UserId, Password);
			Helper_Functions.PrintOut(Result, false);
		}catch (Exception e) {
			Assert.fail(e.getMessage());
		}
	}//end OADR_ApplyDiscount
	
	@Test(dataProvider = "dp")
	public static void OADR_Apply_Discount_To_Account(String Level, String EnrollmentID, String CountryCode, String UserId, String Password){
		try {
			String Result = OADR_ApplyDiscount(Level, EnrollmentID, CountryCode, UserId, Password);
			Helper_Functions.PrintOut(Result, false);
		}catch (Exception e) {
			Assert.fail(e.getMessage());
		}
	}//end OADR_ApplyDiscount

	public static String OADR_ApplyDiscount(String Level, String EnrollmentID, String CountryCode, String UserId, String Password) throws Exception{
		try {
			WebDriver_Functions.Login(UserId, Password);
			WebDriver_Functions.ChangeURL("Enrollment_" + EnrollmentID, CountryCode, false);
	 		
	 		//select the apply discount radio button and continue.
			WebDriver_Functions.Click(By.xpath("//input[(@name='whichBillingAddress') and (@value = 'useContactAddress')]"));
			WebDriver_Functions.takeSnapShot("Apply Discount.png");
			WebDriver_Functions.Click(By.name("continue"));
	 		
	 		//after processing screen
	 		try {
	 			WebDriver_Functions.WaitForTextPresentIn(By.tagName("body"), "We are processing your request.");
	 			WebDriver_Functions.WaitForTextNotPresentIn(By.tagName("body"), "We are processing your request.");
			}catch (Exception e) {}
	 		
	 		if (WebDriver_Functions.isPresent(By.name("discountConflictResolution"))){
	 			WebDriver_Functions.Click(By.xpath("//input[(@name='discountConflictResolution') and (@value = 'applyNewDiscount')]"));
	 			//WebDriver_Functions.ElementMatches(By.xpath("//*[@id='content']/div/div[2]/p[2]/font"), "You have existing discounts for FedEx Express®, FedEx Ground®, and/or FedEx Freight® services on your account. Those discounts will be replaced with new discounts should you choose to enroll in this program. For more information about the new discounts, call a FedEx Advantage® representative at 1.800.434.9918.", 116519);
	 			WebDriver_Functions.takeSnapShot("Discount Conflict.png");
	 			
	 			InvoiceOrCCValidaiton();
	 			
	 			if (WebDriver_Functions.isPresent(By.name("submit"))){
	 				WebDriver_Functions.Click(By.name("submit"));
	 			}
	 			
	 			WebDriver_Functions.WaitForTextNotPresentIn(By.className("mainheader1"), "We are processing your request. Please wait...");
	 		}
	 		
	 		WebDriver_Functions.WaitForText(By.xpath("//*[@id='rightColumn']/table/tbody/tr/td[1]/div/div[1]/div[2]/table/tbody/tr[1]/td[2]"), UserId);
	 		WebDriver_Functions.takeSnapShot("Confirmation.png");
	 		
		}catch (Exception e) {
			throw e;
		}
		return UserId + " " + EnrollmentID;
	}//end OADR_ApplyDiscount

	private static void InvoiceOrCCValidaiton() throws Exception{
		InvoiceOrCCValidaiton("4460", "750000000", "750000001");
	}

	private static void InvoiceOrCCValidaiton(String CCNumber, String Invoice1, String Invoice2) throws Exception{
		if (WebDriver_Functions.isPresent(By.name("invoice1"))){
			WebDriver_Functions.Type(By.name("invoice1"), Invoice1);
			WebDriver_Functions.Type(By.name("invoice2"), Invoice2);
			WebDriver_Functions.Click(By.className("buttonpurple"));
			WebDriver_Functions.WaitNotPresent(By.name("invoiceNumberB"));
		}else if (WebDriver_Functions.isPresent(By.name("lastFourDigits"))) {
			WebDriver_Functions.Type(By.name("lastFourDigits"), CCNumber);//this is just a guess as the number most commonly used. 
			WebDriver_Functions.Click(By.name("submit"));
			WebDriver_Functions.WaitNotPresent(By.name("lastFourDigits"));
		}
	}
}
