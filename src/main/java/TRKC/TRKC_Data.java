package TRKC;

import API_Functions.General_API_Calls;
import SupportClasses.Environment;

public class TRKC_Data {
	public String OAuth_Token_URL = "";
	public String OAuth_Token_Client_ID = "";
	public String OAuth_Token_Client_Secret = "";
	public String OAuth_Token = "";
	
	public String TrackingGenericURL = "";
	public String TrackingWatchListURL = "";
	public String Level = "";

	//Stores the data for each individual level
	public static TRKC_Data DataClass[] = new TRKC_Data[8];
	
	public static TRKC_Data LoadVariables(){
		String currentLevel = Environment.getInstance().getLevel();
		
		int intLevel = Integer.parseInt(currentLevel);
		//if the level details were already loaded then return detail.
		if (DataClass[intLevel] != null) {
			return DataClass[intLevel];
		}
		
		//since the level details have not been loaded load them.
		TRKC_Data DC = new TRKC_Data();
		DC.Level = currentLevel;
		
		String LevelIdentifier[] = null;
  		switch (currentLevel) {
  		case "1":
  			LevelIdentifier = new String[] {"", ""};
  			DC.OAuth_Token_Client_ID = "";
			DC.OAuth_Token_Client_Secret = "";
  			break;
  		case "2":
  			LevelIdentifier = new String[] {"https://wwwdev.idev.fedex.com", ""};
  			DC.OAuth_Token_Client_ID = "";
			DC.OAuth_Token_Client_Secret = "";
  			break;
  		case "3":
  			LevelIdentifier = new String[] {"https://wwwdrt.idev.fedex.com", ""};
  			DC.OAuth_Token_Client_ID = "";
			DC.OAuth_Token_Client_Secret = "";
  			break;
  		case "4":
  			LevelIdentifier = new String[] {"https://wwwstress.dmz.idev.fedex.com", ""};
  			DC.OAuth_Token_Client_ID = "";
			DC.OAuth_Token_Client_Secret = "";
  			break;
  		case "5":
  			LevelIdentifier = new String[] {"https://wwwbit.fedex.com", ""};
  			DC.OAuth_Token_Client_ID = "";
			DC.OAuth_Token_Client_Secret ="";
  			break;
  		case "6":
  			LevelIdentifier = new String[] {"https://wwwtest.fedex.com", ""};
  			DC.OAuth_Token_Client_ID = "";
			DC.OAuth_Token_Client_Secret ="";
  			break;
  		case "7":
  			LevelIdentifier = new String[] {"https://www.fedex.com", ""};
  			DC.OAuth_Token_Client_ID = "";
			DC.OAuth_Token_Client_Secret = "";
  			break;
		}
  		
  		DC.TrackingGenericURL = LevelIdentifier[0] + "/trackingCal/track";
  		DC.TrackingWatchListURL = LevelIdentifier[0] + "/trackingCal/track/v1/shipmentoptions/watch";
		
		//generate the OAuthToken
		if (!DC.OAuth_Token_Client_ID.contentEquals("") && !DC.OAuth_Token_Client_Secret.contentEquals("")) {
	  		DC.OAuth_Token_URL = LevelIdentifier[0] + "/auth/oauth/v2/token";
			DC.OAuth_Token = General_API_Calls.getAuthToken(DC.OAuth_Token_URL, DC.OAuth_Token_Client_ID , DC.OAuth_Token_Client_Secret);	
		}
		
		DataClass[intLevel] = DC;
		
		return DC;
	}

}
