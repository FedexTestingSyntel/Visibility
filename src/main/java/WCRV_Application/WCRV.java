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

public class WCRV{
	static String LevelsToTest = "7";
	static String CountryList[][];

	@BeforeClass
	public void beforeClass() {
		Environment.SetLevelsToTest(LevelsToTest);
		//CountryList = Environment.getCountryList("smoke");
		CountryList = Environment.getCountryList("full");
		//CountryList = Environment.getCountryList("high");
		//CountryList = new String[][]{{"US", "United States"},{"AU", "Australia"},{"CA", "Canada"},{"GB", "United Kingdom"},{"BR", "Brazil"},{"AE", "United Arab Emirates"}};
		//CountryList = new String[][]{{"US", "United States"}};
		CountryList = new String[][]{{"US", ""}, {"HK", ""}, {"ID", ""}, {"LT", ""}, {"GP", ""}};
	}
	
	@DataProvider //(parallel = true)
	public static Iterator<Object[]> dp(Method m) {
		List<Object[]> data = new ArrayList<Object[]>();

		for (int i=0; i < Environment.LevelsToTest.length(); i++) {
			String Level = String.valueOf(Environment.LevelsToTest.charAt(i));
			int intLevel = Integer.parseInt(Level);
			User_Data User_Info_Array[] = User_Data.Get_UserIds(intLevel);
			
			switch (m.getName()) { //Based on the method that is being called the array list will be populated.
				case "WCRV_CheckPermission":
					for (String Country[]: CountryList) {
		    			for (User_Data User_Info: User_Info_Array) {
		    				if (User_Info.WCRV_ENABLED.contains("T") && User_Info.Address_Info.Country_Code.contentEquals(Country[0])) {
		    					data.add( new Object[] {Level, Country[0], User_Info.USER_ID, User_Info.PASSWORD});
		    				}
		    			}
		    		}
					break;
		    	case "WCRV_Generate_RateSheet":
		    		for (String Country[]: CountryList) {
		    			for (User_Data User_Info: User_Info_Array) {
		    				if (User_Info.WCRV_ENABLED.contains("T") && User_Info.Address_Info.Country_Code.contains(Country[0]) && !User_Info.EMAIL_ADDRESS.contentEquals(Helper_Functions.MyEmail)) {
		    					data.add( new Object[] {Level, Country[0], User_Info.USER_ID, User_Info.PASSWORD, "INTRA_COUNTRY", 1});
		    					data.add( new Object[] {Level, Country[0], User_Info.USER_ID, User_Info.PASSWORD, "EXPORT", 1});
		    					data.add( new Object[] {Level, Country[0], User_Info.USER_ID, User_Info.PASSWORD, "IMPORT", 1});
		    					data.add( new Object[] {Level, Country[0], User_Info.USER_ID, User_Info.PASSWORD, "THIRD_PARTY", 1});
		    					data.add( new Object[] {Level, Country[0], User_Info.USER_ID, User_Info.PASSWORD, "ANY", 4});
		    					break;
		    				}
		    			}
		    		}
		    	break;
		    	case "WCRV_Help_Link":
		    		for (User_Data User_Info: User_Info_Array) {
		    			if (User_Info.WCRV_ENABLED.contains("T")) {
		    				for (String Country[]:CountryList) {	
		    					data.add( new Object[] {Level, User_Info, Country[0]});
		    				}
		    				break;
		    			}
		    		}
		    	break;
		    		
			}
		}	
		return data.iterator();
	}

	@Test(dataProvider = "dp", enabled = false)
	public void WCRV_Generate_RateSheet(String Level, String CountryCode, String UserId, String Password, String Service, int ServiceCount) {
		try {
			String Result[] = WCRV_Functions.WCRV_Generate(CountryCode, UserId, Password, Service, ServiceCount);
			Helper_Functions.PrintOut(Arrays.toString(Result), false);
		}catch (Exception e) {
			Assert.fail(e.getMessage());
		}
	}
	
	@Test(dataProvider = "dp")
	public void WCRV_Help_Link(String Level, User_Data User_Info, String CountryCode) {
		try {
			String CurrentCookie = WebDriver_Functions.GetCookieUUID();
			if (CurrentCookie == null || !User_Info.UUID_NBR.contains(CurrentCookie)) {
				WebDriver_Functions.Login(User_Info);
			}
			
			String Result = WCRV_Functions.WCRV_Check_Help_Links(CountryCode);
			Helper_Functions.PrintOut(Result);
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
