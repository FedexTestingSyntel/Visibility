package WDRP_Application_Test;

import java.lang.reflect.Method;
import java.util.ArrayList;
import org.testng.annotations.Test;

import Data_Structures.Address_Data;
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
import WERL_Application.WERL_Functions;
import WebElements.EnrollmentPageElements;

@Listeners(SupportClasses.TestNG_TestListener.class)

public class SmokeTestWDRP{
	static String LevelsToTest = "3";
	
	@BeforeClass
	public void beforeClass() {
		Environment.SetLevelsToTest(LevelsToTest);
	}
	
	@DataProvider (parallel = true)
	public static Iterator<Object[]> dp(Method m) {
		List<Object[]> data = new ArrayList<Object[]>();
		
		for (int i=0; i < Environment.LevelsToTest.length(); i++) {
			String Level = String.valueOf(Environment.LevelsToTest.charAt(i));

			//Based on the method that is being called the array list will be populated.
			switch (m.getName()) {
				case "WERL_Registration":
					data.add( new Object[] {Level});
				break;
			}
		}	
		System.out.println(data.size() + " scenarios.");
		return data.iterator();
	}
	
	/*@Test(dataProvider = "dp")
	public void WDRP_AddressSearch(String Level, Address_Data Address ){		
		try {// still need to finsih
	 		WebDriver_Functions.ChangeURL("WERL", Address.Country_Code, "en", true);
	 		
	 		WebDriver_Functions.Click(EnrollmentPageElements.SignUpButton);
	 		WERL_Functions.EnterContactInformaiton(User_Info, true);
	 		
	 		WERL_Functions.EnterLoginInformaiton(User_Info, true);
	 		
	 		User_Info.UUID_NBR = WebDriver_Functions.GetCookieUUID(); 
	 		String UserName = User_Info.UUID_NBR + "-" + User_Info.Address_Info.Share_Id;
	 		if (WebDriver_Functions.CheckBodyText("Verify your registration address by mail")) {
	 			WERL_Functions.FDMValidation(UserName, "POSTAL");
	 		}else {
	 			
	 		}
	 		
	 		WebDriver_Functions.Click(By.linkText("SAVE"));
	 		WebDriver_Functions.WaitClickable(By.linkText("DONE"));
	 		WebDriver_Functions.Click(By.linkText("DONE"));
	 		
		}catch (Exception e) {
			Assert.fail(e.getMessage());
		}
		
		
	}*/

}