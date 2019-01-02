package Mission_Critical;

import java.util.ArrayList;
import java.util.Arrays;
import org.openqa.selenium.By;
import SupportClasses.DriverFactory;
import SupportClasses.Helper_Functions;
import SupportClasses.WebDriver_Functions;

public class WIDM_Functions{
	
	public static String strPassword = "Test1234";
	
	public static String WIDM_Registration(String AddressDetails[], String Name[], String UserId, String Email_Address) throws Exception{
		String CountryCode = AddressDetails[6];
		try {
			WebDriver_Functions.ChangeURL("WIDM", CountryCode, true);
			WebDriver_Functions.Click(By.linkText("Sign Up Now!"));

			//Enter all of the form data
			WIDM_Registration_Input(AddressDetails, Name, UserId, Email_Address);
			WebDriver_Functions.takeSnapShot("Contact Information.png");
			WebDriver_Functions.Click(By.id("createUserID"));

			//confirmation page
			WebDriver_Functions.WaitForText(By.xpath("//*[@id='content']/div/table/tbody/tr[1]/td[2]/table[2]/tbody/tr[3]/td/table[2]/tbody/tr/td[1]/table/tbody/tr[2]/td/b"), UserId);
			String UUID = WebDriver_Functions.GetCookieValue("fcl_uuid");
			WebDriver_Functions.takeSnapShot("Registration WIDM Confirmation.png");
			String ReturnValue[] = new String[] {UserId, strPassword, UUID};
			Helper_Functions.WriteUserToExcel(UserId, strPassword);
			
			Helper_Functions.PrintOut("WIDM_Registration Completed: " + UserId + "/" + strPassword + "--" + UUID, false);
			return Arrays.toString(ReturnValue);
		}catch (Exception e) {
			e.printStackTrace();
			throw e;
		}
	}//end WIDM_Registration

	public static String WIDM_RegistrationEFWS(String AddressDetails[], String Name[], String UserId) throws Exception{
		String CountryCode = AddressDetails[6];

		try {
			WebDriver_Functions.ChangeURL("WIDM", CountryCode, true);
			WebDriver_Functions.Click(By.linkText("Sign Up Now!"));
			
			//Enter all of the form data
			String EfwsEmail = "robmus50@yahoo.com";
			WIDM_Registration_Input(AddressDetails, Name, UserId, EfwsEmail);
			WebDriver_Functions.takeSnapShot(".png");
			WebDriver_Functions.Click(By.id("createUserID"));

			//Check that the correct error message appears.
			WebDriver_Functions.WaitForText(By.cssSelector("#module\\2e registration\\2e _expanded > table > tbody > tr:nth-child(2) > td > b"), "Your registration request is not approved based on the information submitted.");
			WebDriver_Functions.takeSnapShot("Message.png");
			Helper_Functions.PrintOut("WIDM_Registration_EFWS Completed with: " + EfwsEmail, false);
			return UserId + " " + EfwsEmail;
		}catch (Exception e) {
			e.printStackTrace();
			throw e;
		}
	}//end WIDM_Registration_EFWS
	
