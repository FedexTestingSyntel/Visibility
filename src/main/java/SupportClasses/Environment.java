package SupportClasses;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.testng.SkipException;
import org.testng.annotations.Optional;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;

import Data_Structures.Account_Data;
import Data_Structures.Credit_Card_Data;
import Data_Structures.Enrollment_Data;
import Data_Structures.Tax_Data;
import Data_Structures.User_Data;

public class Environment {
	public static String LevelsToTest;
	public static String DummyInvoiceOne = "750000000", DummyInvoiceTwo = "750000001";
	private static Environment instance = new Environment();
		
	private Environment(){
		//Do-nothing..Do not allow to initialize this class from outside
	}
	
	public static Environment getInstance(){
		return instance;
	}
	
	public static void SetLevelsToTest(String Levels) {
		if (LevelsToTest == null) {
			LevelsToTest = Levels;
			System.err.println("Levels set to " + Levels);
		}else {
			System.err.println("Levels has already been set from XML. Will execute with " + LevelsToTest);
		}
	}
	   		
	//ThreadLocal variable of what level currently testing.
	ThreadLocal<String> Level = new ThreadLocal<String>(){ 
		@Override
		synchronized protected String initialValue(){
			return "";
		}
	};
	
	//set the level that is being tested by current instance
	public void setLevel(String L){ 
		Level.set(L);
	}
	
	public String getLevel() {
		if (Level.get() == null) {
			System.err.print("Environment level not set: Please check Environment class" );
		}
		return Level.get();
	}
	
	@Test (priority = 0)//will run on data sent in XML to configure levels
	@Parameters("Level")
	public void SetEvironmentLevel(@Optional String Levels) {
		if (Levels != null) {
			SetLevelsToTest(Levels);
		}
		throw new SkipException("Forcing skip, will be deleted from report later");
	}
	
