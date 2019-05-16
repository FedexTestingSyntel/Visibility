package SupportClasses;

import java.lang.reflect.Method;
import java.text.Normalizer;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.StringTokenizer;
import java.util.regex.Pattern;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.junit.AfterClass;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;
import Data_Structures.Account_Data;

//import org.testng.annotations.Listeners;
//@Listeners(SupportClasses.TestNG_TestListener.class)

public class Account_Lookup extends Helper_Functions{
	static String LevelToTest = "236";
	static Account_Data AllAddresses[];

	@BeforeClass
	public void beforeClass() {
		Environment.SetLevelsToTest(LevelToTest);
		AllAddresses = Environment.getAddressDetails();
	}
	
	@AfterClass
	public void afterClass() {
		AllAddresses = null;
	}
	
	@DataProvider (parallel = true)
	public static Iterator<Object[]> dp(Method m) {
		List<Object[]> data = new ArrayList<Object[]>();
		
		for (int i=0; i < Environment.LevelsToTest.length(); i++) {
			String Level = String.valueOf(Environment.LevelsToTest.charAt(i));
			//int intLevel = Integer.parseInt(Level);
			Account_Data Existing_Account_Numbers[] = Environment.getAccountDetails(Level);
			switch (m.getName()) { //Based on the method that is being called the array list will be populated.
    			case "AccountCheck":
    				String AccountsNumbers[] = ParsedAcconts("768459240, 768459320, 768459380, 768459460, 768459720, 768459800, 768459860, 768459940, 768460000, 768460060");

    				String AccountsAlreadyPresent = "", PendingAdd = "{\"";
    				for (int j = 0; j < Existing_Account_Numbers.length - 1; j++) {
    					AccountsAlreadyPresent+= Existing_Account_Numbers[j].Account_Number + " ";
    				}
    				for (String S: AccountsNumbers) {
    					if (!AccountsAlreadyPresent.contains(S)) {
    						data.add( new Object[] {LevelToTest, S});
    						PendingAdd+= S + "\", \"";
    					}
    				}	
    				PrintOut(data.size() + "\n" + PendingAdd, false);
    				break;
    			case "Update_Account_Details":	
    				for (int j = 0; j < Existing_Account_Numbers.length - 1; j++) {
    					if (Existing_Account_Numbers[j].FirstName.contentEquals("")) {
    						data.add( new Object[] {Existing_Account_Numbers[j].Level, Existing_Account_Numbers[j].Account_Number});
    					}
    				}
    				break;
			}
		}
		return data.iterator();
	}
	
	@Test(dataProvider = "dp", enabled = false)
	public static void AccountCheck(String Level, String Account_Number){
		try {
			Account_Data Account_Info[] = new Account_Data[] {Account_Details(Account_Number, Level)};
			Account_Data.Write_Accounts_To_Excel(Account_Info, true);
			
			if (Account_Info[0].Level == "6") {
				Account_Info[0].Level = "3";
				Account_Data.Write_Accounts_To_Excel(Account_Info, true);
			}
			
		} catch (Exception e) {
			Assert.fail("Cannot retrieve details");
		}
	}
	
	//this is a cleanup function. The goal is to go through all the account numbers from the test data sheet and make sure still present as well as update the values.
	@Test(dataProvider = "dp", enabled = true)
	public static void Update_Account_Details(String Level, String Account_Number){
		try {
			Account_Data Account_Info[] = new Account_Data[] {Account_Details(Account_Number, Level)};
			boolean updated = Account_Data.Write_Accounts_To_Excel(Account_Info, false);
			Assert.assertTrue(updated);
		} catch (Exception e) {
			Assert.fail("Cannot retrieve details");
		}
	}
	
