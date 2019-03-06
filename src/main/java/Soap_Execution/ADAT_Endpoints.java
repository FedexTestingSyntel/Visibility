package Soap_Execution;

import static org.junit.Assert.assertThat;
import javax.xml.soap.MimeHeaders;
import javax.xml.soap.SOAPBody;
import javax.xml.soap.SOAPElement;
import javax.xml.soap.SOAPEnvelope;
import javax.xml.soap.SOAPMessage;
import javax.xml.soap.SOAPPart;
import org.hamcrest.CoreMatchers;
import SupportClasses.Helper_Functions;

public class ADAT_Endpoints {

	public static void VelocityCheck(String UserName, String soapCreateUserUrl, String soapVelocityUrl, String Organization, String EvaluateRiskOrganization, int Threshold) throws Exception {
		ADAT_UserCreation(soapCreateUserUrl, Organization, UserName);
		
		int i;
		for (i = 1 ; i < Threshold + 1; i++) {
			String Response = ADAT_EvaluateRisk(soapVelocityUrl, EvaluateRiskOrganization, UserName);
			assertThat(Response, CoreMatchers.containsString("<rf11:advice>ALLOW</rf11:advice>"));
			Helper_Functions.PrintOut("Attempt " + i + ": Allowed", false);
		}
		
		String Response = ADAT_EvaluateRisk(soapVelocityUrl, EvaluateRiskOrganization, UserName);
		assertThat(Response, CoreMatchers.containsString("<rf11:advice>DENY</rf11:advice>"));
		Helper_Functions.PrintOut("Attempt " + i + ": Denied", false);
	}
	
	//will take in the url, organization, and user name
	//will return the soap message as a string
	public static String ADAT_UserCreation(String soapCreateUserUrl, String Organization, String UserName) throws Exception {
		SOAPMessage request = GeneralSoapSupport.Soap_Message_Creation();
		SOAPPart soapPart = request.getSOAPPart();
		SOAPEnvelope envelope = soapPart.getEnvelope();;
		//add the name space for the msgs prefix
		String NSmsgs = "http://ws.arcot.com/ArcotUserRegistrySvc/1.0/msgs";
		envelope.addNamespaceDeclaration("msgs", NSmsgs );
		//add the name space for the ns prefix
		String NSns = "http://ws.arcot.com/UserSchema/1.0";
	    envelope.addNamespaceDeclaration("ns", NSns);
	    
	    //add the header content
	    MimeHeaders headers = request.getMimeHeaders();
        headers.addHeader("Accept-Encoding", "gzip,deflate");
        headers.addHeader("Content-Type", "application/soap+xml;charset=UTF-8;action=\"urn:UDS.CreateUser\"");
        String Host = soapCreateUserUrl.replace("http://", "");
        Host = Host.substring(0, Host.indexOf("/"));
        headers.addHeader("Host", Host);
	    
	    SOAPBody body = envelope.getBody();
	    
	    SOAPElement bodyElmnt = body.addBodyElement(envelope.createName("createUserRequest","msgs", NSmsgs));
	    SOAPElement userDetails = bodyElmnt.addChildElement(envelope.createName("userDetails","msgs", NSmsgs));
	    SOAPElement userId = userDetails.addChildElement(envelope.createName("userId","ns", NSns));
	    
	    SOAPElement Org = userId.addChildElement("orgName", "ns");
	    Org.addTextNode(Organization);
	    
	    SOAPElement Name = userId.addChildElement("userName", "ns");
	    Name.addTextNode(UserName);
       
        // Send SOAP Message to SOAP Server
	    String Response = GeneralSoapSupport.callSoapWebService(soapCreateUserUrl, request);
        return Response;
	}

	//will take in the endpoint, organization, and user name
	//will return the pin number that was generated
	public static String ADAT_PinCreation(String soapCreatePinUrl, String Organization, String UserName) throws Exception {
		SOAPMessage request = GeneralSoapSupport.Soap_Message_Creation();
		SOAPPart soapPart = request.getSOAPPart();
		SOAPEnvelope envelope = soapPart.getEnvelope();;
		String NSmsgs = "http://ws.arcot.com/WebFortIssuanceAPI/7.0/msgs";
		envelope.addNamespaceDeclaration("msgs", NSmsgs );
	    
	    MimeHeaders headers = request.getMimeHeaders();
        headers.addHeader("Accept-Encoding", "gzip,deflate");
        headers.addHeader("Content-Type", "application/soap+xml;charset=UTF-8;action=\"urn:CreateCredential\"");
        String Host = soapCreatePinUrl.replace("http://", "");
        Host = Host.substring(0, Host.indexOf("/"));
        headers.addHeader("Host", Host);
	    
	    SOAPBody body = envelope.getBody();
	    
	    SOAPElement bodyElmnt = body.addBodyElement(envelope.createName("CredentialRequest","msgs", NSmsgs));
	    
	    SOAPElement Org = bodyElmnt.addChildElement("orgName", "msgs");
	    Org.addTextNode(Organization);
	    
	    SOAPElement Name = bodyElmnt.addChildElement("userName", "msgs");
	    Name.addTextNode(UserName);
	    
	    SOAPElement Input = bodyElmnt.addChildElement("otpInput", "msgs");
	    Input.addTextNode("");
       
        // Send SOAP Message to SOAP Server
	    String Response = GeneralSoapSupport.callSoapWebService(soapCreatePinUrl, request);
	    
	    String Pin = ParsePin(Response);
	    if (Pin != null) {
	    	Helper_Functions.PrintOut("Pin Generated: " + Pin, false);
	    }
		
		return Response;
	}
	
