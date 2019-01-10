package CXS_Support;

import API_Calls.General_API_Calls;

public class MFAC_Data {
	public String OAuth_Token_URL = "";
	public String OAuth_Token_Client_ID = "";
	public String OAuth_Token_Client_Secret = "";
	public String OAuth_Token = "";
	public String Level = "";
	public String AIssueURL = "";
	public String AVerifyURL;
	public String AVelocityURL = "";
	public String DIssueURL = "";
	public String DVerifyURL = "";
	public String DVelocityURL = "";
	public String OrgPostcard = "FDM-POSTCARD-PIN";
	public String OrgPhone = "FDM-PHONE-PIN";
	public int PinVelocityThresholdPostcard = 0;
	public int PinVelocityThresholdPhone = 0;
	public int AddressVelocityThreshold = 0;

	//Stores the data for each individual level
	private static MFAC_Data DataClass[] = new MFAC_Data[8];
	
	public static MFAC_Data LoadVariables(String Level){
		int intLevel = Integer.parseInt(Level);
		//if the level details were already loaded then return detail.
		if (DataClass[intLevel] != null) {
			return DataClass[intLevel];
		}
		
		//since the level details have not been loaded load them.
		MFAC_Data DC = new MFAC_Data();
		DC.Level = Level;
		
		String LevelIdentifier[] = null;
  		switch (Level) {
  		case "1":
  			LevelIdentifier = new String[] {"", "http://mfacbase-cos-vip.test.cloud.fedex.com:9090"}; break;
  		case "2":
  			LevelIdentifier = new String[] {"https://apidev.idev.fedex.com:8443", "http://mfacdev-cos-vip.test.cloud.fedex.com:9090"}; break;
  		case "3":
  			LevelIdentifier = new String[] {"https://apidrt.idev.fedex.com:8443", "http://mfacdrt-cos-vip.test.cloud.fedex.com:9090"}; break;
  		case "4":
  			LevelIdentifier = new String[] {"https://apistress.idev.fedex.com", "http://mfacstress-cos-vip.test.cloud.fedex.com:9090"}; break;
  		case "5":
  			LevelIdentifier = new String[] {"https://apibit.idev.fedex.com:8443", "http://mfacbit-cos-vip.test.cloud.fedex.com:9090"}; break;
  		case "6":
  			//L6 is not valid for direct URL
  			LevelIdentifier = new String[] {"https://apitest.fedex.com", ""}; break;
  		case "7":
  			//L7 is not valid for direct URL
  			LevelIdentifier = new String[] {"https://api.fedex.com", ""}; break;
		}
  		
  		DC.OAuth_Token_URL = LevelIdentifier[0] + "/auth/oauth/v2/token";
  		
  		//Load the API URLs
  		if (Level != "1") {//API not applicable to L1
  			DC.AIssueURL = LevelIdentifier[0] + "/security/v1/pin";
  			DC.AVerifyURL = LevelIdentifier[0] + "/security/v1/pin/verify";
			DC.AVelocityURL = LevelIdentifier[0] + "/security/v1/addresses/velocitycheck";
  		}
		
  		//Load the Direct URLs
		if (Level != "6" && Level != "7") {//direct not applicable for L6 and Prod
			DC.DIssueURL = LevelIdentifier[1] + "/mfac/v3/issuePIN";
			DC.DVerifyURL = LevelIdentifier[1] + "/mfac/v3/verifyPIN";
			DC.DVelocityURL = LevelIdentifier[1] + "/mfac/v3/addressVelocityCheck";
		}
		
		switch (Level) { //Based on the method that is being called the array list will be populated. This will make the TestNG Pass/Fail results more relevant.
		case "1":
			DC.OAuth_Token_Client_ID = "l7xx1892f99a6f88470ba29abc141cd7bd8d";
			DC.OAuth_Token_Client_Secret ="a4325d011acf4876b3fe3206931b8f5a";
			DC.PinVelocityThresholdPostcard = 3;
			DC.PinVelocityThresholdPhone = 3;
			DC.AddressVelocityThreshold = 10;
			break;
		case "2":
			DC.OAuth_Token_Client_ID = "l7xx1892f99a6f88470ba29abc141cd7bd8d";
			DC.OAuth_Token_Client_Secret ="a4325d011acf4876b3fe3206931b8f5a";
			DC.PinVelocityThresholdPostcard = 3;
			DC.PinVelocityThresholdPhone = 3;
			DC.AddressVelocityThreshold = 10;
			break;
		case "3":
			DC.OAuth_Token_Client_ID = "l7xx1892f99a6f88470ba29abc141cd7bd8d";
			DC.OAuth_Token_Client_Secret ="a4325d011acf4876b3fe3206931b8f5a";
			DC.PinVelocityThresholdPostcard = 3;
			DC.PinVelocityThresholdPhone = 3;
			DC.AddressVelocityThreshold = 10;
			break;
		case "4":
			DC.OAuth_Token_Client_ID = "l7xx3fb9480460af4b55a542a6d1e6d1f5f2";
			DC.OAuth_Token_Client_Secret ="9355487be3c74da696517393ecede89f";
			DC.PinVelocityThresholdPostcard = 3;
			DC.PinVelocityThresholdPhone = 500;
			DC.AddressVelocityThreshold = 10;
			break;
		case "5"://havn't used L5
			DC.OAuth_Token_Client_ID = "";
			DC.OAuth_Token_Client_Secret ="";
			DC.PinVelocityThresholdPostcard = 3;
			DC.PinVelocityThresholdPhone = 3;
			DC.AddressVelocityThreshold = 10;
			break;
		case "6"://need to update these values manually, do not share
			DC.OAuth_Token_Client_ID = "";
			DC.OAuth_Token_Client_Secret ="";
			DC.PinVelocityThresholdPostcard = 3;
			DC.PinVelocityThresholdPhone = 3;
			DC.AddressVelocityThreshold = 10;
			break;
		case "7"://need to update these values manually, do not share
			DC.OAuth_Token_Client_ID = "";
			DC.OAuth_Token_Client_Secret ="";
			DC.PinVelocityThresholdPostcard = 3;
			DC.PinVelocityThresholdPhone = 3;
			DC.AddressVelocityThreshold = 10;
			break;
		}
		//generate the OAuthToken, please note that this is not valid on L1 as API calls cannot be used on that level
		if (!Level.contentEquals("1")) {
			DC.OAuth_Token = General_API_Calls.getAuthToken(DC.OAuth_Token_URL, DC.OAuth_Token_Client_ID , DC.OAuth_Token_Client_Secret);
		}
		
		DataClass[intLevel] = DC;
		
		return DC;
	}
}
