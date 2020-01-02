package INET_Application;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Method;
import java.net.URL;
import java.text.Format;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import javax.net.ssl.HttpsURLConnection;

import org.apache.http.Header;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import org.openqa.selenium.By;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Listeners;
import org.testng.annotations.Test;

import API_Functions.General_API_Calls;
import Data_Structures.Account_Data;
import Data_Structures.Address_Data;
import Data_Structures.Shipment_Data;
import SupportClasses.Environment;
import SupportClasses.Helper_Functions;
import SupportClasses.WebDriver_Functions;

@Listeners(SupportClasses.TestNG_TestListener.class)

public class eMASS_Scans {

	static String Credentials = "832614";
	public static String LocationCode = "NQAA";
	static String Scanning_Info_FedEx_Id = "703233";
	static String Scanning_Info_FedEx_Route = "BSC";
	static String CosmosNumber = "12";
	static String Form_ID = "201"; //default for US to US 
	
	static String Level = "3";
	static String MT = "massEntryForm:pup_repeatdatatable:0";
	
	@BeforeClass
	public void beforeClass() {
		Environment.SetLevelsToTest(Level);
	}
	
	@DataProvider //(parallel = true)
	public static Iterator<Object[]> dp(Method m) {
		List<Object[]> data = new ArrayList<Object[]>();
		
		for (int i=0; i < Environment.LevelsToTest.length(); i++) {
			String Level = String.valueOf(Environment.LevelsToTest.charAt(i));
			//int intLevel = Integer.parseInt(Level);
			Shipment_Data Shipment_Info_Array[] = Data_Structures.Shipment_Data.getTrackingDetails(Level);
			//Based on the method that is being called the array list will be populated
			switch (m.getName()) {
		    	case "eMASSTest":
		    		for (Shipment_Data Shipment_Info: Shipment_Info_Array) {
		    			if (Shipment_Info.Status.contentEquals("In transit") ||
		    					Shipment_Info.Status.contentEquals("Label created")) {
		    				Account_Data Account_Info = Environment.getAddressDetails(Level, "US");
		    				Shipment_Info.setDestination_Address_Info(Account_Info.Billing_Address_Info);
		    				data.add(new Object[] {Level, Shipment_Info});
		    			}
		    		}
		    	break;
		    	case "eMASSFullUpdate":
		    		String TrackingNumbers[] = new String[0];
		    		Shipment_Data Example_Shipment = new Shipment_Data();;
    				Account_Data Account_Info = Environment.getAddressDetails(Level, "US");
    				Example_Shipment.setDestination_Address_Info(Account_Info.Billing_Address_Info);
    				
		    		for (Shipment_Data Shipment_Info: Shipment_Info_Array) {
		    			if (Shipment_Info.Status.contentEquals("In transit") ||
		    					Shipment_Info.Status.contentEquals("Label created")) {
		    				// increment array by one and add tracking number
		    				TrackingNumbers = Arrays.copyOf(TrackingNumbers, TrackingNumbers.length + 1);
		    				TrackingNumbers[TrackingNumbers.length - 1] = Shipment_Info.Tracking_Number;
		    				if (TrackingNumbers.length == 10) {
		    					data.add(new Object[] {Level, Example_Shipment, TrackingNumbers});
		    					TrackingNumbers = new String[0];
		    				}
		    			}
		    		}
		    		
		    		// if at least one update is needed
		    		if (TrackingNumbers != null) {
		    			data.add(new Object[] {Level, Example_Shipment, TrackingNumbers});
		    		}
		    	break;
			}
		}
		return data.iterator();
	}
	
	@Test (dataProvider = "dp", enabled = false)
	public static void eMASSTest(String Level, Shipment_Data Shipment_Info){
		try {
			eMASS_Scans.eMASS_Pickup_Scan(Shipment_Info);
		}catch (Exception e) {
			e.printStackTrace();
			Assert.fail(e.getCause().toString());
		}
	}
	
