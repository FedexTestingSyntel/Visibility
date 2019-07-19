package ADMC_Application;

import org.apache.http.client.methods.HttpGet;
import SupportClasses.General_API_Calls;

public class ADMC_Endpoints {
	
	//Will take in the URL and the cookies and then return the users contact information. 
	public static String RoleAndStatus(String RoleAndStatusURL, String Cookie){
		HttpGet httpGet = new HttpGet(RoleAndStatusURL);
		httpGet.addHeader("Cookie", Cookie);
		
		String Response;
		try {
			Response = General_API_Calls.HTTPCall(httpGet, "");
			
			return Response;
			//Example: {"successful": true,"output": {"migrationStatus": true,"userType": "COMPANY_ADMIN"}}
			//         {"output":{"migrationStatus":false,"userType":"NON_MANAGED"},"successful":true}

		} catch (Exception e) {
			return e.getMessage();
		}	
	}
}