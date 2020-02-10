package JAR;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;
import Data_Structures.Address_Data;
import Data_Structures.Shipment_Data;
import Data_Structures.User_Data;
import INET_Application.eMASS_Scans;
import SupportClasses.Environment;
import SupportClasses.Helper_Functions;
import Test_Data_Update.Tracking_Data_Update;

public class Create_Shipment_Main {
	static String Level = "2";
	static int numberOfAttempts = 5;
	
	@BeforeClass
	public void beforeClass() {
		Environment.SetLevelsToTest(Level);
	}
	
	@DataProvider (parallel = true)
	public static Iterator<Object[]> dp(Method m) {
		List<Object[]> data = new ArrayList<Object[]>();
		Environment.getInstance().setLevel(Level);
		
		User_Data User_Info = new User_Data();

		User_Info.USER_ID = "L2ATRK";
		User_Info.PASSWORD = "Test1234";
		User_Info = Test_Data_Update.TestData_Update.Update_User_Data_Information(Level, User_Info);
		
		Shipment_Data Shipment_Info = new Shipment_Data();
		Shipment_Info.setUser_Info(User_Info);
			    			
		Address_Data Address_Info1 = Address_Data.getAddress(Level, "CA", null);
		Shipment_Info.setOrigin_Address_Info(Address_Info1);
		    				
		Shipment_Info.setDestination_Address_Info(User_Info.getFDMregisteredAddress());
		if (Shipment_Info.Destination_Address_Info == null) {
			 Helper_Functions.PrintOut("User is not registered as FDM");
			 Shipment_Info.setDestination_Address_Info(User_Info.Address_Info);
		} else {
			// When using FDM address update the name to be same as FDM listed name
			Shipment_Info.User_Info.FIRST_NM = Shipment_Info.Destination_Address_Info.First_Name;
			Shipment_Info.User_Info.LAST_NM = Shipment_Info.Destination_Address_Info.Last_Name;
		}
			    			
		Shipment_Info.setService("FedEx Express Saver");
			    			
		data.add( new Object[] {Level, Shipment_Info, numberOfAttempts});
		
		SupportClasses.Helper_Functions.LimitDataProvider(m.getName(), -1, data);
		
		return data.iterator();
	}
	
	@Test(dataProvider = "dp", enabled=true)
	public static void SHPC_Create_Shipment(String Level, Shipment_Data Shipment_Info, int Attempts){
		try {
			Environment.getInstance().setLevel(Level);
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
			
		}catch (Exception e) {
			e.printStackTrace();
 			Assert.fail(e.getCause().toString());
		}
	}
}