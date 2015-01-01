package global;

import gui.TangibleVirtualGame;

import java.io.File;
import java.io.IOException;
import java.io.PrintStream;
import java.net.URL;
import java.net.URLConnection;

import javax.swing.JOptionPane;

public class Functions {
	// First define some constants
	public static PrintStream logginStream = getLogginStream("log.txt");

	// Now define some functions that can be used in all other classes
	public static void errorHandler(Exception e) {
		if (TangibleVirtualGame.frame != null)
			JOptionPane.showMessageDialog(TangibleVirtualGame.frame, e.getMessage(), "An error occurred",
					JOptionPane.ERROR_MESSAGE);
		e.printStackTrace(logginStream);
	}

	public static PrintStream getLogginStream(String fileName) {
		PrintStream stream = System.out;
		try {
			File file = new File(fileName);
			stream = new PrintStream(file);
			System.out.println("The logging is being done in the file "+file.getName()+" (path: "+file.getPath()+")");
			return stream;
		} catch (IOException e) {
			errorHandler(e);
		}
		return System.out;
	}
	
	public static boolean checkInternetConnection(){
		try{
			URL url=new URL("http://skype.com");
			URLConnection con=url.openConnection();
			con.getInputStream();
			return true;
		}
		catch(IOException e){
			return false;
		}
	}
}
