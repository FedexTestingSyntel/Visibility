package Test_Data_Update;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
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
import INET_Application.GroundCorpLoad;
import SupportClasses.Environment;
import SupportClasses.Helper_Functions;

@Listeners(SupportClasses.TestNG_TestListener.class)

public class Tracking_Data_Update {
	static String LevelsToTest = "4";
	static CopyOnWriteArrayList<String[]> TrackingList = new CopyOnWriteArrayList<String[]>() {{add(new String[] {"ReservedForHeaders"});}};
	public final static Lock TrackingNumberListLock = new ReentrantLock();
	private static int dataProviderSize = 0;
	@BeforeClass
	public void beforeClass() {
		Environment.SetLevelsToTest(LevelsToTest);
		API_Functions.General_API_Calls.setPrintOutAPICallFlag(false);
		API_Functions.General_API_Calls.setPrintOutFullResponseFlag(false);
	}
	
	@DataProvider (parallel = true)
	public static Iterator<Object[]> dp(Method m) {
		List<Object[]> data = new ArrayList<Object[]>();
		
		for (int i = 0; i < Environment.LevelsToTest.length(); i++) {
			String Level = String.valueOf(Environment.LevelsToTest.charAt(i));
    		Shipment_Data Shipment_Info_Array[] = Data_Structures.Shipment_Data.getTrackingDetails(Level);
			//Based on the method that is being called the array list will be populated
			switch (m.getName()) {
		    	case "Tracking_Number_Update":
		    		for (Shipment_Data Shipment_Info: Shipment_Info_Array) {
		    			// Add the existing tracking numbers to list.
		    			if (!Shipment_Info.getValidStatus() || Helper_Functions.isNullOrUndefined(Shipment_Info.TrackPackagesResponse)) {
		    				data.add(new Object[] {Level, Shipment_Info.Tracking_Number, Shipment_Info.TRACKING_QUALIFIER, Shipment_Info.User_Info.USER_ID, Shipment_Info.User_Info.PASSWORD});
		    			} 
		    			
		    			else {data.add(new Object[] {Level, Shipment_Info.Tracking_Number, Shipment_Info.TRACKING_QUALIFIER, Shipment_Info.User_Info.USER_ID, Shipment_Info.User_Info.PASSWORD});}
		    		}
		    		break;
		    	case "Apply_Ground_Scan":
		    		for (Shipment_Data Shipment_Info: Shipment_Info_Array) {
		    			// Add the existing tracking numbers to list.
		    			//	if (!Shipment_Info.getValidStatus() || !Shipment_Info.getHasTrackPackagesResponse()) {
		    			if (Shipment_Info.Service.toLowerCase().contains("ground") && 
		    					Helper_Functions.isNullOrUndefined(Shipment_Info.TRACKING_QUALIFIER)) {
		    				data.add(new Object[] {Level, Shipment_Info.Tracking_Number});
		    			} 
		    			
		    			//else {data.add(new Object[] {Level, Shipment_Info.Tracking_Number, Shipment_Info.TRACKING_QUALIFIER});}
		    		}
		    		break;
		    	case "Tracking_Number_AssociatedShipment":
		    		for (Shipment_Data Shipment_Info: Shipment_Info_Array) {
		    			if (!Helper_Functions.isNullOrUndefined(Shipment_Info.Tracking_Number) &&
		    				!Helper_Functions.isNullOrUndefined(Shipment_Info.TRACKING_CARRIER) &&
		    				!Helper_Functions.isNullOrUndefined(Shipment_Info.TRACKING_QUALIFIER) &&
		    				Helper_Functions.isNullOrUndefined(Shipment_Info.associatedShipmentResponse)) {
		    				data.add(new Object[] {Level, Shipment_Info.Tracking_Number, Shipment_Info.TRACKING_QUALIFIER, Shipment_Info.TRACKING_CARRIER});
		    			}
		    		}
		    		break;
			}
		}
		
		SupportClasses.Helper_Functions.LimitDataProvider(m.getName(), -1, data);
		dataProviderSize = data.size();
		return data.iterator();
	}
	
