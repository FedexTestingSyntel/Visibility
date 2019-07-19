package Data_Structures;

import java.util.Arrays;

import SupportClasses.Helper_Functions;

public class Account_Data {
	//Note: When adding a value make sure to check that same reflects in the Environment class
	
	//general Account Information
	public String Level = ""; 
	
	//Shipping Address
	public Address_Data Shipping_Address_Info = new Address_Data();
	
	//Billing Address
	public Address_Data Billing_Address_Info = new Address_Data();
	
	
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
	
	public String Company_Name = "";
	public String FirstName = "";
	public String MiddleName = "";
	public String LastName = "";
	public String LanguageCode = "EN";
	public String Email = "accept@testing.com";
	
	public User_Data User_Info = new User_Data();
	
	public Account_Data() {
		//generic constructor
		User_Info.EMAIL_ADDRESS = Email;
	}

	public static void Print_Account_Address(Account_Data Account_Info) {
		if (Account_Info != null) {
			String Address[] = new String[] {Account_Info.Billing_Address_Info.Address_Line_1, Account_Info.Billing_Address_Info.Address_Line_2, Account_Info.Billing_Address_Info.City, Account_Info.Billing_Address_Info.State_Code, Account_Info.Billing_Address_Info.PostalCode, Account_Info.Billing_Address_Info.Country_Code, Account_Info.Billing_Address_Info.Region};
			Helper_Functions.PrintOut("Account: " + Account_Info.Account_Number + "   " + Arrays.toString(Address));
		}else {
			Helper_Functions.PrintOut("Print_Account_Address recieved null Account_Data");
		}
	}
	
	public static void Print_High_Level_Details(Account_Data Account_Info) {
		String HighLevel[] = new String[] {Account_Info.User_Info.USER_ID, Account_Info.User_Info.UUID_NBR, Account_Info.Account_Number, Account_Info.FirstName, Account_Info.MiddleName, Account_Info.LastName, Account_Info.Email};
		Helper_Functions.PrintOut(Arrays.toString(HighLevel));
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
		String ContactName[] = Helper_Functions.LoadDummyName(Account_Info.Billing_Address_Info.Country_Code, Account_Info.Level);
		Account_Info.FirstName = ContactName[0];
		Account_Info.User_Info.FIRST_NM = ContactName[0];
		Account_Info.MiddleName = ContactName[1];
		Account_Info.User_Info.MIDDLE_NM = ContactName[1];
		Account_Info.LastName = ContactName[2];
		Account_Info.User_Info.LAST_NM = ContactName[2];

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
		Account_Info.User_Info.USER_ID = Helper_Functions.LoadUserID(Base);
		Account_Info.User_Info.PASSWORD = Helper_Functions.myPassword;
		Helper_Functions.PrintOut("UserID: " + Account_Info.User_Info.USER_ID + "   Password: " + Account_Info.User_Info.PASSWORD, false);
		return Account_Info;
	}
	
	public static boolean Account_Data_Compair(Account_Data One, Account_Data Two) {
		
		if (One.Level == Two.Level && 
		One.Shipping_Address_Info.Address_Line_1 == Two.Shipping_Address_Info.Address_Line_1 && 
		One.Shipping_Address_Info.Address_Line_2 == Two.Shipping_Address_Info.Address_Line_2 && 
		One.Shipping_Address_Info.City == Two.Shipping_Address_Info.City && 
		One.Shipping_Address_Info.State == Two.Shipping_Address_Info.State && 
		One.Shipping_Address_Info.State_Code == Two.Shipping_Address_Info.State_Code && 
		One.Shipping_Address_Info.PostalCode == Two.Shipping_Address_Info.PostalCode && 
		One.Shipping_Address_Info.Country_Code == Two.Shipping_Address_Info.Country_Code && 
		One.Shipping_Address_Info.Region == Two.Shipping_Address_Info.Region && 
		One.Shipping_Address_Info.Country == Two.Shipping_Address_Info.Country && 
		One.Shipping_Address_Info.Phone_Number == Two.Shipping_Address_Info.Phone_Number && 
		One.Billing_Address_Info.Address_Line_1 == Two.Billing_Address_Info.Address_Line_1 && 
		One.Billing_Address_Info.Address_Line_2 == Two.Billing_Address_Info.Address_Line_2 && 
		One.Billing_Address_Info.City == Two.Billing_Address_Info.City && 
		One.Billing_Address_Info.State == Two.Billing_Address_Info.State && 
		One.Billing_Address_Info.State_Code == Two.Billing_Address_Info.State_Code && 
		One.Billing_Address_Info.PostalCode == Two.Billing_Address_Info.PostalCode && 
		One.Billing_Address_Info.Country_Code == Two.Billing_Address_Info.Country_Code && 
		One.Billing_Address_Info.Region == Two.Billing_Address_Info.Region && 
		One.Billing_Address_Info.Country == Two.Billing_Address_Info.Country && 
		One.Billing_Address_Info.Phone_Number == Two.Billing_Address_Info.Phone_Number && 
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
		One.User_Info.USER_ID == Two.User_Info.USER_ID && 
		One.Email == Two.Email && 
		One.User_Info.PASSWORD == Two.User_Info.PASSWORD && 
		One.Company_Name == Two.Company_Name && 
		One.User_Info.SECRET_QUESTION_DESC == Two.User_Info.SECRET_QUESTION_DESC && 
		One.User_Info.SECRET_ANSWER_DESC == Two.User_Info.SECRET_ANSWER_DESC && 
		One.User_Info.UUID_NBR == Two.User_Info.UUID_NBR) {
			return true;
		}
			return false;
	}
	
