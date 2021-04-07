package Test_Data_Generation;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Listeners;
import org.testng.annotations.Test;
import Data_Structures.Shipment_Data;
import Data_Structures.User_Data;
import SupportClasses.Environment;
import SupportClasses.Helper_Functions;

@Listeners(SupportClasses.TestNG_TestListener.class)

public class Add_Tracking_Numbers_To_User {
	static String LevelsToTest = "2";
	static Shipment_Data Shipment_Info_Array[];

	@BeforeClass
	public void beforeClass() {
		Environment.SetLevelsToTest(LevelsToTest);
		API_Functions.General_API_Calls.setPrintOutAPICallFlag(false); // false true
		API_Functions.General_API_Calls.setPrintOutFullResponseFlag(false);
	}

	@DataProvider (parallel = true)
	public static Iterator<Object[]> dp(Method m) {
		List<Object[]> data = new ArrayList<Object[]>();

		for (int i = 0; i < Environment.LevelsToTest.length(); i++) {
			String Level = String.valueOf(Environment.LevelsToTest.charAt(i));
			Environment.getInstance().setLevel(Level);
			int intLevel = Integer.parseInt(Level);

			// Only when updating the full list
			Data_Structures.Shipment_Data.setTrackingFilePathName("TrackingNumbersL" + Level + "FullList.xls");
			Helper_Functions.TestingData = Helper_Functions.DataDirectory + "\\TestingDataFullList.xls";

			// get the user to add the tracking numbers to
			User_Data userInfoArray[] = User_Data.Get_UserIds(intLevel);
			Shipment_Info_Array = Data_Structures.Shipment_Data.getTrackingDetails(Level);
			// int numberOfShipments =
			// TRKC.tracking_profile.checkTotalNumberOfShipments(userID, User_Cookies);
			String User_Cookies = "";
			String userID = "";

			switch (m.getName()) {
			case "Add_Tracking_Number":
				for (int numShipments = 1; numShipments < 22; numShipments++) {
					userID = "L" + Level + "ATRK" + numShipments + "K";
					if (numShipments > 20) {
						userID = "L" + Level + "ATRKLARGE";
					}
					for (User_Data User_Info : userInfoArray) {
						if (User_Info.USER_ID.contentEquals(userID)) {
							String Results[] = USRC.login.Login(User_Info.USER_ID, User_Info.PASSWORD);
							User_Cookies = Results[0];
							userID = User_Info.USER_ID;
							break;
						}
					}

					if (!User_Cookies.contentEquals("")) {
						data.add(new Object[] { Level, User_Cookies, userID, numShipments * 1000 });
					}
				}
				
				// reverse the array order, used to add tracking numbers from the bottom of the list first.
				Collections.reverse(Arrays.asList(Shipment_Info_Array));
				
				break;
			case "Add_Exceptions":
				userID = "L" + Level + "ATRKLARGE";
				for (User_Data User_Info : userInfoArray) {
					if (User_Info.USER_ID.contentEquals(userID)) {
						String Results[] = USRC.login.Login(User_Info.USER_ID, User_Info.PASSWORD);
						User_Cookies = Results[0];
						break;
					}
				}

				if (!User_Cookies.contentEquals("")) {
					for (Shipment_Data Shipment_Info : Shipment_Info_Array) {
						if (Shipment_Info.Status.toUpperCase().contains("EXCEPTION")) {
							data.add(new Object[] { Level, User_Cookies, Shipment_Info });
						}
					}
				}

				break;
			case "Add_Tracking_To_User":
				userID = "L" + Level + "ATRKLARGE";
				userID = "L2Use082516";
				for (User_Data User_Info : userInfoArray) {
					if (User_Info.USER_ID.contentEquals(userID)) {
						String Results[] = USRC.login.Login(User_Info.USER_ID, User_Info.PASSWORD);
						User_Cookies = Results[0];
						break;
					}
				}
				
				// reverse the array order, used to add tracking numbers from the bottom of the list first.
				Collections.reverse(Arrays.asList(Shipment_Info_Array));

				if (!User_Cookies.contentEquals("")) {
					for (Shipment_Data Shipment_Info : Shipment_Info_Array) {
						/*if (Shipment_Info.TRACKING_CARRIER.contentEquals("FXFR")) {
							data.add(new Object[] { Level, User_Cookies, Shipment_Info });
						}*/
						data.add(new Object[] { Level, User_Cookies, Shipment_Info });
						// add X number of shipments.
						if (data.size() > 23000) {
							break;
						}
					}
				}
			
				break;
			}
		}

		SupportClasses.Helper_Functions.LimitDataProvider(m.getName(), -1, data);

		return data.iterator();
	}

