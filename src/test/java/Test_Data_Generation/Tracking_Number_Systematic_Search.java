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
import org.testng.annotations.AfterClass;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Listeners;
import org.testng.annotations.Test;
import Data_Structures.Shipment_Data;
import Data_Structures.User_Data;
import SupportClasses.Environment;
import SupportClasses.Helper_Functions;

@Listeners(SupportClasses.TestNG_TestListener.class)

public class Tracking_Number_Systematic_Search {
	static String LevelsToTest = "7";
	static CopyOnWriteArrayList<String> TrackingList = new CopyOnWriteArrayList<String>();
	static CopyOnWriteArrayList<Shipment_Data> TrackingListForFileWrite = new CopyOnWriteArrayList<Shipment_Data>();
	public final static Lock TrackingNumberListLock = new ReentrantLock();
	/*
	 * L1 123456789012 
	 * L2 111111365885L 	794817289549L      794817253102L     794816036302L---
	 * L3 111111258998L 	794809944267L
	 * L4 111111120681L 	776515491451L
	 * L7 111112563080L 	777650739640L          777416008685L --
	 */
	static long Low = 777416281385L;
	static int interval = 30;
	static String User_Cookies = "";

	public static String searchParameterList[] = new String[] {
			// "doorTagNbrList",
			// "dutiesAndTaxesDesc",
			"hasBillOfLadingImage", "hasBillPresentment",
			// "transportationDesc",
			// "billNoteMsg",
			"codReturnTrackNbr", "cerNbrList", "destPieceCount", "docAWBNbr", "milesToDestination", "origPieceCount",
			"originalCharges", "partNbrList", "partnerCarrierNbrList", "skuItemUpcCdList",
			// "shipmentIdList",
			"shipmentType", "goodsClassificationCD", "tcnList",
			"receipientAddrQty",
			"isAnticipatedShipDtLabel"
			// "totalTransitMiles"
	};

	@BeforeClass
	public void beforeClass() {
		Environment.SetLevelsToTest(LevelsToTest);
		API_Functions.General_API_Calls.setPrintOutAPICallFlag(false); /// true false
		API_Functions.General_API_Calls.setPrintOutFullResponseFlag(false);
	}

