package gui;

import global.Functions;

import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Frame;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.HashMap;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import javax.swing.JTextField;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import org.eclipse.swt.custom.CCombo;

import voip.SkypeLocalLibrary;

import com.skype.Call;
import com.skype.Friend;
import com.skype.SkypeException;

import game.GameHandler;
import game.Protocol;

public class GUI {
	public static final String GAME_TITLE = "TangibleVirtualGame";

	protected static final String MORE_INFORMATION = "This is more information";

	public static Call lastCall;

	private static HashMap<String, Component> friendsListOnPanel;
	
	public SkypeLocalLibrary skype = new SkypeLocalLibrary(this);
	
	public GameHandler gh;
	
	public GUIFunctions gf = new GUIFunctions(this);
	
	//screen
	private Dimension screen = Toolkit.getDefaultToolkit().getScreenSize();
	
	//frame
	private JFrame frame;
	
	//tabs
	protected JTabbedPane tabs;
	private JPanel tabContacts, tabInfo, tabGame;
	
	
	//contacts
	private JPanel searchPanel;
	private JButton invite, search;
	private JList contactList;
	private JTextField searchField;
	
	
	public GUI() throws Exception{
		frame = new JFrame(GAME_TITLE);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		tabs = new JTabbedPane();
		
		//contacts tab
		tabContacts = new JPanel(new BorderLayout());
		GridBagConstraints c = new GridBagConstraints();
		invite = new JButton("Invite");
		contactList = new JList(skype.getContacts());
		contactList.addListSelectionListener(new ListSelectionListener(){

			@Override
			public void valueChanged(ListSelectionEvent e) {
				invite.setActionCommand(((Friend)contactList.getSelectedValue()).getId());
			}
			
		});
		
		invite.addActionListener(gf.defaultActionListener());
		search = new JButton("Search");
		searchField = new JTextField();
		searchField.setColumns(30);
		
		search.addActionListener(new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent e){
				try{
					contactList.setListData(skype.getContacts(searchField.getText()));
				} catch(Exception e1){
					Functions.errorHandler(e1);
				}
				
			}
		});
		
		c.gridwidth = 3;
		c.gridy = 1;
		c.fill = 1;
		tabContacts.add(contactList, BorderLayout.CENTER);
		c.gridwidth = 1;
		c.gridy = 0;
		c.gridx = 0;
		searchPanel = new JPanel(new BorderLayout());
		searchPanel.add(invite, BorderLayout.WEST);
		c.gridx = 1;
		searchPanel.add(searchField, BorderLayout.CENTER);
		c.gridx = 2;
		searchPanel.add(search, BorderLayout.EAST);
		tabContacts.add(searchPanel, BorderLayout.NORTH);
		
		
		//info tab
		tabInfo = new JPanel();
		
		//game tab
		tabGame = gf.gameScreen();
		
		tabs.addTab("Contacts", tabContacts);
		tabs.addTab("Info", tabInfo);
		tabs.addTab("Game", tabGame);
		frame.add(tabs);
		frame.setSize(600,480);
		frame.setExtendedState(JFrame.MAXIMIZED_BOTH);
		frame.setVisible(true);
	}
}
