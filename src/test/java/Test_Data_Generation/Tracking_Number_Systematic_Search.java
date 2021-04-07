package Test_Data_Generation;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Listeners;
import org.testng.annotations.Test;
import Data_Structures.Shipment_Data;
import Data_Structures.User_Data;
import SupportClasses.Environment;
import SupportClasses.Helper_Functions;
import Test_Data_Update.Tracking_Data_Update;

import java.math.BigInteger; 

@Listeners(SupportClasses.TestNG_TestListener.class)

public class Tracking_Number_Systematic_Search {
	static String LevelsToTest = "2";
	static CopyOnWriteArrayList<String> TrackingList = new CopyOnWriteArrayList<String>();
	static CopyOnWriteArrayList<Shipment_Data> TrackingListForFileWrite = new CopyOnWriteArrayList<Shipment_Data>();
	public final static Lock TrackingNumberListLock = new ReentrantLock();
	/*
	 * L1 123456789012 
	 * L2 111111365885 	794817289549      794817253102     794817917379
	 * L3 111111258998						794962128310  L3 smartpost 61212821235519929820
	 * L4 111111120681 	776515491451
	 * L7 111112563080 	777650739640          771508034575
	 */
	// static long initialTrackingNumber = 61290100103531525410L;
	
	static String initialTrackingNumber = "794827712677";
	static boolean incrementTrackingNumber = false;
	static int interval = 30;

	static boolean searchParameterOnce = false;
	public static String searchParameterList[] = new String[] {
			// "CDOExists",
			// "hasAssociatedShipments",
			/*// For the shipment facts tab.
			"pickupDt",
			"displayShipDateTime",
			"billofLadingNbrList",
			"hasBillPresentment",
			"clearanceDetailLink",
			"codDetail",
			"cerNbrList",
			"deliveryAttempt",
			"deptNbrList",
			"dimensions",
			"displayEstDeliveryDateTime",
			"doorTagNbrList",
			"piecesPerShipment",
			"invoiceNbrList",
			"masterTrackingNbr",
			"statusWithDetails",
			"packaging",
			"purchaseOrderNbrList",
			"referenceList",
			"rmaList",
			"displayShipDateTime",
			"serviceDesc",
			"referenceDescList",
			"shipperRefList",
			"specialHandlingServicesList",
			"displayStdTransitDate",
			"terms",
			"totalPiecesPerMPSShipment",
			"totalPieces",
			"displayTotalWgt",
			"totalTransitMiles",
			"trackingNumber",
			"displayPkgWgt",
			
			"dutiesAndTaxesDesc",
			"hasBillOfLadingImage",
			"transportationDesc",
			"billNoteMsg",
			"codReturnTrackNbr",
			"milesToDestination",
			"origPieceCount",
			"originalCharges",
			"partNbrList",
			"partnerCarrierNbrList",
			"skuItemUpcCdList",
			"shipmentIdList",
			"shipmentType",
			"goodsClassificationCD",
			"tcnList",
			"isAnticipatedShipDtLabel",
			"isShipPickupDtLabel",
			"isActualPickupLabel",
			"isOrderReceivedLabel",
			"isEstimatedDeliveryDtLabel",
			"isDeliveryDtLabel",
			"isActualDeliveryDtLabel",
			*/
			// "originTZ",
			// "destTZ"
			//consolidation tab
			// "isConsolidationDetail",
			// "consolidationDetails",

			// commodityInformation tab
			// "isCommodityInfoAvail",
			//"harmonizedCode", 
			// "countryOfManufacture" // part of commodityInfoList
			
			// advanced notice tab
			//"fxfAdvanceETA",
			//"fxfAdvanceReason",
			//"fxfAdvanceStatusCode",
			//"fxfAdvanceStatusDesc",
			
			//multistatus tab
			//"isMultiStat",
			//"multiSta",
			
			// returns tab
			//"hasAssociatedReturnShipments",
			
			// for multi piece shipment table.
			// "hasAssociatedShipments"
			
			
			// BANNERS AND ALERTS
			// door tag banner
			//"doorTagNbrList",
			
			//smart post banner
			
			// exceptions
			// "excepReasonList",
			// "excepActionList",
			
			// Info Banner
			// "serviceCommitMessage",
			// Note: should not be in label created state. ex: serviceCommitMessageType: "SHIPMENT_LABEL_CREATED"
			// label created banner
			// Note: should only be in label created state. ex: serviceCommitMessageType: "SHIPMENT_LABEL_CREATED"
			
			// Signature image
			// "isSignatureThumbnailAvailable",
			
			// Geo
			// "destinationGeoCoordinate"
			
			// duplicate results page.
			// "isDuplicate", 
			//"mainStatus"
			
			// "isStreetMapEligible"
			
			// "isInvalid"
			// "isDeliveryDtLabel",
			// "isOrderCompleteLabel"
			
			"isGMPS"
	};
	
