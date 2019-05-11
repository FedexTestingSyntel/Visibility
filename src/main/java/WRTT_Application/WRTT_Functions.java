package WRTT_Application;

import org.openqa.selenium.By;
import SupportClasses.DriverFactory;
import SupportClasses.Helper_Functions;
import SupportClasses.WebDriver_Functions;

public class WRTT_Functions {
	
	public static String WRTT_Generate(int Service, boolean ZoneChart, boolean PDF, boolean List) throws Exception {
 		// [x][0] Service name
        // [x][1] Xpath of the checkbox of the service 
 		String ServicesCheckbox[][] = {
			//Domestic Services 0 - 10
			{"SameDay", "//*[@id='domesticServiceBlock']/div/div[1]/div/div[1]/label/input"}, 
			{"FirstOvernight", "//*[@id='domesticServiceBlock']/div/div[1]/div/div[2]/label/input"},
			{"PriorityOvernight", "//*[@id='domesticServiceBlock']/div/div[1]/div/div[3]/label/input"},
			{"Standard Overnight", "//*[@id='domesticServiceBlock']/div/div[1]/div/div[4]/label/input"},
			{"2DayA.M.","//*[@id='domesticServiceBlock']/div/div[1]/div/div[5]/label/input"},
			{"2Day","//*[@id='domesticServiceBlock']/div/div[1]/div/div[6]/label/input"},
			{"ExpressSaver", "//*[@id='domesticServiceBlock']/div/div[1]/div/div[7]/label/input"},
			{"HawaiiNeighborIsland","//*[@id='domesticServiceBlock']/div/div[1]/div/div[8]/label/input"},
			{"ExpressFreight","//*[@id='domesticServiceBlock']/div/div[2]/div/div[1]/label/input"},
			{"Ground", "//*[@id='domesticServiceBlock']/div/div[2]/div/div[2]/label/input"}, 
			{"Home Delivery", "//*[@id='domesticServiceBlock']/div/div[2]/div/div[3]/label/input"},
			//Export Services 11-17
			{"InternationalNextFlight", "//*[@id='export_module']/div[2]/div/div[1]/div/div[1]/label/input"},
			{"InternationalFirst", "//*[@id='export_module']/div[2]/div/div[1]/div/div[2]/label/input"},
			{"InternationalPriority","//*[@id='export_module']/div[2]/div/div[1]/div/div[3]/label/input"},
			{"InternationalExonomy","//*[@id='export_module']/div[2]/div/div[1]/div/div[4]/label/input"},
			{"ExpressU.S.toPuertoRico","//*[@id='export_module']/div[2]/div/div[1]/div/div[5]/label/input"},
			{"ExpressInternationalFreight","//*[@id='export_module']/div[2]/div/div[2]/div/div[2]/label/input"},
			{"ExpressInternationalPremium","//*[@id='export_module']/div[2]/div/div[2]/div/div[3]/label/input"}
 		};
 		String Title = ""; //this is the title of the attempt <Z><Service><PDF/XLS><List/Retail>

		try {
			
			WebDriver_Functions.ChangeURL("WRTT", "US", true);
			 
			//radio button if the attempt should include zone chart
			if (ZoneChart){
				Title = "Z";
				WebDriver_Functions.Click(By.id("yes"));
			}else{
				WebDriver_Functions.Click(By.id("no"));
			}
			
			Title = Title + ServicesCheckbox[Service][0];
				
			//radio button if the attempt should be for domestic
			if (Service < 11){
				WebDriver_Functions.Click(By.id("domesticradio"));
			}else{
				WebDriver_Functions.Click(By.id("internationalradio"));
			}
				
			//wait for the services to load
			WebDriver_Functions.Click(By.xpath(ServicesCheckbox[Service][1]));
				
			//select the file format
			String Format;
			if (PDF){
				WebDriver_Functions.Click(By.cssSelector("input[name=\"ratesByServiceFormat\"]"));
				Format = "PDF";
			}else{
				WebDriver_Functions.Click(By.xpath("(//input[@name='ratesByServiceFormat'])[2]"));
				Format = "XLS";
			}
			Title = Title + Format;
			//select the rate type, only available for domestic
			String RateType = "";
			if (Service < 11){
				if (List){
					WebDriver_Functions.Click(By.name("ratesByServiceType"));
					RateType = "List";
				}else{
					WebDriver_Functions.Click(By.xpath("(//input[@name='ratesByServiceType'])[2]"));
					RateType = "Retail";
				}
				Title = Title + RateType;
			}else if (!List && Service > 10){
				//ServicesCheckbox[Service][0]
				return "International do not have retail rates. ";
			}
			//WebDriver_Functions.takeSnapShot(Title + ".png");
			WebDriver_Functions.Click(By.id("requestsheetbtn"));

			//download the rates
			String mainWindowHandle = DriverFactory.getInstance().getDriver().getWindowHandle();
			boolean ChildWindowAppeared = false;
			for (String childWindowHandle : DriverFactory.getInstance().getDriver().getWindowHandles()) {
				//If window handle is not main window handle then close it 
				if(!childWindowHandle.equals(mainWindowHandle)){
					ChildWindowAppeared = true;
					DriverFactory.getInstance().getDriver().switchTo().window(childWindowHandle);
					  
					//wait for the download button to load
					WebDriver_Functions.Click(By.xpath("/html/body/form/table/tbody/tr[10]/td/input"));
					//wait.until(ExpectedConditions.presenceOfElementLocated(By.xpath("/html/body/form/table/tbody/tr[10]/td/input"))).click();  //delete after testing
							
					if (!PDF){
						WebDriver_Functions.WaitForTextNot(By.tagName("body"), (""));
						String bodyText = DriverFactory.getInstance().getDriver().findElement(By.tagName("body")).getText();
						Helper_Functions.WriteToFile(bodyText, Helper_Functions.FileSaveDirectory + "\\WRTT\\" + Title);
					}
														
					// Close child windows
					Helper_Functions.Wait(5);
					DriverFactory.getInstance().getDriver().close(); 
				}
			}//end for child window
			
			//switch back to main window
			DriverFactory.getInstance().getDriver().switchTo().window(mainWindowHandle);	
			
			try{
				if (WebDriver_Functions.CheckBodyText("Retail rates are not available for this service. Please select a different service.")){
					//SameDay and InternationalNextFlight are not valid for retail rates
					if ((Service == 0 || Service == 11 || Service == 17) && !List){
						WebDriver_Functions.takeSnapShot(Title + "RetailNotAvailable.png");
						Helper_Functions.PrintOut("WRTT " + Title + " Completed successfully since retail not available", true);
						return Title;
					}else{
						//WebDriver_Functions.takeSnapShot(Title + " SC.png");
						Helper_Functions.PrintOut("!!!Check for Title why rates not available", true);
						throw new Exception ("Retail rates are not available for this service.");
					}
				}else if (!ChildWindowAppeared) {
					throw new Exception ("Child Window did not appear.");
				}else if (WebDriver_Functions.CheckBodyText("Error 500")) {
					throw new Exception("Error 500");
				}
			}catch(Exception e){
				throw e;
			}

			Helper_Functions.PrintOut("WRTT " + Title + " Completed successfully", true);
			String Result[] = new String[] {"ZoneChart: " + ZoneChart, "Service: " + ServicesCheckbox[Service][0], "Format: " + Format, "RateType: " + RateType};
			return Title;
		}catch (Exception e) {
			throw e;
		}
}//end WRTT
 	