	public static String[][] getCountryList(String Identifier){
		Identifier = Identifier.toUpperCase();
		String FullCountryList[][] = new String[][]{{"US", "United States"}, {"AF", "Afghanistan"}, {"AL", "Albania"}, {"DZ", "Algeria"}, {"AS", "American Samoa"}, {"AD", "Andorra"}, {"AO", "Angola"}, {"AI", "Anguilla"}, {"AG", "Antigua and Barbuda"}, {"AR", "Argentina"}, {"AM", "Armenia"}, {"AW", "Aruba"}, {"AU", "Australia"}, {"AT", "Austria"}, {"AZ", "Azerbaijan"}, {"BS", "Bahamas"}, {"BH", "Bahrain"}, {"BD", "Bangladesh"}, {"BB", "Barbados"}, {"BY", "Belarus"}, {"BE", "Belgium"}, {"BZ", "Belize"}, {"BJ", "Benin"}, {"BM", "Bermuda"}, {"BT", "Bhutan"}, {"BO", "Bolivia"}, {"BQ", "Bonaire/Saba/Sint Eustatius"}, {"BA", "Bosnia-Herzegovina"}, {"BW", "Botswana"}, {"BR", "Brazil"}, {"VG", "Britain Virgin Islands"}, {"BN", "Brunei"}, {"BG", "Bulgaria"}, {"BF", "Burkina Faso"}, {"BI", "Burundi"}, {"KH", "Cambodia"}, {"CM", "Cameroon"}, {"CA", "Canada"}, {"CV", "Cape Verde Islands"}, {"KY", "Cayman Islands"}, {"CF", "Central African Republic"}, {"TD", "Chad"}, {"CL", "Chile"}, {"CN", "China"}, {"CO", "Colombia"}, {"KM", "Comoros"}, {"CG", "Congo"}, {"CK", "Cook Islands"}, {"CR", "Costa Rica"}, {"HR", "Croatia"}, {"CU", "Cuba"}, {"CW", "Curacao"}, {"CY", "Cyprus"}, {"CZ", "Czech Republic"}, {"CD", "Democratic Republic of Congo"}, {"DK", "Denmark"}, {"DJ", "Djibouti"}, {"DM", "Dominica"}, {"DO", "Dominican Republic"}, {"TL", "East Timor"}, {"EC", "Ecuador"}, {"EG", "Egypt"}, {"SV", "El Salvador"}, {"GQ", "Equatorial Guinea"}, {"ER", "Eritrea"}, {"EE", "Estonia"}, {"ET", "Ethiopia"}, {"FO", "Faroe Islands"}, {"FJ", "Fiji"}, {"FI", "Finland"}, {"FR", "France"}, {"GF", "French Guiana"}, {"PF", "French Polynesia"}, {"GA", "Gabon"}, {"GM", "Gambia"}, {"GE", "Georgia, Republic of"}, {"DE", "Germany"}, {"GH", "Ghana"}, {"GI", "Gibraltar"}, {"GR", "Greece"}, {"GL", "Greenland"}, {"GD", "Grenada"}, {"GP", "Guadeloupe"}, {"GU", "Guam"}, {"GT", "Guatemala"}, {"GN", "Guinea"}, {"GW", "Guinea-Bissau"}, {"GY", "Guyana"}, {"HT", "Haiti"}, {"HN", "Honduras"}, {"HK", "Hong Kong"}, {"HU", "Hungary"}, {"IS", "Iceland"}, {"IN", "India"}, {"ID", "Indonesia"}, {"IR", "Iran"}, {"IQ", "Iraq Republic"}, {"IE", "Ireland"}, {"IL", "Israel"}, {"IT", "Italy"}, {"CI", "Ivory Coast"}, {"JM", "Jamaica"}, {"JP", "Japan"}, {"JO", "Jordan"}, {"KZ", "Kazakhstan"}, {"KE", "Kenya"}, {"KI", "Kiribati"}, {"KW", "Kuwait"}, {"KG", "Kyrgyzstan"}, {"LA", "Laos"}, {"LV", "Latvia"}, {"LB", "Lebanon"}, {"LS", "Lesotho"}, {"LR", "Liberia"}, {"LY", "Libya"}, {"LI", "Liechtenstein"}, {"LT", "Lithuania"}, {"LU", "Luxembourg"}, {"MO", "Macau"}, {"MK", "Macedonia"}, {"MG", "Madagascar"}, {"MW", "Malawi"}, {"MY", "Malaysia"}, {"MV", "Maldives"}, {"ML", "Mali"}, {"MT", "Malta"}, {"MH", "Marshall Islands"}, {"MQ", "Martinique"}, {"MR", "Mauritania"}, {"MU", "Mauritius"}, {"MX", "Mexico"}, {"FM", "Micronesia"}, {"MD", "Moldova"}, {"MC", "Monaco"}, {"MN", "Mongolia"}, {"ME", "Montenegro"}, {"MS", "Montserrat"}, {"MA", "Morocco"}, {"MZ", "Mozambique"}, {"MM", "Myanmar/Burma"}, {"NA", "Namibia"}, {"NR", "Nauru"}, {"NP", "Nepal"}, {"NL", "Netherlands"}, {"AN", "Netherlands Antilles"}, {"NC", "New Caledonia"}, {"NZ", "New Zealand"}, {"NI", "Nicaragua"}, {"NE", "Niger"}, {"NG", "Nigeria"}, {"NU", "Niue"}, {"NO", "Norway"}, {"OM", "Oman"}, {"PK", "Pakistan"}, {"PW", "Palau"}, {"PS", "Palestine"}, {"PA", "Panama"}, {"PG", "Papua New Guinea"}, {"PY", "Paraguay"}, {"PE", "Peru"}, {"PH", "Philippines"}, {"PL", "Poland"}, {"PT", "Portugal"}, {"PR", "Puerto Rico"}, {"QA", "Qatar"}, {"RE", "Reunion Island"}, {"RO", "Romania"}, {"RU", "Russia"}, {"RW", "Rwanda"}, {"MF", "Saint Martin"}, {"MP", "Saipan"}, {"WS", "Samoa"}, {"SM", "San Marino"}, {"ST", "Sao Tome & Principe"}, {"SA", "Saudi Arabia"}, {"SN", "Senegal"}, {"RS", "Serbia"}, {"SC", "Seychelles"}, {"SL", "Sierra Leone"}, {"SG", "Singapore"}, {"SK", "Slovak Republic"}, {"SI", "Slovenia"}, {"SB", "Solomon Islands"}, {"SO", "Somalia"}, {"ZA", "South Africa"}, {"KR", "South Korea"}, {"ES", "Spain"}, {"LK", "Sri Lanka"}, {"KN", "St Kitts and Nevis"}, {"LC", "St Lucia"}, {"SX", "St Maarten"}, {"VC", "St Vincent"}, {"GP", "St. Barthelemy"}, {"SD", "Sudan"}, {"SR", "Suriname"}, {"SZ", "Swaziland"}, {"SE", "Sweden"}, {"CH", "Switzerland"}, {"SY", "Syria"}, {"TW", "Taiwan"}, {"TJ", "Tajikistan"}, {"TZ", "Tanzania"}, {"TH", "Thailand"}, {"TG", "Togo"}, {"TK", "Tokelau Is."}, {"TO", "Tonga"}, {"TT", "Trinidad And Tobago"}, {"TN", "Tunisia"}, {"TR", "Turkey"}, {"TM", "Turkmenistan"}, {"TC", "Turks And Caicos"}, {"TV", "Tuvalu"}, {"UG", "Uganda"}, {"UA", "Ukraine"}, {"AE", "United Arab Emirates"}, {"GB", "United Kingdom"}, {"UY", "Uruguay"}, {"VI", "US Virgin Islands/St John/St Thomas"}, {"UZ", "Uzbekistan"}, {"VU", "Vanuatu"}, {"VA", "Vatican City State"}, {"VE", "Venezuela"}, {"VN", "Vietnam"}, {"WF", "Wallis and Futuna"}, {"YE", "Yemen"}, {"ZM", "Zambia"}, {"ZW", "Zimbabwe"}};
		String HighCountryList[][] = new String[][]{{"US", "United States"},{"AU", "Australia"},{"CA", "Canada"},{"GB", "United Kingdom"},{"BR", "Brazil"},{"AE", "United Arab Emirates"}};
		String SmokeCountryList[][] = new String[][]{{"US", "United States"}};
		
		//return smoke list by default
		String ReturnList[][] = SmokeCountryList;		//will return smoke test by default 
		if (Identifier.contentEquals("SMOKE")){
			ReturnList = SmokeCountryList;
		}else if (Identifier.contentEquals("FULL")){
			ReturnList = FullCountryList;
		}else if (Identifier.contentEquals("HIGH")){
			ReturnList = HighCountryList;
		}else {
			// if the country code is sent
			if (Identifier.length() == 2) {
				for (int i = 0; i < FullCountryList.length; i++) {
					if (FullCountryList[i][0].contentEquals(Identifier)) {
						ReturnList[0] = FullCountryList[i];
					}
				}
			}
		}
		
		System.out.println("getCountryList is returning - " + Arrays.deepToString(ReturnList));
		return ReturnList;
	}
	
