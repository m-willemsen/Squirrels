package voip;

import java.awt.event.ActionListener;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Map.Entry;
import java.util.Scanner;

import javax.swing.JButton;

import com.skype.Application;
import com.skype.Call;
import com.skype.CallListener;
import com.skype.Chat;
import com.skype.ChatMessage;
import com.skype.ChatMessageListener;
import com.skype.Friend;
import com.skype.Skype;
import com.skype.SkypeClient;
import com.skype.SkypeException;
import com.skype.Stream;
import com.skype.User;
import com.skype.connector.Connector;
import com.skype.connector.ConnectorException;
import com.skype.connector.ConnectorListener;
import com.skype.connector.ConnectorMessageEvent;
import com.skype.connector.ConnectorStatusEvent;

import exceptions.FriendNotFoundException;
import game.GameHandler;
import game.Protocol;
import global.Functions;
import gui.GUIFunctions;
import gui.TangibleVirtualGame;

public class SkypeLocalLibrary extends Functions {
	private boolean videoStatus = true;
	private ConnectorListener listener;
	private Application app;

	public Friend[] getContacts() throws SkypeException, exceptions.SkypeException {
		checkConditions();
		Friend[] contacts = Skype.getContactList().getAllFriends();
		Arrays.sort(contacts, getContactComparator());
		LinkedList<Friend> onlineContacts = new LinkedList<Friend>();
		for (Friend contact : contacts) {
			if (contact.isAuthorized() && !contact.getOnlineStatus().equals(User.Status.OFFLINE)) {
				// Now that we know that the user is online and is accepted in
				// skype, we want to know if it is not a facebook contact
				if (!contact.getId().contains("chat.facebook.com")) {
					onlineContacts.add(contact);
					logginStream.println(contactToString(contact));
				}
			}
		}
		Friend[] result = new Friend[onlineContacts.size()];
		for (int i = 0; i < onlineContacts.size(); i++) {
			result[i] = onlineContacts.get(i);
		}
		return result;
	}

	private Comparator<Friend> getContactComparator() {
		return new Comparator<Friend>() {

			@Override
			public int compare(Friend arg0, Friend arg1) {
				return arg0.getId().compareTo(arg1.getId());
			}

		};

	}

	// TODO - Onderstaande methoden moeten nog getest worden
	public String sendCommand(String command, String responseHeader) throws ConnectorException {
		String response = Connector.getInstance().executeWithId(command, responseHeader);
		Connector.getInstance().addConnectorListener(getConnectorListener());
		return response;
	}

	public ConnectorListener getConnectorListener() {
		if (listener == null) {
			listener = new ConnectorListener() {

				@Override
				public void messageReceived(ConnectorMessageEvent event) {
					// TODO Auto-generated method stub
					logginStream.println("messageReceived: " + event.getMessage());

				}

				@Override
				public void messageSent(ConnectorMessageEvent event) {
					// TODO Auto-generated method stub
					logginStream.println("messageSent: " + event.getMessage());

				}

				@Override
				public void statusChanged(ConnectorStatusEvent event) {
					// TODO Auto-generated method stub
					logginStream.println("statusChanged: " + event.getStatus());

				}

			};
		}
		return listener;
	}

	private String contactToString(Friend contact) {
		String s = "---------------------------------------\n";
		String d = " \n ";
		String e = "\n---------------------------------------\n";
		try {
			return s + "ID: " + contact.getId() + d + "DisplayName: " + contact.getDisplayName() + d + "FullName: "
					+ contact.getFullName() + d + "OnlineStatus: " + contact.getOnlineStatus() + d + "MoodMessage: "
					+ contact.getMoodMessage() + d + "isAuthorized: " + contact.isAuthorized() + d + "isVideoCapable: "
					+ contact.isVideoCapable() + e;
		} catch (SkypeException ex) {
			errorHandler(ex);
		}
		return "Unable to get any data";
	}

