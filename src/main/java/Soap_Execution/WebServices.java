package Soap_Execution;

import static org.junit.Assert.assertThat;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import org.hamcrest.CoreMatchers;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;
import Data_Structures.Account_Data;
import SupportClasses.Environment;

public class WebServices {
	//will parse this string and run all the levels listed in the data provider.
	static String LevelsToTest = "3";	
	
	@BeforeClass
	public void beforeClass() {
		Environment.SetLevelsToTest(LevelsToTest);
	}
	
	@DataProvider //(parallel = true)
	public static Iterator<Object[]> dp (Method m) {
		List<Object[]> data = new ArrayList<Object[]>();
		for (int i = 0; i < Environment.LevelsToTest.length(); i++) {
			String Level = String.valueOf(Environment.LevelsToTest.charAt(i));
				switch (m.getName()) { //Based on the method that is being called the array list will be populated.
				case "WebServices_CreateMeterNumber":
					Account_Data D[] = Environment.getAccountDetails(Level);
					String URL = "https://wsdrt.idev.fedex.com/web-services";
					for (Account_Data Account_Info: D) {
						//Account_Data Account_Info = Helper_Functions.getFreshAccount(Level, "US");
						if (Account_Info != null && Account_Info.Level.contentEquals("3")) {
							data.add( new Object[] {Level, Account_Info, URL});
						}
					}
					break;
				}
		}
		System.out.println("Starting " + m.getName() + " : There are " + data.size() + " scenarios.");
		return data.iterator();
	}
	
	@Test(dataProvider = "dp", priority = 1)
	public void WebServices_CreateMeterNumber(String Level, Account_Data Account_Info, String URL) throws Exception {
		Account_Data.Set_Dummy_Contact_Name(Account_Info);
		Account_Info.Company_Name = "DummyCompany";
		String Response = WebService_Endpoints.CreateMeterNumber(URL, Account_Info);
		
        String Response_Variables[] = new String[] {"<HighestSeverity>SUCCESS</HighestSeverity>", "MeterNumber", "GroundShipperNumber"};
        for (String Variable: Response_Variables) {
			assertThat(Response, CoreMatchers.containsString(Variable));
		}
	}
}
