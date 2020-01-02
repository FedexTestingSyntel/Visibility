package MFAC_Application;

import java.util.ArrayList;
import java.util.Arrays;

import API_Functions.General_API_Calls;
import SupportClasses.Environment;
import SupportClasses.Helper_Functions;

public class MFAC_Data {
	public String OAuth_Token_Client_ID = "";
	public String OAuth_Token_Client_Secret = "";
	public String OAuth_Token = "";
	public String Level = "";
	public String AIssueURL = "";
	public String AVerifyURL;
	public String AVelocityURL = "";
	public String DIssueURL = "";
	public String DVerifyURL = "";
	public String DVelocityURL = "";
	public String OrgPostcard = "FDM-POSTCARD-PIN";
	public String OrgPhone = "FDM-PHONE-PIN";
	public int PinVelocityThresholdPostcard = 0;
	public int PinVelocityThresholdPhone = 0;
	public int AddressVelocityThreshold = 0;
	
	private static String Prod_Client_ID = "";
	private static String Prod_Client_Secret = "";

	//Stores the data for each individual level
	public static MFAC_Data DataClass[] = new MFAC_Data[8];
	
	public static MFAC_Data LoadVariables(){
		String Level  = Environment.getInstance().getLevel();
		int intLevel = Integer.parseInt(Level);
		//if the level details were already loaded then return detail.
		if (DataClass[intLevel] != null) {
			return DataClass[intLevel];
		}
		
		//since the level details have not been loaded load them.
		MFAC_Data DC = new MFAC_Data();
		DC.Level = Level;
		
		String LevelIdentifier[] = null;
  		switch (Level) {
  		case "1":
  			LevelIdentifier = new String[] {"", "http://mfacbase-cos-vip.test.cloUser_Info.fedex.com:9090"}; break;
  		case "2":
  			LevelIdentifier = new String[] {"", "http://mfacdev-cos-vip.test.cloUser_Info.fedex.com:9090"}; break;
  		case "3":
  			LevelIdentifier = new String[] {"", "http://mfacdrt-cos-vip.test.cloUser_Info.fedex.com:9090"}; break;
  		case "4":
  			LevelIdentifier = new String[] {"", "http://mfacstress-cos-vip.test.cloUser_Info.fedex.com:9090"}; break;
  		case "5":
  			LevelIdentifier = new String[] {"", "http://mfacbit-cos-vip.test.cloUser_Info.fedex.com:9090"}; break;
  		case "6":
  			//L6 is not valid for direct URL
  			LevelIdentifier = new String[] {"", ""}; break;
  		case "7":
  			//L7 is not valid for direct URL
  			LevelIdentifier = new String[] {"", ""}; break;
		}
  		LevelIdentifier[0] = General_API_Calls.getAPILevelIedntifier(Level, false);
  		
  		//Load the API URLs
  		if (Level != "1") {//API not applicable to L1
  			DC.AIssueURL = LevelIdentifier[0] + "/security/v1/pin";
  			DC.AVerifyURL = LevelIdentifier[0] + "/security/v1/pin/verify";
			DC.AVelocityURL = LevelIdentifier[0] + "/security/v1/addresses/velocitycheck";
  		}
		
  		//Load the Direct URLs
		if (Level != "6" && Level != "7") {//direct not applicable for L6 and Prod
			DC.DIssueURL = LevelIdentifier[1] + "/mfac/v3/issuePIN";
			DC.DVerifyURL = LevelIdentifier[1] + "/mfac/v3/verifyPIN";
			DC.DVelocityURL = LevelIdentifier[1] + "/mfac/v3/addressVelocityCheck";
		}
		
		switch (Level) { //Based on the method that is being called the array list will be populated. This will make the TestNG Pass/Fail results more relevant.
		case "1":
			DC.OAuth_Token_Client_ID = "l7xx1892f99a6f88470ba29abc141cd7bd8d";
			DC.OAuth_Token_Client_Secret ="a4325d011acf4876b3fe3206931b8f5a";
			DC.PinVelocityThresholdPostcard = 3;
			DC.PinVelocityThresholdPhone = 3;
			DC.AddressVelocityThreshold = 10;
			break;
		case "2":
			DC.OAuth_Token_Client_ID = "l7xx1892f99a6f88470ba29abc141cd7bd8d";
			DC.OAuth_Token_Client_Secret ="a4325d011acf4876b3fe3206931b8f5a";
			DC.PinVelocityThresholdPostcard = 3;
			DC.PinVelocityThresholdPhone = 3;
			DC.AddressVelocityThreshold = 10;
			break;
		case "3":
			DC.OAuth_Token_Client_ID = "l7xx1892f99a6f88470ba29abc141cd7bd8d";
			DC.OAuth_Token_Client_Secret ="a4325d011acf4876b3fe3206931b8f5a";
			DC.PinVelocityThresholdPostcard = 3;
			DC.PinVelocityThresholdPhone = 3;
			DC.AddressVelocityThreshold = 10;
			break;
		case "4":
			DC.OAuth_Token_Client_ID = "l7xx3fb9480460af4b55a542a6d1e6d1f5f2";
			DC.OAuth_Token_Client_Secret ="9355487be3c74da696517393ecede89f";
			DC.PinVelocityThresholdPostcard = 3;
			DC.PinVelocityThresholdPhone = 500;
			DC.AddressVelocityThreshold = 10;
			break;
		case "5"://havn't used L5
			DC.OAuth_Token_Client_ID = "";
			DC.OAuth_Token_Client_Secret ="";
			DC.PinVelocityThresholdPostcard = 3;
			DC.PinVelocityThresholdPhone = 3;
			DC.AddressVelocityThreshold = 10;
			break;
		case "6"://need to update these values manually, do not share
			DC.OAuth_Token_Client_ID = "l7xx55c591482e9c47bfb0556561cb9e8cfa";
			DC.OAuth_Token_Client_Secret ="0c33a18ff14c4bc7bc4880ef30067c52";
			DC.PinVelocityThresholdPostcard = 3;
			DC.PinVelocityThresholdPhone = 3;
			DC.AddressVelocityThreshold = 10;
			break;
		case "7"://need to update these values manually, do not share
			Load_Prod_Data();
			DC.OAuth_Token_Client_ID = Prod_Client_ID;
			DC.OAuth_Token_Client_Secret = Prod_Client_Secret;
			DC.PinVelocityThresholdPostcard = 3;
			DC.PinVelocityThresholdPhone = 3;
			DC.AddressVelocityThreshold = 10;
			break;
		}
		//generate the OAuthToken, please note that this is not valid on L1 as API calls cannot be used on that level
		if (!Level.contentEquals("1")) {
			DC.OAuth_Token = General_API_Calls.getAuthToken(DC.OAuth_Token_Client_ID , DC.OAuth_Token_Client_Secret);
			Helper_Functions.PrintOut("L" + Level + " MFAC BearerToken generated: " + DC.OAuth_Token);
			
		}
		
		DataClass[intLevel] = DC;
		
		return DC;
	}

	public static void Load_Prod_Data() {
		ArrayList<String[]> Excel_Data = Helper_Functions.getExcelData(Helper_Functions.DataDirectory + "\\MFAC_Properties.xls",  "MFAC");//load the relevant information from excel file.
		int ExcelRow = 7;//the rows will correspond to the correct level. With the row 0 being the column titles.
		//below is each column that is expected in the excel and will be loaded.    08/24/18
		//OAuthToken (Will be populated within the class)	Level	OAuthToken_URL	Client_ID	Client_Secret	IssuePin_APIGURL	VerifyPin_APIGURL	Velocity_APIGURL	IssuePin_DirectURL	VerifyPin_DirectURL	Velocity_DirectURL	Pin_Velocity_PostCard	Pin_Velocity_Phone	Address_Velocity
		String EnvironmentInformation[] = Excel_Data.get(ExcelRow);

		Prod_Client_ID = EnvironmentInformation[3];
		Prod_Client_Secret = EnvironmentInformation[4];
		Helper_Functions.PrintOut(Arrays.toString(EnvironmentInformation), false);//print out all of the urls and date for the level, this is just a reference point to executer
	}
}
