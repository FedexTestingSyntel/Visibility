package CXS_Support;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.DataProvider;
import CXS_Support.USRC_Data;
import CXS_Support.MFAC_Helper_Functions;
import SupportClasses.Environment;
import SupportClasses.ThreadLogger;
import SupportClasses.Helper_Functions;
import org.testng.annotations.Listeners;
import org.testng.annotations.Test;
import static org.junit.Assert.assertThat;
import static org.hamcrest.CoreMatchers.containsString;
import API_Calls.*;
import Data_Structures.*;

@Listeners(SupportClasses.TestNG_TestListener.class)

public class USRC_FDM {

	static String LevelsToTest = "2"; //Can but updated to test multiple levels at once if needed. Setting to "23" will test both level 2 and level 3.

	@BeforeClass
	public void beforeClass() {
		Environment.SetLevelsToTest(LevelsToTest);
	}
	
	@DataProvider (parallel = true)
	public Iterator<Object[]> dp(Method m) {
	    List<Object[]> data = new ArrayList<>();
	    
	    for (int i = 0; i < Environment.LevelsToTest.length(); i++) {
	    	String strLevel = "" + Environment.LevelsToTest.charAt(i);
	    	USRC_Data USRC_D = USRC_Data.LoadVariables(strLevel);
	    	MFAC_Data MFAC_D = MFAC_Data.LoadVariables(strLevel);
	    	
			switch (m.getName()) { //Based on the method that is being called the array list will be populated.
			case "EndtoEndEnrollment":
				//data.add(new Object[] {USRC_D.Level, USRC_D.REGCCreateNewUserURL, USRC_D.LoginUserURL, USRC_D.EnrollmentURL, USRC_D.OAuth_Token, USRC_Data.ContactDetailsList.get(0), MFAC_D.OrgPhone});
				
				for (int j = 0; j < 1; j++) {
					String UserID = "L" + strLevel + "FDM" + Helper_Functions.CurrentDateTime() + Helper_Functions.getRandomString(2);
					String Password = "Test1234";
					data.add(new Object[] {strLevel, USRC_D, USRC_D.FDMPostcard_PinType, MFAC_D, MFAC_D.OrgPostcard, UserID, Password, j});
				}
				break;
			case "EndtoEndEnrollment_UserID":
				
					data.add(new Object[] {USRC_D, USRC_D.FDMPostcard_PinType, MFAC_D, MFAC_D.OrgPostcard, "L3FDM111715491", "Test1234"});
				
				
				break;
			}//end switch MethodName
		}
		return data.iterator();
	}
	
