package nz.ac.vuw.ecs.swen225.gp24.domain;

/**
 * The type Exit lock. this is different from a locked door as it needs there to be no chips left in the level
 */
public class ExitLock implements Lock {
	public boolean passable(){return false;}

	/**
	 * when unlocked, replaced with a free tile
	 * @return free tile to replace lock with
	 */
	@Override
	public Cell unlock() {
		return new FreeTile();
	}

	/**
	 * unlocks if there aren't any chips left on the game stage
	 * @param p player
	 * @param g game being checked
	 * @return whether it can be unlocked
	 */
	public boolean unlockable(Player p, Game g){return g.getChipsLeft()==0;}

	public String toString(){return  "ExitLock";}
}
