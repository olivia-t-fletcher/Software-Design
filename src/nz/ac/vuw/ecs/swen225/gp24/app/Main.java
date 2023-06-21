package nz.ac.vuw.ecs.swen225.gp24.app;

import javax.swing.SwingUtilities;

/**
 * Entry point for the game, only job is to initiate App
 *
 * @author George McLachlan
 */
public class Main {

	/**
	 * Call APP...
	 * Not sure why this is throwing JavaDoc warning
	 *
	 * @param args not supported, nor should be used
	 */
	public static void main(String[] args){
		SwingUtilities.invokeLater(App::new);
	}
}