	// used when searching for parameter that is part of an array in the response.
	public static String searchParameterDeepList[][] = new String[][] {
		/*	{"scanEventList", "rtrnShprTrkNbr"}, 
			{"scanEventList", "statusExceptionCode"},
			{"scanEventList", "isException"},
			{"scanEventList", "isClearanceDelay"},
			{"scanEventList", "isDelivered"},
			{"scanEventList", "isDelException"},*/
		// {"excepReasonList", "filler value"},
		// {"excepActionList", "filler value"}
	};
	
	@BeforeClass
	public void beforeClass() {
		Environment.SetLevelsToTest(LevelsToTest);
		API_Functions.General_API_Calls.setPrintOutAPICallFlag(false); /// true false
		API_Functions.General_API_Calls.setPrintOutFullResponseFlag(false);
		Helper_Functions.TestingData = Helper_Functions.DataDirectory + "\\TestingDataFullList.xls";
	}

	@DataProvider (parallel = true)
	public static Iterator<Object[]> dp(Method m) {
		List<Object[]> data = new ArrayList<Object[]>();

		for (int i = 0; i < Environment.LevelsToTest.length(); i++) {
			String Level = String.valueOf(Environment.LevelsToTest.charAt(i));
			// Only when updating the full list
			Data_Structures.Shipment_Data.setTrackingFilePathName("TrackingNumbersL" + Level + "FullList.xls");
			
			boolean fileToLarge = Helper_Functions.CheckFileToLarge(Shipment_Data.getTrackingFilePath(Level));
			if (fileToLarge) {
				return null;
			}
			
			String Tracking_List_Array[] = new String[interval];
			int pos = 0;
			String userToUse = "L" + Level + "ATRKLARGE";// "L" + Level + "ATRKLARGE" L2ATRK010620T160505cpjj
			// String users[] = {"L7ATRK011720T140730kvws", "L7ATRK011720T140730oaqo", "L7ATRK011720T140730vfxp", "L7ATRK011720T140730yztj"};
			userToUse = "L2ATRK122719T113340hzyz";
			String userCookies = "";
			// if want to add tracking numbers to a user.
			userCookies = LoginWithUser(userToUse, Level);
			/*if (Helper_Functions.isNullOrUndefined(userCookies)) {
				return data.iterator();
			}*/
			switch (m.getName()) {
			case "Tracking_Number_Search_Deep_Update":
			case "Tracking_Number_Search":
				// Used to auto add tracking numbers to a user.
				if (!Helper_Functions.isNullOrUndefined(userCookies)) {
					// print out users current shipment count on gui
					int shipmentCount = TRKC.tracking_profile.checkTotalNumberOfShipments(userToUse, userCookies);
					System.out.println('\n' + userToUse + " has a total of " + shipmentCount + " shipments. - " + initialTrackingNumber + "  ");
				}
				
				long iterationModifier =  (interval * 30);
				String sumToAdd;
				if (incrementTrackingNumber) {
					sumToAdd = "1";
				} else {
					// MINUS does not work, need to fix
					sumToAdd = "-1";
				}
				
				BigInteger b = new BigInteger(sumToAdd);
	  
				for (int track = 0; track < iterationModifier; track++) {
					BigInteger a = new BigInteger(initialTrackingNumber);
					BigInteger sum = a.add(b);
					initialTrackingNumber = sum.toString();
					Tracking_List_Array[pos] = initialTrackingNumber;
					pos++;
					if (pos == interval) {
						data.add(new Object[] { Level, Tracking_List_Array, userCookies});
						Tracking_List_Array = new String[interval];
						pos = 0;
					}
				}
				break;
					
			case "Tracking_List_Number_Search":

				Data_Structures.Shipment_Data.setTrackingFilePathName("TrackingNumbersL" + Level + "FullList.xls");
				Shipment_Data Shipment_Info_Array[] = Data_Structures.Shipment_Data.getTrackingDetails(Level);
				for (Shipment_Data Shipment_Info : Shipment_Info_Array) {
					Tracking_List_Array[pos] = String.valueOf(Shipment_Info.Tracking_Number);
					pos++;
					if (pos == interval) {
						// add object and reset array
						data.add(new Object[] { Level, Tracking_List_Array, userCookies});
						Tracking_List_Array = new String[interval];
						pos = 0;
					}
				}
				break;
			}
		}

		SupportClasses.Helper_Functions.LimitDataProvider(m.getName(), -1, data);
		return data.iterator();
	}

