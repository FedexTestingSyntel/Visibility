package SHPC_Application;

import static org.hamcrest.CoreMatchers.containsString;
import static org.junit.Assert.assertThat;
import java.lang.reflect.Method;
import java.text.Format;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.entity.StringEntity;
import org.json.JSONObject;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Listeners;
import org.testng.annotations.Test;
import Data_Structures.Account_Data;
import Data_Structures.Address_Data;
import Data_Structures.Shipment_Data;
import Data_Structures.User_Data;
import SupportClasses.Account_Lookup;
import SupportClasses.Environment;
import SupportClasses.General_API_Calls;
import SupportClasses.Helper_Functions;
import USRC_Application.USRC_Endpoints;

@Listeners(SupportClasses.TestNG_TestListener.class)

public class SHPC_Endpoints {
	static String LevelsToTest = "3";
	
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
		    			if (!User_Info.ACCOUNT_NUMBER.contentEquals("")) {
		    			//if (User_Info.USER_ID.contentEquals("MAGICJP")) {
		    				
		    				// The cookie is in position 0 of the array.
		    				String Cookie[] = USRC_Endpoints.Login(User_Info.USER_ID, User_Info.PASSWORD);

		    				Shipment_Data Shipment_Info = new Shipment_Data();

		    				Address_Data Address_Info = Address_Data.getAddress(Level, "US", null);
		    				//Origin address will be overwritten below if valid account address returned.
		    				Shipment_Info.Origin_Address_Info = Address_Info;
		    				Shipment_Info.Destination_Address_Info = Address_Info;
		    				
		    				try {
			    				String Account_Details[] = USRC_Endpoints.Account_Key_and_Value(Cookie[0]);
			    				Shipment_Info.setAccount_Key(Account_Details[0]);
			    				Shipment_Info.setAccount_Number(Account_Details[1]);
			    				
								Account_Data Account_Info = Account_Lookup.Account_Details(Account_Details[1], Level);
								Shipment_Info.setOrigin_Address_Info(Account_Info.Billing_Address_Info);
								Shipment_Info.User_Info = User_Info;
		    				
								data.add( new Object[] {Level, Shipment_Info});
		    				} catch (Exception e) {
								Helper_Functions.PrintOut("Not able to retrieve address for account " + Shipment_Info.Account_Number);
								e.printStackTrace();
							}
		    				
		    				if (data.size() > 20) {
		    					break;
		    				}
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
			String Cookie[] = USRC_Endpoints.Login(User_Info.USER_ID, User_Info.PASSWORD);

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
			String Response = SHPC_Create_Shipment(Shipment_Info);

			assertThat(Response, containsString("masterTrackingNumber"));
			Helper_Functions.PrintOut(Response);
		}catch (Exception e) {
			e.printStackTrace();
			Assert.fail(e.getCause().toString());
		}
	}
	
	public static String SHPC_Create_Shipment(Shipment_Data Shipment_Info){
		User_Data User_Info = Shipment_Info.User_Info;
		Address_Data Origin_Address_Info = Shipment_Info.Origin_Address_Info;
		Address_Data Destination_Address_Info = Shipment_Info.Destination_Address_Info;
		
		SHPC_Data DC = SHPC_Data.LoadVariables(Environment.getInstance().getLevel());
		String URL = DC.AShipmentURL;
		
		JSONObject accountNumber = new JSONObject()
				.put("key", Shipment_Info.Account_Key)
				.put("value", Shipment_Info.Account_Number);
		
		JSONObject OriginAddress = new JSONObject()
				.put("countryCode", Origin_Address_Info.Country_Code)
				.put("postalCode", Origin_Address_Info.PostalCode)
				.put("city", Origin_Address_Info.City)
				.put("stateOrProvinceCode", Origin_Address_Info.State_Code)
				// Default the residential flag as true for the FDM data.
				.put("residential", true)
				.put("streetLines", new String[] {Origin_Address_Info.Address_Line_1, 
						Origin_Address_Info.Address_Line_2});
		
		JSONObject DestinationAddress = new JSONObject()
				.put("countryCode", Destination_Address_Info.Country_Code)
				.put("postalCode", Destination_Address_Info.PostalCode)
				.put("city", Destination_Address_Info.City)
				.put("stateOrProvinceCode", Destination_Address_Info.State_Code)
				// Default the residential flag as true for the FDM data.
				.put("residential", true)
				.put("streetLines", new String[] {Destination_Address_Info.Address_Line_1, 
						Destination_Address_Info.Address_Line_2});
		
		JSONObject contact = new JSONObject()
				.put("companyName", "")
				.put("personName", User_Info.FIRST_NM + " " + User_Info.LAST_NM)
				.put("phoneNumber", User_Info.PHONE)
				.put("emailAddress", User_Info.EMAIL_ADDRESS);
		
		JSONObject weight = new JSONObject()
				.put("value", "7")
				.put("units", "KG");
		
		JSONObject customsValue = new JSONObject()
				.put("amount", "0")
				.put("currency", "JYE");
		
		JSONObject dimensions = new JSONObject()
				.put("length", "5")
				.put("width", "4")
				.put("height", "6")
				.put("units", "CM");
		
		JSONObject signatureOptionDetail = new JSONObject()
				.put("signatureOptionType", "INDIRECT")    //INDIRECT  DIRECT
				.put("signatureOptionValue", "Indirect signature required"); //  "Indirect signature required"   Direct signature required
		
		String specialServiceTypes[] = new String[] {"SIGNATURE_OPTION"};
		
		JSONObject commoditiesElement = new JSONObject()
				.put("name", "CORRESPONDENCE_NO_COMMERCIAL_VALUE")
				.put("description", "CORRESPONDENCE_NO_COMMERCIAL_VALUE")
				.put("countryOfManufacture", "JP")
				.put("weight", weight)
				.put("customsValue", customsValue)
				.put("quantity", "1")
				.put("quantityUnits", "PCS")
				.put("unitPrice", customsValue);
		
		Object commodities_Array[] = new Object[] {commoditiesElement};
		
		JSONObject packageSpecialServices = new JSONObject()
				.put("signatureOptionDetail", signatureOptionDetail)
				.put("specialServiceTypes", specialServiceTypes);
		
		JSONObject shipper = new JSONObject()
				.put("address", OriginAddress)
				.put("contact", contact);
		
		JSONObject recipientsElement = new JSONObject()
				.put("address", DestinationAddress)
				.put("contact", contact);
		
		Object recipients_Array[] = new Object[] {recipientsElement};
		
		JSONObject accountNumberElement = new JSONObject()
				.put("accountNumber",accountNumber);
		
		JSONObject responsibleParty = new JSONObject()
				.put("responsibleParty",accountNumberElement);
		
		JSONObject shippingChargesPayment = new JSONObject()
				.put("paymentType", "SENDER")
				.put("payor", responsibleParty);
		
		JSONObject dutiesPayment = new JSONObject()
				.put("paymentType", "SENDER")   //RECIPIENT   SENDER
				.put("payor", responsibleParty);
		
		JSONObject commercialInvoice = new JSONObject()
				.put("shipmentPurpose", "NOT_SOLD")
				.put("termsOfSale", "");

		JSONObject customsClearanceDetail = new JSONObject()
				.put("documentContent", "DOCUMENTS_ONLY")
				.put("dutiesPayment", dutiesPayment)
				.put("commercialInvoice", commercialInvoice)
				.put("commodities", commodities_Array);
		
		JSONObject labelSpecification = new JSONObject()
				.put("printerType", "PDF")
				.put("paperType", "LETTER")
				.put("autoPrint", false);
		
		JSONObject documentFormat = new JSONObject()
				.put("imageType", "PDF")
				.put("stockType", "PAPER_LETTER");
		
		JSONObject commercialInvoiceDetail = new JSONObject()
				.put("documentFormat", documentFormat);
		
		JSONObject shippingDocumentSpecification = new JSONObject()
				.put("commercialInvoiceDetail", commercialInvoiceDetail);
		
		JSONObject requestedPackageLineItemsElement = new JSONObject()
				.put("groupPackageCount", 1)
				.put("insuredValue", customsValue)
				.put("weight", weight)
				//.put("customerReferences", null)
				.put("dimensions", dimensions)
				.put("packageSpecialServices", packageSpecialServices);
		
		Object requestedPackageLineItems_Array[] = new Object[] {requestedPackageLineItemsElement};
		
		Format formatter = new SimpleDateFormat("MMM-dd-yyyy"); // "Jul-5-2019", "Jul-11-2019"
		Date dt = new Date();
		Calendar c = Calendar.getInstance(); 
		c.setTime(dt); 
		c.add(Calendar.DATE, 1);
		dt = c.getTime();
	    String TomorrowDateFormatted = formatter.format(dt);
	    
		JSONObject requestedShipment = new JSONObject()
				.put("shipper", shipper)
				.put("recipients", recipients_Array)
				.put("shipTimestamp", TomorrowDateFormatted)
				.put("pickupType", "DROPOFF_AT_FEDEX_LOCATION") // DROPOFF_AT_FEDEX_LOCATION  CONTACT_FEDEX_TO_SCHEDULE
				.put("serviceType", "INTERNATIONAL_ECONOMY")
				.put("packagingType", "YOUR_PACKAGING")
				.put("shippingChargesPayment", shippingChargesPayment)
				.put("customsClearanceDetail", customsClearanceDetail)
				.put("labelSpecification", labelSpecification)
				.put("shippingDocumentSpecification", shippingDocumentSpecification)
				.put("requestedPackageLineItems", requestedPackageLineItems_Array);
		
		HttpPut httpput = new HttpPut(URL);
		JSONObject main = new JSONObject()
			.put("openShipmentAction", "CONFIRM")
			.put("accountNumber", accountNumber)
			.put("requestedShipment", requestedShipment);

		String json = main.toString();
			
		httpput.addHeader("Content-Type", "application/json");
		httpput.addHeader("Authorization", "Bearer " + DC.OAuth_Token);
				
		StringEntity params;
		String Response;
		try {
			params = new StringEntity(json.toString());
			httpput.setEntity(params);
			//Note that an email will be triggered after registration
			Response = General_API_Calls.HTTPCall(httpput, json);	
		} catch (Exception e) {
			e.printStackTrace();
			Response = e.getLocalizedMessage();
		}
		return Response;
	}
}
