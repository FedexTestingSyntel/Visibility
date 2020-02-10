package Data_Structures;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import SupportClasses.Environment;
import SupportClasses.Helper_Functions;

public class User_Data {

	public String ERROR;
	public String UUID_NBR;
	public String USER_ID;
	public String PASSWORD;
	public String SECRET_QUESTION_DESC;
	public String SECRET_ANSWER_DESC;
	
	public String FIRST_NM;
	public String MIDDLE_NM;
	public String LAST_NM;
	public String PHONE_NUMBER;
	public String EMAIL_ADDRESS;
	
	public Address_Data Address_Info = new Address_Data();
/*	public String Address_Line_1 = "";
	public String Address_Line_2 = "";
	public String City = "";
	public String State = "";
	public String State_Code = "";
	public String PostalCode = "";
	public String Country_Code = "";
	public String Region = "";
	public String Country = "";
	public String Share_Id = "";
	public String Residential = ""*/
	
	public String ACCOUNT_NUMBERS;
	public String ACCOUNT_NUMBER_DETAILS[][];
	//ACCOUNT_NUMBER_DETAILS will have the {key, value} of the account number.
	
	public String FSM_ENABLED;
	public String WDPA_ENABLED;
	public String GFBO_ENABLED;
	public String WGRT_ENABLED;
	public String PASSKEY;
	public String MIGRATION_STATUS;
	public String USER_TYPE;
	
	public String FDM_STATUS;

	public boolean IS_LARGE_USER_TYPE;
	public boolean NEEDS_TO_ACCEPT_TERMS;
	public String REQUEST_RUN_DATE;
	public String TOTAL_NUMBER_OF_SHIPMENTS;
	
	public User_Data() {
		//generic constructor
		this.PASSWORD = Helper_Functions.myPassword;
		this.SECRET_QUESTION_DESC = "SP2Q1";//ask for mothers name by default
		this.SECRET_ANSWER_DESC = "mom";
		this.EMAIL_ADDRESS = Helper_Functions.MyEmail;
	}
	public User_Data(boolean blank) {
	}
	
	public static User_Data Set_Dummy_Contact_Name(User_Data User_Info, String BaseValue, String Level) {
		String ContactName[] = Helper_Functions.LoadDummyName(BaseValue, Level);
		User_Info.FIRST_NM = ContactName[0];
		User_Info.LAST_NM = ContactName[2];
		return User_Info;
	}
	
	public static User_Data Print_High_Level_Details(User_Data User_Info) {
		String Details[] = new String[] {User_Info.USER_ID, User_Info.PASSWORD, User_Info.UUID_NBR, User_Info.FIRST_NM, User_Info.MIDDLE_NM, User_Info.LAST_NM};
		Helper_Functions.PrintOut(Arrays.toString(Details));
		return User_Info;
	}
	
	public static User_Data Print_Full_Details(User_Data User_Info) {
		String Details[] = new String[] {User_Info.USER_ID, User_Info.PASSWORD, User_Info.UUID_NBR, User_Info.FIRST_NM, User_Info.MIDDLE_NM, User_Info.LAST_NM, User_Info.MIGRATION_STATUS, User_Info.USER_TYPE};
		Helper_Functions.PrintOut("User_Data: " + Arrays.toString(Details));
		return User_Info;
	}
	
	public static User_Data Set_Same_Account_Data(User_Data User_Info, Account_Data Account_Info) {
		User_Info.Address_Info.City = Account_Info.Billing_Address_Info.City;
		User_Info.Address_Info.Country_Code = Account_Info.Billing_Address_Info.Country_Code;
		User_Info.Address_Info.PostalCode = Account_Info.Billing_Address_Info.PostalCode ;
		User_Info.Address_Info.State_Code = Account_Info.Billing_Address_Info.State_Code ;
		User_Info.Address_Info.Address_Line_1 = Account_Info.Billing_Address_Info.Address_Line_1 ;
		User_Info.Address_Info.Address_Line_2 = Account_Info.Billing_Address_Info.Address_Line_2 ;	
		return User_Info;
	}
	
