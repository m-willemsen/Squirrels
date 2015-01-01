package voip;

import java.awt.event.ActionListener;
import java.util.Arrays;
import java.util.Comparator;
import java.util.LinkedList;

import javax.swing.JButton;

import com.skype.Call;
import com.skype.Chat;
import com.skype.Friend;
import com.skype.Skype;
import com.skype.SkypeException;
import com.skype.User;
import com.skype.connector.Connector;
import com.skype.connector.ConnectorException;
import com.skype.connector.ConnectorListener;
import com.skype.connector.ConnectorMessageEvent;
import com.skype.connector.ConnectorStatusEvent;

import exceptions.FriendNotFoundException;
import global.Functions;

public class SkypeLocalLibrary extends Functions {
	private static boolean videoStatus = true;
	private ConnectorListener listener;

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
	public void sendCommand(String command, String responseHeader) throws ConnectorException {
		Connector.getInstance().executeWithId(command, responseHeader);
		Connector.getInstance().addConnectorListener(getConnectorListener());
	}

	public ConnectorListener getConnectorListener() {
		if (listener == null) {
			listener = new ConnectorListener() {

				@Override
				public void messageReceived(ConnectorMessageEvent event) {
					// TODO Auto-generated method stub
					System.out.println(event.getMessage());

				}

				@Override
				public void messageSent(ConnectorMessageEvent event) {
					// TODO Auto-generated method stub
					System.out.println(event.getMessage());

				}

				@Override
				public void statusChanged(ConnectorStatusEvent event) {
					// TODO Auto-generated method stub
					System.out.println(event.getStatus());

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

	public JButton createIntoButton(Friend friend, ActionListener listener) throws SkypeException, exceptions.SkypeException {
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

	public Friend getFriend(String friendId) throws SkypeException, FriendNotFoundException, exceptions.SkypeException {
		checkConditions();
		Friend res = Skype.getContactList().getFriend(friendId);
		if (res == null)
			throw new FriendNotFoundException("Could not find the friend with the id '" + friendId + "'.");
		return res;
	}

	public Call startCall(String friendId) throws SkypeException, FriendNotFoundException, exceptions.SkypeException {
		checkConditions();
		return getFriend(friendId).call();
	}

	public static void toggleVideo(Call call) throws SkypeException {
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

	/*
	 * //DELETE THIS!! public static void main (String[] args){
	 * 
	 * } //STOP DELETING
	 */
}
