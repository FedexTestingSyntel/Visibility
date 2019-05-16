package WCRV_Application;

import org.testng.annotations.Test;
import Data_Structures.User_Data;
import SupportClasses.Environment;
import SupportClasses.Helper_Functions;
import SupportClasses.WebDriver_Functions;

import org.testng.annotations.BeforeClass;
import org.testng.Assert;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Listeners;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

@Listeners(SupportClasses.TestNG_TestListener.class)

public class WCRV_SmokeTest{
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

			switch (m.getName()) { //Based on the method that is being called the array list will be populated.
				case "WCRV_CheckPermission":
					User_Data UD[] = Environment.Get_UserIds(intLevel);
					for (int j = 0; j < CountryList.length; j++) {
		    			for (int k = 0; k < UD.length; k++) {
		    				if (UD[k].WCRV_ENABLED.contains("T") && UD[k].COUNTRY_CD.contentEquals(CountryList[j][0])) {
		    					data.add( new Object[] {Level, CountryList[j][0], UD[k].USER_ID, UD[k].PASSWORD});
		    				}
		    			}
		    		}
					break;
		    	case "WCRV_Generate_RateSheet":
		    		UD = Environment.Get_UserIds(intLevel);
		    		for (int j = 0; j < CountryList.length; j++) {
		    			for (int k = 0; k < UD.length; k++) {
		    				if (UD[k].WCRV_ENABLED.contains("T") && UD[k].COUNTRY_CD.contains(CountryList[j][0]) && !UD[k].EMAIL_ADDRESS.contentEquals(Helper_Functions.MyEmail)) {
		    					data.add( new Object[] {Level, CountryList[j][0], UD[k].USER_ID, UD[k].PASSWORD, "INTRA_COUNTRY", 1});
		    					data.add( new Object[] {Level, CountryList[j][0], UD[k].USER_ID, UD[k].PASSWORD, "EXPORT", 1});
		    					data.add( new Object[] {Level, CountryList[j][0], UD[k].USER_ID, UD[k].PASSWORD, "IMPORT", 1});
		    					data.add( new Object[] {Level, CountryList[j][0], UD[k].USER_ID, UD[k].PASSWORD, "THIRD_PARTY", 1});
		    					break;
		    				}
		    			}
		    		}
		    	break;
		    	case "WCRV_Generate_RateSheet_Passkey":
		    		UD = Environment.Get_UserIds(intLevel);
		    		for (int j = 0; j < CountryList.length; j++) {
		    			for (int k = 0; k < UD.length; k++) {
		    				if (UD[k].WCRV_ENABLED.contains("T") && UD[k].COUNTRY_CD.contains(CountryList[j][0]) && !UD[k].EMAIL_ADDRESS.contentEquals(Helper_Functions.MyEmail) && UD[k].PASSKEY.contains("T")) {
		    					data.add( new Object[] {Level, CountryList[j][0], UD[k].USER_ID, UD[k].PASSWORD, "INTRA_COUNTRY", 1});
		    					data.add( new Object[] {Level, CountryList[j][0], UD[k].USER_ID, UD[k].PASSWORD, "EXPORT", 1});
		    					data.add( new Object[] {Level, CountryList[j][0], UD[k].USER_ID, UD[k].PASSWORD, "IMPORT", 1});
		    					data.add( new Object[] {Level, CountryList[j][0], UD[k].USER_ID, UD[k].PASSWORD, "THIRD_PARTY", 1});
		    					break;
		    				}
		    			}
		    		}
		    	break;
		    	case "WCRV_Help_Link":
		    		UD = Environment.Get_UserIds(intLevel);
		    		for (int j = 0; j < CountryList.length; j++) {
		    			for (int k = 0; k < UD.length; k++) {
		    				if (UD[k].WCRV_ENABLED.contains("T")) {
		    					data.add( new Object[] {Level, UD[k].USER_ID, UD[k].PASSWORD, CountryList[j][0]});
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
	public void WCRV_Generate_RateSheet(String Level, String CountryCode, String UserId, String Password, String Service, int ServiceCount) {
		try {
			String Result[] = WCRV_Functions.WCRV_Generate(CountryCode, UserId, Password, Service, ServiceCount);
			Helper_Functions.PrintOut(Arrays.toString(Result), false);
		}catch (Exception e) {
			Assert.fail(e.getMessage());
		}
	}
	
	@Test(dataProvider = "dp")
	public void WCRV_Generate_RateSheet_Passkey(String Level, String CountryCode, String UserId, String Password, String Service, int ServiceCount) {
		try {
			String Result[] = WCRV_Functions.WCRV_Generate(CountryCode, UserId, Password, Service, ServiceCount);
			Helper_Functions.PrintOut(Arrays.toString(Result), false);
		}catch (Exception e) {
			Assert.fail(e.getMessage());
		}
	}
	
	@Test(dataProvider = "dp")
	public void WCRV_Help_Link(String Level, String UserId, String Password, String CountryCode) {
		try {
			WebDriver_Functions.Login(UserId, Password);

        	boolean Help_Present = WCRV_Functions.WCRV_Check_Help_Links(CountryCode, UserId, Password);
        	if (Help_Present) {
        		Helper_Functions.PrintOut(CountryCode + " Working", false);
        	}else {
        		Helper_Functions.PrintOut(CountryCode + " help page not loading correctly", false);
        		throw new Exception (CountryCode + " help page not loading correctly");
        	}
		}catch (Exception e) {
			Assert.fail(e.getMessage());
		}
	}
	
	@Test(dataProvider = "dp", enabled = false)
	public void WCRV_CheckPermission(String Level, String CountryCode, String UserId, String Password) {
		try {
			boolean access = WCRV_Functions.WCRV_CheckPermission(CountryCode, UserId, Password);
			if (access){
				Helper_Functions.PrintOut(UserId + " has access to WCRV.", false);
			}else {
				throw new Exception("User does not have access.");
			}
		}catch (Exception e) {
			Assert.fail(e.getMessage());
		}
	}

}
