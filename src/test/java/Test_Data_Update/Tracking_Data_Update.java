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
import Data_Structures.Shipment_Data;
import Data_Structures.User_Data;
import INET_Application.GroundCorpLoad;
import SupportClasses.Environment;
import SupportClasses.Helper_Functions;
import Test_Data_Generation.Tracking_Number_Systematic_Search;

@Listeners(SupportClasses.TestNG_TestListener.class)

public class Tracking_Data_Update {
	static String LevelsToTest = "3";

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
			case "Tracking_Number_Update":
				for (Shipment_Data Shipment_Info : Shipment_Info_Array) {
					if (Shipment_Info.MultiPieceShipment
							&& Helper_Functions.isNullOrUndefined(Shipment_Info.TRACKING_QUALIFIER)) {
						// update tracking that are currently listed as multipiece
						data.add(new Object[] { Level, Shipment_Info.Tracking_Number, Shipment_Info.TRACKING_QUALIFIER,
								Shipment_Info.User_Info.USER_ID, Shipment_Info.User_Info.PASSWORD });
					} else if (!Shipment_Info.getValidStatus()) {
						data.add(new Object[] { Level, Shipment_Info.Tracking_Number, Shipment_Info.TRACKING_QUALIFIER,
								Shipment_Info.User_Info.USER_ID, Shipment_Info.User_Info.PASSWORD });
					} else if (Helper_Functions.isNullOrUndefined(Shipment_Info.TrackPackagesResponse)) {
						data.add(new Object[] { Level, Shipment_Info.Tracking_Number, Shipment_Info.TRACKING_QUALIFIER,
								Shipment_Info.User_Info.USER_ID, Shipment_Info.User_Info.PASSWORD });
					} else if (Helper_Functions.isNullOrUndefined(Shipment_Info.Ship_Date)) {
						data.add(new Object[] { Level, Shipment_Info.Tracking_Number, Shipment_Info.TRACKING_QUALIFIER,
								Shipment_Info.User_Info.USER_ID, Shipment_Info.User_Info.PASSWORD });
					}/* else {
						data.add(new Object[] { Level, Shipment_Info.Tracking_Number, Shipment_Info.TRACKING_QUALIFIER,
								Shipment_Info.User_Info.USER_ID, Shipment_Info.User_Info.PASSWORD });
					}*/

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
			case "Apply_Ground_Scan":
				for (Shipment_Data Shipment_Info : Shipment_Info_Array) {
					// Add the existing tracking numbers to list.
					// if (!Shipment_Info.getValidStatus() ||
					// !Shipment_Info.getHasTrackPackagesResponse()) {
					if (Shipment_Info.Service.toLowerCase().contains("ground")
							&& Helper_Functions.isNullOrUndefined(Shipment_Info.TRACKING_QUALIFIER)) {
						data.add(new Object[] { Level, Shipment_Info.Tracking_Number });
					}

					// else {data.add(new Object[] {Level, Shipment_Info.Tracking_Number,
					// Shipment_Info.TRACKING_QUALIFIER});}
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
							data.add(new Object[] { Level, Tracking_List_Array});
							Tracking_List_Array = new String[interval];
							pos = 0;
						}

					}
				}
				break;
			case "checkTrackingFlags":
				data.add(new Object[] { Level});
				break;
			}
			
		}

		SupportClasses.Helper_Functions.LimitDataProvider(m.getName(), -1, data);
		return data.iterator();
	}

	@Test(dataProvider = "dp", enabled = true) // , invocationCount = 100
	public static void Tracking_Number_Update(String Level, String trackingNumber, String trackingQualifier, String UserID, String Password) {
		try {
			String trackingCarrier = "";
			int keyPosition = 0;
			String Details[][] = new String[][] { 
				{ "TRACKING_NUMBER", trackingNumber }, 
				{ "USER_ID", UserID },
				{ "PASSWORD", Password } };

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

			Details = CMDC_Application.inflight_delivery_options.getInflightDeliveryOptions(Details, trackingNumber,
					SHIP_DATE, trackingQualifier);
			Details = TRKC.associated_shipments.getAssociatedShipment(Details, trackingNumber, trackingQualifier,
					trackingCarrier);
			
			if (trackingQualifier.contentEquals("")) {
				Helper_Functions.PrintOut("The tracking number is no longer valid.");
			}

			updateFile(Details, keyPosition);
		} catch (Exception e) {
			e.printStackTrace();
			Assert.fail(e.getCause().toString());
		}
	}
	
	@Test(dataProvider = "dp", enabled = false)
	public static void Tracking_Number_Bulk_Update(String Level, String Tracking_List_Array[]){
		
		String Range = Tracking_List_Array[0] + "-" + Tracking_List_Array[Tracking_List_Array.length - 1];
		int TrackingNumbersFound = 0;
		try {
			String TrackingPackagesResponse = TRKC.tracking_packages.TrackingPackagesRequest(Tracking_List_Array);
			
			String TrackingPackageList = "packageList\":";
			
			String SingleTrackingResponse = TRKC.tracking_packages.getSingleTrackingResponse(TrackingPackagesResponse);
			
			if (SingleTrackingResponse == null || SingleTrackingResponse.contentEquals("")) {
				throw new Exception("TrackingPackagesResponse is not parseable.");
			}
			
			//Remove the start of the response and just leave the packageList
			TrackingPackagesResponse = TrackingPackagesResponse.substring(TrackingPackagesResponse.indexOf(TrackingPackageList) + TrackingPackageList.length(), TrackingPackagesResponse.length());
			String details[][] = new String[][] {
				{"TRACKING_NUMBER"}, 
				{"TRACKING_QUALIFIER"}, 
				{"TRACKING_CARRIER"},
				{"TrackPackagesResponse"},
			};
			for (int pos = 0; pos < Tracking_List_Array.length; pos++) {
				SingleTrackingResponse = TRKC.tracking_packages.getSingleTrackingResponse(TrackingPackagesResponse);

				String trackingQualifier = API_Functions.General_API_Calls.ParseStringValue(SingleTrackingResponse, "trackingQualifier");
				if (!Helper_Functions.isNullOrUndefined(trackingQualifier)) {
					String trackingNumber = API_Functions.General_API_Calls.ParseStringValue(SingleTrackingResponse, "trackingNbr");
					String trackingCarrierCd = API_Functions.General_API_Calls.ParseStringValue(SingleTrackingResponse, "trackingCarrierCd");;
					for (int updateSize = 0; updateSize < details.length; updateSize++) {
						details[updateSize] = Arrays.copyOf(details[updateSize], details[updateSize].length + 1);
					}
					details[0][details[0].length - 1] = trackingNumber;
					details[1][details[1].length - 1] = trackingQualifier;
					details[2][details[2].length - 1] = trackingCarrierCd;
					details[3][details[3].length - 1] = SingleTrackingResponse;
					TrackingNumbersFound++;
				}
				
				TrackingPackagesResponse = TrackingPackagesResponse.replace(SingleTrackingResponse, "");
			}
			
			String FileName = Shipment_Data.getTrackingFilePath(Level);
			Helper_Functions.WriteToExcel(FileName, "L" + Level, details, 1);
			
			Helper_Functions.PrintOut(Range + ":  " + TrackingNumbersFound);
		}catch (Exception e) {
			e.printStackTrace();
 			Assert.fail(e.getCause().toString());
		}
	}
	
	@Test(dataProvider = "dp", enabled = false)
	public static void Apply_Ground_Scan(String Level, String trackingNumber) {
		try {
			String Tracking[] = new String[] { trackingNumber };
			String GroundResponse = GroundCorpLoad.ValidateAndProcess(Tracking);

			if (GroundResponse.contains("\"valid\":1")) {
				// if received a valid response run updates.
				Tracking_Number_Update(Level, trackingNumber, "", "", "");
			} else {
				Assert.fail(GroundResponse);
			}

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
	
	// used for updating the tracking number to file without trying to update any values
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

	private static void updateFile(String Details[][], int keyPosition) {
		String Level = Environment.getInstance().getLevel();
		String FileName = Shipment_Data.getTrackingFilePath(Level);
		Helper_Functions.WriteToExcel(FileName, "L" + Level, Details, keyPosition);
	}
	
	@Test(dataProvider = "dp", enabled = false) // , invocationCount = 100
	public static void getTrackingNumbersFromUser(String Level, String userID, String userPassword) {
		try {
			int keyPosition = 1;
			String detailsInitial[][] = new String[][] {
				{"TRACKING_NUMBER"},
				{"TRACKING_QUALIFIER"},
				{"TRACKING_CARRIER"},
				{"USER_ID",},
				{"PASSWORD"},
				{"SHIP_DATE"},
				{"DELIVERY_DATE"},
				{"STATUS"},
				//{"shipmentLightListResponse"}
				};
			String details[][] = twoDimensionalArrayClone(detailsInitial);

			String Results[] = USRC.login.Login(userID, userPassword);
			String userCookies = Results[0];

			String pageIndex = "1";
			String shipmentListResponse = TRKC.shipment_list_request.shipmentListRequest(userCookies, pageIndex);
			int totalNumberOfShipments = 0;
			try {
				totalNumberOfShipments = Integer.parseInt(API_Functions.General_API_Calls.ParseStringValue(shipmentListResponse, TRKC.shipment_list_request.totalNumberOfShipmentsParam));
			} catch (Exception e) {
				// in case the total number of shipments returns incorrectly.
				Assert.fail("Unable to retrieve total number of shipments.");
			}
			
			String shipmentLightList = API_Functions.General_API_Calls.ParseStringValue(shipmentListResponse,"shipmentLightList");
			
			String trackingDetails[][] = new String[][] {
				{"TRACKING_NUMBER", "trkNbr" },
				{"TRACKING_QUALIFIER", "trkQual"},
				{"TRACKING_CARRIER", "carrCD"},
				{"USER_ID", userID },
				{"PASSWORD", userPassword },
				{"SHIP_DATE", "shpTs"},
				{"DELIVERY_DATE", "estDelTm"},
				{"STATUS", "keyStat"},
				//{"shipmentLightListResponse", ""}
				};
				
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
							details[position][details[position].length - 1] = API_Functions.General_API_Calls.ParseStringValue(singleTrackingResponse, trackingDetails[position][1]);
							// update tracking number to be used below.
							if (trackingDetails[position][1].contentEquals("TRACKING_NUMBER")) {
								trackingNumber = details[position][details[position].length - 1];
							}
						}
					}
					
					// will print out if this tracking number is one that is still needed.
					Tracking_Number_Systematic_Search.checkTrackingFlags(singleTrackingResponse, trackingNumber); 
					
					//remove the tracking number that was just added to the details list.
					shipmentLightList = shipmentLightList.replace(singleTrackingResponse, "");
					
					//get the next tracking number
					singleTrackingResponse = API_Functions.General_API_Calls.ParseFirstArrayValue(shipmentLightList);
				}
				
				if (details[0].length > 1) {
					// update the file with the data so far.
					updateFile(details, keyPosition);
					Helper_Functions.PrintOut(details[0].length - 1 + " tracking numbers being updated.  User: " + userID + " - " + pageIndex + " of " + totalNumberOfShipments);
				}
			
				// get next page
				if (Integer.parseInt(pageIndex) <= (totalNumberOfShipments - 500)) {
					// reset the details list to default values.
					details = twoDimensionalArrayClone(detailsInitial);
					int pageSize = 500;
					pageIndex = (Integer.parseInt(pageIndex) + pageSize) + "";
					shipmentListResponse = TRKC.shipment_list_request.shipmentListRequest(userCookies, pageIndex);
					shipmentLightList = API_Functions.General_API_Calls.ParseStringValue(shipmentListResponse,"shipmentLightList");
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

	@Test(dataProvider = "dp", enabled = false)
	public static void checkTrackingFlags(String level) {

		String searchParameter = "doorTagNbrList";   ///dutiesAndTaxesDesc
		
		Shipment_Data Shipment_Info_Array[] = Data_Structures.Shipment_Data.getTrackingDetails(level);
		for (Shipment_Data Shipment_Info : Shipment_Info_Array) {
			if (!Helper_Functions.isNullOrUndefined(Shipment_Info.TrackPackagesResponse)) {
				String searchedValue = API_Functions.General_API_Calls.ParseStringValue(Shipment_Info.TrackPackagesResponse, searchParameter);
				if (!Helper_Functions.isNullOrUndefined(searchedValue) 
						&& !searchedValue.contentEquals("false")
						&& !searchedValue.contentEquals("")
						&& !searchedValue.contentEquals("[\"\"]")
						&& !searchedValue.contentEquals("0")) {
					Helper_Functions.PrintOut("Tracking_Number _" + Shipment_Info.Tracking_Number + "_ param: " + searchParameter + "_ value: " + searchedValue);
				}
			}
		}
	}
}