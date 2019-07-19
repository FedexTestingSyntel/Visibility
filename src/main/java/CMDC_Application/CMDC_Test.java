package CMDC_Application;

import org.testng.annotations.BeforeClass;
import org.testng.annotations.Listeners;
import SupportClasses.Environment;
import SupportClasses.Helper_Functions;
import org.testng.annotations.Test;

//import org.testng.annotations.Listeners;
@Listeners(SupportClasses.TestNG_TestListener.class)

public class CMDC_Test {

	static String LevelToTest = "3"; 
	
	@BeforeClass
	public void beforeClass() {
		Environment.getInstance().setLevel(LevelToTest);
	}

	@Test
	public void Testing_CMDC_CountryDetailRequest() {
		String Response = CMDC_Endpoints.CountryDetailRequest("US");
		Helper_Functions.PrintOut(Response);
	}
	
}
