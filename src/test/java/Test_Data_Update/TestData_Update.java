package Test_Data_Update;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import org.junit.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Listeners;
import Data_Structures.User_Data;
import SupportClasses.Environment;
import SupportClasses.Helper_Functions;
import org.testng.annotations.Test;

@Listeners(SupportClasses.TestNG_TestListener.class)

public class TestData_Update {

	//Can but updated to test multiple levels at once if needed. Setting to "23" will test both level 2 and level 3.
	static String LevelsToTest = "2"; 

	@BeforeClass
	public void beforeClass() {
		Environment.SetLevelsToTest(LevelsToTest);
		API_Functions.General_API_Calls.setPrintOutAPICallFlag(false);//  false    true
		API_Functions.General_API_Calls.setPrintOutFullResponseFlag(false);
	}

	@DataProvider (parallel = true)
	public Iterator<Object[]> dp(Method m) {
	    List<Object[]> data = new ArrayList<>();
	    
	    for (int i = 0; i < Environment.LevelsToTest.length(); i++) {
	    	String strLevel = "" + Environment.LevelsToTest.charAt(i);
	    	int intLevel = Integer.parseInt(strLevel);
	    	
	    	//load user ids since both of the below use that value
	    	User_Data User_Info_Array[] = User_Data.Get_UserIds(intLevel, true);
	    	
			switch (m.getName()) { //Based on the method that is being called the array list will be populated.	
			case "Update_Login_Information":
				for (User_Data User_Info: User_Info_Array) {
					if (User_Info.UUID_NBR.contentEquals("") || !User_Info.ERROR.contentEquals("")) {
    					data.add(new Object[] {strLevel, User_Info});
    				} else if (User_Info.EMAIL_ADDRESS == null || User_Info.EMAIL_ADDRESS.contentEquals("")){
    					data.add(new Object[] {strLevel, User_Info});
    				}
					//single specific user.
    				//else if (User_Info.USER_ID.contains("L2ATRKLARGE")){data.add(new Object[] {strLevel, User_Info});}
    				//uncomment if need to run all
    				else{data.add(new Object[] {strLevel, User_Info});}

    			}
				break;
			}//end switch MethodName
		}
	    
	    SupportClasses.Helper_Functions.LimitDataProvider(m.getName(), -1, data);
	    
		return data.iterator();
	}

	@Test (dataProvider = "dp", enabled = true)
	public void Update_Login_Information(String Level, User_Data User_Info) {
		
		try {
			String Cookies = null, fdx_login_fcl_uuid[] = null;

			if (User_Info.PASSWORD == null) {
				User_Info.PASSWORD = "";
			}
			//in case cannot login will check with the generic other passwords
			String GenericPasswords[] = new String[] {User_Info.PASSWORD.replaceAll(" ", ""), "Test1234", "Test12345", "Test123456", "Password1", "Inet2010", "Test1test"};
			for (String TestPassword: GenericPasswords) {
				if (fdx_login_fcl_uuid == null && !TestPassword.contentEquals("")){
					User_Info.PASSWORD = TestPassword;
					fdx_login_fcl_uuid = USRC.login.Login(User_Info.USER_ID, User_Info.PASSWORD);
				}
			}

			//this is the default key position to update the user table. Currently set to 1 for user id value.
			int keyPosition;
			
			String Details[][];
			
			if (fdx_login_fcl_uuid != null){
				Cookies = fdx_login_fcl_uuid[0];
				
				Details = new String[][]{{"ERROR", ""}, //will clear out old error time stamp if failed previously.
						{"UUID_NBR", fdx_login_fcl_uuid[1]},
						{"USER_ID", User_Info.USER_ID}, //set as the key position for making updates below.
						{"PASSWORD", User_Info.PASSWORD}};
				keyPosition = 2;
				
				Details = USRC.USRC_Endpoints.getContact_Details(Details, Cookies);
				
				Details = USRC.USRC_Endpoints.getApp_Role_Info_Check(Details, Cookies);

				Details = USRC.recipient_profile.FDM_Access(Details, Cookies);
				
				Details = TRKC.tracking_profile.getATRK_Profile(Details, Cookies);
				
				Details = ADMC.role_and_status.getRoleAndStatus(Details, Cookies);
				
				Details = ADMC.customviews.getCustomViews(Details, Cookies);
			}else {
				//will save the current time of the failure.
				Details = new String[][]{{"ERROR", Helper_Functions.CurrentDateTime(true)}, {"USER_ID", User_Info.USER_ID}};
				keyPosition = 1;
			}
			
			String FileName = Helper_Functions.DataDirectory + "\\TestingData.xls";
			boolean updatefile = Helper_Functions.WriteToExcel(FileName, "L" + Level, Details, keyPosition);
			//Helper_Functions.PrintOut("Contact Details: " + Arrays.deepToString(Details), true);
			Helper_Functions.PrintOut("Contact Details Attempt: " + User_Info.USER_ID + "  " + Details[0][1] + "  " + Details[1][1], true);
			if (!updatefile) {
				Assert.fail("Not able to update file.");
			}else if (fdx_login_fcl_uuid == null) {
				Assert.fail("Not able to login with " + User_Info.USER_ID);
			}
		}catch (Exception e) {
			e.printStackTrace();
 			Assert.fail(e.getCause().toString());
		}
	}
	
	public String[][] Linkage_Indicator(String Level, String Details[][], String Cookies, String AccountNumber, String AccountKey) {
		String Response = USRC.recipient_profile.RecipientProfile(Cookies);
		Details = Arrays.copyOf(Details, Details.length + 1);
		
		if (Response.contains("recipientProfileEnrollmentStatus\":\"ENROLLED")) {
			Details[Details.length - 1] = new String[] {"FDM_STATUS", Response};//store all of the FDM details
		}else {
			Details[Details.length - 1] = new String[] {"FDM_STATUS", "false"};//not yet enrolled for FDM
		}
		return Details;
	}
	
	public void ExternalVisible(String Level, User_Data User_Info) {
		Update_Login_Information( Level, User_Info);
	}

}