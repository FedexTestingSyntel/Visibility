package SupportClasses;

import java.text.Normalizer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.regex.Pattern;
import org.openqa.selenium.By;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Listeners;
import org.testng.annotations.Test;
import Data_Structures.Account_Data;

@Listeners(SupportClasses.TestNG_TestListener.class)

public class Account_Lookup extends Helper_Functions{
	static String LevelToTest = "3";
	
	@BeforeClass
	public void beforeClass() {
		Environment.SetLevelsToTest(LevelToTest);
	}
	
	@DataProvider //(parallel = true)
	public static Iterator<Object[]> dp() {
		List<Object[]> data = new ArrayList<Object[]>();
		
		//String AccountsNumbers[] = new String[] {"761391380", "761391500", "761391640", "761391720", "761391780", "761391860"};
		
		String AccountsNumbers[] = new String[] {"641970123", "641970140", "641970166", "641970182", "641970204", "641970220", "641970247", "641970263", "641970280", "641970301", "641970328", "641970344", "641970360", "641970387", "641970409", "641970425", "641970441", "641970468", "641970484", "641970506", "641970522", "641970549", "641970565", "641970581", "641970603", "641970620", "641970646", "641970662", "641970689", "641970700", "641970727", "641970743", "641970760", "641970786", "641970808", "641970824", "641970840", "641970867", "641970883", "641970905", "641970921", "641970948", "641970964", "641970980", "641971006", "641971022", "641971049", "641971065", "641971081", "641971103", "641971120", "641971146", "641971162", "641971189", "641971200", "641971227", "641971243", "641971260", "642008820", "642008846", "642008862", "642008889", "642008900", "642008927", " 642009621", "642009648", "642009664", "642009680", "642009702", "642009729", "642009745", "642009761", "642009788", "642009800", " 642009826", "642009842", "642009869", "642009885", "642009907", "642009923", "642009940", "642009966", "642009982", "642010000", "642010026", "642010042", "642010069", "642010085", "642010107", "642010123", "642010140", "642010166", "642010182", "642010204", "642010220", "642010247", "642010263", "642010280", "642010301", "642010328", "642010344", "642010360", "642010387", "642010409", "642010425", "642010441", "642010468", "642010484", "642010506", "642010522", "642010549", "642010565", "642010581", "642010603", "642010620", "642010646", "642010662", "642010689", "642010700", "642010727", "642010743", "642010760", "642010786", "642010808", "642010824", "642010840", "642010867", "642010883", "642010905", "642010921", "642010948", "642010964", "642010980", "642011006", "642011022", "642011049", "642011065", "642011081", "642011103", "642011120", "642011146", "642011162", "642011189", "642011200", "613947345", "613977864", "613953027", "642011227", "642011243", "642011260", "642011286", "642011308", "642048821", "642048848", "642048864", "642048880", "642048902", "642048929", "642048945", "642048961", "642048988", "642049003", "642049020", "642049046", "642049062", "642049089", "642049100", " 642049127", "642049143", "642049160", "642049186", "642049208", "642049224", "642049240", "642049267", "642049283", "642049305", "  ", "642049321", "642049348", "642049364", "642049380", "642049402", "642049429", "642049445", "642049461", "642049488", "642049500", "642049526", "642049542", "642049569", "642049585", "642049607", "642049623", "642049640", "642049666", "642049682", "642049704", "642049720", "642049747", "642049763", "642049780", "642049801", "642049828", "642049844", "642049860", "642049887", "642049909", "642049925", "642049941", "642049968", "642049984", "642050001", "642050028", "642050044", "642050060", "642050087", "642050109", " 642050125", "642050141", "642050168", "642050184", "642050206", "642050222", "642050249", "642050265", "642050281", "642050303", "642050320", "642050346", "642050362", "642050389", "642050400", "642050427", "642050443", "642050460", "642050486", "642050508", "642050524", "642050540", "642050567", "642050583", "642050605", "642050621", "642050648", "642050664", "642050680", "642050702", " 642050729", "642050745", "642050761", "642050788", "642050800", "642050826", "642050842", "642050869", "642050885", "642050907", "642050923", "642050940", "642050966", "642050982", "642051008", "642051024", "642051040", "642051067", "642051083", "642051105", "642051121", "642051148", "642051164", "642051180", "642051202", "642051229", "642051245", "642051261", "642051288", "642051300", "642088823", "642088840", "642088866", "642088882", "642088904", "642088920", "642088947", "642088963", "642088980", "642089005", "642089021", "642089048", "642089064", "642089080", "642089102", "642089129", "642089145", "642089161", "642089188", "642089200", " 642089226", "642089242", "642089269", "642089285", "642089307", "642089323", "642089340", "642089366", "642089382", "642089404", "642089420", "642089447", "642089463", "642089480", "642089501", "642089528", "642089544", "642089560", "642089587", "642089609", "642089625", "642089641", "642089668", "642089684", "642089706", "642089722", "642089749", "642089765", "642089781", "642089803", "642089820", "642089846", "642089862", "642089889", "642089900", "642089927", "642089943", "642089960", "642089986", "642090003", "642090020", "642090046", "642090062", "642090089", "642090100", "642090127", "642090143", "642090160", "642090186", "642090208", "642090224", "642090240", "642090267", "642090283", "642090305", "642090321", "642090348", "642090364", "642090380", "642090402", "642090429", "642090445", "642090461", "642090488", "642090500", "642090526", "642090542", "642090569", "642090585", "642090607", " 642090623", "642090640", "642090666", "642090682", "642090704", "642090720", "642090747", "642090763", "642090780", "642090801", "642090828", "642090844", "642090860", "642090887", "642090909", "642090925", "642090941", "642090968", "642090984", "642091000", "642091026", "642091042", "642091069", "642091085", "642091107", "642091123", "642091140", "642091166", "642091182", "642091204", "642091220", "642091247", "642091263", "642091280", "642091301", "642128884", "642128906", "642128922", "642128949", "642128965", "642128981", "642129007", "642129023", "642129040", "642129066", "642129082", "642129104", "642129120", "642129147", "642129163", " 642129180", "642129201", "642129228", "642129244", "642129260", "642129287", "642129309", "642129325", "642129341", "642129368", "642129384", "642129406", "642129422", "642129449", "642129465", "642129481", "642129503", "642129520", "642129546", "642129562", " 642129589", "642129600", "642129627", "642129643", "642129660", "642129686", "642129708", "642129724", "642129740", "642129767", "642129783", "642129805", "642129821", "642129848", "642129864", "642129880", "642129902", "642129929", "642129945", "642129961", "642129988", "642130005", "642130021", "642130048", "642130064", "642130080", "642130102", "642130129", "642130145", "642130161", " 642130382", "642130404", "642130420", "642130447", "642130463", "642130480", "642130501", "642130528", "642130544", "642130560", " 642130587", "642130609", "642130625", "642130641", "642130668", "642130684", "642130706", "642130722", "642130749", "642130765", "642130781", "642130803", "642130820", "642130846", "642130862", "642130889", "642130900", "642130927", "642130943", "642130960", "642130986", "642131001", "642131028", "642131044", "642131060", "642131087", "642131109", "642131125", "642131141", "642131168", "642131184", "642131206", "642131222", "642131249", "642131265", "642131281", "642131303", "642131320", "642131346", "642131362", " 642169106", "642169122", "642169149", "642169165", "642169181", "642169203", "642169220", "642169246", "642169262", "642169289", "  642169300", "642169327", "642169343", "642169360", "642169386", "642169408", "642169424", "642169440", "642169467", "642169483", "  642169505", "642169521", "642169548", "642169564", "642169580", "642169602", "642169629", "642169645", "642169661", "642169688", "642169700", "642169726", "642169742", "642169769", "642169785", "642169807", "642169823", "642169840", "642169866", "642169882", " 642169904", "642169920", "642169947", "642169963", "642169980", "642170007", "642170023", "642170040", "642170066", "642170082", "642170104", "642170120", "642170147", "642170163", "642170180", "642170201", "642170228", "642170244", "642170260", "642170287", "642170309", "642170325", "642170341", "642170368", "642170384", "642170406", "642170422", "642170449", "642170465", "642170481", " 642170503", "642170520", "642170546", "642170562", "642170589", "642170600", "642170627", "642170643", "642170660", "642170686", "642170708", "642170724", "642170740", "642170767", "642170783", "642170805", "642170821", "642170848", "642170864", "642170880", "642170902", "642170929", "642170945", "642170961", "642170988", "642171003", "642171020", "642171046", "642171062", "642171089", "642171100", "642171127", "642171143", "642171160", "642171186", "642171208", "642171224", "642171240", "642171267", "642171283", "642171305", "642171321", "642171348", "642171364", "642171380", "642208942", "642208969", "642208985", "642209000", "642209027", "642209043", "642209060", "642209086", "642209108", "642209124", "642209140", "642209167", "642209183", "642209205", "642209221", "642209248", "642209264", "642209280", "642209302", "642209329", "642209345", "642209361", "642209388", "642209400", "642209426", " 642209442", "642209469", "642209485", "642209507", "642209523", "642209540", "642209566", "642209582", "642209604", "642209620", " 642209647", "642209663", "642209680", "642209701", "642209728", "642209744", "642209760", "642209787", "642209809", "642209825", "642209841", "642209868", "642209884", "642209906", "642209922", "642209949", "642209965", "642209981", "642210009", "642210025", "642210041", "642210068", "642210084", "642210106", "642210122", "642210149", "642210165", "642210181", "642210203", "642210220"};
		
		for (String S: AccountsNumbers) {
			data.add( new Object[] {LevelToTest, S});
		}	
		return data.iterator();
	}
	