	public static User_Data Set_Generic_Address(User_Data User_Info, String Country_Code) {
		//will load a dummy address from the given country then apply the same to the User_Data.
		String Level = Environment.getInstance().getLevel();
		Address_Data Address_Info = Address_Data.getAddress(Level, "US", null);
		User_Info.Address_Info = Address_Info;
		return User_Info;
	}
	
	public static User_Data Set_User_Id(User_Data User_Info, String Base) {
		User_Info.USER_ID = Helper_Functions.LoadUserID(Base);
		return User_Info;	
	}
	
	public static String[][] Get_User_Data_String_Array(User_Data User_Info) {
		String Data[][] = new String[][] {{"UUID_NBR", User_Info.UUID_NBR}, 
			{"USER_ID", User_Info.USER_ID}, 
			{"PASSWORD", User_Info.PASSWORD}, 
			{"SECRET_QUESTION_DESC", User_Info.SECRET_QUESTION_DESC}, 
			{"SECRET_ANSWER_DESC", User_Info.SECRET_ANSWER_DESC}, 
			{"FIRST_NM", User_Info.FIRST_NM}, 
			{"LAST_NM", User_Info.LAST_NM}, 
			{"EMAIL_ADDRESS", User_Info.EMAIL_ADDRESS}, 
			{"STREET_DESC", User_Info.Address_Info.Address_Line_1}, 
			{"STREET_DESC_TWO", User_Info.Address_Info.Address_Line_2}, 
			{"CITY_NM", User_Info.Address_Info.City}, 
			{"STATE_CD", User_Info.Address_Info.State_Code}, 
			{"POSTAL_CD", User_Info.Address_Info.PostalCode}, 
			{"COUNTRY_CD", User_Info.Address_Info.Country_Code}, };
		return Data;	
	}
	
