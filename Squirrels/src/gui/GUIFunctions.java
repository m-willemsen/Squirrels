package gui;

import java.awt.Component;
import java.awt.Desktop;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JOptionPane;

import voip.SkypeLocalLibrary;

import com.skype.Call;
import com.skype.SkypeException;

import exceptions.FriendNotFoundException;
import global.Functions;

public class GUIFunctions extends Functions {

	public static ActionListener defaultActionListener() {
		return new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent arg0) {
				boolean confirmed = confimationMessage("Are you sure you want to call this friend?");
				if (confirmed && arg0.getSource() instanceof JButton) {
					SkypeLocalLibrary skype = new SkypeLocalLibrary();
					try {
						Call c = skype.startCall(arg0.getActionCommand());
						c.setSendVideoEnabled(true);
						c.setReceiveVideoEnabled(true);
						TangibleVirtualGame.lastCall = c;
					} catch (SkypeException | FriendNotFoundException e) {
						errorHandler(e);
					}
				}
			}

		};
	}

	private static boolean confimationMessage(String message) {
		return JOptionPane.showConfirmDialog(TangibleVirtualGame.frame, message) == JOptionPane.OK_OPTION;
	}

	public static void openWebsite(String url) {
		try {
			URI uri = new URI(url);
			Desktop dt = Desktop.getDesktop();
			dt.browse(uri);
		} catch (IOException | URISyntaxException e) {
			errorHandler(e);
		}
	}
	public static void createFrame(Component panel) {
		createFrame(panel, JFrame.EXIT_ON_CLOSE);
	}

	public static void createFrame(Component panel, int defCloseOperation) {
		// Start by creating a frame
		TangibleVirtualGame.frame = new JFrame(TangibleVirtualGame.GAME_TITLE);
		// Create a panel
		panel.setVisible(true);
		panel.setEnabled(true);
		// Put it into a frame
		TangibleVirtualGame.frame.add(panel);
		TangibleVirtualGame.frame.setDefaultCloseOperation(defCloseOperation);
		TangibleVirtualGame.frame.setExtendedState(JFrame.MAXIMIZED_BOTH);
		TangibleVirtualGame.frame.setVisible(true);
	}

}