	@Test(dataProvider = "dp")
	public static void AccountCheck(String Level, String Account_Number){
		Account_Data Account_Info[] = new Account_Data[] {Account_DataAccountDetails(Account_Number)};
		//Helper_Functions.PrintOut("breakpoint", false);
		try {
			Create_Accounts.writeAccountsToExcel(Account_Info);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}//end WRTT_Rate_Sheet
	
	//String[] {Streetline1 - 0, Streetline2 - 1, City - 2, State - 3, StateCode - 4, postalCode - 5, countryCode - 6};
	public static Account_Data Account_DataAccountDetails(String AccountNumber){
		Account_Data Account_Details = new Account_Data();
		String Streetline1 = "", Streetline2 = "", City ="", State ="", StateCode = "", postalCode = "", countryCode = "", areaCode = "", phoneNumber = "";
		String TempLevel = "L" + Environment.getInstance().getLevel();
		Account_Details.Level = Environment.getInstance().getLevel();
		if (!"L1L2L3L4".contains(TempLevel)){
			PrintOut("Invalid Level to find account number detials, checking account vs L3", true);
			TempLevel = "L3";
		}
 		try {
 			PrintOut("Account number " + AccountNumber + " recieved from " + Thread.currentThread().getStackTrace()[2].getMethodName(), true);
 			WebDriver_Functions.ChangeURL("JSP", "US", false);
 			WebDriver_Functions.Type(By.name("contactAccountNumber"), AccountNumber);
 			WebDriver_Functions.Type(By.name("contactAccountOpCo"), "FX");

 		    //selects the correct radio button for the level, only works for 1,2,3,4
 			WebDriver_Functions.Click(By.xpath("//input[(@name='contactLevel') and (@value = '" + TempLevel + "')]"));
			WebDriver_Functions.Click(By.name("contactAccountSubmit"));
			String SourceText = DriverFactory.getInstance().getDriver().getPageSource();
			
			int intStartingPoint;
			String start = "name=\"";
			String end  = "\" value=\"";
			
			String StartingPoint = "";
			for (int k = 0; k < 2; k++) {
				StartingPoint = "&lt;streetLine&gt;";
				if (SourceText.indexOf(StartingPoint) < 0){
					StartingPoint = "&lt;customer:streetLine&gt;";
				}else {
					SourceText = SourceText.substring(SourceText.indexOf(StartingPoint) + StartingPoint.length(), SourceText.length());
				}
				SourceText = SourceText.substring(SourceText.indexOf(StartingPoint) + StartingPoint.length(), SourceText.length());
				
				
				Streetline1 = SourceText.substring(SourceText.indexOf(start) + start.length(), SourceText.indexOf(end));
				
				StartingPoint = "additionalLine1&gt;";//save if the account number has an address line1 value
				if(SourceText.indexOf(StartingPoint) > 0){
					intStartingPoint = SourceText.indexOf(StartingPoint) + StartingPoint.length();
					SourceText = SourceText.substring(intStartingPoint, SourceText.length());
					Streetline2 = SourceText.substring(SourceText.indexOf(start) + start.length(), SourceText.indexOf(end));
				}
				
				StartingPoint = "geoPoliticalSubdivision2&gt";
				intStartingPoint = SourceText.indexOf(StartingPoint) + StartingPoint.length();
				SourceText = SourceText.substring(intStartingPoint, SourceText.length());
				if (intStartingPoint > StartingPoint.length()){
					City = SourceText.substring(SourceText.indexOf(start) + start.length(), SourceText.indexOf(end));
				}
				
				StartingPoint = "geoPoliticalSubdivision3&gt";			
				intStartingPoint = SourceText.indexOf(StartingPoint) + StartingPoint.length();
				SourceText = SourceText.substring(intStartingPoint, SourceText.length());
				if (intStartingPoint > StartingPoint.length()){
					StateCode = SourceText.substring(SourceText.indexOf(start) + start.length(), SourceText.indexOf(end));
				}
				
				StartingPoint = "postalCode&gt";
				intStartingPoint = SourceText.indexOf(StartingPoint) + StartingPoint.length();
				SourceText = SourceText.substring(intStartingPoint, SourceText.length());
				if (intStartingPoint > StartingPoint.length()){
					postalCode = SourceText.substring(SourceText.indexOf(start) + start.length(), SourceText.indexOf(end));
				}
				
				
				StartingPoint = "countryCode&gt";
				intStartingPoint = SourceText.indexOf(StartingPoint) + StartingPoint.length();
				SourceText = SourceText.substring(intStartingPoint, SourceText.length());
				countryCode = SourceText.substring(SourceText.indexOf(start) + start.length(), SourceText.indexOf(end));
				
				if (postalCode.length() > 5 && countryCode.contentEquals("US")){
					postalCode = postalCode.substring(0, 5);
				}
				
				StartingPoint = "areaCode&gt";
				intStartingPoint = SourceText.indexOf(StartingPoint) + StartingPoint.length();
				SourceText = SourceText.substring(intStartingPoint, SourceText.length());
				if (intStartingPoint > StartingPoint.length()){
					areaCode = SourceText.substring(SourceText.indexOf(start) + start.length(), SourceText.indexOf(end));
				}
				
				StartingPoint = "phoneNumber&gt";
				intStartingPoint = SourceText.indexOf(StartingPoint) + StartingPoint.length();
				SourceText = SourceText.substring(intStartingPoint, SourceText.length());
				if (intStartingPoint > StartingPoint.length()){
					phoneNumber = SourceText.substring(SourceText.indexOf(start) + start.length(), SourceText.indexOf(end));
				}
				
				//remove any special characters
				String AccountDetails[] = {Streetline1, Streetline2, City, State, StateCode, postalCode, countryCode};
				for (int i = 0; i < AccountDetails.length; i++){
					String nfdNormalizedString = Normalizer.normalize(AccountDetails[i], Normalizer.Form.NFD); 
					Pattern pattern = Pattern.compile("\\p{InCombiningDiacriticalMarks}+");
					AccountDetails[i] = pattern.matcher(nfdNormalizedString).replaceAll("");
				}
				
				if (countryCode.length() > 5 || countryCode.length() == 0){
					return null;
				}
				
				if (k == 0) {
					Account_Details.Shipping_Address_Line_1 = Streetline1;
					Account_Details.Shipping_Address_Line_2 = Streetline2;
					Account_Details.Shipping_City = City;
					Account_Details.Shipping_State = State;
					Account_Details.Shipping_State_Code = StateCode;
					Account_Details.Shipping_Phone_Number = areaCode + phoneNumber;
					Account_Details.Shipping_Zip = postalCode;
					Account_Details.Shipping_Country_Code = countryCode;
					Account_Details.Shipping_Region = "";
					Account_Details.Shipping_Country = "";
				}else if (k == 1) {
					Account_Details.Billing_Address_Line_1 = Streetline1;
					Account_Details.Billing_Address_Line_2 = Streetline2;
					Account_Details.Billing_City = City;
					Account_Details.Billing_State = State;
					Account_Details.Billing_State_Code = StateCode;
					Account_Details.Billing_Phone_Number = areaCode + phoneNumber;
					Account_Details.Billing_Zip = postalCode;
					Account_Details.Billing_Country_Code = countryCode;
					Account_Details.Billing_Region = "";
					Account_Details.Billing_Country = "";
				}
				
			}
			
			WebDriver_Functions.ChangeURL("JSP_Express", "US", false);
			WebDriver_Functions.Select(By.id("express"), "expressCreditCard", "v");
			WebDriver_Functions.Click(By.name("expressSubmit"));
			
			SourceText = DriverFactory.getInstance().getDriver().getPageSource();
			//if the account is linked to a credit card.
			String Credit_Card_Type = "", Credit_Card_Number = "", Credit_Card_CVV = "", Credit_Card_Expiration_Month = "", Credit_Card_Expiration_Year = "", Invoice_Number_A = "", Invoice_Number_B = "";
			
			if (SourceText.contains("creditCardId&gt")) {
				StartingPoint = "creditCardId&gt";
				intStartingPoint = SourceText.indexOf(StartingPoint) + StartingPoint.length();
				SourceText = SourceText.substring(intStartingPoint, SourceText.length());
				if (intStartingPoint > StartingPoint.length()){
					Credit_Card_Number = SourceText.substring(SourceText.indexOf(start) + start.length(), SourceText.indexOf(end));
				}
				Credit_Card_Number = Credit_Card_Number.substring(Credit_Card_Number.length() - 4, Credit_Card_Number.length());
				String Credit_Lookup[] = Helper_Functions.LoadCreditCard(Credit_Card_Number);
				Credit_Card_Type = Credit_Lookup[0];
				Credit_Card_Number = Credit_Lookup[1];
				Credit_Card_CVV = Credit_Lookup[2];
				Credit_Card_Expiration_Month = Credit_Lookup[3];
				Credit_Card_Expiration_Year = Credit_Lookup[4];
						
			}else {
				//load dummy invoice numbers
				Invoice_Number_A = "750000000";
				Invoice_Number_B = "750000001";
			}
			
			Account_Details.Account_Number = AccountNumber;
			Account_Details.Credit_Card_Type = Credit_Card_Type;
			Account_Details.Credit_Card_Number = Credit_Card_Number;
			Account_Details.Credit_Card_CVV = Credit_Card_CVV;
			Account_Details.Credit_Card_Expiration_Month = Credit_Card_Expiration_Month;
			Account_Details.Credit_Card_Expiration_Year = Credit_Card_Expiration_Year;
			Account_Details.Invoice_Number_A = Invoice_Number_A;
			Account_Details.Invoice_Number_B = Invoice_Number_B;
			Account_Details.Account_Type = "";
			Account_Details.Tax_ID_One = "";
			Account_Details.Tax_ID_Two = "";

			return Account_Details;
 		}catch (Exception e){
 			e.printStackTrace();
 			PrintOut("Not able to fully retrieve address: " + Streetline1 + " " + Streetline2+ " " + City+ " " + State+ " " + StateCode+ " " +postalCode+ " " + countryCode, true);
			return null;
 		}
 	}//end AccountDetails
}
