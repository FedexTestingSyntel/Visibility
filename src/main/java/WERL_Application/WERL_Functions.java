package WERL_Application;

import org.openqa.selenium.By;
import org.testng.Assert;

import Data_Structures.Address_Data;
import Data_Structures.User_Data;
import SupportClasses.Helper_Functions;
import SupportClasses.WebDriver_Functions;
import WebElements.EnrollmentPageElements;

public class WERL_Functions {

	private static String WERL_Confirmation_Message = "Your Registration is Complete.";
	
	public static boolean EnterContactInformaiton(User_Data User_Info, boolean ClickContinue) throws Exception {
		try {
			WebDriver_Functions.Type(EnrollmentPageElements.FirstNameField, User_Info.FIRST_NM);
			WebDriver_Functions.Type(EnrollmentPageElements.MiddleNameField, User_Info.MIDDLE_NM);
			WebDriver_Functions.Type(EnrollmentPageElements.LastNameField, User_Info.LAST_NM);
			
			Address_Data Address = User_Info.Address_Info;
			WebDriver_Functions.Type(EnrollmentPageElements.AddressOneField, Address.Address_Line_1);
			WebDriver_Functions.Type(EnrollmentPageElements.AddressTwoField, Address.Address_Line_2);
			WebDriver_Functions.WaitPresent(EnrollmentPageElements.CountryField);
			WebDriver_Functions.Select(EnrollmentPageElements.CountryField, Address.Country_Code, "v");
			WebDriver_Functions.Type(EnrollmentPageElements.PostalCodeField, Address.PostalCode);
			WebDriver_Functions.Select(EnrollmentPageElements.StateField, Address.State_Code, "v");
			
			WebDriver_Functions.WaitPresent(EnrollmentPageElements.cityFieldDropdown, EnrollmentPageElements.CityField);
			// The city could be a dropdown or a text field based on the zip code
			if (WebDriver_Functions.isPresent(EnrollmentPageElements.cityFieldDropdown)) {
				WebDriver_Functions.Select(EnrollmentPageElements.cityFieldDropdown, Address.City.toUpperCase(), "v");
			}else {
				WebDriver_Functions.Type(EnrollmentPageElements.CityField, Address.City);
			}
			
			WebDriver_Functions.Type(EnrollmentPageElements.PhoneField, User_Info.PHONE_NUMBER);
			WebDriver_Functions.Type(EnrollmentPageElements.EmailField, User_Info.EMAIL_ADDRESS);
			
			// needed so can use same function for entering all other data
			if (ClickContinue) {
				WebDriver_Functions.takeSnapShot("ContactInfoPage.png");
				WebDriver_Functions.Click(EnrollmentPageElements.SignUpContinueButton);
			}
		}catch (Exception e) {
			e.printStackTrace();
			throw e;
		}
		return true;
	}
	
	public static boolean EnterLoginInformaiton(User_Data User_Info, boolean ClickContinue) throws Exception {
		try {
			if (User_Info.USER_ID.contains("@")) {
				WebDriver_Functions.Click(EnrollmentPageElements.EmailAsUserIDRadioButton);
			}else {
				WebDriver_Functions.Type(EnrollmentPageElements.UserIDField, User_Info.USER_ID);
			}
			
			WebDriver_Functions.Type(EnrollmentPageElements.PasswordField, User_Info.PASSWORD);
			WebDriver_Functions.Type(EnrollmentPageElements.ReenterPasswordField, User_Info.PASSWORD);
			WebDriver_Functions.Select(EnrollmentPageElements.SecretQuestionField, User_Info.SECRET_QUESTION_DESC, "v");
			WebDriver_Functions.Type(EnrollmentPageElements.SecretAnswerField, User_Info.SECRET_ANSWER_DESC);
			
			WebDriver_Functions.Click(EnrollmentPageElements.TermsAndConditionsCheckbox);
			
			if (ClickContinue) {
				WebDriver_Functions.takeSnapShot("LoginInfoPage.png");
				WebDriver_Functions.Click(EnrollmentPageElements.CreateUserIdContinueButton);
		 		//wait for user to be created and process to next screen.
				//At this point enrollment call should have completed.
		 		WebDriver_Functions.WaitPresent(By.id("angularWerl"));
		 		WebDriver_Functions.WaitForBodyText("Verify your registration address");
		 		WebDriver_Functions.takeSnapShot("VerifyRegistation.png");
		 		//Write User to file for later reference
				Helper_Functions.WriteUserToExcel(User_Info.USER_ID, User_Info.PASSWORD);
			}
		}catch (Exception e) {
			e.printStackTrace();
			throw e;
		}
		return true;
	}
	
	//Takes in the same options as the USRC enrollmentOptionsList
	public static boolean FDMValidation(String UserName, String EnrollmentOption) throws Exception {
		try {
			if (EnrollmentOption.contains("POSTAL")) {
				WebDriver_Functions.WaitPresent(EnrollmentPageElements.SendPostcardButton);
				WebDriver_Functions.Click(EnrollmentPageElements.SendPostcardButton);
				//// need to update this later to handle translation as well as correct field.
				WebDriver_Functions.WaitForBodyText("Your activation code is on the way");
				
				int loop = 0;
				// 3 is how many attempts before MFAC starts locking the user out of enrollment.
				while (loop < 3) {
					try {
						// Make a call to MFAC and get a new pin for the user
						String PostCardPin = MFAC_Application.MFAC_Endpoints.IssuePinAPI_External(UserName, EnrollmentOption);
						//refresh the page so that the pin entry is present.
						WebDriver_Functions.ChangeURL("WERL", "US", "en", false);
						
						WebDriver_Functions.Type(EnrollmentPageElements.PinInputField, PostCardPin);
						WebDriver_Functions.Click(EnrollmentPageElements.SubmitPinButton);

						WebDriver_Functions.WaitForBodyText("Your Registration is Complete.");
					} catch (Exception e2) {
						if (e2.getMessage().contains(WERL_Confirmation_Message)) {
							loop++;
							Helper_Functions.PrintOut("Attempting additional pin " + loop);
						} else {
							// tried looping through additional pins and still not working, giving up.
							throw e2;
						}
					}
				}


			}else if (EnrollmentOption.contains("PHONE")) {
				//TBD
			}
		}catch (Exception e) {
			e.printStackTrace();
			throw e;
		}
		return true;
	}
	
}
