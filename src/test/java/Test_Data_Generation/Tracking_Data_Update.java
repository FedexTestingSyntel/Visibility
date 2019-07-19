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
import INET_Application.eMASS_Scans;
import SupportClasses.Environment;

@Listeners(SupportClasses.TestNG_TestListener.class)

public class Tracking_Data_Update {
	static String LevelsToTest = "2";
	
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
		    			if (Shipment_Info.Ship_Date.contentEquals("")) {
		    			//if (Shipment_Info.Tracking_Number.contentEquals("794947308987")) {
		    				data.add(new Object[] {Level, Shipment_Info});
		    			}
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
				String Opco = SupportClasses.General_API_Calls.ParseStringValue(Response_TRKC, "trackingCarrierCd");
				Shipment_Info.setOpco(Opco);
				
				String shipDate = SupportClasses.General_API_Calls.ParseStringValue(Response_TRKC, "shipDt");
				Shipment_Info.setShipDate(shipDate);
				String uniqueIdentifier = SupportClasses.General_API_Calls.ParseStringValue(Response_TRKC, "trackingQualifier");
				String Response_CMDC = CMDC_Application.CMDC_Endpoints.inflightDeliveryOptions(Shipment_Info.Tracking_Number, shipDate, uniqueIdentifier);
				
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
				
			}else {
				eMASS_Scans.eMASS_Pickup_Scan(Shipment_Info);
			}

			Shipment_Info.writeShipment_Data_To_Excel(false);						
		}catch (Exception e) {
			e.printStackTrace();
 			Assert.fail(e.getCause().toString());
		}
	}
}