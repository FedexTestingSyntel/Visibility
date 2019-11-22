package USRC;

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

public class USRC_FDM {
 
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
	    	USRC_Data USRC_D = USRC_Data.USRC_Load();
	    	MFAC_Data MFAC_D = MFAC_Data.LoadVariables();
	    	
			switch (m.getName()) { //Based on the method that is being called the array list will be populated.
			case "EndtoEndEnrollment":
				for (int j = 0 ; j < 10; j++) {
					String UserID = Helper_Functions.LoadUserID("L" + strLevel + "ATRK");
					String Password = "Test1234";
					String ContactDetails[] = USRC_Data.getContactDetails(j);
					
					data.add(new Object[] {strLevel, USRC_D.FDMPostcard_PinType, MFAC_D.OrgPostcard, UserID, Password, ContactDetails});
				}
				break;
			case "CreateNewUsers":
				for (int j = 0 ; j < 1; j++) {
					//String UserID = Helper_Functions.LoadUserID("L" + strLevel + "ATRK");
					// Warning, may not be unique so could be some failures. Updated to make smaller user id
					String UserID = "L" + strLevel + "ATRK" + Helper_Functions.getRandomString(5);
					UserID = "L3ATRKExceptions";
					String Password = "Test1234";
					String ContactDetails[] = USRC_Data.getContactDetails(j);
					
					data.add(new Object[] {strLevel, UserID, Password, ContactDetails});
				}
				break;
				/*
			case "EndtoEndEnrollment_EmailaAsUserId":
				for (int j = 0 ; j < 1; j++) {
					String UserID = Helper_Functions.getRandomEmail("L" + strLevel + "FDM", 30, 30);
					String Password = "Test1234";
					String ContactDetails[] = USRC_Data.getContactDetails(j);
					data.add(new Object[] {strLevel, USRC_D, USRC_D.FDMPostcard_PinType, MFAC_D, MFAC_D.OrgPostcard, UserID, Password, ContactDetails});
				}
				break;
				*/
			case "EndtoEndEnrollment_UserID":
				User_Data User_Info_Array[] = User_Data.Get_UserIds(intLevel);
				//data.add(new Object[] {USRC_D, USRC_D.FDMPostcard_PinType, MFAC_D, MFAC_D.OrgPostcard, "L2FDM012919T124302pm", "Test1234"});
				for (User_Data User_Info: User_Info_Array){
/*	    			if (User_Info.FDM_STATUS.contentEquals("false") 
	    					&& User_Info.getHasValidAccountNumber() 
	    					&& User_Info.getCanScheduleShipment()) {
	    				data.add(new Object[] {strLevel, USRC_D.FDMPostcard_PinType, MFAC_D, MFAC_D.OrgPostcard, User_Info.USER_ID, User_Info.PASSWORD});
	    				if (data.size() > 1) {break;}
	    			}*/
					
	    			if (User_Info.USER_ID.contentEquals("L3642210386US112018T225716ym")) {
	    				data.add(new Object[] {strLevel, USRC_D.FDMPostcard_PinType, MFAC_D, MFAC_D.OrgPostcard, User_Info.USER_ID, User_Info.PASSWORD});
	    				if (data.size() > 1) {break;}
	    			}
	    		}
				break;
			case "EndtoEndEnrollment_EmailaAsUserId":
				String Special = ".-+_=/?^'&{}()";
				for (int j = 0 ; j < Special.length(); j++) {
					String UserID = "Test" + Helper_Functions.getRandomString(10) + Special.charAt(j) + "d@accept.com";
					String Password = "Test1234";
					String ContactDetails[] = USRC_Data.getContactDetails(j);
					ContactDetails[4] = UserID;
					data.add(new Object[] {strLevel, USRC_D.FDMPostcard_PinType, MFAC_D, MFAC_D.OrgPostcard, UserID, Password, ContactDetails});
				}
				break;
			}//end switch MethodName
		}
	    
	    SupportClasses.Helper_Functions.LimitDataProvider(m.getName(), -1, data);
		return data.iterator();
	}
	
	@Test (dataProvider = "dp", priority = 1, description = "380527", enabled = true)
	public void EndtoEndEnrollment(String Level, String USRC_Org, String MFAC_Org, String UserID, String Password, String[] ContactDetails) {
		String Cookie = null;
		String UUID = null;
		String fdx_login_fcl_uuid[] = {"","", ""};
		boolean UserCreated = false;
		try {
			String Response = "";

			//create the new user
			Response = REGC.create_new_user.NewFCLUser(ContactDetails, UserID, Password);
			
			//check to make sure that the userid was created.
			assertThat(Response, containsString("successful\":true"));
			UserCreated = true;
			
			//get the cookies and the uuid of the new user
			fdx_login_fcl_uuid = login.Login(UserID, Password);
			Cookie = fdx_login_fcl_uuid[0];
			UUID = fdx_login_fcl_uuid[1];
				
			//2 - do the enrollment call. Note that the enrollment call will store the ShareID
			Helper_Functions.PrintOut("Enrollment call", false);
			Response = fdm_enrollment.Enrollment(ContactDetails, Cookie);

			assertThat(Response, containsString("enrollmentOptionsList"));
			
			//3 - request a pin
			Helper_Functions.PrintOut("Request pin through USRC", false);
			String ShareID = ParseShareID(Response);
			Response = USRC_Endpoints.CreatePin(Cookie, ShareID, USRC_Org);
			assertThat(Response, containsString("successful\":true"));
		
			//4 - request a pin through MFAC as cannot see the pin generated from the above
			Helper_Functions.PrintOut("Requesting pin through MFAC", false);
			String UserName = UUID + "-" + ShareID;
			Response = MFAC_Endpoints.IssuePinAPI(UserName, MFAC_Org);
			//get the pin from the MFAC call
			String Pin = MFAC_Helper_Functions.ParsePIN(Response);
			
			//5 - enroll the above pin through USRC
			Helper_Functions.PrintOut("Verify pin through USRC", true);
			Response = USRC_Endpoints.VerifyPin(Cookie, ShareID, Pin, USRC_Org);
			assertThat(Response, containsString("responseMessage\":\"Success"));

			//6 - Verify the above enrollment completed succsessfully.
			Helper_Functions.PrintOut("Check recipient profile for new FDM user through USRC", false);
			Response = recipient_profile.RecipientProfile(Cookie);
			
			String Results[] = new String[] {UserID, Password, fdx_login_fcl_uuid[1], USRC_Org};
			Helper_Functions.PrintOut(Arrays.toString(Results), false);

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (UserCreated) {
				User_Data UD = new User_Data();
				UD.USER_ID = UserID;
				UD.PASSWORD = Password;
				UD.writeUserToExcel();
			}
		}
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
	
	@Test (dataProvider = "dp", priority = 1, description = "380527", enabled = false)
	public void EndtoEndEnrollment_EmailaAsUserId(String Level, String USRC_Org, MFAC_Data MFAC_Details, String MFAC_Org, String UserID, String Password, String[] ContactDetails) {
		String Cookie = null, UUID = null, fdx_login_fcl_uuid[] = {"","", ""};
		try {
			String Response = "";
			
			//create the new user
			Response = REGC.create_new_user.NewFCLUser(ContactDetails, UserID, Password);
			
			//check to make sure that the userid was created.
			assertThat(Response, containsString("successful\":true"));
			
			//get the cookies and the uuid of the new user
			fdx_login_fcl_uuid = login.Login(UserID, Password);
			Cookie = fdx_login_fcl_uuid[0];
			UUID = fdx_login_fcl_uuid[1];
				
			//2 - do the enrollment call. Note that the enrollment call will store the ShareID
			Helper_Functions.PrintOut("Enrollment call", false);
			Response = fdm_enrollment.Enrollment(ContactDetails, Cookie);

			assertThat(Response, containsString("enrollmentOptionsList"));
			
			//3 - request a pin
			Helper_Functions.PrintOut("Request pin through USRC", false);
			String ShareID = ParseShareID(Response);
			Response = USRC_Endpoints.CreatePin(Cookie, ShareID, USRC_Org);
			assertThat(Response, containsString("successful\":true"));
		
			//4 - request a pin through MFAC as cannot see the pin generated from the above
			Helper_Functions.PrintOut("Requesting pin through MFAC", false);
			String UserName = UUID + "-" + ShareID;
			Response = MFAC_Endpoints.IssuePinAPI(UserName, MFAC_Org);
			//get the pin from the MFAC call
			String Pin = MFAC_Helper_Functions.ParsePIN(Response);
			
			//5 - enroll the above pin through USRC
			Helper_Functions.PrintOut("Verify pin through USRC", true);
			Response = USRC_Endpoints.VerifyPin(Cookie, ShareID, Pin, USRC_Org);
			assertThat(Response, containsString("responseMessage\":\"Success"));

			//6 - Verify the above enrollment completed succsessfully.
			Helper_Functions.PrintOut("Check recipient profile for new FDM user through USRC", false);
			Response = recipient_profile.RecipientProfile(Cookie);
			
			String Results[] = new String[] {UserID, Password, fdx_login_fcl_uuid[1], USRC_Org};
			Helper_Functions.PrintOut(Arrays.toString(Results), false);
			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	@Test (dataProvider = "dp", priority = 1, description = "380527", enabled = true)
	public void EndtoEndEnrollment_UserID(String Level, String USRC_Org, MFAC_Data MFAC_Details, String MFAC_Org, String UserID, String Password) {
		String Cookie = null, UUID = null, fdx_login_fcl_uuid[] = {"","", ""};
		try {
			String Response = "";
			String ContactDetails[] = USRC_Data.getContactDetails(0);
			
			if (UserID == null) {
				//create the new user
				Response = REGC.create_new_user.NewFCLUser(ContactDetails, UserID, Password);
			
				//check to make sure that the userid was created.
				assertThat(Response, containsString("successful\":true"));
			}
			
			//get the cookies and the uuid of the new user
			fdx_login_fcl_uuid = login.Login(UserID, Password);
			Cookie = fdx_login_fcl_uuid[0];
			UUID = fdx_login_fcl_uuid[1];
				
			//2 - do the enrollment call. Note that the enrollment call will store the ShareID
			Helper_Functions.PrintOut("Enrollment call", false);
			Response = fdm_enrollment.Enrollment(ContactDetails, Cookie);

			assertThat(Response, containsString("enrollmentOptionsList"));
			
			//3 - request a pin
			Helper_Functions.PrintOut("Request pin through USRC", false);
			String ShareID = ParseShareID(Response);
			Response = USRC_Endpoints.CreatePin(Cookie, ShareID, USRC_Org);
			assertThat(Response, containsString("successful\":true"));
		
			//4 - request a pin through MFAC as cannot see the pin generated from the above
			Helper_Functions.PrintOut("Requesting pin through MFAC", false);
			String UserName = UUID + "-" + ShareID;
			Response = MFAC_Endpoints.IssuePinAPI(UserName, MFAC_Org);
			//get the pin from the MFAC call
			String Pin = MFAC_Helper_Functions.ParsePIN(Response);
			
			//5 - enroll the above pin through USRC
			Helper_Functions.PrintOut("Verify pin through USRC", true);
			Response = USRC_Endpoints.VerifyPin(Cookie, ShareID, Pin, USRC_Org);
			assertThat(Response, containsString("responseMessage\":\"Success"));

			//6 - Verify the above enrollment completed successfully.
			Helper_Functions.PrintOut("Check recipient profile for new FDM user through USRC", false);
			Response = recipient_profile.RecipientProfile(Cookie);
			
			Helper_Functions.PrintOut(UserID + "/" + Password + "--" + fdx_login_fcl_uuid[1] + "--" + USRC_Org, false);
			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public static String ParseShareID(String s) {
		if(s.contains("addressVerificationId") && s.contains("enrollmentOptionsList")) {
			String start = "addressVerificationId\":\"";
			return s.substring(s.indexOf(start) + start.length(), s.indexOf("\",\"enrollmentOptionsList"));
		}
		return null;
	}
}
