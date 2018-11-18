package AgileApplications;

import java.lang.reflect.Method;
import java.util.ArrayList;
import org.testng.annotations.Test;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Listeners;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import java.util.Iterator;
import java.util.List;
import SupportClasses.Environment;
import SupportClasses.Helper_Functions;
import TestingFunctions.WIDM_Functions;

@Listeners(SupportClasses.TestNG_TestListener.class)

public class WIDM extends Helper_Functions{
	static String LevelsToTest = "3";
	static String CountryList[][];
	
	@BeforeClass
	public void beforeClass() {
		Environment.SetLevelsToTest(LevelsToTest);
		for (int i=0; i < LevelsToTest.length(); i++) {
			String Level = String.valueOf(LevelsToTest.charAt(i));
			LoadUserIds(Integer.parseInt(Level));
		}
		
		CountryList = Environment.getCountryList("smoke");
		//CountryList = new String[][]{{"US", "United States"}};
	
	}
	
	@DataProvider (parallel = true)
	public static Iterator<Object[]> dp(Method m) {
		List<Object[]> data = new ArrayList<Object[]>();

		for (int i=0; i < Environment.LevelsToTest.length(); i++) {
			String Level = String.valueOf(Environment.LevelsToTest.charAt(i));
			int intLevel = Integer.parseInt(Level);
			switch (m.getName()) { //Based on the method that is being called the array list will be populated.
		    	case "WIDM_ResetPasswordSecret":
		    		for (int j=0; j < CountryList.length; j++) {
						data.add( new Object[] {Level, CountryList[j][0], DataClass[intLevel][1].SSO_LOGIN_DESC, DataClass[intLevel][1].USER_PASSWORD_DESC, DataClass[intLevel][1].SECRET_ANSWER_DESC}); 
					}
		    		break;
				case "WIDM_ForgotUserID":
					data.add( new Object[] {Level, MyEmail});   //level, email
					break;
				case "WIDM_Registration":
				case "WIDM_Registration_ErrorMessages":
					if (Level == "7") {
						data.add( new Object[] {Level, "US"});
					}else {
						for (int j=0; j < CountryList.length; j++) {
							data.add( new Object[] {Level, CountryList[j][0]});
						}
					}
					break;
				case "WIDM_RegistrationEFWS":
					data.add( new Object[] {Level, CountryList[0][0]});
					break;
				case "ResetPasswordWIDM_Email":
					data.add( new Object[] {Level, DataClass[intLevel][1].SSO_LOGIN_DESC, DataClass[intLevel][1].USER_PASSWORD_DESC});
					break;
			}
		}	
		return data.iterator();
	}

	@Test(dataProvider = "dp")
	public void WIDM_Registration(String Level, String CountryCode){
		try {
			String Address[] = LoadAddress(CountryCode);
			//Address = Helper_Functions.AccountDetails("761391020");
			String UserName[] = LoadDummyName("WIDM", Level);
			String UserId = LoadUserID("L" + Level + "WIDM" + CountryCode);
			WIDM_Functions.WIDM_Registration(Address, UserName, UserId);
		}catch (Exception e) {
			Assert.fail(e.getMessage());
		}
	}
	
	@Test(dataProvider = "dp")
	public void WIDM_RegistrationEFWS(String Level, String CountryCode){
		try {
			String Address[] = LoadAddress(CountryCode);
			String UserName[] = LoadDummyName("WIDM", Level);
			String UserId = LoadUserID("L" + Level + "EFWS");
			WIDM_Functions.WIDM_RegistrationEFWS(Address, UserName, UserId);
		}catch (Exception e) {
			Assert.fail(e.getMessage());
		}
	}
	
	@Test(dataProvider = "dp")
	public void WIDM_ResetPasswordSecret(String Level, String CountryCode, String UserId, String Password, String SecretAnswer){
		try {
			WIDM_Functions.ResetPasswordWIDM_Secret(CountryCode, UserId, Password + "5", SecretAnswer);
		}catch (Exception e) {
			Assert.fail(e.getMessage());
		}
	}
	
	@Test(dataProvider = "dp")
	public void WIDM_ForgotUserID(String Level, String Email){
		try {
			WIDM_Functions.Forgot_User_WIDM(Email);
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
			String Address[] = LoadAddress(CountryCode);
			String Name[] = LoadDummyName(CountryCode, Level);
			String UserID = LoadUserID("L" + Level + "T");
			WIDM_Functions.WIDM_Registration_ErrorMessages(Address, Name, UserID) ;
		}catch (Exception e) {
			e.printStackTrace();
			Assert.fail(e.getMessage());
		}
	}
	
}