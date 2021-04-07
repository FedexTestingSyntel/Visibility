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
import Data_Structures.Shipment_Data;
import Data_Structures.User_Data;
import INET_Application.GroundCorpLoad;
import INET_Application.eMASS_Scans;
import SupportClasses.Environment;
import SupportClasses.Helper_Functions;
import Test_Data_Update.Tracking_Data_Update;

@Listeners(SupportClasses.TestNG_TestListener.class)

public class Tracking_Number_Generation_INET_Ground {
	static String LevelsToTest = "3";
	static int numberOfAttempts = 8;
	static boolean successfulTrackingCreation = false;
	static CopyOnWriteArrayList<String[]> TrackingData = new CopyOnWriteArrayList<String[]>(); 

	@BeforeClass
	public void beforeClass() {
		Environment.SetLevelsToTest(LevelsToTest);
		Helper_Functions.TestingData = Helper_Functions.DataDirectory + "\\TestingDataFullList.xls";
	}
	
	@AfterClass
	public void afterClass() {

		ArrayList<String> Tracking = new ArrayList<String>();
		ArrayList<String> TestingIds = new ArrayList<String>();
		for (int j = 0 ; j < TrackingData.size(); j++) {
			Helper_Functions.PrintOut(Arrays.toString(TrackingData.get(j)));
			// hard coded in test case
			Tracking.add(TrackingData.get(j)[1]);
			Tracking.add(TrackingData.get(j)[2]);
		}
		GroundCorpLoad.ValidateAndProcess(Tracking, TestingIds);
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
			case "INET_Create_Shipment_Ground":
				for (User_Data User_Info : userInfoArray) {
					// check for a WADM users
					/*if (User_Info.getHasValidAccountNumber() && User_Info.getCanScheduleShipment()
							&& User_Info.getFDMregisteredAddress() != null
							) {*/
					if (User_Info.USER_ID.contentEquals("RG101918")) {
						Shipment_Data Shipment_Info = new Shipment_Data();
						Shipment_Info.setUser_Info(User_Info);
						// need to set specific data based on ground final file test case.
						User_Info.Address_Info.Address_Line_1 = "8505 NAIL ROAD";
						User_Info.Address_Info.Address_Line_2 = "";
						User_Info.Address_Info.City = "OLIVE BRANCH";
						User_Info.Address_Info.State_Code = "MS";
						User_Info.Address_Info.PostalCode = "38654";
						User_Info.Address_Info.Country_Code = "US";
						User_Info.Address_Info.First_Name = "origin";
						User_Info.Address_Info.Last_Name = "name";
						Shipment_Info.setOrigin_Address_Info(User_Info.Address_Info);
						User_Info.Address_Info.First_Name = "destination";
						Shipment_Info.setDestination_Address_Info(User_Info.Address_Info);


						Shipment_Info.setService("Ground");
						
						boolean singleUser = true;
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
			case "INET_Create_Shipment_Home_Delivery":
				for (User_Data User_Info : userInfoArray) {
					if (User_Info.getHasValidAccountNumber() && User_Info.getCanScheduleShipment()
							&& User_Info.getFDMregisteredAddress() != null
							)  {
						Shipment_Data Shipment_Info = new Shipment_Data();
						Shipment_Info.setUser_Info(User_Info);
			
						// need to set specific data based on ground final file test case.
						User_Info.Address_Info.Address_Line_1 = "3410 41st Ave";
						User_Info.Address_Info.Address_Line_2 = "";
						User_Info.Address_Info.City = "BRENTWOOD";
						User_Info.Address_Info.State_Code = "MD";
						User_Info.Address_Info.PostalCode = "20722";
						User_Info.Address_Info.Country_Code = "US";
						Shipment_Info.setDestination_Address_Info(User_Info.Address_Info);
						Shipment_Info.setOrigin_Address_Info(User_Info.Address_Info);
						
						Shipment_Info.setService("FedEx Home Delivery");
						
						Shipment_Info.pieceShipment = 2;
						
						// add shipment to list to be added to final file at end
						Shipment_Info.GROUND_TEST_ID = "BRENTMEM-HD";
						
						while (data.size() <= numberOfAttempts) {
							Shipment_Data Cloned_Shipment = (Shipment_Data) Shipment_Info.clone();
							data.add(new Object[] { Level, Cloned_Shipment });
						}
					}
				}
				break;
			}
		}

		SupportClasses.Helper_Functions.LimitDataProvider(m.getName(), -1, data);

		return data.iterator();
	}

	@Test(dataProvider = "dp", enabled=true)
	public static void INET_Create_Shipment_Ground(String Level, Shipment_Data Shipment_Info) {
		try {
			Shipment_Info.PrintOutShipmentDetails();
			Shipment_Info = INET_Application.INET_Shipment.INET_Create_Shipment(Shipment_Info);
			
			// ground tracking numbers need to be added to the fusion final file.
			// NOTE: Doesn't look like shipment appears immediately, so can't apply final file, ma
			// String Tracking[] = new String[] { Shipment_Info.Tracking_Number};
			// GroundCorpLoad.ValidateAndProcess(Tracking, "BRENTMEM");
			
			String shipment[] = new String[] {"L"+Level, Shipment_Info.Tracking_Number, Shipment_Info.Service, Shipment_Info.User_Info.USER_ID, Shipment_Info.User_Info.PASSWORD};
			
			String Details[][] = new String[][] {
				{ "TRACKING_NUMBER", Shipment_Info.Tracking_Number },
				{ "USER_ID", Shipment_Info.User_Info.USER_ID },
				{ "PASSWORD", Shipment_Info.User_Info.PASSWORD }, 
				{ "SERVICE", Shipment_Info.Service},
				{ "GROUND_TEST_ID", "BRENTMEM"}
				};
			Tracking_Data_Update.updateFile(Details, 0);
			// update the tracking number.
			// Tracking_Data_Update.Tracking_Number_Update(Level, Shipment_Info.Service, Shipment_Info.Tracking_Number,Shipment_Info.TRACKING_QUALIFIER, Shipment_Info.User_Info.USER_ID,Shipment_Info.User_Info.PASSWORD);
			
			TrackingData.add(shipment);
		} catch (Exception e) {
			e.printStackTrace();
			Assert.fail(e.getCause().toString());
		}
	}

	@Test(dataProvider = "dp", enabled=false)
	public static void INET_Create_Shipment_Home_Delivery(String Level, Shipment_Data Shipment_Info) {
		try {
			Shipment_Info.PrintOutShipmentDetails();
			Shipment_Info = INET_Application.INET_Shipment.INET_Create_Shipment(Shipment_Info);
						
			String shipment[] = new String[] {
					"L"+Level,
					Shipment_Info.Tracking_Number, // position used in after class
					Shipment_Info.GROUND_TEST_ID, // position used in after class
					Shipment_Info.Service,
					Shipment_Info.User_Info.USER_ID,
					Shipment_Info.User_Info.PASSWORD};
			
			String Details[][] = new String[][] {
				{ "TRACKING_NUMBER", Shipment_Info.Tracking_Number },
				{ "USER_ID", Shipment_Info.User_Info.USER_ID },
				{ "PASSWORD", Shipment_Info.User_Info.PASSWORD }, 
				{ "SERVICE", Shipment_Info.Service},
				{ "GROUND_TEST_ID", Shipment_Info.GROUND_TEST_ID}
				};
			Tracking_Data_Update.updateFile(Details, 0);
			
			TrackingData.add(shipment);
		} catch (Exception e) {
			e.printStackTrace();
			Assert.fail(e.getCause().toString());
		}
	}
	
}