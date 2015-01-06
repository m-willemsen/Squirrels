package global;

import javax.swing.JFrame;

import exceptions.RequirementsNotMetException;
import gui.GUI;
import gui.GUIFunctions;

public class Main extends Functions{
	public Main(GUI g) {
		super(g);
	}

	private static GUI g;

	public static void main(String[] args) {
		try {
			g = new GUI();
		} catch (Exception e) {
			Functions f = new Functions(g);
			f.errorHandler(e);
			e.printStackTrace();
			e.printStackTrace(f.logginStream);
		}
	}
}