	public static Account_Data Account_Details(String AccountNumber, String Level){
		Account_Data Account_Info = new Account_Data();
		Account_Info.Account_Number = AccountNumber;
		Account_Info.Level = Level;
		Account_Info = Account_Data_Address_Lookup(Account_Info);
		Account_Info = Account_Data_CreditDetail_Lookup(Account_Info);
		Account_Data.Print_Account_Address(Account_Info);
		return Account_Info;
	}
	/*
	public static Account_Data Account_DataAccountDetails(String AccountNumber, String Level, String AccountType){
		Account_Data Account_Details = new Account_Data();
		String Streetline1 = "", Streetline2 = "", City ="", State ="", StateCode = "", postalCode = "", countryCode = "", areaCode = "", phoneNumber = "";
		Environment.getInstance().setLevel(Level);
		String TempLevel = "L" + Environment.getInstance().getLevel();
		Account_Details.Level = Environment.getInstance().getLevel();
		if (TempLevel.contains("L6")){
			PrintOut("Cannot see account numbers directly in L6, attempting in L3.", true);
			TempLevel = "L3";
		}else if (TempLevel.contains("L7")){
			PrintOut("Cannot see account numbers directly in LP, attempting in L4.", true);
			TempLevel = "L4";
		}
 		try {
 			PrintOut("Account number " + AccountNumber + " recieved from " + Thread.currentThread().getStackTrace()[2].getMethodName(), true);
 			WebDriver_Functions.ChangeURL("JSP", "US", false);
 			WebDriver_Functions.WaitPresent(By.name("contactAccountOpCo"));
 			WebDriver_Functions.Type(By.name("contactAccountNumber"), AccountNumber);
 			String SourceText = "";
 			for(;;) {
 				try {//not sure why but fails about 20% and cannot find element.
 	 				WebDriver_Functions.Type(By.name("contactAccountOpCo"), AccountType);
 	 			}catch(Exception CannotFind) {
 	 				WebDriver_Functions.Type(By.name("contactAccountOpCo"), AccountType);
 	 			}

 	 		    //selects the correct radio button for the level, only works for 1,2,3,4
 	 			WebDriver_Functions.Click(By.xpath("//input[(@name='contactLevel') and (@value = '" + TempLevel + "')]"));
 				WebDriver_Functions.Click(By.name("contactAccountSubmit"));
 				SourceText = DriverFactory.getInstance().getDriver().getPageSource();
 				if (SourceText.contains("streetLine")) {
 					break;
 				}else if (AccountType.contentEquals("FX")) {
 					AccountType = "FDFR";
 				}else {
 					throw new Exception("Details not found");
 				}
 			}
 			
 			
			
			int intStartingPoint;
			String start = "name=\"";
			String end  = "\" value=\"";
			
			String StartingPoint = "";
			for (int k = 0; k < 2; k++) {
				StartingPoint = "&lt;streetLine&gt;";
				if (SourceText.indexOf(StartingPoint) < 0){
					StartingPoint = "&lt;customer:streetLine&gt;";
				}else {
					SourceText = SourceText.substring(SourceText.indexOf(StartingPoint) + StartingPoint.length(), SourceText.length());
				}
				SourceText = SourceText.substring(SourceText.indexOf(StartingPoint) + StartingPoint.length(), SourceText.length());
				
				
				Streetline1 = SourceText.substring(SourceText.indexOf(start) + start.length(), SourceText.indexOf(end));
				
				StartingPoint = "additionalLine1&gt;";//save if the account number has an address line1 value
				if(SourceText.indexOf(StartingPoint) > 0){
					intStartingPoint = SourceText.indexOf(StartingPoint) + StartingPoint.length();
					SourceText = SourceText.substring(intStartingPoint, SourceText.length());
					Streetline2 = SourceText.substring(SourceText.indexOf(start) + start.length(), SourceText.indexOf(end));
				}
				
				StartingPoint = "geoPoliticalSubdivision2&gt";
				intStartingPoint = SourceText.indexOf(StartingPoint) + StartingPoint.length();
				SourceText = SourceText.substring(intStartingPoint, SourceText.length());
				if (intStartingPoint > StartingPoint.length()){
					City = SourceText.substring(SourceText.indexOf(start) + start.length(), SourceText.indexOf(end));
				}
				
				StartingPoint = "geoPoliticalSubdivision3&gt";			
				intStartingPoint = SourceText.indexOf(StartingPoint) + StartingPoint.length();
				SourceText = SourceText.substring(intStartingPoint, SourceText.length());
				if (intStartingPoint > StartingPoint.length()){
					StateCode = SourceText.substring(SourceText.indexOf(start) + start.length(), SourceText.indexOf(end));
				}
				
				StartingPoint = "postalCode&gt";
				intStartingPoint = SourceText.indexOf(StartingPoint) + StartingPoint.length();
				SourceText = SourceText.substring(intStartingPoint, SourceText.length());
				if (intStartingPoint > StartingPoint.length()){
					postalCode = SourceText.substring(SourceText.indexOf(start) + start.length(), SourceText.indexOf(end));
				}
				
				
				StartingPoint = "countryCode&gt";
				intStartingPoint = SourceText.indexOf(StartingPoint) + StartingPoint.length();
				SourceText = SourceText.substring(intStartingPoint, SourceText.length());
				countryCode = SourceText.substring(SourceText.indexOf(start) + start.length(), SourceText.indexOf(end));
				
				if (postalCode.length() > 5 && countryCode.contentEquals("US")){
					postalCode = postalCode.substring(0, 5);
				}
				
				StartingPoint = "areaCode&gt";
				intStartingPoint = SourceText.indexOf(StartingPoint) + StartingPoint.length();
				SourceText = SourceText.substring(intStartingPoint, SourceText.length());
				if (intStartingPoint > StartingPoint.length()){
					areaCode = SourceText.substring(SourceText.indexOf(start) + start.length(), SourceText.indexOf(end));
				}
				
				StartingPoint = "phoneNumber&gt";
				intStartingPoint = SourceText.indexOf(StartingPoint) + StartingPoint.length();
				SourceText = SourceText.substring(intStartingPoint, SourceText.length());
				if (intStartingPoint > StartingPoint.length()){
					phoneNumber = SourceText.substring(SourceText.indexOf(start) + start.length(), SourceText.indexOf(end));
				}
				
				//remove any special characters
				String AccountDetails[] = {Streetline1, Streetline2, City, State, StateCode, postalCode, countryCode};
				for (int i = 0; i < AccountDetails.length; i++){
					String nfdNormalizedString = Normalizer.normalize(AccountDetails[i], Normalizer.Form.NFD); 
					Pattern pattern = Pattern.compile("\\p{InCombiningDiacriticalMarks}+");
					AccountDetails[i] = pattern.matcher(nfdNormalizedString).replaceAll("");
				}
				
				if (countryCode.length() > 5 || countryCode.length() == 0){
					return null;
				}
				
				String region = "", countryName = "";
				for (Account_Data Address: AllAddresses) {
					if (Address != null && Address.Billing_Country_Code.contentEquals(countryCode)) {
						region = Address.Billing_Region;
						countryName = Address.Billing_Country;
						break;
					}
				}
				
				if (k == 0) {
					Account_Details.Shipping_Address_Line_1 = Streetline1;
					Account_Details.Shipping_Address_Line_2 = Streetline2;
					Account_Details.Shipping_City = City;
					Account_Details.Shipping_State = State;
					Account_Details.Shipping_State_Code = StateCode;
					Account_Details.Shipping_Phone_Number = areaCode + phoneNumber;
					Account_Details.Shipping_Zip = postalCode;
					Account_Details.Shipping_Country_Code = countryCode;
					Account_Details.Shipping_Region = region;
					Account_Details.Shipping_Country = countryName;
				}else if (k == 1) {
					Account_Details.Billing_Address_Line_1 = Streetline1;
					Account_Details.Billing_Address_Line_2 = Streetline2;
					Account_Details.Billing_City = City;
					Account_Details.Billing_State = State;
					Account_Details.Billing_State_Code = StateCode;
					Account_Details.Billing_Phone_Number = areaCode + phoneNumber;
					Account_Details.Billing_Zip = postalCode;
					Account_Details.Billing_Country_Code = countryCode;
					Account_Details.Billing_Region = region;
					Account_Details.Billing_Country = countryName;
				}
				
			}
			
			WebDriver_Functions.ChangeURL("JSP_Express", "US", false);
			WebDriver_Functions.Select(By.id("express"), "expressCreditCard", "v");
			WebDriver_Functions.Click(By.name("expressSubmit"));
			
			SourceText = DriverFactory.getInstance().getDriver().getPageSource();
			//if the account is linked to a credit card.
			String Credit_Card_Type = "", Credit_Card_Number = "", Credit_Card_CVV = "", Credit_Card_Expiration_Month = "", Credit_Card_Expiration_Year = "", Invoice_Number_A = "", Invoice_Number_B = "";
			
			if (SourceText.contains("creditCardId&gt")) {
				StartingPoint = "creditCardId&gt";
				intStartingPoint = SourceText.indexOf(StartingPoint) + StartingPoint.length();
				SourceText = SourceText.substring(intStartingPoint, SourceText.length());
				if (intStartingPoint > StartingPoint.length()){
					Credit_Card_Number = SourceText.substring(SourceText.indexOf(start) + start.length(), SourceText.indexOf(end));
				}
				Credit_Card_Number = Credit_Card_Number.substring(Credit_Card_Number.length() - 4, Credit_Card_Number.length());
				String Credit_Lookup[] = Helper_Functions.LoadCreditCard(Credit_Card_Number);
				Credit_Card_Type = Credit_Lookup[0];
				Credit_Card_Number = Credit_Lookup[1];
				Credit_Card_CVV = Credit_Lookup[2];
				Credit_Card_Expiration_Month = Credit_Lookup[3];
				Credit_Card_Expiration_Year = Credit_Lookup[4];
						
			}else {
				//load dummy invoice numbers
				Invoice_Number_A = "750000000";
				Invoice_Number_B = "750000001";
			}
			
			Account_Details.Account_Number = AccountNumber;
			Account_Details.Account_Nickname = AccountNumber + "_" + Account_Details.Billing_Country_Code;
			Account_Details.Masked_Account_Number = Account_Details.Account_Nickname + " - " + Account_Details.Account_Number.substring(Account_Details.Account_Number.length() - 3, Account_Details.Account_Number.length());
			Account_Details.Credit_Card_Type = Credit_Card_Type;
			Account_Details.Credit_Card_Number = Credit_Card_Number;
			Account_Details.Credit_Card_CVV = Credit_Card_CVV;
			Account_Details.Credit_Card_Expiration_Month = Credit_Card_Expiration_Month;
			Account_Details.Credit_Card_Expiration_Year = Credit_Card_Expiration_Year;
			Account_Details.Invoice_Number_A = Invoice_Number_A;
			Account_Details.Invoice_Number_B = Invoice_Number_B;
			Account_Details.Account_Type = "";
			Account_Details.Tax_ID_One = "";
			Account_Details.Tax_ID_Two = "";
			String BillingAddress[] = {Account_Details.Billing_Address_Line_1, Account_Details.Billing_Address_Line_2, Account_Details.Billing_City, Account_Details.Billing_State_Code, Account_Details.Billing_Zip, Account_Details.Billing_Country_Code}; 
			PrintOut("Address Returned: " + Account_Details.Account_Number + Arrays.toString(BillingAddress), true);
			//will load dummy values
			Account_Details.Email = Helper_Functions.MyEmail;
			Account_Details.Password = Helper_Functions.myPassword;
			return Account_Details;
 		}catch (Exception e){
 			e.printStackTrace();
 			PrintOut("Not able to fully retrieve address: " + Streetline1 + " " + Streetline2 + " " + City+ " " + State+ " " + StateCode+ " " +postalCode+ " " + countryCode, true);
			return null;
 		}
 	}
	
	*/
	
