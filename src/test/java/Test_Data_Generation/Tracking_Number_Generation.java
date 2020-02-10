package Test_Data_Generation;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Listeners;
import org.testng.annotations.Test;
import Data_Structures.Address_Data;
import Data_Structures.Shipment_Data;
import Data_Structures.User_Data;
import INET_Application.GroundCorpLoad;
import INET_Application.eMASS_Scans;
import SupportClasses.Environment;
import SupportClasses.Helper_Functions;
import Test_Data_Update.Tracking_Data_Update;

@Listeners(SupportClasses.TestNG_TestListener.class)

public class Tracking_Number_Generation {
	static String LevelsToTest = "3";
	static int numberOfAttempts = 10;
	static boolean successfulTrackingCreation = false;
	
	@BeforeClass
	public void beforeClass() {
		Environment.SetLevelsToTest(LevelsToTest);
	}
	
	@DataProvider  (parallel = true)
	public static Iterator<Object[]> dp(Method m) {
		List<Object[]> data = new ArrayList<Object[]>();
		
		for (int i = 0; i < Environment.LevelsToTest.length(); i++) {
			String Level = String.valueOf(Environment.LevelsToTest.charAt(i));
			int intLevel = Integer.parseInt(Level);
			User_Data userInfoArray[] = User_Data.Get_UserIds(intLevel);
			//Based on the method that is being called the array list will be populated
			switch (m.getName()) {
		    	case "INET_Create_Shipment":
		    		for (User_Data User_Info : userInfoArray) {
		    			//check for a WADM users
		    			if (User_Info.getHasValidAccountNumber() 
		    				&& User_Info.getCanScheduleShipment()
		    				&& User_Info.getFDMregisteredAddress() != null) {
		    			/*if (User_Info.USER_ID.contentEquals("L3FDM171713321")) {*/

			    			Shipment_Data Shipment_Info = new Shipment_Data();
			    			Shipment_Info.setUser_Info(User_Info);
			    			/*String AddressLineOne = "58 CABOT ST";
			    			Account_Data Account_Info = Environment.getAddressDetails(Level, "US", AddressLineOne);*/
			    				
			    			Shipment_Info.setOrigin_Address_Info(User_Info.Address_Info);
			    			Shipment_Info.setDestination_Address_Info(User_Info.getFDMregisteredAddress());
			    			if (Shipment_Info.Destination_Address_Info == null) {
			    				Shipment_Info.setDestination_Address_Info(User_Info.Address_Info);
			    			} else {
			    				// When using FDM address update the name to be same as FDM listed name
			    				Shipment_Info.User_Info.FIRST_NM = Shipment_Info.Destination_Address_Info.First_Name;
			    				Shipment_Info.User_Info.LAST_NM = Shipment_Info.Destination_Address_Info.Last_Name;
			    			}
			    			
			    			Shipment_Info.setService("FedEx Express Saver");
			    			
			    			// Use for creating return shipment tracking numbers.
			    			// Shipment_Info.Shipment_Method = "doNewReturnShipment";
			    			
			    			// Shipment_Info.setService("Ground");
			    			while (numberOfAttempts > 0) {
			    				Shipment_Data Cloned_Shipment = (Shipment_Data) Shipment_Info.clone();
			    				data.add( new Object[] {Level, Cloned_Shipment});
			    				numberOfAttempts--;
			    			}
		    			}
		    		}
		    	break;
		    	case "SHPC_Create_Shipment":
		    		for (User_Data User_Info : userInfoArray) {
		    			if (User_Info.getHasValidAccountNumber() 
		    				&& User_Info.getCanScheduleShipment() 
		    				&& User_Info.getFDMregisteredAddress() != null
		    				) {
		    			/*if (User_Info.USER_ID.contentEquals("L3FDM171713321")) {*/

			    			Shipment_Data Shipment_Info = new Shipment_Data();
			    			Shipment_Info.setUser_Info(User_Info);
			    			
		    				Address_Data Address_Info1 = Address_Data.getAddress(Level, "CA", null);
		    				Shipment_Info.setOrigin_Address_Info(Address_Info1);
		    				
			    			Shipment_Info.setDestination_Address_Info(User_Info.getFDMregisteredAddress());
			    			if (Shipment_Info.Destination_Address_Info == null) {
			    				Shipment_Info.setDestination_Address_Info(User_Info.Address_Info);
			    			} else {
			    				// When using FDM address update the name to be same as FDM listed name
			    				Shipment_Info.User_Info.FIRST_NM = Shipment_Info.Destination_Address_Info.First_Name;
			    				Shipment_Info.User_Info.LAST_NM = Shipment_Info.Destination_Address_Info.Last_Name;
			    			}
			    			
			    			Shipment_Info.setService("FedEx Express Saver");
			    			
			    			// Use for creating return shipment tracking numbers.
			    			// Shipment_Info.Shipment_Method = "doNewReturnShipment";
			    			
			    			data.add( new Object[] {Level, Shipment_Info, numberOfAttempts});
			    			break; // will only try with first user found.
		    			}
		    		}
		    	break;
			}
		}
		
		SupportClasses.Helper_Functions.LimitDataProvider(m.getName(), -1, data);
		
		return data.iterator();
	}

