package SupportClasses;

import java.io.File;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.openqa.selenium.Alert;
import org.openqa.selenium.By;
import org.openqa.selenium.Cookie;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.ui.Select;
import Data_Structures.Enrollment_Data;
import Data_Structures.User_Data;
import USRC_Application.USRC_Endpoints;

public class WebDriver_Functions{

	public static String ChangeURL(String Designation, String CountryCode, String LanguageCode, boolean ClearCookies) throws Exception {
		// The level designation. ex: L2 (dev.idev) v L3 (drt.idev)
		String LevelURL = null;
		String AppUrl = null;
		// Lower case of the country code
		String CCL = null;
		// Upper case of the country code
		String CCU = null;
		// Lower case of the language code
		String LCL = null;
		
		// Default to US if the country code is not sent.
		if (CountryCode == null) {
			CountryCode = "US";
		}
		CCL = CountryCode.toLowerCase();
		CCU = CountryCode.toUpperCase();
		LevelURL = LevelUrlReturn();
		
		// Default to English is the language is not sent.
		if (LanguageCode == null) {
			LanguageCode = "en";
		}
		LCL = LanguageCode.toLowerCase();
		
		//String caller = Thread.currentThread().getStackTrace()[2].getMethodName();
		String AppDesignation = Designation.toUpperCase();
		
		switch (AppDesignation) { 
			case "ADMINREG":	
				AppUrl = LevelURL + "/fcl/web/jsp/accountInfo1.jsp?appName=fclpasskey&registration=true&countryCode=" + CCL + "&languageCode=" + LCL + "&fclHost=" + LevelURL + "&step3URL=" + LevelURL + "%2Fapps%2Fshipadmin&afterwardsURL=" + LevelURL + "%2Fapps%2Fshipadmin&locale=" + LCL + "_" + CCU + "&programIndicator=1";
				break;
			case "ATRK":
				AppUrl = LevelURL + "/apps/fedextracking/?cntry_code=" + CCL + "&locale=" + CCL + "_" + LCL;
				break;
			case "ECAM":			
    			if (Environment.getInstance().getLevel().contentEquals("2")) {
    				AppUrl = "https://ecamdev.idev.fedex.com/ecam/index.html";
    			}else if (Environment.getInstance().getLevel().contentEquals("3")) {
    				AppUrl = "https://ecamdrt.idev.fedex.com/ecam/index.html";
    			}else if (Environment.getInstance().getLevel().contentEquals("5")) {
    				AppUrl = "https://ecambit.idev.fedex.com/ecam/index.html";
    			}
    			break;
			case "EMASS":
    			if (Environment.getInstance().getLevel().contentEquals("2")) {
    				AppUrl = "https://devsso.secure.fedex.com/L2/eShipmentGUI/DisplayLinkHandler?id=251";
    			}else if (Environment.getInstance().getLevel().contentEquals("3")) {
    				AppUrl = "https://testsso.secure.fedex.com/L3/eShipmentGUI/DisplayLinkHandler?id=351";
    			}
    			break;
    		case "ECRV":		
    			AppUrl = LevelURL + "/ratetools/RateToolsEntry.do?link=2&CountryCode=" + CCU + "&locale=" + CCU + "_" + LCL + "";AppUrl = AppUrl.replace("https", "http");
    			break;
			case "FCLCREATE":  	
				AppUrl = LevelURL + "/fcl/web/jsp/contactInfo1.jsp?appName=oadr&locale=" + CCL + "_" + LCL + "&step3URL=https%3A%2F%2F.fedex.com%3A443%2Ffcl%2Fweb%2Fjsp%2FfclValidateAndCreate.jsp&afterwardsURL=" + LevelURL + "%3A443%2Ffcl%2Fweb%2Fjsp%2Foadr.jsp&programIndicator=p314t9n1ey&accountOption=link";
				break;
    		case "FCLLINK":		
    			AppUrl = LevelURL + "/fcl/web/jsp/contactInfo1.jsp?appName=oadr&locale=" + CCL + "_" + LCL + "&step3URL=https%3A%2F%2F.fedex.com%3A443%2Ffcl%2Fweb%2Fjsp%2FfclValidateAndCreate.jsp&afterwardsURL=" + LevelURL + "%3A443%2Ffcl%2Fweb%2Fjsp%2Foadr.jsp&programIndicator=p314t9n1ey&accountOption=link";
    			break; 	
    		case "FCLLINKACCOUNT":
    			AppUrl = LevelURL + "/fcl/web/jsp/accountInfo1.jsp?step3URL=" + LevelURL + "%2Fshipping%2FshipEntryAction.do%3Fmethod%3DdoRegistration%26link%3D1%26locale%3D" + LCL + "_" + CCU + "%26urlparams%3Dus%26sType%3DF&afterwardsURL=" + LevelURL + "%2Fshipping%2FshipEntryAction.do%3Fmethod%3DdoEntry%26link%3D1%26locale%3D" + LCL + "_" + CCU + "%26urlparams%3Dus%26sType%3DF%26programIndicator%3D0&appName=fclfsm&countryCode=" + CCL + "&languageCode=" + LCL + "&programIndicator=1&rp=fclmyprofile";
    			break;
    		case "FCLLINKINTER": 
    			AppUrl = LevelURL + "/fcl/web/jsp/contactInfo.jsp?appName=fclfsm&locale=" + CCL + "_" + LCL + "&step3URL=" + LevelURL + "%2Fship%2FshipEntryAction.do%3Fmethod%3DdoRegistration%26link%3D1%26locale%3D" + LCL + "_" + CCU + "%26urlparams%3D" + CCL + "%26sType%3DF&afterwardsURL=" + LevelURL + "%2Fship%2FshipEntryAction.do%3Fmethod%3DdoEntry%26link%3D1%26locale%3D" + LCL + "_" + CCU + "%26urlparams%3D" + CCL + "%26sType%3DF&programIndicator=0";
    			if(Helper_Functions.Check_Country_Region(CountryCode).contains("APAC")){//APAC Country
					AppUrl = AppUrl.replaceAll("%2Fship%2", "%2Fshipping_apac%2");
 	 			}
    			break;
    		case "WFCLForgot":
    			//https://wwwdrt.idev.fedex.com/fcl/web/jsp/forgotPassword.jsp?appName=fclfsm&locale=us_en&step3URL=https%3A%2F%2Fwwwdrt.idev.fedex.com%2Fshipping%2FshipEntryAction.do%3Fmethod%3DdoRegistration%26link%3D1%26locale%3Den_US%26urlparams%3Dus%26sType%3DF&returnurl=https%3A%2F%2Fwwwdrt.idev.fedex.com%2Fshipping%2FshipEntryAction.do%3Fmethod%3DdoEntry%26link%3D1%26locale%3Den_US%26urlparams%3Dus%26sType%3DF&programIndicator=0
    			AppUrl = "";
				break; 
    		case "WFCLREWARDS":	
    			AppUrl = LevelURL + "/fcl/?appName=fclfederate&locale=" + CCL + "_" + LCL + "&step3URL=" + LevelURL + "%2Ffcl%2FExistingAccountFclStep3.do&returnurl=" + LevelURL + "%2Ffcl%2Fweb%2Fjsp%2Ffederation.jsp&programIndicator=ss90705920&fedId=Epsilon";
    			break;
    		case "WFCL_REWARDS_CONFIRMATION": 
    			AppUrl = LevelURL + "/" + LCL + "-" + CCL + "/online/rewards-cfm.html"; 
    			break;
    		case "WFCL_REWARDS_PAGE":	
    			AppUrl = LevelURL + "/fcl/web/jsp/federation.jsp"; 
    			break;
    		case "FDDT":		
    			AppUrl = LevelURL+ "/EarnedDiscounts/";
    			break;
    		case "HOME":  		
    			AppUrl = LevelURL + "/" + LCL + "-" + CCL + "/home.html";
    			break;
    		case "HOME_PROD":  		
    			AppUrl = "https://www.fedex.com/en-us/home.html";
    			break;
    		case "INET":		
    			AppUrl = LevelURL + "/cgi-bin/ship_it/interNetShip?origincountry=" + CCL + "&locallang=" + LCL + "";
    			break;
    		case "INET_ADD_ACCOUNT":		
    			AppUrl = LevelURL + "/fcl/web/jsp/accountInfo1.jsp?step3URL=" + LevelURL + "%2Fshipping%2FshipEntryAction.do%3Fmethod%3DdoRegistration%26link%3D1%26locale%3D" + LCL + "_" + CCU + "%26urlparams%3D" + CCL + "%26sType%3DF&afterwardsURL=" + LevelURL + "%2Fshipping%2FshipEntryAction.do%3Fmethod%3DdoEntry%26link%3D1%26locale%3D" + LCL + "_" + CCU + "%26urlparams%3D" + CCL + "%26sType%3DF%26programIndicator%3D0&appName=fclfsm&countryCode=" + CCL + "&languageCode=" + LCL + "&programIndicator=1&rp=fclmyprofile";
    			break;
    		case "JSP":  		
    			AppUrl = "http://vjb00030.ute.fedex.com:7085/cfCDSTestApp/contact.jsp";
    			break;
    		case "JSP_EXPRESS":	
    			AppUrl = "http://vjb00030.ute.fedex.com:7085/cfCDSTestApp/express.jsp";
    			break;
    		case "LOGOUT_JSP": 
    			AppUrl = LevelURL + "/fcl/web/jsp/logout.jsp?locale=" + CCL + "_" + LCL;
    			break;
    		case "PREF":  
    			AppUrl = LevelURL + "/preferences";
    			break;	
       		case "WADM":
       		case "IPAS":
    			AppUrl = LevelURL + "/apps/shipadmin/";
				break;
    		case "WPOR":  	
    			AppUrl = LevelURL + "/" + CCL + "/developer/web-services/process.html?tab=tab1";
				break;
    		case "WPORTR":  // WPOR flow - Technology Resources	
    			AppUrl = LevelURL + "/" + CCL + "/developer/ship-manager-server/process.html?tab=tab1";
				break;	
    		case "WPORWS":  	
    			AppUrl = LevelURL + "/" + CCL + "/developer/web-services/process.html?tab=tab3";
				break; 
    		case "WGTM":
    			AppUrl = LevelURL + "/GTM?cntry_code=" + CCL;
				break;
    		case "WGTM_FID":
    			//WGTM page if the find internationl documents link is selected.
    			AppUrl = LevelURL + "FID?clienttype=dotcom&clickedPrint=false&action=entry&hazmatFilter=All&cntry_code=" + CCL + "&lang_code=" + LCL + "&initialrequest=y&option=fid";
				break;						
    		case "WIDM":// this is the Find International documents link from GTM page
    			//AppUrl = LevelURL + "/FID?clienttype=dotcom&clickedPrint=false&action=entry&hazmatFilter=All&cntry_code=" + CCL + "&lang_code=en&option=fid";
    			AppUrl = LevelURL + "/FID?clienttype=dotcomreg&clickedPrint=false&locale=" + CCL + "_" + LCL + "&action=entry&hazmatFilter=All&cntry_code=" + CCL + "&language=" + LCL + "glish&lang_code=" + LCL + "&initialrequest=y&option=fid&ccln=true";
       			break;
    		case "WDPA":  	
    			AppUrl = LevelURL + "/PickupApp/login?locale=" + LCL + "_" + CCL;
				break;
    		case "WDPA_PICKUPS":
    			AppUrl = LevelURL + "/PickupApp/pickuphistory.do?method=doInit";
				break;
    		case "WDPA_LTL":  	
    			AppUrl = LevelURL + "/PickupApp/scheduleFreightPickup.do?method=doInit&locale=" + LCL + "_" + CCL;
				break;	
    		case "WPRL":  	
    			AppUrl = LevelURL + "/apps/myprofile/loginandcontact/?locale=" + LCL + "_" + CCU + "&cntry_code=" + CCL;
				break;
    		case "WPRL_FDM":  	
    			AppUrl = LevelURL + "/apps/myprofile/deliverymanager/?locale=" + LCL + "_" + CCU + "&cntry_code=" + CCU;
				break;
    		case "WPRL_ACC":  	
    			AppUrl = LevelURL + "/apps/myprofile/accountmanagement/?locale=" + LCL + "_" + CCU + "&cntry_code=" + CCU;
				break;	
    		case "WCRV":  	
    			AppUrl = LevelURL + "/apps/ratevisibility/";
				break;
    		case "WRTT":  	
    			AppUrl = LevelURL + "/ratetools/RateToolsMain.do";
    			AppUrl = AppUrl.replace("https", "http");
				break;
    		case "WGRT":  	
    			AppUrl = LevelURL + "/ratefinder/home?cc=" + CCU + "&language=" + LCL + "&locId=express";
				break;
    		case "WERL":  	
    			AppUrl = LevelURL + "/apps/fdmenrollment/";
				break;
    		case "GFBO":  	
    			AppUrl = LevelURL + "/fcl/web/jsp/contactInfo.jsp?appName=fclgfbo&locale=" + CCL + "_" + LCL + "&step3URL=" + LevelURL + "%2Ffedexbillingonline%2Fregistration.jsp%3FuserRegistration%3DY%26locale%3D" + LCL + "_" + CCU + "&afterwardsURL=" + LevelURL + "%2Ffedexbillingonline%3F%26locale%3D" + LCL + "_" + CCU + "&programIndicator";
				break;
    		case "GFBO_LOGIN":
    			AppUrl = LevelURL + "/fedexbillingonline/pages/accountsummary/accountSummaryFBO.xhtml";
				break;
    			/*
    			case "":  	
    			AppUrl = LevelURL + ;
				break;
    			 */
    		default: //as a fall back append the correct level to the AppDesignation that is sent.
    			if (Designation.contains("Enrollment_")){//https://wwwdrt.idev.fedex.com/fcl/ALL?enrollmentid=ml18024117&language=en&country=us 
    				Designation = Designation.replace("Enrollment_", "");
    				AppUrl = LevelURL + "/fcl/ALL?enrollmentid=" + Designation + "&language=" + LCL + "&country=" + CCL;
    			}else if (Designation.contains("DT_")){   //https://wwwdev.idev.fedex.com/en-us/discount-programs/DT/cc16323414.html
    				Designation = Designation.replace("DT_", "");
    				AppUrl = LevelURL + "/" + LCL + "-" + CCL + "/discount-programs/DT/" + Designation + ".html";
    			}else if (CountryCode == null || Designation.contains(LevelURL)){
    				//try and navigate to the url that was passed.
    				AppUrl = Designation;
    			}else{
    				Helper_Functions.PrintOut("Unable to recognise  " + AppDesignation, false);
    				AppUrl = AppDesignation;
    			}
		}//end switch AppDesignation
		
		if (ClearCookies) {
			DriverFactory.getInstance().getDriver().manage().deleteAllCookies();
			Helper_Functions.PrintOut("Cookies Deleted", true);
		}
		Helper_Functions.PrintOut("URL Used: " + AppUrl, true);
		
		DriverFactory.getInstance().getDriver().get(AppUrl);
		
		if (AppDesignation == "WGTM" && !CheckBodyText("English")) {
			AppUrl = AppUrl + "_english";
			DriverFactory.getInstance().getDriver().get(AppUrl);
		}
		return AppUrl;
	}
	