 	public static String WRTT_eCRV_WRTTLink(String CountryCode) throws Exception {	 
		try {
			WebDriver_Functions.ChangeURL("ECRV", CountryCode, true);
			WebDriver_Functions.WaitForText(By.cssSelector("label"), "FedEx Rate Sheets");
			WebDriver_Functions.WaitForText(By.xpath("//div[@id='content']/form/div[3]/div/div/h1/label"), "Standard rates");
			WebDriver_Functions.WaitForText(By.cssSelector("h4 > label"), "View standard rates for a specific FedEx® service across all zones.");
			WebDriver_Functions.WaitForText(By.cssSelector("button.fx-btn-primary"), "Get standard rates");
			WebDriver_Functions.WaitForText(By.xpath("//div[@id='content']/form/div[3]/div[2]/div/h1/label"), "Account-based rates");
			WebDriver_Functions.WaitForText(By.xpath("//div[@id='content']/form/div[3]/div[2]/div/h4/label"), "View account-based rates for a selected FedEx® service across all zones.");
			WebDriver_Functions.WaitForText(By.xpath("//button[@onclick='getWCRV();']"), "Get account-based rates");
			WebDriver_Functions.WaitForText(By.cssSelector("b > label"), "Please note:");
			WebDriver_Functions.WaitForText(By.cssSelector("li > label"), "Additional shipping fees and optional-service fees may apply. Please see Service Info or see FedEx Service Guide for details.");
			WebDriver_Functions.WaitForText(By.xpath("//div[@id='container']/div[3]/div/div/ul/li[2]/label"), "FedEx reserves the right to modify its services and zone structure without notice.");
			WebDriver_Functions.takeSnapShot("eCRV.png");
			//quick check to see if land on page
			try{
				WebDriver_Functions.Click(By.cssSelector("button.fx-btn-primary"));
				String BodyText = DriverFactory.getInstance().getDriver().findElement(By.tagName("body")).getText().toLowerCase();
				if(CountryCode.toLowerCase().matches("us")){//US is the only country that has the WRTT page as of Jan18CL
					WebDriver_Functions.WaitForText(By.xpath("//*[@id='maincontent']/div[2]/div/div/h1"), "Standard Rate Sheets"); 
				}else if(!WebDriver_Functions.GetCurrentURL().toLowerCase().contains("/" + CountryCode.toLowerCase()) && !(WebDriver_Functions.GetCurrentURL().toLowerCase().contains("/rates") || WebDriver_Functions.GetCurrentURL().toLowerCase().contains("/downloadcenter") || WebDriver_Functions.GetCurrentURL().toLowerCase().contains("/serviceguide") || WebDriver_Functions.GetCurrentURL().toLowerCase().contains("/service-guide"))){
					throw new Exception();
				}else if (BodyText.contains("page not found") || BodyText.contains("this page is not available") ){
					throw new Exception();
				}
			}catch (Exception e) {
				Helper_Functions.PrintOut(CountryCode + " WRTT Button not working", false);
				throw new Exception("WRTT_Link: false");
			}
			
			return"WRTT_eCRV for " + CountryCode + " completed.";
		}catch (Exception e) {
			throw e;
		}
	}//end WRTT_eCRV
 	
