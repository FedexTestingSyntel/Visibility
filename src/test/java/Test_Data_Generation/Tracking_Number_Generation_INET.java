package Test_Data_Generation;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.AfterClass;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Listeners;
import org.testng.annotations.Test;

import Data_Structures.Address_Data;
// import Data_Structures.Address_Data;
import Data_Structures.Shipment_Data;
import Data_Structures.User_Data;
// import INET_Application.GroundCorpLoad;
import INET_Application.eMASS_Scans;
import SupportClasses.Environment;
import SupportClasses.Helper_Functions;
import Test_Data_Update.Tracking_Data_Update;

@Listeners(SupportClasses.TestNG_TestListener.class)

public class Tracking_Number_Generation_INET {
	static String LevelsToTest = "2";
	static int numberOfAttempts = 16;
	static boolean singleUser = true;
	static boolean successfulTrackingCreation = false;
	static CopyOnWriteArrayList<String[]> TrackingData = new CopyOnWriteArrayList<String[]>(); 

	@BeforeClass
	public void beforeClass() {
		Environment.SetLevelsToTest(LevelsToTest);
		Helper_Functions.TestingData = Helper_Functions.DataDirectory + "\\TestingDataFullList.xls";
	}
	
	@AfterClass
	public void afterClass() {
		for (int j = 0 ; j < TrackingData.size(); j++) {
			Helper_Functions.PrintOut(Arrays.toString(TrackingData.get(j)));
		}
	}

	@DataProvider //(parallel = true)
	public static Iterator<Object[]> dp(Method m) {
		List<Object[]> data = new ArrayList<Object[]>();

		for (int i = 0; i < Environment.LevelsToTest.length(); i++) {
			String Level = String.valueOf(Environment.LevelsToTest.charAt(i));
			int intLevel = Integer.parseInt(Level);
			User_Data userInfoArray[] = User_Data.Get_UserIds(intLevel);
			// Based on the method that is being called the array list will be populated
			switch (m.getName()) {
			case "INET_Create_Shipment":
				for (User_Data User_Info : userInfoArray) {
					// check for a WADM users
					if (User_Info.getHasValidAccountNumber()
							&& User_Info.getCanScheduleShipment()
							&& User_Info.getFDMregisteredAddress() != null
							// && User_Info.USER_ID.contentEquals("L2ATRK")
							) {
						/*if (User_Info.USER_ID.contentEquals("L3TESTUSER")) {*/

					

						
						Shipment_Data Shipment_Info = new Shipment_Data();
						Shipment_Info.setUser_Info(User_Info);

						Shipment_Info.setOrigin_Address_Info(User_Info.Address_Info);
						
						// Shipment_Info.setOrigin_Address_Info( Address_Data.getAddress(Level, "CA", ""));
						// Address_Data Address_Info1 = Address_Data.getAddress(Level, "DE", "");
						// Shipment_Info.setOrigin_Address_Info(Address_Info1);

						// Need to fix this to get correct FDM address
						// TODO
						// Shipment_Info.setDestination_Address_Info(User_Info.Address_Info);
						Shipment_Info.setDestination_Address_Info(User_Info.getFDMregisteredAddress());
						
						if (Shipment_Info.Destination_Address_Info == null) {
							Helper_Functions.PrintOut("Warning, not able to load FDM destination address.");
							Shipment_Info.setDestination_Address_Info(User_Info.Address_Info);
						} else {
							// set origin same as FDM address
							Shipment_Info.setOrigin_Address_Info(User_Info.getFDMregisteredAddress());
							// When using FDM address update the name to be same as FDM listed name
							Shipment_Info.User_Info.FIRST_NM = Shipment_Info.Destination_Address_Info.First_Name;
							Shipment_Info.User_Info.LAST_NM = Shipment_Info.Destination_Address_Info.Last_Name;
						}

						Shipment_Info.setService("FedEx Express Saver");

						// Use for creating return shipment tracking numbers.
						// Shipment_Info.Shipment_Method = "doNewReturnShipment";
						// Shipment_Info.setService("Ground");
						
						/*while (numberOfAttempts > 0) {
							Shipment_Data Cloned_Shipment = (Shipment_Data) Shipment_Info.clone();
							data.add(new Object[] { Level, Cloned_Shipment });
							numberOfAttempts--;
						}*/
						
						if (singleUser) {
							while (data.size() <= numberOfAttempts) {
								Shipment_Data Cloned_Shipment = (Shipment_Data) Shipment_Info.clone();
								data.add(new Object[] { Level, Cloned_Shipment });
							}
							break; // will only try with first user found.
						} else {
							Shipment_Data Cloned_Shipment = (Shipment_Data) Shipment_Info.clone();
							data.add(new Object[] { Level, Cloned_Shipment });
							if (data.size() >= numberOfAttempts) {
								break;
							}
						}
					}
				}
				break;
			}
		}

		SupportClasses.Helper_Functions.LimitDataProvider(m.getName(), -1, data);

		return data.iterator();
	}

	@Test(dataProvider = "dp")
	public static void INET_Create_Shipment(String Level, Shipment_Data Shipment_Info) {
		try {
			Shipment_Info.PrintOutShipmentDetails();
			Shipment_Info = INET_Application.INET_Shipment.INET_Create_Shipment(Shipment_Info);
			// update the tracking number.
			Tracking_Data_Update.Tracking_Number_Update(Level, Shipment_Info.Tracking_Number,
					Shipment_Info.TRACKING_QUALIFIER, Shipment_Info.User_Info.USER_ID,
					Shipment_Info.User_Info.PASSWORD);
			
			// Shipment_Info.PrintOutShipmentDetails();
			eMASS_Scans.eMASS_Pickup_Scan(Shipment_Info);
			
			// Tracking_Data_Update.Tracking_Number_Update(Level,
			// Shipment_Info.Tracking_Number, Shipment_Info.TRACKING_QUALIFIER,
			// Shipment_Info.User_Info.USER_ID, Shipment_Info.User_Info.PASSWORD);
			String shipment[] = new String[] {"L"+Level, Shipment_Info.Tracking_Number, Shipment_Info.Service, Shipment_Info.User_Info.USER_ID, Shipment_Info.User_Info.PASSWORD};
			TrackingData.add(shipment);
		} catch (Exception e) {
			e.printStackTrace();
			Assert.fail(e.getCause().toString());
		}
	}

}