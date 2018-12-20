package Mission_Critical;

import org.testng.annotations.Test;

import Data_Structures.Account_Data;

import org.testng.annotations.BeforeClass;
import org.testng.Assert;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Listeners;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import SupportClasses.*;

@Listeners(SupportClasses.TestNG_TestListener.class)

public class MC_PI_1{
	static String LevelsToTest = "3";

	@BeforeClass
	public void beforeClass() {
		Environment.SetLevelsToTest(LevelsToTest);
	}
	
	@DataProvider (parallel = true)
	public static Iterator<Object[]> dp(Method m) {
		List<Object[]> data = new ArrayList<Object[]>();

		for (int i=0; i < Environment.LevelsToTest.length(); i++) {
			String Level = String.valueOf(Environment.LevelsToTest.charAt(i));
			switch (m.getName()) { //Based on the method that is being called the array list will be populated.
		    	case "BR_TaxID":
		    			String EnrollmentID[] = Helper_Functions.LoadEnrollmentIDs("BR");
		    			ArrayList<String[]> TaxInfo = Helper_Functions.getTaxInfo("BR");
		    			for (String Tax[]: TaxInfo) {
		    				if (Tax[4].contentEquals("B")) {
		    					data.add(new Object[] {Level, EnrollmentID[0], "BR", Tax, true});
		    				}else {
		    					data.add(new Object[] {Level, EnrollmentID[0], "BR", Tax, false});
		    				}
		    				
		    			}
		    		break;
		    	case "Account_Number_Masking":
		    		Account_Data[] Accounts = Environment.getAccountDetails(Level);
		    		boolean invoiceflag = false, creditcardflag = false;
		    		for (int j = 0; j < Accounts.length - 1; j++) {
		    			if (!invoiceflag && Accounts[j].Billing_Country_Code.contentEquals("US") && Accounts[j].Credit_Card_Type.isEmpty()) {
		    				data.add( new Object[] {Level, Accounts[j], ""});
				    		data.add( new Object[] {Level, Accounts[j], "Nickname"});
				    		invoiceflag = true;
		    			}else if (!creditcardflag && Accounts[j].Billing_Country_Code.contentEquals("US") && Accounts[j].Invoice_Number_A.isEmpty()) {
		    				data.add( new Object[] {Level, Accounts[j], ""});
				    		data.add( new Object[] {Level, Accounts[j], "Nickname"});
				    		creditcardflag = true;
		    			}
		    			
		    			if (creditcardflag && invoiceflag) {
		    				break;
		    			}
		    		}
		    		break;
		    	case "TNT_Vat_Validation":
		    		String Vat_Validation[] = {"GB", "FR", "BE", "NL", "LU"};
		    		Vat_Validation = new String[] {"GB"};
		    		for (int j = 0; j < Vat_Validation.length; j++) {
		    			EnrollmentID = Helper_Functions.LoadEnrollmentIDs(Vat_Validation[j]);
		    			ArrayList<String[]> TaxInfoLoc = Helper_Functions.getTaxInfo(Vat_Validation[j]);
		    			//for each vat example of the country add a test
		    			for (String Tax[]: TaxInfoLoc) {
		    				
		    				if (Tax[4].contentEquals("B")) {//business account
		    					data.add(new Object[] {Level, EnrollmentID[0], Vat_Validation[j], Tax, Tax[2], true});
		    				}else {
		    					data.add(new Object[] {Level, EnrollmentID[0], Vat_Validation[j], Tax, Tax[2], false});
		    				}
		    				/*
		    				if (Tax[4].contentEquals("B1")) {//when trying to test specific examples, need to update the excel sheet.
		    					data.add(new Object[] {Level, EnrollmentID[0], Vat_Validation[j], Tax, Tax[2], true});
		    				}*/
		    				
		    			}
					}
		    		break;
		    	case "TNT_Zip_Validation":
		    		String Zip_Validation[] = {"GB", "NL", "CL"};
		    		Zip_Validation = new String[] {"GB"};
		    		for (int j = 0; j < Zip_Validation.length; j++) {
		    			EnrollmentID = Helper_Functions.LoadEnrollmentIDs(Zip_Validation[j]);
		    			ArrayList<String[]> TaxInfoLoc = Helper_Functions.getTaxInfo(Zip_Validation[j]);
		    			//find a valid vat id numbers
		    			for (String Tax[]: TaxInfoLoc) {
		    				if (Tax[3].contentEquals("Valid")) {
		    					String Address[] = Helper_Functions.LoadAddress(Zip_Validation[j]);
		    					boolean BusinessAccount = false;
		    					if (Tax[4].contentEquals("B")) {//business account
		    						BusinessAccount = true;
			    				}
		    					String ZipCode = Address[5];
		    					//attempt with no spaces
		    					if (ZipCode.contains(" ")) {
		    						data.add(new Object[] {Level, EnrollmentID[0], Address, ZipCode.replaceAll(" ", ""), Tax, BusinessAccount});
		    					}
		    					//make zip code empty
		    					data.add(new Object[] {Level, EnrollmentID[0], Address, "", Tax, BusinessAccount});
		    					//add a random character to the zip
		    					//data.add(new Object[] {Level, EnrollmentID[0], Address, ZipCode + Helper_Functions.getRandomString(1), Tax, BusinessAccount});
		    					//remove last character from zip
		    					//data.add(new Object[] {Level, EnrollmentID[0], Address, ZipCode.substring(0, ZipCode.length() - 1), Tax, BusinessAccount});
		    					//remove first character from zip
		    					//data.add(new Object[] {Level, EnrollmentID[0], Address, ZipCode.substring(1, ZipCode.length()), Tax, BusinessAccount});
		    					break;
		    				}
		    			}
		    			
					}
		    		break;
			}
		}	
		return data.iterator();
	}

