package ADMC;

import java.util.Arrays;

import org.apache.http.client.methods.HttpGet;

import API_Functions.General_API_Calls;

public class role_and_status {
	
	//Will take in the URL and the cookies and then return the users contact information. 
	public static String RoleAndStatus(String Cookie){
		ADMC_Data ADMC_Details = ADMC_Data.LoadVariables();
		
		HttpGet httpGet = new HttpGet(ADMC_Details.RoleAndStatusURL);
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
	
	public static String[][] getRoleAndStatus(String Details[][], String Cookies){

		
		String RoleAndStatusResponse = RoleAndStatus(Cookies);
		if (RoleAndStatusResponse == null || !RoleAndStatusResponse.contains("\"successful\":true")) {
			// call did not complete successfully
			return Details;
		}
		//Example: {"successful": true,"output": {"migrationStatus": true,"userType": "COMPANY_ADMIN"}}
		//         {"output":{"migrationStatus":false,"userType":"NON_MANAGED"},"successful":true}
		
		String Parse[][] = {{"MIGRATION_STATUS", "migrationStatus\":", ","},
							{"USER_TYPE", "\"userType\":\"", "\""}
							};

		for (int j = 0; j < Parse.length; j++) {
			Details = Arrays.copyOf(Details, Details.length + 1);
			if (RoleAndStatusResponse.contains(Parse[j][1])) {
				//substring staring with the variable in question
				String temp = RoleAndStatusResponse.substring(RoleAndStatusResponse.indexOf(Parse[j][1]) + Parse[j][1].length(), RoleAndStatusResponse.length());
				String value_needed = temp.substring(0, temp.indexOf(Parse[j][2]));
				Details[Details.length - 1] = new String[] {Parse[j][0], value_needed};
			}else {
				Details[Details.length - 1] = new String[] {Parse[j][0], "NA"};
			}
		}
		
		if (!Details[Details.length - 1][1].contains("NON_MANAGED") && Details[Details.length - 2][1].contains("true") ) {
			Details[Details.length - 2][1] = "WADM";
		}else if (!Details[Details.length - 1][1].contains("NON_MANAGED") && Details[Details.length - 2][1].contains("false") ) {
			Details[Details.length - 2][1] = "IPAS";
		}
		
		Details = Arrays.copyOf(Details, Details.length + 1);
		Details[Details.length - 1] = new String[] {"RoleAndStatus", RoleAndStatusResponse};
	
		return Details;
	}
}