	@Test(dataProvider = "dp", enabled = true, invocationCount = 21000) // , invocationCount = 300
	public static void Tracking_Number_Search(String Level, String Tracking_List_Array[], String userCookies) {

		try {
			// String TrackingPackagesResponse = TRKC.tracking_packages.TrackingPackagesRequest(userCookies, Tracking_List_Array);
			String TrackingPackagesResponse = TRKC.tracking_packages.TrackingPackagesRequest("", Tracking_List_Array);
			// System.out.println(TrackingPackagesResponse);
			String TrackingPackageList = "packageList\":";

			String SingleTrackingResponse = TRKC.tracking_packages.getSingleTrackingResponse(TrackingPackagesResponse);

			if (SingleTrackingResponse == null || SingleTrackingResponse.contentEquals("")) {
				throw new Exception("TrackingPackagesResponse is not parseable.   TrackingPackagesResponse - " + TrackingPackagesResponse);
			}

			// Remove the start of the response and just leave the packageList
			TrackingPackagesResponse = TrackingPackagesResponse.substring(
					TrackingPackagesResponse.indexOf(TrackingPackageList) + TrackingPackageList.length(),
					TrackingPackagesResponse.length());

			for (int pos = 0; pos < Tracking_List_Array.length; pos++) {
				SingleTrackingResponse = TRKC.tracking_packages.getSingleTrackingResponse(TrackingPackagesResponse);
				if (SingleTrackingResponse == null) {
					break;
				}
				String trackingQualifier = API_Functions.General_API_Calls.ParseStringValue(SingleTrackingResponse,
						"trackingQualifier");
				if (!Helper_Functions.isNullOrUndefined(trackingQualifier)) {
					String trackingNumber = API_Functions.General_API_Calls.ParseStringValue(SingleTrackingResponse,
							"trackingNbr");
					String trackingCarrierCd = API_Functions.General_API_Calls.ParseStringValue(SingleTrackingResponse,
							"trackingCarrierCd");
					
					checkTrackingFlags(SingleTrackingResponse, trackingNumber);
					
					// {"stdTransitDate":"","displayStdTransitDate":""}
					// displayEstDeliveryTm: "8:00 pm" SEAN
					/*String searchedValue1 = API_Functions.General_API_Calls.ParseStringValue(SingleTrackingResponse, "displayEstDeliveryTm");
					String searchedValue2 = API_Functions.General_API_Calls.ParseStringValue(SingleTrackingResponse, "standardTransitDate");
					if (!searchedValue1.contentEquals("") &&
							!searchedValue1.contentEquals("Pending") &&
							searchedValue2.contentEquals("{\"stdTransitDate\":\"\",\"displayStdTransitDate\":\"\"}")) {
						System.err.println(trackingNumber);
					}*/
					
					String Details[][] = new String[][] { { "TRACKING_NUMBER", trackingNumber },
							{ "TRACKING_QUALIFIER", trackingQualifier },
							{ "TRACKING_CARRIER", trackingCarrierCd }};
							
					// Details = Arrays.copyOf(Details, Details.length + 1);
					// this structure is slightly different then the track packages call.
					// Details[Details.length - 1] = new String[] {"TrackPackagesResponse", SingleTrackingResponse};	
							
					String ShipElement[] = {"SHIP_DATE", ""};
					Details = Arrays.copyOf(Details, Details.length + 1);
					ShipElement[1] = API_Functions.General_API_Calls.ParseStringValue(SingleTrackingResponse, "shipDt");
					Details[Details.length - 1] = ShipElement;

					String DeliveryElement[] = {"DELIVERY_DATE", ""};
					Details = Arrays.copyOf(Details, Details.length + 1);
					DeliveryElement[1] = API_Functions.General_API_Calls.ParseStringValue(SingleTrackingResponse, "actDeliveryDt");
					Details[Details.length - 1] = DeliveryElement;
					
					String ServiceElement[] = {"SERVICE", ""};
					Details = Arrays.copyOf(Details, Details.length + 1);
					ServiceElement[1] = API_Functions.General_API_Calls.ParseStringValue(SingleTrackingResponse, "serviceCD");
					Details[Details.length - 1] = ServiceElement;
					
					String StatusElement[] = {"STATUS", ""};
					Details = Arrays.copyOf(Details, Details.length + 1);
					StatusElement[1] = API_Functions.General_API_Calls.ParseStringValue(SingleTrackingResponse, "keyStatus");
					Details[Details.length - 1] = StatusElement;

					String FileName = Shipment_Data.getTrackingFilePath(Level);
					Helper_Functions.WriteToExcel(FileName, "L" + Level, Details, 1);
					
					// print out the tracking number
					// String sig = API_Functions.General_API_Calls.ParseStringValue(SingleTrackingResponse, "signatureRequired");
					// String date = API_Functions.General_API_Calls.ParseStringValue(SingleTrackingResponse, "displayEstDeliveryDt");
					// Helper_Functions.PrintOut(trackingNumber + " with signature of _" + sig + "_ delivery date of _" + date);
					// Helper_Functions.PrintOut(trackingNumber + " ");
					
					if (!Helper_Functions.isNullOrUndefined(userCookies)) {
						addTrackingNumberToUser(trackingNumber, trackingQualifier, trackingCarrierCd, Level, userCookies);
					}
					
					// visual count when tracking numbers added.
					// System.out.print('+');
					// System.out.print(trackingNumber + ", ");
					
				}

				TrackingPackagesResponse = TrackingPackagesResponse.replace(SingleTrackingResponse, "");
			}

			// Helper_Functions.PrintOut(Range + ":  " + TrackingNumbersFound);

		} catch (Exception e) {
			e.printStackTrace();
			Assert.fail(e.getCause().toString());
		}
	}

