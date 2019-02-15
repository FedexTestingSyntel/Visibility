package SoapExample;

import java.io.ByteArrayOutputStream;
import javax.xml.soap.MessageFactory;
import javax.xml.soap.SOAPBody;
import javax.xml.soap.SOAPConnection;
import javax.xml.soap.SOAPConnectionFactory;
import javax.xml.soap.SOAPConstants;
import javax.xml.soap.SOAPEnvelope;
import javax.xml.soap.SOAPFault;
import javax.xml.soap.SOAPHeader;
import javax.xml.soap.SOAPMessage;
import javax.xml.soap.SOAPPart;
import SupportClasses.Helper_Functions;

public class GeneralSoapSupport {

	public static SOAPMessage Soap_Message_Creation() throws Exception {
    	//needed to change the default name space from "http://schemas.xmlsoap.org/soap/envelope/" to "http://www.w3.org/2003/05/soap-envelope"
	    SOAPMessage message = MessageFactory.newInstance(SOAPConstants.SOAP_1_2_PROTOCOL).createMessage();
	    SOAPPart soapPart = message.getSOAPPart();
	    SOAPEnvelope envelope = soapPart.getEnvelope();  
	    SOAPHeader header = message.getSOAPHeader();
	    SOAPBody body = message.getSOAPBody();
	    SOAPFault fault = body.getFault();
	    
	    //get rid of default name space
	    envelope.removeNamespaceDeclaration(envelope.getPrefix());
	    
	    String Preferred_Prefix = "soap";
	    envelope.addNamespaceDeclaration(Preferred_Prefix, "http://www.w3.org/2003/05/soap-envelope");
	    envelope.setPrefix(Preferred_Prefix);
	    body.setPrefix(Preferred_Prefix);
	    header.setPrefix(Preferred_Prefix);
	    
	    if (fault != null) {
	        fault.setPrefix(Preferred_Prefix);
	    }
	    
        return message;
    }

	public static String callSoapWebService(String soapEndpointUrl, SOAPMessage soapRequest) throws Exception {
        try {
    		String MethodName = Thread.currentThread().getStackTrace()[2].getMethodName();
    		// Save the SOAP Request
    		ByteArrayOutputStream out = new ByteArrayOutputStream();
            soapRequest.writeTo(out);
            String Request = new String(out.toByteArray()), Response = "";
            System.out.println(Request);
            
            // Create SOAP Connection
            SOAPConnectionFactory soapConnectionFactory = SOAPConnectionFactory.newInstance();
            SOAPConnection soapConnection = soapConnectionFactory.createConnection();

            // Send SOAP Message to SOAP Server
            SOAPMessage soapResponse = soapConnection.call(soapRequest, soapEndpointUrl);
            
            // Save the SOAP Response
            out.reset();
            soapResponse.writeTo(out);
            Response = new String(out.toByteArray());
            
            Helper_Functions.PrintOut(MethodName + " soapEndpoint: " + soapEndpointUrl + "\n    " +
            						MethodName + " soapRequest: " + Request + "\n    " +
            						MethodName +  " soapResponse: " + Response + "\n", true);
            
            soapConnection.close();
            return Response;
        } catch (Exception e) {
            System.err.println("\nError occurred while sending SOAP Request to Server!"
            		+ "\nMake sure you have the correct endpoint URL and SOAPAction!\n");
            throw e;
        }
    }
    
}