	@Test(dataProvider = "dp", enabled = false) // invocationCount = 22,
	public static void Add_Tracking_Number(String Level, String Cookie, String userId, int shipmentsNeeded) {
		try {
			int numberOfShipments = TRKC.tracking_profile.checkTotalNumberOfShipments(userId, Cookie);

			for (Shipment_Data Shipment_Info : Shipment_Info_Array) {
				if (numberOfShipments >= shipmentsNeeded) {
					// check the updated shipment count
					numberOfShipments = TRKC.tracking_profile.checkTotalNumberOfShipments(userId, Cookie); 
					if (numberOfShipments >= shipmentsNeeded) { 
						 break;
					}
					 
				} else if (!Shipment_Info.TRACKING_QUALIFIER.contentEquals("")) {
					boolean validResponse = Add_Tracking_Number(Level, Cookie, Shipment_Info);
					if (validResponse) {
						numberOfShipments++;
					}

					int mod = numberOfShipments % 20;
					if (numberOfShipments > 0 && mod == 0) {
						Helper_Functions.PrintOut(userId + " now has " + numberOfShipments + " shipments.");
					}
					if (numberOfShipments > 0 && numberOfShipments % 100 == 0) { 
						// check the updated shipment count 
						numberOfShipments = TRKC.tracking_profile.checkTotalNumberOfShipments(userId, Cookie);
					}
				}
			}

			// Helper_Functions.PrintOut(Shipment_Info.Tracking_Number + " added.");
		} catch (Exception e) {
			e.printStackTrace();
			Assert.fail(e.getCause().toString());
		}
	}

	public static boolean Add_Tracking_Number(String Level, String Cookie, Shipment_Data Shipment_Info) {
		try {
			String Response_TRKC = "";
			int numberOfAttempts = 5;
			for (int i = 0; i < numberOfAttempts; i++) {
				Response_TRKC = TRKC.shipment_option_add_request.ShipmentOptionAddRequest(Cookie,
						Shipment_Info.Tracking_Number, Shipment_Info.TRACKING_QUALIFIER,
						Shipment_Info.TRACKING_CARRIER);
				if (!Response_TRKC.contains("TRAQS connection/request problem")) {
					i = numberOfAttempts;
				} else if (i == 4) {
					// uncomment if want to fail after making 4 attempts
					// Assert.fail(Shipment_Info.Tracking_Number);
				}
			}

			boolean validResponse = TRKC.helper.checkValidResponse(Response_TRKC);
			return validResponse;

			// Helper_Functions.PrintOut(Shipment_Info.Tracking_Number + " added.");
		} catch (Exception e) {
			e.printStackTrace();
			Assert.fail(e.getCause().toString());
		}
		return false;
	}

	@Test(dataProvider = "dp", enabled = false)
	public static void Add_Exceptions(String Level, String Cookie, Shipment_Data Shipment_Info) {
		try {
			String Response_TRKC = "";
			int number_of_attempts = 5;
			for (int i = 0; i < number_of_attempts; i++) {
				Response_TRKC = TRKC.shipment_option_add_request.ShipmentOptionAddRequest(Cookie,
						Shipment_Info.Tracking_Number, Shipment_Info.TRACKING_QUALIFIER,
						Shipment_Info.TRACKING_CARRIER);
				if (!Response_TRKC.contains("TRAQS connection/request problem")) {
					i = number_of_attempts;
				}
			}
			boolean validResponse = TRKC.helper.checkValidResponse(Response_TRKC);
			Assert.assertTrue(validResponse);

		} catch (Exception e) {
			e.printStackTrace();
			Assert.fail(e.getCause().toString());
			Helper_Functions.PrintOut(Shipment_Info.Tracking_Number + ", ", true);
		}
	}
	
	@Test(dataProvider = "dp", enabled = true)
	public static void Add_Tracking_To_User(String Level, String Cookie, Shipment_Data Shipment_Info) {
		try {
			String Response_TRKC = "";
			int number_of_attempts = 5;
			for (int i = 0; i < number_of_attempts; i++) {
				Response_TRKC = TRKC.shipment_option_add_request.ShipmentOptionAddRequest(Cookie,
						Shipment_Info.Tracking_Number, Shipment_Info.TRACKING_QUALIFIER,
						Shipment_Info.TRACKING_CARRIER);
				if (!Response_TRKC.contains("TRAQS connection/request problem")) {
					i = number_of_attempts;
				}
			}
			// Helper_Functions.PrintOut(Response_TRKC);
			boolean validResponse = TRKC.helper.checkValidResponse(Response_TRKC);
			Assert.assertTrue(validResponse);

		} catch (Exception e) {
			e.printStackTrace();
			Assert.fail(e.getCause().toString());
			Helper_Functions.PrintOut(Shipment_Info.Tracking_Number + ", ", true);
		}
	}
}