	public Account_Data(Account_Data account_Info) {
		this.Level = account_Info.Level;
		this.Shipping_Address_Info.Address_Line_1 = account_Info.Shipping_Address_Info.Address_Line_1;
		this.Shipping_Address_Info.Address_Line_2 = account_Info.Shipping_Address_Info.Address_Line_2;
		this.Shipping_Address_Info.City = account_Info.Shipping_Address_Info.City;
		this.Shipping_Address_Info.State = account_Info.Shipping_Address_Info.State;
		this.Shipping_Address_Info.State_Code = account_Info.Shipping_Address_Info.State_Code;
		this.Shipping_Address_Info.PostalCode = account_Info.Shipping_Address_Info.PostalCode;
		this.Shipping_Address_Info.Country_Code = account_Info.Shipping_Address_Info.Country_Code;
		this.Shipping_Address_Info.Region = account_Info.Shipping_Address_Info.Region;
		this.Shipping_Address_Info.Country = account_Info.Shipping_Address_Info.Country;
		this.Shipping_Address_Info.Phone_Number = account_Info.Shipping_Address_Info.Phone_Number;
		this.Billing_Address_Info.Address_Line_1 = account_Info.Billing_Address_Info.Address_Line_1;
		this.Billing_Address_Info.Address_Line_2 = account_Info.Billing_Address_Info.Address_Line_2;
		this.Billing_Address_Info.City = account_Info.Billing_Address_Info.City;
		this.Billing_Address_Info.State = account_Info.Billing_Address_Info.State;
		this.Billing_Address_Info.State_Code = account_Info.Billing_Address_Info.State_Code;
		this.Billing_Address_Info.PostalCode = account_Info.Billing_Address_Info.PostalCode;
		this.Billing_Address_Info.Country_Code = account_Info.Billing_Address_Info.Country_Code;
		this.Billing_Address_Info.Region = account_Info.Billing_Address_Info.Region;
		this.Billing_Address_Info.Country = account_Info.Billing_Address_Info.Country;
		this.Billing_Address_Info.Phone_Number = account_Info.Billing_Address_Info.Phone_Number;
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
		this.User_Info.USER_ID = account_Info.User_Info.USER_ID;
		this.Email = account_Info.Email;
		this.User_Info.UUID_NBR = account_Info.User_Info.UUID_NBR;
		this.Company_Name = account_Info.Company_Name;
		this.User_Info.SECRET_ANSWER_DESC = account_Info.User_Info.SECRET_ANSWER_DESC;
		this.User_Info.SECRET_QUESTION_DESC = account_Info.User_Info.SECRET_QUESTION_DESC;
		this.User_Info.PASSWORD = account_Info.User_Info.PASSWORD;
		this.LanguageCode = account_Info.LanguageCode;
	}
	
	public void Address_Overwrite(Account_Data account_Info) {
		this.Shipping_Address_Info.Address_Line_1 = account_Info.Shipping_Address_Info.Address_Line_1;
		this.Shipping_Address_Info.Address_Line_2 = account_Info.Shipping_Address_Info.Address_Line_2;
		this.Shipping_Address_Info.City = account_Info.Shipping_Address_Info.City;
		this.Shipping_Address_Info.State = account_Info.Shipping_Address_Info.State;
		this.Shipping_Address_Info.State_Code = account_Info.Shipping_Address_Info.State_Code;
		this.Shipping_Address_Info.PostalCode = account_Info.Shipping_Address_Info.PostalCode;
		this.Shipping_Address_Info.Country_Code = account_Info.Shipping_Address_Info.Country_Code;
		this.Shipping_Address_Info.Region = account_Info.Shipping_Address_Info.Region;
		this.Shipping_Address_Info.Country = account_Info.Shipping_Address_Info.Country;
		this.Shipping_Address_Info.Phone_Number = account_Info.Shipping_Address_Info.Phone_Number;
		this.Billing_Address_Info.Address_Line_1 = account_Info.Billing_Address_Info.Address_Line_1;
		this.Billing_Address_Info.Address_Line_2 = account_Info.Billing_Address_Info.Address_Line_2;
		this.Billing_Address_Info.City = account_Info.Billing_Address_Info.City;
		this.Billing_Address_Info.State = account_Info.Billing_Address_Info.State;
		this.Billing_Address_Info.State_Code = account_Info.Billing_Address_Info.State_Code;
		this.Billing_Address_Info.PostalCode = account_Info.Billing_Address_Info.PostalCode;
		this.Billing_Address_Info.Country_Code = account_Info.Billing_Address_Info.Country_Code;
		this.Billing_Address_Info.Region = account_Info.Billing_Address_Info.Region;
		this.Billing_Address_Info.Country = account_Info.Billing_Address_Info.Country;
		this.Billing_Address_Info.Phone_Number = account_Info.Billing_Address_Info.Phone_Number;
	}
	
