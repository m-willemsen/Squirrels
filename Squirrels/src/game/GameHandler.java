package game;

import java.util.Arrays;
import java.util.concurrent.locks.ReentrantLock;

import javax.swing.JOptionPane;

import com.skype.SkypeException;

import voip.SkypeLocalLibrary;
import global.Functions;
import gui.GUI;
import gui.GUIFunctions;
import gui.TangibleVirtualGame;

public class GameHandler extends Functions {
	private Protocol p;
	/**
	 * The last message that has been send from this client.
	 */
	private String messageSend;
	/**
	 * The current position of the pawn of the real player on this side of the application
	 */
	private int positionMyPawn;
	private int finalPosition = 25;
	private int[] positionsWithCurrentQuestions = new int[] { 1, 6, 12, 18, 24 };
	private int positionOpponentPawn;
	//private ReentrantLock lock = new ReentrantLock();
	
	private GUI g;
	private boolean myTurn;

	public GameHandler(GUI g) {
		this.g = g;
	}

	public String receiveCommandsFromSkype(String commandMessage) throws SkypeException {
		//Create some variables
		String command = Protocol.getCommand(commandMessage);
		String[] params = Protocol.getParams(commandMessage);
		
		//Print for debugging
		System.out.println("Command received: " + commandMessage);
		System.out.println("Command=" + command);
		System.out.println("Params: " + implode(params, " - "));
		
		// You need to fill the hashmap of the protocol, if it is not done yet
		if (p == null) {
			p = new Protocol();
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
			System.out.println("Go back to the starting position");
			reset();
			return sendAppropriateCommandBack(command, null);
		}
		else if (command.equals(Protocol.START)) {
			System.out.println("CREATE NEW GAME");
			g.gameIsStarted = true;
			init();
			return sendAppropriateCommandBack(command, null);
		}
		else if (command.equals(Protocol.ERROR)) {
			System.out.println("WE RECEIVED AN ERROR");
			String errorMessage = "No errormessage available";
			if (params.length>0){
				errorMessage = params[0];
			}
			String sendCommand = implode(Arrays.copyOfRange(params, 1, params.length), Protocol.DIVIDER);
			System.out.println("Errormessage: "+errorMessage);
			System.out.println("sendCommand: "+sendCommand);
			
			JOptionPane.showMessageDialog(g.frame, "An error occurred:\n The error message: "+errorMessage+".\n This error occurred during sending this command: "+sendCommand, "An error occurred", JOptionPane.ERROR_MESSAGE);
		}
		else {
			//This command is unknown, so send back an error.
			sendCommand(Protocol.ERROR,new String[]{"Unkown command received"});
		}
		return null;
	}

	public void reset() {
		// Set all values to their startvalue
		positionMyPawn = 0; //own pawn
		doMove(0); //Opponents pawn
	}

	public void init() {
		System.out.println("gameIsStarted: "+g.gameIsStarted);
		if (!g.gameIsStarted) {
			sendCommand(Protocol.START, null);
			myTurn = true;
			g.gf.refreshGameScreen();
			reset();
		}
		
		//TODO start something here, that will monitor the game
	}

	private void doMove(int newLocation) {
		positionOpponentPawn = newLocation;
		System.out.println("We need to move to " + newLocation);
		//Now move the real piece to this position
		checkFinish();
		
		//TODO do this on the real game
	}
	
	/**
	 * Should be triggered when the game senses that a move has been made
	 * @param newLocation the new location of the pawn.
	 */
	public void playerDidMove(int newLocation){
		if (myTurn || newLocation==positionMyPawn){
		positionMyPawn = newLocation;
		System.out.println("We have moved to " + newLocation);
		checkQuestionType();
		checkFinish();
		myTurn = false;
		sendCommand(Protocol.DOMOVE, new String[]{Integer.toString(newLocation)});
		}
		else {
			JOptionPane.showMessageDialog(g.frame, "You made a move, but it was not your turn. Please put your pawn back at position "+positionMyPawn+".", "An error occured", JOptionPane.ERROR_MESSAGE);
		}
	}

	private void checkQuestionType() {
		if (Arrays.binarySearch(positionsWithCurrentQuestions, positionMyPawn)>=0){
			//The current position is not in the array, so no problem
		}
		else {
			//Turn of the cam!!
			try {
				SkypeLocalLibrary skype = new SkypeLocalLibrary(g);
				skype.setVideoOn(false);
			} catch (SkypeException e) {
				errorHandler(e);
			}
		}
	}

	private boolean checkFinish() {
		if (positionMyPawn > finalPosition || positionOpponentPawn>finalPosition) {
			return true;
		}
		return false;
	}

	public String sendAppropriateCommandBack(String command, String[] parameters) {
		// You need to fill the hashmap of the protocol, if it is not done yet
		if (p == null) {
			p = new Protocol();
		}
		return sendCommand(Protocol.commandos.get(command), parameters);
	}

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
				System.out.println("Set game is started to true");
				g.gameIsStarted=true;
				init();
			}
			System.out.println("SEND THIS: " + message);
			g.skype.getChat().send(message);
			messageSend = message;
			return message;
		} catch (SkypeException | exceptions.SkypeException e) {
			errorHandler(e);
			return Protocol.COMMAND_PREFIX + Protocol.DIVIDER + Protocol.ERROR + Protocol.DIVIDER
					+ e.getLocalizedMessage();
		}
	}
	
	public boolean isItMyTurn(){
		return myTurn;
	}

}
