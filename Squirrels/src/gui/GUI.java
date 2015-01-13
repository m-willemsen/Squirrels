package gui;

import game.GameHandler;
import global.Functions;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.DefaultListCellRenderer;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import javax.swing.JTextField;
import javax.swing.JTextPane;
import javax.swing.border.LineBorder;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import voip.SkypeLocalLibrary;

import com.skype.Call;
import com.skype.Friend;
import com.skype.SkypeException;

public class GUI {
	public static final String GAME_TITLE = "TangibleVirtualGame";

	protected static final String MORE_INFORMATION = "Welcom to our TangibleVirtualGame.\n\n"
			+ "How to play?\n"
			+ "First you have to start your Skype-program. If you don't have it yet, you can download it from skype.com.\n"
			+ "If you have done this, you need to start the game by inviting someone to play. \n"
			+ "The other person must run the game too, before inviting him. \n"
			+ "Now a pop-up will show from the game, and from Skype. \n"
			+ "You have to ignore the Skype pop-up and click \"Yes\" on the game pop-up. \n"
			+ "Now you will be able to talk to eachother. A Skype call has been made. \n"
			+ "You can now start playing. The system will show if it is your turn or not.";

	public static Call lastCall;

	public SkypeLocalLibrary skype;

	public GameHandler gh = new GameHandler(this);

	public boolean gameIsStarted = false;

	public GUIFunctions gf = new GUIFunctions(this);

	// frame
	public JFrame frame;

	// tabs
	protected JTabbedPane tabs;
	private JPanel tabContacts, tabInfo, tabGame;

	// contacts
	private JPanel searchPanel;
	private JButton invite, search;
	private JList<Friend> contactList;
	private JTextField searchField;
	private ActionListener searchListener;

	public GUI() {// throws Exception {
		try {
			skype = new SkypeLocalLibrary(this);
		} catch (SkypeException e) {
			System.out.println("An error occurred. It should be handled. Error: "+e.getLocalizedMessage());
			Functions f = new Functions(this);
			f.errorHandler(e);
		}
		System.out.println("Skype local library created");
		frame = new JFrame(GAME_TITLE);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		tabs = new JTabbedPane();

		// contacts tab
		tabContacts = new JPanel(new BorderLayout());
		invite = new JButton("Invite");
		invite.setBackground(Color.gray);
		Friend[] list = null;
		try {
			if (skype != null)
				list = skype.getContacts();
		} catch (SkypeException | exceptions.SkypeException e) {
			Functions f = new Functions(this);
			f.errorHandler(e);
		}
		contactList = new JList<Friend>(list);
		contactList.addListSelectionListener(new ListSelectionListener() {

			@Override
			public void valueChanged(ListSelectionEvent e) {
				invite.setActionCommand(((Friend) contactList.getSelectedValue()).getId());
			}

		});
		contactList.setCellRenderer(new ProListCellRenderer());
		contactList.setBorder(new LineBorder(Color.white, 1));

		invite.addActionListener(gf.defaultActionListener());
		search = new JButton("Search");
		search.setBackground(Color.gray);
		searchField = new JTextField();
		searchField.setColumns(30);
		searchField.setBackground(Color.lightGray);

		searchListener = new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				try {
					contactList.setListData(skype.getContacts(searchField.getText()));
				} catch (Exception e1) {
					e1.printStackTrace();
				}

			}
		};
		search.addActionListener(searchListener);
		searchField.addActionListener(searchListener);

		tabContacts.add(contactList, BorderLayout.CENTER);
		searchPanel = new JPanel(new BorderLayout());
		searchPanel.add(invite, BorderLayout.WEST);
		searchPanel.add(searchField, BorderLayout.CENTER);
		searchPanel.add(search, BorderLayout.EAST);
		tabContacts.add(searchPanel, BorderLayout.NORTH);

		// info tab
		tabInfo = new JPanel(new BorderLayout());
		JTextPane text = new JTextPane();
		text.setText(MORE_INFORMATION);
		//text.setWrapStyleWord(true);
		Font f = new Font(Font.SANS_SERIF, 3, 25);
		text.setFont(f);
		tabInfo.add(text, BorderLayout.CENTER);

		// game tab
		tabGame = gf.gameScreen();

		tabs.addTab("Contacts", tabContacts);
		tabs.addTab("Info", tabInfo);
		tabs.addTab("Game", tabGame);
		frame.add(tabs);
		frame.setSize(600, 480);
		frame.setExtendedState(JFrame.MAXIMIZED_BOTH);
		frame.setVisible(true);
	}
}

class ProListCellRenderer extends DefaultListCellRenderer {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@Override
	public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected,
			boolean cellHasFocus) {
		Component c = super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
		if (isSelected) {
			c.setForeground(Color.white);
			c.setBackground(Color.black);
		}
		if (index % 2 == 0) {
			c.setBackground(Color.gray);
		} else {
			c.setBackground(Color.lightGray);
		}
		return c;
	}
}