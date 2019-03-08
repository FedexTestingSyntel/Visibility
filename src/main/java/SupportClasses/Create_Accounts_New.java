package SupportClasses;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.StringTokenizer;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.openqa.selenium.By;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Listeners;
import org.testng.annotations.Test;
import Data_Structures.Account_Data;
import Data_Structures.Credit_Card_Data;

@Listeners(SupportClasses.TestNG_TestListener.class)

public class Create_Accounts_New{
	private static String ECAMuserid;
	private static String ECAMpassword;
	
	static String LevelsToTest = "3";
	static String CountryList[][]; 
	
	@BeforeClass
	public static void ECAM_Data(){
		ArrayList<String[]> PersonalData = new ArrayList<String[]>();
		PersonalData = Helper_Functions.getExcelData(Helper_Functions.DataDirectory + "\\Load_Your_UserIds.xls",  "Data");//create your own file with the specific data
		for(String s[]: PersonalData) {
			if (s[0].contentEquals("ECAM")) {
				ECAMuserid = s[1];
				ECAMpassword = s[2];
			}
		}
		
		Environment.SetLevelsToTest(LevelsToTest);
		CountryList = Environment.getCountryList("US");
	}
	
	@DataProvider //(parallel = true)
	public static Iterator<Object[]> dp(Method m) {
		List<Object[]> data = new ArrayList<Object[]>();
		
		for (int i=0; i < LevelsToTest.length(); i++) {
			String Level = String.valueOf(LevelsToTest.charAt(i));
			for (int j = 0; j < CountryList.length; j++) {
				Account_Data Account_Info = Environment.getAddressDetails(Level, CountryList[j][0]);
    			data.add( new Object[] {Level, Account_Info});
			}
		}
		return data.iterator();
	}

	@Test(dataProvider = "dp")
	public void Account_Creation(String Level, Account_Data Account_Info) {
		try {
			String Operating_Companies = "E";
			if ("US_CA_".contains(Account_Info.Billing_Country_Code)) {
				Operating_Companies += "G";
			}
			if ("US_CA_MX_".contains(Account_Info.Billing_Country_Code)) {
				Operating_Companies += "F";
			}
			Account_Data[] Accounts = null;
			Account_Data.Print_Account_Address(Account_Info);
			Account_Info.Email = Helper_Functions.MyFakeEmail;
			Account_Info.Company_Name = Helper_Functions.CurrentDateTime();
			Accounts = CreateAccountNumbers(Account_Info, Operating_Companies, 10);
			writeAccountsToExcel(Accounts, Operating_Companies);

		}catch (Exception e) {
			Assert.fail(e.getMessage());
		}
	}
	
