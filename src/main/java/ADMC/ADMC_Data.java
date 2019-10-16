package ADMC;

import SupportClasses.Environment;

public class ADMC_Data {
	public String Level = "";
	
	public String RoleAndStatusURL = "";
	
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
  		String GenericLevel = LevelUrlReturn(Level);
  		DC.RoleAndStatusURL = GenericLevel + "/adminCal/v1/users/roleandstatus";	
		
		
		DataClass[intLevel] = DC;
		
		return DC;
	}
	
	public static String LevelUrlReturn(String level2) {
  		String LevelURL = null;
  		switch (level2) {
      		case "1":
      			LevelURL = "https://wwwbase.idev.fedex.com"; break;
      		case "2":
      			LevelURL = "https://wwwdev.idev.fedex.com";  break;
      		case "3":
      			LevelURL = "https://wwwdrt.idev.fedex.com"; break;
      		case "4":
      			LevelURL = "https://wwwstress.dmz.idev.fedex.com"; break;
      		case "5":
      			LevelURL = "https://wwwbit.idev.fedex.com"; break;
      		case "6":
      			LevelURL = "https://wwwtest.fedex.com"; break;
      		case "7":
      			LevelURL = "https://www.fedex.com"; break;
  		}
  		return LevelURL;
  	}
}