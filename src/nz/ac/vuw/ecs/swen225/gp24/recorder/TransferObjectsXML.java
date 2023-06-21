package nz.ac.vuw.ecs.swen225.gp24.recorder;
import java.util.ArrayList;
import java.util.Iterator;

import org.dom4j.Element;

/**
 * XML Objects Class
 * Class for associating objects to a string var value
 * for referencing in converting the game tiles into an
 * XML file for recording of movements and loading saved games
 * @author Liv Fletcher
 */
public class TransferObjectsXML {
    /**
     * ObjectsXML Function
     * @param boardEelement 2d array of game board
     * Convert the XML file objects back into game objects
	 * @return boardArray 2d array of board string objects which associate with
     * the ObjectsXML string values
     */
    public static String[][] buildBoard(Element boardEelement) {
    	ArrayList<ArrayList<String>> board = new ArrayList<>();
        // Loop through and associate the parent/child objects e.g
		// tile element is a child of row where rows are a child of board
    	for(Iterator<Element> i = boardEelement.elementIterator("rows"); i.hasNext();) {
			// Associate the child (row) to the parent (board) 
    		Element row = (Element) i.next();
    		ArrayList<String> rowString = new ArrayList<>();
    		for(Iterator<Element> j = row.elementIterator(); j.hasNext();) {
				// Associate the child (tile) to the parent (row) 
    			Element tile = (Element) j.next();
    			rowString.add(tile.asXML().toString());
    		}
    		board.add(rowString);
    	}
    		
    	String[][] boardArray = new String[board.size()][board.size()];
    	int x = 0;
		// These loops go through the boards 2d array and associate the string
		// values to the objects matching from the ObjectsXML.java as a means to
		// "load" back from the xml file into the game for replaying
    	for(ArrayList<String> rowArrayList : board) {
    		int y = 0;
    		for(String cells : rowArrayList) {
				//System.out.println(cells);
				// Wall Tile
    			if(cells.equals("<wall/>")) {
    				boardArray[x][y] = "Wall";
    			} // Free Tile
    			else if(cells.equals("<tile/>")) {
    				boardArray[x][y] = "FreeTile";
    			} // Exit Tile
    			else if(cells.equals("<exit/>")) {
    				boardArray[x][y] = "Exit";
    			} // Exit Lock
				else if(cells.equals("<exit-lock/>")) {
    				boardArray[x][y] = "ExitLock";
    			} // Door - Yellow
    			else if(cells.equals("<door-yellow/>")) {
    				boardArray[x][y] = "Door yellow";
    			} // Door - Blue
    			else if(cells.equals("<door-blue/>")) {
    				boardArray[x][y] = "Door blue";
    			} // Door - Red
    			else if(cells.equals("<door-red/>")) {
    				boardArray[x][y] = "Door red";
    			} // Door - Green
    			else if(cells.equals("<door-green/>")) {
    				boardArray[x][y] = "Door green";
    			} // Key - Yellow
				else if(cells.equals("<key-yellow/>")) {
    				boardArray[x][y] = "Key yellow";
    			} // Key - Blue
    			else if(cells.equals("<key-blue/>")) {
    				boardArray[x][y] = "Key blue";
    			} // Key - Red
    			else if(cells.equals("<key-red/>")) {
    				boardArray[x][y] = "Key red";
    			} // Key - Green
    			else if(cells.equals("<key-green/>")) {
    				boardArray[x][y] = "Key green";
    			} // Info Tile
				else if(cells.equals("<infoField/>")) {
    				boardArray[x][y] = "InfoField";
    			} // Chip - Treasure
				else if(cells.equals("<chip/>")) {
    				boardArray[x][y] = "Chip";
    			} // Player - Move Up
				else if(cells.equals("<move-up/>")) {
    				boardArray[x][y] = "Player up";
    			} // Player - Move Down
				else if(cells.equals("<move-down/>")) {
    				boardArray[x][y] = "Player down";
    			} // Player - Move Left
				else if(cells.equals("<move-left/>")) {
    				boardArray[x][y] = "Player left";
    			} // Player - Move Right
				else if(cells.equals("<move-right/>")) {
    				boardArray[x][y] = "Player right";
    			} // Actor - Move Up
				else if(cells.equals("<actor-up/>")) {
    				boardArray[x][y] = "Actor up";
    			} // Actor - Move Down
				else if(cells.equals("<actor-down/>")) {
    				boardArray[x][y] = "Actor down";
    			} // Actor - Move Left
				else if(cells.equals("<actor-left/>")) {
    				boardArray[x][y] = "Actor left";
    			} // Actor - Move Right
				else if(cells.equals("<actor-right/>")) {
    				boardArray[x][y] = "Actor right";
    			}
				y++;
    		}
			x++;
    	}   		
    	return boardArray;
    }
}