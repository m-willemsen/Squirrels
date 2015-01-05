package global;

import javax.swing.JFrame;

import exceptions.RequirementsNotMetException;
import gui.GUI;
import gui.GUIFunctions;

public class Main extends Functions{
	public static void main(String[] args) {
		try {
			GUI g = new GUI();
		} catch (Exception e) {
			errorHandler(e);
			e.printStackTrace();
			e.printStackTrace(logginStream);
		}
	}
}