	public static String[] ParsedAcconts(String AccountNumbers) {
		AccountNumbers = AccountNumbers.replaceAll(",", "");
		AccountNumbers = AccountNumbers + " ";
		AccountNumbers.replaceAll("  ", " ");
		StringTokenizer st1 = new StringTokenizer(AccountNumbers, " "); 
		List<String> list = new ArrayList<String>();
		while (st1.hasMoreTokens()) {
			list.add(st1.nextToken());
		}
		String[] stringArray = list.toArray(new String[0]);
		return stringArray;
	}
	
	public static Account_Data Account_Data_Address_Lookup(Account_Data Account_Info){
  		try{
  			HttpPost httppost = new HttpPost("http://vjb00030.ute.fedex.com:7085/cfCDSTestApp/contact.jsp");

  			httppost.addHeader("Content-Type", "application/x-www-form-urlencoded");
  			httppost.addHeader("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,image/apng,*/*;q=0.8,application/signed-exchange;v=b3");

  			String OpCo[] = new String[] {"FX", "FDFR"};
  			String Level = "L" + Account_Info.Level;
  			
  			if (Level.contains("L6")){
  				PrintOut("Cannot see account numbers directly in L6, attempting in L3.", true);
  				Level = "L3";
  			}else if (Level.contains("L7")){
  				PrintOut("Cannot see account numbers directly in LP, attempting in L4.", true);
  				Level = "L4";
  			}
  			
  			
  			String SourceText = "";
  			for (int i = 0 ; i < 2; i++) {
  	  			List<NameValuePair> urlParameters = new ArrayList<NameValuePair>();
  	  			urlParameters.add(new BasicNameValuePair("contact", "accountContact"));
  	  			urlParameters.add(new BasicNameValuePair("contactAccountNumber", Account_Info.Account_Number));
  	  			urlParameters.add(new BasicNameValuePair("contactAccountOpCo", OpCo[i]));
  	  			urlParameters.add(new BasicNameValuePair("contactAccountContactType", ""));
  	  			urlParameters.add(new BasicNameValuePair("contactAccountSubmit", "submit"));
  	  			urlParameters.add(new BasicNameValuePair("contactLevel", Level));

  	  			httppost.setEntity(new UrlEncodedFormEntity(urlParameters));
  	  			
  	  			HttpEntity entity = httppost.getEntity();
  	  			String Request = EntityUtils.toString(entity, "UTF-8");
  	  			String Response = General_API_Calls.HTTPCall(httppost, Request);	
  	  			SourceText = Response;
  	  			
  	  			//if details not found try next OpCo
  	  			if (SourceText.contains("streetLine")) {
  	  				break;
  	  			}else if (i == OpCo.length - 1) {
  	  				Helper_Functions.PrintOut("Unable to find address details.");
  	  			}
  			}
  			
  			String start = "name = \"", end  = "\" value=\"";
  			int shareId = 0, streetline1 = 1, streetline2 = 2, city = 3, statecode = 4, postalcode = 5, countrycode = 6, areacode = 7, phoneNumber = 8, language = 9, firstName = 10, lastName = 11; 
			String AccountDetails[][] = {{"shareId", "shareId>\">", ""},
					{"streetline1", "streetLine>\">", ""}, 
					{"streetline2", "additionalLine1>\">", ""}, 
					{"city", "geoPoliticalSubdivision2>\">", ""}, 
					{"statecode", "geoPoliticalSubdivision3>\">", ""}, 
					{"postalcode", "postalCode>\">", ""}, 
					{"countrycode", "countryCode>\">", ""}, 
					{"areacode", "areaCode>\">", ""},
					{"phoneNumber", "phoneNumber>\">", ""}, 
					{"language", "<language>\">", ""}, 
					{"firstName", "<firstName>\">", ""}, 
					{"lastName", "<lastName>\">", ""}, 
					};
			

			for (int j = 0; j < 2; j++) {
	  			for (int k = 0 ; k < AccountDetails.length; k++) {
	  				String StartingPoint = AccountDetails[k][1];
	  				int intStartingPoint = SourceText.indexOf(StartingPoint) + StartingPoint.length();
	  				SourceText = SourceText.substring(intStartingPoint, SourceText.length());
	  				//make sure something was found.
	  				if (intStartingPoint > StartingPoint.length()){
	  					int s = SourceText.indexOf(start) + start.length(), e = SourceText.indexOf(end);
	  					AccountDetails[k][2] = SourceText.substring(s, e);
	  				}
	  			}

	  			//remove special characters
  				for (int i = 0; i < AccountDetails.length; i++){
  					String nfdNormalizedString = Normalizer.normalize(AccountDetails[i][2], Normalizer.Form.NFD); 
  					Pattern pattern = Pattern.compile("\\p{InCombiningDiacriticalMarks}+");
  					AccountDetails[i][2] = pattern.matcher(nfdNormalizedString).replaceAll("");
  				}
  				
  				//If the zip is greater then 5 and this is for US address then only return first 5 characters
  				if (AccountDetails[postalcode][2].length() > 5 && AccountDetails[countrycode][2].contentEquals("US")){
  					AccountDetails[postalcode][2] = AccountDetails[postalcode][2].substring(0, 5);
				}
  				
  	  			if (j == 0) {//shipping address
  	  				Account_Info.Shipping_Share_Id = AccountDetails[shareId][2];
  					Account_Info.Shipping_Address_Line_1 = AccountDetails[streetline1][2];
  					Account_Info.Shipping_Address_Line_2 = AccountDetails[streetline2][2];
  					Account_Info.Shipping_City = AccountDetails[city][2];
  					Account_Info.Shipping_State_Code = AccountDetails[statecode][2];
  					Account_Info.Shipping_Zip = AccountDetails[postalcode][2];
  					Account_Info.Shipping_Country_Code = AccountDetails[countrycode][2];
  					Account_Info.Shipping_Phone_Number = AccountDetails[areacode][2] + AccountDetails[phoneNumber][2];		
  				}else {//billing address
  					Account_Info.Billing_Share_Id = AccountDetails[shareId][2];
  					Account_Info.Billing_Address_Line_1 = AccountDetails[streetline1][2];
  					Account_Info.Billing_Address_Line_2 = AccountDetails[streetline2][2];
  					Account_Info.Billing_City = AccountDetails[city][2];
  					Account_Info.Billing_State_Code = AccountDetails[statecode][2];
  					Account_Info.Billing_Zip = AccountDetails[postalcode][2];
  					Account_Info.Billing_Country_Code = AccountDetails[countrycode][2];
  					Account_Info.Billing_Phone_Number = AccountDetails[areacode][2] + AccountDetails[phoneNumber][2];
  				}

  	  			if (!AccountDetails[language][2].contentEquals("")) {
  	  				Account_Info.LanguageCode = AccountDetails[language][2];
  	  			}
  	  			if (!AccountDetails[firstName][2].contentEquals("")) {
	  				Account_Info.FirstName = AccountDetails[firstName][2];
	  			}
  	  			if (!AccountDetails[lastName][2].contentEquals("")) {
  	  				Account_Info.LastName = AccountDetails[lastName][2];
  	  			}
			}

  			//make sure country is valid
  			if (Account_Info.Billing_Country_Code.length() > 5 || Account_Info.Billing_Country_Code.length() == 0){
				return null;
			}else {
  				//check and add the region and country name
				for (Account_Data Address: AllAddresses) {
					boolean Billing = false, Shipping = false;
					if (!Billing && Address != null && Address.Billing_Country_Code.contentEquals(AccountDetails[countrycode][2])) {
						Account_Info.Billing_Region = Address.Billing_Region;
	  					Account_Info.Billing_Country = Address.Billing_Country;
	  					Billing = true;
					}
					if (!Shipping && Address != null && Address.Shipping_Country_Code.contentEquals(AccountDetails[countrycode][2])) {
						Account_Info.Shipping_Region = Address.Shipping_Region;
	  					Account_Info.Shipping_Country = Address.Shipping_Country;
	  					Shipping = true;
					}
					if (Billing && Shipping) {
						break;
					}
				}
			}
  			
  			Account_Info.Account_Nickname = Account_Info.Account_Number + "_" + Account_Info.Billing_Country_Code;
  			Account_Info.Masked_Account_Number = Account_Info.Account_Nickname + " - " + Account_Info.Account_Number.substring(Account_Info.Account_Number.length() - 3, Account_Info.Account_Number.length());
			
  			return Account_Info;
  		}catch (Exception e){
  			e.printStackTrace();
  			return null;
  		}
  	}