	public static String ChangeURL_EnrollmentID(Enrollment_Data ED, boolean AEM, boolean ClearCookies) throws Exception {
		String LevelURL = LevelUrlReturn();
		String AppUrl = null;
		if (AEM) {
			AppUrl = ED.AEM_LINK;
		}else {
			AppUrl = LevelURL + "/fcl/ALL?enrollmentid=" + ED.ENROLLMENT_ID + "&language=en&country=" + ED.COUNTRY_CODE.toLowerCase();
			
			if (!ED.MEMBERSHIP_ID.contentEquals("")) {
				AppUrl = AppUrl + "&membershipID=" + ED.MEMBERSHIP_ID;
			}
			if (!ED.PASSCODE.contentEquals("")) {
				AppUrl = AppUrl + "&passcode=" + ED.PASSCODE;
			}
		}
		
		return ChangeURL(AppUrl, null, null, ClearCookies);
	}
	
	public static boolean CheckBodyText(String TextToCheck) {
		String bodyText = DriverFactory.getInstance().getDriver().findElement(By.tagName("body")).getText();
		if (bodyText.contains(TextToCheck)) {
			return true;
		}
		return false;
	}
	
	public static void Type(By Ele, String text) throws Exception {
		//wait for the element to be present.
		WaitPresent(Ele);
		for (int i = 0 ; i < 10 ; i++) {
			//get the instance of the webdriver and then find the element by the element passed to this function
			WebElement Element = DriverFactory.getInstance().getDriver().findElement(Ele);
			//clear out any existing text.
			JavascriptExecutor myExecutor = ((JavascriptExecutor) DriverFactory.getInstance().getDriver());
			myExecutor.executeScript("arguments[0].value='';", Element);
			
			if (i < 3) {
				Element.sendKeys(text);//try entering character by character
			}else {
				myExecutor = ((JavascriptExecutor) DriverFactory.getInstance().getDriver());
				myExecutor.executeScript("arguments[0].value='" + text + "';", Element); //enter all at once, fastest method
			}
			try {Thread.sleep(100);} catch (InterruptedException e) {}//wait for a moment the check to make sure text was entered as expected
			if (Element.getText().contentEquals(text) || Element.getAttribute("value").contentEquals(text)) {
				Helper_Functions.PrintOut("    T--Text Entered " + text + " in element " + Ele.toString(), true);
				//Element.sendKeys(Keys.DOWN);//check on this copied from WCRV
				//Element.sendKeys(Keys.RETURN);
				return;
			}
		}
		throw new Exception("Not able to enter text");
    }
	
