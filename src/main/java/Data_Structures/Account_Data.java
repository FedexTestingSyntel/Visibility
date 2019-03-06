package Data_Structures;

import java.util.Arrays;

import SupportClasses.Helper_Functions;

public class Account_Data {
	//Note: When adding a value make sure to check that same reflects in the Environment class
	
	//general Account Information
	public String Level = "";
	
	//Shipping Address
	public String Shipping_Address_Line_1 = "";
	public String Shipping_Address_Line_2 = "";
	public String Shipping_City = "";
	public String Shipping_State = "";
	public String Shipping_State_Code = "";
	public String Shipping_Phone_Number = "";
	public String Shipping_Zip = "";
	public String Shipping_Country_Code = "";
	public String Shipping_Region = "";
	public String Shipping_Country = "";
	
	//Billing Address
	public String Billing_Address_Line_1 = "";
	public String Billing_Address_Line_2 = "";
	public String Billing_City = "";
	public String Billing_State = "";
	public String Billing_State_Code = "";
	public String Billing_Phone_Number = "";
	public String Billing_Zip = "";
	public String Billing_Country_Code = "";
	public String Billing_Region = "";
	public String Billing_Country = "";
	
	//Account Number
	public String Account_Number = "";
	public String Account_Nickname = "";
	public String Masked_Account_Number = "";
	
	//Account Payment Settings
	public String Credit_Card_Type = "";
	public String Credit_Card_Number = "";
	public String Credit_Card_CVV = "";
	public String Credit_Card_Expiration_Month = "";
	public String Credit_Card_Expiration_Year = "";
	public String Invoice_Number_A = "";
	public String Invoice_Number_B = "";
	public String Account_Type = "";
	
	//TaxInformation
	public String Tax_ID_One = "";
	public String Tax_ID_Two = "";
	
	//These are generic values that can be set during registration
	public String Company_Name = "";
	public String FirstName = "";
	public String MiddleName = "";
	public String LastName = "";
	public String Email = "accept@testing.com";
	
	public String UserId = "";
	public String Password = "Test1234";
	public String UUID = "";
	public String Secret_Question = "SP2Q1"; //"What is your mother's first name?"
	public String Secret_Answer = "mom";
	
	public Account_Data() {
		//generic constructor
	}

	public static void Print_Account_Address(Account_Data Account_Info) {
		String Address[] = new String[] {Account_Info.Billing_Address_Line_1, Account_Info.Billing_Address_Line_2, Account_Info.Billing_City, Account_Info.Billing_State_Code, Account_Info.Billing_Zip, Account_Info.Billing_Country_Code, Account_Info.Billing_Region};
		Helper_Functions.PrintOut(Arrays.toString(Address));
	}
	
	public static Account_Data Set_Account_Nickname(Account_Data Account_Info, String Nickname) {
		Account_Info.Account_Nickname = Nickname;
		String Last_Three_Digits = Account_Info.Account_Number.substring(Account_Info.Account_Number.length() - 3, Account_Info.Account_Number.length());
		if (Nickname.contentEquals("")) {
			Account_Info.Masked_Account_Number = "My Account - " + Last_Three_Digits;
		}else {
			Account_Info.Masked_Account_Number = Nickname + " - " + Last_Three_Digits;
		}
		return Account_Info;
	}
	
	public static Account_Data Set_Dummy_Contact_Name(Account_Data Account_Info) {
		String ContactName[] = Helper_Functions.LoadDummyName(Account_Info.Billing_Country_Code, Account_Info.Level);
		Account_Info.FirstName = ContactName[0];
		Account_Info.MiddleName = ContactName[1];
		Account_Info.LastName = ContactName[2];
		return Account_Info;
	}
	
	public static String Last_Four_of_Credit_Card(Account_Data Account_Info) {
		if (Account_Info.Credit_Card_Number != null && !Account_Info.Credit_Card_Number.contentEquals("")) {
			return Account_Info.Credit_Card_Number.substring(Account_Info.Credit_Card_Number.length() - 4, Account_Info.Credit_Card_Number.length());
		}
		return null;
	}
	
	public static Account_Data Set_Credit_Card(Account_Data Account_Info, Credit_Card_Data CC_Info) {
		Account_Info.Credit_Card_Type = CC_Info.TYPE;
		Account_Info.Credit_Card_Number = CC_Info.CARD_NUMBER;
		Account_Info.Credit_Card_CVV = CC_Info.CVV;
		Account_Info.Credit_Card_Expiration_Month = CC_Info.EXPIRATION_MONTH;
		Account_Info.Credit_Card_Expiration_Year = CC_Info.EXPIRATION_YEAR;
		return Account_Info;
	}
	
	public static Account_Data Set_UserId(Account_Data Account_Info, String Base) {
		Account_Info.UserId = Helper_Functions.LoadUserID(Base);
		Account_Info.Password = Helper_Functions.myPassword;
		return Account_Info;
	}
	
