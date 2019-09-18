package Test_Data_Generation;

import static org.hamcrest.CoreMatchers.containsString;
import static org.junit.Assert.assertThat;
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

@Listeners(SupportClasses.TestNG_TestListener.class)

public class Add_Tracking_Numbers_To_User {
	static String LevelsToTest = "3";
	
	@BeforeClass
	public void beforeClass() {
		Environment.SetLevelsToTest(LevelsToTest);
	}

	@DataProvider (parallel = true)
	public static Iterator<Object[]> dp(Method m) {
		List<Object[]> data = new ArrayList<Object[]>();
		
		for (int i=0; i < Environment.LevelsToTest.length(); i++) {
			String Level = String.valueOf(Environment.LevelsToTest.charAt(i));
			//setting env since using the below USRC call
			Environment.getInstance().setLevel(Level);
			int intLevel = Integer.parseInt(Level);
			//get the user to add the tracking numbers to 
			User_Data User_Info_Array[] = User_Data.Get_UserIds(intLevel);
			String User_Cookies = "";
    		for (User_Data User_Info : User_Info_Array) {
    			if (User_Info.USER_ID.contentEquals("L3ATRK")) {
    				String Results[] = USRC_Application.USRC_Endpoints.Login(User_Info.USER_ID, User_Info.PASSWORD);
    				User_Cookies = Results[0];
    				break;
    			}
    		}
    		
    		if (!User_Cookies.contentEquals("")) {
    			switch (m.getName()) {
		    	case "Add_Tracking_Number":
		    		Shipment_Data Shipment_Info_Array[] = Data_Structures.Shipment_Data.getTrackingDetails(Level);
		    		for (Shipment_Data Shipment_Info: Shipment_Info_Array) {
		    			if (!Shipment_Info.TRACKING_QUALIFIER.contentEquals("")) {
		    				data.add(new Object[] {Level, User_Cookies, Shipment_Info});
		    			}
		    		}
		    	break;
    			}
    		}
		}
		
		int last = 1700;
		while (last < data.size()) {
			data.remove(0);
		}
		return data.iterator();
	}

	@Test(dataProvider = "dp")
	public static void Add_Tracking_Number(String Level, String Cookie, Shipment_Data Shipment_Info){
		try {
			String Response_TRKC = "";
			int number_of_attempts = 5;
			for (int i = 0 ; i < number_of_attempts ; i ++) {
				Response_TRKC = TRKC.TRKC_Endpoints.ShipmentOptionAddRequest(Cookie, Shipment_Info.Tracking_Number, Shipment_Info.TRACKING_QUALIFIER, Shipment_Info.TRACKING_CARRIER);
				if (!Response_TRKC.contains("TRAQS connection/request problem")) {
					i = number_of_attempts;
				}
			}
			assertThat(Response_TRKC, containsString("{\"successful\":true"));
			//Response_TRKC = TRKC_Application.TRKC_Endpoints.ShipmentOptionWatchListRequest(Cookie, Shipment_Info.Tracking_Number, Shipment_Info.TRACKING_QUALIFIER, Shipment_Info.TRACKING_CARRIER);
			//assertThat(Response_TRKC, containsString("{\"successful\":true"));					
		}catch (Exception e) {
			e.printStackTrace();
 			Assert.fail(e.getCause().toString());
		}
	}
}