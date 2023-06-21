package nz.ac.vuw.ecs.swen225.gp24.domain;

/**
 * The type Door. doors can be unlocked if the player has the relevant key in their inventory
 */
public class Door implements Lock{


    /**
     * The Key colour.
     */
    String keyColour;

    /**
     * Instantiates a new Door.
     *
     * @param kc the colour of the needed key
     */
    public Door(String kc){this.keyColour=kc;}
	public boolean passable() {return false;}

    /**
     * getter method for colour
     *
     * @return colour of door/key needed to unlock door
     */
    public String getKeyColour(){return keyColour;}

	/**
	 *
	 * @return tile to replace door with
	 */

	@Override
	public Cell unlock() {
		return new FreeTile();
	}

	/**
	 * whether the door can be unlocked or not
	 * @param p player unlocking door
	 * @param g game door is being unlocked in
	 * @return true/false depending on if the relevant key is in the player's inventory
	 */
	@Override
	public boolean unlockable(Player p, Game g){return p.inventory().contains(new Key(keyColour));}
	public String toString(){return "Door " + keyColour;}
}
