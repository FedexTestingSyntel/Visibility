package SHPC_Application;

import SupportClasses.General_API_Calls;
import SupportClasses.Helper_Functions;

public class SHPC_Data {
	public String OAuth_Token_URL = "";
	public String OAuth_Token_Client_ID = "";
	public String OAuth_Token_Client_Secret = "";
	public String OAuth_Token = "";
	public String AShipmentURL = "";
	public String Level = "";

	//Stores the data for each individual level
	public static SHPC_Data DataClass[] = new SHPC_Data[8];
	
	public static SHPC_Data LoadVariables(String Level){
		int intLevel = Integer.parseInt(Level);
		//if the level details were already loaded then return detail.
		if (DataClass[intLevel] != null) {
			return DataClass[intLevel];
		}
		
		//since the level details have not been loaded load them.
		SHPC_Data DC = new SHPC_Data();
		DC.Level = Level;
		
		String LevelIdentifier[] = null;
  		switch (Level) {
  		case "1":
  			LevelIdentifier = new String[] {"", ""}; break;
  		case "2":
  			LevelIdentifier = new String[] {"https://apidev.idev.fedex.com", ""}; break;
  		case "3":
  			LevelIdentifier = new String[] {"https://apidrt.idev.fedex.com:8443", ""}; break;
  		case "4":
  			LevelIdentifier = new String[] {"https://apistress.idev.fedex.com", ""}; break;
  		case "5":
  			LevelIdentifier = new String[] {"https://apibit.fedex.com", ""}; break;
  		case "6":
  			//L6 is not valid for direct URL
  			LevelIdentifier = new String[] {"https://apitest.fedex.com", ""}; break;
  		case "7":
  			//L7 is not valid for direct URL
  			LevelIdentifier = new String[] {"https://api.fedex.com", ""}; break;
		}
  		
  		DC.AShipmentURL = LevelIdentifier[0] + "/ship/v3/shipments/";
		
		switch (Level) { //Based on the method that is being called the array list will be populated. This will make the TestNG Pass/Fail results more relevant.
		case "1":
			DC.OAuth_Token_Client_ID = "l7xx1892f99a6f88470ba29abc141cd7bd8d";
			DC.OAuth_Token_Client_Secret ="a4325d011acf4876b3fe3206931b8f5a";
			break;
		case "2":
			DC.OAuth_Token_Client_ID = "l7xx1892f99a6f88470ba29abc141cd7bd8d";
			DC.OAuth_Token_Client_Secret ="a4325d011acf4876b3fe3206931b8f5a";
			break;
		case "3":
			DC.OAuth_Token_Client_ID = "l7xx1892f99a6f88470ba29abc141cd7bd8d";
			DC.OAuth_Token_Client_Secret ="a4325d011acf4876b3fe3206931b8f5a";
			break;
		case "4":
			DC.OAuth_Token_Client_ID = "l7xx4a86a91576b14d4bb7ba81f52470e48d";
			DC.OAuth_Token_Client_Secret ="bb0bc6e8fcba4813989ff50895590f30";
			break;
		case "5"://havn't used L5
			DC.OAuth_Token_Client_ID = "";
			DC.OAuth_Token_Client_Secret ="";
			break;
		case "6":
			DC.OAuth_Token_Client_ID = "l7xxa01b9cda982d40d1abe22f4433a9abb3";
			DC.OAuth_Token_Client_Secret ="86c6b1eb63f1430e90de7c7d05f61f30";
			break;
		case "7":
			DC.OAuth_Token_Client_ID = "";
			DC.OAuth_Token_Client_Secret = "";
			break;
		}
		
		//generate the OAuthToken, please note that this is not valid on L1 as API calls cannot be used on that level
		if (!Level.contentEquals("1")) {
	  		DC.OAuth_Token_URL = LevelIdentifier[0] + "/auth/oauth/v2/token";
			DC.OAuth_Token = General_API_Calls.getAuthToken(DC.OAuth_Token_URL, DC.OAuth_Token_Client_ID , DC.OAuth_Token_Client_Secret);
			Helper_Functions.PrintOut("L" + Level + " SHPC BearerToken generated: " + DC.OAuth_Token);
			
		}
		
		DataClass[intLevel] = DC;
		
		return DC;
	}

}
