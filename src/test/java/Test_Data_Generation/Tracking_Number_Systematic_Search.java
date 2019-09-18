package Test_Data_Generation;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import org.testng.Assert;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Listeners;
import org.testng.annotations.Test;
import Data_Structures.Shipment_Data;
import SupportClasses.Environment;
import SupportClasses.Helper_Functions;

@Listeners(SupportClasses.TestNG_TestListener.class)

public class Tracking_Number_Systematic_Search {
	static String LevelsToTest = "3";
	static CopyOnWriteArrayList<String> TrackingList = new CopyOnWriteArrayList<String>();
	static CopyOnWriteArrayList<Shipment_Data> TrackingListForFileWrite = new CopyOnWriteArrayList<Shipment_Data>();
	public final static Lock TrackingNumberListLock = new ReentrantLock();

	// 111111365885L                   L2    
	// 111111258699L   794950870011L   L3
	//  794809944267L
	// static long low = 794948139575L;
	static long High = 794809941268L;  
	static int interval = 3000;

	@BeforeClass
	public void beforeClass() {
		Environment.SetLevelsToTest(LevelsToTest);
		API_Functions.General_API_Calls.PrintOutAPICall = false;
		API_Functions.General_API_Calls.PrintOutFullResponse = false;
	}
	
	@AfterMethod
	public void UpdateExcelAfterMethod() {
		if (TrackingListForFileWrite.size() > 10) {
			int size = TrackingListForFileWrite.size();
			Shipment_Data S_D_A[] = new Shipment_Data [size];
			for (int i = 0; i < size; i++) {
				S_D_A[i] = TrackingListForFileWrite.get(0);
				TrackingListForFileWrite.remove(0);
			}
			Data_Structures.Shipment_Data.writeShipments_Data_To_Excel(S_D_A);
		}
	}


	@DataProvider (parallel = true)
	public static Iterator<Object[]> dp(Method m) {
		List<Object[]> data = new ArrayList<Object[]>();

		for (int i = 0; i < Environment.LevelsToTest.length(); i++) {
			String Level = String.valueOf(Environment.LevelsToTest.charAt(i));
			switch (m.getName()) {
			case "Tracking_Number_Search_V2":
				Shipment_Data Shipment_Data_Array[] = new Shipment_Data[30];
				int pos = 0;
				for (long track = High; track > High - interval; track--) {
					Shipment_Data_Array[pos] = new Shipment_Data();
					Shipment_Data_Array[pos].Tracking_Number = String.valueOf(track);
					pos++;
					if (pos == 30) {
						data.add(new Object[] { Level, Shipment_Data_Array });
						Shipment_Data_Array = new Shipment_Data[30];
						pos = 0;
					}
				}
				break;

			}
		}

		return data.iterator();
	}

