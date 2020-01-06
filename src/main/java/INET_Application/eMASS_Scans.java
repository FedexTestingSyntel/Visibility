package INET_Application;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import org.openqa.selenium.By;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Listeners;
import org.testng.annotations.Test;
import Data_Structures.Account_Data;
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
			
			// Enter from id
			WebDriver_Functions.Type(By.id(MT + ":formCd_inputtext"), Form_ID);
			
			// Enter Cosmos ID
			if (WebDriver_Functions.isEnabled(By.id(MT + ":cosmosNbr_inputtext"))) {
				WebDriver_Functions.Type(By.id(MT + ":cosmosNbr_inputtext"), CosmosNumber);
			}
			
			// if the destination address was not passed then load a dummy value for the pickup scan.
			if (Shipment_Info.Destination_Address_Info == null || 
					Shipment_Info.Destination_Address_Info.City.contentEquals("")) {
				Account_Data Account_Info = Environment.getAddressDetails(Level, "US");
				Shipment_Info.setDestination_Address_Info(Account_Info.Billing_Address_Info);
			}

			// Due to EMASS glitch     Enter first digit of city, country, and postal again
			for (int addressLoop = 0;addressLoop < 4; addressLoop++) {
				int valuesEnteredCorrectly = 0;
				// Enter first digit of city
				valuesEnteredCorrectly += TypeAndCheck(By.id(MT + ":destCityCd_inputtext"), Shipment_Info.Destination_Address_Info.City.substring(0, 1).toUpperCase());
					
				// Enter country
				valuesEnteredCorrectly += TypeAndCheck(By.id(MT + ":destCountryCd_inputtext"), Shipment_Info.Destination_Address_Info.Country_Code);
					
				// Enter destination postal code
				valuesEnteredCorrectly += TypeAndCheck(By.id(MT + ":destZipCd_inputtext"), Shipment_Info.Destination_Address_Info.PostalCode);
					
				// Click base service
				WebDriver_Functions.Click(By.id(MT + ":baseSvc_selectonemenu"));
				
				WaitForInProgressOverlay();
				
				if (valuesEnteredCorrectly == 3) {
					// break from the loop if all were updated successfully.
					addressLoop = 4;
				}else {
					addressLoop++;
				}
			}
		
			// Click base service  
			WebDriver_Functions.WaitForTextPresentIn(By.id(MT + ":baseSvc_selectonemenu"), "Express Saver");
			// (20) Express Saver
			WebDriver_Functions.Select(By.id(MT + ":baseSvc_selectonemenu"), "20", "v");
			WaitForInProgressOverlay();
			// come back and fix this so the correct service selected
			//WebDriver_Functions.Select(By.id(MT + ":baseSvc_selectonemenu"), Shipment_Info.Service, "t");
			// Package type (01) Customer Packaging
			
			WebDriver_Functions.Select(By.id(MT + ":pkgType_selectonemenu"), "01", "v");

			// Delivery
			WebDriver_Functions.Click(By.xpath("//*[@id='massEntryForm:pup_repeatdatatable:0:handlingCd_selectmanymenu']/option[2]"));
			WaitForInProgressOverlay();
			//WebDriver_Functions.Select(By.id(MT + ":handlingCd_selectmanymenu"), "02", "v");
			
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
			
				// Enter form id
				TypeAndCheck(By.id(TblRow + ":formCd_inputtext"), Form_ID);
					
				// Click city just to move focus and get page to load.
				WebDriver_Functions.Click(By.id(TblRow + ":destCityCd_inputtext"));
				WaitForInProgressOverlay();

				// Due to EMASS glitch     Enter first digit of city, country, and postal again
				for (int addressLoop = 0; addressLoop < 3; addressLoop++) {
					// Enter first digit of city
					String city = Shipment_Info.Destination_Address_Info.City.substring(0, 1).toUpperCase();
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
					
					addressLoop++;
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
	
	public static int TypeAndCheck(By Ele, String Input) {
		try {
			// enter value for blank field.
			if (WebDriver_Functions.GetText(Ele).contentEquals("") && WebDriver_Functions.GetValue(Ele).contentEquals("")) {
				WebDriver_Functions.Type(Ele, Input);
			}
			
			if (!WebDriver_Functions.GetText(Ele).contentEquals(Input) && !WebDriver_Functions.GetValue(Ele).contentEquals(Input)) {
				Helper_Functions.PrintOut(String.format("Expected: %s Currentl Text: %s Current Value: %s", Input, WebDriver_Functions.GetText(Ele), WebDriver_Functions.GetValue(Ele)));
				return 0;
			} else {
				return 1;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return 0;
	}
	
}
