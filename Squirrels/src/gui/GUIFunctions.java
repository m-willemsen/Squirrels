package gui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Font;
import java.awt.Frame;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

import voip.SkypeLocalLibrary;

import com.skype.Call;
import com.skype.SkypeException;

import exceptions.FriendNotFoundException;
import game.GameHandler;
import game.Protocol;
import global.Functions;

public class GUIFunctions extends Functions {

	public static ActionListener defaultActionListener() {
		return new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent arg0) {
				boolean confirmed = confimationMessage("Are you sure you want to call this friend?");
				if (confirmed && arg0.getSource() instanceof JButton) {
					try {
						SkypeLocalLibrary skype = new SkypeLocalLibrary();
						Call c = skype.startCall(arg0.getActionCommand());
						TangibleVirtualGame.lastCall = c;
						System.out.println("Call created, now start the game");
						TangibleVirtualGame.frame.dispose();
						createFrame(gameScreen());
						c.setSendVideoEnabled(true);
					} catch (SkypeException | FriendNotFoundException | exceptions.SkypeException e) {
						errorHandler(e);
					}
				}
			}

		};
	}

	protected static Component gameScreen() {
		Component panel = new JPanel();
		panel.setSize(Frame.WIDTH, Frame.HEIGHT);
		JPanel p = (JPanel) panel;
		JButton startButton = new JButton("Start!!");
		startButton.addActionListener(new ActionListener(){

			@Override
			public void actionPerformed(ActionEvent arg0) {
				GameHandler gh = new GameHandler();
				gh.sendCommand(Protocol.START, null);
			}
			
		});
		p.add(startButton);
		return panel;
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
						SkypeLocalLibrary skype = new SkypeLocalLibrary();
						skype.toggleVideo(TangibleVirtualGame.lastCall);
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
