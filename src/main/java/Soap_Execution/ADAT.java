package Soap_Execution;

import javax.xml.soap.*;
import static org.junit.Assert.assertThat;
//import static org.hamcrest.core.IsNot.not;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import org.hamcrest.CoreMatchers;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Listeners;
import org.testng.annotations.Test;
import Data_Structures.ADAT_Data;
import SupportClasses.Environment;
import SupportClasses.Helper_Functions;

//listner needed for using overwrites for report generation and 
@Listeners(SupportClasses.TestNG_TestListener.class)

public class ADAT {

	//will parse this string and run all the levels listed in the data provider.
	static String LevelsToTest = "3";	
	
	@BeforeClass
	public void beforeClass() {
		Environment.SetLevelsToTest(LevelsToTest);
	}
	
	@DataProvider (parallel = true)
	public static Iterator<Object[]> dp (Method m) {
		List<Object[]> data = new ArrayList<Object[]>();
		for (int i = 0; i < Environment.LevelsToTest.length(); i++) {
			String Level = String.valueOf(Environment.LevelsToTest.charAt(i));
			if (Level.contains("6")) {
				Helper_Functions.PrintOut("Need to make sure to us Tunnel for L6 execution due to firewall", false);
			}
			ADAT_Data DC = ADAT_Data.LoadVariables(Level);
			String Organizations[] = new String[] {
					DC.OrgPostcard, 
					//DC.OrgPhone
					};
			for (String Org: Organizations) {
				switch (m.getName()) { //Based on the method that is being called the array list will be populated.
				case "ADAT_CreateUser":
				case "ADAT_CreatePin":
				case "ADAT_AddressVelocity":
				case "ADAT_PostcardVelocity":
				case "ADAT_PhoneVelocity":	
				case "ADAT_VerifyPin":
				case "ADAT_VerifyPin_Invalid":
					data.add( new Object[] {Level, DC, Org});
					break;
				}
			}
		}
		System.out.println("Starting " + m.getName() + " : There are " + data.size() + " scenarios.");
		return data.iterator();
	}
	
	@Test(dataProvider = "dp", priority = 1)
	public void ADAT_CreateUser(String Level, ADAT_Data Data_Class, String Organization) throws Exception {
		//Using the assumption the organization will be in the "FDM-??-PIN" format
		String UserName = Helper_Functions.LoadUserID("L" + Level + Organization.substring(4 , Organization.indexOf("-PIN")));
		String Response = ADAT_UserCreation(Data_Class.CreateUserUrl, Organization, UserName);
		
        String Response_Variables[] = new String[] {"udsTransactionID", "The operation was successful!"};
        for (String Variable: Response_Variables) {
			assertThat(Response, CoreMatchers.containsString(Variable));
		}
	}
	
	@Test(dataProvider = "dp", priority = 2)
	public void ADAT_CreatePin(String Level, ADAT_Data Data_Class, String Organization) throws Exception {
		//create the user to do the pin validation. Using the assumption the organization will be in the "FDM-??-PIN" format
		String UserName = Helper_Functions.LoadUserID("L" + Level + Organization.substring(4 , Organization.indexOf("-PIN")));
		ADAT_UserCreation(Data_Class.CreateUserUrl, Organization, UserName);
		
		String Response = ADAT_PinCreation(Data_Class.CreatePinUrl, Organization, UserName);
		
        String Response_Variables[] = new String[] {Organization, "createTime", "validityStartTime", "validityEndTime", "otp"};
        for (String Variable: Response_Variables) {
			assertThat(Response, CoreMatchers.containsString(Variable));
		}
	}
	
	@Test(dataProvider = "dp", priority = 3)
	public void ADAT_AddressVelocity(String Level, ADAT_Data Data_Class, String Organization) throws Exception {
		String UserName = Helper_Functions.LoadUserID("L" + Level + "AddVel");
		VelocityCheck(UserName, Data_Class.CreateUserUrl, Data_Class.VelocityUrl, Organization, Data_Class.OrgAddressVelocity, Data_Class.AddressVelocityThreshold);
	}
	
