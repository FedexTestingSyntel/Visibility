package SupportClasses;

import java.util.HashMap;
import java.util.concurrent.TimeUnit;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.remote.CapabilityType;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.openqa.selenium.support.ui.WebDriverWait;

public class DriverFactory{
	
	private static DriverFactory instance = new DriverFactory();
	public static int BrowserCurrent = 0, BrowserLimit = 3;
	public static int WaitTimeOut = 30;
	private static WebDriver DriverStorage[] = new WebDriver[BrowserLimit];
	private static boolean DriverInUse[] = new boolean[BrowserLimit];
	
	//private DriverFactory(){//Do-nothing..Do not allow to initialize this class from outside}
	
	public static DriverFactory getInstance(){
		return instance;
	}

	ThreadLocal<WebDriver> driver = new ThreadLocal<WebDriver>(){ // thread local driver object for webdriver
	   @Override
	   synchronized protected WebDriver initialValue(){
		   //1 Thread will be waiting for a driver to be free to begin execution
		   while (BrowserCurrent >= BrowserLimit){
			   try {
				   Thread.sleep(5000);
			   } catch (InterruptedException e) {}
		   }
		   
		   for(int i = 0; i < BrowserLimit; i++) {
			   if (DriverStorage[i] != null && !DriverInUse[i]) {
				   BrowserCurrent++;
				   DriverInUse[i] = true;
				   return DriverStorage[i];
			   }
		   }
		   
		   //create a new driver
		   int position = BrowserCurrent;
		   WebDriver temp = CreateDriver();
		   DriverStorage[position] = temp;
		   DriverInUse[position] = true;

		   return temp;
	   }
   };
   
   public WebDriver CreateDriver() {
	   WebDriver Locdriver = null;
	   //make sure driver in the project folder
	   System.setProperty("webdriver.chrome.driver",System.getProperty("user.dir") + "\\chromedriver.exe");
	   
	   ChromeOptions options = new ChromeOptions();
	   //this is used to remove the "Chrome is being controlled by automated test software" banner
	   options.addArguments("disable-infobars"); 
	   //options.addArguments("--start-fullscreen");
	   options.addArguments("start-maximized");   
		
	   HashMap<String, Object> chromeOptionsMap = new HashMap<String, Object>();
	   chromeOptionsMap.put("plugins.plugins_disabled", new String[] {"Chrome PDF Viewer"});
	   chromeOptionsMap.put("plugins.always_open_pdf_externally", true);
	   options.setExperimentalOption("prefs", chromeOptionsMap);
	   String downloadFilepath = Helper_Functions.FileSaveDirectory  + "\\Download";
	   chromeOptionsMap.put("download.default_directory", downloadFilepath);
	   DesiredCapabilities cap = DesiredCapabilities.chrome();
	   cap.setCapability(ChromeOptions.CAPABILITY, chromeOptionsMap);
	   cap.setCapability(CapabilityType.ACCEPT_SSL_CERTS, true);
	   cap.setCapability(ChromeOptions.CAPABILITY, options);

	   Locdriver = new ChromeDriver(options);
	   Locdriver.manage().timeouts().implicitlyWait(WaitTimeOut, TimeUnit.SECONDS);  		
	   BrowserCurrent++;
	   return Locdriver;
	   
   }
   	   
   // call this method to get the driver object and launch the browser
   public WebDriver getDriver(){ 
	   return driver.get();
   }

   //Release all the drivers to be used in next test
   public void releaseDriver(){
	   //if there is a driver in use
	   if (BrowserCurrent > 0) {
		   for (int i = 0 ; i < BrowserLimit; i++) {
			   if (DriverStorage[i] == driver.get()) {
				   DriverInUse[i] = false;
				   BrowserCurrent--;
				  // System.err.println("Closing thread " + Thread.currentThread().getId());
				   //remove thread local driver
				   driver.remove(); 
				   ThreadLogger.getInstance().ResetLogs();
				   break;
			   }
		   }
	   }
   }
   
   // Quits the driver and closes the browser
   public static void closeDrivers(){ 
	   for (int i = 0 ; i < BrowserLimit; i++) {
		   try {
			   DriverStorage[i].close();
		   }catch (Exception e) {
			   //e.printStackTrace();
		   }
	   }
   }
    
   // thread local wait object for the webdriver
   ThreadLocal<WebDriverWait> wait = new ThreadLocal<WebDriverWait>(){ 
	   @Override
 	   synchronized protected WebDriverWait initialValue(){
 		   return new WebDriverWait(DriverFactory.getInstance().getDriver(), WaitTimeOut);
 	   }
    };
    
    public WebDriverWait getDriverWait(){ 
 	   return wait.get();
    }
    
    ThreadLocal<WebDriverWait> quickwait = new ThreadLocal<WebDriverWait>(){
  	   @Override
  	   synchronized protected WebDriverWait initialValue(){
  		   return new WebDriverWait(DriverFactory.getInstance().getDriver(), 1);
  	   }
     };
     
     public WebDriverWait getDriverQuickWait(){
  	   return quickwait.get();
     }
     
     //This is a thread local reference of teh screenshot directory.
     //need to implement this later
    static  ThreadLocal<String> ScreenshotPath = new ThreadLocal<String>(){
    	   @Override
    	   synchronized protected String initialValue(){
    		   return "";
    	   }
     };
     
     public static String getScreenshotPath(){
    	 return ScreenshotPath.get();
     }
    
     public static void setScreenshotPath(String SC){
    	 ScreenshotPath.set(SC);
     }
}