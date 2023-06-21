package nz.ac.vuw.ecs.swen225.gp24.persistancy;

import nz.ac.vuw.ecs.swen225.gp24.domain.InfoField;
import nz.ac.vuw.ecs.swen225.gp24.domain.Actor;
import nz.ac.vuw.ecs.swen225.gp24.domain.Player;
import nz.ac.vuw.ecs.swen225.gp24.domain.Key;
import nz.ac.vuw.ecs.swen225.gp24.domain.FreeTile;
import nz.ac.vuw.ecs.swen225.gp24.domain.Door;
import nz.ac.vuw.ecs.swen225.gp24.domain.Exit;
import nz.ac.vuw.ecs.swen225.gp24.domain.Wall;
import nz.ac.vuw.ecs.swen225.gp24.domain.Cell;
import nz.ac.vuw.ecs.swen225.gp24.domain.Chip;
import nz.ac.vuw.ecs.swen225.gp24.domain.Game;
import nz.ac.vuw.ecs.swen225.gp24.domain.ExitLock;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.JDOMException;
import org.jdom2.input.SAXBuilder;
import org.jdom2.output.Format;
import org.jdom2.output.XMLOutputter;


/**
 * Contains static methods that save and load game.
 *
 * @author Natnael Gebremichael id= 300543025.
 */
public class Persistancy {
    /**
     * Load game.
     *
     * @param fileNamePath the path and the name of the file to be loaded.
     * @return returns a game.
     * @throws IOException throws exception if unable to load the file.
     */
    public static Game load(final String fileNamePath) throws IOException {

        //checks the filename is the correct format
        if (!fileNamePath.endsWith(".xml")) {
            throw new IOException("Missing or incorrect file format.");
        }
        try {
            // Required steps to load XML document.
            SAXBuilder sax = new SAXBuilder();
            Document doc = sax.build(new File(fileNamePath));

            Element root = doc.getRootElement();
            int levelNumber = Integer.parseInt(root.getAttributeValue("levelNumber"));
            int totalTime = (int) Float.parseFloat(root.getAttributeValue("totalTime"));
            Element tiles = root.getChild("Tiles");

            int totalRows = tiles.getChildren().size();
            int totalCols = tiles.getChildren().get(0).getChildren().size();

            Cell[][] cells = new Cell[totalRows][totalCols];
            for (int row = 0; row < totalRows; row++) {
                Element currentItem = tiles.getChildren().get(row);
                for (int col = 0; col < totalCols; col++) {
                    Element currentTile = currentItem.getChildren().get(col);
                    String type = currentTile.getName();
                    switch (type) {
                        case "FreeTile" -> cells[row][col] = new FreeTile();
                        case "Player" -> {
                            Player newPlayer = new Player();
                            if ((currentTile.getAttributeValue("PlayerBlock")) != null) {
                                newPlayer.setBlock(new InfoField(currentTile.getAttributeValue("PlayerBlock")));
                            }
                            newPlayer.setDirection((currentTile.getAttributeValue("PlayerDir")));
                            cells[row][col] = newPlayer;
                        }
                        case "Actor" -> {
                            Actor newActor = new Actor();
                            newActor.setDirection((currentTile.getAttributeValue("ActorDir")));
                            cells[row][col] = newActor;
                        }
                        case "Wall" -> cells[row][col] = new Wall();
                        case "InfoField" -> {
                            String info = currentTile.getAttributeValue("info");
                            cells[row][col] = new InfoField(info);
                        }
                        case "Door" -> {
                            String colour = currentTile.getAttributeValue("colour");
                            cells[row][col] = new Door(colour);
                        }
                        case "Exit" -> cells[row][col] = new Exit();
                        case "ExitLock" -> cells[row][col] = new ExitLock();
                        case "Key" -> {
                            String colour = currentTile.getAttributeValue("colour");
                            cells[row][col] = new Key(colour);
                        }
                        case "Chip" -> cells[row][col] = new Chip();
                    }

                }
            }
            return new Game(cells, levelNumber, totalTime);
        } catch (IOException | JDOMException e) {
            System.out.println("Error unable to loading file." + e);
        }
        return null;  //unable to load file
    }

