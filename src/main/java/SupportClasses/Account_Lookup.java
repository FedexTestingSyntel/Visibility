package SupportClasses;

import java.text.Normalizer;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.regex.Pattern;
import org.openqa.selenium.By;
import org.testng.Assert;
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
	
	@DataProvider (parallel = true)
	public static Iterator<Object[]> dp() {
		List<Object[]> data = new ArrayList<Object[]>();
		
		//String AccountsNumbers[] = new String[] {"761391380", "761391500", "761391640", "761391720", "761391780", "761391860"};
		//String AccountsNumbers[] = new String[] {"642531069", "642141724", "642141740", "642531565", "642531581", "642531603", "642531620", "642569147", "642569163", "642141783", "642141805", "642141821", "642142240", "642142267", "642142283", "642569309", "642569325", "642569341", "642569368", "642569384", "642569406", "642304305", "642304321", "642304348", "642142364", "642142380", "642142402", "642304542", "642304569", "642304585", "642304623", "642304640", "642304666", "642570048", "642570064", "642570080", "642143000", "642143026", "642143042", "642570102", "642570129", "642570145", "642009567", "642009583", "642009605", "642304682", "642304704", "642304720", "642304747", "642304763", "642304780", "642009508", "642009524", "642009540", "642304801", "642304828", "642304844", "642304887", "642304909", "642304925", "642304941", "642304968", "642304984", "642305000", "642305026", "642305042", "642305069", "642305085", "642305107", "642211544", "642211560", "642211587", "642211641", "642211668", "642211684", "642211706", "642211722", "642211749", "642211781", "642211803", "642211820", "642211846", "642211862", "642211889", "642211900", "642211927", "642211943", "642211960", "642211986", "642212001", "642212109", "642212125", "642212141", "642212044", "642212060", "642212087", "642212206", "642212222", "642212249", "642212265", "642212281", "642212303", "642212362", "642212389", "642212400", "642212427", "642212443", "642212460", "642212486", "642212508", "642212524", "642212567", "642212583", "642212605", "642212621", "642212648", "642212664", "642212702", "642212729", "642212745", "642212761", "642212788", "642212800", "642212826", "642212842", "642212869", "642455621", "642455648", "642455664", "642455680", "642455702", "642455729", "642455745", "642455761", "642455788", "642455826", "642455842", "642455869", "642455966", "642455982", "642456008", "642456024", "642456040", "642456067", "642456083", "642456105", "642456121", "642456148", "642456164", "642456180", "642456229", "642456245", "642456261", "642456300", "642456326", "642456288", "642456342", "642456369", "642456385", "642456440", "642456466", "642456482", "642456504", "642456520", "642456547", "642456563", "642494104", "642494120", "642494163", "642494180", "642494201", "642494228", "642494244", "642494260", "642494309", "642494325", "642494341", "642494384", "642494406", "642494422", "642494449", "642494465", "642494481", "642494503", "642494520", "642494546", "642494562", "642494589", "642494600", "642494627", "642494643", "642494660", "642494686", "642494708", "642494724", "642537407", "642537423", "642537440", "642537482", "642537504", "642537520", "642537547", "642537563", "642537580", "642537601", "642537628", "642537644", "642097903", "642097920", "642097946", "642097962", "642097989", "642098004", "642098020", "642098047", "642098063", "642098080", "642098101", "642098128", "642098144", "642098160", "642098187", "642098209", "642098225", "642098241", "642098268", "642098284", "642098306", "642098322", "642098349", "642098365", "642305140", "642305166", "642305182", "642098381", "642098403", "642098420", "642098446", "642098462", "642098489", "642098500", "642098527", "642098543", "642098560", "642098586", "642098608", "642098624", "642098640", "642098667", "642098683", "642098705", "642098721", "642098748", "642098764", "642098780", "642098802", "642136380", "642136402", "642136429", "642136445", "642136461", "642136488", "642136500", "642136526", "642136542", "642136569", "642136585", "642136607", "642136623", "642136640", "642136666", "642136682", "642136704", "642136720", "642136747", "642136763", "642136780", "642136801", "642136828", "642136844", "642136860", "642136887", "642136909", "642136925", "642136941", "642136968", "642136984", "642137000", "642137026", "642137042", "642137069", "642137085", "642137107", "642137123", "642137140", "642137166", "642137182", "642137204", "642137220", "642137247", "642137263", "642137280", "642137301", "642137328", "642137344", "642137360", "642137387", "642137409", "642137425", "642305204", "642305220", "642305247", "642137565", "642137581", "642137603", "642137620", "642137646", "642137662", "642137689", "642137700", "642137727", "642137743", "642137760", "642137786", "642137824", "642137840", "642137867", "642137883", "642137905", "642137921", "642137948", "642137964", "642137980", "642138006", "642138022", "642138049", "642138065", "642138081", "642138103", "642305263", "642305280", "642305301", "642138464", "642138480", "642138502", "642138529", "642138545", "642138561", "642138588", "642138600", "642138626", "642138642", "642138669", "642138685", "642138707", "642138723", "642138740", "642138766", "642138782", "642138804", "642138820", "642138847", "642138863", "642138880", "642176404", "642176420", "642305328", "642305344", "642305360", "642176447", "642176463", "642176480", "642176501", "642176528", "642176544", "642176560", "642176587", "642176609", "642176625", "642176641", "642176668", "642142941", "642142968", "642142984", "642142887", "642142909", "642142925", "642176684", "642176706", "642176722", "642176749", "642176765", "642176781", "642176803", "642176820", "642176846", "642176862", "642176889", "642176900", "642176927", "642176943", "642176960", "642176986", "642177001", "642177028", "642177044", "642177060", "642177087", "642177109", "642177125", "642177141", "642305387", "642305409", "642305425", "642143069", "642143085", "642143107", "642143166", "642143182", "642143204", "642305441", "642305468", "642305484", "642177400", "642177427", "642177443", "642305506", "642305522", "642305549", "642177460", "642177486", "642177508", "642177524", "642177540", "642177567", "642177583", "642177605", "642177621", "642177648", "642177664", "642177680", "642177702", "642177729", "642177745", "642305565", "642305581", "642305603", "642305646", "642305662", "642305689", "642177761", "642177788", "642177800", "642177826", "642177842", "642177869", "642177885", "642177907", "642177923", "642177940", "642177966", "642177982", "642178008", "642178024", "642178040", "642306065", "642306081", "642306103", "642178067", "642178083", "642178105", "642178121", "642178148", "642178164", "642178180", "642178202", "642178229", "642178245", "642178261", "642178288", "642306120", "642306146", "642306162", "642178300", "642178326", "642178342", "642306308", "642306324", "642306340", "642142763", "642142780", "642142801", "642142682", "642142704", "642142720", "642142623", "642142640", "642142666", "642142569", "642142585", "642142607", "642178369", "642178385", "642178407", "642142500", "642142526", "642142542", "642178423", "642178440", "642178466", "642142445", "642142461", "642142488", "642178547", "642178563", "642178580", "642306367", "642306383", "642306405", "642178601", "642178628", "642178644", "642178660", "642178687", "642178709", "642142305", "642142321", "642142348", "642306421", "642306448", "642306464", "642177346", "642177362", "642177389", "642306480", "642344005", "642344021", "642177281", "642177303", "642177320", "642177222", "642177249", "642177265", "642177168", "642177184", "642177206"};

		String AccountsNumbers[] = new String[] {"01251003", "84728881", "00595000", "10759512", "84613390", "92053064", "84788374", "91053112", "93958735", "25778701", "91314766", "35244484", "03977676", "84512854", "94746026", "84338760", "89737226", "10100554", "86807501", "01116231", "41407704", "93996794", "84352167", "82770335", "84647727", "95504213", "07142133", "04949490", "88758698", "92203867", "83325725", "00365816", "00453246", "00480712", "00470131", "95466537", "95737728", "19408152", "02501332", "15201240", "95720214", "91660061", "04298350", "61366034", "00240855", "84348019", "66722272", "72370303", "84617322", "85889674", "92236369", "88758931", "84315298", "95710852", "13966455", "93506438", "84291670", "84493196", "95560381", "95804760", "17819104", "86821621", "54268653", "87195492", "53218300", "88901228", "63057050", "90621684", "85736225", "84396049", "17794895", "84412398", "92264005", "91261333", "49730516", "00451426", "00481854", "00315685", "00315556", "00417535", "87483762", "62721433", "00112943", "85236031", "84507406", "89667650", "00265506", "07409942", "84407855", "79808363", "85714830", "04806756", "00760244", "93716470", "10830105", "37633783", "87879464", "84647866", "88391323", "90373884", "86997269", "93122292", "06296172", "00530714", "84582871", "84704645", "93315411", "83488901", "62657184", "92372237", "95470766", "93749182", "85076060", "17865525", "92115034", "85728394", "86304492", "92778124", "01177422", "94791456", "96666434", "96666154", "29954562", "95905012", "00112770", "93551397", "95868948", "04339506", "11392393", "27912544", "00405823", "00100575", "92728632", "84312496", "85299523", "87194127", "92423672", "93399084", "92432841", "93918837", "92557130", "92245951", "95835957", "86090395", "90475199", "97003875", "00806562", "85255490", "87709391", "83496751", "42787986", "06584841", "84335143", "84297626", "89771953", "91528254", "54338373", "00367953", "91370815", "00306806", "00316072", "00409721", "00454226", "71075292", "86296257", "96665991", "28790775", "97093070", "89216811", "00459093", "24305595", "84947526", "91236355", "91774785", "92742555", "92900216", "93581005", "94542999", "84381581", "94794464", "96671570", "84579102", "89025335", "93487758", "26554194", "93927985", "84710330", "00310133", "83635370", "87668192", "30901986", "84257159", "95799249", "00443026", "47057415", "00300963", "00409172", "00466314", "00412414", "07696463", "85398223", "92206396", "84565084", "93606554", "92558395", "97127872", "97150344", "92610407", "95689287", "00103456", "87309353", "84403303", "84285150", "91627551", "82995126", "84396824", "84408302", "85667338", "90056326", "91144227", "91207344", "91918960", "93193034", "94169245", "96873155", "84340699", "00702520", "84286612", "88907293", "84936074", "28345531", "04893921", "54451025", "84419015", "64443971", "16338803", "94644900", "16639361", "94709030", "20114452", "88652856", "32315570", "92446574", "37141370", "19565335", "23043134", "14218551", "22660912", "23883145", "00320552", "00364280", "00372223", "00396712", "95717823", "00102642", "84564244", "94939813", "94085605", "91855508", "00537596", "00108452", "96960582", "86210409", "73755824", "84941355", "84222715", "16807232", "16399692", "84313679", "88478157", "94335521", "02174852", "86516653", "74880002", "85211217", "92800923", "71152351", "00603201", "86058734", "15661155", "89810574", "84649632", "86871381", "95669740", "84281606", "07398370", "87135308", "00958145", "00899500", "00100004", "49686081", "86090153", "89788517", "84296147", "00358190", "95704151", "89384069", "95724908", "05334114", "96622861", "00462486", "00104160", "18665441", "91652162", "89978576", "83213416", "92565894", "95674601", "84397071", "85620234", "90475208", "92902909", "08774146", "87187296", "85691012", "84290285", "85102669", "89965873", "21198251", "89849229", "87216224", "00145003", "17739271", "00257003", "39685214", "73615894", "00308685", "00371103", "00301070", "00309934", "43056005", "94675131", "01251132", "00312701", "95100971", "94497828", "07459992", "97150322", "01200011", "94760691", "86270074", "00100203", "00109351", "30568042", "04979122", "96333286"};

		Account_Data Data[] = Environment.getAccountDetails(LevelToTest);
		String AccountsAlreadyPresent = "", PendingAdd = "{\"";
		for (int i = 0; i < Data.length - 1; i++) {
			AccountsAlreadyPresent+= Data[i].Account_Number + " ";
		}
		for (String S: AccountsNumbers) {
			if (!AccountsAlreadyPresent.contains(S)) {
				data.add( new Object[] {LevelToTest, S});
				PendingAdd+= S + "\", \"";
			}
		}	
		PrintOut(data.size() + "\n" + PendingAdd, false);

		return data.iterator();
	}
	
	@Test(dataProvider = "dp")
	public static void AccountCheck(String Level, String Account_Number){
		
		//Helper_Functions.PrintOut("breakpoint", false);
		try {
			String AccountType = "FDFR"; //FDFR for freight, FX for express
			Account_Data Account_Info[] = new Account_Data[] {Account_DataAccountDetails(Account_Number, Level, AccountType)};
			Create_Accounts.writeAccountsToExcel(Account_Info);
			//Account_Info[0].Level = "6"; //if they are L6 account numbers also present in L3 this would copy them as well.
			//Create_Accounts.writeAccountsToExcel(Account_Info);
		} catch (Exception e) {
			Assert.fail(e.getMessage());
		}
	}
	
	//String[] {Streetline1 - 0, Streetline2 - 1, City - 2, State - 3, StateCode - 4, postalCode - 5, countryCode - 6};
	public static Account_Data Account_DataAccountDetails(String AccountNumber, String Level, String AccountType) throws Exception{
		Account_Data Account_Details = new Account_Data();
		String Streetline1 = "", Streetline2 = "", City ="", State ="", StateCode = "", postalCode = "", countryCode = "", areaCode = "", phoneNumber = "";
		Environment.getInstance().setLevel(Level);
		String TempLevel = "L" + Environment.getInstance().getLevel();
		Account_Details.Level = Environment.getInstance().getLevel();
		if (!"L1L2L3L4".contains(TempLevel)){
			PrintOut("Invalid Level to find account number detials, checking account vs L3", true);
			TempLevel = "L3";
		}
 		try {
 			PrintOut("Account number " + AccountNumber + " recieved from " + Thread.currentThread().getStackTrace()[2].getMethodName(), true);
 			WebDriver_Functions.ChangeURL("JSP", "US", false);
 			WebDriver_Functions.WaitPresent(By.name("contactAccountOpCo"));
 			WebDriver_Functions.Type(By.name("contactAccountNumber"), AccountNumber);
 			try {//not sure why but fails about 20% and cannot find element.
 				WebDriver_Functions.Type(By.name("contactAccountOpCo"), AccountType);
 			}catch(Exception CannotFind) {
 				WebDriver_Functions.Type(By.name("contactAccountOpCo"), AccountType);
 			}

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
			Account_Details.Account_Nickname = AccountNumber + "_" + Account_Details.Billing_Country_Code;
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
			PrintOut("Address Returned: " + Streetline1 + " " + Streetline2 + " " + City+ " " + State+ " " + StateCode+ " " +postalCode+ " " + countryCode, true);
			return Account_Details;
 		}catch (Exception e){
 			e.printStackTrace();
 			PrintOut("Not able to fully retrieve address: " + Streetline1 + " " + Streetline2 + " " + City+ " " + State+ " " + StateCode+ " " +postalCode+ " " + countryCode, true);
			throw e;
 		}
 	}
}
