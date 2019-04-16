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

public class MC_PI_4{
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

			switch (m.getName()) { //Based on the method that is being called the array list will be populated.
				case "WFCL_Alliance_Marketing_CodeRequired":
					break;
			}
		}

		System.out.println("Starting " + m.getName() + " : There are " + data.size() + " scenarios.");
		return data.iterator();
	}

	@Test(dataProvider = "dp", description = "", enabled = false)
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