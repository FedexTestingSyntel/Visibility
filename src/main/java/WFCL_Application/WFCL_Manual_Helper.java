package WFCL_Application;

import org.testng.annotations.Test;
import Data_Structures.Account_Data;
import Data_Structures.Enrollment_Data;
import Data_Structures.Tax_Data;
import Data_Structures.User_Data;
import org.testng.annotations.BeforeClass;
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

public class WFCL_Manual_Helper{
	static String LevelsToTest = "3";  
	static String CountryList[][]; 

	@BeforeClass
	public void beforeClass() {
		Environment.SetLevelsToTest(LevelsToTest);
		CountryList = Environment.getCountryList("US");
		//CountryList = new String[][]{{"JP", "Japan"}, {"MY", "Malaysia"}, {"SG", "Singapore"}, {"AU", "Australia"}, {"NZ", "New Zealand"}, {"HK", "Hong Kong"}, {"TW", "Taiwan"}, {"TH", "Thailand"}};
		//CountryList = new String[][]{{"SG", "Singapore"}, {"AU", "Australia"}, {"NZ", "New Zealand"}, {"HK", "Hong Kong"}};
		//CountryList = Environment.getCountryList("BR");
		//CountryList = Environment.getCountryList("JP");
		//CountryList = Environment.getCountryList("high");
		Helper_Functions.MyEmail = "accept@fedex.com";
		//CountryList = new String[][]{{"AU", ""}, {"JP", ""}};
	}
	 
	@DataProvider (parallel = true)
	public static Iterator<Object[]> dp(Method m) {
		List<Object[]> data = new ArrayList<Object[]>();

		for (int i=0; i < Environment.LevelsToTest.length(); i++) {
			String Level = String.valueOf(Environment.LevelsToTest.charAt(i));
			//Account_Data Account_Info = null;
			//int intLevel = Integer.parseInt(Level);
			switch (m.getName()) { //Based on the method that is being called the array list will be populated.
		    	case "AccountRegistration_INET_Test":
		    		/*String Accounts[] = {"642454684", "642167308", "642455400", "797691380", "642454749", "642167405", "642455184", "797691500", "642454765", "642167529", "642455206", "797691640", "642454781", "642167626", "642455222", "797691860", "642454803", "642167723", "642455249", "797692280", "642454820", "642167820", "642455281", "797692700", "642454846", "642167928", "642455303", "797391700", "642454862", "642168142", "642455320"};
		    		for (String A: Accounts) {
		    			data.add( new Object[] {Level, A});
		    		}
		    		*/
		    		
		    		data.add( new Object[] {Level, "642304305"});
		    		data.add( new Object[] {Level, "643112523"});
		    		//data.add( new Object[] {Level, "642260243"});
			    	//data.add( new Object[] {Level, "633504580"});
		    		break;
		    	case "AccountRegistration_FDDT":
		    		String Accounts[] = {"400119775", "400162913", "401143076", "40338185", "404547437", "405245957", "405517965", "405638452"};
		    		for (String A: Accounts) {
		    			data.add( new Object[] {Level, A});
		    		}

		    		break;
		    	case "Link_Account_To_Specific_User":
		    		User_Data User_Info = new User_Data();
					User_Info.SSO_LOGIN_DESC = "L6Acc700232794N032019T104345twxs";
					User_Info.USER_PASSWORD_DESC = "Test1234";
					String AccountNumber = "700195279";
		    		data.add( new Object[] {Level, User_Info, AccountNumber});
		    		AccountNumber = "700194795";
		    		data.add( new Object[] {Level, User_Info, AccountNumber});
		    		break;
			}
		}	
		return data.iterator();
	}

	@Test(dataProvider = "dp", priority = 1, enabled = true)
	public void AccountRegistration_INET_Test(String Level, String Account){
		try {
			Account_Data Account_Info = Account_Lookup.Account_DataAccountDetails(Account, Level, "FX");
			Account_Data.Set_Dummy_Contact_Name(Account_Info);
			Account_Data.Set_UserId(Account_Info, "L" + Level + "Acc" + Account + "N");
			//Account_Info.UserId = "L3WFCLUSERID01";
			//create user id and link to account number.
			Account_Info = WFCL_Functions_UsingData.Account_Linkage(Account_Info);
			//register the userid to INET
			WFCL_Functions_UsingData.INET_Registration(Account_Info);
			String Result[] = new String[] {Account_Info.UserId, Account_Info.Password, Account_Info.Account_Number, Account_Info.UUID};
			Helper_Functions.PrintOut(Arrays.toString(Result), false);
		}catch (Exception e) {
			Assert.fail(e.getMessage());
		}
	}
	
	@Test(dataProvider = "dp", priority = 1, enabled = true)
	public void AccountRegistration_FDDT(String Level, String Account){
		try {
			Account_Data Account_Info = Account_Lookup.Account_DataAccountDetails(Account, Level, "FX");
			Account_Data.Set_Dummy_Contact_Name(Account_Info);
			Account_Data.Set_UserId(Account_Info, "L" + Level + "Acc" + Account + "N");
			Account_Info.UserId = "L3WFCLUSERID05";
			//create user id and link to account number.
		//WFCL_Functions_UsingData.WFCL_UserRegistration(Account_Info); 
			//register the userid to INET
			WFCL_Functions_UsingData.Account_Linkage_FDDT(Account_Info);
			String Result[] = new String[] {Account_Info.UserId, Account_Info.Password, Account_Info.Account_Number, Account_Info.UUID};
			Helper_Functions.PrintOut(Arrays.toString(Result), false);
		}catch (Exception e) {
			Assert.fail(e.getMessage());
		}
	}
	
	@Test(dataProvider = "dp", priority = 1, enabled = true)
	public void Link_Account_To_Specific_User(String Level, User_Data User_Info, String Account_Number){
		try {
			Account_Data Account_Info = Account_Lookup.Account_DataAccountDetails(Account_Number, Level, "FX");
			String Result[] = WFCL_Functions_UsingData.Account_Linkage(User_Info, Account_Info);
			Helper_Functions.PrintOut(Arrays.toString(Result), false);
		}catch (Exception e) {
			Assert.fail(e.getMessage());
		}
	}
	

}