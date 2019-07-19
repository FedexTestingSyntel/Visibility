package SupportClasses;

import java.awt.Desktop;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringReader;
import java.io.StringWriter;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import org.testng.IReporter;
import org.testng.IResultMap;
import org.testng.ISuite;
import org.testng.ISuiteResult;
import org.testng.ITestContext;
import org.testng.ITestResult;
import org.testng.xml.XmlSuite;
import com.itextpdf.text.Document;
import com.itextpdf.text.PageSize;
import com.itextpdf.text.html.simpleparser.HTMLWorker;
import com.itextpdf.text.pdf.PdfWriter;

public class TestNG_ReportListener implements IReporter {
	
	int totalTestCount = 0, runningTotalTestCount = 0;
	int totalTestPassed = 0, runningTotalTestPassed = 0;
	int totalTestFailed = 0, runningTotalTestFailed = 0;
	int totalTestSkipped = 0, runningTotalTestSkipped = 0;
	
	//the name of the set environment test, this will remove it from being printed in the report
	//need to come back later and make this better

	@Override
	public void generateReport(List<XmlSuite> xmlSuites, List<ISuite> suites, String outputDirectory) {
		
		boolean ReportGenerated = false;
		
		try{
			//Name of the test suit from the XML
			String Application = xmlSuites.get(0).getName().substring(0, 4);
			
			// Get content data in TestNG report template file.
			// Condensed so the file in embedded
			String customReportTemplateStr = "<!DOCTYPE html><html><head><style>table {    font-family: arial, sans-serif;    border-collapse: collapse;    width: 100%;    font-size: 11px;}td, th {    border: 1px solid #dddddd;    text-align: left;    padding: 8px;}</style></head><body>	<h2>$TestNG_Custom_Report_Title$</h2>	    <table>      <thead>        <tr>          <th>Test Name</th>          <th># Total Methods</th>          <th># Passed</th>          <th># Skipped</th>          <th># Failed</th>          <th>Browser</th>          <th>Start Time</th>          <th>End Time</th>          <th>Execute Time (hh:mm:ss:ms)</th>          <th>Included Groups</th>          <th>Excluded Groups</th>        </tr>       </thead>       $Test_Case_Summary$    </table>		</br></br></br>		<table id=\"overview\">      <thead>        <tr>          <th>Scenarios</th>          <th>Status</th>          <th>Comments</th>        </tr>      </thead>       $Test_Case_Overview$    </table>		</br></br></br>	    <table id=\"summary\">      <thead>        <tr>          <th>Application</th>          <th>Method</th>          <th>Start Time</th>          <th>Execution Time (hh:mm:ss:ms)</th>          <th>Parameter</th>          <th>Reporter Message</th>        </tr>      </thead>       $Test_Case_Detail$    </table>  </body>  </html>";
			
			// Create custom report title.
			String customReportTitle = this.getCustomReportTitle(Application + " TestNG Report");
			
			//Create test overview data.
			String customSuiteOverview = this.getTestMehodOverviewCreation(suites);
			
			// Create test suite summary data.
			//This will list the total test cases and eht number of passed and failed scenarios
			String customSuiteSummary = this.getTestSuiteSummary(suites);
			
			// Create test methods summary data.
			String customTestMethodSummary = this.getTestMehodSummary(suites);
			
			// Replace report title place holder with custom title.
			customReportTemplateStr = customReportTemplateStr.replaceAll("\\$TestNG_Custom_Report_Title\\$", Matcher.quoteReplacement(customReportTitle));
			
			// Replace test suite place holder with custom test suite summary.
			customReportTemplateStr = customReportTemplateStr.replaceAll("\\$Test_Case_Summary\\$", Matcher.quoteReplacement(customSuiteSummary));
			
			//Replace test 
			customReportTemplateStr = customReportTemplateStr.replaceAll("\\$Test_Case_Overview\\$", Matcher.quoteReplacement(customSuiteOverview));
			
 			// Replace test methods place holder with custom test method summary.
			customReportTemplateStr = customReportTemplateStr.replaceAll("\\$Test_Case_Detail\\$", Matcher.quoteReplacement(customTestMethodSummary));
			
			// Write replaced test report content to custom-emailable-report.html.
			String ReportName = Helper_Functions.CurrentDateTime() + " L" + Environment.LevelsToTest + " " + Application + " Report";
			outputDirectory = Helper_Functions.FileSaveDirectory + "\\" + Application + "\\" + ReportName;
			//Add the test case count to the file name.
			outputDirectory += String.format(" T%sP%sF%s.html", runningTotalTestCount, runningTotalTestPassed, runningTotalTestFailed);
			File targetFile = new File(outputDirectory);
			
			//Create folder directory for writing the report.
			String Folder = outputDirectory;
			FileWriter fw = null;
			try {
				Folder = outputDirectory.substring(0, outputDirectory.lastIndexOf("\\"));
				if (!(new File(Folder)).exists()) {
					new File(Folder).mkdir();
				}
				fw = new FileWriter(targetFile);
				fw.write(customReportTemplateStr);
				System.out.println("Report Saved: " + outputDirectory);
				ReportGenerated = true;
			} catch (Exception e) {  
				System.out.println("Warning, Unable to create directory for: " + Folder);
			}finally {
				fw.flush();
				fw.close();
			}
			
			if (ReportGenerated) {
				//open the newly created report.
				File htmlFile = new File(outputDirectory);
				Desktop.getDesktop().browse(htmlFile.toURI());
			}
			
		}catch(Exception ex){
			ex.printStackTrace();
		}
	}
	