	@Test(dataProvider = "dp", enabled = true)   // true  false
	public static void INET_Create_Shipment(String Level, Shipment_Data Shipment_Info){
		try {
			Shipment_Info.PrintOutShipmentDetails();
			Shipment_Info = INET_Application.INET_Shipment.INET_Create_Shipment(Shipment_Info);
			// update the tracking number.
			Tracking_Data_Update.Tracking_Number_Update(Level, Shipment_Info.Tracking_Number, Shipment_Info.TRACKING_QUALIFIER, Shipment_Info.User_Info.USER_ID, Shipment_Info.User_Info.PASSWORD);
			// Shipment_Info.PrintOutShipmentDetails();
			eMASS_Scans.eMASS_Pickup_Scan(Shipment_Info);
			
			if (Shipment_Info.Service.toLowerCase().contains("ground")) {
				String Tracking[] = new String[] { Shipment_Info.Tracking_Number };
				GroundCorpLoad.ValidateAndProcess(Tracking);
			}

			// Tracking_Data_Update.Tracking_Number_Update(Level, Shipment_Info.Tracking_Number, Shipment_Info.TRACKING_QUALIFIER, Shipment_Info.User_Info.USER_ID, Shipment_Info.User_Info.PASSWORD);
		
		}catch (Exception e) {
			e.printStackTrace();
 			Assert.fail(e.getCause().toString());
		}
	}
	
	@Test(dataProvider = "dp", enabled=false)   // true  false
	public static void SHPC_Create_Shipment(String Level, Shipment_Data Shipment_Info, int Attempts){
		try {
			Shipment_Info.PrintOutShipmentDetails();
			String LoginValues[] = USRC.login.Login(Shipment_Info.User_Info.USER_ID, Shipment_Info.User_Info.PASSWORD);
			String Cookies = LoginValues[0];
			
			String TrackingNumbers[] = {};
			
			while(Attempts > 0) {
				Shipment_Info.Tracking_Number = SHPC.create_shipment.createTrackingNumber(Shipment_Info, Cookies);
				
				if (Shipment_Info.Tracking_Number != null) {
					// update the tracking number.
					Tracking_Data_Update.Tracking_Number_To_File(Level, Shipment_Info.Tracking_Number, Shipment_Info.User_Info.USER_ID, Shipment_Info.User_Info.PASSWORD);
					TrackingNumbers = Arrays.copyOf(TrackingNumbers, TrackingNumbers.length + 1);
					TrackingNumbers[TrackingNumbers.length - 1] = Shipment_Info.Tracking_Number;
				} else {
					Helper_Functions.PrintOut("Unable to create tracking number.");
				}
				Attempts--;
			}
			
			if (TrackingNumbers.length == 0) {
				Assert.fail("Unable to create any shipments.");
			} else {
				Helper_Functions.PrintOut(String.format("%s tracking numbers have been created. %s", Arrays.toString(TrackingNumbers), Shipment_Info.User_Info.USER_ID));
				eMASS_Scans.eMASS_Multiple_Pickup_Scan(Shipment_Info, TrackingNumbers);
			}
			successfulTrackingCreation = true;
		}catch (Exception e) {
			e.printStackTrace();
 			Assert.fail(e.getCause().toString());
		}
	}
}