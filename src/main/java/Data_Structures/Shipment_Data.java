package Data_Structures;

import java.util.ArrayList;
import java.util.Arrays;

import SupportClasses.Environment;
import SupportClasses.Helper_Functions;

public class Shipment_Data {

	public String Ship_Date = "";
	public String DELIVERY_DATE = "";
	public String Tracking_Number = "";
	public String TRACKING_CARRIER = "";
	public String TRACKING_QUALIFIER = "";
	public String Service = "";
	public User_Data User_Info = new User_Data();
	public String Status = "";
	public String Account_Number = "";
	public String Account_Key = "";
	public Address_Data Origin_Address_Info = new Address_Data();
	public Address_Data Destination_Address_Info = new Address_Data();
	
	//The WCDO options
	public boolean RESCHEDULE = false;	
	public boolean REROUTE = false;	
	public boolean REDIRECT_HOLD_AT_LOCATION = false;	
	public boolean SIGNATURE_RELEASE = false;	
	public boolean DELIVERY_INSTRUCTIONS = false;	
	public boolean DELIVERY_SUSPENSIONS = false;
	
	public Shipment_Data() {
	}
	
	public boolean PrintOutShipmentDetails() {
		String Output[] = new String[] {this.Tracking_Number, this.User_Info.USER_ID, this.Service, Address_Data.Address_String(Origin_Address_Info), Address_Data.Address_String(Destination_Address_Info)};
		Helper_Functions.PrintOut(Arrays.toString(Output));
		return true;
	}
	
	public boolean setShipDate(String ShipDate) {
		this.Ship_Date = ShipDate;
		return true;
	}
	
	public boolean setEstDeliveryDate(String EstDeliveryDate) {
		this.DELIVERY_DATE = EstDeliveryDate;
		return true;
	}
	
	public boolean setAccount_Number(String Account_Number) {
		this.Account_Number = Account_Number;
		return true;
	}

	public boolean setAccount_Key(String Account_Key) {
		this.Account_Key = Account_Key;
		return true;
	}

	public boolean setUser_Info(User_Data User_Info) {
		this.User_Info = User_Info;
		return true;
	}
	
	public boolean setOrigin_Address_Info(Address_Data Origin_Address_Info) {
		this.Origin_Address_Info = Origin_Address_Info;
		return true;
	}
	
	public boolean setDestination_Address_Info(Address_Data Destination_Address_Info) {
		this.Destination_Address_Info = Destination_Address_Info;
		return true;
	}

	public boolean setTRACKING_CARRIER(String TRACKING_CARRIER) {
		this.TRACKING_CARRIER = TRACKING_CARRIER;
		return true;
	}
	
	public boolean setTRACKING_QUALIFIER(String TRACKING_QUALIFIER) {
		this.TRACKING_QUALIFIER = TRACKING_QUALIFIER;
		return true;
	}
	

	public boolean setService(String Service) {
		this.Service = Service;
		return true;
	}

	public boolean setStatus(String Status) {
		this.Status = Status;
		return true;
	}

	public boolean setTracking_Number(String Tracking_Number) {
		this.Tracking_Number = Tracking_Number;
		return true;
	}
	
	public boolean writeShipment_Data_To_Excel(boolean newShipment) {
		String Data[][] = new String[][] {
			{"SHIP_DATE", this.Ship_Date}, 
			{"DELIVERY_DATE", this.DELIVERY_DATE},
			{"TRACKING_NUMBER", this.Tracking_Number}, // hard coded below
			{"TRACKING_QUALIFIER", this.TRACKING_QUALIFIER}, // hard coded below
			{"TRACKING_CARRIER", this.TRACKING_CARRIER}, 
			{"SERVICE", this.Service},
			{"USER_ID", this.User_Info.USER_ID},
			{"PASSWORD", this.User_Info.PASSWORD},
			{"STATUS", this.Status},
			{"RESCHEDULE", Boolean.toString(this.RESCHEDULE)},
			{"REROUTE", Boolean.toString(this.REROUTE)},
			{"REDIRECT_HOLD_AT_LOCATION", Boolean.toString(this.REDIRECT_HOLD_AT_LOCATION)},
			{"SIGNATURE_RELEASE", Boolean.toString(this.SIGNATURE_RELEASE)},
			{"DELIVERY_INSTRUCTIONS", Boolean.toString(this.DELIVERY_INSTRUCTIONS)},
			{"DELIVERY_SUSPENSIONS", Boolean.toString(this.DELIVERY_SUSPENSIONS)}
		};

		int IdentifierColumn = -1;
		if (!newShipment) {
			if (!this.TRACKING_QUALIFIER.contentEquals("")) {
				//identify by the tracking qualifier 
				IdentifierColumn = 3;
			}else {
				//identify by the tracking number 
				IdentifierColumn = 2;
			}
		}
		
		try {
			String Level = Environment.getInstance().getLevel();
			boolean File_Updated = Helper_Functions.WriteToExcel(Helper_Functions.DataDirectory + "\\TrackingNumbers.xls", "L" + Level, Data, IdentifierColumn);
			
			//if the file could not be updated by tracking qualifier then try updating by tracking number.
			if(!File_Updated && IdentifierColumn == 3) {
				File_Updated = Helper_Functions.WriteToExcel(Helper_Functions.DataDirectory + "\\TrackingNumbers.xls", "L" + Level, Data, 2);
			}
			
			return File_Updated;
		}catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}
	
