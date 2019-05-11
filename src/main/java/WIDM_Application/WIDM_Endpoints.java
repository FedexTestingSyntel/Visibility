package WIDM_Application;

import javax.xml.soap.MimeHeaders;
import javax.xml.soap.Name;
import javax.xml.soap.SOAPBody;
import javax.xml.soap.SOAPElement;
import javax.xml.soap.SOAPEnvelope;
import javax.xml.soap.SOAPHeader;
import javax.xml.soap.SOAPHeaderElement;
import javax.xml.soap.SOAPMessage;
import javax.xml.soap.SOAPPart;
import Data_Structures.Account_Data;
import Data_Structures.User_Data;
import Soap_Execution.GeneralSoapSupport;
import SupportClasses.Helper_Functions;

public class WIDM_Endpoints {
	private static String MainNaimspace = "http://schemas.xmlsoap.org/soap/envelope/", MainPrefix = "soapenv";
	
	//will return the soap message as a string
	public static String AAA_User_Create(String soapCreateUserUrl, Account_Data Accont_Info, String Email_As_UserId) throws Exception {
		try {
			String FillerCompany = "Company" + Helper_Functions.CurrentDateTime(), lnge_Cd = "en", email_Flag = "false", device_ID = "12345", ip_address = "192.168.33.26", fdxcbid_BAR = "12345678901234", email_opt_flag = "false";
			
			SOAPMessage request = GeneralSoapSupport.Soap_Message_Creation(MainNaimspace, MainPrefix);
			SOAPPart soapPart = request.getSOAPPart();
			SOAPEnvelope envelope = soapPart.getEnvelope();
			//add the name space for the ns prefix
			String NS = "http://tews6/wsdl";
			envelope.addNamespaceDeclaration("", NS);
			SOAPBody soapBody = envelope.getBody();

		    //add the header content
		    SOAPHeader header = envelope.getHeader();
		    Name name = envelope.createName("AAAUserCreateTaskContext", "", NS);
		    SOAPHeaderElement CreateTaskContext = header.addHeaderElement(name);
		    SOAPElement AdminElement = CreateTaskContext.addChildElement("admin_id", "");
		    AdminElement.addTextNode("tewsadmin");
		    
			MimeHeaders headers = request.getMimeHeaders();
			headers.addHeader("Accept-Encoding", "gzip,deflate");
			headers.addHeader("Content-Type", "text/xml;charset=UTF-8");
			headers.addHeader("SOAPAction", "AAAUserCreateSoap");
			String Host = soapCreateUserUrl.replace("http://", "");
			Host = Host.substring(0, Host.indexOf("/"));
			headers.addHeader("Host", Host);
			 
		    
		    // SOAP Body
			SOAPElement soapBodyElem, soapBodyElem1, soapBodyElem2;
		 	soapBodyElem = soapBody.addChildElement("AAAUserCreate");
		 	soapBodyElem1 = soapBodyElem.addChildElement("AAAUserCreateProfileTab");
		 			
		 	// Construct Elements under "AAAUserCreateProfileTab"
		 	soapBodyElem2 = soapBodyElem1.addChildElement("_PCT_FIRST_NAME_PCT_");
		 	soapBodyElem2.addTextNode(Accont_Info.FirstName);

		 	soapBodyElem2 = soapBodyElem1.addChildElement("LOGIN_DOT_INITIALS_NM");
		 	soapBodyElem2.addTextNode(Accont_Info.MiddleName);

		 	soapBodyElem2 = soapBodyElem1.addChildElement("_PCT_LAST_NAME_PCT_");
		 	soapBodyElem2.addTextNode(Accont_Info.LastName);
		 	
		 	soapBodyElem2 = soapBodyElem1.addChildElement("_PCT_FULL_NAME_PCT_");
		 	soapBodyElem2.addTextNode(Accont_Info.FirstName + " " + Accont_Info.LastName);
		 	
		 	soapBodyElem2 = soapBodyElem1.addChildElement("LOGIN_DOT_COMPANY_NM");
		 	soapBodyElem2.addTextNode(FillerCompany);
		 	
		 	soapBodyElem2 = soapBodyElem1.addChildElement("_PCT_EMAIL_PCT_");
		 	soapBodyElem2.addTextNode(Accont_Info.Email);

		 	soapBodyElem2 = soapBodyElem1.addChildElement("LOGIN_DOT_STREET_DESC");
		 	soapBodyElem2.addTextNode(Accont_Info.Billing_Address_Line_1);
		 	
		 	soapBodyElem2 = soapBodyElem1.addChildElement("LOGIN_DOT_STREET2_DESC");
		 	soapBodyElem2.addTextNode(Accont_Info.Billing_Address_Line_2);
		 	
		 	soapBodyElem2 = soapBodyElem1.addChildElement("LOGIN_DOT_CITY_NM");
		 	soapBodyElem2.addTextNode(Accont_Info.Billing_City);
		 	
		 	soapBodyElem2 = soapBodyElem1.addChildElement("LOGIN_DOT_STATE_CD");
		 	soapBodyElem2.addTextNode(Accont_Info.Billing_State_Code);
		 	
		 	soapBodyElem2 = soapBodyElem1.addChildElement("LOGIN_DOT_POSTAL_CD");
		 	soapBodyElem2.addTextNode(Accont_Info.Billing_Zip);
		 	
		 	soapBodyElem2 = soapBodyElem1.addChildElement("_PCT_COUNTRY_PCT_");
		 	soapBodyElem2.addTextNode(Accont_Info.Billing_Country_Code);
		 	
		 	soapBodyElem2 = soapBodyElem1.addChildElement("LOGIN_DOT_PHONE_NBR");
		 	soapBodyElem2.addTextNode(Accont_Info.Billing_Phone_Number);
		 	
		 	soapBodyElem2 = soapBodyElem1.addChildElement("LOGIN_DOT_FAX_NBR");
		 	soapBodyElem2.addTextNode(Accont_Info.Billing_Phone_Number);                  ////currently sending the phone number

		 	//populate the field if receive a value, if no value then will not add
		 	if(Email_As_UserId != null && !Email_As_UserId.isEmpty()){
		 		soapBodyElem2 = soapBodyElem1.addChildElement("LOGIN_DOT_EMAIL_AS_USERID_FLG");
		 		soapBodyElem2.addTextNode(Email_As_UserId);	
		 	}
		 	
		 	soapBodyElem2 = soapBodyElem1.addChildElement("_PCT_USER_ID_PCT_");
		 	soapBodyElem2.addTextNode(Accont_Info.UserId);
		 	
		 	soapBodyElem2 = soapBodyElem1.addChildElement("_BAR_fedexConfirmPassword_BAR_");
		 	soapBodyElem2.addTextNode(Accont_Info.Password);

		 	soapBodyElem2 = soapBodyElem1.addChildElement("LOGIN_DOT_SECRET_QUESTION_DESC");
		 	soapBodyElem2.addTextNode(Accont_Info.Secret_Question);
		 	
		 	soapBodyElem2 = soapBodyElem1.addChildElement("_BAR_LOGIN_DOT_CRYPT_ANSWER_DESC1_BAR_");
		 	soapBodyElem2.addTextNode(Accont_Info.Secret_Answer);
		 	
		 	soapBodyElem2 = soapBodyElem1.addChildElement("_PCT_LANGUAGE_PCT_");
		 	soapBodyElem2.addTextNode(lnge_Cd);
		 	
		 	soapBodyElem2 = soapBodyElem1.addChildElement("LOGIN_DOT_EMAIL_ALLOWED_FLG");
		 	soapBodyElem2.addTextNode(email_Flag);
		 	
		 	soapBodyElem2 = soapBodyElem1.addChildElement("_PCT_DEVICE_ID_PCT_");
		 	soapBodyElem2.addTextNode(device_ID);
		 	
		 	soapBodyElem2 = soapBodyElem1.addChildElement("_PCT_IP_ADDRESS_PCT_");
		 	soapBodyElem2.addTextNode(ip_address);
		 	
		 	soapBodyElem2 = soapBodyElem1.addChildElement("_PCT_FDX_CBID_PCT_");
		 	soapBodyElem2.addTextNode(fdxcbid_BAR);
		 	
		 	soapBodyElem2 = soapBodyElem1.addChildElement("LOGIN_DOT_EMAIL_OPT_IN_FLG");
		 	soapBodyElem2.addTextNode(email_opt_flag);
	       
	        // Send SOAP Message to SOAP Server
		    String Response = GeneralSoapSupport.callSoapWebService(soapCreateUserUrl, request);
	        return Response;
	        //<?xml version="1.0" encoding="UTF-8"?><soapenv:Envelope xmlns:soapenv="http://schemas.xmlsoap.org/soap/envelope/" xmlns="http://tews6/wsdl" xmlns:xsd="http://www.w3.org/2001/XMLSchema" xmlns:SOAP-ENC="http://schemas.xmlsoap.org/soap/encoding/" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance" xsi:schemaLocation="http://schemas.xmlsoap.org/soap/envelope/ http://schemas.xmlsoap.org/soap/envelope/" ><soapenv:Body><ImsStatus version="6.0" ><transactionId>20b6148e-8734bb31-5606e0e6-659b3</transactionId></ImsStatus></soapenv:Body></soapenv:Envelope>

		} catch (Exception e) {
			System.err.println("Error in AAA_User_Create");
			return null;
		}
	}

