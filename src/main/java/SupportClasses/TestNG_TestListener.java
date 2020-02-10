package SupportClasses;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;    //The below needed for tracking the status of the tests.
import java.util.Date;
import java.util.Iterator;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import org.testng.ITestContext;
import org.testng.ITestListener;
import org.testng.ITestNGMethod;
import org.testng.ITestResult;
import SupportClasses.ThreadLogger;

public class TestNG_TestListener implements ITestListener{
	
	private static ArrayList<String> ResultsLog = new ArrayList<String>();//the results of all test cases
	private static ArrayList<String[]> ResultsOverview = new ArrayList<String[]>();
	private final static Lock lock = new ReentrantLock();//to make sure the httpclient works with the parallel execution
	Date startTime;
	Date endTime;
	
	
	@Override
    public void onStart(ITestContext arg0) {
		startTime = new Date();
		ArrayList<String[]> PersonalData = new ArrayList<String[]>();
		PersonalData = Helper_Functions.getExcelData(Helper_Functions.DataDirectory + "\\Load_Your_UserIds.xls",  "Data");//create your own file with the specific data
		for(String s[]: PersonalData) {
			if(s[0].contentEquals("MYEMAIL")){
				Helper_Functions.MyEmail = s[1];
			}else if(s[0].contentEquals("DEFAULTPASSWORD")){
				Helper_Functions.myPassword = s[2];
			}
		}
		
		try {
			Runtime.getRuntime().exec("taskkill /F /IM ChromeDriver.exe");//close out the old processes if still present.
		} catch (Exception e) {}
    }
	
	// if the first parameter of the test is an integer set the environment level.
	@Override
	public void onTestStart(ITestResult arg0) {
		//lock has been added to make the screenshot path unique
		lock.lock();
        Object[] inputArgs = arg0.getParameters();
        if (inputArgs != null && inputArgs.length > 0) {
        	//check if first parameter is an integer, as part of these test the assumption is that if the level is passed will be first variable.
        	//will set the level string as a thread local variable.
        	if (inputArgs[0] != null && Helper_Functions.isInteger(inputArgs[0].toString(), 10)) {
        		Environment.getInstance().setLevel(inputArgs[0].toString());
            }
        }
        
		//set the base screenshot name that will be used through the test.
		DriverFactory.setScreenshotPath(Helper_Functions.ScreenshotBase() + arg0.getName() + " ");
		// Helper_Functions.PrintOut("ScreenshotPath: " + DriverFactory.getScreenshotPath(), false);
        // Helper_Functions.PrintOut(Environment.getInstance().getLevel(), true);
		try {Thread.sleep(1);} catch (InterruptedException e) {}
		lock.unlock();
    }

    @Override
    public void onTestSuccess(ITestResult arg0) {
    	TestResults(arg0);
    }

    @Override
    public void onTestFailure(ITestResult arg0) {
    	//Try and print a screenshot of the failure
    	try {
    		long ThreadID = Thread.currentThread().getId();
    		//ThreadID is added to ensure that the screenshot name will be unique.
			WebDriver_Functions.takeSnapShot("Failure T" + ThreadID + " " + Helper_Functions.CurrentDateTime() + ".png");
		} catch (Exception e) {
			e.printStackTrace();
		}
    	String Message = arg0.getThrowable().getMessage();
    	if (Message != null && !Message.contentEquals("null")) {
    		Helper_Functions.PrintOut("onTestFailure: " + arg0.getThrowable().getMessage(), false);
    	}
    	TestResults(arg0);
    }

    @Override
    public void onTestSkipped(ITestResult arg0) {
    	DriverFactory.getInstance().releaseDriver();
    }

    @Override
    public void onFinish(ITestContext arg0) {
    	endTime = new Date();
        //remove all skipped tests from the results.
    	Iterator<ITestResult> skippedTestCases = arg0.getSkippedTests().getAllResults().iterator();
        while (skippedTestCases.hasNext()) {
            ITestResult skippedTestCase = skippedTestCases.next();
            ITestNGMethod method = skippedTestCase.getMethod();
            if (arg0.getSkippedTests().getResults(method).size() > 0) {
               // System.out.println("Removing:" + skippedTestCase.getTestClass().toString());
                skippedTestCases.remove();
            }
        }
         
    	Helper_Functions.PrintOut("\n\n", false);
	
		for (int i = 0 ; i < ResultsLog.size(); i++) {
			//+1 so that console count starts at 1 instead of 0
			Helper_Functions.PrintOut(i + 1 + ") " + ResultsLog.get(i), false);
		}
		
		DriverFactory.closeDrivers();
		
		try {
			Runtime.getRuntime().exec("taskkill /F /IM ChromeDriver.exe");//close out the old processes if still present.
			Helper_Functions.PrintOut("ChromeDriver.exe Cleanup Executed", true);
			Helper_Functions.MoveOldLogs();
		} catch (Exception e) {}
		
		// Print out when the execution started and when ended.
		DateFormat dateFormat = new SimpleDateFormat("HH:mm:ss");
		Helper_Functions.PrintOut("Start Time: " + dateFormat.format(startTime) + " - End Time: " + dateFormat.format(endTime), true);
    }

    @Override
    public void onTestFailedButWithinSuccessPercentage(ITestResult arg0) {
    }
    
    private void TestResults(ITestResult arg0) {
    	boolean SaveStatusFlag = false;
    	if (SaveStatusFlag) {
        	int SpaceSaver = 150;//added due to this taking up an infinite amount memory.
        	try {
        		String AttemptLogs = ThreadLogger.getInstance().ReturnLogString();
            	//save the results of given test locally
        		if (ResultsLog.size() < SpaceSaver) {
        			ResultsLog.add(AttemptLogs);
        		}
            	
            	
            	//save the summary of the issue.
            	String status = Helper_Functions.Failed;
            	if (arg0.getStatus() == ITestResult.SUCCESS) {
            		status = Helper_Functions.Passed;
            	}
            	
            	if (ResultsOverview.size() < SpaceSaver) {
            		ResultsOverview.add(new String[]{arg0.getMethod().getMethodName(), status, ""});
            		arg0.setAttribute("ExecutionLog", AttemptLogs);// this will save the trace to the test
            	}
            	
            	ArrayList<String> CurrentLogs = ThreadLogger.getInstance().ReturnLogs();
            	//reset the logs of the given thread back to blank
            	String TestCompleteData = "";		
            	for (int i = 0; i < CurrentLogs.size(); i++){
            		if (TestCompleteData.contentEquals("")) {
            			TestCompleteData = CurrentLogs.get(i);
            		}else {
            			TestCompleteData += System.lineSeparator() + CurrentLogs.get(i);
            		}
        		}
            	ThreadLogger.ThreadLog.add(TestCompleteData + System.lineSeparator());

            	//clear out the old attempts
            	ThreadLogger.getInstance().ResetLogs();
        	}catch (Exception e){
        		Helper_Functions.PrintOut("Warning, unable to save test results. " + e.getLocalizedMessage(), true);
        	}
    	}else {
    		ThreadLogger.getInstance().ResetLogs();
    	}

    	//need to add a better way to check if should close browser
    	if (DriverFactory.BrowserCurrent > 0) {
    		DriverFactory.getInstance().releaseDriver();
    	}
    }
}