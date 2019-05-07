package Data_Structures;

public class WIDM_Data {

	public String Level = "";
	public String EndpointUrl = "";

	public static WIDM_Data LoadVariables(String Level){
		//int intLevel = Integer.parseInt(Level);

		//since the level details have not been loaded load them.
		WIDM_Data DC = new WIDM_Data();
		DC.Level = Level;
		
		String LevelIdentifier = "";
  		switch (Level) {
  		case "1": //not confirmed since do not validate on L1
  			LevelIdentifier = "http://widmbase1.idev.fedex.com:10099/iam/im/TEWS6/widm"; 
  			break;
  		case "2":
  			LevelIdentifier = "http://widmdev.idev.fedex.com:10099/iam/im/TEWS6/widm"; 
  			break;
  		case "3":
  			LevelIdentifier = "http://widmdrt.idev.fedex.com:10099/iam/im/TEWS6/widm";
  			break;
  		case "4"://not confirmed since do not validate on L4
  			LevelIdentifier = ""; 
  			break;
  		case "5"://not confirmed since do not validate on L4
  			LevelIdentifier = ""; 
  			break;
  		case "6":
  			LevelIdentifier = "http://widmtest-cos.zmd.fedex.com:10099/iam/im/TEWS6/widm"; 

  			break;
  		case "7":
  			LevelIdentifier = ""; 
  			break;
		}
  
  		DC.EndpointUrl = LevelIdentifier;
  		
		return DC;
	}

}