	/* Build custom report title. */
	private String getCustomReportTitle(String title){
		StringBuffer retBuf = new StringBuffer();
		retBuf.append(title + " " + this.getDateInStringFormat(new Date()));
		return retBuf.toString();
	}
	
	/* Build test suite summary data. */
	private String getTestSuiteSummary(List<ISuite> suites){
		StringBuffer retBuf = new StringBuffer();
		
		try{
			for(ISuite tempSuite: suites){
				retBuf.append("<tr><td colspan=11><center><b>" + tempSuite.getName() + "</b></center></td></tr>");
				
				Map<String, ISuiteResult> testResults = tempSuite.getResults();
				
				for (ISuiteResult result : testResults.values()) {
					
					retBuf.append("<tr>");
					ITestContext testObj = result.getTestContext();
					totalTestPassed = testObj.getPassedTests().getAllMethods().size();
					totalTestSkipped = testObj.getSkippedTests().getAllMethods().size();
					totalTestFailed = testObj.getFailedTests().getAllMethods().size();
					totalTestCount = totalTestPassed + totalTestSkipped + totalTestFailed;// + totalTestCount;
					
					runningTotalTestCount += totalTestCount;
					runningTotalTestPassed += totalTestPassed;
					runningTotalTestFailed += totalTestFailed;
					runningTotalTestSkipped += totalTestSkipped;
					
					/* Test name. */
					retBuf.append("<td>" + testObj.getName() + "</td>");
					/* Total method count. */
					retBuf.append("<td>" + totalTestCount + "</td>");
					/* Passed method count. */
					retBuf.append("<td bgcolor=green>" + totalTestPassed+ "</td>");
					/* Skipped method count. */
					retBuf.append("<td bgcolor=yellow>" + totalTestSkipped + "</td>");
					/* Failed method count. */
					retBuf.append("<td bgcolor=red>" + totalTestFailed + "</td>");
					/* Get browser type. */
					String browserType = tempSuite.getParameter("browserType");
					if(browserType==null || browserType.trim().length()==0){
						browserType = "Chrome";
					}
					/* Append browser type. */
					retBuf.append("<td>" + browserType + "</td>");
					/* Start Date*/
					Date startDate = testObj.getStartDate();
					retBuf.append("<td>" + this.getDateInStringFormat(startDate) + "</td>");
					/* End Date*/
					Date endDate = testObj.getEndDate();
					retBuf.append("<td>" + this.getDateInStringFormat(endDate) + "</td>");
					/* Execute Time */
					long deltaTime = endDate.getTime() - startDate.getTime();
					String deltaTimeStr = this.convertDeltaTimeToString(deltaTime);
					retBuf.append("<td>" + deltaTimeStr + "</td>");
					/* Include groups. */
					retBuf.append("<td>" + this.stringArrayToString(testObj.getIncludedGroups()) + "</td>");
					/* Exclude groups. */
					retBuf.append("<td>" + this.stringArrayToString(testObj.getExcludedGroups()) + "</td>");
					retBuf.append("</tr>");
				}
			}
		}catch(Exception ex){
			ex.printStackTrace();
		}
		return retBuf.toString();
	}