	@Test(dataProvider = "dp", priority = 3)
	public void ADAT_PostcardVelocity(String Level, ADAT_Data Data_Class, String Organization) throws Exception {
		String UserName = Helper_Functions.LoadUserID("L" + Level + "PostVel");
		VelocityCheck(UserName, Data_Class.CreateUserUrl, Data_Class.VelocityUrl, Organization, Data_Class.OrgPostcardVelocity, Data_Class.PostcardPinVelocityThreshold);
	}
	
	@Test(dataProvider = "dp", priority = 3)
	public void ADAT_PhoneVelocity(String Level, ADAT_Data Data_Class, String Organization) throws Exception {
		String UserName = Helper_Functions.LoadUserID("L" + Level + "PhoneVel");
		VelocityCheck(UserName, Data_Class.CreateUserUrl, Data_Class.VelocityUrl, Organization, Data_Class.OrgPhoneVelocity, Data_Class.PhonePinVelocityThreshold);
	}
	
	@Test(dataProvider = "dp", priority = 4)
	public void ADAT_VerifyPin(String Level, ADAT_Data Data_Class, String Organization) throws Exception {
		//create the user to do the pin validation. Using the assumption the organization will be in the "FDM-??-PIN" format
		String UserName = Helper_Functions.LoadUserID("L" + Level + Organization.substring(4 , Organization.indexOf("-PIN")));
		ADAT_UserCreation(Data_Class.CreateUserUrl, Organization, UserName);

		String Response = ADAT_PinCreation(Data_Class.CreatePinUrl, Organization, UserName);
		
		Response = ADAT_VerifyPin(Data_Class.VerifyPinUrl, Organization, UserName, ParsePin(Response));

        String Response_Variables[] = new String[] {"transactionID", "message", "<cx:message>The operation was successful.</cx:message>"};
        for (String Variable: Response_Variables) {
			assertThat(Response, CoreMatchers.containsString(Variable));
		}
	}
	
	@Test(dataProvider = "dp", priority = 4)
	public void ADAT_VerifyPin_Invalid(String Level, ADAT_Data Data_Class, String Organization) throws Exception {
		//create the user to do the pin validation. Using the assumption the organization will be in the "FDM-??-PIN" format
		String UserName = Helper_Functions.LoadUserID("L" + Level + Organization.substring(4 , Organization.indexOf("-PIN")));
		ADAT_UserCreation(Data_Class.CreateUserUrl, Organization, UserName);

		String Response = ADAT_PinCreation(Data_Class.CreatePinUrl, Organization, UserName);
		
		Response = ADAT_VerifyPin(Data_Class.VerifyPinUrl, Organization, UserName, ParsePin(Response) + "1");
		assertThat(Response, CoreMatchers.containsString("400Bad"));
	}
	
	public void VelocityCheck(String UserName, String soapCreateUserUrl, String soapVelocityUrl, String Organization, String EvaluateRiskOrganization, int Threshold) throws Exception {
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
	public String ADAT_UserCreation(String soapCreateUserUrl, String Organization, String UserName) throws Exception {
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
	public String ADAT_PinCreation(String soapCreatePinUrl, String Organization, String UserName) throws Exception {
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
	
	public String ParsePin(String Request){
        String Start = "<wfiss60xsd:otp>", End = "</wfiss60xsd:otp>";
        if (Request != null && Request.contains(Start) && Request.contains(End)) {
        	return Request.substring(Request.indexOf(Start) + Start.length(), Request.indexOf(End));
        }else {
        	return null;
        }
        
	}
	
	public String ADAT_VerifyPin(String soapVerifyPinUrl, String Organization, String UserName, String Pin) throws Exception {
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
	
	public String ADAT_EvaluateRisk(String soapEvaluateRiskUrl, String Organization, String UserName) throws Exception{
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