	public static boolean Account_Data_Compair(Account_Data One, Account_Data Two) {
		
		if (One.Level == Two.Level && 
		One.Shipping_Address_Line_1 == Two.Shipping_Address_Line_1 && 
		One.Shipping_Address_Line_2 == Two.Shipping_Address_Line_2 && 
		One.Shipping_City == Two.Shipping_City && 
		One.Shipping_State == Two.Shipping_State && 
		One.Shipping_State_Code == Two.Shipping_State_Code && 
		One.Shipping_Zip == Two.Shipping_Zip && 
		One.Shipping_Country_Code == Two.Shipping_Country_Code && 
		One.Shipping_Region == Two.Shipping_Region && 
		One.Shipping_Country == Two.Shipping_Country && 
		One.Shipping_Phone_Number == Two.Shipping_Phone_Number && 
		One.Billing_Address_Line_1 == Two.Billing_Address_Line_1 && 
		One.Billing_Address_Line_2 == Two.Billing_Address_Line_2 && 
		One.Billing_City == Two.Billing_City && 
		One.Billing_State == Two.Billing_State && 
		One.Billing_State_Code == Two.Billing_State_Code && 
		One.Billing_Zip == Two.Billing_Zip && 
		One.Billing_Country_Code == Two.Billing_Country_Code && 
		One.Billing_Region == Two.Billing_Region && 
		One.Billing_Country == Two.Billing_Country && 
		One.Billing_Phone_Number == Two.Billing_Phone_Number && 
		One.Account_Number == Two.Account_Number && 
		One.Credit_Card_Type == Two.Credit_Card_Type && 
		One.Credit_Card_Number == Two.Credit_Card_Number && 
		One.Credit_Card_CVV == Two.Credit_Card_CVV && 
		One.Credit_Card_Expiration_Month == Two.Credit_Card_Expiration_Month && 
		One.Credit_Card_Expiration_Year == Two.Credit_Card_Expiration_Year && 
		One.Invoice_Number_A == Two.Invoice_Number_A && 
		One.Invoice_Number_B == Two.Invoice_Number_B && 
		One.Account_Type == Two.Account_Type && 
		One.Tax_ID_One == Two.Tax_ID_One && 
		One.Tax_ID_Two == Two.Tax_ID_Two && 
		One.Account_Nickname == Two.Account_Nickname && 
		One.Masked_Account_Number == Two.Masked_Account_Number && 
		One.FirstName == Two.FirstName && 
		One.MiddleName == Two.MiddleName && 
		One.LastName == Two.LastName && 
		One.UserId == Two.UserId && 
		One.Email == Two.Email && 
		One.Password == Two.Password && 
		One.Company_Name == Two.Company_Name && 
		One.Secret_Question == Two.Secret_Question && 
		One.Secret_Answer == Two.Secret_Answer && 
		One.UUID == Two.UUID) {
			return true;
		}
			return false;
	}
	
	public Account_Data(Account_Data account_Info) {
		this.Level = account_Info.Level;
		this.Shipping_Address_Line_1 = account_Info.Shipping_Address_Line_1;
		this.Shipping_Address_Line_2 = account_Info.Shipping_Address_Line_2;
		this.Shipping_City = account_Info.Shipping_City;
		this.Shipping_State = account_Info.Shipping_State;
		this.Shipping_State_Code = account_Info.Shipping_State_Code;
		this.Shipping_Zip = account_Info.Shipping_Zip;
		this.Shipping_Country_Code = account_Info.Shipping_Country_Code;
		this.Shipping_Region = account_Info.Shipping_Region;
		this.Shipping_Country = account_Info.Shipping_Country;
		this.Shipping_Phone_Number = account_Info.Shipping_Phone_Number;
		this.Billing_Address_Line_1 = account_Info.Billing_Address_Line_1;
		this.Billing_Address_Line_2 = account_Info.Billing_Address_Line_2;
		this.Billing_City = account_Info.Billing_City;
		this.Billing_State = account_Info.Billing_State;
		this.Billing_State_Code = account_Info.Billing_State_Code;
		this.Billing_Zip = account_Info.Billing_Zip;
		this.Billing_Country_Code = account_Info.Billing_Country_Code;
		this.Billing_Region = account_Info.Billing_Region;
		this.Billing_Country = account_Info.Billing_Country;
		this.Billing_Phone_Number = account_Info.Billing_Phone_Number;
		this.Account_Number = account_Info.Account_Number;
		this.Credit_Card_Type = account_Info.Credit_Card_Type;
		this.Credit_Card_Number = account_Info.Credit_Card_Number;
		this.Credit_Card_CVV = account_Info.Credit_Card_CVV;
		this.Credit_Card_Expiration_Month = account_Info.Credit_Card_Expiration_Month;
		this.Credit_Card_Expiration_Year = account_Info.Credit_Card_Expiration_Year;
		this.Invoice_Number_A = account_Info.Invoice_Number_A;
		this.Invoice_Number_B = account_Info.Invoice_Number_B;
		this.Account_Type = account_Info.Account_Type;
		this.Tax_ID_One = account_Info.Tax_ID_One;
		this.Tax_ID_Two = account_Info.Tax_ID_Two;
		this.Account_Nickname = account_Info.Account_Nickname;
		this.Masked_Account_Number = account_Info.Masked_Account_Number;
		this.FirstName = account_Info.FirstName;
		this.MiddleName = account_Info.MiddleName;
		this.LastName = account_Info.LastName;
		this.UserId = account_Info.UserId;
		this.Email = account_Info.Email;
		this.UUID = account_Info.UUID;
		this.Company_Name = account_Info.Company_Name;
		this.Secret_Answer = account_Info.Secret_Answer;
		this.Secret_Question = account_Info.Secret_Question;
		this.Password = account_Info.Password;
	}
}
