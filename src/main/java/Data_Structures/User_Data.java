package Data_Structures;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.IntStream;

import SupportClasses.Environment;
import SupportClasses.Helper_Functions;

public class User_Data {
	public String Level = "";
	public String UUID_NBR = "";
	public String USER_ID = "";
	
	public String PASSWORD = "";
	public String SECRET_QUESTION_DESC = "";
	public String SECRET_ANSWER_DESC = "";
	
	public String FIRST_NM = "";
	public String MIDDLE_NM = "";
	public String LAST_NM = "";
	public String EMAIL_ADDRESS = "";
	
	public Address_Data Address_Info = new Address_Data();
	
	public String ACCOUNT_NUMBER = "";
	public String ATRK_ENABLED = "";
	public String WCRV_ENABLED = "";
	public String GFBO_ENABLED = "";
	public String WGRT_ENABLED = "";
	public String WDPA_ENABLED = "";
	public String GROUND_ENABLED = "";
	public String EXPRESS_ENABLED = "";
	public String FREIGHT_ENABLED = "";
	public String PASSKEY = "";
	public String FDM_STATUS = "";
	public String USER_TYPE = "";
	public String MIGRATION_STATUS = "";
	
	public String PH_INTL_CALL_PREFIX_CD = "1";//assumed 1
	public String PHONE = "";
	public String MOBL_INTL_CALL_PREFIX_CD = "";
	public String MOBILE_PHONE = "";
	public String FAX_INTL_CALL_PREFIX_CD = "";
	public String FAX_NUMBER = "";
	
	public String ERROR = "";
	
	public String COMPANY_NAME = "";
	
	public User_Data() {
		//generic constructor
		this.PASSWORD = Helper_Functions.myPassword;
		this.SECRET_QUESTION_DESC = "SP2Q1";//ask for mothers name by default
		this.SECRET_ANSWER_DESC = "mom";
		this.EMAIL_ADDRESS = Helper_Functions.MyEmail;
		this.PHONE = Helper_Functions.myPhone;
	}
	
	public static User_Data Set_Dummy_Contact_Name(User_Data User_Info, String BaseValue, String Level) {
		String ContactName[] = Helper_Functions.LoadDummyName(BaseValue, Level);
		User_Info.FIRST_NM = ContactName[0];
		User_Info.LAST_NM = ContactName[2];
		return User_Info;
	}
	
	public static User_Data Set_Dummy_Phone_Number(User_Data User_Info) {
		String ContactDetails[][] = Helper_Functions.LoadPhone_Mobile_Fax_Email(User_Info.Address_Info.Country_Code);
		User_Info.PH_INTL_CALL_PREFIX_CD = ContactDetails[0][0];
		User_Info.PHONE = ContactDetails[0][1];
		User_Info.FAX_INTL_CALL_PREFIX_CD = ContactDetails[0][0];
		User_Info.FAX_NUMBER = ContactDetails[0][1];
		User_Info.MOBL_INTL_CALL_PREFIX_CD = ContactDetails[0][0];
		User_Info.MOBILE_PHONE = ContactDetails[0][1];
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
		User_Info.Level = Environment.getInstance().getLevel();
		Address_Data Address_Info = Address_Data.getAddress(User_Info.Level, "US", null);
		User_Info.Address_Info = Address_Info;
		return User_Info;
	}
	
	public static User_Data Set_User_Id(User_Data User_Info, String Base) {
		User_Info.USER_ID = Helper_Functions.LoadUserID(Base);
		return User_Info;	
	}
	
