package OADR_Application;

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
import Data_Structures.USRC_Data;
import Data_Structures.User_Data;
import SupportClasses.*;
import USRC_Application.USRC_API_Endpoints;
import edu.emory.mathcs.backport.java.util.Arrays;

@Listeners(SupportClasses.TestNG_TestListener.class)

public class OADR_SmokeTest{
	static String LevelsToTest = "3";
	static String CountryList[][];
	
	@BeforeClass
	public void beforeClass() {
		Environment.SetLevelsToTest(LevelsToTest);
		CountryList = Environment.getCountryList("smoke");
	}
	
	@DataProvider (parallel = true)
	public static Iterator<Object[]> dp(Method m) {
		List<Object[]> data = new ArrayList<Object[]>();

		for (int i=0; i < Environment.LevelsToTest.length(); i++) {
			String Level = String.valueOf(Environment.LevelsToTest.charAt(i));
			int intLevel = Integer.parseInt(Level);
			User_Data User_Info_Array[];
			switch (m.getName()) { //Based on the method that is being called the array list will be populated.
		    	case "OADR_Apply_Discount_To_Account":
		    		User_Info_Array = Environment.Get_UserIds(intLevel);
		    		Enrollment_Data ED[] = Environment.getEnrollmentDetails(intLevel);
		    		for (int j = 0; j < CountryList.length; j++) {
		    			for (Enrollment_Data Enrollment: ED) {
		    				if (Enrollment.COUNTRY_CODE.contentEquals(CountryList[j][0])) {
					    		for (int k = 0; k < User_Info_Array.length; k++) {
					    			//user from correct coutry that is an admin with an account number.
				    				if (User_Info_Array[k].Address_Info.Country_Code.contentEquals(CountryList[j][0]) && User_Info_Array[k].PASSKEY.contentEquals("T") && !User_Info_Array[k].ACCOUNT_NUMBER.contentEquals("")) {
				    					data.add( new Object[] {Level, Enrollment, User_Info_Array[k].USER_ID, User_Info_Array[k].PASSWORD});
				    					User_Info_Array[k].Address_Info.Country_Code = "";//so the same user will not be used again for same scenario
					    				break;
				    				}
				    			}
		    				}
		    				
		    			}	
		    		}
		    		while(data.size() > 5) {
		    			data.remove(0);
		    		}
		    		
		    	break;
			}
		}
		return data.iterator();
	}
	
	@Test(dataProvider = "dp")
	public static void OADR_Apply_Discount_To_Account(String Level, Enrollment_Data Enrollment_Info, String UserId, String Password){
		try {
			String Result = Arrays.toString(OADR_ApplyDiscount(Level, UserId, Password, Enrollment_Info));
			Helper_Functions.PrintOut(Result, false);
		}catch (Exception e) {
			Assert.fail(Enrollment_Info.ENROLLMENT_ID + ": " + e.getMessage());
		}
	}//end OADR_ApplyDiscount

	public static String[] OADR_ApplyDiscount(String Level, String UserId, String Password, Enrollment_Data ED) throws Exception{
		try {
			WebDriver_Functions.Login(UserId, Password);

			WebDriver_Functions.ChangeURL_EnrollmentID(ED, false, false);

			if (WebDriver_Functions.isPresent(By.id("apply discounts"))) {
				WebDriver_Functions.Click(By.id("apply discounts"));
				Helper_Functions.PrintOut("Discount " + ED.ENROLLMENT_ID + " is not migrationed to AEM");
			}
				
	 		//select the apply discount radio button and continue.
			WebDriver_Functions.Click(By.xpath("//input[(@name='whichBillingAddress') and (@value = 'useContactAddress')]"));
			WebDriver_Functions.takeSnapShot("Apply Discount.png");
			WebDriver_Functions.Click(By.name("continue"));
	 		
	 		//after processing screen
	 		try {
	 			WebDriver_Functions.WaitForTextPresentIn(By.tagName("body"), "We are processing your request.");
	 			WebDriver_Functions.WaitForTextNotPresentIn(By.tagName("body"), "We are processing your request.");
			}catch (Exception e) {}
	 		
	 		Helper_Functions.Wait(1);
	 		
	 		if (WebDriver_Functions.isPresent(By.xpath("//input[(@name='discountConflictResolution') and (@value = 'applyNewDiscount')]"))){
	 			WebDriver_Functions.Click(By.xpath("//input[(@name='discountConflictResolution') and (@value = 'applyNewDiscount')]"));
	 			WebDriver_Functions.takeSnapShot("Discount Conflict.png");

	 			InvoiceOrCCValidaiton();
	 			
	 			if (WebDriver_Functions.isPresent(By.name("submit"))){
	 				WebDriver_Functions.Click(By.name("submit"));
	 			}
	 			
	 			WebDriver_Functions.WaitForTextNotPresentIn(By.className("mainheader1"), "We are processing your request. Please wait...");
	 		}
	 		
	 		WebDriver_Functions.WaitForText(By.xpath("//*[@id='rightColumn']/table/tbody/tr/td[1]/div/div[1]/div[2]/table/tbody/tr[1]/td[2]"), UserId);
	 		WebDriver_Functions.takeSnapShot("Confirmation.png");
	 		
	 		String Results[] = new String[] {UserId, ED.ENROLLMENT_ID};
			return Results;
		}catch (Exception e) {
			throw e;
		}
	}//end OADR_ApplyDiscount

