package WPRL_Application;

import java.util.ArrayList;
import org.openqa.selenium.By;

import MFAC_Application.MFAC_API_Endpoints;
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
			String Pin = MFAC_API_Endpoints.IssuePinAPI_External(UUID + "-" + ShareID, "postal");
			//check the confirmation page that the postcard has been sent
			WebDriver_Functions.WaitForText(By.xpath("//*[@id='angularWerl']/app-root/app-postal-confirmation/div/h1"), "Your activation code is on the way");
			WebDriver_Functions.ChangeURL("WPRL_FDM", "US", false);//navigate to WPRL FDM page
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
			ContactList.add(new String[] {"John", "A", "Doe", Phone, Email, "58 CABOT ST", "", "HARTFORD", "CT", "6112", "US", "247p0y08t50f3zed2exvaue09"});
			ContactList.add(new String[] {"John", "A", "Doe", Phone, Email, "9133 SUPERIOR DR", "", "OLIVEBRANCH", "MS", "38654", "US", "2ojrqs935gpa1ypnicxcspwm5"});
			ContactList.add(new String[] {"John", "A", "Doe", Phone, Email, "75-681 LALII PL", "", "KAILUA KONA", "HI", "96740", "US", "464fn6icyb6e4iw9vetuyz5ir"});
			ContactList.add(new String[] {"John", "A", "Doe", Phone, Email, "95 HUTCHINS DR", "", "PORTLAND", "ME", "4102", "US", "3cvnkwdfotey3zldrko808dhf"});
			ContactList.add(new String[] {"John", "A", "Doe", Phone, Email, "329 MADISON ST APT 202", "", "DENVER", "MD", "80206", "US", "7hqf1rpftonlfthiifapqp45k"});
			ContactList.add(new String[] {"John", "A", "Doe", Phone, Email, "1622 Colloquium Dr", "APT 208", "HENDERSON", "NV", "89014", "US", "6v97hdvv359dhctbtiwxpg1ta"});
			ContactList.add(new String[] {"John", "A", "Doe", Phone, Email, "515 Kenwood Ave", "APT 208", "DAYTON", "OH", "45406", "US", "72n99a8430p2mx2b4ko4d28qk"});
			ContactList.add(new String[] {"John", "A", "Doe", Phone, Email, "8599-8501 11th St SE", "", "Kensal", "ND", "58455", "US", "240rzltzd9btm85wxwvd78rww"});
			ContactList.add(new String[] {"John", "A", "Doe", Phone, Email, "27822-27184 S Meridian Rd", "", "Aurora", "OR", "97002", "US", "3wwkvkzrpsxgax31vxbv5qxb5"});
			ContactList.add(new String[] {"John", "A", "Doe", Phone, Email, "359 Spur Rd", "", "Orofino", "ID", "83544", "US", "1ug631gi15scz0rxur0hrx8qh"});
			ContactList.add(new String[] {"John", "A", "Doe", Phone, Email, "3614 DELVERNE RD", "", "BALTIMORE", "MD", "21218", "US", "7i4ksltmhnqlxhzbs9j6356ix"});
			ContactList.add(new String[] {"John", "A", "Doe", Phone, Email, "705 W 22nd St", "", "Higginsville", "MO", "64037", "US", "68pkv8fdgamily7onk626n7xt"});
			ContactList.add(new String[] {"John", "A", "Doe", Phone, Email, "1047 Osage Ave", "", "Richland", "IA", "52585", "US", "6iub8xcoggkqhcaiwolwqc1pu"});
			ContactList.add(new String[] {"John", "A", "Doe", Phone, Email, "2916 Co Rd 130", "", "Fort Ripley", "MN", "56449", "US", "20rpuzw2vy8mho3jbtxolcjyl"});
			ContactList.add(new String[] {"John", "A", "Doe", Phone, Email, "1308 Almond Ave", "", "Madison", "FL", "32340", "US", "1rg816yy05wsscduu2r9ft3zt"});
			ContactList.add(new String[] {"John", "A", "Doe", Phone, Email, "1223 S 15th St", "", "Chickasha", "OK", "73018", "US", "6ywz8db0rb1q71dbtykbkubel"});
			ContactList.add(new String[] {"John", "A", "Doe", Phone, Email, "4802 S Loomis Blvd", "", "CHICAGO", "IL", "60609", "US", "1mdm2q039dobzsq76tn3q18eb"});
			ContactList.add(new String[] {"John", "A", "Doe", Phone, Email, "4005 California Ave", "APT 208", "Modesto", "CA", "95358", "US", "72tnisxa7rocqbgsahjwhzwn8"});
			ContactList.add(new String[] {"John", "A", "Doe", Phone, Email, "8931 Preston Rd", "APT 208", "DALLAS", "TX", "75247", "US", "7hgby17d1j4wgo3oolifmijdl"});
			ContactList.add(new String[] {"John", "A", "Doe", Phone, Email, "220 E Southland Blvd", "APT 208", "LOUISVILLE", "KY", "40214", "US", "2zpzble6zzsqoos0c7z4wlf4d"});
			ContactList.add(new String[] {"John", "A", "Doe", Phone, Email, "1650 Ironwood Ct", "APT 208", "AUGUSTA", "GA", "30905", "US", "20462kzvl8lajb7v6k3u4ve6m"});
			ContactList.add(new String[] {"John", "A", "Doe", Phone, Email, "21380 Goose Rd ", "", "Ellendale", "DE", "19941", "US", "1qp291cxx2s8xnxtqvuue9ki1"});
			ContactList.add(new String[] {"John", "A", "Doe", Phone, Email, "515 Kenwood Ave", "APT 208", "DAYTON", "NJ", "45406", "US", "72n99a8430p2mx2b4ko4d28qk"});
			ContactList.add(new String[] {"John", "A", "Doe", Phone, Email, "3151 Alberta Pl", "", "West Jordan", "UT", "84084", "US", "29dqbpmahuuic8bps0sszs1vj"});
			ContactList.add(new String[] {"John", "A", "Doe", Phone, Email, "4861 LA-121861 LA-181 S 5  5 Rd", "", "Boyce", "LA", "71409", "US", "6f6zcjs39l2vm11fzjxbobhyc"});
			ContactList.add(new String[] {"John", "A", "Doe", Phone, Email, "181 S 5 Rd", "", "Morrill", "NE", "69358", "US", "6ptezn20228z2mf7v11s5rj57"});
			ContactList.add(new String[] {"John", "A", "Doe", Phone, Email, "180 Trout St", "", "Hartsel", "CO", "80449", "US", "3rrx02yst8bxdze5uybo08d57"});
			ContactList.add(new String[] {"John", "A", "Doe", Phone, Email, "2115 Sand Hill Rd", "", "Marriottsville", "MD", "21104", "US", "5acsnolr5izxaqaplh664rf6y"});
			ContactList.add(new String[] {"John", "A", "Doe", Phone, Email, "2800 E 550 S", "", "Star City", "IN", "46985", "US", "32zmbi1gdn4v627885l146e09"});
			ContactList.add(new String[] {"John", "A", "Doe", Phone, Email, "1437 Augusta Hwy", "", "Round O", "SC", "29474", "US", "75ovwgbx781nz4g12214sbwp6"});

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