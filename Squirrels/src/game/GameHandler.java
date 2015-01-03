package game;

import com.skype.SkypeException;

import voip.SkypeLocalLibrary;
import global.Functions;
import gui.TangibleVirtualGame;

public class GameHandler extends Functions {
	private Protocol p;
	private String messageSend;

	//TODO Fix that this class will handle the game
	public GameHandler(){
		p = new Protocol();
		
	}
	
	public GameHandler(boolean isGameStarted){
		if (!isGameStarted){
			sendCommand(Protocol.START, null);
		}
	}
	
	public String receiveCommandsFromSkype(String commandMessage){
		System.out.println("Command received: "+commandMessage);
		String command = Protocol.getCommand(commandMessage);
		System.out.println("Command="+command);
		System.out.println("Params: "+implode(Protocol.getParams(commandMessage)," +++ "));
		if (Protocol.commandos.values().contains(command)){
			boolean messageArrived = Protocol.checkMatch(commandMessage, messageSend);
			
		}
		if (command.equals(Protocol.DOMOVE)){
			System.out.println("We need to move "+Protocol.getParams(commandMessage)[0]+" steps");
			return sendAppropriateCommandBack(Protocol.DOMOVE, Protocol.getParams(commandMessage));
		}
		if(command.equals(Protocol.RESET)){}
		if(command.equals(Protocol.START)){
			System.out.println("CREATE NEW GAME");
			new GameHandler(true);
			return sendAppropriateCommandBack(Protocol.START, null);
		}
		return null;
	}
	
	public String sendAppropriateCommandBack(String command, String[] parameters){
		return sendCommand(Protocol.commandos.get(command), parameters);
	}

	public String sendCommand(String command, String[] parameters) {
		try {
			SkypeLocalLibrary skype = new SkypeLocalLibrary();
			if (command == null){
				throw new SkypeException("No command found");
			}
			String message = Protocol.COMMAND_PREFIX+Protocol.DIVIDER+command;
			if (parameters != null)
				message+=Protocol.DIVIDER+implode(parameters, Protocol.DIVIDER);
			System.out.println("SEND THIS: "+message);
			skype.getChat().send(message);
			messageSend = message;
			return message;
		} catch (SkypeException | exceptions.SkypeException e) {
			errorHandler(e);
			return Protocol.COMMAND_PREFIX+Protocol.DIVIDER+Protocol.ERROR+Protocol.DIVIDER+e.getLocalizedMessage();
		}
	}

	
	
}
