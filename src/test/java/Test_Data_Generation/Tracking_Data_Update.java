package Test_Data_Generation;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Listeners;
import org.testng.annotations.Test;
import Data_Structures.Shipment_Data;
import INET_Application.GroundCorpLoad;
import INET_Application.eMASS_Scans;
import SupportClasses.Environment;
import SupportClasses.Helper_Functions;
import Data_Structures.User_Data;

@Listeners(SupportClasses.TestNG_TestListener.class)

public class Tracking_Data_Update {
	static String LevelsToTest = "2";
	private static boolean applyExpressPickupScanFlag = false;
	private static boolean applyGroundPickupScanFlag = false;
	static CopyOnWriteArrayList<String> TrackingList = new CopyOnWriteArrayList<String>();
	static CopyOnWriteArrayList<Object[]> NewTrackingList = new CopyOnWriteArrayList<Object[]>(); 
	public final static Lock TrackingNumberListLock = new ReentrantLock();
	
	static long interal = 5000L;
	// static long low = 111111111111L + (interal * 11);
	//111111255365         
	static long low = 111111255365L;
	
	@BeforeClass
	public void beforeClass() {
		Environment.SetLevelsToTest(LevelsToTest);
	}
	
	@DataProvider (parallel = true)
	public static Iterator<Object[]> dp(Method m) {
		List<Object[]> data = new ArrayList<Object[]>();
		
		for (int i = 0; i < Environment.LevelsToTest.length(); i++) {
			String Level = String.valueOf(Environment.LevelsToTest.charAt(i));
    		Shipment_Data Shipment_Info_Array[] = Data_Structures.Shipment_Data.getTrackingDetails(Level);
			//Based on the method that is being called the array list will be populated
			switch (m.getName()) {
		    	case "Tracking_Number_Update":
		    		String TrackingNumber = "";
		    		for (Shipment_Data Shipment_Info: Shipment_Info_Array) {
		    			// Add the existing tracking numbers to list.
		    			TrackingList.add(Shipment_Info.Tracking_Number);
		    			if (Shipment_Info.Ship_Date.contentEquals("") || Shipment_Info.TRACKING_QUALIFIER.contentEquals("") ) {
		    				data.add(new Object[] {Level, Shipment_Info});
		    				TrackingNumber += ", " + Shipment_Info.Tracking_Number;
		    			}
		    			// uncomment out the below to update for all
		    			else if (!TrackingNumber.contains(Shipment_Info.Tracking_Number)){
		    				data.add(new Object[] {Level, Shipment_Info});
		    				TrackingNumber += ", " + Shipment_Info.Tracking_Number;
		    			}
		    		}
		    	break;
		    	case "Pull_Tracking_Numbers":
		    		String UserIds = "";
		    		for (Shipment_Data Shipment_Info: Shipment_Info_Array) {
		    			// Add the existing tracking numbers to list.
		    			TrackingList.add(Shipment_Info.Tracking_Number);
		    			if (!Shipment_Info.User_Info.USER_ID.contentEquals("") ) {
		    				if (!UserIds.contains("L" + Level + "-" + Shipment_Info.User_Info.USER_ID + ", ")) {
		    					data.add(new Object[] {Level, Shipment_Info.User_Info});
		    					UserIds += "L" + Level + "-" + Shipment_Info.User_Info.USER_ID + ", ";
		    				}
		    			}
		    		}
		    		
		    		/* // sample for when checking all users
		    		int intLevel = Integer.parseInt(Level);
		    		User_Data User_Info_Array[] = User_Data.Get_UserIds(intLevel);
		    		for (User_Data User_Info : User_Info_Array) {
		    			data.add(new Object[] {Level, User_Info});
		    		} 
		    		*/
		    	break;
		    	case "Tracking_Number_Search":
		    		// long low = 111111111111L + (5000L * 2);
		    		// long high = 999999999999L;
		    		String Tracking_Array[] = new String[30];
		    		int pos = 0;
		    		for (long track = low ; track < low + interal; track++) {
		    			Tracking_Array[pos] = String.valueOf(track);
		    			pos++;
						if (pos == 30) {
							data.add(new Object[] {Level, Tracking_Array});
							Tracking_Array = new String[30];
							pos = 0;
						}
		    		}
		    		low += interal;
		    	break;

			}
		}
		
		return data.iterator();
	}
	