	@Test(dataProvider = "dp", enabled = false)
	public static void Tracking_List_Number_Search(String Level, String Tracking_List_Array[], String userCookies) {
		try {
			// when attempting in the logged in state.
			// String TrackingPackagesResponse = TRKC.tracking_packages.TrackingPackagesRequest(userCookies, Tracking_List_Array);
			
			String TrackingPackagesResponse = TRKC.tracking_packages.TrackingPackagesRequest(userCookies, Tracking_List_Array);

			String TrackingPackageList = "packageList\":";

			String SingleTrackingResponse = TRKC.tracking_packages.getSingleTrackingResponse(TrackingPackagesResponse);

			if (SingleTrackingResponse == null || SingleTrackingResponse.contentEquals("")) {
				throw new Exception("TrackingPackagesResponse is not parseable.");
			}

			// Remove the start of the response and just leave the packageList
			TrackingPackagesResponse = TrackingPackagesResponse.substring(
					TrackingPackagesResponse.indexOf(TrackingPackageList) + TrackingPackageList.length(),
					TrackingPackagesResponse.length());

			for (int pos = 0; pos < Tracking_List_Array.length; pos++) {
				SingleTrackingResponse = TRKC.tracking_packages.getSingleTrackingResponse(TrackingPackagesResponse);

				if (SingleTrackingResponse == null) {
					// if the response does not contain tracking lists.
					break;
				}
				
				String trackingQualifier = API_Functions.General_API_Calls.ParseStringValue(SingleTrackingResponse,
						"trackingQualifier");
				if (!Helper_Functions.isNullOrUndefined(trackingQualifier)) {
					String trackingNumber = API_Functions.General_API_Calls.ParseStringValue(SingleTrackingResponse,
							"trackingNbr");
					checkTrackingFlags(SingleTrackingResponse, trackingNumber);
				}

				TrackingPackagesResponse = TrackingPackagesResponse.replace(SingleTrackingResponse, "");
			}

		} catch (Exception e) {
			e.printStackTrace();
			Assert.fail(e.getCause().toString());
		}
	}