	@Test (dataProvider = "dp", enabled = true)
	public static void eMASSFullUpdate(String Level, Shipment_Data Shipment_Info, String TrackingNumbers[]){
		try {
			eMASS_Scans.eMASS_Multiple_Pickup_Scan(Shipment_Info, TrackingNumbers);
		}catch (Exception e) {
			e.printStackTrace();
			Assert.fail(e.getCause().toString());
		}
	}
	
	@Test (enabled = false)
	public static void eMASSAPITest(){
		try {
			Shipment_Data Shipment_Info = new Shipment_Data();
			Address_Data Address_Info = Address_Data.getAddress(Level, "US", null);
			Shipment_Info.Origin_Address_Info = Address_Info;
			Address_Data Address_Info2 = Address_Data.getAddress(Level, "CA", null);
			Shipment_Info.Destination_Address_Info = Address_Info2;
			
			Shipment_Info.Tracking_Number = "794816293452";
			Shipment_Info.Origin_Address_Info.Country_Code = "US";
			Shipment_Info.Origin_Address_Info.PostalCode = "38119";
			Environment.getInstance().setLevel("2");
			
			eMASS_API(Shipment_Info);
		}catch (Exception e) {
			e.printStackTrace();
			Assert.fail(e.getCause().toString());
		}
	}
	
	public static boolean eMASS_Pickup_Scan(Shipment_Data Shipment_Info) throws Exception{
		try {
			
			eMASS_Navigate_And_Login(1);
			
			eMASS_Scanning_Info();

			// Enter tracking number
			WebDriver_Functions.Type(By.id(MT + ":trkNo_inputtext"), Shipment_Info.Tracking_Number);
			
			// Enter from id
			WebDriver_Functions.Type(By.id(MT + ":formCd_inputtext"), Form_ID);
			
			// Enter Cosmos ID, random 2 digit
			if (WebDriver_Functions.isEnabled(By.id(MT + ":cosmosNbr_inputtext"))) {
				WebDriver_Functions.Type(By.id(MT + ":cosmosNbr_inputtext"), CosmosNumber);
			}
			
			// if the destination address was not passed then load a dummy value for the pickup scan.
			if (Shipment_Info.Destination_Address_Info == null || 
					Shipment_Info.Destination_Address_Info.City.contentEquals("")) {
				Account_Data Account_Info = Environment.getAddressDetails(Level, "US");
				Shipment_Info.setDestination_Address_Info(Account_Info.Billing_Address_Info);
			}
			
			int loopPickupDestination = 0;
			// Due to EMASS glitch     Enter first digit of city, country, and postal again
			while (loopPickupDestination < 2) {
				// Enter first digit of city
				WebDriver_Functions.Type(By.id(MT + ":destCityCd_inputtext"), Shipment_Info.Destination_Address_Info.City.substring(0, 1).toUpperCase());
					
				// Enter country
				WebDriver_Functions.Type(By.id(MT + ":destCountryCd_inputtext"), Shipment_Info.Destination_Address_Info.Country_Code);
					
				// Enter destination postal code
				WebDriver_Functions.Type(By.id(MT + ":destZipCd_inputtext"), Shipment_Info.Destination_Address_Info.PostalCode);
					
				// Click base service
				WebDriver_Functions.Click(By.id(MT + ":baseSvc_selectonemenu"));
				
				WaitForInProgressOverlay();
				
				loopPickupDestination++;
			}
		
			// Click base service  
			WebDriver_Functions.WaitForTextPresentIn(By.id(MT + ":baseSvc_selectonemenu"), "Express Saver");
			WaitForInProgressOverlay();
			// (20) Express Saver
			WebDriver_Functions.Select(By.id(MT + ":baseSvc_selectonemenu"), "20", "v");
			// come back and fix this so the correct service selected
			//WebDriver_Functions.Select(By.id(MT + ":baseSvc_selectonemenu"), Shipment_Info.Service, "t");
			
			// Package type (01) Customer Packaging
			WaitForInProgressOverlay();
			WebDriver_Functions.Select(By.id(MT + ":pkgType_selectonemenu"), "01", "v");

			// Delivery
			WebDriver_Functions.Click(By.xpath("//*[@id='massEntryForm:pup_repeatdatatable:0:handlingCd_selectmanymenu']/option[2]"));
			//WebDriver_Functions.Select(By.id(MT + ":handlingCd_selectmanymenu"), "02", "v");
			
			// Click submit
			WaitForInProgressOverlay();
			WebDriver_Functions.Click(By.id("massEntryForm:pup_savebutton"));
			
			// Click yes on confirmations.
			WaitForInProgressOverlay();
			WebDriver_Functions.Click(By.id("massEntryForm:save_confirmation_yesbutton"));
			
			// The below should be the final confirmation
			WaitForInProgressOverlay();
			WebDriver_Functions.Click(By.id("massEntryForm:alertDialog_okbutton"));
			
			return true;
		}catch (Exception e){
			throw e;
		}
	}
	
