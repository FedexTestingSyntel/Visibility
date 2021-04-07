package API_Functions;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.Arrays;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.net.ssl.HttpsURLConnection;
import org.apache.http.Header;
import org.apache.http.HttpRequest;
import org.apache.http.HttpResponse;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.util.EntityUtils;
import SupportClasses.Environment;
import SupportClasses.Helper_Functions;

public class General_API_Calls {
	// flag will determine if the url, request, and response will be printed to the console.
	private static boolean PrintOutAPICall = true;
	private static boolean PrintOutFullResponse = false;
	private static int HTTPCallTimeout = 15; // in seconds
	
	private final static Lock lock = new ReentrantLock();//to make sure the httpclient works with the parallel execution
	// private static HttpClient httpclient = HttpClients.createDefault();//made static to speed up socket execution

	// Have one (or more) threads ready to do the async tasks. Do this during startup of your app.
	static ExecutorService executor = Executors.newFixedThreadPool(4); 
	
	public static String getAuthToken(String Client_Iden, String Client_Sec) {
		String CallingMethod = Thread.currentThread().getStackTrace()[2].getMethodName();
		String CallingIdentifier = "L" + Environment.getInstance().getLevel() + " " + CallingMethod;
		//ex https://apidev.idev.fedex.com/auth/oauth/v2/token 
		String URL = General_API_Calls.getAPILevelIedntifier(Environment.getInstance().getLevel(), false) + "/auth/oauth/v2/token";
		String response = "";
		try {
			String queryParam = "grant_type=client_credentials" + "&client_id=" + Client_Iden + "&client_secret=" + Client_Sec + "&scope=oob";
			String method = "POST";
			URL serviceUrl = new URL(URL);

			HttpsURLConnection con = (HttpsURLConnection) serviceUrl.openConnection();
			con.setRequestMethod(method);
			
			String urlParameters = queryParam;
			StringBuffer response_buff = new StringBuffer();
			con.setDoOutput(true);
			DataOutputStream wr = new DataOutputStream(con.getOutputStream());
			wr.writeBytes(urlParameters);
			wr.flush();
			wr.close();
			BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
			String inputLine;
			while ((inputLine = in.readLine()) != null) {
				response_buff.append(inputLine);
			}
			in.close();
			response = response_buff.toString();
			
			if (response.contains("access_token")) {
				//{  "access_token":"ddea4340-d1e1-46bf-94b0-1271c49aa1a0",  "token_type":"Bearer",  "expires_in":3600,  "scope":"oob"}
				String token = API_Functions.General_API_Calls.ParseStringValue(response, "access_token");	
				Helper_Functions.PrintOut(CallingIdentifier + " BearerToken: " + token);
				return token;
			}
		} catch (Exception e) {
			Helper_Functions.PrintOut("Unable to generate BearerToken -- " + CallingIdentifier);
			e.printStackTrace();
		}
		return null;
	}//end getAuthToken
	
	public static String HTTPCall(HttpRequest Request, String Request_Body) throws Exception {
		String RequestHeaders = "", Response = "";

		lock.lock();
		RequestConfig config = RequestConfig.custom()
		  .setConnectTimeout(HTTPCallTimeout * 1000)
		  .setConnectionRequestTimeout(HTTPCallTimeout * 1000)
		  .setSocketTimeout(HTTPCallTimeout * 1000).build();
		
		CloseableHttpClient client = HttpClientBuilder.create().setDefaultRequestConfig(config).build();
		
		String MethodName = Thread.currentThread().getStackTrace()[2].getMethodName();
		try {
			Header[] headers = Request.getAllHeaders();
  			//takes apart the headers of the request and print them separated by the underscore.
  			for (Header header : headers) {
  				RequestHeaders += header + "___";
  			}
			
  			HttpResponse httpresponse = client.execute((HttpUriRequest) Request);
			Response = EntityUtils.toString(httpresponse.getEntity());
			Response = RemoveUnicode(Response);
			return Response;
		}catch (Exception e) {
			Response = e.getMessage() + e.getCause();
			return e.getMessage() + e.getCause();
		}finally {
			String Response_to_Print = Response.replaceAll("\n", "").replaceAll("\r", "");
			
			// String action = Request.getParams().toString();
			Print_Out_API_Call(MethodName, Request.toString(), RequestHeaders, Request_Body, Response_to_Print);
			lock.unlock();
		} 
	}
	