 	public static String WRTT_eCRV_Service_Links(String CountryCode) throws Exception {	 
		try {
			WebDriver_Functions.ChangeURL("ECRV", CountryCode, true);
			WebDriver_Functions.WaitForText(By.cssSelector("li > label"), "Additional shipping fees and optional-service fees may apply. Please see Service Info or see FedEx Service Guide for details.");
			WebDriver_Functions.WaitForText(By.linkText("Service Info"), "Service Info");
			String Service_Info = DriverFactory.getInstance().getDriver().findElement(By.linkText("Service Info")).getAttribute("href"); 
			//check the link
			WebDriver_Functions.WaitForText(By.xpath("//div[@id='container']/div[3]/div/div/ul/li/label/a[2]"), "FedEx Service Guide");
			String FedEx_Service_Guide = DriverFactory.getInstance().getDriver().findElement(By.xpath("//div[@id='container']/div[3]/div/div/ul/li/label/a[2]")).getAttribute("href"); 
			WebDriver_Functions.WaitForText(By.xpath("//div[@id='container']/div[3]/div/div/ul/li[2]/label"), "FedEx reserves the right to modify its services and zone structure without notice.");
			WebDriver_Functions.takeSnapShot("eCRV.png");
			//quick check to see if land on page
			
			boolean blnService_Info = true, blnFedEx_Service_Guide = true;
			//check the Service_Info link that was present on eCRV page
			try{
				DriverFactory.getInstance().getDriver().get(Service_Info);
				if(!WebDriver_Functions.GetCurrentURL().toLowerCase().contains("/" + CountryCode.toLowerCase() + "/")){throw new Exception();}
				WebDriver_Functions.WaitForText(By.cssSelector("h1"), "Service Guide");
			}catch (Exception e) {
				blnService_Info = false;
				Helper_Functions.PrintOut(CountryCode + " Service_Info Landed on:    " + WebDriver_Functions.GetCurrentURL(), true);
			}
			
			//check the FedEx_Service_Guide link that was present on eCRV page
			try{
				DriverFactory.getInstance().getDriver().get(FedEx_Service_Guide);
				if(!WebDriver_Functions.GetCurrentURL().toLowerCase().contains("/" + CountryCode.toLowerCase())){throw new Exception();}
				WebDriver_Functions.WaitForText(By.cssSelector("h1"), "Service Guide");
			}catch (Exception e) {
				blnFedEx_Service_Guide = false;
				Helper_Functions.PrintOut(CountryCode + " FedEx_Service_Guide Landed on     " + WebDriver_Functions.GetCurrentURL(), true);
			}
			
			if (!blnService_Info && !blnFedEx_Service_Guide){
				//PrintOut("Service_Info and FedEx_Service_Guide not navigating correctly.");  //445766 open for this issue
			}else if (!blnService_Info){
				Helper_Functions.PrintOut("Service_Info not navigating correctly.", true);
			}else if (!blnFedEx_Service_Guide){
				Helper_Functions.PrintOut("FedEx_Service_Guide not navigating correctly.", true);
			}
			
			if (blnService_Info == false || blnFedEx_Service_Guide == false) {
				Helper_Functions.PrintOut("FedEx_Service_Guide: " + blnFedEx_Service_Guide + ", Service_Info: " + blnService_Info, true);
				throw new Exception("FedEx_Service_Guide: " + blnFedEx_Service_Guide + ", Service_Info: " + blnService_Info);
			}
			
			return"WRTT_eCRV_Service_Links for " + CountryCode + " completed.";
		}catch (Exception e) {
			throw e;
		}
	}//end WRTT_eCRV_Service_Links

 	public static String eCRVNavigation(String CountryCode) throws Exception {
		// launch the browser and direct it to the Base URL
		try{
			WebDriver_Functions.ChangeURL("WGRT", CountryCode, true);
				
			WebDriver_Functions.Click(By.id("wgrt.rateTools.menu_div"));
			WebDriver_Functions.Click(By.id("wgrt.rateTools.rate._div"));
			WebDriver_Functions.isPresent(By.xpath("//*[@id='content']/form/div[4]/div[1]/div/button/label"));
			return CountryCode + " Rate sheet link in WGRT is enabled";
		}catch (Exception e){
			throw e;
		}
	}
}
