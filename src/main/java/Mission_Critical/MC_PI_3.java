package Mission_Critical;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import org.testng.annotations.Test;
import Data_Structures.Account_Data;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Listeners;
import org.openqa.selenium.By;
import org.testng.Assert;
import org.testng.SkipException;
import org.testng.annotations.BeforeClass;
import java.util.Iterator;
import java.util.List;
import SupportClasses.DriverFactory;
import SupportClasses.Environment;
import SupportClasses.Helper_Functions;
import SupportClasses.WebDriver_Functions;
import WFCL_Application.WFCL_Functions;
import WIDM_Application.WIDM_Functions;

@Listeners(SupportClasses.TestNG_TestListener.class)

public class MC_PI_3{
	static String LevelsToTest = "3";
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
			
			String InValid_Email = "abc~cde@fedex.com";	

			switch (m.getName()) { //Based on the method that is being called the array list will be populated.
				case "":
					break;
			}
		}
		System.out.println("Starting " + m.getName() + " : There are " + data.size() + " scenarios.");
		return data.iterator();
	}

	
	//443446 alliance program page transition - getting rid of old WFCL marketing page
	//443483 loading email dt
	//456323 remove ~ symbol WFCL
	//443452 loading dt.html
	//443496 alliance program page testing
	//458437 remove ~ symbol WIDM
	
	
	
	
}