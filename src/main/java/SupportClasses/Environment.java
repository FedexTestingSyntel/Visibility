package SupportClasses;

import java.util.ArrayList;
import java.util.Arrays;
import org.testng.SkipException;
import org.testng.annotations.Optional;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;

public class Environment {
	public static String LevelsToTest;
	private static Environment instance = new Environment();
	public static ArrayList<String[]> AddressDetails = new ArrayList<String[]>();
	public static ArrayList<String[]> TaxData = new ArrayList<String[]>();
	static ArrayList<String[]> AccountsAlreadyCreated_L1, AccountsAlreadyCreated_L2, AccountsAlreadyCreated_L3, AccountsAlreadyCreated_L4, AccountsAlreadyCreated_L5, AccountsAlreadyCreated_L6, AccountsAlreadyCreated_L7;
	
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
			for (int i = 0; i < Environment.LevelsToTest.length(); i++) {
				String Level = String.valueOf(Environment.LevelsToTest.charAt(i));
				Helper_Functions.LoadUserIds(Integer.parseInt(Level));
			}
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
		Identifier = Identifier.toLowerCase();
		String FullCountryList[][] = new String[][]{{"US", "United States"}, {"AF", "Afghanistan"}, {"AL", "Albania"}, {"DZ", "Algeria"}, {"AS", "American Samoa"}, {"AD", "Andorra"}, {"AO", "Angola"}, {"AI", "Anguilla"}, {"AG", "Antigua and Barbuda"}, {"AR", "Argentina"}, {"AM", "Armenia"}, {"AW", "Aruba"}, {"AU", "Australia"}, {"AT", "Austria"}, {"AZ", "Azerbaijan"}, {"BS", "Bahamas"}, {"BH", "Bahrain"}, {"BD", "Bangladesh"}, {"BB", "Barbados"}, {"BY", "Belarus"}, {"BE", "Belgium"}, {"BZ", "Belize"}, {"BJ", "Benin"}, {"BM", "Bermuda"}, {"BT", "Bhutan"}, {"BO", "Bolivia"}, {"BQ", "Bonaire/Saba/Sint Eustatius"}, {"BA", "Bosnia-Herzegovina"}, {"BW", "Botswana"}, {"BR", "Brazil"}, {"VG", "Britain Virgin Islands"}, {"BN", "Brunei"}, {"BG", "Bulgaria"}, {"BF", "Burkina Faso"}, {"BI", "Burundi"}, {"KH", "Cambodia"}, {"CM", "Cameroon"}, {"CA", "Canada"}, {"CV", "Cape Verde Islands"}, {"KY", "Cayman Islands"}, {"CF", "Central African Republic"}, {"TD", "Chad"}, {"CL", "Chile"}, {"CN", "China"}, {"CO", "Colombia"}, {"KM", "Comoros"}, {"CG", "Congo"}, {"CK", "Cook Islands"}, {"CR", "Costa Rica"}, {"HR", "Croatia"}, {"CU", "Cuba"}, {"CW", "Curacao"}, {"CY", "Cyprus"}, {"CZ", "Czech Republic"}, {"CD", "Democratic Republic of Congo"}, {"DK", "Denmark"}, {"DJ", "Djibouti"}, {"DM", "Dominica"}, {"DO", "Dominican Republic"}, {"TL", "East Timor"}, {"EC", "Ecuador"}, {"EG", "Egypt"}, {"SV", "El Salvador"}, {"GQ", "Equatorial Guinea"}, {"ER", "Eritrea"}, {"EE", "Estonia"}, {"ET", "Ethiopia"}, {"FO", "Faroe Islands"}, {"FJ", "Fiji"}, {"FI", "Finland"}, {"FR", "France"}, {"GF", "French Guiana"}, {"PF", "French Polynesia"}, {"GA", "Gabon"}, {"GM", "Gambia"}, {"GE", "Georgia, Republic of"}, {"DE", "Germany"}, {"GH", "Ghana"}, {"GI", "Gibraltar"}, {"GR", "Greece"}, {"GL", "Greenland"}, {"GD", "Grenada"}, {"GP", "Guadeloupe"}, {"GU", "Guam"}, {"GT", "Guatemala"}, {"GN", "Guinea"}, {"GW", "Guinea-Bissau"}, {"GY", "Guyana"}, {"HT", "Haiti"}, {"HN", "Honduras"}, {"HK", "Hong Kong"}, {"HU", "Hungary"}, {"IS", "Iceland"}, {"IN", "India"}, {"ID", "Indonesia"}, {"IR", "Iran"}, {"IQ", "Iraq Republic"}, {"IE", "Ireland"}, {"IL", "Israel"}, {"IT", "Italy"}, {"CI", "Ivory Coast"}, {"JM", "Jamaica"}, {"JP", "Japan"}, {"JO", "Jordan"}, {"KZ", "Kazakhstan"}, {"KE", "Kenya"}, {"KI", "Kiribati"}, {"KW", "Kuwait"}, {"KG", "Kyrgyzstan"}, {"LA", "Laos"}, {"LV", "Latvia"}, {"LB", "Lebanon"}, {"LS", "Lesotho"}, {"LR", "Liberia"}, {"LY", "Libya"}, {"LI", "Liechtenstein"}, {"LT", "Lithuania"}, {"LU", "Luxembourg"}, {"MO", "Macau"}, {"MK", "Macedonia"}, {"MG", "Madagascar"}, {"MW", "Malawi"}, {"MY", "Malaysia"}, {"MV", "Maldives"}, {"ML", "Mali"}, {"MT", "Malta"}, {"MH", "Marshall Islands"}, {"MQ", "Martinique"}, {"MR", "Mauritania"}, {"MU", "Mauritius"}, {"MX", "Mexico"}, {"FM", "Micronesia"}, {"MD", "Moldova"}, {"MC", "Monaco"}, {"MN", "Mongolia"}, {"ME", "Montenegro"}, {"MS", "Montserrat"}, {"MA", "Morocco"}, {"MZ", "Mozambique"}, {"MM", "Myanmar/Burma"}, {"NA", "Namibia"}, {"NR", "Nauru"}, {"NP", "Nepal"}, {"NL", "Netherlands"}, {"AN", "Netherlands Antilles"}, {"NC", "New Caledonia"}, {"NZ", "New Zealand"}, {"NI", "Nicaragua"}, {"NE", "Niger"}, {"NG", "Nigeria"}, {"NU", "Niue"}, {"NO", "Norway"}, {"OM", "Oman"}, {"PK", "Pakistan"}, {"PW", "Palau"}, {"PS", "Palestine"}, {"PA", "Panama"}, {"PG", "Papua New Guinea"}, {"PY", "Paraguay"}, {"PE", "Peru"}, {"PH", "Philippines"}, {"PL", "Poland"}, {"PT", "Portugal"}, {"PR", "Puerto Rico"}, {"QA", "Qatar"}, {"RE", "Reunion Island"}, {"RO", "Romania"}, {"RU", "Russia"}, {"RW", "Rwanda"}, {"MF", "Saint Martin"}, {"MP", "Saipan"}, {"WS", "Samoa"}, {"SM", "San Marino"}, {"ST", "Sao Tome & Principe"}, {"SA", "Saudi Arabia"}, {"SN", "Senegal"}, {"RS", "Serbia"}, {"SC", "Seychelles"}, {"SL", "Sierra Leone"}, {"SG", "Singapore"}, {"SK", "Slovak Republic"}, {"SI", "Slovenia"}, {"SB", "Solomon Islands"}, {"SO", "Somalia"}, {"ZA", "South Africa"}, {"KR", "South Korea"}, {"ES", "Spain"}, {"LK", "Sri Lanka"}, {"KN", "St Kitts and Nevis"}, {"LC", "St Lucia"}, {"SX", "St Maarten"}, {"VC", "St Vincent"}, {"GP", "St. Barthelemy"}, {"SD", "Sudan"}, {"SR", "Suriname"}, {"SZ", "Swaziland"}, {"SE", "Sweden"}, {"CH", "Switzerland"}, {"SY", "Syria"}, {"TW", "Taiwan"}, {"TJ", "Tajikistan"}, {"TZ", "Tanzania"}, {"TH", "Thailand"}, {"TG", "Togo"}, {"TK", "Tokelau Is."}, {"TO", "Tonga"}, {"TT", "Trinidad And Tobago"}, {"TN", "Tunisia"}, {"TR", "Turkey"}, {"TM", "Turkmenistan"}, {"TC", "Turks And Caicos"}, {"TV", "Tuvalu"}, {"UG", "Uganda"}, {"UA", "Ukraine"}, {"AE", "United Arab Emirates"}, {"GB", "United Kingdom"}, {"UY", "Uruguay"}, {"VI", "US Virgin Islands/St John/St Thomas"}, {"UZ", "Uzbekistan"}, {"VU", "Vanuatu"}, {"VA", "Vatican City State"}, {"VE", "Venezuela"}, {"VN", "Vietnam"}, {"WF", "Wallis and Futuna"}, {"YE", "Yemen"}, {"ZM", "Zambia"}, {"ZW", "Zimbabwe"}};
		String SmokeCountryList[][] = new String[][]{{"US", "United States"}};
		