	public static ArrayList<String[]> getAccountList(String Level){
		//load the data from excel
		ArrayList<String[]> AccountsAlreadyCreated = Helper_Functions.getExcelData(Helper_Functions.DataDirectory + "\\AddressDetails.xls", "Account_Numbers");
		//get the headers of the excel that will be the first line
		String Headers[] = AccountsAlreadyCreated.get(0);
		int LevelPosition = -1;
		
		//find the position of the excel where the level is stored
		for (String s: Headers) {
			LevelPosition++;
			if (s.contentEquals("Level")) {
				break;
			}
		}
		
		//remove all rows that are not the same as the level that is being sent to this function
		//please note that starting at position 1 since the headers will remain.
		for (int i = 1; i < AccountsAlreadyCreated.size(); i++) {
			if (!AccountsAlreadyCreated.get(i)[LevelPosition].contentEquals(Level)) {
				AccountsAlreadyCreated.remove(i);
				i--;
			}
		}
		
		//return the list of accounts specific to the level sent.
		return AccountsAlreadyCreated;
	}
	
	public static Account_Data[] getAccountDetails(String Level){
		//if the data is already loaded then return the values
		ArrayList<String[]> AccountsAlreadyCreated = Environment.getAccountList(Level);
		AccountsAlreadyCreated.removeAll(Collections.singleton(null));
		Account_Data Account_Details[] = new Account_Data[AccountsAlreadyCreated.size()];
		String Headers[] = AccountsAlreadyCreated.get(0);
		for (int i = 1; i < AccountsAlreadyCreated.size(); i++) {
			String Row[] = AccountsAlreadyCreated.get(i);
			//Helper_Functions.PrintOut(Arrays.toString(Row), false);
			Account_Details[i - 1] = new Account_Data();
			//will load the dummy email address by default.
			Account_Details[i - 1].Email = Helper_Functions.MyEmail;
			for (int j = 0; j <Headers.length; j++) {
				int pos = i - 1;
				if (Row[j] == null) {
					Row[j] = "";
				}
				switch (Headers[j]) {
		  		case "Level":
		  			Account_Details[pos].Level = Row[j];
		  			break;
		  		case "Shipping_Address_Line_1":
		  			Account_Details[pos].Shipping_Address_Info.Address_Line_1 = Row[j];
		  			break;
		  		case "Shipping_Address_Line_2":
		  			Account_Details[pos].Shipping_Address_Info.Address_Line_2 = Row[j];
		  			break;
		  		case "Shipping_City":
		  			Account_Details[pos].Shipping_Address_Info.City = Row[j];
		  			break;
		  		case "Shipping_State":
		  			Account_Details[pos].Shipping_Address_Info.State = Row[j];
		  			break;
		  		case "Shipping_State_Code":
		  			Account_Details[pos].Shipping_Address_Info.State_Code = Row[j];
		  			break;
		  		case "Shipping_Phone_Number":
		  			Account_Details[pos].Shipping_Address_Info.Phone_Number = Row[j];
		  			break;
		  		case "Shipping_Zip":
		  			Account_Details[pos].Shipping_Address_Info.Zip = Row[j];
		  			break;
		  		case "Shipping_Country_Code":
		  			Account_Details[pos].Shipping_Address_Info.Country_Code = Row[j];
		  			break;
		  		case "Shipping_Region":
		  			Account_Details[pos].Shipping_Address_Info.Region = Row[j];
		  			break;
		  		case "Shipping_Country":
		  			Account_Details[pos].Shipping_Address_Info.Country = Row[j];
		  			break;
		  		case "Shipping_Share_Id":
		  			Account_Details[pos].Shipping_Address_Info.Share_Id = Row[j];
		  			break;
		  		case "Billing_Address_Line_1":
		  			Account_Details[pos].Billing_Address_Info.Address_Line_1 = Row[j];
		  			break;
		  		case "Billing_Address_Line_2":
		  			Account_Details[pos].Billing_Address_Info.Address_Line_2 = Row[j];
		  			break;
		  		case "Billing_City":
		  			Account_Details[pos].Billing_Address_Info.City = Row[j];
		  			break;
		  		case "Billing_State":
		  			Account_Details[pos].Billing_Address_Info.State = Row[j];
		  			break;
		  		case "Billing_State_Code":
		  			Account_Details[pos].Billing_Address_Info.State_Code = Row[j];
		  			break;
		  		case "Billing_Phone_Number":
		  			Account_Details[pos].Billing_Address_Info.Phone_Number = Row[j];
		  			break;
		  		case "Billing_Zip":
		  			Account_Details[pos].Billing_Address_Info.Zip = Row[j];
		  			break;
		  		case "Billing_Country_Code":
		  			Account_Details[pos].Billing_Address_Info.Country_Code = Row[j];
		  			break;
		  		case "Billing_Region":
		  			Account_Details[pos].Billing_Address_Info.Region = Row[j];
		  			break;
		  		case "Billing_Country":
		  			Account_Details[pos].Billing_Address_Info.Country = Row[j];
		  			break;
		  		case "Billing_Share_Id":
		  			Account_Details[pos].Billing_Address_Info.Share_Id = Row[j];
		  			break;
		  		case "Account_Number":
		  			Account_Details[pos].Account_Number = Row[j];
		  			if (Account_Details[pos].Billing_Address_Info.Country_Code != null && !Account_Details[pos].Billing_Address_Info.Country_Code.contentEquals("")) {
		  				Account_Details[pos].Account_Nickname = Row[j] + "_" + Account_Details[pos].Billing_Address_Info.Country_Code;
		  			}else {
		  				Account_Details[pos].Account_Nickname = Row[j] + "_Acc";
		  			}
		  			/*
		  			if (Account_Details[pos].Account_Number.contains("641304840")) { //Used for debug specific account lookup
		  				Helper_Functions.PrintOut(Arrays.toString(Headers));
		  				Helper_Functions.PrintOut(Arrays.toString(Row));
		  			}
		  			*/
		  			break;
		  		case "Credit_Card_Type":
		  			Account_Details[pos].Credit_Card_Type = Row[j];
		  			break;
		  		case "Credit_Card_Number":
		  			Account_Details[pos].Credit_Card_Number = Row[j];
		  			break;
		  		case "Credit_Card_CVV":
		  			Account_Details[pos].Credit_Card_CVV = Row[j];
		  			break;
		  		case "Credit_Card_Expiration_Month":
		  			Account_Details[pos].Credit_Card_Expiration_Month = Row[j];
		  			break;
		  		case "Credit_Card_Expiration_Year":
		  			Account_Details[pos].Credit_Card_Expiration_Year = Row[j];
		  			break;
		  		case "Invoice_Number_A":
		  			Account_Details[pos].Invoice_Number_A = Row[j];
		  			break;
		  		case "Invoice_Number_B":
		  			Account_Details[pos].Invoice_Number_B = Row[j];
		  			break;
		  		case "Account_Type":
		  			Account_Details[pos].Account_Type = Row[j];
		  			break;
		  		case "Tax_ID_One":
		  			Account_Details[pos].Tax_ID_One = Row[j];
		  			break;
		  		case "Tax_ID_Two":
		  			Account_Details[pos].Tax_ID_Two = Row[j];
		  			break;
		  		case "First_Name":
		  			Account_Details[pos].FirstName = Row[j];
		  			break;
		  		case "Last_Name":
		  			Account_Details[pos].LastName = Row[j];
		  			break;
				}//end switch
			}
		}
  		return Account_Details;
	}
	
