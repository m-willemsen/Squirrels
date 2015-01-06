package gui;

import global.Functions;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.DefaultListCellRenderer;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import javax.swing.JTextField;
import javax.swing.border.LineBorder;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import voip.SkypeLocalLibrary;

import com.skype.Call;
import com.skype.Friend;

import game.GameHandler;

public class GUI {
	public static final String GAME_TITLE = "TangibleVirtualGame";

	protected static final String MORE_INFORMATION = "This is more information";

	public static Call lastCall;

	public SkypeLocalLibrary skype = new SkypeLocalLibrary(this);

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

	public GUI() throws Exception {
		frame = new JFrame(GAME_TITLE);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		tabs = new JTabbedPane();

		// contacts tab
		tabContacts = new JPanel(new BorderLayout());
		invite = new JButton("Invite");
		invite.setBackground(Color.gray);

		contactList = new JList<Friend>(skype.getContacts());
		contactList.addListSelectionListener(new ListSelectionListener() {

			@Override
			public void valueChanged(ListSelectionEvent e) {
				invite.setActionCommand(((Friend) contactList
						.getSelectedValue()).getId());
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
					contactList.setListData(skype.getContacts(searchField
							.getText()));
				} catch (Exception e1) {
					Functions.errorHandler(e1);
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
		tabInfo = new JPanel();

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
	@Override
	public Component getListCellRendererComponent(JList list, Object value,
			int index, boolean isSelected, boolean cellHasFocus) {
		Component c = super.getListCellRendererComponent(list, value, index,
				isSelected, cellHasFocus);
		if (isSelected){
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
