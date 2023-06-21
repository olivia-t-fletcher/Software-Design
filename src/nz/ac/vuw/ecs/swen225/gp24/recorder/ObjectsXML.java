package nz.ac.vuw.ecs.swen225.gp24.recorder;
import org.dom4j.Element;

/**
 * @author Liv Fletcher
 * XML Objects Class
 * Class for associating objects to a string var value
 * for referencing in converting the game tiles into an
 * XML file for recording of movements and loading saved games
 */
public class ObjectsXML {
    /**
     * ObjectsXML Function
     * Put the created objects onto the 2d board
     * @param root root/parent of the tree
     * @param boardArray 2d array of game board
     * @return board of elements
     */
    public static Element cellXML(Element root, String[][] boardArray) {
        // Assigning the root
        Element board = root.addElement("board");
        // Loop through the 2d board array and associate each element to a tile obj type
        for(int i = 0; i < boardArray.length; i++) {
            // Rows, being the children of the board
            Element rows = board.addElement("rows");
            // Loop through the parent and assign the rows children (elements/tiles)
            for(int j = 0; j < boardArray[i].length; j++) {
                // Wall Tile
                // note to self these need to match ObjectsXML string values
                if(boardArray[i][j].equals("Wall")) {
                    wallXML(rows);
                } // Free Tile
                else if(boardArray[i][j].equals("FreeTile")) {
                    tileXML(rows);
                } // Exit, to next level
                else if(boardArray[i][j].equals("Exit")) {
                    exitXML(rows);
                } // ExitLock, where the Exit object is behind
                else if(boardArray[i][j].equals("ExitLock")) {
                // this is closed when all keys have not been collected
                    exitLockXML(rows);
                } // Locked Yellow Door 
                else if(boardArray[i][j].equals("Door yellow")) {
                    doorXML("yellow", rows);
                } // Locked Blue Door 
                else if(boardArray[i][j].equals("Door blue")) {
                    doorXML("blue", rows);
                } // Locked Red Door 
                else if(boardArray[i][j].equals("Door red")) {
                    doorXML("red", rows);
                } // Locked Green Door 
                else if(boardArray[i][j].equals("Door green")) {
                    doorXML("green", rows);
                } // Yellow Key 
                else if(boardArray[i][j].equals("Key yellow")) {
                    keyXML("yellow", rows);
                } // Blue Key 
                else if(boardArray[i][j].equals("Key blue")) {
                    keyXML("blue", rows);
                } // Red Key
                else if(boardArray[i][j].equals("Key red")) {
                    keyXML("red", rows);
                } // Green Key
                else if(boardArray[i][j].equals("Key green")) {
                    keyXML("green", rows);
                } // Information Field 
                else if(boardArray[i][j].equals("InfoField")) {
                    infoFieldXML(rows);
                } // Treasure 
                else if(boardArray[i][j].equals("Chip")) {
                    chipXML(rows);
                } // Player Movement Up
                else if(boardArray[i][j].equals("Player up")) {
                    playerXML("up", rows);
                } // Player Movement Down
                else if(boardArray[i][j].equals("Player down")) {
                    playerXML("down", rows);
                } // Player Movement Left
                else if(boardArray[i][j].equals("Player left")) {
                    playerXML("left", rows);
                } // Player Movement Right
                else if(boardArray[i][j].equals("Player right")) {
                    playerXML("right", rows);
                } // Actor Movement up
                else if(boardArray[i][j].equals("Actor up")) {
                    actorXML("up", rows);
                } // Actor Movement down
                else if(boardArray[i][j].equals("Actor down")) {
                    actorXML("down", rows);
                } // Actor Movement left
                else if(boardArray[i][j].equals("Actor left")) {
                    actorXML("left", rows);
                } // Actor Movement Right
                else if(boardArray[i][j].equals("Actor right")) {
                    actorXML("right", rows);
                }
            }
        }
        return board;
    }

    /**
     * Player XML Object Creation
     * @param dir Direction of the player [up, down, left, right]
     * @param rows Element interface which associates objects to the XML format, 
     * in this instance we are associating the position of the player
     */
    public static void playerXML(String dir, Element rows) {
        String direction = "move-" + dir;
        Element player = rows.addElement(direction);
        //Element direction = player.addElement(dir);
    }
    /**
     * Player XML Object Creation
     * @param dir Direction of the actor [up, down, left, right]
     * @param rows Element interface which associates objects to the XML format, 
     * in this instance we are associating the position of the player
     */
    public static void actorXML(String dir, Element rows) {
        String direction = "actor-" + dir;
        Element actor = rows.addElement(direction);
    }
    /**
     * Key XML Object Creation
     * @param col Key colour type
     * @param rows Element interface which associates objects to the XML format, 
     * in this instance we are associating the colour of the key
     */
    public static void keyXML(String col, Element rows) {
        String keyColour = "key-" + col;
        Element key = rows.addElement(keyColour);
    }
    /**
     * Door XML Object Creation
     * @param col Door colour type
     * @param rows Element interface which associates objects to the XML format
     */ 
    public static void doorXML(String col, Element rows) {
        String doorColour = "door-" + col;
        Element door = rows.addElement(doorColour);
    }
    /**
     * Exit XML Object Creation
     * @param rows Element interface which associates objects to the XML format
     * The "Exit" tile will always be passable as it is behind the exitLock
     */
    public static void exitXML(Element rows) {
        Element exit = rows.addElement("exit"); 
    }
    /**
     * Exit Lock XML Object Creation
     * @param rows Element interface which associates exitlock object to the XML format
     */
    public static void exitLockXML(Element rows) {
        String doorType = "exit-lock";
        Element exit = rows.addElement(doorType); 
    }
    /**
     * Info Field XML Object Creation
     * @param rows Element interface which associates infofield object to the XML format
     */
    public static void infoFieldXML(Element rows) {
        Element info = rows.addElement("infoField");
    }
    /**
     * Treasure XML Object Creation
     * @param rows Element interface which associates the treasure object to the XML format
     */
    public static void chipXML(Element rows) {
        Element chip = rows.addElement("chip");
    }
    /**
     * Tile XML Object Creation
     * @param rows Element interface which associates free tile objects to the XML format
     */
    public static void tileXML(Element rows) {
        Element tiles = rows.addElement("tile");
    }
    /**
     * Wall XML Object Creation
     * @param rows Element interface which associates wall objects to the XML format
     */
    public static void wallXML(Element rows) {
        Element walls = rows.addElement("wall");
    }
}
