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
import Data_Structures.User_Data;
import SupportClasses.Environment;
import SupportClasses.Helper_Functions;

@Listeners(SupportClasses.TestNG_TestListener.class)

public class Add_Tracking_Numbers_To_User {
	static String LevelsToTest = "3";
	static int numShipments = 0;
	
	@BeforeClass
	public void beforeClass() {
		Environment.SetLevelsToTest(LevelsToTest); 
		API_Functions.General_API_Calls.setPrintOutAPICallFlag(false);    // false  true
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
			
			//get the user to add the tracking numbers to 
			User_Data userInfoArray[] = User_Data.Get_UserIds(intLevel);
			String User_Cookies = "";
			
			String userID = "L" + Level + "ATRK" + numShipments + "K";
    		for (User_Data User_Info : userInfoArray) {
    			if (User_Info.USER_ID.contentEquals(userID)) {
    				String Results[] = USRC.login.Login(User_Info.USER_ID, User_Info.PASSWORD);
    				User_Cookies = Results[0];
    				break;
    			}
    		}
    		
    		if (!User_Cookies.contentEquals("")) {
    			Shipment_Data Shipment_Info_Array[] = Data_Structures.Shipment_Data.getTrackingDetails(Level);
    			int numberOfShipments = TRKC.tracking_profile.checkTotalNumberOfShipments(userID, User_Cookies);
    			
    			switch (m.getName()) {
		    		case "Add_Tracking_Number":
		    			for (Shipment_Data Shipment_Info: Shipment_Info_Array) {
		    				if (numberOfShipments >= numShipments * 1000) {
		    					numShipments++;
		    					break;
		    				}
		    				
		    				if (!Shipment_Info.TRACKING_QUALIFIER.contentEquals("")) {
		    					data.add(new Object[] {Level, User_Cookies, Shipment_Info});
		    					numberOfShipments++;
		    				}
		    			}
		    		break;
		    		case "Add_Exceptions":
		    			for (Shipment_Data Shipment_Info: Shipment_Info_Array) {
		    				if (Shipment_Info.Status.toUpperCase().contains("EXCEPTION")) {
		    					data.add(new Object[] {Level, User_Cookies, Shipment_Info});
		    				}
		    			}
		    		break;
    			}
    		}
		}
		
		SupportClasses.Helper_Functions.LimitDataProvider(m.getName(), -1, data);
		
		return data.iterator();
	}

	@Test(dataProvider = "dp", invocationCount = 18, enabled = false)
	public static void Add_Tracking_Number(String Level, String Cookie, Shipment_Data Shipment_Info) {
		try {
			String Response_TRKC = "";
			int numberOfAttempts = 5;
			for (int i = 0 ; i < numberOfAttempts ; i ++) {
				Response_TRKC = TRKC.shipment_option_add_request.ShipmentOptionAddRequest(Cookie, Shipment_Info.Tracking_Number, Shipment_Info.TRACKING_QUALIFIER, Shipment_Info.TRACKING_CARRIER);
				if (!Response_TRKC.contains("TRAQS connection/request problem")) {
					i = numberOfAttempts;
				} else if (i == 4) {
					Assert.fail(Shipment_Info.Tracking_Number);
				}
			}
			boolean validResponse = TRKC.helper.checkValidResponse(Response_TRKC);
			Assert.assertTrue(validResponse);
					
			// Helper_Functions.PrintOut(Shipment_Info.Tracking_Number + " added.");
		} catch (Exception e) {
			e.printStackTrace();
 			Assert.fail(e.getCause().toString());
 			Helper_Functions.PrintOut(Shipment_Info.Tracking_Number + ", ", true);
		}
	}
	
	@Test(dataProvider = "dp", enabled = false)
	public static void Add_Exceptions(String Level, String Cookie, Shipment_Data Shipment_Info){
		try {
			String Response_TRKC = "";
			int number_of_attempts = 5;
			for (int i = 0 ; i < number_of_attempts ; i ++) {
				Response_TRKC = TRKC.shipment_option_add_request.ShipmentOptionAddRequest(Cookie, Shipment_Info.Tracking_Number, Shipment_Info.TRACKING_QUALIFIER, Shipment_Info.TRACKING_CARRIER);
				if (!Response_TRKC.contains("TRAQS connection/request problem")) {
					i = number_of_attempts;
				}
			}
			boolean validResponse = TRKC.helper.checkValidResponse(Response_TRKC);
			Assert.assertTrue(validResponse);
					
		}catch (Exception e) {
			e.printStackTrace();
 			Assert.fail(e.getCause().toString());
 			Helper_Functions.PrintOut(Shipment_Info.Tracking_Number + ", ", true);
		}
	}
}