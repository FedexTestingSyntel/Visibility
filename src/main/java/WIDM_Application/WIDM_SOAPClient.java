package WIDM_Application;

/** 
 * SOAPClient4XG. Read the SOAP envelope file passed as the second
 * parameter, pass it to the SOAP endpoint passed as the first parameter, and
 * print out the SOAP envelope passed as a response.  with help from Michael
 * Brennan 03/09/01
 * 
 *
 * @author  Bob DuCharme
 * @version 1.1
 * @param   SOAPUrl      URL of SOAP Endpoint to send request.
 * @param   xmlFile2Send A file with an XML document of the request.  
 *
*/
 
import java.io.*;
import java.net.*;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.io.File;
import java.lang.reflect.Method;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.testng.annotations.BeforeClass;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;
import org.w3c.dom.Attr;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import SupportClasses.Environment;

public class WIDM_SOAPClient {
	static String LevelsToTest = "3";

	@BeforeClass
	public void beforeClass() {
		Environment.SetLevelsToTest(LevelsToTest);
	}
	
	@DataProvider (parallel = true)
	public static Iterator<Object[]> dp(Method m) {
		List<Object[]> data = new ArrayList<Object[]>();

		for (int i=0; i < Environment.LevelsToTest.length(); i++) {
			String Level = String.valueOf(Environment.LevelsToTest.charAt(i));
			//int intLevel = Integer.parseInt(Level);
			
			switch (m.getName()) { //Based on the method that is being called the array list will be populated.
				case "WFCL_Reset_Password_Email_Validation":
					data.add( new Object[] {Level});
					break;
			}
		}	
		return data.iterator();
	}
	
	
	@Test(dataProvider = "dp")
    public static void WIDM_SOAP_Attempt(String Level) throws Exception {
        String SOAPUrl      = "http://widmdev.idev.fedex.com:10099/iam/im/TEWS6/widm";
        String xmlFile2Send = CreateXML();
      
        // Create the connection where we're going to send the file.
        URL url = new URL(SOAPUrl);
        URLConnection connection = url.openConnection();
        HttpURLConnection httpConn = (HttpURLConnection) connection;
 
        // Open the input file. After we copy it to a byte array, we can see
        // how big it is so that we can set the HTTP Cotent-Length
        // property. (See complete e-mail below for more on this.)
 
        FileInputStream fin = new FileInputStream(xmlFile2Send);
 
        ByteArrayOutputStream bout = new ByteArrayOutputStream();
     
        // Copy the SOAP file to the open connection.
        copy(fin,bout);
        fin.close();
 
        byte[] b = bout.toByteArray();
 
        // Set the appropriate HTTP parameters.
        httpConn.setRequestProperty( "Content-Length",
                                     String.valueOf( b.length ) );
        httpConn.setRequestProperty("Content-Type","text/xml; charset=utf-8");
        httpConn.setRequestMethod( "POST" );
        httpConn.setDoOutput(true);
        httpConn.setDoInput(true);
 
        // Everything's set up; send the XML that was read in to b.
        OutputStream out = httpConn.getOutputStream();
        out.write( b );    
        out.close();
 
        // Read the response and write it to standard out.
 
        InputStreamReader isr =
            new InputStreamReader(httpConn.getInputStream());
        BufferedReader in = new BufferedReader(isr);
 
        String inputLine;
 
        while ((inputLine = in.readLine()) != null)
            System.out.println(inputLine);
 
        in.close();
    }
 
  // copy method from From E.R. Harold's book "Java I/O"
  public static void copy(InputStream in, OutputStream out) 
   throws IOException {
 
    // do not allow other threads to read from the
    // input or write to the output while copying is
    // taking place
 
    synchronized (in) {
      synchronized (out) {
 
        byte[] buffer = new byte[256];
        while (true) {
          int bytesRead = in.read(buffer);
          if (bytesRead == -1) break;
          out.write(buffer, 0, bytesRead);
        }
      }
    }
  } 
  
  public static String CreateXML() {

	  try {

		DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
		DocumentBuilder docBuilder = docFactory.newDocumentBuilder();

		// root elements
		Document doc = docBuilder.newDocument();
		Element rootElement = doc.createElement("company");
		doc.appendChild(rootElement);

		// staff elements
		Element staff = doc.createElement("Staff");
		rootElement.appendChild(staff);

		// set attribute to staff element
		Attr attr = doc.createAttribute("id");
		attr.setValue("1");
		staff.setAttributeNode(attr);

		// shorten way
		// staff.setAttribute("id", "1");

		// firstname elements
		Element firstname = doc.createElement("firstname");
		firstname.appendChild(doc.createTextNode("yong"));
		staff.appendChild(firstname);

		// lastname elements
		Element lastname = doc.createElement("lastname");
		lastname.appendChild(doc.createTextNode("mook kim"));
		staff.appendChild(lastname);

		// nickname elements
		Element nickname = doc.createElement("nickname");
		nickname.appendChild(doc.createTextNode("mkyong"));
		staff.appendChild(nickname);

		// salary elements
		Element salary = doc.createElement("salary");
		salary.appendChild(doc.createTextNode("100000"));
		staff.appendChild(salary);

		// write the content into xml file
		TransformerFactory transformerFactory = TransformerFactory.newInstance();
		Transformer transformer = transformerFactory.newTransformer();
		DOMSource source = new DOMSource(doc);
		StreamResult result = new StreamResult(new File("C:\\file.xml"));

		// Output to console for testing
		// StreamResult result = new StreamResult(System.out);

		transformer.transform(source, result);

		System.out.println("File saved!");
		return source.toString();
	  } catch (Exception e) {
		e.printStackTrace();
	  } 
	  return null;
	}
}