	//return the url of the new tab or if no other tabs present returns blank
	public static String CloseNewTabAndNavigateInCurrent(){
		try {
			ArrayList<String> tabs2 = new ArrayList<String> (DriverFactory.getInstance().getDriver().getWindowHandles());
		    DriverFactory.getInstance().getDriver().switchTo().window(tabs2.get(1));
		    String RedirectURL = WebDriver_Functions.GetCurrentURL();
		    DriverFactory.getInstance().getDriver().close();
		    DriverFactory.getInstance().getDriver().switchTo().window(tabs2.get(0));
		    ChangeURL(RedirectURL, null, null, false);
		    return RedirectURL;
		}catch (Exception e) {
			 return "";
		}
    }
	
	public static void Click(By Ele) throws Exception{
		WaitClickable(Ele);
		//WebDriver_Functions.Click(By.xpath("//input[(@name='accountType') and (@value = 'linkAccount')]")); //example of multiple checks with xpath
		JavascriptExecutor js = ((JavascriptExecutor) DriverFactory.getInstance().getDriver());
		Actions a = new Actions(DriverFactory.getInstance().getDriver());
		try {
			//set the timeout to 1 second, this is to reduce wait time for this method.
			DriverFactory.getInstance().getDriver().manage().timeouts().implicitlyWait(1, TimeUnit.SECONDS);
			
			a.moveToElement(DriverFactory.getInstance().getDriver().findElement(Ele)).perform();
			
			DriverFactory.getInstance().getDriver().findElement(Ele).click();
		}catch (Exception e) {
			try {
				js.executeScript("window.scrollBy(0, 300)");//scroll down
				a.moveToElement(DriverFactory.getInstance().getDriver().findElement(Ele)).perform();
				DriverFactory.getInstance().getDriver().findElement(Ele).click();
			}catch (Exception e1) {
				js.executeScript("window.scrollBy(0, -300)");//scroll up
				a.moveToElement(DriverFactory.getInstance().getDriver().findElement(Ele)).perform();
				DriverFactory.getInstance().getDriver().findElement(Ele).click();
			}
		}finally {
			Helper_Functions.PrintOut("    E--Element Clicked " + Ele.toString(), true);
			//set the timeout back to the original value.
			DriverFactory.getInstance().getDriver().manage().timeouts().implicitlyWait(DriverFactory.WaitTimeOut, TimeUnit.SECONDS);
		}
		
		// js.executeScript("window.scrollBy(0, -100)");//scroll down
		//js.executeScript("window.scrollBy(0,-2000)");// This  will scroll up the page by 500 pixel vertical	
		//js.executeScript("window.scrollTo(0, document.body.scrollHeight)");  //scroll to bottom of the page
	}
	
