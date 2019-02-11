package SoapExample;

import javax.xml.namespace.QName;
import javax.xml.soap.*;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertTrue;
import javax.xml.ws.handler.MessageContext;
import javax.xml.ws.handler.soap.SOAPHandler;
import javax.xml.ws.handler.soap.SOAPMessageContext;
import java.io.ByteArrayOutputStream;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;
import org.w3c.dom.NamedNodeMap;

public class SoapAutomation {

	@DataProvider (parallel = true)
	public static Iterator<Object[]> dp(Method m) {
		List<Object[]> data = new ArrayList<Object[]>();
		String soapEndpointUrl = "";
			switch (m.getName()) { //Based on the method that is being called the array list will be populated.
				case "Soap_Test":
					//String SOAPUrl = ;
					soapEndpointUrl = "http://widmtest.fedex.com:10099/iam/im/TEWS6/widm";
					data.add( new Object[] {soapEndpointUrl});
					break;
				case "ADAT_CreateUser":
					//String SOAPUrl = ;
					soapEndpointUrl = "http://adat9-udweb-l3-edcw.test.cloud.fedex.com:7001/arcotuds/services/ArcotUserRegistrySvc";
					data.add( new Object[] {soapEndpointUrl});
					break;
				default:
					soapEndpointUrl = "www.test.com";
					data.add( new Object[] {soapEndpointUrl});
					break;
			}
		System.out.println("Starting " + m.getName() + " : There are " + data.size() + " scenarios.");
		return data.iterator();
	}
	