	public void Apply_Discount(Enrollment_Data EN, String Credit_Card, String DTValue, String UserId) throws Exception{
		//select the apply discount radio button and continue.
		WebDriver_Functions.Click(By.xpath("//input[(@name='whichBillingAddress') and (@value = 'useContactAddress')]"));
		WebDriver_Functions.takeSnapShot(EN.ENROLLMENT_ID + " Apply Discount.png");
		WebDriver_Functions.Click(By.name("continue"));
	 		
	 	//after processing screen
	 	try {
	 		WebDriver_Functions.WaitForTextPresentIn(By.tagName("body"), "We are processing your request.");
	 		WebDriver_Functions.WaitForTextNotPresentIn(By.tagName("body"), "We are processing your request.");
		}catch (Exception e) {}
	 		
	 	if (WebDriver_Functions.CheckBodyText("An error has occurred and we are unable to process your request. Please try again.")) {
			throw new Exception("An error has occurred and we are unable to process your request. Please try again.");
		}
	 		
	 	if (WebDriver_Functions.isPresent(By.name("discountConflictResolution"))){
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
	 	if (DTValue != null && !DTValue.contentEquals("") && !WebDriver_Functions.CheckBodyText(DTValue)) {
			Helper_Functions.PrintOut("Discount is not matching expected");
 		}
	 	WebDriver_Functions.takeSnapShot(EN.ENROLLMENT_ID + " Confirmation.png");
	}
	

	private static void InvoiceOrCCValidaiton() throws Exception{
		InvoiceOrCCValidaiton(null, Environment.DummyInvoiceTwo, Environment.DummyInvoiceOne);
	}
	
	private static void InvoiceOrCCValidaiton(String Credit_Card) throws Exception{
		InvoiceOrCCValidaiton(Credit_Card, Environment.DummyInvoiceTwo, Environment.DummyInvoiceOne);
	}

	private static void InvoiceOrCCValidaiton(String CCNumber, String Invoice1, String Invoice2) throws Exception{
		if (WebDriver_Functions.isPresent(By.name("invoice1"))){
			WebDriver_Functions.Type(By.name("invoice1"), Invoice1);
			WebDriver_Functions.Type(By.name("invoice2"), Invoice2);
			WebDriver_Functions.Click(By.className("buttonpurple"));
			WebDriver_Functions.WaitNotPresent(By.name("invoiceNumberB"));
		}else if (WebDriver_Functions.isPresent(By.name("lastFourDigits"))) {
			if (CCNumber == null) {
				String Login_Cookie = WebDriver_Functions.GetCookieValue("fdx_login");
				USRC_Data User_Info = USRC_Data.LoadVariables(Environment.getInstance().getLevel());
				CCNumber = USRC_API_Endpoints.AccountRetrieval_Then_EnterpriseCustomer(User_Info.GenericUSRCURL, "fdx_login=" + Login_Cookie);
				if (CCNumber.contentEquals("")) {
					Helper_Functions.PrintOut("WARNING, not able to retrive Credit Card linked to this account. Attmpeting with 4460");
					CCNumber = "4460";
				}
			}
			WebDriver_Functions.Type(By.name("lastFourDigits"), CCNumber);//this is just a guess as the number most commonly used. 
			WebDriver_Functions.Click(By.name("submit"));
			WebDriver_Functions.WaitNotPresent(By.name("lastFourDigits"));
		}
	}
	
}