	//takes is the element, value, and selectByType
	public static void Select(By Ele, String Value, String SelectBy) throws Exception {
		
		if (WebDriver_Functions.isPresent(Ele) && WebDriver_Functions.isVisable(Ele)) {
			SelectBy = SelectBy.toLowerCase(); //just in case wrong font is sent.
			Click(Ele);
			String Message = null;
			
			if (SelectBy.contentEquals("i")) {
				new Select(DriverFactory.getInstance().getDriver().findElement(Ele)).selectByIndex(Integer.parseInt(Value));
				Message = "by index " + Value;//Add to this later to show the item selected.
			}else if (SelectBy.contentEquals("v")) {
				new Select(DriverFactory.getInstance().getDriver().findElement(Ele)).selectByValue(Value);
				Message = "by value " + Value;
			}else if (SelectBy.contentEquals("t")) {
				new Select(DriverFactory.getInstance().getDriver().findElement(Ele)).selectByVisibleText(Value);
				Message = "by visible text " + Value;
			}else {
				Helper_Functions.PrintOut("Not able to select by " + SelectBy, true);
				throw new Exception("Invalid SelectBy sent to Select(By Ele, String Value, String SelectBy)");
			}
			Helper_Functions.PrintOut("    S--Selected Element " + Ele.toString() + "   " + Message, true);
		}else {
			Helper_Functions.PrintOut("    Element not present on page, failing Select option", true);
		}
	}
	
