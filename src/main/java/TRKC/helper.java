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
				// uncomment the below if you want to see the error message.
				// String responseCode = response.substring(response.indexOf("\"code\":\"") + 8, response.length());
				// responseCode = response.substring(0, response.indexOf("\""));
				// Helper_Functions.PrintOut("Response does not contain error code of 0. Error most likely present. -- " + responseCode);
				return false;
			}
			return true;
		}
		// Helper_Functions.PrintOut("Response does not contain successful true. \n" + response);
		return false;
	}
	
}
