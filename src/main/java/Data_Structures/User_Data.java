package Data_Structures;

import SupportClasses.Helper_Functions;

public class User_Data {
	public String UUID_NBR;
	public String SSO_LOGIN_DESC;
	public String USER_PASSWORD_DESC;
	public String SECRET_QUESTION_DESC;
	public String SECRET_ANSWER_DESC;
	public String FIRST_NM;
	public String MIDDLE_NM;
	public String LAST_NM;
	public String EMAIL_ADDRESS;
	public String STREET_DESC;
	public String STREET_DESC_2;
	public String CITY_NM;
	public String STATE_CD;
	public String POSTAL_CD;
	public String COUNTRY_CD;
	public String ACCOUNT_NUMBER;
	public String WCRV_ENABLED;
	public String GFBO_ENABLED;
	public String WGRT_ENABLED;
	public String WDPA_ENABLED;
	public String GROUND_ENABLED;
	public String EXPRESS_ENABLED;
	public String FREIGHT_ENABLED;
	public String PASSKEY;
	public String FDM_STATUS;
	public String USER_TYPE;
	public String MIGRATION_STATUS;
	
	public String PHONE;
	public String MOBILE_PHONE;
	public String FAX_NUMBER;
	
	public String ERROR;
	
	public User_Data() {
		//generic constructor
		this.USER_PASSWORD_DESC = Helper_Functions.myPassword;
		this.SECRET_QUESTION_DESC = "SP2Q1";//ask for mothers name by default
		this.SECRET_ANSWER_DESC = "mom";
		this.EMAIL_ADDRESS = Helper_Functions.MyEmail;
	}
	
	public static User_Data Set_Dummy_Contact_Name(User_Data User_Info, String CountryCode, String Level) {
		String ContactName[] = Helper_Functions.LoadDummyName(CountryCode, Level);
		User_Info.FIRST_NM = ContactName[0];
		User_Info.LAST_NM = ContactName[2];
		return User_Info;
	}
}

