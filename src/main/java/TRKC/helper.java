package TRKC;

public class helper {

	public static boolean checkValidResponse(String response){
		// must be successful true with no error code in the response.
		
		if (response.contains("{\"successful\":true")) {
			if (response.contains("\"code\"")) {
				// code 0 is the default no issues response.
				if (response.contains("{\"code\":\"0\"")) {
					return true;
				}
				return false;
			}
			return true;
		}
		return false;
	}
	
}
