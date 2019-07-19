
package WebElements;

import org.openqa.selenium.By;

public class AdvancedTrackingPageElements {

	
	public static By fistTrackingHamburgerMenu = By.cssSelector("ag-grid-angular#listViewGrid div.ag-cell.ag-cell-not-inline-editing.ag-cell-with-height.ag-cell-value.ag-column-hover.ag-cell-focus > span > button");
	
	//When using remove shipment make sure hamburger menu is open.
	public static By hamburgerRemoveShipment = By.id("removeShipment");
	
}