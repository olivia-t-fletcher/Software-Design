package nz.ac.vuw.ecs.swen225.gp24.recorder;

import java.io.File;
import java.util.List;
import java.util.Iterator;
import java.util.ArrayList;

import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import org.dom4j.Element;
import org.dom4j.Document;
import org.dom4j.io.SAXReader;
import org.dom4j.io.XMLWriter;
import org.dom4j.DocumentException;
import org.dom4j.DocumentHelper;

/**
 * Recorder Class
 * This class is reponsible for saving and loading XML
 * @author Liv Fletcher
 */
public class Recorder {
    /**
     * Fields 
     */
    private static String XMLName = ".xml";
    private static String fileName = "recorded-save ";
    private static String seperator = System.getProperty("file.separator");
    // For testing, this is used when saving locally via a Linux machine, otherwise it will save to repo saves
    private static String saveLocationLinux = seperator + "group-project" + seperator + "src" + seperator +
    		"nz.ac.vuw.ecs.swen225.gp24" + seperator + "recorder";
    // For testing, this is used when saving locally via a Windows machine, otherwise it will save to repo saves
    private static String saveLocation = "/group-project/src/nz.ac.vuw.ecs.swen225.gp24/recorder";

    private static ArrayList <String[][]> boards = new ArrayList<>();  

    /**
     * Add Object Association
     * Add the XML object association for converting from and to a game instance
     * @param root root Element of the board
     */
    public static void addObjectAssociation(Element root) { 
        // Loop through each board and associate each elements object type
        for(String[][] board : boards) {
            ObjectsXML.cellXML(root, board);
        }
    }

    /**
     * Record History
     * Pass in each "new" board to the boards arraylist
     * @param board 2d array of the game board
     */
    public void recordHistory(String[][] board) {
        boards.add(board);
    }
    
    /**
     * Load File
     * Pass in each "new" board to the boards arraylist
     * @param input File input
     * @return loadBoard(root) call helper method
     */
    public static ArrayList<String[][]> loadFile(File input) {
        // Sax reader for XML object associations
        SAXReader reader = new SAXReader();
        Document doc = null;
        try {
            doc = reader.read(input);
        } catch(DocumentException error) {
            error.printStackTrace();
        }
        Element root = (Element) doc.getRootElement();
        return loadBoard(root);
    }
    /**
     * Load Board 
     * Load board function which loops through each board to "build" it
     * back into a game instance for replaying (this is the opposite 
     * funtion to the saving where we take the game object into an XML
     * whereas here we are taking from the XML save back into the game)
     * @param element Element cells
     * @return s arrayList of board strings with the transferred xml string objects
     */
    public static ArrayList<String[][]> loadBoard(Element element) {
    	ArrayList<String[][]> s = new ArrayList<>();
        // Helper function to the loadFile where after loading and receiving each
        // root element we now associate them using our TransferObjectsXML class
        // as a means to "build it back" from the XML file 
        for(Iterator<Element> i = element.elementIterator("board"); i.hasNext();) {
        	Element b = i.next();
            s.add(TransferObjectsXML.buildBoard(b));
    	}
        return s;
    }

    /**
     * Save Game
     * Write the file from the game recording into an XML file format
     */
    public void saveFile() {
    	LocalDateTime fileDate = LocalDateTime.now();
    	DateTimeFormatter format = DateTimeFormatter.ofPattern("dd-MM-yyyy HH-mm-ss");
    	String path =  "saves" + seperator + fileName + fileDate.format(format) + XMLName;
        Document game = DocumentHelper.createDocument();
        Element root = game.addElement("level");
        addObjectAssociation(root);
        XMLWriter writeToFile;
		try {
			writeToFile = new XMLWriter(new FileWriter(path));
	        writeToFile.write(game);
	        writeToFile.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
    }
}