	public static void WIDM_Registration_Input(String AddressDetails[], String Name[], String UserId, String Email_Address) throws Exception{
		WebDriver_Functions.Type(By.id("firstName"), Name[0]);
		WebDriver_Functions.Type(By.id("lastName"), Name[2]);
		WebDriver_Functions.Type(By.id("email"), Email_Address);
		WebDriver_Functions.Type(By.id("retypeEmail"), Email_Address);
		WebDriver_Functions.Type(By.id("address1"), AddressDetails[0]);
		WebDriver_Functions.Type(By.id("address2"), AddressDetails[1]);
		if (WebDriver_Functions.isPresent(By.id("city"))) {
			WebDriver_Functions.Type(By.id("city"), AddressDetails[2]);
		}

		if (AddressDetails[4] != ""){
			WebDriver_Functions.Type(By.id("state"),AddressDetails[4]);
		}
		
		if (AddressDetails[5] != ""){
			WebDriver_Functions.Type(By.id("zip"),AddressDetails[5]);
		}

		if (WebDriver_Functions.isPresent(By.xpath("//*[@id='module.registration._expanded']/table/tbody/tr/td[1]/table/tbody/tr[24]/td[2]"))) {
			WebDriver_Functions.ElementMatches(By.xpath("//*[@id='module.registration._expanded']/table/tbody/tr/td[1]/table/tbody/tr[24]/td[2]"), "Country/Territory", 116678);
		}else {
			WebDriver_Functions.ElementMatches(By.xpath("//*[@id='module.registration._expanded']/table/tbody/tr/td[1]/table/tbody/tr[23]/td[2]"), "Country/Territory", 116678); //for non us
		}
		
		if (AddressDetails[6].toLowerCase().contains("ca")) {
			//for Canada the list populates ca_english or ca_french
			WebDriver_Functions.Select(By.id("country1"), "ca_english", "v");
		}else{
			WebDriver_Functions.Select(By.id("country1"), AddressDetails[6].toLowerCase(), "v");
		}

		WebDriver_Functions.Type(By.id("phone"), Helper_Functions.ValidPhoneNumber(AddressDetails[6]));
		WebDriver_Functions.Type(By.id("uid"), UserId); 
		Helper_Functions.PrintOut("Userid is: " + UserId, true);
		WebDriver_Functions.Type(By.id("password"), strPassword);
		WebDriver_Functions.Type(By.id("retypePassword"), strPassword);
		WebDriver_Functions.Select(By.id("reminderQuestion"), "What is your mother's first name?", "t");
		WebDriver_Functions.Type(By.id("reminderAnswer"), "mom");
		WebDriver_Functions.Click(By.id("acceptterms"));
		if (!DriverFactory.getInstance().getDriver().findElement(By.id("acceptterms")).isSelected()){//added this as there is an issue with error messages and script clicking checkbox, works normally manually.
			WebDriver_Functions.Click(By.id("acceptterms"));
		}
	}//end WIDM_Registration_Input

	public static String WIDM_Registration_ErrorMessages(String AddressDetails[], String Name[], String UserId) throws Exception{
		String CountryCode = AddressDetails[6];;

		try {
			ArrayList<String> ResultsList = new ArrayList<String>();
			WebDriver_Functions.ChangeURL("WIDM", CountryCode, true);
			WebDriver_Functions.Click(By.linkText("Sign Up Now!"));

			WebDriver_Functions.Click(By.id("createUserID"));
			ResultsList.add(WebDriver_Functions.ElementMatches(By.id("firstempty"), "First name is required.", 1014978) + " 1014978");

			WebDriver_Functions.Type(By.id("firstName"), "!!!First");
			WebDriver_Functions.Click(By.id("createUserID"));
			WebDriver_Functions.takeSnapShot("Error Messages.png");
			ResultsList.add(WebDriver_Functions.ElementMatches(By.id("firstspecialchar"), "Invalid character in First name.", 1014978) + " 1014978");
			ResultsList.add(WebDriver_Functions.ElementMatches(By.id("lastempty"), "Last name is required.", 1014980) + " 1014980");
			ResultsList.add(WebDriver_Functions.ElementMatches(By.id("emailempty"), "Email address is required.", 1) + " 1");
			ResultsList.add(WebDriver_Functions.ElementMatches(By.id("reemailempty"), "Retyped email address is required.", 2) + " 2");
			ResultsList.add(WebDriver_Functions.ElementMatches(By.id("add1empty"), "Address 1 is required.", 3) + " 3");
			//ResultsList.add(WebDriver_Functions.ElementMatches(By.id(""), "", ) + " ");
			//ResultsList.add(WebDriver_Functions.ElementMatches(By.id(""), "", ) + " ");

			//ResultsList.add(WebDriver_Functions.ElementMatches(By.id(""), "", ) + " ");

			WebDriver_Functions.Type(By.id("lastName"), "L");
			WebDriver_Functions.Click(By.id("createUserID"));
			ResultsList.add(WebDriver_Functions.ElementMatches(By.id("invalidlength"), "Last name must be at least 2 characters.", 1014980) + " 1014980");
		
			WebDriver_Functions.Type(By.id("lastName"), "!!Last");
			WebDriver_Functions.Click(By.id("createUserID"));
			ResultsList.add(WebDriver_Functions.ElementMatches(By.id("lnspecialchar"), "Invalid character in Last name.", 1014980) + " 1014980");

			WebDriver_Functions.Type(By.id("lastName"), "!@#$%^&*()");
			WebDriver_Functions.Click(By.id("createUserID"));
			ResultsList.add(WebDriver_Functions.ElementMatches(By.id("lnspecialchar"), "Invalid character in Last name.", 1019256) + " 1019256");

			WebDriver_Functions.Type(By.id("firstName"), Name[0]);
			WebDriver_Functions.Type(By.id("uid"), UserId);

			//Still need to add the remaining scenarios
			String Failures = "";
			for (String FailedChecks: ResultsList) {
				if (FailedChecks.contains("false")) {
					Failures += FailedChecks + " ";
				}
			}
			
			Helper_Functions.PrintOut(Failures, false);
			return Failures;
		}catch (Exception e) {
			e.printStackTrace();
			throw e;
		}
	}//end WIDM_Registration_Input

