package ADMC_Application;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.DataProvider;
import Data_Structures.ADMC_Data;
import Data_Structures.USRC_Data;
import Data_Structures.User_Data;
import SupportClasses.Environment;
import SupportClasses.Helper_Functions;
import USRC_Application.USRC_API_Endpoints;
import org.testng.annotations.Test;

//import org.testng.annotations.Listeners;
//@Listeners(SupportClasses.TestNG_TestListener.class)

public class ADMC_General {

	static String LevelsToTest = "3"; //Can but updated to test multiple levels at once if needed. Setting to "23" will test both level 2 and level 3.

	@BeforeClass
	public void beforeClass() {
		Environment.SetLevelsToTest(LevelsToTest);
	}
	
	@DataProvider //(parallel = true)
	public Iterator<Object[]> dp(Method m) {
	    List<Object[]> data = new ArrayList<>();
	    
	    for (int i = 0; i < Environment.LevelsToTest.length(); i++) {
	    	String strLevel = "" + Environment.LevelsToTest.charAt(i);
	    	int intLevel = Integer.parseInt(strLevel);
	    	
	    	//load the variables per level
			switch (m.getName()) { //Based on the method that is being called the array list will be populated.
			case "CheckRole":
				User_Data User_Info[] = User_Data.Get_UserIds(intLevel);
				for (int k = 0; k < User_Info.length; k++) {
    				data.add(new Object[] {strLevel, User_Info[k].USER_ID, User_Info[k].PASSWORD});
    			}
				break;
			}//end switch MethodName
		}
	    
	    System.out.println(data.size() + " scenarios.");
		return data.iterator();
	}
	
	@Test (dataProvider = "dp", enabled = true)
	public void CheckRole(String Level, String UserID, String Password) {
		ADMC_Data ADMC_D = ADMC_Data.LoadVariables(Level);
    	USRC_Data USRC_D = USRC_Data.LoadVariables(Level);
    	
		String UUID = null, fdx_login_fcl_uuid[] = {"","", ""};
		fdx_login_fcl_uuid = USRC_API_Endpoints.Login(USRC_D.GenericUSRCURL, UserID, Password);
		
		String Role = ADMC_API_Endpoints.RoleAndStatus(ADMC_D.RoleAndStatusURL, fdx_login_fcl_uuid[0]);
		Helper_Functions.PrintOut(Role, false);	
		
		
		Helper_Functions.PrintOut(UserID + "/" + Password + "--" + UUID, false);

	}

}
