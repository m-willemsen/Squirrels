package game;

import gnu.io.CommPortIdentifier;
import gnu.io.NoSuchPortException;

import java.util.Iterator;
import java.util.List;
import java.util.Scanner;

import org.zu.ardulink.Link;
import org.zu.ardulink.RawDataListener;
import org.zu.ardulink.event.ConnectionEvent;
import org.zu.ardulink.event.ConnectionListener;
import org.zu.ardulink.event.DigitalReadChangeEvent;
import org.zu.ardulink.event.DigitalReadChangeListener;
import org.zu.ardulink.event.DisconnectionEvent;

public class TestArduino {

	private static Link link;

	public static void main (String[] args){
		link = Link.getDefaultInstance();
//		try {
//			CommPortIdentifier port = CommPortIdentifier.getPortIdentifier("COM3");
//			
//		} catch (NoSuchPortException e1) {
//			// TODO Auto-generated catch block
//			e1.printStackTrace();
//		}
		List<String> ports = link.getPortList();
		Iterator<String> iter = ports.iterator();
		System.out.println(ports.size());
Link.getDefaultInstance().addRawDataListener(new RawDataListener() {
			
			@Override
			public void parseInput(String id, int numBytes, int[] message) {
				StringBuilder build = new StringBuilder(numBytes + 1);
				for (int i = 0; i < numBytes; i++) {
					build.append((char)message[i]);
				}
				System.out.println(build.toString());
			}
		});
		link.addDigitalReadChangeListener(new DigitalReadChangeListener() {

			@Override
			public void stateChanged(DigitalReadChangeEvent e) {
				System.out.println("bla");
			}

			@Override
			public int getPinListening() {
				System.out.println("bla2");
				return 8;
			}
		});

		link.addRawDataListener(new RawDataListener() {

			@Override
			public void parseInput(String id, int numBytes, int[] message) {
				System.out.println("bla3");
			}
		});
		
		link.addConnectionListener(new ConnectionListener(){

			@Override
			public void connected(ConnectionEvent arg0) {
				System.out.println("21");
			}

			@Override
			public void disconnected(DisconnectionEvent arg0) {
				System.out.println("22");
			}
			
		});
		
		link.addDigitalReadChangeListener(new DigitalReadChangeListener(){

			@Override
			public int getPinListening() {
				System.out.println("11");
				return 0;
			}

			@Override
			public void stateChanged(DigitalReadChangeEvent arg0) {
				System.out.println("12");
				
			}
			
		});
		link.connect("COM3", 9600);
		Thread t = new Thread(){
			public void run(){
				Scanner s = new Scanner(System.in);
				while (s.hasNextLine()){
				link.writeSerial(s.nextLine());
				}
				s.close();
			}
		};
		t.start();
		System.out.println("Now start listening");
	}
	
	
}
