package INET_Application;

import java.util.ArrayList;

import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.json.JSONObject;

import API_Functions.General_API_Calls;
import SupportClasses.Environment;
import SupportClasses.Helper_Functions;

public class GroundCorpLoad {
	
	//ArrayList<String> Tracking = new ArrayList<String>();
	public static boolean ValidateAndProcess(ArrayList<String> TrackingIds, ArrayList<String> testID){
		try{
			String URL = "http://corploadlvsvc.test.cloud.fedex.com/FINAL_FILE/ValidateAndProcess";
			HttpPost httppost = new HttpPost(URL);
			
			httppost.addHeader("Content-Type", "application/json");
			
			// TODO: come back and redo this, lazy
			String trackIds ="";
			String testIds ="";
			for(String ID: TrackingIds) {
				if (trackIds.contentEquals("")) {
					trackIds = ID;
				}else {
					trackIds += "\n" + ID;
				}
			}
			for(String ID: testID) {
				if (testIds.contentEquals("")) {
					testIds = ID;
				}else {
					testIds += "\n" + ID;
				}
			}
			
			String Level = Environment.getInstance().getLevel();
			
			JSONObject Main = new JSONObject()
			        .put("environment", "L" + Level)
			        .put("allowFlag", "Y")
			        .put("referenceId", testIds)
			        .put("trackIds", trackIds)
			        .put("delCons", "")
			        .put("testIds", trackIds)
			        .put("choice", "FINALTRKS");
			
  			String Request = Main.toString();

  			httppost.addHeader("Content-Type", "application/json");
			StringEntity params = new StringEntity(Request.toString());
			httppost.setEntity(params);
  			
			String Response = General_API_Calls.HTTPCall(httppost, Request);
		
			if (Response.contains("\"valid\":1")) {
				return true;
			} else {
				Helper_Functions.PrintOut("Not accepted to final file.");
				return false;
			}
		}catch (Exception e){
			e.printStackTrace();
			return false;
		}
			/*
			 URL: POST http://corploadsvc.test.cloud.fedex.com/FINAL_FILE/ValidateAndProcess HTTP/1.1
     		Headers: Content-Type: application/json___Content-Type: application/json___
     		Request: {"testIds":"794948925776","environment":"L3","allowFlag":"Y","trackIds":"794948925776","delCons":"","choice":"FINALTRKS","referenceId":"mcarnkwkhqkt"}
     		Response: {"entered":1,"duplicates":0,"errors":0,"valid":1,"uniqueId":"F42175529","finalFile":true,"message":"","random":"F511774"}
			*/
	}

}