	@Test(dataProvider = "dp", enabled = false, invocationCount = 80000) // , invocationCount = 300
	public static void Tracking_Number_Search_Deep_Update(String Level, String Tracking_List_Array[], String userCookies) {

		CopyOnWriteArrayList<String> trackingListAdded = new CopyOnWriteArrayList<String>();
		
		try {
			// String TrackingPackagesResponse = TRKC.tracking_packages.TrackingPackagesRequest(userCookies, Tracking_List_Array);
			String TrackingPackagesResponse = TRKC.tracking_packages.TrackingPackagesRequest("", Tracking_List_Array);
			// System.out.println(TrackingPackagesResponse);
			String TrackingPackageList = "packageList\":";

			String SingleTrackingResponse = TRKC.tracking_packages.getSingleTrackingResponse(TrackingPackagesResponse);

			if (SingleTrackingResponse == null || SingleTrackingResponse.contentEquals("")) {
				throw new Exception("TrackingPackagesResponse is not parseable.   TrackingPackagesResponse - " + TrackingPackagesResponse);
			}

			// Remove the start of the response and just leave the packageList
			TrackingPackagesResponse = TrackingPackagesResponse.substring(
					TrackingPackagesResponse.indexOf(TrackingPackageList) + TrackingPackageList.length(),
					TrackingPackagesResponse.length());

			for (int pos = 0; pos < Tracking_List_Array.length; pos++) {
				SingleTrackingResponse = TRKC.tracking_packages.getSingleTrackingResponse(TrackingPackagesResponse);
				if (SingleTrackingResponse == null) {
					break;
				}
				String trackingQualifier = API_Functions.General_API_Calls.ParseStringValue(SingleTrackingResponse,
						"trackingQualifier");
				if (!Helper_Functions.isNullOrUndefined(trackingQualifier)) {
					String trackingNumber = API_Functions.General_API_Calls.ParseStringValue(SingleTrackingResponse,
							"trackingNbr");
					
					// checkTrackingFlags(SingleTrackingResponse, trackingNumber);
					
					Tracking_Data_Update.Tracking_Number_Update(Level, trackingNumber, trackingQualifier, "", "");
					trackingListAdded.add(trackingNumber);
				}

				TrackingPackagesResponse = TrackingPackagesResponse.replace(SingleTrackingResponse, "");
			}
			
			Helper_Functions.PrintOut(trackingListAdded.size() + " tracking numbers found. " + trackingListAdded.toString());

		} catch (Exception e) {
			e.printStackTrace();
			Assert.fail(e.getCause().toString());
		}
	}

	
	public static void addTrackingNumberToUser(String trackingNumber, String trackingQualifier,
			String trackingCarrierCd, String Level, String cookie) {
		// add tracking number to user.
		try {
			Shipment_Data Shipment_Info = new Shipment_Data();
			Shipment_Info.Tracking_Number = trackingNumber;
			Shipment_Info.TRACKING_QUALIFIER = trackingQualifier;
			Shipment_Info.TRACKING_CARRIER = trackingCarrierCd;
			Test_Data_Generation.Add_Tracking_Numbers_To_User.Add_Tracking_Number(Level, cookie, Shipment_Info);
		} catch (Exception e) {
			Helper_Functions.PrintOut("Error adding tracking number " + trackingNumber + " to user.");
		}
	}

