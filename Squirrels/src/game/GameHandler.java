package game;

import global.Functions;
import gnu.io.CommPortIdentifier;
import gnu.io.SerialPort;
import gnu.io.SerialPortEvent;
import gnu.io.SerialPortEventListener;
import gui.GUI;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.util.Arrays;
import java.util.Enumeration;
import java.util.List;
import java.util.Scanner;

import javax.swing.JOptionPane;

import org.zu.ardulink.Link;
import org.zu.ardulink.RawDataListener;

import com.skype.SkypeException;

public class GameHandler extends Functions implements SerialPortEventListener {
	private Protocol p;
	/**
	 * The last message that has been send from this client.
	 */
	private String messageSend;
	/**
	 * The current position of the pawn of the real player on this side of the application
	 */
	public int positionMyPawn;
	private int finalPosition = 18;
	private int[] positionsWithCurrentQuestions = new int[] { 1, 6, 12, 18, 24 };
	public int positionOpponentPawn;
	//private ReentrantLock lock = new ReentrantLock();
	
	private GUI g;
	private boolean myTurn;
	private Link link;

	/**
	 * Create a new gameHandler
	 * @param g The gui.
	 */
	public GameHandler(GUI g) {
		super(g);
		this.g = g;
		initialize();
	}

	private RawDataListener arduinoListener() {
		return new RawDataListener(){

			@Override
			public void parseInput(String arg0, int arg1, int[] arg2) {
				System.out.println("INPUT ARRIVED");
				System.out.println("- "+arg0);
				System.out.println("- "+arg1);
				System.out.println("- "+Arrays.toString(arg2));
				playerDidMove(arg1);
			}
			
		};
	}

	/**
	 * A command is received through Skype
	 * @param commandMessage The message we received
	 * @return The command we will send in return
	 * @throws SkypeException throws this exception if it was thrown by one of the methods that are requested.
	 */
	public String receiveCommandsFromSkype(String commandMessage) throws SkypeException {
		//Create some variables
		String command = Protocol.getCommand(commandMessage);
		String[] params = Protocol.getParams(commandMessage);
		
		// You need to fill the hashmap of the protocol, if it is not done yet
		if (p == null) {
			p = new Protocol(g);
		}
		//Protocol created, now check which command is received and handle this
		if (Protocol.commandos.values().contains(command)) {
			boolean messageArrived = Protocol.checkMatch(commandMessage, messageSend);
			if (!messageArrived)
				throw new SkypeException("Send command "+messageSend+", but received "+commandMessage);
		}
		else if (command.equals(Protocol.DOMOVE)) {
			try {
				doMove(Integer.parseInt(Protocol.getParams(commandMessage)[0]));
			} catch (NumberFormatException e) {
				sendCommand(Protocol.ERROR, new String[] { commandMessage });
			}
			return sendAppropriateCommandBack(Protocol.DOMOVE, Protocol.getParams(commandMessage));
		}
		else if (command.equals(Protocol.RESET)) {
			reset();
			return sendAppropriateCommandBack(command, null);
		}
		else if (command.equals(Protocol.START)) {
			g.gameIsStarted = true;
			init();
			return sendAppropriateCommandBack(command, null);
		}
		else if (command.equals(Protocol.ERROR)) {
			String errorMessage = "No errormessage available";
			if (params.length>0){
				errorMessage = params[0];
			}
			String sendCommand = implode(Arrays.copyOfRange(params, 1, params.length), Protocol.DIVIDER);
			JOptionPane.showMessageDialog(g.frame, "An error occurred:\n The error message: "+errorMessage+".\n This error occurred during sending this command: "+sendCommand, "An error occurred", JOptionPane.ERROR_MESSAGE);
		}
		else {
			//This command is unknown, so send back an error.
			sendCommand(Protocol.ERROR,new String[]{"Unkown command received"});
		}
		return null;
	}

	/**
	 * Reset the game. Set both pawns at the startlocation (0).
	 */
	public void reset() {
		// Set all values to their startvalue
		positionMyPawn = 1; //own pawn
		doMove(1); //Opponents pawn
	}

	/**
	 * Initialize the game if it is not started yet.
	 * 	- Send the start command
	 *  - Set the turn
	 *  - Refresh the gamescreen
	 *  - Set both pawns at the startlocation
	 */
	public void init() {
		if (!g.gameIsStarted) {
			sendCommand(Protocol.START, null);
			myTurn = true;
			g.gf.refreshGameScreen();
			reset();
		}
	}

	/**
	 * Set the pawn of the opponent. Also change the turn.
	 * @param newLocation the new location for the pawn
	 */
	private void doMove(int newLocation) {
		positionOpponentPawn = newLocation;
		System.out.println("We need to move the opponents pawn to " + newLocation);
		//Now move the real piece to this position
		bw.write(Integer.toString(newLocation));
		bw.flush();
		checkFinish();
		myTurn = true;
		g.gf.refreshGameScreen();
	}
	
	/**
	 * Should be triggered when the game senses that a move has been made
	 * @param newLocation the new location of the pawn.
	 */
	public void playerDidMove(int newLocation){
		if (newLocation <= positionMyPawn || newLocation==1){}
		else if (myTurn || newLocation==positionMyPawn){
		positionMyPawn = newLocation;
		System.out.println("We have moved our own pawn to " + newLocation);
		checkQuestionType();
		checkFinish();
		myTurn = false;
		sendCommand(Protocol.DOMOVE, new String[]{Integer.toString(newLocation)});
		g.gf.refreshGameScreen();
		}
		else {
			JOptionPane.showMessageDialog(g.frame, "You made a move, but it was not your turn. Please put your pawn back at position "+positionMyPawn+".", "An error occured", JOptionPane.ERROR_MESSAGE);
		}
	}

