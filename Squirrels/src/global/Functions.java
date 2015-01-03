package global;

import gui.TangibleVirtualGame;

import java.awt.Desktop;
import java.io.File;
import java.io.IOException;
import java.io.PrintStream;
import java.lang.reflect.Array;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLConnection;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map.Entry;

import javax.swing.JOptionPane;

public class Functions {
	// First define some constants
	public static PrintStream logginStream = getLogginStream("log.txt");

	// Now define some functions that can be used in all other classes
	/**
	 * This function handles all Exceptions. If there is a catch, use this function. It creates a popup field with the error. It also places the whole error in the log.
	 * @param e The error
	 */
	public static void errorHandler(Exception e) {
		if (TangibleVirtualGame.frame != null)
			JOptionPane.showMessageDialog(TangibleVirtualGame.frame, e.getMessage(), "An error occurred",
					JOptionPane.ERROR_MESSAGE);
		e.printStackTrace(logginStream);
	}

	/**
	 * Create a stream to a file
	 * @param fileName the file where the stream should go to
	 * @return the stream, on which you can print errors. On default it will return System.out
	 */
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
		return stream;
	}
	
	/**
	 * Check if there is an internet connection
	 * @return true if there is a connection
	 */
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
	
	/**
	 * Just a simple oneline return a string if a condition is true
	 * @param condition
	 * @param waar
	 * @param onwaar
	 * @return the first string if the condition is true, the second if it is false.
	 */
	public static String returnIf(boolean condition, String waar, String onwaar){
		if(condition){
			return waar;
		}
		return onwaar;
	}
	
	/**
	 * Open a website
	 * @param url url to the website that should be opened.
	 */
	public static void openWebsite(String url) {
		try {
			URI uri = new URI(url);
			Desktop dt = Desktop.getDesktop();
			dt.browse(uri);
		} catch (IOException | URISyntaxException e) {
			errorHandler(e);
		}
	}
	
	/**
	 * Returns a string with a visual representation of the provided hashmap
	 * @param map
	 * @return a string with the hashmap
	 */
	public static <K, V> String showHashMap(HashMap<K, V> map){
		String result = "This is a visualisation of the hashmap:\n";
		Iterator<Entry<K, V>> iter = map.entrySet().iterator();
		while(iter.hasNext()){
			Entry<K, V> entry = iter.next();
			result +=entry.getKey()+"->"+entry.getValue()+"\n";
		}
		return result;
		
	}
	
	/**
	 * Create a single string out of a sting array. This is the opposite of split.
	 * @param array the array that should be returned
	 * @param filler the string that should be appended between the arrayitems
	 * @return the string
	 */
	public static String implode(String[] array, String filler) {
		if (array.length>0){
			String result = array[0];
			for (String item: Arrays.copyOfRange(array, 1, array.length) ){
				result+=filler+item;
			}
			return result;
		}
		else {
			return null;
		}
	}
}
