package Test_Data_Generation;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Listeners;
import org.testng.annotations.Test;
import Data_Structures.Shipment_Data;
import INET_Application.GroundCorpLoad;
import INET_Application.eMASS_Scans;
import SupportClasses.Environment;

@Listeners(SupportClasses.TestNG_TestListener.class)

public class Tracking_Data_Update {
	static String LevelsToTest = "3";
	private static boolean applyExpressPickupScanFlag = false;
	private static boolean applyGroundPickupScanFlag = false;
	
	@BeforeClass
	public void beforeClass() {
		Environment.SetLevelsToTest(LevelsToTest);
	}
	
	@DataProvider (parallel = true)
	public static Iterator<Object[]> dp(Method m) {
		List<Object[]> data = new ArrayList<Object[]>();
		
		for (int i=0; i < Environment.LevelsToTest.length(); i++) {
			String Level = String.valueOf(Environment.LevelsToTest.charAt(i));
			//int intLevel = Integer.parseInt(Level);
			//Based on the method that is being called the array list will be populated
			switch (m.getName()) {
		    	case "Tracking_Number_Update":
		    		Shipment_Data Shipment_Info_Array[] = Data_Structures.Shipment_Data.getTrackingDetails(Level);
		    		for (Shipment_Data Shipment_Info: Shipment_Info_Array) {
		    			if (Shipment_Info.Ship_Date.contentEquals("") || Shipment_Info.TRACKING_QUALIFIER.contentEquals("") ) {
		    				data.add(new Object[] {Level, Shipment_Info});
		    			}
		    			// uncomment out the below to update for all
		    			else {data.add(new Object[] {Level, Shipment_Info});}
		    		}
		    	break;
			}
		}
		return data.iterator();
	}

	@Test(dataProvider = "dp")
	public static void Tracking_Number_Update(String Level, Shipment_Data Shipment_Info){
		try {
			
			//String Response2 = CMDC_Application.CMDC_Endpoints.inflightDeliveryOptions(TrackingNumber);

			String Response_TRKC = TRKC_Application.TRKC_Endpoints.TrackingPackagesRequest(Shipment_Info.Tracking_Number);
			String Status = SupportClasses.General_API_Calls.ParseStringValue(Response_TRKC, "keyStatus");
			Status = Status.replaceAll(" ", "_").toUpperCase();
			Shipment_Info.setStatus(Status);

			String Service = SupportClasses.General_API_Calls.ParseStringValue(Response_TRKC, "serviceCD");
			if (!Service.contentEquals("")) {
				Shipment_Info.setService(Service);
				String TRACKING_CARRIER = SupportClasses.General_API_Calls.ParseStringValue(Response_TRKC, "trackingCarrierCd");
				Shipment_Info.setTRACKING_CARRIER(TRACKING_CARRIER);
				
				String shipDate = SupportClasses.General_API_Calls.ParseStringValue(Response_TRKC, "shipDt");
				Shipment_Info.setShipDate(shipDate);
				
				String estDeliveryDate = SupportClasses.General_API_Calls.ParseStringValue(Response_TRKC, "displayEstDeliveryDateTime");
				Shipment_Info.setEstDeliveryDate(estDeliveryDate);
				
				String trackingQualifier = SupportClasses.General_API_Calls.ParseStringValue(Response_TRKC, "trackingQualifier");
				Shipment_Info.setTRACKING_QUALIFIER(trackingQualifier);
				String Response_CMDC = CMDC_Application.CMDC_Endpoints.inflightDeliveryOptions(Shipment_Info.Tracking_Number, shipDate, trackingQualifier);
				
				if (Response_CMDC.contains("\"status\":\"ENABLED\",\"type\":\"RESCHEDULE\"")) {
					Shipment_Info.RESCHEDULE = true;
				}else {
					Shipment_Info.RESCHEDULE = false;
				}
				
				if (Response_CMDC.contains("\"status\":\"ENABLED\",\"type\":\"REROUTE\"")) {
					Shipment_Info.REROUTE = true;
				}else {
					Shipment_Info.REROUTE = false;
				}
				
				if (Response_CMDC.contains("\"status\":\"ENABLED\",\"type\":\"REDIRECT_HOLD_AT_LOCATION\"")) {
					Shipment_Info.REDIRECT_HOLD_AT_LOCATION = true;
				}else {
					Shipment_Info.REDIRECT_HOLD_AT_LOCATION = false;
				}
				
				if (Response_CMDC.contains("\"status\":\"ENABLED\",\"type\":\"SIGNATURE_RELEASE\"")) {
					Shipment_Info.SIGNATURE_RELEASE = true;
				}else {
					Shipment_Info.SIGNATURE_RELEASE = false;
				}
				
				if (Response_CMDC.contains("\"status\":\"ENABLED\",\"type\":\"DELIVERY_INSTRUCTIONS\"")) {
					Shipment_Info.DELIVERY_INSTRUCTIONS = true;
				}else {
					Shipment_Info.DELIVERY_INSTRUCTIONS = false;
				}
				
				if (Response_CMDC.contains("\"status\":\"ENABLED\",\"type\":\"DELIVERY_SUSPENSIONS\"")) {
					Shipment_Info.DELIVERY_SUSPENSIONS = true;
				}else {
					Shipment_Info.DELIVERY_SUSPENSIONS = false;
				}
				
				//try applying the pickup scan
				if (applyExpressPickupScanFlag && "IN_TRANSIT LABEL_CREATED".contains(Shipment_Info.Status)) {
					eMASS_Scans.eMASS_Pickup_Scan(Shipment_Info);
				}
				
			}else {
				//if (applyPickupScanFlag && Shipment_Info.Service.toLowerCase().contains("ground")) {
				if (applyGroundPickupScanFlag && Shipment_Info.Service.toLowerCase().contains("ground")) {
					String Tracking[] = new String[] {Shipment_Info.Tracking_Number};
					GroundCorpLoad.ValidateAndProcess(Tracking);
				}
				Shipment_Data cleared_Shipment_Info = new Shipment_Data();
				cleared_Shipment_Info.setTracking_Number(Shipment_Info.Tracking_Number);
				cleared_Shipment_Info.setUser_Info(Shipment_Info.User_Info);
				cleared_Shipment_Info.setService(Shipment_Info.Service);
				cleared_Shipment_Info.setStatus("Status_Not_Found");
				// make the update to the excel so know not working
				// the pickup may still be in progress and will update in transit/label created later
				cleared_Shipment_Info.writeShipment_Data_To_Excel(false);
				Assert.fail(cleared_Shipment_Info.Status);
			}

			Shipment_Info.writeShipment_Data_To_Excel(false);						
		}catch (Exception e) {
			e.printStackTrace();
 			Assert.fail(e.getCause().toString());
		}
	}
}