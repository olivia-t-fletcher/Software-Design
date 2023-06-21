package nz.ac.vuw.ecs.swen225.gp24.domain;

/**
 * The interface Lock. this is for locked doors and exit locks.
 */
public interface Lock extends Cell {
    /**
     * Unlock door.
     *
     * @return the cell to replace it with
     */
    Cell unlock();

    /**
     * Unlockable boolean.
     *
     * @param p the player
     * @param g the game
     * @return whether it can be unlocked
     */
    boolean unlockable(Player p, Game g);
}