	public static Enrollment_Data[] getEnrollmentDetails(int Level) {
		//if the data is already loaded then return the values
		ArrayList<String[]> EnrollmentDetails = new ArrayList<String[]>();
		
		EnrollmentDetails = Helper_Functions.getExcelData(Helper_Functions.DataDirectory + "\\EnrollmentIds.xls",  "EnrollmentIds");//load the relevant information from excel file.
		
		Enrollment_Data Enrollment_D[] = new Enrollment_Data[EnrollmentDetails.size() - 1];
		String Headers[] = EnrollmentDetails.get(0);
		String LevelURL = WebDriver_Functions.LevelUrlReturn(Level);
		for (int i = 1; i < EnrollmentDetails.size(); i++) {
			String Row[] = EnrollmentDetails.get(i);

			Enrollment_D[i -1] = new Enrollment_Data();
			for (int j = 0; j <Headers.length; j++) {
				int pos = i - 1;
				switch (Headers[j]) {
		  		case "ENROLLMENT_ID":
		  			Enrollment_D[pos].ENROLLMENT_ID = Row[j];
		  			break;
		  		case "COUNTRY_CODE":
		  			Enrollment_D[pos].COUNTRY_CODE = Row[j];
		  			break;
		  		case "PROGRAM_NAME":
		  			Enrollment_D[pos].PROGRAM_NAME = Row[j];
		  			break;
		  		case "PASSCODE":
		  			Enrollment_D[pos].PASSCODE = Row[j];
		  			break;
		  		case "MEMBERSHIP_ID":
		  			Enrollment_D[pos].MEMBERSHIP_ID = Row[j];
		  			break;
		  		case "IDENTIFIER":
		  			Enrollment_D[pos].IDENTIFIER = Row[j];
		  			break;
		  		case "AEM_LINK":
		  			//The url in the excel should be for production
		  			Enrollment_D[pos].AEM_LINK = Row[j].replace("https://www.fedex.com", LevelURL);
		  			break;
				}//end switch
			}
		}
		
  		return Enrollment_D;
	}
	
