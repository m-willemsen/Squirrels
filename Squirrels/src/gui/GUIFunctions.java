	package gui;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Frame;
import java.awt.GridLayout;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SwingConstants;

import voip.SkypeLocalLibrary;

import com.skype.Call;
import com.skype.SkypeException;

import exceptions.FriendNotFoundException;
import game.Protocol;
import global.Functions;

public class GUIFunctions extends Functions {
	private GUI g;
	private JTextField nrOfSteps;

	public GUIFunctions(GUI g) {
		super(g);
		this.g = g;
		try {
			if (g.skype==null){
				g.skype = new SkypeLocalLibrary(g);
			}
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
						Call c = g.skype.startCall(arg0.getActionCommand());
						GUI.lastCall = c;
						System.out.println("Call created, now start the game");
						if (c.getStatus().equals(Call.Status.INPROGRESS))
						g.gh.init();
						g.gf.refreshGameScreen();
						//c.setSendVideoEnabled(true);
					} catch (SkypeException | FriendNotFoundException | exceptions.SkypeException e) {
						errorHandler(e);
					}
				}
			}

		};
	}

	public void refreshGameScreen() {
		System.out.println("herman");
		JPanel herman = gameScreen();
		try{
		g.tabs.remove(2);
		g.tabs.addTab("Game", herman);
		g.tabs.setSelectedIndex(2);
		}
		catch(ArrayIndexOutOfBoundsException e){
			refreshGameScreen();
		}
	}

	public JPanel gameScreen() {
		JPanel tabGame = new JPanel(new GridLayout(7,1));
		tabGame.setFont(g.font);
		if (GUI.lastCall == null) {
			g.frame.setSize(Toolkit.getDefaultToolkit().getScreenSize());
			tabGame.add(new JLabel("You need to make a call first"));
		} else {
			g.frame.setSize(new Dimension((int)(Toolkit.getDefaultToolkit().getScreenSize().getWidth() * 0.2), 
					(int)Toolkit.getDefaultToolkit().getScreenSize().getHeight()));
			tabGame.setSize(Frame.WIDTH, Frame.HEIGHT);
			JPanel p = tabGame;
			JLabel turn;
			if(g.gh.isItMyTurn())
				turn = new JLabel("It is your turn!",SwingConstants.CENTER);
			else
				turn = new JLabel("Wait for the other player, please.",SwingConstants.CENTER);
			turn.setBackground(Color.gray);
			turn.setFont(g.font);
			p.add(turn);
			JLabel myPawn;
			myPawn = new JLabel("Your location: "+g.gh.positionMyPawn,SwingConstants.CENTER);
			myPawn.setFont(g.font);
			myPawn.setBackground(Color.gray);
			p.add(myPawn);
			
			JLabel OtherPawn;
			OtherPawn = new JLabel("The other person's location: "+g.gh.positionOpponentPawn,SwingConstants.CENTER);
			OtherPawn.setFont(g.font);
			OtherPawn.setBackground(Color.gray);
			p.add(OtherPawn);
			
			JButton startButton = new JButton("Start!!");
			startButton.setFont(g.font);
			startButton.setBackground(Color.gray);
			startButton.addActionListener(new ActionListener() {

				@Override
				public void actionPerformed(ActionEvent arg0) {
					g.gh.init();
				}

			});
			p.add(startButton);
			
			nrOfSteps = new JTextField();
			nrOfSteps.setFont(g.font);
			final String placeholderText = "Fill in the number of steps";
			nrOfSteps.setText(placeholderText);
			nrOfSteps.addFocusListener(new FocusListener(){
		        @Override
		        public void focusGained(FocusEvent e){
		        	if (nrOfSteps.getText().equals(placeholderText))
		        		nrOfSteps.setText("");
		        }

				@Override
				public void focusLost(FocusEvent arg0) {
					if (nrOfSteps.getText().equals(""))
		        		nrOfSteps.setText(placeholderText);
				}
		    });
			nrOfSteps.setColumns(30);
			nrOfSteps.setBackground(Color.lightGray);
			ActionListener doMoveActionListener = new ActionListener() {

				@Override
				public void actionPerformed(ActionEvent arg0) {
					try {
						int steps = Integer.parseInt(nrOfSteps.getText());
						g.gh.playerDidMove(g.gh.positionMyPawn+steps);
					}
					catch(NumberFormatException e){
						errorHandler(e);
					}
					//g.gh.sendCommand(Protocol.DOMOVE, new String[] { "10" });
				}

			};
			nrOfSteps.addActionListener(doMoveActionListener);
			p.add(nrOfSteps);
			JButton move = new JButton("DO MOVE");
			move.setFont(g.font);
			move.setBackground(Color.gray);
			move.addActionListener(doMoveActionListener);
			p.add(move);
			JButton reset = new JButton("Reset");
			reset.setFont(g.font);
			reset.setBackground(Color.gray);
			reset.addActionListener(new ActionListener() {

				@Override
				public void actionPerformed(ActionEvent arg0) {
					g.gh.reset();
					g.gh.sendCommand(Protocol.RESET, null);
				}

			});
			p.add(reset);
		}
		return tabGame;
	}

	public boolean confimationMessage(String message) {
		return JOptionPane.showConfirmDialog(g.frame, message) == JOptionPane.OK_OPTION;
	}

	public JButton toggleVideoButton() {
		JButton button = null;
		try {
			button = new JButton("Turn video "
					+ returnIf(GUI.lastCall.isSendVideoEnabled(), "off", "on"));
			button.addActionListener(new ActionListener() {

				@Override
				public void actionPerformed(ActionEvent arg0) {
					try {
						SkypeLocalLibrary skype = new SkypeLocalLibrary(g);
						skype.toggleVideo(GUI.lastCall);
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
