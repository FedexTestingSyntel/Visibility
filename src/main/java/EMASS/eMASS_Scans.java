package EMASS;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Method;
import java.net.URL;
import java.text.Format;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import javax.net.ssl.HttpsURLConnection;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Listeners;
import org.testng.annotations.Test;
import Data_Structures.Account_Data;
import Data_Structures.Shipment_Data;
import SupportClasses.Environment;
import SupportClasses.Helper_Functions;

@Listeners(SupportClasses.TestNG_TestListener.class)

public class eMASS_Scans {

	static String Level = "3";
	
	@BeforeClass
	public void beforeClass() {
		Environment.SetLevelsToTest(Level);
	}
	
	@DataProvider //(parallel = true)
	public static Iterator<Object[]> dp(Method m) {
		List<Object[]> data = new ArrayList<Object[]>();
		
		for (int i=0; i < Environment.LevelsToTest.length(); i++) {
			String Level = String.valueOf(Environment.LevelsToTest.charAt(i));
			//int intLevel = Integer.parseInt(Level);
			Shipment_Data Shipment_Info_Array[] = Data_Structures.Shipment_Data.getTrackingDetails(Level);
			//Based on the method that is being called the array list will be populated
			switch (m.getName()) {
		    	case "eMASSAPITest":
		    		for (Shipment_Data Shipment_Info: Shipment_Info_Array) {
		    			if (Shipment_Info.Status.contentEquals("In transit") ||
		    					Shipment_Info.Status.contentEquals("Label created")) {
		    				Account_Data Account_Info = Environment.getAddressDetails(Level, "US");
		    				Shipment_Info.setOrigin_Address_Info(Account_Info.Billing_Address_Info);
		    				data.add(new Object[] {Level, Shipment_Info});
		    			}
		    		}
		    	break;
			}
		}
		return data.iterator();
	}
	
	@Test (dataProvider = "dp", enabled = true)
	public static void eMASSAPITest(String Level, Shipment_Data Shipment_Info){
		try {
			eMASS_API(Shipment_Info);
		}catch (Exception e) {
			e.printStackTrace();
			Assert.fail(e.getCause().toString());
		}
	}
	
	/// Still work in progress
	public static boolean eMASS_API(Shipment_Data Shipment_Info) {
		String CallingMethod = Thread.currentThread().getStackTrace()[2].getMethodName();
		String Level = Environment.getInstance().getLevel();
		String CallingIdentifier = "L" + Level + " " + CallingMethod;
		
		EMASS_Data EMASS_Details = EMASS_Data.LoadVariables(Level);
	
		String response = "";
		try {
			String queryParam = "massEntryForm=massEntryForm&ice.window=n3k4mxbm13&ice.view=v5kcn8a9m&massEntryForm%3AempNbr_inputtext=$$SCANNINGID$$&massEntryForm%3Aroute_inputtext=$$ROUTE$$&massEntryForm%3AscanDate_calendar_input=$$DATE$$&massEntryForm%3Apup_repeatinputpanel_rowCount_selectonemenu=0&massEntryForm%3Apup_repeatdatatable%3A0%3AtrkNo_inputtext=$$TRACKINGNUMBER$$&massEntryForm%3Apup_repeatdatatable%3A0%3AformCd_inputtext=$$FORMID$$&massEntryForm%3Apup_repeatdatatable%3A0%3AscanTime_maskedinput_field=10%3A11&massEntryForm%3Apup_repeatdatatable%3A0%3AstopType_selectonemenu=O&massEntryForm%3Apup_repeatdatatable%3A0%3AdestCityCd_inputtext=S&massEntryForm%3Apup_repeatdatatable%3A0%3AdestCountryCd_inputtext=$$COUNTRYCODE$$&massEntryForm%3Apup_repeatdatatable%3A0%3AdestZipCd_inputtext=$$ZIPCODE$$&massEntryForm%3Apup_repeatdatatable%3A0%3AbaseSvc_selectonemenu=20&massEntryForm%3Apup_repeatdatatable%3A0%3ApkgType_selectonemenu=01&massEntryForm%3Apup_repeatdatatable%3A0%3AhandlingCd_selectmanymenu=01&massEntryForm%3Apup_repeatdatatable%3A0%3Ahours_inputtext=0&massEntryForm%3Apup_repeatdatatable%3A0%3AdeliveryAddr_inputtext=&massEntryForm%3Apup_repeatdatatable%3A0%3Acomments_textarea_input=&icefacesCssUpdates=&javax.faces.ViewState=-7710573347804357216%3A164217649180926224&javax.faces.source=massEntryForm%3Asave_confirmation_yesbutton&javax.faces.partial.event=click&javax.faces.partial.execute=%40all&javax.faces.partial.render=%40all&ice.window=n3k4mxbm13&ice.view=v5kcn8a9m&ice.focus=massEntryForm%3Asave_confirmation_yesbutton&massEntryForm%3Asave_confirmation_yesbutton=Yes&ice.event.target=massEntryForm%3Asave_confirmation_yesbutton&ice.event.captured=massEntryForm%3Asave_confirmation_yesbutton&ice.event.type=onclick&ice.event.alt=false&ice.event.ctrl=false&ice.event.shift=false&ice.event.meta=false&ice.event.x=637&ice.event.y=558.4815063476562&ice.event.left=false&ice.event.right=false&ice.submit.type=ice.s&ice.submit.serialization=form&javax.faces.partial.ajax=true";
			
			
			Format formatter = new SimpleDateFormat("MMddyyyy"); // 12262019 -> MMDDYYYY format
			Date dt = new Date();
			Calendar c = Calendar.getInstance(); 
			c.setTime(dt); 
			String todayDateFormatted = formatter.format(dt);
			queryParam = queryParam.replace("$$DATE$$", todayDateFormatted);
			
			queryParam = queryParam.replace("$$FORMID$$", EMASS_Details.Form_ID);
			queryParam = queryParam.replace("$$ROUTE$$", EMASS_Details.Scanning_Info_FedEx_Route);
			queryParam = queryParam.replace("$$SCANNINGID$$", EMASS_Details.Scanning_Info_FedEx_Id);
			queryParam = queryParam.replace("$$TRACKINGNUMBER$$", Shipment_Info.Tracking_Number);
			queryParam = queryParam.replace("$$COUNTRYCODE$$", Shipment_Info.Origin_Address_Info.Country_Code);
			queryParam = queryParam.replace("$$ZIPCODE$$", Shipment_Info.Origin_Address_Info.PostalCode);
			
			String method = "POST";
			URL serviceUrl = new URL(EMASS_Details.ShipmentURL);

			HttpsURLConnection con = (HttpsURLConnection) serviceUrl.openConnection();
			con.setRequestMethod(method);
			
			String Cookie = EMASS_Data.getSessionCookie();
			con.setRequestProperty("Cookie", Cookie);
			
			String urlParameters = queryParam;
			StringBuffer response_buff = new StringBuffer();
			con.setDoOutput(true);
			DataOutputStream wr = new DataOutputStream(con.getOutputStream());
			wr.writeBytes(urlParameters);
			wr.flush();
			wr.close();
			BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
			String inputLine;
			while ((inputLine = in.readLine()) != null) {
				response_buff.append(inputLine);
			}
			in.close();
			response = response_buff.toString();
			
			if (response.contains("Update this with what is expected to be in successful reply")) {
				return true;
			}
		} catch (Exception e) {
			Helper_Functions.PrintOut("Unable to apply eMASS PUP Scan -- " + CallingIdentifier);
			e.printStackTrace();
		}
		return false;
	}	
}
