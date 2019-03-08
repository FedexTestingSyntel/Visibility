package syntel.steps;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.StringReader;
import java.io.StringWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.net.URLConnection;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.soap.MessageFactory;
import javax.xml.soap.SOAPBody;
import javax.xml.soap.SOAPConnection;
import javax.xml.soap.SOAPConnectionFactory;
import javax.xml.soap.SOAPElement;
import javax.xml.soap.SOAPEnvelope;
import javax.xml.soap.SOAPException;
import javax.xml.soap.SOAPHeader;
import javax.xml.soap.SOAPMessage;
import javax.xml.soap.SOAPPart;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import java.awt.HeadlessException;
import syntel.hwdriver.ExecutionInstance;
import syntel.hwdriver.Sessions;

public class FedexInfosecChange {
	private static final String ODE_ELEMENT_UNDEPLOY = "undeploy";
	private static final String ODE_ELEMENT_PACKAGENAME = "packageName";
	String SessionID = "";
	private static Sessions ses;
	int requiredStep;

	public FedexInfosecChange(String sessionID) {
		super();
		SessionID = sessionID;
		ses = ExecutionInstance.getSession(SessionID);
		ses.getScenario_ID();
		requiredStep = ses.getCurrentStepUnderExecution();
	}

	private String getTestData(String string, int Count) {
		if (ses.getVariable(string) != null) {
			String[] var = ses.getVariable(string).split(";");
			if (var.length > Count)
				return var[Count];
			else
				return "";
		} else
			return "";
	}

	public void callFedexInfosecChangeService(int count) {
		try {
			String endpointURLRegion = ses.getTestData("Region");
			String soapEndpointUrl = null;
			if (endpointURLRegion.contentEquals("L2")) {
				soapEndpointUrl = "http://widmdev.idev.fedex.com:10099/iam/im/TEWS6/widm";
				ExecutionInstance.addStepEntry(ses, "END Point URL was updated sucessfully",
						"EndPoint URL " + soapEndpointUrl, "Pass");
			}

			// Create SOAP Connection
			SOAPConnectionFactory soapConnectionFactory = SOAPConnectionFactory.newInstance();
			SOAPConnection soapConnection = soapConnectionFactory.createConnection();

			// Send SOAP Message to SOAP Server
			SOAPMessage soapResponse = soapConnection.call(createInfosecChngeSoapEnvelope(), soapEndpointUrl);

			// Print the SOAP Response
			System.out.println("Response SOAP Message:");
			soapResponse.writeTo(System.out);
			System.out.println();

			soapConnection.close();

			// SOAPMessage soapMessage= createSoapEnvelope();

			final StringWriter sw = new StringWriter();

			try {
				TransformerFactory.newInstance().newTransformer().transform(new DOMSource(soapResponse.getSOAPPart()),
						new StreamResult(sw));
			} catch (TransformerException e) {
				throw new RuntimeException(e);
			}

			if (sw.toString() != null) {
				System.out.println("Final Response:" + sw.toString());
				ExecutionInstance.addStepEntry(ses, "SOAP Response generated sucessfully",
						"response is:  " + sw.toString(), "Pass");
			}else if (sw.toString() == null) {
				System.out.println("Final Response:" + sw.toString());
				ExecutionInstance.addStepEntry(ses, "SOAP Response was not generated sucessfully",
						"response is:  " + sw.toString(), "Fail");
			}

			responseValidation(sw.toString());

		} catch (Exception e) {
			System.err.println(
					"\nError occurred while sending SOAP Request to Server!\nMake sure you have the correct endpoint URL and SOAPAction!\n");
			e.printStackTrace();
		}
	}