	//will only return valid
	public static Tax_Data getTaxDetails(String CountryCode) {
		Tax_Data AllTD[] = getTaxDetails();
		
		for (Tax_Data TD: AllTD) {
			if (TD.COUNTRY_CODE.contentEquals(CountryCode) && TD.ERROR_CODE.contentEquals("Valid")) {
				return TD;
			}
		}
		return null;
	}
	
	public static Tax_Data[] getTaxDetails() {
		//if the data is already loaded then return the values
		ArrayList<String[]> TaxDetails = new ArrayList<String[]>();
		
		TaxDetails = Helper_Functions.getExcelData(Helper_Functions.DataDirectory + "\\TaxData.xls",  "TaxIds");//load the relevant information from excel file.
		
		Tax_Data Tax_D[] = new Tax_Data[TaxDetails.size() - 1];
		String Headers[] = TaxDetails.get(0);
		for (int i = 1; i < TaxDetails.size(); i++) {
			String Row[] = TaxDetails.get(i);

			Tax_D[i -1] = new Tax_Data();
			for (int j = 0; j <Headers.length; j++) {
				int pos = i - 1;
				switch (Headers[j]) {
				case "COUNTRY_CODE":
		  			Tax_D[pos].COUNTRY_CODE = Row[j];
		  			break;
				case "TAX_ID":
		  			Tax_D[pos].TAX_ID = Row[j];
		  			break;
				case "STATE_TAX_ID":
		  			Tax_D[pos].STATE_TAX_ID = Row[j];
		  			break;
				case "ERROR_CODE":
		  			Tax_D[pos].ERROR_CODE = Row[j];
		  			break;
				case "TYPE":
		  			Tax_D[pos].TYPE = Row[j];
		  			break;
				case "REQUIREMENT":
		  			Tax_D[pos].REQUIREMENT = Row[j];
		  			break;
				}//end switch
			}
		}
		
  		return Tax_D;
	}
	
