package Test_Data_Update;

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
import Test_Data_Generation.Tracking_Number_Systematic_Search;

@Listeners(SupportClasses.TestNG_TestListener.class)

public class Tracking_Data_Update_Additional {
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
			int intLevel = Integer.parseInt(Level);

			// Only when updating the full list
			// Data_Structures.Shipment_Data.setTrackingFilePathName("TrackingNumbersL" + Level + "FullList.xls");

			Shipment_Data Shipment_Info_Array[] = Data_Structures.Shipment_Data.getTrackingDetails(Level);
			// Based on the method that is being called the array list will be populated
			switch (m.getName()) {
			case "Apply_Ground_Scan":
				for (Shipment_Data Shipment_Info : Shipment_Info_Array) {
					if (Shipment_Info.TRACKING_CARRIER.contentEquals("") && !Helper_Functions.isNullOrUndefined(Shipment_Info.GROUND_TEST_ID)) {
						data.add(new Object[] { Level, Shipment_Info.Tracking_Number, Shipment_Info.GROUND_TEST_ID});
					}
				}
				// data.add(new Object[] { Level, "794976655748" });
				break;	
			case "Apply_Express_PUP_Scan":
				String TrackingNumbers[] = {};
				Shipment_Data tempShipmentInfo = new Shipment_Data();
				Address_Data Address_Info1 = Address_Data.getAddress(Level, "US", null);
				tempShipmentInfo.setOrigin_Address_Info(Address_Info1);
				tempShipmentInfo.setDestination_Address_Info(Address_Info1);
				for (Shipment_Data Shipment_Info : Shipment_Info_Array) {
					if (!Shipment_Info.Service.toLowerCase().contains("ground")
							&& (Shipment_Info.Status.toLowerCase().contains("label created") ||
									Shipment_Info.Status.toLowerCase().contains("in transit"))) {
						TrackingNumbers = Arrays.copyOf(TrackingNumbers, TrackingNumbers.length + 1);
						TrackingNumbers[TrackingNumbers.length - 1] = Shipment_Info.Tracking_Number;
						// only 10 tracking numbers can have scan applied at once.
						// changed to 2 for redundancy, more risk of random failure
						if (TrackingNumbers.length > 2) {
							data.add(new Object[] { Level, tempShipmentInfo, TrackingNumbers});
							TrackingNumbers = Arrays.copyOf(TrackingNumbers, 0);
						}
					}
				}
				
				if (TrackingNumbers.length > 0) {
					data.add(new Object[] { Level, tempShipmentInfo, TrackingNumbers});
				}
				break;
			case "eMASS_API_Pickup_Scan":
				for (Shipment_Data Shipment_Info : Shipment_Info_Array) {
					if (!Shipment_Info.Service.toLowerCase().contains("ground")
							&& Shipment_Info.Status.toLowerCase().contains("label created")) {
						data.add(new Object[] { Level, Shipment_Info.Tracking_Number});
					}
				}
				break;
			case "Tracking_Number_AssociatedShipment":
				for (Shipment_Data Shipment_Info : Shipment_Info_Array) {
					if (!Helper_Functions.isNullOrUndefined(Shipment_Info.Tracking_Number)
							&& !Helper_Functions.isNullOrUndefined(Shipment_Info.TRACKING_CARRIER)
							&& Shipment_Info.TRACKING_CARRIER.contentEquals("FXFR")
							&& !Helper_Functions.isNullOrUndefined(Shipment_Info.TRACKING_QUALIFIER)
							&& Helper_Functions.isNullOrUndefined(Shipment_Info.associatedShipmentResponse)) {
						data.add(new Object[] { Level, Shipment_Info.Tracking_Number, Shipment_Info.TRACKING_QUALIFIER,
								Shipment_Info.TRACKING_CARRIER });
					}
				}
				break;
			case "getTrackingNumbersFromUser":
				// data.add(new Object[] { Level, "L3AGILETEST01", "Test1234"});
				User_Data userInfoArray[] = User_Data.Get_UserIds(intLevel);

				for (User_Data user_Info : userInfoArray) {
					if (user_Info.TOTAL_NUMBER_OF_SHIPMENTS != null
							&& !user_Info.TOTAL_NUMBER_OF_SHIPMENTS.contentEquals("")
							&& Integer.parseInt(user_Info.TOTAL_NUMBER_OF_SHIPMENTS) > 1) {
						data.add(new Object[] { Level, user_Info.USER_ID, user_Info.PASSWORD});
					}
				}
				break;
			case "Tracking_Number_Bulk_Update":
				int interval = 30;
				String Tracking_List_Array[] = new String[interval];
				int pos = 0;
				for (Shipment_Data Shipment_Info : Shipment_Info_Array) {
					if (!Helper_Functions.isNullOrUndefined(Shipment_Info.Tracking_Number)) {
						Tracking_List_Array[pos] = Shipment_Info.Tracking_Number;
						pos++;
						if (pos == 30) {
							data.add(new Object[] { Level, Tracking_List_Array });
							Tracking_List_Array = new String[interval];
							pos = 0;
						}
					}
				}
				
				if (!Helper_Functions.isNullOrUndefined(Tracking_List_Array[0])) {
					data.add(new Object[] { Level, Tracking_List_Array });
				}
				break;
			case "checkTrackingFlags":
				data.add(new Object[] { Level });
				break;
			}

		}

		SupportClasses.Helper_Functions.LimitDataProvider(m.getName(), -1, data);
		return data.iterator();
	}

	@Test(dataProvider = "dp", enabled = false)
	public static void Tracking_Number_Bulk_Update(String Level, String Tracking_List_Array[]) {

		String Range = Tracking_List_Array[0] + "-" + Tracking_List_Array[Tracking_List_Array.length - 1];
		int TrackingNumbersFound = 0;
		int TrackingNumbersNotFound = 0; 
		try {
			String TrackingPackagesResponse = TRKC.tracking_packages.TrackingPackagesRequest("", Tracking_List_Array);

			String TrackingPackageList = "packageList\":";

			String SingleTrackingResponse = TRKC.tracking_packages.getSingleTrackingResponse(TrackingPackagesResponse);

			if (SingleTrackingResponse == null || SingleTrackingResponse.contentEquals("")) {
				throw new Exception("TrackingPackagesResponse is not parseable.");
			}

			// Remove the start of the response and just leave the packageList
			TrackingPackagesResponse = TrackingPackagesResponse.substring(
					TrackingPackagesResponse.indexOf(TrackingPackageList) + TrackingPackageList.length(),
					TrackingPackagesResponse.length());
			String details[][] = new String[][] { 
				{"TRACKING_NUMBER"}, 
				{"TRACKING_QUALIFIER"},
				{"TRACKING_CARRIER"},
				{"STATUS"},
				{"ERROR"},
				{"SHIP_DATE"},
				{"DELIVERY_DATE"},
				{"KEY_STATUS_CD"}
				};
				
			for (int pos = 0; pos < Tracking_List_Array.length; pos++) {
				SingleTrackingResponse = TRKC.tracking_packages.getSingleTrackingResponse(TrackingPackagesResponse);
				if (Helper_Functions.isNullOrUndefined(SingleTrackingResponse) || Helper_Functions.isNullOrUndefined(TrackingPackagesResponse)) {
					break;
				}
				
				String trackingQualifier = API_Functions.General_API_Calls.ParseStringValue(SingleTrackingResponse, "trackingQualifier");
				if (!Helper_Functions.isNullOrUndefined(trackingQualifier)) {
					String trackingNumber = API_Functions.General_API_Calls.ParseStringValue(SingleTrackingResponse, "trackingNbr");
					String trackingCarrierCd = API_Functions.General_API_Calls.ParseStringValue(SingleTrackingResponse, "trackingCarrierCd");
					
					String SHIP_DATE = API_Functions.General_API_Calls.ParseStringValue(SingleTrackingResponse, "shipDt");
					String DELIVERY_DATE = API_Functions.General_API_Calls.ParseStringValue(SingleTrackingResponse, "actDeliveryDt");
					String KEY_STATUS_CD = API_Functions.General_API_Calls.ParseStringValue(SingleTrackingResponse, "keyStatusCD");
					
					for (int updateSize = 0; updateSize < details.length; updateSize++) {
						details[updateSize] = Arrays.copyOf(details[updateSize], details[updateSize].length + 1);
					}
					String staus = API_Functions.General_API_Calls.ParseStringValue(SingleTrackingResponse, "keyStatus");
					details[0][details[0].length - 1] = trackingNumber;
					details[1][details[1].length - 1] = trackingQualifier;
					details[2][details[2].length - 1] = trackingCarrierCd;
					details[3][details[3].length - 1] = staus;
					details[4][details[4].length - 1] = "";
					details[5][details[5].length - 1] = SHIP_DATE;
					details[6][details[6].length - 1] = DELIVERY_DATE;
					details[7][details[7].length - 1] = KEY_STATUS_CD;
					TrackingNumbersFound++;
				} else {
					String trackingNumber = API_Functions.General_API_Calls.ParseStringValue(SingleTrackingResponse, "trackingNbr");
					for (int updateSize = 0; updateSize < details.length; updateSize++) {
						details[updateSize] = Arrays.copyOf(details[updateSize], details[updateSize].length + 1);
					}
					details[0][details[0].length - 1] = trackingNumber;
					details[1][details[1].length - 1] = "";
					details[2][details[2].length - 1] = "";
					details[3][details[3].length - 1] = "";
					details[4][details[4].length - 1] = Helper_Functions.CurrentDateTime();
					details[5][details[5].length - 1] = "";
					details[6][details[6].length - 1] = "";
					TrackingNumbersNotFound++;
				}

				TrackingPackagesResponse = TrackingPackagesResponse.replace(SingleTrackingResponse, "");
			}

			String FileName = Shipment_Data.getTrackingFilePath(Level);
			Helper_Functions.WriteToExcel(FileName, "L" + Level, details, 0);

			Helper_Functions.PrintOut(Range + ":  " + TrackingNumbersFound + " not found: " + TrackingNumbersNotFound);
		} catch (Exception e) {
			e.printStackTrace();
			Assert.fail(e.getCause().toString());
		}
	}

	@Test(dataProvider = "dp", enabled = false)
	public static void Apply_Ground_Scan(String Level, String trackingNumber, String testID) {
		try {
			String Tracking[] = new String[] { trackingNumber };
			// boolean groundResponse = GroundCorpLoad.ValidateAndProcess(Tracking, testID);
			boolean groundResponse = true;
			if (groundResponse) {
				// if received a valid response run updates.
				Tracking_Data_Update.Tracking_Number_Update(Level, trackingNumber, "", "", "");
			} else {
				Assert.fail("tracking numbers not added to final file.");
			}

		} catch (Exception e) {
			e.printStackTrace();
			Assert.fail(e.getCause().toString());
		}
	}
	
	@Test(dataProvider = "dp", enabled = true)
	public static void Apply_Express_PUP_Scan(String Level, Shipment_Data Shipment_Info, String TrackingNumbers[]) {
		try {
			boolean scansApplied = eMASS_Scans.eMASS_Multiple_Pickup_Scan(Shipment_Info, TrackingNumbers);
			Assert.assertTrue(scansApplied);
		} catch (Exception e) {
			e.printStackTrace();
			Assert.fail(e.getCause().toString());
		}
	}
	
	@Test(dataProvider = "dp", enabled = false)
	public static void eMASS_API_Pickup_Scan(String Level, String trackingNumber) {
		try {
			boolean scansApplied = eMASS_Scans.eMASS_API_Pickup_Scan(trackingNumber);
			Assert.assertTrue(scansApplied);
		} catch (Exception e) {
			e.printStackTrace();
			Assert.fail(e.getCause().toString());
		}
	}

	@Test(dataProvider = "dp", enabled = false) // , invocationCount = 100
	public static void Tracking_Number_AssociatedShipment(String Level, String trackingNumber, String trackingQualifier,
			String trackingCarrier) {
		try {
			int keyPosition = 0;
			String Details[][] = new String[][] { { "TRACKING_QUALIFIER", trackingQualifier } };

			Details = TRKC.associated_shipments.getAssociatedShipment(Details, trackingNumber, trackingQualifier,
					trackingCarrier);

			updateFile(Details, keyPosition);
			Helper_Functions
					.PrintOut("Tracking_Number:" + trackingNumber + "  TRACKING_QUALIFIER:" + trackingQualifier);
		} catch (Exception e) {
			// e.printStackTrace();
			// Assert.fail(e.getCause().toString());
			Assert.fail();
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
			int keyPosition = 1;
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

/*	@Test(dataProvider = "dp", enabled = true)
	public static void checkTrackingFlags(String level) {

		String searchParameter = "isDuplicate";

		Shipment_Data Shipment_Info_Array[] = Data_Structures.Shipment_Data.getTrackingDetails(level);
		for (Shipment_Data Shipment_Info : Shipment_Info_Array) {
			if (!Helper_Functions.isNullOrUndefined(Shipment_Info.TrackPackagesResponse)) {
				String searchedValue = API_Functions.General_API_Calls
						.ParseStringValue(Shipment_Info.TrackPackagesResponse, searchParameter);
				if (!Helper_Functions.isNullOrUndefined(searchedValue)
						&& !searchedValue.contentEquals("false")
						&& !searchedValue.contentEquals("")
						&& !searchedValue.contentEquals("[\"\"]")
						&& !searchedValue.contentEquals("0")) {
					Helper_Functions.PrintOut("Tracking_Number _" + Shipment_Info.Tracking_Number + "_ param: "
							+ searchParameter + "_ value: " + searchedValue);
				}
				
				if (searchedValue.contentEquals("")) {
					Helper_Functions.PrintOut("Tracking_Number _" + Shipment_Info.Tracking_Number + "_ param: "
							+ searchParameter + "_ value: " + searchedValue);
				}
			}
		}
	}*/
}