package game;

import java.util.Arrays;

import com.skype.Skype;

public class TestGame {
	public static void main (String[] args){
		GameHandler gh = new GameHandler();
		String command = (gh.sendCommand(Protocol.DOMOVE, new String[]{"2"}));
		String response = (gh.sendAppropriateCommandBack(Protocol.getCommand(command), Protocol.getParams(command)));
		//System.out.println(gh.receiveCommandsFromSkype(command));
		System.out.println(Protocol.checkMatch(response, command));
	}
}
