package WebElements;

import org.openqa.selenium.By;

public class EnrollmentPageElements {

	// Start - The Marketing page of WERL
	public static By SignUpButton = By.linkText("SIGN UP NOW");
	// End - The Marketing Page of WERL 
	
	
	// Start - The User Contact Name and address page of WERL
	public static By HeaderText = By.cssSelector("app-sign-up h1");
	
	public static By InstructionText = By.cssSelector("app-sign-up h2");
	
	public static By FirstNameField = By.id("firstNameField");
	
	public static By MiddleNameField = By.id("middleNameField");

	public static By LastNameField = By.id("lastNameField");

	public static By AddressOneField = By.id("addressField");

	public static By AddressTwoField = By.id("addressLine2Field");

	public static By CountryField = By.id("countryField");
	
	public static By PostalCodeField = By.id("zipField");

	public static By CityFailField = By.id("cityField");

	public static By CityField = By.id("cityField");
	
	public static By cityFieldDropdown = By.id("cityFieldDropdown");

	public static By StateField = By.id("stateField");

	public static By EmailField = By.id("email") ;

	public static By PhoneField = By.id("phone");

	public static By SignUpContinueButton = By.id("signUpContinueButton");
	// End - The User Contact Name and address page of WERL
	
	
	// Start - The User Login Credential information page of WERL
	public static By CreateUserIDRadioButton = By.id("userIDRadio");
	
	public static By EmailAsUserIDRadioButton = By.id("remailRadio");
	
	public static By UserIDField = By.id("userID");

	public static By PasswordField = By.id("password");

	public static By ReenterPasswordField = By.id("reenterPassword");

	public static By SecretQuestionField = By.id("secretQuestion");

	public static By SecretAnswerField = By.id("secretAnswer");
	
	public static By TermsAndConditionsCheckbox = By.id("termsAndConditions");
	
	public static By CreateUserIdContinueButton = By.id("createUserIdContinueButton");
	// End - The User Login Credential information page of WERL
	
	
	

	
	
	// Start - The FDM enrollment page of WERL
	public static By EditAddress = By.linkText("EDIT ADDRESS");
	
	public static By SendPostcardButton = By.id("sendPostcardButton");
	// End - The FDM enrollment page of WERL
	
	// Start - The FDM postcard pin entry page of WERL
	public static By PinInputField = By.id("pinInputField");
		
	public static By SubmitPinButton = By.id("submitPinButton");
	// End - The FDM postcard pin entry page of WERL
}
