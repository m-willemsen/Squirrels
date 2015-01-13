package game;

import global.Functions;
import gui.GUI;

import java.util.Arrays;

import javax.swing.JOptionPane;

import com.skype.SkypeException;

public class GameHandler extends Functions {
	private Protocol p;
	/**
	 * The last message that has been send from this client.
	 */
	private String messageSend;
	/**
	 * The current position of the pawn of the real player on this side of the application
	 */
	public int positionMyPawn;
	private int finalPosition = 25;
	private int[] positionsWithCurrentQuestions = new int[] { 1, 6, 12, 18, 24 };
	public int positionOpponentPawn;
	//private ReentrantLock lock = new ReentrantLock();
	
	private GUI g;
	private boolean myTurn;

	/**
	 * Create a new gameHandler
	 * @param g The gui.
	 */
	public GameHandler(GUI g) {
		super(g);
		this.g = g;
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
		positionMyPawn = 0; //own pawn
		doMove(0); //Opponents pawn
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
		
		//TODO start something here, that will monitor the game
	}

	/**
	 * Set the pawn of the opponent. Also change the turn.
	 * @param newLocation the new location for the pawn
	 */
	private void doMove(int newLocation) {
		positionOpponentPawn = newLocation;
		System.out.println("We need to move the opponents pawn to " + newLocation);
		//Now move the real piece to this position
		checkFinish();
		myTurn = true;
		g.gf.refreshGameScreen();
		//TODO do this on the real game
	}
	
	/**
	 * Should be triggered when the game senses that a move has been made
	 * @param newLocation the new location of the pawn.
	 */
	public void playerDidMove(int newLocation){
		if (myTurn || newLocation==positionMyPawn){
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

}