    public static void takeSnapShot(String FileName) throws Exception{
    	try {
    		FileName = DriverFactory.getScreenshotPath() + FileName;
        	if (DriverFactory.BrowserCurrent == 0) {
        		return;
        	}
        	String CallingClass = Thread.currentThread().getStackTrace()[2].getClassName();
        	CallingClass = CallingClass.substring(CallingClass.indexOf(".") + 1, CallingClass.length());//remove the package of the class
        	if (CallingClass.contains("_")) {
        		CallingClass = CallingClass.substring(0, CallingClass.indexOf("_"));
        	}
        	//The below assumes that the classes follow the format of the APPName_Method. ex. If the app is WFCL then the class name would be WFCL_JUnit or WFCL_TestNG
        	//uncomment to test the stack trace.  for (StackTraceElement ste : Thread.currentThread().getStackTrace()) {System.out.println(ste);}
        	
        	Thread.sleep(1000); //added to give page extra time to load 
        	
            //Convert web driver object to TakeScreenshot
            TakesScreenshot scrShot =((TakesScreenshot) DriverFactory.getInstance().getDriver());
            //Call getScreenshotAs method to create image file
            File SrcFile=scrShot.getScreenshotAs(OutputType.FILE);
            //Move image file to new destination
            String FilePath = Helper_Functions.FileSaveDirectory + "\\" + CallingClass + "\\" + FileName;

            String Folder = FilePath;
    		try {
    			Folder = FilePath.substring(0, FilePath.lastIndexOf("\\"));
    			if (!(new File(Folder)).exists()) {
    				new File(Folder).mkdir();
    			}
    		} catch (Exception e) {  
    			System.out.println("Warning, Unable to create directory for: " + Folder);
    		}

    		File DestFile=new File(FilePath);
            
            //Copy file at destination
            FileUtils.copyFile(SrcFile, DestFile);
            Helper_Functions.PrintOut("Screenshot Taken:  " + FileName, false);
    	}catch (Exception e) {
    		System.out.println("Warning, Unable to take screenshot.");
    	}
    	
    }
	
	public static boolean isPresent(By Ele){
		boolean result = true;
	    try {
	    	DriverFactory.getInstance().getDriver().manage().timeouts().implicitlyWait(DriverFactory.WaitTimeOut, TimeUnit.MICROSECONDS); //sets the timeout for short to make reduce delay
	    	DriverFactory.getInstance().getDriver().findElement(Ele);
	    	result = DriverFactory.getInstance().getDriver().findElement(Ele).isDisplayed();
	    }catch (Exception e) {
	    	result = false;
	    	//Helper_Functions.PrintOut(Ele.toString() + " is not present");
	    }finally {
	    	DriverFactory.getInstance().getDriver().manage().timeouts().implicitlyWait(DriverFactory.WaitTimeOut, TimeUnit.SECONDS);
	    }
	
	    return result;
	}
	
	public static boolean isEnabled(By Ele){
		boolean result = false;
	    try {
	    	DriverFactory.getInstance().getDriver().manage().timeouts().implicitlyWait(DriverFactory.WaitTimeOut, TimeUnit.MICROSECONDS); //sets the timeout for short to make reduce delay
	    	DriverFactory.getInstance().getDriver().findElement(Ele);
	    	result = DriverFactory.getInstance().getDriver().findElement(Ele).isEnabled();
	    }catch (Exception e) {
	    	//Helper_Functions.PrintOut(Ele.toString() + " not able to verify if disabled.");
	    }finally {
	    	DriverFactory.getInstance().getDriver().manage().timeouts().implicitlyWait(DriverFactory.WaitTimeOut, TimeUnit.SECONDS);
	    }
	
	    return result;
	}
	
	public static String getClass(By Ele){
		String result = "";
	    try {
	    	DriverFactory.getInstance().getDriver().manage().timeouts().implicitlyWait(DriverFactory.WaitTimeOut, TimeUnit.MICROSECONDS); //sets the timeout for short to make reduce delay
	    	DriverFactory.getInstance().getDriver().findElement(Ele);
	    	result = DriverFactory.getInstance().getDriver().findElement(Ele).getAttribute("class");
	    }catch (Exception e) {
	    	Helper_Functions.PrintOut("Unable to retrive class of " + Ele.toString());
	    }finally {
	    	DriverFactory.getInstance().getDriver().manage().timeouts().implicitlyWait(DriverFactory.WaitTimeOut, TimeUnit.SECONDS);
	    }
	
	    return result;
	}
	
	public static boolean ClickIfPresent(By Ele) throws Exception{
		if (isPresent(Ele)) {
			Click(Ele);
			return true;
		}
		return false;
	}
	
	public static boolean isVisable(By Ele) {
		if (isPresent(Ele)) {
			return DriverFactory.getInstance().getDriver().findElement(Ele).isDisplayed();
		}
		return false;
	}
	
	public static boolean WaitNotPresent(By Ele) throws Exception{
		for(int i = 0; i < DriverFactory.WaitTimeOut; i++) {
			if (!isPresent(Ele)) {
				return true;
			}
			Thread.sleep(1000);
		}
		throw new Exception ("Element is still present on page.  " + Ele.toString());
	}
	
	public static boolean WaitNotVisable(By Ele) throws Exception{
		for(int i = 0; i < DriverFactory.WaitTimeOut; i++) {
			if (!isVisable(Ele)) {
				return true;
			}
			Thread.sleep(1000);
		}
		throw new Exception ("Element is still visable on page.  " + Ele.toString());
		//DriverFactory.getInstance().getDriverWait().until(ExpectedConditions.invisibilityOfElementLocated(Ele));
	}
    
