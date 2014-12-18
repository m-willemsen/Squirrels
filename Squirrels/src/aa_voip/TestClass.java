package aa_voip;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;

import javax.media.NoPlayerException;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JOptionPane;

public class TestClass {

	public static void main(String[] args) {
		run();

	}

	private static void run() {
		JFileChooser chooser = new JFileChooser();
		int option = chooser.showOpenDialog(null);

		if (option == JFileChooser.APPROVE_OPTION) {
			try {
				URL url = chooser.getSelectedFile().toURL();
				createAndShowGUI(url);
			} catch (NoPlayerException e) {
				// System.err.println("The URL was malformed...");
				e.printStackTrace();
				run();
			} catch (MalformedURLException e) {
				System.err.println("The URL was malformed...");
				e.printStackTrace();
				run();
			}
		}
		else {
			System.out.println("1");
			JOptionPane.showMessageDialog(null, "Please select a file to show", "Error", JOptionPane.ERROR_MESSAGE);
		}
	}

	public static void createAndShowGUI(URL url) throws NoPlayerException {
		SimplePlayer playerFrame = new SimplePlayer("Simple Media Player App",
				url);
		playerFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		playerFrame.setSize(400, 300);
		playerFrame.setVisible(true);
	}

	public static ActionListener AL = new ActionListener() {		
		@Override
		public void actionPerformed(ActionEvent e) {
			System.out.println("test");
			if (e.getSource() instanceof JFrame) {
				JFrame frame = (JFrame) e.getSource();
				frame.dispose();
				run();
			}
		}

	};

}
