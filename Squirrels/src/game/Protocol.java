package game;

import global.Functions;
import gui.GUI;

import java.util.Arrays;
import java.util.HashMap;

public class Protocol extends Functions {
	public static final String COMMAND_PREFIX = "COMMAND_PREFIX";
	public static final String DIVIDER = "::";
	public static HashMap<String, String> commandos = new HashMap<String, String>();
	public static int COMMAND_INDEX = COMMAND_PREFIX.split(DIVIDER).length;

	//These are the commands
	/**
	 * This command will tell the other GameHandler to start a game
	 * @param none
	 */
	public static final String START = "START";
	
	/**
	 * This command will tell the other GameHandler to move the pawn x steps
	 * @param the new location of the pawn (note this must be an int, if not, it will return an error)
	 */
	public static final String DOMOVE = "DOMOVE";
	
	/**
	 * This command will tell the other GameHandler to execute the reset() method
	 * @param none
	 */
	public static final String RESET = "RESET";
	/**
	 * This command will tell the other GameHandler that there was an error in a command
	 * @param errormessage
	 * @param the received command
	 */
	public static final String ERROR = "ERROR";
	/**
	 * This command will tell the GameHandler what the answer to the previous question was.
	 * @param WRONG/RIGHT
	 */
	public static final String CONFIRM_ANSWER = "ANSWER";
	//End of commands
	public static String kvd = "->"; // Key-value divider
	public static String ed = "\n"; //Entry divider
	public static String datasource = DOMOVE+kvd+DOMOVE+"ED"+ed+
			START+kvd+START+"ED"+ed+
			CONFIRM_ANSWER+kvd+CONFIRM_ANSWER+"ED"+ed+
			RESET+kvd+RESET+"ED"+ed;
	
	public Protocol(GUI g){
		super(g);
		fillCommandoHashMap();
	}

	private String fillCommandoHashMap() {
		String[] lines = datasource.split(ed);
		for (String line:lines){
			String[] keyenvalue = line.split(kvd);
			commandos.put(keyenvalue[0], keyenvalue[1]);
		}
		return showHashMap(commandos);
	}
	
	public static String getCommand(String commandMessage){
		String[] commandArray = commandMessage.split(Protocol.DIVIDER);
		int commandindex = Protocol.COMMAND_INDEX;
		String command = commandArray[commandindex];
		return command;
		
	}
	
	public static String[] getParams(String commandMessage){
		String[] arr = commandMessage.split(DIVIDER);
		return Arrays.copyOfRange(arr, COMMAND_INDEX+1, arr.length);
	}

	public static boolean checkMatch(String commandMessage, String messageSend) {
		System.out.println("Compare "+commandMessage+" with this one "+messageSend);
		return (commandos.get(getCommand(messageSend)).equals(getCommand(commandMessage)));
	}
}
