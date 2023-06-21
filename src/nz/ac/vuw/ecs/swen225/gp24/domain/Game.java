package nz.ac.vuw.ecs.swen225.gp24.domain;

import java.util.ArrayList;
import java.util.concurrent.ThreadLocalRandom;


import java.util.Arrays;
import java.util.List;

/**
 * The type Game.
 */
public class Game {
    /**
     * The player that is moved by keyboard input.
     */
    Player p;
    /**
     * The Game field.
     */
    Cell[][] gameField;

    /**
     * The Total chips on the board when it is instantiated.
     */
    final int totalChips; //this is immutable for the game

    /**
     * The Actors(enemies) present in the level.
     */
    List<Actor> actors;

    /**
     * Whether a key has been picked up since the last ping.
     */
    boolean keyPickedUp;
    /**
     * Whether treasure has been picked up since the last ping.
     */
    boolean treasurePickedUp;
    /**
     * Whether it is game over.
     */
    boolean gameOver;

    /**
     * The Level number.
     */
    final int levelNumber;
    /**
     * The Time remaining.
     */
    float time;
    /**
     * The Ping count.
     */
    int pingCount;


    /**
     * The enum Direction.
     */
    public enum Direction {
        /**
         * Up direction.
         */
        UP,
        /**
         * Down direction.
         */
        DOWN,
        /**
         * Left direction.
         */
        LEFT,
        /**
         * Right direction.
         */
        RIGHT
    }

    /**
     * game is created from 2d array of cells.
     * player, actors, and number of chips are loaded directly from the array
     *
     * @param cells       2d array of cells
     * @param levelNumber the level number
     * @param time        time remaining in the level
     */
    public Game(Cell[][] cells, int levelNumber, int time){
        this.levelNumber = levelNumber;
        this.time = time;
        pingCount=0;
        p = findPlayer(cells);
        gameField = cells.clone();
        totalChips = Arrays.stream(cells).flatMap(Arrays::stream).filter(a -> a instanceof Chip).toList().size();
        actors = new ArrayList<>();
        for(int i =0; i< cells.length; i++){
            for(int j = 0; j<cells[0].length; j++){
                if(cells[i][j] instanceof Actor a){
                    actors.add(a);
                    a.setX(i);
                    a.setY(j);
                }
            }
        }
        p.onExit = false;
    }

    /**
     * Get total chips.
     *
     * @return total chips in the level (chips on the floor + in inventory)
     */
    public int getTotalChips(){
        return this.totalChips;
    }

    /**
     * Get chips left int.
     *
     * @return chips not collected by player
     */
    public int getChipsLeft(){return getTotalChips()-p.chips();}

    /**
     * getter and setter for time. time is decremented by a float value - this is treated as a ping
     *
     * @return float
     */
    public float getTime(){return time;}

    /**
     * Decrement time.
     *
     * @param i the delta time
     */
    public void decrementTime(float i){time -=i; actorMoveAll(); pingCount++;}

    /**
     * Get level int.
     *
     * @return the int
     */
    public int getLevel(){return this.levelNumber;}

    /**
     * Player getter.
     *
     * @return the player
     */
    public Player player(){
        return this.p;
    }

    /**
     * getters and setters for board
     *
     * @return cell [ ] [ ]
     */
    public Cell[][] getBoard(){
        return this.gameField.clone();
    }

    /**
     * returns a 2d array where each entry is a cell toString
     *
     * @return string [ ] [ ]
     */
    public String[][] getBoardStrings(){
        String[][] stringBoard = new String[this.gameField.length][this.gameField[0].length];
        for(int i = 0; i< this.gameField.length; i++){
            for(int j = 0; j < this.gameField[0].length; j++){
                stringBoard[i][j]= this.gameField[i][j].toString();
            }
        }
        return stringBoard;
    }

    /**
     * board setter
     *
     * @param board the board
     */
    public void setBoard(Cell[][] board){
        if(board.length!=this.gameField.length || board[0].length!=this.gameField[0].length){
            throw new IllegalArgumentException("Incorrect dimensions");
        }
        this.gameField = board.clone();
    }

    /**
     * moves all actors on a board in a random direction
     */
    public void actorMoveAll(){
        if(pingCount%10!=0){
            return;
        }
        for(Actor a : actors){
            int dirNum = ThreadLocalRandom.current().nextInt(0, 4);
            Direction d = Direction.values()[dirNum];
            actorMove(a,d);
        }
    }

