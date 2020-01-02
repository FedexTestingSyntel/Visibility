package ADMC;

import java.util.Arrays;

import org.apache.http.client.methods.HttpGet;

import API_Functions.General_API_Calls;
import CMDC_Application.CMDC_Data;
import SupportClasses.Environment;

public class customviews {
	
	//Will take in the URL and the cookies and then return the users contact information. 
	public static String CustomViews(String Cookie){
		ADMC_Data ADMC_Details = ADMC_Data.LoadVariables();
		
		HttpGet httpGet = new HttpGet(ADMC_Details.CustomViews);
		httpGet.addHeader("Cookie", Cookie);
		
		String Response;
		try {
			Response = General_API_Calls.HTTPCall(httpGet, "");
			
			return Response;
			//Example: {"successful":true,"output":{"customViews":[{"customViewID":"374732","defaultCustomView":false,"deviceType":"DESKTOP","viewName":"Exceptions","interfaceID":"DEFAULT","viewType":"STANDARD","viewFilters":[{"viewItemType":"FILTER","filterID":"isException","values":["isException"]}],"viewColumns":[{"columnID":"63","sortValue":"N","viewItemType":"COLUMN"},{"columnID":"5","sortValue":"N","viewItemType":"COLUMN"},{"columnID":"62","sortValue":"N","viewItemType":"COLUMN"},{"columnID":"3","sortValue":"N","viewItemType":"COLUMN"},{"columnID":"4","sortValue":"N","viewItemType":"COLUMN"},{"columnID":"24","sortValue":"N","viewItemType":"COLUMN"},{"columnID":"21","sortValue":"N","viewItemType":"COLUMN"},{"columnID":"7","sortValue":"N","viewItemType":"COLUMN"},{"columnID":"17","sortValue":"N","viewItemType":"COLUMN"},{"columnID":"14","sortValue":"N","viewItemType":"COLUMN"},{"columnID":"6","sortValue":"N","viewItemType":"COLUMN"},{"columnID":"61","sortValue":"N","viewItemType":"COLUMN"},{"columnID":"9","sortValue":"N","viewItemType":"COLUMN"},{"columnID":"59","sortValue":"N","viewItemType":"COLUMN"},{"columnID":"60","sortValue":"N","viewItemType":"COLUMN"}],"defaultLayoutType":"LIST_LAYOUT"},{"customViewID":"374721","defaultCustomView":false,"deviceType":"DESKTOP","viewName":"All Shipments","interfaceID":"DEFAULT","viewType":"STANDARD","viewFilters":[{"viewItemType":"FILTER"}],"viewColumns":[{"columnID":"63","sortValue":"N","viewItemType":"COLUMN"},{"columnID":"5","sortValue":"N","viewItemType":"COLUMN"},{"columnID":"62","sortValue":"N","viewItemType":"COLUMN"},{"columnID":"3","sortValue":"N","viewItemType":"COLUMN"},{"columnID":"4","sortValue":"N","viewItemType":"COLUMN"},{"columnID":"24","sortValue":"N","viewItemType":"COLUMN"},{"columnID":"21","sortValue":"N","viewItemType":"COLUMN"},{"columnID":"7","sortValue":"N","viewItemType":"COLUMN"},{"columnID":"17","sortValue":"N","viewItemType":"COLUMN"},{"columnID":"14","sortValue":"N","viewItemType":"COLUMN"},{"columnID":"6","sortValue":"N","viewItemType":"COLUMN"},{"columnID":"61","sortValue":"N","viewItemType":"COLUMN"}],"defaultLayoutType":"LIST_LAYOUT"},{"customViewID":"374722","defaultCustomView":false,"deviceType":"DESKTOP","viewName":"Inbound","interfaceID":"DEFAULT","viewType":"STANDARD","viewFilters":[{"viewItemType":"FILTER","filterID":"isInboundDirection","values":["isInboundDirection"]}],"viewColumns":[{"columnID":"63","sortValue":"N","viewItemType":"COLUMN"},{"columnID":"5","sortValue":"N","viewItemType":"COLUMN"},{"columnID":"62","sortValue":"N","viewItemType":"COLUMN"},{"columnID":"3","sortValue":"N","viewItemType":"COLUMN"},{"columnID":"4","sortValue":"N","viewItemType":"COLUMN"},{"columnID":"24","sortValue":"N","viewItemType":"COLUMN"},{"columnID":"21","sortValue":"N","viewItemType":"COLUMN"},{"columnID":"7","sortValue":"N","viewItemType":"COLUMN"}],"defaultLayoutType":"LIST_LAYOUT"},{"customViewID":"374741","defaultCustomView":true,"deviceType":"DESKTOP","viewName":"Watch list","interfaceID":"DEFAULT","viewType":"STANDARD","viewFilters":[{"viewItemType":"FILTER","filterID":"isWatched","values":["isWatched"]}],"viewColumns":[{"columnID":"63","sortValue":"N","viewItemType":"COLUMN"},{"columnID":"5","sortValue":"N","viewItemType":"COLUMN"},{"columnID":"62","sortValue":"N","viewItemType":"COLUMN"},{"columnID":"3","sortValue":"N","viewItemType":"COLUMN"},{"columnID":"4","sortValue":"N","viewItemType":"COLUMN"},{"columnID":"24","sortValue":"N","viewItemType":"COLUMN"},{"columnID":"21","sortValue":"N","viewItemType":"COLUMN"},{"columnID":"7","sortValue":"N","viewItemType":"COLUMN"},{"columnID":"17","sortValue":"N","viewItemType":"COLUMN"},{"columnID":"14","sortValue":"N","viewItemType":"COLUMN"},{"columnID":"6","sortValue":"N","viewItemType":"COLUMN"},{"columnID":"61","sortValue":"N","viewItemType":"COLUMN"}],"defaultLayoutType":"LIST_LAYOUT"},{"customViewID":"374728","defaultCustomView":false,"deviceType":"DESKTOP","viewName":"Unknown Direction","interfaceID":"DEFAULT","viewType":"STANDARD","viewFilters":[{"viewItemType":"FILTER","filterID":"isUnknownDirection","values":["isUnknownDirection"]}],"viewColumns":[{"columnID":"63","sortValue":"N","viewItemType":"COLUMN"},{"columnID":"5","sortValue":"N","viewItemType":"COLUMN"},{"columnID":"62","sortValue":"N","viewItemType":"COLUMN"},{"columnID":"3","sortValue":"N","viewItemType":"COLUMN"},{"columnID":"24","sortValue":"N","viewItemType":"COLUMN"},{"columnID":"21","sortValue":"N","viewItemType":"COLUMN"},{"columnID":"7","sortValue":"N","viewItemType":"COLUMN"},{"columnID":"14","sortValue":"N","viewItemType":"COLUMN"},{"columnID":"6","sortValue":"N","viewItemType":"COLUMN"}],"defaultLayoutType":"LIST_LAYOUT"},{"customViewID":"374723","defaultCustomView":false,"deviceType":"DESKTOP","viewName":"Outbound","interfaceID":"DEFAULT","viewType":"STANDARD","viewFilters":[{"viewItemType":"FILTER","filterID":"isOutboundDirection","values":["isOutboundDirection"]}],"viewColumns":[{"columnID":"63","sortValue":"N","viewItemType":"COLUMN"},{"columnID":"5","sortValue":"N","viewItemType":"COLUMN"},{"columnID":"62","sortValue":"N","viewItemType":"COLUMN"},{"columnID":"3","sortValue":"N","viewItemType":"COLUMN"},{"columnID":"17","sortValue":"N","viewItemType":"COLUMN"},{"columnID":"14","sortValue":"N","viewItemType":"COLUMN"},{"columnID":"6","sortValue":"N","viewItemType":"COLUMN"},{"columnID":"29","sortValue":"N","viewItemType":"COLUMN"}],"defaultLayoutType":"LIST_LAYOUT"},{"customViewID":"374724","defaultCustomView":false,"deviceType":"DESKTOP","viewName":"Third Party","interfaceID":"DEFAULT","viewType":"STANDARD","viewFilters":[{"viewItemType":"FILTER","filterID":"isThirdPartyDirection","values":["isThirdPartyDirection"]}],"viewColumns":[{"columnID":"63","sortValue":"N","viewItemType":"COLUMN"},{"columnID":"5","sortValue":"N","viewItemType":"COLUMN"},{"columnID":"62","sortValue":"N","viewItemType":"COLUMN"},{"columnID":"24","sortValue":"N","viewItemType":"COLUMN"},{"columnID":"21","sortValue":"N","viewItemType":"COLUMN"},{"columnID":"7","sortValue":"N","viewItemType":"COLUMN"},{"columnID":"14","sortValue":"N","viewItemType":"COLUMN"},{"columnID":"54","sortValue":"N","viewItemType":"COLUMN"},{"columnID":"6","sortValue":"N","viewItemType":"COLUMN"}],"defaultLayoutType":"LIST_LAYOUT"}]}}
			//         {"output":{"migrationStatus":false,"userType":"NON_MANAGED"},"successful":true}

		} catch (Exception e) {
			return e.getMessage();
		}	
	}
	
