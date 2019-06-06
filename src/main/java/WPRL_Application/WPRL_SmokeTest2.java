package WPRL_Application;

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

public class WPRL_SmokeTest2 { 

	static String LevelsToTest = "3";
	static String CountryList[][];

	@BeforeClass
	public void beforeClass() {
		Environment.SetLevelsToTest(LevelsToTest);
		CountryList = Environment.getCountryList("smoke");
		Helper_Functions.MyEmail = "accept@fedex.com";
	}
	
	@DataProvider (parallel = true)
	public static Iterator<Object[]> dp(Method m) {
		List<Object[]> data = new ArrayList<Object[]>();

		for (int i=0; i < Environment.LevelsToTest.length(); i++) {
			String Level = String.valueOf(Environment.LevelsToTest.charAt(i));
			int intLevel = Integer.parseInt(Level);
			User_Data User_Info_Array[] = User_Data.Get_UserIds(intLevel);
			
			//Based on the method that is being called the array list will be populated.
			switch (m.getName()) { 
		    	case "WPRL_ContactInfo_Admin":
		    	case "WPRL_AccountManagement_Admin_Nickname":
		    		for (String Country[]: CountryList) {
		    			boolean AdminUser = false, NonAdminUser = false;
		    			for (User_Data User_Info: User_Info_Array) {
		    				if (!AdminUser && User_Info.PASSKEY.contentEquals("T") && User_Info.Address_Info.Country_Code.contentEquals(Country[0])) {
		    					User_Info.EMAIL_ADDRESS = "accept@fedex.com";
		    					data.add( new Object[] {Level, User_Info});
		    					AdminUser = true;
		    				}else if (!NonAdminUser && User_Info.PASSKEY.contentEquals("F") && User_Info.Address_Info.Country_Code.contentEquals(Country[0])) {
		    					User_Info.EMAIL_ADDRESS = "accept@fedex.com";
		    					data.add( new Object[] {Level, User_Info});
		    					NonAdminUser = true;
		    				}
		    				if (AdminUser && NonAdminUser) {
		    					break;
		    				}
		    			}
					}
		    		break;

		    		
		    		
		    	
		    	case "WPRL_AccountManagement_Admin":
		    		for (int j = 0; j < CountryList.length; j++) {
		    			for (User_Data User_Info: User_Info_Array) {
		    				if (User_Info.PASSKEY.contentEquals("T") && User_Info.Address_Info.Country_Code.contentEquals(CountryList[j][0]) && !User_Info.ACCOUNT_NUMBER.contentEquals("")) {
		    					data.add( new Object[] {Level, User_Info.USER_ID, User_Info.PASSWORD, User_Info.Address_Info.Country_Code, Helper_Functions.MyEmail});
		    					break;
		    				}
		    			}
					}
		    	break;
		    	case "WPRL_FDM":
		    		for (int j = 0; j < CountryList.length; j++) {
		    			for (User_Data User_Info: User_Info_Array) {
		    				if (!User_Info.FDM_STATUS.contentEquals("F") && User_Info.Address_Info.Country_Code.contentEquals(CountryList[j][0])) {
		    					data.add( new Object[] {Level, User_Info.USER_ID, User_Info.PASSWORD, CountryList[j][0], Helper_Functions.MyFakeEmail});
		    					break;
		    				}
		    			}
					}
		    	break;
		    	case "WPRL_AccountManagement_NonPasskey":
		    		for (int j = 0; j < CountryList.length; j++) {
		    			for (User_Data User_Info: User_Info_Array) {
		    				if (User_Info.PASSKEY.contentEquals("F") && User_Info.Address_Info.Country_Code.contentEquals(CountryList[j][0]) && !User_Info.ACCOUNT_NUMBER.contentEquals("")) {
		    					data.add( new Object[] {Level, User_Info.USER_ID, User_Info.PASSWORD, User_Info.Address_Info.Country_Code, Helper_Functions.MyEmail});
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
	public void WPRL_ContactInfo(String Level, User_Data User_Info) {
		try {
			String Name_Initial[] = new String[] {User_Info.FIRST_NM, User_Info.MIDDLE_NM, User_Info.LAST_NM};
			User_Data.Set_Dummy_Contact_Name(User_Info, "New", Level);
			User_Data.Set_Dummy_Phone_Number(User_Info);

			WPRL_Functions_Data.WPRL_Contact(User_Info);

			User_Data.Print_High_Level_Details(User_Info);
			String Name_Final[] = new String[] {User_Info.FIRST_NM, User_Info.MIDDLE_NM, User_Info.LAST_NM};
			Helper_Functions.PrintOut(User_Info.USER_TYPE + " Name has been successfully updated: " + Arrays.toString(Name_Initial) + " to " + Arrays.toString(Name_Final), false);
		}catch (Exception e) {
			Assert.fail(e.getMessage());
		}
	}
	
	
	@Test(dataProvider = "dp")
	public void WPRL_AccountManagement_Nickname(String Level, User_Data User_Info) {
		try {
			String Result[] =  WPRL_Functions_Data.WPRL_AccountManagement_Nickname(User_Info);
			Helper_Functions.PrintOut(Arrays.toString(Result), false);
		}catch (Exception e) {
			Assert.fail(e.getMessage());
		}
	}
	

	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	///////////////


	@Test(dataProvider = "dp", enabled = false)
	public void WPRL_FDM(String Level, String UserID, String Password, String CountryCode, String Email) {
		try {
			String Address[] = Helper_Functions.LoadAddress(CountryCode);
			String CreditCard[] = Helper_Functions.LoadCreditCard("V");
			String ContactName[] = Helper_Functions.LoadDummyName(CountryCode, Level);
			String Result[] =  WPRL_Functions.WPRL_FDM(CountryCode, UserID, Password, Address, CreditCard, ContactName);
			Helper_Functions.PrintOut(Arrays.toString(Result), false);
		}catch (Exception e) {
			Assert.fail(e.getMessage());
		}
	}
}

