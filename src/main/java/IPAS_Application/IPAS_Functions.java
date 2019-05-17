package IPAS_Application;

import java.text.SimpleDateFormat;
import java.util.Date;
import org.openqa.selenium.By;
import Data_Structures.User_Data;
import SupportClasses.Environment;
import SupportClasses.Helper_Functions;
import SupportClasses.WebDriver_Functions;

public class IPAS_Functions {

	public static String[] Invite_User(User_Data User_Info, User_Data User_Info_Invited, String Role) throws Exception{
		
		WebDriver_Functions.Login(User_Info.USER_ID, User_Info.PASSWORD);
		WebDriver_Functions.ChangeURL("IPAS", User_Info.Address_Info.Country_Code, false);

		//click the invite user button
		WebDriver_Functions.Click(By.xpath("//*[@id='usersModule']/table/tbody[2]/tr[3]/td[2]/input"));
		
		//select the first department
		WebDriver_Functions.Select(By.id("viewDeptSelId"), "1", "i");
		
		//enter the user details
    	//trying to make a unique value to enter
		User_Info_Invited.USER_ID = Helper_Functions.getRandomString(10);;
		WebDriver_Functions.Type(By.name("userID"), User_Info_Invited.USER_ID);
		
		Date curDate = new Date();
    	SimpleDateFormat Timeformatter = new SimpleDateFormat("HHmmss"); //using time to have a measurement of how long it took to get the invite
		User_Data.Set_Dummy_Contact_Name(User_Info_Invited, Timeformatter.format(curDate), Environment.getInstance().getLevel());
		WebDriver_Functions.Type(By.name("userFirstName"), User_Info_Invited.FIRST_NM);
		WebDriver_Functions.Type(By.name("userLastName"), User_Info_Invited.LAST_NM);
		WebDriver_Functions.Type(By.name("emailId"), User_Info_Invited.EMAIL_ADDRESS);
		
		//select the radio button for the user role
		switch (Role.toUpperCase()) {
			case "REGULAR":
				WebDriver_Functions.Click(By.xpath("//input[(@name='adminRoleCd') and (@value = 'U')]"));
				break;
			case "DEPARTMENT":
				WebDriver_Functions.Click(By.xpath("//input[(@name='adminRoleCd') and (@value = 'D')]"));
				break;
			case "COMPANY":
				WebDriver_Functions.Click(By.xpath("//input[(@name='adminRoleCd') and (@value = 'A')]"));
				break;
		}
		
		WebDriver_Functions.Click(By.id("invitebutton1"));
		
		String Result[] = new String[] {User_Info_Invited.USER_ID, User_Info_Invited.EMAIL_ADDRESS, Role};
		return Result;
	}//end WFCL_ContactInfo_Page
	
}
