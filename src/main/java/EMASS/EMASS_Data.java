package EMASS;

import SupportClasses.Environment;

public class EMASS_Data {

	String Level = "";
	String Credentials = "832614";
	String LocationCode = "NQAA";
	String Scanning_Info_FedEx_Id = "0000703233";
	String Scanning_Info_FedEx_Route = "BSC";
	String CosmosNumber = "12";
	String Form_ID = "0201"; //default for US to US 
	
	String ShipmentURL = "";
	String SessionCookieURL = "";
	String SessionCookie = null;
	
	//Stores the data for each individual level
	private static EMASS_Data DataClass[] = new EMASS_Data[8];
	
	public static EMASS_Data LoadVariables(String Level){
		int intLevel = Integer.parseInt(Level);
		//if the level details were already loaded then return detail.
		if (DataClass[intLevel] != null) {
			return DataClass[intLevel];
		}
		
		//since the level details have not been loaded load them.
		EMASS_Data DC = new EMASS_Data();
		DC.Level = Level;
		
  		//currently uses a generic URL.
		// String LevelIdentifier = General_API_Calls.getAPILevelIedntifier(Level, true);
  		switch (Level) {
  			case "2":
  				DC.ShipmentURL = "https://devsso.secure.fedex.com/L2/Event-Entry-5539C/main.jsf";
  				DC.SessionCookieURL = "https://devoam.secure.fedex.com/oam/server/auth_cred_submit";
  				break;
  			case "3":
  				DC.ShipmentURL = "https://testsso.secure.fedex.com/L3/Event-Entry-5539C/main.jsf";
  				DC.SessionCookieURL = "https://testoam.secure.fedex.com/oam/server/auth_cred_submit";
  				break;
  		}

		DataClass[intLevel] = DC;
		
		return DC;
	}
	
	public static String getSessionCookie() {
		String Level = Environment.getInstance().getLevel();
		int intLevel = Integer.parseInt(Level);
		if (DataClass[intLevel] == null) {
			LoadVariables(Level);
		}
		if (DataClass[intLevel].SessionCookie == null) {
			DataClass[intLevel].SessionCookie = EMASS.eMASS_session_cookie.getEMASSCookie(DataClass[intLevel].SessionCookieURL, Level);
		}
		
		return DataClass[intLevel].SessionCookie;
	}

}