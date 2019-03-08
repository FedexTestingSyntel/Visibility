package syntel.steps;

import syntel.hwdriver.ExecutionInstance;
import syntel.hwdriver.Sessions;

public class WIDMSOAP {
	String SessionID = "";
	private static Sessions ses;

	public WIDMSOAP(String sessionID) {
		super();
		SessionID = sessionID;
		ses = ExecutionInstance.getSession(SessionID);

	}

	public void InitiateExecution() throws Exception {
		String a = ses.getVariable("TestScenario");

		String[] steps = a.split(";");

		int i = steps.length;

		int loginCount = 0;
		int guiCount = 0;
		int l = 0;

		for (int j = 0; j < i; j++) {
			ses.setVariable("currentstep", steps[j]);
			int count = iteratorcount(j, ses.getVariable("TestScenario"));
			System.out.println(count);
			soapRequestCall(count);
		}
	}

	public void soapRequestCall(int Count) throws Exception {
		String ID = ses.getVariable("QC_ID").replace(".0", "");
		Boolean a = false;
		
		if (ses.getVariable("currentstep").trim().equalsIgnoreCase("callUpdateUserService")) {
			AAAUserUpdate req = new AAAUserUpdate(SessionID);
			req.callUpdateSoapWebService(Count);
		}  else if (ses.getVariable("currentstep").trim().equalsIgnoreCase("callCreateUserService")) {
			AAAUserCreate req = new AAAUserCreate(SessionID);
			req.callUserCreateService(Count);
		}  else if (ses.getVariable("currentstep").trim().equalsIgnoreCase("callFedexChngPwdService")) {
			FedexChangeMyPassword req = new FedexChangeMyPassword(SessionID);
			req.callFedexChangeMyPasswordService(Count);
		}   else if (ses.getVariable("currentstep").trim().equalsIgnoreCase("callFedexInfosecChangeService")) {
			FedexInfosecChange req = new FedexInfosecChange(SessionID);
			req.callFedexInfosecChangeService(Count);
		}	else if (ses.getVariable("currentstep").trim().equalsIgnoreCase("callSelfRegService")) {
			FedexSelfRegistration req = new FedexSelfRegistration(SessionID);
			req.callselfRegService(Count);
		}   else if (ses.getVariable("currentstep").trim().equalsIgnoreCase("Wait")) {
			Wait wait = new Wait(ID);
			wait.w_wait();
		}
	}

	// getting the iteration Count
	public static int iteratorcount(int j, String Scenario) {

		String[] steps = Scenario.split(";");

		int i = steps.length;

		int count = 0;

		if (j != 0) {

			// checking iteration
			for (int k = j - 1; k >= 0; k--) {

				if (steps[j].equals(steps[k])) {

					count++;

				}
			}
		}
		return count;
	}
}