package aa_voip;

import java.awt.BorderLayout;
import java.awt.Component;
import java.io.IOException;
import java.net.URL;

import javax.media.CannotRealizeException;
import javax.media.Manager;
import javax.media.NoPlayerException;
import javax.media.Player;
import javax.swing.JButton;
import javax.swing.JFrame;

public class SimplePlayer extends JFrame {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private Player player;

	public SimplePlayer(String title, URL url) throws NoPlayerException {
		super(title);
		Manager.setHint(Manager.LIGHTWEIGHT_RENDERER, true);
		try {
			createPlayer(url);
		} catch (CannotRealizeException e) {
			e.printStackTrace();
		} catch (NoPlayerException e) {
			e.printStackTrace();
			player.close();
			throw new NoPlayerException();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private void createPlayer(URL url) throws NoPlayerException,
			CannotRealizeException, IOException {
		player = Manager.createRealizedPlayer(url);
		JButton tryAgainButton = new JButton("Try Again");
		tryAgainButton.addActionListener(TestClass.AL);
		System.out.println(tryAgainButton.getActionListeners());
		Component comp;
		if ((comp = player.getVisualComponent()) != null) {
			add(comp, BorderLayout.CENTER);
		}
		if ((comp = player.getControlPanelComponent()) != null) {
			add(comp, BorderLayout.SOUTH);
		}
		if ((comp = tryAgainButton) != null) {
			add(comp, BorderLayout.NORTH);
		}
	}

}