    /**
     * Save.
     *
     * @param game     Snapshot of a Game.
     * @param fileName the name to be used when saving the game.
     * @throws IOException throws exception when there are issues.
     */
    public static void save(final Game game, final String fileName) throws IOException {

        if (!fileName.endsWith(".xml")) {
            throw new IOException("Missing or incorrect file format.");
        }

        //Class<?> spiderClass = loadClass();

        Document doc = new Document();

        // Create our root element and save it to a variable.
        doc.setRootElement(new Element("Game"));
        Element root = doc.getRootElement();

        // Add the game parameters as attributes.
        root.setAttribute("levelNumber", "" + game.getLevel());
        root.setAttribute("totalTime", "" + game.getTime());

        // Create a parent element which will contain all child Tiles.
        Element allCells = new Element("Tiles");
        Cell[][] map = game.getBoard();

        for (Cell[] cells : map) {
            // Get the current row of tiles.
            Element set = new Element("Row");
            for (Cell cell : cells) {

                // Construct a tileElement to put all information from tileObject into.
                Element tileElement = new Element(cell.getClass().getSimpleName());

                // Save different attributes to tileElement depending on its type.
                if (cell instanceof Key) {
                    tileElement.setAttribute("colour", ((Key) cell).getKeyColour());
                } else if (cell instanceof Door) {
                    tileElement.setAttribute("colour", ((Door) cell).getKeyColour());
                } else if (cell instanceof InfoField) {
                    tileElement.setAttribute("info", ((InfoField) cell).getInformation());
                } else if (cell instanceof Player) {
                    Cell block = ((Player) cell).getBlock();
                    if (block instanceof InfoField) {
                        tileElement.setAttribute("PlayerBlock", ((InfoField) block).getInformation());
                    }
                    tileElement.setAttribute("PlayerDir", ((Player) cell).getDirection());
                } else if (cell instanceof Actor) {
                    tileElement.setAttribute("ActorDir", ((Actor) cell).getDirection());
                }

                // We can now add tileElement as a child of items.
                set.getChildren().add(tileElement);
            }
            // The row is complete. We can now add it as a child of allTiles.
            allCells.getChildren().add(set);
        }
        // We can now add the parent tile element as a child of root.
        root.getChildren().add(allCells);
        XMLOutputter xmlOutputter = new XMLOutputter(Format.getPrettyFormat());

        try (FileOutputStream fos = new FileOutputStream("./src/nz/ac/vuw/ecs/swen225/gp24/Persistancy/Saved/" + fileName)) {
            xmlOutputter.output(doc, fos);

        } catch (IOException e) {
            throw new IOException("Error saving XML: " + e);
        }

    }

    /**
     * This method is used by app to converts 2d.
     * array of string representing the game into.
     * 2d array of cell.
     *
     * @param boardStrings 2d array of string
     * @return 2d array of cells.
     */
    public static Cell[][] encodeStrings(String[][] boardStrings) {
        // assuming square board for now
        int totalRows = boardStrings.length;
        int totalCols = boardStrings.length;
        Cell[][] cells = new Cell[totalRows][totalCols];

        for (int row = 0; row < totalRows; row++) {
            for (int col = 0; col < totalCols; col++) {
                String type = boardStrings[row][col];
                String[] arr = {""};
                if (type != null) {
                    arr = type.split(" ", 2);
                }
                switch (arr[0]) {
                    case "FreeTile" -> cells[row][col] = new FreeTile();
                    case "Player" -> {
                        Player newPlayer = new Player();
                        newPlayer.setDirection(arr[1]);
                        cells[row][col] = newPlayer;
                    }
                    case "Actor" -> {
                        Actor newActor = new Actor();
                        newActor.setDirection(arr[1]);
                        cells[row][col] = newActor;
                    }
                    case "Wall" -> cells[row][col] = new Wall();
                    case "InfoField" -> cells[row][col] = new InfoField("");
                    case "Door" -> cells[row][col] = new Door(arr[1]);
                    case "Exit" -> cells[row][col] = new Exit();
                    case "ExitLock" -> cells[row][col] = new ExitLock();
                    case "Key" -> cells[row][col] = new Key(arr[1]);
                    case "Chip" -> cells[row][col] = new Chip();
                    default -> cells[row][col] = null;
                }
            }
        }
        return cells;
    }
}

