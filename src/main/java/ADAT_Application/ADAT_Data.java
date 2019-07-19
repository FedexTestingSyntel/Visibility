package ADAT_Application;

public class ADAT_Data {

	public String Level = "";
	public String CreatePinUrl = "";
	public String CreateUserUrl = "";
	public String VelocityUrl = "";
	public String VerifyPinUrl = "";
	public String OrgPostcard = "FDM-POSTCARD-PIN";
	public String OrgPhone = "FDM-PHONE-PIN";
	public String OrgAddressVelocity = "FDM-ADDRESS-VELOCITY";
	public String OrgPostcardVelocity = "FDM-POSTCARD-VELOCITY";
	public String OrgPhoneVelocity = "FDM-PHONEPIN-VELOCITY";
	public int PostcardPinVelocityThreshold = 0;
	public int PhonePinVelocityThreshold = 0;
	public int AddressVelocityThreshold = 0;

	//Stores the data for each individual level
	private static ADAT_Data DataClass[] = new ADAT_Data[8];
	
	public static ADAT_Data LoadVariables(String Level){
		int intLevel = Integer.parseInt(Level);
		//if the level details were already loaded then return detail.
		if (DataClass[intLevel] != null) {
			return DataClass[intLevel];
		}
		
		//since the level details have not been loaded load them.
		ADAT_Data DC = new ADAT_Data();
		DC.Level = Level;
		
		String LevelIdentifier = "";
  		switch (Level) {
  		case "1": //not confirmed since do not validate on L1
  			LevelIdentifier = ""; 
  			DC.PostcardPinVelocityThreshold = 3;
			DC.PhonePinVelocityThreshold = 3;
			DC.AddressVelocityThreshold = 10;
  			break;
  		case "2":
  			LevelIdentifier = "http://adat9-satxws-l2.test.cloud.fedex.com"; 
  			DC.PostcardPinVelocityThreshold = 3;
			DC.PhonePinVelocityThreshold = 3;
			DC.AddressVelocityThreshold = 10;
  			break;
  		case "3":
  			LevelIdentifier = "http://adat9-udweb-l3-edcw.test.cloud.fedex.com";
  			DC.PostcardPinVelocityThreshold = 3;
			DC.PhonePinVelocityThreshold = 3;
			DC.AddressVelocityThreshold = 10;
  			break;
  		case "4"://not confirmed since do not validate on L4
  			LevelIdentifier = "http://adat-satxws-l4-edcw.test.cloud.fedex.com"; 
  			DC.PostcardPinVelocityThreshold = 3;
			DC.PhonePinVelocityThreshold = 3;
			DC.AddressVelocityThreshold = 10;
  			break;
  		case "5"://not confirmed since do not validate on L4
  			LevelIdentifier = ""; 
  			DC.PostcardPinVelocityThreshold = 3;
			DC.PhonePinVelocityThreshold = 3;
			DC.AddressVelocityThreshold = 10;
  			break;
  		case "6":
  			LevelIdentifier = "http://localhost";
  			DC.PostcardPinVelocityThreshold = 3;
			DC.PhonePinVelocityThreshold = 3;
			DC.AddressVelocityThreshold = 10;
  			break;
  		case "7":
  			LevelIdentifier = "http://adat-satxws-lp.prod.cloud.fedex.com";   
  			//got from Ritchie on 5/19 not able to work and he's getting same. said was working last checkout.
  			//http://c0002104.prod.cloud.fedex.com:7001/arcotuds/services/ArcotUserRegistrySvc 
  			LevelIdentifier = "http://c0002104.prod.cloud.fedex.com"; 
  			DC.PostcardPinVelocityThreshold = 3;
			DC.PhonePinVelocityThreshold = 3;
			DC.AddressVelocityThreshold = 10;
  			break;
		}
  		
  		DC.CreatePinUrl = LevelIdentifier + ":9744/services/WebFortAuthSvc";
  		DC.CreateUserUrl = LevelIdentifier + ":7001/arcotuds/services/ArcotUserRegistrySvc";
  		DC.VelocityUrl = LevelIdentifier + ":7778/services/RiskFortEvaluateRiskSvc";
  		if (Level.contentEquals("3")) {//try and optimize this later for all levels
  			DC.VelocityUrl = "http://adat9-ratxws-l3-edcw.test.cloud.fedex.com" + ":7778/services/RiskFortEvaluateRiskSvc";
  			DC.CreatePinUrl = "http://adat9-satxws-l3-edcw.test.cloud.fedex.com" + ":9744/services/WebFortAuthSvc";
  		}
  		
  		DC.VerifyPinUrl = DC.CreatePinUrl;//same end point is used.
  					
		DataClass[intLevel] = DC;
		return DC;
	}

}
