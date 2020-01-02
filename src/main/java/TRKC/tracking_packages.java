package TRKC;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONObject;
import API_Functions.General_API_Calls;
import SupportClasses.Helper_Functions;

public class tracking_packages {

	// will take in multiple tracking numbers and make single call.
	public static String TrackingPackagesRequest(String TrackingNumberArray[]){
		Object trackingInfoListArray[] = new Object[TrackingNumberArray.length];
		
		for (int i = 0; i < TrackingNumberArray.length; i++) {
			String TrackingNumber = TrackingNumberArray[i];
  			trackingInfoListArray[i] = buildTrackingInfoListArray(TrackingNumber, "", "");
		}
		return TrackingPackagesRequestBuild(trackingInfoListArray);
	}
	
	// will take in single tracking number and make single call.
	public static String TrackingPackagesRequest(String TrackingNumber){
		Object trackingInfoListArray[] = new Object[] {buildTrackingInfoListArray(TrackingNumber, "", "")};
		return TrackingPackagesRequestBuild(trackingInfoListArray);
	}
	
	public static String TrackingPackagesRequest(String trackingNumber, String trackingQualifier, String trackingCarrier){
		Object trackingInfoListArray[] = new Object[] {buildTrackingInfoListArray(trackingNumber, trackingQualifier, trackingCarrier)};
		return TrackingPackagesRequestBuild(trackingInfoListArray);
	}
	
	private static Object buildTrackingInfoListArray(String trackingNumber, String trackingQualifier, String trackingCarrier) {
		JSONObject trackNumberInfo = new JSONObject()
	  				.put("trackingNumber", trackingNumber)
	  				.put("trackingQualifier", trackingQualifier)
	  				.put("trackingCarrier", trackingCarrier);
			
		JSONObject trackingInfoListElement = new JSONObject()
	  				.put("trackNumberInfo", trackNumberInfo);
			
		return trackingInfoListElement;
	}
	