    public static boolean WaitPresent(By Ele) throws Exception{
		for(int i = 0; i < DriverFactory.WaitTimeOut; i++) {
			if (isPresent(Ele)) {
				return true;
			}
			Thread.sleep(1000);
		}
		throw new Exception ("Element is not present on page.  " + Ele.toString());
    	//DriverFactory.getInstance().getDriverWait().until(ExpectedConditions.presenceOfElementLocated(Ele));
	}
    
    public static boolean WaitPresent(By Ele1, By Ele2) throws Exception{
		for(int i = 0; i < DriverFactory.WaitTimeOut; i++) {
			if (isPresent(Ele1) || isPresent(Ele2)) {
				return true;
			}
			Thread.sleep(1000);
		}
		throw new Exception ("Elements not present on page.  " + Ele1.toString() + ", " + Ele2.toString());
    	//DriverFactory.getInstance().getDriverWait().until(ExpectedConditions.or(ExpectedConditions.presenceOfElementLocated(Ele1), ExpectedConditions.presenceOfElementLocated(Ele2)));
	}
    
	public static boolean WaitForText(By Ele, String Text) throws Exception{
		for(int i = 0; i < DriverFactory.WaitTimeOut; i++) {
			if (GetText(Ele).contentEquals(Text)) {
				return true;
			}
			Thread.sleep(1000);
		}
		throw new Exception ("Text not present.  " + Ele.toString() + " - " + Text);
		//DriverFactory.getInstance().getDriverWait().until(ExpectedConditions.textToBe(Ele, Text));
	}
	
	public static void WaitForBodyText(String TextToCheck) throws Exception{
		WaitForTextPresentIn(By.tagName("body"), TextToCheck);
	}
	
	public static void WaitForTextNot(By Ele, String Text) throws Exception{
		
		for(int i = 0; i < DriverFactory.WaitTimeOut; i++) {
			try {
				//check to see if the text is not present in the element
				if (!GetText(Ele).contentEquals(Text)) {
					return;
				}
			}catch (Exception e) {
			}
			Thread.sleep(1000);
		}

		if (isPresent(Ele)) {
			throw new Exception ("Text still present.  " + Ele.toString() + " - " + Text);
		}else{
			throw new Exception ("Element is not present on page.  " + Ele.toString() + " - " + Text);
		}
		
		//DriverFactory.getInstance().getDriverWait().until(ExpectedConditions.not(ExpectedConditions.textToBe(Ele, Text)));
	}
	
	public static void WaitOr_TextToBe(By Ele1, String Text1, By Ele2, String Text2) throws Exception{
		for(int i = 0; i < DriverFactory.WaitTimeOut + 1; i++) {
			String ValueT1 = DriverFactory.getInstance().getDriver().findElement(Ele1).getAttribute("value");
			String ValueT2 = DriverFactory.getInstance().getDriver().findElement(Ele2).getAttribute("value");
			String TextT1 = DriverFactory.getInstance().getDriver().findElement(Ele1).getText();
			String TextT2 = DriverFactory.getInstance().getDriver().findElement(Ele2).getText();
			if (( ValueT1 != null && ValueT1.contentEquals(Text1)) || (ValueT2 != null && ValueT2.contentEquals(Text2))) {
				break;
			}else if (( TextT1 != null && TextT1.contentEquals(Text1)) || (TextT2 != null && TextT2.contentEquals(Text2))) {
				break;
			}else if (i == DriverFactory.WaitTimeOut) {
				throw new Exception ("Text does not match.  " + Ele1.toString() + " " + Ele2.toString());
			}
			Thread.sleep(1000);
		}
	}
	
	public static boolean WaitForTextPresentIn(By Ele, String Text) throws Exception{
		for(int i = 0; i < DriverFactory.WaitTimeOut + 1; i++) {
			if (isPresent(Ele)) {
				String DriverValue = DriverFactory.getInstance().getDriver().findElement(Ele).getAttribute("value");
				String DriverText = DriverFactory.getInstance().getDriver().findElement(Ele).getText();
				if (DriverValue != null && DriverValue.contains(Text)) {
					return true;
				}else if (DriverText!= null && DriverText.contains(Text)) {
					return true;
				}else if (i == DriverFactory.WaitTimeOut) {
					throw new Exception ("Text does not match.  " + Ele.toString() + "   " + Text);
				}
			}
			Thread.sleep(1000);//will give time incase the element needs to load on the page.
		}
		throw new Exception ("Element not present on page." + Ele.toString());
	}
	
	public static void WaitForTextNotPresentIn(By Ele, String Text) throws Exception{
		for(int i = 0; i < DriverFactory.WaitTimeOut + 1; i++) {
			if (!DriverFactory.getInstance().getDriver().findElement(Ele).getAttribute("value").contains(Text)) {
				break;
			}else if (!DriverFactory.getInstance().getDriver().findElement(Ele).getText().contains(Text)) {
				break;
			}else if (i == DriverFactory.WaitTimeOut) {
				throw new Exception ("Text does not match.  " + Ele.toString());
			}
			Thread.sleep(1000);
		}
	}

    public static void WaitClickable(By Ele) throws Exception{
		for(int i = 0; i < DriverFactory.WaitTimeOut + 1; i++) {
			if (DriverFactory.getInstance().getDriver().findElement(Ele).isDisplayed()) {
				break;
			}
			Thread.sleep(1000);
		}
    	
    	
    	//DriverFactory.getInstance().getDriverWait().until(ExpectedConditions.elementToBeClickable(Ele));
	}
    
