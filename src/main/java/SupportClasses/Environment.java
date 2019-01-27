package SupportClasses;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.testng.SkipException;
import org.testng.annotations.Optional;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;

import Data_Structures.Account_Data;
import Data_Structures.User_Data;

public class Environment {
	public static String LevelsToTest;
	private static Environment instance = new Environment();
	public static ArrayList<String[]> AddressDetails = new ArrayList<String[]>();
	public static ArrayList<String[]> TaxData = new ArrayList<String[]>();
	public static Account_Data Account_Details[][] = new Account_Data[8][];
	public static Account_Data Address_Data[];	
		
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
	
	public static void getAddressList() {
		AddressDetails = Helper_Functions.getExcelData(Helper_Functions.DataDirectory + "\\AddressDetails.xls",  "Accounts");//load the relevant information from excel file.
	}
	
	public static ArrayList<String[]> getTaxData(String CountryCode) {
		if (TaxData == null) {
			//Load all data from the excel into the array list
			TaxData = Helper_Functions.getExcelData(Helper_Functions.DataDirectory + "\\TaxData.xls",  "TaxIds");
		
			//This is a check to see if there have been any changes to the excel data
			String Column_Headers[] = TaxData.get(0);
			String Expected_Headers[] = new String[] {"Country_Code", "Tax_ID", "State_Tax_ID", "Error_Code"};
			for (int j = 0; j < Expected_Headers.length; j++) {
				if (!Column_Headers[j].contentEquals(Expected_Headers[j])) {
					System.err.println("WARNING: mismatch with the tax data excel sheet");
				}
			}
		}
		return TaxData;
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
		int intLevel = Integer.parseInt(Level);
		//if the data is already loaded then return the values
		if (Account_Details[intLevel] != null) {
			return Account_Details[intLevel];
		}
		
		ArrayList<String[]> AccountsAlreadyCreated = Environment.getAccountList(Level);
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
		  			Account_Details[pos].Shipping_Address_Line_1 = Row[j];
		  			break;
		  		case "Shipping_Address_Line_2":
		  			Account_Details[pos].Shipping_Address_Line_2 = Row[j];
		  			break;
		  		case "Shipping_City":
		  			Account_Details[pos].Shipping_City = Row[j];
		  			break;
		  		case "Shipping_State":
		  			Account_Details[pos].Shipping_State = Row[j];
		  			break;
		  		case "Shipping_State_Code":
		  			Account_Details[pos].Shipping_State_Code = Row[j];
		  			break;
		  		case "Shipping_Phone_Number":
		  			Account_Details[pos].Shipping_Phone_Number = Row[j];
		  			break;
		  		case "Shipping_Zip":
		  			Account_Details[pos].Shipping_Zip = Row[j];
		  			break;
		  		case "Shipping_Country_Code":
		  			Account_Details[pos].Shipping_Country_Code = Row[j];
		  			break;
		  		case "Shipping_Region":
		  			Account_Details[pos].Shipping_Region = Row[j];
		  			break;
		  		case "Shipping_Country":
		  			Account_Details[pos].Shipping_Country = Row[j];
		  			break;
		  		case "Billing_Address_Line_1":
		  			Account_Details[pos].Billing_Address_Line_1 = Row[j];
		  			break;
		  		case "Billing_Address_Line_2":
		  			Account_Details[pos].Billing_Address_Line_2 = Row[j];
		  			break;
		  		case "Billing_City":
		  			Account_Details[pos].Billing_City = Row[j];
		  			break;
		  		case "Billing_State":
		  			Account_Details[pos].Billing_State = Row[j];
		  			break;
		  		case "Billing_State_Code":
		  			Account_Details[pos].Billing_State_Code = Row[j];
		  			break;
		  		case "Billing_Phone_Number":
		  			Account_Details[pos].Billing_Phone_Number = Row[j];
		  			break;
		  		case "Billing_Zip":
		  			Account_Details[pos].Billing_Zip = Row[j];
		  			break;
		  		case "Billing_Country_Code":
		  			Account_Details[pos].Billing_Country_Code = Row[j];
		  			break;
		  		case "Billing_Region":
		  			Account_Details[pos].Billing_Region = Row[j];
		  			break;
		  		case "Billing_Country":
		  			Account_Details[pos].Billing_Country = Row[j];
		  			break;
		  		case "Account_Number":
		  			Account_Details[pos].Account_Number = Row[j];
		  			if (Account_Details[pos].Billing_Country_Code != null && !Account_Details[pos].Billing_Country_Code.contentEquals("")) {
		  				Account_Details[pos].Account_Nickname = Row[j] + "_" + Account_Details[pos].Billing_Country_Code;
		  			}else {
		  				Account_Details[pos].Account_Nickname = Row[j] + "_Acc";
		  			}
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
				}//end switch
			}
		}
  		//invalid input
  		return Account_Details;
	}

	public static Account_Data[] getAddressDetails() {
		//if the data is already loaded then return the values
		if (Address_Data!= null) {
			return Address_Data;
		}else if (AddressDetails == null || AddressDetails.size() == 0){
			getAddressList();
		}
		
		Address_Data = new Account_Data[AddressDetails.size()];
		String Headers[] = AddressDetails.get(0);
		for (int i = 1; i < AddressDetails.size(); i++) {
			String Row[] = AddressDetails.get(i);

			Address_Data[i -1] = new Account_Data();
			for (int j = 0; j <Headers.length; j++) {
				int pos = i - 1;
				switch (Headers[j]) {
		  		case "Address_Line_1":
		  			Address_Data[pos].Shipping_Address_Line_1 = Row[j];
		  			Address_Data[pos].Billing_Address_Line_1 = Row[j];
		  			break;
		  		case "Address_Line_2":
		  			Address_Data[pos].Shipping_Address_Line_2 = Row[j];
		  			Address_Data[pos].Billing_Address_Line_2 = Row[j];
		  			break;
		  		case "City":
		  			Address_Data[pos].Shipping_City = Row[j];
		  			Address_Data[pos].Billing_City = Row[j];
		  			break;
		  		case "State":
		  			Address_Data[pos].Shipping_State = Row[j];
		  			Address_Data[pos].Billing_State = Row[j];
		  			break;
		  		case "State_Code":
		  			Address_Data[pos].Shipping_State_Code = Row[j];
		  			Address_Data[pos].Billing_State_Code = Row[j];
		  			break;
		  		case "Zip":
		  			Address_Data[pos].Shipping_Zip = Row[j];
		  			Address_Data[pos].Billing_Zip = Row[j];
		  			break;
		  		case "Country_Code":
		  			Address_Data[pos].Shipping_Country_Code = Row[j];
		  			Address_Data[pos].Billing_Country_Code = Row[j];
		  			break;
		  		case "Region":
		  			Address_Data[pos].Shipping_Region = Row[j];
		  			Address_Data[pos].Billing_Region = Row[j];
		  			break;
		  		case "Country":
		  			Address_Data[pos].Shipping_Country = Row[j];
		  			Address_Data[pos].Billing_Country = Row[j];
		  			break;
				}//end switch
			}
			//will load the dummy email address and phone number by default.
			Address_Data[i -1].Billing_Phone_Number = Helper_Functions.myPhone;
			Address_Data[i -1].Shipping_Phone_Number = Helper_Functions.myPhone;
			Address_Data[i -1].Email = Helper_Functions.MyEmail;
		}
  		return Address_Data;
	}

	//will load the userids into the data class even if the rows have been changed.
	public static User_Data[] Get_UserIds(int intLevel) {
		List<String[]> FullDataFromExcel = new ArrayList<String[]>();
		FullDataFromExcel = Helper_Functions.getExcelData(Helper_Functions.DataDirectory + "\\TestingData.xls", "L" + intLevel);
		//a list of the Userids
		User_Data DataClass[] = new User_Data[FullDataFromExcel.size() - 1];
		
		String Headers[] = FullDataFromExcel.get(0);
		for (int i = 1; i < FullDataFromExcel.size(); i++) {
			String Row[] = FullDataFromExcel.get(i);
			DataClass[i - 1] = new User_Data(); 
			for (int j = 0; j <Headers.length; j++) {
				int pos = i - 1;
				switch (Headers[j]) {
		  		case "UUID_NBR":
					DataClass[pos].UUID_NBR = Row[j];
					break;
		  		case "SSO_LOGIN_DESC":
		  			DataClass[pos].SSO_LOGIN_DESC = Row[j];
					break;
		  		case "USER_PASSWORD_DESC":
		  			DataClass[pos].USER_PASSWORD_DESC = Row[j];
					break;
		  		case "SECRET_QUESTION_DESC":
		  			DataClass[pos].SECRET_QUESTION_DESC = Row[j];
					break;
		  		case "SECRET_ANSWER_DESC":
		  			DataClass[pos].SECRET_ANSWER_DESC = Row[j];
					break;
		  		case "FIRST_NM":
		  			DataClass[pos].FIRST_NM = Row[j];
					break;
		  		case "LAST_NM":
		  			DataClass[pos].LAST_NM = Row[j];
					break;
		  		case "STREET_DESC":
		  			DataClass[pos].STREET_DESC = Row[j];
					break;
		  		case "CITY_NM":
		  			DataClass[pos].CITY_NM = Row[j];
					break;
		  		case "STATE_CD":
		  			DataClass[pos].STATE_CD = Row[j];
					break;
		  		case "POSTAL_CD":
		  			DataClass[pos].POSTAL_CD = Row[j];
					break;
		  		case "COUNTRY_CD":
		  			DataClass[pos].COUNTRY_CD = Row[j];
					break;
		  		case "ACCOUNT_NUMBER":
		  			DataClass[pos].ACCOUNT_NUMBER = Row[j];
					break;
		  		case "WCRV_ENABLED":
		  			DataClass[pos].WCRV_ENABLED = Row[j];
					break;	
		  		case "GFBO_ENABLED":
		  			DataClass[pos].GFBO_ENABLED = Row[j];
					break;	
		  		case "WGRT_ENABLED":
		  			DataClass[pos].WGRT_ENABLED = Row[j];
					break;	
		  		case "WDPA_ENABLED":
		  			DataClass[pos].WDPA_ENABLED = Row[j];
					break;	
		  		case "PASSKEY":
		  			DataClass[pos].PASSKEY = Row[j];
					break;	
		  		case "ERROR":
		  			DataClass[pos].ERROR = Row[j];
					break;	
				}//end switch
			}
		}
		return DataClass;
	}
}
