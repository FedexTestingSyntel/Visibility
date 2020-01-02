package ADMC;

import API_Functions.General_API_Calls;
import SupportClasses.Environment;

public class ADMC_Data {
	public String Level = "";
	
	public String RoleAndStatusURL = "";
	public String CustomViews = "";
	
	//Stores the data for each individual level
	private static ADMC_Data DataClass[] = new ADMC_Data[8];
	
	public static ADMC_Data LoadVariables(){
		String Level = Environment.getInstance().getLevel();
		int intLevel = Integer.parseInt(Level);
		//if the level details were already loaded then return detail.
		if (DataClass[intLevel] != null) {
			return DataClass[intLevel];
		}
		
		//since the level details have not been loaded load them.
		ADMC_Data DC = new ADMC_Data();
		DC.Level = Level;
		
  		//currently uses a generic URL.
		String LevelIdentifier = General_API_Calls.getAPILevelIedntifier(Level, true);
  		DC.RoleAndStatusURL = LevelIdentifier + "/adminCal/v1/users/roleandstatus";	
  		DC.CustomViews = LevelIdentifier + "/adminCal/v1/preferences/customviews";
		
		DataClass[intLevel] = DC;
		
		return DC;
	}
}