	public static Account_Data Account_Data_CreditDetail_Lookup(Account_Data Account_Info){
  		try{
  			HttpClient httpclient = HttpClients.createDefault();
  			HttpPost httppost = new HttpPost("http://vjb00030.ute.fedex.com:7085/cfCDSTestApp/express.jsp");

  			httppost.addHeader("Content-Type", "application/x-www-form-urlencoded");
  			httppost.addHeader("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,image/apng,*/*;q=0.8,application/signed-exchange;v=b3");

  			String Level = Account_Info.Level;
  			if (Account_Info.Level.contains("L6")){
  				PrintOut("Cannot see account numbers directly in L6, attempting in L3.", true);
  				Level = "L3";
  			}else if (Account_Info.Level.contains("L7")){
  				PrintOut("Cannot see account numbers directly in LP, attempting in L4.", true);
  				Level = "L4";
  			}
  			
  			String SourceText = "";
  	  		List<NameValuePair> urlParameters = new ArrayList<NameValuePair>();
  	  		urlParameters.add(new BasicNameValuePair("express", "expressCreditCard"));
  	  		urlParameters.add(new BasicNameValuePair("expressAccountNumber", Account_Info.Account_Number));
  	  		urlParameters.add(new BasicNameValuePair("expressSubmit", "submit"));
  	  		urlParameters.add(new BasicNameValuePair("expressLevel", Level));
  	  			
  	  		httppost.setEntity(new UrlEncodedFormEntity(urlParameters));
  	  		HttpResponse Response = httpclient.execute(httppost);
  	  			
  	  		HttpEntity entity = Response.getEntity();
  	  		SourceText = EntityUtils.toString(entity, "UTF-8");
  			
  			String start = "name = \"", end  = "\" value=\"";
			String AccountDetails[][] = {{"CreditCardType", "<type>", ""}, 
					{"creditCardId", "<creditCardId>", ""}, 
					{"expDateMonth", "<expDateMonth>", ""}, 
					{"expDateYear", "<expDateYear>", ""},  
					};
			

	  		for (int k = 0 ; k < AccountDetails.length; k++) {
	  			String StartingPoint = AccountDetails[k][1];
	  			int intStartingPoint = SourceText.indexOf(StartingPoint) + StartingPoint.length();
	  			SourceText = SourceText.substring(intStartingPoint, SourceText.length());
	  			if (intStartingPoint > StartingPoint.length()){
	  				int s = SourceText.indexOf(start) + start.length(), e = SourceText.indexOf(end);
	  				AccountDetails[k][2] = SourceText.substring(s, e);
	  			}
	  		}
	  			
	  		if (!AccountDetails[0][2].contentEquals("")) {
	  			String Last_Four_Digits = AccountDetails[1][2].substring(AccountDetails[1][2].length() - 4, AccountDetails[1][2].length());
				String Credit_Lookup[] = Helper_Functions.LoadCreditCard(Last_Four_Digits);
				Account_Info.Credit_Card_Type = Credit_Lookup[0];
				Account_Info.Credit_Card_Number = Credit_Lookup[1];
				Account_Info.Credit_Card_CVV = Credit_Lookup[2];
				Account_Info.Credit_Card_Expiration_Month = AccountDetails[2][2];
				//last 2 digits of the year.
				Account_Info.Credit_Card_Expiration_Year = AccountDetails[3][2].substring(AccountDetails[3][2].length() - 2, AccountDetails[3][2].length());
	  		}else {
				//load dummy invoice numbers
	  			Account_Info.Invoice_Number_A = "750000000";
	  			Account_Info.Invoice_Number_B = "750000001";
			}
			
  			return Account_Info;
  		}catch (Exception e){
  			e.printStackTrace();
  			return null;
  		}
  	}
	
}