	public static boolean checkTrackingFlags(String trackPackagesResponse, String trackingNumber) {
		boolean foundValue = false;
		int MatchesExpected = 2; // update with the number of matches expected.
		int localMatches = 0;
		String FoundResponse = "";
		
		if (!Helper_Functions.isNullOrUndefined(trackPackagesResponse)) {
			int empty = 0;
			for (int pos = 0; pos < searchParameterList.length; pos++) {
				String searchParameter = searchParameterList[pos];
				String searchedValue = API_Functions.General_API_Calls.ParseStringValue(trackPackagesResponse, searchParameter);
				if ( !Helper_Functions.isNullOrUndefined(searchedValue)
						&& !searchedValue.contentEquals("false")
						&& !searchedValue.contentEquals("[\"\"]")
						&& !searchedValue.contentEquals("0")
						&& !searchedValue.contentEquals("Pending")) {
					
					if (searchedValue.contentEquals("")) {
						empty++;
					}
					
					if (!Helper_Functions.isNullOrUndefined(FoundResponse)) {
						FoundResponse += "\n_ param:" + searchParameter + "_ val:" + searchedValue;
						localMatches++;
					} else {
						FoundResponse = "TrkNbr_" + trackingNumber + "_ param:" + searchParameter + "_ val:" + searchedValue;
						localMatches++;
					}

					foundValue = true;

					// specific to MPS
					if (searchParameterList[pos].contentEquals("hasAssociatedShipments")) {
						String Details[][] = new String[][] { { "TRACKING_NUMBER", trackingNumber }};
						String trackingQualifier = API_Functions.General_API_Calls.ParseStringValue(trackPackagesResponse,
									"trackingQualifier");
						String trackingCarrierCd = API_Functions.General_API_Calls.ParseStringValue(trackPackagesResponse,
									"trackingCarrierCd");
						Details = TRKC.associated_shipments.getAssociatedShipment(Details, trackingNumber, trackingQualifier, trackingCarrierCd);
						// DO associated shipment call
					}
						
					if (searchParameterOnce) {
						searchParameterList[pos] = null;
					}
				}
			}
			
			
			for (int pos = 0; pos < searchParameterDeepList.length; pos++) {
				if (!Helper_Functions.isNullOrUndefined(searchParameterDeepList[pos])) {
					String searchListParameter = searchParameterDeepList[pos][0];
					String searchParameter = searchParameterDeepList[pos][1];
					
					String searchedValueArray = API_Functions.General_API_Calls.ParseStringValue(trackPackagesResponse,
							searchListParameter);
					String tempSearchedValueArray = searchedValueArray;
					
					// make sure not null and also no [""] as an empty array.
					if (!Helper_Functions.isNullOrUndefined(tempSearchedValueArray) && !tempSearchedValueArray.contentEquals("[\"\"]")) {
						
						if (tempSearchedValueArray.contains("\",")) {
							// used to check if array has multiple values.
							System.err.println("     Tracking_Number _" + trackingNumber + "_ param: " + searchListParameter + " - " + tempSearchedValueArray);
						} else {
							// DELETE LATER
							System.err.println(tempSearchedValueArray);
						}
						
						while (tempSearchedValueArray.contains(searchParameter)) {
							if (tempSearchedValueArray.contains(searchParameter)) {
								String searchedValue = API_Functions.General_API_Calls.ParseStringValue(tempSearchedValueArray, searchParameter);
								if (!Helper_Functions.isNullOrUndefined(searchedValue) && !searchedValue.contentEquals("false")
									&& !searchedValue.contentEquals("[\"\"]")
									&& !searchedValue.contentEquals("0")) {
									System.err.println("     Tracking_Number _" + trackingNumber + "_ param: " + searchListParameter + " - " + searchParameter
											+ "_ value: " + searchedValue);
									foundValue = true;
									
									if (searchParameterOnce) {
										searchParameterDeepList[pos] = null;
									}
								}
							} 
							
							tempSearchedValueArray = tempSearchedValueArray.replaceFirst(searchParameter, "");
						}
					}
				}
			}
		}
		if (foundValue) {
			System.err.println(FoundResponse);
		}
		
		return foundValue;
	}

