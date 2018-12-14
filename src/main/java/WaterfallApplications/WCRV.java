package WaterfallApplications;

import org.testng.annotations.Test;
import SupportClasses.Environment;
import SupportClasses.Helper_Functions;
import org.testng.annotations.BeforeClass;
import org.testng.Assert;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Listeners;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import TestingFunctions.*;

@Listeners(SupportClasses.TestNG_TestListener.class)

public class WCRV{
	static ArrayList<String[]> ResultsList = new ArrayList<String[]>();
	static String LevelsToTest = "3";
	static String CountryList[][];

	@BeforeClass
	public void beforeClass() {
		Environment.SetLevelsToTest(LevelsToTest);
	
		CountryList = Environment.getCountryList("smoke");
		//CountryList = new String[][]{{"US", "United States"}};
	}
	
	@DataProvider (parallel = true)
	public static Iterator<Object[]> dp(Method m) {
		List<Object[]> data = new ArrayList<Object[]>();

		for (int i=0; i < Environment.LevelsToTest.length(); i++) {
			String Level = String.valueOf(Environment.LevelsToTest.charAt(i));
			int intLevel = Integer.parseInt(Level);
			//int intLevel = Integer.parseInt(Level);
			switch (m.getName()) { //Based on the method that is being called the array list will be populated.
		    	case "WCRV_Generate_RateSheet":
		    		/*
		    		for (int j = 0; j < CountryList.length; j++) {
		    			for (int k = 0; k < Helper_Functions.DataClass[intLevel].length; k++) {
		    				if (Helper_Functions.DataClass[intLevel][k].SSO_LOGIN_DESC.contains("WCRV") && Helper_Functions.DataClass[intLevel][k].SSO_LOGIN_DESC.contains(CountryList[j][0]) ) {
		    					data.add( new Object[] {Level, CountryList[j][0], Helper_Functions.DataClass[intLevel][k].SSO_LOGIN_DESC, Helper_Functions.DataClass[intLevel][k].USER_PASSWORD_DESC, "intra"});
		    					data.add( new Object[] {Level, CountryList[j][0], Helper_Functions.DataClass[intLevel][k].SSO_LOGIN_DESC, Helper_Functions.DataClass[intLevel][k].USER_PASSWORD_DESC, "notintra"});
		    					break;
		    				}
		    			}
		    		}
		    		*/
		    		/*data.add( new Object[] {Level, "US", "L3GtcAdmin", "Test1234", "intra"});
					data.add( new Object[] {Level, "US", "L3GtcAdmin", "Test1234", "notintra"});
		    		
					data.add( new Object[] {Level, "US", "L3UsNonAdminOwner", "Test1234", "intra"});
					data.add( new Object[] {Level, "US", "L3UsNonAdminOwner", "Test1234", "notintra"});
		    		*/
					
					data.add( new Object[] {Level, "US", "L3AdminCA", "Test1234", "intra"});
					data.add( new Object[] {Level, "US", "L3WCRV609188405", "Test1234", "notintra"});
					data.add( new Object[] {Level, "US", "L3CNRATENA", "Test1234", "intra"});
					data.add( new Object[] {Level, "US", "L3COratDIS", "Test1234", "notintra"});
					data.add( new Object[] {Level, "US", "L3DERATENA1", "Test1234", "intra"});
		    	break;
			}
		}	
		return data.iterator();
	}

	@Test(dataProvider = "dp")
	public void WCRV_Generate_RateSheet(String Level, String CountryCode, String UserId, String Password, String Service) {
		try {
			String Result[] = WCRV_Functions.WCRV_Generate(CountryCode, UserId, Password, Service);
			Helper_Functions.PrintOut(Arrays.toString(Result), false);
		}catch (Exception e) {
			Assert.fail(e.getMessage());
		}
	}
	/*
	@Test
	public void WCRV_Generate_Admin_International(){
		String Result[] = null;
		try {
			Result = WCRV_Generate("US", strWCRVAdmin, strPassword, "notintra");
		}catch (Exception e) {
			Assert.fail();
		}
		PassOrFail = true;
		ResultsList.set(TestNumber, UpdateArrayList(ResultsList.get(TestNumber), 1, Arrays.toString(Result)));
	}
	
	@Test
	public void WCRV_Generate_NonAdmin_Domestic(){
		String Result[] = null;
		try {
			Result = WCRV_Generate("US", strWCRVnonAdmin, strPassword, "intra");
		}catch (Exception e) {
			Assert.fail();
		}
		PassOrFail = true;
		ResultsList.set(TestNumber, UpdateArrayList(ResultsList.get(TestNumber), 1, Arrays.toString(Result)));
	}
	
	@Test
	public void WCRV_Generate_NonAdmin_International(){
		String Result[] = null;
		try {
			Result = WCRV_Generate("US", strWCRVnonAdmin, strPassword, "notintra");
		}catch (Exception e) {
			Assert.fail();
		}
		PassOrFail = true;
		ResultsList.set(TestNumber, UpdateArrayList(ResultsList.get(TestNumber), 1, Arrays.toString(Result)));
	}
	*/
}