	/* Get date string format value. */
	private String getDateInStringFormat(Date date){
		StringBuffer retBuf = new StringBuffer();
		if(date == null){
			date = new Date();
		}
		DateFormat df = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss a");
		retBuf.append(df.format(date));
		return retBuf.toString();
	}
	
	/* Convert long type deltaTime to format hh:mm:ss:mi. */
	private String convertDeltaTimeToString(long deltaTime){
		StringBuffer retBuf = new StringBuffer();
		
		long milli = deltaTime/ 1000;
		long seconds = (deltaTime / 1000) % 60 ;
		long minutes = (deltaTime / (1000 * 60)) % 60;
		long hours   = (deltaTime / (1000 * 60* 60)) % 24;
		
		retBuf.append(String.format("%02d : %02d : %02d : %03d", hours, minutes, seconds, milli));
		
		return retBuf.toString();
	}
	
	/* Get test method summary info. */
	private String getTestMehodSummary(List<ISuite> suites){
		StringBuffer retBuf = new StringBuffer();
		
		try{
			for(ISuite tempSuite: suites){
				retBuf.append("<tr><td colspan=7><center><b>" + tempSuite.getName() + "</b></center></td></tr>");
				
				Map<String, ISuiteResult> testResults = tempSuite.getResults();
				
				for (ISuiteResult result : testResults.values()) {
					
					ITestContext testObj = result.getTestContext();

					String testName = testObj.getName();
					
					/* Get failed test method related data. */
					IResultMap testFailedResult = testObj.getFailedTests();
					String failedTestMethodInfo = this.getTestMethodReport(testName, testFailedResult, false, false);
					retBuf.append(failedTestMethodInfo);
					
					/* Get skipped test method related data. */
					IResultMap testSkippedResult = testObj.getSkippedTests();
					String skippedTestMethodInfo = this.getTestMethodReport(testName, testSkippedResult, false, true);
					retBuf.append(skippedTestMethodInfo);
					
					/* Get passed test method related data. */
					IResultMap testPassedResult = testObj.getPassedTests();
					String passedTestMethodInfo = this.getTestMethodReport(testName, testPassedResult, true, false);
					retBuf.append(passedTestMethodInfo);
				}
			}
		}catch(Exception ex){
			ex.printStackTrace();
		}
		return retBuf.toString();
	}
	
