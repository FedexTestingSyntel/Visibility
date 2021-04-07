package ATRK_Application;

import org.testng.annotations.Test;
import Data_Structures.User_Data;
import org.testng.annotations.BeforeClass;
import org.testng.Assert;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Listeners;
import java.lang.reflect.Method;
import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import SupportClasses.*;

@Listeners(SupportClasses.TestNG_TestListener.class)

public class ATRK_Performance{
	static String LevelsToTest = "7";

	@BeforeClass
	public void beforeClass() {
		Environment.SetLevelsToTest(LevelsToTest);
		API_Functions.General_API_Calls.setPrintOutAPICallFlag(false);// false true
		API_Functions.General_API_Calls.setPrintOutFullResponseFlag(false);
		// uncomment for updating full list.
		Helper_Functions.TestingData = Helper_Functions.DataDirectory + "\\TestingDataFullList.xls";
	}
	
	@DataProvider  (parallel = true)
	public static Iterator<Object[]> dp(Method m) {
		List<Object[]> data = new ArrayList<Object[]>();
		
		for (int i = 0; i < Environment.LevelsToTest.length(); i++) {
			String Level = String.valueOf(Environment.LevelsToTest.charAt(i));
			int intLevel = Integer.parseInt(Level);
			User_Data userInfoArray[] = User_Data.Get_UserIds(intLevel);
			//Based on the method that is being called the array list will be populated
			switch (m.getName()) {
		    	case "TestSpeed":
		    		for (User_Data User_Info : userInfoArray) {
		    			if (User_Info.USER_ID.contentEquals("l4user008")) {
		    				// update the timeout time to 600 seconds.
		    				DriverFactory.WaitTimeOut= 300;
		    				for (int cnt = 0; cnt < 20; cnt++) {
		    					data.add( new Object[] {Level, User_Info});
		    				}
		    			}
		    		}
		    	break;
			}
		}
		
		SupportClasses.Helper_Functions.LimitDataProvider(m.getName(), -1, data);
		
		return data.iterator();
	}

	@Test(dataProvider = "dp")
	public void TestSpeed(String Level, User_Data User_Info) {
		try {
			WebDriver_Functions.Login(User_Info);
			WebDriver_Functions.ChangeURL("ATRK_BETA", null, null, false);
			WebDriver_Functions.WaitForBodyText("Loading");
			Instant start = Instant.now();
			
			WebDriver_Functions.WaitForBodyText("Welcome");
			Instant finish = Instant.now();
			 
		    long timeElapsed = Duration.between(start, finish).toMillis();  //in millis
			Helper_Functions.PrintOut("Completed in " + timeElapsed + " mili seconds.");

		}catch (Exception e) {
			Assert.fail(e.getMessage());
		}
	}
}