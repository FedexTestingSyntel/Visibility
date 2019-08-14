package TRKC_Application;

import SupportClasses.General_API_Calls;
import SupportClasses.Helper_Functions;

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
	
	public static TRKC_Data LoadVariables(String Level){
		int intLevel = Integer.parseInt(Level);
		//if the level details were already loaded then return detail.
		if (DataClass[intLevel] != null) {
			return DataClass[intLevel];
		}
		
		//since the level details have not been loaded load them.
		TRKC_Data DC = new TRKC_Data();
		DC.Level = Level;
		
		String LevelIdentifier[] = null;
  		switch (Level) {
  		case "1":
  			LevelIdentifier = new String[] {"", ""}; break;
  		case "2":
  			LevelIdentifier = new String[] {"https://wwwdev.idev.fedex.com", ""}; break;
  		case "3":
  			LevelIdentifier = new String[] {"https://wwwdrt.idev.fedex.com:8443", ""}; break;
  		case "4":
  			LevelIdentifier = new String[] {"https://wwwstress.idev.fedex.com", ""}; break;
  		case "5":
  			LevelIdentifier = new String[] {"https://wwwbit.fedex.com", ""}; break;
  		case "6":
  			//L6 is not valid for direct URL
  			LevelIdentifier = new String[] {"https://wwwtest.fedex.com", ""}; break;
  		case "7":
  			//L7 is not valid for direct URL
  			LevelIdentifier = new String[] {"https://www.fedex.com", ""}; break;
		}
  		
  		DC.TrackingGenericURL = LevelIdentifier[0] + "trackingCal/track";
  		DC.TrackingWatchListURL = LevelIdentifier[0] + "trackingCal/track/v1/shipmentoptions/watch";
  		
		
		switch (Level) { //Based on the method that is being called the array list will be populated. This will make the TestNG Pass/Fail results more relevant.
		case "1":
			DC.OAuth_Token_Client_ID = "";
			DC.OAuth_Token_Client_Secret ="";
			break;
		case "2":
			DC.OAuth_Token_Client_ID = "";
			DC.OAuth_Token_Client_Secret ="";
			break;
		case "3":
			DC.OAuth_Token_Client_ID = "";
			DC.OAuth_Token_Client_Secret ="";
			break;
		case "4":
			DC.OAuth_Token_Client_ID = "";
			DC.OAuth_Token_Client_Secret ="";
			break;
		case "5"://havn't used L5
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
		
		//generate the OAuthToken, please note that this is not valid on L1 as API calls cannot be used on that level
		/*   Not sure if needed yet
		if (!Level.contentEquals("1")) {
	  		DC.OAuth_Token_URL = LevelIdentifier[0] + "/auth/oauth/v2/token";
			DC.OAuth_Token = General_API_Calls.getAuthToken(DC.OAuth_Token_URL, DC.OAuth_Token_Client_ID , DC.OAuth_Token_Client_Secret);
			Helper_Functions.PrintOut("L" + Level + " SHPC BearerToken generated: " + DC.OAuth_Token);
			
		}
		*/
		
		DataClass[intLevel] = DC;
		
		return DC;
	}

}