	public static void Print_Out_API_Call(String MethodName, String URL, String RequestHeaders, String Request_Body, String Response) {
		if (PrintOutAPICall) {
			// used to save space when writing to console.
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
	
	//remove all the unicode characters and will make them the corresponding special characters.
	public static String RemoveUnicode(String data) {
		Pattern p = Pattern.compile("\\\\u(\\p{XDigit}{4})");
		Matcher m = p.matcher(data);
		StringBuffer buf = new StringBuffer(data.length());
		while (m.find()) {
		  String ch = String.valueOf((char) Integer.parseInt(m.group(1), 16));
		  m.appendReplacement(buf, Matcher.quoteReplacement(ch));
		}
		m.appendTail(buf);
		return buf.toString();
	}
	
	public static String ParseStringValue(String Main, String Parameter) {
		if(Main != null && Parameter != null && Main.contains(Parameter)) {
			// find the parameter and return what the value is.
			// send Foo, will turn into "Foo":". Then will return value such as "Foo":"value"
			Parameter = "\"" + Parameter + "\":";
			if( Main.contains(Parameter) ) {
				Main = Main.substring(Main.indexOf(Parameter) + Parameter.length(), Main.length());
				if (Main.charAt(0) == '"') {
					Main = Main.substring(1, Main.length());
				}
				int curlyBraces = 0;
				int squareBraces = 0;
				for(int i = 0 ; i < Main.length(); i++) {
					char letter = Main.charAt(i);
					if (letter == '[') {
						squareBraces++;
					}else if (letter == ']') {
						squareBraces--;
					}else if (letter == '{') {
						curlyBraces++;
					}else if (letter == '}') {
						curlyBraces--;
					}
					
					boolean Curly = (curlyBraces <= 0), Square = (squareBraces <= 0);
					if (Curly && Square) {
						if ((letter == ']' || letter == '}') && curlyBraces == 0 && squareBraces == 0) {
							return Main.substring(0, i + 1);
						}else if (letter == '"' || letter == ']' || letter == '}') {
							if (i > 0 && Main.charAt(i - 1) == ',') {
								i--;
							}
							return Main.substring(0, i);
						}
					}
				}
			}
		}
		return null;
	}
	
	public static String ParseFirstArrayValue(String Main) {
		if (Main != null) {
			if (Main.charAt(0) == '[') {
				Main = Main.substring(1, Main.length());
			}
			// find the parameter and return what the value is.
			// send Foo, will turn into "Foo":". Then will return value such as "Foo":"value"
			int curlyBraces = 0;
			int squareBraces = 0;
			for (int i = 0 ; i < Main.length(); i++) {
				char letter = Main.charAt(i);
				if (letter == '[') {
					squareBraces++;
				} else if (letter == ']') {
					squareBraces--;
				} else if (letter == '{') {
					curlyBraces++;
				} else if (letter == '}') {
					curlyBraces--;
				}
					
				boolean Curly = (curlyBraces <= 0), Square = (squareBraces <= 0);
				if (Curly && Square) {
					if ((letter == ']' || letter == '}') && curlyBraces == 0 && squareBraces == 0) {
						return Main.substring(0, i + 1);
					} else if (letter == '"' || letter == ']' || letter == '}') {
						if (i > 0 && Main.charAt(i - 1) == ',') {
							i--;
						}
						String response = Main.substring(0, i);
						if (response.contentEquals("")) {
							return null;
						}
						return response;
					}
				}
			}
		}
		return null;
	}

	public static void setPrintOutAPICallFlag(boolean flag) {
		PrintOutAPICall = flag;
	}
	
	public static void setPrintOutFullResponseFlag(boolean flag) {
		PrintOutFullResponse = flag;
	}
	
	public static void setHTTPCallTimeout(int secondsValue) {
		HTTPCallTimeout = secondsValue;
	}
	
	public static String[][] copyToTwoDimArray(String BaseArray[][], String Addition[]) {
		String TwoDAddition[][] = new String[1][];
		TwoDAddition[0] = Addition;
		return copyToTwoDimArray(BaseArray, TwoDAddition);
	}
	
	public static String[][] copyToTwoDimArray(String BaseArray[][], String Addition[][]) {
		for (String Add[]: Addition) {
			BaseArray = Arrays.copyOf(BaseArray, BaseArray.length + 1);
			BaseArray[BaseArray.length - 1] = Add;
		}
		return BaseArray;
	}
	
	public static String getAPILevelIedntifier (String Level, boolean genericURLFlag) {
		String LevelIdentifier = null;
		if (genericURLFlag) {
	  		switch (Level) {
	  		case "1":
	  			LevelIdentifier = "NotSetForLevelOne";
	  		case "2":
	  			LevelIdentifier = "https://wwwdev.idev.fedex.com";
	  			break;
	  		case "3":
	  			LevelIdentifier = "https://wwwdrt.idev.fedex.com";
	  			break;
	  		case "4":
	  			LevelIdentifier = "https://wwwstress.dmz.idev.fedex.com";
	  			break;
	  		case "5":
	  			LevelIdentifier = "https://wwwbit.idev.fedex.com";
	  			break;
	  		case "6":
	  			LevelIdentifier = "https://wwwtest.fedex.com";
	  			break;
	  		case "7":
	  			LevelIdentifier = "https://www.fedex.com";
	  			break;
	  		}
		} else {
	  		switch (Level) {
	  		case "1":
	  			LevelIdentifier = "NotSetForLevelOne";
	  		case "2":
	  			LevelIdentifier = "https://apidev.idev.fedex.com";
	  			break;
	  		case "3":
	  			LevelIdentifier = "https://apidrt.idev.fedex.com";
	  			break;
	  		case "4":
	  			LevelIdentifier = "https://apistress.idev.fedex.com";
	  			break;
	  		case "5":
	  			LevelIdentifier = "https://apibit.fedex.com";
	  			break;
	  		case "6":
	  			LevelIdentifier = "https://apitest.fedex.com";
	  			break;
	  		case "7":
	  			LevelIdentifier = "https://api.fedex.com";
	  			break;
	  		}
		}

  		return LevelIdentifier;
	}


}