    public static String getURLByLinkText(String LinkText)throws Exception{
    	if (isPresent(By.linkText(LinkText))) {
    		String ElementURL = DriverFactory.getInstance().getDriver().findElement(By.linkText(LinkText)).getAttribute("href");
    		
    		//Will do a check on the page if there are multiple elements and different navigations.
			List<WebElement> elements = DriverFactory.getInstance().getDriver().findElements(By.linkText(LinkText)); 
			for (WebElement element: elements){ 
				if (!element.getAttribute("href").contentEquals(ElementURL)) {
					Helper_Functions.PrintOut("Warning, Multiple Links on page with same text and differnt urls.\n" + element.getAttribute("href"));
				} 
			}
    		
    		return ElementURL;
    	}else {
    		return ""; //element is not preset on page.
    	}
	}
    
    public static String getURLByhref(By Ele)throws Exception{
    	if (isPresent(Ele)) {
    		String ElementURL = DriverFactory.getInstance().getDriver().findElement(Ele).getAttribute("href");
    		return ElementURL;
    	}else {
    		return ""; //element is not preset on page.
    	}
	}
    
    public static boolean isSelected(By Ele) throws Exception{
    	try {
    		return DriverFactory.getInstance().getDriver().findElement(Ele).isSelected();
    	}catch(Exception e) {
    		return false;//False if element not present.
    	}
    	
    }
	
    public static boolean OpenFile(String FilePath) {
    	try {
        	DriverFactory.getInstance().getDriver().get(FilePath);
    		return true;
    	}catch(Exception e) {
    		Helper_Functions.PrintOut("Counld not open file: " + FilePath);
    		return false;//False if file could not be opened.
    	}
    }
    
    public static String GetCookieUUID(){
    	return GetCookieValue("fcl_uuid");
    }
    
	public static String GetCookieValue(String Name){
		Set<Cookie> cookies = DriverFactory.getInstance().getDriver().manage().getCookies();
        Iterator<Cookie> itr = cookies.iterator();
        while (itr.hasNext()) {
            Cookie cookie = itr.next();
            if (cookie.getName().contentEquals(Name)){
            	Helper_Functions.PrintOut(Name + " value is " + cookie.getValue(), true);
            	return cookie.getValue();
            }
           //System.out.println("Name: " + cookie.getName() + "\n Path: " + cookie.getPath()+ "\n  Domain:  " + cookie.getDomain() + "\n   Value:  " + cookie.getValue()+ "\n    Expiry:  " + cookie.getExpiry());
        }
		return null;
	}
	
	public static void SetCookieValue(String Name, String Value){
		org.openqa.selenium.Cookie cookie;
		cookie = new org.openqa.selenium.Cookie(Name , Value);
		DriverFactory.getInstance().getDriver().manage().addCookie(cookie);
	}
	
	public static String GetCurrentURL() {
		return DriverFactory.getInstance().getDriver().getCurrentUrl();
	}

	//takes in the element expected and requirement and checks if true
	//if error will print message
	public static boolean ElementMatches(By bypath, String expected, int requirement){
		String CurrentText = "<<not present>>";
		try {
			CurrentText = GetText(bypath);
			if (CurrentText.contentEquals("<<not present>>") || CurrentText.contentEquals("")) {
				CurrentText =  GetValue(bypath); 
			}
			
			if (CurrentText.contentEquals(expected)) {
				Helper_Functions.PrintOut("Verified Text: " + expected.replaceAll("\n", "\\ n") + "     " + bypath, true); //will replace all new line characters for sake of formatting.
				return true;
			}else {
				System.err.println("Failure matching element.");
				Helper_Functions.PrintOut("FAILURE: _" + expected + "_ is not present. ID " + requirement + ". Current _" + CurrentText + "_", true);
				Helper_Functions.PrintOut("Differnce starts at: " + StringUtils.difference(expected, CurrentText), true);
				return false;
			}
		}catch (Exception e){
			Helper_Functions.PrintOut("FAILURE: Unble to validate element " + bypath + " for " + expected, true);
		} 
		return false;
	}
	
	public static boolean ElementMatchesSelect(By bypath, String expected, int requirement){
		String CurrentText = "<<not present>>";
		try {
			Select select = new Select(DriverFactory.getInstance().getDriver().findElement(bypath));
			WebElement option = select.getFirstSelectedOption();
			CurrentText = option.getText();
			 
			if (!CurrentText.contentEquals(expected)) {
				throw new Exception ("does not match");
			}
			Helper_Functions.PrintOut("Verified Text: " + expected + "     " + bypath, true);
			return true;
		}catch (Exception e){
			Helper_Functions.PrintOut("FAILURE: _" + expected + "_ is not present. ID " + requirement + ". Current _" + CurrentText + "_", true);
			Helper_Functions.PrintOut("Differnce starts at: " + StringUtils.difference(expected, CurrentText), true);
		} 
		return false;
	}
	
	public static String GetValue(By Ele) {
		return DriverFactory.getInstance().getDriver().findElement(Ele).getAttribute("value");
	}
	
	public static String GetText(By Ele) {
		return DriverFactory.getInstance().getDriver().findElement(Ele).getText();
	}
	
	public static String GetBodyText() {
		return GetText(By.tagName("body"));
	}

