package SupportClasses;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.openqa.selenium.By;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

public class SharePoint{

	@BeforeClass
	public void beforeClass() {
		Environment.SetLevelsToTest("3");//filler value
	}
	
	@DataProvider
	public static Iterator<Object[]> dp() {
		List<Object[]> data = new ArrayList<Object[]>();
		String Version = "2140";
		String VersionMissionCritical = "3040";
		String Load = "May19DL";
		String ApplicationList[][] = new String[][]{
			//{"WCRV", "https://myfedex.sharepoint.com/teams/fedex-com-docs/fedex-com-WCRV/default.aspx?RootFolder=%2Fteams%2Ffedex%2Dcom%2Ddocs%2Ffedex%2Dcom%2DWCRV%2FShared%20Documents%2Ffedex%2Dcom%2DWCRV%2FRoot%20Folder&FolderCTID=0x012000FDFF41960BC8754FBF537F3625FEAD5D&View=%7B75B35A49%2DEB72%2D482C%2D9E59%2D2FA70CF41B23%7D"},
			//{"WRTT", "https://myfedex.sharepoint.com/teams/fedex-com-docs/fedex-com-RateTools/default.aspx?RootFolder=%2Fteams%2Ffedex%2Dcom%2Ddocs%2Ffedex%2Dcom%2DRateTools%2FShared%20Documents%2Ffedex%2Dcom%2DRateTools%2FRoot%20Folder&FolderCTID=0x01200007B93CFF5217244BBD8A93F7CCD56BD2&View=%7B45E8E2B4%2D4D6F%2D4309%2DBCF9%2D4532DD77B854%7D"},
			//{"WDPA", "https://myfedex.sharepoint.com/teams/fedex-com-docs/fedex-com-PickupService/default.aspx?RootFolder=%2Fteams%2Ffedex%2Dcom%2Ddocs%2Ffedex%2Dcom%2DPickupService%2FShared%20Documents%2Ffedex%2Dcom%2DPickupService%2FRoot%20Folder&FolderCTID=0x012000A2807C843B04624BBD8C000149F825BE&View=%7BB6A8E2F6%2D1C0D%2D468E%2DB683%2D080DB9240FDF%7D"},
			//{"OADR", "https://myfedex.sharepoint.com/teams/fedex-com-docs/fedex-com-OnlineAcctDscntReg/default.aspx?RootFolder=%2Fteams%2Ffedex%2Dcom%2Ddocs%2Ffedex%2Dcom%2DOnlineAcctDscntReg%2FShared%20Documents%2Ffedex%2Dcom%2DOnlineAcctDscntReg%2FRoot%20Folder&FolderCTID=0x0120005DF42B5D801B53429AB2C8687DD6AE8B&View=%7BC959F639%2D9D32%2D4EBB%2DBF50%2DB11C8F09522D%7D"},
			{"WPRL", "https://myfedex.sharepoint.com/teams/fedex-com-docs/fedex-com-CommonLoginClient/default.aspx?RootFolder=%2Fteams%2Ffedex%2Dcom%2Ddocs%2Ffedex%2Dcom%2DCommonLoginClient%2FShared%20Documents%2Ffedex%2Dcom%2DCommonLoginClient%2FRoot%20Folder&FolderCTID=0x012000D8A92FA4CE17D04B9DAD64EE7C638C18&View=%7BD0F45911%2D9E17%2D479F%2D9F16%2D2DED66F51F63%7D"},
			{"WFCL", "https://myfedex.sharepoint.com/teams/fedex-com-docs/fedex-com-CommonLoginClient/default.aspx?RootFolder=%2Fteams%2Ffedex%2Dcom%2Ddocs%2Ffedex%2Dcom%2DCommonLoginClient%2FShared%20Documents%2Ffedex%2Dcom%2DCommonLoginClient%2FRoot%20Folder&FolderCTID=0x012000D8A92FA4CE17D04B9DAD64EE7C638C18&View=%7BD0F45911%2D9E17%2D479F%2D9F16%2D2DED66F51F63%7D"},
			{"FCLA", "https://myfedex.sharepoint.com/teams/fedex-com-docs/fedex-com-FCL-API/default.aspx?RootFolder=%2Fteams%2Ffedex%2Dcom%2Ddocs%2Ffedex%2Dcom%2DFCL%2DAPI%2FShared%20Documents%2Ffedex%2Dcom%2DFCL%2DAPI%2FRoot%20Folder&FolderCTID=0x01200016F7C66666C3F3429B69EB04F319539F&View=%7B59040423%2D10E4%2D4A7C%2DB44E%2D34769129CAC7%7D"},
			{"WSTM", "https://myfedex.sharepoint.com/teams/fedex-com-docs/fedex-com-casiteminder/default.aspx"},
			{"WIDM", "https://myfedex.sharepoint.com/teams/fedex-com-docs/fedex-com-caidentitymanager/default.aspx?RootFolder=%2Fteams%2Ffedex%2Dcom%2Ddocs%2Ffedex%2Dcom%2DCAIdentityManager%2FShared%20Documents%2Ffedex%2Dcom%2DCAIdentityManager%2FRoot%20Folder&FolderCTID=0x0120009D70D6D1274D9E4DAF2C25919B3AF16B&View=%7BEA1BF171%2DD464%2D42FE%2D9DCB%2D1B07F53722EC%7D"},
			//{"ADAT", "https://myfedex.sharepoint.com/teams/fedex-com-docs/fedex-com-AdvAuthentication/default.aspx?RootFolder=%2Fteams%2Ffedex%2Dcom%2Ddocs%2Ffedex%2Dcom%2DAdvAuthentication%2FShared%20Documents%2Ffedex%2Dcom%2DAdvAuthentication%2FRoot%20Folder&FolderCTID=0x0120001FD7418AD688034FB2D18E0CF28C5132&View=%7BF9F35722%2D0D65%2D4DDD%2D8C04%2D49503A805088%7D"},
				};

		for (int i = 0; i < ApplicationList.length; i++) {
			if ("WFCL,FCLA,WSTM,WIDM,ADAT".contains(ApplicationList[i][0])) {
				data.add( new Object[] {ApplicationList[i][0] + VersionMissionCritical + " " + Load, ApplicationList[i][1]});
			}else {
				data.add( new Object[] {ApplicationList[i][0] + Version + " " + Load, ApplicationList[i][1]});
			}
			
		}
		return data.iterator();
	}
	
