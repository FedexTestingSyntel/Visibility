package REGC;

import API_Functions.General_API_Calls;
import SupportClasses.Environment;

public class REGC_Data {

	public String CreateNewUserURL;

	//Stores the data for each individual level
	private static REGC_Data DataClass[] = new REGC_Data[8];
	
	public static REGC_Data LoadVariables(){
		String Level = Environment.getInstance().getLevel();
		int intLevel = Integer.parseInt(Level);
		//if the level details were already loaded then return detail.
		if (DataClass[intLevel] != null) {
			return DataClass[intLevel];
		}
		
		//since the level details have not been loaded load them.
		REGC_Data DC = new REGC_Data();
  		
  		//currently uses a generic URL.
		String LevelIdentifier = General_API_Calls.getAPILevelIedntifier(Level, true);
  		DC.CreateNewUserURL = LevelIdentifier + "/regcal/registration/newfcluser";
		
		DataClass[intLevel] = DC;
		
		return DC;
	}
}