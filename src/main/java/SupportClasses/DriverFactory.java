package SupportClasses;

import java.util.HashMap;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.ie.InternetExplorerDriver;
import org.openqa.selenium.remote.CapabilityType;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.openqa.selenium.support.ui.WebDriverWait;

public class DriverFactory{
	
	private static DriverFactory instance = new DriverFactory();
	public static int BrowserCurrent = 0, BrowserLimit = 3;
	public static int WaitTimeOut = 30;
	private static WebDriver DriverStorage[] = new WebDriver[BrowserLimit];
	private static boolean DriverInUse[] = new boolean[BrowserLimit];
	private static int DriverType = 0;
	
	//private DriverFactory(){//Do-nothing..Do not allow to initialize this class from outside}
	
	public static DriverFactory getInstance(){
		//String caller = Thread.currentThread().getStackTrace()[2].getMethodName();
		return instance;
	}

	ThreadLocal<WebDriver> driver = new ThreadLocal<WebDriver>(){ // thread local driver object for webdriver
	   @Override
	   synchronized protected WebDriver initialValue(){
		  // String caller = Thread.currentThread().getStackTrace()[2].getMethodName();
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
   
   private final static Lock Driverlock = new ReentrantLock();//prevent excel ready clashes
   private WebDriver CreateDriver() {
	   Driverlock.lock();
	   WebDriver Locdriver = null;
	   for (int i = 1; i < 4; i++) {//try three times to create the driver.
		   //still need to research why the below error message is appearing
		   //Nov 19, 2018 11:56:30 AM org.openqa.selenium.os.UnixProcess checkForError
		   //SEVERE: org.apache.commons.exec.ExecuteException: Process exited with an error: 1 (Exit value: 1)
		   try {
			   if (DriverType == 0) {//Chrome
				  //make sure driver in the project folder         https://sites.google.com/a/chromium.org/chromedriver/
				   String Location = System.getProperty("user.dir") + "\\chromedriver.exe";
				   System.setProperty("webdriver.chrome.driver", Location);
		   
				   ChromeOptions options = new ChromeOptions();
				   //this is used to remove the "Chrome is being controlled by automated test software" banner
				   options.addArguments("disable-infobars"); 
				   //options.addArguments("--start-fullscreen");
				   options.addArguments("start-maximized"); 
				   //open the dev tools at start
				   //options.addArguments("--auto-open-devtools-for-tabs");
			
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
			   }else if (DriverType == 1) {//IE
				   String Location = System.getProperty("user.dir") + "\\IEDriverServer.exe";
				   System.setProperty("webdriver.ie.driver", Location);
				   Locdriver = new InternetExplorerDriver();
			   }
			   		
			   BrowserCurrent++;
		   }catch (Exception e) {
			   System.err.println("ERROR CREATING DRIVER, Attempt " + i);
		   }
		   
		   if (Locdriver != null) {
			   break;//break from for loop
		   }
	   }
	   Driverlock.unlock();
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
    
    public boolean setQuickWait(boolean SetQuickWait) {
    	if (SetQuickWait) {
    		this.wait = this.quickwait;
    	}else {
    		wait.remove();//remove the current status, next time called will be recreated
    	}
		return SetQuickWait;
    	
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
     
     public static void setBrowserLimit(int BL){
    	 BrowserLimit = BL;
    	 DriverStorage = new WebDriver[BrowserLimit];
    	 DriverInUse = new boolean[BrowserLimit];
     }
}