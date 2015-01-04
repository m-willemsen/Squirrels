package game;

import global.Functions;

import java.util.Arrays;
import java.util.HashMap;

public class Protocol extends Functions {
	public static final String COMMAND_PREFIX = "COMMAND";
	public static final String DIVIDER = "::";
	public static HashMap<String, String> commandos = new HashMap<String, String>();
	public static int COMMAND_INDEX = COMMAND_PREFIX.split(DIVIDER).length;

	//THese are the commands
	public static final String START = "START";
	public static final String DOMOVE = "DOMOVE";
	public static final String RESET = "RESET";
	public static final String ERROR = "ERROR";
	public static final String CONFIRM_ANSWER = "ANSWER";
	//End of commands
	public static String kvd = "->"; // Key-value divider
	public static String ed = "\n"; //Entry divider
	public static String datasource = DOMOVE+kvd+"DIDMOVE"+ed+
			START+kvd+"STARTED"+ed+
			CONFIRM_ANSWER+kvd+"ANSWERED"+ed+
			RESET+kvd+"RESETTED"+ed;
	
	public Protocol(){
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