	public static User_Data[] Get_UserIds(int intLevel) {
		return Get_UserIds( intLevel, false);
	}
	//will load the userids into the data class even if the rows have been changed.
	public static User_Data[] Get_UserIds(int intLevel, boolean returnAllUsers) {
		List<String[]> FullDataFromExcel = new ArrayList<String[]>();
		FullDataFromExcel = Helper_Functions.getExcelData(Helper_Functions.TestingData, "L" + intLevel);
		//a list of the Userids
		User_Data userInfoArray[] = new User_Data[FullDataFromExcel.size() - 1];
		
		String Headers[] = FullDataFromExcel.get(0);
		
		for (int i = 1; i < FullDataFromExcel.size(); i++) {
			String Row[] = FullDataFromExcel.get(i);
			userInfoArray[i - 1] = new User_Data(false); 
			int pos = i - 1;
			
			for (int j = 0; j <Headers.length; j++) {
				String CellValue = Row[j];
				switch (Headers[j]) {
		  		case "ERROR":
		  			userInfoArray[pos].ERROR = CellValue;
					break;	
		  		case "UUID_NBR":
					userInfoArray[pos].UUID_NBR = CellValue;
					break;
		  		case "USER_ID":
		  			userInfoArray[pos].USER_ID = CellValue;
					break;
		  		case "PASSWORD":
		  			userInfoArray[pos].PASSWORD = CellValue;
					break;
		  		case "SECRET_QUESTION_DESC":
		  			userInfoArray[pos].SECRET_QUESTION_DESC = CellValue;
					break;
		  		case "SECRET_ANSWER_DESC":
		  			userInfoArray[pos].SECRET_ANSWER_DESC = CellValue;
					break;
		  		case "FIRST_NM":
		  			userInfoArray[pos].FIRST_NM = CellValue;
					break;
		  		case "MIDDLE_NM":
		  			userInfoArray[pos].MIDDLE_NM = CellValue;
					break;
		  		case "LAST_NM":
		  			userInfoArray[pos].LAST_NM = CellValue;
					break;
		  		case "PHONE_NUMBER":
		  			userInfoArray[pos].PHONE_NUMBER = CellValue;
		  			userInfoArray[pos].Address_Info.Phone_Number = CellValue;
					break;
		  		case "EMAIL_ADDRESS":
		  			userInfoArray[pos].EMAIL_ADDRESS = CellValue;
					break;
		  		case "STREET_DESC":
		  			userInfoArray[pos].Address_Info.Address_Line_1 = CellValue;
					break;
		  		case "STREET_DESC_TWO":
		  			userInfoArray[pos].Address_Info.Address_Line_2 = CellValue;
					break;
		  		case "CITY_NM":
		  			userInfoArray[pos].Address_Info.City = CellValue;
					break;
		  		case "STATE_CD":
		  			userInfoArray[pos].Address_Info.State_Code = CellValue;
					break;
		  		case "POSTAL_CD":
		  			userInfoArray[pos].Address_Info.PostalCode = CellValue;
					break;
		  		case "COUNTRY_CD":
		  			userInfoArray[pos].Address_Info.Country_Code = CellValue;
					break;
		  		case "RESIDENTIAL":
		  			userInfoArray[pos].Address_Info.Residential = CellValue;
					break;
		  		case "ACCOUNT_NUMBERS":
		  			userInfoArray[pos].ACCOUNT_NUMBERS = CellValue;
					break;
		  		case "FSM_ENABLED":
		  			userInfoArray[pos].FSM_ENABLED = CellValue;
					break;	
		  		case "WDPA_ENABLED":
		  			userInfoArray[pos].WDPA_ENABLED = CellValue;
					break;
		  		case "GFBO_ENABLED":
		  			userInfoArray[pos].GFBO_ENABLED = CellValue;
					break;	
		  		case "WGRT_ENABLED":
		  			userInfoArray[pos].WGRT_ENABLED = CellValue;
					break;	
		  		case "PASSKEY":
		  			userInfoArray[pos].PASSKEY = CellValue;
					break;
		  		case "MIGRATION_STATUS":
		  			userInfoArray[pos].MIGRATION_STATUS = CellValue;
					break;	
		  		case "USER_TYPE":
		  			userInfoArray[pos].USER_TYPE = CellValue;
					break;
		  		case "FDM_STATUS":
		  			userInfoArray[pos].FDM_STATUS = CellValue;
					break;	
		  		case "IS_LARGE_USER_TYPE":
		  			userInfoArray[pos].IS_LARGE_USER_TYPE = Boolean.parseBoolean(CellValue);
					break;	
		  		case "NEEDS_TO_ACCEPT_TERMS":
		  			userInfoArray[pos].NEEDS_TO_ACCEPT_TERMS = Boolean.parseBoolean(CellValue);
					break;
		  		case "REQUEST_RUN_DATE":
		  			userInfoArray[pos].REQUEST_RUN_DATE = CellValue;
					break;	
		  		case "TOTAL_NUMBER_OF_SHIPMENTS":
		  			userInfoArray[pos].TOTAL_NUMBER_OF_SHIPMENTS = CellValue;
					break;
				}//end switch
			}
			
			if (userInfoArray[pos].ACCOUNT_NUMBERS != "") {
				 //EX   {"key":"0458136f8ca69488cce23d8b4216cacc","value":""}{"key":"d78e3b4ac25591d933221f06555d9c21","value":""}
				String tempACCOUNT_NUMBERS = userInfoArray[pos].ACCOUNT_NUMBERS;
				while(tempACCOUNT_NUMBERS != null && tempACCOUNT_NUMBERS.contains("key")){
					String key = API_Functions.General_API_Calls.ParseStringValue(tempACCOUNT_NUMBERS, "key");
					String value = API_Functions.General_API_Calls.ParseStringValue(tempACCOUNT_NUMBERS, "value");
					tempACCOUNT_NUMBERS = tempACCOUNT_NUMBERS.replaceFirst(key, "");
					tempACCOUNT_NUMBERS = tempACCOUNT_NUMBERS.replaceFirst(value, "");
					tempACCOUNT_NUMBERS = tempACCOUNT_NUMBERS.replace("{\"key\":\"\",\"value\":\"\"", "");
					
					if (!value.contentEquals("")) {
						if (userInfoArray[pos].ACCOUNT_NUMBER_DETAILS == null) {
							userInfoArray[pos].ACCOUNT_NUMBER_DETAILS = new String[1][];
						}else {
							userInfoArray[pos].ACCOUNT_NUMBER_DETAILS = Arrays.copyOf(userInfoArray[pos].ACCOUNT_NUMBER_DETAILS, userInfoArray[pos].ACCOUNT_NUMBER_DETAILS.length + 1);
						}
						String NewElement[] = new String[] {key, value};
						userInfoArray[pos].ACCOUNT_NUMBER_DETAILS[userInfoArray[pos].ACCOUNT_NUMBER_DETAILS.length - 1] = NewElement;
					}
					
				}
			}
			
			
			
			if (userInfoArray[i - 1].USER_ID == null || userInfoArray[i - 1].USER_ID.contentEquals("")) {
				userInfoArray[i - 1].ERROR = "ERROR";
			}
		}
		
		for (int index = 0; index < userInfoArray.length; index++) {
			if (!returnAllUsers && !userInfoArray[index].ERROR.contentEquals("")) {
					userInfoArray = removeTheElement(userInfoArray, index);
				}
			// Added to remove the EAN users with the Void First name issue.
			else if (userInfoArray[index].Address_Info.Country_Code.contentEquals("..")) {
				userInfoArray = removeTheElement(userInfoArray, index);
			}
		}

		return userInfoArray;
	}
	