	public static String ResetPasswordWIDM_Secret(String CountryCode, String strUserName, String NewPassword, String SecretAnswer, boolean ErrorExpected) throws Exception{
		if(strUserName == null){
			Helper_Functions.PrintOut("Cannot login with user id as null. Recieved from " + Thread.currentThread().getStackTrace()[2].getMethodName(), true);
			throw new Exception("Userid required.");
		} 
		
		WebDriver_Functions.ChangeURL("WIDM", CountryCode, true);

		WebDriver_Functions.Click(By.name("forgotUidPwd"));//click the forgot password link
		WebDriver_Functions.Type(By.name("userID"), strUserName);
		WebDriver_Functions.takeSnapShot("Password Reset " + NewPassword + ".png");
		WebDriver_Functions.Click(By.xpath("//*[@id='module.forgotuseridandpassword._expanded\']/table/tbody/tr/td[1]/form/table/tbody/tr[6]/td/input[2]"));//click option 1 for reset through user id
		WebDriver_Functions.Click(By.xpath("//*[@id='module.resetpasswordoptions._expanded']/table/tbody/tr/td[1]/form/table/tbody/tr[5]/td/input"));//click the option 1 button and try to answer with secret question
		WebDriver_Functions.Type(By.name("answer"), SecretAnswer);
		WebDriver_Functions.takeSnapShot("Reset Password Secret " + NewPassword + ".png");
		WebDriver_Functions.Click(By.name("action1"));
		
		//wait for the new password text box appears. If doesn't with throw and error and try the next password
		WebDriver_Functions.WaitPresent(By.name("password"));
		WebDriver_Functions.Type(By.name("password"),NewPassword);
		WebDriver_Functions.Type(By.name("retypePassword"),NewPassword);
		WebDriver_Functions.takeSnapShot("New Password " + NewPassword + ".png");
		WebDriver_Functions.Click(By.name("confirm"));
		
		if (ErrorExpected) {
			WebDriver_Functions.WaitForText(By.xpath("//*[@id='content']/div/form/table/tbody/tr[6]/td/table/tbody/tr[2]/td/b"), "New password cannot be the same as the last password.");
			WebDriver_Functions.takeSnapShot("Same Password " + NewPassword + ".png");
			NewPassword = NewPassword + "5";
			WebDriver_Functions.Type(By.name("password"),NewPassword);
			WebDriver_Functions.Type(By.name("retypePassword"),NewPassword);
			WebDriver_Functions.takeSnapShot("New Password " + NewPassword + ".png");
			WebDriver_Functions.Click(By.name("confirm"));
		}
		
		WebDriver_Functions.WaitForText(By.xpath("//*[@id='content']/div/table/tbody/tr[1]/td/table/tbody/tr/td/table/tbody/tr[1]/td"), "Thank you. Your password has been reset");
		boolean loginAttempt = WebDriver_Functions.Login(strUserName, NewPassword);
		Helper_Functions.PrintOut(strUserName + " has had the password changed to " + NewPassword, true);
		if (NewPassword.contentEquals(strPassword)){
			return NewPassword;
		}else if (loginAttempt){
			String Result = strUserName + " " + NewPassword + " " +  ResetPasswordWIDM_Secret(CountryCode, strUserName, strPassword, SecretAnswer, false);//change the password back
			Helper_Functions.PrintOut(Result, false);
			return Result;
		}else{
			Helper_Functions.PrintOut("Not able to verify changed password", true);
			throw new Exception("Not able to complete with generic secret questions.");
		}
	}//end ResetPasswordWIDM

