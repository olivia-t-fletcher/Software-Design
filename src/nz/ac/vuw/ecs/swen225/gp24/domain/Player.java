package nz.ac.vuw.ecs.swen225.gp24.domain;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * The type Player.
 */
public class Player implements Cell{
	/**
	 * The Direction the player is facing.
	 */
	Game.Direction dir;
	/**
	 * The X index of the player in the game field.
	 */
	int x;
	/**
	 * The Y index of the player in the game field.
	 */
	int y;
	/**
	 * The Inventory (list of keys).
	 */
	List<Key> inventory;
	/**
	 * The Chip counter (inventory of chips).
	 */
	int chipCounter;
	/**
	 * Boolean holding whether the player is standing on an info block.
	 */
	boolean onInfoBlock = false;
	/**
	 * Boolean holding whether the player is standing on the exit.
	 */
	boolean onExit = false;
	/**
	 * Block player is currently standing on.
	 */
	Cell block;

	/**
	 * player has:
	 * inventory of keys
	 * counter (no. of chips)
	 */
	public Player(){
	inventory = new ArrayList<>();
	chipCounter = 0;
	dir = Game.Direction.UP;
	block = new FreeTile();

}

	/**
	 * getters and setters for player inventory
	 *
	 * @return the list
	 */
	public List<Key> inventory(){return List.copyOf(this.inventory);}

	/**
	 * Inventory toString.
	 *
	 * @return string representation of all keys in inventory
	 */
	public String inventoryToString(){return this.inventory.stream().map(Key::toString).collect(Collectors.joining(", "));}

	/**
	 * Get x int.
	 *
	 * @return the int
	 */
	public int getX(){return this.x;}

	/**
	 * Get y int.
	 *
	 * @return the int
	 */
	public int getY(){return this.y;}

	/**
	 * Chips int.
	 *
	 * @return the number of chips in the player's inventory
	 */
	public int chips(){return this.chipCounter;}

	/**
	 * Set location.
	 *
	 * @param x the x index
	 * @param y the y index
	 */
	public void setLocation(int x, int y){this.x=x; this.y=y;}

	/**
	 * moves player in game, assuming unlockable doors have been unlocked
	 *
	 * @param g game the player is moving in
	 * @param x x value of destination
	 * @param y y value of destination
	 * @param d direction for sprite drawing
	 */
	public void move(Game g, int x, int y, Game.Direction d) {

		Cell[][] c = g.getBoard();
		if(x < 0 || x>c.length || y<0 || y>c[0].length){
			throw new IllegalArgumentException("can't move to there!");
		}

		for(int i =0; i < c.length; i++){
			for(int j = 0; j < c[0].length; j++){
				if(c[i][j] instanceof Player){
					this.x=i; this.y=j;
				}
			}
		}
		if(this.x < 0 || this.x>c.length || this.y<0 || this.y>c[0].length){
			throw new IllegalArgumentException("invalid starting position!");
		}

		c[this.x][this.y] = block;
		block = c[x][y];
		onInfoBlock = c[x][y] instanceof InfoField;
		onExit = c[x][y] instanceof Exit;
		if(g.getChipsLeft()>0&&onExit){
			throw new IllegalStateException("shouldn't be able to access exit!");
		}
		this.x = x;
		this.y = y;
		c[this.x][this.y]= this;
		this.dir =d;
		g.setBoard(c);

	}

	/**
	 * pickUp methods for keys and chips.
	 *
	 * @param k the key being picked up
	 */
	public void pickUp(Key k){inventory.add(k);}

	/**
	 * Pick up.
	 */
	public void pickUp(){
		chipCounter++;
	}
	public String toString(){
		return "Player " + this.getDirection();
	}

	/**
	 * direction getters and setters
	 *
	 * @return lowercase string value of direction
	 */
	public String getDirection(){return this.dir.toString().toLowerCase();}

	/**
	 * Set direction.
	 *
	 * @param d the direction
	 */
	public void setDirection(String d){
		switch(d){
			case "left" -> dir= Game.Direction.LEFT;
			case "right" -> dir= Game.Direction.RIGHT;
			case "up" -> dir= Game.Direction.UP;
			case "down" -> dir= Game.Direction.DOWN;
			default -> throw new IllegalArgumentException("Unexpected string: " + d);
		}
	}
	@Override
	public boolean passable() {
		return true;
	}

	/**
	 * getters and setters for the persistent block the player is standing on (e.g. info field or exit)
	 *
	 * @return block player is on
	 */
	public Cell getBlock(){return block;}

	/**
	 * Set block.
	 *
	 * @param c the c
	 */
	public void setBlock(Cell c){this.block = c;}
}