	@Test(dataProvider = "dp", enabled = false)
	public static void Tracking_Number_Update(String Level, Shipment_Data Shipment_Info){
		try {
			String Response_TRKC = TRKC_Application.TRKC_Endpoints.TrackingPackagesRequest(Shipment_Info.Tracking_Number);
			if (Response_TRKC.contains("This tracking number cannot be found. Please check the number or contact the sender.")) {
				throw new Exception("This tracking number cannot be found");
			}
			
			do {				
				String Status = SupportClasses.General_API_Calls.ParseStringValue(Response_TRKC, "keyStatus");
				if (Status == null || Status.contentEquals("")) {
					throw new Exception("Error retrieving status.");
				}
				
				Status = Status.replaceAll(" ", "_").toUpperCase();
				Shipment_Info.setStatus(Status);

				String Service = SupportClasses.General_API_Calls.ParseStringValue(Response_TRKC, "serviceCD");
				String trackingQualifier = SupportClasses.General_API_Calls.ParseStringValue(Response_TRKC, "trackingQualifier");
				
				if (!Service.contentEquals("")) {
					Shipment_Info.setService(Service);
					String TRACKING_CARRIER = SupportClasses.General_API_Calls.ParseStringValue(Response_TRKC, "trackingCarrierCd");
					Shipment_Info.setTRACKING_CARRIER(TRACKING_CARRIER);
					
					String shipDate = SupportClasses.General_API_Calls.ParseStringValue(Response_TRKC, "shipDt");
					Shipment_Info.setShipDate(shipDate);
					
					String estDeliveryDate = SupportClasses.General_API_Calls.ParseStringValue(Response_TRKC, "displayEstDeliveryDateTime");
					Shipment_Info.setEstDeliveryDate(estDeliveryDate);
					
					updateInflightDeliveryOptions(Shipment_Info);
					
					//// REMOVE LATER
					Shipment_Info.setTRACKING_QUALIFIER(trackingQualifier);
					
					// if this is updating a tracking number with no qualifier
					if (Shipment_Info.TRACKING_QUALIFIER.contentEquals("")) {
						Shipment_Info.setTRACKING_QUALIFIER(trackingQualifier);
						checkAndAddTrackingQualifier(trackingQualifier);
						Shipment_Info.writeShipment_Data_To_Excel(false);
					}else {
						Shipment_Info.setTRACKING_QUALIFIER(trackingQualifier);
						if (checkAndAddTrackingQualifier(trackingQualifier)) {
							Shipment_Info.writeShipment_Data_To_Excel(true);
						}else {
							// update existing shipment
							Shipment_Info.writeShipment_Data_To_Excel(false);
						}

					}
					
				}else {
					//if (applyPickupScanFlag && Shipment_Info.Service.toLowerCase().contains("ground")) {
					if (applyGroundPickupScanFlag && Shipment_Info.Service.toLowerCase().contains("ground")) {
						String Tracking[] = new String[] {Shipment_Info.Tracking_Number};
						GroundCorpLoad.ValidateAndProcess(Tracking);
						eMASS_Scans.eMASS_Pickup_Scan(Shipment_Info);
					}
					Shipment_Data cleared_Shipment_Info = new Shipment_Data();
					cleared_Shipment_Info.setTracking_Number(Shipment_Info.Tracking_Number);
					cleared_Shipment_Info.setTRACKING_QUALIFIER(trackingQualifier);
					cleared_Shipment_Info.setUser_Info(Shipment_Info.User_Info);
					cleared_Shipment_Info.setService(Shipment_Info.Service);
					cleared_Shipment_Info.setStatus("Status_Not_Found");

					// make the update to the excel so know not working
					// the pickup may still be in progress and will update in transit/label created later
					cleared_Shipment_Info.writeShipment_Data_To_Excel(checkAndAddTrackingQualifier(trackingQualifier));
				}

				if (Response_TRKC.contains("\"trackingNbr\":")) {
					// remove the identifier for the first tracking number in list.
					Response_TRKC = Response_TRKC.substring(Response_TRKC.indexOf("\"trackingNbr\":") + 20, Response_TRKC.length() - 1);
					if (Response_TRKC.contains("\"trackingNbr\":")) {
						//get rid of the everything before the current tracking number.
						Response_TRKC = Response_TRKC.substring(Response_TRKC.indexOf("\"trackingNbr\":"), Response_TRKC.length() - 1);
						Helper_Functions.PrintOut("Douplicate tracking number found: " + Shipment_Info.Tracking_Number, false);
					}
				}
				//if there are multiple shipments with same tracking number
			}while(Response_TRKC.contains("\"trackingNbr\":"));
			
			Helper_Functions.PrintOut(Shipment_Info.Tracking_Number + "  " + Shipment_Info.TRACKING_QUALIFIER + "  " + Shipment_Info.DELIVERY_DATE);
		}catch (Exception e) {
			//e.printStackTrace();
 			//Assert.fail(e.getCause().toString());
			Assert.fail();
		}
	}

	@Test(dataProvider = "dp", enabled = false)
	public static void Pull_Tracking_Numbers(String Level, User_Data User_Info){
		try {
			String Results[] = USRC_Application.USRC_Endpoints.Login(User_Info.USER_ID, User_Info.PASSWORD);
			if (Results != null) {
				String User_Cookie = Results[0];
				int pageNumber = 1;
				boolean getNextPage = true;
				while (getNextPage) {
					getNextPage = false;
					String Response_TRKC = TRKC_Application.TRKC_Endpoints.ShipmentListRequest(User_Cookie, String.valueOf(pageNumber));
					if (Response_TRKC == null || Response_TRKC.contentEquals("") || 
							SupportClasses.General_API_Calls.ParseStringValue(Response_TRKC, "totalNumberOfShipments") == null) {
						// Did not retrieve response, break out of loop.
						break;
					}
					//Check the page number to see if tracking call should be executed for second page.
					
					int totalNumberOfShipments = Integer.parseInt(SupportClasses.General_API_Calls.ParseStringValue(Response_TRKC, "totalNumberOfShipments"));
					if (totalNumberOfShipments > (500 * pageNumber)) {
						getNextPage = true;
						pageNumber++;
					}
					
					// Make sure call was successful with some tracking numbers being listed.
					if (Response_TRKC.contains("dispTrkNbr") && Response_TRKC.contains("{\"successful\":true")) {
						Shipment_Data Shipment_Info = new Shipment_Data();
						Shipment_Info.setUser_Info(User_Info);
						for(;;) {
							//reset the tracking number.
							String TRACKING_NUMBER = SupportClasses.General_API_Calls.ParseStringValue(Response_TRKC, "dispTrkNbr");
							if (TRACKING_NUMBER != null && !TRACKING_NUMBER.contentEquals("")) {
								Shipment_Info.setTracking_Number(TRACKING_NUMBER);

								String RemovedTrackingNumber = "dispTrkNbr\":\"" + TRACKING_NUMBER;
								Response_TRKC = Response_TRKC.substring(Response_TRKC.indexOf(RemovedTrackingNumber) + RemovedTrackingNumber.length(), Response_TRKC.length());
								
								String trackingQualifier = SupportClasses.General_API_Calls.ParseStringValue(Response_TRKC, "trackingQualifier");
								Shipment_Info.setTRACKING_QUALIFIER(trackingQualifier);
								if (checkAndAddTrackingQualifier(trackingQualifier)) {
									Shipment_Info.writeShipment_Data_To_Excel(true);
									// get updated data and write to excel
									//NewTrackingList.add(Level, Shipment_Info);
								}
							}else {
								break; 
							}
						}
					}
				}
			}//if not able to login
		}catch (Exception e) {
			e.printStackTrace();
 			Assert.fail(e.getCause().toString());
		}
	}
	
