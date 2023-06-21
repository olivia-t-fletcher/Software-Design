package nz.ac.vuw.ecs.swen225.gp24.domain;


/**
 * enemy class.
 */
public class Actor implements Cell {
    private int x;
    private int y;
    /**
     * The Direction the actor is facing.
     */
    Game.Direction dir;

    /**
     * actor is initialised facing upwards.
     * note that actors do not need to know their location when they are created
     */
    public Actor() {
        dir = Game.Direction.UP;
    }

    /**
     * getter and setter methods for x and y values of actor location.
     *
     * @return int
     */
    public int getX(){return this.x;}

    /**
     * Get y.
     *
     * @return y
     */
    public int getY(){return this.y;}

    /**
     * Set x.
     *
     * @param i the x value
     */
    public void setX(int i){x = i;}

    /**
     * Set y.
     *
     * @param j the y value
     */
    public void setY(int j){y = j;}

    /**
     * the actor moves to a given location, not necessarily in the same direction as d.
     * actors can only move across free tiles,
     * as opposed to players which can also walk over info boxes, keys, and chips.
     *
     * @param g the game within which the actor is moving
     * @param d the direction the actor is moving in (for drawing)
     * @param x the x value of the goal
     * @param y the y value of the goal
     */
    public void move(Game g, Game.Direction d, int x, int y) {


        Cell[][] c = g.getBoard();
        if (x < 0 || x > c.length || y < 0 || y > c[0].length) {
            throw new IllegalArgumentException("can't move to there!");
        }
        if (this.x < 0 || this.x > c.length || this.y < 0 || this.y > c[0].length) {
            throw new IllegalArgumentException("invalid starting position!");
        }
        c[this.x][this.y] = new FreeTile();
        this.x = x;
        this.y = y;
        c[this.x][this.y] = this;
        this.dir = d;
    }

    /**
     * the actor can't be passed
     * @return false
     */
    @Override
    public boolean passable() {
        return false;
    }
    public String toString() {
        return "Actor " + getDirection();
    }

    /**
     * getter and setter methods for direction
     *
     * @return direction
     */
    public String getDirection() {
        return this.dir.toString().toLowerCase();
    }

    /**
     * Sets direction.
     *
     * @param d the direction
     */
    public void setDirection(Game.Direction d) {
        this.dir = d;
    }

    /**
     * setter method for direction for use with persistency when loading from a file
     *
     * @param d string representation of direction
     */
    public void setDirection(String d) {
        switch (d) {
            case "left" -> dir = Game.Direction.LEFT;
            case "right" -> dir = Game.Direction.RIGHT;
            case "up" -> dir = Game.Direction.UP;
            case "down" -> dir = Game.Direction.DOWN;
            default -> throw new IllegalArgumentException("Unexpected string: " + d);
        }
    }
}
