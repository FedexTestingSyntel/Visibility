package INET_Application;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebElement;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Listeners;
import org.testng.annotations.Test;
import Data_Structures.Account_Data;
import Data_Structures.Address_Data;
import Data_Structures.Shipment_Data;
import Data_Structures.User_Data;
import SupportClasses.DriverFactory;
import SupportClasses.Environment;
import SupportClasses.Helper_Functions;
import SupportClasses.WebDriver_Functions;
import Test_Data_Generation.Tracking_Data_Update;

@Listeners(SupportClasses.TestNG_TestListener.class)

public class INET_Shipment {
	static String LevelsToTest = "2";
	
	@BeforeClass
	public void beforeClass() {
		Environment.SetLevelsToTest(LevelsToTest);
	}
	
	@DataProvider (parallel = true)
	public static Iterator<Object[]> dp(Method m) {
		List<Object[]> data = new ArrayList<Object[]>();
		
		for (int i=0; i < Environment.LevelsToTest.length(); i++) {
			String Level = String.valueOf(Environment.LevelsToTest.charAt(i));
			int intLevel = Integer.parseInt(Level);
			
			//Based on the method that is being called the array list will be populated
			switch (m.getName()) {
		    	case "INET_Create_Shipment":
		    		User_Data User_Info_Array[] = User_Data.Get_UserIds(intLevel);
		    		for (User_Data User_Info : User_Info_Array) {
		    			//check for a WADM users
		    			if (User_Info.ATRK_ENABLED.contentEquals("T") && User_Info.WDPA_ENABLED.contentEquals("T")) {
		    				int loops = 0;
		    				while (loops < 10) {
			    				Shipment_Data Shipment_Info = new Shipment_Data();
			    				Shipment_Info.setUser_Info(User_Info);
			    				String AddressLineOne = "2519 W ROYAL LN";
			    				Account_Data Account_Info = Environment.getAddressDetails(Level, "US", AddressLineOne);
			    				Shipment_Info.setOrigin_Address_Info(Account_Info.Shipping_Address_Info);
			    				Shipment_Info.setDestination_Address_Info(Account_Info.Billing_Address_Info);
			    				Shipment_Info.setService("FedEx Express Saver");
		    					data.add( new Object[] {Level, Shipment_Info});
		    					loops++;
		    				}
		    				while (loops < 20) {
			    				Shipment_Data Shipment_Info = new Shipment_Data();
			    				Shipment_Info.setUser_Info(User_Info);
			    				String AddressLineOne = "2519 W ROYAL LN";
			    				Account_Data Account_Info = Environment.getAddressDetails(Level, "US", AddressLineOne);
			    				Shipment_Info.setOrigin_Address_Info(User_Info.Address_Info);
			    				Shipment_Info.setDestination_Address_Info(Account_Info.Billing_Address_Info);
			    				Shipment_Info.setService("Ground");
		    					data.add( new Object[] {Level, Shipment_Info});
		    					loops++;
		    				}
		    				break;
		    			}
		    		}
		    	break;
			}
		}
		
		while(data.size() > 20) {
			data.remove(data.size() - 1);
		}
		return data.iterator();
	}
	
	@Test(dataProvider = "dp")
	public static void INET_Create_Shipment(String Level, Shipment_Data Shipment_Info){
		try {
			Shipment_Info = INET_Create_Shipment(Shipment_Info);
			if (Shipment_Info.Service.contains("Ground")) {
				String Tracking[] = new String[] {Shipment_Info.Tracking_Number};
				GroundCorpLoad.ValidateAndProcess(Tracking);
			}else {
				eMASS_Scans.eMASS_Pickup_Scan(Shipment_Info);
			}
			
			Tracking_Data_Update.Tracking_Number_Update(Level, Shipment_Info);
		}catch (Exception e) {
			e.printStackTrace();
 			Assert.fail(e.getCause().toString());
		}
	}
	
