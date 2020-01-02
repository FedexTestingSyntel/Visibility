package USRC;

import java.util.ArrayList;
import java.util.List;
import org.apache.http.Header;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONObject;
import API_Functions.General_API_Calls;
import SupportClasses.Helper_Functions;
import SupportClasses.WebDriver_Functions;

public class login {

	 //Takes in the userid, password, and the level and returns the cookies generated.
  	//{fdx_=[cookie], uuid, full_cookies};  0 is the cookie, 1 is the uuid, 2 are all the cookies     ex [fdx_login=ssodrt-cos2.97fb.364ddc40, gw0g0h657p]
  	public static String[] Login(String UserID, String Password){
  		try{
  			USRC_Data USRC_Details = USRC_Data.USRC_Load();
  			HttpClient httpclient = HttpClients.createDefault();
  			
  			HttpPost httppost = new HttpPost(USRC_Details.GenericUSRCURL);

  			JSONObject processingParameters = new JSONObject()
  				.put("anonymousTransaction", true)
  				.put("clientId", "WCON")  //WCDO
  				.put("clientVersion", "3")
  				.put("returnDetailedErrors", false)
  				.put("returnLocalizedDateTime", false);
  				
  			JSONObject LogInRequest = new JSONObject()
  				.put("processingParameters", processingParameters)
  				.put("userName", UserID)
  				.put("password", Password);
  				
  			JSONObject main = new JSONObject()
  				.put("LogInRequest", LogInRequest);
  		
  			String json = main.toString();
  				
  			httppost.addHeader("Content-Type", "application/x-www-form-urlencoded");
  			httppost.addHeader("Accept", "application/json");
  			/*httppost.addHeader("Content-Type", "application/json");*/
  			httppost.addHeader("Content-Type", "charset=UTF-8");
  			

  			List<NameValuePair> urlParameters = new ArrayList<NameValuePair>();
  			urlParameters.add(new BasicNameValuePair("action", "LogIn"));
  			urlParameters.add(new BasicNameValuePair("format", "json"));
  			urlParameters.add(new BasicNameValuePair("version", "1"));
  			urlParameters.add(new BasicNameValuePair("locale", "en_US"));
  			urlParameters.add(new BasicNameValuePair("data", json));

  			httppost.setEntity(new UrlEncodedFormEntity(urlParameters));
  			// Helper_Functions.PrintOut("Get cookie from USRC for " + UserID + "/" + Password, true);
  			HttpResponse Response = httpclient.execute(httppost);
  			// Helper_Functions.PrintOut("response: " + EntityUtils.toString(Response.getEntity()), true);
  			Header[] Headers = Response.getAllHeaders();
  			//takes apart the headers of the response and returns the fdx_login cookie if present
  			String fdx_login = null, fcl_uuid = null, full_cookies = "", RequestHeaders = "";
  			for (Header Header : Headers) {
  				//String Test = header.getName() + "    " + header.getValue();PrintOut(Test, false);// used in debugging or if want to see other cookie values
  				if (Header.getName().contentEquals("Set-Cookie") && Header.getValue().contains("fdx_login")) {
  					fdx_login = Header.toString().replace("Set-Cookie: ", "").replace("; domain=.fedex.com; path=/; secure", "");
  				}else if (Header.getName().contentEquals("Set-Cookie") && Header.getValue().contains("fcl_uuid")) {
  					fcl_uuid = Header.toString().replace("Set-Cookie: fcl_uuid=", "").replace("; domain=.fedex.com; path=/; secure", "");
  				}
  				if (Header.getName().contentEquals("Set-Cookie")) {
  					full_cookies += Header.toString(); 
  				}
  				RequestHeaders += Header + "___";
  			}
  			
  			String MethodName = "USRCLogin";
  			General_API_Calls.Print_Out_API_Call(MethodName, httppost.toString(), RequestHeaders, json, Response.toString());
					  
  			if (fdx_login == null) {
  				throw new Exception("Unable to login");
  			}
  			String HeaderArray[] = new String[] {fdx_login, fcl_uuid, full_cookies};
  			return HeaderArray;
  		}catch (Exception e){
  			System.err.println("USRC.login() - Unable to login - " + UserID + " / " + Password);
  			// e.printStackTrace();
  			return null;
  		}
  	}
	
	// This is used to set the cookies of the browser after loggin in from an API call to USRC
	public static String Login_API_Load_Cookies(String UserID, String Password){
		String FullCookies;
  		try{
  			String loginAttempt[] = Login(UserID, Password);
  			if (loginAttempt != null && loginAttempt[2] != null && loginAttempt[2].contains("fdx_login")) {
  				FullCookies = loginAttempt[2].replace("secureSet-Cookie: ", "");
  				FullCookies = FullCookies.replace("Set-Cookie: ", "");
  				FullCookies = FullCookies.replaceAll("; ", ";");
  				while(FullCookies.contains(";")) {
  					for (int charpos = 0; charpos < FullCookies.length(); charpos++) {
  						if (FullCookies.charAt(charpos) == ';') {
  							String CookieName = FullCookies.substring(0, FullCookies.indexOf('='));
  							String CookieValue = FullCookies.substring(FullCookies.indexOf('=') + 1, charpos);
  							WebDriver_Functions.SetCookieValue(CookieName, CookieValue);
  							FullCookies = FullCookies.substring(charpos + 1, FullCookies.length());
  							break;
  						}
  					}
  				}
  				return loginAttempt[2];
  			} else {
  				throw new Exception("Not able to login with " + UserID + " - " + Password);
  			}
  		}catch (Exception e){
  			e.printStackTrace();
  			return null;
  		}
  	}
	
}