	public JButton createIntoButton(Friend friend, ActionListener listener) throws SkypeException,
			exceptions.SkypeException {
		checkConditions();
		String display = friend.getFullName();
		if (display == null || display.equals("") || display.equals("-")) {
			display = friend.getDisplayName();
		}
		if (display == null || display.equals("") || display.equals("-")) {
			display = friend.getId();
		}
		JButton button = new JButton(display);
		button.addActionListener(listener);
		button.setActionCommand(friend.getId());
		return button;
	}

	public Chat getChat(String friendId) throws SkypeException, exceptions.SkypeException {
		checkConditions();
		return Skype.getContactList().getFriend(friendId).chat();
	}

	public Chat getChat() throws SkypeException, exceptions.SkypeException {
		return getChat(TangibleVirtualGame.lastCall.getPartnerId());
	}

	public Friend getFriend(String friendId) throws SkypeException, FriendNotFoundException, exceptions.SkypeException {
		checkConditions();
		Friend res = Skype.getContactList().getFriend(friendId);
		if (res == null)
			throw new FriendNotFoundException("Could not find the friend with the id '" + friendId + "'.");
		return res;
	}

	public Call startCall(String friendId) throws SkypeException, FriendNotFoundException, exceptions.SkypeException {
		checkConditions();
		//check if call exists
		//TODO
//		Iterator<Entry<String, Call>> iter = Call.calls.entrySet().iterator();
//		while(iter.hasNext()){
//			Call c = iter.next().getValue();
//			
//			if (c.getPartner().getId().equals(friendId)){
//				return c;
//			}
//		}
		Call call = getFriend(friendId).call();
		SkypeClient.showSkypeWindow();
		SkypeClient.showChatWindow(friendId, "Zullen we een spelletje " + TangibleVirtualGame.GAME_TITLE
				+ " spelen? Daarvoor moet je wel het spel geinstalleerd hebben.");
		/*Scanner s = new Scanner(System.in);
		boolean closeChat = false;
		while (!closeChat) {
			if (s.hasNextLine()) {
				System.out.println("Typ hier een bericht om te verzenden: (of typ close)");
				String message = s.nextLine();
				if (message.equals("close")) {
					closeChat = true;
				}
				Skype.chat(friendId).send(message);
			}
		}
		s.close();*/
		return call;
	}

	public void toggleVideo(Call call) throws SkypeException {
		if (call == null){
			call = TangibleVirtualGame.lastCall;
		}
		boolean newvideoStatus = !videoStatus;
		videoStatus = newvideoStatus;
		call.setReceiveVideoEnabled(videoStatus);
	}

	private void checkConditions() throws exceptions.SkypeException, SkypeException {
		if (!Skype.isInstalled()) {
			throw new exceptions.SkypeException("Please install Skype from skype.com");
		}
		if (!Skype.isRunning()) {
			throw new exceptions.SkypeException("Please start Skype");
		}
	}

	public Friend[] getContacts(String skypeId) throws SkypeException, exceptions.SkypeException {
		checkConditions();
		Friend[] contacts = Skype.getContactList().getAllFriends();
		Arrays.sort(contacts, getContactComparator());
		LinkedList<Friend> onlineContacts = new LinkedList<Friend>();
		for (Friend contact : contacts) {
			if (contact.isAuthorized() && !contact.getOnlineStatus().equals(User.Status.OFFLINE)) {
				// Now that we know that the user is online and is accepted in
				// skype, we want to know if it is not a facebook contact
				if (!contact.getId().contains("chat.facebook.com")) {
					// Finally check if it matches the search criteria
					if (contact.getId().contains(skypeId)) {
						onlineContacts.add(contact);
						logginStream.println(contactToString(contact));
					}
				}
			}
		}
		Friend[] result = new Friend[onlineContacts.size()];
		for (int i = 0; i < onlineContacts.size(); i++) {
			result[i] = onlineContacts.get(i);
		}
		return result;
	}