	public static Shipment_Data INET_Create_Shipment(Shipment_Data Shipment_Info) throws Exception{
		try {
			// launch the browser and direct it to the Base URL
			WebDriver_Functions.Login(Shipment_Info.User_Info);
			WebDriver_Functions.ChangeURL("INET", Shipment_Info.Origin_Address_Info.Country_Code, null, false);
			
			//Click edit on the from address
			if (WebDriver_Functions.isVisable(By.id("module.from._headerEdit"))) {
				WebDriver_Functions.WaitClickable(By.id("module.from._headerEdit"));
				WebDriver_Functions.Click(By.id("module.from._headerEdit"));
			}

			String Location[] = {"from", "to"};
			for (String Loc : Location){
				
				Address_Data Address_Info = Shipment_Info.Origin_Address_Info;
				if (Loc.contentEquals(Location[1])){
					//change address to from address and input data.
					Address_Info = Shipment_Info.Destination_Address_Info;
				}
				
				//select the country
	 			WebDriver_Functions.Select(By.id(Loc + "Data.countryCode"), Address_Info.Country_Code, "v");
	 			
				WebDriver_Functions.WaitPresent(By.id(Loc + "Data.contactName"));
				WebDriver_Functions.Type(By.id(Loc + "Data.contactName"), Shipment_Info.User_Info.USER_ID);

				WebDriver_Functions.Type(By.id(Loc + "Data.companyName"), "Automated");
				WebDriver_Functions.Type(By.id(Loc + "Data.addressLine1"), Address_Info.Address_Line_1);
				WebDriver_Functions.Type(By.id(Loc + "Data.addressLine2"), Address_Info.Address_Line_2);
				
				if (!Address_Info.PostalCode.isEmpty()){
					//had to change method of input due to issues with error message when zip is cleared
					JavascriptExecutor myExecutor = ((JavascriptExecutor) DriverFactory.getInstance().getDriver());
			    	WebElement zipcode = DriverFactory.getInstance().getDriver().findElement(By.id(Loc + "Data.zipPostalCode"));
			    	myExecutor.executeScript("arguments[0].value='"+ Address_Info.PostalCode + "';", zipcode);
			    	Helper_Functions.PrintOut("    T--Text Entered " + Address_Info.PostalCode + " in element " + zipcode.toString(), true);
				}
				//enter city 
				WebDriver_Functions.Type(By.id(Loc + "Data.city"), Address_Info.City);
				WebDriver_Functions.Select(By.id(Loc + "Data.stateProvinceCode"), Address_Info.State_Code, "v");
				WebDriver_Functions.Type(By.id(Loc + "Data.phoneNumber"), Helper_Functions.myPhone);
			}
			
			//Select the to address is residential
			WebDriver_Functions.Click(By.id("toData.residential"));

			//Service type
			WebDriver_Functions.WaitClickable(By.id("psdData.serviceType"));
		    WebElement dropdown = DriverFactory.getInstance().getDriver().findElement(By.id("psdData.serviceType"));
		    //dropdown.click();
		    List<WebElement> options = dropdown.findElements(By.tagName("option"));
		    String optTxt = null;
		    for(WebElement option : options){
		    	optTxt = option.getText();
		        if(optTxt.contains(Shipment_Info.Service)){
		            break;
		        }
		    } 
		    
		    WebDriver_Functions.WaitPresent(By.id("psd.mps.row.weight.0"));
		    WebDriver_Functions.Type(By.id("psd.mps.row.weight.0"), "120");
		    
		    WebDriver_Functions.WaitPresent(By.id("psdData.serviceType"));
		    WebDriver_Functions.Select(By.id("psdData.serviceType"), optTxt, "t");
		    
			if (optTxt.contentEquals("First Overnight") ||
					optTxt.contentEquals("Priority Overnight") ||
					optTxt.contentEquals("Standard Overnight") ||
					optTxt.contentEquals("FedEx 2Day AM") ||
					optTxt.contentEquals("FedEx 2Day") ||
					optTxt.contentEquals("FedEx Express Saver")
					){
				
				WebDriver_Functions.WaitPresent(By.id("psdData.packageType"));
				WebDriver_Functions.Select(By.id("psdData.packageType"), "Your Packaging", "v");
				
				if (optTxt.contains("International")){
					WebDriver_Functions.Click(By.id("commodityData.packageContents.products"));
					WebDriver_Functions.Type(By.id("commodityData.totalCustomsValue") ,"10");
				}

			}else if (optTxt.contentEquals("Ground")){
				
				WebDriver_Functions.Select(By.id("psdData.packageType"), "2", "i");//Barrel
				WebDriver_Functions.Select(By.id("commodityData.shipmentPurposeCode"), "3", "i");//Gift
				WebDriver_Functions.Type(By.id("commodityData.totalCustomsValue") ,"10");

			}else if (optTxt.contentEquals("Freight")){
				//Package type is set to "Your Packaging"
				WebDriver_Functions.WaitPresent(By.id("psd.mps.row.dimensions.0"));
				WebDriver_Functions.Type(By.id("psd.mps.row.weight.0") ,"200");
				
				//wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("psd.mps.row.dimensions.0")));
				WebDriver_Functions.Select(By.id("psd.mps.row.dimensions.0"), "manual", "v");
				//wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("psd.mps.row.dimensionLength.0")));
				//wait.until(ExpectedConditions.elementToBeClickable(By.id("psd.mps.row.dimensionLength.0")));
				
				WebDriver_Functions.Type(By.id("psd.mps.row.dimensionLength.0") ,"8");
				WebDriver_Functions.Type(By.id("psd.mps.row.dimensionWidth.0") ,"4");
				WebDriver_Functions.Type(By.id("psd.mps.row.dimensionHeight.0") ,"3");

				WebDriver_Functions.Select(By.id("pdm.dimProfile"), "manual", "v");
				//wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("pdm.dimLength")));
				//wait.until(ExpectedConditions.elementToBeClickable(By.id("pdm.dimLength")));
				WebDriver_Functions.Type(By.id("pdm.dimLength") ,"8");
				WebDriver_Functions.Type(By.id("pdm.dimWidth") ,"4");
				WebDriver_Functions.Type(By.id("pdm.dimHeight") ,"3");
				//Lift gate
				WebDriver_Functions.Select(By.id("pdm.truckType"), "L", "v");
				//Trailer size = 28
				WebDriver_Functions.Select(By.id("pdm.truckSize"), "28", "v");
			}
			
			//Select indirect signature required.
			WebDriver_Functions.WaitClickable(By.id("module.ss._headerEdit"));
	WebDriver_Functions.Wait(2);
			WebDriver_Functions.Click(By.id("module.ss._headerEdit"));
			WebDriver_Functions.WaitForTextPresentIn(By.id("ss.signature.sel"), "Indirect signature required");
			WebDriver_Functions.Select(By.id("ss.signature.sel"), "Indirect signature required", "t");
			
			//schedule the shipment
			WebDriver_Functions.takeSnapShot("Shipment.png");
			WebDriver_Functions.Click(By.id("completeShip.ship.field"));
			
			//Enter product/commodity information
			if (optTxt.contains("International")){
				try{
					WebDriver_Functions.Select(By.id("commodityData.chosenProfile.profileID"), "add", "v");
					WebDriver_Functions.WaitPresent(By.id("commodityData.chosenProfile.description"));
					WebDriver_Functions.Type(By.id("commodityData.chosenProfile.description") ,"Generic Description");
					WebDriver_Functions.Select(By.id("commodityData.chosenProfile.unitOfMeasure"), "1", "i");
					WebDriver_Functions.Type(By.id("commodityData.chosenProfile.quantity") ,"10");
					WebDriver_Functions.Type(By.id("commodityData.chosenProfile.commodityWeight") ,"50");

					WebDriver_Functions.Select(By.id("commodityData.chosenProfile.manufacturingCountry"), "1", "i");
					WebDriver_Functions.Click(By.id("commodity.button.addCommodity"));
					WebDriver_Functions.WaitForText(By.id("commodity.summaryTable._contents._row1._col2"), "Generic Description");
					WebDriver_Functions.takeSnapShot("product_commodity information.png");
					WebDriver_Functions.Click(By.id("completeShip.ship.field"));
				}catch (Exception e){}
			}
			
			//Confirm shipping details
			if (WebDriver_Functions.isPresent(By.id("completeShip.ship.field"))) {
				WebDriver_Functions.Click(By.id("completeShip.ship.field"));
			}else if (WebDriver_Functions.isPresent(By.id("confirm.ship.field"))) {
				WebDriver_Functions.Click(By.id("confirm.ship.field"));
			}
			
			//confirmation page
			WebDriver_Functions.WaitPresent(By.id("trackingNumber"));
			String Tracking_Number = WebDriver_Functions.GetText(By.id("label.trackingNumber")).trim();
			Shipment_Info.setTracking_Number(Tracking_Number);
			Shipment_Info.PrintOutShipmentDetails();
			WebDriver_Functions.takeSnapShot("Shipment Confirmation.png");

			if (WebDriver_Functions.isPresent(By.id("label.alert.unsuccessfulPickupSchedule"))){
				Helper_Functions.PrintOut(WebDriver_Functions.GetText(By.id("label.alert.unsuccessfulPickupSchedule")), true);
			}
			
			Shipment_Info.writeShipment_Data_To_Excel(true);
			return Shipment_Info;
		}catch (Exception e){
			throw e;
		}
	}
}