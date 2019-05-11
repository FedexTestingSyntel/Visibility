package WIDM_Application;

import static org.junit.Assert.assertThat;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.lang.reflect.Method;
import org.hamcrest.CoreMatchers;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Listeners;
import org.testng.annotations.Test;
import Data_Structures.Account_Data;
import Data_Structures.User_Data;
import Data_Structures.WIDM_Data;
import SupportClasses.Environment;
import SupportClasses.Helper_Functions;

@Listeners(SupportClasses.TestNG_TestListener.class)

public class WIDM_SOAPClient {
	static String LevelsToTest = "2";
	static String CountryList[][];
	
	@BeforeClass
	public void beforeClass() {
		Environment.SetLevelsToTest(LevelsToTest);
		
		CountryList = Environment.getCountryList("smoke");
		//CountryList = Environment.getCountryList("JP");
		Helper_Functions.MyEmail = "OtherEmail@accept.com";
	}
	
	@DataProvider (parallel = true)
	public static Iterator<Object[]> dp(Method m) {
		List<Object[]> data = new ArrayList<Object[]>();

		for (int i=0; i < Environment.LevelsToTest.length(); i++) {
			String Level = String.valueOf(Environment.LevelsToTest.charAt(i));
			int intLevel = Integer.parseInt(Level);
			
			WIDM_Data WIDM_Info = WIDM_Data.LoadVariables(Level);
			User_Data User_Info[];
			
			String EmailUserIdFlag = "true", NotEmailUserIdFlag = "false", IgnoreEmailUserIdFlag = null;
			
			switch (m.getName()) { //Based on the method that is being called the array list will be populated.
				case "AAAUserCreate":
					for (int j = 0; j < CountryList.length; j++) {
						Account_Data Account_Info = Helper_Functions.getAddressDetails(Level, CountryList[j][0]);
						data.add( new Object[] {Level, Account_Info, WIDM_Info});
					}
					break;
				case "AAAUserCreate_Email_As_UserId":
					for (int j = 0; j < CountryList.length; j++) {
						Account_Data Account_Info = Helper_Functions.getAddressDetails(Level, CountryList[j][0]);
						Account_Info.UserId = "L" + Level + "Email" + Helper_Functions.CurrentDateTime() + Helper_Functions.getRandomString(2) + "@accept.com";
						Account_Info.Email = Account_Info.UserId;
						data.add( new Object[] {Level, Account_Info, WIDM_Info, EmailUserIdFlag});
					}
					break;
				case "AAAUserUpdate":
					User_Info = Environment.Get_UserIds(intLevel);
		    		for (int j = 0; j < CountryList.length; j++) {
		    			for (int k = 0; k < User_Info.length; k++) {
		    				if (User_Info[k].COUNTRY_CD.contentEquals(CountryList[j][0]) && !User_Info[k].SECRET_ANSWER_DESC.contentEquals("") && User_Info[k].USER_TYPE.contentEquals("NON_MANAGED")) {
		    					data.add( new Object[] {Level, User_Info[k], WIDM_Info});
		    					break;
		    				}
		    			}
					}
			}
		}	
		return data.iterator();
	}

	@Test(dataProvider = "dp")
	public void AAAUserCreate(String Level, Account_Data Account_Info, WIDM_Data WIDM_Info){
		try {
			Account_Data.Set_UserId(Account_Info, "L" + Level + "WIDMCreate" + Account_Info.Billing_Country_Code);
			String Response = WIDM_Endpoints.AAA_User_Create(WIDM_Info.EndpointUrl, Account_Info, null);
			Account_Data.Print_High_Level_Details(Account_Info);
			assertThat(Response, CoreMatchers.containsString("<transactionId>"));
			Helper_Functions.PrintOut(Response);
			Helper_Functions.WriteUserToExcel(Account_Info.UserId, Account_Info.Password);
		}catch (Exception e) {
			Assert.fail(e.getMessage());
		}
	}
	
	@Test(dataProvider = "dp")
	public void AAAUserCreate_Email_As_UserId_Mismatch(String Level, Account_Data Account_Info, WIDM_Data WIDM_Info, String Email_As_UserId){
		try {
			Account_Data.Set_Dummy_Contact_Name(Account_Info);
			Account_Data.Set_UserId(Account_Info, "L" + Level + "WIDMCreate" + Account_Info.Billing_Country_Code);
			String Response = WIDM_Endpoints.AAA_User_Create(WIDM_Info.EndpointUrl, Account_Info, Email_As_UserId);
			Account_Data.Print_High_Level_Details(Account_Info);
			assertThat(Response, CoreMatchers.containsString("<transactionId>"));
			Helper_Functions.PrintOut(Response);
			Helper_Functions.WriteUserToExcel(Account_Info.UserId, Account_Info.Password);
		}catch (Exception e) {
			Assert.fail(e.getMessage());
		}
	}
	
	@Test(dataProvider = "dp")
	public void AAAUserCreate_Email_As_UserId(String Level, Account_Data Account_Info, WIDM_Data WIDM_Info, String Email_As_UserId){
		try {
			Account_Data.Set_Dummy_Contact_Name(Account_Info);
			//Account_Data.Set_UserId(Account_Info, "L" + Level + "WIDMCreate" + Account_Info.Billing_Country_Code);
			String Response = WIDM_Endpoints.AAA_User_Create(WIDM_Info.EndpointUrl, Account_Info, Email_As_UserId);
			Account_Data.Print_High_Level_Details(Account_Info);
			assertThat(Response, CoreMatchers.containsString("<transactionId>"));
			Helper_Functions.WriteUserToExcel(Account_Info.UserId, Account_Info.Password);
		}catch (Exception e) {
			Assert.fail(e.getMessage());
		}
	}
	
	@Test(dataProvider = "dp")
	public void AAAUserUpdate(String Level, User_Data User_Info, WIDM_Data WIDM_Info){
		try {
			User_Data.Print_High_Level_Details(User_Info);
			User_Data.Set_Dummy_Contact_Name(User_Info, User_Info.COUNTRY_CD, Level);
			String Response = WIDM_Endpoints.AAA_User_Update(WIDM_Info.EndpointUrl, User_Info, null);
			User_Data.Print_High_Level_Details(User_Info);
			assertThat(Response, CoreMatchers.containsString("<transactionId>"));

		}catch (Exception e) {
			Assert.fail(e.getMessage());
		}
	}
}
