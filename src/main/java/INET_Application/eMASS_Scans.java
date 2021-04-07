package INET_Application;

import java.lang.reflect.Method;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.message.BasicNameValuePair;
import org.openqa.selenium.By;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Listeners;
import org.testng.annotations.Test;
import Data_Structures.Account_Data;
import Data_Structures.Shipment_Data;
import SupportClasses.Environment;
import SupportClasses.General_API_Calls;
import SupportClasses.Helper_Functions;
import SupportClasses.WebDriver_Functions;

@Listeners(SupportClasses.TestNG_TestListener.class)

public class eMASS_Scans {

	static String Credentials = "5159473";
	public static String LocationCode = "NQAA";
	static String Scanning_Info_FedEx_Id = "703233";
	static String Scanning_Info_FedEx_Route = "BSC";
	static String CosmosNumber = "12";
	static String Form_ID = "0201"; //default for US to US 
	static String SessionEmassCookie = "";
	
	static String Level = "2";
	static String MT = "massEntryForm:pup_repeatdatatable:0";
	
	@BeforeClass
	public void beforeClass() {
		Environment.SetLevelsToTest(Level);
	}
	
	@DataProvider // (parallel = true)
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
	
	@Test (dataProvider = "dp", enabled = true)
	public static void eMASSTest(String Level, Shipment_Data Shipment_Info){
		try {
			eMASS_Scans.eMASS_Pickup_Scan(Shipment_Info);
		}catch (Exception e) {
			e.printStackTrace();
			Assert.fail(e.getCause().toString());
		}
	}
	
	@Test (dataProvider = "dp", enabled = false)
	public static void eMASSFullUpdate(String Level, Shipment_Data Shipment_Info, String TrackingNumbers[]){
		try {
			eMASS_Scans.eMASS_Multiple_Pickup_Scan(Shipment_Info, TrackingNumbers);
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
			
			// if the destination address was not passed then load a dummy value for the pickup scan.
			if (Shipment_Info.Destination_Address_Info == null || 
					Shipment_Info.Destination_Address_Info.City.contentEquals("")) {
				Account_Data Account_Info = Environment.getAddressDetails(Level, "US");
				Shipment_Info.setDestination_Address_Info(Account_Info.Billing_Address_Info);
			}

			enter_Address_Details(Shipment_Info);
		
			int attempts = 0;
			boolean serviceSelected = false;
			while (attempts < 2 && !serviceSelected) {
				try {
					WebDriver_Functions.Click(By.id(MT + ":baseSvc_selectonemenu"));
					// Click base service  
					WebDriver_Functions.WaitForTextPresentIn(By.id(MT + ":baseSvc_selectonemenu"), "Express Saver");
					WaitForInProgressOverlay();
					// (20) Express Saver
					WebDriver_Functions.Select(By.id(MT + ":baseSvc_selectonemenu"), "20", "v");
					WaitForInProgressOverlay();
					// come back and fix this so the correct service selected
					//WebDriver_Functions.Select(By.id(MT + ":baseSvc_selectonemenu"), Shipment_Info.Service, "t");
					// Package type (01) Customer Packaging
					
					WebDriver_Functions.Select(By.id(MT + ":pkgType_selectonemenu"), "01", "v");
					WaitForInProgressOverlay();
					serviceSelected = true;
				} catch (Exception e){
					WaitForInProgressOverlay();
					attempts++;
				}
			}
			
			// Delivery
			WebDriver_Functions.Click(By.xpath("//*[@id='massEntryForm:pup_repeatdatatable:0:handlingCd_selectmanymenu']/option[2]"));
			WaitForInProgressOverlay();
			//WebDriver_Functions.Select(By.id(MT + ":handlingCd_selectmanymenu"), "02", "v");
			
			// double check address details were entered fully before submit
			enter_Address_Details(Shipment_Info);
			
			// Click submit
			WebDriver_Functions.Click(By.id("massEntryForm:pup_savebutton"));
			
			// Click yes on confirmations.
			WebDriver_Functions.Click(By.id("massEntryForm:save_confirmation_yesbutton"));
			
			// The below should be the final confirmation
			WebDriver_Functions.Click(By.id("massEntryForm:alertDialog_okbutton"));
			
			return true;
		}catch (Exception e){
			throw e;
		}
	}
	
	public static void enter_Address_Details(Shipment_Data Shipment_Info) {
		int numberOfDataChecks = 4;
		for (int addressLoop = 0; addressLoop < numberOfDataChecks; addressLoop++) {
			int valuesEnteredCorrectly = 0;
			// Enter form id
			valuesEnteredCorrectly += TypeAndCheck(By.id(MT + ":formCd_inputtext"), Form_ID);

			// Enter Cosmos ID
			if (WebDriver_Functions.isEnabled(By.id(MT + ":cosmosNbr_inputtext"))) {
				valuesEnteredCorrectly += TypeAndCheck(By.id(MT + ":cosmosNbr_inputtext"), CosmosNumber);
			}

			// Enter first digit of city
			valuesEnteredCorrectly += TypeAndCheck(By.id(MT + ":destCityCd_inputtext"), Shipment_Info.Destination_Address_Info.City.substring(0, 1).toUpperCase());

			// Enter country
			valuesEnteredCorrectly += TypeAndCheck(By.id(MT + ":destCountryCd_inputtext"), Shipment_Info.Destination_Address_Info.Country_Code);

			// Enter destination postal code
			valuesEnteredCorrectly += TypeAndCheck(By.id(MT + ":destZipCd_inputtext"), Shipment_Info.Destination_Address_Info.PostalCode);

			// Click base service - Used to trigger and test GUI to see if some value entered already was cleared.
			try {
				WaitForInProgressOverlay();
				WebDriver_Functions.Click(By.id(MT + ":baseSvc_selectonemenu"));
			} catch (Exception e) {}
			
			if (addressLoop > 1 && valuesEnteredCorrectly == 0) {
				// break from the loop if all were updated successfully.
				addressLoop = numberOfDataChecks;
			}
		}
	}
	