	@Test(dataProvider = "dp", enabled = true, invocationCount = 100)
	public static void Tracking_Number_Search(String Level, String TrackingArray[]){
		try {
			String Response_TRKC = TRKC_Application.TRKC_Endpoints.TrackingPackagesRequest(TrackingArray);
			
			do {
				String Status = SupportClasses.General_API_Calls.ParseStringValue(Response_TRKC, "keyStatus");
				if (Status == null || Status.contentEquals("")) {
					throw new Exception("Error retrieving status.");
				}
				Shipment_Data Shipment_Info = new Shipment_Data();
				String Tracking_Number = SupportClasses.General_API_Calls.ParseStringValue(Response_TRKC, "trackingNbr");
				Shipment_Info.setTracking_Number(Tracking_Number);
				
				Status = Status.replaceAll(" ", "_").toUpperCase();
				Shipment_Info.setStatus(Status);

				String Service = SupportClasses.General_API_Calls.ParseStringValue(Response_TRKC, "serviceCD");
				String trackingQualifier = SupportClasses.General_API_Calls.ParseStringValue(Response_TRKC, "trackingQualifier");
				Shipment_Info.setTRACKING_QUALIFIER(trackingQualifier);
				
				if (!Service.contentEquals("")) {
					Shipment_Info.setService(Service);
					String TRACKING_CARRIER = SupportClasses.General_API_Calls.ParseStringValue(Response_TRKC, "trackingCarrierCd");
					Shipment_Info.setTRACKING_CARRIER(TRACKING_CARRIER);
					
					String shipDate = SupportClasses.General_API_Calls.ParseStringValue(Response_TRKC, "shipDt");
					Shipment_Info.setShipDate(shipDate);
					
					String estDeliveryDate = SupportClasses.General_API_Calls.ParseStringValue(Response_TRKC, "displayEstDeliveryDateTime");
					Shipment_Info.setEstDeliveryDate(estDeliveryDate);
					
					//updateInflightDeliveryOptions(Shipment_Info);
					
					
					if (checkAndAddTrackingQualifier(trackingQualifier)) {
						Shipment_Info.writeShipment_Data_To_Excel(true);
					}
				}

				if (Response_TRKC.contains("\"trackingNbr\":")) {
					// remove the identifier for the first tracking number in list.
					Response_TRKC = Response_TRKC.substring(Response_TRKC.indexOf("\"trackingNbr\":") + 20, Response_TRKC.length() - 1);
					if (Response_TRKC.contains("\"trackingNbr\":")) {
						//get rid of the everything before the current tracking number.
						Response_TRKC = Response_TRKC.substring(Response_TRKC.indexOf("\"trackingNbr\":"), Response_TRKC.length() - 1);
					}
				}
				//if there are multiple shipments with same tracking number
			}while(Response_TRKC.contains("\"trackingNbr\":"));
		}catch (Exception e) {
			Assert.fail();
			System.out.print("fail, ");
		}
	}
	
	// Will return true if tracking Qualifier is new.
	public static boolean checkAndAddTrackingQualifier(String trackingQualifier) {
		boolean newTrackingFlag = false;
		String LevelIdent = ", L" + Environment.getInstance().getLevel();
		trackingQualifier = LevelIdent + trackingQualifier;
		TrackingNumberListLock.lock();
		if (TrackingList.size() == 0 ) {
			String Level = Environment.getInstance().getLevel();
    		Shipment_Data Shipment_Info_Array[] = Data_Structures.Shipment_Data.getTrackingDetails(Level);
    		for (Shipment_Data Shipment_Info: Shipment_Info_Array) {
    			// Add the existing tracking qualifiers to list.
    			TrackingList.add(LevelIdent + Shipment_Info.TRACKING_QUALIFIER);
    		}
		}
		
		if (!TrackingList.contains(trackingQualifier)) {
			newTrackingFlag = true;
			TrackingList.add(trackingQualifier);
			Helper_Functions.PrintOut("New Tracking Added " + trackingQualifier + "              Total Tracking: " + TrackingList.size(), false);
		}
		TrackingNumberListLock.unlock();
		return newTrackingFlag;
	}
	
	public static Shipment_Data updateInflightDeliveryOptions(Shipment_Data Shipment_Info) throws Exception{
		String Response_CMDC = CMDC_Application.CMDC_Endpoints.inflightDeliveryOptions(Shipment_Info.Tracking_Number, Shipment_Info.Ship_Date, Shipment_Info.TRACKING_QUALIFIER);
		
		if (Response_CMDC.contains("\"status\":\"ENABLED\",\"type\":\"RESCHEDULE\"")) {
			Shipment_Info.RESCHEDULE = true;
		}else {
			Shipment_Info.RESCHEDULE = false;
		}
		
		if (Response_CMDC.contains("\"status\":\"ENABLED\",\"type\":\"REROUTE\"")) {
			Shipment_Info.REROUTE = true;
		}else {
			Shipment_Info.REROUTE = false;
		}
		
		if (Response_CMDC.contains("\"status\":\"ENABLED\",\"type\":\"REDIRECT_HOLD_AT_LOCATION\"")) {
			Shipment_Info.REDIRECT_HOLD_AT_LOCATION = true;
		}else {
			Shipment_Info.REDIRECT_HOLD_AT_LOCATION = false;
		}
		
		if (Response_CMDC.contains("\"status\":\"ENABLED\",\"type\":\"SIGNATURE_RELEASE\"")) {
			Shipment_Info.SIGNATURE_RELEASE = true;
		}else {
			Shipment_Info.SIGNATURE_RELEASE = false;
		}
		
		if (Response_CMDC.contains("\"status\":\"ENABLED\",\"type\":\"DELIVERY_INSTRUCTIONS\"")) {
			Shipment_Info.DELIVERY_INSTRUCTIONS = true;
		}else {
			Shipment_Info.DELIVERY_INSTRUCTIONS = false;
		}
		
		if (Response_CMDC.contains("\"status\":\"ENABLED\",\"type\":\"DELIVERY_SUSPENSIONS\"")) {
			Shipment_Info.DELIVERY_SUSPENSIONS = true;
		}else {
			Shipment_Info.DELIVERY_SUSPENSIONS = false;
		}
		
		//try applying the pickup scan
		if (applyExpressPickupScanFlag && "IN_TRANSIT LABEL_CREATED".contains(Shipment_Info.Status)) {
			eMASS_Scans.eMASS_Pickup_Scan(Shipment_Info);
		}
		
		return Shipment_Info;
	}
	
	
	