	//will return the soap message as a string
	public static String AAA_User_Update(String soapUpdateUserUrl, User_Data User_Info, String Email_As_UserId) throws Exception {
		try {
			String FillerCompany = "Company" + Helper_Functions.CurrentDateTime();
			
			SOAPMessage request = GeneralSoapSupport.Soap_Message_Creation(MainNaimspace, MainPrefix);
			SOAPPart soapPart = request.getSOAPPart();
			SOAPEnvelope envelope = soapPart.getEnvelope();
			//add the name space for the ns prefix
			String NS = "http://tews6/wsdl";
			envelope.addNamespaceDeclaration("", NS);
			SOAPBody soapBody = envelope.getBody();

			
		    //add the header content
		    SOAPHeader header = envelope.getHeader();
		    Name name = envelope.createName("AAAUserUpdateTaskContext", "", NS);
		    SOAPHeaderElement CreateTaskContext = header.addHeaderElement(name);
		    SOAPElement AdminElement = CreateTaskContext.addChildElement("admin_id", "");
		    AdminElement.addTextNode("tewsadmin");
		    
			MimeHeaders headers = request.getMimeHeaders();
			headers.addHeader("Accept-Encoding", "gzip,deflate");
			headers.addHeader("Content-Type", "text/xml;charset=UTF-8");
			headers.addHeader("SOAPAction", "AAAUserCreateSoap");
			String Host = soapUpdateUserUrl.replace("http://", "");
			Host = Host.substring(0, Host.indexOf("/"));
			headers.addHeader("Host", Host);
			 
		    
		    // SOAP Body
			SOAPElement soapBodyElem, soapBodyElem1, soapBodyElem2, soapBodyElem3;
			soapBodyElem = soapBody.addChildElement("AAAUserUpdate");
			soapBodyElem1 = soapBodyElem.addChildElement("AAAUserUpdateSearch");
			soapBodyElem2 = soapBodyElem1.addChildElement("Subject");
			soapBodyElem3 = soapBodyElem2.addChildElement("UID");
			soapBodyElem3.addTextNode(User_Info.UUID_NBR);

			soapBodyElem1 = soapBodyElem.addChildElement("AAAUserUpdateProfileTab");

			// Construct Elements under "AAAUserUpdateProfileTab"
			soapBodyElem2 = soapBodyElem1.addChildElement("_PCT_UUID_NBR_PCT_");
			soapBodyElem2.addTextNode(User_Info.UUID_NBR);

			soapBodyElem2 = soapBodyElem1.addChildElement("_PCT_FIRST_NAME_PCT_");
			soapBodyElem2.addTextNode(User_Info.FIRST_NM);
			soapBodyElem2 = soapBodyElem1.addChildElement("LOGIN_DOT_INITIALS_NM");
			soapBodyElem2.addTextNode(User_Info.MIDDLE_NM);
			soapBodyElem2 = soapBodyElem1.addChildElement("_PCT_LAST_NAME_PCT_");
			soapBodyElem2.addTextNode(User_Info.LAST_NM);

			soapBodyElem2 = soapBodyElem1.addChildElement("LOGIN_DOT_COMPANY_NM");
			soapBodyElem2.addTextNode(FillerCompany);

			soapBodyElem2 = soapBodyElem1.addChildElement("LOGIN_DOT_STREET_DESC");
			soapBodyElem2.addTextNode(User_Info.STREET_DESC);
			soapBodyElem2 = soapBodyElem1.addChildElement("LOGIN_DOT_STREET_2DESC");
			soapBodyElem2.addTextNode(User_Info.STREET_DESC_2);
			soapBodyElem2 = soapBodyElem1.addChildElement("LOGIN_DOT_CITY_NM");
			soapBodyElem2.addTextNode(User_Info.CITY_NM);
			soapBodyElem2 = soapBodyElem1.addChildElement("LOGIN_DOT_STATE_CD");
			soapBodyElem2.addTextNode(User_Info.STATE_CD);
			soapBodyElem2 = soapBodyElem1.addChildElement("LOGIN_DOT_POSTAL_CD");
			soapBodyElem2.addTextNode(User_Info.POSTAL_CD);
			soapBodyElem2 = soapBodyElem1.addChildElement("_PCT_COUNTRY_PCT_");
			soapBodyElem2.addTextNode(User_Info.COUNTRY_CD);
			soapBodyElem2 = soapBodyElem1.addChildElement("LOGIN_DOT_PHONE_NBR");
			soapBodyElem2.addTextNode(User_Info.PHONE);
			soapBodyElem2 = soapBodyElem1.addChildElement("LOGIN_DOT_FAX_NBR");
			soapBodyElem2.addTextNode(User_Info.FAX_NUMBER);
			
			if(Email_As_UserId != null && !Email_As_UserId.isEmpty()){
				soapBodyElem2 = soapBodyElem1.addChildElement("LOGIN_DOT_EMAIL_AS_USERID_FLG");
				soapBodyElem2.addTextNode(Email_As_UserId);
			}

			soapBodyElem2 = soapBodyElem1.addChildElement("_PCT_EMAIL_PCT_");
			soapBodyElem2.addTextNode(User_Info.EMAIL_ADDRESS);
			soapBodyElem2 = soapBodyElem1.addChildElement("_PCT_USER_ID_PCT_");
			soapBodyElem2.addTextNode(User_Info.USER_ID);
			soapBodyElem2 = soapBodyElem1.addChildElement("_BAR_fedexOldPassword_BAR_");
			soapBodyElem2.addTextNode(User_Info.PASSWORD);
			soapBodyElem2 = soapBodyElem1.addChildElement("_BAR_fedexConfirmPassword_BAR_");
			soapBodyElem2.addTextNode(User_Info.PASSWORD);
			soapBodyElem2 = soapBodyElem1.addChildElement("LOGIN_DOT_SECRET_QUESTION_DESC");
			soapBodyElem2.addTextNode(User_Info.SECRET_QUESTION_DESC);
			soapBodyElem2 = soapBodyElem1.addChildElement("_BAR_LOGIN_DOT_CRYPT_ANSWER_DESC1_BAR_");
			soapBodyElem2.addTextNode(User_Info.SECRET_ANSWER_DESC);
			
			//Using filler values at this time.
			String lnge_Cd = "en", email_Flag = "false", ph_intl_call_cd = "080",mobile_intl_call_cd  = "901", fax_intl_call_cd = "44", ip_address = "192.168.33.26", device_id = "123456", fdxcbid_BAR = "987654123456", email_opt_flag = "false";
			
			soapBodyElem2 = soapBodyElem1.addChildElement("_PCT_LANGUAGE_PCT_");
			soapBodyElem2.addTextNode(lnge_Cd);
			soapBodyElem2 = soapBodyElem1.addChildElement("_BAR_EMAIL_FLAG_BAR_");
			soapBodyElem2.addTextNode(email_Flag);
			soapBodyElem2 = soapBodyElem1.addChildElement("LOGIN_DOT_MOBILE_PHONE_NBR");
			soapBodyElem2.addTextNode(User_Info.MOBILE_PHONE);
			soapBodyElem2 = soapBodyElem1.addChildElement("LOGIN_DOT_PH_INTL_CALL_PREFIX_CD");
			soapBodyElem2.addTextNode(ph_intl_call_cd);
			soapBodyElem2 = soapBodyElem1.addChildElement("LOGIN_DOT_MOBL_INTL_CALL_PREFIX_CD");
			soapBodyElem2.addTextNode(mobile_intl_call_cd);
			soapBodyElem2 = soapBodyElem1.addChildElement("LOGIN_DOT_FAX_INTL_CALL_PREFIX_CD");
			soapBodyElem2.addTextNode(fax_intl_call_cd);
			soapBodyElem2 = soapBodyElem1.addChildElement("_BAR_ipaddress_BAR_");
			soapBodyElem2.addTextNode(ip_address);
			soapBodyElem2 = soapBodyElem1.addChildElement("_BAR_deviceid_BAR_");
			soapBodyElem2.addTextNode(device_id);
			soapBodyElem2 = soapBodyElem1.addChildElement("_BAR_fdxcbid_BAR_");
			soapBodyElem2.addTextNode(fdxcbid_BAR);
			soapBodyElem2 = soapBodyElem1.addChildElement("LOGIN_DOT_EMAIL_OPT_IN_FLG");
			soapBodyElem2.addTextNode(email_opt_flag);
	       
	        // Send SOAP Message to SOAP Server
		    String Response = GeneralSoapSupport.callSoapWebService(soapUpdateUserUrl, request);
	        return Response;
	        //<?xml version="1.0" encoding="UTF-8"?><soapenv:Envelope xmlns:soapenv="http://schemas.xmlsoap.org/soap/envelope/" xmlns="http://tews6/wsdl" xmlns:xsd="http://www.w3.org/2001/XMLSchema" xmlns:SOAP-ENC="http://schemas.xmlsoap.org/soap/encoding/" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance" xsi:schemaLocation="http://schemas.xmlsoap.org/soap/envelope/ http://schemas.xmlsoap.org/soap/envelope/" >
	        //<soapenv:Body><ImsStatus version="6.0" ><transactionId>20b6148e-8734bb31-5606e0e6-659b3</transactionId></ImsStatus></soapenv:Body></soapenv:Envelope>

		} catch (Exception e) {
			System.err.println("Error in AAA_User_Create");
			return null;
		}
	}
	

}
