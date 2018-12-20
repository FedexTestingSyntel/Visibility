package WaterfallApplications;

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

public class WRTT{
	static String LevelsToTest = "3";
	static String CountryList[][];
	static boolean SmokeTest = false;
	
	@BeforeClass
	public void beforeClass() {
		Environment.SetLevelsToTest(LevelsToTest);
		
		//if (SmokeTest) 
		CountryList = new String[][]{{"US", "United States"}, {"CA", "Canada"}};
	}
	
	@DataProvider (parallel = true)
	public static Iterator<Object[]> dp(Method m) {
		List<Object[]> data = new ArrayList<Object[]>();

		for (int i=0; i < Environment.LevelsToTest.length(); i++) {
			String Level = String.valueOf(Environment.LevelsToTest.charAt(i));
			//int intLevel = Integer.parseInt(Level);

			switch (m.getName()) { //Based on the method that is being called the array list will be populated.
		    	case "WRTT_Rate_Sheet":
		    		if (SmokeTest) {
		    			data.add( new Object[] {Level, 4, true, true, true});
		    			data.add( new Object[] {Level, 5, true, true, false});
		    			data.add( new Object[] {Level, 14, true, true, false});
		    			data.add( new Object[] {Level, 15, true, true, true});
		    		}else {//loop and do all services and all permutations
		    			for (int j = 0; j < 18; j++) {
		    				int n = 3; //three is the numbers of booleans involved
		    				for (int k = 0; k < Math.pow(2, n); k++) {
		    			        String bin = Integer.toBinaryString(k);
		    			        while (bin.length() < n) {
		    			        	bin = "0" + bin;
		    			        }
		    			        char[] chars = bin.toCharArray();
		    			        boolean[] boolArray = new boolean[n];
		    			        for (int l = 0; l < chars.length; l++) {
		    			            boolArray[l] = chars[l] == '0' ? true : false;
		    			        }
			    				data.add( new Object[] {Level, j, boolArray[0], boolArray[1], boolArray[2]});
		    			    }
		    			}
		    		}
		    	break;
		    	case "WRTT_eCRV_Page":
		    	case "WRTT_SpalshPage_eCRV":
		    		data.add( new Object[] {Level, "US"});
		    	break;
			}
		}	
		return data.iterator();
	}
	
	@Test(dataProvider = "dp")
	public static void WRTT_Rate_Sheet(String Level, int Service, boolean ZoneChart, boolean PDF, boolean List){
		Helper_Functions.PrintOut("Validate the rate sheet download through WRTT", false);
		try {
			String Result = WRTT_Functions.WRTT_Generate(Service, ZoneChart, PDF, List);
			Helper_Functions.PrintOut(Result, false);
		}catch (Exception e) {
			Assert.fail(e.getMessage());
		}
	}//end WRTT_Rate_Sheet
	
	@Test(dataProvider = "dp")
	public static void WRTT_eCRV_Page(String Level, String CountryCode){
		Helper_Functions.PrintOut("Validate the eCRV page for " + CountryCode, false);
		try {
			String Result = WRTT_Functions.WRTT_eCRV(CountryCode);
			Helper_Functions.PrintOut(Result, false);
		}catch (Exception e) {
			Assert.fail(e.getMessage());
		}
	}//end WRTT_eCRV
	
	@Test(dataProvider = "dp")
	public static void WRTT_SpalshPage_eCRV(String Level, String CountryCode){
		Helper_Functions.PrintOut("Validate the navigation from home page to eCRV page for " + CountryCode, false);
		try {
			String Result = WRTT_Functions.eCRVNavigation(CountryCode);
			Helper_Functions.PrintOut(Result, false);
		}catch (Exception e) {
			Assert.fail(e.getMessage());
		}
	}//end WRTT_eCRV
 }