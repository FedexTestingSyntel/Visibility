package REGC;

import SupportClasses.Environment;

public class REGC_Data {

	public String CreateNewUserURL;

	//Stores the data for each individual level
	private static REGC_Data DataClass[] = new REGC_Data[8];
	
	public static REGC_Data REGC_Load(){
		String Level = Environment.getInstance().getLevel();
		int intLevel = Integer.parseInt(Level);
		//if the level details were already loaded then return detail.
		if (DataClass[intLevel] != null) {
			return DataClass[intLevel];
		}
		
		//since the level details have not been loaded load them.
		REGC_Data DC = new REGC_Data();
  		
  		//currently uses a generic URL.
  		String GenericLevel = LevelUrlReturn(Level);
  		DC.CreateNewUserURL = GenericLevel + "/regcal/registration/newfcluser";
		
		DataClass[intLevel] = DC;
		
		return DC;
	}
	
	public static String LevelUrlReturn(String level) {
  		String LevelURL = null;
  		switch (level) {
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