	public static boolean Write_Accounts_To_Excel(Account_Data Account_Info[], boolean NewAccount) throws Exception{
		String fileName = Helper_Functions.DataDirectory + "\\AddressDetails.xls", sheetName = "Account_Numbers"; 
		
		for(int i = 0; i < Account_Info.length; i++) {
			String Details[][] = new String[][]{{"Level", Account_Info[i].Level},  ///this is hard coded and used below as position 0
				{"Account_Number", Account_Info[i].Account_Number} ,   ///this is hard coded and used below as position 1
				{"First_Name", Account_Info[i].FirstName}, 
				{"Last_Name", Account_Info[i].LastName} ,
				{"Shipping_Address_Line_1", Account_Info[i].Shipping_Address_Info.Address_Line_1} ,
				{"Shipping_Address_Line_2", Account_Info[i].Shipping_Address_Info.Address_Line_2} ,
				{"Shipping_City", Account_Info[i].Shipping_Address_Info.City} ,
				{"Shipping_State", Account_Info[i].Shipping_Address_Info.State} ,
				{"Shipping_State_Code", Account_Info[i].Shipping_Address_Info.State_Code} ,
				{"Shipping_Phone_Number", Account_Info[i].Shipping_Address_Info.Phone_Number} ,
				{"Shipping_Zip", Account_Info[i].Shipping_Address_Info.PostalCode} ,
				{"Shipping_Country_Code", Account_Info[i].Shipping_Address_Info.Country_Code} ,
				{"Shipping_Region", Account_Info[i].Shipping_Address_Info.Region} ,
				{"Shipping_Country", Account_Info[i].Shipping_Address_Info.Country} ,
				{"Shipping_Share_Id", Account_Info[i].Shipping_Address_Info.Share_Id} ,
				{"Billing_Address_Line_1", Account_Info[i].Billing_Address_Info.Address_Line_1} ,
				{"Billing_Address_Line_2", Account_Info[i].Billing_Address_Info.Address_Line_2} ,
				{"Billing_City", Account_Info[i].Billing_Address_Info.City} ,
				{"Billing_State", Account_Info[i].Billing_Address_Info.State} ,
				{"Billing_State_Code", Account_Info[i].Billing_Address_Info.State_Code} ,
				{"Billing_Phone_Number", Account_Info[i].Billing_Address_Info.Phone_Number} ,
				{"Billing_Zip", Account_Info[i].Billing_Address_Info.PostalCode} ,
				{"Billing_Country_Code", Account_Info[i].Billing_Address_Info.Country_Code} ,
				{"Billing_Region", Account_Info[i].Billing_Address_Info.Region} ,
				{"Billing_Country", Account_Info[i].Billing_Address_Info.Country} ,
				{"Billing_Share_Id", Account_Info[i].Billing_Address_Info.Share_Id} ,
				{"Credit_Card_Type", Account_Info[i].Credit_Card_Type} ,
				{"Credit_Card_Number", Account_Info[i].Credit_Card_Number} ,
				{"Credit_Card_CVV", Account_Info[i].Credit_Card_CVV} ,
				{"Credit_Card_Expiration_Month", Account_Info[i].Credit_Card_Expiration_Month} ,
				{"Credit_Card_Expiration_Year", Account_Info[i].Credit_Card_Expiration_Year} ,
				{"Invoice_Number_A", Account_Info[i].Invoice_Number_A} ,
				{"Invoice_Number_B", Account_Info[i].Invoice_Number_B} ,
				{"Account_Type", Account_Info[i].Account_Type} ,
				{"Tax_ID_One", Account_Info[i].Tax_ID_One} ,
				{"Tax_ID_Two", Account_Info[i].Tax_ID_Two},
				{"Operating_Companies", Account_Info[i].Account_Type}};
			
			int keyPosition[] = new int[] {-1};
			if (!NewAccount) {
				keyPosition = new int[] {0, 1};//will check based on level and account number
			}
				
			try {
				Helper_Functions.WriteToExcel(fileName, sheetName, Details, keyPosition);
				//write the same accounts to the backup sheet, just for reference later
				Helper_Functions.WriteToExcel(fileName, sheetName+ "_Backup", Details, keyPosition);
			}catch (Exception e) {
				//in case file is in use
				fileName = Helper_Functions.DataDirectory + "\\AddressDetails Backup.xls"; sheetName = "Account_Numbers"; 
				Helper_Functions.WriteToExcel(fileName, sheetName, Details, keyPosition);
			}
		}

		return true;
	}
}
