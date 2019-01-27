package WDPA_Application;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Listeners;
import org.testng.annotations.Test;

import Data_Structures.User_Data;
import SupportClasses.Environment;
import SupportClasses.Helper_Functions;

@Listeners(SupportClasses.TestNG_TestListener.class)

public class WDPA extends WDPA_Functions{
	
	static ArrayList<String[]> AddressDetails = new ArrayList<String[]>();
	static String LevelsToTest = "7";
	static String CountryList[][];

	@BeforeClass
	public void beforeClass() {
		Environment.SetLevelsToTest(LevelsToTest);

		CountryList = Environment.getCountryList("smoke");
		//CountryList = new String[][]{{"US", "United States"}};
		//CountryList = new String[][]{{"CA", "Canada"}};
	}
	
	@DataProvider (parallel = true)
	public static Iterator<Object[]> dp(Method m) {
		List<Object[]> data = new ArrayList<Object[]>();

		for (int i=0; i < Environment.LevelsToTest.length(); i++) {
			String Level = String.valueOf(Environment.LevelsToTest.charAt(i));
			int intLevel = Integer.parseInt(Level);

			switch (m.getName()) { //Based on the method that is being called the array list will be populated.
		    	case "Pickup_Ground":
		    	case "Pickup_Express":
		    	case "Pickup_ExpressFright"://need to fix this later, not for all countries.
		    		User_Data UD[] = Environment.Get_UserIds(intLevel);
		    		for (int j = 0; j < CountryList.length; j++) {
		    			for (int k = 1; k < UD.length; k++) {
		    				if (UD[k].SSO_LOGIN_DESC.contains("WDPA")) {
		    					data.add( new Object[] {Level, CountryList[j][0], UD[k].SSO_LOGIN_DESC, UD[k].USER_PASSWORD_DESC});
		    					break;
		    				}
		    			}
					}
		    	break;
		    	case "Pickup_LTLFreight":    //update this later to restrict based on country
		    		UD = Environment.Get_UserIds(intLevel);
		    		for (int j = 0; j < CountryList.length; j++) {
		    			for (int k = 0; k < UD.length; k++) {
		    				if (UD[k].SSO_LOGIN_DESC.contains("Freight") || UD[k].SSO_LOGIN_DESC.contains("LTL")) {
		    					data.add( new Object[] {Level, CountryList[j][0], UD[k].SSO_LOGIN_DESC, UD[k].USER_PASSWORD_DESC});
		    					break;
		    				}
		    			}
					}
		    	break;
		    	case "Pickup_LTLFreight_Anonymous":    //update this later to restrict based on country
		    		for (int j = 0; j < CountryList.length; j++) {
		    			data.add( new Object[] {Level, CountryList[j][0]});
					}
		    	break;
			}
		}	
		return data.iterator();
	}
	
	@Test(dataProvider = "dp")
	public static void Pickup_Ground(String Level, String CountryCode, String UserID, String Password){
		Helper_Functions.PrintOut("Schedule a ground pickup.", false);
		try {
			String Address[] = Helper_Functions.LoadAddress(CountryCode);
			String Result = Arrays.toString(WDPAPickupDetailed(CountryCode, UserID, Password, "ground", "CompanyNameHere", "John Doe", "9011111111", Address, null, "INET"));
			Helper_Functions.PrintOut(Result, false);
		}catch (Exception e) {
			Assert.fail(e.getMessage());
		}
	}//end WDPA_Pickup_Ground
	
	@Test(dataProvider = "dp")
	public static void Pickup_Express(String Level, String CountryCode, String UserID, String Password){
		Helper_Functions.PrintOut("Schedule an express pickup.", false);
		try {
			String Address[] = Helper_Functions.LoadAddress(CountryCode);
			WDPAPickupDetailed(CountryCode, UserID, Password, "express", "CompanyNameHere", "John Doe", "9011111111", Address, null, "INET");
		}catch (Exception e) {
			Assert.fail(e.getMessage());
		}
	}//end WDPA_Pickup_Express
	
	@Test(dataProvider = "dp")
	public static void Pickup_ExpressFright(String Level, String CountryCode, String UserID, String Password){
		Helper_Functions.PrintOut("Schedule an express freight pickup.", false);
		try {
			String PackageDetails[] = {"1", "444", "L", "1400", "1800", "ExpLTL Attempt", "FedEx 1Day Freight", "ConfFiller", "side of barn", "5", "6", "7"};
			String Address[] = Helper_Functions.LoadAddress(CountryCode);
			String Result = Arrays.toString(WDPAPickupDetailed("US", UserID, Password, "expFreight", "ExpressLTL Testing", "ExpressLTL Attempt", "9011111111", Address, PackageDetails, "INET"));
			Helper_Functions.PrintOut(Result, false);
		}catch (Exception e) {
			Assert.fail(e.getMessage());
		}
	}//end WDPAPickup_ExpressFright
	
	@Test(dataProvider = "dp")
	public static void Pickup_LTLFreight(String Level, String CountryCode, String UserID, String Password){
		Helper_Functions.PrintOut("Schedule a LTL pickup while logged in.", false);
		try {
			String Address[] = Helper_Functions.LoadAddress(CountryCode);
			String Result = WDPALTLPickup(Address, UserID, Password, "10", "400");
			Helper_Functions.PrintOut(Result, false);
		}catch (Exception e) {
			Assert.fail(e.getMessage());
		}
	}//end WDPAPickup_ExpressFright
	
	@Test(dataProvider = "dp")
	public static void Pickup_LTLFreight_Anonymous(String Level, String CountryCode){
		Helper_Functions.PrintOut("Schedule a LTL pickup while not logged into FedEx.com", false);
		try {
			String Address[] = Helper_Functions.LoadAddress(CountryCode);
			String Result = WDPALTLPickup(Address, "", "", "10", "400");
			Helper_Functions.PrintOut(Result, false);
		}catch (Exception e) {
			Assert.fail(e.getMessage());
		}
	}//end WDPAPickup_ExpressFright
	
	@Test
	public static void Looping_LTL() {
		Environment.getInstance().setLevel("6");
		boolean breakout = false;
		while  (!breakout) {
			try {
				String Address[] = Helper_Functions.LoadAddress("US");
				String Result = WDPALTLPickup(Address, "", "", "10", "400");
				Helper_Functions.PrintOut(Result, false);
				
			}catch (Exception e) {
				Helper_Functions.PrintOut("FAIL\n\n", false);
			}
			
			//Helper_Functions.Wait(60);
		}
		
	}
	
	
}