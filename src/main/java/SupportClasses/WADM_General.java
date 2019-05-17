package SupportClasses;

import org.testng.annotations.Test;
import Data_Structures.Account_Data;
import Data_Structures.Enrollment_Data;
import Data_Structures.Tax_Data;
import Data_Structures.User_Data;
import org.testng.annotations.BeforeClass;
import org.openqa.selenium.By;
import org.testng.Assert;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Listeners;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import SupportClasses.*;

@Listeners(SupportClasses.TestNG_TestListener.class)

public class WADM_General{
	static String LevelsToTest = "2";  
	static String CountryList[][]; 

	@BeforeClass
	public void beforeClass() {
		Environment.SetLevelsToTest(LevelsToTest);
		CountryList = Environment.getCountryList("US");
		//CountryList = new String[][]{{"JP", "Japan"}, {"MY", "Malaysia"}, {"SG", "Singapore"}, {"AU", "Australia"}, {"NZ", "New Zealand"}, {"HK", "Hong Kong"}, {"TW", "Taiwan"}, {"TH", "Thailand"}};
		//CountryList = new String[][]{{"SG", "Singapore"}, {"AU", "Australia"}, {"NZ", "New Zealand"}, {"HK", "Hong Kong"}};
		//CountryList = Environment.getCountryList("BR");
		//CountryList = Environment.getCountryList("FR");
		//CountryList = Environment.getCountryList("high");
		//Helper_Functions.MyEmail = "accept@fedex.com";
		//CountryList = new String[][]{{"US", ""}, {"CA", ""}};
	}
	
	@DataProvider (parallel = true)
	public static Iterator<Object[]> dp(Method m) {
		List<Object[]> data = new ArrayList<Object[]>();

		for (int i=0; i < Environment.LevelsToTest.length(); i++) {
			String Level = String.valueOf(Environment.LevelsToTest.charAt(i));
			int intLevel = Integer.parseInt(Level);
			switch (m.getName()) { //Based on the method that is being called the array list will be populated.
		    case "WADM_Add_Users":
		    	User_Data User_Info_Array[] = Environment.Get_UserIds(intLevel);
		    	for (int j = 0; j < CountryList.length; j++) {
		    		for (int k = 0; k < User_Info_Array.length; k++) {
		    			if (User_Info_Array[k].Address_Info.Country_Code.contentEquals(CountryList[j][0]) && !User_Info_Array[k].PASSKEY.contentEquals("T")) {
		    				data.add( new Object[] {Level, User_Info_Array[k]});
		    				break;
		    			}
		    		}
				}
		    	break;
			}
		}
		return data.iterator();
	}

	@Test(dataProvider = "dp")
	public void WADM_Add_Users(String Level, User_Data User_Info) {
		try {
			for (int i = 0 ; i < 10; i++) {
				User_Data New_User_Info = new User_Data();
				WADM_Add_User_To_Company(User_Info, New_User_Info);
			}
			
			
		}catch (Exception e) {
			Assert.fail(e.getMessage());
		}
	}
	
	public static String WADM_Add_User_To_Company(User_Data User_Info, User_Data New_User_Info) throws Exception{
 		WebDriver_Functions.ChangeURL("INET", User_Info.Address_Info.Country_Code, true);
 		
    	try{

		}catch (Exception e){
			Helper_Functions.PrintOut("Secret quesiton " + User_Info.SECRET_ANSWER_DESC + " was not accepted.", true);
			return e.getMessage();
		}
		return "";
	}//end WADM_Add_User_To_Company

}