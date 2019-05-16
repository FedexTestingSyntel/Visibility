package WIDM_Application;

public class WIDM_Error_Codes {

	public static String WIDM_Error_Code(String Error_Code) {
		String Errors[][] = new String[][]{
			{"98", "98: User Id and Email address are not matching."}
		};
		
		for (int i = 0; i < Errors.length; i++) {
			if (Errors[i][0].contentEquals(Error_Code)) {
				return Errors[i][1];
			}
		}
		
		//no corresponding error code found.
		return null;
	}
	
}
