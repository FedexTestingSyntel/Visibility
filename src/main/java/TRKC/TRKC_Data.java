package TRKC;

import API_Functions.General_API_Calls;
import SupportClasses.Environment;

public class TRKC_Data {
	public String OAuth_Token_Client_ID = "";
	public String OAuth_Token_Client_Secret = "";
	public String OAuth_Token = "";
	
	public String TrackingGenericURL = "";
	public String TrackingWatchListURL = "";
	public String Level = "";

	//Stores the data for each individual level
	public static TRKC_Data DataClass[] = new TRKC_Data[8];
	
	public static TRKC_Data LoadVariables(){
		String Level = Environment.getInstance().getLevel();
		
		int intLevel = Integer.parseInt(Level);
		//if the level details were already loaded then return detail.
		if (DataClass[intLevel] != null) {
			return DataClass[intLevel];
		}
		
		//since the level details have not been loaded load them.
		TRKC_Data DC = new TRKC_Data();
		DC.Level = Level;

		String LevelIdentifier = General_API_Calls.getAPILevelIedntifier(Level, true);
		
  		switch (Level) {
  		case "1":
  			DC.OAuth_Token_Client_ID = "";
			DC.OAuth_Token_Client_Secret = "";
  			break;
  		case "2":
  			DC.OAuth_Token_Client_ID = "";
			DC.OAuth_Token_Client_Secret = "";
  			break;
  		case "3":
  			DC.OAuth_Token_Client_ID = "";
			DC.OAuth_Token_Client_Secret = "";
  			break;
  		case "4":
  			DC.OAuth_Token_Client_ID = "";
			DC.OAuth_Token_Client_Secret = "";
  			break;
  		case "5":
  			DC.OAuth_Token_Client_ID = "";
			DC.OAuth_Token_Client_Secret ="";
  			break;
  		case "6":
  			DC.OAuth_Token_Client_ID = "";
			DC.OAuth_Token_Client_Secret ="";
  			break;
  		case "7":
  			DC.OAuth_Token_Client_ID = "";
			DC.OAuth_Token_Client_Secret = "";
  			break;
		}
  		
  		DC.TrackingGenericURL = LevelIdentifier + "/trackingCal/track";
  		DC.TrackingWatchListURL = LevelIdentifier + "/trackingCal/track/v1/shipmentoptions/watch";
		
		//generate the OAuthToken
		if (!DC.OAuth_Token_Client_ID.contentEquals("") && !DC.OAuth_Token_Client_Secret.contentEquals("")) {
			DC.OAuth_Token = General_API_Calls.getAuthToken(DC.OAuth_Token_Client_ID , DC.OAuth_Token_Client_Secret);	
		}
		
		DataClass[intLevel] = DC;
		
		return DC;
	}

}
