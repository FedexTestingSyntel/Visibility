package WPRL_Application;

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

import Data_Structures.USRC_Data;
import Data_Structures.User_Data;
import SupportClasses.Environment;
import SupportClasses.Helper_Functions;
import SupportClasses.WebDriver_Functions;
import USRC_Application.USRC_API_Endpoints;

@Listeners(SupportClasses.TestNG_TestListener.class)

public class WPRL_Email_As_UserID_July19 { 
	static String LevelsToTest = "3";
	static String CountryList[][];

	@BeforeClass
	public void beforeClass() {
		Environment.SetLevelsToTest(LevelsToTest);
		Helper_Functions.MyEmail = Helper_Functions.MyFakeEmail;//just to make sure do not use personal email for email as user id
		CountryList = new String[][]{{"US", "United States"}};
	}
	
	@DataProvider (parallel = true)
	public static Iterator<Object[]> dp(Method m) {
		List<Object[]> data = new ArrayList<Object[]>();

		for (int i=0; i < Environment.LevelsToTest.length(); i++) {
			String Level = String.valueOf(Environment.LevelsToTest.charAt(i));
			int intLevel = Integer.parseInt(Level);
			//Based on the method that is being called the array list will be populated.
			switch (m.getName()) { 
		    	case "WPRL_Contact_Admin":
		    		User_Data User_Info[] = User_Data.Get_UserIds(intLevel);
		    		for (int j = 0; j < CountryList.length; j++) {
		    			for (int k = 0; k < User_Info.length; k++) {
		    				if (User_Info[k].Address_Info.Country_Code.contentEquals(CountryList[j][0]) && !User_Info[k].USER_ID.contains("@")) {
		    					data.add( new Object[] {Level, User_Info[k]});
		    					break;
		    				}
		    			}
					}
		    	break;
			}
		}	
		return data.iterator();
	}

	@Test(dataProvider = "dp")
	public void WPRL_User_Email_As_UserID(String Level, User_Data User_Info) {
		try {
			//his will create a unique email address
			User_Info.EMAIL_ADDRESS = Helper_Functions.CurrentDateTime() + Helper_Functions.getRandomString(3) + "@WPRLTesting.com";
			User_Info = WPRL_Functions_Data.WPRL_Contact(User_Info);
			
			//get the email address from USRC call
			String Email_Address = "NA";
    		try {
    			USRC_Data USRC_Details = USRC_Data.LoadVariables(Environment.getInstance().getLevel());
    			String[] fdx_login_fcl_uuid = USRC_API_Endpoints.Login(USRC_Details.GenericUSRCURL, User_Info.USER_ID, User_Info.PASSWORD);
    			String ContactDetailsResponse = USRC_API_Endpoints.ViewUserProfileWIDM(USRC_Details.ViewUserProfileWIDMURL, fdx_login_fcl_uuid[0]);
    			String ContactDetailsParsed[][] = new String[][] {{"EMAIL_ADDRESS", ""}};
    			ContactDetailsParsed = USRC_API_Endpoints.Parse_ViewUserProfileWIDM(ContactDetailsResponse, ContactDetailsParsed);
    			Email_Address = ContactDetailsParsed[0][1];
    		}catch (Exception e1) {
    			Helper_Functions.PrintOut("Error " + e1.getMessage() + "   " + Email_Address, true);
    		}
    		
    		//click edit to login information
    		WebDriver_Functions.Click(By.cssSelector("#logininfo > #show-hide > div.fx-toggler > #edit"));
    		
    		//UserID radio button
    		WebDriver_Functions.WaitClickable(By.id("useridradio1"));
    		WebDriver_Functions.WaitForText(By.id("userId_opt_1"), "Create my own User ID");
    		//WebDriver_Functions.wai
    		
    		
    		                                    //*[@id="edit"]
    		
			//Helper_Functions.PrintOut(Arrays.toString(Result), false);
		}catch (Exception e) {
			Assert.fail(e.getMessage());
		}
	}
	
}
