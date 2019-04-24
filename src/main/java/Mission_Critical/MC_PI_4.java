package Mission_Critical;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import org.testng.annotations.Test;
import Data_Structures.Account_Data;
import Data_Structures.User_Data;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Listeners;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import java.util.Iterator;
import java.util.List;
import SupportClasses.Environment;
import SupportClasses.Helper_Functions;
import WFCL_Application.WFCL_Functions_UsingData;

@Listeners(SupportClasses.TestNG_TestListener.class)

public class MC_PI_4{
	static String LevelsToTest = "3";
	static String CountryList[][];
	final boolean EnableCompleted = false;
	
	@BeforeClass
	public void beforeClass() {
		Environment.SetLevelsToTest(LevelsToTest);
		CountryList = Environment.getCountryList("smoke");
	}
	
	@DataProvider// (parallel = true)
	public static Iterator<Object[]> dp(Method m) {
		List<Object[]> data = new ArrayList<Object[]>();

		for (int i=0; i < Environment.LevelsToTest.length(); i++) {
			String Level = String.valueOf(Environment.LevelsToTest.charAt(i));
			int intLevel = Integer.parseInt(Level);
			String Rewards_APAC_AND_LAC[] = new String[] {"au", "cn", "hk", "jp", "my", "nz", "ph", "sg", "kr", "tw", "th", "br", "mx"};
			if ("23".contains(Level)) {
				Rewards_APAC_AND_LAC = new String[] {"jp"};//only Japan loaded in L2 and L3
			}
			
			switch (m.getName()) { //Based on the method that is being called the array list will be populated.
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
					User_Data UD[] = Environment.Get_UserIds(intLevel);
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
	@Test(dataProvider = "dp", description = "483863", enabled = true)
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
}