	/**
	 * Check if it is a "current" question, so the cam should be turned off.
	 */
	private void checkQuestionType() {
		if (Arrays.binarySearch(positionsWithCurrentQuestions, positionMyPawn)>=0){
			//The current position is not in the array, so no problem
		}
		else {
			//Turn of the cam!!
			/* Since this is not supported anymore, we will just show a message to the players
			try {
				SkypeLocalLibrary skype = new SkypeLocalLibrary(g);
				skype.setVideoOn(false);
			} catch (SkypeException e) {
				errorHandler(e);
			}*/
			JOptionPane.showMessageDialog(g.frame, "For this question, you need to turn of your camera. Please do this before continuing.");
		}
	}

	/**
	 * Check if the game is finished
	 * @return true if the game is finished
	 */
	private boolean checkFinish() {
		if (positionMyPawn > finalPosition || positionOpponentPawn>finalPosition) {
			return true;
		}
		return false;
	}

	/**
	 * Send the command that confirms the received command
	 * @param command the received command
	 * @param parameters the parameters for the received command
	 * @return the command that is send
	 */
	public String sendAppropriateCommandBack(String command, String[] parameters) {
		// You need to fill the hashmap of the protocol, if it is not done yet
		if (p == null) {
			p = new Protocol(g);
		}
		return sendCommand(Protocol.commandos.get(command), parameters);
	}

	/**
	 * Send a command
	 * @param command
	 * @param parameters
	 * @return the command that is send
	 */
	public String sendCommand(String command, String[] parameters) {
		try {
			//lock.lock();
			if (command == null) {
				throw new SkypeException("No command found");
			}
			String message = Protocol.COMMAND_PREFIX + Protocol.DIVIDER + command;
			if (parameters != null)
				message += Protocol.DIVIDER + implode(parameters, Protocol.DIVIDER);
			if (command.equals(Protocol.START)){
				g.gameIsStarted=true;
				init();
			}
			g.skype.getChat().send(message);
			messageSend = message;
			return message;
		} catch (SkypeException | exceptions.SkypeException e) {
			errorHandler(e);
			return Protocol.COMMAND_PREFIX + Protocol.DIVIDER + Protocol.ERROR + Protocol.DIVIDER
					+ e.getLocalizedMessage();
		}
	}
	
	/**
	 * Check if it is my turn
	 * @return true if it is my turn
	 */
	public boolean isItMyTurn(){
		return myTurn;
	}
	
	/**ARDUINO STUFF**/
	private BufferedReader input;
	private OutputStream output;
	private static final int TIME_OUT = 2000;
	private static final int DATA_RATE = 9600;
	private static final String PORT_NAMES[] = {                  "/dev/tty.usbserial-A9007UX1", // Mac OS X
        "/dev/ttyUSB0", // Linux
        "COM3", // Windows
};
	private static Scanner s;
	SerialPort serialPort;
	private PrintWriter bw;
	
	public void initialize() {
	    CommPortIdentifier portId = null;
	    Enumeration portEnum = CommPortIdentifier.getPortIdentifiers();

	    //First, Find an instance of serial port as set in PORT_NAMES.
	    while (portEnum.hasMoreElements()) {
	        CommPortIdentifier currPortId = (CommPortIdentifier) portEnum.nextElement();
	        for (String portName : PORT_NAMES) {
	            if (currPortId.getName().equals(portName)) {
	                portId = currPortId;
	                break;
	            }
	        }
	    }
	    if (portId == null) {
	        JOptionPane.showMessageDialog(g.frame, "Could not find COM port. Please install javax.comm (see installation folder) and the Adruino IDE (see arduino.org)", "Error", JOptionPane.ERROR_MESSAGE);
	        return;
	    }

	    try {
	        serialPort = (SerialPort) portId.open(this.getClass().getName(),
	                TIME_OUT);
	        serialPort.setSerialPortParams(DATA_RATE,
	                SerialPort.DATABITS_8,
	                SerialPort.STOPBITS_1,
	                SerialPort.PARITY_NONE);

	        // open the streams
	        input = new BufferedReader(new InputStreamReader(serialPort.getInputStream()));
	        output = serialPort.getOutputStream();

	        serialPort.addEventListener(this);
	        serialPort.notifyOnDataAvailable(true);
	    } catch (Exception e) {
	        System.err.println(e.toString());
	    }
	    bw = new PrintWriter(output);
	    Thread wT = new Thread (){
	    	public void run(){
				Scanner s = new Scanner(System.in);
				while (s.hasNextLine()){
					bw.write(s.nextLine());
					bw.flush();
				}
				s.close();
			}
	    };
	    wT.start();
	}


	public synchronized void close() {
	    if (serialPort != null) {
	        serialPort.removeEventListener();
	        serialPort.close();
	    }
	}

	public synchronized void serialEvent(SerialPortEvent oEvent) {
	    if (oEvent.getEventType() == SerialPortEvent.DATA_AVAILABLE) {
	        try {
	            String inputLine=null;
	            if (input.ready()) {
	                inputLine = input.readLine();
	                            System.out.println(inputLine);
	                            try {
	                            playerDidMove(Integer.parseInt(inputLine));
	                            }catch(NumberFormatException e){
	                            	if (!inputLine.contains("I received:")){
	                            		errorHandler(e);
	                            	}
	                            }
	            }

	        } catch (Exception e) {
	            System.err.println(e.toString());
	        }
	    }
	    // Ignore all the other eventTypes, but you should consider the other ones.
	}

}
