package Data_Structures;

public class WIDM_Data {

	public static String get_EndpointURL(String Level){
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
  			LevelIdentifier = null; 
  			break;
  		case "5"://not confirmed since do not validate on L4
  			LevelIdentifier = null; 
  			break;
  		case "6":
  			LevelIdentifier = "http://widmtest-cos.zmd.fedex.com:10099/iam/im/TEWS6/widm"; 
  			break;
  		case "7":
  			LevelIdentifier = null;  
  			break;
		}
  
  		return LevelIdentifier;
	}
}
