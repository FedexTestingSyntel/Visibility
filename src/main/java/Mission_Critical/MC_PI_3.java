package Mission_Critical;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import org.testng.annotations.Test;

import Data_Structures.Account_Data;
import Data_Structures.Enrollment_Data;
import Data_Structures.User_Data;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Listeners;
import org.openqa.selenium.By;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import java.util.Iterator;
import java.util.List;
import SupportClasses.Environment;
import SupportClasses.Helper_Functions;
import SupportClasses.WebDriver_Functions;
import WFCL_Application.WFCL_Functions;
import WFCL_Application.WFCL_Functions_UsingData;
import WIDM_Application.WIDM_Functions;

@Listeners(SupportClasses.TestNG_TestListener.class)

public class MC_PI_3{
	static String LevelsToTest = "6";
	static String CountryList[][];
	final boolean EnableCompleted = false;
	
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
			String Rewards_APAC_AND_LAC[] = new String[] {"au", "cn", "hk", "jp", "my", "nz", "ph", "sg", "kr", "tw", "th", "br", "mx"};

			switch (m.getName()) { //Based on the method that is being called the array list will be populated.
				case"WIDM_Registration_Email_Validation":
				case"WFCL_Registration_Email_Validation":
					for (int j=0; j < CountryList.length; j++) {
						String InValid_Email = "abc~cde@fedex.com";	
						data.add( new Object[] {Level, CountryList[j][0], InValid_Email});
					}
					break;
				case "WFCL_Alliance_Marketing_Error_Page":
				case "WFCL_Alliance_Marketing_Error_Page_Blank":
					Enrollment_Data ED[] = Environment.getEnrollmentDetails(intLevel);
					for (Enrollment_Data Enrollment_Info: ED) {
						//if AEM and a pass code/membership is required.
						if (!Enrollment_Info.AEM_LINK.contentEquals("") && (!Enrollment_Info.MEMBERSHIP_ID.contentEquals("") ||  !Enrollment_Info.PASSCODE.contentEquals(""))) {
							data.add( new Object[] {Level, Enrollment_Info});
						}
					}
					break;
				case "WFCL_Alliance_Marketing_CodeRequired":
					User_Data UD[] = Environment.Get_UserIds(intLevel);
		    		for (int k = 0; k < UD.length; k++) {
		    			if (UD[k].PASSKEY.contentEquals("T")) {
							ED = Environment.getEnrollmentDetails(intLevel);
							for (Enrollment_Data Enrollment_info: ED) {
								//if AEM and a pass code/membership is required.
								if (!Enrollment_info.AEM_LINK.contentEquals("") && (!Enrollment_info.MEMBERSHIP_ID.contentEquals("") ||  !Enrollment_info.PASSCODE.contentEquals(""))) {
									data.add( new Object[] {Level, UD[k], Enrollment_info});
								}
							}
							break;
		    			}
					}
					break;
				case "WFCL_Alliance_Marketing":
					UD = Environment.Get_UserIds(intLevel);
		    		for (int k = 0; k < UD.length; k++) {
		    			if (UD[k].PASSKEY.contentEquals("T")) {
							Enrollment_Data ED_One[] = Environment.getEnrollmentDetails(intLevel);
							for (Enrollment_Data Enrollment_Info: ED_One) {
								//if AEM and a pass code/membership is required.
								if (!Enrollment_Info.AEM_LINK.contentEquals("") && Enrollment_Info.MEMBERSHIP_ID.contentEquals("") && Enrollment_Info.PASSCODE.contentEquals("")) {
									data.add( new Object[] {Level, UD[k], Enrollment_Info});
								}
							}
							break;
		    			}
					}
					break;
				case "WFCL_Rewards_Registration_APAC_AND_LAC":
		    		for (int j = 0; j < Rewards_APAC_AND_LAC.length; j++) {
		    			Account_Data Account_Info = Helper_Functions.getFreshAccount(Level, Rewards_APAC_AND_LAC[j]);
		    			if (Account_Info != null) {
		    				data.add( new Object[] {Level, Account_Info});
		    			}else {
		    				Helper_Functions.PrintOut("Account is not available for country. " + Rewards_APAC_AND_LAC[j]);
		    			}
					}
					break;
				case "WFCL_Rewards_Login_APAC_AND_LAC":
					UD = Environment.Get_UserIds(intLevel);
		    		for (int k = 0; k < UD.length; k++) {
		    			if (UD[k].PASSKEY.contentEquals("T")) {
		    				for (int j = 0; j < Rewards_APAC_AND_LAC.length; j++) {
		    					data.add( new Object[] {Level, Rewards_APAC_AND_LAC[j], UD[k]});
							}
							break;
		    			}
					}
					break;
			}
		}

		System.out.println("Starting " + m.getName() + " : There are " + data.size() + " scenarios.");
		return data.iterator();
	}

	//458437 remove ~ symbol WIDM
	//will affect all WIDM flows, as part of this automation just checking the registration flow.
	@Test(dataProvider = "dp", description = "458437", enabled = false)
	public void WIDM_Registration_Email_Validation(String Level, String CountryCode, String Email){
		try {
			String UserId = Helper_Functions.LoadUserID("L" + Level + "WIDM" + CountryCode);
			WebDriver_Functions.ChangeURL("WIDM", CountryCode, true);
			WebDriver_Functions.Click(By.linkText("Sign Up Now!"));
			WIDM_Functions.WIDM_Registration_Input(null, null, UserId, Email);
			WebDriver_Functions.Click(By.id("createUserID"));
			

			WebDriver_Functions.WaitForText(By.id("emailinvalid"), "Email address is not valid.");
			WebDriver_Functions.takeSnapShot("Invalid Email.png");
			Helper_Functions.PrintOut("Email Address: " + Email + " validated. Error message recieved.", false);
			
		}catch (Exception e) {
			Assert.fail(e.getLocalizedMessage());
		}
	}
	
	//456323 remove ~ symbol WFCL
	//will affect all WFCL flows, as part of this automation just checking the registration flow.
	@Test(dataProvider = "dp", description = "456323", enabled = false)
	public void WFCL_Registration_Email_Validation(String Level, String CountryCode, String Email) {
		try {
			String Address[] = Helper_Functions.LoadAddress(CountryCode);
			String ContactName[] = Helper_Functions.LoadDummyName("Create", Level);
			String UserId = Helper_Functions.LoadUserID("L" + Level + "WFCL" + CountryCode);

			WebDriver_Functions.ChangeURL("Pref", CountryCode, true);//navigate to email preferences page to load cookies
			WebDriver_Functions.Click(By.id("registernow"));
			WFCL_Functions.WFCL_ContactInfo_Page(ContactName, Address, UserId, Email, true); //enters all of the details
			WebDriver_Functions.WaitForText(By.id("emailinvalid"), "Email address is not valid.");
			WebDriver_Functions.takeSnapShot("Invalid Email.png");

		}catch (Exception e) {
			Assert.fail(e.getMessage());
		}
	}

	//443446 alliance program page transition - getting rid of old WFCL marketing page
	//443452 loading dt.html
	@Test(dataProvider = "dp", description = "443496, 443446, 443452", enabled = true) 
	public void WFCL_Alliance_Marketing(String Level, User_Data User_Info, Enrollment_Data Enrollment_Info) {
		try {
			String Result[] = WFCL_Functions_UsingData.AEM_Discount_Validate( User_Info, Enrollment_Info);
			Helper_Functions.PrintOut(Arrays.toString(Result), false);
		}catch (Exception e) {
			Assert.fail(e.getMessage());
		}
	}
	
	//443446 alliance program page transition - getting rid of old WFCL marketing page
	//443452 loading dt.html
	@Test(dataProvider = "dp", description = "482695, 443446, 443452", enabled = true)
	public void WFCL_Alliance_Marketing_CodeRequired(String Level, User_Data User_Info, Enrollment_Data Enrollment_Info) {
		try {
			String Result[] = WFCL_Functions_UsingData.AEM_Discount_Validate( User_Info, Enrollment_Info);
			Helper_Functions.PrintOut(Arrays.toString(Result), false);
		}catch (Exception e) {
			Assert.fail(e.getMessage());
		}
	}
	
	@Test(dataProvider = "dp", description = "482700, 494070", enabled = true)
	public void WFCL_Alliance_Marketing_Error_Page(String Level, Enrollment_Data Enrollment_Info) {
		try {
			String Result = WFCL_Functions_UsingData.AEM_Error_Validation(Enrollment_Info, "");

			Helper_Functions.PrintOut(Result, false);
		}catch (Exception e) {
			Assert.fail(Enrollment_Info.ENROLLMENT_ID + "  " + e.getMessage());
		}
	}
	
	@Test(dataProvider = "dp", description = "482700, 494070", enabled = true)
	public void WFCL_Alliance_Marketing_Error_Page_Blank(String Level, Enrollment_Data Enrollment_Info) {
		try {
			String Result = WFCL_Functions_UsingData.AEM_Error_Validation(Enrollment_Info, " ");

			Helper_Functions.PrintOut(Result, false);
		}catch (Exception e) {
			Assert.fail(Enrollment_Info.ENROLLMENT_ID + "  " + e.getMessage());
		}
	}
	
	@Test(dataProvider = "dp", description = "483863", enabled = false)
	public void WFCL_Rewards_Registration_APAC_AND_LAC(String Level, Account_Data Account_Info) {
		try {
			Account_Data.Print_Account_Address(Account_Info);
			Account_Data.Set_UserId(Account_Info, "L" + Level + Account_Info.Billing_Country_Code + "Rewards");
			Account_Data.Set_Dummy_Contact_Name(Account_Info);
			
			String Result[] = WFCL_Functions_UsingData.WFCL_RewardsRegistration(Account_Info);

			Helper_Functions.PrintOut(Arrays.toString(Result), false);
		}catch (Exception e) {
			Assert.fail(e.getMessage());
		}
	}
	
	@Test(dataProvider = "dp", description = "483861", enabled = false)
	public void WFCL_Rewards_Login_APAC_AND_LAC(String Level, String CountryCode, User_Data User_Info) {
		try {

			String Result[] = WFCL_Functions_UsingData.WFCL_RewardsLogin(CountryCode, User_Info);

			Helper_Functions.PrintOut(Arrays.toString(Result), false);
		}catch (Exception e) {
			Assert.fail(e.getMessage());
		}
	}
	
	
	
	
	
	@DataProvider (parallel = true)
	public static Iterator<Object[]> dp_dt() {
		List<Object[]> data = new ArrayList<Object[]>();
		for (int i=0; i < Environment.LevelsToTest.length(); i++) {
			String Level = String.valueOf(Environment.LevelsToTest.charAt(i));
			int intLevel = Integer.parseInt(Level);
			Enrollment_Data ED_One[] = Environment.getEnrollmentDetails(intLevel);
			for (Enrollment_Data Enrollment_Info: ED_One) {
				if (!Enrollment_Info.AEM_LINK.contentEquals("")) {
					data.add( new Object[] {Level, Enrollment_Info});
				}
			}
		}
		return data.iterator();
	}
	//quick check on the values in the DT files
	@Test(dataProvider = "dp_dt", description = "", enabled = false)
	public void WFCL_DT_Check(String Level, Enrollment_Data Enrollment_Info) {
		String DTValue = "";
		try {
			WebDriver_Functions.ChangeURL("DT_" + Enrollment_Info.ENROLLMENT_ID, Enrollment_Info.COUNTRY_CODE, false);
			WebDriver_Functions.takeSnapShot(Enrollment_Info.ENROLLMENT_ID + " DT Value.png");
			
			//close if there is an alert message. This is specific to L6
			if (WebDriver_Functions.isPresent(By.cssSelector("body > header > fedex-alert > div > div > span.fxg-alert__close-btn > svg"))) {
				WebDriver_Functions.Click(By.cssSelector("body > header > fedex-alert > div > div > span.fxg-alert__close-btn > svg"));
			}
			DTValue = WebDriver_Functions.GetBodyText();
			
		} catch (Exception e) {
			Assert.fail(e.getMessage());
		}
		Helper_Functions.PrintOut(DTValue);
		
	}
}