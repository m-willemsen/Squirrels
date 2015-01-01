package gui;

import java.awt.Component;
import java.awt.Frame;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map.Entry;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;

import com.skype.Call;
import com.skype.Friend;
import com.skype.Skype;
import com.skype.SkypeException;

import exceptions.RequirementsNotMetException;
import global.Functions;
import voip.SkypeLocalLibrary;

public class TangibleVirtualGame extends Functions {
	public static final String GAME_TITLE = "TangibleVirtualGame";

	public static JFrame frame;

	protected static Call lastCall;

	public static void main(String[] args) {
		try {
			GUIFunctions.createFrame(startScreen());
		} catch (RequirementsNotMetException e) {
			System.out.println("Requirements are not met");
			e.printStackTrace();
			e.printStackTrace(logginStream);
		}
	}

	public static Component startScreen() throws RequirementsNotMetException {
		runPreProgramChecks();
		Component panel = new JPanel();
		panel.setSize(Frame.WIDTH, Frame.HEIGHT);
		JPanel p = (JPanel) panel;
		GridLayout layout = new GridLayout();
		layout.setColumns(3);
		p.setLayout(layout);
		JButton contactButton = new JButton("View Contacts");
		contactButton.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent arg0) {
				GUIFunctions.createFrame(contactScreen(), JFrame.DISPOSE_ON_CLOSE);
			}

		});
		
		p.add(contactButton);
		JButton contactSearchButton = new JButton("Search Contacts");
		contactSearchButton.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent arg0) {
				GUIFunctions.createFrame(contactSearchScreen(), JFrame.DISPOSE_ON_CLOSE);
			}

		});
		p.add(contactSearchButton);
		JButton moreInfoButton = new JButton("More Information");
		p.add(moreInfoButton);
		// JButton downloadSkypeButton = new JButton("Download Skype");
		return panel;
	}

	private static boolean runPreProgramChecks() throws RequirementsNotMetException {
		HashMap<String, String> errors = new HashMap<String, String>();
		//Check if skype is installed
		if (!Skype.isInstalled()){
			errors.put("Install Skype", "You need to install skype to be able to run this program");
		}
		//Check if skype is started
		try {
			if (!Skype.isRunning()){
				errors.put("Start Skype", "You need to start skype before starting this program");
			}
		} catch (SkypeException e) {
			errorHandler(e);
			errors.put(e.getMessage(), Arrays.toString(e.getStackTrace()));
		}
		//Check if there is an internet connection
		if(!checkInternetConnection()){
			errors.put("Create internet connection", "You need an internet connection to be able to run this program");
		}
		//Now check if there are any errors.
		if (errors.size()>0){
			String errormessage = "Please resolve these errors:\n"+showErrors(errors);
			System.out.println(errormessage);
			JOptionPane.showMessageDialog(null, errormessage, "There were errors while starting this program", JOptionPane.ERROR_MESSAGE);
			throw new RequirementsNotMetException(errormessage);
		}
		return true;
	}



	private static String showErrors(HashMap<String, String> errors) {
		String result = "";
		Iterator<Entry<String, String>> iter = errors.entrySet().iterator();
		while(iter.hasNext()){
			Entry<String, String> entry = iter.next();
			result+="- "+entry.getKey()+": "+entry.getValue()+"\n";
		}
		return result;
	}



	public static Component contactScreen() {
		Component panel = new JPanel();
		panel.setSize(Frame.WIDTH, Frame.HEIGHT);
		SkypeLocalLibrary skype = new SkypeLocalLibrary();
		try {
			for (Friend friend : skype.getContacts()) {
				((JPanel) panel).add(skype.createIntoButton(friend, GUIFunctions.defaultActionListener()));
			}
		} catch (SkypeException e) {
			errorHandler(e);
		}
		return panel;
	}

	public static Component contactSearchScreen() {
		HashMap<String, Component> friendsListOnPanel = new HashMap<String, Component>();
		Component panel = new JPanel();
		panel.setSize(Frame.WIDTH, Frame.HEIGHT);
		JPanel p = (JPanel) panel;
		SkypeLocalLibrary skype = new SkypeLocalLibrary();

		// Create search textfield
		JTextField tf = new JTextField();
		tf.setColumns(30);
		ActionListener listener = new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent arg0) {
				// Create buttons for results
				try {
					//Remove all previous results
					Iterator<Entry<String, Component>> iter = friendsListOnPanel.entrySet().iterator();
					while (iter.hasNext()){
						p.remove(iter.next().getValue());
					}
					Friend[] friends = skype.getContacts(tf.getText());
					if (friends.length > 0) {
						//Add the new results
						for (Friend friend : friends) {
							JButton bt = skype.createIntoButton(friend, GUIFunctions.defaultActionListener());
							friendsListOnPanel.put(friend.getId(), bt);
							p.add(bt);
						}
					} else {
						JLabel bt = new JLabel("There were no results");
						p.add(bt);
						friendsListOnPanel.put("none", bt);
					}
					p.invalidate();p.repaint();p.setVisible(true);frame.invalidate();frame.repaint();frame.setVisible(true);
				} catch (SkypeException e) {
					errorHandler(e);
				}
			}
		};
		tf.addActionListener(listener);
		p.add(tf);
		// Create search button
		JButton submit = new JButton("Search!");
		submit.setActionCommand(tf.getText());
		p.add(submit);
		// Create actionlistener for search button
		submit.addActionListener(listener);
		return panel;
	}

}