package WFCL_Application;

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

public class WFCL_PI_1{
	static String LevelsToTest = "2";

	@BeforeClass
	public void beforeClass() {
		Environment.SetLevelsToTest(LevelsToTest);
	}
	
	@DataProvider (parallel = true)
	public static Iterator<Object[]> dp(Method m) {
		List<Object[]> data = new ArrayList<Object[]>();

		for (int i=0; i < Environment.LevelsToTest.length(); i++) {
			String Level = String.valueOf(Environment.LevelsToTest.charAt(i));
			//int intLevel = Integer.parseInt(Level);
			switch (m.getName()) { //Based on the method that is being called the array list will be populated.
		    	case "BR_TaxID":
		    			String EnrollmentID[] = Helper_Functions.LoadEnrollmentIDs("BR");
		    			ArrayList<String[]> TaxInfo = Helper_Functions.getTaxInfo("BR");
		    			for (String Tax[]: TaxInfo) {
		    				if (Tax[4].contentEquals("B")) {
		    					//data.add(new Object[] {Level, EnrollmentID[0], "BR", Tax, true});
		    				}else {
		    					data.add(new Object[] {Level, EnrollmentID[0], "BR", Tax, false});
		    				}
		    				
		    			}
		    		break;
		    	case "Account_Number_Masking":
		    		Account_Data[] Accounts = Environment.getAccountDetails(Level);
		    		boolean invoiceflag = false, creditcardflag = false;
		    		for (int j = 0; j< Accounts.length; j++) {
		    			if (!invoiceflag && Accounts[j].Billing_Country_Code.contentEquals("US") && Accounts[j].Credit_Card_Type.isEmpty()) {
		    				data.add( new Object[] {Level, "US", Accounts[j].Account_Number, ""});
				    		data.add( new Object[] {Level, "US", Accounts[j].Account_Number, "Nickname"});
				    		invoiceflag = true;
		    			}else if (!creditcardflag && Accounts[j].Billing_Country_Code.contentEquals("US") && Accounts[j].Invoice_Number_A.isEmpty()) {
		    				data.add( new Object[] {Level, "US", Accounts[j].Account_Number, ""});
				    		data.add( new Object[] {Level, "US", Accounts[j].Account_Number, "Nickname"});
				    		creditcardflag = true;
		    			}
		    			
		    			if (creditcardflag && invoiceflag) {
		    				break;
		    			}
		    		}
		    		break;
			}
		}	
		return data.iterator();
	}

	@Test(dataProvider = "dp", description = "349582")
	public void BR_TaxID(String Level, String EnrollmentID, String CountryCode, String VatNumber[], boolean BuisnessAccount) {
		try {
			String CreditCard[] = Helper_Functions.LoadCreditCard("V");
			String ShippingAddress[] = Helper_Functions.LoadAddress(CountryCode), BillingAddress[] = ShippingAddress;
			String UserId = Helper_Functions.LoadUserID("L" + Level + EnrollmentID + "CC");
			String ContactName[] = Helper_Functions.LoadDummyName(CountryCode + "CC", Level);

			String Result[] = WFCL_Functions.CreditCardRegistrationEnroll(EnrollmentID, CreditCard, ShippingAddress, BillingAddress, ContactName, UserId, BuisnessAccount, VatNumber);
			Helper_Functions.PrintOut(Arrays.toString(Result), false);
		}catch (Exception e) {
			Assert.fail(e.getMessage());
		}
	}
	
	@Test(dataProvider = "dp", priority = 3)//since this method will consume an account number run after others have completed
	public void Account_Number_Masking(String Level, String CountryCode, String FreshAccount, String Nickname) {
		try {
			String UserName[] = Helper_Functions.LoadDummyName("INET", Level);
			String UserID = Helper_Functions.LoadUserID("L" + Level + CountryCode);
			String AddressDetails[] = Helper_Functions.AccountDetails(FreshAccount);
			String AddressMismatch[] = new String[AddressDetails.length];
			System.arraycopy( AddressDetails, 0, AddressMismatch, 0, AddressDetails.length );
			//update the address with different data, currently only configured for US account
			AddressMismatch[2] = "MEMPHIS";
			AddressMismatch[4] = "TN";
			AddressMismatch[5] = "38119";
			String Result = WFCL_Functions.WFCL_AdminReg_WithMismatch(UserName, UserID, FreshAccount, AddressDetails, AddressMismatch, Nickname);
			Helper_Functions.PrintOut(Result, false);
		}catch (Exception e) {
			Assert.fail(e.getMessage());
		}
	}
}
