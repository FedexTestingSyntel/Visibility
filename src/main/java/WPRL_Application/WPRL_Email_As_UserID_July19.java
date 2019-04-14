package WPRL_Application;

import java.lang.reflect.Method;
import java.util.ArrayList;
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
		    		User_Data UD[] = Environment.Get_UserIds(intLevel);
		    		for (int j = 0; j < CountryList.length; j++) {
		    			for (int k = 0; k < UD.length; k++) {
		    				if (UD[k].COUNTRY_CD.contentEquals(CountryList[j][0]) && !UD[k].SSO_LOGIN_DESC.contains("@")) {
		    					data.add( new Object[] {Level, UD[k]});
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
			String EmailAddress = Helper_Functions.CurrentDateTime() + "@WPRLTesting.com";
			//String Result[] =  WPRL_Functions.WPRL_Contact(UserID, Password, Address, ContactName, ContactDetails, Email);
			//Helper_Functions.PrintOut(Arrays.toString(Result), false);
		}catch (Exception e) {
			Assert.fail(e.getMessage());
		}
	}
	
}
