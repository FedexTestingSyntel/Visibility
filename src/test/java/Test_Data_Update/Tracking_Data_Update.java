package Test_Data_Update;

// import java.io.File;
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
import Data_Structures.Shipment_Data;
import SupportClasses.Environment;
import SupportClasses.Helper_Functions;
import Test_Data_Generation.Tracking_Number_Systematic_Search;

@Listeners(SupportClasses.TestNG_TestListener.class)

public class Tracking_Data_Update {
	static String LevelsToTest = "2";

	@BeforeClass
	public void beforeClass() {
		Environment.SetLevelsToTest(LevelsToTest);
		API_Functions.General_API_Calls.setPrintOutAPICallFlag(false); // true false
		API_Functions.General_API_Calls.setPrintOutFullResponseFlag(false);
	}

	@DataProvider (parallel = true)
	public static Iterator<Object[]> dp(Method m) {
		List<Object[]> data = new ArrayList<Object[]>();

		for (int i = 0; i < Environment.LevelsToTest.length(); i++) {
			String Level = String.valueOf(Environment.LevelsToTest.charAt(i));
			// Only when updating the full list
			// Data_Structures.Shipment_Data.setTrackingFilePathName("TrackingNumbersL" + Level + "FullList.xls");
			
			boolean fileToLarge = Helper_Functions.CheckFileToLarge(Shipment_Data.getTrackingFilePath(Level));
			if (fileToLarge) {
				return null;
			}
			// int intLevel = Integer.parseInt(Level);

			Shipment_Data Shipment_Info_Array[] = Data_Structures.Shipment_Data.getTrackingDetails(Level);
			// Based on the method that is being called the array list will be populated
			switch (m.getName()) {
			case "Tracking_Number_Update":
				for (Shipment_Data Shipment_Info : Shipment_Info_Array) {
					boolean add = false;
					// comment out what's not need to speed up update.
					if (Shipment_Info.MultiPieceShipment&& Helper_Functions.isNullOrUndefined(Shipment_Info.TRACKING_QUALIFIER)) {add = true;} 
					else if (!Shipment_Info.getValidStatus()) {add = true;} 
					// else if (Helper_Functions.isNullOrUndefined(Shipment_Info.Ship_Date)) {add = true;} 
					else if (Helper_Functions.isNullOrUndefined(Shipment_Info.TrackPackagesResponse)) {add = true;} 
					else { add = true;}
					
					if (add) {
						data.add(new Object[] { Level, Shipment_Info.Tracking_Number,
								Shipment_Info.TRACKING_QUALIFIER, Shipment_Info.User_Info.USER_ID,
								Shipment_Info.User_Info.PASSWORD }); 
					}
					

					/*
					 * if (!Helper_Functions.isNullOrUndefined(Shipment_Info.TRACKING_QUALIFIER) &&
					 * Shipment_Info.TRACKING_CARRIER.contentEquals("FDXG")) { data.add(new Object[]
					 * {Level, Shipment_Info.Tracking_Number, Shipment_Info.TRACKING_QUALIFIER,
					 * Shipment_Info.User_Info.USER_ID, Shipment_Info.User_Info.PASSWORD}); }
					 */

					/*
					 * if (Helper_Functions.isNullOrUndefined(Shipment_Info.TRACKING_QUALIFIER)) {
					 * // update tracking that are currently listed as multipiece data.add(new
					 * Object[] {Level, Shipment_Info.Tracking_Number,
					 * Shipment_Info.TRACKING_QUALIFIER, Shipment_Info.User_Info.USER_ID,
					 * Shipment_Info.User_Info.PASSWORD}); }
					 */
				}
				break;
			}
		}

		SupportClasses.Helper_Functions.LimitDataProvider(m.getName(), -1, data);
		return data.iterator();
	}

