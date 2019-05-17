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

public class WPRL_SmokeTest { 

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
			//Based on the method that is being called the array list will be populated.
			switch (m.getName()) { 
		    	case "WPRL_ContactInfo_Admin":
		    		User_Data User_Info[] = Environment.Get_UserIds(intLevel);
		    		for (int j = 0; j < CountryList.length; j++) {
		    			for (int k = 0; k < User_Info.length; k++) {
		    				if (User_Info[k].PASSKEY.contentEquals("T") && User_Info[k].Address_Info.Country_Code.contentEquals(CountryList[j][0])) {
		    					data.add( new Object[] {Level, User_Info[k].USER_ID, User_Info[k].PASSWORD, User_Info[k].Address_Info.Country_Code, Helper_Functions.MyEmail});
		    					break;
		    				}
		    			}
					}
		    		break;
		    	case "WPRL_AccountManagement_Passkey":
		    		User_Info = Environment.Get_UserIds(intLevel);
		    		for (int j = 0; j < CountryList.length; j++) {
		    			for (int k = 0; k < User_Info.length; k++) {
		    				if (User_Info[k].PASSKEY.contentEquals("T") && User_Info[k].Address_Info.Country_Code.contentEquals(CountryList[j][0]) && !User_Info[k].ACCOUNT_NUMBER.contentEquals("")) {
		    					data.add( new Object[] {Level, User_Info[k].USER_ID, User_Info[k].PASSWORD, User_Info[k].Address_Info.Country_Code, Helper_Functions.MyEmail});
		    					break;
		    				}
		    			}
					}
		    	break;
		    	case "WPRL_FDM":
		    		User_Info = Environment.Get_UserIds(intLevel);
		    		for (int j = 0; j < CountryList.length; j++) {
		    			for (int k = 0; k < User_Info.length; k++) {
		    				if (!User_Info[k].FDM_STATUS.contentEquals("F") && User_Info[k].Address_Info.Country_Code.contentEquals(CountryList[j][0])) {
		    					data.add( new Object[] {Level, User_Info[k].USER_ID, User_Info[k].PASSWORD, CountryList[j][0], Helper_Functions.MyFakeEmail});
		    					break;
		    				}
		    			}
					}
		    	break;
		    	case "WPRL_AccountManagement_NonPasskey":
		    		User_Info = Environment.Get_UserIds(intLevel);
		    		for (int j = 0; j < CountryList.length; j++) {
		    			for (int k = 0; k < User_Info.length; k++) {
		    				if (User_Info[k].PASSKEY.contentEquals("F") && User_Info[k].Address_Info.Country_Code.contentEquals(CountryList[j][0]) && !User_Info[k].ACCOUNT_NUMBER.contentEquals("")) {
		    					data.add( new Object[] {Level, User_Info[k].USER_ID, User_Info[k].PASSWORD, User_Info[k].Address_Info.Country_Code, Helper_Functions.MyEmail});
		    					break;
		    				}
		    			}
					}
		    		break;
		    	case "WPRL_ContactInfo_NonAdmin":
		    		User_Info = Environment.Get_UserIds(intLevel);
		    		for (int j = 0; j < CountryList.length; j++) {
		    			for (int k = 0; k < User_Info.length; k++) {
		    				if (User_Info[k].PASSKEY.contentEquals("F") && User_Info[k].Address_Info.Country_Code.contentEquals(CountryList[j][0])) {
		    					data.add( new Object[] {Level, User_Info[k].USER_ID, User_Info[k].PASSWORD, CountryList[j][0], Helper_Functions.MyEmail});
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
	public void WPRL_ContactInfo_Admin(String Level, String UserID, String Password, String CountryCode, String Email) {
		try {
			String Address[] = Helper_Functions.LoadAddress(CountryCode);
			String ContactName[] = Helper_Functions.LoadDummyName(CountryCode, Level);
			String ContactDetails[][] = Helper_Functions.LoadPhone_Mobile_Fax_Email(CountryCode);
			String Result[] =  WPRL_Functions.WPRL_Contact(UserID, Password, Address, ContactName, ContactDetails, Email);
			Helper_Functions.PrintOut(Arrays.toString(Result), false);
		}catch (Exception e) {
			Assert.fail(e.getMessage());
		}
	}
	
	@Test(dataProvider = "dp")
	public void WPRL_ContactInfo_NonAdmin(String Level, String UserID, String Password, String CountryCode, String Email) {
		try {
			String Address[] = Helper_Functions.LoadAddress(CountryCode);
			String ContactName[] = Helper_Functions.LoadDummyName(CountryCode, Level);
			String ContactDetails[][] = Helper_Functions.LoadPhone_Mobile_Fax_Email(CountryCode);
			String Result[] =  WPRL_Functions.WPRL_Contact(UserID, Password, Address, ContactName, ContactDetails, Email);
			Helper_Functions.PrintOut(Arrays.toString(Result), false);
		}catch (Exception e) {
			Assert.fail(e.getMessage());
		}
	}
	
	@Test(dataProvider = "dp")
	public void WPRL_AccountManagement_Passkey(String Level, String UserID, String Password, String CountryCode, String Email) {
		try {
			String Address[] = Helper_Functions.LoadAddress(CountryCode);
			String CreditCard[] = Helper_Functions.LoadCreditCard("V");
			String ContactName[] = Helper_Functions.LoadDummyName(CountryCode, Level);
			String Result[] =  WPRL_Functions.WPRL_AccountManagement(UserID, Password, Address, CreditCard, ContactName);
			Helper_Functions.PrintOut(Arrays.toString(Result), false);
		}catch (Exception e) {
			Assert.fail(e.getMessage());
		}
	}
	
	@Test(dataProvider = "dp")
	public void WPRL_AccountManagement_NonPasskey(String Level, String UserID, String Password, String CountryCode, String Email) {
		try {
			String Address[] = Helper_Functions.LoadAddress(CountryCode);
			String CreditCard[] = Helper_Functions.LoadCreditCard("V");
			String ContactName[] = Helper_Functions.LoadDummyName(CountryCode, Level);
			String Result[] =  WPRL_Functions.WPRL_AccountManagement(UserID, Password, Address, CreditCard, ContactName);
			Helper_Functions.PrintOut(Arrays.toString(Result), false);
		}catch (Exception e) {
			Assert.fail(e.getMessage());
		}
	}

	@Test(dataProvider = "dp")
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