	public static boolean eMASS_Multiple_Pickup_Scan(Shipment_Data Shipment_Info, String TrackingNumbers[]) throws Exception{
		try {
			
			eMASS_Navigate_And_Login(1);
			
			eMASS_Scanning_Info();
			
			// Add rows based on the number of tracking numbers. If only single tracking number will not add any rows
			if (TrackingNumbers.length > 9) {
				Helper_Functions.PrintOut("EMASS only accepts 10 tracking numbers at a time.");
			}
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
			
				// Enter form id
				TypeAndCheck(By.id(TblRow + ":formCd_inputtext"), Form_ID);
					
				String city = Shipment_Info.Destination_Address_Info.City.substring(0, 1).toUpperCase();
				TypeAndCheck(By.id(TblRow + ":destCityCd_inputtext"), city);
				WaitForInProgressOverlay();

				// Due to EMASS glitch     Enter first digit of city, country, and postal again
				for (int addressLoop = 0; addressLoop < 3; addressLoop++) {
					// Enter first digit of city
					TypeAndCheck(By.id(TblRow + ":destCityCd_inputtext"), city);	
					
					// Enter destination postal code
					String zip = Shipment_Info.Destination_Address_Info.PostalCode;
					TypeAndCheck(By.id(TblRow + ":destZipCd_inputtext"), zip);
					
					// Enter country
					String country = Shipment_Info.Destination_Address_Info.Country_Code;
					TypeAndCheck(By.id(TblRow + ":destCountryCd_inputtext"), country);
					
					// Click base service
					WebDriver_Functions.Click(By.id(TblRow + ":baseSvc_selectonemenu"));
					WaitForInProgressOverlay();
					
					// double check the form id has been entered.
					TypeAndCheck(By.id(TblRow + ":formCd_inputtext"), Form_ID);
					
					addressLoop++;
				}
			
				
				try {
					// Click base service  
					WebDriver_Functions.WaitForTextPresentIn(By.id(TblRow + ":baseSvc_selectonemenu"), "Express Saver");
					WaitForInProgressOverlay();
					
					// (20) Express Saver
					WebDriver_Functions.Select(By.id(TblRow + ":baseSvc_selectonemenu"), "20", "v");
					WaitForInProgressOverlay();
					
					// Package type (01) Customer Packaging
					WebDriver_Functions.Select(By.id(TblRow + ":pkgType_selectonemenu"), "01", "v");
				}catch (Exception e) {
					// Added additional attempt when trying to select service. 
					//TODO: Need to see why the emass page does not load fully intermittently.
					// Click base service  
					WebDriver_Functions.WaitForTextPresentIn(By.id(TblRow + ":baseSvc_selectonemenu"), "Express Saver");
					WaitForInProgressOverlay();
					
					// (20) Express Saver
					WebDriver_Functions.Select(By.id(TblRow + ":baseSvc_selectonemenu"), "20", "v");
					WaitForInProgressOverlay();
					
					// Package type (01) Customer Packaging
					WebDriver_Functions.Select(By.id(TblRow + ":pkgType_selectonemenu"), "01", "v");
				}
				
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
				
				
				// Need to fix this issue, not sure why but sometimes the cosmos id is being cleared out half way through.
				// Enter Cosmos ID, random 2 digit
				if (WebDriver_Functions.isEnabled(By.id(TblRow + ":cosmosNbr_inputtext"))) {
					TypeAndCheck(By.id(TblRow + ":cosmosNbr_inputtext"), CosmosNumber);
				}
			}
			
			// Click submit
			WaitForInProgressOverlay();
			WebDriver_Functions.Click(By.id("massEntryForm:pup_savebutton"));
			
			// Click yes on confirmations.
			WebDriver_Functions.Click(By.id("massEntryForm:save_confirmation_yesbutton"));
			
			// The below should be the final confirmation
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
	
	public static boolean eMASS_Navigate_And_Login(int ScanOption) throws Exception {
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
			try {
				WebDriver_Functions.WaitClickable(By.linkText("Pickup"));
				WebDriver_Functions.Click(By.linkText("Pickup"));
				WebDriver_Functions.WaitClickable(By.linkText("PUP - Package Pick Up"));
				WebDriver_Functions.Click(By.linkText("PUP - Package Pick Up"));
			} catch (Exception e) {
				Helper_Functions.PrintOut("Second attempt to click PUP");
				WebDriver_Functions.WaitClickable(By.linkText("Pickup"));
				WebDriver_Functions.Click(By.linkText("Pickup"));
				WebDriver_Functions.WaitClickable(By.linkText("PUP - Package Pick Up"));
				WebDriver_Functions.Click(By.linkText("PUP - Package Pick Up"));
			}
			
		} else if (ScanOption == 2) {
			// Click the delivery exception option
			WebDriver_Functions.Click(By.linkText("Delivery"));
			WebDriver_Functions.Click(By.linkText("DEX - Delivery Attempt"));
			WebDriver_Functions.Click(By.linkText("03 - Incorrect Address"));
		}
		
		return true;
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
		// Need to make this dynamic
		WebDriver_Functions.Wait(2);
		// WebDriver_Functions.WaitPresent(By.className("ice-sub-mon-txt"));
		
		WebDriver_Functions.WaitNotVisable(By.className("ui-widget-overlay"));
		WebDriver_Functions.WaitNotVisable(By.id("massEntryForm:submitMonitor_clone"));
		//Helper_Functions.PrintOut("ele not present");
	}
	
