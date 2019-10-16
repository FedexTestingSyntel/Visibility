package SHPC;

import java.text.Format;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.entity.StringEntity;
import org.json.JSONObject;
import org.testng.annotations.Listeners;
import API_Functions.General_API_Calls;
import Data_Structures.Address_Data;
import Data_Structures.Shipment_Data;
import Data_Structures.User_Data;
import SupportClasses.Environment;
import SupportClasses.Helper_Functions;

@Listeners(SupportClasses.TestNG_TestListener.class)

public class create_shipment {
	public static String SHPC_Create_Shipment(Shipment_Data Shipment_Info, String Cookies){
		User_Data User_Info = Shipment_Info.User_Info;
		Address_Data Origin_Address_Info = Shipment_Info.Origin_Address_Info;
		Address_Data Destination_Address_Info = Shipment_Info.Destination_Address_Info;
		
		SHPC_Data DC = SHPC_Data.SHPC_Load();
		
		if (!Shipment_Info.User_Info.getHasValidAccountNumber()) {
			Helper_Functions.PrintOut("User does not have valid accounts");
		}
		
		JSONObject accountNumber = new JSONObject()
				.put("key", Shipment_Info.User_Info.ACCOUNT_NUMBER_DETAILS[0][0])
				.put("value", Shipment_Info.User_Info.ACCOUNT_NUMBER_DETAILS[0][1]);
		
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
				.put("phoneNumber", User_Info.PHONE_NUMBER)
				.put("emailAddress", User_Info.EMAIL_ADDRESS);
		
		JSONObject weight = new JSONObject()
				.put("value", "7")
				.put("units", "KG");
		
		String nullString = null;
		JSONObject customsValue = new JSONObject()
				.put("amount", nullString)
				.put("currency", "JYE");
		/*JSONObject customsValue = new JSONObject()
				.put("amount", "0")
				.put("currency", "JYE");*/
		
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
				.put("paymentType", "RECIPIENT")   //RECIPIENT   SENDER
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
				.put("customerReferences", nullString)
				.put("dimensions", dimensions)
				.put("packageSpecialServices", packageSpecialServices);
		
		Object requestedPackageLineItems_Array[] = new Object[] {requestedPackageLineItemsElement};
		
		Format formatter = new SimpleDateFormat("MMM-d-yyyy"); // "Jul-5-2019", "Jul-11-2019"
		Date dt = new Date();
		Calendar c = Calendar.getInstance(); 
		c.setTime(dt); 
		c.add(Calendar.DATE, 1);
		dt = c.getTime();
		// if the day of the month is two digits
		if (dt.getDate() > 9) {
			formatter = new SimpleDateFormat("MMM-dd-yyyy");
		}
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
		
		HttpPut httpput = new HttpPut(DC.AShipmentURL);
		JSONObject main = new JSONObject()
			.put("openShipmentAction", "CONFIRM")
			.put("accountNumber", accountNumber)
			.put("requestedShipment", requestedShipment);

		String json = main.toString();
			
		httpput.addHeader("Content-Type", "application/json");
		httpput.addHeader("Authorization", "Bearer " + DC.getOAuthToken());
		httpput.addHeader("Cookie", Cookies);
				
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
