package Data_Structures;

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
	
	public String Account_Number = "";
	
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
	public String Account_Nickname = "";
	public String Masked_Account_Number = "";
	public String FirstName = "";
	public String MiddleName = "";
	public String LastName = "";
	public String UUID = "";
	public String UserId = "";
	

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
		this.UUID = account_Info.UUID;
	}


	public Account_Data() {
		//generic constructor
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
}