		//return smoke list by default
		String ReturnList[][] = SmokeCountryList;
		if (Identifier.contentEquals("smoke")){
			ReturnList = SmokeCountryList;
		}else if (Identifier.contentEquals("full")){
			ReturnList = FullCountryList;
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
  		switch (Level) {
  		case "1":
  			if (AccountsAlreadyCreated_L1 == null) {
  				AccountsAlreadyCreated_L1 = new ArrayList<String[]>();
  				AccountsAlreadyCreated_L1 = Helper_Functions.getExcelData(Helper_Functions.DataDirectory + "\\AddressDetails.xls", "L" + Level + "_Account_Numbers");//load the relevant information from excel file.
  			}
  			return AccountsAlreadyCreated_L1;
  	  	case "2":
  	  		if (AccountsAlreadyCreated_L2 == null) {
  	  			AccountsAlreadyCreated_L2 = new ArrayList<String[]>();
				AccountsAlreadyCreated_L2 = Helper_Functions.getExcelData(Helper_Functions.DataDirectory + "\\AddressDetails.xls", "L" + Level + "_Account_Numbers");//load the relevant information from excel file.
			}
			return AccountsAlreadyCreated_L2;
  		case "3":
  			if (AccountsAlreadyCreated_L3 == null) {
  				AccountsAlreadyCreated_L3 = new ArrayList<String[]>();
  				AccountsAlreadyCreated_L3 = Helper_Functions.getExcelData(Helper_Functions.DataDirectory + "\\AddressDetails.xls", "L" + Level + "_Account_Numbers");//load the relevant information from excel file.
  			}
  			return AccountsAlreadyCreated_L3;
  		case "4":
  			if (AccountsAlreadyCreated_L4 == null) {
  				AccountsAlreadyCreated_L4 = new ArrayList<String[]>();
  				AccountsAlreadyCreated_L4 = Helper_Functions.getExcelData(Helper_Functions.DataDirectory + "\\AddressDetails.xls", "L" + Level + "_Account_Numbers");//load the relevant information from excel file.
  			}
  			return AccountsAlreadyCreated_L4;
  		case "5":
  			if (AccountsAlreadyCreated_L5 == null) {
  				AccountsAlreadyCreated_L5 = new ArrayList<String[]>();
  				AccountsAlreadyCreated_L5 = Helper_Functions.getExcelData(Helper_Functions.DataDirectory + "\\AddressDetails.xls", "L" + Level + "_Account_Numbers");//load the relevant information from excel file.
  			}
  			return AccountsAlreadyCreated_L5;
  		case "6":
  			if (AccountsAlreadyCreated_L6 == null) {
  				AccountsAlreadyCreated_L6 = new ArrayList<String[]>();
  				AccountsAlreadyCreated_L6 = Helper_Functions.getExcelData(Helper_Functions.DataDirectory + "\\AddressDetails.xls", "L" + Level + "_Account_Numbers");//load the relevant information from excel file.
  			}
  			return AccountsAlreadyCreated_L6;
  		case "7":
  			if (AccountsAlreadyCreated_L7 == null) {
  				AccountsAlreadyCreated_L7 = new ArrayList<String[]>();
  				AccountsAlreadyCreated_L7 = Helper_Functions.getExcelData(Helper_Functions.DataDirectory + "\\AddressDetails.xls", "L" + Level + "_Account_Numbers");//load the relevant information from excel file.
  			}
  			return AccountsAlreadyCreated_L7;
		}
  		//invalid input
  		return null;
	}
}
