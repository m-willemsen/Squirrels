package aa_voip;

import java.awt.Component;
import java.awt.HeadlessException;
import java.io.IOException;

import javax.media.*;
import javax.swing.JApplet;

public class StreamingApplet extends JApplet implements ControllerListener {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private MediaLocator mediaLocator;
	private Player player;

	public void init() {
		mediaLocator = new MediaLocator(getParameter("rtp"));

		Manager.setHint(Manager.LIGHTWEIGHT_RENDERER, true);
		if (mediaLocator != null) {
			try {
				createPlayer();
			} catch (NoPlayerException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	private void createPlayer() throws NoPlayerException, IOException {
		player = Manager.createPlayer(mediaLocator);
		player.addControllerListener(this);
	}

	public void start() {
		if (mediaLocator != null) player.start();
	}

	public void stop() {
		player.stop();
		player.deallocate();
	}

	public void destroy() {
		player.stop();
		player.close();
	}

	@Override
	public void controllerUpdate(ControllerEvent e) {
		if (e instanceof RealizeCompleteEvent){
			Component comp;
			if ((comp=player.getControlPanelComponent()) != null){
				add(comp);
				validate();
			}
		}
	}
}
