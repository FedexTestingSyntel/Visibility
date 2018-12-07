package WPRL_Application;

import java.util.ArrayList;
import org.openqa.selenium.By;
import SupportClasses.Helper_Functions;
import SupportClasses.WebDriver_Functions;
	
public class WERL_Functions {

	static ArrayList<String[]> ContactList = new ArrayList<String[]>();
	
	public static String[] WERL_Postal_FDM(int AddresLoc, String UserId, String Password) throws Exception{
		try {
			//go to the INET page just to load the cookies
			WebDriver_Functions.ChangeURL("WERL", "US", true);
			WebDriver_Functions.Click(By.xpath("//*[@id='1505909112729']/div/div/div[1]/div/p/span[1]/strong/a"));//Sign up now button
			
			String Contact[] = FDMAddress(AddresLoc);

			EnterContactDetails(Contact, UserId, Password);
			
			WebDriver_Functions.WaitClickable(By.id("sendPostcardButton"));  
			WebDriver_Functions.takeSnapShot("WERL Pin Request.png");
			WebDriver_Functions.Click(By.id("sendPostcardButton"));
			
			String UUID = WebDriver_Functions.GetCookieValue("fcl_uuid");
			String ShareID = Contact[11];
			String Pin = MFAC_PinGeneration.IssuePinAPI(UUID + "-" + ShareID, "postal");
			//check the confirmation page that the postcard has been sent
			WebDriver_Functions.WaitForText(By.xpath("//*[@id='angularWerl']/app-root/app-postal-confirmation/div/h1"), "Your activation code is on the way");
			WebDriver_Functions.ChangeURL("WPRL_FDM", "US", true);//navigate to WPRL FDM page
			WebDriver_Functions.Click(By.id("activationLink"));
			
			WebDriver_Functions.WaitPresent(By.id("pinInputField"));
			WebDriver_Functions.Type(By.id("smsPinEntryPinValue"), Pin);
			WebDriver_Functions.takeSnapShot("WERL Pin Entry.png");

			WebDriver_Functions.Click(By.id("submitPinButton"));
	        
			WebDriver_Functions.takeSnapShot("FDM conformation notifications.png");
	        WebDriver_Functions.Click(By.id("regConfModuleModalSaveValue"));
	        return new String[] {UserId, Password, Contact[0]};
		}catch (Exception e) {
			throw e;
		}	
	}//end WPRL_Contact
		
	public static boolean EnterContactDetails(String Contact[], String UserId, String Password) throws Exception{
		try {
			//WERL contact information page displayed.
			//0 - First name, 1 - middle name, 2 - last name, 3 - phone number, 4 - email address, 5 - address line 1, 6 - address line 2, 7 - city, 8 - state code, 9 - zip code, 10 - country
			//{"TESTER", "", "ZNINEZ", Phone, Email, "75-681 LALII PL", "", "KAILUA KONA", "HI", "96740", "US"};
			WebDriver_Functions.WaitPresent(By.id("regFormModuleZipValue"));
			WebDriver_Functions.Select(By.id("regFormModuleStateSelect"), Contact[8], "v");
			WebDriver_Functions.Type(By.id("regFormModuleZipValue"), Contact[9]);
			WebDriver_Functions.Type(By.id("regFormModuleFirstNameValue"), Contact[0]);//change focus form the zip field
			
			Helper_Functions.Wait(5);
			WebDriver_Functions.Type(By.id("regFormModuleFirstNameValue"), Contact[0]);
			WebDriver_Functions.Type(By.id("regFormModuleMiddleNameValue"), Contact[1]);
			WebDriver_Functions.Type(By.id("regFormModuleLastNameValue"), Contact[2]);
			WebDriver_Functions.Type(By.id("regFormModulePhoneValue"), Contact[3]);
			WebDriver_Functions.Type(By.id("regFormModuleEmailValue"), Contact[4]);
			WebDriver_Functions.Type(By.id("regFormModuleAddress1Value"), Contact[5]);
			WebDriver_Functions.Type(By.id("regFormModuleAddress2Value"), Contact[6]);
			try {
				WebDriver_Functions.Select(By.id("regFormModuleCitySelect"), Contact[7].toUpperCase(), "t");
			}catch (Exception e) {}
			
			WebDriver_Functions.Type(By.id("regFormModuleUserIdValue"), UserId);
			WebDriver_Functions.Type(By.id("regFormModulePswdValue"), Password);
			WebDriver_Functions.Type(By.id("regFormModuleReenterPswdValue"), Password);
			WebDriver_Functions.Select(By.id("regFormModuleSecretQuestionSelect"), "1", "i");//secret question to moms first name
			WebDriver_Functions.Type(By.id("regFormModuleSecretAnswerValue"), "mom");
			if (!WebDriver_Functions.isSelected(By.id("regFormModuleTermsOfAgreementValue"))) {
				WebDriver_Functions.Click(By.id("regFormModuleTermsOfAgreementValue"));
			}
			WebDriver_Functions.takeSnapShot("WERL Contact Information.png");
			WebDriver_Functions.Click(By.id("regFormModuleContinueValue"));
		}catch (Exception e) {
			throw e;
		}
		return true;
	}
	