	public void initSkype() throws SkypeException {
		try {
			checkConditions();
		} catch (exceptions.SkypeException e) {
			errorHandler(e);
		}
		Skype.addCallListener(new CallListener() {

			@Override
			public void callReceived(Call receivedCall) throws SkypeException {
				TangibleVirtualGame.lastCall = receivedCall;
				if(GUIFunctions.confimationMessage("You are being called by "+receivedCall.getPartner().getFullName()+". Would you like to play a game with him/her?")){
					receivedCall.answer();
					GUIFunctions.createFrame(GUIFunctions.gameScreen());
				}
				else {
					receivedCall.cancel();
					TangibleVirtualGame.lastCall = null;
				}
				System.out.println("callReceived triggered: " + receivedCall.getPartnerId());
			}

			@Override
			public void callMaked(Call makedCall) throws SkypeException {
				System.out.println("callMaked triggered: " + makedCall.getPartnerId());
			}

		});
		//Skype.removeChatMessageListener(getDefaultChatMessageListener());
		if (Skype.chatMessageListeners.size()==0){
		Skype.addChatMessageListener(getDefaultChatMessageListener());
		System.out.println(Arrays.toString(Skype.chatMessageListeners.toArray()));
		}
		// try {
		//
		// System.out.println("REPLY="+Connector.getInstance().executeWithId("DOMOVE 2",
		// "DIDMOVE"));
		// System.out.println("TEST IETSS NIEUWS");
		// System.out.println(sendCommand("DOMOVE 2", "DIDMOVE"));
		//
		// } catch (ConnectorException e) {
		// // TODO Auto-generated catch block
		// e.printStackTrace();
		// }
		// app =
		// Skype.addApplication(Connector.getInstance().getApplicationName());
		// Stream[] streams = app.getAllStreams();
		// System.out.println("There are "+streams.length+" streams for the game.");
		// for (Stream stream:streams){
		// stream.addStreamListener(new MyStreamlistener());
		// printStreamInfo(stream);
		// }
	}

	private ChatMessageListener getDefaultChatMessageListener() {
		return new ChatMessageListener() {

			@Override
			public void chatMessageReceived(ChatMessage receivedChatMessage) throws SkypeException {
				sendMessageToGame(receivedChatMessage);
				System.out.println("chatMessageReceived TRIGGERED: " + receivedChatMessage.getSenderId() + " - ZEGT: "
						+ receivedChatMessage.getContent());
			}

			@Override
			public void chatMessageSent(ChatMessage sentChatMessage) throws SkypeException {
				System.out.println("chatMessageSent TRIGGERED: " + sentChatMessage.getSenderId() + " - ZEGT: "
						+ sentChatMessage.getContent());
			}

		};
	}

	protected void sendMessageToGame(ChatMessage receivedChatMessage) {
		try {
			if (TangibleVirtualGame.lastCall!= null && receivedChatMessage.getContent().startsWith(Protocol.COMMAND_PREFIX)&&receivedChatMessage.getSenderId().equals(TangibleVirtualGame.lastCall.getPartnerId())){
				GameHandler gh = new GameHandler();
				gh.receiveCommandsFromSkype(receivedChatMessage.getContent());
			}
		} catch (SkypeException e) {
			errorHandler(e);
		}
	}

	private void printStreamInfo(Stream stream) {
		System.out.println("All info about stream " + stream.getId() + ":\n" + "app: " + stream.getApplication() + "\n"
				+ "friend: " + stream.getFriend().toString());
	}

	public SkypeLocalLibrary() throws SkypeException {
		initSkype();
	}

	public void setVideoOn(boolean status) throws SkypeException {
		TangibleVirtualGame.lastCall.setReceiveVideoEnabled(status);
		TangibleVirtualGame.lastCall.setSendVideoEnabled(status);
		
	}

	/*
	 * //DELETE THIS!! public static void main (String[] args){
	 * 
	 * } //STOP DELETING
	 */
}
