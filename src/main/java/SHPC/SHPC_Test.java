package SHPC;

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
import Data_Structures.Address_Data;
import Data_Structures.Shipment_Data;
import Data_Structures.User_Data;
import SupportClasses.Environment;
import SupportClasses.Helper_Functions;
import USRC.USRC_Endpoints;

@Listeners(SupportClasses.TestNG_TestListener.class)

public class SHPC_Test {
	static String LevelsToTest = "3";
	
	@BeforeClass
	public void beforeClass() {
		Environment.SetLevelsToTest(LevelsToTest);
		API_Functions.General_API_Calls.setPrintOutAPICallFlag(true);
		API_Functions.General_API_Calls.setPrintOutFullResponseFlag(true);
	}
	
	@DataProvider// (parallel = true)
	public static Iterator<Object[]> dp(Method m) {
		List<Object[]> data = new ArrayList<Object[]>();
		
		for (int i = 0; i < Environment.LevelsToTest.length(); i++) {
			String Level = String.valueOf(Environment.LevelsToTest.charAt(i));
			int intLevel = Integer.parseInt(Level);
			Environment.getInstance().setLevel(Level);
			//Based on the method that is being called the array list will be populated
			User_Data User_Info_Array[] = User_Data.Get_UserIds(intLevel);
			
			switch (m.getName()) {
	    		case "CreditCard_Linked":
	    			for (User_Data User_Info : User_Info_Array) {
	    				data.add( new Object[] {Level, User_Info});
	    			}
	    		break;
		    	case "Create_Shipment":
		    		for (User_Data User_Info : User_Info_Array) {
		    			//check for a WADM users
		    			if (User_Info.getHasValidAccountNumber() 
		    					&& User_Info.getCanScheduleShipment()
		    					&& User_Info.getFDMregisteredAddress() != null) {
		    				
		    				Shipment_Data Shipment_Info = new Shipment_Data();

		    				Address_Data Address_Info = Address_Data.getAddress(Level, "US", null);
		    				Shipment_Info.Origin_Address_Info = Address_Info;
		    				Shipment_Info.Destination_Address_Info = User_Info.getFDMregisteredAddress();
							Shipment_Info.User_Info = User_Info;
		    				
							data.add( new Object[] {Level, Shipment_Info});
							break;
		    			}
		    		}
		    	break;
			}
		}
		
		// Remove until less that X tests
		//while (data.size() > 50) {
		//	data.remove(data.size() - 1);
		//}
		return data.iterator();
	}
	
	@Test(dataProvider = "dp", enabled =false)
	public static void CreditCard_Linked(String Level, User_Data User_Info){
		try {	    				
			// The cookie is in position 0 of the array.
			String Cookie[] = USRC.login.Login(User_Info.USER_ID, User_Info.PASSWORD);

			String CreditCard_Number = USRC_Endpoints.AccountRetrieval_Then_EnterpriseCustomer(Cookie[0]);
			if (!CreditCard_Number.contentEquals("")) {
				Helper_Functions.PrintOut(User_Info.USER_ID);
			}else {
				throw new Exception("User not linked.");
			}
		}catch (Exception e) {
			Assert.fail(e.getCause().toString());
		}
	}
	
	@Test(dataProvider = "dp")
	public static void Create_Shipment(String Level, Shipment_Data Shipment_Info){
		try {
			String LoginValues[] = USRC.login.Login(Shipment_Info.User_Info.USER_ID, Shipment_Info.User_Info.PASSWORD);
			String Cookies = LoginValues[0];
			String Response = create_shipment.SHPC_Create_Shipment(Shipment_Info, Cookies);

			assertThat(Response, containsString("masterTrackingNumber"));
			Helper_Functions.PrintOut(Response);
		}catch (Exception e) {
			e.printStackTrace();
			Assert.fail(e.getCause().toString());
		}
	}
}