	public static boolean eMASS_Multiple_Pickup_Scan(Shipment_Data Shipment_Info, String TrackingNumbers[]) throws Exception{
		try {
			
			eMASS_Navigate_And_Login(1);
			
			eMASS_Scanning_Info();
			
			// Add rows based on the number of tracking numbers. If only single tracking number will not add any rows
			WebDriver_Functions.Select(By.id("massEntryForm:pup_repeatinputpanel_rowCount_selectonemenu"), String.valueOf(TrackingNumbers.length - 1), "v");
			WaitForInProgressOverlay();
			
			int TableRow = -1;
			
			for (String TrackNbr:TrackingNumbers) {
				TableRow++;
				String TblRow = "massEntryForm:pup_repeatdatatable:" + TableRow;
				Shipment_Info.Tracking_Number = TrackNbr;
				
				// Enter tracking number
				TypeAndCheck(By.id(TblRow + ":trkNo_inputtext"), Shipment_Info.Tracking_Number);
				
				// Issue in GUI where form id is cleared out when entering zip, need to enter after address details.
				
				// Enter Cosmos ID, random 2 digit
				if (WebDriver_Functions.isEnabled(By.id(TblRow + ":cosmosNbr_inputtext"))) {
					TypeAndCheck(By.id(TblRow + ":cosmosNbr_inputtext"), CosmosNumber);
				}
				

				// Enter first digit of city
				String city = Shipment_Info.Destination_Address_Info.City.substring(0, 1).toUpperCase();
				TypeAndCheck(By.id(TblRow + ":destCityCd_inputtext"), city);
				
				// Enter destination postal code
				String zip = Shipment_Info.Destination_Address_Info.PostalCode;
				TypeAndCheck(By.id(TblRow + ":destZipCd_inputtext"), zip);
				
				// Enter country
				String country = Shipment_Info.Destination_Address_Info.Country_Code;
				TypeAndCheck(By.id(TblRow + ":destCountryCd_inputtext"), country);

				// Enter form id
				TypeAndCheck(By.id(TblRow + ":formCd_inputtext"), Form_ID);
					
				// Click city just to move focus and get page to load.
				WebDriver_Functions.Click(By.id(TblRow + ":destCityCd_inputtext"));
				WaitForInProgressOverlay();

			
				// Click base service  
				WebDriver_Functions.WaitForTextPresentIn(By.id(TblRow + ":baseSvc_selectonemenu"), "Express Saver");
				WaitForInProgressOverlay();
				
				// (20) Express Saver
				WebDriver_Functions.Select(By.id(TblRow + ":baseSvc_selectonemenu"), "20", "v");
				WaitForInProgressOverlay();
				
				// Package type (01) Customer Packaging
				WebDriver_Functions.Select(By.id(TblRow + ":pkgType_selectonemenu"), "01", "v");

				// Delivery
				WebDriver_Functions.Click(By.xpath("//*[@id='" + TblRow + ":handlingCd_selectmanymenu']/option[2]"));
				
				// This is needed as the GUI will clear the data that was just entered. Should not be needed but bad guid
				if (WebDriver_Functions.isEnabled(By.id(TblRow + ":cosmosNbr_inputtext"))) {
					TypeAndCheck(By.id(TblRow + ":cosmosNbr_inputtext"), CosmosNumber);
				}
				TypeAndCheck(By.id(TblRow + ":destCityCd_inputtext"), city);
				TypeAndCheck(By.id(TblRow + ":destZipCd_inputtext"), zip);
				TypeAndCheck(By.id(TblRow + ":destCountryCd_inputtext"), country);
				TypeAndCheck(By.id(TblRow + ":formCd_inputtext"), Form_ID);
				// End of redundent check
			}
			
			// Click submit
			WaitForInProgressOverlay();
			WebDriver_Functions.Click(By.id("massEntryForm:pup_savebutton"));
			
			// Click yes on confirmations.
			WaitForInProgressOverlay();
			WebDriver_Functions.Click(By.id("massEntryForm:save_confirmation_yesbutton"));
			
			// The below should be the final confirmation
			WaitForInProgressOverlay();
			WebDriver_Functions.Click(By.id("massEntryForm:alertDialog_okbutton"));
			
			return true;
		}catch (Exception e){
			throw e;
		}
	}
	