	private static String TrackingPackagesRequestBuild(Object trackingInfoListArray[]){
  		try{
  			TRKC_Data TRKC_Details = TRKC_Data.LoadVariables();
  			HttpPost httppost = new HttpPost(TRKC_Details.TrackingGenericURL);

  			JSONObject processingParameters = new JSONObject();
  			
  			JSONObject TrackPackagesRequest = new JSONObject()
  	  				.put("appType", "WTRK")
  	  				.put("appDeviceType", "DESKTOP")
  	  				.put("supportHTML", true)
  	  				.put("supportCurrentLocation", true)
  	  				.put("uniqueKey", "")
  	  				.put("processingParameters", processingParameters)
  	  				.put("trackingInfoList", trackingInfoListArray);
  			
  			JSONObject main = new JSONObject()
  				.put("TrackPackagesRequest", TrackPackagesRequest);

  			String json = main.toString();
  				
  			httppost.addHeader("Content-Type", "application/x-www-form-urlencoded");
  			httppost.addHeader("Content-Type", "charset=UTF-8");
  			
  			List<NameValuePair> urlParameters = new ArrayList<NameValuePair>();
  			urlParameters.add(new BasicNameValuePair("action", "trackpackages"));
  			urlParameters.add(new BasicNameValuePair("format", "json"));
  			urlParameters.add(new BasicNameValuePair("version", "1"));
  			urlParameters.add(new BasicNameValuePair("locale", "en_US"));
  			urlParameters.add(new BasicNameValuePair("data", json));

  			httppost.setEntity(new UrlEncodedFormEntity(urlParameters));
  			String Response = General_API_Calls.HTTPCall(httppost, json);	
			
  			// {"TrackPackagesResponse":{"successful":true,"passedLoggedInCheck":true,"errorList":[{"code":"0","message":"Request was successfully processed.","source":null,"rootCause":null}],"packageList":[{"shipperAccountNumber":null,"trackingNbr":"794809555252","trackingQualifier":"2458673000\u007e794809555252\u007eFX","trackingCarrierCd":"FDXE","trackingCarrierDesc":"FedEx Express","displayTrackingNbr":"794809555252","shipperCmpnyName":"","shipperName":"","shipperAddr1":"","shipperAddr2":"","shipperCity":"IRVING","shipperStateCD":"TX","shipperZip":"","shipperCntryCD":"US","shipperPhoneNbr":"","shippedBy":"","recipientCmpnyName":"","recipientName":"","recipientAddr1":"","recipientAddr2":"","recipientCity":"IRVING","recipientStateCD":"TX","recipientZip":"","recipientCntryCD":"US","recipientPhoneNbr":"","shippedTo":"","keyStatus":"Label created","keyStatusCD":"OC","lastScanStatus":"","lastScanDateTime":"","receivedByNm":"","subStatus":"","mainStatus":"Shipment information sent to FedEx","statusBarCD":"IN","shortStatus":"","shortStatusCD":"","statusLocationAddr1":"","statusLocationAddr2":"","statusLocationCity":"","statusLocationStateCD":"","statusLocationZip":"","statusLocationCntryCD":"","statusWithDetails":"Shipment information sent to FedEx\u003b Please check back later for shipment status or subscribe for e\u002dmail notifications","halType":"","halCmpnyName":"","isHALAddress":false,"shipDt":"2019\u002d07\u002d08T00\u003a00\u003a00\u002d06\u003a00","displayShipDt":"7\u002f08\u002f2019","displayShipTm":"","displayShipDateTime":"7\u002f08\u002f2019","pickupDt":"","displayPickupDt":"","displayPickupTm":"","displayPickupDateTime":"","estDeliveryDt":"","estDeliveryTm":"","displayEstDeliveryDt":"Pending","displayEstDeliveryTm":"Pending","displayEstDeliveryDateTime":"Pending","actDeliveryDt":"","displayActDeliveryDt":"","displayActDeliveryTm":"","displayActDeliveryDateTime":"","tenderedDt":"","displayTenderedDt":"","displayTenderedTm":"","displayTenderedDateTime":"","apptDeliveryDt":"","displayApptDeliveryDt":"","displayApptDeliveryTm":"","displayApptDeliveryDateTime":"","attemptedDelivery":null,"availableAtStation":null,"codDetail":null,"nickName":"","note":"","matchedAccountList":[""],"fxfAdvanceETA":"","fxfAdvanceReason":"","fxfAdvanceStatusCode":"","fxfAdvanceStatusDesc":"","destLink":"","originLink":"","hasBillOfLadingImage":false,"hasBillPresentment":false,"signatureRequired":1,"totalKgsWgt":"4.54","displayTotalKgsWgt":"4.54 kgs","totalLbsWgt":"10.0","displayTotalLbsWgt":"10 lbs","displayTotalWgt":"10 lbs \u002f 4.54 kgs","pkgKgsWgt":"4.54","displayPkgKgsWgt":"4.54 kgs","pkgLbsWgt":"10.0","displayPkgLbsWgt":"10 lbs","displayPkgWgt":"10 lbs \u002f 4.54 kgs","totalDIMLbsWgt":"","displayTotalDIMLbsWgt":"","totalDIMKgsWgt":"","displayTotalDIMKgsWgt":"","displayTotalDIMWgt":"","dimensions":"","masterTrackingNbr":"","masterQualifier":"","masterCarrierCD":"","originalOutboundTrackingNbr":null,"originalOutboundQualifier":"","originalOutboundCarrierCD":"","invoiceNbrList":[""],"referenceList":[""],"doorTagNbrList":[""],"referenceDescList":[""],"purchaseOrderNbrList":[""],"billofLadingNbrList":[""],"shipperRefList":[""],"rmaList":[""],"deptNbrList":[""],"shipmentIdList":[""],"tcnList":[""],"partnerCarrierNbrList":[""],"hasAssociatedShipments":false,"hasAssociatedReturnShipments":false,"assocShpGrp":0,"drTgGrp":["0"],"associationInfoList":[{"trackingNumberInfo":{"trackingNumber":"","trackingQualifier":"","trackingCarrier":"","processingParameters":null},"associatedType":""}],"returnReason":"","returnRelationship":null,"skuItemUpcCdList":[""],"receiveQtyList":[""],"itemDescList":[""],"partNbrList":[""],"serviceCD":"FIRST\u005fOVERNIGHT","serviceDesc":"FedEx First Overnight","serviceShortDesc":"FO","packageType":"FEDEX\u005fBOX","packaging":"FedEx Box","clearanceDetailLink":"","showClearanceDetailLink":false,"manufactureCountryCDList":[""],"commodityCDList":[""],"commodityDescList":[""],"cerNbrList":[""],"cerComplaintCDList":[""],"cerComplaintDescList":[""],"cerEventDateList":[""],"displayCerEventDateList":[""],"totalPieces":"1","specialHandlingServicesList":["Deliver Weekday","Residential Delivery","Indirect Signature Required"],"shipmentType":"","pkgContentDesc1":"","pkgContentDesc2":"","docAWBNbr":"","originalCharges":"","transportationCD":"","transportationDesc":"","dutiesAndTaxesCD":"","dutiesAndTaxesDesc":"","origPieceCount":"","destPieceCount":"","billNoteMsg":"","goodsClassificationCD":"","receipientAddrQty":"0","deliveryAttempt":"0","codReturnTrackNbr":"","returnMovementStatus":null,"scanEventList":[{"date":"2019\u002d07\u002d08","time":"13\u003a40\u003a31","gmtOffset":"\u002d05\u003a00","status":"Shipment information sent to FedEx","statusCD":"OC","scanLocation":"","scanDetails":"","scanDetailsHtml":"","rtrnShprTrkNbr":"","statusExceptionCode":"","isException":false,"isDelException":false,"isClearanceDelay":false,"isDelivered":false}],"originAddr1":"","originAddr2":"","originCity":"IRVING","originStateCD":"TX","originZip":"","originCntryCD":"US","originLocationID":"DALA","originTermCity":"IRVING","originTermStateCD":"TX","destLocationAddr1":"5000 HANSON DRIVE","destLocationAddr2":"","destLocationCity":"IRVING","destLocationStateCD":"TX","destLocationZip":"75038","destLocationCntryCD":"US","destLocationID":"DALA","destLocationTermCity":"IRVING","destLocationTermStateCD":"TX","destAddr1":"","destAddr2":"","destCity":"IRVING","destStateCD":"TX","destZip":"","destCntryCD":"US","halAddr1":"","halAddr2":"","halCity":"","halStateCD":"","halZipCD":"","halCntryCD":"","actualDelAddrCity":"","actualDelAddrStateCD":"","actualDelAddrZipCD":"","actualDelAddrCntryCD":"","totalTransitMiles":"","excepReasonList":[""],"excepActionList":[""],"exceptionReason":"","exceptionAction":"","statusDetailsList":["Please check back later for shipment status or subscribe for e\u002dmail notifications"],"trackErrCD":"","destTZ":"","originTZ":"","isMultiStat":"0","multiStatList":[{"multiPiec":"","multiTm":"","multiDispTm":"","multiSta":""}],"maskMessage":"","deliveryService":"","milestoDestination":"","terms":"Shipper","payorAcctNbr":"","meterNumber":"","originUbanizationCode":"","originCountryName":"","isOriginResidential":false,"halUrbanizationCD":"","halCountryName":"","actualDelAddrUrbanizationCD":"","actualDelAddrCountryName":"","destUrbanizationCD":"","destCountryName":"","delToDesc":"","recpShareID":"9do4qtquxlmix0i558smkp0z","shprShareID":"9do4qtquxlmix0i558smkp0z","requestedAppointmentInfoList":[{"spclInstructDesc":"","delivOptn":"","delivOptnStatus":"","reqApptWdw":"","reqApptDesc":"","rerouteTRKNbr":"","beginTm":"","endTm":""}],"defaultCDOType":"CDO","returnAuthorizationName":"","totalCustomsValueAmount":"","totalCustomsValueCurrency":"","packageInsuredValueAmount":"","packageInsuredValueCurrency":"","estDelTimeWindow":{"estDelTmWindowStart":"","estDelTmWindowEnd":"","displayEstDelTmWindowTmStart":"","displayEstDelTmWindowTmEnd":""},"standardTransitTimeWindow":{"stdTransitTimeStart":"","displayStdTransitTimeStart":"","stdTransitTimeEnd":"","displayStdTransitTimeEnd":""},"standardTransitDate":{"stdTransitDate":"","displayStdTransitDate":""},"pkgDimIn":"","pkgDimCm":"","returnedToShipperTrackNbr":"","commodityInfoList":[{"countryOfManufacture":"","harmonizedCode":"","description":""}],"statusActionDesc":"","destinationGeoCoordinate":null,"serviceCommitMessage":"The delivery date may be updated when FedEx receives the package.","serviceCommitMessageType":"SHIPMENT\u005fLABEL\u005fCREATED","lastUpdateDestinationAddress":{"streetLineList":[],"city":"IRVING","stateOrProvinceCode":"TX","postalCode":"","countryCode":"US","residential":false,"addressVerificationId":"9do4qtquxlmix0i558smkp0z","shareId":null,"addressClassification":null,"addressClassificationConfidence":null,"classification":null,"urbanizationCode":null,"countryName":"United States","geographicCoordinates":null,"processingParameters":null},"halAddressLocationId":null,"streetGeoCoordinate":null,"isIndirectSignatureReleaseEligible":false,"isRerouteEligible":false,"isRescheduleEligible":false,"coBrandedLogoLocation":null,"coBrandedLogoUrl":null,"coBrandedCouponLocation":null,"coBrandedCouponUrl":null,"coBrandedLogoAltTxt":null,"coBrandedCouponAltTxt":null,"brokerName":"","brokerCompanyName":"","buyerSoldToPartyName":"","buyerSoldToPartyCompanyName":"","importerOfRecordCompanyName":"","importerOfRecordName":"","consolidationDetails":null,"exclusionReasonDetails":null,"piecesPerShipment":"0","totalPiecesPerMPSShipment":"0","matched":false,"codrequired":false,"mpstype":"","fxfAdvanceNotice":true,"rthavailableCD":"","excepReasonListNoInit":[""],"excepActionListNoInit":[""],"statusDetailsListNoInit":["Please check back later for shipment status or subscribe for e\u002dmail notifications"],"isHistoricalEDTW":false,"isNonHistoricalEDTW":false,"isTargetedMsg":false,"isCommodityInfoAvail":false,"isStreetMapEligible":false,"isPending":false,"isMaskShipper":false,"isHalEligible":false,"isBeforePossessionStatus":true,"isFedexOfficeOnlineOrders":false,"isFedexOfficeInStoreOrders":false,"isMultipleStop":false,"isCustomCritical":false,"isNotFound":false,"isFreight":false,"isResidential":true,"isDestResidential":true,"isHALResidential":false,"isActualDelAddrResidential":false,"isReqEstDelDt":false,"isCDOEligible":false,"isMtchdByRecShrID":false,"isMtchdByShiprShrID":false,"isConsolidationDetail":false,"isExclusionReason":false,"isException":false,"isShipmentException":false,"isExpiring":false,"isExpired":false,"isPrePickup":true,"isPickup":false,"isInTransit":false,"isInProgress":true,"isDelException":false,"isClearanceDelay":false,"isDelivered":false,"isDroppedOff":false,"isHAL":false,"isOnSchedule":false,"isDeliveryToday":false,"isInFedExPossession":false,"isSave":false,"isWatch":false,"isHistorical":false,"isTenderedNotification":true,"isDeliveredNotification":true,"isExceptionNotification":true,"isCurrentStatusNotification":false,"isEstDelTmWindowLabel":false,"isAnticipatedShipDtLabel":true,"isShipPickupDtLabel":false,"isActualPickupLabel":false,"isOrderReceivedLabel":false,"isEstimatedDeliveryDtLabel":true,"isDeliveryDtLabel":false,"isActualDeliveryDtLabel":false,"isOrderCompleteLabel":false,"isTenderedDtLabel":false,"isShipDtLabel":false,"isOutboundDirection":false,"isInboundDirection":false,"isThirdpartyDirection":false,"isUnknownDirection":false,"isFSM":false,"isReturn":false,"isOriginalOutBound":false,"isChildPackage":false,"isParentPackage":false,"isReclassifiedAsSingleShipment":false,"isDuplicate":false,"isInvalid":false,"isSignatureThumbnailAvailable":false,"isSignatureAvailable":false,"isSpod":false,"isMPS":false,"isGMPS":false,"CDOInfoList":[{"spclInstructDesc":"","delivOptn":"","delivOptnStatus":"","reqApptWdw":"","reqApptDesc":"","rerouteTRKNbr":"","beginTm":"","endTm":""}],"CDOExists":false,"isEstimatedDeliveryDateChangeNotification":true,"isDocumentAvailable":false,"isCanceled":false,"errorList":[{"code":"","message":"","source":null,"rootCause":null}],"isSuccessful":true}],"alerts":null,"cxsalerts":null}}
  			
  			return Response;
  		}catch (Exception e){
  			//e.printStackTrace();
  			return null;
  		}
  	}