	// @Test (priority = 10)
	public void WriteRemainder() {
		int size = TrackingListForFileWrite.size();
		if (size > 0) {
			Shipment_Data S_D_A[] = new Shipment_Data[0];
			for (Shipment_Data SD : TrackingListForFileWrite) {
				S_D_A = Arrays.copyOf(S_D_A, S_D_A.length + 1);
				S_D_A[S_D_A.length - 1] = SD;
				TrackingListForFileWrite.remove(SD);
			}

			Data_Structures.Shipment_Data.writeShipments_Data_To_Excel(S_D_A);
		}

	}

	public static Shipment_Data updateTrackingPackagesRequest(Shipment_Data Shipment_Info,
			String TrackingPackagesResponse) throws Exception {
		String Status = API_Functions.General_API_Calls.ParseStringValue(TrackingPackagesResponse, "keyStatus");

		Status = Status.replaceAll(" ", "_").toUpperCase();
		Shipment_Info.setStatus(Status);

		String Service = API_Functions.General_API_Calls.ParseStringValue(TrackingPackagesResponse, "serviceCD");
		String trackingQualifier = API_Functions.General_API_Calls.ParseStringValue(TrackingPackagesResponse,
				"trackingQualifier");
		Shipment_Info.setTRACKING_QUALIFIER(trackingQualifier);

		if (!Service.contentEquals("")) {
			Shipment_Info.setService(Service);

			String TRACKING_CARRIER = API_Functions.General_API_Calls.ParseStringValue(TrackingPackagesResponse,
					"trackingCarrierCd");
			Shipment_Info.setTRACKING_CARRIER(TRACKING_CARRIER);

			String shipDate = API_Functions.General_API_Calls.ParseStringValue(TrackingPackagesResponse, "shipDt");
			Shipment_Info.setShipDate(shipDate);

			String estDeliveryDate = API_Functions.General_API_Calls.ParseStringValue(TrackingPackagesResponse,
					"displayEstDeliveryDateTime");
			Shipment_Info.setEstDeliveryDate(estDeliveryDate);

			String isEstDelTmWindowLabel = API_Functions.General_API_Calls.ParseStringValue(TrackingPackagesResponse,
					"isEstDelTmWindowLabel");
			Shipment_Info.isEstDelTmWindowLabel = Boolean.parseBoolean(isEstDelTmWindowLabel);

			String estDelTmWindowStart = API_Functions.General_API_Calls.ParseStringValue(TrackingPackagesResponse,
					"estDelTmWindowStart");
			Shipment_Info.estDelTmWindowStart = estDelTmWindowStart;

			String isNonHistoricalEDTW = API_Functions.General_API_Calls.ParseStringValue(TrackingPackagesResponse,
					"isNonHistoricalEDTW");
			Shipment_Info.isNonHistoricalEDTW = Boolean.parseBoolean(isNonHistoricalEDTW);
		}
		return Shipment_Info;
	}

	// Will return true if tracking Qualifier is new.
	public static boolean checkAndAddTrackingQualifier(String trackingQualifier) {
		boolean newTrackingFlag = false;
		TrackingNumberListLock.lock();
		if (TrackingList.size() == 0) {
			String Level = Environment.getInstance().getLevel();
			Shipment_Data Shipment_Info_Array[] = Data_Structures.Shipment_Data.getTrackingDetails(Level);
			for (Shipment_Data Shipment_Info : Shipment_Info_Array) {
				// Add the existing tracking qualifiers to list.
				TrackingList.add(Shipment_Info.TRACKING_QUALIFIER);
			}
		}

		if (!TrackingList.contains(trackingQualifier)) {
			newTrackingFlag = true;
			TrackingList.add(trackingQualifier);
			Helper_Functions.PrintOut("New Tracking Added " + trackingQualifier + "              Total Tracking: "
					+ TrackingList.size() + "    " + (TrackingListForFileWrite.size() + 1), false);
		}
		TrackingNumberListLock.unlock();
		return newTrackingFlag;
	}