	public static boolean eMASS_Exception_Scan(Shipment_Data Shipment_Info) throws Exception{
		try {
			
			eMASS_Navigate_And_Login(2);

			// Enter the scanning info
			WaitForInProgressOverlay();
			WebDriver_Functions.Type(By.id("massEntryForm:empNbr_inputtext"), Scanning_Info_FedEx_Id);
			WebDriver_Functions.Click(By.id("massEntryForm:route_inputtext"));
			WaitForInProgressOverlay();
			WebDriver_Functions.Type(By.id("massEntryForm:route_inputtext"), Scanning_Info_FedEx_Route);
			WebDriver_Functions.Click(By.id(MT + ":trkNo_inputtext"));
			WaitForInProgressOverlay();
			
			// Enter tracking number
			WebDriver_Functions.Type(By.id("massEntryForm:dex03_repeatdatatable:0:multiTrkNoEntry_textarea_input"), Shipment_Info.Tracking_Number);
			
			// Enter the scan time
			WebDriver_Functions.Type(By.id("massEntryForm:dex03_repeatdatatable:0:scanTime_maskedinput_field"), "1200");
			
			// Enter Cosmos ID, random 2 digit
			if (WebDriver_Functions.isEnabled(By.id(MT + ":cosmosNbr_inputtext"))) {
				WebDriver_Functions.Type(By.id(MT + ":cosmosNbr_inputtext"), CosmosNumber);
			}
			
			// if the destination address was not passed then load a dummy value for the pickup scan.
			if (Shipment_Info.Destination_Address_Info == null || 
					Shipment_Info.Destination_Address_Info.City.contentEquals("")) {
				Account_Data Account_Info = Environment.getAddressDetails(Level, "US");
				Shipment_Info.setDestination_Address_Info(Account_Info.Billing_Address_Info);
			}
			
			// Enter first digit of city
			WebDriver_Functions.Type(By.id(MT + ":destCityCd_inputtext"), Shipment_Info.Destination_Address_Info.City.substring(0, 1).toUpperCase());
				
			// Enter country
			WebDriver_Functions.Type(By.id(MT + ":destCountryCd_inputtext"), Shipment_Info.Destination_Address_Info.Country_Code);
				
			// Enter destination postal code
			WebDriver_Functions.Type(By.id(MT + ":destZipCd_inputtext"), Shipment_Info.Destination_Address_Info.PostalCode);
				
			// Click base service
			WebDriver_Functions.Click(By.id(MT + ":baseSvc_selectonemenu"));
			
	// Due to EMASS glitch     Enter first digit of city       Enter country
			WaitForInProgressOverlay();
			WebDriver_Functions.Type(By.id(MT + ":formCd_inputtext"), Form_ID);
			WebDriver_Functions.Type(By.id(MT + ":destCityCd_inputtext"), Shipment_Info.Destination_Address_Info.City.substring(0, 1).toUpperCase()); 
			WebDriver_Functions.Type(By.id(MT + ":destCountryCd_inputtext"), Shipment_Info.Destination_Address_Info.Country_Code);
			WebDriver_Functions.Click(By.id(MT + ":baseSvc_selectonemenu"));
			
			// Click base service  
			WebDriver_Functions.WaitForTextPresentIn(By.id(MT + ":baseSvc_selectonemenu"), "Express Saver");
			WaitForInProgressOverlay();
			// (20) Express Saver
			WebDriver_Functions.Select(By.id(MT + ":baseSvc_selectonemenu"), "20", "v");
			// come back and fix this so the correct service selected
			//WebDriver_Functions.Select(By.id(MT + ":baseSvc_selectonemenu"), Shipment_Info.Service, "t");
			
			// Package type (01) Customer Packaging
			WaitForInProgressOverlay();
			WebDriver_Functions.Select(By.id(MT + ":pkgType_selectonemenu"), "01", "v");

			// Delivery
			WebDriver_Functions.Click(By.xpath("//*[@id='massEntryForm:pup_repeatdatatable:0:handlingCd_selectmanymenu']/option[2]"));
			//WebDriver_Functions.Select(By.id(MT + ":handlingCd_selectmanymenu"), "02", "v");
			
			// Click submit
			WaitForInProgressOverlay();
			WebDriver_Functions.Click(By.id("massEntryForm:pup_savebutton"));
			
			// Click yes on confirmations.
			WaitForInProgressOverlay();
			WebDriver_Functions.Click(By.id("massEntryForm:save_confirmation_yesbutton"));
			
			// The below should be the final confirmation
			WebDriver_Functions.Click(By.id("massEntryForm:alertDialog_okbutton"));
			
			/*Shipment_Info.writeShipment_Data_To_Excel(false);*/
			
			return true;
		}catch (Exception e){
			throw e;
		}
	}
	
