package nz.ac.vuw.ecs.swen225.gp24.domain;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

/**
 * The type Domain tests.
 */
public class DomainTests {
    /**
     * testing directional movement & orientation
     */
    @Test
    public void test1(){
        Cell[][] c = {{new FreeTile(), new FreeTile()}, {new FreeTile(), new Player()}};
        Game g = new Game(c, 1, 100);
        g.receiveInput(Game.Direction.LEFT);
        assertEquals("""
                FreeTile FreeTile\s
                Player left FreeTile\s
                """, boardPrinter(g));
    }

    /**
     * unable to move out of bounds
     */
    @Test
    public void test2(){
        Cell[][] c = {{new FreeTile(), new FreeTile()}, {new Player(), new FreeTile()}};
        Game g = new Game(c, 1, 100);
        g.receiveInput(Game.Direction.RIGHT);
        assertEquals("""
                FreeTile Player up\s
                FreeTile FreeTile\s
                """, boardPrinter(g));
    }

    /**
     * can't move through walls
     */
    @Test public void test3(){
        Cell[][] c = {{new FreeTile(), new FreeTile()}, {new Player(), new Wall()}};
        Game g = new Game(c, 1, 100);
        g.receiveInput(Game.Direction.DOWN);
        assertEquals("""
                FreeTile Player up\s
                FreeTile Wall\s
                """, boardPrinter(g));
    }

    /**
     * other directional movement
     */
    @Test public void test4(){
        Cell[][] c = {{new FreeTile(), new FreeTile()}, {new Player(), new Wall()}};
        Game g = new Game(c, 1, 100);
        g.receiveInput(Game.Direction.UP);
        g.receiveInput(Game.Direction.RIGHT);
        g.receiveInput(Game.Direction.LEFT);
        g.receiveInput(Game.Direction.DOWN);
        assertEquals("""
                FreeTile FreeTile\s
                Player down Wall\s
                """, boardPrinter(g));
    }

    /**
     * key pickup
     */
    @Test public void test5(){
        Cell[][] c = {{new Key("b"), new FreeTile()}, {new Player(), new Wall()}};
        Game g = new Game(c, 1, 100);
        g.receiveInput(Game.Direction.LEFT);
        assertEquals("""
                Player left FreeTile\s
                FreeTile Wall\s
                """, boardPrinter(g));
    }

    /**
     * keys in inventory
     */
    @Test public void test6(){
        Cell[][] c = {{new FreeTile(), new FreeTile()}, {new Player(), new Key("r")}};
        Game g = new Game(c, 1, 100);
        g.receiveInput(Game.Direction.DOWN);
        assertEquals(g.player().inventoryToString(), "Key r");
    }

    /**
     * key unlocking door
     */
    @Test public void test7(){
        Cell[][] c = {{new Door("red"), new FreeTile()}, {new Player(), new Key("red")}};
        Game g = new Game(c, 1, 100);
        g.receiveInput(Game.Direction.DOWN);
        g.receiveInput(Game.Direction.LEFT);
        g.receiveInput(Game.Direction.UP);
        assertEquals("""
                Player up FreeTile\s
                FreeTile FreeTile\s
                """, boardPrinter(g));
    }

    /**
     * key unable to unlock door
     */
    @Test public void test8(){
        Cell[][] c = {{new Door("r"), new FreeTile()}, {new Player(), new Key("b")}};
        Game g = new Game(c, 1, 100);
        g.receiveInput(Game.Direction.DOWN);
        g.receiveInput(Game.Direction.LEFT);
        g.receiveInput(Game.Direction.UP);
        assertEquals("""
                Door r FreeTile\s
                Player left FreeTile\s
                """, boardPrinter(g));
    }

    /**
     * treasure pickup
     */
    @Test public void test9(){
        Cell[][] c = {{new FreeTile(), new FreeTile()}, {new Player(), new Chip()}};
        Game g = new Game(c, 1, 100);
        boardPrinter(g);
        g.receiveInput(Game.Direction.DOWN);
        boardPrinter(g);
        g.receiveInput(Game.Direction.UP);
        assertEquals(1,g.player().chips());
        assertEquals("""
                FreeTile Player up\s
                FreeTile FreeTile\s
                """, boardPrinter(g));
    }

    /**
     * exit lock use
     */
    @Test public void test10(){
        Cell[][] c = {{new ExitLock(), new FreeTile()}, {new Player(), new Chip()}};
        Game g = new Game(c, 1, 100);
        g.receiveInput(Game.Direction.DOWN);
        g.receiveInput(Game.Direction.LEFT);
        assertEquals(1, g.player().chips());
        assertEquals(0, g.getChipsLeft());
        assertEquals(1, g.getTotalChips());
        g.receiveInput(Game.Direction.UP);
        assertEquals("""
                Player up FreeTile\s
                FreeTile FreeTile\s
                """, boardPrinter(g));

    }

