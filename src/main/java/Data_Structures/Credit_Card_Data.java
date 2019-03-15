package Data_Structures;

public class Credit_Card_Data {
	
	public String TYPE = "";
	public String CARD_NUMBER = "";
	public String CVV = "";
	public String EXPIRATION_MONTH = "";
	public String EXPIRATION_YEAR = "";
	
	public static String[] CreditCardStringArray(Credit_Card_Data Credit_Card_Info) {
		String CardDetails[] = new String[] {Credit_Card_Info.TYPE, Credit_Card_Info.CARD_NUMBER, Credit_Card_Info.CVV, Credit_Card_Info.EXPIRATION_MONTH, Credit_Card_Info.EXPIRATION_YEAR};
		return CardDetails;
	}
	
}