	public static String[][] getCustomViews(String Details[][], String Cookies){

		
		String CustomViewsResponse = CustomViews(Cookies);
		if (CustomViewsResponse == null || !CustomViewsResponse.contains("\"successful\":true")) {
			// call did not complete successfully
			return Details;
		} else if (CustomViewsResponse.contains("defaultCustomView\":true")) {
			// Save the full response.
			Details = Arrays.copyOf(Details, Details.length + 1);
			Details[Details.length - 1] = new String[] {"CustomViews", CustomViewsResponse};
			
			String customViews = API_Functions.General_API_Calls.ParseStringValue(CustomViewsResponse, "customViews");
			
			String UsersDefaultView = "";
			while(customViews.contains("\"defaultCustomView\"")) {
				// check if the top view is the default view.
				String defaultCustomViewCheck = API_Functions.General_API_Calls.ParseStringValue(customViews, "defaultCustomView");
				if (defaultCustomViewCheck.contentEquals("true")) {
					String DefaultView = "\"defaultCustomView\":\"" + API_Functions.General_API_Calls.ParseStringValue(customViews, "viewName") +  "\"";
					if (UsersDefaultView.contentEquals("")) {
						UsersDefaultView = DefaultView;
						// "defaultCustomView":"NON_MANAGED"
					} else {
						// The user should only have one default view but in case ADMC sends back more update accordingly.
						UsersDefaultView += ", " + DefaultView;
					}
				}
				
				String SearchString = "defaultLayoutType";
				if (customViews.contains(SearchString)) {
					// Remove the view that was just checked
					customViews = customViews.substring(customViews.indexOf(SearchString) + SearchString.length(), customViews.length());
				} else {
					//
					break;
				}
			}
			
			// Save the users default view to array.
			Details = Arrays.copyOf(Details, Details.length + 1);
			Details[Details.length - 1] = new String[] {"DEFAULT_CUSTOM_VIEW", UsersDefaultView};
		}
	
		return Details;
	}
}