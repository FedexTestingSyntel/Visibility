package WCRV_Application;

import org.testng.annotations.Test;

import Data_Structures.User_Data;
import SupportClasses.Environment;
import SupportClasses.Helper_Functions;
import org.testng.annotations.BeforeClass;
import org.testng.Assert;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Listeners;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

@Listeners(SupportClasses.TestNG_TestListener.class)

public class WCRV{
	static String LevelsToTest = "7";
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
			int intLevel = Integer.parseInt(Level);
			//int intLevel = Integer.parseInt(Level);
			switch (m.getName()) { //Based on the method that is being called the array list will be populated.
				case "WCRV_CheckPermission":
					User_Data UD[] = Environment.Get_UserIds(intLevel);
					for (int j = 0; j < CountryList.length; j++) {
		    			for (int k = 0; k < UD.length; k++) {
		    				if (UD[k].WCRV_ENABLED.contains("Enabled")) {
		    					data.add( new Object[] {Level, CountryList[j][0], UD[k].SSO_LOGIN_DESC, UD[k].USER_PASSWORD_DESC});
		    				}
		    			}
		    		}
					break;
		    	case "WCRV_Generate_RateSheet":
		    		UD = Environment.Get_UserIds(intLevel);
		    		for (int j = 0; j < CountryList.length; j++) {
		    			for (int k = 0; k < UD.length; k++) {
		    				if (UD[k].WCRV_ENABLED.contains("Enabled")) {
		    					data.add( new Object[] {Level, CountryList[j][0], UD[k].SSO_LOGIN_DESC, UD[k].USER_PASSWORD_DESC, "INTRA_COUNTRY", 1});
		    					data.add( new Object[] {Level, CountryList[j][0], UD[k].SSO_LOGIN_DESC, UD[k].USER_PASSWORD_DESC, "EXPORT", 1});
		    					break;
		    				}
		    			}
		    		}
		    		
/*
					if (intLevel == 7 ) { 
						data.add( new Object[] {Level, "US", "BMProdIPAS", "Test1234", "EXPORT", 1}); 
						data.add( new Object[] {Level, "US", "CONonAdmOwn", "Test1234", "EXPORT", 1}); 
						data.add( new Object[] {Level, "US", "WADMProDE", "Test1234", "EXPORT", 1}); 
						//data.add( new Object[] {Level, "US", "WADMProDEStandard", "Test1234", "EXPORT", 1}); 
						//data.add( new Object[] {Level, "US", "FRProdWADM", "Test1234", "EXPORT", 1}); 
						data.add( new Object[] {Level, "US", "FRProdIPAS", "Test1234", "EXPORT", 1}); 
						//data.add( new Object[] {Level, "US", "ESNonAdmNonOwnInvited", "Test1234", "EXPORT", 1}); 
						data.add( new Object[] {Level, "US", "WADMProEEStandard", "Test1234", "EXPORT", 1}); 
						data.add( new Object[] {Level, "US", "ProdUsandBrWADM", "Test1234", "INTRA_COUNTRY", 1}); 
						data.add( new Object[] {Level, "US", "ProdUsandDeWADM", "Test1234", "INTRA_COUNTRY", 1}); 
						data.add( new Object[] {Level, "US", "BRProdAdm", "Test1234", "EXPORT", 1}); 
						data.add( new Object[] {Level, "US", "BRProdAdmNoPer", "Test1234", "EXPORT", 1}); 
						data.add( new Object[] {Level, "US", "BRProdWADM", "Test1234", "EXPORT", 1}); 
						data.add( new Object[] {Level, "US", "BRProdStd", "Test1234", "EXPORT", 1}); 
						data.add( new Object[] {Level, "US", "BRProdInvtY", "Test1234", "EXPORT", 1}); 
						data.add( new Object[] {Level, "US", "BRProdInNo", "Test1234", "EXPORT", 1}); 
						data.add( new Object[] {Level, "US", "DOPRODPRICE1", "Test1234", "EXPORT", 1}); 
						//data.add( new Object[] {Level, "US", "DOPRODPRICEInv", "Test1234", "EXPORT", 1}); 
						//data.add( new Object[] {Level, "US", "DOProdIPAS", "Test1234", "EXPORT", 1}); 
						data.add( new Object[] {Level, "US", "Prod809304124", "Test1234", "EXPORT", 1}); 
						data.add( new Object[] {Level, "US", "AEProdAdm", "Test1234", "EXPORT", 1}); 
						data.add( new Object[] {Level, "US", "AEProdOwner", "Test1234", "EXPORT", 1}); 
						data.add( new Object[] {Level, "US", "ProductionMeisaTesting", "Test1234", "EXPORT", 1}); 
						data.add( new Object[] {Level, "US", "ProductionMeisaTesting", "Test1234", "EXPORT", 1}); 
						data.add( new Object[] {Level, "US", "ProductionMeisaTesting", "Test1234", "EXPORT", 1}); 
					}else if (intLevel == 3 ) {
			    		data.add( new Object[] {Level, "US", "L3GtcAdmin", "Test1234", "INTRA_COUNTRY", 1});
						data.add( new Object[] {Level, "US", "L3GtcAdmin", "Test1234", "EXPORT", 1});
						data.add( new Object[] {Level, "US", "L3UsNonAdminOwner", "Test1234", "INTRA_COUNTRY", 1});
						data.add( new Object[] {Level, "US", "L3UsNonAdminOwner", "Test1234", "EXPORT", 1});
						data.add( new Object[] {Level, "US", "L3AdminCA", "Test1234", "INTRA_COUNTRY", 1});
						data.add( new Object[] {Level, "US", "L3WCRV609188405", "Test1234", "EXPORT", 1});
						data.add( new Object[] {Level, "US", "L3CNRATENA", "Test1234", "INTRA_COUNTRY", 1});
						data.add( new Object[] {Level, "US", "L3COratDIS", "Test1234", "EXPORT", 1});
						data.add( new Object[] {Level, "US", "L3DERATENA1", "Test1234", "INTRA_COUNTRY", 1});
					}
					*/
		    	break;
			}
		}	
		return data.iterator();
	}

	@Test(dataProvider = "dp")
	public void WCRV_Generate_RateSheet(String Level, String CountryCode, String UserId, String Password, String Service, int ServiceCount) {
		try {
			String Result[] = WCRV_Functions.WCRV_Generate(CountryCode, UserId, Password, Service, ServiceCount);
			Helper_Functions.PrintOut(Arrays.toString(Result), false);
		}catch (Exception e) {
			Assert.fail(e.getMessage());
		}
	}
	
	@Test(dataProvider = "dp")
	public void WCRV_CheckPermission(String Level, String CountryCode, String UserId, String Password) {
		try {
			boolean access = WCRV_Functions.WCRV_CheckPermission(CountryCode, UserId, Password);
			if (access){
				Helper_Functions.PrintOut(UserId + " has access to WCRV.", false);
			}else {
				throw new Exception("User does not have access.");
			}
		}catch (Exception e) {
			Assert.fail(e.getMessage());
		}
	}
	/*
	@Test
	public void WCRV_Generate_Admin_International(){
		String Result[] = null;
		try {
			Result = WCRV_Generate("US", strWCRVAdmin, strPassword, "notintra");
		}catch (Exception e) {
			Assert.fail();
		}
		PassOrFail = true;
		ResultsList.set(TestNumber, UpdateArrayList(ResultsList.get(TestNumber), 1, Arrays.toString(Result)));
	}
	
	@Test
	public void WCRV_Generate_NonAdmin_Domestic(){
		String Result[] = null;
		try {
			Result = WCRV_Generate("US", strWCRVnonAdmin, strPassword, "intra");
		}catch (Exception e) {
			Assert.fail();
		}
		PassOrFail = true;
		ResultsList.set(TestNumber, UpdateArrayList(ResultsList.get(TestNumber), 1, Arrays.toString(Result)));
	}
	
	@Test
	public void WCRV_Generate_NonAdmin_International(){
		String Result[] = null;
		try {
			Result = WCRV_Generate("US", strWCRVnonAdmin, strPassword, "notintra");
		}catch (Exception e) {
			Assert.fail();
		}
		PassOrFail = true;
		ResultsList.set(TestNumber, UpdateArrayList(ResultsList.get(TestNumber), 1, Arrays.toString(Result)));
	}
	*/
}
