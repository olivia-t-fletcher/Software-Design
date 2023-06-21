package nz.ac.vuw.ecs.swen225.gp24.domain;

/**
 * The type Exit.
 */
public class Exit implements Cell {
	/**
	 * exit doesn't have many methods- it behaves as a freeTile
	 * @return the exit can be walked on
	 */
	public boolean passable() {return true;}
	public String toString(){return "Exit";}

}