	//will return single card of specific type.
	public static Credit_Card_Data getCreditCardDetails(String Level, String Type) {
		Credit_Card_Data CreditCards[] = getCreditCardDetails(Level);

		for (Credit_Card_Data CD : CreditCards) {
			if (CD.TYPE.contains(Type)) {
				Helper_Functions.PrintOut("Credit Card: " + Arrays.toString(Credit_Card_Data.CreditCardStringArray(CD)), false);
				return CD;
			}
		}
		if (CreditCards != null) {
			Helper_Functions.PrintOut("Credit Card of type " + Type + " not found. Using the below card instead.", false);
			Helper_Functions.PrintOut("Credit Card: " + Arrays.toString(Credit_Card_Data.CreditCardStringArray(CreditCards[0])), false);
			return CreditCards[0];
		}
		
		return null;
	}
	
	//will return the next card on the list after the one given
	public static Credit_Card_Data getCreditCardDetails(String Level, String Type, String CardNumber) {
		Credit_Card_Data CreditCards[] = getCreditCardDetails(Level);
		boolean returnNextCard = false;
		for (Credit_Card_Data CD : CreditCards) {
			if (CD.TYPE.contains(Type) && CD.CARD_NUMBER.contentEquals(CardNumber)){
				returnNextCard = true;
			}else if (CD.CARD_NUMBER.contentEquals(CardNumber)){
				returnNextCard = true;
			}else if (returnNextCard && !CD.TYPE.contains(Type)) {
				Helper_Functions.PrintOut("Credit Card: " + Arrays.toString(Credit_Card_Data.CreditCardStringArray(CD)), false);
				return CD;
			}
		}
		//if the card given was last in the list will return the card that was first.
		if (returnNextCard && CreditCards != null) {
			return CreditCards[0];
		}
		return null;
	}
	
	//will return all credit cards for the environment
	public static Credit_Card_Data[] getCreditCardDetails(String Level) {
		//if the data is already loaded then return the values
		ArrayList<String[]> CCDetails = new ArrayList<String[]>();
		
		if (Level.contains("7")) {
			Level = "Prod";
		}else {
			Level = "Test";
		}
		CCDetails = Helper_Functions.getExcelData(Helper_Functions.DataDirectory + "\\CreditCardDetails.xls",  Level);//load the relevant information from excel file.
		
		Credit_Card_Data Credit_D[] = new Credit_Card_Data[CCDetails.size() - 1];
		String Headers[] = CCDetails.get(0);
		for (int i = 1; i < CCDetails.size(); i++) {
			String Row[] = CCDetails.get(i);

			Credit_D[i -1] = new Credit_Card_Data();
			for (int j = 0; j <Headers.length; j++) {
				int pos = i - 1;
				switch (Headers[j]) {
				case "TYPE":
		  			Credit_D[pos].TYPE = Row[j];
		  			break;
				case "CARD_NUMBER":
		  			Credit_D[pos].CARD_NUMBER = Row[j];
		  			break;
				case "CVV":
		  			Credit_D[pos].CVV = Row[j];
		  			break;
				case "EXPIRATION_MONTH":
		  			Credit_D[pos].EXPIRATION_MONTH = Row[j];
		  			break;
				case "EXPIRATION_YEAR":
		  			Credit_D[pos].EXPIRATION_YEAR = Row[j];
		  			break;
				}//end switch
			}
		}
		
  		return Credit_D;
	}
	
	public static Account_Data getAddressDetails(String Level, String CountryCode) {
		return getAddressDetails(Level, CountryCode, "");
	}
	