	public static String Reset_Password_WIDM_Email(String strUserName, String Password) throws Exception{
		try{
			WebDriver_Functions.Login(strUserName, Password);

			
			String Email = "<<not found>>";
			try {
				WebDriver_Functions.ChangeURL("WPRL", "US", false);
				WebDriver_Functions.WaitPresent(By.id("ci_fullname_val"));
				String UserDetails = DriverFactory.getInstance().getDriver().findElement(By.id("ci_fullname_val")).getText();
				Email = UserDetails.substring(UserDetails.lastIndexOf('\n') + 1, UserDetails.length());
			}catch(Exception e2) {
				Helper_Functions.PrintOut("Not able to validate the email address for this user.", false);
			}
			
			//trigger the password reset email
			WebDriver_Functions.ChangeURL("WIDM", "US", true);
			WebDriver_Functions.Click(By.name("forgotUidPwd"));
			WebDriver_Functions.Type(By.name("userID"),strUserName);

			WebDriver_Functions.Click(By.xpath("//*[@id='module.forgotuseridandpassword._expanded']/table/tbody/tr/td[1]/form/table/tbody/tr[6]/td/input[2]"));
			//click the option 1 button and try to answer with secret question
			WebDriver_Functions.Click(By.name("action2"));
			WebDriver_Functions.WaitPresent(By.id("linkaction"));
			WebDriver_Functions.takeSnapShot(".png");
			Helper_Functions.PrintOut("Completed ResetPasswordWIDM using " + strUserName + ". An email has been triggered to " + Email, false);

			return strUserName + Email;
		}catch(Exception e){
			e.printStackTrace();
	throw e;
		}
	}//end ResetPasswordWIDM

	public static String Forgot_User_WIDM(String CountryCode, String Email) throws Exception{
		try{	
			WebDriver_Functions.ChangeURL("WIDM", CountryCode, true);
			WebDriver_Functions.Click(By.name("forgotUidPwd"));
			//wait for text box for user id to appear
			WebDriver_Functions.Type(By.name("email"),Email);
			WebDriver_Functions.takeSnapShot(".png");
			WebDriver_Functions.Click(By.xpath("//*[@id='module.forgotuseridandpassword._expanded']/table/tbody/tr/td[3]/form/table/tbody/tr[6]/td/input[2]"));
			WebDriver_Functions.takeSnapShot("Confirmation.png");
			
			Helper_Functions.PrintOut("Completed Forgot User Confirmation using " + Email, false);
			return "Email sent to + " + Email;
		}catch(Exception e){
			e.printStackTrace();
			throw e;
		}
	}//end ResetPasswordWIDM
}