	@Test(dataProvider = "dp", description = "349582", enabled = false)
	public void BR_TaxID(String Level, String EnrollmentID, String CountryCode, String VatNumber[], boolean BuisnessAccount) {
		try {
			String CreditCard[] = Helper_Functions.LoadCreditCard("V");
			String ShippingAddress[] = Helper_Functions.LoadAddress(CountryCode), BillingAddress[] = ShippingAddress;
			String UserId = Helper_Functions.LoadUserID("L" + Level + EnrollmentID + "CC");
			String ContactName[] = Helper_Functions.LoadDummyName(CountryCode + "CC", Level);

			String Result[] = WFCL_Functions.CreditCardRegistrationEnroll(EnrollmentID, CreditCard, ShippingAddress, BillingAddress, ContactName, UserId, Helper_Functions.MyEmail, BuisnessAccount, VatNumber);
			Helper_Functions.PrintOut(Arrays.toString(Result), false);
		}catch (Exception e) {
			Assert.fail(e.getMessage());
		}
	}
	
	@Test(dataProvider = "dp", priority = 3,  description = "385877, 385878, 285883", enabled = false)//since this method will consume an account number run after others have completed
	public void Account_Number_Masking(String Level, Account_Data Account_Info, String Nickname) {
		try {
			String UserName[] = Helper_Functions.LoadDummyName("INET", Level);
			Account_Info.FirstName = UserName[0];
			Account_Info.MiddleName = UserName[1];
			Account_Info.LastName = UserName[2];
			
			Account_Info = Account_Data.Set_Account_Nickname(Account_Info, Nickname);
			
			Account_Info.UserId = Helper_Functions.LoadUserID("L" + Level + Account_Info.Billing_Country_Code);
			
			Account_Data Account_Info_Mismatch = new Account_Data(Account_Info);
			//update the address with different data, currently only configured for US account
			Account_Info_Mismatch.Billing_City = "MEMPHIS";
			Account_Info_Mismatch.Billing_State = "Tennessee";
			Account_Info_Mismatch.Billing_State_Code = "TN";
			Account_Info_Mismatch.Billing_Zip = "38119";
			
			String Result = WFCL_Functions_UsingData.Account_Number_Masking(Account_Info, Account_Info_Mismatch);
			Helper_Functions.PrintOut(Result, false);
		}catch (Exception e) {
			Assert.fail(e.getMessage());
		}
	}

	@Test(dataProvider = "dp", description = "368854",enabled = true)
	public void TNT_Vat_Validation(String Level, String EnrollmentID, String CountryCode, String VatNumber[], String TaxID, boolean BuisnessAccount) {
		try {
			String CreditCard[] = Helper_Functions.LoadCreditCard("V");
			String ShippingAddress[] = Helper_Functions.LoadAddress(CountryCode), BillingAddress[] = ShippingAddress;
			String UserId = Helper_Functions.LoadUserID("L" + Level + CountryCode + "CC");
			Helper_Functions.PrintOut(DriverFactory.getScreenshotPath(), true);
			String ContactName[] = Helper_Functions.LoadDummyName(CountryCode + "CC", Level);
			//print out VAT for reference
			Helper_Functions.PrintOut("Vat: " + Arrays.toString(VatNumber), false);
			String Result[] = WFCL_Functions.CreditCardRegistrationEnroll(EnrollmentID, CreditCard, ShippingAddress, BillingAddress, ContactName, UserId, Helper_Functions.MyEmail, BuisnessAccount, VatNumber);
			
 			Helper_Functions.PrintOut(Arrays.toString(Result), false);
		}catch (Exception e) {
			Assert.fail(e.getMessage());
		}
	}
	
	@Test(dataProvider = "dp",  description = "368854", enabled = true)
	public void TNT_Zip_Validation(String Level, String EnrollmentID, String Address[], String ZipCode, String VatNumber[], boolean BuisnessAccount) {
		try {
			String CountryCode = Address[6];
			String CreditCard[] = Helper_Functions.LoadCreditCard("V");
			String ShippingAddress[] = Address, BillingAddress[] = ShippingAddress;
			BillingAddress[5] = ZipCode; //load the given zip code as the billing address.
			String UserId = Helper_Functions.LoadUserID("L" + Level + CountryCode + "CC");
			String ContactName[] = Helper_Functions.LoadDummyName(CountryCode + "CC", Level);
			Helper_Functions.PrintOut("ZipCode Attempt: " + ZipCode, false);
			String Result[] = WFCL_Functions.CreditCardRegistrationEnroll(EnrollmentID, CreditCard, ShippingAddress, BillingAddress, ContactName, UserId, Helper_Functions.MyEmail, BuisnessAccount, VatNumber);
			
 			Helper_Functions.PrintOut(Arrays.toString(Result), false);
		}catch (Exception e) {
			Assert.fail(e.getMessage());
		}
	}
}