	public static Shipment_Data updateInflightDeliveryOptions(Shipment_Data Shipment_Info) throws Exception {
		String Response_CMDC = CMDC_Application.CMDC_Endpoints.inflightDeliveryOptions(Shipment_Info.Tracking_Number,
				Shipment_Info.Ship_Date, Shipment_Info.TRACKING_QUALIFIER);

		if (Response_CMDC.contains("\"status\":\"ENABLED\",\"type\":\"RESCHEDULE\"")) {
			Shipment_Info.RESCHEDULE = true;
		} else {
			Shipment_Info.RESCHEDULE = false;
		}

		if (Response_CMDC.contains("\"status\":\"ENABLED\",\"type\":\"REROUTE\"")) {
			Shipment_Info.REROUTE = true;
		} else {
			Shipment_Info.REROUTE = false;
		}

		if (Response_CMDC.contains("\"status\":\"ENABLED\",\"type\":\"REDIRECT_HOLD_AT_LOCATION\"")) {
			Shipment_Info.REDIRECT_HOLD_AT_LOCATION = true;
		} else {
			Shipment_Info.REDIRECT_HOLD_AT_LOCATION = false;
		}

		if (Response_CMDC.contains("\"status\":\"ENABLED\",\"type\":\"SIGNATURE_RELEASE\"")) {
			Shipment_Info.SIGNATURE_RELEASE = true;
		} else {
			Shipment_Info.SIGNATURE_RELEASE = false;
		}

		if (Response_CMDC.contains("\"status\":\"ENABLED\",\"type\":\"DELIVERY_INSTRUCTIONS\"")) {
			Shipment_Info.DELIVERY_INSTRUCTIONS = true;
		} else {
			Shipment_Info.DELIVERY_INSTRUCTIONS = false;
		}

		if (Response_CMDC.contains("\"status\":\"ENABLED\",\"type\":\"DELIVERY_SUSPENSIONS\"")) {
			Shipment_Info.DELIVERY_SUSPENSIONS = true;
		} else {
			Shipment_Info.DELIVERY_SUSPENSIONS = false;
		}

		return Shipment_Info;
	}
	
	public static String LoginWithUser (String userToUse, String Level) {
		int intLevel = Integer.parseInt(Level);
		
		User_Data userInfoArray[] = User_Data.Get_UserIds(intLevel);
		Environment.getInstance().setLevel(Level);
		for (User_Data User_Info : userInfoArray) {
			if (User_Info.USER_ID.contentEquals(userToUse)) {
				String Results[] = USRC.login.Login(User_Info.USER_ID, User_Info.PASSWORD);
				if (!Helper_Functions.isNullOrUndefined(Results) && !Helper_Functions.isNullOrUndefined(Results[0])) {
					return Results[0];
				}
			}
		}
		
		return null;
	}

/*	@AfterMethod
	public void UpdateExcelAfterMethod() {
		if (TrackingListForFileWrite.size() > 15) {
			UpdateFile();
		}
	}

	@AfterClass
	public void afterClass() {
		UpdateFile();
	}*/

/*	private void UpdateFile() {
		Shipment_Data S_D_A_1[] = new Shipment_Data[0];
		for (Shipment_Data SD : TrackingListForFileWrite) {
			S_D_A_1 = Arrays.copyOf(S_D_A_1, S_D_A_1.length + 1);
			S_D_A_1[S_D_A_1.length - 1] = SD;
			TrackingListForFileWrite.remove(SD);
		}

		Data_Structures.Shipment_Data.writeShipments_Data_To_Excel(S_D_A_1);
	}*/
}