package WRTT_Application;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Listeners;
import org.testng.annotations.Test;
import SupportClasses.Environment;
import SupportClasses.Helper_Functions;
import TestingFunctions.WRTT_Functions;

@Listeners(SupportClasses.TestNG_TestListener.class)

public class WRTT_SmokeTest{
	static String LevelsToTest = "2";
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
			//int intLevel = Integer.parseInt(Level);

			switch (m.getName()) { //Based on the method that is being called the array list will be populated.
		    	case "WRTT_Rate_Sheet":
		    		data.add( new Object[] {Level, 4, true, true, true});
		    		data.add( new Object[] {Level, 5, true, true, false});
		    		data.add( new Object[] {Level, 14, true, true, false});
		    		data.add( new Object[] {Level, 15, true, true, true});
		    	break;
		    	case "WRTT_SpalshPage_eCRV":
		    	case "WRTT_eCRV_WRTTLink":
		    		for (int j=0; j < CountryList.length; j++) {				
						data.add( new Object[] {Level, CountryList[j][0]});
					}
		    	break;
			}
		}	
		return data.iterator();
	}
	
	@Test(dataProvider = "dp", enabled = true)
	public static void WRTT_Rate_Sheet(String Level, int Service, boolean ZoneChart, boolean PDF, boolean List){
		Helper_Functions.PrintOut("Validate the rate sheet download through WRTT", false);
		try {
			String Result = WRTT_Functions.WRTT_Generate(Service, ZoneChart, PDF, List);
			Helper_Functions.PrintOut(Result, false);
		}catch (Exception e) {
			Assert.fail(e.getMessage());
		}
	}//end WRTT_Rate_Sheet
	
	@Test(dataProvider = "dp", enabled = true)
	public static void WRTT_eCRV_WRTTLink(String Level, String CountryCode){
		Helper_Functions.PrintOut("Validate the eCRV page for " + CountryCode, false);
		try {
			String Result = WRTT_Functions.WRTT_eCRV_WRTTLink(CountryCode);
			Helper_Functions.PrintOut(Result, false);
		}catch (Exception e) {
			Assert.fail(e.getMessage());
		}
	}//end WRTT_eCRV
	
	@Test(dataProvider = "dp", enabled = true)
	public static void WRTT_SpalshPage_eCRV(String Level, String CountryCode){
		Helper_Functions.PrintOut("Validate the RateSheets link is present in WGRT page for " + CountryCode, false);
		try {
			String Result = WRTT_Functions.eCRVNavigation(CountryCode);
			Helper_Functions.PrintOut(Result, false);
		}catch (Exception e) {
			Assert.fail(e.getMessage());
		}
	}//end WRTT_eCRV
 }