	public static Account_Data getAddressDetails(String Level, String CountryCode, String Type) {
		Account_Data AllAddresses[] = getAddressDetails();
		CountryCode = CountryCode.toUpperCase();
		
		for (Account_Data AD: AllAddresses) {
			if (AD != null && AD.Billing_Address_Info.Country_Code.contentEquals(CountryCode)) {
				if (Level.contentEquals("7") && Type.contentEquals("CreditCard") && AD.Billing_Address_Info.Address_Line_1.contentEquals("10 FEDEX PKWY 2nd FL")){
					//unique to credit card registration.
					AD.Level = Level;
					Account_Data.Set_Dummy_Contact_Name(AD);
					return AD;
				}else if (!Level.contentEquals("7") || (!Type.contentEquals("CreditCard") && !AD.Billing_Address_Info.Country_Code.contentEquals("US"))){
					AD.Level = Level;
					Account_Data.Set_Dummy_Contact_Name(AD);
					return AD;
				}
			}
		}
		System.err.println("Data not loaded for country of " + CountryCode);
		return null;
	}
	
	public static Account_Data[] getAddressDetails() {
		//if the data is already loaded then return the values
		ArrayList<String[]> AddressDetails = new ArrayList<String[]>();
		
		AddressDetails = Helper_Functions.getExcelData(Helper_Functions.DataDirectory + "\\AddressDetails.xls",  "Countries");//load the relevant information from excel file.

		Account_Data Address_Data[] = new Account_Data[AddressDetails.size()];
		String Headers[] = AddressDetails.get(0);
		for (int i = 1; i < AddressDetails.size(); i++) {
			String Row[] = AddressDetails.get(i);

			Address_Data[i -1] = new Account_Data();
			for (int j = 0; j <Headers.length; j++) {
				int pos = i - 1;
				switch (Headers[j]) {
		  		case "Address_Line_1":
		  			Address_Data[pos].Shipping_Address_Info.Address_Line_1 = Row[j];
		  			Address_Data[pos].Billing_Address_Info.Address_Line_1 = Row[j];
		  			break;
		  		case "Address_Line_2":
		  			Address_Data[pos].Shipping_Address_Info.Address_Line_2 = Row[j];
		  			Address_Data[pos].Billing_Address_Info.Address_Line_2 = Row[j];
		  			break;
		  		case "City":
		  			Address_Data[pos].Shipping_Address_Info.City = Row[j];
		  			Address_Data[pos].Billing_Address_Info.City = Row[j];
		  			break;
		  		case "State":
		  			Address_Data[pos].Shipping_Address_Info.State = Row[j];
		  			Address_Data[pos].Billing_Address_Info.State = Row[j];
		  			break;
		  		case "State_Code":
		  			Address_Data[pos].Shipping_Address_Info.State_Code = Row[j];
		  			Address_Data[pos].Billing_Address_Info.State_Code = Row[j];
		  			break;
		  		case "Zip":
		  			Address_Data[pos].Shipping_Address_Info.Zip = Row[j];
		  			Address_Data[pos].Billing_Address_Info.Zip = Row[j];
		  			break;
		  		case "Country_Code":
		  			Address_Data[pos].Shipping_Address_Info.Country_Code = Row[j];
		  			Address_Data[pos].Billing_Address_Info.Country_Code = Row[j];
		  			break;
		  		case "Region":
		  			Address_Data[pos].Shipping_Address_Info.Region = Row[j];
		  			Address_Data[pos].Billing_Address_Info.Region = Row[j];
		  			break;
		  		case "Country":
		  			Address_Data[pos].Shipping_Address_Info.Country = Row[j];
		  			Address_Data[pos].Billing_Address_Info.Country = Row[j];
		  			break;
		  		case "Share_Id":
		  			Address_Data[pos].Shipping_Address_Info.Share_Id = Row[j];
		  			Address_Data[pos].Billing_Address_Info.Share_Id = Row[j];
		  			break;
				}//end switch
			}
			//will load the dummy email address and phone number by default.
			Address_Data[i -1].Billing_Address_Info.Phone_Number = Helper_Functions.myPhone;
			Address_Data[i -1].Shipping_Address_Info.Phone_Number = Helper_Functions.myPhone;
			Address_Data[i -1].Email = Helper_Functions.MyEmail;
		}
  		return Address_Data;
	}