	public static boolean eMASS_Navigate_And_Login(int ScanOption) {
		try {
			WebDriver_Functions.ChangeURL("EMASS", null, null, false);
			
			// Login if prompted
			if (WebDriver_Functions.isPresent(By.id("username"))) {
				WebDriver_Functions.Type(By.id("username"), Credentials);
				WebDriver_Functions.Type(By.id("password"), Credentials);
				WebDriver_Functions.Click(By.id("submit"));
			}
			
			// Enter the location  ex :NQAA
			if (WebDriver_Functions.isPresent(By.id("locationField"))) {
				WebDriver_Functions.Type(By.id("locationField"), LocationCode);
				WebDriver_Functions.Click(By.className("primaryButton"));
			}
			
			if (ScanOption == 1) { 
				// Click the Pickup option
				WebDriver_Functions.Click(By.linkText("Pickup"));
				WebDriver_Functions.Click(By.linkText("PUP - Package Pick Up"));
			} else if (ScanOption == 2) {
				// Click the delivery exception option
				WebDriver_Functions.Click(By.linkText("Delivery"));
				WebDriver_Functions.Click(By.linkText("DEX - Delivery Attempt"));
				WebDriver_Functions.Click(By.linkText("03 - Incorrect Address"));
			}
			
			return true;
		} catch (Exception e) {
			return false;
		}
	}
	