	public static int TypeAndCheck(By Ele, String Input) {
		try {
			// enter value for blank field.
			if (WebDriver_Functions.GetText(Ele).contentEquals("") && WebDriver_Functions.GetValue(Ele).contentEquals("")) {
				WebDriver_Functions.Type(Ele, Input);
			}
			
			if (!WebDriver_Functions.GetText(Ele).contentEquals(Input) && !WebDriver_Functions.GetValue(Ele).contentEquals(Input)) {
				Helper_Functions.PrintOut(String.format("Expected: %s Currentl Text: %s Current Value: %s", Input, WebDriver_Functions.GetText(Ele), WebDriver_Functions.GetValue(Ele)));
				return 1;
			} else {
				return 0;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return 0;
	}
	
	public static boolean eMASS_API_Pickup_Scan(String trackingNumber) throws Exception{
		try {
			if (SessionEmassCookie.contentEquals("")) {
				//sending 0 so will not navigate.
				eMASS_Navigate_And_Login(0);
	  			// get the cookies from the browser. 
	  			//TODO: Figure out how to generate the needed cookies without using browser.
				SessionEmassCookie = WebDriver_Functions.GetCookies();
			}
			
			/*
			Accept: * / *    // spaces added due to comment
			Accept-Encoding: gzip, deflate, br
			Accept-Language: en-US,en;q=0.9
			Connection: keep-alive
			Content-Length: 1999
			Content-type: application/x-www-form-urlencoded;charset=UTF-8
			Cookie: eShipmentGUI.roles=view,edit,hsort_view; eShipmentGUI.linkId=351; fdx_cbid=31382261241593548677781040317251; fdx_locale=en_US; level=test; fdx_redirect=en-us; cc_path=us; check=true; AMCVS_1E22171B520E93BF0A490D44%40AdobeOrg=1; _gcl_au=1.1.934935371.1593548683; s_ecid=MCMID%7C55471647958800170383525577233961454792; fcl_contactname=Jenae Penoli; fdx_login=10015.c1f8.439b649f; fcl_fname=Jenae; fcl_uuid=SpZFmeGs39; postalCode=96003; siteDC=edc; SMIDENTITY=mnAJhFp7phlHDlSII/Nznb2gyHpfTmwSgZQHrcIwIWLASgYmSt0oVd2NyyxADk/UTUsj9ScRHesQ3VEpmqVqvs90rKCtfbLnYOHs41XTrGolfY5c4KiFpj1GwWuUkGNR8EaKP/KEJiJYusOwuYB2W9Mj3G7AwPFLWbVbNj8SjMWevOz+OD+QPQGE3Uv6rjfIHiDivqey+0xbCSjUAtgM9+cNRMJX/E4IDWkkfEDETNWVSGKYJviNqOdVg1Hl1ZPklVuMxsHxlo8lrx3V477F5R/NW2vVJpAfnZ9aiQ2BpfzEJkrrlzAnqpimQ+yz12pGYMn05epZ5LDHBkG6cBoSLqB8kz85XJWhXuK7eDtA1sL8uZ31xQ9aZoX/my8pJD7L0lZ+xgGT9/cxGvFAUdev/8D1gcht9SEN1cEiQgy7ZPiFBpQ+yZdRIngAC+yoYP266Rs6aabJ+sV04WCZ+TOqRd+3iTag3DAqJx6zS8Lys8r/nWZEU+1YndQnQK/iCWIFd3/BdFsBosCCR/LNwtN/ogPHaXdJtYY/; AMCV_1E22171B520E93BF0A490D44%40AdobeOrg=1075005958%7CMCIDTS%7C18444%7CMCMID%7C55471647958800170383525577233961454792%7CMCAAMLH-1594153507%7C7%7CMCAAMB-1594153507%7CRKhpRz8krg2tLO6pguXWp5olkAcUniQYPHaMWWgdJ3xzPWQmdj0y%7CMCOPTOUT-1593555883s%7CNONE%7CMCAID%7CNONE%7CvVersion%7C4.4.1%7CMCCIDH%7C-132854918; Nina-nina-fedex-session=%7B%22loginStatus%22%3A%22loggedIn%22%2C%22locale%22%3A%22en_us%22%2C%22lcstat%22%3Afalse%7D; ADRUM=s=1593548785609&r=https%3A%2F%2Fwww.fedex.com%2Ffedextracking%2F%3F0; mbox=session#dafa7224132b41a4aa7908cc04c556d8#1593550657|PC#dafa7224132b41a4aa7908cc04c556d8.35_0#1594758397; s_sq=%5B%5BB%5D%5D; s_pers=%20s_vnum%3D1593579600765%2526vn%253D1%7C1593579600765%3B%20s_dfa%3Dfedexglbl%252Cfedexus%7C1593550596592%3B%20sc_fcl_uuid%3DSpZFmeGs39%7C1601324796624%3B%20gpv_pageName%3Dus%252Fen%252Ffedex%252Fwfcl%252Fmyprofile%252Fdeliverymanager%7C1593550668173%3B%20s_nr%3D1593548868185-New%7C1625084868185%3B%20s_invisit%3Dtrue%7C1593550668200%3B; aemserver=Pre-Prod; experimentation_subject_id=IjQzY2E5ZWU2LTU4NzctNDU2YS05NTI5LWFiZWVlODJjNDE2OCI%3D--d9da145fef885a705c6b6f0b8b7aeb9833376634; s_sess=%20s_cc%3Dtrue%3B%20setLink%3D%3B%20s_visit%3D1%3B%20SC_LINKS%3D%3B%20s_ppv%3Dus%252Fen%252Ffedex%252Fwfcl%252Fmyprofile%252Fdeliverymanager%252C65%252C62%252C2081.1112060546875%3B; WSSOLanguage=en_US; OAMAuthnCookie_testsso.secure.fedex.com_443=939cf701da2d2f8308a13ab76b6dce9de0372b64%7EvnxNPMi7GPYoA5Y64P4douRnxGvXm5xnApm5%2BMo%2BvDUlNcE8uL0oVfuIcD7mUniKmb1qeBjq3I761kFPW%2Fz49ZGnYYMzyRUmv109cUOqYToesBR7SG1%2BsnGVb577XrxS%2FcXHa4K9jn5%2B3wrZC9WmelDkwQLLyMGrJ%2FRf7yXH8XzijpGIkv%2FPM%2FgkMQ4pKX12NlewoHXcxS5eWeVSGaybUEjIz4BY7zO1dTxipKrX8sKvIMSPA121jpXiIdVuE8dXU7Z8BfhEJGOWSFLTRfzbj%2BUDjhgLTrzOT1EPXcilCz7iwPq7kwpVdbHU9TcbIGJVNpgn72FPsGw4urvK1dmjXJ5btFGTLMBdxBibF2juQu%2FX0DgCvr%2FfwBSp99dqKyFjIBoYLFmf2%2F5HHQX8Y2AHgZeEbit20JZ0YUBuKJCy%2BpqBHn6txazZqtEBxkSeDkG9AnVwZHxXy%2FParPV5Ivuc%2FxTDL%2FvRAc8tSNwUWuFz0im8cQG2GsO0DyhhcBSUrmo7Rp5nRsYMG%2BLGHkWUg%2FAjcNtIl3NO9r7aFf9JrY7FL8s%3D; OAMAuthnHintCookie=1; eShipmentGUISessionID=9_EHGASsp_AdsRPHeJmUcH57L44a9h852xjb8aGONX29Dc8ejLH-!1241575345; eShipmentGUI.userId=5159473; eShipmentGUI.locationCode=NQAA; Event-EntrySessionID=pgoHGBWtzwyKTT8WYCJcoGhZiJvRnPU1ndX2tPPWkMXW9Lsg0DuV!-798884383; ice.push.browser=5wkc2fnctv; ice.connection.contextpath=.; ice.connection.running=2d157:acquired; OAM_GITO=WKONy1272NNBOImLBE17IA==~yp4MmGauj895K2NZCuLkLfqboyiWCosiQXNgJ7VIirxQcAPI+HFwq8OiBv0dBCCf0b0EkLI4OkuqjBEO5cOAcwsbGN91vrIL7ucwOfLky5z+Rj/FvgUT+Rl7Pf2n1dF4yjaXgbivC6Wp/UHsgDmQ6WLjTauMLLINuBR9KuP/8dn3UOwl4gujHSAUXRvc0WimjknCeGIDrrFscq57oI7v/lpIB0QJJt5zm1PujX6hWeE=; ice.connection.lease=1593552038799
			Faces-Request: partial/ajax
			Host: testsso.secure.fedex.com
			Origin: https://testsso.secure.fedex.com
			Referer: https://testsso.secure.fedex.com/L3/Event-Entry-5539C/
			Sec-Fetch-Dest: empty
			Sec-Fetch-Mode: cors
			Sec-Fetch-Site: same-origin
			User-Agent: Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/83.0.4103.116 Safari/537.36

			Request URL: https://testsso.secure.fedex.com/L3/Event-Entry-5539C/main.jsf
			Request Method: POST
			
			massEntryForm: massEntryForm
			ice.window: 92kc2fnctv
			ice.view: v16q1jgi6
			massEntryForm:empNbr_inputtext: 0000703233
			massEntryForm:route_inputtext: 020
			massEntryForm:scanDate_calendar_input: 06302020
			massEntryForm:pup_repeatinputpanel_rowCount_selectonemenu: 0
			massEntryForm:pup_repeatdatatable:0:trkNo_inputtext: 794976048187
			massEntryForm:pup_repeatdatatable:0:formCd_inputtext: 0201
			massEntryForm:pup_repeatdatatable:0:scanTime_maskedinput_field: 16:18
			massEntryForm:pup_repeatdatatable:0:cosmosNbr_inputtext: 0201
			massEntryForm:pup_repeatdatatable:0:stopType_selectonemenu: O
			massEntryForm:pup_repeatdatatable:0:destCityCd_inputtext: A
			massEntryForm:pup_repeatdatatable:0:destCountryCd_inputtext: US
			massEntryForm:pup_repeatdatatable:0:destZipCd_inputtext: 75063
			massEntryForm:pup_repeatdatatable:0:baseSvc_selectonemenu: 20
			massEntryForm:pup_repeatdatatable:0:pkgType_selectonemenu: 01
			massEntryForm:pup_repeatdatatable:0:handlingCd_selectmanymenu: 01
			massEntryForm:pup_repeatdatatable:0:hours_inputtext: 0
			massEntryForm:pup_repeatdatatable:0:deliveryAddr_inputtext: 
			massEntryForm:pup_repeatdatatable:0:comments_textarea_input: 
			icefacesCssUpdates: 
			javax.faces.ViewState: 4403610302597563339:-7234975737240945987
			javax.faces.source: massEntryForm:save_confirmation_yesbutton
			javax.faces.partial.event: click
			javax.faces.partial.execute: @all
			javax.faces.partial.render: @all
			ice.window: 92kc2fnctv
			ice.view: v16q1jgi6
			ice.focus: massEntryForm:save_confirmation_yesbutton
			massEntryForm:save_confirmation_yesbutton: Yes
			ice.event.target: massEntryForm:save_confirmation_yesbutton
			ice.event.captured: massEntryForm:save_confirmation_yesbutton
			ice.event.type: onclick
			ice.event.alt: false
			ice.event.ctrl: false
			ice.event.shift: false
			ice.event.meta: false
			ice.event.x: 570
			ice.event.y: 550.6666564941406
			ice.event.left: false
			ice.event.right: false
			ice.submit.type: ice.s
			ice.submit.serialization: form
			javax.faces.partial.ajax: true
			
			<?xml version='1.0' encoding='UTF-8'?>
<partial-response><changes><update id="massEntryForm:route_message"><![CDATA[<span id="massEntryForm:route_message"></span>]]></update><update id="massEntryForm:pup_repeatdatatable:0:cancel_checkbox"><![CDATA[<input class="none" disabled="true" id="massEntryForm:pup_repeatdatatable:0:cancel_checkbox" name="massEntryForm:pup_repeatdatatable:0:cancel_checkbox" style="color:gray;font-style:italic;background-color:#E5E4E2;;" type="checkbox" />]]></update><update id="massEntryForm:pup_repeatdatatable:0:mpsFlag_checkbox"><![CDATA[<input class="none" disabled="true" id="massEntryForm:pup_repeatdatatable:0:mpsFlag_checkbox" name="massEntryForm:pup_repeatdatatable:0:mpsFlag_checkbox" style="color:gray;font-style:italic;background-color:#E5E4E2;;" type="checkbox" />]]></update><update id="massEntryForm:pup_repeatdatatable:0:trkNo_label"><![CDATA[<label for="massEntryForm:pup_repeatdatatable:0:trkNo_inputtext" id="massEntryForm:pup_repeatdatatable:0:trkNo_label" style="color:gray;font-style:italic;background-color:#E5E4E2;;" title="Enter Track Number with some prior scan activity on all screens except pickup">
*Tracking Nbr</label>]]></update><update id="massEntryForm:pup_repeatdatatable:0:trkNo_inputtext"><![CDATA[<input class="none" disabled="true" id="massEntryForm:pup_repeatdatatable:0:trkNo_inputtext" maxlength="12" name="massEntryForm:pup_repeatdatatable:0:trkNo_inputtext" size="15" style="color:gray;font-style:italic;background-color:#E5E4E2;;" type="text" value="794976048187" />]]></update><update id="massEntryForm:pup_repeatdatatable:0:formCd_label"><![CDATA[<label for="massEntryForm:pup_repeatdatatable:0:formCd_inputtext" id="massEntryForm:pup_repeatdatatable:0:formCd_label" style="color:gray;font-style:italic;background-color:#E5E4E2;;" title="Enter Form ID">
*Form ID</label>]]></update><update id="massEntryForm:pup_repeatdatatable:0:formCd_inputtext"><![CDATA[<input class="none" disabled="true" id="massEntryForm:pup_repeatdatatable:0:formCd_inputtext" maxlength="4" name="massEntryForm:pup_repeatdatatable:0:formCd_inputtext" size="4" style="color:gray;font-style:italic;background-color:#E5E4E2;;" type="text" value="0201" />]]></update><update id="massEntryForm:pup_repeatdatatable:0:pkgRouted_checkbox"><![CDATA[<input class="none" disabled="true" id="massEntryForm:pup_repeatdatatable:0:pkgRouted_checkbox" name="massEntryForm:pup_repeatdatatable:0:pkgRouted_checkbox" style="color:gray;font-style:italic;background-color:#E5E4E2;;" type="checkbox" />]]></update><update id="massEntryForm:pup_repeatdatatable:0:scanTime_maskedinput"><![CDATA[<span id="massEntryForm:pup_repeatdatatable:0:scanTime_maskedinput"><input aria-disabled="true" aria-required="true" class="ui-inputfield ui-inputmask ui-widget ui-state-default ui-corner-all ui-state-required none" disabled="disabled" maxlength="5" name="massEntryForm:pup_repeatdatatable:0:scanTime_maskedinput_field" role="textbox" size="5" style="color:gray;font-style:italic;background-color:#E5E4E2;;" type="text" value="16:18" /><script type="text/javascript">var scanTime_maskedinput = ice.ace.create("InputMask",["massEntryForm:pup_repeatdatatable:0:scanTime_maskedinput",{"mask":"99:99","inFieldLabel":"","inFieldLabelStyleClass":"ui-input-label-infield","labelIsInField":false,"behaviors":{"change":{"source":"massEntryForm:pup_repeatdatatable:0:scanTime_maskedinput","execute":'massEntryForm:pup_repeatdatatable:0:scanTime_maskedinput',"render":'massEntryForm:pup_repeatdatatable:0:scanTime_maskedinput massEntryForm:pup_repeatdatatable:0:scanTime_message',"event":"valueChange"}}}]);</script></span>]]></update><update id="massEntryForm:pup_repeatdatatable:0:dadsFlag_checkbox"><![CDATA[<input class="none" disabled="true" id="massEntryForm:pup_repeatdatatable:0:dadsFlag_checkbox" name="massEntryForm:pup_repeatdatatable:0:dadsFlag_checkbox" style="color:gray;font-style:italic;background-color:#E5E4E2;;" type="checkbox" />]]></update><update id="massEntryForm:pup_repeatdatatable:0:cosmosNbr_label"><![CDATA[<label for="massEntryForm:pup_repeatdatatable:0:cosmosNbr_inputtext" id="massEntryForm:pup_repeatdatatable:0:cosmosNbr_label" style="color:gray;font-style:italic;background-color:#E5E4E2;;">
*COSMOS Nbr</label>]]></update><update id="massEntryForm:pup_repeatdatatable:0:cosmosNbr_inputtext"><![CDATA[<input class="none" disabled="true" id="massEntryForm:pup_repeatdatatable:0:cosmosNbr_inputtext" maxlength="5" name="massEntryForm:pup_repeatdatatable:0:cosmosNbr_inputtext" size="5" style="color:gray;font-style:italic;background-color:#E5E4E2;;" type="text" value="0201" />]]></update><update id="massEntryForm:pup_repeatdatatable:0:stopType_label"><![CDATA[<label for="massEntryForm:pup_repeatdatatable:0:stopType_selectonemenu" id="massEntryForm:pup_repeatdatatable:0:stopType_label" style="color:gray;font-style:italic;background-color:#E5E4E2;;">
*Stop Type</label>]]></update><update id="massEntryForm:pup_repeatdatatable:0:stopType_selectonemenu"><![CDATA[<select class="none" disabled="true" id="massEntryForm:pup_repeatdatatable:0:stopType_selectonemenu" name="massEntryForm:pup_repeatdatatable:0:stopType_selectonemenu" size="1" style="color:gray;font-style:italic;background-color:#E5E4E2;;">	<option value="default">--Select an item--</option>
	<option value="R">Regular</option>
	<option value="C">Oncall</option>
	<option value="D">Dropbox</option>
	<option value="O" selected="true">Over the counter</option>
	<option value="T">Meter Oncall</option>
	<option value="M">Meter Regular</option>
</select>]]></update><update id="massEntryForm:pup_repeatdatatable:0:destCityCd_label"><![CDATA[<label for="massEntryForm:pup_repeatdatatable:0:destCityCd_inputtext" id="massEntryForm:pup_repeatdatatable:0:destCityCd_label" style="color:gray;font-style:italic;background-color:#E5E4E2;;">
*City</label>]]></update><update id="massEntryForm:pup_repeatdatatable:0:destCityCd_inputtext"><![CDATA[<input class="none" disabled="true" id="massEntryForm:pup_repeatdatatable:0:destCityCd_inputtext" maxlength="1" name="massEntryForm:pup_repeatdatatable:0:destCityCd_inputtext" size="1" style="color:gray;font-style:italic;background-color:#E5E4E2;;" type="text" value="A" />]]></update><update id="massEntryForm:pup_repeatdatatable:0:destCountryCd_label"><![CDATA[<label for="massEntryForm:pup_repeatdatatable:0:destCountryCd_inputtext" id="massEntryForm:pup_repeatdatatable:0:destCountryCd_label" style="color:gray;font-style:italic;background-color:#E5E4E2;;">
*Country</label>]]></update><update id="massEntryForm:pup_repeatdatatable:0:destCountryCd_inputtext"><![CDATA[<input class="none" disabled="true" id="massEntryForm:pup_repeatdatatable:0:destCountryCd_inputtext" maxlength="2" name="massEntryForm:pup_repeatdatatable:0:destCountryCd_inputtext" size="2" style="color:gray;font-style:italic;background-color:#E5E4E2;;" type="text" value="US" />]]></update><update id="massEntryForm:pup_repeatdatatable:0:destZipCd_label"><![CDATA[<label for="massEntryForm:pup_repeatdatatable:0:destZipCd_inputtext" id="massEntryForm:pup_repeatdatatable:0:destZipCd_label" style="color:gray;font-style:italic;background-color:#E5E4E2;;">
*Dest Postal </label>]]></update><update id="massEntryForm:pup_repeatdatatable:0:destZipCd_inputtext"><![CDATA[<input class="none" disabled="true" id="massEntryForm:pup_repeatdatatable:0:destZipCd_inputtext" maxlength="9" name="massEntryForm:pup_repeatdatatable:0:destZipCd_inputtext" size="9" style="color:gray;font-style:italic;background-color:#E5E4E2;;" type="text" value="75063" />]]></update><update id="massEntryForm:pup_repeatdatatable:0:baseSvc_label"><![CDATA[<label for="massEntryForm:pup_repeatdatatable:0:baseSvc_selectonemenu" id="massEntryForm:pup_repeatdatatable:0:baseSvc_label" style="color:gray;font-style:italic;background-color:#E5E4E2;;">
*Base Service</label>]]></update><update id="massEntryForm:pup_repeatdatatable:0:baseSvc_selectonemenu"><![CDATA[<select class="none" disabled="true" id="massEntryForm:pup_repeatdatatable:0:baseSvc_selectonemenu" name="massEntryForm:pup_repeatdatatable:0:baseSvc_selectonemenu" size="1" style="color:gray;font-style:italic;background-color:#E5E4E2;;">	<option value="default">--Select an item--</option>
	<option value="01">(01) Priority Overnight</option>
	<option value="03">(03) 2Day</option>
	<option value="05">(05) Standard Overnight</option>
	<option value="06">(06) First Overnight</option>
	<option value="20" selected="true">(20) Express Saver</option>
	<option value="39">(39) First Overnight Freight</option>
	<option value="49">(49) 2DAY AM</option>
	<option value="70">(70) 1 Day Freight</option>
	<option value="80">(80) 2 Day Freight</option>
	<option value="83">(83) 3 Day Freight</option>
</select>]]></update><update id="massEntryForm:pup_repeatdatatable:0:pkgType_label"><![CDATA[<label for="massEntryForm:pup_repeatdatatable:0:pkgType_selectonemenu" id="massEntryForm:pup_repeatdatatable:0:pkgType_label" style="color:gray;font-style:italic;background-color:#E5E4E2;;">
*Pkg Type</label>]]></update><update id="massEntryForm:pup_repeatdatatable:0:pkgType_selectonemenu"><![CDATA[<select class="none" disabled="true" id="massEntryForm:pup_repeatdatatable:0:pkgType_selectonemenu" name="massEntryForm:pup_repeatdatatable:0:pkgType_selectonemenu" size="1" style="color:gray;font-style:italic;background-color:#E5E4E2;;">	<option value="default">--Select an item--</option>
	<option value="01" selected="true">(01) Customer Packaging</option>
	<option value="02">(02) FedEx Pak</option>
	<option value="04">(04) FedEx Tube</option>
	<option value="06">(06) FedEx Envelope</option>
	<option value="13">(13) FedEx Small Box</option>
	<option value="23">(23) FedEx Medium Box</option>
	<option value="33">(33) FedEx Large Box</option>
	<option value="43">(43) FedEx Extra-Large Box</option>
</select>]]></update><update id="massEntryForm:pup_repeatdatatable:0:handlingCd_selectmanymenu"><![CDATA[<select class="selectMany" disabled="true" id="massEntryForm:pup_repeatdatatable:0:handlingCd_selectmanymenu" multiple="true" name="massEntryForm:pup_repeatdatatable:0:handlingCd_selectmanymenu" size="1" style="color:gray;font-style:italic;background-color:#E5E4E2;;">	<option value="01">(01) Hold at Location</option>
	<option value="02">(02) Deliver Weekday</option>
	<option value="31">(31) Saturday Hold At FedEx Loc</option>
	<option value="04">(04) Inaccessible Dangerous Goods</option>
	<option value="05">(05) Electronic Sig. Service</option>
	<option value="06">(06) Dry Ice</option>
	<option value="07">(07) Other Special Service</option>
	<option value="10">(10) Direct Signature Required</option>
	<option value="11">(11) Chargeable Code</option>
	<option value="16">(16) Additional Handling Service</option>
	<option value="28">(28) Resi Delivery</option>
	<option value="34">(34) Indirect Signature Required</option>
	<option value="35">(35) Adult Signature Required</option>
	<option value="36">(36) No Signature Required</option>
	<option value="55">(55) Alcohol</option>
	<option value="61">(61) Lift Gate</option>
	<option value="68">(68) Freight On Value Own Risk</option>
	<option value="69">(69) Freight On Value Carrier Risk</option>
	<option value="71">(71) Pharmacy</option>
	<option value="78">(78) Argentina Surcharge</option>
	<option value="79">(79) Argentina Surcharge</option>
	<option value="81">(81) Argentina Surcharge</option>
	<option value="82">(82) Single Shipment (Freight Only)</option>
	<option value="84">(84) Inside Pickup (Freight Only)</option>
	<option value="85">(85) Inside Delivery (Freight Only)</option>
	<option value="86">(86) Reconsignment (Freight Only)</option>
	<option value="87">(87) Marking N Tagging (Freight Only)</option>
	<option value="89">(89) Extra Labor (Freight Only)</option>
	<option value="93">(93) Excepted Lithium Battery</option>
	<option value="98">(98) Declared Value</option>
	<option value="99">(99) Shipper Reference Number</option>
</select>]]></update><update id="massEntryForm:pup_repeatdatatable:0:chargeableCd_selectmanymenu"><![CDATA[<select class="selectMany" disabled="true" id="massEntryForm:pup_repeatdatatable:0:chargeableCd_selectmanymenu" multiple="true" name="massEntryForm:pup_repeatdatatable:0:chargeableCd_selectmanymenu" size="1" style="color:gray;font-style:italic;background-color:#E5E4E2;;">	<option value="23">(23) High Floor</option>
	<option value="24">(24) Rail Mode</option>
	<option value="29">(29) Storage</option>
	<option value="38">(38) On Demand Care Dry Ice</option>
	<option value="41">(41) Inside Pickup</option>
	<option value="42">(42) Inside Delivery</option>
	<option value="43">(43) Extended Pickup</option>
	<option value="44">(44) Extended Delivery</option>
	<option value="45">(45) Single Shipment</option>
	<option value="46">(46) Reconsignment Charge</option>
	<option value="47">(47) Marking and Tagging</option>
	<option value="48">(48) Delivery Reattempt</option>
	<option value="49">(49) Extra Labor</option>
	<option value="50">(50) Residential Pickup</option>
	<option value="51">(51) Residential Delivery</option>
	<option value="52">(52) Airbill Automation</option>
	<option value="58">(58) On Demand Care Gel Packs</option>
	<option value="59">(59) On Demand Care Cold Storage</option>
	<option value="60">(60) Special Equipment</option>
	<option value="67">(67) AHS Non Stackable</option>
</select>]]></update><update id="massEntryForm:pup_repeatdatatable:0:hours_inputtext"><![CDATA[<input class="none" disabled="true" id="massEntryForm:pup_repeatdatatable:0:hours_inputtext" maxlength="2" name="massEntryForm:pup_repeatdatatable:0:hours_inputtext" size="2" style="color:gray;font-style:italic;background-color:#E5E4E2;;" type="text" value="0" />]]></update><update id="massEntryForm:pup_repeatdatatable:0:deliveryAddr_inputtext"><![CDATA[<input class="none" disabled="true" id="massEntryForm:pup_repeatdatatable:0:deliveryAddr_inputtext" maxlength="30" name="massEntryForm:pup_repeatdatatable:0:deliveryAddr_inputtext" size="30" style="color:gray;font-style:italic;background-color:#E5E4E2;;" type="text" value="" />]]></update><update id="massEntryForm:pup_repeatdatatable:0:comments_textarea_input"><![CDATA[<textarea aria-disabled="true" aria-multiline="true" class="ui-inputfield ui-textareaentry ui-widget ui-state-default ui-corner-all ui-state-optional ui-textareaentry-resizable none" cols="30" disabled="disabled" id="massEntryForm:pup_repeatdatatable:0:comments_textarea_input" name="massEntryForm:pup_repeatdatatable:0:comments_textarea_input" role="textbox" rows="3" style="color:gray;font-style:italic;background-color:#E5E4E2;;"></textarea>]]></update><update id="massEntryForm:pup_repeatdatatable:0:status"><![CDATA[<label id="massEntryForm:pup_repeatdatatable:0:status">
Processed</label>]]></update><update id="j_idt25"><![CDATA[<div id="j_idt25"></div>]]></update><update id="javax.faces.ViewState"><![CDATA[4403610302597563339:-7234975737240945987]]></update><eval><![CDATA[document.getElementById('massEntryForm:alertDialog_label').innerHTML='Scan(s) processed successfully.'; alertDialog.show();ice.applyFocus('massEntryForm:save_confirmation_yesbutton');var iceFormIdList=['massEntryForm', 'v16q1jgi6-retrieve-update', 'v16q1jgi6-single-submit']; ice.fixViewStates(iceFormIdList,'4403610302597563339:-7234975737240945987');]]></eval><extension aceCallbackParam="validationFailed">{"validationFailed":false}</extension></changes></partial-response>
			*/

			
	  		try{
	  			String level = Environment.getInstance().getLevel();
// Not sure if this will be the correct url for L2.
	  			HttpPost httppost = new HttpPost("https://testsso.secure.fedex.com/L" + level + "/Event-Entry-5539C/main.jsf");
	  			
	  			httppost.addHeader("Content-Type", "application/x-www-form-urlencoded");
	  			httppost.addHeader("Accept", "*/*");
	  			httppost.addHeader("Cookie", SessionEmassCookie);
	  			
	  			List<NameValuePair> urlParameters = new ArrayList<NameValuePair>();
	  			urlParameters.add(new BasicNameValuePair("massEntryForm", "massEntryForm"));
	  			urlParameters.add(new BasicNameValuePair("ice.window", "92kc2fnctv"));
	  			urlParameters.add(new BasicNameValuePair("ice.view", "v16q1jgi6"));
	  			urlParameters.add(new BasicNameValuePair("massEntryForm:empNbr_inputtext", "0000" + Scanning_Info_FedEx_Id));
	  			urlParameters.add(new BasicNameValuePair("massEntryForm:route_inputtext", Scanning_Info_FedEx_Route));
	  			Date curDate = new Date();
	  			SimpleDateFormat Dateformatter = new SimpleDateFormat("MMddyyyy");
	  			urlParameters.add(new BasicNameValuePair("massEntryForm:scanDate_calendar_input", Dateformatter.format(curDate)));
	  			urlParameters.add(new BasicNameValuePair("massEntryForm:pup_repeatinputpanel_rowCount_selectonemenu", "0"));
	  			urlParameters.add(new BasicNameValuePair("massEntryForm:pup_repeatdatatable:0:trkNo_inputtext", trackingNumber));
	  			urlParameters.add(new BasicNameValuePair("massEntryForm:pup_repeatdatatable:0:formCd_inputtext", Form_ID));
	  			SimpleDateFormat Timeformatter = new SimpleDateFormat("HH:mm");
	  			urlParameters.add(new BasicNameValuePair("massEntryForm:pup_repeatdatatable:0:scanTime_maskedinput_field", Timeformatter.format(curDate)));
	  			urlParameters.add(new BasicNameValuePair("massEntryForm:pup_repeatdatatable:0:cosmosNbr_inputtext", Form_ID));
	  			urlParameters.add(new BasicNameValuePair("massEntryForm:pup_repeatdatatable:0:stopType_selectonemenu", "O"));
	  			urlParameters.add(new BasicNameValuePair("massEntryForm:pup_repeatdatatable:0:destCityCd_inputtext", "A"));
	  			urlParameters.add(new BasicNameValuePair("massEntryForm:pup_repeatdatatable:0:destCountryCd_inputtext", "US"));
	  			urlParameters.add(new BasicNameValuePair("massEntryForm:pup_repeatdatatable:0:destZipCd_inputtext", "75063"));
	  			urlParameters.add(new BasicNameValuePair("massEntryForm:pup_repeatdatatable:0:baseSvc_selectonemenu", "20"));
	  			urlParameters.add(new BasicNameValuePair("massEntryForm:pup_repeatdatatable:0:pkgType_selectonemenu", "01"));
	  			urlParameters.add(new BasicNameValuePair("massEntryForm:pup_repeatdatatable:0:handlingCd_selectmanymenu", "01"));
	  			urlParameters.add(new BasicNameValuePair("massEntryForm:pup_repeatdatatable:0:hours_inputtext", "0"));
	  			urlParameters.add(new BasicNameValuePair("massEntryForm:pup_repeatdatatable:0:deliveryAddr_inputtext", ""));
	  			urlParameters.add(new BasicNameValuePair("massEntryForm:pup_repeatdatatable:0:comments_textarea_input", ""));
	  			urlParameters.add(new BasicNameValuePair("icefacesCssUpdates", ""));
	  			urlParameters.add(new BasicNameValuePair("javax.faces.ViewState", "4403610302597563339:-7234975737240945987"));
	  			urlParameters.add(new BasicNameValuePair("javax.faces.source", "massEntryForm:save_confirmation_yesbutton"));
	  			urlParameters.add(new BasicNameValuePair("javax.faces.partial.event", "click"));
	  			urlParameters.add(new BasicNameValuePair("javax.faces.partial.execute", "@all"));
	  			urlParameters.add(new BasicNameValuePair("javax.faces.partial.render", "@all"));
	  			urlParameters.add(new BasicNameValuePair("ice.window", "92kc2fnctv"));
	  			urlParameters.add(new BasicNameValuePair("ice.view", "v16q1jgi6"));
	  			urlParameters.add(new BasicNameValuePair("ice.focus", "massEntryForm:save_confirmation_yesbutton"));
	  			urlParameters.add(new BasicNameValuePair("massEntryForm:save_confirmation_yesbutton", "Yes"));
	  			urlParameters.add(new BasicNameValuePair("ice.event.target", "massEntryForm:save_confirmation_yesbutton"));
	  			urlParameters.add(new BasicNameValuePair("ice.event.captured", "massEntryForm:save_confirmation_yesbutton"));
	  			urlParameters.add(new BasicNameValuePair("ice.event.type", "onclick"));
	  			urlParameters.add(new BasicNameValuePair("ice.event.alt", "false"));
	  			urlParameters.add(new BasicNameValuePair("ice.event.ctrl", "false"));
	  			urlParameters.add(new BasicNameValuePair("ice.event.shift", "false"));
	  			urlParameters.add(new BasicNameValuePair("ice.event.meta", "false"));
	  			urlParameters.add(new BasicNameValuePair("ice.event.x", "570"));
	  			urlParameters.add(new BasicNameValuePair("ice.event.y", "550.6666564941406"));
	  			urlParameters.add(new BasicNameValuePair("ice.event.left", "false"));
	  			urlParameters.add(new BasicNameValuePair("ice.event.right", "false"));
	  			urlParameters.add(new BasicNameValuePair("ice.submit.type", "ice.s"));
	  			urlParameters.add(new BasicNameValuePair("ice.submit.serialization", "form"));
	  			urlParameters.add(new BasicNameValuePair("javax.faces.partial.ajax", "true"));


	  			httppost.setEntity(new UrlEncodedFormEntity(urlParameters));

	  			String Response = General_API_Calls.HTTPCall(httppost, "");	
	  			if (Response.contains("<extension aceCallbackParam=\"validationFailed\">{\"validationFailed\":false}</extension>")) {
	  				return true;
	  			}

	  		}catch (Exception e){
	  			e.printStackTrace();
	  		}
		}catch (Exception e){
			throw e;
		}
		return false;
	}
	
	
}
