package CMDC_Application;

import API_Functions.General_API_Calls;

public class CMDC_Data {
	public String Level = "";
	
	public String CMDCURL = "";
	
	//Stores the data for each individual level
	private static CMDC_Data DataClass[] = new CMDC_Data[8];
	
	public static CMDC_Data LoadVariables(String Level){
		int intLevel = Integer.parseInt(Level);
		//if the level details were already loaded then return detail.
		if (DataClass[intLevel] != null) {
			return DataClass[intLevel];
		}
		
		//since the level details have not been loaded load them.
		CMDC_Data DC = new CMDC_Data();
		DC.Level = Level;
		
  		//currently uses a generic URL.
		String LevelIdentifier = General_API_Calls.getAPILevelIedntifier(Level, true);
  		DC.CMDCURL = LevelIdentifier + "/commonDataCal/commondata";	
		
		DataClass[intLevel] = DC;
		
		return DC;
	}

}