	@Test(dataProvider = "dp", enabled = true) // , invocationCount = 100
	public static void Tracking_Number_Update(String Level, String trackingNumber, String trackingQualifier,
			String UserID, String Password) {
		try {
			String trackingCarrier = "";
			int keyPosition = 0;
			String Details[][] = new String[][] { { "TRACKING_NUMBER", trackingNumber }, { "USER_ID", UserID },
					{ "PASSWORD", Password }};

			if (!trackingQualifier.contentEquals("")) {
				Details = Arrays.copyOf(Details, Details.length + 1);
				Details[Details.length - 1] = new String[] { "TRACKING_QUALIFIER", trackingQualifier };
				keyPosition = Details.length - 1;
			}

			Details = TRKC.tracking_packages.getTrackpackages(Details, trackingNumber, trackingQualifier, "");
			String SHIP_DATE = "";
			for (String[] Element : Details) {
				if (Element != null && Element[0] != null && Element[1] != null) {
					if (Element[0].contentEquals("SHIP_DATE")) {
						SHIP_DATE = Element[1];
					} else if (Element[0].contentEquals("TRACKING_QUALIFIER")) {
						trackingQualifier = Element[1];
					} else if (Element[0].contentEquals("TRACKING_CARRIER")) {
						trackingCarrier = Element[1];
					}
					
				}
			}

			// Note: may make call even if shipment has no CDO or associated shipments
			Details = CMDC_Application.inflight_delivery_options.getInflightDeliveryOptions(Details, trackingNumber, SHIP_DATE, trackingQualifier);
			Details = TRKC.associated_shipments.getAssociatedShipment(Details, trackingNumber, trackingQualifier, trackingCarrier);

			if (trackingQualifier.contentEquals("")) {
				Helper_Functions.PrintOut("The tracking number is no longer valid. " + trackingNumber);
			}

			updateFile(Details, keyPosition);
		} catch (Exception e) {
			e.printStackTrace();
			Assert.fail(e.getCause().toString());
		}
	}

	// used for updating the tracking number to file without trying to update any
	// values
	public static void Tracking_Number_To_File(String Level, String trackingNumber, String UserID, String Password) {
		try {
			int keyPosition = 0;
			String Details[][] = new String[][] { { "TRACKING_NUMBER", trackingNumber }, { "USER_ID", UserID },
					{ "PASSWORD", Password } };

			updateFile(Details, keyPosition);
		} catch (Exception e) {
			e.printStackTrace();
			Assert.fail(e.getCause().toString());
		}
	}

	public static void updateFile(String Details[][], int keyPosition) {
		String Level = Environment.getInstance().getLevel();
		String FileName = Shipment_Data.getTrackingFilePath(Level);
		Helper_Functions.WriteToExcel(FileName, "L" + Level, Details, keyPosition);
	}

