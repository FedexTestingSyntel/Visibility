package USRC_Application;

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
 
	static String LevelsToTest = "6"; //Can but updated to test multiple levels at once if needed. Setting to "23" will test both level 2 and level 3.

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
	    	USRC_Data USRC_D = USRC_Data.LoadVariables(strLevel);
	    	MFAC_Data MFAC_D = MFAC_Data.LoadVariables(strLevel);
	    	
			switch (m.getName()) { //Based on the method that is being called the array list will be populated.
			case "EndtoEndEnrollment":
				for (int j = 0 ; j < 1; j++) {
					String UserID = Helper_Functions.LoadUserID("L" + strLevel + "FDM");
					String Password = "Test1234";
					String ContactDetails[] = USRC_Data.getContactDetails(j);
					
					data.add(new Object[] {strLevel, USRC_D, USRC_D.FDMPostcard_PinType, MFAC_D, MFAC_D.OrgPostcard, UserID, Password, ContactDetails});
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
	    			if (User_Info.FDM_STATUS.contentEquals("F") && User_Info.EMAIL_ADDRESS.contentEquals("accept@fedex.com")) {
	    				data.add(new Object[] {USRC_D, USRC_D.FDMPostcard_PinType, MFAC_D, MFAC_D.OrgPostcard, User_Info.USER_ID, User_Info.PASSWORD});
	    				if (data.size() > 10) {
	    					break;
	    				}
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
					data.add(new Object[] {strLevel, USRC_D, USRC_D.FDMPostcard_PinType, MFAC_D, MFAC_D.OrgPostcard, UserID, Password, ContactDetails});
				}
				break;
			}//end switch MethodName
		}
		return data.iterator();
	}
	
	@Test (dataProvider = "dp", priority = 1, description = "380527", enabled = true)
	public void EndtoEndEnrollment(String Level, USRC_Data USRC_Details, String USRC_Org, MFAC_Data MFAC_Details, String MFAC_Org, String UserID, String Password, String[] ContactDetails) {
		String Cookie = null, UUID = null, fdx_login_fcl_uuid[] = {"","", ""};
		try {
			String Response = "";

			//create the new user
			Response = USRC_Endpoints.NewFCLUser(USRC_Details.REGCCreateNewUserURL, ContactDetails, UserID, Password);
			
			//check to make sure that the userid was created.
			assertThat(Response, containsString("successful\":true"));
			
			//get the cookies and the uuid of the new user
			fdx_login_fcl_uuid = USRC_Endpoints.Login(UserID, Password);
			Cookie = fdx_login_fcl_uuid[0];
			UUID = fdx_login_fcl_uuid[1];
				
			//2 - do the enrollment call. Note that the enrollment call will store the ShareID
			Helper_Functions.PrintOut("Enrollment call", false);
			Response = USRC_Endpoints.Enrollment(USRC_Details.EnrollmentURL, USRC_Details.OAuth_Token, ContactDetails, Cookie);

			assertThat(Response, containsString("enrollmentOptionsList"));
			
			//3 - request a pin
			Helper_Functions.PrintOut("Request pin through USRC", false);
			String ShareID = ParseShareID(Response);
			Response = USRC_Endpoints.CreatePin(USRC_Details.CreatePinURL, USRC_Details.OAuth_Token, Cookie, ShareID, USRC_Org);
			assertThat(Response, containsString("successful\":true"));
		
			//4 - request a pin through MFAC as cannot see the pin generated from the above
			Helper_Functions.PrintOut("Requesting pin through MFAC", false);
			String UserName = UUID + "-" + ShareID;
			Response = MFAC_Endpoints.IssuePinAPI(UserName, MFAC_Org, MFAC_Details.AIssueURL, MFAC_Details.OAuth_Token);
			//get the pin from the MFAC call
			String Pin = MFAC_Helper_Functions.ParsePIN(Response);
			
			//5 - enroll the above pin through USRC
			Helper_Functions.PrintOut("Verify pin through USRC", true);
			Response = USRC_Endpoints.VerifyPin(USRC_Details.VerifyPinURL, USRC_Details.OAuth_Token, Cookie, ShareID, Pin, USRC_Org);
			assertThat(Response, containsString("responseMessage\":\"Success"));

			//6 - Verify the above enrollment completed succsessfully.
			Helper_Functions.PrintOut("Check recipient profile for new FDM user through USRC", false);
			Response = USRC_Endpoints.RecipientProfile(USRC_Details.GenericUSRCURL, Cookie);
			
			String Results[] = new String[] {UserID, Password, fdx_login_fcl_uuid[1], USRC_Org};
			Helper_Functions.PrintOut(Arrays.toString(Results), false);
			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	@Test (dataProvider = "dp", priority = 1, description = "380527")
	public void EndtoEndEnrollment_EmailaAsUserId(String Level, USRC_Data USRC_Details, String USRC_Org, MFAC_Data MFAC_Details, String MFAC_Org, String UserID, String Password, String[] ContactDetails) {
		String Cookie = null, UUID = null, fdx_login_fcl_uuid[] = {"","", ""};
		try {
			String Response = "";
			
			//create the new user
			Response = USRC_Endpoints.NewFCLUser(USRC_Details.REGCCreateNewUserURL, ContactDetails, UserID, Password);
			
			//check to make sure that the userid was created.
			assertThat(Response, containsString("successful\":true"));
			
			//get the cookies and the uuid of the new user
			fdx_login_fcl_uuid = USRC_Endpoints.Login(UserID, Password);
			Cookie = fdx_login_fcl_uuid[0];
			UUID = fdx_login_fcl_uuid[1];
				
			//2 - do the enrollment call. Note that the enrollment call will store the ShareID
			Helper_Functions.PrintOut("Enrollment call", false);
			Response = USRC_Endpoints.Enrollment(USRC_Details.EnrollmentURL, USRC_Details.OAuth_Token, ContactDetails, Cookie);

			assertThat(Response, containsString("enrollmentOptionsList"));
			
			//3 - request a pin
			Helper_Functions.PrintOut("Request pin through USRC", false);
			String ShareID = ParseShareID(Response);
			Response = USRC_Endpoints.CreatePin(USRC_Details.CreatePinURL, USRC_Details.OAuth_Token, Cookie, ShareID, USRC_Org);
			assertThat(Response, containsString("successful\":true"));
		
			//4 - request a pin through MFAC as cannot see the pin generated from the above
			Helper_Functions.PrintOut("Requesting pin through MFAC", false);
			String UserName = UUID + "-" + ShareID;
			Response = MFAC_Endpoints.IssuePinAPI(UserName, MFAC_Org, MFAC_Details.AIssueURL, MFAC_Details.OAuth_Token);
			//get the pin from the MFAC call
			String Pin = MFAC_Helper_Functions.ParsePIN(Response);
			
			//5 - enroll the above pin through USRC
			Helper_Functions.PrintOut("Verify pin through USRC", true);
			Response = USRC_Endpoints.VerifyPin(USRC_Details.VerifyPinURL, USRC_Details.OAuth_Token, Cookie, ShareID, Pin, USRC_Org);
			assertThat(Response, containsString("responseMessage\":\"Success"));

			//6 - Verify the above enrollment completed succsessfully.
			Helper_Functions.PrintOut("Check recipient profile for new FDM user through USRC", false);
			Response = USRC_Endpoints.RecipientProfile(USRC_Details.GenericUSRCURL, Cookie);
			
			String Results[] = new String[] {UserID, Password, fdx_login_fcl_uuid[1], USRC_Org};
			Helper_Functions.PrintOut(Arrays.toString(Results), false);
			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	@Test (dataProvider = "dp", priority = 1, description = "380527", enabled = false)
	public void EndtoEndEnrollment_UserID(USRC_Data USRC_Details, String USRC_Org, MFAC_Data MFAC_Details, String MFAC_Org, String UserID, String Password) {
		String Cookie = null, UUID = null, fdx_login_fcl_uuid[] = {"","", ""};
		try {
			String Response = "";
			String ContactDetails[] = USRC_Data.getContactDetails(0);
			
			if (UserID == null) {
				//create the new user
				Response = USRC_Endpoints.NewFCLUser(USRC_Details.REGCCreateNewUserURL, ContactDetails, UserID, Password);
			
				//check to make sure that the userid was created.
				assertThat(Response, containsString("successful\":true"));
			}
			
			//get the cookies and the uuid of the new user
			fdx_login_fcl_uuid = USRC_Endpoints.Login(UserID, Password);
			Cookie = fdx_login_fcl_uuid[0];
			UUID = fdx_login_fcl_uuid[1];
				
			//2 - do the enrollment call. Note that the enrollment call will store the ShareID
			Helper_Functions.PrintOut("Enrollment call", false);
			Response = USRC_Endpoints.Enrollment(USRC_Details.EnrollmentURL, USRC_Details.OAuth_Token, ContactDetails, Cookie);

			assertThat(Response, containsString("enrollmentOptionsList"));
			
			//3 - request a pin
			Helper_Functions.PrintOut("Request pin through USRC", false);
			String ShareID = ParseShareID(Response);
			Response = USRC_Endpoints.CreatePin(USRC_Details.CreatePinURL, USRC_Details.OAuth_Token, Cookie, ShareID, USRC_Org);
			assertThat(Response, containsString("successful\":true"));
		
			//4 - request a pin through MFAC as cannot see the pin generated from the above
			Helper_Functions.PrintOut("Requesting pin through MFAC", false);
			String UserName = UUID + "-" + ShareID;
			Response = MFAC_Endpoints.IssuePinAPI(UserName, MFAC_Org, MFAC_Details.AIssueURL, MFAC_Details.OAuth_Token);
			//get the pin from the MFAC call
			String Pin = MFAC_Helper_Functions.ParsePIN(Response);
			
			//5 - enroll the above pin through USRC
			Helper_Functions.PrintOut("Verify pin through USRC", true);
			Response = USRC_Endpoints.VerifyPin(USRC_Details.VerifyPinURL, USRC_Details.OAuth_Token, Cookie, ShareID, Pin, USRC_Org);
			assertThat(Response, containsString("responseMessage\":\"Success"));

			//6 - Verify the above enrollment completed successfully.
			Helper_Functions.PrintOut("Check recipient profile for new FDM user through USRC", false);
			Response = USRC_Endpoints.RecipientProfile(USRC_Details.GenericUSRCURL, Cookie);
			
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
