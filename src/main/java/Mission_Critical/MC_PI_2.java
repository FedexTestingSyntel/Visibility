package Mission_Critical;

import java.lang.reflect.Method;
import java.util.ArrayList;
import org.testng.annotations.Test;
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
import TestingFunctions.WIDM_Functions;

@Listeners(SupportClasses.TestNG_TestListener.class)

public class MC_PI_2{
	static String LevelsToTest = "2";
	static String CountryList[][];
	
	@BeforeClass
	public void beforeClass() {
		Environment.SetLevelsToTest(LevelsToTest);
		CountryList = Environment.getCountryList("smoke");
		//CountryList = new String[][]{{"US", "United States"}};
	}
	
	@DataProvider (parallel = true)
	public static Iterator<Object[]> dp(Method m) {
		List<Object[]> data = new ArrayList<Object[]>();

		for (int i=0; i < Environment.LevelsToTest.length(); i++) {
			String Level = String.valueOf(Environment.LevelsToTest.charAt(i));
			switch (m.getName()) { //Based on the method that is being called the array list will be populated.
				case "WIDM_Registration_Invalid_Email":
					for (int j=0; j < CountryList.length; j++) {
						String Invalid_Email[] = new String[] {"@Grp2_DOTCOMsyntelinc.com",
								"Grp2@DOTCOM@syntelinc.com", 
								"Grp2_DOTCOM@syntelinc", 
								"a@.c", 
								".GRP2_DOTCOM@syntelinc.com", 
								"tencharacttencharacttencharacttencharacttencharacttencharact12345@accept.com",
								"tencharacttencharacttencharacttencharacttencharacttencharact1234@tencharacttencharacttencharacttencharacttencharacttencharacttencharacttencharacttenchar1.com",
								"tencharacttencharacttencharacttencharacttencharacttencharact1234@tencharacttencharacttencharacttencharacttencharacttencharacttencharacttencharacttencharacttencharacttencharacttencharacttencharacttencharacttencharacttencharacttencharacttencharact12345678.com"};
						
						for (String Email: Invalid_Email) {
							data.add( new Object[] {Level, CountryList[j][0], Email});
						}
						
					}
					break;
				case "WIDM_Registration_Valid_Email":
					for (int j=0; j < CountryList.length; j++) {
						String Valid_Emails[] = new String[] {"accept@tencharacttencharacttencharacttencharacttencharacttencharacttencharacttencharacttenchar.com",
								"a@b.c", 
								"tencharacttencharacttencharacttencharacttencharacttencharact1234@accept.com", 
								"tencharacttencharacttencharacttencharacttencharacttencharact1234@tencharacttencharacttencharacttencharacttencharacttencharacttencharacttencharacttencharacttencharacttencharacttencharacttencharacttencharacttencharacttencharacttencharacttencharact1234567.com", 
								"GRP2_DOTCOM@syntelinc.com", 
								"GRP2-DOTCOM@syntelinc.com"};		
						 
						for (String Email: Valid_Emails) {
							data.add( new Object[] {Level, CountryList[j][0], Email});
						}
						
					}
					break;
				case "WIDM_Email_BounceBack":
					if (Level.contentEquals("2")) {
						data.add( new Object[] {Level, "L2BadEmail110718T134301xv", "Test1234", Helper_Functions.getRandomString(22) + "@gmail.com"});
					}else if (Level.contentEquals("3")) {
						data.add( new Object[] {Level, "L3USCreate120518T194625px", "Test1234", Helper_Functions.getRandomString(22) + "@gmail.com"});
					}
					break;
			}
		}	
		return data.iterator();
	}

	@Test(dataProvider = "dp", description = "411835")
	public void WIDM_Registration_Valid_Email(String Level, String CountryCode, String Email){
		try {
			String Address[] = Helper_Functions.LoadAddress(CountryCode);
			String UserName[] = Helper_Functions.LoadDummyName("WIDM", Level);
			String UserId = Helper_Functions.LoadUserID("L" + Level + "WIDM" + CountryCode);
			WIDM_Functions.WIDM_Registration(Address, UserName, UserId, Email);
			Helper_Functions.PrintOut("Email Address: " + Email + " validated.", false);
		}catch (Exception e) {
			Assert.fail(e.getMessage());
		}
	}
	
	@Test(dataProvider = "dp", description = "411835")
	public void WIDM_Registration_Invalid_Email(String Level, String CountryCode, String Email){
		try {
			String Address[] = Helper_Functions.LoadAddress(CountryCode);
			String UserName[] = Helper_Functions.LoadDummyName("WIDM", Level);
			String UserId = Helper_Functions.LoadUserID("L" + Level + "WIDM" + CountryCode);
			WebDriver_Functions.ChangeURL("WIDM", CountryCode, true);
			WebDriver_Functions.Click(By.linkText("Sign Up Now!"));
			//Enter all of the form data
			WIDM_Functions.WIDM_Registration_Input(Address, UserName, UserId, Email);
			WebDriver_Functions.Click(By.id("createUserID"));
			WebDriver_Functions.WaitForText(By.id("emailinvalid"), "Email address is not valid.");
			WebDriver_Functions.takeSnapShot("Invalid Email.png");
			Helper_Functions.PrintOut("Email Address: " + Email + " validated. Error message recieved.", false);
		}catch (Exception e) {
			Assert.fail(e.getMessage());
		}
	}
	
	@Test(dataProvider = "dp", description = "420303", enabled = false)
	public void WIDM_Email_BounceBack(String Level, String UserID, String Password, String Email){
		try {
			WebDriver_Functions.ChangeURL("WGTM_FID", "US", true);
			WebDriver_Functions.Type(By.id("username"), UserID);
			WebDriver_Functions.Type(By.id("password"), Password);
			WebDriver_Functions.Click(By.name("login"));
			
			WebDriver_Functions.WaitForText(By.className("uid"), UserID);
			WebDriver_Functions.takeSnapShot(" BounceBack Page.png");
			WebDriver_Functions.Type(By.name("email"), Email);
			WebDriver_Functions.Type(By.name("retypeEmail"), Email);
			WebDriver_Functions.Click(By.name("submit"));
			
			WebDriver_Functions.WaitForTextPresentIn(By.xpath("//*[@id='content']/div/table/tbody/tr[1]/td/table/tbody/tr/td/table/tbody/tr[1]/td"), "Please click on the confirmation link in the email to complete updating your information.");
			WebDriver_Functions.takeSnapShot(" BounceBack Submission.png");
		}catch (Exception e) {
			Assert.fail(e.getMessage());
		}
	}


}