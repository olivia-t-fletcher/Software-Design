package nz.ac.vuw.ecs.swen225.gp24.renderer;
import nz.ac.vuw.ecs.swen225.gp24.domain.*;

/**
 * This class is used to create a viewport for the game.
 * Used by Renderer to keep the player in the centre of the game being drawn.
 * 
 * @author Serafina Slevin
 *
 */
public final class Viewport {
    
    /**
     * Finds the player in the given level.
     * 
     * @param Cell[][] cellsToDraw 
     * @return int[] x and y coordinates of the player
     */
    public static int[] findPlayer(Cell[][] cellsToDraw) {
        int[] playerXY = new int[2];
        for (int x = 0; x < cellsToDraw.length; x++) {
            for (int y = 0; y < cellsToDraw[1].length; y++) {
                if (cellsToDraw[x][y] instanceof Player) {
                    playerXY[0] = x;
                    playerXY[1] = y;
                }
            }
        }
        return playerXY;
    }

    /**
     * returns the cells that will be drawn by paintComponent in Renderer.
     * 
     * @param Cell[][]
     * @param viewPortSize
     * @return Cell[][] the cells that will be drawn
     */
    public static Cell[][] getViewport(Cell[][] cellsToDraw, int viewPortSize) {
        int[] playerXY = findPlayer(cellsToDraw);
        int playerX = playerXY[0];
        int playerY = playerXY[1];
        int xStart = playerX - viewPortSize / 2;
        int yStart = playerY - viewPortSize / 2;
        Cell[][] viewport = new Cell[viewPortSize][viewPortSize];
        for (int x = 0; x < viewPortSize; x++) {
            for (int y = 0; y < viewPortSize; y++) {
                if (xStart + x < 0 || yStart + y < 0 || xStart + x >= cellsToDraw.length
                        || yStart + y >= cellsToDraw[1].length) {
                    viewport[x][y] = new Wall();
                } else
                    viewport[x][y] = cellsToDraw[xStart + x][yStart + y];
            }
        }
        return viewport;
    }

}
