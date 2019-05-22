package Data_Structures;

import java.util.Arrays;

import SupportClasses.Environment;
import SupportClasses.Helper_Functions;

public class User_Data {
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
	
	public static User_Data Set_Same_Account_Data(User_Data User_Info, Account_Data Account_Info) {
		User_Info.Address_Info.City = Account_Info.Billing_Address_Info.City;
		User_Info.Address_Info.Country_Code = Account_Info.Billing_Address_Info.Country_Code;
		User_Info.Address_Info.Zip = Account_Info.Billing_Address_Info.Zip ;
		User_Info.Address_Info.State_Code = Account_Info.Billing_Address_Info.State_Code ;
		User_Info.Address_Info.Address_Line_1 = Account_Info.Billing_Address_Info.Address_Line_1 ;
		User_Info.Address_Info.Address_Line_2 = Account_Info.Billing_Address_Info.Address_Line_2 ;	
		return User_Info;
	}
	
	public static User_Data Set_Generic_Address(User_Data User_Info, String Country_Code) {
		//will load a dummy address from the given country then apply the same to the User_Data.
		Account_Data Account_Info = Helper_Functions.getAddressDetails(Environment.getInstance().getLevel(), Country_Code);
		User_Info.Address_Info.City = Account_Info.Billing_Address_Info.City;
		User_Info.Address_Info.Country_Code = Account_Info.Billing_Address_Info.Country_Code;
		User_Info.Address_Info.Zip = Account_Info.Billing_Address_Info.Zip ;
		User_Info.Address_Info.State_Code = Account_Info.Billing_Address_Info.State_Code ;
		User_Info.Address_Info.Address_Line_1 = Account_Info.Billing_Address_Info.Address_Line_1 ;
		User_Info.Address_Info.Address_Line_2 = Account_Info.Billing_Address_Info.Address_Line_2 ;	
		return User_Info;
	}
	
	public static User_Data Set_User_Id(User_Data User_Info, String Base) {
		User_Info.USER_ID = Helper_Functions.LoadUserID(Base);
		return User_Info;	
	}
	
}

