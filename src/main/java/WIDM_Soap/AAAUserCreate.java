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

public class AAAUserCreate {
	private static final String ODE_ELEMENT_UNDEPLOY = "undeploy";
	private static final String ODE_ELEMENT_PACKAGENAME = "packageName";
	String SessionID = "";
	private static Sessions ses;
	int requiredStep;

	public AAAUserCreate(String sessionID) {
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

	public void callUserCreateService(int count) {
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
			SOAPMessage soapResponse = soapConnection.call(createUserServiceSoapEnvelope(), soapEndpointUrl);

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

		try {
			ExecutionInstance.addStepEntry(ses, "SOAP Response validation ",
					"SOAP Response validation started", "Pass");

			int len = doc.getElementsByTagName("tews:code").getLength();
			int slen=doc.getElementsByTagName("transactionId").getLength();
			if(len>0) {
			for (int i = 0; i < len; i++) {
				Node node = doc.getElementsByTagName("tews:code").item(i);
				String responseCode = node.getTextContent();
				System.out.println("Response code is: " + responseCode);
				ExecutionInstance.addStepEntry(ses, "SOAP Response code captured sucessfully",
						"SOAP Response code is : " + responseCode, "Pass");
				if (responseCode.contains("500")) {
					node = doc.getElementsByTagName("tews:description").item(i);
					String responseDescription = node.getTextContent();

					ExecutionInstance.addStepEntry(ses, "SOAP Response description captured sucessfully",
							"SOAP Response description is : " + responseDescription, "Pass");
					System.out.println("responseDescription :- " + responseDescription);
				}
			  }
			}else if(slen>0) {
				ExecutionInstance.addStepEntry(ses, "SOAP Response transaction id captured ",
						"SOAP Response transaction ID captured sucessfully", "Pass");
				
				for(int i=0;i<slen;i++) {
				Node node = doc.getElementsByTagName("transactionId").item(i);
				String responseCode = node.getTextContent();
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

	private SOAPMessage createUserServiceSoapEnvelope() throws Exception {
		MessageFactory messageFactory = MessageFactory.newInstance();
		SOAPMessage soapMessage = messageFactory.createMessage();

		try {
			String admin_id = ses.getTestData("admin_id");
			String first_Name = ses.getTestData("FIRST_NAME");
			String initials_Nm = ses.getTestData("INITIALS_NM");
			String last_Name = ses.getTestData("LAST_NAME");
			String full_Name = ses.getTestData("FULL_NAME");
			String cmpny_Name = ses.getTestData("DOT_COMPANY_NM");
			String email_PCT = ses.getTestData("EMAIL_PCT");
			String desc = ses.getTestData("DOT_STREET_DESC");
			String city_Nm = ses.getTestData("DOT_CITY_NM");
			String state_Cd = ses.getTestData("DOT_STATE_CD");
			String postal_Cd = ses.getTestData("DOT_POSTAL_CD");
			String country_Cd = ses.getTestData("COUNTRY_PCT");
			String phone_Nmbr = ses.getTestData("DOT_PHONE_NBR");
			String fax_Nmbr = ses.getTestData("DOT_FAX_NBR");
			String email_UserID_Flag = ses.getTestData("DOT_EMAIL_AS_USERID_FLG");
			String user_Id = ses.getTestData("USER_ID");
			String fedexCnfmPwd = ses.getTestData("fedexConfirmPassword_BAR");
			String sec_Ques = ses.getTestData("DOT_SECRET_QUESTION_DESC");
			String login_ans_desc = ses.getTestData("LOGIN_DOT_CRYPT_ANSWER_DESC1_BAR");
			String lnge_Cd = ses.getTestData("LANGUAGE_PCT");
			String email_Flag = ses.getTestData("EMAIL_ALLOWED_FLG");
			String device_ID = ses.getTestData("DEVICE_ID");
			String ip_address = ses.getTestData("IP_ADDRESS_PCT");
			String fdxcbid_BAR = ses.getTestData("FDX_CBID_PCT");
			String email_opt_flag = ses.getTestData("DOT_EMAIL_OPT_IN_FLG");
			
			

			SOAPElement soapBodyElem, soapBodyElem1, soapBodyElem2;

			// SOAPMessage soapMessage=factory.createMessage();
			SOAPPart soapPart = soapMessage.getSOAPPart();

			String myNamespace = "";
			String myNamespaceURI = "http://tews6/wsdl";

			// SOAP Envelope
			SOAPEnvelope envelope = soapPart.getEnvelope();
			envelope.addNamespaceDeclaration(myNamespace, myNamespaceURI);
			// SOAP Header
			SOAPHeader header = envelope.getHeader();
			soapBodyElem = header.addChildElement(envelope.addChildElement("AAAUserCreateTaskContext"));
			soapBodyElem1 = soapBodyElem.addChildElement(envelope.addChildElement("admin_id"));
			soapBodyElem1.addTextNode(admin_id);

			// SOAP Body
			SOAPBody soapBody = envelope.getBody();
			soapBodyElem = soapBody.addChildElement("AAAUserCreate");
			soapBodyElem1 = soapBodyElem.addChildElement("AAAUserCreateProfileTab");
			
			// Construct Elements under "AAAUserCreateProfileTab"
			soapBodyElem2 = soapBodyElem1.addChildElement("_PCT_FIRST_NAME_PCT_");
			soapBodyElem2.addTextNode(first_Name);

			soapBodyElem2 = soapBodyElem1.addChildElement("LOGIN_DOT_INITIALS_NM");
			soapBodyElem2.addTextNode(initials_Nm);

			soapBodyElem2 = soapBodyElem1.addChildElement("_PCT_LAST_NAME_PCT_");
			soapBodyElem2.addTextNode(last_Name);
			
			soapBodyElem2 = soapBodyElem1.addChildElement("_PCT_FULL_NAME_PCT_");
			soapBodyElem2.addTextNode(full_Name);
			
			soapBodyElem2 = soapBodyElem1.addChildElement("LOGIN_DOT_COMPANY_NM");
			soapBodyElem2.addTextNode(cmpny_Name);
			
			soapBodyElem2 = soapBodyElem1.addChildElement("_PCT_EMAIL_PCT_");
			soapBodyElem2.addTextNode(email_PCT);

			soapBodyElem2 = soapBodyElem1.addChildElement("LOGIN_DOT_STREET_DESC");
			soapBodyElem2.addTextNode(desc);
			
			soapBodyElem2 = soapBodyElem1.addChildElement("LOGIN_DOT_CITY_NM");
			soapBodyElem2.addTextNode(city_Nm);
			
			soapBodyElem2 = soapBodyElem1.addChildElement("LOGIN_DOT_STATE_CD");
			soapBodyElem2.addTextNode(state_Cd);
			
			soapBodyElem2 = soapBodyElem1.addChildElement("LOGIN_DOT_POSTAL_CD");
			soapBodyElem2.addTextNode(postal_Cd);
			
			soapBodyElem2 = soapBodyElem1.addChildElement("_PCT_COUNTRY_PCT_");
			soapBodyElem2.addTextNode(country_Cd);
			
			soapBodyElem2 = soapBodyElem1.addChildElement("LOGIN_DOT_PHONE_NBR");
			soapBodyElem2.addTextNode(phone_Nmbr);
			
			soapBodyElem2 = soapBodyElem1.addChildElement("LOGIN_DOT_FAX_NBR");
			soapBodyElem2.addTextNode(fax_Nmbr);

			
			System.out.println("email_AsUseID:- "+email_UserID_Flag);
			if(!email_UserID_Flag.isEmpty())
			{
				soapBodyElem2 = soapBodyElem1.addChildElement("LOGIN_DOT_EMAIL_AS_USERID_FLG");
				soapBodyElem2.addTextNode(email_UserID_Flag);	
			}
			
			
			
			soapBodyElem2 = soapBodyElem1.addChildElement("_PCT_USER_ID_PCT_");
			soapBodyElem2.addTextNode(user_Id);
			
			soapBodyElem2 = soapBodyElem1.addChildElement("_BAR_fedexConfirmPassword_BAR_");
			soapBodyElem2.addTextNode(fedexCnfmPwd);

			soapBodyElem2 = soapBodyElem1.addChildElement("LOGIN_DOT_SECRET_QUESTION_DESC");
			soapBodyElem2.addTextNode(sec_Ques);
			
			soapBodyElem2 = soapBodyElem1.addChildElement("_BAR_LOGIN_DOT_CRYPT_ANSWER_DESC1_BAR_");
			soapBodyElem2.addTextNode(login_ans_desc);
			
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