	public static String ParsePin(String Request){
        String Start = "<wfiss60xsd:otp>", End = "</wfiss60xsd:otp>";
        if (Request != null && Request.contains(Start) && Request.contains(End)) {
        	return Request.substring(Request.indexOf(Start) + Start.length(), Request.indexOf(End));
        }else {
        	return null;
        }
        
	}
	
	public static String ADAT_VerifyPin(String soapVerifyPinUrl, String Organization, String UserName, String Pin) throws Exception {
		SOAPMessage request = GeneralSoapSupport.Soap_Message_Creation();
		SOAPPart soapPart = request.getSOAPPart();
		SOAPEnvelope envelope = soapPart.getEnvelope();;
		String NSmsgs = "http://ws.arcot.com/WebFortAuthAPI/7.0/msgs";
		envelope.addNamespaceDeclaration("msgs", NSmsgs );
	    
	    MimeHeaders headers = request.getMimeHeaders();
        headers.addHeader("Accept-Encoding", "gzip,deflate");
        headers.addHeader("Content-Type", "application/soap+xml;charset=UTF-8;action=\"urn:VerifyOTP\"");
        String Host = soapVerifyPinUrl.replace("http://", "");
        Host = Host.substring(0, Host.indexOf("/"));
        headers.addHeader("Host", Host);
        headers.addHeader("Connection", "Keep-Alive");
	    
	    SOAPBody body = envelope.getBody();
	    
	    SOAPElement bodyElmnt = body.addBodyElement(envelope.createName("VerifyOTP","msgs", NSmsgs));
	    
	    SOAPElement Org = bodyElmnt.addChildElement("orgName", "msgs");
	    Org.addTextNode(Organization);
	    
	    SOAPElement Name = bodyElmnt.addChildElement("userName", "msgs");
	    Name.addTextNode(UserName);
	    
	    SOAPElement Input = bodyElmnt.addChildElement("otp", "msgs");
	    Input.addTextNode(Pin);
       
        // Send SOAP Message to SOAP Server
	    String Response = GeneralSoapSupport.callSoapWebService(soapVerifyPinUrl, request);
		return Response;
	}
	
	public static String ADAT_EvaluateRisk(String soapEvaluateRiskUrl, String Organization, String UserName) throws Exception{
		SOAPMessage request = GeneralSoapSupport.Soap_Message_Creation();
		SOAPPart soapPart = request.getSOAPPart();
		SOAPEnvelope envelope = soapPart.getEnvelope();;
		//add the name space for the ns prefix
		String NSwsdl = "http://ws.arcot.com/RiskFortEvaluateRiskAPI/3.0/wsdl";
	    envelope.addNamespaceDeclaration("wsdl", NSwsdl);
	    
	    //add the header content
	    MimeHeaders headers = request.getMimeHeaders();
        headers.addHeader("Accept-Encoding", "gzip,deflate");
        headers.addHeader("Content-Type", "application/soap+xml;charset=UTF-8;action=\"urn:evaluateRiskEx30B12\"");
        //note: Host seems to only be needed for the phone pin velocity call
        String Host = soapEvaluateRiskUrl.replace("http://", "");
        Host = Host.substring(0, Host.indexOf("/"));
        headers.addHeader("Host", Host);
        
	    SOAPBody body = envelope.getBody();
	    SOAPElement bodyElmnt = body.addBodyElement(envelope.createName("EvaluateRiskRequest","wsdl", NSwsdl));
	    SOAPElement userContext = bodyElmnt.addChildElement(envelope.createName("userContext","wsdl", NSwsdl));
	    
	    SOAPElement Org = userContext.addChildElement("orgName", "wsdl");
	    Org.addTextNode(Organization);
	    
	    SOAPElement Name = userContext.addChildElement("userName", "wsdl");
	    Name.addTextNode(UserName);
       
        // Send SOAP Message to SOAP Server
	    String Response = GeneralSoapSupport.callSoapWebService(soapEvaluateRiskUrl, request);
        
        String Response_Variables[] = new String[] {"<rf11:advice>", Organization, "The operation was successful."};
        for (String Variable: Response_Variables) {
			assertThat(Response, CoreMatchers.containsString(Variable));
		}
        return Response;
	}
	
}