	public static String[][] getTrackpackages(String[][] Details, String trackingNumber, String trackingQualifier, String trackingCarrier) throws Exception {
		String TrackPackagesResponse = TrackingPackagesRequest(trackingNumber, trackingQualifier, trackingCarrier);
		
		Details = Arrays.copyOf(Details, Details.length + 1);
		Details[Details.length - 1] = new String[] {"TrackPackagesResponse", TrackPackagesResponse};
		
		if (Helper_Functions.isNullOrUndefined(TrackPackagesResponse) 
				|| !TrackPackagesResponse.contains("\"successful\":true")) {
			// call did not complete successfully
			return Details;
		}
		
		String Tracking_Details[][] = new String[][] {
			{"SHIP_DATE", "shipDt"}, // The "SHIP_DATE" value is hard coded to be used in calling method.
			{"DELIVERY_DATE", "displayEstDeliveryDateTime"},
			{"TRACKING_CARRIER", "trackingCarrierCd"},
			{"SERVICE", "serviceCD"},
			{"STATUS", "keyStatus"},
			{"isEstDelTmWindowLabel", "isEstDelTmWindowLabel"},
			{"estDelTmWindowStart", "estDelTmWindowStart"},
			{"isNonHistoricalEDTW", "isNonHistoricalEDTW"}
		};
		
		// Add the new element to the details array
		for (String[] NewElement: Tracking_Details) {
			if (!TrackPackagesResponse.contains(NewElement[1])) {
				throw new Exception("Identier " + NewElement[1] + " not present in TrackPackagesResponse.");
			}
			Details = Arrays.copyOf(Details, Details.length + 1);
			NewElement[1] = API_Functions.General_API_Calls.ParseStringValue(TrackPackagesResponse, NewElement[1]);
			Details[Details.length - 1] = NewElement;
		}
		
		if (trackingQualifier.contentEquals("")) {
			Details = Arrays.copyOf(Details, Details.length + 1);
			String[] NewElement = {"TRACKING_QUALIFIER", API_Functions.General_API_Calls.ParseStringValue(TrackPackagesResponse, "trackingQualifier")};
			Details[Details.length - 1] = NewElement;
		}
		
		return Details;
	}
}
