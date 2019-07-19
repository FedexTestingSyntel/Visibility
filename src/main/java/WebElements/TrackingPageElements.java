package WebElements;

import java.util.Random;
import org.openqa.selenium.By;
import SupportClasses.WebDriver_Functions;

public class TrackingPageElements {
	
	public static By LastUpdatedTime = By.className("footerBarLastUpdatedTimeDiv");
	
	public static By loadingImgage = By.className("loadingImg");
	
	public static By nextUpdateLink = By.className("nxtUpdateLnk");
	
	public static By acceptTrackingTerms = By.className("acceptTrackingTermsAcceptButton wtrk_button");

	public static By firstTrackingNumber = By.className("r1");

	public static By randomTrackingNumber() {
		Random rand = new Random();
		By TrackingNumber = null;
		
		//try 20 times to return a random tracking number from 1-10
		for(int i = 0 ; i < 20; i++) {
			int Tracking = rand.nextInt(10) + 1;
			
			TrackingNumber = By.className("r" + Tracking);
			//if element found on page then break out and return
			if (TrackingNumber != null && WebDriver_Functions.isPresent(TrackingNumber)) {
				break;
			}
		}
		
		return TrackingNumber;
	}
}
