package Soap_Execution;

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

	public static SOAPMessage Soap_Message_Creation(String MainNs, String Preferred_Prefix) throws Exception {
    	//needed to customize the default namespace
	    SOAPMessage message = MessageFactory.newInstance(SOAPConstants.SOAP_1_2_PROTOCOL).createMessage();
	    SOAPPart soapPart = message.getSOAPPart();
	    SOAPEnvelope envelope = soapPart.getEnvelope();  
	    SOAPHeader header = message.getSOAPHeader();
	    SOAPBody body = message.getSOAPBody();
	    SOAPFault fault = body.getFault();
	    
	    //get rid of default name space
	    envelope.removeNamespaceDeclaration(envelope.getPrefix());
	    
	    envelope.addNamespaceDeclaration(Preferred_Prefix, MainNs);  
	    envelope.setPrefix(Preferred_Prefix);
	    body.setPrefix(Preferred_Prefix);
	    header.setPrefix(Preferred_Prefix);
	    
	    if (fault != null) {
	        fault.setPrefix(Preferred_Prefix);
	    }
	    
        return message;
    }

	//will execute the soap call and return the response as a string. In event of error will retrun error message as string.
	public static String callSoapWebService(String soapEndpointUrl, SOAPMessage soapRequest) throws Exception {
        
		SOAPMessage soapResponse = null;
		String MethodName = Thread.currentThread().getStackTrace()[2].getMethodName();
		
		try {
    		// Save the SOAP Request
    		ByteArrayOutputStream out = new ByteArrayOutputStream();
            soapRequest.writeTo(out);
            String Request = new String(out.toByteArray()), Response = "";
            //System.out.println("Request for debug: \n" + Request);
            
            // Create SOAP Connection
            SOAPConnectionFactory soapConnectionFactory = SOAPConnectionFactory.newInstance();
            SOAPConnection soapConnection = soapConnectionFactory.createConnection();

            // Send SOAP Message to SOAP Server
            String RequestFormatted = Request.replaceAll("\n", " <New Line> ").replaceAll("\r", " <tab> ");
            Helper_Functions.PrintOut(MethodName + " soapEndpoint: " + soapEndpointUrl + "\n    " +
            						MethodName + " soapRequest: " + RequestFormatted, true);
            soapResponse = soapConnection.call(soapRequest, soapEndpointUrl);
            
            // Save the SOAP Response
            out.reset();
            soapResponse.writeTo(out);
            Response = new String(out.toByteArray());
            String ResponseFormatted = Response.replaceAll("\n", "").replaceAll("\r", "");
            
            soapConnection.close();
            Helper_Functions.PrintOut("    " + MethodName +  " soapResponse: " + ResponseFormatted + "\n", false);
            return Response;
        } catch (Exception e) {
        	//try to find a way to return the soap message in the event of exception. Currently 400 errors are not returning the full soap message.
        	Helper_Functions.PrintOut("    " + MethodName +  " soapResponse: " + e.getMessage() + "\n", false);
        	return e.getMessage();
        }
    }
    
}