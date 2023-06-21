package nz.ac.vuw.ecs.swen225.gp24.domain;

/**
 * The type Key.
 */
public class Key implements Cell {
    /**
     * The Color of the key.
     */
    public String color;

    /**
     * key is defined by colour.
     *
     * @param color the colour of the key ("r", "b" etc.)
     */
    public Key(String color){
		this.color=color;
	}
	public boolean passable() {return true;}
	public String toString(){return "Key " + this.color;}

    /**
     * Get key colour string.
     *
     * @return the string
     */
    public String getKeyColour(){return color;}

    /**
     * picks up the key.
     *
     * @param g the game the key is being picked up in
     * @return tile to replace the key
     */
    public Cell pickUp(Game g){
		g.player().pickUp(this);
		return new FreeTile();
	}

	/**
	 * keys are the same if their colours are the same.
	 * @param o object key is being compared to
	 * @return if the key colours match
	 */
	public boolean equals(Object o){
		if(o instanceof Key k){
			return k.getKeyColour().equals(this.getKeyColour());
		}
		return false;
	}
	public int hashCode(){return this.toString().hashCode();}
}
