package API_Functions;

import com.google.api.client.http.GenericUrl;
import com.google.api.client.http.HttpBackOffUnsuccessfulResponseHandler;
import com.google.api.client.http.HttpContent;
import com.google.api.client.http.HttpRequest;
import com.google.api.client.http.HttpRequestFactory;
import com.google.api.client.http.HttpResponse;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.JsonObjectParser;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.client.util.ExponentialBackOff;
import com.google.gson.reflect.TypeToken;
import java.lang.reflect.Type;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import SupportClasses.Environment;
import SupportClasses.Helper_Functions;

public class General_API_Calls_Concurrent {
	// flag will determine if the url, request, and response will be printed to the console.
	private static boolean PrintOutAPICall = true;
	private static boolean PrintOutFullResponse = false;
	
	// Have one (or more) threads ready to do the async tasks. Do this during startup of your app.
	static ExecutorService executor = Executors.newFixedThreadPool(4); 
	
	public static String HTTPCall(String a, GenericUrl b, HttpContent c) throws Exception {
		String rawResponse = null;
		
		HttpRequestFactory requestFactory= new NetHttpTransport().createRequestFactory();
        HttpRequest request = requestFactory.buildRequest(a, b, c);
        rawResponse = request.execute().parseAsString();
		
        /*if ("GET".equals(Request.getRequestMethod())){
			
			HttpRequestFactory requestFactory= new NetHttpTransport().createRequestFactory();
	        HttpRequest request = requestFactory.buildGetRequest(
	        		new GenericUrl(Request.getUrl().toString()));

	        
		} else if ("POST".equals(Request.getRequestMethod())){
			HttpRequestFactory requestFactory= new NetHttpTransport().createRequestFactory();
	        HttpRequest request = requestFactory.buildGetRequest(
	        		new GenericUrl("https://www.fedex.com/adminCal/v1/users/roleandstatus"));
	        rawResponse = request.execute().parseAsString();
		}
		*/

		System.out.println(rawResponse);
        return rawResponse;
	}
	
	public static void Print_Out_API_Call(String MethodName, String URL, String RequestHeaders, String Request_Body, String Response) {
		if (PrintOutAPICall) {
			if (Response != null && Response.length() > 600 && !PrintOutFullResponse) {
				int length = Response.length();
				Response = Response.substring(0, 600) + "... (Response full length was " + length + ", Print_Out_API_Call() )";
			}
		
			Helper_Functions.PrintOut(MethodName + " URL: " + URL + "\n    " + 
				  MethodName + " Headers: " + RequestHeaders + "\n    " +
				  MethodName + " Request: " + Request_Body + "\n    " + 
				  MethodName + " Response: " + Response, true); 
		}
	}

	//This will return only first value that matches
	public static String Parse_API_For_Value(String MainValue, String Substring) {
		try {
			if (MainValue != null && MainValue.contains(Substring)) {
				//remove everything before the substring that is needed
				MainValue = MainValue.substring(MainValue.indexOf(Substring) + Substring.length() + 3, MainValue.length());
				String ReturnValue = MainValue.substring(0, MainValue.indexOf("\""));
				return ReturnValue;
			}
		}catch (Exception e){
			System.err.println("Error in General_API_Calls.Parse_API_For_Value " + e.getMessage());
		}
		return "NOTKNOWN";
	}
	

	public static void setPrintOutAPICallFlag(boolean flag) {
		PrintOutAPICall = flag;
	}
	
	public static void setPrintOutFullResponseFlag(boolean flag) {
		PrintOutFullResponse = flag;
	}
}




