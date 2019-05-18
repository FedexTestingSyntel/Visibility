package WADM_Application;

import java.text.SimpleDateFormat;
import java.util.Date;

import org.openqa.selenium.By;

import Data_Structures.User_Data;
import SupportClasses.Environment;
import SupportClasses.Helper_Functions;
import SupportClasses.WebDriver_Functions;

public class WADM_Functions {

	public static String WADM_Add_User_To_Company(User_Data User_Info, User_Data New_User_Info) throws Exception{
 		WebDriver_Functions.ChangeURL("INET", User_Info.Address_Info.Country_Code, true);
 		
    	try{

		}catch (Exception e){
			Helper_Functions.PrintOut("Secret quesiton " + User_Info.SECRET_ANSWER_DESC + " was not accepted.", true);
			return e.getMessage();
		}
		return "";
	}//end WADM_Add_User_To_Company
	
	public static String[] Invite_User(User_Data User_Info, User_Data User_Info_Invited, String Role) throws Exception{
		
		WebDriver_Functions.Login(User_Info);
		WebDriver_Functions.ChangeURL("WADM", User_Info.Address_Info.Country_Code, false);

		//click the create user button
		WebDriver_Functions.WaitClickable(By.id("usertabs"));
		WebDriver_Functions.Click(By.id("usertabs"));
		
		
		//enter the user details
    	//trying to make a unique value to enter
		Date curDate = new Date();
		SimpleDateFormat Dateformatter = new SimpleDateFormat("MMddyy");
		String Unique_Id = Dateformatter.format(curDate) + Helper_Functions.getRandomString(4);
		WebDriver_Functions.Type(By.name("userAlias"), Unique_Id);
		User_Info_Invited.USER_ID = Unique_Id;
		
    	SimpleDateFormat Timeformatter = new SimpleDateFormat("HHmmss"); //using time to have a measurement of how long it took to get the invite
		User_Data.Set_Dummy_Contact_Name(User_Info_Invited, Timeformatter.format(curDate), Environment.getInstance().getLevel());
		WebDriver_Functions.Type(By.name("userfirstName"), User_Info_Invited.FIRST_NM);
		WebDriver_Functions.Type(By.name("middleName"), User_Info_Invited.MIDDLE_NM);
		WebDriver_Functions.Type(By.name("userlastName"), User_Info_Invited.LAST_NM);
		WebDriver_Functions.Type(By.name("email"), User_Info_Invited.EMAIL_ADDRESS);
		
		//click the radio button to mark the user as invited.
		WebDriver_Functions.Click(By.id("inviteUsers"));
		
		//select the radio button for the user role
		switch (Role.toUpperCase()) {
			case "REGULAR":
				WebDriver_Functions.Select(By.id("userAdminTypeSelect"), "user", "v");
				break;
			case "DEPARTMENT"://added to avoid confusion. The old IPAS flow considers these groups.
			case "GROUP":
				WebDriver_Functions.Select(By.id("userAdminTypeSelect"), "group", "v");
				//will select the first group by default.
				WebDriver_Functions.Select(By.id("currentGroupSelect"), "1", "i");
				break;
			case "COMPANY":
				WebDriver_Functions.Select(By.id("userAdminTypeSelect"), "company", "v");
				break;
		}
		
		//need to add at least 1 account to the user.
		WebDriver_Functions.Click(By.id("addAccountButton"));
		//wait for the account pop-up to appear.
		WebDriver_Functions.WaitPresent(By.xpath("//*[@id='managetables-checkbox']/input"));
		//click the add all accounts button.
		WebDriver_Functions.Click(By.xpath("//*[@id='managetables-checkbox']/input"));
		//add account button from the pop-up.
		WebDriver_Functions.Click(By.id("addAccounts"));
		
		//click save, at this point the user should be created
		WebDriver_Functions.Click(By.id("userSave"));
		
		//This will wait for the confirmation that the creation was successful. Not safe for different languages.
		WebDriver_Functions.WaitForBodyText("Your updates have been saved.");
		WebDriver_Functions.WaitForBodyText("PENDING");
		
		String Result[] = new String[] {User_Info_Invited.USER_ID, User_Info_Invited.EMAIL_ADDRESS, Role};
		return Result;
	}//end WFCL_ContactInfo_Page
	
	
}