	/* Get failed, passed or skipped test methods report. */
	private String getTestMethodReport(String testName, IResultMap testResultMap, boolean passedReault, boolean skippedResult){
		ArrayList<String[]> ResultList = new ArrayList<String[]>();
		StringBuffer retStrBuf = new StringBuffer();
		
		String resultTitle = testName;
		
		String color = "green";
		
		if(skippedResult){
			resultTitle += " - Skipped ";
			color = "yellow";
		}else if(passedReault){
			resultTitle += " - Passed ";
			color = "green";
		}else{
			resultTitle += " - Failed ";
			color = "red";
		}
		
		retStrBuf.append("<tr bgcolor=" + color + "><td colspan=7><center><b>" + resultTitle + "</b></center></td></tr>");
			
		Set<ITestResult> testResultSet = testResultMap.getAllResults();
			
		for(ITestResult testResult : testResultSet){
			StringBuffer sortingStrBuf = new StringBuffer();
			String Application = "", testMethodName = "", startDateStr = "", executeTimeStr = "", paramStr = "", reporterMessage = "";
			
			//Get Application name, should be the same as the tesitng class name
			Application = testResult.getTestClass().getName();
			Application = Application.substring(Application.lastIndexOf(".") + 1, Application.length());
			
			//Get testMethodName
			testMethodName = testResult.getMethod().getMethodName();
				
			//Get startDateStr
			long startTimeMillis = testResult.getStartMillis();
			startDateStr = this.getDateInStringFormat(new Date(startTimeMillis));

			//Get Execute time.
			long deltaMillis = testResult.getEndMillis() - testResult.getStartMillis();
			executeTimeStr = this.convertDeltaTimeToString(deltaMillis);

			//Get parameter list.
			Object paramObjArr[] = testResult.getParameters();
			for(Object paramObj : paramObjArr){
				try {
					paramStr += paramObj.toString() + "<br />";
				}catch (Exception e) {
					paramStr += paramObj + "<br />";
				}
			}
				
			//This is a custom variable that is set in the TestListener for trace of execution.
			try {
				Object val = testResult.getAttribute("ExecutionLog");
				String ExecutionLog = val.toString().replaceAll("\n", "<br />");
				reporterMessage = ExecutionLog;
			}catch (Exception e) {}

			
			//Get exception message. If there was an exception will be added to the end of the reporterMessage
			Throwable exception = testResult.getThrowable();
			if(exception!=null){
				StringWriter sw = new StringWriter();
				PrintWriter pw = new PrintWriter(sw);
				exception.printStackTrace(pw);
				
				reporterMessage += "\n\n" + sw.toString();
			}
			reporterMessage = reporterMessage.replaceAll("<br />", "\n\n");
			
			sortingStrBuf.append("<tr bgcolor=" + color + ">");
			
			/* Add test class name. */
			sortingStrBuf.append("<td>" + Application + "</td>");
			/* Add test method name. */
			sortingStrBuf.append("<td>" + testMethodName + "</td>");
			/* Add start time. */
			sortingStrBuf.append("<td>" + startDateStr + "</td>");
			/* Add execution time. */
			sortingStrBuf.append("<td>" + executeTimeStr + "</td>");
			/* Add parameter. */
			sortingStrBuf.append("<td>" + paramStr + "</td>");
			/* Add reporter message. Added table to make reading the values easier as well as display is XML is embedded*/
			sortingStrBuf.append("<td><textarea rows=\"10\" cols=\"60\" style=\"border:none;\">" + reporterMessage + "</textarea></td>");
			
			sortingStrBuf.append("</tr>");
			
			ResultList.add(new String[] {Application, sortingStrBuf.toString()});

		}
		
		Collections.sort(ResultList,new Comparator<String[]>() {
			public int compare(String[] strings, String[] otherStrings) {
				return strings[0].compareTo(otherStrings[0]);
			}
		});
		
		for (String[] sa : ResultList) {
			retStrBuf.append(sa[1]);
		}
		
		return retStrBuf.toString();
	}
	