	public static User_Data[] removeTheElement(User_Data[] userInfoArray,  int index) {

		// If the array is empty 
		// or the index is not in array range 
		// return the original array 
		if (userInfoArray == null || index < 0 || index >= userInfoArray.length) { 
			return userInfoArray; 
		} 
		
		 // Create another array of size one less 
		User_Data[] anotherArray = new User_Data[userInfoArray.length - 1]; 
 
       // Copy the elements from starting till index 
       // from original array to the other array 
       System.arraycopy(userInfoArray, 0, anotherArray, 0, index); 
 
       // Copy the elements from index + 1 till end 
       // from original array to the other array 
       System.arraycopy(userInfoArray, index + 1, 
                        anotherArray, index, 
                        userInfoArray.length - index - 1);

       // return the resultant array 
       return anotherArray; 
    }
	
	public boolean getATRKStatus() {
		// if user needs to accept terms and conditions not ATRK enabled
		return !this.NEEDS_TO_ACCEPT_TERMS;
	}
	
	public boolean getCanScheduleShipment() {
		boolean flag = false;
		if (this.FSM_ENABLED != null && this.FSM_ENABLED.contentEquals("USER")) {
			flag = true;
		}
		// Helper_Functions.PrintOut("getCanScheduleShipment: " + flag);
		return flag;
	}
	
	public boolean getHasValidAccountNumber() {
		boolean flag = false;
		if (this.ACCOUNT_NUMBER_DETAILS != null) {
			flag = true;
		}
		// Helper_Functions.PrintOut("getHasValidAccountNumber: " + flag);
		return flag;
	}
	
	public Address_Data getFDMregisteredAddress() {
		Address_Data AD = null;;
		if (this.FDM_STATUS != null && this.FDM_STATUS.contains("contactAndAddress") && this.FDM_STATUS.contains("contactAndAddress")) {
			AD =  USRC.recipient_profile.getFirstFDMAddress(this.FDM_STATUS);
			// Helper_Functions.PrintOut("getFDMregisteredAddress: " + AD.Address_Line_1);
		}
		return AD;
	}
	
	public boolean writeUserToExcel() {
		String FileName = Helper_Functions.DataDirectory + "\\TestingData.xls";
		String Level = Environment.getInstance().getLevel();
		String Details[][] = new String[][]{{"USER_ID", this.USER_ID}, {"PASSWORD", this.PASSWORD}};
		int keyPosition = 0;
		boolean updatefile = Helper_Functions.WriteToExcel(FileName, "L" + Level, Details, keyPosition);
		return updatefile;
	}
	
}