	@Test(dataProvider = "dp", enabled = true)
	public static void Tracking_Number_Search_V2(String Level, Shipment_Data Shipment_Info_Array[]){
		String Range = Shipment_Info_Array[0].Tracking_Number + "-" + Shipment_Info_Array[Shipment_Info_Array.length - 1].Tracking_Number;
		String TrackingFound = Range + "-- New Tracking Numbers: ";
		try {
			String TrackingArray[] = new String[Shipment_Info_Array.length];
			for(int cnt = 0; cnt < Shipment_Info_Array.length; cnt++) {
				TrackingArray[cnt] = Shipment_Info_Array[cnt].Tracking_Number;
			}
			String TrackingPackagesResponse = TRKC.TRKC_Endpoints.TrackingPackagesRequest(TrackingArray);
			
			String TrackingPackageList = "packageList\":";
			String TrackingStart = "{\"shipperAccountNumber\"";
			String TrackingEnd = "\"isSuccessful\":";
			
			if (!TrackingPackagesResponse.contains(TrackingStart) ||
					!TrackingPackagesResponse.contains(TrackingEnd) ||
					!TrackingPackagesResponse.contains(TrackingStart)) {
				throw new Exception("TrackingPackagesResponse is not parseable.");
			}
			
			//Remove the start of the response and just leave the packageList
			TrackingPackagesResponse = TrackingPackagesResponse.substring(TrackingPackagesResponse.indexOf(TrackingPackageList) + TrackingPackageList.length(), TrackingPackagesResponse.length());
			
			for (int pos = 0; pos < Shipment_Info_Array.length; pos++) {
				String SingleTrackingResponse = TrackingPackagesResponse.substring(TrackingPackagesResponse.indexOf(TrackingStart), TrackingPackagesResponse.indexOf(TrackingEnd) + TrackingEnd.length());
				Shipment_Info_Array[pos] = updateTrackingPackagesRequest(Shipment_Info_Array[pos], SingleTrackingResponse);
				if (SingleTrackingResponse.contains("\"CDOExists\":true")) {
					Shipment_Info_Array[pos] = updateInflightDeliveryOptions(Shipment_Info_Array[pos]);
				}
				
				String Service = Shipment_Info_Array[pos].Service;
				if (Service.contentEquals("")) {
					Shipment_Info_Array[pos] = null;
				}else if (checkAndAddTrackingQualifier(Shipment_Info_Array[pos].TRACKING_QUALIFIER)){
					TrackingFound+= Shipment_Info_Array[pos].Tracking_Number + ", ";
					TrackingListForFileWrite.add(Shipment_Info_Array[pos]);
				}else {
					Shipment_Info_Array[pos] = null;
				}
				
				TrackingPackagesResponse = TrackingPackagesResponse.replace(SingleTrackingResponse, "");
			}
			
			Helper_Functions.PrintOut(TrackingFound, false);
		}catch (Exception e) {
			e.printStackTrace();
 			Assert.fail(e.getCause().toString());
			Assert.fail();
		}
	}
	
	public static Shipment_Data updateTrackingPackagesRequest(Shipment_Data Shipment_Info, String TrackingPackagesResponse) throws Exception{		
		String Status = API_Functions.General_API_Calls.ParseStringValue(TrackingPackagesResponse, "keyStatus");
		
		Status = Status.replaceAll(" ", "_").toUpperCase();
		Shipment_Info.setStatus(Status);

		String Service = API_Functions.General_API_Calls.ParseStringValue(TrackingPackagesResponse, "serviceCD");
		String trackingQualifier = API_Functions.General_API_Calls.ParseStringValue(TrackingPackagesResponse, "trackingQualifier");
		Shipment_Info.setTRACKING_QUALIFIER(trackingQualifier);
		
		if (!Service.contentEquals("")) {
			Shipment_Info.setService(Service);
			
			String TRACKING_CARRIER = API_Functions.General_API_Calls.ParseStringValue(TrackingPackagesResponse, "trackingCarrierCd");
			Shipment_Info.setTRACKING_CARRIER(TRACKING_CARRIER);
				
			String shipDate = API_Functions.General_API_Calls.ParseStringValue(TrackingPackagesResponse, "shipDt");
			Shipment_Info.setShipDate(shipDate);
				
			String estDeliveryDate = API_Functions.General_API_Calls.ParseStringValue(TrackingPackagesResponse, "displayEstDeliveryDateTime");
			Shipment_Info.setEstDeliveryDate(estDeliveryDate);
				
			String isEstDelTmWindowLabel = API_Functions.General_API_Calls.ParseStringValue(TrackingPackagesResponse, "isEstDelTmWindowLabel");
			Shipment_Info.isEstDelTmWindowLabel = Boolean.parseBoolean(isEstDelTmWindowLabel);
				
			String estDelTmWindowStart = API_Functions.General_API_Calls.ParseStringValue(TrackingPackagesResponse, "estDelTmWindowStart");
			Shipment_Info.estDelTmWindowStart = estDelTmWindowStart;
				
			String isNonHistoricalEDTW = API_Functions.General_API_Calls.ParseStringValue(TrackingPackagesResponse, "isNonHistoricalEDTW");
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
			Helper_Functions.PrintOut(
					"New Tracking Added " + trackingQualifier + "              Total Tracking: " + TrackingList.size() + "    " + (TrackingListForFileWrite.size() + 1),
					false);
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

	
}