	/* Get test method summary info. */
	private String getTestMehodOverviewCreation(List<ISuite> suites){
		StringBuffer retBuf = new StringBuffer();
		
		try{
			for(ISuite tempSuite: suites){
				Map<String, ISuiteResult> testResults = tempSuite.getResults();
				
				for (ISuiteResult result : testResults.values()) {
					
					ITestContext testObj = result.getTestContext();
					
					/* Get failed test method related data. */
					IResultMap testFailedResult = testObj.getFailedTests();
					String failedTestMethodInfo = this.getTestMethodOverview(testFailedResult, false, false);
					retBuf.append(failedTestMethodInfo);
					
					/* Get skipped test method related data. */
					IResultMap testSkippedResult = testObj.getSkippedTests();
					String skippedTestMethodInfo = this.getTestMethodOverview(testSkippedResult, false, true);
					retBuf.append(skippedTestMethodInfo);
					
					/* Get passed test method related data. */
					IResultMap testPassedResult = testObj.getPassedTests();
					String passedTestMethodInfo = this.getTestMethodOverview(testPassedResult, true, false);
					retBuf.append(passedTestMethodInfo);
				}
			}
		}catch(Exception ex){
			ex.printStackTrace();
		}
		return retBuf.toString();
	}
	
	/* Get failed, passed or skipped test methods overview. */
	private String getTestMethodOverview(IResultMap testResultMap, boolean passedReault, boolean skippedResult){
		ArrayList<String[]> ResultList = new ArrayList<String[]>();
		StringBuffer retStrBuf = new StringBuffer();
	
		String status = "Passed";
		
		if(skippedResult){
			status = "Na";
		}else if(passedReault){
			status = "Passed";
		}else{
			status = "Failed";
		}
			
		Set<ITestResult> testResultSet = testResultMap.getAllResults();
			
		for(ITestResult testResult : testResultSet){
			StringBuffer sortingStrBuf = new StringBuffer();
			String Application = "", testMethodName = "", ResponseMessage = "";
			
			//Get Application name, should be the same as the tesitng class name
			Application = testResult.getTestClass().getName();
			Application = Application.substring(Application.lastIndexOf(".") + 1, Application.length());
				
			//Get testMethodName
			//testMethodName = Application + " " + testResult.getMethod().getMethodName();
			testMethodName = testResult.getMethod().getMethodName();
			
			//Get exception message. If there was an exception will be added to the end of the reporterMessage
			Throwable exception = testResult.getThrowable();
			if(exception!=null){
				ResponseMessage = exception.getLocalizedMessage();
			}else {
				try {
					String val = testResult.getAttribute("ExecutionLog").toString();
					while (ResponseMessage.contentEquals("") || ResponseMessage.contentEquals("\n") ) {
						ResponseMessage = val.substring(val.lastIndexOf("\n"), val.length());
						val = val.substring(0, val.lastIndexOf("\n"));
					}
					
				}catch (Exception e) {}
			}
			
			//if the error message is to long then reduce to first 500 characters.
			if (ResponseMessage.length() > 500) {
				ResponseMessage = ResponseMessage.substring(0, 500) + "...";
			}
			
			/* Add test method name. */
			sortingStrBuf.append("<td>" + testMethodName + "</td>");
			/* Add status of the test. */
			sortingStrBuf.append("<td>" + status + "</td>");
			/* Add comments of the test.*/ 
			sortingStrBuf.append("<td>" + ResponseMessage + "</td>");

			
			sortingStrBuf.append("</tr>");
			
			ResultList.add(new String[] {Application, sortingStrBuf.toString()});
		}
		//the sorting is done based on the Application
		Collections.sort(ResultList,new Comparator<String[]>() {
			public int compare(String[] strings, String[] otherStrings) {
				return strings[0].compareTo(otherStrings[0]);
			}
		});
		//the sorting is done based on the Scenarios
		Collections.sort(ResultList,new Comparator<String[]>() {
			public int compare(String[] strings, String[] otherStrings) {
				if (strings[0].contentEquals(otherStrings[0])) {
					return strings[1].compareTo(otherStrings[1]);
				}else {
					return strings[0].compareTo(otherStrings[0]);
				}
			}
		});
		
		for (String[] sa : ResultList) {
			retStrBuf.append(sa[1]);
		}
		
		return retStrBuf.toString();
	}
	
	
	/* Convert a string array elements to a string. */
	private String stringArrayToString(String strArr[]) {
		StringBuffer retStrBuf = new StringBuffer();
		if(strArr!=null){
			for(String str : strArr){
				retStrBuf.append(str + " ");
			}
		}
		return retStrBuf.toString();
	}
	
