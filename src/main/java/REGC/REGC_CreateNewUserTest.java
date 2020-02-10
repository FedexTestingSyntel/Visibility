package REGC;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.DataProvider;
import Data_Structures.User_Data;
import MFAC_Application.MFAC_Data;
import MFAC_Application.MFAC_Endpoints;
import MFAC_Application.MFAC_Helper_Functions;
import SupportClasses.Environment;
import SupportClasses.Helper_Functions;
import org.testng.annotations.Listeners;
import org.testng.annotations.Test;
import static org.junit.Assert.assertThat;
import static org.hamcrest.CoreMatchers.containsString;

@Listeners(SupportClasses.TestNG_TestListener.class)

public class REGC_CreateNewUserTest {
 
	static String LevelsToTest = "3"; //Can but updated to test multiple levels at once if needed. Setting to "23" will test both level 2 and level 3.

	@BeforeClass
	public void beforeClass() {
		Environment.SetLevelsToTest(LevelsToTest);
		API_Functions.General_API_Calls.setPrintOutAPICallFlag(true);
		API_Functions.General_API_Calls.setPrintOutFullResponseFlag(true);
	}
	
	@DataProvider (parallel = true)
	public Iterator<Object[]> dp(Method m) {
	    List<Object[]> data = new ArrayList<>();
	    
	    for (int i = 0; i < Environment.LevelsToTest.length(); i++) {
	    	String strLevel = String.valueOf(Environment.LevelsToTest.charAt(i));
			int intLevel = Integer.parseInt(strLevel);
			Environment.getInstance().setLevel(strLevel);
			User_Data userInfoArray[];
			userInfoArray = User_Data.Get_UserIds(intLevel);
			
			switch (m.getName()) { //Based on the method that is being called the array list will be populated.
			case "CreateNewUsers":
				
				User_Data User_Info = null; // will be loaded with user details of an already FDM enrolled user.
				for (User_Data User : userInfoArray) {
					if (User.getFDMregisteredAddress() != null) {
						User.PASSWORD = SupportClasses.Helper_Functions.myPassword; // Test1234 or generic password listed there.
						User_Info = User;
						break; // break out of the userInfoArray loop
					}
				}
				
				String ValidEmails[] = new String[] {"emmarosy.minj.osv@fedex.com", "1234567890123456789-123456789(1234567890123456789)12345678901234@6789.asdfasdfas", "a@3456789.aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa", "a@b.c"};
				for (String Email: ValidEmails) {
					User_Data TempUser = User_Info;
					TempUser.USER_ID = Helper_Functions.LoadUserID("L" + strLevel + "WERLValid");
					TempUser.EMAIL_ADDRESS = Email;
					data.add(new Object[] {strLevel, TempUser});
				}
				
				String InValidEmails[] = new String[] {"emmarosy.minj.osv@fe@dex.com", ".emmarosy.minj.osv@fe@dex.com", "emmaro%sy.minj.osv@fedex.com", "!accept.@fedex.com", "accept.@fedex.com", "accept@.fedex.com", "12345678901234567890123456789)123456789(1234567890123456789012345@b.c", "a@3456789.aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaab", "a@b.c?", "a@3456789.a1b", "a@3456789.a!b"};
				for (String Email: InValidEmails) {
					User_Data TempUser = User_Info;
					TempUser.USER_ID = Helper_Functions.LoadUserID("L" + strLevel + "WERLInvalid");
					TempUser.EMAIL_ADDRESS = Email;
					data.add(new Object[] {strLevel, TempUser});
				}
				break;
			}//end switch MethodName
		}
	    
	    SupportClasses.Helper_Functions.LimitDataProvider(m.getName(), -1, data);
		return data.iterator();
	}

	@Test (dataProvider = "dp", enabled = true)
	public void CreateNewUsers(String Level, String UserID, String Password, String[] ContactDetails) {

		try {
			String Response = "";

			//create the new user
			Response = REGC.create_new_user.NewFCLUser(ContactDetails, UserID, Password);
			
			//check to make sure that the userid was created.
			assertThat(Response, containsString("successful\":true"));

		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
