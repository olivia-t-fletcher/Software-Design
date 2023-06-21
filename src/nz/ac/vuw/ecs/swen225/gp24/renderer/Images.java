package nz.ac.vuw.ecs.swen225.gp24.renderer;

import java.awt.image.BufferedImage;
import javax.imageio.ImageIO;
import nz.ac.vuw.ecs.swen225.gp24.domain.*;
import java.io.File;
import java.io.IOException;

/**
 * This class is used to load images from the img folder.
 * Used by renderer when drawing the game.
 * 
 * @author Serafina Slevin
 *
 */
public final class Images {
	/**
	 * red door
	 */
	public static final BufferedImage doorRed = loadImage("Doors/door-red");
	/**
	 * green door
	 */
	public static final BufferedImage doorGreen = loadImage("Doors/door-green");
	/**
	 * blue door
	 */
	public static final BufferedImage doorBlue = loadImage("Doors/door-blue");
	/**
	 * yellow door
	 */
	public static final BufferedImage doorYellow = loadImage("Doors/door-yellow");

	/**
	 * exit (blue squares)
	 */
	public static final BufferedImage exit = loadImage("exit-tile");
	/**
	 * cracked wall image
	 */
	public static final BufferedImage exitLock = loadImage("exit-lock");
	/**
	 * floor image
	 */
	public static final BufferedImage freeTile = loadImage("ground-tile");
	/**
	 * info tile 
	 */
	public static final BufferedImage infoField = loadImage("info-tile");
	/**
	 * info pop up for first level
	 */
	public static final BufferedImage infoPopup1 = loadImage("infofield-level1");
	/**
	 * info popup for second level
	 */
	public static final BufferedImage infoPopup2 = loadImage("infofield-level2");
	/**
	 * goomba image (enemy)
	 */
	public static final BufferedImage enemy = loadImage("enemy");
	/**
	 * goomba moving up image (enemy)
	 */
	public static final BufferedImage enemyUp = loadImage("enemy-moveUp");
	/**
	 * star image (treasure)
	 */
	public static final BufferedImage treasure = loadImage("treasure");
	/**
	 * red key
	 */
	public static final BufferedImage keyRed = loadImage("Keys/key-red");
	/**
	 * green key
	 */
	public static final BufferedImage keyGreen = loadImage("Keys/key-green");
	/**
	 * blue key
	 */
	public static final BufferedImage keyBlue = loadImage("Keys/key-blue");
	/**
	 * yellow key
	 */
	public static final BufferedImage keyYellow = loadImage("Keys/key-yellow");
	/**
	 * player moving left
	 */
	public static final BufferedImage playerL = loadImage("player-moveLeft");
	/**
	 * player moving right 
	 */
	public static final BufferedImage playerR = loadImage("player-moveRight");
	/**
	 * player moving up
	 */
	public static final BufferedImage playerU = loadImage("player-moveUp");
	/**
	 * player moving down
	 */
	public static final BufferedImage playerD = loadImage("player-moveDown");
	/**
	 * wall image
	 */
	public static final BufferedImage wall = loadImage("wall-tile");
	/**
	 * this is useful for debugging, not used in game 
	 */
	public static final BufferedImage nullTile = loadImage("null");

	private Images() {
	} // making constructor private so cannot be called

	/**
	 * This method is used to load the image file from the img folder
	 * 
	 * @param path	the path to the image file
	 * @return BufferedImage of the image
	 */
	private static BufferedImage loadImage(String path) {
		try {
			return ImageIO.read(loadFile(path));
		} catch (IOException e) {
			e.printStackTrace();
			System.out.println("Error loading image: " + path);
			return null;
		}
	}

	/**
	 * Returns the image of the given cell
	 * 
	 * @param cell Cell to get image of
	 * @return BufferedImage of the cell
	 */
	public static BufferedImage getImage(Cell cell) {
		return switch (cell.toString()) {
			case "Door red" -> doorRed;
			case "Door green" -> doorGreen;
			case "Door blue" -> doorBlue;
			case "Door yellow" -> doorYellow;

			case "Exit" -> exit;
			case "ExitLock" -> exitLock;
			case "FreeTile" -> freeTile;
			case "InfoField" -> infoField;
			case "Actor up" -> enemyUp;
			case "Actor down" -> enemy;
			case "Actor left" -> enemy;
			case "Actor right" -> enemy;
			case "Chip" -> treasure;

			case "Key red" -> keyRed;
			case "Key green" -> keyGreen;
			case "Key blue" -> keyBlue;
			case "Key yellow" -> keyYellow;

			case "Player up" -> playerU;
			case "Player down" -> playerD;
			case "Player left" -> playerL;
			case "Player right" -> playerR;

			case "Wall" -> wall;

			default -> throw new IllegalArgumentException("Unexpected value, toString of cell is: " + cell.toString());
		};
	}

	/**
	 * Returns the image of the given item
	 * 
	 * @param path	file path of the image
	 * @return File from .png file of the image
	 */
	private static File loadFile(String path) {
		try {
			return new File("img/" + path + ".png");
		} catch (Error e) {
			System.out.println("Error loading file: " + path);
			return null;
		}
	}

}