	public void responseValidation(String responsexml) throws ParserConfigurationException, SAXException, IOException {

		DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
		DocumentBuilder docBuilder = docFactory.newDocumentBuilder();
		Document doc = docBuilder.parse(new InputSource((new StringReader(responsexml))));
		doc.getDocumentElement().normalize();
		String responseDescription=null;
		String responseCode=null;

		try {
			ExecutionInstance.addStepEntry(ses, "SOAP Response validation ",
					"SOAP Response validation started", "Pass");

			int len = doc.getElementsByTagName("tews:code").getLength();
			int slen=doc.getElementsByTagName("transactionId").getLength();
			if(len>0) {
			for (int i = 0; i < len; i++) {
				Node node = doc.getElementsByTagName("tews:code").item(i);
				 responseCode = node.getTextContent();
				System.out.println("Response code is: " + responseCode);
				ExecutionInstance.addStepEntry(ses, "SOAP Response code captured sucessfully",
						"SOAP Response code is : " + responseCode, "Pass");
				if (responseCode.contains("500")) {
					node = doc.getElementsByTagName("tews:description").item(i);
					 responseDescription = node.getTextContent();

					ExecutionInstance.addStepEntry(ses, "SOAP Response description captured sucessfully",
							"SOAP Response description is : " + responseDescription, "Pass");
					System.out.println("responseDescription :- " + responseDescription);
				}else {
					ExecutionInstance.addStepEntry(ses, "SOAP Response code is not 500",
						"SOAP Response code is not 500" + responseCode, "Fail");
					ExecutionInstance.addStepEntry(ses, "SOAP Response description",
							"SOAP Response description is: " + responseCode, "Fail");
				}
			  }
			}else if(slen>0) {
				ExecutionInstance.addStepEntry(ses, "SOAP Response transaction id captured ",
						"SOAP Response transaction ID captured sucessfully", "Pass");
				
				for(int i=0;i<slen;i++) {
				Node node = doc.getElementsByTagName("transactionId").item(i);
				responseCode = node.getTextContent();
				try {
					ExecutionInstance.addStepEntry(ses, "SOAP Response transaction id captured ",
							"SOAP Response Transaction ID is: " + responseCode, "Pass");
				} catch (Exception e1) {
					
					e1.printStackTrace();
				}
				System.out.println("Treansaction id is : " + responseCode);
				}
				
			}else {
				ExecutionInstance.addStepEntry(ses, "SOAP Response validation ",
						"SOAP Response validation failed", "Fail");
			}

		} catch (Exception e) {
			e.printStackTrace();

		}
	}

	private SOAPMessage createInfosecChngeSoapEnvelope() throws Exception {
		MessageFactory messageFactory = MessageFactory.newInstance();
		SOAPMessage soapMessage = messageFactory.createMessage();

		try {
			String admin_id = ses.getTestData("admin_id");
			String uid = ses.getTestData("UID");
			String userID = ses.getTestData("PCT_USER_ID_PCT");
			String cnfrmPwd = ses.getTestData("BAR_fedexConfirmPassword_BAR");
			
			
			SOAPElement soapBodyElem, soapBodyElem1, soapBodyElem2,soapBodyElem3;

			// SOAPMessage soapMessage=factory.createMessage();
			SOAPPart soapPart = soapMessage.getSOAPPart();

			String myNamespace = "";
			String myNamespaceURI = "http://tews6/wsdl";

			// SOAP Envelope
			SOAPEnvelope envelope = soapPart.getEnvelope();
			envelope.addNamespaceDeclaration(myNamespace, myNamespaceURI);
			// SOAP Header
			SOAPHeader header = envelope.getHeader();
			soapBodyElem = header.addChildElement("wsdl:FedexInfosecChangeTaskContext");
			soapBodyElem1 = soapBodyElem.addChildElement("wsdl:admin_id");
			soapBodyElem1.addTextNode(admin_id);

			// SOAP Body
			SOAPBody soapBody = envelope.getBody();
			
			
			     
			
			
			soapBodyElem = soapBody.addChildElement("wsdl:FedexInfosecChange");
			soapBodyElem1 = soapBodyElem.addChildElement("wsdl:FedexInfosecChangeSearch");
			soapBodyElem2 = soapBodyElem1.addChildElement("wsdl:Subject");
			soapBodyElem3 = soapBodyElem2.addChildElement("wsdl:UID");
			soapBodyElem3.addTextNode(uid);
			
			
			// Construct Elements under "wsdl:FedexChangeMyPassword"
			soapBodyElem1 = soapBodyElem.addChildElement("wsdl:FedexInfosecChangeProfileTab");
			soapBodyElem2 = soapBodyElem1.addChildElement("wsdl:_PCT_USER_ID_PCT_");
			soapBodyElem2.addTextNode(userID);
			
			soapBodyElem2 = soapBodyElem1.addChildElement("wsdl:_BAR_fedexConfirmPassword_BAR_");
			soapBodyElem2.addTextNode(cnfrmPwd);
			
			

			soapMessage.writeTo(System.out);

			//Saving soap messages changes if any
			soapMessage.saveChanges();

			//Converting the response to String
			final StringWriter sw = new StringWriter();

			try {
				TransformerFactory.newInstance().newTransformer().transform(new DOMSource(soapMessage.getSOAPPart()),
						new StreamResult(sw));
			} catch (TransformerException e) {
				throw new RuntimeException(e);
			}
			if (sw.toString() != null) {

				// Now you have the XML as a String:
				System.out.println("FinalString:" + sw.toString());
				ExecutionInstance.addStepEntry(ses, "SOAP Request was created sucessfully ",
						"SOAP REquest is:  " + sw.toString(), "Pass");
			} else if (sw.toString() == null){
				ExecutionInstance.addStepEntry(ses, "SOAP Request was not created as expected ",
						"SOAP REquest is:  " + sw.toString(), "Fail");
			}

		} catch (Exception e) {

			e.printStackTrace();

		}
		return soapMessage;
	}

}