	@Test(dataProvider = "dp", description = "411835", enabled = false)
	public void Soap_Test(String SOAPUrl){		
		try {
		
			callSoapWebService(SOAPUrl);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	@Test(dataProvider = "dp", description = "ADAT", enabled = false)
	public void ADAT_CreateUser(String EndpointUrl){		
		try {
            // Create SOAP Connection
            SOAPConnectionFactory soapConnectionFactory = SOAPConnectionFactory.newInstance();
            SOAPConnection soapConnection = soapConnectionFactory.createConnection();

            // Send SOAP Message to SOAP Server
           // SOAPMessage soapResponse = soapConnection.call(ADAT_CreateUser_Call(), EndpointUrl);

            // Print the SOAP Response
            System.out.println("Response SOAP Message:");
            //soapResponse.writeTo(System.out);
            System.out.println();

            soapConnection.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	@Test
	public void testSoapBody() throws Exception {
		SOAPMessage message = ADAT_Soap_Creation();
		SOAPPart soapPart = message.getSOAPPart();
		SOAPEnvelope envelope = soapPart.getEnvelope();
	    SOAPBody body = envelope.getBody();
	    
	    SOAPElement bodyElmnt = body.addBodyElement(envelope.createName("createUserRequest","msgs",""));
	    
	    SOAPElement userDetails = bodyElmnt.addChildElement(envelope.createName("userDetails","msgs", "a"));	//need to get rid of the name space value.
	    SOAPElement userId = userDetails.addChildElement(envelope.createName("userId","ns","a"));//need to get rid of the name space value.
	    SOAPElement Org = userId.addChildElement("orgName", "ns");
	    Org.addTextNode("FDM-POSTCARD-PIN");
	    SOAPElement Name = userId.addChildElement("userName", "ns");
	    Name.addTextNode("L3Feb062019");
	   // userDetails.addChildElement("createUserRequest","msg");
	    
	    System.out.println("Request SOAP Message:");
	    message.writeTo(System.out);
        System.out.println("\n");
        
        
        // Create SOAP Connection
        SOAPConnectionFactory soapConnectionFactory = SOAPConnectionFactory.newInstance();
        SOAPConnection soapConnection = soapConnectionFactory.createConnection();

        // Send SOAP Message to SOAP Server
        String soapEndpointUrl = "http://adat9-udweb-l3-edcw.test.cloud.fedex.com:7001/arcotuds/services/ArcotUserRegistrySvc";
        SOAPMessage soapResponse = soapConnection.call(message, soapEndpointUrl);
        System.out.println("Response SOAP Message:");
        soapResponse.writeTo(System.out);
        System.out.println("\n");
	}
	
	
	@Test(dataProvider = "dp", description = "411835", enabled = false)
	public void Example(String SOAPUrl){		
		try {
			MessageFactory messageFactory = MessageFactory.newInstance();
		    SOAPMessage soapMessage = messageFactory.createMessage();
		    //createSimpleSOAPPart( soapMessage);
		   // createSOAPPart( soapMessage) ;
		    soapMessage.writeTo(System.out);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	

    private static SOAPMessage ADAT_Soap_Creation() throws Exception {
	    MessageFactory fact = MessageFactory.newInstance();
	    SOAPMessage message = fact.createMessage();
	    SOAPPart soapPart = message.getSOAPPart();
	    SOAPEnvelope envelope = soapPart.getEnvelope();;  //need to cheand the default name space from "http://schemas.xmlsoap.org/soap/envelope/" to "http://www.w3.org/2003/05/soap-envelope"
	    envelope.setAttribute("xmlns:SOAP-ENV" , "http://www.w3.org/2003/05/soap-envelope");
	    envelope.addNamespaceDeclaration("msgs", "http://ws.arcot.com/ArcotUserRegistrySvc/1.0/msgs");
	    envelope.addNamespaceDeclaration("ns", "http://ws.arcot.com/UserSchema/1.0");
        return message;
    }
	
    private static void callSoapWebService(String soapEndpointUrl) {
        try {
            // Create SOAP Connection
            SOAPConnectionFactory soapConnectionFactory = SOAPConnectionFactory.newInstance();
            SOAPConnection soapConnection = soapConnectionFactory.createConnection();

            // Send SOAP Message to SOAP Server
            SOAPMessage soapResponse = soapConnection.call(createSOAPRequest(), soapEndpointUrl);

            // Print the SOAP Response
            System.out.println("Response SOAP Message:");
            soapResponse.writeTo(System.out);
            System.out.println();

            soapConnection.close();
        } catch (Exception e) {
            System.err.println("\nError occurred while sending SOAP Request to Server!\nMake sure you have the correct endpoint URL and SOAPAction!\n");
            e.printStackTrace();
        }
    }
	
	private static void WIDM_UserCreate(SOAPMessage soapMessage) throws SOAPException {
        SOAPPart soapPart = soapMessage.getSOAPPart();

        String myNamespace = "TestDesc";
        String myNamespaceURI = "http://tews6/wsdl";

        // SOAP Envelope
        SOAPEnvelope envelope = soapPart.getEnvelope();
        envelope.addNamespaceDeclaration(myNamespace, myNamespaceURI);
        
		SOAPHeader header = envelope.getHeader();
		String Proj = "Project";
		SOAPHeaderElement header_one = header.addHeaderElement(new QName(Proj, "AAAUserCreateTaskContext"));
		header_one.setTextContent("tewsadmin");
		//////////////////////////////Need to work on this to get headers in correct format
		
		SOAPBody body = envelope.getBody();
		SOAPBodyElement element = body.addBodyElement(new QName("Create", "AAAUserCreate"));
		element.addChildElement("WS").addTextNode("Training on Web service");

		SOAPBodyElement element1 = body.addBodyElement(envelope.createName("JAVA3", "training4", "webB"));
		element1.addChildElement("Spring").addTextNode("Training on Spring 3.0");
        
            /*
            Constructed SOAP Request Message:
            <SOAP-ENV:Envelope xmlns:SOAP-ENV="http://schemas.xmlsoap.org/soap/envelope/" xmlns:myNamespace="https://www.w3schools.com/xml/">
                <SOAP-ENV:Header/>
                <SOAP-ENV:Body>
                    <myNamespace:CelsiusToFahrenheit>
                        <myNamespace:Celsius>100</myNamespace:Celsius>
                    </myNamespace:CelsiusToFahrenheit>
                </SOAP-ENV:Body>
            </SOAP-ENV:Envelope>
            */

    }

    private static SOAPMessage createSOAPRequest() throws Exception {
        MessageFactory messageFactory = MessageFactory.newInstance();
        SOAPMessage soapMessage = messageFactory.createMessage();

        WIDM_UserCreate(soapMessage);

        soapMessage.saveChanges();

        /* Print the request message, just for debugging purposes */
        System.out.println("Request SOAP Message:");
        soapMessage.writeTo(System.out);
        System.out.println("\n");

        return soapMessage;
    }
}



/*
private void createSOAPPart(SOAPMessage message) throws SOAPException {
SOAPPart sPart = message.getSOAPPart();
SOAPEnvelope env = sPart.getEnvelope();
SOAPBody body = env.getBody();

final SOAPHeader soapHeader = env.getHeader();
soapHeader
        .addHeaderElement(env.createName("TestHeader1", "swa", "http://fakeNamespace.org"));
soapHeader
        .addHeaderElement(env.createName("TestHeader2", "swa", "http://fakeNamespace.org"));
final SOAPHeaderElement headerEle3 =
        soapHeader.addHeaderElement(
                env.createName("TestHeader3", "swa", "http://fakeNamespace.org"));
final SOAPElement ch1 = headerEle3.addChildElement("he3", "swa");
ch1.addTextNode("Im Header Element of header3");

Name ns = env.createName("echo", "swa", "http://fakeNamespace.org");
SOAPBodyElement bodyElement = body.addBodyElement(ns);

Name nameMain = env.createName("internal");
SOAPElement mainChildEle = bodyElement.addChildElement(nameMain);

Name ns2 = env.createName("text");
SOAPElement textReference = mainChildEle.addChildElement(ns2);
Name hrefAttr = env.createName("href");
textReference.addAttribute(hrefAttr, "cid:submitSampleText@apache.org");

Name ns3 = env.createName("image");
SOAPElement imageReference = mainChildEle.addChildElement(ns3);
Name ns31 = env.createName("inner");
final SOAPElement img = imageReference.addChildElement(ns31);
img.addAttribute(hrefAttr, "cid:submitSampleImage@apache.org");

Name ns4 = env.createName("plaintxt");
SOAPElement plainTxt = mainChildEle.addChildElement(ns4);
plainTxt.addTextNode("This is simple plain text");

Name ns5 = env.createName("nested");
SOAPElement nested = mainChildEle.addChildElement(ns5);
nested.addTextNode("Nested1 Plain Text");
Name ns6 = env.createName("nested2");
SOAPElement nested2 = nested.addChildElement(ns6);
nested2.addTextNode("Nested2 Plain Text");
}*/