	@Test(dataProvider = "dp", enabled = true)
	public static void Add_Share_Point_File_Struct(String LoadTitle, String ApplicationURL){
		
		//Helper_Functions.PrintOut("breakpoint", false);
		try {
			WebDriver_Functions.ChangeURL(ApplicationURL, null, false);
			if (WebDriver_Functions.isPresent(By.name("loginfmt"))) {
				//not logged in, wait for user to enter correct details
				WebDriver_Functions.Type(By.name("loginfmt"), Helper_Functions.MyEmail);
				Helper_Functions.PrintOut("Please enter details and navigate to ladning page.");
				int Delay = 180;
				for(int i = 0; i < Delay; i++) {
					if (WebDriver_Functions.isPresent(By.id("DeltaPlaceHolderPageTitleInTitleArea"))) {
						i = Delay;
					}
					Helper_Functions.Wait(1);
				}
			}
			
			//assumed logged in at this point.
			//click the new button
			WebDriver_Functions.Click(By.id("QCB1_Button1"));
			//click to create a new folder
			WebDriver_Functions.Click(By.id("js-newdocWOPI-divFolder-txt-WPQ2"));
			//enter the name of the folder and click create
			WebDriver_Functions.Type(By.id("ccfc_folderNameInput_0_onetidIOFile"), LoadTitle);
			WebDriver_Functions.Click(By.id("csfd_createButton_toolBarTbl_RightRptControls_diidIOSaveItem"));
			if (!WebDriver_Functions.isPresent(By.linkText(LoadTitle))) {
				WebDriver_Functions.Click(By.id("WPQ2_ListTitleViewSelectorMenu_Container_overflow"));
				WebDriver_Functions.Click(By.id("ID_OverflowOption_0"));
				
			}
			
			
			///STOPPED, not sure if this is work automating, the login flow alone takes way to many steps.
			
		} catch (Exception e) {
			Assert.fail("Cannot retrieve details");
		}
	}
}
