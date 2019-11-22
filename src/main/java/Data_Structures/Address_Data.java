package Data_Structures;

import SupportClasses.Environment;
import edu.emory.mathcs.backport.java.util.Arrays;

public class Address_Data {
	
	public String First_Name;
	public String Last_Name;
	public String Address_Line_1 = "";
	public String Address_Line_2 = "";
	public String City = "";
	public String State = "";
	public String State_Code = "";
	public String Phone_Number = "";
	public String PostalCode = "";
	public String Country_Code = "";
	public String Region = "";
	public String Country = "";
	//only applicable for US currently 5-17-19
	public String Share_Id = "";
	public String Residential = "";
	public String Location_Code = "";
	
	public static String Address_String(Address_Data Address_Info) {
		String Address[] = new String[]{Address_Info.Address_Line_1, 
				Address_Info.Address_Line_2, 
				Address_Info.City, 
				Address_Info.State_Code, 
				Address_Info.PostalCode, 
				Address_Info.Country_Code};

		return Arrays.toString(Address);
	}
	
	public static Address_Data getAddress(String Level, String CountryCode, String AddressLineOne) {
		Account_Data AllAddresses[] = Environment.getAddressDetails();
		CountryCode = CountryCode.toUpperCase();
		
		if (AddressLineOne == null) {
			AddressLineOne = "";
		}
		
		for (Account_Data AD: AllAddresses) {
			if (AD != null && 
					AD.Billing_Address_Info.Country_Code.contentEquals(CountryCode) &&
					(AD.Billing_Address_Info.Address_Line_1.contentEquals(AddressLineOne) || 
							AddressLineOne.contentEquals(""))) {
				Address_Data Address = new Address_Data();
				Address.Address_Line_1 = AD.Billing_Address_Info.Address_Line_1;
				Address.Address_Line_2 = AD.Billing_Address_Info.Address_Line_2;
				Address.City = AD.Billing_Address_Info.City;
				Address.State = AD.Billing_Address_Info.State;
				Address.State_Code = AD.Billing_Address_Info.State_Code;
				Address.Phone_Number = AD.Billing_Address_Info.Phone_Number;
				Address.PostalCode = AD.Billing_Address_Info.PostalCode;
				Address.Country_Code = AD.Billing_Address_Info.Country_Code;
				Address.Region = AD.Billing_Address_Info.Region;
				Address.Country = AD.Billing_Address_Info.Country;
				Address.Share_Id = AD.Billing_Address_Info.Share_Id;
				Address.Location_Code = AD.Billing_Address_Info.Location_Code;
				return Address;
			}
		}
		System.err.println("Data not loaded for country of " + CountryCode);
		return null;
	}
}