	public static String[][] Get_User_Data_String_Array(User_Data User_Info) {
		String Data[][] = new String[][] {{"UUID_NBR", User_Info.UUID_NBR}, 
			{"SSO_LOGIN_DESC", User_Info.USER_ID}, 
			{"USER_PASSWORD_DESC", User_Info.PASSWORD}, 
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
	
	//will load the userids into the data class even if the rows have been changed.
	public static User_Data[] Get_UserIds(int intLevel) {
		List<String[]> FullDataFromExcel = new ArrayList<String[]>();
		FullDataFromExcel = Helper_Functions.getExcelData(Helper_Functions.DataDirectory + "\\TestingData.xls", "L" + intLevel);
		//a list of the Userids
		User_Data User_Info_Array[] = new User_Data[FullDataFromExcel.size() - 1];
		
		String Headers[] = FullDataFromExcel.get(0);
		for (int i = 1; i < FullDataFromExcel.size(); i++) {
			String Row[] = FullDataFromExcel.get(i);
			User_Info_Array[i - 1] = new User_Data(); 
			User_Info_Array[i - 1].Level = Integer.toString(intLevel);
			for (int j = 0; j <Headers.length; j++) {
				int pos = i - 1;
				switch (Headers[j]) {
		  		case "UUID_NBR":
					User_Info_Array[pos].UUID_NBR = Row[j];
					break;
		  		case "SSO_LOGIN_DESC":
		  			User_Info_Array[pos].USER_ID = Row[j];
					break;
		  		case "USER_PASSWORD_DESC":
		  			User_Info_Array[pos].PASSWORD = Row[j];
					break;
		  		case "SECRET_QUESTION_DESC":
		  			User_Info_Array[pos].SECRET_QUESTION_DESC = Row[j];
					break;
		  		case "SECRET_ANSWER_DESC":
		  			User_Info_Array[pos].SECRET_ANSWER_DESC = Row[j];
					break;
		  		case "FIRST_NM":
		  			User_Info_Array[pos].FIRST_NM = Row[j];
					break;
		  		case "LAST_NM":
		  			User_Info_Array[pos].LAST_NM = Row[j];
					break;
		  		case "EMAIL_ADDRESS":
		  			User_Info_Array[pos].EMAIL_ADDRESS = Row[j];
					break;
		  		case "STREET_DESC":
		  			User_Info_Array[pos].Address_Info.Address_Line_1 = Row[j];
					break;
		  		case "STREET_DESC_TWO":
		  			User_Info_Array[pos].Address_Info.Address_Line_2 = Row[j];
					break;
		  		case "CITY_NM":
		  			User_Info_Array[pos].Address_Info.City = Row[j];
					break;
		  		case "STATE_CD":
		  			User_Info_Array[pos].Address_Info.State_Code = Row[j];
					break;
		  		case "POSTAL_CD":
		  			User_Info_Array[pos].Address_Info.PostalCode = Row[j];
					break;
		  		case "COUNTRY_CD":
		  			User_Info_Array[pos].Address_Info.Country_Code = Row[j];
					break;
		  		case "ACCOUNT_NUMBER":
		  			User_Info_Array[pos].ACCOUNT_NUMBER = Row[j];
					break;
		  		case "ATRK_ENABLED":
		  			User_Info_Array[pos].ATRK_ENABLED = Row[j];
					break;
		  		case "WCRV_ENABLED":
		  			User_Info_Array[pos].WCRV_ENABLED = Row[j];
					break;	
		  		case "GFBO_ENABLED":
		  			User_Info_Array[pos].GFBO_ENABLED = Row[j];
					break;	
		  		case "WGRT_ENABLED":
		  			User_Info_Array[pos].WGRT_ENABLED = Row[j];
					break;	
		  		case "WDPA_ENABLED":
		  			User_Info_Array[pos].WDPA_ENABLED = Row[j];
					break;	
		  		case "GROUND_ENABLED":
		  			User_Info_Array[pos].GROUND_ENABLED = Row[j];
					break;
		  		case "EXPRESS_ENABLED":
		  			User_Info_Array[pos].EXPRESS_ENABLED = Row[j];
					break;
		  		case "PASSKEY":
		  			User_Info_Array[pos].PASSKEY = Row[j];
					break;
		  		case "FDM_STATUS":
		  			User_Info_Array[pos].FDM_STATUS = Row[j];
					break;	
		  		case "FREIGHT_ENABLED":
		  			User_Info_Array[pos].FREIGHT_ENABLED = Row[j];
					break;	
		  		case "ERROR":
		  			User_Info_Array[pos].ERROR = Row[j];
					break;	
		  		case "MIGRATION_STATUS":
		  			User_Info_Array[pos].MIGRATION_STATUS = Row[j];
					break;	
		  		case "USER_TYPE":
		  			User_Info_Array[pos].USER_TYPE = Row[j];
					break;	
				}//end switch
			}
			
			if (User_Info_Array[i - 1].USER_ID == null || User_Info_Array[i - 1].USER_ID.contentEquals("")) {
				User_Info_Array[i - 1].ERROR = "ERROR";
			}
		}
		
		//remove all users that have an error
		for (int index = 0; index < User_Info_Array.length; index++) {
			if (!User_Info_Array[index].ERROR.contentEquals("")) {
				User_Info_Array = removeTheElement(User_Info_Array, index);
			}
		}
		
		return User_Info_Array;
	}
	
	public static User_Data[] removeTheElement(User_Data[] User_Info_Array,  int index) { 

		// If the array is empty 
		// or the index is not in array range 
		// return the original array 
		if (User_Info_Array == null || index < 0 || index >= User_Info_Array.length) { 
			return User_Info_Array; 
		} 
		
		 // Create another array of size one less 
		User_Data[] anotherArray = new User_Data[User_Info_Array.length - 1]; 
 
       // Copy the elements from starting till index 
       // from original array to the other array 
       System.arraycopy(User_Info_Array, 0, anotherArray, 0, index); 
 
       // Copy the elements from index + 1 till end 
       // from original array to the other array 
       System.arraycopy(User_Info_Array, index + 1, 
                        anotherArray, index, 
                        User_Info_Array.length - index - 1);

       // return the resultant array 
       return anotherArray; 
       } 
}

