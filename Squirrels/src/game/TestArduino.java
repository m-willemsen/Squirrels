package game;

import java.util.Iterator;
import java.util.List;

import org.zu.ardulink.Link;
import org.zu.ardulink.RawDataListener;
import org.zu.ardulink.event.DigitalReadChangeEvent;
import org.zu.ardulink.event.DigitalReadChangeListener;

public class TestArduino {

	public static void main (String[] args){
		Link link = Link.getDefaultInstance();
		List<String> ports = link.getPortList();
		Iterator<String> iter = ports.iterator();
		System.out.println(ports.size());
		while(iter.hasNext()){
			System.out.println(iter.next());
		}
		link.connect("COM3");
		
		link.addDigitalReadChangeListener(new DigitalReadChangeListener() {

			@Override
			public void stateChanged(DigitalReadChangeEvent e) {
				System.out.println("bla");
			}

			@Override
			public int getPinListening() {
				System.out.println("bla2");
				return 11;
			}
		});

		link.addRawDataListener(new RawDataListener() {

			@Override
			public void parseInput(String id, int numBytes, int[] message) {
				System.out.println("bla3");
			}
		});
	}
	
	
}