	//will return all credit cards for the environment
	public static Shipment_Data[] getTrackingDetails(String Level) {
		//if the data is already loaded then return the values
		ArrayList<String[]> Tracking_Details = new ArrayList<String[]>();
		
		Tracking_Details = Helper_Functions.getExcelData(Helper_Functions.DataDirectory + "\\TrackingNumbers.xls",  "L" + Level);//load the relevant information from excel file.
		
		Shipment_Data Shipment_Info_Array[] = new Shipment_Data[Tracking_Details.size() - 1];
		String Headers[] = Tracking_Details.get(0);
		for (int i = 1; i < Tracking_Details.size(); i++) {
			String Row[] = Tracking_Details.get(i);

			Shipment_Info_Array[i -1] = new Shipment_Data();
			Shipment_Info_Array[i -1].User_Info = new User_Data();
			for (int j = 0; j <Headers.length; j++) {
				int pos = i - 1;
				switch (Headers[j]) {
				case "SHIP_DATE":
					Shipment_Info_Array[pos].Ship_Date = Row[j];
		  			break;
				case "DELIVERY_DATE":
					Shipment_Info_Array[pos].DELIVERY_DATE = Row[j];
		  			break;
				case "TRACKING_NUMBER":
					Shipment_Info_Array[pos].Tracking_Number = Row[j];
		  			break;
				case "TRACKING_QUALIFIER":
					Shipment_Info_Array[pos].TRACKING_QUALIFIER = Row[j];
		  			break;
				case "TRACKING_CARRIER":
					Shipment_Info_Array[pos].TRACKING_CARRIER = Row[j];
		  			break;
				case "SERVICE":
					Shipment_Info_Array[pos].Service = Row[j];
		  			break;
				case "USER_ID":
					Shipment_Info_Array[pos].User_Info.USER_ID = Row[j];
		  			break;
				case "PASSWORD":
					Shipment_Info_Array[pos].User_Info.PASSWORD = Row[j];
		  			break;
		  		case "STATUS":
		  			Shipment_Info_Array[pos].Status = Row[j];
		  			break;
		  		case "RESCHEDULE":
		  			Shipment_Info_Array[pos].RESCHEDULE = Boolean.parseBoolean(Row[j]);
		  			break;
		  		case "REROUTE":
		  			Shipment_Info_Array[pos].REROUTE = Boolean.parseBoolean(Row[j]);
		  			break;
		  		case "REDIRECT_HOLD_AT_LOCATION":
		  			Shipment_Info_Array[pos].REDIRECT_HOLD_AT_LOCATION = Boolean.parseBoolean(Row[j]);
		  			break;
		  		case "SIGNATURE_RELEASE":
		  			Shipment_Info_Array[pos].SIGNATURE_RELEASE = Boolean.parseBoolean(Row[j]);
		  			break;
		  		case "DELIVERY_INSTRUCTIONS":
		  			Shipment_Info_Array[pos].DELIVERY_INSTRUCTIONS = Boolean.parseBoolean(Row[j]);
		  			break;
		  		case "DELIVERY_SUSPENSIONS":
		  			Shipment_Info_Array[pos].DELIVERY_SUSPENSIONS = Boolean.parseBoolean(Row[j]);
		  			break;
				}//end switch
			}
		}
		
  		return Shipment_Info_Array;
	}
}
