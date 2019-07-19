package Data_Structures;

public class Credit_Card_Data {
	
	public String TYPE = "";
	public String CARD_NUMBER = "";
	public String CARD_NUMBER_LAST_FOUR = "";
	public String CVV = "";
	public String EXPIRATION_MONTH = "";
	public String EXPIRATION_YEAR = "";
	
	//Billing Address
	public Address_Data Address_Info = new Address_Data();
	
	public static String[] CreditCardStringArray(Credit_Card_Data Credit_Card_Info) {
		String CardDetails[] = new String[] {Credit_Card_Info.TYPE, Credit_Card_Info.CARD_NUMBER, Credit_Card_Info.CVV, Credit_Card_Info.EXPIRATION_MONTH, Credit_Card_Info.EXPIRATION_YEAR};
		return CardDetails;
	}
	
	public static Credit_Card_Data Set_Credit_Card_Address(Credit_Card_Data Credit_Card_Info, Address_Data Address) {
		Credit_Card_Info.Address_Info.Address_Line_1 = Address.Address_Line_1;
		Credit_Card_Info.Address_Info.Address_Line_2 = Address.Address_Line_2;
		Credit_Card_Info.Address_Info.City = Address.City;
		Credit_Card_Info.Address_Info.State = Address.State;
		Credit_Card_Info.Address_Info.State_Code = Address.State_Code;
		Credit_Card_Info.Address_Info.Phone_Number = Address.Phone_Number;
		Credit_Card_Info.Address_Info.PostalCode = Address.PostalCode;
		Credit_Card_Info.Address_Info.Country_Code = Address.Country_Code;
		Credit_Card_Info.Address_Info.Region = Address.Region;
		Credit_Card_Info.Address_Info.Country = Address.Country;
		return Credit_Card_Info;
	}
	
}
