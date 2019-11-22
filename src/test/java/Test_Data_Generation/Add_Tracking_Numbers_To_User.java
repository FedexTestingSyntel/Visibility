package Test_Data_Generation;

/*import static org.hamcrest.CoreMatchers.containsString;
import static org.junit.Assert.assertThat;*/
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
	
	@BeforeClass
	public void beforeClass() {
		Environment.SetLevelsToTest(LevelsToTest);
		API_Functions.General_API_Calls.setPrintOutAPICallFlag(false);
		API_Functions.General_API_Calls.setPrintOutFullResponseFlag(false);
		
		// Data_Structures.Shipment_Data.setTrackingFilePathName("TrackingNumbersL3FullList.xls");
	}

	@DataProvider (parallel = true)
	public static Iterator<Object[]> dp(Method m) {
		List<Object[]> data = new ArrayList<Object[]>();
		
		for (int i = 0; i < Environment.LevelsToTest.length(); i++) {
			String Level = String.valueOf(Environment.LevelsToTest.charAt(i));
			Environment.getInstance().setLevel(Level);
			int intLevel = Integer.parseInt(Level);
			//get the user to add the tracking numbers to 
			User_Data User_Info_Array[] = User_Data.Get_UserIds(intLevel);
			String User_Cookies = "";
    		for (User_Data User_Info : User_Info_Array) {
    			if (User_Info.USER_ID.contentEquals("L3ATRKExceptions")) {
    				String Results[] = USRC.login.Login(User_Info.USER_ID, User_Info.PASSWORD);
    				User_Cookies = Results[0];
    				break;
    			}
    		}
    		
    		if (!User_Cookies.contentEquals("")) {
    			Shipment_Data Shipment_Info_Array[] = Data_Structures.Shipment_Data.getTrackingDetails(Level);
    			switch (m.getName()) {
		    		case "Add_Tracking_Number":
		    			for (Shipment_Data Shipment_Info: Shipment_Info_Array) {
		    				if (!Shipment_Info.TRACKING_QUALIFIER.contentEquals("")) {
		    					// if (Shipment_Info.MultiPieceShipment) {
		    					data.add(new Object[] {Level, User_Cookies, Shipment_Info});
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

	@Test(dataProvider = "dp")
	public static void Add_Tracking_Number(String Level, String Cookie, Shipment_Data Shipment_Info){
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
/*			assertThat(Response_TRKC, containsString("{\"successful\":true"));
			if (Response_TRKC.contains("\"code\"")) {
				assertThat(Response_TRKC, containsString("{\"code\":\"0\""));
			}*/
					
		}catch (Exception e) {
			e.printStackTrace();
 			Assert.fail(e.getCause().toString());
 			Helper_Functions.PrintOut(Shipment_Info.Tracking_Number + ", ", true);
		}
	}
	
	@Test(dataProvider = "dp", enabled = true)
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