package TRKC;

import java.util.ArrayList;
import java.util.List;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONObject;
import org.testng.annotations.Test;

import API_Functions.General_API_Calls;

public class get_spod_image {

	public static String GetImageRequest(String Cookie, String trackingNumber, String trackingQualifier, String trackingCarrier){
  		try{
  			/*TRKC_Data TRKC_Details = TRKC_Data.LoadVariables();
  			HttpPost httppost = new HttpPost(TRKC_Details.TrackingGenericURL);*/
  			
  			HttpPost httppost = new HttpPost("https://www.fedex.com/trackingCal/track");

  			JSONObject processingParameters = new JSONObject();
  			
  			JSONObject trackNumberInfoEle = new JSONObject()
  					.put("trackingNumber", trackingNumber)
  	  				.put("trackingQualifier", trackingQualifier)
  	  				.put("trackingCarrier", trackingCarrier);
  			
  			JSONObject trackNumberInfo = new JSONObject()
  					.put("trackNumberInfo", trackNumberInfoEle);
  			
  			Object trackNumberInfoArray[] = new Object[] {trackNumberInfo};
  			
  			JSONObject GetImageRequest = new JSONObject()
  	  				.put("appType", "WTRK")
  	  				.put("appDeviceType", "DESKTOP")
  	  				.put("uniqueKey", "")
  	  				.put("processingParameters", processingParameters)
  	  				.put("trackingInfoList", trackNumberInfoArray);
  			
  			JSONObject main = new JSONObject()
  	  				.put("GetImageRequest", GetImageRequest);

  			String json = main.toString();
  				
  			httppost.addHeader("Content-Type", "application/x-www-form-urlencoded");
  			httppost.addHeader("Content-Type", "charset=UTF-8");
  			httppost.addHeader("Cookie", Cookie);
  			
  			List<NameValuePair> urlParameters = new ArrayList<NameValuePair>();
  			urlParameters.add(new BasicNameValuePair("action", "getSpodImage"));
  			urlParameters.add(new BasicNameValuePair("format", "json"));
  			urlParameters.add(new BasicNameValuePair("version", "1"));
  			urlParameters.add(new BasicNameValuePair("locale", "en_US"));
  			urlParameters.add(new BasicNameValuePair("data", json));

  			httppost.setEntity(new UrlEncodedFormEntity(urlParameters));
  			String Response = General_API_Calls.HTTPCall(httppost, json);	
  			
  			return Response;
  		}catch (Exception e){
  			//e.printStackTrace();
  			return null;
  		}
  	}
	
	@Test
	void testcall() {
		
		this.GetImageRequest("", "390650083060", "2458905000~390650083060~FX", "FDXE");
	}
	
}
