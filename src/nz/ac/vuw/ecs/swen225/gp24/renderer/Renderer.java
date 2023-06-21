package nz.ac.vuw.ecs.swen225.gp24.renderer;

import javax.swing.JPanel;
import java.awt.Graphics;
import nz.ac.vuw.ecs.swen225.gp24.domain.*;

/**
 * The renderer for the game.
 * 
 * @author Serafina Slevin
 *
 */
public class Renderer extends JPanel {
	/**
	 * fields for the renderer
	 */
	public Cell[][] cellsToDraw; // the cells that will be drawn from the game 
	public Game game; //game to be drawn
	public boolean replayMode; // true=Live game, false=replay

	/**
	 * Constructor for the renderer.
	 * 
	 * @param replayMode true=Live game, false=replay
	 */
	public Renderer(boolean replayMode) {
		this.replayMode = replayMode;
	}

	/**
	 * This method is used to set the cells that are being drawn by renderer
	 * 
	 * @param cells Cell[][]
	 */
	public void setCells(Cell[][] cells) {
		this.cellsToDraw = cells;
	}

	/**
	 * this method is being used to set the game that is being drawn by renderer
	 * 
	 * @param g the game being rendered 
	 */
	public void setGame(Game g) {
		this.game = g;
	}

	/**
	 * This method is called when the panel needs to be redrawn.
	 * 
	 * @param g the graphics object to draw on
	 */
	@Override
	public void paintComponent(Graphics g) {
		if (!replayMode)cellsToDraw = game.getBoard();
		Cell[][] viewPort = Viewport.getViewport(cellsToDraw, 9);
		int width = (getWidth() / viewPort.length);
		int height = (getHeight() / viewPort[1].length);

		for (int x = 0; x < viewPort.length; x++) {
			for (int y = 0; y < viewPort[1].length; y++) {
				// drawing the basic ground tile
				g.drawImage(Images.freeTile, x * width, y * height, width, height, this);

				Cell c = viewPort[x][y];// current cell in array

				if (c == null||c.toString().equals("null"))
					g.drawImage(Images.wall, x * width, y * height, width, height, this);
				else if (c.toString().equals("FreeTile"))
					continue;
				else
					g.drawImage(Images.getImage(c), x * width, y * height, width, height, this);
				
				if(replayMode) continue;
				if (game.isStandingOnInfoField() == true) {
					int[] playerXY = Viewport.findPlayer(cellsToDraw);
					int playerX = playerXY[0];
					int playerY = playerXY[1];
					if (game.getLevel() == 1)
						g.drawImage(Images.infoPopup1, (playerX - 2) * width, (playerY - 3) * height, width * 3,
								height * 2, this);
					if (game.getLevel() == 2)
						g.drawImage(Images.infoPopup2, (playerX - 2) * width, (playerY - 3) * height, width * 3,
								height * 2, this);
				}
			}
		}
	}
}
