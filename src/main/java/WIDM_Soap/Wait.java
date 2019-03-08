package syntel.steps;



import syntel.hwdriver.ExecutionInstance;
import syntel.hwdriver.Sessions;

public class Wait {
	String SessionID="";
	private Sessions ses;	

	public Wait(String sessionID) 
	{
		super();
		SessionID = sessionID;
		ses = ExecutionInstance.getSession(SessionID);	
	}

	private String getTestData(String string,int Count)
	{
		System.out.println(string);
		System.out.println(ses.getVariable(string));
		if(ses.getVariable(string)!=null)
		{
			String[] var=ses.getVariable(string).split(";");
			if(var.length>Count)
				return var[Count];
			else
				return "";
		}
		else
			return "";
	}

	public void w_wait() throws Exception
	{
		System.out.println("Waiting for 10 sec");
		Thread.sleep(1000);
		System.out.println("Wait task Completed");
	}

}

