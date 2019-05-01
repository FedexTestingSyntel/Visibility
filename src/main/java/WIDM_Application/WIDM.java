package WIDM_Application;

import java.lang.reflect.Method;
import java.util.ArrayList;
import org.testng.annotations.Test;

import Data_Structures.User_Data;

import org.testng.annotations.DataProvider;
import org.testng.annotations.Listeners;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import java.util.Iterator;
import java.util.List;
import SupportClasses.Environment;
import SupportClasses.Helper_Functions;

@Listeners(SupportClasses.TestNG_TestListener.class)

public class WIDM{
	static String LevelsToTest = "6";
	static String CountryList[][];
	
	@BeforeClass
	public void beforeClass() {
		Environment.SetLevelsToTest(LevelsToTest);
		
		CountryList = Environment.getCountryList("smoke");
		//CountryList = new String[][]{{"US", "United States"}};
		//Helper_Functions.MyEmail = "OtherEmail@accept.com";
	}
	
	@DataProvider (parallel = true)
	public static Iterator<Object[]> dp(Method m) {
		List<Object[]> data = new ArrayList<Object[]>();

		for (int i=0; i < Environment.LevelsToTest.length(); i++) {
			String Level = String.valueOf(Environment.LevelsToTest.charAt(i));
			int intLevel = Integer.parseInt(Level);
			switch (m.getName()) { //Based on the method that is being called the array list will be populated.
		    	case "WIDM_ResetPasswordSecret":
		    		User_Data UD[] = Environment.Get_UserIds(intLevel);
		    		for (int j = 0; j < CountryList.length; j++) {
		    			for (int k = 0; k < UD.length; k++) {
		    				if (UD[k].SSO_LOGIN_DESC.contains("WIDM")) {
		    					data.add( new Object[] {Level, CountryList[j][0], UD[k].SSO_LOGIN_DESC, UD[k].USER_PASSWORD_DESC + "5", UD[k].SECRET_ANSWER_DESC});
		    					break;
		    				}
		    			}
					}
		    		break;
				case "WIDM_ForgotUserID":
					data.add( new Object[] {Level, Helper_Functions.MyEmail});   //level, email
					break;
				case "WIDM_Registration":
				case "WIDM_Registration_ErrorMessages":
					if (Level == "7") {
						data.add( new Object[] {Level, "US", Helper_Functions.MyEmail});
					}else {
						for (int j=0; j < CountryList.length; j++) {
							data.add( new Object[] {Level, CountryList[j][0], Helper_Functions.MyEmail});
						}
					}
					break;
				case "WIDM_RegistrationEFWS":
					data.add( new Object[] {Level, CountryList[0][0]});
					break;
				case "ResetPasswordWIDM_Email":
					UD = Environment.Get_UserIds(intLevel);
		    		for (int j = 0; j < CountryList.length; j++) {
		    			for (int k = 0; k < UD.length; k++) {
		    				if (UD[k].COUNTRY_CD.contentEquals(CountryList[j][0])) {
		    					data.add( new Object[] {Level, UD[k].SSO_LOGIN_DESC, UD[k].USER_PASSWORD_DESC});
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
	public void WIDM_Registration(String Level, String CountryCode, String EmailAddress){
		try {
			String Address[] = Helper_Functions.LoadAddress(CountryCode);
			//Address = Helper_Functions.AccountDetails("761391020");
			String UserName[] = Helper_Functions.LoadDummyName("WIDM", Level);
			String UserId = Helper_Functions.LoadUserID("L" + Level + "WIDM" + CountryCode);
			//UserId = Helper_Functions.LoadUserID("L" + Level + "UserId" + CountryCode);
			WIDM_Functions.WIDM_Registration(Address, UserName, UserId, EmailAddress);
			
		}catch (Exception e) {
			Assert.fail(e.getMessage());
		}
	}
	
	@Test(dataProvider = "dp")
	public void WIDM_RegistrationEFWS(String Level, String CountryCode){
		try {
			String Address[] = Helper_Functions.LoadAddress(CountryCode);
			String UserName[] = Helper_Functions.LoadDummyName("WIDM", Level);
			String UserId = Helper_Functions.LoadUserID("L" + Level + "EFWS");
			WIDM_Functions.WIDM_RegistrationEFWS(Address, UserName, UserId);
		}catch (Exception e) {
			Assert.fail(e.getMessage());
		}
	}
	
	@Test(dataProvider = "dp")
	public void WIDM_ResetPasswordSecret(String Level, String CountryCode, String UserId, String Password, String SecretAnswer){
		try {
			WIDM_Functions.ResetPasswordWIDM_Secret(CountryCode, UserId, Password + "5", SecretAnswer, false);
		}catch (Exception e) {
			Assert.fail(e.getMessage());
		}
	}
	
	@Test(dataProvider = "dp")
	public void WIDM_ForgotUserID(String Level, String Email){
		try {
			WIDM_Functions.Forgot_User_WIDM("US", Email);
		}catch (Exception e) {
			Assert.fail(e.getMessage());
		}
	}
	
	@Test(dataProvider = "dp")
	public void ResetPasswordWIDM_Email(String Level, String UserId, String Password){
		try {
			WIDM_Functions.Reset_Password_WIDM_Email(UserId, Password);
		}catch (Exception e) {
			Assert.fail(e.getMessage());
		}
	}

	@Test(dataProvider = "dp")
	public void WIDM_Registration_ErrorMessages(String Level, String CountryCode){
		try {
			String Address[] = Helper_Functions.LoadAddress(CountryCode);
			String Name[] = Helper_Functions.LoadDummyName(CountryCode, Level);
			String UserID = Helper_Functions.LoadUserID("L" + Level + "T");
			WIDM_Functions.WIDM_Registration_ErrorMessages(Address, Name, UserID) ;
		}catch (Exception e) {
			e.printStackTrace();
			Assert.fail(e.getMessage());
		}
	}
	
}