package INET_Application;

import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.json.JSONObject;

import SupportClasses.Environment;
import SupportClasses.General_API_Calls;
import SupportClasses.Helper_Functions;

public class GroundCorpLoad {

	public static boolean AddGroundTracking(String TrackingIds[]) {
		String Response = ValidateAndProcess(TrackingIds);
		if (Response.contains("\"valid\":" + TrackingIds.length)) {
			return true;
		}
		return false;
	}
	
	public static String ValidateAndProcess(String TrackingIds[]){
		try{
			String URL = "http://corploadsvc.test.cloud.fedex.com/FINAL_FILE/ValidateAndProcess";
			HttpPost httppost = new HttpPost(URL);
			
			httppost.addHeader("Content-Type", "application/json");
			
			String trackIds ="";
			for(String ID: TrackingIds) {
				if (trackIds.contentEquals("")) {
					trackIds = ID;
				}else {
					trackIds += "\n" + ID;
				}
			}
			
			String Level = Environment.getInstance().getLevel();
			
			JSONObject Main = new JSONObject()
			        .put("environment", "L" + Level)
			        .put("allowFlag", "Y")
			        .put("referenceId", Helper_Functions.getRandomString(12))
			        .put("trackIds", trackIds)
			        .put("delCons", "")
			        .put("testIds", trackIds)
			        .put("choice", "FINALTRKS");
			
  			String Request = Main.toString();

  			httppost.addHeader("Content-Type", "application/json");
			StringEntity params = new StringEntity(Request.toString());
			httppost.setEntity(params);
  			
			String Response = General_API_Calls.HTTPCall(httppost, Request);
		
			return Response;
		}catch (Exception e){
			e.printStackTrace();
			return e.toString();
		}
			/*
			Sample url:    
			.....
			*/
	}

}