	/*
	 * Check vs L1 so that i can pull tracking numbers if there are doubplicates 
	 * EX L1 123456789012
	 * {"ShipmentListLightResponse":{"successful":true,"errorList":[{"code":"0","message":"Request was successfully processed.","source":null,"rootCause":null}],"hasMoreShipments":"false","nextPageToken":"","totalNumberOfShipments":"6","requestRunDate":"2019\u002d08\u002d27T13\u003a14\u003a28\u002d05\u003a00","shipmentLightList":[{"trkNbr":"123456789012","trkQual":"123456789012\u007e123456789012\u007eFDFR","carrCD":"FXFR","carrDesc":"FedEx Freight","statBarCD":"PU","mainStat":"","keyStat":"Picked up","statWithDet":"Picked up\u003b SEATTLE, WA\u003b On trailer DOCK","recByNm":"","keyStatCD":"PU","subStat":"SEATTLE, WA","isFSM":"0","isHAL":"0","isClrDel":"0","isDelExc":"0","isDel":"0","isExc":"0","isAdvNot":"0","isWatch":"1","isRet":"0","isOrigOut":"0","isIn":"0","isOut":"0","isThrd":"0","isUnkn":"1","isDelToday":"0","isOnSch":"0","isShipExc":"0","isInFedExPosses":"0","tenderedDtLabel":false,"isMtchdByRecShrID":"0","isMtchdByShiprShrID":"0","isResi":"1","isSngltn":"1","delTs":"","estDelTs":"","shpTs":"2018\u002d02\u002d02T00\u003a00\u003a00\u002d06\u003a00","estDelTm":"","dispDelDt":"","dispDelTm":"","dispEstDelDt":"Pending","dispEstDelTm":"Pending","dispShpDt":"2\u002f02\u002f2018","tndrDt":"2018\u002d02\u002d02T13\u003a02\u003a15\u002d07\u003a00","dispTndrDt":"2\u002f02\u002f2018","dispTndrTm":"1\u003a02 pm","dispTndrDtTm":"2\u002f02\u002f2018 1\u003a02 pm","apptDelTs":"","displayApptDelDt":"","displayApptDelTm":"","displayApptDelDtTm":"","mstrTrkNbr":"","nickNm":"","note":"","retRsnLst":"","retRel":"","srvCD":"FEDEX\u005fFREIGHT\u005fECONOMY","srvDesc":"FedEx Freight Economy","prchOrdNbr":"","ref":"","invNbr":"","recpShareID":"","recpAddr1":"","recpCity":"SEATTLE","recpCntryCD":"US","recpCoNm":"","recpNm":"","recpStCD":"WA","recpZip":"","shprShareID":"","shprAddr1":"","shprCity":"SEATTLE","shprCntryCD":"US","shprCoNm":"","shprNm":"","shprStCD":"WA","shprZip":"","totPiec":"3333","totLbsWgt":"22222.0","dispTotLbsWt":"22,222 lbs","totKgsWgt":"10079.73","disptotKgsWt":"10,079.73 kgs","pkgLbsWgt":"22222.0","dispPkgLbsWgt":"22,222 lbs","pkgKgsWgt":"10079.73","dispPkgKgsWgt":"10,079.73 kgs","delAtmpt":"0","specHandLst":"","dispTrkNbr":"123456789012","shpTo":"","shpBy":"","acctNbrs":"","signReq":0,"delToDesc":"","reqApptWdw":"","reqApptDesc":"","reqApptInfoLst":[{"spclInstructDesc":"","delivOptn":"","delivOptnStatus":"","reqApptWdw":"","reqApptDesc":"","rerouteTRKNbr":"","beginTm":"","endTm":""}],"successful":true,"recpAddrQty":"0","retAuthNm":"","outLinkToRetTrkNbr":"","codRetTrkNbr":"","orgPiecCt":"","destPiecCt":"","retMovStat":"","totCustValAmt":"","totCustValCur":"","pkgInsValAmt":"","pkgInsValCur":"","payAcctNbr":"","metNbr":"","tcnLst":[""],"rmaLst":[""],"partCarrNbrLst":[""],"dptNbrLst":[""],"billofLadingNbrLst":[""],"shipmentIdLst":[""],"pkgType":"","pkging":"","dim":"","units":"","pkgDimIn":"","pkgDimCm":"","totDIMLbsWgt":"","dispTotDIMLbsWgt":"","totDIMKgsWgt":"","dispTotDIMKgsWgt":"","dispTotDIMWgt":"","stdTransitTmWin":{"stdTransitTimeStart":"","displayStdTransitTimeStart":"","stdTransitTimeEnd":"2018\u002d02\u002d06T00\u003a00\u003a00\u002d06\u003a00","displayStdTransitTimeEnd":""},"stdTransitDt":{"stdTransitDate":"2018\u002d02\u002d06T00\u003a00\u003a00\u002d06\u003a00","displayStdTransitDate":"2\u002f06\u002f2018"},"statusActionDesc":"","estDelTimeWindow":{"estDelTmWindowStart":"","estDelTmWindowEnd":"","displayEstDelTmWindowTmStart":"","displayEstDelTmWindowTmEnd":""},"returnedToShipperTrkNbr":"","brokerName":"","brokerCompanyName":"","buyerSoldToPartyName":"","buyerSoldToPartyCompanyName":"","importerOfRecordCompanyName":"","importerOfRecordName":"","consolidationDetails":null,"exclusionReasonDetails":[],"piecesPerShipment":"0","totalPiecesPerMPSShipment":"3333","sigAvail":false,"spod":false,"errorList":[{"code":"","message":"","source":null,"rootCause":null}],"isActualDelDtLbl":false,"isTenNotf":false,"isDelNotf":true,"isExpNotf":true,"isCurStNotf":true,"isEstDelDtChngNotf":true,"isAntShpDtLabel":false,"isDocsAvail":false,"isConsolidationDetail":false,"isDroppedOff":false,"isExclusionReason":false,"isCommodityInfoAvail":false,"isEstDelTmWindowLabel":false,"isShipDtLabel":true},{"trkNbr":"123456789012","trkQual":"2458225001\u007e123456789012\u007eFX","carrCD":"FDXE","carrDesc":"FedEx Express","statBarCD":"PU","mainStat":"","keyStat":"Picked up","statWithDet":"Picked up\u003b DALLAS, TX","recByNm":"","keyStatCD":"PU","subStat":"DALLAS, TX","isFSM":"0","isHAL":"0","isClrDel":"0","isDelExc":"0","isDel":"0","isExc":"0","isAdvNot":"0","isWatch":"1","isRet":"0","isOrigOut":"0","isIn":"0","isOut":"0","isThrd":"0","isUnkn":"1","isDelToday":"0","isOnSch":"0","isShipExc":"0","isInFedExPosses":"0","tenderedDtLabel":false,"isMtchdByRecShrID":"0","isMtchdByShiprShrID":"0","isResi":"1","isSngltn":"1","delTs":"","estDelTs":"","shpTs":"2018\u002d04\u002d18T00\u003a00\u003a00\u002d06\u003a00","estDelTm":"","dispDelDt":"","dispDelTm":"","dispEstDelDt":"Pending","dispEstDelTm":"Pending","dispShpDt":"4\u002f18\u002f2018","tndrDt":"2018\u002d04\u002d18T04\u003a39\u003a00\u002d05\u003a00","dispTndrDt":"4\u002f18\u002f2018","dispTndrTm":"4\u003a39 am","dispTndrDtTm":"4\u002f18\u002f2018 4\u003a39 am","apptDelTs":"","displayApptDelDt":"","displayApptDelTm":"","displayApptDelDtTm":"","mstrTrkNbr":"","nickNm":"","note":"","retRsnLst":"","retRel":"","srvCD":"PRIORITY\u005fOVERNIGHT","srvDesc":"FedEx Priority Overnight","prchOrdNbr":"","ref":"ASJFGVAS","invNbr":"","recpShareID":"","recpAddr1":"","recpCity":"TESTSKAJDF","recpCntryCD":"US","recpCoNm":"","recpNm":"","recpStCD":"NY","recpZip":"","shprShareID":"5mxvcgwt2vmvqinmryhyyae51","shprAddr1":"","shprCity":"WAINSCOTT","shprCntryCD":"US","shprCoNm":"","shprNm":"","shprStCD":"NY","shprZip":"","totPiec":"1","totLbsWgt":"","dispTotLbsWt":"","totKgsWgt":"","disptotKgsWt":"","pkgLbsWgt":"","dispPkgLbsWgt":"","pkgKgsWgt":"","dispPkgKgsWgt":"","delAtmpt":"0","specHandLst":"Deliver Weekday\u007c\u003a\u007cBroker Selection Option\u007c\u003a\u007c","dispTrkNbr":"123456789012","shpTo":"","shpBy":"","acctNbrs":"","signReq":0,"delToDesc":"","reqApptWdw":"","reqApptDesc":"","reqApptInfoLst":[{"spclInstructDesc":"","delivOptn":"","delivOptnStatus":"","reqApptWdw":"","reqApptDesc":"","rerouteTRKNbr":"","beginTm":"","endTm":""}],"successful":true,"recpAddrQty":"0","retAuthNm":"","outLinkToRetTrkNbr":"","codRetTrkNbr":"","orgPiecCt":"","destPiecCt":"","retMovStat":"","totCustValAmt":"","totCustValCur":"","pkgInsValAmt":"","pkgInsValCur":"","payAcctNbr":"","metNbr":"","tcnLst":[""],"rmaLst":[""],"partCarrNbrLst":[""],"dptNbrLst":[""],"billofLadingNbrLst":[""],"shipmentIdLst":[""],"pkgType":"FEDEX\u005fPAK","pkging":"FedEx Pak","dim":"","units":"","pkgDimIn":"","pkgDimCm":"","totDIMLbsWgt":"","dispTotDIMLbsWgt":"","totDIMKgsWgt":"","dispTotDIMKgsWgt":"","dispTotDIMWgt":"","stdTransitTmWin":{"stdTransitTimeStart":"","displayStdTransitTimeStart":"","stdTransitTimeEnd":"2018\u002d04\u002d19T10\u003a30\u003a00\u002d04\u003a00","displayStdTransitTimeEnd":"10\u003a30 am"},"stdTransitDt":{"stdTransitDate":"2018\u002d04\u002d19T10\u003a30\u003a00\u002d04\u003a00","displayStdTransitDate":"4\u002f19\u002f2018"},"statusActionDesc":"","estDelTimeWindow":{"estDelTmWindowStart":"","estDelTmWindowEnd":"","displayEstDelTmWindowTmStart":"","displayEstDelTmWindowTmEnd":""},"returnedToShipperTrkNbr":"","brokerName":"","brokerCompanyName":"","buyerSoldToPartyName":"","buyerSoldToPartyCompanyName":"","importerOfRecordCompanyName":"","importerOfRecordName":"","consolidationDetails":null,"exclusionReasonDetails":[],"piecesPerShipment":"0","totalPiecesPerMPSShipment":"0","sigAvail":false,"spod":false,"errorList":[{"code":"","message":"","source":null,"rootCause":null}],"isActualDelDtLbl":false,"isTenNotf":false,"isDelNotf":true,"isExpNotf":true,"isCurStNotf":true,"isEstDelDtChngNotf":true,"isAntShpDtLabel":false,"isDocsAvail":false,"isConsolidationDetail":false,"isDroppedOff":false,"isExclusionReason":false,"isCommodityInfoAvail":false,"isEstDelTmWindowLabel":false,"isShipDtLabel":true},{"trkNbr":"123456789012","trkQual":"2458241001\u007e123456789012\u007eFX","carrCD":"FDXE","carrDesc":"FedEx Express","statBarCD":"PU","mainStat":"","keyStat":"Picked up","statWithDet":"Picked up\u003b MEMPHIS, TN","recByNm":"","keyStatCD":"PU","subStat":"MEMPHIS, TN","isFSM":"0","isHAL":"0","isClrDel":"0","isDelExc":"0","isDel":"0","isExc":"0","isAdvNot":"0","isWatch":"1","isRet":"0","isOrigOut":"0","isIn":"0","isOut":"0","isThrd":"0","isUnkn":"1","isDelToday":"0","isOnSch":"0","isShipExc":"0","isInFedExPosses":"0","tenderedDtLabel":false,"isMtchdByRecShrID":"0","isMtchdByShiprShrID":"0","isResi":"1","isSngltn":"1","delTs":"","estDelTs":"","shpTs":"2018\u002d05\u002d03T00\u003a00\u003a00\u002d06\u003a00","estDelTm":"","dispDelDt":"","dispDelTm":"","dispEstDelDt":"Pending","dispEstDelTm":"Pending","dispShpDt":"5\u002f03\u002f2018","tndrDt":"2018\u002d05\u002d03T00\u003a27\u003a00\u002d05\u003a00","dispTndrDt":"5\u002f03\u002f2018","dispTndrTm":"12\u003a27 am","dispTndrDtTm":"5\u002f03\u002f2018 12\u003a27 am","apptDelTs":"","displayApptDelDt":"","displayApptDelTm":"","displayApptDelDtTm":"","mstrTrkNbr":"","nickNm":"","note":"","retRsnLst":"","retRel":"","srvCD":"INTERNATIONAL\u005fPRIORITY","srvDesc":"FedEx International Priority","prchOrdNbr":"","ref":"","invNbr":"","recpShareID":"","recpAddr1":"","recpCity":"ONTARIO","recpCntryCD":"CA","recpCoNm":"","recpNm":"","recpStCD":"ON","recpZip":"","shprShareID":"6mbwzpswcxryg1svyg5k3ktsh","shprAddr1":"","shprCity":"MEMPHIS","shprCntryCD":"US","shprCoNm":"","shprNm":"","shprStCD":"TN","shprZip":"","totPiec":"1","totLbsWgt":"4.41","dispTotLbsWt":"4.41 lbs","totKgsWgt":"2.0","disptotKgsWt":"2 kgs","pkgLbsWgt":"4.41","dispPkgLbsWgt":"4.41 lbs","pkgKgsWgt":"2.0","dispPkgKgsWgt":"2 kgs","delAtmpt":"0","specHandLst":"Deliver Weekday\u007c\u003a\u007cBroker Selection Option\u007c\u003a\u007cNo Signature Required\u007c\u003a\u007c","dispTrkNbr":"123456789012","shpTo":"","shpBy":"","acctNbrs":"","signReq":0,"delToDesc":"","reqApptWdw":"","reqApptDesc":"","reqApptInfoLst":[{"spclInstructDesc":"","delivOptn":"","delivOptnStatus":"","reqApptWdw":"","reqApptDesc":"","rerouteTRKNbr":"","beginTm":"","endTm":""}],"successful":true,"recpAddrQty":"0","retAuthNm":"","outLinkToRetTrkNbr":"","codRetTrkNbr":"","orgPiecCt":"","destPiecCt":"","retMovStat":"","totCustValAmt":"","totCustValCur":"","pkgInsValAmt":"","pkgInsValCur":"","payAcctNbr":"","metNbr":"","tcnLst":[""],"rmaLst":[""],"partCarrNbrLst":[""],"dptNbrLst":[""],"billofLadingNbrLst":[""],"shipmentIdLst":[""],"pkgType":"YOUR\u005fPACKAGING","pkging":"Your Packaging","dim":"","units":"","pkgDimIn":"1x1x1 in.","pkgDimCm":"2.54x2.54x2.54 cms","totDIMLbsWgt":"","dispTotDIMLbsWgt":"","totDIMKgsWgt":"","dispTotDIMKgsWgt":"","dispTotDIMWgt":"","stdTransitTmWin":{"stdTransitTimeStart":"","displayStdTransitTimeStart":"","stdTransitTimeEnd":"2018\u002d05\u002d04T13\u003a30\u003a00\u002d04\u003a00","displayStdTransitTimeEnd":"1\u003a30 pm"},"stdTransitDt":{"stdTransitDate":"2018\u002d05\u002d04T13\u003a30\u003a00\u002d04\u003a00","displayStdTransitDate":"5\u002f04\u002f2018"},"statusActionDesc":"","estDelTimeWindow":{"estDelTmWindowStart":"","estDelTmWindowEnd":"","displayEstDelTmWindowTmStart":"","displayEstDelTmWindowTmEnd":""},"returnedToShipperTrkNbr":"","brokerName":"","brokerCompanyName":"","buyerSoldToPartyName":"","buyerSoldToPartyCompanyName":"","importerOfRecordCompanyName":"","importerOfRecordName":"","consolidationDetails":null,"exclusionReasonDetails":[],"piecesPerShipment":"0","totalPiecesPerMPSShipment":"0","sigAvail":false,"spod":false,"errorList":[{"code":"","message":"","source":null,"rootCause":null}],"isActualDelDtLbl":false,"isTenNotf":false,"isDelNotf":true,"isExpNotf":true,"isCurStNotf":true,"isEstDelDtChngNotf":true,"isAntShpDtLabel":false,"isDocsAvail":false,"isConsolidationDetail":false,"isDroppedOff":false,"isExclusionReason":false,"isCommodityInfoAvail":false,"isEstDelTmWindowLabel":false,"isShipDtLabel":true},{"trkNbr":"123456789012","trkQual":"2458354000\u007e123456789012\u007eFX","carrCD":"FDXE","carrDesc":"FedEx Express","statBarCD":"EX","mainStat":"","keyStat":"Shipment exception","statWithDet":"Shipment exception\u003b CAGUAS, PR\u003b Package received after final location pickup has occurred. Scheduled for pickup next business day.","recByNm":"","keyStatCD":"SE","subStat":"","isFSM":"0","isHAL":"0","isClrDel":"0","isDelExc":"0","isDel":"0","isExc":"1","isAdvNot":"0","isWatch":"1","isRet":"0","isOrigOut":"0","isIn":"0","isOut":"0","isThrd":"0","isUnkn":"1","isDelToday":"0","isOnSch":"0","isShipExc":"1","isInFedExPosses":"0","tenderedDtLabel":false,"isMtchdByRecShrID":"0","isMtchdByShiprShrID":"0","isResi":"1","isSngltn":"1","delTs":"","estDelTs":"","shpTs":"2018\u002d08\u002d24T00\u003a00\u003a00\u002d06\u003a00","estDelTm":"","dispDelDt":"","dispDelTm":"","dispEstDelDt":"Pending","dispEstDelTm":"Pending","dispShpDt":"8\u002f24\u002f2018","tndrDt":"2018\u002d08\u002d23T07\u003a38\u003a00\u002d04\u003a00","dispTndrDt":"8\u002f23\u002f2018","dispTndrTm":"7\u003a38 am","dispTndrDtTm":"8\u002f23\u002f2018 7\u003a38 am","apptDelTs":"","displayApptDelDt":"","displayApptDelTm":"","displayApptDelDtTm":"","mstrTrkNbr":"","nickNm":"","note":"","retRsnLst":"","retRel":"","srvCD":"PRIORITY\u005fOVERNIGHT","srvDesc":"FedEx Priority Overnight","prchOrdNbr":"","ref":"","invNbr":"","recpShareID":"","recpAddr1":"","recpCity":"","recpCntryCD":"","recpCoNm":"","recpNm":"","recpStCD":"","recpZip":"","shprShareID":"","shprAddr1":"","shprCity":"","shprCntryCD":"","shprCoNm":"","shprNm":"","shprStCD":"","shprZip":"","totPiec":"1","totLbsWgt":"","dispTotLbsWt":"","totKgsWgt":"","disptotKgsWt":"","pkgLbsWgt":"","dispPkgLbsWgt":"","pkgKgsWgt":"","dispPkgKgsWgt":"","delAtmpt":"0","specHandLst":"Deliver Weekday\u007c\u003a\u007c","dispTrkNbr":"123456789012","shpTo":"","shpBy":"","acctNbrs":"","signReq":0,"delToDesc":"","reqApptWdw":"","reqApptDesc":"","reqApptInfoLst":[{"spclInstructDesc":"","delivOptn":"","delivOptnStatus":"","reqApptWdw":"","reqApptDesc":"","rerouteTRKNbr":"","beginTm":"","endTm":""}],"successful":true,"recpAddrQty":"0","retAuthNm":"","outLinkToRetTrkNbr":"","codRetTrkNbr":"","orgPiecCt":"","destPiecCt":"","retMovStat":"","totCustValAmt":"","totCustValCur":"","pkgInsValAmt":"","pkgInsValCur":"","payAcctNbr":"","metNbr":"","tcnLst":[""],"rmaLst":[""],"partCarrNbrLst":[""],"dptNbrLst":[""],"billofLadingNbrLst":[""],"shipmentIdLst":[""],"pkgType":"YOUR\u005fPACKAGING","pkging":"Your Packaging","dim":"","units":"","pkgDimIn":"","pkgDimCm":"","totDIMLbsWgt":"","dispTotDIMLbsWgt":"","totDIMKgsWgt":"","dispTotDIMKgsWgt":"","dispTotDIMWgt":"","stdTransitTmWin":{"stdTransitTimeStart":"","displayStdTransitTimeStart":"","stdTransitTimeEnd":"2018\u002d08\u002d28T17\u003a00\u003a00\u002d04\u003a00","displayStdTransitTimeEnd":"5\u003a00 pm"},"stdTransitDt":{"stdTransitDate":"2018\u002d08\u002d28T17\u003a00\u003a00\u002d04\u003a00","displayStdTransitDate":"8\u002f28\u002f2018"},"statusActionDesc":"NO\u005fACTION\u005fREQUIRED","estDelTimeWindow":{"estDelTmWindowStart":"","estDelTmWindowEnd":"","displayEstDelTmWindowTmStart":"","displayEstDelTmWindowTmEnd":""},"returnedToShipperTrkNbr":"","brokerName":"","brokerCompanyName":"","buyerSoldToPartyName":"","buyerSoldToPartyCompanyName":"","importerOfRecordCompanyName":"","importerOfRecordName":"","consolidationDetails":null,"exclusionReasonDetails":[],"piecesPerShipment":"0","totalPiecesPerMPSShipment":"0","sigAvail":false,"spod":false,"errorList":[{"code":"","message":"","source":null,"rootCause":null}],"isActualDelDtLbl":false,"isTenNotf":false,"isDelNotf":true,"isExpNotf":true,"isCurStNotf":true,"isEstDelDtChngNotf":true,"isAntShpDtLabel":false,"isDocsAvail":false,"isConsolidationDetail":false,"isDroppedOff":false,"isExclusionReason":false,"isCommodityInfoAvail":false,"isEstDelTmWindowLabel":false,"isShipDtLabel":true},{"trkNbr":"123456789012","trkQual":"2458424000\u007e123456789012\u007eFX","carrCD":"FDXE","carrDesc":"FedEx Express","statBarCD":"IT","mainStat":"","keyStat":"In transit","statWithDet":"In transit\u003b MEMPHIS, TN\u003b Tendered to authorized agent for final delivery","recByNm":"","keyStatCD":"IT","subStat":"MEMPHIS, TN","isFSM":"0","isHAL":"0","isClrDel":"0","isDelExc":"0","isDel":"0","isExc":"0","isAdvNot":"0","isWatch":"1","isRet":"0","isOrigOut":"0","isIn":"0","isOut":"0","isThrd":"0","isUnkn":"1","isDelToday":"0","isOnSch":"0","isShipExc":"0","isInFedExPosses":"0","tenderedDtLabel":false,"isMtchdByRecShrID":"0","isMtchdByShiprShrID":"0","isResi":"1","isSngltn":"1","delTs":"","estDelTs":"","shpTs":"2018\u002d11\u002d02T00\u003a00\u003a00\u002d06\u003a00","estDelTm":"","dispDelDt":"","dispDelTm":"","dispEstDelDt":"Pending","dispEstDelTm":"Pending","dispShpDt":"11\u002f02\u002f2018","tndrDt":"2018\u002d10\u002d22T09\u003a13\u003a00\u002d05\u003a00","dispTndrDt":"10\u002f22\u002f2018","dispTndrTm":"9\u003a13 am","dispTndrDtTm":"10\u002f22\u002f2018 9\u003a13 am","apptDelTs":"","displayApptDelDt":"","displayApptDelTm":"","displayApptDelDtTm":"","mstrTrkNbr":"","nickNm":"","note":"","retRsnLst":"","retRel":"","srvCD":"INTERNATIONAL\u005fPRIORITY","srvDesc":"FedEx International Priority","prchOrdNbr":"","ref":"","invNbr":"","recpShareID":"","recpAddr1":"","recpCity":"","recpCntryCD":"","recpCoNm":"","recpNm":"","recpStCD":"","recpZip":"","shprShareID":"","shprAddr1":"","shprCity":"","shprCntryCD":"","shprCoNm":"","shprNm":"","shprStCD":"","shprZip":"","totPiec":"1","totLbsWgt":"","dispTotLbsWt":"","totKgsWgt":"","disptotKgsWt":"","pkgLbsWgt":"","dispPkgLbsWgt":"","pkgKgsWgt":"","dispPkgKgsWgt":"","delAtmpt":"2","specHandLst":"Deliver Weekday\u007c\u003a\u007cBroker Selection Option\u007c\u003a\u007cDirect Signature Required\u007c\u003a\u007cIndirect Signature Required\u007c\u003a\u007cAdult Signature Required\u007c\u003a\u007c","dispTrkNbr":"123456789012","shpTo":"","shpBy":"","acctNbrs":"","signReq":2,"delToDesc":"","reqApptWdw":"","reqApptDesc":"","reqApptInfoLst":[{"spclInstructDesc":"","delivOptn":"","delivOptnStatus":"","reqApptWdw":"","reqApptDesc":"","rerouteTRKNbr":"","beginTm":"","endTm":""}],"successful":true,"recpAddrQty":"0","retAuthNm":"","outLinkToRetTrkNbr":"","codRetTrkNbr":"","orgPiecCt":"","destPiecCt":"","retMovStat":"","totCustValAmt":"","totCustValCur":"","pkgInsValAmt":"","pkgInsValCur":"","payAcctNbr":"","metNbr":"","tcnLst":[""],"rmaLst":[""],"partCarrNbrLst":[""],"dptNbrLst":[""],"billofLadingNbrLst":[""],"shipmentIdLst":[""],"pkgType":"FEDEX\u005fPAK","pkging":"FedEx Pak","dim":"","units":"","pkgDimIn":"","pkgDimCm":"","totDIMLbsWgt":"","dispTotDIMLbsWgt":"","totDIMKgsWgt":"","dispTotDIMKgsWgt":"","dispTotDIMWgt":"","stdTransitTmWin":{"stdTransitTimeStart":"","displayStdTransitTimeStart":"","stdTransitTimeEnd":"","displayStdTransitTimeEnd":""},"stdTransitDt":{"stdTransitDate":"","displayStdTransitDate":""},"statusActionDesc":"","estDelTimeWindow":{"estDelTmWindowStart":"","estDelTmWindowEnd":"","displayEstDelTmWindowTmStart":"","displayEstDelTmWindowTmEnd":""},"returnedToShipperTrkNbr":"","brokerName":"","brokerCompanyName":"","buyerSoldToPartyName":"","buyerSoldToPartyCompanyName":"","importerOfRecordCompanyName":"","importerOfRecordName":"","consolidationDetails":null,"exclusionReasonDetails":[],"piecesPerShipment":"0","totalPiecesPerMPSShipment":"0","sigAvail":false,"spod":false,"errorList":[{"code":"","message":"","source":null,"rootCause":null}],"isActualDelDtLbl":false,"isTenNotf":false,"isDelNotf":true,"isExpNotf":true,"isCurStNotf":true,"isEstDelDtChngNotf":true,"isAntShpDtLabel":false,"isDocsAvail":false,"isConsolidationDetail":false,"isDroppedOff":false,"isExclusionReason":false,"isCommodityInfoAvail":false,"isEstDelTmWindowLabel":false,"isShipDtLabel":true},{"trkNbr":"123456789012","trkQual":"2458519000\u007e123456789012\u007eFX","carrCD":"FDXE","carrDesc":"FedEx Express","statBarCD":"DL","mainStat":"","keyStat":"Delivered","statWithDet":"Delivered\u003a 2\u002f11\u002f2019 2\u003a59 pm Signed for by\u003aR. REETA\u003b CAGUAS, PR\u003b Damaged, delivery completed","recByNm":"R. REETA","keyStatCD":"DL","subStat":"Signed for by\u003a R. REETA","isFSM":"0","isHAL":"0","isClrDel":"0","isDelExc":"0","isDel":"1","isExc":"0","isAdvNot":"0","isWatch":"1","isRet":"0","isOrigOut":"0","isIn":"0","isOut":"0","isThrd":"0","isUnkn":"1","isDelToday":"0","isOnSch":"0","isShipExc":"0","isInFedExPosses":"0","tenderedDtLabel":false,"isMtchdByRecShrID":"0","isMtchdByShiprShrID":"0","isResi":"1","isSngltn":"1","delTs":"2019\u002d02\u002d11T14\u003a59\u003a00\u002d04\u003a00","estDelTs":"","shpTs":"2019\u002d02\u002d04T00\u003a00\u003a00\u002d06\u003a00","estDelTm":"","dispDelDt":"2\u002f11\u002f2019","dispDelTm":"2\u003a59 pm","dispEstDelDt":"","dispEstDelTm":"","dispShpDt":"2\u002f04\u002f2019","tndrDt":"2019\u002d02\u002d03T12\u003a26\u003a00\u002d06\u003a00","dispTndrDt":"2\u002f03\u002f2019","dispTndrTm":"12\u003a26 pm","dispTndrDtTm":"2\u002f03\u002f2019 12\u003a26 pm","apptDelTs":"","displayApptDelDt":"","displayApptDelTm":"","displayApptDelDtTm":"","mstrTrkNbr":"","nickNm":"","note":"","retRsnLst":"","retRel":"","srvCD":"FEDEX\u005fEXPRESS\u005fSAVER","srvDesc":"FedEx Economy","prchOrdNbr":"","ref":"","invNbr":"","recpShareID":"","recpAddr1":"","recpCity":"","recpCntryCD":"","recpCoNm":"","recpNm":"","recpStCD":"","recpZip":"","shprShareID":"","shprAddr1":"","shprCity":"","shprCntryCD":"","shprCoNm":"","shprNm":"","shprStCD":"","shprZip":"","totPiec":"1","totLbsWgt":"","dispTotLbsWt":"","totKgsWgt":"","disptotKgsWt":"","pkgLbsWgt":"","dispPkgLbsWgt":"","pkgKgsWgt":"","dispPkgKgsWgt":"","delAtmpt":"2","specHandLst":"Hold at Location\u007c\u003a\u007c","dispTrkNbr":"123456789012","shpTo":"","shpBy":"","acctNbrs":"","signReq":0,"delToDesc":"FedEx Location","reqApptWdw":"","reqApptDesc":"","reqApptInfoLst":[{"spclInstructDesc":"","delivOptn":"","delivOptnStatus":"","reqApptWdw":"","reqApptDesc":"","rerouteTRKNbr":"","beginTm":"","endTm":""}],"successful":true,"recpAddrQty":"0","retAuthNm":"","outLinkToRetTrkNbr":"","codRetTrkNbr":"","orgPiecCt":"","destPiecCt":"","retMovStat":"","totCustValAmt":"","totCustValCur":"","pkgInsValAmt":"","pkgInsValCur":"","payAcctNbr":"","metNbr":"","tcnLst":[""],"rmaLst":[""],"partCarrNbrLst":[""],"dptNbrLst":[""],"billofLadingNbrLst":[""],"shipmentIdLst":[""],"pkgType":"YOUR\u005fPACKAGING","pkging":"Your Packaging","dim":"","units":"","pkgDimIn":"","pkgDimCm":"","totDIMLbsWgt":"","dispTotDIMLbsWgt":"","totDIMKgsWgt":"","dispTotDIMKgsWgt":"","dispTotDIMWgt":"","stdTransitTmWin":{"stdTransitTimeStart":"","displayStdTransitTimeStart":"","stdTransitTimeEnd":"","displayStdTransitTimeEnd":""},"stdTransitDt":{"stdTransitDate":"","displayStdTransitDate":""},"statusActionDesc":"","estDelTimeWindow":{"estDelTmWindowStart":"","estDelTmWindowEnd":"","displayEstDelTmWindowTmStart":"","displayEstDelTmWindowTmEnd":""},"returnedToShipperTrkNbr":"","brokerName":"","brokerCompanyName":"","buyerSoldToPartyName":"","buyerSoldToPartyCompanyName":"","importerOfRecordCompanyName":"","importerOfRecordName":"","consolidationDetails":null,"exclusionReasonDetails":[],"piecesPerShipment":"0","totalPiecesPerMPSShipment":"0","sigAvail":false,"spod":true,"errorList":[{"code":"","message":"","source":null,"rootCause":null}],"isActualDelDtLbl":true,"isTenNotf":false,"isDelNotf":true,"isExpNotf":false,"isCurStNotf":true,"isEstDelDtChngNotf":false,"isAntShpDtLabel":false,"isDocsAvail":false,"isConsolidationDetail":false,"isDroppedOff":false,"isExclusionReason":false,"isCommodityInfoAvail":false,"isEstDelTmWindowLabel":false,"isShipDtLabel":true}],"consolidationEligibilty":false,"alerts":null,"cxsalerts":null}}
	 * 
	 */
}