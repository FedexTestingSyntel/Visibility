package Soap_Execution;

import javax.xml.soap.MimeHeaders;
import javax.xml.soap.SOAPBody;
import javax.xml.soap.SOAPElement;
import javax.xml.soap.SOAPEnvelope;
import javax.xml.soap.SOAPFault;
import javax.xml.soap.SOAPHeader;
import javax.xml.soap.SOAPMessage;
import javax.xml.soap.SOAPPart;

import org.w3c.dom.Node;

import Data_Structures.Account_Data;

public class WebService_Endpoints {

	private static String MainNaimspace = "http://schemas.xmlsoap.org/soap/envelope/", MainPrefix = "SOAP-ENV";
	
	//will take in the url, organization, and user name
	//will return the soap message as a string
	public static String CreateMeterNumber(String soapCreateMeterNumberURL, Account_Data Account_Info) throws Exception {
		SOAPMessage message = GeneralSoapSupport.Soap_Message_Creation(MainNaimspace, MainPrefix);
		SOAPPart soapPart = message.getSOAPPart();
		SOAPEnvelope envelope = soapPart.getEnvelope();

		//add the name space for the v5 prefix
		String NSv5 = "http://fedex.com/ws/registration/v5";
		envelope.addNamespaceDeclaration("v5", NSv5 );

	    //add the header content
	    MimeHeaders header = message.getMimeHeaders();
        header.addHeader("Accept-Encoding", "gzip,deflate");
        header.addHeader("Content-Type", "application/soap+xml;charset=UTF-8;action=\"urn:deleteAssociation3012\"");
        String Host = soapCreateMeterNumberURL.replace("http://", "");
        Host = Host.substring(0, Host.indexOf("/"));
        header.addHeader("Host", Host);
	    
	    SOAPBody body = envelope.getBody();
	    
	    //SubscriptionRequest starts
	    SOAPElement SubscriptionRequest = body.addBodyElement(envelope.createName("SubscriptionRequest","v5", NSv5));
	    
	    /// WebAuthenticationDetail section starts
	    SOAPElement WebAuthenticationDetail = SubscriptionRequest.addChildElement(envelope.createName("WebAuthenticationDetail","v5", NSv5));
	    
		String strCspKey = "RZEndj3VoTN7IZPr";
		String strCspPassword = "FyOhzHVSNhQOhGlOoT7R5DRAW";
	    SOAPElement CspCredential = WebAuthenticationDetail.addChildElement(envelope.createName("CspCredential","v5", NSv5));
	    SOAPElement Key1 = CspCredential.addChildElement("Key", "v5");
	    Key1.addTextNode(strCspKey);
	    SOAPElement Password1 = CspCredential.addChildElement("Password", "v5");
	    Password1.addTextNode(strCspPassword);
       
		String strUserKey = "IwxibuPidazmPdWY";
		String strUserPassword = "lqPWoVeWGAFboqFOtiBU9qfCy";
	    SOAPElement UserCredential = WebAuthenticationDetail.addChildElement(envelope.createName("UserCredential","v5", NSv5));
	    SOAPElement Key2 = UserCredential.addChildElement("Key", "v5");
	    Key2.addTextNode(strUserKey);
	    SOAPElement Password2 = UserCredential.addChildElement("Password", "v5");
	    Password2.addTextNode(strUserPassword);
	    /// WebAuthenticationDetail ends
	    
	    ///ClientDetail starts
		String strSoftwareId = "FXRS";
		String strSoftwareRelease = "1900";
	    SOAPElement ClientDetail = SubscriptionRequest.addChildElement(envelope.createName("ClientDetail","v5", NSv5));
	    SOAPElement AccountNumber = ClientDetail.addChildElement("AccountNumber", "v5");
	    AccountNumber.addTextNode(Account_Info.Account_Number);
	    SOAPElement SoftwareId = ClientDetail.addChildElement("SoftwareId", "v5");
	    SoftwareId.addTextNode(strSoftwareId);
	    SOAPElement SoftwareRelease = ClientDetail.addChildElement("SoftwareRelease", "v5");
	    SoftwareRelease.addTextNode(strSoftwareRelease);
	    ///ClientDetail ends
	    
	    ///TransactionDetail starts
		String TransactionCustomerTransactionId = "Subscription";
	    SOAPElement TransactionDetail = SubscriptionRequest.addChildElement(envelope.createName("TransactionDetail","v5", NSv5));
	    SOAPElement CustomerTransactionId = TransactionDetail.addChildElement("CustomerTransactionId", "v5");
	    CustomerTransactionId.addTextNode(TransactionCustomerTransactionId);
	    ///TransactionDetail ends
	    
	    ///Version starts
		String VersionServiceId = "fcas";
		String VersionMajor = "5";
		String VersionIntermediate = "1";
		String VersionMinor = "0";
	    SOAPElement Version = SubscriptionRequest.addChildElement(envelope.createName("Version","v5", NSv5));
	    SOAPElement ServiceId = Version.addChildElement("ServiceId", "v5");
	    ServiceId.addTextNode(VersionServiceId);
	    SOAPElement Major = Version.addChildElement("Major", "v5");
	    Major.addTextNode(VersionMajor);
	    SOAPElement Intermediate = Version.addChildElement("Intermediate", "v5");
	    Intermediate.addTextNode(VersionIntermediate);
	    SOAPElement Minor = Version.addChildElement("Minor", "v5");
	    Minor.addTextNode(VersionMinor);
	    ///Version ends
	    
	    String strServiceLevel = "DO_NOT_VALIDATE_ACCOUNT";
		String strCspType = "TRADITIONAL_API";
	    SOAPElement ServiceLevel = SubscriptionRequest.addChildElement("ServiceLevel", "v5");
	    ServiceLevel.addTextNode(strServiceLevel);
	    SOAPElement CspType = SubscriptionRequest.addChildElement("CspType", "v5");
	    CspType.addTextNode(strCspType);
	    
	    ///Subscriber starts
	    SOAPElement Subscriber = SubscriptionRequest.addChildElement(envelope.createName("Subscriber","v5", NSv5));
	    SOAPElement AccountNumber_Subscriber = Subscriber.addChildElement("AccountNumber", "v5");
	    AccountNumber_Subscriber.addTextNode(Account_Info.Account_Number);
	    
	    ////Contact starts
	    
	    SOAPElement Contact = Subscriber.addChildElement(envelope.createName("Contact","v5", NSv5));
	    SOAPElement PersonName = Contact.addChildElement("PersonName", "v5");
	    //PersonName.addTextNode(Account_Info.FirstName);
	    PersonName.addTextNode("Anshul");
	    SOAPElement CompanyName = Contact.addChildElement("CompanyName", "v5");
	    //CompanyName.addTextNode(Account_Info.Company_Name);
	    CompanyName.addTextNode("STORAGETEK PR-SOUTH");
	    SOAPElement PhoneNumber = Contact.addChildElement("PhoneNumber", "v5");
	    //PhoneNumber.addTextNode(Account_Info.Billing_Phone_Number);
	    PhoneNumber.addTextNode("1234567890");
	    SOAPElement EmailAddress = Contact.addChildElement("EMailAddress", "v5");
	    //EmailAddress.addTextNode(Account_Info.Email);
	    EmailAddress.addTextNode("ansh@ayhoo.co.in");
	    ////Contact ends
	    
	    ////Address starts
	    SOAPElement Address = Subscriber.addChildElement(envelope.createName("Address","v5", NSv5));
	    SOAPElement StreetLines = Address.addChildElement("StreetLines", "v5");
	    StreetLines.addTextNode(Account_Info.Shipping_Address_Line_1);
	    SOAPElement City = Address.addChildElement("City", "v5");
	    City.addTextNode(Account_Info.Shipping_City);
	    SOAPElement StateOrProvinceCode = Address.addChildElement("StateOrProvinceCode", "v5");
	    StateOrProvinceCode.addTextNode(Account_Info.Shipping_State_Code);
	    SOAPElement PostalCode = Address.addChildElement("PostalCode", "v5");
	    PostalCode.addTextNode(Account_Info.Shipping_Zip);
	    SOAPElement CountryCode = Address.addChildElement("CountryCode", "v5");
	    CountryCode.addTextNode(Account_Info.Shipping_Country_Code);
	    ////Address ends
	    ///Subscriber ends
	    
	    ////AccountShippingAddress starts
	    		//assuming same address for now
	    SOAPElement AccountShippingAddress = SubscriptionRequest.addChildElement(envelope.createName("AccountShippingAddress","v5", NSv5));
	    SOAPElement StreetLines1 = AccountShippingAddress.addChildElement("StreetLines", "v5");
	    StreetLines1.addTextNode(Account_Info.Billing_Address_Line_1);
	    SOAPElement City1 = AccountShippingAddress.addChildElement("City", "v5");
	    City1.addTextNode(Account_Info.Billing_City);
	    SOAPElement StateOrProvinceCode1 = AccountShippingAddress.addChildElement("StateOrProvinceCode", "v5");
	    StateOrProvinceCode1.addTextNode(Account_Info.Billing_State_Code);
	    SOAPElement PostalCode1 = AccountShippingAddress.addChildElement("PostalCode", "v5");
	    PostalCode1.addTextNode(Account_Info.Billing_Zip);
	    SOAPElement CountryCode1 = AccountShippingAddress.addChildElement("CountryCode", "v5");
	    CountryCode1.addTextNode(Account_Info.Billing_Country_Code);
	    ////AccountShippingAddress ends
	    
	    String strPeripheralType = "SOFTWARE";
	    SOAPElement PeripheralType = SubscriptionRequest.addChildElement("PeripheralType", "v5");
	    PeripheralType.addTextNode(strPeripheralType);
	    //SubscriptionRequest ends
	    
        // Send SOAP Message to SOAP Server
	    String Response = GeneralSoapSupport.callSoapWebService(soapCreateMeterNumberURL, message);
        return Response;
	}
	
}
