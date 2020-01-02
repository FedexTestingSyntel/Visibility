package TRKC;

import SupportClasses.Helper_Functions;

public class helper {

	public static boolean checkValidResponse(String response){
		// must be successful true with no error code in the response.
		
		if (response.contains("{\"successful\":true")) {
			if (response.contains("\"code\"")) {
				// code 0 is the default no issues response.
				if (response.contains("{\"code\":\"0\"")) {
					return true;
				}
				Helper_Functions.PrintOut("Response does not containt error code of 0. Error most likely present. \n" + response);
				return false;
			}
			return true;
		}
		Helper_Functions.PrintOut("Response does not contain successful true. \n" + response);
		return false;
	}
	
}