	public static Account_Data[] CreateAccountNumbers(Account_Data Account_Info, String OperatingCompanies, int NumAccounts) throws Exception{
		try {
			// AccountDetails Example = 
			//ShippingCountryCode, BillingCountryCode, OperatingCompanies (E = Express, G = Ground, F = Freight so "EDF" is all three), NumberOfAccounts
			// AddressDetails Example =  {"10 FEDEX PKWY 2nd FL", "", "COLLIERVILLE", "Tennessee", "TN", "38017", "US"});
			//Address line 1, address line 2, City, StateName, StateCode, ZipCode, CountryCode

			WebDriver_Functions.ChangeURL("ECAM", "", false);
			if (WebDriver_Functions.isPresent(By.id("username"))) {
				WebDriver_Functions.Type(By.id("username"), ECAMuserid);
				WebDriver_Functions.Type(By.id("password"), ECAMpassword);
				WebDriver_Functions.Click(By.id("submit"));
			}

			//ECAM Page
			for(int i = 0; i < 30; i++) {
				try {
					WebDriver_Functions.WaitPresent(By.id("new_act_info"));
		 			WebDriver_Functions.WaitClickable(By.id("new_act_info"));
					WebDriver_Functions.Click(By.id("new_act_info"));
					break;
				}catch (Exception e) {
					Helper_Functions.Wait(1);
				}
			}

			WebDriver_Functions.WaitPresent(By.id("acct_info_ship_countrylist"));
			String ShippingCountrCode = Account_Info.Shipping_Country_Code, BillingCountryCode = Account_Info.Billing_Country_Code;
			WebDriver_Functions.Select(By.id("acct_info_countrylist"), BillingCountryCode, "v");//billing country
			WebDriver_Functions.Select(By.id("acct_info_ship_countrylist"), ShippingCountrCode, "v");//shipping country
			WebDriver_Functions.Select(By.id("acct_info_source_grp"), "ALLIANCES","v");//the Source group of the account numbers
			
			WebDriver_Functions.WaitPresent(By.id("acctinfo_customertype"));
			String Account_Type = "BUSINESS";
			WebDriver_Functions.Select(By.id("acctinfo_customertype") , Account_Type,"v");//Customer WebDriver_Functions.Type
			
			//This next section will populate based on the country and the customer type
			if (OperatingCompanies.contains("E")) {
				WebDriver_Functions.Click(By.id("check_exp"));
				WebDriver_Functions.Select(By.id("acct_type_exp") , Account_Type,"v");//Express WebDriver_Functions.Type
			}
			if (OperatingCompanies.contains("G")) {
				WebDriver_Functions.Click(By.id("check_gnd"));
				WebDriver_Functions.Select(By.id("acct_type_exp") , Account_Type,"v");//Express WebDriver_Functions.Type
			}
			if (OperatingCompanies.contains("F")) {
				WebDriver_Functions.Click(By.id("check_fht"));
				WebDriver_Functions.Select(By.id("acct_type_fht") , "SHIPPER","v");//Freight WebDriver_Functions.Type
				WebDriver_Functions.Select(By.id("acct_sub_type_fht") , "DOCK","v");//Freight Sub WebDriver_Functions.Type
			}
			
			WebDriver_Functions.Type(By.id("acctinfo_no_acct"), Integer.toString(NumAccounts)); //number of account numbers that should be created
			WebDriver_Functions.Click(By.id("next_contact"));
			
			//Account Contact Information
			WebDriver_Functions.Type(By.id("first_name"), Account_Info.FirstName);
			WebDriver_Functions.Type(By.id("last_name"), Account_Info.LastName);
			try {
				WebDriver_Functions.Select(By.id("contact_language") , "EN","v");//set the language as English
			}catch (Exception e){}
			
			WebDriver_Functions.Type(By.id("contact_phn_one"), Account_Info.Shipping_Phone_Number);
			WebDriver_Functions.Click(By.name("ship_radio"));
			WebDriver_Functions.Click(By.id("next_address"));
			
			//Account Address
			WebDriver_Functions.Type(By.id("acctinfo_postal_input_info"), Account_Info.Billing_Zip);
			WebDriver_Functions.Type(By.id("add_info_company"), Account_Info.Company_Name);
			WebDriver_Functions.Type(By.id("address_phn_number"), Account_Info.Billing_Phone_Number);
			WebDriver_Functions.Type(By.id("acctinfo_addr_one"), Account_Info.Billing_Address_Line_1);
			WebDriver_Functions.Type(By.id("acctinfo_addr_two"), Account_Info.Billing_Address_Line_2);
			
			//try and select the city, may be only the single city or multiple based on zip code.
			if (WebDriver_Functions.isVisable(By.id("acctinfo_city_input_info_list"))) {
				WebDriver_Functions.WaitClickable(By.id("acctinfo_city_input_info_list"));
				WebDriver_Functions.Select(By.id("acctinfo_city_input_info_list"), Account_Info.Billing_City.toUpperCase(), "v");
			}else if (WebDriver_Functions.isVisable(By.id("acctinfo_city_input_info_box"))) {
				WebDriver_Functions.Type(By.id("acctinfo_city_input_info_box"), Account_Info.Billing_City.toUpperCase());
			}
			
			//enter the state
			if (WebDriver_Functions.isVisable(By.id("acctinfo_state_input_info"))) {
				WebDriver_Functions.Select(By.id("acctinfo_state_input_info"), Account_Info.Billing_State_Code.toUpperCase(), "v");
			}
			
			if (WebDriver_Functions.isVisable(By.id("nomatch"))) {
				WebDriver_Functions.Click(By.id("nomatch"));
			}
			
			Helper_Functions.Wait(4); //need to remove later, issue is page not passing the regulatory section.
			if (WebDriver_Functions.isVisable(By.id("next_payment"))) {
				WebDriver_Functions.Click(By.id("next_payment"));
			}
			
			//if the address validation page is displayed.
			if(WebDriver_Functions.isVisable(By.id("adrs_val_non_modified"))){
				WebDriver_Functions.Select(By.id("addr_validation_override_input_info"), "CUSTOMER_PROVIDED_PROOF", "v");
				WebDriver_Functions.Click(By.id("adrs_val_non_modified"));
			}else if (WebDriver_Functions.isVisable(By.id("nomatch"))){
				WebDriver_Functions.Click(By.id("nomatch"));
			}
			
			//Regulator Information page
			String StateTax = "", CountryTax = "";
			if (WebDriver_Functions.isVisable(By.id("next_reg"))) {
				ArrayList<String[]> TaxData = Environment.getTaxData(BillingCountryCode) ;
				for (String s[]: TaxData) {
					if (s[0].contentEquals(BillingCountryCode)) {
						StateTax = s[1];
						CountryTax = s[2];
						break;
					}
				}
				
				if (WebDriver_Functions.isVisable(By.xpath("//*[@id='mand']")) && StateTax != null){//Tax id 1
					WebDriver_Functions.Type(By.id("reg_tax_id_one"), StateTax);
				}
				if (WebDriver_Functions.isVisable(By.xpath("//*[@id='mand1']")) && CountryTax != null){//Tax id 2
					WebDriver_Functions.Type(By.id("reg_tax_id_two"), CountryTax);
				}
				WebDriver_Functions.Click(By.id("next_reg"));
			}
			
			Helper_Functions.Wait(4); //need to remove later
			
			if (WebDriver_Functions.isVisable(By.id("next_payment"))) {
				WebDriver_Functions.Click(By.id("next_payment"));
			}
			
			if (WebDriver_Functions.isVisable(By.id("nomatch"))) {
				WebDriver_Functions.Click(By.id("nomatch"));
			}
			
			//Added due to L5 issue, there could be a {Alert text : Address could not be validated because of internal error.} ignore it
			Helper_Functions.Wait(4); //need to remove later
			WebDriver_Functions.CloseAlertPopUp();
			
			if (WebDriver_Functions.isVisable(By.id("next_reg"))) {
				WebDriver_Functions.Click(By.id("next_reg"));
			}
			
			//Payment Information
			Credit_Card_Data CC_Info = Environment.getCreditCardDetails(Account_Info.Level, "V");
			String Payment = "Invoice";
			try {
				WebDriver_Functions.WaitPresent(By.id("acct_pay_info_list"));
				WebDriver_Functions.Select(By.id("acct_pay_info_list"), "Credit_Card","v");
				
				//Example  "Visa", "4005554444444460", "460", "12", "20"
				WebDriver_Functions.Type(By.id("acct_payment_info_number_details"), CC_Info.CARD_NUMBER);
				WebDriver_Functions.Type(By.id("name_on_card"), Account_Info.FirstName + " " + Account_Info.LastName);
				WebDriver_Functions.Type(By.id("acct_pay_expiry"), CC_Info.EXPIRATION_MONTH + "/20" + CC_Info.EXPIRATION_YEAR);
				WebDriver_Functions.Type(By.id("acct_pay_cvv_code"), CC_Info.CVV);
				WebDriver_Functions.Select(By.id("acct_payment_info_card_type") , CC_Info.TYPE.toUpperCase(),"v");   //Auto populated
				WebDriver_Functions.Click(By.id("next_comments"));
				Payment = CC_Info.TYPE;
				Account_Data.Set_Credit_Card(Account_Info, CC_Info);
			}catch (Exception e){
				WebDriver_Functions.Select(By.id("acct_pay_info_list") , "Invoice","v");	
				WebDriver_Functions.Click(By.id("next_comments"));
			}
			
			//Comment/confirmation page
			WebDriver_Functions.Click(By.id("comments_form_save"));	
			//*[@id="dialog-confirm"]/center/text()
			WebDriver_Functions.WaitPresent(By.id("dialog-confirm"));
			WebDriver_Functions.WaitForTextPresentIn(By.id("dialog-confirm"), "Account has been created");
	        String AccountNumbers = DriverFactory.getInstance().getDriver().findElement(By.id("dialog-confirm")).getText();
	        Helper_Functions.PrintOut("AccountNumbers:   " + AccountNumbers + "     -- " + Payment, false);
			AccountNumbers = AccountNumbers.replace("Account has been created successfully and Account Numbers are ", "");
			AccountNumbers = AccountNumbers.replaceAll(" ", "");
			
			Account_Data Account_Details[] = new Account_Data[NumAccounts];
			StringTokenizer tok = new StringTokenizer(AccountNumbers, ",", true);

			for (int i = 0 ; i < NumAccounts; i++) {
				Account_Details[i] = new Account_Data(Account_Info);
				String token = tok.nextToken();
				Account_Details[i].Account_Number = token;
			    Account_Details[i].Account_Type = Account_Type;
			    Account_Details[i].Tax_ID_One = StateTax;
			    Account_Details[i].Tax_ID_Two = CountryTax;
			    
			    if (Payment.contentEquals("Invoice")) {
			    	//load the dummy invoice numbers.
			    	Account_Details[i].Invoice_Number_A = Environment.DummyInvoiceOne;
			    	Account_Details[i].Invoice_Number_B = Environment.DummyInvoiceTwo;
			    }

			    if(tok.hasMoreTokens()) {
			    	tok.nextToken();
			    }
			}
			return Account_Details;
		} catch (Exception e) {
			throw e;
		}
	}
	
