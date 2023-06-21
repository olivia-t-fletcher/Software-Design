package nz.ac.vuw.ecs.swen225.gp24.persistancy.test;

import nz.ac.vuw.ecs.swen225.gp24.domain.*;
import nz.ac.vuw.ecs.swen225.gp24.persistancy.Persistancy;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.fail;

public class PersistancyTest {
    /**
     * Used to test game objects can be repeatedly saved and loaded.
     */
    @Test
    public void test1() {
        System.out.println("Test 1");
        try {
            Cell[][] maze = new Cell[5][5];
            for (int row = 0; row < 5; row++) {
                for (int col = 0; col < 5; col++) {
                    if (row == 0 || col == 0 || row == maze.length - 1 || col == maze.length - 1) {
                        maze[row][col] = new Wall();
                    } else if (col == 1 && row == 1) {
                        maze[row][col] = new Player();
                    } else {
                        maze[row][col] = new FreeTile();
                    }
                }
            }

            Game testGame = new Game(maze, 1, 60);
            String originalString = drawBoard(testGame);

            Persistancy.save(testGame, "saved.xml");
            Game loaded = Persistancy.load("./src/nz/ac/vuw/ecs/swen225/gp24/Persistancy/Saved/" + "saved.xml");
            for (int i = 0; i < 5; i++) {
                loaded = Persistancy.load("./src/nz/ac/vuw/ecs/swen225/gp24/Persistancy/Saved/" + "saved.xml");
                Persistancy.save(loaded, "saved.xml");
            }

            String loadedString = drawBoard(loaded);
            Assertions.assertEquals(originalString, loadedString);
        } catch (IOException e) {
            e.printStackTrace();
            fail();
        }
    }

    /**
     * This is used to test the only files with
     * correct extension (.xml) are used to load
     */
    @Test
    public void test2() {
        System.out.println("Test 2");
        try {
            Persistancy.load("level1"); // Should throw an IOException.
            fail();
        } catch (IOException e) {
            System.out.println(e + "error");
        }

    }

    /**
     * Testing level 1 can be loaded
     */
    @Test
    public void test3() {
        System.out.println("\nTest 3");
        try {
            Game toLoad = Persistancy.load("./src/nz/ac/vuw/ecs/swen225/gp24/Persistancy/Levels/" + "level1.xml");
            String originalString = drawBoard(toLoad);

            Persistancy.save(toLoad, "saved.xml");
            toLoad = Persistancy.load("./src/nz/ac/vuw/ecs/swen225/gp24/Persistancy/Saved/" + "saved.xml");
            String finalString = drawBoard(toLoad);

            Assertions.assertEquals(originalString, finalString);

        } catch (IOException e) {
            e.printStackTrace();
            fail();
        }
    }

    /**
     * Testing level 1 can be loaded
     */
    @Test
    public void test4() {
        System.out.println("Test 4");
        try {
            Game toLoad = Persistancy.load("./src/nz/ac/vuw/ecs/swen225/gp24/Persistancy/Levels/" + "level2.xml");
            String originalString = drawBoard(toLoad);

            Persistancy.save(toLoad, "saved.xml");
            toLoad = Persistancy.load("./src/nz/ac/vuw/ecs/swen225/gp24/Persistancy/Saved/" + "saved.xml");
            String finalString = drawBoard(toLoad);

            Assertions.assertEquals(originalString, finalString);

        } catch (IOException e) {
            e.printStackTrace();
            fail();
        }
    }

    /**
     * test actor and infoField can be added
     */
    @Test
    public void test5() {
        System.out.println("Test 5");
        try {
            Cell[][] maze = new Cell[5][5];
            for (int row = 0; row < 5; row++) {
                for (int col = 0; col < 5; col++) {
                    if (row == 0 || col == 0 || row == maze.length - 1 || col == maze.length - 1) {
                        maze[row][col] = new Actor();
                    } else if (col == 1 && row == 1) {
                        maze[row][col] = new Player();
                    } else {
                        maze[row][col] = new InfoField("test");
                    }
                }
            }

            Game testGame = new Game(maze, 1, 60);
            String originalString = drawBoard(testGame);

            Persistancy.save(testGame, "saved.xml");
            Game loaded = Persistancy.load("./src/nz/ac/vuw/ecs/swen225/gp24/Persistancy/Saved/" + "saved.xml");
            for (int i = 0; i < 5; i++) {
                loaded = Persistancy.load("./src/nz/ac/vuw/ecs/swen225/gp24/Persistancy/Saved/" + "saved.xml");
                Persistancy.save(loaded, "saved.xml");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * test Door and Key can be added
     */
    @Test
    public void test6() {
        System.out.println("Test 6");
        try {
            Cell[][] maze = new Cell[5][5];
            for (int row = 0; row < 5; row++) {
                for (int col = 0; col < 5; col++) {
                    if (row == 0 || col == 0 || row == maze.length - 1 || col == maze.length - 1) {
                        maze[row][col] = new Door("yellow");
                    } else if (col == 1 && row == 1) {
                        maze[row][col] = new Player();
                    } else {
                        maze[row][col] = new Key("yellow");
                    }
                }
            }

            Game testGame = new Game(maze, 1, 60);
            String originalString = drawBoard(testGame);

            Persistancy.save(testGame, "saved.xml");
            for (int i = 0; i < 5; i++) {
                Game loaded = Persistancy.load("./src/nz/ac/vuw/ecs/swen225/gp24/Persistancy/Saved/" + "saved.xml");
                Persistancy.save(loaded, "saved.xml");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * test Chip/treasure and ExitLock can be added
     */
    @Test
    public void test7() {
        System.out.println("Test 7");
        try {
            Cell[][] maze = new Cell[5][5];
            for (int row = 0; row < 5; row++) {
                for (int col = 0; col < 5; col++) {
                    if (row == 0 || col == 0 || row == maze.length - 1 || col == maze.length - 1) {
                        maze[row][col] = new Chip();
                    } else if (col == 1 && row == 1) {
                        maze[row][col] = new Player();
                    } else {
                        maze[row][col] = new ExitLock();
                    }
                }
            }
            Game testGame = new Game(maze, 1, 60);
            Persistancy.save(testGame, "saved.xml");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    /**
     * Used to draw board for testing purposes only.
     *
     * @param testGame game object being tested.
     * @return returns string representation of board.
     */
    public String drawBoard(Game testGame) {
        StringBuilder board = new StringBuilder();
        Cell[][] cells = testGame.getBoard();
        for (Cell[] cell : cells) {
            for (int col = 0; col < cells[1].length; col++) {
                board.append(cell[col]);
                board.append("|");
                if (col == cells[0].length - 1) {
                    board.append("\n");
                }
            }
        }
        System.out.println(board);
        return board.toString();
    }
}
