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

@Listeners(SupportClasses.TestNG_TestListener.class)

public class Tracking_Number_Generation {
	static String LevelsToTest = "2";
	static int numberOfAttempts = 5;
	
	@BeforeClass
	public void beforeClass() {
		Environment.SetLevelsToTest(LevelsToTest);
	}
	
	@DataProvider //(parallel = true)
	public static Iterator<Object[]> dp(Method m) {
		List<Object[]> data = new ArrayList<Object[]>();
		
		for (int i = 0; i < Environment.LevelsToTest.length(); i++) {
			String Level = String.valueOf(Environment.LevelsToTest.charAt(i));
			int intLevel = Integer.parseInt(Level);
			
			//Based on the method that is being called the array list will be populated
			switch (m.getName()) {
		    	case "INET_Create_Shipment":
		    		User_Data User_Info_Array[] = User_Data.Get_UserIds(intLevel);
		    		
		    		for (User_Data User_Info : User_Info_Array) {
		    			//check for a WADM users
		    			if (User_Info.getHasValidAccountNumber() 
		    				&& User_Info.getCanScheduleShipment()
		    				&& User_Info.getFDMregisteredAddress() != null) {
		    			/*if (User_Info.USER_ID.contentEquals("L3TESTUSER")) {*/

			    			Shipment_Data Shipment_Info = new Shipment_Data();
			    			Shipment_Info.setUser_Info(User_Info);
			    			/*String AddressLineOne = "58 CABOT ST";
			    			Account_Data Account_Info = Environment.getAddressDetails(Level, "US", AddressLineOne);*/
			    				
			    			Shipment_Info.setOrigin_Address_Info(User_Info.Address_Info);
			    			Shipment_Info.setDestination_Address_Info(User_Info.getFDMregisteredAddress());
			    				
			    			Shipment_Info.setService("FedEx Express Saver");
			    			// Shipment_Info.setService("Ground");
		    				data.add( new Object[] {Level, Shipment_Info});
		    				numberOfAttempts--;
		    			}
		    			if (numberOfAttempts == 0) { 
		    				break;
		    			}
		    		}
		    	break;
			}
		}
		
		SupportClasses.Helper_Functions.LimitDataProvider(m.getName(), -1, data);
		
		return data.iterator();
	}

	@Test(dataProvider = "dp")
	public static void INET_Create_Shipment(String Level, Shipment_Data Shipment_Info){
		try {
			Shipment_Info.PrintOutShipmentDetails();
			INET_Application.INET_Shipment.INET_Create_Shipment(Level, Shipment_Info);
		}catch (Exception e) {
			e.printStackTrace();
 			Assert.fail(e.getCause().toString());
		}
	}
}