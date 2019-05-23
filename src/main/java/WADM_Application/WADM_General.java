package WADM_Application;

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

public class WADM_General{
	static String LevelsToTest = "7";  
	static String CountryList[][]; 

	@BeforeClass
	public void beforeClass() {
		Environment.SetLevelsToTest(LevelsToTest);
		CountryList = Environment.getCountryList("US");
		//Helper_Functions.MyEmail = "accept@fedex.com";
	}
	
	@DataProvider //(parallel = true)
	public static Iterator<Object[]> dp(Method m) {
		List<Object[]> data = new ArrayList<Object[]>();

		for (int i=0; i < Environment.LevelsToTest.length(); i++) {
			String Level = String.valueOf(Environment.LevelsToTest.charAt(i));
			int intLevel = Integer.parseInt(Level);
			User_Data User_Info_Array[] = User_Data.Get_UserIds(intLevel);
			
			switch (m.getName()) { //Based on the method that is being called the array list will be populated.
		    case "WADM_Add_Users":
		    	for (int j = 0; j < CountryList.length; j++) {
		    		for (int k = 0; k < User_Info_Array.length; k++) {
		    			if (User_Info_Array[k].Address_Info.Country_Code.contentEquals(CountryList[j][0]) && !User_Info_Array[k].PASSKEY.contentEquals("T")) {
		    				data.add( new Object[] {Level, User_Info_Array[k]});
		    				break;
		    			}
		    		}
				}
		    	break;
		    case "WADM_Invitation_Attempt":
	    		for (User_Data User_Info: User_Info_Array) {
		    		if (User_Info.MIGRATION_STATUS.contains("WADM") && User_Info.USER_TYPE.contains("COMPANY_ADMIN")) {
		    			data.add( new Object[] {Level, User_Info, "REGULAR"});
		    			data.add( new Object[] {Level, User_Info, "GROUP"});
		    			data.add( new Object[] {Level, User_Info, "COMPANY"});
		    			break;
		    		}
	    		}
	    	break;
			}
		}
		return data.iterator();
	}

	@Test(dataProvider = "dp")
	public void WADM_Invitation_Attempt(String Level, User_Data User_Info, String Role) {
		try {
			User_Data User_Info_Invited = new User_Data();
			User_Info_Invited.EMAIL_ADDRESS = Helper_Functions.MyEmail;

			String Result[] = WADM_Functions.Invite_User(User_Info, User_Info_Invited, Role);
			Helper_Functions.PrintOut(Arrays.toString(Result), false);
		}catch (Exception e) {
			Assert.fail(e.getMessage());
		}
	}
	
	@Test(dataProvider = "dp", enabled = false)
	public void WADM_Add_Users(String Level, User_Data User_Info) {
		try {
			for (int i = 0 ; i < 10; i++) {
				User_Data New_User_Info = new User_Data();
				WADM_Functions.WADM_Add_User_To_Company(User_Info, New_User_Info);
			}
			
			
		}catch (Exception e) {
			Assert.fail(e.getMessage());
		}
	}
	


}