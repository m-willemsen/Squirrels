package gui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Desktop;
import java.awt.Font;
import java.awt.Frame;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

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
					} catch (SkypeException | FriendNotFoundException | exceptions.SkypeException e) {
						errorHandler(e);
					}
				}
			}

		};
	}

	private static boolean confimationMessage(String message) {
		return JOptionPane.showConfirmDialog(TangibleVirtualGame.frame, message) == JOptionPane.OK_OPTION;
	}
	
	public static void createFrame(Component panel) {
		createFrame(panel, JFrame.EXIT_ON_CLOSE, null);
	}
	public static void createFrame(Component panel, int defCloseOperation) {
		createFrame(panel, defCloseOperation, null);
	}

	public static void createFrame(Component panel, int defCloseOperation, String title) {
		// Start by creating a frame
		TangibleVirtualGame.frame = new JFrame(TangibleVirtualGame.GAME_TITLE);
		// Create a title (if needed)
		if(title != null){
			JPanel titlePanel = new JPanel();
			JLabel titleLabel = new JLabel(title);
			System.out.println("titleLabel.getText()="+titleLabel.getText());
			titlePanel.setSize(TangibleVirtualGame.frame.getWidth(), 150);
			titlePanel.setAlignmentX(Frame.CENTER_ALIGNMENT);
			titlePanel.setAlignmentY(Frame.CENTER_ALIGNMENT);
			titlePanel.setBackground(Color.RED);
			titlePanel.add(titleLabel);
			
			titleLabel.setFont(new Font("Serif", Font.BOLD, 40));
			
			TangibleVirtualGame.frame.add(titlePanel, BorderLayout.PAGE_START);
		}
		// Create a panel
		panel.setVisible(true);
		panel.setEnabled(true);
		// Put it into a frame
		TangibleVirtualGame.frame.add(panel);
		TangibleVirtualGame.frame.setDefaultCloseOperation(defCloseOperation);
		TangibleVirtualGame.frame.setExtendedState(JFrame.MAXIMIZED_BOTH);
		TangibleVirtualGame.frame.setVisible(true);
	}
	
	public static JButton toggleVideoButton(){
		JButton button = null;
		try {
			button = new JButton("Turn video "+returnIf(TangibleVirtualGame.lastCall.isSendVideoEnabled(), "off", "on"));
			button.addActionListener(new ActionListener(){

				@Override
				public void actionPerformed(ActionEvent arg0) {
					try {
						SkypeLocalLibrary.toggleVideo(TangibleVirtualGame.lastCall);
					} catch (SkypeException e) {
						errorHandler(e);
					}
				}
				
			});
		} catch (SkypeException e) {
			errorHandler(e);
		}
		return button;
	}
}
