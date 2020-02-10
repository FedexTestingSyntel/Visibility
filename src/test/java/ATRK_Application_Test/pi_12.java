package ATRK_Application_Test;

import static org.hamcrest.CoreMatchers.containsString;
import static org.junit.Assert.assertThat;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import org.testng.annotations.Test;
import Data_Structures.User_Data;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Listeners;
import org.hamcrest.CoreMatchers;

import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import java.util.Iterator;
import java.util.List;
import SupportClasses.Environment;
import SupportClasses.Helper_Functions;
import SupportClasses.WebDriver_Functions;
import WebElements.AdvancedTrackingPageElements;
import WebElements.TrackingPageElements;

@Listeners(SupportClasses.TestNG_TestListener.class)

public class pi_12{
	static String LevelsToTest = "3";
	private static String CountryList[] = new String[] {"US", "CA", "MX", "GB", "AE", "JP"};
	
	@BeforeClass
	public void beforeClass() {
		Environment.SetLevelsToTest(LevelsToTest);
	}
	
	@DataProvider (parallel = true)
	public static Iterator<Object[]> dp(Method m) {
		List<Object[]> data = new ArrayList<Object[]>();
		User_Data userInfoArray[];
		
		for (int i=0; i < Environment.LevelsToTest.length(); i++) {
			String Level = String.valueOf(Environment.LevelsToTest.charAt(i));
			int intLevel = Integer.parseInt(Level);
			switch (m.getName()) { //Based on the method that is being called the array list will be populated.
		    	case "ATRK_AVERT_1228244":
		    		userInfoArray = User_Data.Get_UserIds(intLevel);
		    		for (String CountryCode: CountryList) {
		    			for (User_Data User_Info: userInfoArray) {
		    				//if (User_Info.MIGRATION_STATUS.contentEquals("WADM")) { ////just first user for now until get WTRK access added
		    					data.add( new Object[] {Level, User_Info, CountryCode});
		    					break;
		    				//}
		    			}
					}
		    		break;
			}
		}	
		System.out.println(data.size() + " scenarios.");
		return data.iterator();
	}
	
	@Test(dataProvider = "dp", description = "554236, 558010 - AVERT #1228244")
	public void ATRK_AVERT_1228244(String Level, User_Data User_Info, String CountryCode){		
		try {
			WebDriver_Functions.Login(User_Info);
	 		WebDriver_Functions.ChangeURL("ATRK", CountryCode, null, false);
	 		
	 		// Wait for the 'Last updated' message to appear. 
	 		// This is a check to make sure the page loaded and ready to test next step.
	 		WebDriver_Functions.WaitPresent(TrackingPageElements.LastUpdatedTime);
	 		
	 		// Check to make sure the loading image has been removed.
			boolean loadingImg = WebDriver_Functions.isEnabled(TrackingPageElements.loadingImgage);

			// Check to make sure the "Last Updated Text is not present.
			boolean nxtUpdateLnk = WebDriver_Functions.isEnabled(TrackingPageElements.nextUpdateLink);

			// Clear out the logged in cookies
			String RemovedCookies[] = new String[] {"SMIDENTITY", "fdx_login", "fcl_uuid"};
			for (String CookieName: RemovedCookies) {
				WebDriver_Functions.SetCookieValue(CookieName, "");
			}
			Helper_Functions.PrintOut("Cookies Cleared");
			
			// Save the current url to make testing US easier, update later
			String CurrentURL = WebDriver_Functions.GetCurrentURL();
			String LevelURL = WebDriver_Functions.LevelUrlReturn();
			// Click a tracking number on the ATRK page
			WebDriver_Functions.Click(TrackingPageElements.randomTrackingNumber());
			
			//Check to make sure the user has been redirected to correct login page.
			boolean LoggedOutRedirect = false;
			
			// Will first check if the LastUpdatedTime is not present to prove user navigated from WTRK page
			WebDriver_Functions.WaitNotPresent(TrackingPageElements.LastUpdatedTime);
			
			// User must be on correct country login page and no longer in WTRK
			String CurrentPage = WebDriver_Functions.GetCurrentURL();
			if (CurrentPage.contentEquals(CurrentURL) && 
					CountryCode.contentEquals("US")) {
				LoggedOutRedirect = true;
			}else if (CurrentPage.contentEquals(LevelURL + "/apps/fedextracking/") && 
					!CountryCode.contentEquals("US")) {
				//for non us locals
				LoggedOutRedirect = true;
			}
			
			Helper_Functions.PrintOut("User is currently on _" + CurrentPage + "_");
						
			String Results[] = new String[] {"Country " + CountryCode, 
					"loadingImg Present:" + loadingImg, 
					"nxtUpdateLnk Present:" + nxtUpdateLnk, 
					"LoggedOutRedirect:" + LoggedOutRedirect};
			
			//Print out the results of the tests for debug if fails.
			Helper_Functions.PrintOut(Arrays.toString(Results));
			
			assertThat(Arrays.toString(Results), CoreMatchers.allOf(
					containsString("loadingImg Present:false"), 
					containsString("nxtUpdateLnk Present:false"), 
					containsString("LoggedOutRedirect:true")));
			
		}catch (Exception e) {
			Assert.fail(e.getMessage());
		}
	}

