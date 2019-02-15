package SoapExample;

import java.util.Set;

import javax.xml.namespace.QName;
import javax.xml.soap.SOAPMessage;
import javax.xml.ws.handler.MessageContext;
import javax.xml.ws.handler.soap.SOAPHandler;
import javax.xml.ws.handler.soap.SOAPMessageContext;

public class SoapHandler implements SOAPHandler<SOAPMessageContext>
{

  @Override
  public boolean handleMessage(SOAPMessageContext soapMessageContext){
    try{
      SOAPMessage message = soapMessageContext.getMessage();
      // I haven't tested this
      message.getSOAPHeader().setPrefix("soapenv");
      soapMessageContext.setMessage(message);
    }
    catch (Exception e) {
    }

    return true;
  }

@Override
public void close(MessageContext arg0) {
	// TODO Auto-generated method stub
	
}

@Override
public boolean handleFault(SOAPMessageContext arg0) {
	// TODO Auto-generated method stub
	return false;
}

@Override
public Set<QName> getHeaders() {
	// TODO Auto-generated method stub
	return null;
}

}