	//will load the userids into the data class even if the rows have been changed.
	public static User_Data[] Get_UserIds(int intLevel) {
		List<String[]> FullDataFromExcel = new ArrayList<String[]>();
		FullDataFromExcel = Helper_Functions.getExcelData(Helper_Functions.DataDirectory + "\\TestingData.xls", "L" + intLevel);
		//a list of the Userids
		User_Data User_Info_Array[] = new User_Data[FullDataFromExcel.size() - 1];
		
		String Headers[] = FullDataFromExcel.get(0);
		for (int i = 1; i < FullDataFromExcel.size(); i++) {
			String Row[] = FullDataFromExcel.get(i);
			User_Info_Array[i - 1] = new User_Data(); 
			for (int j = 0; j <Headers.length; j++) {
				int pos = i - 1;
				switch (Headers[j]) {
		  		case "UUID_NBR":
					User_Info_Array[pos].UUID_NBR = Row[j];
					break;
		  		case "SSO_LOGIN_DESC":
		  			User_Info_Array[pos].USER_ID = Row[j];
					break;
		  		case "USER_PASSWORD_DESC":
		  			User_Info_Array[pos].PASSWORD = Row[j];
					break;
		  		case "SECRET_QUESTION_DESC":
		  			User_Info_Array[pos].SECRET_QUESTION_DESC = Row[j];
					break;
		  		case "SECRET_ANSWER_DESC":
		  			User_Info_Array[pos].SECRET_ANSWER_DESC = Row[j];
					break;
		  		case "FIRST_NM":
		  			User_Info_Array[pos].FIRST_NM = Row[j];
					break;
		  		case "LAST_NM":
		  			User_Info_Array[pos].LAST_NM = Row[j];
					break;
		  		case "EMAIL_ADDRESS":
		  			User_Info_Array[pos].EMAIL_ADDRESS = Row[j];
					break;
		  		case "STREET_DESC":
		  			User_Info_Array[pos].Address_Info.Address_Line_1 = Row[j];
					break;
		  		case "CITY_NM":
		  			User_Info_Array[pos].Address_Info.City = Row[j];
					break;
		  		case "STATE_CD":
		  			User_Info_Array[pos].Address_Info.City = Row[j];
					break;
		  		case "POSTAL_CD":
		  			User_Info_Array[pos].Address_Info.City = Row[j];
					break;
		  		case "COUNTRY_CD":
		  			User_Info_Array[pos].Address_Info.Country_Code = Row[j];
					break;
		  		case "ACCOUNT_NUMBER":
		  			User_Info_Array[pos].ACCOUNT_NUMBER = Row[j];
					break;
		  		case "WCRV_ENABLED":
		  			User_Info_Array[pos].WCRV_ENABLED = Row[j];
					break;	
		  		case "GFBO_ENABLED":
		  			User_Info_Array[pos].GFBO_ENABLED = Row[j];
					break;	
		  		case "WGRT_ENABLED":
		  			User_Info_Array[pos].WGRT_ENABLED = Row[j];
					break;	
		  		case "WDPA_ENABLED":
		  			User_Info_Array[pos].WDPA_ENABLED = Row[j];
					break;	
		  		case "GROUND_ENABLED":
		  			User_Info_Array[pos].GROUND_ENABLED = Row[j];
					break;
		  		case "EXPRESS_ENABLED":
		  			User_Info_Array[pos].EXPRESS_ENABLED = Row[j];
					break;
		  		case "PASSKEY":
		  			User_Info_Array[pos].PASSKEY = Row[j];
					break;
		  		case "FDM_STATUS":
		  			User_Info_Array[pos].FDM_STATUS = Row[j];
					break;	
		  		case "FREIGHT_ENABLED":
		  			User_Info_Array[pos].FREIGHT_ENABLED = Row[j];
					break;	
		  		case "ERROR":
		  			User_Info_Array[pos].ERROR = Row[j];
					break;	
		  		case "MIGRATION_STATUS":
		  			User_Info_Array[pos].MIGRATION_STATUS = Row[j];
					break;	
		  		case "USER_TYPE":
		  			User_Info_Array[pos].USER_TYPE = Row[j];
					break;	
				}//end switch
			}
			
			if (User_Info_Array[i - 1].USER_ID == null || User_Info_Array[i - 1].USER_ID.contentEquals("")) {
				User_Info_Array[i - 1].ERROR = "ERROR";
			}
		}
		
		return User_Info_Array;
	}
}
