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
import SupportClasses.Helper_Functions;

@Listeners(SupportClasses.TestNG_TestListener.class)

public class create_shipment {
	public static String SHPC_Create_Shipment(Shipment_Data Shipment_Info, String Cookies){
		User_Data User_Info = Shipment_Info.User_Info;
		Address_Data Origin_Address_Info = Shipment_Info.Origin_Address_Info;
		Address_Data Destination_Address_Info = Shipment_Info.Destination_Address_Info;
		
		// update the email address to ignore spam notifications.
		Shipment_Info.User_Info.EMAIL_ADDRESS = "accept@fedex.com";
		
		/// MAY NEED TO REMOVE THIS LATER. Seeing validation from MAGIC where spaces are not valid
		if (Origin_Address_Info.PostalCode.contains(" ")) {
			Origin_Address_Info.PostalCode = Origin_Address_Info.PostalCode.replaceAll(" ", "");
		}
		if (Destination_Address_Info.PostalCode.contains(" ")) {
			Destination_Address_Info.PostalCode = Destination_Address_Info.PostalCode.replaceAll(" ", "");
		}
		
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
				// Default the residential flag as true for the FDM data.    true
				.put("residential", true)
				.put("streetLines", new String[] {Origin_Address_Info.Address_Line_1, 
						Origin_Address_Info.Address_Line_2});
		/* TODO: Will need to start sending address line 3 in the future.
		 * Will need to add passing address line 3 in future.
		 * Should be for July20CL 12/16/19
		 * .put("streetLines", new String[] {Origin_Address_Info.Address_Line_1, 
				Origin_Address_Info.Address_Line_2, Origin_Address_Info.Address_Line_3});*/
		
		JSONObject DestinationAddress = new JSONObject()
				.put("countryCode", Destination_Address_Info.Country_Code)
				.put("postalCode", Destination_Address_Info.PostalCode)
				.put("city", Destination_Address_Info.City)
				.put("stateOrProvinceCode", Destination_Address_Info.State_Code)
				// Default the residential flag as true for the FDM data.     true
				.put("residential", true)
				.put("streetLines", new String[] {Destination_Address_Info.Address_Line_1, 
						Destination_Address_Info.Address_Line_2, ""});
		// TODO: Same as above, address line 3 added.
		
		JSONObject contact = new JSONObject()
				.put("companyName", "")
				.put("personName", User_Info.FIRST_NM + " " + User_Info.LAST_NM)
				.put("phoneNumber", User_Info.PHONE_NUMBER)
				.put("emailAddress", User_Info.EMAIL_ADDRESS);
		
		JSONObject weight = new JSONObject()
				.put("value", "7")
				.put("units", "KG");
		
		JSONObject customsValue = new JSONObject()
				.put("amount", "12")
				.put("currency", "USD");
		
		JSONObject dimensions = new JSONObject()
				.put("length", "5")
				.put("width", "4")
				.put("height", "6")
				.put("units", "CM");
		
		// Need to work on signiture, SHPC throws error that signiture option is not recognized.
 		JSONObject signatureOptionDetail = new JSONObject()
				.put("signatureOptionType", "INDIRECT")    //INDIRECT  DIRECT
				.put("signatureOptionValue", "Indirect signature required"); //  "Indirect signature required"   Direct signature required
		
		String specialServiceTypes[] = new String[] {"SIGNATURE_OPTION"};
		
		JSONObject packageSpecialServices = new JSONObject()
				.put("signatureOptionDetail", signatureOptionDetail)
				.put("specialServiceTypes", specialServiceTypes);
		
		JSONObject commoditiesElement = new JSONObject()
				.put("name", "PERSONAL_DOCUMENT") //PERSONAL_DOCUMENT   CORRESPONDENCE_NO_COMMERCIAL_VALUE
				.put("description", "PERSONAL_DOCUMENT") //PERSONAL_DOCUMENT  CORRESPONDENCE_NO_COMMERCIAL_VALUE
				.put("countryOfManufacture", "US")
				.put("weight", weight)
				.put("customsValue", customsValue)
				.put("quantity", "1")
				.put("quantityUnits", "PCS")
				.put("unitPrice", customsValue);
		
		JSONObject shipper = new JSONObject()
				.put("address", OriginAddress)
				.put("contact", contact);
		
		JSONObject recipientsElement = new JSONObject()
				.put("address", DestinationAddress)
				.put("contact", contact);
		
		Object recipients_Array[] = new Object[] {recipientsElement};
		
		JSONObject accountNumberElement = new JSONObject()
				.put("accountNumber", accountNumber);
		
		JSONObject responsibleParty = new JSONObject()
				.put("responsibleParty", accountNumberElement);
		
		JSONObject payorAccountNumber = new JSONObject()
				.put("key", "")
				.put("value", "");
		
		JSONObject payorAccountNumberElement = new JSONObject()
				.put("accountNumber", payorAccountNumber);
		
		JSONObject payorResponsibleParty = new JSONObject()
				.put("responsibleParty",payorAccountNumberElement);
		
		JSONObject shippingChargesPayment = new JSONObject()
				.put("paymentType", "SENDER")
				.put("payor", responsibleParty);
		
		JSONObject dutiesPayment = new JSONObject()
				.put("paymentType", "RECIPIENT")   //RECIPIENT   SENDER
				.put("payor", payorResponsibleParty);
		
		JSONObject commercialInvoice = new JSONObject()
				.put("shipmentPurpose", "NOT_SOLD")
				.put("specialInstructions", "") //added on 11/5/19
				.put("termsOfSale", "");

		Object commodities_Array[] = new Object[] {commoditiesElement};
		
		JSONObject customsClearanceDetail = new JSONObject()
				.put("documentContent", "DOCUMENTS_ONLY")
				.put("dutiesPayment", dutiesPayment)
				.put("commercialInvoice", commercialInvoice)
				.put("commodities", commodities_Array);
		
		JSONObject labelSpecification = new JSONObject()
				.put("printerType", "PDF")
				.put("paperType", "LETTER")
				.put("autoPrint", false);
		
		JSONObject requestedPackageLineItemsElement = new JSONObject()
				.put("groupPackageCount", 1)
				.put("insuredValue", customsValue)
				.put("weight", weight)
				.put("customerReferences", JSONObject.NULL)
				.put("dimensions", dimensions)
				.put("packageSpecialServices", packageSpecialServices);;
		
		Object requestedPackageLineItems_Array[] = new Object[] {requestedPackageLineItemsElement};
		
		Format formatter = new SimpleDateFormat("MMM-d-yyyy"); // "Jul-5-2019", "Jul-11-2019"
		Date dt = new Date();
		Calendar c = Calendar.getInstance(); 
		c.setTime(dt); 
		// c.add(Calendar.DATE, 1);
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
				.put("serviceType", "INTERNATIONAL_ECONOMY") //   INTERNATIONAL_ECONOMY
				.put("packagingType", "YOUR_PACKAGING")
				.put("shippingChargesPayment", shippingChargesPayment)
				.put("customsClearanceDetail", customsClearanceDetail)
				.put("labelSpecification", labelSpecification)
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
		
		// TESTING IF NEEDED
		httpput.addHeader("X-clientid", "MAGS");
		httpput.addHeader("X-clientversion", "1.0");
		httpput.addHeader("X-locale", "en_AU");
		
		StringEntity params;
		String Response;
		try {
			params = new StringEntity(json.toString());
			httpput.setEntity(params);
			// Update the timeout time as the SHPC call tends to take longer.
			General_API_Calls.setHTTPCallTimeout(60);
			//Note that an email will be triggered after registration
			Response = General_API_Calls.HTTPCall(httpput, json);	
		} catch (Exception e) {
			e.printStackTrace();
			Response = e.getLocalizedMessage();
		}
		return Response;
	}
	