	public void CreatePDFReport(String outputDirectory, String ReportName) {
		try {
			String path = outputDirectory + ".pdf";
			PdfWriter pdfWriter = null;

			// create a new document
			Document document = new Document();
			pdfWriter = PdfWriter.getInstance(document, new FileOutputStream(path));
			document.open();
			
            // get Instance of the PDFWriter
            pdfWriter = PdfWriter.getInstance(document, new FileOutputStream(path));
            //pdfWriter.setPdfVersion(PdfWriter.PDF_VERSION_1_4);
            pdfWriter.setLinearPageMode();
            pdfWriter.setFullCompression();


            // document header attributes
            document.addAuthor("GTM");
            document.addCreationDate();
            document.addProducer();
            document.addCreator("SK");
            document.addTitle(ReportName);
            document.setPageSize(PageSize.A4);

            // open document
            document.open();

            HTMLWorker htmlWorker = new HTMLWorker(document);

            String str = "";
            StringBuilder contentBuilder = new StringBuilder();
            BufferedReader in = null;

            //System.out.println("Html Content :");
            try {
                in = new BufferedReader(new FileReader(outputDirectory + ".html"));

                while ((str = in.readLine()) != null) {

                    contentBuilder.append(str);
                    //System.out.println(str);
                }
            } catch (Exception e) {
                System.out.print("HTML file close problem:" + e.getMessage());
            } finally {
                in.close();
                System.gc();
            }
            String content = contentBuilder.toString();

            htmlWorker.parse(new StringReader(content));
            document.close();
            pdfWriter.close();
            System.out.println("Report Saved as PDF: " + outputDirectory + ".pdf");
        }catch (Exception e) {
            e.printStackTrace();
        }
	}
	
	/*
	private static void sendPDFReportByGMail(String from, String pass, String to, String subject, String body, String FileName) {
    	Properties props = System.getProperties();
    	String host = "smtp.gmail.com";
    	props.put("mail.smtp.starttls.enable", "true");
    	props.put("mail.smtp.host", host);
    	props.put("mail.smtp.user", from);
    	props.put("mail.smtp.User_Info.PASSWORD", pass);
    	props.put("mail.smtp.port", "587");
    	props.put("mail.smtp.auth", "true");
    	Session session = Session.getDefaultInstance(props);

    	MimeMessage message = new MimeMessage(session);
 
    	try {
    	    //Set from address
    		message.setFrom(new InternetAddress(from));
    		message.addRecipient(Message.RecipientType.TO, new InternetAddress(to));

    		//Set subject
    		message.setSubject(subject);
    		message.setText(body);
    		BodyPart objMessageBodyPart = new MimeBodyPart();
    		objMessageBodyPart.setText("Please Find The Attached Report File!");
    		Multipart multipart = new MimeMultipart();
    		multipart.addBodyPart(objMessageBodyPart);
    		objMessageBodyPart = new MimeBodyPart();

    		//Set path to the pdf report file
    		String filename = System.getProperty("user.dir")+"\\test-output\\custom-emailable-report.html";

    		//Create data source to attach the file in mail
    		DataSource source = new FileDataSource(filename);
    		objMessageBodyPart.setDataHandler(new DataHandler(source));
    		objMessageBodyPart.setFileName(filename);
    		multipart.addBodyPart(objMessageBodyPart);
    		message.setContent(multipart);
    		Transport transport = session.getTransport("smtp");
    		transport.connect(host, from, pass);
    		transport.sendMessage(message, message.getAllRecipients());
    		transport.close();
    		Helper_Functions.PrintOut("Emial has been sent to " + pass, true);
    	}catch (Exception ae) {
    		ae.printStackTrace();
    	}
    }
    */

}