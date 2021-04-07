package USRC;

import java.util.ArrayList;
import API_Functions.General_API_Calls;
import SupportClasses.Environment;

public class USRC_Data {
	public String OAuth_Token_Client_ID;
	public String OAuth_Token_Client_Secret;
	public String OAuth_Token;
	public String Level;
	
	public String EnrollmentURL;
	public String CreatePinURL;
	public String PendingAddressURL;
	public String VerifyPinURL;
	public String GetAccountsURL;
	public String CancelEnrollmentURL;
	public String GenericUSRCURL; 
	public String REGCCreateNewUserURL;
	public String ViewUserProfileWIDMURL;
	public String UpdateUserContactInformationWIDMURL;
	public String Validate;
	public String FDMSMS_PinType = "SMS";
	public String FDMPostcard_PinType = "POSTAL";
	
	public static ArrayList<String[]> ContactDetailsList = new ArrayList<String[]>();

	//Stores the data for each individual level
	private static USRC_Data DataClass[] = new USRC_Data[8];
	
	public static USRC_Data USRC_Load(){
		String Level = Environment.getInstance().getLevel();
		int intLevel = Integer.parseInt(Level);
		//if the level details were already loaded then return detail.
		if (DataClass[intLevel] != null) {
			return DataClass[intLevel];
		}
		
		//since the level details have not been loaded load them.
		USRC_Data DC = new USRC_Data();
		DC.Level = Level;
		
		String LevelIdentifier = General_API_Calls.getAPILevelIedntifier(Level, true);
		String APILevelIdentifier = General_API_Calls.getAPILevelIedntifier(Level, false);
  		switch (Level) {
  		case "1":		//expand to user direct end-points later as needed
			DC.OAuth_Token_Client_ID = "l7xx1892f99a6f88470ba29abc141cd7bd8d";
			DC.OAuth_Token_Client_Secret ="a4325d011acf4876b3fe3206931b8f5a";
  			break;
  		case "2":
			DC.OAuth_Token_Client_ID = "l7xx1892f99a6f88470ba29abc141cd7bd8d";
			DC.OAuth_Token_Client_Secret ="a4325d011acf4876b3fe3206931b8f5a";
  			break;
  		case "3":
			DC.OAuth_Token_Client_ID = "l7xx1892f99a6f88470ba29abc141cd7bd8d";
			DC.OAuth_Token_Client_Secret ="a4325d011acf4876b3fe3206931b8f5a";
  			break;
  		case "4":
			/*DC.OAuth_Token_Client_ID = "l7xx4a86a91576b14d4bb7ba81f52470e48d";
			DC.OAuth_Token_Client_Secret ="bb0bc6e8fcba4813989ff50895590f30";*/
			DC.OAuth_Token_Client_ID = "l7xx1841f44a800f4a509aa630b3c009a83d"; // updated on 10-11-19
			DC.OAuth_Token_Client_Secret ="e8e41ab3d70b47eab59e01240772df62";
  			break;
  		case "5":
			DC.OAuth_Token_Client_ID = "";
			DC.OAuth_Token_Client_Secret ="";
  			break;
  		case "6":
			DC.OAuth_Token_Client_ID = "l7xx13465335b1224abb88e4587bed90eb40";
			DC.OAuth_Token_Client_Secret = "dd4ece74e7b04d8f991d4264efdf5100";
  			break;
  		case "7":
			DC.OAuth_Token_Client_ID = "l7xx474b79016a4d4ec5a60bf7a7e5e7e6fe";
			DC.OAuth_Token_Client_Secret ="448399ccafaa4f62a4ed202fc5ef3a01";
  			break;
		}
  		
  		//Load the API URLs
  		if (Level != "1") {//API not applicable to L1
  			DC.EnrollmentURL = APILevelIdentifier + "/deliverymanager/v1/enrollment";
  			DC.CreatePinURL = APILevelIdentifier + "/deliverymanager/v1/pin";
  			DC.PendingAddressURL = APILevelIdentifier + "/deliverymanager/v1/addresses/pending";
  			DC.VerifyPinURL = APILevelIdentifier + "/deliverymanager/v2/enrollment/pin";
  			DC.CancelEnrollmentURL = APILevelIdentifier + "/deliverymanager/v1/deliveryoptions/cancel";
  			DC.Validate = APILevelIdentifier + "/user/v2/login/validate";
  			DC.GetAccountsURL = APILevelIdentifier + "/user/v2/accounts";
  		}
  		
  		DC.GenericUSRCURL = LevelIdentifier + "/userCal/user";	
  		DC.REGCCreateNewUserURL = LevelIdentifier + "/regcal/registration/newfcluser";
  		DC.ViewUserProfileWIDMURL = LevelIdentifier + "/userCal/rest/v2/ViewUserProfileWIDM";
  		DC.UpdateUserContactInformationWIDMURL = LevelIdentifier + "/userCal/rest/v2/UpdateUserContactInformationWIDM";

		ArrayList<String[]> ContactList = new ArrayList<String[]>();
		String Phone = "9011111111", Email = "YouNeedToUpdateThisLater@fedex.com";
		if (Level.contentEquals("6")) {//need to user real data in L6
			//ContactList.add(new String[] {"PavanKumar", "", "Bindela", Phone, Email, "2717 W Royal Ln", "Apt 315", "Irving", "TX", "75063", "US", ""});
			//ContactList.add(new String[] {"Suresh", "", "Dhandapani", Phone, Email, "880 Schilling Farm Road", "Apt 102", "Collierville", "TN", "38017", "US", ""});
			ContactList.add(new String[] {"TESTER", "", "ZCHUCKZ", Phone, Email, "32 MEADOW CREST DR", "", "SHERWOOD", "AR", "72120", "US", "6xmc5tpjhrtaymw7yfwshqfao"});//L3FDM061418T172631
			ContactList.add(new String[] {"TESTER", "", "ZTHREEZ", Phone, Email, "58 CABOT ST", "", "HARTFORD", "CT", "06112", "US", "247p0y08t50f3zed2exvaue09"});//L3FDM061418T172722
			ContactList.add(new String[] {"TESTER", "", "ZLILLYZ", Phone, Email, "9133 SUPERIOR DR","", "OLIVEBRANCH", "MS", "38654", "US", "2ojrqs935gpa1ypnicxcspwm5"}); //L3FDM061418T173123
			ContactList.add(new String[] {"TESTER", "", "ZNINEZ", Phone, Email, "75-681 LALII PL", "", "KAILUA KONA", "HI", "96740", "US", "464fn6icyb6e4iw9vetuyz5ir"}); //L3FDM061518T092155
			ContactList.add(new String[] {"TESTER", "", "ZZORANGEZ", Phone, Email, "95 HUTCHINS DR", "", "PORTLAND", "ME", "04102", "US", "3cvnkwdfotey3zldrko808dhf"}); //L3FDM061518T092633
			ContactList.add(new String[] {"TESTER", "", "ZPURPLEZ", Phone, Email, "3614 DELVERNE RD", "", "BALTIMORE", "MD", "21218", "US", "7i4ksltmhnqlxhzbs9j6356ix"}); //L3FDM061518T092659
			
		}else if (Level.contentEquals("7")) {//need to user real data in LP
			// ContactList.add(new String[] {"naga", "", "vijayan", Phone, Email, "5340 ROSE RIDGE LN", "", "COLORADO SPRINGS", "CO", "80917", "US", "5t2ypr8g64nhb6kkg0m1eyfu7"});
			// ContactList.add(new String[] {"First", "", "Last", Phone, Email, "9427 Ruidosa Trl", "", "Irving", "TX", "75063", "US", "5g0d06szxzxyh5diyhyu4lnwi"});
			ContactList.add(new String[] {"First", "", "Last", Phone, Email, "7939 Silver Lake Ln", "Apt 303", "Memphis", "TN", "38119", "US", "3t2h8ncpcsjngrs20zhedg1s2"});
		}else {
			//ContactList.add(new String[] {"Udaya", "", "Uriti", Phone, Email, "9427 Ruidosa Trl", "", "Irving", "TX", "75063", "US", "5g0d06szxzxyh5diyhyu4lnwi"});
			//ContactList.add(new String[] {"Resmi", "", "Raveendran", Phone, Email, "387 Main Street", "3760 QNFW", "WACO", "TX", "76712", "US", "38mynlmeqanwftczn0yyg7jlz"});
			//ContactList.add(new String[] {"FIRSTNAME", "", "LASTNAME", Phone, Email, "2519 W ROYAL LN", "", "IRVING", "TX", "75063", "US", "c2cj08lvbmwkv5j1r5k38l1g"});
			// ContactList.add(new String[] {"Udaya", "", "Uriti", Phone, Email, "9427 Ruidosa Trl", "", "IRVING", "TX", "75063", "US", "5g0d06szxzxyh5diyhyu4lnwi"});
			
			//These address are working from the WERL page as well         //Please note this data was setup for when using Nuestar as the validator
			ContactList.add(new String[] {"TESTER", "", "ZCHUCKZ", "4694996524", "FEDEXA_USERA@FEDEX.COM", "32 MEADOW CREST DR", "", "SHERWOOD", "AR", "72120", "US", "6xmc5tpjhrtaymw7yfwshqfao"});//L3FDM061418T172631
			ContactList.add(new String[] {"TESTER", "", "ZTHREEZ", "4694996524", "FEDEXB_USERB@FEDEX.COM", "58 CABOT ST", "", "HARTFORD", "CT", "06112", "US", "247p0y08t50f3zed2exvaue09"});//L3FDM061418T172722
			ContactList.add(new String[] {"TESTER", "", "ZLILLYZ", Phone, "FEDEXC_USERC@FEDEX.COM", "9133 SUPERIOR DR","", "OLIVEBRANCH", "MS", "38654", "US", "2ojrqs935gpa1ypnicxcspwm5"}); //L3FDM061418T173123
			ContactList.add(new String[] {"TESTER", "", "ZNINEZ", Phone, "FEDEXD_USERD@FEDEX.COM", "75-681 LALII PL", "", "KAILUA KONA", "HI", "96740", "US", "464fn6icyb6e4iw9vetuyz5ir"}); //L3FDM061518T092155
			ContactList.add(new String[] {"TESTER", "", "ZZORANGEZ", Phone, "FEDEXE_USERE@FEDEX.COM", "95 HUTCHINS DR", "", "PORTLAND", "ME", "04102", "US", "3cvnkwdfotey3zldrko808dhf"}); //L3FDM061518T092633
			ContactList.add(new String[] {"TESTER", "", "ZPURPLEZ", Phone, Email, "3614 DELVERNE RD", "", "BALTIMORE", "MD", "21218", "US", "7i4ksltmhnqlxhzbs9j6356ix"}); //L3FDM061518T092659
			
			//These address will only work through direct call
			ContactList.add(new String[] {"TESTER", "", "ZFAMILYZ", Phone, Email, "891 FEDERAL RIDGE RD", "APT 202", "COLLIERVILLE", "TN", "38125", "US", "44k0o0ipf25thfcyl8svm65zz"});
			ContactList.add(new String[] {"TESTER", "", "ZTWOZ", Phone, "FEDEXF_USERF@FEDEX.COM", "329 MADISON ST", "APT 202", "DENVER", "CO", "80206", "US", "7hqf1rpftonlfthiifapqp45k"});
			ContactList.add(new String[] {"TESTER", "", "ZFOURZ", Phone, Email, "310 HAINES ST", "", "NEWARK", "DE", "19717", "US", "4cg4ekdc7zs9n40bhkgt3c7wj"});
			ContactList.add(new String[] {"TESTER", "", "ZFAMILYZ", Phone, Email, "3935 MISSION HILLS DR", "APT 203", "MEMPHIS", "TN", "38017", "US", "1lzav5yemhi3x440fnow7x44p"});
			ContactList.add(new String[] {"TESTER", "", "ZMAPLEZ", Phone, Email, "1203 BEARTOOTH DR","", "LAUREL", "MT", "59044", "US", "1gmjbut6uu5u6p8uy5f66u8r2"});
			ContactList.add(new String[] {"TESTER", "M", "ZRAZORBACKZ", Phone, Email, "6350 HEDGEWOOD DR", "", "ALLENTOWN", "PA", "18106", "US", "4amkkxogwfzill5yytaiun4ew"});
			ContactList.add(new String[] {"TESTER", "", "ZNINEZ", Phone, Email, "75-681 LALII PL", "", "KAILUA KONA", "HI", "96740", "US", "464fn6icyb6e4iw9vetuyz5ir"});
			ContactList.add(new String[] {"TESTER", "", "ZTENZ", Phone, Email, "1430 E 17TH ST", "", "IDAHO FALLS", "ID", "83404", "US", "6ty7pkr5m2ll10avo0jue5yop"});
			ContactList.add(new String[] {"TESTER", "", "ZELEVENZ", Phone, Email, "410 W WASHINGTON ST", "", "CASEYVILLE", "IL", "62232", "US", "3mg730s7lokdmn90hi44lgw2h"});
			ContactList.add(new String[] {"TESTER", "", "ZTHIRTEENZ", Phone, Email, "8527 UNIVERSITY BLVD", "STE 99", "DES MOINES", "IA", "50325", "US", ""});
			ContactList.add(new String[] {"TESTER", "", "ZBLUEZ", Phone, Email, "2206 URBANDALE ST", "", "SHREVEPORT", "LA", "71118", "US", "ki87l0p7fq4frpue8y45qlzv"});
			ContactList.add(new String[] {"TESTER", "", "ZZORANGEZ", Phone, Email, "95 HUTCHINS DR", "", "PORTLAND", "ME", "04102", "US", "3cvnkwdfotey3zldrko808dhf"});
			ContactList.add(new String[] {"TESTER", "", "ZPURPLEZ", Phone, Email, "3614 DELVERNE RD", "", "BALTIMORE", "MD", "21218", "US", "7i4ksltmhnqlxhzbs9j6356ix"});
		}
		ContactDetailsList = ContactList;
		
		DataClass[intLevel] = DC;
		return DC;
	}

	public static String[] getContactDetails(int con) {
		if (ContactDetailsList.size() > con){
			return ContactDetailsList.get(con);
		}else {
			return getContactDetails(con % ContactDetailsList.size());
		}
	}

	public static String getOAuthToken() {
		String Level = Environment.getInstance().getLevel();
		int intLevel = Integer.parseInt(Level);
		if (DataClass[intLevel] == null) {
			USRC_Load();
		}
		if (DataClass[intLevel].OAuth_Token == null) {
			// Generate a new OAuth_Token
			DataClass[intLevel].OAuth_Token = General_API_Calls.getAuthToken(DataClass[intLevel].OAuth_Token_Client_ID, DataClass[intLevel].OAuth_Token_Client_Secret);
		}
		
		return DataClass[intLevel].OAuth_Token;
	}

}