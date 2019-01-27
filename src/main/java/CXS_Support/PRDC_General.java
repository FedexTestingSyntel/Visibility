package CXS_Support;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import Data_Structures.User_Data;
import SupportClasses.Environment;
import SupportClasses.Helper_Functions;

public class PRDC_General {

	static String LevelsToTest = "3"; //Can but updated to test multiple levels at once if needed. Setting to "23" will test both level 2 and level 3.

	@BeforeClass
	public void beforeClass() {
		Environment.SetLevelsToTest(LevelsToTest);
	}
	
	@DataProvider (parallel = true)
	public Iterator<Object[]> dp(Method m) {
	    List<Object[]> data = new ArrayList<>();
	    
	    for (int i = 0; i < Environment.LevelsToTest.length(); i++) {
	    	String strLevel = "" + Environment.LevelsToTest.charAt(i);
	    	int intLevel = Integer.parseInt(strLevel);
	    	PRDC_Data PRDC_D = PRDC_Data.LoadVariables(strLevel);
	    	USRC_Data USRC_D = USRC_Data.LoadVariables(strLevel);
	    	
			switch (m.getName()) { //Based on the method that is being called the array list will be populated.
				case "Check_WCRV_Access":
					User_Data UD[] =  Environment.Get_UserIds(intLevel);
					for (int k = 0; k < UD.length; k++) {
						data.add(new Object[] {strLevel, USRC_D, PRDC_D, UD[k].SSO_LOGIN_DESC, UD[k].USER_PASSWORD_DESC});
					}
					break;
			}//end switch MethodName
		}
		return data.iterator();
	}

	@Test (dataProvider = "dp")
	public void Check_WCRV_Access(String Level, USRC_Data USRC_Details, PRDC_Data PRDC_Details, String UserID, String Password) {
		String Cookies = null, fdx_login_fcl_uuid[] = null;
		//get the cookies and the uuid of the user
		fdx_login_fcl_uuid = USRC_API_Endpoints.Login(USRC_Details.LoginUserURL, UserID, Password);
		
		String WCRV_Access_List[][] = {{"UUID_NBR", ""},//index 0 and set below
				{"SSO_LOGIN_DESC", UserID.replaceAll(" ", "")},
				{"USER_PASSWORD_DESC", Password.replaceAll(" ", "")},
				{"WCRV_ENABLED", ""}};//index 3 and set below
				
		if (fdx_login_fcl_uuid != null){
			Cookies = fdx_login_fcl_uuid[0];
			//save the uuid
			WCRV_Access_List[0][1] =  fdx_login_fcl_uuid[1]; 
			String AccountDetails = PRDC_API_Endpoints.Accounts_Call(PRDC_Details.AccountsURL, Cookies);
			
			if (AccountDetails.contains("displayRateSheetFlag\":true") && AccountDetails.contains("discountPricingFlag\":true") && AccountDetails.contains("accountCountryEnabledFlag\":true")){
				WCRV_Access_List[3][1] = "Enabled";
			}else if (!AccountDetails.contains("displayRateSheetFlag")){
				Helper_Functions.PrintOut("Warning, PRDC call did not return premission status.", false);
				Assert.fail();
			}else {
				if (AccountDetails.contains("displayRateSheetFlag\":false")){
					//the user does not have the view rate sheet privilege
					WCRV_Access_List[3][1] += "displayRateSheet_False ";
				}else if (AccountDetails.contains("discountPricingFlag\":false")){
					//no account numbers have a discount applied
					WCRV_Access_List[3][1] += "discountPricingFlag_False ";
				}else if (AccountDetails.contains("accountCountryEnabledFlag\":false")){
					//no account numbers are from a valid country
					WCRV_Access_List[3][1] += "accountCountryEnabledFlag_False";
				}else {
					WCRV_Access_List[3][1] = "N";
				}
			}
			Helper_Functions.PrintOut(UserID + "/" + Password + " -- Status: " + WCRV_Access_List[3][1], false);
			
			String FileName = Helper_Functions.DataDirectory + "\\TestingData.xls";
			Helper_Functions.WriteToExcel(FileName, "L" + Level, WCRV_Access_List, 1);
			
			USRC_API_Endpoints.Logout(USRC_Details.LoginUserURL);
		}
	}
	
}
