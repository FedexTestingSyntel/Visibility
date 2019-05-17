package IPAS_Application;

import java.lang.reflect.Method;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Listeners;
import org.testng.annotations.Test;
import Data_Structures.User_Data;
import SupportClasses.Environment;
import SupportClasses.Helper_Functions;

@Listeners(SupportClasses.TestNG_TestListener.class)

public class IPAS_Invitation {

	static String LevelsToTest = "7";  

	@BeforeClass
	public void beforeClass() {
		Environment.SetLevelsToTest(LevelsToTest);
		//Helper_Functions.MyEmail = "";//This is the email that the invites will be sent to.
	}
	 
	@DataProvider (parallel = true)
	public static Iterator<Object[]> dp(Method m) {
		List<Object[]> data = new ArrayList<Object[]>();
		
		for (int i=0; i < Environment.LevelsToTest.length(); i++) {
			String Level = String.valueOf(Environment.LevelsToTest.charAt(i));
			int intLevel = Integer.parseInt(Level);
			User_Data User_Info_Array[] = Environment.Get_UserIds(intLevel);
			switch (m.getName()) { //Based on the method that is being called the array list will be populated.
		    	case "IPAS_Invitation_Attempt":
		    		for (User_Data User_Info: User_Info_Array) {
			    		if (User_Info.MIGRATION_STATUS.contains("IPAS") && User_Info.USER_TYPE.contains("COMPANY_ADMIN")) {
			    			data.add( new Object[] {Level, User_Info, "REGULAR"});
			    			data.add( new Object[] {Level, User_Info, "DEPARTMENT"});
			    			data.add( new Object[] {Level, User_Info, "COMPANY"});
			    			break;
			    		}
		    		}
		    	break;
			}
		}	
		return data.iterator();
	}

	@Test(dataProvider = "dp")
	public void IPAS_Invitation_Attempt(String Level, User_Data User_Info, String Role) {
		try {
			User_Data User_Info_Invited = new User_Data();
			User_Info_Invited.EMAIL_ADDRESS = Helper_Functions.MyEmail;

			String Result[] = IPAS_Functions.Invite_User(User_Info, User_Info_Invited, Role);
			Helper_Functions.PrintOut(Arrays.toString(Result), false);
		}catch (Exception e) {
			Assert.fail(e.getMessage());
		}
	}
	
}