	@Test(dataProvider = "dp", enabled = false) // , invocationCount = 100
	public static void getTrackingNumbersFromUser(String Level, String userID, String userPassword) {
		try {
			// int keyPosition = 1;
			String detailsInitial[][] = new String[][] { { "TRACKING_NUMBER" }, { "TRACKING_QUALIFIER" },
					{ "TRACKING_CARRIER" }, { "USER_ID", }, { "PASSWORD" }, { "SHIP_DATE" }, { "DELIVERY_DATE" },
					{ "STATUS" },
					// {"shipmentLightListResponse"}
			};
			String details[][] = twoDimensionalArrayClone(detailsInitial);

			String Results[] = USRC.login.Login(userID, userPassword);
			String userCookies = Results[0];

			String pageIndex = "1";
			String shipmentListResponse = TRKC.shipment_list_request.shipmentListRequest(userCookies, pageIndex);
			int totalNumberOfShipments = 0;
			try {
				totalNumberOfShipments = Integer.parseInt(API_Functions.General_API_Calls.ParseStringValue(
						shipmentListResponse, TRKC.shipment_list_request.totalNumberOfShipmentsParam));
			} catch (Exception e) {
				// in case the total number of shipments returns incorrectly.
				Assert.fail("Unable to retrieve total number of shipments.");
			}

			String shipmentLightList = API_Functions.General_API_Calls.ParseStringValue(shipmentListResponse,
					"shipmentLightList");

			String trackingDetails[][] = new String[][] { { "TRACKING_NUMBER", "trkNbr" },
					{ "TRACKING_QUALIFIER", "trkQual" }, { "TRACKING_CARRIER", "carrCD" }, { "USER_ID", userID },
					{ "PASSWORD", userPassword }, { "SHIP_DATE", "shpTs" }, { "DELIVERY_DATE", "estDelTm" },
					{ "STATUS", "keyStat" },
					// {"shipmentLightListResponse", ""}
			};

			// for searching the pachages in TRKC track packages call.
			String Tracking_List_Array[] = new String[30];
			int pos = 0;

			// get every page
			for (;;) {
				String singleTrackingResponse = API_Functions.General_API_Calls.ParseFirstArrayValue(shipmentLightList);
				// Parse the first page
				while (singleTrackingResponse != null) {
					String trackingNumber = "";
					for (int position = 0; position < trackingDetails.length; position++) {
						details[position] = Arrays.copyOf(details[position], details[position].length + 1);
						if (details[position][0].contentEquals("USER_ID")) {
							details[position][details[position].length - 1] = userID;
						} else if (details[position][0].contentEquals("PASSWORD")) {
							details[position][details[position].length - 1] = userPassword;
						} else if (details[position][0].contentEquals("shipmentLightListResponse")) {
							details[position][details[position].length - 1] = singleTrackingResponse;
						} else {
							details[position][details[position].length - 1] = API_Functions.General_API_Calls
									.ParseStringValue(singleTrackingResponse, trackingDetails[position][1]);
							// update tracking number to be used below.
							if (trackingDetails[position][0].contentEquals("TRACKING_NUMBER")) {
								trackingNumber = details[position][details[position].length - 1];
							}
						}
					}

					if (singleTrackingResponse.contains("spod\":true") && singleTrackingResponse.contains("sigAvail\":true")) {
						// Helper_Functions.PrintOut("trackingNumber: " + trackingNumber + "  User: " + userID);
						String Tracking_Number_Search[] = new String[] {trackingNumber};
						// TODO: Should use a dynamic cookie
						Test_Data_Generation.Tracking_Number_Systematic_Search.Tracking_Number_Search(Level, Tracking_Number_Search, userCookies);
					}
					
					 Tracking_List_Array[pos] = trackingNumber; pos++;
					 if (pos == 30) {
						 Tracking_Number_Systematic_Search.Tracking_Number_Search(Level, Tracking_List_Array, userCookies); 
						 Tracking_List_Array = new String[30];
						 pos = 0;
					 }
					 // will print out if this tracking number is one that is still needed.
					 Tracking_Number_Systematic_Search.checkTrackingFlags(singleTrackingResponse,
					 trackingNumber);

					// remove the tracking number that was just added to the details list.
					shipmentLightList = shipmentLightList.replace(singleTrackingResponse, "");

					// get the next tracking number
					singleTrackingResponse = API_Functions.General_API_Calls.ParseFirstArrayValue(shipmentLightList);
				}

				// Tracking_Number_Systematic_Search.Tracking_Number_Search(Level, Tracking_List_Array, userCookies);

				/*
				 * if (details[0].length > 1) { // update the file with the data so far.
				 * updateFile(details, keyPosition); Helper_Functions.PrintOut(details[0].length
				 * - 1 + " tracking numbers being updated.  User: " + userID + " - " + pageIndex
				 * + " of " + totalNumberOfShipments); }
				 */

				// get next page
				if (Integer.parseInt(pageIndex) <= (totalNumberOfShipments - 500)) {
					// reset the details list to default values.
					details = twoDimensionalArrayClone(detailsInitial);
					int pageSize = 500;
					pageIndex = (Integer.parseInt(pageIndex) + pageSize) + "";
					shipmentListResponse = TRKC.shipment_list_request.shipmentListRequest(userCookies, pageIndex);
					shipmentLightList = API_Functions.General_API_Calls.ParseStringValue(shipmentListResponse,
							"shipmentLightList");
				} else {
					// no more pages
					break;
				}

			}

		} catch (Exception e) {
			e.printStackTrace();
			Assert.fail(e.getCause().toString());
		}
	}

	public static String[][] twoDimensionalArrayClone(String[][] a) {
		String[][] b = new String[a.length][];
		for (int i = 0; i < a.length; i++) {
			b[i] = a[i].clone();
		}
		return b;
	}
}