    /**
     * exit lock unusable
     */
    @Test public void test11(){
        Cell[][] c = {{new ExitLock(), new Chip()}, {new Player(), new Chip()}};
        Game g = new Game(c, 1, 100);
        g.receiveInput(Game.Direction.LEFT);
        boardPrinter(g);
        assertEquals(0, g.player().chips());
        assertEquals(2, g.getChipsLeft());
        assertEquals("""
                ExitLock Player up\s
                Chip Chip\s
                """, boardPrinter(g));
    }

    /**
     * info field use
     */
    @Test public void test12(){
        Cell[][]c = {{new InfoField(","), new FreeTile()}, {new Player(), new FreeTile()}};
        Game g = new Game(c, 1, 100);
        g.receiveInput(Game.Direction.LEFT);
        assertEquals("""
                Player left FreeTile\s
                FreeTile FreeTile\s
                """, boardPrinter(g));

    }

    /**
     * info field remains after being walked on
     */
    @Test public void test13(){
        Cell[][]c = {{new InfoField(","), new FreeTile()}, {new Player(), new FreeTile()}};
        Game g = new Game(c, 1, 100);
        g.receiveInput(Game.Direction.LEFT);
        g.receiveInput(Game.Direction.RIGHT);
        assertEquals("""
                InfoField Player right\s
                FreeTile FreeTile\s
                """, boardPrinter(g));

    }

    /**
     * info field text remains
     */
    @Test public void test14(){
        Cell[][]c = {{new InfoField("test"), new FreeTile()}, {new Player(), new FreeTile()}};
        Game g = new Game(c, 1, 100);
        g.receiveInput(Game.Direction.LEFT);
        g.receiveInput(Game.Direction.RIGHT);
        assertEquals("""
                InfoField Player right\s
                FreeTile FreeTile\s
                """, boardPrinter(g));
        InfoField f = (InfoField) c[0][0];
        assertEquals("test", f.getInformation());
    }

    /**
     * player moves through exit lock
     */
    @Test public void test15(){
        Cell[][]c = {{new ExitLock(), new FreeTile()}, {new Player(), new FreeTile()}};
        Game g = new Game(c, 1, 100);
        g.receiveInput(Game.Direction.LEFT);
        assertEquals("""
                Player left FreeTile\s
                FreeTile FreeTile\s
                """, boardPrinter(g));
    }

    /**
     * toString testing
     */
    @Test public void test16(){
        Cell[][]c = {{new FreeTile(), new FreeTile()}, {new Player(), new FreeTile()}};
        Game g = new Game(c, 1, 100);
        assertEquals(g.getBoardStrings()[0][0],"FreeTile");
    }

    /**
     * actor movement
     */
    @Test public void test17(){
        Cell[][]c = {{new Player(), new FreeTile(), new FreeTile()},
                {new FreeTile(), new Actor(), new FreeTile()},
                {new FreeTile(), new FreeTile(), new FreeTile()}};
        Game g = new Game(c, 1, 401);
        String before = boardPrinter(g);
        g.decrementTime(1);
        String after = boardPrinter(g);
        assertNotEquals(before, after);
        g.decrementTime(1);
        assertEquals(after, boardPrinter(g));
    }

    /**
     * actor toString
     */
    @Test public void test18(){
        Actor a = new Actor();
        a.setDirection(Game.Direction.UP);
        assertEquals(Game.Direction.UP.toString().toLowerCase(), a.getDirection());
    }

    /**
     * player block
     */
    @Test public void test19(){
        Cell e = new Exit();
        Cell[][]c = {{e, new FreeTile()}, {new Player(), new FreeTile()}};
        Game g = new Game(c,1,100);
        g.receiveInput(Game.Direction.UP);
        g.receiveInput(Game.Direction.LEFT);
        assertEquals(g.player().getBlock(), e);
        g.player().setBlock(new FreeTile());
        g.receiveInput(Game.Direction.RIGHT);
        assertEquals("""
                FreeTile Player right\s
                FreeTile FreeTile\s
                """, boardPrinter(g));

    }


    /**
     * Board printer string.
     *
     * @param g the game being printed
     * @return visual string representation of board state
     */
    public String boardPrinter(Game g){
        StringBuilder result = new StringBuilder();
        for (int i = 0; i < g.getBoard().length; i++) {
            for (int j = 0; j < g.getBoard()[0].length; j++) {
                result.append(g.getBoard()[j][i].toString()).append(" ");
            }
            result.append("\n");
        }
        return result.toString();
    }

}