	public static String[] FDMAddress(int i){
		String Phone = "9011111111", Email = "FedexTestingSyntel@gmail.com";
		
		if (ContactList.isEmpty()){
			ContactList.add(new String[] {"John", "A", "Doe", Phone, Email, "32 MEADOW CREST DR", "", "SHERWOOD", "AR", "72120", "US", "6xmc5tpjhrtaymw7yfwshqfao"});
			ContactList.add(new String[] {"John", "A", "Doe", Phone, Email, "58 CABOT ST", "", "HARTFORD", "CT", "06112", "US", ""});
			ContactList.add(new String[] {"John", "A", "Doe", Phone, Email, "9133 SUPERIOR DR", "", "OLIVEBRANCH", "MS", "38654", "US"});
			ContactList.add(new String[] {"John", "A", "Doe", Phone, Email, "75-681 LALII PL", "", "KAILUA KONA", "HI", "96740", "US"});
			ContactList.add(new String[] {"John", "A", "Doe", Phone, Email, "95 HUTCHINS DR", "", "PORTLAND", "ME", "04102", "US"});
			ContactList.add(new String[] {"John", "A", "Doe", Phone, Email, "329 MADISON ST APT 202", "", "DENVER", "MD", "80206", "US"});
			ContactList.add(new String[] {"John", "A", "Doe", Phone, Email, "210 W FOSTER AVE", "APT 208", "HENDERSON", "NV", "89011", "US"});
			ContactList.add(new String[] {"John", "A", "Doe", Phone, Email, "4242 IRVING BLVD", "APT 208", "DALLAS", "TX", "75247", "US"});
			ContactList.add(new String[] {"John", "A", "Doe", Phone, Email, "11201 AMPERE CT", "APT 208", "LOUISVILLE", "KY", "40299", "US"});
			ContactList.add(new String[] {"John", "A", "Doe", Phone, Email, "250 BOBBY JONES EXPY", "APT 208", "AUGUSTA", "GA", "30907", "US"});
			ContactList.add(new String[] {"John", "A", "Doe", Phone, Email, "182 LEWIS RD", "APT 208", "ROYAL OAKS", "CA", "95076", "US"});
			ContactList.add(new String[] {"John", "A", "Doe", Phone, Email, "2635 S WESTERN AVE", "", "CHICAGO", "IL", "60608", "US"});
			ContactList.add(new String[] {"John", "A", "Doe", Phone, Email, "150 DOCKS CORNER RD", "APT 208", "DAYTON", "NJ", "08810", "US"});
			ContactList.add(new String[] {"John", "A", "Doe", Phone, Email, "210 Main St", "", "Levan", "UT", "84639", "US"});
			ContactList.add(new String[] {"John", "A", "Doe", Phone, Email, "3614 DELVERNE RD", "", "BALTIMORE", "MD", "21218", "US"});
			ContactList.add(new String[] {"John", "A", "Doe", Phone, Email, "2784 Prospect-Upper Sandusky Rd S", "APT 202", "Marion", "OH", "43302", "US"});
			ContactList.add(new String[] {"John", "A", "Doe", Phone, Email, "6701-6799 N 19th St", "", "Tampa", "FL", "33610", "US"});
			ContactList.add(new String[] {"John", "A", "Doe", Phone, Email, "10625 Parsons Rd", "", "Duluth", "GA", "30097", "US"});
			ContactList.add(new String[] {"John", "A", "Doe", Phone, Email, "359 Spur Rd", "", "Orofino", "ID", "83544", "US"});
			ContactList.add(new String[] {"John", "A", "Doe", Phone, Email, "765-731 Mayfair Ln", "", "Carmel", "IN", "46032", "US"});
			ContactList.add(new String[] {"John", "A", "Doe", Phone, Email, "2299-2277 380th St", "", "Harcourt", "IA", "50544", "US"});
			ContactList.add(new String[] {"John", "A", "Doe", Phone, Email, "15501-16195 340th Rd", "", "Amherst", "NE", "68812", "US"});
			ContactList.add(new String[] {"John", "A", "Doe", Phone, Email, "11987 Island Daisy Ln", "", "Park Rapids", "MN", "56470", "US"});
			ContactList.add(new String[] {"John", "A", "Doe", Phone, Email, "N W 1101 B C", "", "Windsor", "MO", "65360", "US"});
			ContactList.add(new String[] {"John", "A", "Doe", Phone, Email, "220 Central Ave", "", "Great Falls", "MT", "59401", "US"});
			ContactList.add(new String[] {"John", "A", "Doe", Phone, Email, "259 Sunset Rd SW", "", "Albuquerque", "NM", "87105", "US"});
			ContactList.add(new String[] {"John", "A", "Doe", Phone, Email, "6-120 Fitzsimonds Rd", "", "Jericho", "VT", "05465", "US"});
			ContactList.add(new String[] {"John", "A", "Doe", Phone, Email, "27822-27184 S Meridian Rd", "", "Aurora", "OR", "97002", "US"});
			ContactList.add(new String[] {"John", "A", "Doe", Phone, Email, "100-198 E Florida Ave", "", "Chickasha", "OK", "73018", "US"});
			ContactList.add(new String[] {"John", "A", "Doe", Phone, Email, "8599-8501 11th St SE", "", "Kensal", "ND", "58455", "US"});

			
			
			//These address are working from the WERL page as well
			/*
			ContactList.add(new String[] {"TESTER", "", "ZCHUCKZ", Phone, Email, "32 MEADOW CREST DR", "", "SHERWOOD", "AR", "72120", "US", "6xmc5tpjhrtaymw7yfwshqfao"});//L3FDM061418T172631
			ContactList.add(new String[] {"TESTER", "", "ZTHREEZ", Phone, Email, "58 CABOT ST", "", "HARTFORD", "CT", "06112", "US", "247p0y08t50f3zed2exvaue09"});//L3FDM061418T172722
			ContactList.add(new String[] {"TESTER", "", "ZLILLYZ", Phone, Email, "9133 SUPERIOR DR","", "OLIVEBRANCH", "MS", "38654", "US", "2ojrqs935gpa1ypnicxcspwm5"}); //L3FDM061418T173123
			ContactList.add(new String[] {"TESTER", "", "ZNINEZ", Phone, Email, "75-681 LALII PL", "", "KAILUA KONA", "HI", "96740", "US", "464fn6icyb6e4iw9vetuyz5ir"}); //L3FDM061518T092155
			ContactList.add(new String[] {"TESTER", "", "ZZORANGEZ", Phone, Email, "95 HUTCHINS DR", "", "PORTLAND", "ME", "04102", "US", "3cvnkwdfotey3zldrko808dhf"}); //L3FDM061518T092633
			ContactList.add(new String[] {"TESTER", "", "ZPURPLEZ", Phone, Email, "3614 DELVERNE RD", "", "BALTIMORE", "MD", "21218", "US", "7i4ksltmhnqlxhzbs9j6356ix"}); //L3FDM061518T092659
			*/
		}
		return ContactList.get(i % ContactList.size());
	}
	
	public static void CheckWERLFDM() {
		//String Contact[] = FDMAddress(0);
		
		for (int i = 0; i < ContactList.size();i++) {
			//https://wwwdrt.idev.fedex.com/apps/fdmenrollment/
			try {
				//ChangeURL("https://" + strLevelURL + "/apps/fdmenrollment/");
				//*[@id="1505909112729"]/div/div/div[1]/div/p/span[1]/strong/a

			}catch (Exception e) {
				Helper_Functions.PrintOut("General Error in " + Thread.currentThread().getStackTrace()[2].getMethodName(), false);
			}
			Helper_Functions.PrintOut("Finished " + Thread.currentThread().getStackTrace()[2].getMethodName(), true);
		}
	}

}