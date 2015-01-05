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
	private GUI g;
	private SkypeLocalLibrary skype;
	
	public GUIFunctions(GUI g){
		this.g = g;
		try {
			skype = new SkypeLocalLibrary(g);
		} catch (SkypeException e) {
			errorHandler(e);
		}
	}

	public ActionListener defaultActionListener() {
		return new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent arg0) {
				boolean confirmed = confimationMessage("Are you sure you want to call this friend?");
				if (confirmed && arg0.getSource() instanceof JButton) {
					try {
						Call c = skype.startCall(arg0.getActionCommand());
						g.lastCall = c;
						System.out.println("Call created, now start the game");
						g.gf.refreshGameScreen();
						c.setSendVideoEnabled(true);
					} catch (SkypeException | FriendNotFoundException | exceptions.SkypeException e) {
						errorHandler(e);
					}
				}
			}

		};
	}
	
	public void refreshGameScreen(){
		System.out.println("herman");
		JPanel herman = gameScreen();
		g.tabs.remove(2);
		g.tabs.addTab("Game", herman);
	}

	public JPanel gameScreen() {
		JPanel tabGame = new JPanel();
		if (GUI.lastCall == null){
			tabGame.add(new JLabel("You need to make a call first"));
		}
		else {
		tabGame.setSize(Frame.WIDTH, Frame.HEIGHT);
		JPanel p = (JPanel) tabGame;
		JButton startButton = new JButton("Start!!");
		startButton.addActionListener(new ActionListener(){

			@Override
			public void actionPerformed(ActionEvent arg0) {
				g.gh.sendCommand(Protocol.START, null);
			}
			
		});
		p.add(startButton);
		JButton move = new JButton("DO MOVE");
		move.addActionListener(new ActionListener(){

			@Override
			public void actionPerformed(ActionEvent arg0) {
				g.gh.sendCommand(Protocol.DOMOVE, new String[]{"10"});
			}
			
		});
		p.add(move);
		JButton reset = new JButton("Reset");
		reset.addActionListener(new ActionListener(){

			@Override
			public void actionPerformed(ActionEvent arg0) {
				g.gh.sendCommand(Protocol.RESET, null);
			}
			
		});
		p.add(reset);
		}
		return tabGame;
	}

	public static boolean confimationMessage(String message) {
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
	
	public JButton toggleVideoButton(){
		JButton button = null;
		try {
			button = new JButton("Turn video "+returnIf(TangibleVirtualGame.lastCall.isSendVideoEnabled(), "off", "on"));
			button.addActionListener(new ActionListener(){

				@Override
				public void actionPerformed(ActionEvent arg0) {
					try {
						SkypeLocalLibrary skype = new SkypeLocalLibrary(g);
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