	@Test(dataProvider = "dp", enabled = true) // , invocationCount = 100
	public static void Tracking_Number_Update(String Level, String trackingNumber, String trackingQualifier, String UserID, String Password){
		try {
			String trackingCarrier = "";
			int keyPosition = -1;
			String Details[][] = new String[][] {
				{"TRACKING_NUMBER", trackingNumber}, 
				{"USER_ID", UserID}, 
				{"PASSWORD", Password}
			};
			
			if (!trackingQualifier.contentEquals("")) {
				Details = Arrays.copyOf(Details, Details.length + 1);
				Details[Details.length - 1] = new String[]{"TRACKING_QUALIFIER", trackingQualifier};
				keyPosition = Details.length - 1;
			}
			
			Details = TRKC.tracking_packages.getTrackpackages(Details, trackingNumber, trackingQualifier, "");
			String SHIP_DATE = "";
			for(String[] Element: Details) {
				if (Element != null && Element[0] != null && Element[1] != null) {
					if (Element[0].contentEquals("SHIP_DATE")) {
						SHIP_DATE = Element[1];
					}else if (Element[0].contentEquals("TRACKING_QUALIFIER")) {
						trackingQualifier = Element[1];
					}
					else if (Element[0].contentEquals("TRACKING_CARRIER")) {
						trackingCarrier = Element[1];
					}
				}
			}
			
			Details = CMDC_Application.inflight_delivery_options.getInflightDeliveryOptions(Details, trackingNumber, SHIP_DATE, trackingQualifier);
			Details = TRKC.associated_shipments.getAssociatedShipment(Details, trackingNumber, trackingQualifier, trackingCarrier);
			
			updateFile(Details, keyPosition);
			/*String FileName = Helper_Functions.DataDirectory + "\\TrackingNumbers.xls";*/
/*			String FileName = Shipment_Data.getTrackingFilePath(Level);
			boolean updatefile = Helper_Functions.WriteToExcel(FileName, "L" + Level, Details, keyPosition);
			*/
			Helper_Functions.PrintOut("Tracking_Number:" + trackingNumber + "  TRACKING_QUALIFIER:" + trackingQualifier);
		}catch (Exception e) {
			//e.printStackTrace();
 			//Assert.fail(e.getCause().toString());
			Assert.fail();
		}
	}

	@Test(dataProvider = "dp", enabled = false)
	public static void Apply_Ground_Scan(String Level, String trackingNumber){
		try {
			String Tracking[] = new String[] { trackingNumber };
			String GroundResponse = GroundCorpLoad.ValidateAndProcess(Tracking);
			
			if (GroundResponse.contains("\"valid\":1")){
				// if received a valid response run updates.
				Tracking_Number_Update(Level, trackingNumber, "", "", "");
			} else {
				Assert.fail(GroundResponse);
			}

		}catch (Exception e) {
			e.printStackTrace();
 			Assert.fail(e.getCause().toString());
		}
	}

	
	@Test(dataProvider = "dp", enabled = false) // , invocationCount = 100
	public static void Tracking_Number_AssociatedShipment(String Level, String trackingNumber, String trackingQualifier, String trackingCarrier){
		try {
			int keyPosition = 0;
			String Details[][] = new String[][] {
				{"TRACKING_QUALIFIER", trackingQualifier}
			};
			
			Details = TRKC.associated_shipments.getAssociatedShipment(Details, trackingNumber, trackingQualifier, trackingCarrier);
			
/*			String FileName = Shipment_Data.getTrackingFilePath(Level);
			boolean updatefile = Helper_Functions.WriteToExcel(FileName, "L" + Level, Details, keyPosition);
			*/
			updateFile(Details, keyPosition);
			Helper_Functions.PrintOut("Tracking_Number:" + trackingNumber + "  TRACKING_QUALIFIER:" + trackingQualifier);
		}catch (Exception e) {
			//e.printStackTrace();
 			//Assert.fail(e.getCause().toString());
			Assert.fail();
		}
	}

	@AfterMethod
	public void UpdateDataProviderRemaining() {
		dataProviderSize--;
	}

	private static void updateFile(String Details[][], int keyPosition) {
/*		String Headers[] = new String[Details.length];
		for(int headcnt = 0 ; headcnt < Details.length; headcnt++) {
			Headers[headcnt] = Details[headcnt][0];
		}
		
		if (TrackingList.size() == 1) {
			//set the first row as the headers.
			TrackingList.set(0, Headers);
		}
		
		String CurrentHeaders[] = TrackingList.get(0);
		
		if (Arrays.equals(Headers, CurrentHeaders) ){
			String Row[] = new String[Details.length];
			for(int i = 0 ; i < Details.length; i++) {
				Row[i] = Details[i][1];
			}
			TrackingList.add(Row);
			
			if ((TrackingList.size() > 10 && TrackingNumberListLock.tryLock()) 
					|| dataProviderSize < 2) {
				TrackingNumberListLock.lock();
				String D[][] = new String[][] {TrackingList.get(0)};
				while (TrackingList.size() > 1) {
					D = Arrays.copyOf(D, D.length + 1);
					D[D.length - 1] = TrackingList.get(1);
					TrackingList.remove(1);
				}
				String Level = Environment.getInstance().getLevel();
				String FileName = Shipment_Data.getTrackingFilePath(Level);
				boolean updatefile = Helper_Functions.WriteToExcel(FileName, "L" + Level, Details, keyPosition);
				Helper_Functions.PrintOut(D.length - 1 + " tracking numbers updated: " + updatefile);
				TrackingNumberListLock.unlock();
			}
		} else {
			String Level = Environment.getInstance().getLevel();
			String FileName = Shipment_Data.getTrackingFilePath(Level);
			boolean updatefile = Helper_Functions.WriteToExcel(FileName, "L" + Level, Details, keyPosition);
			Helper_Functions.PrintOut("Headers not matching: " + updatefile);
		}*/
		
		String Level = Environment.getInstance().getLevel();
		String FileName = Shipment_Data.getTrackingFilePath(Level);
		boolean updatefile = Helper_Functions.WriteToExcel(FileName, "L" + Level, Details, keyPosition);
		Helper_Functions.PrintOut("File Updated: " + updatefile);
	}

}