	public static void eMASS_Scanning_Info() {
		// Enter the scanning info
		try {
			WaitForInProgressOverlay();
			WebDriver_Functions.Type(By.id("massEntryForm:empNbr_inputtext"), Scanning_Info_FedEx_Id);
			WebDriver_Functions.Click(By.id("massEntryForm:route_inputtext"));
			WaitForInProgressOverlay();
			WebDriver_Functions.Type(By.id("massEntryForm:route_inputtext"), Scanning_Info_FedEx_Route);
			WebDriver_Functions.Click(By.id(MT + ":trkNo_inputtext"));
			WaitForInProgressOverlay();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public static void WaitForInProgressOverlay() throws Exception {
		WebDriver_Functions.Wait(3);
		// WebDriver_Functions.WaitPresent(By.className("ice-sub-mon-txt"));
		
		WebDriver_Functions.WaitNotVisable(By.className("ui-widget-overlay"));
		WebDriver_Functions.WaitNotVisable(By.id("massEntryForm:submitMonitor_clone"));
		//Helper_Functions.PrintOut("ele not present");
	}
	
	public static void TypeAndCheck(By Ele, String Input) {
		try {
			if (!WebDriver_Functions.GetText(Ele).contentEquals(Input) && !WebDriver_Functions.GetValue(Ele).contentEquals(Input)) {
				WebDriver_Functions.Type(Ele, Input);
				WaitForInProgressOverlay();
			}
			
			if (!WebDriver_Functions.GetText(Ele).contentEquals(Input) && !WebDriver_Functions.GetValue(Ele).contentEquals(Input)) {
				Helper_Functions.PrintOut(String.format("Expected: %s Currentl Text: %s Current Value: %s", Input, WebDriver_Functions.GetText(Ele), WebDriver_Functions.GetValue(Ele)));
				TypeAndCheck(Ele, Input);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/// Still work in progress, need to generate the Oauth and send the cookie in the request.
	public static boolean eMASS_API(Shipment_Data Shipment_Info) {
		String CallingMethod = Thread.currentThread().getStackTrace()[2].getMethodName();
		String Level = Environment.getInstance().getLevel();
		String CallingIdentifier = "L" + Level + " " + CallingMethod;

		String URL = "https://devsso.secure.fedex.com/L2/Event-Entry-5539C/main.jsf";
		String response = "";
		try {
			String queryParam = "massEntryForm=massEntryForm&ice.window=n3k4mxbm13&ice.view=v5kcn8a9m&massEntryForm%3AempNbr_inputtext=0000703233&massEntryForm%3Aroute_inputtext=BSC&massEntryForm%3AscanDate_calendar_input=$$DATE$$&massEntryForm%3Apup_repeatinputpanel_rowCount_selectonemenu=0&massEntryForm%3Apup_repeatdatatable%3A0%3AtrkNo_inputtext=$$TRACKINGNUMBER$$&massEntryForm%3Apup_repeatdatatable%3A0%3AformCd_inputtext=0201&massEntryForm%3Apup_repeatdatatable%3A0%3AscanTime_maskedinput_field=10%3A11&massEntryForm%3Apup_repeatdatatable%3A0%3AstopType_selectonemenu=O&massEntryForm%3Apup_repeatdatatable%3A0%3AdestCityCd_inputtext=S&massEntryForm%3Apup_repeatdatatable%3A0%3AdestCountryCd_inputtext=$$COUNTRYCODE$$&massEntryForm%3Apup_repeatdatatable%3A0%3AdestZipCd_inputtext=$$ZIPCODE$$&massEntryForm%3Apup_repeatdatatable%3A0%3AbaseSvc_selectonemenu=20&massEntryForm%3Apup_repeatdatatable%3A0%3ApkgType_selectonemenu=01&massEntryForm%3Apup_repeatdatatable%3A0%3AhandlingCd_selectmanymenu=01&massEntryForm%3Apup_repeatdatatable%3A0%3Ahours_inputtext=0&massEntryForm%3Apup_repeatdatatable%3A0%3AdeliveryAddr_inputtext=&massEntryForm%3Apup_repeatdatatable%3A0%3Acomments_textarea_input=&icefacesCssUpdates=&javax.faces.ViewState=-7710573347804357216%3A164217649180926224&javax.faces.source=massEntryForm%3Asave_confirmation_yesbutton&javax.faces.partial.event=click&javax.faces.partial.execute=%40all&javax.faces.partial.render=%40all&ice.window=n3k4mxbm13&ice.view=v5kcn8a9m&ice.focus=massEntryForm%3Asave_confirmation_yesbutton&massEntryForm%3Asave_confirmation_yesbutton=Yes&ice.event.target=massEntryForm%3Asave_confirmation_yesbutton&ice.event.captured=massEntryForm%3Asave_confirmation_yesbutton&ice.event.type=onclick&ice.event.alt=false&ice.event.ctrl=false&ice.event.shift=false&ice.event.meta=false&ice.event.x=637&ice.event.y=558.4815063476562&ice.event.left=false&ice.event.right=false&ice.submit.type=ice.s&ice.submit.serialization=form&javax.faces.partial.ajax=true";
			
			
			Format formatter = new SimpleDateFormat("MMddyyyy"); // 12262019 -> MMDDYYYY format
			Date dt = new Date();
			Calendar c = Calendar.getInstance(); 
			c.setTime(dt); 
			String todayDateFormatted = formatter.format(dt);
			queryParam = queryParam.replace("$$DATE$$", todayDateFormatted);
			
			queryParam = queryParam.replace("$$TRACKINGNUMBER$$", Shipment_Info.Tracking_Number);
			queryParam = queryParam.replace("$$COUNTRYCODE$$", Shipment_Info.Origin_Address_Info.Country_Code);
			queryParam = queryParam.replace("$$ZIPCODE$$", Shipment_Info.Origin_Address_Info.PostalCode);
			
			
			String method = "POST";
			URL serviceUrl = new URL(URL);

			HttpsURLConnection con = (HttpsURLConnection) serviceUrl.openConnection();
			con.setRequestMethod(method);
			
			con.setRequestProperty("Cookie", getEMASSCookie(Level));
			
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
			
			if (response.contains("Update this with what is expected to be in successful reply")) {
				
				return true;
			}
		} catch (Exception e) {
			Helper_Functions.PrintOut("Unable to apply eMASS PUP Scan -- " + CallingIdentifier);
			e.printStackTrace();
		}
		return false;
	}

	private static String getEMASSCookie(String Level) {
		String URL = "";
		if (Level.contentEquals("2")) {
			URL = "https://devoam.secure.fedex.com/oam/server/auth_cred_submit";
		} else if (Level.contentEquals("2")) {
			// not confirmed  https://drtoam.secure.fedex.com/oam/server/auth_cred_submit
			URL = "";
		}
		
		HttpPost httppost = new HttpPost(URL);

		httppost.addHeader("Content-Type", "application/x-www-form-urlencoded");
		httppost.addHeader("Accept", "application/json");
		httppost.addHeader("Content-Type", "application/json");

		List<NameValuePair> urlParameters = new ArrayList<NameValuePair>();
		urlParameters.add(new BasicNameValuePair("username", Credentials));
		urlParameters.add(new BasicNameValuePair("password", Credentials));
		urlParameters.add(new BasicNameValuePair("request_id", "8067903622302088728"));
		urlParameters.add(new BasicNameValuePair("OAM_REQ", "VERSION_4~A8fP3DN3H5doCQNc5rdsWN%2fXOxz8r7xcLTN7%2brWexyjJ6tSU1DpcW9DP4zgLopZ5jSvtBskR0otxshXwWiqdb6CQ7eGXZIg9r95vawpTCZlVw%2bwX6nKI4z1P1oGZYV8De4RAm8pejsLCPEVVPPmbAzJAztU5KTmXL2YQDhHhvhKeNcHAjgBxLcUdQ7qh%2bEvXjBmdtcmo5NesOSQAypVF0AaTSDwXblrI4LuUiqmW7d%2b7TWADhinUIMekZOvxHsH6iA%2b08%2bBlmv3wrBuvbVIbiatuwCP%2fNEF5jmIVl3aiCgYdJYjL45P7PNCW5g53Enh4H%2fNcbeBBE2DuzVZLG3z00cs4qy8JK33pN9ny33Vjdps4G9cG8qltnuthWlX2uku9bcCXXJpsUOZVmlZ7I1BwQpgSt1C7rTT8K2V80BzuXduHOQ9dlFDhRdwZeZI33HUj5am7kT2ujwqsvSl5cRP5i%2fgNABP7amiLArE1HIG8nbqTtP%2fKCEDB1A7x%2bCgh214Ni3PGYJbPSq97nIH7yMy83D%2fcAjs8bFkIr9cTqpcqkEb23aTaA3b2VOViYQIuNNKL%2bkSTia%2brMAXo%2fVvrdhmwv7ABPMu4ea4N3AHkWiY6d4vscAv6GHCaJWz3Bg%2bCAbL6EaV9qs50A5N3UUzfwNMBhQqZ5mnZ5w1MawUbWL1XCJN5OMqnlMHvqlGh3U%2bs9fk%2f0AvmhcCcyQIpk6fuZa5j6iTEgSi7J1gbW%2f7uYkTMa0H%2fIsRZCY7CUnLfwsYvjpCiz%2bwrH%2b44lwI7AU2Pdw8yG%2fCDTiNihrHQMVB8kzQIVTQQ6aAwhpxjQApHzgSmnwwrqr2QpiOFYmzSchOAEHa8iN7FdawaLCIRXZBrqOscvUG5s3JnYHmhtvbtTNO%2fLTYY1PtrvOososaaW9GoFbfLcuwnxOOwgi8e5Z%2fNnWf67n1aB9UQr0PcgCgiGyFKHwjDUCDDlbo6UFJwe%2bSQRhotqpQl1ig5iLdjPGVmwPNK6tLiHlN%2fOJH7pCREABPsbW1LcH7BlktNUiCHCEM%2bsaLudHRsIfgs7DjNNkBitPkSS3pskW0RxArpa%2bHJd9cmx2xTSv11k9LfqGzjUaq4Hrule3RUjPUy0j3Yh3yuECmQdAfkmqQew81W8RrGFsGAsgRnIBr7JJBEnfUWV0GxW%2bdi%2faPqRznpbF%2f8qjUpjrykAC3Yq%2bIxYpuiYr0hbp%2fXUpLykgJ0mLb7RhVoPQ1AxvOME82gONkPiftMB2A5L6iISz9RgkFsxJSpBiclhQe5nBb7k8xj2gLl4SxpqRCTg5CcG5t0m1qBog0CjIKvKExO5fWK2RL2xo82rjHLVqJjmS45mQo2JYJT0vGiZg0KMP83YjO12KbjwTOpAFCgtRq4HNMw2gv2fJx7SwDsmGiIlE%2bXQux6EjUoGWP5KxTpRBHXfpcoVu29p93MiJyNDg0Q9lr0actUZquv6P%2fHnq%2fID1SGglITBx19x33Qrt2IHP2BFBlwEpenSfdqn9cRS4pLNVEiDCRZluWGvP0TZo%2bWfxqmqcWwQ%2fgC7Okvo3LsV1EBL6LqaaaFfUqKZNBGfuf%2b9LtG4E3EIDxQrvoWPpbP6whohjHV57iv8eVTB87xzbTTx6uI3yliQEhCJE5tbLkMDVZ73L8KK%2bqBDQRtNrLyHrc%2bK75N1LWfQZfw0JIm1Csa0POk1AnGM5JcZJVQMw6gDSeuwfCuVl4l%2fnobri%2f7LR7YzL8Fjv%2f%2f8IhOZbKkoqesv7fBpXBMU2JImOglAf2WnJqYGOVWkhlfczXXXn6%2b4XIuEjSyydq0yxREPMmI8ihmiaC%2bBjLi5RMy7NKKB9IFKo8h%2br1szkSW05ZyAWB1HCRN8hHHmBwg0Li2%2f7Ms%2bnckoPSucLaw0sMnvM38a9VDEmgEo2E5GoJ3re2ksN%2funEVxzT18lvx4bZCanrHDXAUraHJnEJvWNicuBmvAFctAIVVCz2nPHcIpKgXPw9wASH9Yvi5be%2fCYkWiwgt%2bjoZn2JczXQqo2z0lStt5nNRWBdmG0EHLpDr5G%2bQh7QnIZz94X%2bib0BuEDvVzJOfwEMmoCvbbLOND8oG%2bCxcCSuHqrkb47FVyG7xGkh%2fsaZQE7mrM0"));
		urlParameters.add(new BasicNameValuePair("lang", "en_US"));
		

		try {
			httppost.setEntity(new UrlEncodedFormEntity(urlParameters));
			HttpClient httpclient = HttpClients.createDefault();
			HttpResponse Response = httpclient.execute(httppost);
			Header[] Headers = Response.getAllHeaders();
			
			String Cookies = "";
			String RequestHeaders = "";
			for (Header Header : Headers) {
				if (Header.getName().contentEquals("Set-Cookie")) {
					Cookies += Header.toString().replace("Set-Cookie: ", ""); 
				}
				RequestHeaders += Header + "___";
			}
				
			String MethodName = "EMASSLogin";
			General_API_Calls.Print_Out_API_Call(MethodName, httppost.toString(), RequestHeaders, Credentials, Response.toString());

			return Cookies;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}	  
	}
	
}
