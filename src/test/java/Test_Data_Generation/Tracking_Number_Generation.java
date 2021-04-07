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
	static String LevelsToTest = "2";
	static int numberOfAttempts = 10;

	@BeforeClass
	public void beforeClass() {
		Environment.SetLevelsToTest(LevelsToTest);
	}

	@DataProvider (parallel = true)
	public static Iterator<Object[]> dp(Method m) {
		List<Object[]> data = new ArrayList<Object[]>();

		for (int i = 0; i < Environment.LevelsToTest.length(); i++) {
			String Level = String.valueOf(Environment.LevelsToTest.charAt(i));
			int intLevel = Integer.parseInt(Level);
			User_Data userInfoArray[] = User_Data.Get_UserIds(intLevel);
			// Based on the method that is being called the array list will be populated
			switch (m.getName()) {
			case "SHPC_Create_Shipment":
				for (User_Data User_Info : userInfoArray) {
					if (User_Info.getHasValidAccountNumber()
							&& User_Info.getCanScheduleShipment()
							&& User_Info.getFDMregisteredAddress() != null
							// && User_Info.USER_ID.contentEquals("L3FDM171713321")
							) {
						/* if (User_Info.USER_ID.contentEquals("RG101918")) { */

						Shipment_Data Shipment_Info = new Shipment_Data();
						Shipment_Info.setUser_Info(User_Info);

						Address_Data Address_Info1 = Address_Data.getAddress(Level, "US", "10 FED EX PKWY");
						Shipment_Info.setOrigin_Address_Info(Address_Info1);
						
						// Address_Data Address_Info2 = Address_Data.getAddress(Level, "US", "10 FED EX PKWY");
						// Address_Data Address_Info2 = Address_Data.getAddress(Level, "CA", "50 Barnes Rd");
						// Shipment_Info.setDestination_Address_Info(Address_Info2);
						Shipment_Info.setOrigin_Address_Info(User_Info.getFDMregisteredAddress());
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

						boolean singleUser = true;
						if (singleUser) {
							data.add(new Object[] { Level, Shipment_Info, numberOfAttempts });
							break; // will only try with first user found.
						} else {
							data.add(new Object[] { Level, Shipment_Info, 1 });
							if (data.size() >= numberOfAttempts) {
								break;
							}
						}

					}
				} // end loop through users
				break;
			}
		}

		SupportClasses.Helper_Functions.LimitDataProvider(m.getName(), -1, data);

		return data.iterator();
	}

	@Test(dataProvider = "dp")
	public static void SHPC_Create_Shipment(String Level, Shipment_Data Shipment_Info, int Attempts) {
		try {
			Shipment_Info.PrintOutShipmentDetails();
			String LoginValues[] = USRC.login.Login(Shipment_Info.User_Info.USER_ID, Shipment_Info.User_Info.PASSWORD);
			String Cookies = LoginValues[0];

			String TrackingNumbers[] = {};

			while (Attempts > 0) {
				// Helper_Functions.PrintOut("User " + Shipment_Info.User_Info.USER_ID + " about
				// to create shipment.");
				Shipment_Info.Tracking_Number = SHPC.create_shipment.createTrackingNumber(Shipment_Info, Cookies);

				if (Shipment_Info.Tracking_Number != null) {
					// update the tracking number.
					Tracking_Data_Update.Tracking_Number_To_File(Level, Shipment_Info.Tracking_Number,
							Shipment_Info.User_Info.USER_ID, Shipment_Info.User_Info.PASSWORD);
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
				Helper_Functions.PrintOut(String.format("%s tracking numbers have been created. %s",
						Arrays.toString(TrackingNumbers), Shipment_Info.User_Info.USER_ID));
				eMASS_Scans.eMASS_Multiple_Pickup_Scan(Shipment_Info, TrackingNumbers);
			}

		} catch (Exception e) {
			e.printStackTrace();
			Assert.fail(e.getCause().toString());
		}
	}
}