	@DataProvider(parallel = true)
	public static Iterator<Object[]> dp(Method m) {
		List<Object[]> data = new ArrayList<Object[]>();

		for (int i = 0; i < Environment.LevelsToTest.length(); i++) {
			String Level = String.valueOf(Environment.LevelsToTest.charAt(i));
			int intLevel = Integer.parseInt(Level);
			// Only when updating the full list
			Data_Structures.Shipment_Data.setTrackingFilePathName("TrackingNumbersL" + Level + "FullList.xls");

			String Tracking_List_Array[] = new String[interval];
			int pos = 0;

			switch (m.getName()) {
			case "Tracking_Number_Search":
				// Used to auto add tracking numbers to a user.
				User_Data userInfoArray[] = User_Data.Get_UserIds(intLevel);
				Environment.getInstance().setLevel(Level);

				String userToUse = "L" + Level + "ATRKLARGE";// "L" + Level + "ATRKLARGE" L2ATRK010620T160505cpjj
				userToUse = "L" + Level + "ATRKExtraordinary"; /// L2ATRKExtraordinary  L3C5150067600
				for (User_Data User_Info : userInfoArray) {
					if (User_Info.USER_ID.contentEquals(userToUse)) {
						String Results[] = USRC.login.Login(User_Info.USER_ID, User_Info.PASSWORD);
						if (!Helper_Functions.isNullOrUndefined(Results)
								&& !Helper_Functions.isNullOrUndefined(Results[0])) {
							User_Cookies = Results[0];
							// print out users current shipment count on gui
							int shipmentCount = TRKC.tracking_profile.checkTotalNumberOfShipments(User_Info.USER_ID,
									User_Cookies);
							long lastIteration = Low - (interval * 30);
							System.out.println('\n' + User_Info.USER_ID + " has a total of " + shipmentCount + " shipments. - " + lastIteration + "  ");
							// Helper_Functions.PrintOut("\n" + User_Info.USER_ID + " has a total of " + shipmentCount + " shipments. - " + lastIteration);
							for (long track = Low; track > lastIteration; track--) {
								boolean useRandomNumber = false;
								if (useRandomNumber) {
									long leftLimit = 1111L;
									long rightLimit = 888888888888L;
									long generatedLong = leftLimit + (long) (Math.random() * (rightLimit - leftLimit));
									Tracking_List_Array[pos] = String.valueOf(generatedLong);
								} else {
									Tracking_List_Array[pos] = String.valueOf(track);
								}
								pos++;
								if (pos == interval) {
									data.add(new Object[] { Level, Tracking_List_Array });
									Tracking_List_Array = new String[interval];
									pos = 0;
								}
								Low--;
							}
							break;
						}
					}
				}
				// break from Tracking_Number_Search
				break;
			case "Tracking_List_Number_Search":
				Data_Structures.Shipment_Data.setTrackingFilePathName("TrackingNumbersL" + Level + "FullList.xls");
				Shipment_Data Shipment_Info_Array[] = Data_Structures.Shipment_Data.getTrackingDetails(Level);
				for (Shipment_Data Shipment_Info : Shipment_Info_Array) {
					Tracking_List_Array[pos] = String.valueOf(Shipment_Info.Tracking_Number);
					pos++;
					if (pos == interval) {
						// add object and reset array
						data.add(new Object[] { Level, Tracking_List_Array });
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

	@Test(dataProvider = "dp", enabled = true, invocationCount = 80000) // , invocationCount = 300
	public static void Tracking_Number_Search(String Level, String Tracking_List_Array[]) {

		String Range = Tracking_List_Array[0] + "-" + Tracking_List_Array[Tracking_List_Array.length - 1];
		String TrackingNumbersFound = "";
		try {
			String TrackingPackagesResponse = TRKC.tracking_packages.TrackingPackagesRequest(Tracking_List_Array);

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

				String trackingQualifier = API_Functions.General_API_Calls.ParseStringValue(SingleTrackingResponse,
						"trackingQualifier");
				if (!Helper_Functions.isNullOrUndefined(trackingQualifier)) {
					String trackingNumber = API_Functions.General_API_Calls.ParseStringValue(SingleTrackingResponse,
							"trackingNbr");
					String trackingCarrierCd = API_Functions.General_API_Calls.ParseStringValue(SingleTrackingResponse,
							"trackingCarrierCd");

					if (checkTrackingFlags(SingleTrackingResponse, trackingNumber)) {
						// Test_Data_Update.Tracking_Data_Update.Tracking_Number_Update(Level,
						// trackingNumber, trackingQualifier, "", "");
						String Details[][] = new String[][] { { "TRACKING_NUMBER", trackingNumber },
								{ "TRACKING_QUALIFIER", trackingQualifier }, { "TRACKING_CARRIER", trackingCarrierCd },
								{ "TrackPackagesResponse", SingleTrackingResponse },
								{ "SHIP_DATE",
										API_Functions.General_API_Calls.ParseStringValue(SingleTrackingResponse,
												"shipDt") },
								{ "DELIVERY_DATE",
										API_Functions.General_API_Calls.ParseStringValue(SingleTrackingResponse,
												"displayActDeliveryDt") },
								{ "SERVICE",
										API_Functions.General_API_Calls.ParseStringValue(SingleTrackingResponse,
												"trackingCarrierDesc") },
								{ "STATUS", API_Functions.General_API_Calls.ParseStringValue(SingleTrackingResponse,
										"keyStatus") } };

						// Helper_Functions.PrintOut(Arrays.deepToString(Details));
						String FileName = Shipment_Data.getTrackingFilePath(Level);
						Helper_Functions.WriteToExcel(FileName, "L" + Level, Details, 1);
					}
/*					String consolidationDetails = API_Functions.General_API_Calls
							.ParseStringValue(SingleTrackingResponse, "consolidationDetails");
					if (consolidationDetails != null && consolidationDetails.contentEquals("true")) {
						Helper_Functions.PrintOut(trackingNumber + " is consolidated.");
					}*/

					TrackingNumbersFound += "  " + trackingNumber;

					addTrackingNumberToUser(trackingNumber, trackingQualifier, trackingCarrierCd, Level, User_Cookies);
					// visual count when tracking numbers added.
					System.out.print('+');
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
	public static void Tracking_List_Number_Search(String Level, String Tracking_List_Array[]) {
		try {
			String TrackingPackagesResponse = TRKC.tracking_packages.TrackingPackagesRequest(Tracking_List_Array);

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

	public static boolean checkTrackingFlags(String trackPackagesResponse, String tracking_Number) {
		boolean writeTrackingNumberToFile = false;
		for (int pos = 0; pos < searchParameterList.length; pos++) {
			String searchParameter = searchParameterList[pos];
			if (!Helper_Functions.isNullOrUndefined(trackPackagesResponse)) {
				String searchedValue = API_Functions.General_API_Calls.ParseStringValue(trackPackagesResponse,
						searchParameter);
				if (!Helper_Functions.isNullOrUndefined(searchedValue) && !searchedValue.contentEquals("false")
						&& !searchedValue.contentEquals("") && !searchedValue.contentEquals("[\"\"]")
						&& !searchedValue.contentEquals("0")) {
					System.err.println("     Tracking_Number _" + tracking_Number + "_ param: " + searchParameter
							+ "_ value: " + searchedValue);
					writeTrackingNumberToFile = true;
					searchParameterList[pos] = null;
				}
			}
		}

		return writeTrackingNumberToFile;
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

	@AfterMethod
	public void UpdateExcelAfterMethod() {
		if (TrackingListForFileWrite.size() > 15) {
			UpdateFile();
		}
	}

	@AfterClass
	public void afterClass() {
		UpdateFile();
	}

	private void UpdateFile() {
		Shipment_Data S_D_A_1[] = new Shipment_Data[0];
		for (Shipment_Data SD : TrackingListForFileWrite) {
			S_D_A_1 = Arrays.copyOf(S_D_A_1, S_D_A_1.length + 1);
			S_D_A_1[S_D_A_1.length - 1] = SD;
			TrackingListForFileWrite.remove(SD);
		}

		Data_Structures.Shipment_Data.writeShipments_Data_To_Excel(S_D_A_1);
	}

}