    public static boolean Login(User_Data User_Info) throws Exception {
    	//high level 
    	if(User_Info.USER_ID == null || User_Info.USER_ID == ""){
    		Helper_Functions.PrintOut("Cannot login with user id as null. Recieved from " + Thread.currentThread().getStackTrace()[2].getMethodName(), true);
    		throw new Exception("User not provided");
    	}else if (User_Info.PASSWORD == null || User_Info.PASSWORD == "") {
    		Helper_Functions.PrintOut("Cannot login with password as null. Recieved from " + Thread.currentThread().getStackTrace()[2].getMethodName(), true);
    		throw new Exception("Password not provided.");
    	}
    	
    	//navigate to US home page and clear cookies.
    	ChangeURL("HOME", User_Info.Address_Info.Country_Code, null, true);
    	
    	try{
    		//try to login from the USRC API call
    		USRC_Endpoints.Login_API_Load_Cookies(User_Info.USER_ID, User_Info.PASSWORD);
    		if (GetCookieValue("fcl_uuid") == null) {
        		throw new Exception("Not able to login through USRC call");
        	}
    	    return true;
    	}catch(Exception e){
    		Helper_Functions.PrintOut(e.getMessage(), true);
    	}

    	
    	try{
    		//try to login from the WDPA page
        	ChangeURL("WDPA", null, null, false);
    		Type(By.name("username"), User_Info.USER_ID);
    		Type(By.name("password"), User_Info.PASSWORD);
    		Click(By.name("login"));
    		if (GetCookieValue("fcl_uuid") == null) {
        		throw new Exception("Did not login through WDPA");
        	}
    		Helper_Functions.PrintOut("Login:" + User_Info.USER_ID + "/" + User_Info.PASSWORD, true);
    	    return true;
    	}catch(Exception e3){
    		Helper_Functions.PrintOut("Not able to login through WDPA", true);
    	}
    		
        try{
    		//try to login from the INET page.
        	ChangeURL("INET", null, null, false);
        	if (DriverFactory.getInstance().getDriver().getPageSource().contains("Error 404") || DriverFactory.getInstance().getDriver().getPageSource().contains("unable to process your request at this time.")){
        		throw new Exception();
        	}
        		
        	Type(By.name("username"), User_Info.USER_ID);
        	Type(By.name("password"), User_Info.PASSWORD);
        	Click(By.name("login"));
        	if (GetCookieValue("fcl_uuid") == null) {
        		throw new Exception("Did not login through INET");
        	}
        	Helper_Functions.PrintOut("Login:" + User_Info.USER_ID + "/" + User_Info.PASSWORD, true);
            return true;
    	}catch(Exception e2){
    		Helper_Functions.PrintOut("Not able to login through INET", true);
    	}
    	
    	try{
    		//try to login from the WGTM page
        	ChangeURL("WIDM", null, null, false);
    		Type(By.id("username"), User_Info.USER_ID);
    		Type(By.id("password"), User_Info.PASSWORD);
    		Click(By.name("login"));
    		if (GetCookieValue("fcl_uuid") == null) {
        		throw new Exception("Did not login through WGTM");
        	}
    		Helper_Functions.PrintOut("Login:" + User_Info.USER_ID + "/" + User_Info.PASSWORD, true);
    	    return true;
    	}catch(Exception e3){
    		Helper_Functions.PrintOut("Not able to login through WGTM", true);
    	}
    	
    	Helper_Functions.PrintOut("Not able to login through any flows, check user id.", true);
		return false;
      }   
  	
  	public static String LevelUrlReturn() throws Exception {
  		if (!Environment.getInstance().getLevel().contentEquals("")) {
  			return LevelUrlReturn(Integer.valueOf(Environment.getInstance().getLevel()));
  		}else {
  			Helper_Functions.PrintOut("The level has not been set in the environment class. Please correct and retry. Environment.getInstance().getLevel(). From WebDriver_Functions.", false);
  			return null;
  		}
  	}
  	
  	public static String LevelUrlReturn(int Level) {
  		String LevelURL = null;
  		//Helper_Functions.PrintOut(Environment.getInstance().getLevel(), false);
  		switch (Level) {
      		case 1:
      			LevelURL = "https://wwwbase.idev.fedex.com"; break;
      		case 2:
      			LevelURL = "https://wwwdev.idev.fedex.com";  break;
      		case 3:
      			LevelURL = "https://wwwdrt.idev.fedex.com"; break;
      		case 4:
      			LevelURL = "https://wwwstress.dmz.idev.fedex.com"; break;
      		case 5:
      			LevelURL = "https://wwwbit.idev.fedex.com"; break;
      		case 6:
      			LevelURL = "https://wwwtest.fedex.com"; break;
      		case 7:
      			LevelURL = "https://www.fedex.com"; break;
  		}
  		return LevelURL;
  	}
  	
	public static boolean CheckifPasskey(){
		boolean AdminUser = false;
		try{
			ChangeURL("WADM", null, null, false);
			for (int i = 0; i < 20; i++) {
				if (CheckBodyText("Get started")) {
					break;
				}else if (CheckBodyText("Admin Home") || CheckBodyText("Please contact your administrator.")) {
					AdminUser = true;
					break;
				}
				Thread.sleep(500);
			}
		}catch(Exception e){
			Helper_Functions.PrintOut("Error when checking if admin", true);
		}
		Helper_Functions.PrintOut("Passkey check: " + AdminUser, true);
		return AdminUser;
	}	
	
	public static void CloseAlertPopUp() {
		try {
			Alert alert = DriverFactory.getInstance().getDriver().switchTo().alert();

			alert.accept(); //for two buttons, choose the affirmative one
			// or
			alert.dismiss(); //to cancel the affirmative decision, i.e., pop up will be dismissed and no action will take place
		}catch (Exception e) {}
	}

	public static void Wait(long Seconds) {
		try {
			Thread.sleep(Seconds * 1000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
}