	public static boolean writeAccountsToExcel(Account_Data Account_Info[],String Operating_Companies) throws Exception{
		String fileName = Helper_Functions.DataDirectory + "\\AddressDetails.xls", sheetName = "Account_Numbers"; 
		
		writeAccountsToExcel(Account_Info, Operating_Companies, fileName, sheetName);
		//write the same accounts to the backup sheet, just for reference later
		writeAccountsToExcel(Account_Info, Operating_Companies, fileName, sheetName + "_Backup");
		return true;
	}
	
	public static boolean writeAccountsToExcel(Account_Data Account_Info[], String Operating_Companies, String fileName, String sheetName) throws Exception{
		for(Account_Data AD: Account_Info) {
			String ContactDetailsParsed[][] = {{"Level", AD.Level},
					{"Shipping_Address_Line_1", AD.Shipping_Address_Line_1},
					{"Shipping_Address_Line_2", AD.Shipping_Address_Line_2},
					{"Shipping_City", AD.Shipping_City},
					{"Shipping_State", AD.Shipping_State},
					{"Shipping_State_Code", AD.Shipping_State_Code},
					{"Shipping_Phone_Number", AD.Shipping_Phone_Number},
					{"Shipping_Zip", AD.Shipping_Zip},
					{"Shipping_Country_Code", AD.Shipping_Country_Code},
					{"Shipping_Region", AD.Shipping_Region},
					{"Shipping_Country", AD.Shipping_Country},
					{"Billing_Address_Line_1", AD.Billing_Address_Line_1},
					{"Billing_Address_Line_2", AD.Billing_Address_Line_2},
					{"Billing_City", AD.Billing_City},
					{"Billing_State", AD.Billing_State},
					{"Billing_State_Code", AD.Billing_State_Code},
					{"Billing_Phone_Number", AD.Billing_Phone_Number},
					{"Billing_Zip", AD.Billing_Zip},
					{"Billing_Country_Code", AD.Billing_Country_Code},
					{"Billing_Region", AD.Billing_Region},
					{"Billing_Country", AD.Billing_Country},
					{"Account_Number", AD.Account_Number},
					{"Credit_Card_Type", AD.Credit_Card_Type},
					{"Credit_Card_Number", AD.Credit_Card_Number},
					{"Credit_Card_CVV", AD.Credit_Card_CVV},
					{"Credit_Card_Expiration_Month", AD.Credit_Card_Expiration_Month},
					{"Credit_Card_Expiration_Year", AD.Credit_Card_Expiration_Year},
					{"Invoice_Number_A", AD.Invoice_Number_A},
					{"Invoice_Number_B", AD.Invoice_Number_B},
					{"Account_Type", AD.Account_Type},
					{"Tax_ID_One", AD.Tax_ID_One },
					{"Tax_ID_Two", AD.Tax_ID_Two}, 
					{"Operating_Companies", Operating_Companies}};
			Helper_Functions.WriteToExcel(fileName, sheetName, ContactDetailsParsed, -1);
		}
		return true;
	}
}
	/*
	
	public static void main(String[] args) {
		ArrayList<String[]> Addresses = new ArrayList<String[]>();
		Addresses = Helper_Functions.getExcelData(".\\Data\\AddressDetails.xls", "Accounts");//load the relevant information from excel file.
		String Level = "2";
		String AddressDetailsFormat[] = Addresses.get(0);
		Helper_Functions.PrintOut(Arrays.toString(AddressDetailsFormat), false);
		// AccountDetails Example = 
		//ShippingCountryCode, BillingCountryCode, OperatingCompanies (E = Express, G = Ground, F = Freight so "EDF" is all three), NumberOfAccounts
		// AddressDetails Example =  {"10 FEDEX PKWY 2nd FL", "", "COLLIERVILLE", "Tennessee", "TN", "38017", "US"});
		//Address line 1, address line 2, City, StateName, StateCode, ZipCode, CountryCode
		
		for(int i = 1; i < Addresses.size(); i++) {
			String CountryDetails[] = Addresses.get(i);
			for (int Format = 0; Format < 8; Format++) {
				boolean update = false;
				String initial = CountryDetails[Format];
				if (CountryDetails[Format].contains("\n") || CountryDetails[Format].contains("  ")) {
					CountryDetails[Format] = CountryDetails[Format].replaceAll("\n", "");
					CountryDetails[Format] = CountryDetails[Format].replaceAll("  ", " ");
				}
				if (Format == 6 && CountryDetails[Format] != CountryDetails[Format].toUpperCase()) {
					CountryDetails[Format] = CountryDetails[Format].toUpperCase();
				}
				if (CountryDetails[Format].length() > 30) {
					CountryDetails[Format] = CountryDetails[Format].substring(0, 29);
				}
				if (!CountryDetails[Format].matches("[^A-Za-z0-9 ]")) {
					CountryDetails[Format] = unAccent(CountryDetails[Format]);
				}
				if (update) {
					PrintOut("Updated: _" + initial + "_ -> _" + CountryDetails[Format], false);
					Helper_Functions.writeExcelData(".\\Data\\AddressDetails.xls", "Accounts", CountryDetails[Format], i, Format);
				}
			}
			
			
			String CountryCode = CountryDetails[6];
			//if (CountryCode.contentEquals("US")) {
				String AccountNumber = Helper_Functions.getExcelFreshAccount(Level, CountryCode, false);
				if (AccountNumber == null || !AccountNumber.contains(",")) {
					String OperatingCompanies = "E";
					if (CountryDetails[7].contentEquals("us")) {
						OperatingCompanies += "F";
					}
				
					String AccountDetails[] = new String[] {CountryCode, CountryCode, OperatingCompanies, "10"};
					String AddressDetails[] = new String[] {CountryDetails[0], CountryDetails[1], CountryDetails[2], CountryDetails[3], CountryDetails[4], CountryDetails[5], CountryCode};
				
					String Accounts = null;
					try {
						Accounts = Helper_Functions.CreateAccountNumbers(Level, AccountDetails, AddressDetails);
						Helper_Functions.PrintOut(Accounts, false);
						writeExcelData(".\\Data\\AddressDetails.xls", "Accounts", Accounts, i, 8 + Integer.valueOf(Level));
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			//}
			
		}

		DriverFactory.getInstance().removeDriver();
	}
	
	public static String unAccent(String s) {
	    //
	    // JDK1.5
	    //   use sun.text.Normalizer.normalize(s, Normalizer.DECOMP, 0);
	    //
	    String temp = Normalizer.normalize(s, Normalizer.Form.NFD);
	    Pattern pattern = Pattern.compile("\\p{InCombiningDiacriticalMarks}+");
	    return pattern.matcher(temp).replaceAll("");
	  }

}

/*
need to add the below to the address excel sheet.


			ContactList.add(new String[] {"3614 DELVERNE RD", "", "BALTIMORE", "Maryland", "MD", "21218", "US", "7i4ksltmhnqlxhzbs9j6356ix"});
			ContactList.add(new String[] {"891 Federal Ridge road", "Apt 202", "COLLIERVILLE", "Tennessee", "TN", "38017", "US", "44k0o0ipf25thfcyl8svm65zz"});
			ContactList.add(new String[] {"910 S MADISON ROW CT", "", "COLLIERVILLE", "Tennessee", "TN",  "38017", "US", "16nsluqsf6tdh736wjaito6"});
			ContactList.add(new String[] {"10 FEDEX PKWY 2nd FL", "", "COLLIERVILLE", "Tennessee", "TN", "38017", "US"});
			ContactList.add(new String[] {"10 FedEx Parkway", "", "COLLIERVILLE", "Tennessee", "TN", "38017", "US"});
			ContactList.add(new String[] {"3400, N Charles St", "Apt #103", "Baltimore", "Maryland", "MD", "21218", "US"});
			ContactList.add(new String[] {"10 FedEx Parkway", "", "COLLIERVILLE", "Tennessee", "TN", "38017", "US"});
			ContactList.add(new String[] {"7939 Silver Lake Lane", "Apt 303", "Memphis", "Tennessee", "TN", "38119", "US"});
			ContactList.add(new String[] {"4900 Alton Court", "", "Irondale", "Alabama", "AL", "35210", "US"});
			ContactList.add(new String[] {"240 Turner Rd", "", "Forrest City", "Arkansas", "AR", "72335", "US"});
			ContactList.add(new String[] {"9015 E VIA LINDA STE 999", "", "SCOTTSDALE", "Arizona", "AZ", "85258", "US"});
			ContactList.add(new String[] {"3901 INGLEWOOD AVE", "", "REDONDO BEACH", "California", "CA", "90278", "US", "3dfkio9dacatwofvg2ydhwahs"});
			ContactList.add(new String[] {"350 spectrum loop", "", "colorado springs", "Colorado", "CO", "80921", "US"});
			ContactList.add(new String[] {"4 MEADOW ST", "", "NORWALK", "Connecticut", "CT", "06854", "US"});
			ContactList.add(new String[] {"1900 SUMMIT TOWER BLVD STE 300", "", "ORLANDO", "Florida", "FL", "32810", "US"});
			ContactList.add(new String[] {"1070 BERTRAM RD", "", "AUGUSTA", "Georgia", "GA", "30909", "US"});
			ContactList.add(new String[] {"1154 FORT STREET MALL", "", "HONOLULU", "Hawaii", "HI", "96813", "US"});
			ContactList.add(new String[] {"8527 UNIVERSITY BLVD STE 99", "", "DES MOINES", "Iowa", "IA", "50325", "US"});
			ContactList.add(new String[] {"1430 E 17TH ST", "", "IDAHO FALLS", "Idaho", "ID", "83404", "US"});
			ContactList.add(new String[] {"1315 W 22ND ST", "", "OAK BROOK", "Illinois", "IL", "60523", "US"});
			ContactList.add(new String[] {"6648 S PERIMETER RD", "", "INDIANAPOLIS", " Indiana", "IN", "46241", "US"});
			ContactList.add(new String[] {"1530 S HOOVER", "", "WICHITA", "Kansas", "KS", "67209", "US"});
			ContactList.add(new String[] {"6330 STRAWBERRY LN", "", "LOUISVILLE", "Kentucky", "KY", "40214", "US"});
			ContactList.add(new String[] {"2122 GREENWOOD RD", "", "SHREVEPORT", "Louisiana", "LA", "71103", "US"});
			ContactList.add(new String[] {"25 Sycamore Ave", "", "Medford", "Massachusetts", "MA", "02155", "US"});
			ContactList.add(new String[] {"95 HUTCHINS DR", "", "PORTLAND", "Maine", "ME", "04102", "US"});
			ContactList.add(new String[] {"2386 TRAVERSEFIELD", "", "TRAVERSE CITY", "Michigan", "MI", "49686", "US"});
			ContactList.add(new String[] {"261 CHESTER ST", "", "SAINT PAUL", "Minnesota", "MN", "55107", "US"});
			ContactList.add(new String[] {"9133 Superior Dr", "", "OLIVE BRANCH", "Mississippi", "MS", "38654", "US"});
			ContactList.add(new String[] {"1203 Beartooth Drive", "", "Laurel", "Montana", "MT", "59044", "US"});
			ContactList.add(new String[] {"3801 BEAM RD STE F", "", "CHARLOTTE", "North Carolina", "NC", "28217", "US"});
			ContactList.add(new String[] {"7130 Q ST", "", "OMAHA", "Nebraska", "NE", "68117", "US"});
			ContactList.add(new String[] {"190 JONY DR", "", "CARLSTADT", "New Jersey", "NJ", "07072", "US"});
			ContactList.add(new String[] {"98 WESTGATE ST", "", "LAS CRUCES", "New Mexico", "NM", "88005", "US"});
			ContactList.add(new String[] {"1025 WESTCHESTER AVE STE", "", "WEST HARRISON", "New York", "NY", "10604", "US"});
			ContactList.add(new String[] {"1330 ELM ST", "", "CINCINNATI", "Ohio", "OH", "45202", "US"});
			ContactList.add(new String[] {"7181 S Mingo Rd", "", "Tulsa", "Oklahoma", "OK", "74133", "US"});
			ContactList.add(new String[] {"1800 NW 169TH PL STE B200", "", "BEAVERTON", "Oregon", "OR", "97006", "US"});
			ContactList.add(new String[] {"6350 HEDGEWOOD DR", "", "ALLENTOWN", "Pennsylvania", "PA", "18106", "US"});
			ContactList.add(new String[] {"255 METRO CENTER BLVD", "", "WARWICK", "Rhode Island", "RI", "02886", "US"});
			ContactList.add(new String[] {"345 W STEAMBOAT DR", "", "NORTH SIOUX CITY", "South Dakota", "SD", "57049", "US"});
			ContactList.add(new String[] {"4200 Regent Blvd", "", "Irving", "Texas", "TX", "75063", "US"});
			ContactList.add(new String[] {"200 S MARQUETTE RD", "", "PRAIRIE DU CHIEN", "Wisconsin", "WI", "53821", "US"});
			ContactList.add(new String[] {"1206 GREENBRIER ST", "", "CHARLESTON", "West Virginia", "WV", "25311", "US"});
			ContactList.add(new String[] {"1249 Tongass Avenue", "", "KETCHIKAN","Alaska", "AK",  "99901", "US"});
			ContactList.add(new String[] {"1555 E University Dr #1", "", "Mesa","Arizona", "AZ",  "85203", "US"});
			ContactList.add(new String[] {"32 Meadow Crest Dr", "", "Sherwood", "Arkansas", "AR", "72120", "US"});
			ContactList.add(new String[] {"329 Madison Street", "", "Denver", "Colorado", "CO", "80206", "US"});
			ContactList.add(new String[] {"58 Cabot St", "", "Hartford", "Connecticut", "CT", "06112", "US"});
			ContactList.add(new String[] {"310 Haines St", "", "Newark", "Delaware", "DE", "19717", "US"});
			ContactList.add(new String[] {"1405 Rhode Island Ave NW", "", "Washington", "District of Columbia", "DC", "20005", "US"});
			ContactList.add(new String[] {"2950 N 28th Terrace", "", "Hollywood", "Florida", "FL", "33020", "US"});
			ContactList.add(new String[] {"901 Hitt St", "", "Columbia", "Missouri", "MO", "65212", "US"});
			ContactList.add(new String[] {"75-681 Lalii Pl", "", "Kailua Kona", "Hawaii", "HI", "96740", "US"});
			ContactList.add(new String[] {"410 W Washington St", "", "Caseyville", "Illinois", "IL", "62232", "US"});
			ContactList.add(new String[] {"1131 Shelby St", "", "Indianapolis", "Indiana", "IN", "46203", "US"});
			ContactList.add(new String[] {"11110 W Greenspoint St", "", "Wichita", "Kansas", "KS", "67205", "US"});
			ContactList.add(new String[] {"5613 Fern Valley Rd", "", "Louisville", "Kentucky", "KY", "40228", "US"});
			ContactList.add(new String[] {"2206 Urbandale St", "", "Shreveport", "Louisiana", "LA", "71118", "US"});
			ContactList.add(new String[] {"89 Turnpike Rd", "", "Ipswich", "Massachusetts", "MA", "01938", "US"});
			ContactList.add(new String[] {"500 S State St # 2005", "", "Ann Arbor", "Michigan", "MI", "48109", "US"});
			ContactList.add(new String[] {"210 Delaware St SE", "", "Minneapolis", "Minnesota", "MN", "55455", "US"});
			
			ContactList.add(new String[] {"310 ROUTE 70", "", "ABU DHABI", "", "", "", "AE"});
			ContactList.add(new String[] {"Ciudad Evita", "", "DUA Buenos Aires", "", "", "B1778", "AR"});
			ContactList.add(new String[] {"Regina Bianchi Peruzzo", "", "Rio Grande do Sul", "Rio Grande do Sul", "RS", "99965", "BR"});
			ContactList.add(new String[] {"2 MARINE PARADE", "", "BELIZE CITY", "", "", "480", "BZ"});
			ContactList.add(new String[] {"1100 BOUL RENE-LEVESQUE E", "", "QUEBEC", "QUEBEC", "PQ", "G1R 5V2", "CA"});
			ContactList.add(new String[] {"", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "CL"});
			ContactList.add(new String[] {"BROADWAY LAYOUT", "", "BEIJING ODA", "", "", "102200", "CN"});
			ContactList.add(new String[] {"791 MILL STREET", "", "WITTEN", "", "", "58448", "DE"});
			ContactList.add(new String[] {"120 MARINE DRIVE", "", "LAUTOKA", "", "", "", "FJ"});
			ContactList.add(new String[] {"4 Boulevard Berthier", "", "Paris", "", "", "75017", "FR"});
			ContactList.add(new String[] {"333 SHERWOOD DRIVE", "", "NEWPORT", "", "", "TF10 7BX", "GB"});
			ContactList.add(new String[] {"10 AVENUE PARK", "", "BARRIGADA", "", "", "96913", "GU"});
			ContactList.add(new String[] {"BROADWAY LAYOUT", "", "BEIJING ODA", "", "", "102200", "HK"});
			ContactList.add(new String[] {"150 Kennedy Road", "", "HONG KONG", "", "", "", "HK"});
			ContactList.add(new String[] {"Aghapura", "", "", "Telangana", "TS", "500001", "IN"});
			ContactList.add(new String[] {"Via di Acqua Bullicante", "", "Roma", "", "", "00176", "IT"});
			ContactList.add(new String[] {"1-5-2 Higashi-Shimbashi", "", "Minato-ku", "", "", "1057123", "JP"});
			ContactList.add(new String[] {"Calle San Juan de Dios 68", "", "MEXICO", "Distrito Federal", "DF", "14370", "MX"});
			ContactList.add(new String[] {"10, Lorong P Ramlee, Kuala Lumpur", "", "KUALA LUMPUR", "", "", "50250", "MY"});
			ContactList.add(new String[] {"15 Stevens Cl", "", "Singapore", "", "", "25795", "SG"});





//to get declaired variables from a class, look into this later on how it works.
//https://stackoverflow.com/questions/2466038/how-do-i-iterate-over-class-members
public int getObject(Object obj) {
    for (Field field : obj.getClass().getDeclaredFields()) {
        //field.setAccessible(true); // if you want to modify private fields
        System.out.println(field.getName()
                 + " - " + field.getType()
                 + " - " + field.get(obj));
    }
}
 */
