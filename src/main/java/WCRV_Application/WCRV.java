package WCRV_Application;

import org.testng.annotations.Test;
import Data_Structures.User_Data;
import SupportClasses.Environment;
import SupportClasses.Helper_Functions;
import SupportClasses.WebDriver_Functions;

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
	static String LevelsToTest = "3";
	static String CountryList[][];

	@BeforeClass
	public void beforeClass() {
		Environment.SetLevelsToTest(LevelsToTest);
		CountryList = Environment.getCountryList("smoke");
		//CountryList = Environment.getCountryList("full");
		//CountryList = Environment.getCountryList("high");
		//CountryList = new String[][]{{"US", "United States"},{"AU", "Australia"},{"CA", "Canada"},{"GB", "United Kingdom"},{"BR", "Brazil"},{"AE", "United Arab Emirates"}};

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
		    				if (UD[k].WCRV_ENABLED.contains("T") && UD[k].COUNTRY_CD.contentEquals(CountryList[j][0])) {
		    					data.add( new Object[] {Level, CountryList[j][0], UD[k].SSO_LOGIN_DESC, UD[k].USER_PASSWORD_DESC});
		    				}
		    			}
		    		}
					break;
		    	case "WCRV_Generate_RateSheet":
		    		UD = Environment.Get_UserIds(intLevel);
		    		for (int j = 0; j < CountryList.length; j++) {
		    			for (int k = 0; k < UD.length; k++) {
		    				if (UD[k].WCRV_ENABLED.contains("T") && UD[k].COUNTRY_CD.contains(CountryList[j][0]) && !UD[k].EMAIL_ADDRESS.contentEquals(Helper_Functions.MyEmail)) {
		    					data.add( new Object[] {Level, CountryList[j][0], UD[k].SSO_LOGIN_DESC, UD[k].USER_PASSWORD_DESC, "INTRA_COUNTRY", 1});
		    					data.add( new Object[] {Level, CountryList[j][0], UD[k].SSO_LOGIN_DESC, UD[k].USER_PASSWORD_DESC, "EXPORT", 1});
		    					data.add( new Object[] {Level, CountryList[j][0], UD[k].SSO_LOGIN_DESC, UD[k].USER_PASSWORD_DESC, "IMPORT", 1});
		    					data.add( new Object[] {Level, CountryList[j][0], UD[k].SSO_LOGIN_DESC, UD[k].USER_PASSWORD_DESC, "THIRD_PARTY", 1});
		    					data.add( new Object[] {Level, CountryList[j][0], UD[k].SSO_LOGIN_DESC, UD[k].USER_PASSWORD_DESC, "ANY", 4});
		    					break;
		    				}
		    			}
		    		}
		    	break;
		    	case "WCRV_Help_Link":
		    		UD = Environment.Get_UserIds(intLevel);
		    		for (int k = 0; k < UD.length; k++) {
		    			if (UD[k].WCRV_ENABLED.contains("T")) {
		    				data.add( new Object[] {Level, UD[k].SSO_LOGIN_DESC, UD[k].USER_PASSWORD_DESC});
		    				break;
		    			}
		    		}
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
	public void WCRV_Help_Link(String Level, String UserId, String Password) {
		try {
			String Working = "", NotWorking = "", NotEnabled = "";
			WebDriver_Functions.Login(UserId, Password);
			
    		for (int j = 0; j < CountryList.length; j++) {
    	 		//updated on March 2019 after apac being added
    	 		String ECRV_ENABLED_COUNTRIES_LOCALE= "es_AR,en_AR,en_AW,de_AT,en_AT,en_BS,en_BB,en_BM,en_BQ,pt_BR,en_BR,en_VG,en_KY,es_CL,en_CL,es_CO,en_CO,en_CW,da_DK,en_DK,es_DO,en_DO,en_EE,en_FI,fi_FI,de_DE,en_DE,en_GD,fr_GP,en_GP,it_IT,en_IT,en_JM,en_LV,en_LT,fr_MQ,en_MQ,en_NO,no_NO,en_PL,pl_PL,es_ES,en_ES,en_KN,en_LC,en_SX,en_VC,sv_SE,en_SE,en_TT,en_TC,es_UY,en_UY,en_VI,es_VE,en_VE,en_BE,fr_BE,nl_BE,es_CR,en_CR,cs_CZ,en_CZ,fr_FR,en_FR,es_GT,en_GT,en_HU,hu_HU,en_LU,nl_NL,en_NL,es_PA,en_PA,sl_SI,en_SI,en_CH,de_CH,fr_CH,it_CH,fr_CA,en_CA,en_IE,en_GB,en_MX,es_MX,en_BH,en_IN,en_KW,en_AE,ar_AE,en_BW,en_MW,en_MZ,en_NA,en_ZA,en_SZ,en_ZM,en_US,es_US,en_PR,es_PR,en_AU,zh_CN,en_CN,en_GU,tc_HK,en_HK,zh_HK,ja_JP,en_JP,en_MO,en_MY,en_NZ,en_PH,en_SG,ko_KR,en_KR,tc_TW,en_TW,zh_TW,en_TH,th_TH,en_VN,en_ID,";
    	 		if (ECRV_ENABLED_COUNTRIES_LOCALE.contains(CountryList[j][0] + ",")) {
        			boolean Help_Present = WCRV_Functions.WCRV_Check_Help_Links(CountryList[j][0], UserId, Password);
        			if (Help_Present) {
        				Working += CountryList[j][0] + ", ";
        				Helper_Functions.PrintOut(CountryList[j][0] + " Working", false);
        			}else {
        				NotWorking += CountryList[j][0] + ", ";
        				Helper_Functions.PrintOut(CountryList[j][0] + " help page not loading correctly", false);
        			}
    	 		}else {
    	 			NotEnabled+= CountryList[j][0] + ", ";
    	 		}

    		}
			
    		Helper_Functions.PrintOut("   Countries with working help pages: " + Working, false);
    		Helper_Functions.PrintOut("   Countries with incorrect help pages: " + NotWorking, false);
    		Helper_Functions.PrintOut("   Countries skipped as not enabled for WCRV: " + NotEnabled, false);
		}catch (Exception e) {
			Assert.fail(e.getMessage());
		}
	}
	
	@Test(dataProvider = "dp", enabled = false)
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

}
