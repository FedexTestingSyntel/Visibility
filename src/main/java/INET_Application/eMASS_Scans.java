package INET_Application;

import java.lang.reflect.Method;
import java.util.ArrayList;
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
	
	@DataProvider (parallel = true)
	public static Iterator<Object[]> dp(Method m) {
		List<Object[]> data = new ArrayList<Object[]>();
		
		for (int i=0; i < Environment.LevelsToTest.length(); i++) {
			String Level = String.valueOf(Environment.LevelsToTest.charAt(i));
			//int intLevel = Integer.parseInt(Level);
			//Based on the method that is being called the array list will be populated
			switch (m.getName()) {
		    	case "eMASSTest":
		    		Shipment_Data Shipment_Info_Array[] = Data_Structures.Shipment_Data.getTrackingDetails(Level);
		    		for (Shipment_Data Shipment_Info: Shipment_Info_Array) {
		    			if (Shipment_Info.Status.contentEquals("LABEL_CREATED") ||
		    					Shipment_Info.Status.contentEquals("UNKNOWN")) {
		    				Account_Data Account_Info = Environment.getAddressDetails(Level, "US");
		    				Shipment_Info.setDestination_Address_Info(Account_Info.Billing_Address_Info);
		    				data.add(new Object[] {Level, Shipment_Info});
		    			}
		    		}
		    	break;
			}
		}
		return data.iterator();
	}
	
	@Test (dataProvider = "dp")
	public static void eMASSTest(String Level, Shipment_Data Shipment_Info){
		try {
			eMASS_Scans.eMASS_Pickup_Scan(Shipment_Info);
		}catch (Exception e) {
			e.printStackTrace();
			Assert.fail(e.getCause().toString());
		}
	}
	
	public static boolean eMASS_Pickup_Scan(Shipment_Data Shipment_Info) throws Exception{
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
			
			// Click the Pickup option from header
			WebDriver_Functions.Click(By.linkText("Pickup"));
			WebDriver_Functions.Click(By.linkText("PUP - Package Pick Up"));

			// Enter the scanning info
			WaitForInProgressOverlay();
			WebDriver_Functions.Type(By.id("massEntryForm:empNbr_inputtext"), Scanning_Info_FedEx_Id);
			WebDriver_Functions.Click(By.id("massEntryForm:route_inputtext"));
			WaitForInProgressOverlay();
			WebDriver_Functions.Type(By.id("massEntryForm:route_inputtext"), Scanning_Info_FedEx_Route);
			WebDriver_Functions.Click(By.id(MT + ":trkNo_inputtext"));
			WaitForInProgressOverlay();
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
			WaitForInProgressOverlay();
			WebDriver_Functions.Click(By.id("massEntryForm:alertDialog_okbutton"));
			
			Shipment_Info.writeShipment_Data_To_Excel(false);
			
			return true;
		}catch (Exception e){
			throw e;
		}
	}
	
	public static boolean eMASS_Exception_Scan(Shipment_Data Shipment_Info) throws Exception{
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
			
			// Click the Pickup option from header
			WebDriver_Functions.Click(By.linkText("Delivery"));
			WebDriver_Functions.Click(By.linkText("DEX - Delivery Attempt"));
			WebDriver_Functions.Click(By.linkText("03 - Incorrect Address"));

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
			
			Shipment_Info.writeShipment_Data_To_Excel(false);
			
			return true;
		}catch (Exception e){
			throw e;
		}
	}
	
	public static void WaitForInProgressOverlay() throws Exception {
		WebDriver_Functions.Wait(1);
		//WebDriver_Functions.WaitPresent(By.className("ice-sub-mon-txt"));
		WebDriver_Functions.WaitNotVisable(By.className("massEntryForm:submitMonitor_clone"));
		//Helper_Functions.PrintOut("ele not present");
	}
}