	@Test(dataProvider = "dp", description = "361427 - Tracking: Delete Shipment")
	public void ATRK_Tracking_Delete_Shipment(String Level, User_Data User_Info, String CountryCode){		
		try {
			WebDriver_Functions.Login(User_Info);
	 		WebDriver_Functions.ChangeURL("ATRK", CountryCode, null, false);
	 		
	 		// Click the hamburger icon for the first package.
	 		WebDriver_Functions.Click(AdvancedTrackingPageElements.fistTrackingHamburgerMenu);
	 		
	 		// Click the remove shipment button
	 		WebDriver_Functions.Click(AdvancedTrackingPageElements.hamburgerRemoveShipment);
	 		
	 			 		
	 		// Wait for the 'Last updated' message to appear. 
	 		// This is a check to make sure the page loaded and ready to test next step.
	 		WebDriver_Functions.WaitPresent(TrackingPageElements.LastUpdatedTime);
	 		
	 		// Check to make sure the loading image has been removed.
			boolean loadingImg = WebDriver_Functions.isEnabled(TrackingPageElements.loadingImgage);

			// Check to make sure the "Last Updated Text is not present.
			boolean nxtUpdateLnk = WebDriver_Functions.isEnabled(TrackingPageElements.nextUpdateLink);

			// Clear out the logged in cookies
			String RemovedCookies[] = new String[] {"SMIDENTITY", "fdx_login", "fcl_uuid"};
			for (String CookieName: RemovedCookies) {
				WebDriver_Functions.SetCookieValue(CookieName, "");
			}
			Helper_Functions.PrintOut("Cookies Cleared");
			
			// Save the current url to make testing US easier, update later
			String CurrentURL = WebDriver_Functions.GetCurrentURL();
			String LevelURL = WebDriver_Functions.LevelUrlReturn();
			// Click a tracking number on the ATRK page
			WebDriver_Functions.Click(TrackingPageElements.randomTrackingNumber());
			
			//Check to make sure the user has been redirected to correct login page.
			boolean LoggedOutRedirect = false;
			
			// Will first check if the LastUpdatedTime is not present to prove user navigated from WTRK page
			WebDriver_Functions.WaitNotPresent(TrackingPageElements.LastUpdatedTime);
			
			// User must be on correct country login page and no longer in WTRK
			String CurrentPage = WebDriver_Functions.GetCurrentURL();
			if (CurrentPage.contentEquals(CurrentURL) && 
					CountryCode.contentEquals("US")) {
				LoggedOutRedirect = true;
			}else if (CurrentPage.contentEquals(LevelURL + "/apps/fedextracking/") && 
					!CountryCode.contentEquals("US")) {
				//for non us locals
				LoggedOutRedirect = true;
			}
			
			Helper_Functions.PrintOut("User is currently on _" + CurrentPage + "_");
						
			String Results[] = new String[] {"Country " + CountryCode, 
					"loadingImg Present:" + loadingImg, 
					"nxtUpdateLnk Present:" + nxtUpdateLnk, 
					"LoggedOutRedirect:" + LoggedOutRedirect};
			
			//Print out the results of the tests for debug if fails.
			Helper_Functions.PrintOut(Arrays.toString(Results));
			
			assertThat(Arrays.toString(Results), CoreMatchers.allOf(
					containsString("loadingImg Present:false"), 
					containsString("nxtUpdateLnk Present:false"), 
					containsString("LoggedOutRedirect:true")));
			
		}catch (Exception e) {
			Assert.fail(e.getMessage());
		}
	}
}