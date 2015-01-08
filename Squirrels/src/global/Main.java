package global;

import gui.GUI;

public class Main extends Functions{
	public Main() {
		super(g);
	}

	private static GUI g;

	public static void main(String[] args) {
		System.out.println("Start the game!");
		g = new GUI();
	}
}
