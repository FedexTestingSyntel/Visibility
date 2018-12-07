package SupportClasses;

import java.text.Normalizer;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.regex.Pattern;
import org.openqa.selenium.By;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Listeners;
import org.testng.annotations.Test;
import Data_Structures.Account_Data;

@Listeners(SupportClasses.TestNG_TestListener.class)

public class Account_Lookup extends Helper_Functions{
	static String LevelToTest = "3";
	
	@BeforeClass
	public void beforeClass() {
		Environment.SetLevelsToTest(LevelToTest);
	}
	
	@DataProvider //(parallel = true)
	public static Iterator<Object[]> dp() {
		List<Object[]> data = new ArrayList<Object[]>();
		
		//String AccountsNumbers[] = new String[] {"761391380", "761391500", "761391640", "761391720", "761391780", "761391860"};
		
		String AccountsNumbers[] = new String[] {"642861700", "642861727", "642861743", "642861760", "642861786", "642899341", "642899368", "642899384", "642899406", "642899422"};

				for (String S: AccountsNumbers) {
			data.add( new Object[] {LevelToTest, S});
		}	
		return data.iterator();
	}
	
	@Test(dataProvider = "dp")
	public static void AccountCheck(String Level, String Account_Number){
		Account_Data Account_Info[] = new Account_Data[] {Account_DataAccountDetails(Account_Number, Level)};
		//Helper_Functions.PrintOut("breakpoint", false);
		try {
			Create_Accounts.writeAccountsToExcel(Account_Info);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}//end WRTT_Rate_Sheet
	
	//String[] {Streetline1 - 0, Streetline2 - 1, City - 2, State - 3, StateCode - 4, postalCode - 5, countryCode - 6};
	public static Account_Data Account_DataAccountDetails(String AccountNumber, String Level){
		Account_Data Account_Details = new Account_Data();
		String Streetline1 = "", Streetline2 = "", City ="", State ="", StateCode = "", postalCode = "", countryCode = "", areaCode = "", phoneNumber = "";
		Environment.getInstance().setLevel(Level);
		String TempLevel = "L" + Environment.getInstance().getLevel();
		Account_Details.Level = Environment.getInstance().getLevel();
		if (!"L1L2L3L4".contains(TempLevel)){
			PrintOut("Invalid Level to find account number detials, checking account vs L3", true);
			TempLevel = "L3";
		}
 		try {
 			PrintOut("Account number " + AccountNumber + " recieved from " + Thread.currentThread().getStackTrace()[2].getMethodName(), true);
 			WebDriver_Functions.ChangeURL("JSP", "US", false);
 			WebDriver_Functions.Type(By.name("contactAccountNumber"), AccountNumber);
 			WebDriver_Functions.Type(By.name("contactAccountOpCo"), "FX");

 		    //selects the correct radio button for the level, only works for 1,2,3,4
 			WebDriver_Functions.Click(By.xpath("//input[(@name='contactLevel') and (@value = '" + TempLevel + "')]"));
			WebDriver_Functions.Click(By.name("contactAccountSubmit"));
			String SourceText = DriverFactory.getInstance().getDriver().getPageSource();
			
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
				
				if (k == 0) {
					Account_Details.Shipping_Address_Line_1 = Streetline1;
					Account_Details.Shipping_Address_Line_2 = Streetline2;
					Account_Details.Shipping_City = City;
					Account_Details.Shipping_State = State;
					Account_Details.Shipping_State_Code = StateCode;
					Account_Details.Shipping_Phone_Number = areaCode + phoneNumber;
					Account_Details.Shipping_Zip = postalCode;
					Account_Details.Shipping_Country_Code = countryCode;
					Account_Details.Shipping_Region = "";
					Account_Details.Shipping_Country = "";
				}else if (k == 1) {
					Account_Details.Billing_Address_Line_1 = Streetline1;
					Account_Details.Billing_Address_Line_2 = Streetline2;
					Account_Details.Billing_City = City;
					Account_Details.Billing_State = State;
					Account_Details.Billing_State_Code = StateCode;
					Account_Details.Billing_Phone_Number = areaCode + phoneNumber;
					Account_Details.Billing_Zip = postalCode;
					Account_Details.Billing_Country_Code = countryCode;
					Account_Details.Billing_Region = "";
					Account_Details.Billing_Country = "";
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
			PrintOut("Address Returned: " + Streetline1 + " " + Streetline2 + " " + City+ " " + State+ " " + StateCode+ " " +postalCode+ " " + countryCode, true);
			return Account_Details;
 		}catch (Exception e){
 			e.printStackTrace();
 			PrintOut("Not able to fully retrieve address: " + Streetline1 + " " + Streetline2 + " " + City+ " " + State+ " " + StateCode+ " " +postalCode+ " " + countryCode, true);
			return null;
 		}
 	}//end AccountDetails
}