    /**
     * moves a single actor.
     * actors can only walk on free tiles
     *
     * @param a actor being moved
     * @param d direction
     */
    public void actorMove(Actor a, Direction d){
        int x = a.getX();
        int y = a.getY();
        switch (d) {
            case LEFT -> x -= 1;
            case RIGHT -> x += 1;
            case UP -> y -= 1;
            case DOWN -> y += 1;
        }
        Cell c = getAt(x,y);
        if(c==null){
            throw new IllegalArgumentException("cell is null!");
        }
        else if(c instanceof Player){
            a.move(this,d,x,y);
            this.gameOver=true;

        }
        else if(c instanceof FreeTile){
            a.move(this, d, x,y);
        }


    }

    /**
     * finds the player in a certain 2d array field. if there are multiple players, only the first is returned
     *
     * @param field the field
     * @return player
     */
    public static Player findPlayer(Cell[][] field){
        for(int i = 0; i< field.length; i++){
            for(int j = 0; j< field.length; j++){
                if(field[i][j] instanceof Player p){
                    p.setLocation(i, j);
                    return p;
                }
            }
        }
        throw new IllegalStateException("no player in level!");
    }

    /**
     * receives movement input from app
     *
     * @param d direction to move
     */
    public void receiveInput(Direction d){
        try {
            movePlayer(d);
        }catch(IllegalArgumentException e){}
    }

    /**
     * gets cell at certain location
     * @param x x index of cell
     * @param y y index of cell
     * @return cell at location
     */
    private Cell getAt(int x, int y){
        if(x<0||x>=gameField.length || y<0 || y>=gameField[0].length){
            throw new IllegalArgumentException("invalid location!");
        }
        return gameField[x][y];
    }

    /**
     * moves player in a certain direction
     * if the location they're moving to is a locked door or chip/key that can be picked up, it's done
     * then the player moves
     *
     * @param d direction player is moving in
     * @throws IllegalArgumentException the illegal argument exception
     */
    public void movePlayer(Direction d) throws IllegalArgumentException{
        if(findPlayer(this.gameField)==null){
            return;
        }
        int x = p.getX();
        int y = p.getY();
        Cell[][] board = this.getBoard();
        if(!getAt(x,y).passable()){throw new IllegalArgumentException("x:" + x +"y:"+ y + "current position unreachable");}
        switch (d) {
            case LEFT -> x -= 1;
            case RIGHT -> x += 1;
            case UP -> y -= 1;
            case DOWN -> y += 1;
        }
        Cell c = getAt(x,y);
        if(c==null){
            throw new IllegalArgumentException("cell is null!");
        }
        if(c instanceof Lock){
            if(((Lock) c).unlockable(p, this)){board[x][y]=((Lock) c).unlock();}
        }
        else if(c instanceof Key kc) {
            board[x][y] = kc.pickUp(this);
            keyPickedUp=true;
        }
        else if(c instanceof Chip cc){
            board[x][y]=cc.pickUp(this);
            treasurePickedUp = true;
        }
        else if(c instanceof Actor){
            this.gameOver=true;
        }
        this.setBoard(board);
        c = getAt(x,y);
        if(c.passable()){
            try {
                p.move(this, x, y, d);
            }catch(IllegalArgumentException e){}
        }

    }

    /**
     * getters for player status and location
     *
     * @return the boolean
     */
    public boolean isStandingOnExit(){return this.player().onExit;}

    /**
     * Is standing on info field getter.
     *
     * @return the boolean
     */
    public boolean isStandingOnInfoField(){return this.player().onInfoBlock;}

    /**
     * Is game over getter.
     *
     * @return the boolean
     */
    public boolean isGameOver(){return this.gameOver;}

    /**
     * checks if treasure or a key was picked up since the last time the method was called
     *
     * @return boolean
     */
    public boolean wasKeyPickedUp(){boolean kp = keyPickedUp; keyPickedUp=false; return kp;}

    /**
     * Was treasure picked up getter.
     *
     * @return the boolean
     */
    public boolean wasTreasurePickedUp(){boolean tp = treasurePickedUp; treasurePickedUp=false; return tp;}



}