	@Test (dataProvider = "dp", priority = 1, description = "380527")
	public void EndtoEndEnrollment(String Level, USRC_Data USRC_Details, String USRC_Org, MFAC_Data MFAC_Details, String MFAC_Org, String UserID, String Password, int Contact) {
		String Cookie = null, UUID = null, fdx_login_fcl_uuid[] = {"","", ""};
		try {
			String Response = "";
			String ContactDetails[] = USRC_Data.ContactDetailsList.get(Contact);
			
			//create the new user
			Response = USRC_API_Endpoints.NewFCLUser(USRC_Details.REGCCreateNewUserURL, ContactDetails, UserID, Password);
			
			//check to make sure that the userid was created.
			assertThat(Response, containsString("successful\":true"));
			
			//get the cookies and the uuid of the new user
			fdx_login_fcl_uuid = USRC_API_Endpoints.Login(USRC_Details.LoginUserURL, UserID, Password);
			Cookie = fdx_login_fcl_uuid[0];
			UUID = fdx_login_fcl_uuid[1];
				
			//2 - do the enrollment call. Note that the enrollment call will store the ShareID
			Helper_Functions.PrintOut("Enrollment call", false);
			Response = USRC_API_Endpoints.Enrollment(USRC_Details.EnrollmentURL, USRC_Details.OAuth_Token, ContactDetails, Cookie);

			assertThat(Response, containsString("enrollmentOptionsList"));
			
			//3 - request a pin
			Helper_Functions.PrintOut("Request pin through USRC", false);
			String ShareID = ContactDetails[11];
			Response = USRC_API_Endpoints.CreatePin(USRC_Details.CreatePinURL, USRC_Details.OAuth_Token, Cookie, ShareID, USRC_Org);
			assertThat(Response, containsString("successful\":true"));
		
			//4 - request a pin through MFAC as cannot see the pin generated from the above
			Helper_Functions.PrintOut("Requesting pin through MFAC", false);
			String UserName = UUID + "-" + ShareID;
			Response = MFAC_API_Endpoints.IssuePinAPI(UserName, MFAC_Org, MFAC_Details.AIssueURL, MFAC_Details.OAuth_Token);
			//get the pin from the MFAC call
			String Pin = MFAC_Helper_Functions.ParsePIN(Response);
			
			//5 - enroll the above pin through USRC
			Helper_Functions.PrintOut("Verify pin through USRC", true);
			Response = USRC_API_Endpoints.VerifyPin(USRC_Details.VerifyPinURL, USRC_Details.OAuth_Token, Cookie, ShareID, Pin, USRC_Org);
			assertThat(Response, containsString("responseMessage\":\"Success"));

			//6 - Verify the above enrollment completed succsessfully.
			Helper_Functions.PrintOut("Check recipient profile for new FDM user through USRC", false);
			Response = USRC_API_Endpoints.RecipientProfile(USRC_Details.LoginUserURL, Cookie);
			
			Helper_Functions.PrintOut(UserID + "/" + Password + "--" + fdx_login_fcl_uuid[1] + "--" + USRC_Org, false);
			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	@Test (dataProvider = "dp", priority = 1, description = "380527")
	public void EndtoEndEnrollment_UserID(USRC_Data USRC_Details, String USRC_Org, MFAC_Data MFAC_Details, String MFAC_Org, String UserID, String Password) {
		String Cookie = null, UUID = null, fdx_login_fcl_uuid[] = {"","", ""};
		try {
			String Response = "";
			String ContactDetails[] = USRC_Data.ContactDetailsList.get(0);
			
			if (UserID == null) {
				//create the new user
				Response = USRC_API_Endpoints.NewFCLUser(USRC_Details.REGCCreateNewUserURL, ContactDetails, UserID, Password);
			
				//check to make sure that the userid was created.
				assertThat(Response, containsString("successful\":true"));
			}
			
			//get the cookies and the uuid of the new user
			fdx_login_fcl_uuid = USRC_API_Endpoints.Login(USRC_Details.LoginUserURL, UserID, Password);
			Cookie = fdx_login_fcl_uuid[0];
			UUID = fdx_login_fcl_uuid[1];
				
			//2 - do the enrollment call. Note that the enrollment call will store the ShareID
			Helper_Functions.PrintOut("Enrollment call", false);
			Response = USRC_API_Endpoints.Enrollment(USRC_Details.EnrollmentURL, USRC_Details.OAuth_Token, ContactDetails, Cookie);

			assertThat(Response, containsString("enrollmentOptionsList"));
			
			//3 - request a pin
			Helper_Functions.PrintOut("Request pin through USRC", false);
			String ShareID = ContactDetails[11];
			Response = USRC_API_Endpoints.CreatePin(USRC_Details.CreatePinURL, USRC_Details.OAuth_Token, Cookie, ShareID, USRC_Org);
			assertThat(Response, containsString("successful\":true"));
		
			//4 - request a pin through MFAC as cannot see the pin generated from the above
			Helper_Functions.PrintOut("Requesting pin through MFAC", false);
			String UserName = UUID + "-" + ShareID;
			Response = MFAC_API_Endpoints.IssuePinAPI(UserName, MFAC_Org, MFAC_Details.AIssueURL, MFAC_Details.OAuth_Token);
			//get the pin from the MFAC call
			String Pin = MFAC_Helper_Functions.ParsePIN(Response);
			
			//5 - enroll the above pin through USRC
			Helper_Functions.PrintOut("Verify pin through USRC", true);
			Response = USRC_API_Endpoints.VerifyPin(USRC_Details.VerifyPinURL, USRC_Details.OAuth_Token, Cookie, ShareID, Pin, USRC_Org);
			assertThat(Response, containsString("responseMessage\":\"Success"));

			//6 - Verify the above enrollment completed succsessfully.
			Helper_Functions.PrintOut("Check recipient profile for new FDM user through USRC", false);
			Response = USRC_API_Endpoints.RecipientProfile(USRC_Details.LoginUserURL, Cookie);
			
			Helper_Functions.PrintOut(UserID + "/" + Password + "--" + fdx_login_fcl_uuid[1] + "--" + USRC_Org, false);
			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