	public static String createTrackingNumber(Shipment_Data Shipment_Info, String Cookies) {
		String Response = create_shipment.SHPC_Create_Shipment(Shipment_Info, Cookies);

		if (Response.contains("masterTrackingNumber")) {
			// return just the tracking number
			return API_Functions.General_API_Calls.ParseStringValue(Response, "masterTrackingNumber");
		} else {
			return null;
		}
		
	}
	
/*	public static String SHPC_Create_Shipment(Shipment_Data Shipment_Info, String Cookies){
		User_Data User_Info = Shipment_Info.User_Info;
		Address_Data Origin_Address_Info = Shipment_Info.Origin_Address_Info;
		Address_Data Destination_Address_Info = Shipment_Info.Destination_Address_Info;
		
		// update the email address to ignore spam notifications.
		Shipment_Info.User_Info.EMAIL_ADDRESS = "accept@fedex.com";
		
		/// MAY NEED TO REMOVE THIS LATER. Seeing validation from MAGIC where spaces are not valid
		if (Origin_Address_Info.PostalCode.contains(" ")) {
			Origin_Address_Info.PostalCode = Origin_Address_Info.PostalCode.replaceAll(" ", "");
		}
		if (Destination_Address_Info.PostalCode.contains(" ")) {
			Destination_Address_Info.PostalCode = Destination_Address_Info.PostalCode.replaceAll(" ", "");
		}
		
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
				// Default the residential flag as true for the FDM data.    true
				.put("residential", false)
				.put("streetLines", new String[] {Origin_Address_Info.Address_Line_1, 
						Origin_Address_Info.Address_Line_2, ""});
		 TODO: Will need to start sending address line 3 in the future.
		 * Will need to add passing address line 3 in future.
		 * Should be for July20CL 12/16/19
		 * .put("streetLines", new String[] {Origin_Address_Info.Address_Line_1, 
				Origin_Address_Info.Address_Line_2, Origin_Address_Info.Address_Line_3});
		
		JSONObject DestinationAddress = new JSONObject()
				.put("countryCode", Destination_Address_Info.Country_Code)
				.put("postalCode", Destination_Address_Info.PostalCode)
				.put("city", Destination_Address_Info.City)
				.put("stateOrProvinceCode", Destination_Address_Info.State_Code)
				// Default the residential flag as true for the FDM data.     true
				.put("residential", false)
				.put("streetLines", new String[] {Destination_Address_Info.Address_Line_1, 
						Destination_Address_Info.Address_Line_2, ""});
		// TODO: Same as above, address line 3 added.
		
		JSONObject contact = new JSONObject()
				.put("companyName", "")
				.put("personName", User_Info.FIRST_NM + " " + User_Info.LAST_NM)
				.put("phoneNumber", User_Info.PHONE_NUMBER)
				.put("emailAddress", User_Info.EMAIL_ADDRESS);
		
		JSONObject weight = new JSONObject()
				.put("value", "7")
				.put("units", "KG");
		
		JSONObject customsValue = new JSONObject()
				.put("amount", "12")
				.put("currency", "USD");
		
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
				.put("name", "PERSONAL_DOCUMENT") //PERSONAL_DOCUMENT   CORRESPONDENCE_NO_COMMERCIAL_VALUE
				.put("description", "PERSONAL_DOCUMENT") //PERSONAL_DOCUMENT  CORRESPONDENCE_NO_COMMERCIAL_VALUE
				.put("countryOfManufacture", "US")
				.put("weight", weight)
				.put("customsValue", customsValue)
				.put("quantity", "1")
				.put("quantityUnits", "PCS")
				.put("unitPrice", customsValue);
		
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
				.put("accountNumber", accountNumber);
		
		JSONObject responsibleParty = new JSONObject()
				.put("responsibleParty", accountNumberElement);
		
		JSONObject payorAccountNumber = new JSONObject()
				.put("key", "")
				.put("value", "");
		
		JSONObject payorAccountNumberElement = new JSONObject()
				.put("accountNumber", payorAccountNumber);
		
		JSONObject payorResponsibleParty = new JSONObject()
				.put("responsibleParty",payorAccountNumberElement);
		
		JSONObject shippingChargesPayment = new JSONObject()
				.put("paymentType", "SENDER")
				.put("payor", responsibleParty);
		
		JSONObject dutiesPayment = new JSONObject()
				.put("paymentType", "RECIPIENT")   //RECIPIENT   SENDER
				.put("payor", payorResponsibleParty);
		
		JSONObject commercialInvoice = new JSONObject()
				.put("shipmentPurpose", "NOT_SOLD")
				.put("specialInstructions", "") //added on 11/5/19
				.put("termsOfSale", "");

		Object commodities_Array[] = new Object[] {commoditiesElement};
		
		JSONObject customsClearanceDetail = new JSONObject()
				.put("documentContent", "DOCUMENTS_ONLY")
				.put("dutiesPayment", dutiesPayment)
				.put("commercialInvoice", commercialInvoice)
				.put("commodities", commodities_Array);
		
		JSONObject labelSpecification = new JSONObject()
				.put("printerType", "PDF")
				.put("paperType", "LETTER")
				.put("autoPrint", false);
		
		JSONObject Email_recipients_Element = new JSONObject()
				.put("emailAddress", Shipment_Info.User_Info.EMAIL_ADDRESS)
				.put("emailNotificationRecipientType", "RECIPIENT")
				.put("locale", "en")
				.put("notificationEventType", new String[] {"ON_DELIVERY",
				        "ON_EXCEPTION",
				        "ON_SHIPMENT",
				        "ON_ESTIMATED_DELIVERY",
				        "ON_TENDER"})
				.put("notificationFormatType", "HTML")
				.put("notificationType", "EMAIL");
		
		Object Email_recipients_Array[] = new Object[] {Email_recipients_Element};
		
		JSONObject emailNotificationDetail = new JSONObject()
				.put("recipients", Email_recipients_Array);
		
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
				.put("customerReferences", JSONObject.NULL)
				.put("dimensions", dimensions)
				.put("packageSpecialServices", packageSpecialServices);
		
		Object requestedPackageLineItems_Array[] = new Object[] {requestedPackageLineItemsElement};
		
		Format formatter = new SimpleDateFormat("MMM-d-yyyy"); // "Jul-5-2019", "Jul-11-2019"
		Date dt = new Date();
		Calendar c = Calendar.getInstance(); 
		c.setTime(dt); 
		// c.add(Calendar.DATE, 1);
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
				.put("emailNotificationDetail", emailNotificationDetail)
				// .put("shippingDocumentSpecification", shippingDocumentSpecification)
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
		
		// TESTING IF NEEDED
		httpput.addHeader("X-clientid", "MAGS");
		httpput.addHeader("X-clientversion", "1.0");
		httpput.addHeader("X-locale", "en_AU");
		
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
	}*/
}
