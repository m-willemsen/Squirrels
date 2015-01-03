package voip;

import com.skype.SkypeException;
import com.skype.StreamListener;

public class MyStreamlistener implements StreamListener {

	@Override
	public void textReceived(String receivedText) throws SkypeException {
		System.out.println("RECIEVED NEW MESSAGE:: "+receivedText);
	}

	@Override
	public void datagramReceived(String receivedDatagram) throws SkypeException {
		System.out.println("RECIEVED NEW DATAGRAM:: "+receivedDatagram);
	}

}
